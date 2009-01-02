/**
 * 
 */
package net.sf.bt747.j2se.app.map;

import org.jdesktop.swingx.mapviewer.wms.WMSService;


/**
 * @author Mario
 *
 */
public class MapFactoryInfos {
    private final static WMSService wms = new WMSService();
    // Not used for license reasons
//    private final static int GOOGLEMAPS_MAX_ZOOM = 18;
//    
//    public final static MyTileFactoryInfo tfiGoogleMaps = new MyTileFactoryInfo(
//            "gmapstreet",
//            1, GOOGLEMAPS_MAX_ZOOM - 2, GOOGLEMAPS_MAX_ZOOM, 256, true, true,
//            "http://khm2.google.com/kh?v=33", "x", "y", "z",
//            "Google Maps", "http://maps.google.com") {
//        public String getTileUrl(int x, int y, int zoom) {
//            zoom = GOOGLEMAPS_MAX_ZOOM - zoom;
//            String url = this.baseURL + "&x=" + x + "&y=" + y + "&z=" + zoom;
//            return url;
//        }
//    };
    
    public final static MyTileFactoryInfo tfiOpenStreetMap = new MyTileFactoryInfo(
            "osm", 1, 18, 19, 256, true, true,
            "http://tile.openstreetmap.org", "x", "y", "z",
            "OpenStreetMap (Mapnik)", "http://www.openstreetmap.org") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = this.baseURL + "/" + zoom + "/" + x + "/" + y
                    + ".png";
            return url;
        }
    };
    public final static MyTileFactoryInfo tfiOSM_OSM_CYCLE = new MyTileFactoryInfo(
            "osmcycle", 1, 15, 19, 256, true, true,
            "http://www.thunderflames.org/tiles/cycle", "x", "y", "z",
            "OpenCycleMap.org - Creative Commons-by-SA License",
            "http://www.opencylemap.org") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = this.baseURL + "/" + zoom + "/" + x + "/" + y
                    + ".png";
            return url;
        }
    };
    public final static MyTileFactoryInfo tfiOSM_OSMARENDER = new MyTileFactoryInfo(
            "osmarender", 1, 18, 19, 256, true, true,
            "http://tah.openstreetmap.org/Tiles/tile", "x", "y", "z",
            "Open Street Map (Osmarender)",
            "http://www.openstreetmap.org/?layers=0B00FTF") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = this.baseURL + "/" + zoom + "/" + x + "/" + y
                    + ".png";
            return url;
        }
    };
    public final static MyTileFactoryInfo tfiWMS = new MyTileFactoryInfo("WMS", 1,
            15, 19, 512, true, true, "http://wms.jpl.nasa.gov/wms.cgi?", "x",
            "y", "z", "NASA", "http://wms.jpl.nasa.gov") {
        public String getTileUrl(int x, int y, int zoom) {
            int zz = getTotalMapZoom() - zoom;
            int z = 4;
            z = (int) Math.pow(2, (double) zz - 1);
            return wms.toWMSURL(x - z, z - 1 - y, zz, getTileSize(zoom));
        }
    };
    
    public final static MyTileFactoryInfo tfiSWISS = new WMSTileFactoryInfo(
            "WMS", 1,
            15, 19, 256, true, true, "http://wms.jpl.nasa.gov/wms.cgi?", "x",
            "y", "z", "NASA", "http://wms.jpl.nasa.gov",
            50.,52.,4.,6.) {
        
    };

    
    public final static MyTileFactoryInfo tfiDigitalGlobe = new WmsEPSG4326TileFactoryInfo(
            "dig256", 1,
            18, 19, 256, true, true, "http://wms.globexplorer.com/gexservlets/wms?", "x",
            "y", "z", "Digital Globe", "http://www.digitalglobe.com") {
        
        public String getLayer() {
            return "GlobeXplorer%20Image";
        }
        
        /* (non-Javadoc)
         * @see net.sf.bt747.j2se.app.map.WmsEPSG4326TileFactoryInfo#getFormat()
         */
        @Override
        public String getFormat() {
            return "image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
        }
        
    };

    public final static MyTileFactoryInfo tfiDigitalGlobe128 = new WmsEPSG4326TileFactoryInfo(
            "dig128", 4,
            18, 19, 128, true, true, "http://wms.globexplorer.com/gexservlets/wms?", "x",
            "y", "z", "Digital Globe", "http://www.digitalglobe.com") {
        public String getLayer() {
            return "GlobeXplorer%20Image";
        }
        
        /* (non-Javadoc)
         * @see net.sf.bt747.j2se.app.map.WmsEPSG4326TileFactoryInfo#getFormat()
         */
        @Override
        public String getFormat() {
            return "image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
        }
        
    };

    public final static MyTileFactoryInfo tfiDigitalGlobe512 = new WmsEPSG4326TileFactoryInfo(
            "dig512", 1,
            18, 19, 512, true, true, "http://wms.globexplorer.com/gexservlets/wms?", "x",
            "y", "z", "Digital Globe", "http://www.digitalglobe.com") {
        public String getLayer() {
            return "GlobeXplorer%20Image";
        }
        
        /* (non-Javadoc)
         * @see net.sf.bt747.j2se.app.map.WmsEPSG4326TileFactoryInfo#getFormat()
         */
        @Override
        public String getFormat() {
            return "image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
        }
        
    };

}
