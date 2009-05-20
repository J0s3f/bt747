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
    // private final static int GOOGLEMAPS_MAX_ZOOM = 18;
    //    
    // public final static MyTileFactoryInfo tfiGoogleMaps = new
    // MyTileFactoryInfo(
    // "gmapstreet",
    // 1, GOOGLEMAPS_MAX_ZOOM - 2, GOOGLEMAPS_MAX_ZOOM, 256, true, true,
    // "http://khm2.google.com/kh?v=33", "x", "y", "z",
    // "Google Maps", "http://maps.google.com") {
    // public String getTileUrl(int x, int y, int zoom) {
    // zoom = GOOGLEMAPS_MAX_ZOOM - zoom;
    // String url = this.baseURL + "&x=" + x + "&y=" + y + "&z=" + zoom;
    // return url;
    // }
    // };

    public final static MyTileFactoryInfo tfiOpenStreetMap = new MyTileFactoryInfo(
            "osm", 1, 18, 19, 256, true, true,
            "http://tile.openstreetmap.org", "x", "y", "z",
            "OpenStreetMap (Mapnik)", "http://www.openstreetmap.org") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
            return url;
        }
    };
    public final static MyTileFactoryInfo tfiOSM_OSM_CYCLE = new MyTileFactoryInfo(
            "osmcycle", 7, 18, 19, 256, true, true,
            "http://www.thunderflames.org/tiles/cycle", "x", "y", "z",
            "OpenCycleMap.org - Creative Commons-by-SA License",
            "http://www.opencylemap.org") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
            return url;
        }
    };
    public final static MyTileFactoryInfo tfiOSM_OSMARENDER = new MyTileFactoryInfo(
            "osmarender", 2, 18, 19, 256, true, true,
            "http://tah.openstreetmap.org/Tiles/tile", "x", "y", "z",
            "Open Street Map (Osmarender)",
            "http://www.openstreetmap.org/?layers=0B00FTF") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
            return url;
        }
    };
    /* Can not use due to licensing limitations ? => not used */
    public final static MyTileFactoryInfo tfiGOOGLEMAPS = new MyTileFactoryInfo(
            "gmaps", 1, 16, 17, 256, false, true,
            "http://mt.google.com/mt?n=404&v=w2.999", "x", "y", "zoom",
            "Google Maps",
            "http://www.google.com/maps") {
//        public String getTileUrl(int x, int y, int zoom) {
//            zoom = getTotalMapZoom() - zoom;
//            String url = baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
//            return url;
//        }
    };
     

    public final static MyTileFactoryInfo tfiWMS = new MyTileFactoryInfo(
            "WMS", 1, 15, 19, 512, true, true,
            "http://wms.jpl.nasa.gov/wms.cgi?", "x", "y", "z", "NASA",
            "http://wms.jpl.nasa.gov") {
        public String getTileUrl(int x, int y, int zoom) {
            int zz = getTotalMapZoom() - zoom;
            int z = 4;
            z = (int) Math.pow(2, (double) zz - 1);
            return wms.toWMSURL(x - z, z - 1 - y, zz, getTileSize(zoom));
        }
    };

    public final static MyTileFactoryInfo tfiSWISS = new WMSTileFactoryInfo(
            "WMS", 1, 15, 19, 256, true, true,
            "http://wms.jpl.nasa.gov/wms.cgi?", "x", "y", "z", "NASA",
            "http://wms.jpl.nasa.gov", 50., 52., 4., 6.) {

    };

    // No longer works.
//    public final static MyTileFactoryInfo tfiDigitalGlobe = new WmsEPSG4326TileFactoryInfo(
//            "dig256", 1, 18, 19, 256, true, true,
//            "http://wms.globexplorer.com/gexservlets/wms?", "x", "y", "z",
//            "Digital Globe", "http://www.digitalglobe.com") {
//
//        public String getLayer() {
//            return "GlobeXplorer%20Image";
//        }
//
//        /*
//         * (non-Javadoc)
//         * 
//         * @see net.sf.bt747.j2se.app.map.WmsEPSG4326TileFactoryInfo#getFormat()
//         */
//        @Override
//        public String getFormat() {
//            return "image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
//        }
//
//    };

