/**
 * 
 */
package net.sf.bt747.j2se.app.map;

import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.util.MercatorUtils;

/**
 * @author Mario
 * 
 */
public class WMSTileFactoryInfo extends MyTileFactoryInfo {

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
    public WMSTileFactoryInfo(final String name, final int minimumZoomLevel,
            final int maximumZoomLevel, final int totalMapZoom,
            final int tileSize, final boolean xr2l, final boolean yt2b,
            final String baseURL, final String xparam, final String yparam,
            final String zparam, final String description, final String url) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam,
                description, url);
        setUpValues();
    }

    public WMSTileFactoryInfo(final String name, final int minimumZoomLevel,
            final int maximumZoomLevel, final int totalMapZoom,
            final int tileSize, final boolean xr2l, final boolean yt2b,
            final String baseURL, final String xparam, final String yparam,
            final String zparam, final String description, final String url,
            final double bbxmin, final double bbxmax, final double bbymin,
            final double bbymax) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam,
                description, url);
        this.bbxmax = bbxmax;
        this.bbxmin = bbxmin;
        this.bbymax = bbymax;
        this.bbymin = bbymin;
        setUpValues();
    }

    private static final double MAGIC_NUMBER = 6356752.3142;
    private static final double DEG2RAD = 0.0174532922519943;

    // var PI=3.14159267;
    private static final double dd2MercMetersLng(final double p_lng) {
        return MAGIC_NUMBER * (p_lng * DEG2RAD);
    }

    // Clipping at 85.05112878
    private static final double CLIP_LIMIT = 85.05112878;

    private static final double dd2MercMetersLat(double p_lat) {
        if (p_lat >= 85) {
            p_lat = 85;
        }
        if (p_lat <= -85) {
            p_lat = -85;
        }
        return MAGIC_NUMBER
                * Math.log(Math.tan(((p_lat * DEG2RAD) + (Math.PI / 2)) / 2));
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
    public String toWMSURL(final int xx, final int yy, final int zoom) {
        // styles="default";
        srs = "EPSG:4326";
        styles = "";
        final int ts = getTileSize(zoom);
        // GeoPosition gp1 = getPosition(new Point2D.Double(xx*ts,yy*ts), zoom, this);
        // GeoPosition gp2 = getPosition(new Point2D.Double((xx+1)*ts,(yy+1)*ts),
        // zoom, this);
        final double ulx = MercatorUtils.xToLong(xx * ts, radiusAtZoom[zoom]);
        final double uly;// = MercatorUtils.yToLat(y * ts, radius);
        final double lrx;// = MercatorUtils.xToLong((x + 1) * ts, radius);
        final double lry;// = MercatorUtils.yToLat((y + 1) * ts, radius);
        final String bbox = "";
        // ulx = gp1.getLongitude();
        // uly= gp1.getLatitude();
        // lrx = gp2.getLongitude();
        // lry= gp2.getLatitude();

        // var ts = this.tileSize;
        // var ul = this.getLatLng(a*ts,(b+1)*ts, c);
        // var lr = this.getLatLng((a+1)*ts, b*ts, c);
        // user mercator for small scale
        // if (zoom > 0) {
        // bbox = dd2MercMetersLng(ulx) + "," + dd2MercMetersLat(uly) + "," +
        // dd2MercMetersLng(lrx)+ "," + dd2MercMetersLat(lry);
        // srs="EPSG:54004";
        // } else {
        // bbox = ulx + "," + uly + "," + lrx + "," + lry;
        // srs="EPSG:3785" // GlobalMercator [Google Maps, Yahoo Maps,
        // Microsoft Maps]
        srs = "EPSG:4326"; // GlobalGeodetic [OpenLayers Base Map, Google
        // Earth]
        // Convert to EPSG:900913 (Spherical Mercator) in meters (Total = 2 *
        // PI * 6378137]

        // }
        // var url = url + "REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=" + layers
        // + "&STYLES=" + this.Styles + "&FORMAT=" + format +
        // "&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&"+l_strSRS+"&BBOX=" + bbox + "&WIDTH="
        // + ts + "&HEIGHT=" + ;
        // this.cURL=url;
        // return url;
        //        
        layer = "GlobeXplorer%20Image";
        // service="WMS";
        format = "image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
        final int x = xx - mapWidthInTilesAtZoom[zoom] / 2;
        final int y = mapWidthInTilesAtZoom[zoom] / 2 - yy - 1;
        final double radius = radiusAtZoom[zoom];
        // GeoUtil.getBitmapCoordinate(c, zoomLevel, info);
        // String bbox = ulx + "," + uly + "," + lrx + "," + lry;
        // bbox =
        // "2.31726828957381,48.9179588116455,2.31842233403588,48.9192730492482";
        final String url = baseURL
        // Version
                + "version=1.1.1"
                // Service type
                + "&SERVICE=WMS"
                // Request map
                + "&request=GetMap"
                // Request layers
                + "&Layers=" + layer
                // Request format
                + "&format=" + format +
                // This is the position bounding box
                "&BBOX=" + bbox +
                // This is the tile width and height
                "&width=" + ts + "&height=" + ts +
                // This is ...
                "&SRS=" + srs +
                // This is the style (if any)
                "&Styles=" + styles +
                // Apparently transparency is possible.
                // "&transparent=TRUE"+
                "";
        System.err.println(url);
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

    private double[] radiusAtZoom;

    private void setUpValues() {
        final int totalMapZoom = getTotalMapZoom();
        int tilesize = getTileSize(0); // Any zoom is ok.
        mapCenterInPixelsAtZoom = new Point2D.Double[totalMapZoom + 1];
        mapWidthInTilesAtZoom = new int[totalMapZoom + 1];
        radiusAtZoom = new double[totalMapZoom + 1];

        // 2 * math.pi * 6378137 ;

        // for each zoom level
        for (int z = totalMapZoom; z >= 0; --z) {
            final int t2 = getTileSize(z) / 2;
            mapCenterInPixelsAtZoom[z] = new Point2D.Double(t2, t2);
            mapWidthInTilesAtZoom[z] = tilesize / getTileSize(z);
            radiusAtZoom[z] = (mapWidthInTilesAtZoom[z] / 2) / Math.PI;
            tilesize *= 2;
        }
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(final String layer) {
        this.layer = layer;
    }
}
