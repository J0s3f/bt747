/**
 * 
 */
package net.sf.bt747.j2se.app.map;

import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public class WmsEPSG4326TileFactoryInfo extends MyTileFactoryInfo {

    /**
     * @param name
     * @param minimumZoomLevel
     * @param maximumZoomLevel
     * @param totalMapZoom
     * @param tileSize
     * @param xr2l
     * @param yt2b
     * @param baseURL
     * @param xparam
     * @param yparam
     * @param zparam
     * @param description
     * @param url
     */
    public WmsEPSG4326TileFactoryInfo(final String name,
            final int minimumZoomLevel, final int maximumZoomLevel,
            final int totalMapZoom, final int tileSize, final boolean xr2l,
            final boolean yt2b, final String baseURL, final String xparam,
            final String yparam, final String zparam,
            final String description, final String url) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam,
                description, url);
        setUpValues();
    }

    public WmsEPSG4326TileFactoryInfo(final String name,
            final int minimumZoomLevel, final int maximumZoomLevel,
            final int totalMapZoom, final int tileSize, final boolean xr2l,
            final boolean yt2b, final String baseURL, final String xparam,
            final String yparam, final String zparam,
            final String description, final String url, final double bbxmin,
            final double bbxmax, final double bbymin, final double bbymax) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam,
                description, url);
        this.bbxmax = bbxmax;
        this.bbxmin = bbxmin;
        this.bbymax = bbymax;
        this.bbymin = bbymin;
        setUpValues();
    }

    // WGS 84 (EPSG CRS code 4978)

    private String layer = "BMNG";
    private String srs = "EPSG:4326";
    private String format = "image/png";
    private String styles = "";
    private double bbxmin = -180.;
    private double bbxmax = 180.;
    private double bbymin = -80.;
    private double bbymax = 80.;

    // &SRS=EPSG:4326&BBOX=2.31726828957381,48.9179588116455,2.31842233403588,48.9192730492482&WIDTH=353&HEIGHT=402
    // &&BBOX=-152.40234375,-89.12777169565308,-152.314453125,-89.12643273928488&width=512&height=512&SRS=WMS&Styles=
    public String toWMSURL(final int xx, final int yy, final int zoomLevel) {
        if ((yy != 0) && (yy >= getMapHeightInTilesAtZoom(zoomLevel))) {
            return null;
        }

        final int ts = getTileSize(zoomLevel);
        final double radius = resolution(zoomLevel);
        double ulx = xx * radius - 180;
        double uly = yy * radius - 90;
        double lrx = (xx + 1) * radius - 180;
        double lry = (yy + 1) * radius - 90;

        if (isYt2b()) {
            double t;
            t = -lry;
            lry = -uly;
            uly = t;
        }

        if (!isXr2l()) {
            double t;
            t = -lrx;
            lrx = -ulx;
            ulx = t;
        }

        String bbox;

        bbox = ulx + "," + uly + "," + lrx + "," + lry;

        final String url = baseURL
        // Version
                + "version=1.1.1"
                // Service type
                + "&SERVICE=WMS"
                // Request map
                + "&request=GetMap"
                // Request layers
                + "&Layers=" + getLayer()
                // Request format
                + "&format=" + getFormat() +
                // This is the position bounding box
                "&BBOX=" + bbox +
                // This is the tile width and height
                "&width=" + ts + "&height=" + ts +
                // This is ...
                "&SRS=" + getSrs() +
                // This is the style (if any)
                "&Styles=" + getStyles() +
                // Apparently transparency is possible.
                // "&transparent=TRUE"+
                "";
        if (Generic.getDebugLevel() > 1) {
            Generic.debug(url);
        }
        return url;
        // return url;
    }

    public String getTileUrl(final int x, final int y, final int zoom) {
        return toWMSURL(x, y, zoom);
    }

    /**
     * Usefull help from
     */
    /*
     * The number of tiles wide at each zoom level
     */
    private int[] mapWidthInTilesAtZoom;
    /**
     * An array of coordinates in <em>pixels</em> that indicates the center
     * in the world map for the given zoom level.
     */
    private Point2D[] mapCenterInPixelsAtZoom;

    private void setUpValues() {
        final int totalMapZoom = getTotalMapZoom();
        mapCenterInPixelsAtZoom = new Point2D.Double[totalMapZoom + 1];
        mapWidthInTilesAtZoom = new int[totalMapZoom + 1];

        int t2 = getTileSize(totalMapZoom) / 2;
        // for each zoom level
        for (int z = totalMapZoom; z >= 0; --z) {
            mapCenterInPixelsAtZoom[z] = new Point2D.Double(t2, t2 / 2);
            mapWidthInTilesAtZoom[z] = 1 << (totalMapZoom - z);
            t2 *= 2;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.TileFactoryInfo#getMapCenterInPixelsAtZoom(int)
     */
    @Override
    public Point2D getMapCenterInPixelsAtZoom(final int zoom) {
        return mapCenterInPixelsAtZoom[zoom];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.TileFactoryInfo#getMapWidthInTilesAtZoom(int)
     */
    @Override
    public int getMapWidthInTilesAtZoom(final int zoomLevel) {
        return mapWidthInTilesAtZoom[zoomLevel];
    }

    /* (non-Javadoc)
     * @see org.jdesktop.swingx.mapviewer.TileFactoryInfo#getMapHeightInTilesAtZoom(int)
     */
    public int getMapHeightInTilesAtZoom(final int zoom) {
        int h;
        h = mapWidthInTilesAtZoom[zoom] / 2;
        if (h == 0) {
            h = 1;
        }
        return h;
    }

    private final double resolution(final int zoomLevel) {
        return 360. / getMapWidthInTilesAtZoom(zoomLevel);
    }

    public Point2D getBitmapCoordinate(final double latitude,
            final double longitude, final int zoomLevel) {
        final double res = resolution(zoomLevel) / getTileSize(zoomLevel);
        final double x = (180 + longitude) / res;
        final double y = (90 - latitude) / res;
        return new Point2D.Double(x, y);
    }

    public GeoPosition getPosition(final Point2D pixelCoordinate,
            final int zoomLevel) {
        final double res = resolution(zoomLevel) / getTileSize(zoomLevel);
        final double wx = pixelCoordinate.getX();
        final double wy = pixelCoordinate.getY();
        final double lon = wx * res - 180;
        final double lat = -wy * res + 90;
        // System.err.println("Request:"+pixelCoordinate);
        // System.err.println(getBitmapCoordinate(lat, lon, zoomLevel));
        // getBitmapCoordinate(lat,lon);
        return new GeoPosition(lat, lon);
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(final String layer) {
        this.layer = layer;
    }

    /**
     * @param srs
     *                the srs to set
     */
    public void setSrs(final String srs) {
        this.srs = srs;
    }

    /**
     * @return the srs
     */
    public String getSrs() {
        return srs;
    }

    /**
     * @param format
     *                the format to set
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param styles
     *                the styles to set
     */
    public void setStyles(final String styles) {
        this.styles = styles;
    }

    /**
     * @return the styles
     */
    public String getStyles() {
        return styles;
    }
}
