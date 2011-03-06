package org.jdesktop.swingx.mapviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

import org.jdesktop.swingx.mapviewer.util.GeoUtil;

/**
 * The <code>AbstractTileFactory</code> provides a basic implementation for
 * the TileFactory.
 */
public abstract class AbstractTileFactory extends TileFactory {

    private static final Logger LOG = Logger.getLogger(DefaultTileFactory.class.getName());
    private BufferedImage tileLoadingImage;
    private static final int SOCKET_TIMEOUT_MS = 30000;
    private HashSet<String> maps = new HashSet<String>();
    private int parentX = 0;
    private int parentY = 0;
    private static int mapZoom;
    private static String baseURL;

    public AbstractTileFactory(TileFactoryInfo info) {
        super(info);
        Deamons.addDeamonUser();
        setupFactory(info);
        mapZoom = info.getTotalMapZoom();
        baseURL = info.baseURL;
    }

    /**
     * Creates a new instance of DefaultTileFactory using the specified
     * TileFactoryInfo and number of tile loading threads
     * 
     * @param info
     *            a TileFactoryInfo to configure this TileFactory
     */
    private void setupFactory(TileFactoryInfo info) {
        BufferedImage loadingImage;
        this.info = info;
        try {
            URL url = this.getClass().getResource("resources/loading.png");
            if (url == null) {
                url = AbstractTileFactory.class.getResource("resources/loading.png");
            }
            loadingImage = ImageIO.read(url);
        } catch (Throwable ex) {
            LOG.severe("could not load 'loading.png'");
            loadingImage = new BufferedImage(16, 16,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = loadingImage.createGraphics();
            g2.setColor(Color.black);
            g2.fillRect(0, 0, 16, 16);
            g2.dispose();
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        int tileSize = info.getTileSize(0);
        tileLoadingImage = gc.createCompatibleImage(tileSize, tileSize,
                Transparency.OPAQUE);
        Graphics2D g = tileLoadingImage.createGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, tileSize, tileSize);
        int imageX = (tileSize - loadingImage.getWidth(null)) / 2;
        int imageY = (tileSize - loadingImage.getHeight(null)) / 2;
        g.drawImage(loadingImage, imageX, imageY, null);
    }
    private TileFactoryInfo info;
    private static TileCache cache = new TileCache();

    /**
     * Gets the size of an edge of a tile in pixels at the current zoom level.
     * Tiles must be square.
     * 
     * @param zoom
     *            the current zoom level
     * @return the size of an edge of a tile in pixels
     */
    @Override
    public int getTileSize(int zoom) {
        return getInfo().getTileSize(zoom);
    }

    /**
     * Get the size of the world bitmap at the current zoom level in
     * <b>tiles</b>
     * 
     * @param zoom
     *            the current zoom level
     * @return size of the world bitmap in tiles
     */
    @Override
    public Dimension getMapSize(int zoom) {
        return GeoUtil.getMapSize(zoom, getInfo());
    }

    /**
     * Convert a GeoPosition to a Point2D pixel coordinate in the world bitmap
     * 
     * @param c
     *            a coordinate
     * @param zoomLevel
     *            the current zoom level
     * @return a pixel location in the world bitmap
     */
    @Override
    public Point2D geoToPixel(GeoPosition c, int zoomLevel) {
        return GeoUtil.getBitmapCoordinate(c, zoomLevel, getInfo());
    }

    /**
     * Converts a pixel coordinate in the world bitmap to a GeoPosition
     * 
     * @param pixelCoordinate
     *            a point in the world bitmap at the current zoom level
     * @param zoom
     *            the current zoom level
     * @return the point in lat/long coordinates
     */
    @Override
    public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
        return getInfo().getPosition(pixelCoordinate, zoom);
    }

    /**
     * Get the TileFactoryInfo describing this TileFactory
     * 
     * @return a TileFactoryInfo
     */
    @Override
    public TileFactoryInfo getInfo() {
        return info;
    }

