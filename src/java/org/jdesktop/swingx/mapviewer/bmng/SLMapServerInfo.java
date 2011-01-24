package org.jdesktop.swingx.mapviewer.bmng;

import java.awt.geom.Point2D;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;


/** A TileFactoryInfo subclass which knows how to connect
 * to the SwingLabs map server. This server contains 2k resolution
 * Blue Marble data from NASA.
 */
public class SLMapServerInfo extends TileFactoryInfo {
    
    private static final int pyramid_top = 8+1;
    private static final int midpoint = 5;
    private static final int normal_tile_size = 675;
    public SLMapServerInfo() {
        this("http://maps.joshy.net/tiles/bmng_tiles_3");
    }
    
    public SLMapServerInfo(String baseURL) {
        super(0, pyramid_top-1, pyramid_top,
                normal_tile_size,
                true, false, baseURL, "", "", "");
        setDefaultZoomLevel(0);
    }
    
    public int getMidpoint() {
        return midpoint;
    }
    
    public int getTileSize(int zoom) {
        int size = super.getTileSize(zoom);
        if(zoom < midpoint) {
            return size;
        } else {
            for(int i=0; i< zoom +1 - midpoint; i++) {
                size = size/2;
            }
            return size;
        }
    }
    
    
    @Override
    public int getMapWidthInTilesAtZoom(int zoom) {
        if(zoom < midpoint) {
            return (int)Math.pow(2,midpoint-zoom);
        } else {
            return 2;
        }
    }

    @Override
   public int getMapHeightInTilesAtZoom(int zoom) {
        return getMapWidthInTilesAtZoom(zoom)/2;
    }
    
    @Override
    public String getTileUrl(int x, int y, int zoom) {
        int ty = y;
        int tx = x;
        
        //int width_in_tiles = (int)Math.pow(2,pyramid_top-zoom);
        int width_in_tiles = getMapWidthInTilesAtZoom(zoom);
        //System.out.println("width in tiles = " + width_in_tiles + " x = " + tx + " y = " + ty);
        if(ty < 0) { return null; }
        if(zoom < midpoint) {
            if(ty >= width_in_tiles/2) { return null; }
        } else {
            if(ty != 0) { return null; }
        }
        
        String url = this.baseURL + "/" + zoom + "/"+ ty + "/" + tx + ".jpg";
        //System.out.println("returning: " + url);
        return url;
    }

    @Override
    public Point2D getBitmapCoordinate(double latitude, double longitude, int zoom) {
        //return super.getBitmapCoordinate(latitude, longitude, zoomLevel);
        // calc the pixels per degree
        //Dimension mapSizeInTiles = getMapSize(zoom);
        //double size_in_tiles = (double)getInfo().getMapWidthInTilesAtZoom(zoom);
        //double size_in_tiles = Math.pow(2, getInfo().getTotalMapZoom() - zoom);
        final double size_in_pixels = getMapWidthInTilesAtZoom(zoom)*getTileSize(zoom);
        final double ppd = size_in_pixels / 360;

        // the center of the world
        final double centerX = this.getTileSize(zoom)*getMapWidthInTilesAtZoom(zoom)/2;
        final double centerY = this.getTileSize(zoom)*getMapHeightInTilesAtZoom(zoom)/2;

        final double x = longitude * ppd + centerX;
        final double y = -latitude * ppd + centerY;

        return new Point2D.Double(x, y);
    }

    @Override
    public GeoPosition getPosition(Point2D pix, int zoom) {
        // calc the pixels per degree
        final double size_in_pixels = getMapWidthInTilesAtZoom(zoom)*getTileSize(zoom);
        final double ppd = size_in_pixels / 360;

        // the center of the world
        final double centerX = getTileSize(zoom)*getMapWidthInTilesAtZoom(zoom)/2;
        final double centerY = getTileSize(zoom)*getMapHeightInTilesAtZoom(zoom)/2;

        final double lon = (pix.getX() - centerX)/ppd;
        final double lat = -(pix.getY() - centerY)/ppd;

        return new GeoPosition(lat,lon);
    }

    
}