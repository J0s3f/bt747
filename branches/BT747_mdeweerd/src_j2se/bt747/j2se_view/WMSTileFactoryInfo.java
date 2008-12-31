/**
 * 
 */
package bt747.j2se_view;

import java.awt.geom.Point2D;

import net.sf.bt747.j2se.app.map.MyTileFactoryInfo;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;
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
    public WMSTileFactoryInfo(String name, int minimumZoomLevel,
            int maximumZoomLevel, int totalMapZoom, int tileSize,
            boolean xr2l, boolean yt2b, String baseURL, String xparam,
            String yparam, String zparam, String description, String url) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam,
                description, url);
        setUpValues();
    }

    public WMSTileFactoryInfo(String name, int minimumZoomLevel,
            int maximumZoomLevel, int totalMapZoom, int tileSize,
            boolean xr2l, boolean yt2b, String baseURL, String xparam,
            String yparam, String zparam, String description, String url,
            double bbxmin, double bbxmax, double bbymin, double bbymax) {
        super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam,
                description, url);
        this.bbxmax = bbxmax;
        this.bbxmin = bbxmin;
        this.bbymax = bbymax;
        this.bbymin = bbymin;
        setUpValues();
    }

    
    private static final double MAGIC_NUMBER=6356752.3142;
    private static final double DEG2RAD=0.0174532922519943;
    //var PI=3.14159267;
    private static final double dd2MercMetersLng(double p_lng) {
    return MAGIC_NUMBER*(p_lng*DEG2RAD);
    }
    private static final double dd2MercMetersLat(double p_lat) {
    if (p_lat >= 85) p_lat=85;
    if (p_lat <= -85) p_lat=-85;
    return MAGIC_NUMBER*Math.log(Math.tan(((p_lat*DEG2RAD)+(Math.PI/2)) /2));
    }

    // WGS 84 (EPSG CRS code 4978)
    
    private String layer = "BMNG";
    private String srs = "EPSG:4326";
    private String format = "image/png";
    private String styles = "";
    private double bbxmin = -180.;
    private double bbxmax = 180.;
    private double bbymin = -90.;
    private double bbymax = 90.;

    //&SRS=EPSG:4326&BBOX=2.31726828957381,48.9179588116455,2.31842233403588,48.9192730492482&WIDTH=353&HEIGHT=402
    //&&BBOX=-152.40234375,-89.12777169565308,-152.314453125,-89.12643273928488&width=512&height=512&SRS=WMS&Styles=
    public String toWMSURL(int xx, int yy, int zoom) {
        //styles="default";
        srs = "EPSG:4326";
        styles = "";
        int ts = getTileSize(zoom);
        GeoPosition gp1 = GeoUtil.getPosition(new Point2D.Double(xx*ts,yy*ts), zoom, this);
        GeoPosition gp2 = GeoUtil.getPosition(new Point2D.Double((xx+1)*ts,(yy+1)*ts), zoom, this);
        double ulx = MercatorUtils.xToLong(xx * ts, radiusAtZoom[zoom]);
        double uly;// = MercatorUtils.yToLat(y * ts, radius);
        double lrx;// = MercatorUtils.xToLong((x + 1) * ts, radius);
        double lry;// = MercatorUtils.yToLat((y + 1) * ts, radius);
        String bbox;
        ulx = gp1.getLongitude();
        uly= gp1.getLatitude();
        lrx = gp2.getLongitude();
        lry= gp2.getLatitude();

        //var ts = this.tileSize;
        //var ul = this.getLatLng(a*ts,(b+1)*ts, c);
        //var lr = this.getLatLng((a+1)*ts, b*ts, c);
        // user mercator for small scale
//        if (zoom > 0) {
//         bbox = dd2MercMetersLng(ulx) + "," + dd2MercMetersLat(uly) + "," + dd2MercMetersLng(lrx)+ "," + dd2MercMetersLat(lry);
//          srs="EPSG:54004";
//        } else {
         bbox = ulx + "," + uly + "," + lrx + "," + lry;
        srs="EPSG:4326";
//        }
//        var url = url + "REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=" + layers + "&STYLES=" + this.Styles + "&FORMAT=" + format + "&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&"+l_strSRS+"&BBOX=" + bbox + "&WIDTH=" + ts + "&HEIGHT=" + ;
//        this.cURL=url;
//        return url;
//        
        layer="GlobeXplorer%20Image";
        //service="WMS";
        format="image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
        int x = xx - mapWidthInTilesAtZoom[zoom]/2;
        int y= mapWidthInTilesAtZoom[zoom]/2 - yy - 1;
        double radius = radiusAtZoom[zoom];
        //GeoUtil.getBitmapCoordinate(c, zoomLevel, info);
        //String bbox = ulx + "," + uly + "," + lrx + "," + lry;
        //bbox = "2.31726828957381,48.9179588116455,2.31842233403588,48.9192730492482";
        String url = baseURL
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
        //return url;
    }

    public String getTileUrl(int x, int y, int zoom) {
        return toWMSURL(x, y, zoom);
    }
    
    /*
     * The number of tiles wide at each zoom level
     */
    private int[] mapWidthInTilesAtZoom;
   /**
     * An array of coordinates in <em>pixels</em> that indicates the center in the
     * world map for the given zoom level.
     */
    private Point2D[] mapCenterInPixelsAtZoom;

    private double[]  radiusAtZoom;
    private void setUpValues() {
        int totalMapZoom = getTotalMapZoom();
        int tilesize = getTileSize(0);  // Any zoom is ok.
        mapCenterInPixelsAtZoom = new Point2D.Double[totalMapZoom+1];
        mapWidthInTilesAtZoom = new int[totalMapZoom+1];
        radiusAtZoom = new double[totalMapZoom+1];
    
        // for each zoom level
        for (int z = totalMapZoom; z >= 0; --z) {
            int t2 = this.getTileSize(z) / 2;
            mapCenterInPixelsAtZoom[z] = new Point2D.Double(t2, t2);
            mapWidthInTilesAtZoom[z] = tilesize / this.getTileSize(z);
            radiusAtZoom[z] = (mapWidthInTilesAtZoom[z]/2)/Math.PI;
            tilesize *= 2;
        }

    }
    
    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }
}