//    public final static MyTileFactoryInfo tfiDigitalGlobe128 = new WmsEPSG4326TileFactoryInfo(
//            "dig128", 4, 18, 19, 128, true, true,
//            "http://wms.globexplorer.com/gexservlets/wms?", "x", "y", "z",
//            "Digital Globe", "http://www.digitalglobe.com") {
//        public String getLayer() {
//            return "GlobeXplorer%20Image";
//        }
//
//        /*
//         * (non-Javadoc)
//         * 
//         * @see net.sf.bt747.j2se.app.map.WmsEPSG4326TileFactoryInfo#getFormat()
//         */
//        @Override
//        public String getFormat() {
//            return "image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
//        }
//
//    };
//
//    public final static MyTileFactoryInfo tfiDigitalGlobe512 = new WmsEPSG4326TileFactoryInfo(
//            "dig512", 1, 18, 19, 512, true, true,
//            "http://wms.globexplorer.com/gexservlets/wms?", "x", "y", "z",
//            "Digital Globe", "http://www.digitalglobe.com") {
//        public String getLayer() {
//            return "GlobeXplorer%20Image";
//        }
//
//        /*
//         * (non-Javadoc)
//         * 
//         * @see net.sf.bt747.j2se.app.map.WmsEPSG4326TileFactoryInfo#getFormat()
//         */
//        @Override
//        public String getFormat() {
//            return "image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE";
//        }
//
//    };

    // Next list of maps unverified.

    public final static MyTileFactoryInfo tfiTopo = new WmsEPSG4326TileFactoryInfo(
            "Topo",
            5,
            17,
            17 + 1,
            400,
            true,
            true,
            "http://terraservice.net/ogcmap6.ashx?version=1.1.1&styles=&bgcolor=0xCCCCCC&exceptions=INIMAGE",
            "", "", "", "Topo maps by USGS via terraserver-usa.com",
            "http://terraservice.net") {
        public String getLayer() {
            return "DRG";
        }

        public String getFormat() {
            return "image/jpeg";
        }
    };
    public final static MyTileFactoryInfo tfiAerial = new WmsEPSG4326TileFactoryInfo(
            "Aerial",
            7,
            18,
            18 + 1,
            400,
            true,
            true,
            "http://terraservice.net/ogcmap6.ashx?version=1.1.1&styles=&bgcolor=0xCCCCCC&exceptions=INIMAGE",
            "", "", "", "Imagery by USGS via terraserver-usa.com",
            "http://terraservice.net") {
        public String getLayer() {
            return "DOQ";
        }

        public String getFormat() {
            return "image/jpeg";
        }
    };
    public final static MyTileFactoryInfo tfiNRCan = new WmsEPSG4326TileFactoryInfo(
            "NRCan",
            6,
            18,
            18 + 1,
            600,
            true,
            true,
            "http://wms.cits.rncan.gc.ca/cgi-bin/cubeserv.cgi?version=1.1.3&bgcolor=0xFFFFFF&exceptions=application/vnd.ogc.se_inimage",
            "", "", "", "Maps by NRCan.gc.ca", "http://wms.cits.rncan.gc.ca") {
        public String getLayer() {
            return "PUB_50K:CARTES_MATRICIELLES/RASTER_MAPS";
        }

        public String getFormat() {
            return "image/png";
        }
    };
    public final static MyTileFactoryInfo tfiNEXRAD = new WmsEPSG4326TileFactoryInfo(
            "NEXRAD",
            3,
            14,
            14 + 1,
            256,
            true,
            true,
            "http://mesonet.agron.iastate.edu/cgi-bin/wms/nexrad/n0r.cgi?version=1.1.1&service=WMS&transparent=true",
            "", "", "", "NEXRAD imagery from Iowa Environmental Mesonet",
            "http://mesonet.agron.iastate.edu") {
        public String getLayer() {
            return "nexrad-n0r";
        }

        public String getFormat() {
            return "image/png";
        }
    }; // NOTE: for combo maps using Google tiles, tileSize MUST be 256!!!
    public final static MyTileFactoryInfo tfiLandsat = new WmsEPSG4326TileFactoryInfo(
            "Landsat", 3, 15, 15 + 1, 260, true, true,
            "http://onearth.jpl.nasa.gov/wms.cgi?", "", "", "",
            "Map by NASA", "http://onearth.jpl.nasa.gov") {
        public String getLayer() {
            return "global_mosaic";
        }

        public String getFormat() {
            return "image/jpeg";
        }
    };
    public final static MyTileFactoryInfo tfiBlueMarble = new WmsEPSG4326TileFactoryInfo(
            "BlueMarble", 3, 8, 8 + 1, 260, true, true,
            "http://onearth.jpl.nasa.gov/wms.cgi?", "", "", "",
            "Map by NASA", "http://onearth.jpl.nasa.gov") {
        public String getLayer() {
            return "modis";
        }

        public String getFormat() {
            return "image/jpeg";
        }
    };
    // public final static MyTileFactoryInfo tfiBlueMarble = new
    // WmsEPSG4326TileFactoryInfo("BlueMarble",3,8,8+1,260,true,true,"http://www2.demis.nl/wms/wms.asp?service=WMS&wms=BlueMarble&wmtver=1.0.0&format=jpeg&transparent=false&exceptions=inimage&wrapdateline=true",
    // "","","","Map by DEMIS","http://www2.demis.nl") {public String
    // getLayer(){ return "Earth+Image,Borders"; } public String getFormat() {
    // return ""; }};
    public final static MyTileFactoryInfo tfiMODIS = new WmsEPSG4326TileFactoryInfo(
            "MODIS", 3, 10, 10 + 1, 260, true, true,
            "http://onearth.jpl.nasa.gov/wms.cgi?", "", "", "",
            "Map by NASA", "http://onearth.jpl.nasa.gov") {
        public String getLayer() {
            return "daily_planet";
        }

        public String getFormat() {
            return "image/jpeg";
        }
    };
    public final static MyTileFactoryInfo tfiSRTM = new WmsEPSG4326TileFactoryInfo(
            "SRTM", 6, 14, 14 + 1, 260, true, true,
            "http://onearth.jpl.nasa.gov/wms.cgi?", "", "", "",
            "SRTM elevation data by NASA", "http://onearth.jpl.nasa.gov") {
        public String getLayer() {
            return "huemapped_srtm";
        }

        public String getFormat() {
            return "image/jpeg";
        }
    };



    // Key needed:
    // Get key:
    // http://api.ign.fr/getToken?key=XXXXXXXXXXX&output=json&callback=Geoportal.GeoRMHandler.UXXXX.callback
    // "http://wxs.ign.fr/geoportail/wmsc?" +
    // "LAYERS=ORTHOIMAGERY.ORTHOPHOTOS&EXCEPTIONS=text/xml" +
    // "&FORMAT=image/jpeg&SERVICE=WMS&VERSION=1.1.1" +
    // "&STYLES=&SRS=IGNF:GEOPORTALANF" +
    // "&BBOX=-6639616,1777664,-6637568,1779712" +
    // "&WIDTH=256&HEIGHT=256&TILED=true";
}
