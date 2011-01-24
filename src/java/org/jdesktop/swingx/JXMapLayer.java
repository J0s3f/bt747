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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.bmng.SLMapServerInfo;
import org.jdesktop.swingx.painter.AbstractPainter;

/**
 * A tile oriented map component that can easily be used with tile sources on
 * the web like Google and Yahoo maps, satellite data such as NASA imagery,
 * and also with file based sources like pre-processed NASA images. A known
 * map provider can be used with the SLMapServerInfo, which will connect to a
 * 2km resolution version of NASA's Blue Marble Next Generation imagery.
 * 
 * @see SLMapServerInfo for more information.
 * 
 *      The JXMapLayer implements only the map rendering part of the
 *      JXMapViewer.
 * 
 *      Note, the JXMapViewer has three center point properties. The
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
public class JXMapLayer extends AbstractPainter<JXMapViewer> {

    private static final Logger LOG = Logger.getLogger(JXMapLayer.class
            .getName());

    static {
        LOG.setLevel(Level.SEVERE);
    }

    /**
     * The overlay to delegate to for painting the "foreground" of the map
     * component. This would include painting waypoints, day/night, etc. Also
     * receives mouse events.
     */
    private Image loadingImage;
    private HashMap<String, Tile> currentTiles = new HashMap<String, Tile>();
    private HashMap<String, Point> tileWindowCoords = new HashMap<String, Point>();

    /**
     * Create a new JXMapViewer. By default it will use the EmptyTileFactory
     */
    public JXMapLayer() {
        setCacheable(false);
        try {
            URL url = this.getClass().getResource(
                    "mapviewer/resources/loading.png");
            this.setLoadingImage(ImageIO.read(url));
        } catch (Throwable ex) {
            BufferedImage img = new BufferedImage(16, 16,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(Color.black);
            g2.fillRect(0, 0, 16, 16);
            g2.dispose();
            this.setLoadingImage(img);
        }
    }

    private int prevWidth = 0;
    private int prevHeight = 0;
    private Rectangle prevViewPort = new Rectangle();

    protected void doPaint(Graphics2D g, JXMapViewer component, int width,
            int height) {
        if (prevWidth != width || prevHeight != height
                || !prevViewPort.equals(component.getViewportBounds())) {
            prevWidth = width;
            prevHeight = height;
            prevViewPort = new Rectangle(component.getViewportBounds());
            needToCalcMapTiles = true;
        }
        drawMapTiles(g, component);
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
    protected synchronized void drawMapTiles(final Graphics g,
            JXMapViewer mapViewer) {
        try {
            final Rectangle viewportBounds = mapViewer.getViewportBounds();
            final int zoom = mapViewer.getZoom();

            // If we're not drawing map tiles currently, nothing to do here...
            if (!mapViewer.isDrawMapTiles()) {
                return;
            }
            if (needToCalcMapTiles) {
                calcRequiredTiles(mapViewer.getTileFactory(), viewportBounds,
                        zoom, mapViewer.getUniqueViewName());
            }

            boolean hasTiles;

            synchronized (requiredTilesMutex) {
                hasTiles = requiredTiles != null;
            }
            if (hasTiles) {
                requestMapTiles(mapViewer.getTileFactory(), mapViewer
                        .getUniqueViewName());
            }

            // int newZoom = getZoom();
            int tileSize = mapViewer.getTileFactory().getTileSize(zoom);
            for (Tile tile : currentTiles.values()) {
                Point point;
                synchronized (requiredTilesMutex) {
                    point = tileWindowCoords.get(tile.getKey());
                }
                if(point == null) {
                    // Skip tile - not shown.
                    continue;
                }
                BufferedImage tileImage = tile.getImage();
                if (tileImage == null) {
                    if (mapViewer.isOpaque()) {
                        g.setColor(mapViewer.getBackground());
                        g.fillRect(point.x, point.y, tileSize, tileSize);
                    }
                } else {
                    g.drawImage(tileImage, point.x, point.y, null);
                }

                if (mapViewer.isDrawTileBorders()) {
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
            e.printStackTrace();
            setDirty(true);
        }
    }

    /**
     * A property for an image which will be display when an image is still
     * loading.
     * 
     * @return the current property value
     */
    public Image getLoadingImage() {
        return loadingImage;
    }

    /**
     * A property for an image which will be display when an image is still
     * loading.
     * 
     * @param loadingImage
     *            the new property value
     */
    public void setLoadingImage(Image loadingImage) {
        this.loadingImage = loadingImage;
    }

    private volatile boolean needToCalcMapTiles = false;
    private volatile int currentTilesZoom = -10;
    private Rectangle currentTilesBounds = new Rectangle(0, 0, 0, 0);
    private LinkedList<Tile> requiredTiles;
    private final Integer requiredTilesMutex = new Integer(5);

    private synchronized void calcRequiredTiles(TileFactory tileFactory,
            Rectangle viewportBounds, int zoom, String viewName) {
        try {
            int tileSize = tileFactory.getTileSize(zoom);
            LinkedList<Tile> newRequiredTiles;
            boolean sameTiles = false;
            needToCalcMapTiles = false;

            synchronized (requiredTilesMutex) {
                if ((zoom == currentTilesZoom)
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
                        tile = currentTiles.get(tileFactory.getTileKey(
                                currentTile_MapTileIndex_X,
                                currentTile_MapTileIndex_Y, zoom));
                    }
                    if (tile == null) {
                        tile = tileFactory.getTileInstance(
                                currentTile_MapTileIndex_X,
                                currentTile_MapTileIndex_Y, zoom);
                        tile.addPropertyChangeListener("image",
                                tileImageChangeListener);
                    }

                    newRequiredTiles.add(tile);
                    final Point pt = new Point(currentTile_WindowCoordsX,
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
                    currentTilesZoom = zoom;
                }

                requestMapTiles(tileFactory, viewName);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "During update of needed tiles", e);
        }

        ignoreImageUpdates = false;
    }

    private void requestMapTiles(final TileFactory factory,
            final String viewName) {
        synchronized (requiredTilesMutex) {
            if (requiredTiles != null) {
                factory.setRequiredTiles(requiredTiles, viewName);
                requiredTiles = null;
            }

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
                setDirty(true);
            }
        }
    }
}
