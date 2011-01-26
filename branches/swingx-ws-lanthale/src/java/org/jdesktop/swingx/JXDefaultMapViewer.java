/*
 * MapViewer.java
 * 
 * Created on March 14, 2006, 2:14 PM
 * 
 * To change this template, choose Tools | Template Manager and open the
 * template in the editor.
 */
package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.DesignMode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * A tile oriented map component that can easily be used with tile sources on
 * the web like Google and Yahoo maps, satellite data such as NASA imagery,
 * and also with file based sources like pre-processed NASA images. A known
 * map provider can be used with the SLMapServerInfo, which will connect to a
 * 2km resolution version of NASA's Blue Marble Next Generation imagery.
 * 
 * @see SLMapServerInfo for more information.
 * 
 *      Note, the JXDefaultMapViewer has three center point properties. The
 *      <B>addressLocation</B> property represents an abstract center of the
 *      map. This would usually be something like the first item in a search
 *      result. It is a {@link GeoPosition}. The <b>centerPosition</b>
 *      property represents the current center point of the map. If the user
 *      pans the map then the centerPosition point will change but the
 *      <B>addressLocation</B> will not. Calling
 *      <B>recenterToAddressLocation()</B> will move the map back to that
 *      center address. The <B>center</B> property represents the same point
 *      as the centerPosition property, but as a Point2D in pixel space
 *      instead of a GeoPosition in lat/long space. Note that the center
 *      property is a Point2D in the entire world bitmap, not in the portion
 *      of the map currently visible. You can use the
 *      <B>getViewportBounds()</B> method to find the portion of the map
 *      currently visible and adjust your calculations accordingly. Changing
 *      the <B>center</B> property will change the <B>centerPosition</B>
 *      property and vice versa. All three properties are bound.
 * @author Joshua.Marinacci@sun.com
 * @see org.jdesktop.swingx.mapviewer.bmng.SLMapServerInfo
 */
public class JXDefaultMapViewer extends JXMapViewer {

