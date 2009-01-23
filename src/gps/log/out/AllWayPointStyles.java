/**
 * 
 */
package gps.log.out;

/**
 * @author Mario
 * 
 */
public class AllWayPointStyles extends WayPointStyleSet {
    private static final String[][] IconStyles = {
            /**
             * STANDARD MTK BASED LOGGER STYLES
             * 
             */
            { "T", "TimeStamp",
                    "http://maps.google.com/mapfiles/kml/paddle/T.png" },
            { "D", "DistanceStamp",
                    "http://maps.google.com/mapfiles/kml/paddle/D.png" },
            { "S", "SpeedStamp",
                    "http://maps.google.com/mapfiles/kml/paddle/S.png" },
            { "B", "ButtonStamp",
                    "http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png" },
            { "M", "MixStamp",
                    "http://maps.google.com/mapfiles/kml/paddle/M.png" },
            // { "0001", "", "http://maps.google.com/mapfiles/kml/pal4/.png"
            // },
            // { "0002", "", "http://maps.google.com/mapfiles/kml/pal4/.png"
            // },
            // { "0004", "", "http://maps.google.com/mapfiles/kml/pal4/.png"
            // },
            // { "0008", "", "http://maps.google.com/mapfiles/kml/pal4/.png"
            // },
            { "0010", "Picture",
                    "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            { "0020", "Gaz Station",
                    "http://maps.google.com/mapfiles/kml/shapes/gas_stations.png" },
            { "0040", "Phone Booth",
                    "http://maps.google.com/mapfiles/kml/shapes/phone.png" },
            { "0080", "ATM",
                    "http://maps.google.com/mapfiles/kml/shapes/euro.png" },
            { "0100", "Bus Stop",
                    "http://maps.google.com/mapfiles/kml/shapes/bus.png" },
            { "0200", "Parking",
                    "http://maps.google.com/mapfiles/kml/shapes/parking_lot.png" },
            { "0400", "Post Box",
                    "http://maps.google.com/mapfiles/kml/shapes/post_office.png" },
            { "0800", "Railway",
                    "http://maps.google.com/mapfiles/kml/shapes/rail.png" },
            { "1000", "Restaurant",
                    "http://maps.google.com/mapfiles/kml/shapes/dining.png" },
            { "2000", "Bridge",
                    "http://maps.google.com/mapfiles/kml/shapes/water.png" },
            { "4000", "View",
                    "http://maps.google.com/mapfiles/kml/shapes/flag.png" },
            { "8000", "Other",
                    "http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png" },
            // TODO: change next values.
            { "0300", "Voice",
                    "http://maps.google.com/mapfiles/kml/paddle/V.png" },
            { "0500", "Way Point",
                    "http://maps.google.com/mapfiles/kml/pal4/icon29.png" },

            /**
             * Category 1 Styles<br>
             * Geotag results.
             */
            /** 0x0101 Picture: A geotagged picture */
            { "0101", "Picture",
                    "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /** 0x0102 Audio recording: Whatever audio recording */
            // { "0102", "Audio recording",
            // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /** 0x0103 Note: A simple text note. */
            // { "0103", "Text note",
            // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /**
             * 0x0104 Document: A more advanced document (could be openoffice
             * document, pdf, ...). IN general anything that does not fall
             * under another specific type in this category.
             */
            // { "0104", "Document",
            // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /** 0x0105 Phone Call */
            { "0108", "Phone Call",
                    "http://maps.google.com/mapfiles/kml/shapes/phone.png" },
            /** 0x0106 SMS */
            // { "0106", "SMS",
            // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /** 0x0107 Video Recording Start */
            // { "0107", "Video Recording Start",
            // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /** 0x0108 Video Recording End */
            // { "0108", "Video Recording End",
            // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /** 0x0109 Shopping: Could be based on time recording on ticket */
            // { "0109", "Shopping",
            // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
            /** 0x010A Restaurant: Could be based on time recording on ticket */
            { "010A", "Restaurant",
                    "http://maps.google.com/mapfiles/kml/shapes/dining.png" },
    /** 0x010B Meeting */
    // { "010B", "Meeting",
    // "http://maps.google.com/mapfiles/kml/shapes/camera.png" },
    };

    /**
     * 
     */
    public AllWayPointStyles() {
        super(AllWayPointStyles.IconStyles);
    }
}