    // Not static for backward compatibility.
    public TileCache getTileCache() {
        return AbstractTileFactory.cache;
    }

    public static void setTileCache(TileCache cache) {
        AbstractTileFactory.cache = cache;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Deamons.removeDeamonUser();
        for (String mapName : maps) {
            removeRequiredTiles(mapName);
        }
        maps.clear();
        ;
    }

    private final static class Deamons {

        /** The number of factories that use the deamons. */
        private static int numTileFactoriesUsingDeamons = 0;
        private static ThreadGroup tg;
        private static final int numTileLoaders = 10;

        private synchronized static void addDeamonUser() {
            numTileFactoriesUsingDeamons++;
        }

        private synchronized static void removeDeamonUser() {
            numTileFactoriesUsingDeamons--;

            if (numTileFactoriesUsingDeamons == 0 && tg != null) {
                try {
                    tg.stop();
                    tg.interrupt();
                    tg.destroy();
                } catch (Exception e) {
                }
                tg = null;
            }
        }

        private synchronized static void startTileLoaders() {
            if (tg == null) {
                tg = new ThreadGroup("tileloaders");
                tg.setDaemon(true);
                for (int i = 1; i <= numTileLoaders; i++) {
                    Thread t = new Thread(tg, new TileLoader("loader-" + i),
                            "tile-loader-" + i);
                    t.setPriority(Thread.MIN_PRIORITY);
                    t.setDaemon(true);
                    t.start();
                }
                for (int i = 1; i <= numTileLoaders; i++) {
                    Thread t = new Thread(tg, new TileDownloader(
                            "tile-downloader-" + i), "tile-downloader-" + i);
                    t.setPriority(Thread.MIN_PRIORITY);
                    t.setDaemon(true);
                    t.start();
                }
            }
        }
    }

    /**
     * An inner class which actually loads the tiles. Used by the thread
     * queue. Subclasses can override this if necessary.
     */
    private static class TileLoader implements Runnable {

        private String name;
        private volatile boolean running;

        private TileLoader(String name) {
            this.name = name;
        }

        public void run() {
            running = true;
            tileLoadLoop:
            while (running) {
                try {
                    Tile tile = tileHandler.getTileToLoad();
                    if (tile == null) {
                        continue tileLoadLoop;
                    }

                    tryTileInCache(tile);
                    if (tile.needsToBeDownLoaded()) {
                        tileHandler.tileStatusChanged();
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Problem in load loop", e);
                }
            }
        }

        public void stop() {
            running = false;
        }
    }

    private final static String getCacheTileKey(Tile tile) {
        return tile.getKey();
    }
    private static final String PROP_USER_AGENT_IDEN = "User-Agent";
    private static final String PROP_USER_AGENT_DEFAULT_VALUE = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)";
    private static String userAgent = PROP_USER_AGENT_DEFAULT_VALUE;

    public static void setUserAgent(final String userAgent) {
        AbstractTileFactory.userAgent = userAgent;
    }

    private static class TileDownloader implements Runnable {

        private String name;

        TileDownloader(String name) {
            this.name = name;
        }
        private volatile boolean running;

        public void stop() {
            running = false;
        }

        private Integer[] getParentKeys(String key) {
            Integer[] ret = new Integer[3];
            StringTokenizer str = new StringTokenizer(key, "/");
            int i = 0;
            while (str.hasMoreTokens()) {
                String nextToken = str.nextToken();
                if (nextToken.indexOf(".png") != -1) {
                    nextToken = nextToken.substring(0, nextToken.indexOf("."));
                }
                if (nextToken.equalsIgnoreCase("osm") == false) {
                    ret[i] = new Integer(nextToken);
                    i++;
                }
            }
            return ret;
        }

