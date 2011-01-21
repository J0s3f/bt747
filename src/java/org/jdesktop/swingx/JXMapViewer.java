/**
 * 
 */
package org.jdesktop.swingx;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.DesignMode;
import java.util.Set;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * @author Mario
 *
 */
public abstract class JXMapViewer extends JXPanel implements DesignMode
{
    /**
     * Indicate that the component is being used at design time, such as in a
     * visual editor like NetBeans' Matisse
     * 
     * @param b
     *            indicates if the component is being used at design time
     */
    public abstract void setDesignTime(boolean b);

    /**
     * Indicates whether the component is being used at design time, such as in
     * a visual editor like NetBeans' Matisse
     * 
     * @return boolean indicating if the component is being used at design time
     */
    public abstract boolean isDesignTime();

    /**
     * Sets the map overlay. This is a Painter which will paint on top of the
     * map. It can be used to draw waypoints, lines, or static overlays like
     * text messages.
     * 
     * @param overlay
     *            the map overlay to use
     * @see org.jdesktop.swingx.painters.Painter
     */
    @SuppressWarnings("unchecked")
    public abstract void setOverlayPainter(Painter overlay);

    /**
     * Gets the current map overlay
     *
     * @return the current map overlay
     */
    public abstract Painter getOverlayPainter();

    /**
     * Returns the bounds of the viewport in pixels. This can be used to
     * transform points into the world bitmap coordinate space.
     *
     * @return the bounds in <em>pixels</em> of the "view" of this map
     */
    public abstract Rectangle getViewportBounds();

    /**
     * Sets whether the map should recenter itself on mouse clicks (middle mouse
     * clicks?)
     *
     * @param b
     *            if should recenter
     */
    public abstract void setRecenterOnClickEnabled(boolean b);

    /**
     * Indicates if the map should recenter itself on mouse clicks.
     *
     * @return boolean indicating if the map should recenter itself
     */
    public abstract boolean isRecenterOnClickEnabled();

    /**
     * Set the current newZoom level
     *
     * @param newZoom
     *            the new newZoom level
     */
    public abstract void setZoom(int newZoom);

    /**
     * Set the current newZoom level and uses as zoomcenter the given position
     *
     * @param newZoom
     *            the new newZoom level
     */
    public abstract void setZoom(Point2D zoomCenter, int newZoom);

    public abstract void setZoomAndCenter(Point2D center, int newZoom);

    /**
     * Gets the current newZoom level
     *
     * @return the current newZoom level
     */
    public abstract int getZoom();

    /**
     * Gets the current address location of the map. This property does not
     * change when the user pans the map. This property is bound.
     *
     * @return the current map location (address)
     */
    public abstract GeoPosition getAddressLocation();

    /**
     * Gets the current address location of the map
     *
     * @param addressLocation
     *            the new address location
     * @see getAddressLocation()
     */
    public abstract void setAddressLocation(GeoPosition addressLocation);

    /**
     * Re-centers the map to have the current address location be at the center
     * of the map, accounting for the map's width and height.
     *
     * @see getAddressLocation
     */
    public abstract void recenterToAddressLocation();

    /**
     * Indicates if the tile borders should be drawn. Mainly used for debugging.
     *
     * @return the value of this property
     */
    public abstract boolean isDrawTileBorders();

    /**
     * Set if the tile borders should be drawn. Mainly used for debugging.
     *
     * @param drawTileBorders
     *            new value of this drawTileBorders
     */
    public abstract void setDrawTileBorders(boolean drawTileBorders);

    /**
     * A property indicating if the map should be pannable by the user using the
     * mouse.
     *
     * @return property value
     */
    public abstract boolean isPanEnabled();

    /**
     * A property indicating if the map should be pannable by the user using the
     * mouse.
     *
     * @param panEnabled
     *            new property value
     */
    public abstract void setPanEnabled(boolean panEnabled);

    /**
     * A property indicating if the map should be zoomable by the user using the
     * mouse wheel.
     *
     * @return the current property value
     */
    public abstract boolean isZoomEnabled();

    /**
     * A property indicating if the map should be zoomable by the user using the
     * mouse wheel.
     *
     * @param zoomEnabled
     *            the new value of the property
     */
    public abstract void setZoomEnabled(boolean zoomEnabled);

    /**
     * A property indicating the center position of the map
     *
     * @param geoPosition
     *            the new property value
     */
    public abstract void setCenterPosition(GeoPosition geoPosition);

    /**
     * A property indicating the center position of the map
     *
     * @return the current center position
     */
    public abstract GeoPosition getCenterPosition();

    /**
     * Get the current factory
     *
     * @return the current property value
     */
    public abstract TileFactory getTileFactory();

    /**
     * Set the current tile factory
     *
     * @param factory
     *            the new property value
     */
    public abstract void setTileFactory(TileFactory factory);

    /**
     * A property for an image which will be display when an image is still
     * loading.
     *
     * @return the current property value
     */
    public abstract Image getLoadingImage();

    /**
     * A property for an image which will be display when an image is still
     * loading.
     *
     * @param loadingImage
     *            the new property value
     */
    public abstract void setLoadingImage(Image loadingImage);

    /**
     * Gets the current pixel center of the map. This point is in the global
     * bitmap coordinate system, not as lat/longs.
     *
     * @return the current center of the map as a pixel value
     */
    public abstract Point2D getCenter();

    /**
     * Sets the new center of the map in pixel coordinates.
     *
     * @param center
     *            the new center of the map in pixel coordinates
     */
    public abstract void setCenter(Point2D newCenter);

    /**
     * Calculates a newZoom level so that all points in the specified set will be
     * visible on screen. This is useful if you have a bunch of points in an
     * area like a city and you want to newZoom out so that the entire city and
     * it's points are visible without panning.
     * 
     * @param positions
     *            A set of GeoPositions to calculate the new newZoom from
     */
    public abstract void calculateZoomFrom(Set<GeoPosition> positions);

    public abstract boolean isRestrictOutsidePanning();

    public abstract void setRestrictOutsidePanning(boolean restrictOutsidePanning);

    public abstract boolean isHorizontalWrapped();

    public abstract void setHorizontalWrapped(boolean horizontalWrapped);

    public abstract String getUniqueViewName();
    
    /**
     * Specifies whether or not the image tiles should actually be painted.
     */
    private boolean drawMapTiles = true;

    public void setDrawMapTiles(boolean drawMapTiles) {
        this.drawMapTiles = drawMapTiles;
    }

    public boolean isDrawMapTiles() {
        return drawMapTiles;
    }

    /**
     * Converts the specified GeoPosition to a point in the JXMapViewer's local
     * coordinate space. This method is especially useful when drawing lat/long
     * positions on the map.
     *
     * @param pos
     *            a GeoPosition on the map
     * @return the point in the local coordinate space of the map
     */
    public abstract Point2D convertGeoPositionToPoint(GeoPosition pos);

    /**
     * Converts the specified Point2D in the JXMapViewer's local coordinate
     * space to a GeoPosition on the map. This method is especially useful for
     * determining the GeoPosition under the mouse cursor.
     *
     * @param pt
     *            a point in the local coordinate space of the map
     * @return the point converted to a GeoPosition
     */
    public abstract GeoPosition convertPointToGeoPosition(Point2D pt);
    
    
    /**
     * Set another map rendering layer. Will only work if the JXMapViewer
     * allowes it.
     * 
     * @param mapLayer
     */
    public void setMapLayer(AbstractPainter<JXMapViewer> mapLayer) {
        // Default implementation is empty.
    }
}
