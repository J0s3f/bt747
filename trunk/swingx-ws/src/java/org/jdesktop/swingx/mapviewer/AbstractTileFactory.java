package org.jdesktop.swingx.mapviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.mapviewer.util.GeoUtil;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 * The <code>AbstractTileFactory</code> provides a basic implementation for
 * the TileFactory.
 */
public abstract class AbstractTileFactory extends TileFactory {

    private static final Logger LOG = Logger
            .getLogger(DefaultTileFactory.class.getName());
    private BufferedImage tileLoadingImage;
    private static final int SOCKET_TIMEOUT_MS = 30000;
    private HashSet<String> maps = new HashSet<String>();

    public AbstractTileFactory(TileFactoryInfo info) {
        super(info);
        Deamons.addDeamonUser();
        setupFactory(info);
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
                url = AbstractTileFactory.class
                        .getResource("resources/loading.png");
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
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
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
        int tilezoom = getTileZoom(zoom);
        return getInfo().getTileSize(tilezoom) << (tilezoom-zoom);
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
        int tilezoom = getTileZoom(zoom);
        int scale = 1 << (tilezoom-zoom);

        Dimension s = GeoUtil.getMapSize(tilezoom, getInfo());
        if(scale!=1) {
            s.setSize(s.getWidth()*scale, s.getHeight()*scale);
        }
        return s;
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
    public Point2D geoToPixel(GeoPosition c, int zoom) {
        int tilezoom = getTileZoom(zoom);
        int scale = 1 << (tilezoom-zoom);

        Point2D p = GeoUtil.getBitmapCoordinate(c, tilezoom, getInfo());
        if (scale != 1) {
            p.setLocation(p.getX() * scale, p.getY() * scale);
        }
        return p;
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
        int tilezoom = getTileZoom(zoom);
        int scale = 1 << (tilezoom-zoom);
        Point2D p = (Point2D) pixelCoordinate.clone();
        if (scale != 1) {
            p.setLocation(p.getX() / scale, p.getY() / scale);
        }

        return getInfo().getPosition(p, tilezoom);
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
            tileLoadLoop: while (running) {
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

        public void run() {
            running = true;

            tileDownloadLoop: while (running) {
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

                try {
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
                    BufferedImage img = GraphicsUtilities
                            .loadCompatibleImage(new ByteArrayInputStream(
                                    bimg));
                    cache.put(getCacheTileKey(tile), bimg, img);
                    tile.setImage(img);
                    continue tileDownloadLoop;
                } catch (Exception e) {
                    if (e instanceof SocketTimeoutException) {
                        LOG.log(Level.INFO, name
                                + " timed out downloading tile: "
                                + tile.getKey() + " url: " + urlString);
                        tile.setNeedsToBeDownLoaded(true);
                        tileHandler.tileStatusChanged();
                    } else if (e instanceof FileNotFoundException) {
                        LOG.log(Level.INFO, name + " tile doesn't exist: "
                                + tile.getKey() + " url: " + urlString);
                    } else {
                        LOG.log(Level.SEVERE, name
                                + " error downloading tile: " + tile.getKey()
                                + " url: " + urlString, e);
                    }
                } catch (OutOfMemoryError e) {
                    LOG.log(Level.SEVERE, name + " out of memory "
                            + tile.getKey() + " url: " + urlString, e);
                }

            }
        }
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
        zoom=getTileZoom(zoom);
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
                Hashtable<String, Tile> requiredTilesForMap = requiredTiles
                        .get(mapName);
                if (requiredTilesForMap == null) {
                    return null;
                }
                return requiredTilesForMap.get(key);
            }
        }

        private Iterator<Hashtable<String, Tile>> loadMapIterator = null;
        private Iterator<Tile> loadTileIterator = null;

        public void tileStatusChanged() {
            synchronized(requiredTiles) {
               requiredTiles.notifyAll();
            }
        }

        private void initLoadMapIterator() {
            synchronized(requiredTiles) {
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
                                        if (tile.setNeedsToBeLoaded(false)) {
                                            return tile;
                                        }
                                    }
                                }
                            } while (loadMapIterator.hasNext()
                                    && ((loadTileIterator = loadMapIterator
                                            .next().values().iterator()) != null));

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
                                    if (tile.setNeedsToBeDownLoaded(false)) {
                                        return tile;
                                    }
                                }
                            } while (downloadMapIterator.hasNext()
                                    && ((downloadTileIterator = downloadMapIterator
                                            .next().values().iterator()) != null));

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
                Hashtable<String, Tile> requiredTilesForMap = requiredTiles
                        .get(mapName);
                if (requiredTilesForMap == null) {
                    // Required tiles for map does not exist yet - create the
                    // hash.
                    requiredTilesForMap = new Hashtable<String, Tile>();
                    requiredTiles.put(mapName, requiredTilesForMap);
                }

                // Remove any tiles no longer needed by the map from the
                // required list.
                Iterator<Entry<String, Tile>> entryIt = requiredTilesForMap
                        .entrySet().iterator();
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