        public void run() {
            running = true;

            tileDownloadLoop:
            while (running) {
                Tile tile = tileHandler.getTileToDownload();

                if (tile == null) {
                    continue tileDownloadLoop;
                }
                ByteArrayOutputStream bout = new ByteArrayOutputStream(
                        40 * 1024);
                byte[] buf = new byte[256];
                // String urlString = info.getTileUrl(tile.getX(),
                // tile.getY(), tile.getZoom());
                String urlString = tile.getURL();
                System.out.println("URL " + tile.getURL());
                if (urlString.startsWith("virtual-")) {
                    try {
                        String xyIndexParent = computeXYIndexParentTile(tile);
                        BufferedImage img = cache.get(xyIndexParent);
                        if (img == null) //download
                        {
                            //urlString.substring(urlString.lastIndexOf("virtual-")+8,urlString.indexOf("osm/"));
                            Integer[] parentKeys = getParentKeys(xyIndexParent);
                            Tile parentTile = new Tile("osm", parentKeys[1], parentKeys[2], parentKeys[0], baseURL + "/" + parentKeys[0] + "/" + parentKeys[1] + "/" + parentKeys[2] + ".png", parentKeys[0] + "/" + parentKeys[1] + "/" + parentKeys[2]);
                            downloadTile(baseURL + "/" + (mapZoom - parentKeys[0]) + "/" + parentKeys[1] + "/" + parentKeys[2] + ".png", bout, buf, parentTile);
                            img = cache.get(xyIndexParent);
//                            System.out.println("****************************");
                        }
                        HashMap<String, BufferedImage> calculatedTilesLowerZoomHashMap = computeScaledImage(tile, xyIndexParent, img);
                        if (calculatedTilesLowerZoomHashMap != null) {
                            Set<String> keySet = calculatedTilesLowerZoomHashMap.keySet();
                            for (String tileKey : keySet) {
                                BufferedImage scaledImage = calculatedTilesLowerZoomHashMap.get(tileKey);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write(scaledImage, "png", baos);
                                byte[] bytesOut = baos.toByteArray();
                                Integer[] parentKeys = getParentKeys(tileKey);
                                Tile t = new Tile("osm", parentKeys[1], parentKeys[2], parentKeys[0], baseURL + "/" + parentKeys[0] + "/" + parentKeys[1] + "/" + parentKeys[2] + ".png", parentKeys[0] + "/" + parentKeys[1] + "/" + parentKeys[2]);
                                t.setNeedsToBeDownLoaded(false);
                                t.setImage(scaledImage);
                                tileHandler.addTile(tileKey, t);
                                cache.put(getCacheTileKey(t), bytesOut, scaledImage);
                                t.setNeedsToBeDownLoaded(false);
                            }
                        }
                        continue tileDownloadLoop;
                        //@TODO: create lower zoom tiles and place it into the cache
                    } catch (IOException ex) {
                        Logger.getLogger(AbstractTileFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (downloadTile(urlString, bout, buf, tile)) {
                        continue tileDownloadLoop;
                    }
                }
            }
        }

        private boolean downloadTile(String urlString, ByteArrayOutputStream bout, byte[] buf, Tile tile) {
            try {
                System.out.println("Download URL: " + urlString);
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                conn.setRequestProperty(PROP_USER_AGENT_IDEN, userAgent);
                /*
                 * setting these timeouts ensures the read does not lock
                 * up indefinitely when the server has problems.
                 */
                conn.setConnectTimeout(SOCKET_TIMEOUT_MS);
                conn.setReadTimeout(SOCKET_TIMEOUT_MS);
                InputStream ins = conn.getInputStream();
                bout.reset();
                while (true) {
                    int n = ins.read(buf);
                    if (n == -1) {
                        break;
                    }
                    bout.write(buf, 0, n);
                }
                byte[] bimg = bout.toByteArray();
                BufferedImage img = GraphicsUtilities.loadCompatibleImage(new ByteArrayInputStream(bimg));
                cache.put(getCacheTileKey(tile), bimg, img);
                tile.setImage(img);
                return true;
            } catch (Exception e) {
                if (e instanceof SocketTimeoutException) {
                    LOG.log(Level.INFO, name + " timed out downloading tile: " + tile.getKey() + " url: " + urlString);
                    tile.setNeedsToBeDownLoaded(true);
                    tileHandler.tileStatusChanged();
                } else if (e instanceof FileNotFoundException) {
                    System.out.println("URL " + urlString);
                    LOG.log(Level.INFO, name + " tile doesn't exist: " + tile.getKey() + " url: " + urlString);
                } else {
                    LOG.log(Level.SEVERE, name + " error downloading tile: " + tile.getKey() + " url: " + urlString, e);
                }
            } catch (OutOfMemoryError e) {
                LOG.log(Level.SEVERE, name + " out of memory " + tile.getKey() + " url: " + urlString, e);
            }
            return false;
        }
    }

    private static String computeXYIndexParentTile(Tile requestedTile) {
        int x;
        int y;
        //compute values
        if (requestedTile.getX() % 2 == 0) {
            // even
            x = (requestedTile.getX() - 1) / 2;
        } else {
            // odd
            x = requestedTile.getX() / 2;
        }
        if (requestedTile.getY() % 2 == 0) {
            // even
            y = requestedTile.getY() / 2;
        } else {
            // odd
            y = (requestedTile.getY() - 1) / 2;
        }
        int zoom = requestedTile.getZoom() + 1;
        String key = "osm/" + zoom + "/" + x + "/" + y + ".png";
        return key;
    }

    private static HashMap<String, BufferedImage> computeScaledImage(Tile requestedTile, String keyParentTile, BufferedImage unscaledImage) {
        int w;
        int h;
        int targetWidth;
        int targetHeight;
        if (unscaledImage == null) {
            return null;
        }
        int type = (unscaledImage.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        w = unscaledImage.getWidth();
        h = unscaledImage.getHeight();
        targetWidth = w * 2;
        targetHeight = h * 2;
        BufferedImage bsrc = unscaledImage;
        BufferedImage bdest = new BufferedImage(targetWidth, targetHeight, type);        
        Graphics2D g = bdest.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        AffineTransform at = AffineTransform.getScaleInstance((double) targetWidth / bsrc.getWidth(), (double) targetHeight / bsrc.getHeight());
        g.drawRenderedImage(bsrc, at);
        try {
            ImageIO.write(bdest, "png", new File("/Users/selfemp/Desktop/scaledimg.png"));
        } catch (IOException ex) {
            Logger.getLogger(AbstractTileFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getTileImages(keyParentTile, bdest, requestedTile);
    }

    private static HashMap<String, BufferedImage> getTileImages(String key, BufferedImage tileImage, Tile requestedTile) {
        HashMap<String, BufferedImage> ret = new HashMap<String, BufferedImage>();
        int zoom = requestedTile.getZoom();
        int x = requestedTile.getX();
        int y = requestedTile.getY();
        boolean isXEven,
                isYEven;
        String tileKey = "";
        if (x % 2 == 0) {
            // even
            isXEven = true;
        } else {
            // odd
            isXEven = false;
        }
        if (y % 2 == 0) {
            // even
            isYEven = true;
        } else {
            // odd
            isYEven = false;
        }
        if (isXEven == true && isYEven == true) {
            tileKey = "osm/" + zoom + "/" + x + "/" + y + ".png";
            ret.put(tileKey, tileImage.getSubimage(256, 0, 256, 256));
            tileKey = "osm/" + zoom + "/" + (x - 1) + "/" + y + ".png";
            ret.put(tileKey, tileImage.getSubimage(0, 0, 256, 256));
            tileKey = "osm/" + zoom + "/" + (x - 1) + "/" + (y + 1) + ".png";
            ret.put(tileKey, tileImage.getSubimage(0, 256, 256, 256));
            tileKey = "osm/" + zoom + "/" + x + "/" + (y + 1) + ".png";
            ret.put(tileKey, tileImage.getSubimage(256, 256, 256, 256));            
        } else if (isXEven == false && isYEven == true) {
            tileKey = "osm/" + zoom + "/" + x + "/" + y + ".png";
            ret.put(tileKey, tileImage.getSubimage(0, 0, 256, 256));
            tileKey = "osm/" + zoom + "/" + x + "/" + (y - 1) + ".png";
            ret.put(tileKey, tileImage.getSubimage(0, 256, 256, 256));
            tileKey = "osm/" + zoom + "/" + (x + 1) + "/" + y + ".png";
            ret.put(tileKey, tileImage.getSubimage(256, 0, 256, 256));
            tileKey = "osm/" + zoom + "/" + (x + 1) + "/" + (y + 1) + ".png";
            ret.put(tileKey, tileImage.getSubimage(256, 256, 256, 256));
            try {
                ImageIO.write(tileImage.getSubimage(0, 0, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t1.png"));
                ImageIO.write(tileImage.getSubimage(0, 256, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t2.png"));
                ImageIO.write(tileImage.getSubimage(256, 0, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t3.png"));
                ImageIO.write(tileImage.getSubimage(256, 256, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t4.png"));
            } catch (IOException ex) {
                Logger.getLogger(AbstractTileFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (isXEven == true && isYEven == false) {
            tileKey = "osm/" + zoom + "/" + x + "/" + y + ".png";
            ret.put(tileKey, tileImage.getSubimage(256, 256, 256, 256));
            tileKey = "osm/" + zoom + "/" + x + "/" + (y - 1) + ".png";
            ret.put(tileKey, tileImage.getSubimage(256, 0, 256, 256));
            tileKey = "osm/" + zoom + "/" + (x - 1) + "/" + y + ".png";
            ret.put(tileKey, tileImage.getSubimage(0, 256, 256, 256));
            tileKey = "osm/" + zoom + "/" + (x - 1) + "/" + (y - 1) + ".png";
            ret.put(tileKey, tileImage.getSubimage(0, 0, 256, 256));
            try {
                ImageIO.write(tileImage.getSubimage(256, 256, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t1.png"));
                ImageIO.write(tileImage.getSubimage(256, 0, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t2.png"));
                ImageIO.write(tileImage.getSubimage(0, 256, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t3.png"));
                ImageIO.write(tileImage.getSubimage(0, 0, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t4.png"));
            } catch (IOException ex) {
                Logger.getLogger(AbstractTileFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (isXEven == false && isYEven == false) {
            tileKey = "osm/" + zoom + "/" + x + "/" + y + ".png";
            ret.put(tileKey, tileImage.getSubimage(256, 0, 256, 256));
//            tileKey = "osm/" + zoom + "/" + x + "/" + (y - 1) + ".png";
//            ret.put(tileKey, tileImage.getSubimage(0, 0, 256, 256));
//            tileKey = "osm/" + zoom + "/" + (x + 1) + "/" + y + ".png";
//            ret.put(tileKey, tileImage.getSubimage(256, 256, 256, 256));
//            tileKey = "osm/" + zoom + "/" + (x + 1) + "/" + (y - 1) + ".png";
//            ret.put(tileKey, tileImage.getSubimage(256, 0, 256, 256));
            try {
                ImageIO.write(tileImage.getSubimage(0, 256, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t1.png"));
                ImageIO.write(tileImage.getSubimage(0, 0, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t2.png"));
                ImageIO.write(tileImage.getSubimage(256, 256, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t3.png"));
                ImageIO.write(tileImage.getSubimage(256, 0, 256, 256), "png", new File("/Users/selfemp/Desktop/scaledimg-t4.png"));
            } catch (IOException ex) {
                Logger.getLogger(AbstractTileFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    private static BufferedImage tryTileInCache(Tile tile) {
        BufferedImage image = cache.get(getCacheTileKey(tile));
        Long imageDate = cache.getImageDate(getCacheTileKey(tile));
        if (image != null) {
            tile.setImage(image);
        }
        if ((image == null)
                || (imageDate == null)
                || ((System.currentTimeMillis() - imageDate) > 7 * 24 * 60
                * 60 * 1000)) {
            tile.setNeedsToBeDownLoaded(true);
            tileHandler.tileStatusChanged();
        }
        return image;
    }

    public void setRequiredTiles(Collection<Tile> tiles, String mapName) {
        maps.add(mapName); // To be sure to remove from required tiles.
        tileHandler.setRequiredTiles(tiles, mapName, info);
    }

    public Tile getTile(String key, String mapName) {
        return tileHandler.getTile(key, mapName);
    }

    @Override
    public Tile getTileInstance(int x, int y, int zoom) {
        String key = getTileKey(x, y, zoom);
        Tile t = tileHandler.getTile(key);
        if (t != null) {
            return t;
        }
        t = super.getTileInstance(x, y, zoom);
        tileHandler.addTile(key, t);
        return t;
    }
    private final static TilesHandler tileHandler = new TilesHandler();
    private final static int THREAD_TIMEOUT = 500;

    private static class TilesHandler {

        final Hashtable<String, Hashtable<String, Tile>> requiredTiles = new Hashtable<String, Hashtable<String, Tile>>();
        final Hashtable<String, SoftReference<Tile>> tileReferenceCache = new Hashtable<String, SoftReference<Tile>>(
                32);

        public Tile getTile(String key) {
            Tile t;
            synchronized (tileReferenceCache) {
                SoftReference<Tile> tr = tileReferenceCache.get(key);
                if (tr != null) {
                    t = tr.get();
                    // System.err.println("success for "+ key);
                } else {
                    t = null;
                }
            }
            return t;
        }

        public void addTile(String key, Tile t) {
            synchronized (tileReferenceCache) {
                tileReferenceCache.put(key, new SoftReference<Tile>(t));
            }
        }

        /**
         * Returns the tile that is located at the given tilePoint for this
         * zoom. For example, if getMapSize() returns 10x20 for this zoom, and
         * the tilePoint is (3,5), then the appropriate tile will be located
         * and returned.
         * 
         * @param tilePoint
         * @param zoom
         * @return
         */
        public Tile getTile(String key, String mapName) {
            synchronized (requiredTiles) {
                Hashtable<String, Tile> requiredTilesForMap = requiredTiles.get(mapName);
                if (requiredTilesForMap == null) {
                    return null;
                }
                return requiredTilesForMap.get(key);
            }
        }
        private Iterator<Hashtable<String, Tile>> loadMapIterator = null;
        private Iterator<Tile> loadTileIterator = null;

        public void tileStatusChanged() {
            synchronized (requiredTiles) {
                requiredTiles.notifyAll();
            }
        }

        private void initLoadMapIterator() {
            synchronized (requiredTiles) {
                loadMapIterator = requiredTiles.values().iterator();
                loadTileIterator = null;
            }
        }

        public Tile getTileToLoad() {
            try {
                while (true) {
                    synchronized (requiredTiles) {
                        if (loadMapIterator == null
                                || !loadMapIterator.hasNext()) {
                            initLoadMapIterator();
                        }
                        Tile tile = null;
                        if (loadMapIterator != null) {

                            do {
                                while (loadTileIterator != null
                                        && loadTileIterator.hasNext()) {
                                    {
                                        tile = loadTileIterator.next();
                                        if (tile.getZoom() <= 2) {
                                            String uRL = tile.getURL();
                                            if (uRL.startsWith("virtual-") == false) {
                                                tile.setUrl("virtual-" + tile.getURL());
                                                tile.setNeedsToBeDownLoaded(true);
                                            }
                                        }
                                        if (tile.setNeedsToBeLoaded(false)) {
                                            return tile;
                                        }
                                    }
                                }
                            } while (loadMapIterator.hasNext()
                                    && ((loadTileIterator = loadMapIterator.next().values().iterator()) != null));

                        }
                        requiredTiles.wait(THREAD_TIMEOUT);
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE,
                        " caught exception while polling requiredTiles", e);
            }
            return null;
        }
        private Iterator<Hashtable<String, Tile>> downloadMapIterator = null;
        private Iterator<Tile> downloadTileIterator = null;

        private void initDownloadMapIterator() {
            synchronized (requiredTiles) {
                downloadMapIterator = requiredTiles.values().iterator();
                downloadTileIterator = null;
            }
        }

        public Tile getTileToDownload() {
            try {
                while (true) {
                    synchronized (requiredTiles) {
                        if (downloadMapIterator == null
                                || !downloadMapIterator.hasNext()) {
                            initDownloadMapIterator();
                        }
                        Tile tile = null;
                        if (downloadMapIterator != null) {

                            do {
                                while (downloadTileIterator != null
                                        && downloadTileIterator.hasNext()) {
                                    tile = downloadTileIterator.next();
                                    if (tile.getZoom() <= 2) {
                                        String uRL = tile.getURL();
                                        if (uRL.startsWith("virtual-") == false) {
                                            tile.setUrl("virtual-" + tile.getURL());
                                        }
                                    }
                                    if (tile.setNeedsToBeDownLoaded(false)) {
                                        return tile;
                                    }
                                }
                            } while (downloadMapIterator.hasNext()
                                    && ((downloadTileIterator = downloadMapIterator.next().values().iterator()) != null));

                        }
                        requiredTiles.wait(THREAD_TIMEOUT);
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE,
                        " caught exception while polling requiredTiles", e);
            }
            return null;
        }

        public void setRequiredTiles(Collection<Tile> tiles, String mapName,
                TileFactoryInfo info) {
            Deamons.startTileLoaders();

            synchronized (requiredTiles) {
                Hashtable<String, Tile> requiredTilesForMap = requiredTiles.get(mapName);
                if (requiredTilesForMap == null) {
                    // Required tiles for map does not exist yet - create the
                    // hash.
                    requiredTilesForMap = new Hashtable<String, Tile>();
                    requiredTiles.put(mapName, requiredTilesForMap);
                }

                // Remove any tiles no longer needed by the map from the
                // required list.
                Iterator<Entry<String, Tile>> entryIt = requiredTilesForMap.entrySet().iterator();
                while (entryIt.hasNext()) {
                    Entry<String, Tile> entry = entryIt.next();
                    if (!tiles.contains(entry.getValue())) {
                        entryIt.remove();
                    }
                }

                // Check the new tiles and get them.
                for (Tile tile : tiles) {
                    if (requiredTilesForMap.containsKey(tile.getKey())) {
                        // Key already available - do nothing.
                        continue;
                    }
                    if (tile.getZoom() <= 2) {
                        requiredTilesForMap.put(tile.getKey(), tile);
                        tile.setNeedsToBeLoaded(true);
                    }
                    if ((tile.getImage() == null)
                            && GeoUtil.isValidTile(tile.getX(), tile.getY(),
                            tile.getZoom(), info)) {
                        // tile.setImage(tileLoadingImage);
                        requiredTilesForMap.put(tile.getKey(), tile);
                        tile.setNeedsToBeLoaded(true);
                        if (requiredTilesForMap.get(tile.getKey()) == null) {
                            LOG.log(Level.SEVERE, "Problem adding tile "
                                    + tile.getKey());
                        }
                    }
                }
                requiredTilesUpdated();
                requiredTiles.notifyAll();
            }
        }

        private void requiredTilesUpdated() {
            initLoadMapIterator();
            initDownloadMapIterator();
        }

        public boolean removeRequiredTiles(String mapName) {
            synchronized (requiredTiles) {
                if (requiredTiles.containsKey(mapName)) {
                    requiredTiles.get(mapName).clear();
                    requiredTiles.remove(mapName);
                    requiredTilesUpdated();
                }
                return requiredTiles.isEmpty();
            }
        }
    }

    @Override
    public void removeRequiredTiles(String mapName) {
        if (tileHandler.removeRequiredTiles(mapName)) {
            // Can be used to stop deamons
            maps.remove(mapName);
        }
    }
}