/*
 * Tile.java
 *
 * Created on March 14, 2006, 4:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.jdesktop.swingx.mapviewer;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.jdesktop.beans.AbstractBean;

/**
 * The Tile class represents a particular square image
 * piece of the world bitmap at a particular zoom level.
 * @author joshy
 */
public class Tile extends AbstractBean {

    private static final Logger LOG = Logger.getLogger(Tile.class.getName());


    static {
        LOG.setLevel(Level.OFF);
    }
    //Most Recently Accessed Tiles. These are strong references, to prevent reloading
    //of most recently used tiles.
    //private static final Map<URI, BufferedImage> recentlyAccessed = new HashMap<URI, BufferedImage>();
    //private static final TileCache cache = new TileCache();
    /**
     * If an error occurs while loading a tile, store the exception
     * here.
     */
    private Throwable error;
    /**
     * The url of the image to load for this tile
     */
    private String url;
    /**
     * The zoom level this tile is for
     */
    private int zoom,  x,  y;
    private final String key;
    /**
     * The image loaded for this Tile
     */
    BufferedImage image = null;
    private boolean needsToBeLoaded;
    private boolean needsToBeDownLoaded;

    /**
     * Create a new Tile at the specified tile point and zoom level
     * @param location
     * @param zoom
     */
    /*
    public Tile(int x, int y, int zoom) {
    this(null,x,y,zoom);
    }
     * */
    public Tile(String maptype, int x, int y, int zoom, final String url, final String baseKey) {
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.key = getKey(maptype, x, y, zoom, url, baseKey);
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    private final static String getExt(final String url) {
        if (url != null) {
            final int idx = url.lastIndexOf(".");
            final int idxSplit = url.lastIndexOf("/");
            if (idx > 0 && (idx > idxSplit)) { // Test includes idxSplit < 0
                return url.substring(idx);
            }
        }
        return ".img";
    }

    public static String getKey(String mapname, int x, int y, int zoom, String url, final String baseKey) {
        String newKey;
        // newKey = "zoom_" + zoom + "_x_" + x + "_y_" + y;
        newKey = baseKey;
        if (baseKey.indexOf('.') < 0) {
            newKey += getExt(url);
        }
        if (mapname != null && mapname.length() != 0) {
            newKey = mapname + File.separatorChar + newKey;
        }
        return newKey;
    }

    /**
     * Returns the last error in a possible chain of errors that occured during the loading of the tile
     */
    public Throwable getUnrecoverableError() {
        return error;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the Throwable tied to any error that may have ocurred while loading the tile. This error may change several times if multiple
     * errors occur
     * 
     * @return
     */
    public Throwable getLoadingError() {
        return error;
    }

    public boolean setNeedsToBeLoaded(boolean state) {
        boolean prev;
        synchronized (this) {
            prev = needsToBeLoaded;
            needsToBeLoaded = state;
        }
        return prev;
    }

    public boolean setNeedsToBeDownLoaded(boolean state) {
        boolean prev;
        synchronized (this) {
            prev = needsToBeDownLoaded;
            needsToBeDownLoaded = state;
        }
        return prev;
    }

    public boolean needsToBeDownLoaded() {
        return needsToBeDownLoaded;
    }

    /**
     * Sets the Image associated with this Tile.
     */
    public void setImage(BufferedImage img) {
        // don't re-notify loaded if no change.
        if (image == img) {
            return;
        }
        image = img;
        firePropertyChangeOnEDT("image", null, image);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("image has changed for tile: " + getKey());
        }
    }

    /**
     * Returns the Image associated with this Tile.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @return the zoom level that this tile belongs in
     */
    public int getZoom() {
        return zoom;
    }

    //////////////////JavaOne Hack///////////////////
    private PropertyChangeListener uniqueListener = null;

    /**
     * Adds a single property change listener. If a listener has been previously
     * added then it will be replaced by the new one.
     * @param propertyName
     * @param listener
     */
    public void addUniquePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (uniqueListener != null && uniqueListener != listener) {
            removePropertyChangeListener(propertyName, uniqueListener);
        }
        if (uniqueListener != listener) {
            uniqueListener = listener;
            addPropertyChangeListener(propertyName, uniqueListener);
        }
    }

    /////////////////End JavaOne Hack/////////////////
    /**
     */
    void firePropertyChangeOnEDT(final String propertyName, final Object oldValue, final Object newValue) {
        if (!EventQueue.isDispatchThread()) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("enqueuing propertychange for event thread for tile: " + getKey());
            }
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    if (LOG.isLoggable(Level.INFO)) {
                        LOG.info("firing property change on event thread for tile " + getKey());
                    }
                    firePropertyChange(propertyName, oldValue, newValue);
                }
            });
        } else {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("firing property change on event thread for tile " + getKey());
            }
            firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Gets the URL of this tile.
     * @return
     */
    public String getURL() {
        return url;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tile)) {
            return false;
        }
        Tile otherTile = (Tile) other;
        return otherTile.getKey().equals(getKey());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
    }
}