    private static final Logger LOG = Logger
            .getLogger(JXDefaultMapViewer.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @seejava.awt.Container#addPropertyChangeListener(java.beans.
     * PropertyChangeListener)
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // TODO Auto-generated method stub
        super.addPropertyChangeListener(listener);
    }

    static {
        LOG.setLevel(Level.SEVERE);
    }
    @SuppressWarnings("unused")
    private final boolean isNegativeYAllowed = true; // maybe rename to
    // isNorthBounded and
    // isSouthBounded?
    /**
     * The newZoom level. Generally a value between 1 and 15 (TODO Is this
     * true for all the mapping worlds? What does this mean if some mapping
     * system doesn't support the newZoom level?
     */
    private int zoom = 1;
    /**
     * The position, in <I>map coordinates</I> of the center point. This is
     * defined as the distance from the top and left edges of the map in
     * pixels. Dragging the map component will change the center position.
     * Zooming in/out will cause the center to be recalculated so as to remain
     * in the center of the new "map".
     */
    private Point2D center = new Point2D.Double(0, 0);
    /**
     * Indicates whether or not to draw the borders between tiles. Defaults to
     * false.
     * 
     * TODO Generally not very nice looking, very much a product of testing
     * Consider whether this should really be a property or not.
     */
    private boolean drawTileBorders = false;
    /**
     * Factory used by this component to grab the tiles necessary for painting
     * the map.
     */
    private TileFactory factory;
    /**
     * The position in latitude/longitude of the "address" being mapped. This
     * is a special coordinate that, when moved, will cause the map to be
     * moved as well. It is separate from "center" in that "center" tracks the
     * current center (in pixels) of the viewport whereas this will not change
     * when panning or zooming. Whenever the addressLocation is changed,
     * however, the map will be repositioned.
     */
    private GeoPosition addressLocation;
    /**
     * Specifies whether panning is enabled. Panning is being able to click
     * and drag the map around to cause it to move
     */
    private boolean panEnabled = true;
    /**
     * Specifies whether zooming is enabled (the mouse wheel, for example,
     * zooms)
     */
    private boolean zoomEnabled = true;
    /**
     * Indicates whether the component should recenter the map when the
     * "middle" mouse button is pressed
     */
    private boolean recenterOnClickEnabled = true;
    /**
     * The overlay to delegate to for painting the "foreground" of the map
     * component. This would include painting waypoints, day/night, etc. Also
     * receives mouse events.
     */
    private Painter overlay;
    private boolean designTime;
    private Image loadingImage;
    private boolean restrictOutsidePanning = false;
    private boolean horizontalWrapped = true;
    /**
     * Specifies whether or not the image tiles should actually be painted.
     */
    private boolean drawMapTiles = true;
    private HashMap<String, Tile> currentTiles = new HashMap<String, Tile>();
    private HashMap<String, Point> tileWindowCoords = new HashMap<String, Point>();
    private MapBackgroundPainter backgroundPainter;
    private boolean shownOnce = false;

    /**
     * Create a new JXDefaultMapViewer. By default it will use the
     * EmptyTileFactory
     */
    public JXDefaultMapViewer() {
        factory = new EmptyTileFactory();
        // setTileFactory(new GoogleTileFactory());
        MouseInputListener mia = new PanMouseInputListener();
        setRecenterOnClickEnabled(false);
        this.addMouseListener(mia);
        this.addMouseMotionListener(mia);
        this.addMouseWheelListener(new ZoomMouseWheelListener());
        this.addKeyListener(new PanKeyListener());
        // make a dummy loading image
        try {
            URL url = this.getClass().getResource(
                    "mapviewer/resources/loading.png");
            this.setLoadingImage(ImageIO.read(url));
        } catch (Throwable ex) {
            System.out.println("could not load 'loading.png'");
            BufferedImage img = new BufferedImage(16, 16,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(Color.black);
            g2.fillRect(0, 0, 16, 16);
            g2.dispose();
            this.setLoadingImage(img);
        }

        backgroundPainter = new MapBackgroundPainter();
        setBackgroundPainter(backgroundPainter);
    }

    // the method that does the actual painting
    public final String getUniqueViewName() {
        // TODO: make this unique.
        return getName();
    }

    private final class MapBackgroundPainter extends AbstractPainter<JXPanel> {

        private int prevWidth = 0;
        private int prevHeight = 0;

        protected void doPaint(Graphics2D g, JXPanel component, int width,
                int height) {
            if (prevWidth != width || prevHeight != height) {
                prevWidth = width;
                prevHeight = height;
                needToCalcMapTiles = true;
            }
            doPaintComponent(g);
        }

        public void needRepaint() {
            if (!isDirty()) {
                setDirty(true);
            }
        }
    };

    private void doPaintComponent(Graphics g) {/*
                                                * if (isOpaque() ||
                                                * isDesignTime()) {
                                                * g.setColor(getBackground());
                                                * g.fillRect(0,0,getWidth(),
                                                * getHeight()); }
                                                */
        if (isDesignTime()) {
        } else {
            int zoom = getZoom();
            Rectangle viewportBounds = getViewportBounds();
            drawMapTiles(g, zoom, viewportBounds);
            drawOverlays(zoom, g, viewportBounds);
        }
        super.paintBorder(g);
    }

    private void needsRepaint() {
        if (backgroundPainter != null) {
            backgroundPainter.needRepaint();
        }
        repaint(20);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setDesignTime(boolean)
     */
    public void setDesignTime(boolean b) {
        this.designTime = b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#isDesignTime()
     */
    public boolean isDesignTime() {
        return designTime;
    }

    /**
     * Draw the map tiles. This method is for implementation use only.
     * 
     * @param g
     *            Graphics
     * @param newZoom
     *            newZoom level to draw at
     * @param viewportBounds
     *            the bounds to draw within
     */
    protected void drawMapTiles(final Graphics g, final int zoom,
            Rectangle viewportBounds) {
        try {
            // If we're not drawing map tiles currently, nothing to do here...
            if (!drawMapTiles) {
                return;
            }
            if (needToCalcMapTiles) {
                calcRequiredTiles();
            }

            boolean hasTiles;

            synchronized (requiredTilesMutex) {
                hasTiles = requiredTiles != null;
            }
            if (hasTiles) {
                requestMapTiles();
            }

            shownOnce = true;
            // int newZoom = getZoom();
            int tileSize = getTileFactory().getTileSize(zoom);
            for (Tile tile : currentTiles.values()) {
                Point point;
                synchronized (requiredTilesMutex) {
                    point = tileWindowCoords.get(tile.getKey());
                }
                if (point == null) {
                    continue;
                }
                BufferedImage tileImage = tile.getImage();
                if (tileImage == null) {
                    if (isOpaque()) {
                        g.setColor(getBackground());
                        g.fillRect(point.x, point.y, tileSize, tileSize);
                    }
                } else {
                    g.drawImage(tileImage, point.x, point.y, null);
                }

                if (isDrawTileBorders()) {
                    g.setColor(Color.black);
                    g.drawRect(point.x, point.y, tileSize, tileSize);
                    g.drawRect(point.x + tileSize / 2 - 5, point.y + tileSize
                            / 2 - 5, 10, 10);
                    g.setColor(Color.white);
                    g.drawRect(point.x + 1, point.y + 1, tileSize, tileSize);
                    String text = tile.getX() + ", " + tile.getY() + ", "
                            + zoom;
                    g.setColor(Color.black);
                    g.drawString(text, point.x + 10, point.y + 30);
                    g.drawString(text, point.x + 10 + 2, point.y + 30 + 2);
                    g.setColor(Color.white);
                    g.drawString(text, point.x + 10 + 1, point.y + 30 + 1);
                }
            }
        } catch (ConcurrentModificationException e) {
            // Nothing to worry about - request repaint.
            repaint();
        }
    }

    private void drawOverlays(final int zoom, final Graphics g,
            final Rectangle viewportBounds) {
        if (overlay != null) {
            overlay.paint((Graphics2D) g, this, getWidth(), getHeight());
            needsRepaint();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.JXMapViewer#setOverlayPainter(org.jdesktop.swingx
     * .painter.Painter)
     */
    @SuppressWarnings("unchecked")
    public void setOverlayPainter(Painter overlay) {
        Painter old = getOverlayPainter();
        this.overlay = overlay;
        firePropertyChange("mapOverlay", old, getOverlayPainter());
        needsRepaint();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getOverlayPainter()
     */
    public Painter getOverlayPainter() {
        return overlay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getViewportBounds()
     */
    public Rectangle getViewportBounds() {
        return calculateViewportBounds(getCenter());
    }

    private Rectangle calculateViewportBounds(Point2D center) {
        Insets insets = getInsets();
        // calculate the "visible" viewport area in pixels
        int viewportWidth = getWidth() - insets.left - insets.right;
        int viewportHeight = getHeight() - insets.top - insets.bottom;
        double viewportX = (center.getX() - viewportWidth / 2);
        double viewportY = (center.getY() - viewportHeight / 2);
        return new Rectangle((int) viewportX, (int) viewportY, viewportWidth,
                viewportHeight);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setRecenterOnClickEnabled(boolean)
     */
    public void setRecenterOnClickEnabled(boolean b) {
        boolean old = isRecenterOnClickEnabled();
        recenterOnClickEnabled = b;
        firePropertyChange("recenterOnClickEnabled", old,
                isRecenterOnClickEnabled());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#isRecenterOnClickEnabled()
     */
    public boolean isRecenterOnClickEnabled() {
        return recenterOnClickEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setZoom(int)
     */
    public void setZoom(int newZoom) {
        this.setZoom(getCenter(), newZoom);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setZoom(java.awt.geom.Point2D,
     * int)
     */
    public void setZoom(Point2D zoomCenter, int newZoom) {
        final TileFactoryInfo info = getTileFactory().getInfo();
        // Correct zoom level - should return ?
        if (info != null
                && (newZoom < info.getMinimumZoomLevel() || newZoom > info
                        .getMaximumZoomLevel())) {
            if (newZoom > info.getMaximumZoomLevel()) {
                newZoom = info.getMaximumZoomLevel();
            } else if (newZoom < info.getMinimumZoomLevel()) {
                newZoom = info.getMinimumZoomLevel();
            }
        }

        int oldZoom = this.zoom;
        Point2D oldCenter = getCenter();
        double xOffset;
        double yOffset;
        xOffset = oldCenter.getX() - zoomCenter.getX();
        yOffset = oldCenter.getY() - zoomCenter.getY();
        Dimension oldMapSize = getTileFactory().getMapSize(oldZoom);
        Dimension mapSize = getTileFactory().getMapSize(newZoom);
        setZoomAndCenter(new Point2D.Double(xOffset
                + (zoomCenter.getX() * (mapSize.getWidth() / oldMapSize
                        .getWidth())), yOffset
                + (zoomCenter.getY() * (mapSize.getHeight() / oldMapSize
                        .getHeight()))), newZoom);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.JXMapViewer#setZoomAndCenter(java.awt.geom.Point2D,
     * int)
     */
    public void setZoomAndCenter(Point2D center, int newZoom) {
        if (newZoom != this.zoom) {
            int oldZoom = this.zoom;
            this.zoom = newZoom;
            this.firePropertyChange("zoom", oldZoom, newZoom);
            needsRepaint();
        }
        Dimension mapSize = getTileFactory().getMapSize(newZoom);
        setCenter(center);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getZoom()
     */
    public int getZoom() {
        return this.zoom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getAddressLocation()
     */
    public GeoPosition getAddressLocation() {
        return addressLocation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.JXMapViewer#setAddressLocation(org.jdesktop.swingx
     * .mapviewer.GeoPosition)
     */
    public void setAddressLocation(GeoPosition addressLocation) {
        GeoPosition old = getAddressLocation();
        this.addressLocation = addressLocation;
        setCenter(getTileFactory().geoToPixel(addressLocation, getZoom()));
        firePropertyChange("addressLocation", old, getAddressLocation());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#recenterToAddressLocation()
     */
    public void recenterToAddressLocation() {
        setCenter(getTileFactory()
                .geoToPixel(getAddressLocation(), getZoom()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#isDrawTileBorders()
     */
    public boolean isDrawTileBorders() {
        return drawTileBorders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setDrawTileBorders(boolean)
     */
    public void setDrawTileBorders(boolean drawTileBorders) {
        boolean old = isDrawTileBorders();
        this.drawTileBorders = drawTileBorders;
        firePropertyChange("drawTileBorders", old, isDrawTileBorders());
        needsRepaint();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#isPanEnabled()
     */
    public boolean isPanEnabled() {
        return panEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setPanEnabled(boolean)
     */
    public void setPanEnabled(boolean panEnabled) {
        boolean old = isPanEnabled();
        this.panEnabled = panEnabled;
        firePropertyChange("panEnabled", old, isPanEnabled());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#isZoomEnabled()
     */
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setZoomEnabled(boolean)
     */
    public void setZoomEnabled(boolean zoomEnabled) {
        boolean old = isZoomEnabled();
        this.zoomEnabled = zoomEnabled;
        firePropertyChange("zoomEnabled", old, isZoomEnabled());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.JXMapViewer#setCenterPosition(org.jdesktop.swingx
     * .mapviewer.GeoPosition)
     */
    public void setCenterPosition(GeoPosition geoPosition) {
        GeoPosition oldVal = getCenterPosition();
        setCenter(getTileFactory().geoToPixel(geoPosition, zoom));
        GeoPosition newVal = getCenterPosition();
        firePropertyChange("centerPosition", oldVal, newVal);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getCenterPosition()
     */
    public GeoPosition getCenterPosition() {
        return getTileFactory().pixelToGeo(getCenter(), zoom);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getTileFactory()
     */
    public TileFactory getTileFactory() {
        return factory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.JXMapViewer#setTileFactory(org.jdesktop.swingx.
     * mapviewer.TileFactory)
     */
    public void setTileFactory(TileFactory factory) {
        if (this.factory != null) {
            this.factory.removeRequiredTiles(getUniqueViewName());
            this.factory = null;
        }

        this.factory = factory;
        this.setZoom(factory.getInfo().getDefaultZoomLevel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getLoadingImage()
     */
    public Image getLoadingImage() {
        return loadingImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setLoadingImage(java.awt.Image)
     */
    public void setLoadingImage(Image loadingImage) {
        this.loadingImage = loadingImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#getCenter()
     */
    public Point2D getCenter() {
        return center;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setCenter(java.awt.geom.Point2D)
     */
    public void setCenter(Point2D newCenter) {
        if (newCenter.equals(center)) {
            return;
        }

        Point2D oldCenter = center;
        if (isRestrictOutsidePanning()) {
            Insets insets = getInsets();
            int viewportHeight = getHeight() - insets.top - insets.bottom;
            int viewportWidth = getWidth() - insets.left - insets.right;
            // don't let the user pan over the top edge
            Rectangle newVP = calculateViewportBounds(newCenter);
            if (newVP.getY() < 0) {
                double centerY = viewportHeight / 2;
                newCenter = new Point2D.Double(newCenter.getX(), centerY);
            }
            // don't let the user pan over the left edge

            if (!isHorizontalWrapped() && newVP.getX() < 0) {
                double centerX = viewportWidth / 2;
                newCenter = new Point2D.Double(centerX, newCenter.getY());
            }
            // don't let the user pan over the bottom edge

            Dimension mapSize = getTileFactory().getMapSize(getZoom());
            int mapHeight = (int) mapSize.getHeight()
                    * getTileFactory().getTileSize(getZoom());
            if (newVP.getY() + newVP.getHeight() > mapHeight) {
                double centerY = mapHeight - viewportHeight / 2;
                newCenter = new Point2D.Double(newCenter.getX(), centerY);
            }
            // don't let the user pan over the right edge

            int mapWidth = (int) mapSize.getWidth()
                    * getTileFactory().getTileSize(getZoom());
            if (!isHorizontalWrapped()
                    && (newVP.getX() + newVP.getWidth() > mapWidth)) {
                double centerX = mapWidth - viewportWidth / 2;
                newCenter = new Point2D.Double(centerX, newCenter.getY());
            }
            // if map is to small then just center it vert

            if (mapHeight < newVP.getHeight()) {
                double centerY = mapHeight / 2;// viewportHeight/2;// -
                // mapHeight/2;
                newCenter = new Point2D.Double(newCenter.getX(), centerY);
            }
            // if map is too small then just center it horiz

            if (!isHorizontalWrapped() && mapWidth < newVP.getWidth()) {
                double centerX = mapWidth / 2;
                newCenter = new Point2D.Double(centerX, newCenter.getY());
            }

        }

        GeoPosition oldGP = this.getCenterPosition();
        center = newCenter;
        firePropertyChange("center", oldCenter, center);
        firePropertyChange("centerPosition", oldGP, this.getCenterPosition());

        calcRequiredTiles();

        needsRepaint();

    }

    private volatile boolean needToCalcMapTiles = false;
    private volatile int currentTilesZoom = -10;
    private Rectangle currentTilesBounds = new Rectangle(0, 0, 0, 0);
    private LinkedList<Tile> requiredTiles;
    private final Integer requiredTilesMutex = new Integer(5);

    private void calcRequiredTiles() {
        try {
            int z_zoom = getZoom();
            int tileSize = getTileFactory().getTileSize(z_zoom);
            LinkedList<Tile> newRequiredTiles;
            boolean sameTiles = false;
            needToCalcMapTiles = false;

            // calculate the "visible" viewport area in tiles
            Rectangle viewportBounds = getViewportBounds();
            synchronized (requiredTilesMutex) {
                if ((z_zoom == currentTilesZoom)
                        && currentTilesBounds.contains(viewportBounds)) {
                    sameTiles = true;
                }
                tileWindowCoords.clear();
            }

            ignoreImageUpdates = true;

            /*
             * calculate x and y map tile indexes for the top left tile. These
             * are indexes for the full tile space of the map at this newZoom
             * level.
             */
            int topLeftTile_MapTileIndex_X = (viewportBounds.x / tileSize);
            int topLeftTile_MapTileIndex_Y = (viewportBounds.y / tileSize);

            int numWide = (viewportBounds.x + viewportBounds.width - topLeftTile_MapTileIndex_X
                    * tileSize)
                    / tileSize + 1;
            int numHigh = (viewportBounds.y + viewportBounds.height - topLeftTile_MapTileIndex_Y
                    * tileSize)
                    / tileSize + 1;

            currentTilesBounds = new Rectangle(topLeftTile_MapTileIndex_X
                    * tileSize, topLeftTile_MapTileIndex_Y * tileSize,
                    numWide * tileSize, numHigh * tileSize);

            newRequiredTiles = new LinkedList<Tile>();
            for (int currentTile_WindowTileIndex_X = 0; currentTile_WindowTileIndex_X < numWide; currentTile_WindowTileIndex_X++) {
                for (int currentTile_WindowTileIndex_Y = 0; currentTile_WindowTileIndex_Y < numHigh; currentTile_WindowTileIndex_Y++) {
                    int currentTile_MapTileIndex_X = currentTile_WindowTileIndex_X
                            + topLeftTile_MapTileIndex_X;
                    int currentTile_MapTileIndex_Y = currentTile_WindowTileIndex_Y
                            + topLeftTile_MapTileIndex_Y;
                    int currentTile_WindowCoordsX = currentTile_MapTileIndex_X
                            * tileSize - viewportBounds.x;
                    int currentTile_WindowCoordsY = currentTile_MapTileIndex_Y
                            * tileSize - viewportBounds.y;
                    Tile tile;
                    synchronized (requiredTilesMutex) {
                        tile = currentTiles.get(getTileFactory().getTileKey(
                                currentTile_MapTileIndex_X,
                                currentTile_MapTileIndex_Y, z_zoom));
                    }
                    if (tile == null) {
                        tile = getTileFactory().getTileInstance(
                                currentTile_MapTileIndex_X,
                                currentTile_MapTileIndex_Y, z_zoom);
                        tile.addPropertyChangeListener("image",
                                tileImageChangeListener);
                    }

                    newRequiredTiles.add(tile);
                    Point pt = new Point(currentTile_WindowCoordsX,
                            currentTile_WindowCoordsY);
                    tileWindowCoords.put(tile.getKey(), pt);
                }

            }
            if (!sameTiles) {
                synchronized (requiredTilesMutex) {
                    currentTiles.clear();
                    for (Tile tile : newRequiredTiles) {
                        currentTiles.put(tile.getKey(), tile);
                    }

                    requiredTiles = newRequiredTiles;
                    currentTilesZoom = z_zoom;
                }

                if (shownOnce) {
                    requestMapTiles();
                } else {
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "During update of needed tiles", e);
        }

        ignoreImageUpdates = false;
    }

    private void requestMapTiles() {
        synchronized (requiredTilesMutex) {
            if (requiredTiles != null) {
                factory.setRequiredTiles(requiredTiles, getUniqueViewName());
                requiredTiles = null;
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#calculateZoomFrom(java.util.Set)
     */
    public void calculateZoomFrom(Set<GeoPosition> positions) {
        // u.p("calculating a newZoom based on: ");
        // u.p(positions);
        if (positions.size() < 1) {
            return;
        }

        int newZoom = getTileFactory().getInfo().getMinimumZoomLevel();
        Rectangle2D rect = generateBoundingRect(positions, newZoom);
        // Rectangle2D viewport = map.getViewportBounds();
        Point2D newCenter;

        do {
            // u.p("not contained");
            newCenter = new Point2D.Double(rect.getX() + rect.getWidth() / 2,
                    rect.getY() + rect.getHeight() / 2);

            if (calculateViewportBounds(newCenter).contains(rect)) {
                // u.p("did it finally");
                break;
            }

            newZoom = newZoom + 1;
            if (newZoom > getTileFactory().getInfo().getMaximumZoomLevel()) {
                newZoom = getTileFactory().getInfo().getMaximumZoomLevel();
                break;
            }

            rect = generateBoundingRect(positions, newZoom);
        } while (true);
        setZoomAndCenter(newCenter, newZoom);
    }

    private Rectangle2D generateBoundingRect(
            final Set<GeoPosition> positions, final int zoom) {
        final Dimension mapSize = getTileFactory().getMapSize(zoom);
        Rectangle2D rect = null;
        final int ts = getTileFactory().getInfo().getTileSize(zoom);
        final double mapHeight = mapSize.getHeight() * ts;
        final double mapWidth = mapSize.getWidth() * ts;
        final Rectangle2D view = new Rectangle2D.Double(0, 0, mapWidth,
                mapHeight);

        for (GeoPosition pos : positions) {
            final Point2D point = getTileFactory().geoToPixel(pos, zoom);
            if (view.contains(point)) {
                if (rect == null) {
                    rect = new Rectangle2D.Double(point.getX(), point.getY(),
                            1, 1);
                } else {
                    rect.add(point);
                }

            }
        }
        if (rect == null) {
            return view;
        } else {
            return rect;
        }

    }

    // a property change listener which forces repaints when tiles finish
    // loading
    private TileImageChangeListener tileImageChangeListener = new TileImageChangeListener();
    private volatile boolean ignoreImageUpdates = false;

    private final class TileImageChangeListener implements
            PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (!ignoreImageUpdates) {
                Tile tile = (Tile) evt.getSource();
                if (!currentTiles.containsKey(tile.getKey())) {
                    return;
                }
                needsRepaint();
            }
        }
    }

    // used to pan using the arrow keys
    private class PanKeyListener extends KeyAdapter {

        private static final int OFFSET = 10;
        private static final int MULTIPLIER = 10;

        @Override
        public void keyTyped(KeyEvent e) {
            super.keyTyped(e);
            int oldzoom = getZoom();
            int newzoom = oldzoom;
            switch (e.getKeyChar()) {
            case '+':
                newzoom--;
                break;
            case '-':
                newzoom++;
                break;
            }
            if (newzoom != oldzoom) {
                setZoom(newzoom);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int delta_x = 0;
            int delta_y = 0;

            switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                delta_x = -OFFSET;
                break;
            case KeyEvent.VK_RIGHT:
                delta_x = OFFSET;
                break;
            case KeyEvent.VK_UP:
                delta_y = -OFFSET;
                break;
            case KeyEvent.VK_DOWN:
                delta_y = OFFSET;
                break;

            }
            if (delta_x != 0 || delta_y != 0) {
                int mod = e.getModifiers();
                if ((mod & KeyEvent.CTRL_MASK) != 0
                        || (mod & KeyEvent.CTRL_DOWN_MASK) != 0) {
                    delta_x *= MULTIPLIER;
                    delta_y *= MULTIPLIER;
                }
                if ((mod & KeyEvent.SHIFT_MASK) != 0
                        || (mod & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                    delta_x *= MULTIPLIER;
                    delta_y *= MULTIPLIER;
                }
                Rectangle bounds = getViewportBounds();
                double x = bounds.getCenterX() + delta_x;
                double y = bounds.getCenterY() + delta_y;
                setCenter(new Point2D.Double(x, y));
            }

        }
    }

    // used to pan using press and drag mouse gestures

    private Point2D convertPointToPoint2D(Point p) {
        Rectangle bounds = getViewportBounds();
        double x = bounds.getX() + p.getX();
        double y = bounds.getY() + p.getY();
        return new Point2D.Double(x, y);
    }

    private class PanMouseInputListener implements MouseInputListener {

        Point prev;

        public void mousePressed(MouseEvent evt) {
            // remember the point... just in case panning happens.
            prev = evt.getPoint();
            // if the middle mouse button is clicked, recenter the view
            if (isRecenterOnClickEnabled()
                    && (SwingUtilities.isMiddleMouseButton(evt) || (SwingUtilities
                            .isLeftMouseButton(evt) && evt.getClickCount() == 2))) {
                recenterMap(evt);
            }
        }

        private void recenterMap(MouseEvent evt) {
            setCenter(convertPointToPoint2D(evt.getPoint()));
        }

        public void mouseDragged(MouseEvent evt) {
            if (isPanEnabled() && prev != null) {
                Point current = evt.getPoint();
                double x = getCenter().getX() - (current.x - prev.x);
                double y = getCenter().getY() - (current.y - prev.y);
                if (!isNegativeYAllowed) {
                    if (y < 0) {
                        y = 0;
                    }
                }
                int maxHeight = (int) (getTileFactory().getMapSize(getZoom())
                        .getHeight() * getTileFactory()
                        .getTileSize(getZoom()));
                if (y > maxHeight) {
                    y = maxHeight;
                }
                prev = current;
                setCenter(new Point2D.Double(x, y));
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        public void mouseReleased(MouseEvent evt) {
            prev = null;
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        public void mouseMoved(MouseEvent e) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    requestFocusInWindow();
                }
            });
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    // zooms using the mouse wheel

    private class ZoomMouseWheelListener implements MouseWheelListener {

        public void mouseWheelMoved(MouseWheelEvent e) {
            if (isZoomEnabled()) {
                setZoom(convertPointToPoint2D(e.getPoint()), getZoom()
                        + e.getWheelRotation());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#isRestrictOutsidePanning()
     */
    public boolean isRestrictOutsidePanning() {
        return restrictOutsidePanning;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setRestrictOutsidePanning(boolean)
     */
    public void setRestrictOutsidePanning(boolean restrictOutsidePanning) {
        this.restrictOutsidePanning = restrictOutsidePanning;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#isHorizontalWrapped()
     */
    public boolean isHorizontalWrapped() {
        return horizontalWrapped;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setHorizontalWrapped(boolean)
     */
    public void setHorizontalWrapped(boolean horizontalWrapped) {
        this.horizontalWrapped = horizontalWrapped;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setDrawMapTiles(boolean)
     */
    public void setDrawMapTiles(boolean drawMapTiles) {
        this.drawMapTiles = drawMapTiles;
    }

    public boolean isDrawMapTiles() {
        return this.drawMapTiles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.JXMapViewer#convertGeoPositionToPoint(org.jdesktop
     * .swingx.mapviewer.GeoPosition)
     */
    public Point2D convertGeoPositionToPoint(GeoPosition pos) {
        // convert from geo to world bitmap
        Point2D pt = getTileFactory().geoToPixel(pos, getZoom());
        // convert from world bitmap to local
        Rectangle bounds = getViewportBounds();
        return new Point2D.Double(pt.getX() - bounds.getX(), pt.getY()
                - bounds.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.JXMapViewer#convertPointToGeoPosition(java.awt.
     * geom.Point2D)
     */
    public GeoPosition convertPointToGeoPosition(Point2D pt) {
        // convert from local to world bitmap
        Rectangle bounds = getViewportBounds();
        Point2D pt2 = new Point2D.Double(pt.getX() + bounds.getX(), pt.getY()
                + bounds.getY());
        // convert from world bitmap to geo
        GeoPosition pos = getTileFactory().pixelToGeo(pt2, getZoom());
        return pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.JXMapViewer#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            needToCalcMapTiles = true;
            // If maximized, need to update map.
            needsRepaint();

        }

    }
}
