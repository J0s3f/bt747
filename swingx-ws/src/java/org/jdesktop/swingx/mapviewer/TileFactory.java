/*
 * TileFactory.java
 *
 * Created on March 17, 2006, 8:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.jdesktop.swingx.mapviewer;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import java.util.Collection;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;

/**t
 * A class that can produce tiles and convert coordinates to pixels
 * @author joshy
 */
public abstract class TileFactory /*TODO extends AbstractBean*/ {

    private TileFactoryInfo info;

    /** 
     * Creates a new instance of TileFactory
     * @param info a TileFactoryInfo to configure this TileFactory 
     */
    protected TileFactory(TileFactoryInfo info) {
        this.info = info;
    }

    /**
     * Gets the size of an edge of a tile in pixels at the current zoom level. Tiles must be square.
     * @param zoom the current zoom level
     * @return the size of an edge of a tile in pixels
     */
    public int getTileSize(int zoom) {
        return getInfo().getTileSize(zoom);
    }

    /**
     * Returns a Dimension containing the width and height of the map, 
     * in tiles at the
     * current zoom level.
     * So a Dimension that returns 10x20 would be 10 tiles wide and 20 tiles
     * tall. These values can be multipled by getTileSize() to determine the
     * pixel width/height for the map at the given zoom level
     * @return the size of the world bitmap in tiles
     * @param zoom the current zoom level
     */
    public Dimension getMapSize(int zoom) {
        return GeoUtil.getMapSize(zoom, getInfo());
    }

    /**
     *
     * Return a tile that was previously requested using setRequiredTiles
     *
     */
    public abstract Tile getTile(String key, String mapName);

    public String getTileKey(int x, int y, int zoom) {
        return Tile.getKey(info.getName(), x, y, zoom, info.getTileUrl(x, y, zoom), info.getTileBaseKey(x, y, zoom));
    }

    ;

    public Tile getTileInstance(int x, int y, int zoom) {
        return new Tile(info.getName(), x, y, zoom, info.getTileUrl(x, y, zoom), info.getTileBaseKey(x, y, zoom));
    }

    ;

    /**
     * Convert a pixel in the world bitmap at the specified
     * zoom level into a GeoPosition
     * @param pixelCoordinate a Point2D representing a pixel in the world bitmap
     * @param zoom the zoom level of the world bitmap
     * @return the converted GeoPosition
     */
    public GeoPosition pixelToGeo(Point2D pixelCoordinate, int zoom) {
        int minzoom = getInfo().getMinimumZoomLevel();
        int extrazoom = minzoom - zoom;
        int tilezoom = zoom;
        if (extrazoom <= 0) {
            extrazoom = 0;
        } else {
            tilezoom = zoom + extrazoom;
        }
        int scale = 1 << extrazoom;
        Point2D p = (Point2D) pixelCoordinate.clone();
        if (scale != 1) {
            p.setLocation(p.getX() / scale, p.getY() / scale);
        }

        return getInfo().getPosition(p, tilezoom);
    }

    /**
     * Convert a GeoPosition to a pixel position in the world bitmap
     * a the specified zoom level.
     * @param c a GeoPosition
     * @param zoom the zoom level to extract the pixel coordinate for
     * @return the pixel point
     */
    public Point2D geoToPixel(GeoPosition c, int zoom) {
        int minzoom = getInfo().getMinimumZoomLevel();
        int extrazoom = minzoom - zoom;
        int tilezoom = zoom;
        if (extrazoom <= 0) {
            extrazoom = 0;
        } else {
            tilezoom = zoom + extrazoom;
        }
        int scale = 1 << extrazoom;

        Point2D p = GeoUtil.getBitmapCoordinate(c, tilezoom, getInfo());
        if (scale != 1) {
            p.setLocation(p.getX() * scale, p.getY() * scale);
        }
        return p;
    }

    /**
     * Get the TileFactoryInfo describing this TileFactory
     * @return a TileFactoryInfo
     */
    public TileFactoryInfo getInfo() {
        return info;
    }

    /**
     * Set the collection of required tiles. The TileFactory will ensure that these tiles are loaded ASAP.
     *
     * @param requiredTiles
     */
    public abstract void setRequiredTiles(Collection<Tile> requiredTiles, String mapName);

    /** Called when a map is no longer referencing the factory for eventual cleanup */
    public void removeRequiredTiles(String mapName) {
    }

    ;
}
