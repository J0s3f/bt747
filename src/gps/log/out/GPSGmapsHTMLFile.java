// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to write a GPX file.
 * 
 * @author Mario De Weerd
 */
public final class GPSGmapsHTMLFile extends GPSFile {
    private final StringBuffer rec = new StringBuffer(1024); // reused
    // stringbuffer

    private boolean m_isWayType;
    private boolean m_newTrack = true;
    private int m_currentFilter;

    /**
     * 
     */
    public GPSGmapsHTMLFile() {
        super();
        numberOfPasses = 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);
        m_currentFilter = GPSFilter.WAYPT;
        m_isWayType = true;
    }

    public boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
            // if(m_multipleFiles) {
            // closeFile();
            // }
            nbrOfPassesToGo--;
            previousDate = 0;
            m_isWayType = false;
            m_currentFilter = GPSFilter.TRKPT;
            // if(!m_multipleFiles) {
            // writeDataHeader();
            // }
            return true;
        } else {
            return false;
        }
    }

    protected void writeFileHeader(final String Name) {
        String header;
        header = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\r\n"
                + "<head>\r\n"
                + "<title>KML to Google Maps converter using PHP </title>\r\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=Windows-1252\" />\r\n"
                + "<meta name=\"description\" content=\"test - powered by Google Maps\" />\r\n"
                + "\r\n"
                + "<script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp\" type=\"text/javascript\">\r\n"
                + "</script>\r\n"
                + "\r\n"
                + "<style type=\"text/css\">\r\n"
                + "             v\\:* {\r\n"
                + "             behavior:url(#default#VML);\r\n"
                + "             }\r\n"
                + "             html, body, #map\r\n"
                + "             {\r\n"
                + "               width: 100%;\r\n"
                + "               height: 100%;\r\n"
                + "             }\r\n"
                + "           body             {\r\n"
                + "             margin-top: 0px;\r\n"
                + "             margin-right: 0px;\r\n"
                + "             margin-left: 0px;\r\n"
                + "             margin-bottom: 0px;\r\n"
                + "             }\r\n"
                + "\r\n"
                + "</style>\r\n"
                + "</head>\r\n"
                + "\r\n"
                + "<body>\r\n"
                + "<div id=\"map\"> </div>\r\n"
                + "\r\n"
                + "<script type=\"text/javascript\">\r\n"
                + "//<![CDATA[             // check for compatibility\r\n"
                + "\r\n"
                + "if (GBrowserIsCompatible()) {                 // call the info window opener for the given index\r\n"
                + "\r\n"
                + "  function makeOpenerCaller(i) {\r\n"
                + "     return function() { showMarkerInfo(i); };\r\n"
                + "\r\n"
                + "  }                 // open an info window\r\n"
                + "\r\n"
                + "  function showMarkerInfo(i) {\r\n"
                + "     markers[i].openInfoWindowHtml(infoHtmls[i]);\r\n"
                + "  }                 // create the map\r\n"
                + "\r\n"
                + "     var map = new GMap2(document.getElementById(\"map\"));\r\n"
                + "     map.setCenter(new GLatLng(0,0));\r\n"
                + "     map.setMapType(G_SATELLITE_MAP);\r\n"
                + "     map.enableScrollWheelZoom();\r\n"
                + "     map.addControl(new GLargeMapControl());\r\n"
                + "     map.addControl(new GMapTypeControl());\r\n"
                + "     map.addControl(new GScaleControl());\r\n"
                + "     map.addControl(new GOverviewMapControl());\r\n"
                + "     if (window.attachEvent) {\r\n"
                + "       window.attachEvent(\"onresize\", function() {this.map.onResize()} );\r\n"
                + "     } else {\r\n"
                + "       window.addEventListener(\"resize\", function() {this.map.onResize()} , false);\r\n"
                + "     }                 // add a polyline overlay\r\n"
                + "     var points = new Array();" + "points=[];" + "";
        // "points.push(new GPoint(3.11492833333333,45.75697))";
        // map.addOverlay(new GPolyline(points,"#960000",2,.75));
        writeTxt(header);
        minlat = 9999;
        maxlat = -9999;
        minlon = 9999;
        maxlon = -9999;
    }

    private double minlat;
    private double maxlat;
    private double minlon;
    private double maxlon;

    protected void writeDataHeader() {
        String header;
        if (m_isWayType) {
        } else {
            header = "points=[];";
            m_newTrack = true;
            writeTxt(header);
        }
    }

    protected void writeDataFooter() {
        if (m_isWayType) {
        } else {
            writeTxt("map.addOverlay(new GPolyline(points,\"#0000FF\",2,.75));\r\n");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(final GPSRecord r) {
        super.writeRecord(r);


            if (!ptFilters[m_currentFilter].doFilter(r)) {
                // The track is interrupted by a removed log item.
                // Break the track in the output file
                if (!m_isWayType && !m_newTrack && !firstRecord) {
                    m_newTrack = true;
                    writeTxt("map.addOverlay(new GPolyline(points,\"#0000FF\",2,.75));\r\n");
                    writeTxt("points=[];");
                    // "points.push(new GPoint(3.11492833333333,45.75697))";
                    // map.addOverlay(new GPolyline(points,"#960000",2,.75));
                }
            } else {
                // This log item is to be transcribed in the output file.

                // StringBuffer rec=new StringBuffer(1024);
                rec.setLength(0);
                m_newTrack = false;
                // " <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
                // " <ele>12.863281</ele>"+
                // " <time>2005-05-16T11:49:06Z</time>"+
                // " <name>Cala Sant Vicen� - Mallorca</name>"+
                // " <sym>City</sym>"+
                // " </wpt>"+

                // lat="latitudeType [1] ?"
                // lon="longitudeType [1] ?">
                // if(m_isWayType) {
                // rec.append("<wpt ");
                // } else {
                // rec.append("<trkpt ");
                // }
                if (r.hasPosition()) {
                    rec.append("points.push(new GLatLng(");
                    rec.append(JavaLibBridge.toString(r.getLatitude(), 6));
                    rec.append(',');
                    rec.append(JavaLibBridge.toString(r.getLongitude(), 6));
                    rec.append("));");

                    if (r.getLatitude() < minlat) {
                        minlat = r.getLatitude();
                    }
                    if (r.getLatitude() > maxlat) {
                        maxlat = r.getLatitude();
                    }
                    if (r.getLongitude() < minlon) {
                        minlon = r.getLongitude();
                    }
                    if (r.getLongitude() > maxlon) {
                        maxlon = r.getLongitude();
                    }
                }
                // //
                // // if(m_isWayType) {
                // //
                // rec.append("<name>"+JavaLibBridge.toString(m_recCount)+"</name>\r\n");
                // // }
                // // <ele> xsd:decimal </ele> [0..1] ? (elevation in meters)
                //                
                // // <time> xsd:dateTime </time> [0..1] ?
                // //2005-05-16T11:49:06Z
                // if((activeFields.utc!=0)) {
                // rec.append("<time>");
                // if(activeFields.utc!=0) {
                // timeStr+=JavaLibBridge.toString(t.year)+"-"
                // +( t.month<10?"0":"")+JavaLibBridge.toString(t.month)+"-"
                // +( t.day<10?"0":"")+JavaLibBridge.toString(t.day)+"T"
                // +( t.hour<10?"0":"")+JavaLibBridge.toString(t.hour)+":"
                // +(t.minute<10?"0":"")+JavaLibBridge.toString(t.minute)+":"
                // +(t.second<10?"0":"")+JavaLibBridge.toString(t.second)
                // ;
                // if(activeFields.milisecond!=0) {
                // timeStr+=".";
                // timeStr+=(s.milisecond<100)?"0":"";
                // timeStr+=(s.milisecond<10)?"0":"";
                // timeStr+=JavaLibBridge.toString(s.milisecond);
                // }
                // timeStr+="Z";
                // rec.append(timeStr);
                // }
                // rec.append("</time>\r\n");
                // }
                // // <magvar> degreesType </magvar> [0..1] ?
                // if((activeFields.heading!=0)) {
                // rec.append("<magvar>");
                // rec.append(JavaLibBridge.toString(s.heading));
                // rec.append("</magvar>\r\n");
                // }
                // // <geoidheight> xsd:decimal </geoidheight> [0..1] ?
                // if((activeFields.height!=0)) {
                // rec.append("<ele>");
                // rec.append(JavaLibBridge.toString(s.height,3));
                // rec.append("</ele>\r\n");
                // }
                //                
                // // <name> xsd:string </name> [0..1] ?
                // rec.append("<name>");
                // if(m_isWayType) {
                // rec.append("wpt-");
                // } else {
                // rec.append("trkpt-");
                // }
                // if((activeFields.utc!=0)) {
                // rec.append(timeStr);
                // } else {
                // rec.append(JavaLibBridge.toString(m_recCount));
                // }
                // rec.append("</name>\r\n");
                //                
                // // <cmt> xsd:string </cmt> [0..1] ?
                // // <desc> xsd:string </desc> [0..1] ?
                // // <src> xsd:string </src> [0..1] ? // Source of data
                //                
                // // <link> linkType </link> [0..*] ?
                // // <sym> xsd:string </sym> [0..1] ?
                // // <type> xsd:string </type> [0..1] ?
                // if((activeFields.rcr!=0)) {
                // if((s.rcr&BT747_dev.RCR_TIME_MASK)!=0) {
                // rcrStr+="T";
                // }
                // if((s.rcr&BT747_dev.RCR_SPEED_MASK)!=0) {
                // rcrStr+="S";
                // }
                // if((s.rcr&BT747_dev.RCR_DISTANCE_MASK)!=0) {
                // rcrStr+="D";
                // }
                // if((s.rcr&BT747_dev.RCR_BUTTON_MASK)!=0) {
                // rcrStr+="B";
                // }
                // // if(style.length()!=1) {
                // // style="M";
                // // }
                // rec.append("<type>");
                // rec.append(rcrStr);
                // rec.append("</type>\r\n");
                // }
                //                
                // // <fix> fixType </fix> [0..1] ?
                // if((activeFields.valid!=0)) {
                // switch(s.valid) {
                // case 0x0001:
                // fixStr+="none"; //"No fix";
                // break;
                // case 0x0002:
                // fixStr+= "3d"; //"SPS";
                // break;
                // case 0x0004:
                // fixStr+="dgps";
                // break;
                // case 0x0008:
                // fixStr+="pps"; // Military signal
                // break;
                // case 0x0010:
                // //tmp+="RTK";
                // break;
                // case 0x0020:
                // //tmp+="FRTK";
                // break;
                // case 0x0040:
                // //tmp+= "Estimated mode";
                // break;
                // case 0x0080:
                // //tmp+= "Manual input mode";
                // break;
                // case 0x0100:
                // //tmp+= "Simulator mode";
                // break;
                // default:
                // //tmp+="Unknown mode";
                // }
                // if(fixStr.length()!=0) {
                // rec.append("<fix>");
                // rec.append(fixStr);
                // rec.append("</fix>\r\n");
                // }
                // }
                // // <sat> xsd:nonNegativeInteger </sat> [0..1] ?
                // // <hdop> xsd:decimal </hdop> [0..1] ?
                // if((activeFields.hdop!=0)) {
                // hdopStr=JavaLibBridge.toString(s.hdop/100.0,2);
                // rec.append("<hdop>");
                // rec.append(hdopStr);
                // rec.append("</hdop>\r\n");
                // }
                // // <vdop> xsd:decimal </vdop> [0..1] ?
                // if((activeFields.vdop!=0)) {
                // rec.append("<vdop>");
                // rec.append(JavaLibBridge.toString(s.vdop/100.0,2));
                // rec.append("</vdop>\r\n");
                // }
                // // <pdop> xsd:decimal </pdop> [0..1] ?
                // if((activeFields.pdop!=0)) {
                // rec.append("<pdop>");
                // rec.append(JavaLibBridge.toString(s.pdop/100.0,2));
                // rec.append("</pdop>\r\n");
                // }
                // // <ageofdgpsdata> xsd:decimal </ageofdgpsdata> [0..1] ?
                // if((activeFields.nsat!=0)) {
                // nsatStr+=JavaLibBridge.toString(s.nsat/256);
                // nsatStr+="(";
                // nsatStr+=JavaLibBridge.toString(s.nsat%256);
                // nsatStr+=")";
                // rec.append("<nsat>");
                // rec.append(nsatStr);
                // rec.append("</nsat>\r\n");
                // }
                // if((activeFields.dage!=0)) {
                // rec.append("<ageofdgpsdata>");
                // rec.append(JavaLibBridge.toString(s.dage));
                // rec.append("</ageofdgpsdata>\r\n");
                // }
                //                
                // // <dgpsid> dgpsStationType </dgpsid> [0..1] ?
                // if((activeFields.dsta!=0)) {
                // rec.append("<dgpsid>");
                // rec.append(JavaLibBridge.toString(s.dsta));
                // rec.append("</dgpsid>\r\n");
                // }
                // // <extensions> extensionsType </extensions> [0..1] ?
                //                
                // if((activeFields.speed!=0)) {
                // rec.append("<speed>");
                // rec.append(JavaLibBridge.toString(s.speed,3));
                // rec.append("</speed>\r\n");
                // }
                //                
                // if((activeFields.distance!=0)) {
                // rec.append("<distance>");
                // rec.append(JavaLibBridge.toString(s.distance,2)); //+" m\r\n"
                // rec.append("</distance>\r\n");
                // }
                //                
                // // No comments, so commented out.
                // rec.append("<cmt>");
                // rec.append("<![CDATA[");
                // rec.append(fixStr+","+rcrStr+","+hdopStr+","+nsatStr);
                // // // <pdop> xsd:decimal </pdop> [0..1] ?
                // rec.append("]]>");
                // rec.append("</cmt>\r\n");
                //                
                // if(m_isWayType) {
                // rec.append("</wpt>\r\n");
                // } else {
                // rec.append("</trkpt>\r\n");
                // }

                writeTxt(rec.toString());

            }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#FinaliseFile()
     */
    public void finaliseFile() {
        if (isOpen()) {
            String footer;
            writeDataFooter();
            footer = "map.setCenter(new GLatLng("
                    + JavaLibBridge.toString((maxlat + minlat) / 2)
                    + ","
                    + JavaLibBridge.toString((maxlon + minlon) / 2)
                    + "));"
                    + "map.setZoom(map.getBoundsZoomLevel(new GLatLngBounds(new GLatLng("
                    + minlat
                    + ','
                    + minlon
                    + "),new GLatLng("
                    + maxlat
                    + ','
                    + maxlon
                    + "))));"
                    + "\r\n"
                    + "   }\r\n"
                    + "   else {\r\n"
                    + "     document.getElementById(\"quicklinks\").innerHTML = \"Your web browser is not compatible with this website.\"\r\n"
                    + "   }\r\n" + "//]]>\r\n" + "</script>\r\n" + "\r\n"
                    + "</body>\r\n" + "</html>";
            writeTxt(footer);
        }
        super.finaliseFile();

    }

}
