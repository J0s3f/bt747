//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************  
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;
import gps.tracks.PolylineEncoder;
import gps.tracks.Track;
import gps.tracks.Trackpoint;

import bt747.Version;
import bt747.sys.Convert;
import bt747.util.Hashtable;

/**Class to write a Google Maps HTML file.
 * @author Mario De Weerd
 */
public class GPSGmapsHTMLEncodedFile extends GPSFile {
    private StringBuffer rec = new StringBuffer(1024);  // reused stringbuffer

    private boolean isWayType;
    private boolean isNewTrack = true;
    private int currentFilter;
    
    private Track track;
    
    private int trackIndex = 0;  // Index for tracks

    private String trackOnclickFuncCalls = ""; // Javascript function calls if click.
    
    /**
     * 
     */
    public GPSGmapsHTMLEncodedFile() {
        super();
        C_NUMBER_OF_PASSES = 2;
    }

    /* (non-Javadoc)
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public final void initialiseFile(
            final String basename,
            final String ext,
            final int card,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, card, oneFilePerDay);
        currentFilter = GPSFilter.C_WAYPT_IDX;
        isWayType = true;
        
        track = new Track();
        trackDescription = "";
    }
    
    
    
    public final boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
//            if (m_multipleFiles) {
//                closeFile();
//            }
            trackDescription = "";
            nbrOfPassesToGo--;
            previousDate = 0;
            isWayType = false;
            currentFilter = GPSFilter.C_TRKPT_IDX;
//            if (!m_multipleFiles) {
//                writeDataHeader();
//            }
            return true;
        } else {
            return false;
        }
    }
    
    private String googleKeyCode = "";  // Google key code for web
    private String keyCode() {
        if (googleKeyCode.length() != 0) {
            return ";key=" + googleKeyCode;
        }
        return ""; // default
    }


    protected final void writeFileHeader(final String trackName) {
        StringBuffer l_header = new StringBuffer(1700);
        l_header.setLength(0);
        l_header.append(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
                + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\""
                + " xmlns:v=\"urn:schemas-microsoft-com:vml\">\r\n"
                + "<head>\r\n"
                + "<title>");
        l_header.append(trackName);
        
        l_header.append("</title>\r\n"
                + "<meta http-equiv=\"Content-Type\" "
                + "content=\"text/html; charset=utf-8\" />\r\n"
                + "<meta name=\"description\" "
                + "content=\"Tracks - Generated with BT747"
                + Version.VERSION_NUMBER
                + " http://sf.net/projects/bt747 - powered by Google Maps\" />\r\n"
                + "\r\n"
                + "<script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp");
        l_header.append(keyCode());
        l_header.append("\" type=\"text/javascript\">\r\n"
                + "</script>\r\n"
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
                + "<body onload=\"setFooter()\" onresize=\"setFooter()\""
                + " onunload=\"GUnload()\">\r\n"
                + "<div id=\"map\"> </div>\r\n"
                + "<div id=\"footer\">\r\n"
                + "\r\n"
                + "<script type=\"text/javascript\">\r\n"
                + "//<![CDATA[ \r\n"             // check for compatibility 
                + "\r\n"
                // call the info window opener for the given index
                + "if (GBrowserIsCompatible()) {\r\n"  
                + "\r\n"
                + "  function makeOpenerCaller(i) {"
                + "     return function() { showMarkerInfo(i); };"
                + "  }\r\n"                 // open an info window 
                + "\r\n"
                + "  function showMarkerInfo(i) {\r\n"
                + "     markers[i].openInfoWindowHtml(infoHtmls[i]);\r\n"
                + "  }\r\n"                 // create the map
                // Code from http://www.pompage.net/pompe/pieds/
                + "function getWindowHeight() {\r\n"
                + "    var windowHeight=0;\r\n"
                + "    if (typeof(window.innerHeight)==\'number\') {\r\n"
                + "        windowHeight=window.innerHeight;\r\n"
                + "    }\r\n"
                + "    else {\r\n"
                + "     if (document.documentElement&&\r\n"
                + "       document.documentElement.clientHeight) {\r\n"
                + "         windowHeight = "
                + "document.documentElement.clientHeight;\r\n"
                + "    }\r\n"
                + "    else {\r\n"
                + "     if (document.body&&document.body.clientHeight) {\r\n"
                + "         windowHeight=document.body.clientHeight;\r\n"
                + "      }\r\n"
                + "     }\r\n"
                + "    }\r\n"
                + "    return windowHeight;\r\n"
                + "}"
                + "function setFooter() {\r\n"
                + " if (document.getElementById) {\r\n"
                + "  var windowHeight=getWindowHeight();\r\n"
                + "  var footerElement=document.getElementById(\'footer\');\r\n"
                + "  var footerHeight=footerElement.offsetHeight;\r\n"
                + "  if (windowHeight-footerHeight>400) {\r\n"
                + "   document.getElementById(\'map\').style.height=\r\n"
                + "    (windowHeight-footerHeight)+\'px\';\r\n"
                + "  } else {\\r\\n"
                + "   document.getElementById(\'map\').style.height=400;\r\n"
//              + "     footerElement.style.position=\'static\';\r\n"
                + "  }\r\n"  // else
                + " }\r\n"
                + "}\r\n" // Function 
                + "    function trackClick(trk,val) {\r\n"
                + "      if (val == 1) {\r\n"
                + "        map.addOverlay(trk);\r\n"
                + "      } else {\r\n"
                + "        map.removeOverlay(trk);\r\n"
                + "      } }\r\n"
                + "    var clickStr; clickStr=\"\";"
                + "    function clickString() {\r\n"
                + "    document.write(clickStr);"
                + "    }\r\n"
                
//                + "var blueIcon = new GIcon(G_DEFAULT_ICON);"
//                + "blueIcon.image = \"http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png\";"
//                + "markerOptions = { icon:blueIcon };" + // Set up our GMarkerOptions object
                
                + "     var map = new GMap2(document.getElementById(\"map\"));\r\n"
                + "     map.setCenter(new GLatLng(0,0));\r\n"
               // + "var mgrOptions = { borderPadding: 50, maxZoom: 15, trackMarkers: true };\r\n"
                + "var mgr = new GMarkerManager(map);\r\n"
                + "     map.setMapType(G_SATELLITE_MAP);\r\n"
                + "     map.enableScrollWheelZoom();\r\n"
                + "     map.addControl(new GLargeMapControl());\r\n"
                + "     map.addControl(new GMapTypeControl());\r\n"
                + "     map.addControl(new GScaleControl());\r\n"
                + "     map.addControl(new GOverviewMapControl());\r\n"
//                + "     if (window.attachEvent) {\r\n"
//                + "       window.attachEvent(\"onresize\", function() {this.map.onResize()} );\r\n"
//                + "     } else {\r\n"
//                + "       window.addEventListener(\"resize\", function() {this.map.onResize()} , false);\r\n"
//                + "     }\r\n" +                // add a polyline overlay 
                + "");
                //"points.push(new GPoint(3.11492833333333,45.75697))";
        //map.addOverlay(new GPolyline(points,"#960000",2,.75));
        writeTxt(l_header.toString());
    }
    
    private double minlat;
    private double maxlat;
    private double minlon;
    private double maxlon;
    
    protected final void writeDataHeader() {
        if (isWayType) {
        } else {
            isNewTrack = true;
            minlat = 90;
            maxlat = -90;
            minlon = 180;
            maxlon = -180;
        }
    }
    
    protected final void endTrack(final String hexColor) {
        PolylineEncoder a = new PolylineEncoder();
        Hashtable res;
        if (false) {
            res = a.createEncodings(track, 17, 4);
        } else {
            res = a.dpEncode(track);
        }
        String tmp;
        tmp = (String) res.get("encodedPoints");
        
        
        if (tmp.length() >= 2) {
            rec.setLength(0);
            
            // Track assignment
            trackOnclickFuncCalls +=
                "trackClick(track" + trackIndex
                + ",this.checked);";
            rec.append("var track");
            rec.append(trackIndex);
            
            rec.append(";\r\nmap.addOverlay(");
            rec.append("track");
            rec.append(trackIndex);
            rec.append("=new GPolyline.fromEncoded({\r\n" 
                    + "  color: \"#");
            rec.append(hexColor);
            rec.append("\",\r\n"
                    + "  weight: 4,\r\n" 
                    + "  opacity: 0.8,\r\n" 
                    + "  points: \"");
            rec.append(tmp);
            rec.append("\",\r\n"
                    + "  levels: \"");
            rec.append(res.get("encodedLevels"));
            
            rec.append("\",\r\n" 
                    + "  zoomFactor: 2,\r\n" 
                    + "  numLevels: 18\r\n" 
                    + "}));\r\n");
            //writeTxt(PolylineEncoder.replace(rec.toString(),"\\", "\\\\"));
            writeTxt(rec.toString());
            rec.setLength(0);
        }

        //System.out.println(rec.toString());
        trackIndex++;
        track=new Track();
    }
    
    protected final void writeDataFooter() {
        if (isWayType) {
            if (track.size() != 0) {
                rec.setLength(0);
                rec.append("mgr.addMarkers([");
                for (int i = track.size() - 1; i >= 0; i--) {
                    rec.append("new GMarker(new GLatLng(");
                    rec.append(Convert.toString(track.get(i).getLatDouble(),
                            5));
                    rec.append(',');
                    rec.append(Convert.toString(track.get(i).getLonDouble(),
                            5));
                    rec.append("))");
                    if (i != 0) {
                        rec.append(',');
                    }
                }
                rec.append("],0);mgr.refresh();\r\n");
                writeTxt(rec.toString());
            }
            track = new Track();
        } else {
            endTrack(goodTrackColor);
            splitOrEndTrack();
        }
    }
    
    private void splitOrEndTrack() {
        StringBuffer lrec = new StringBuffer();
        if (!isWayType && (trackOnclickFuncCalls.length() != 0)) {
            lrec.setLength(0);
            //Vm.debug("Do:"+m_TrackDescription);

            lrec.append(
                    "clickStr+= \"" + trackDescription
                    + "<input type=\\\"checkbox\\\""
                    + "           onClick=\\\""
                    + trackOnclickFuncCalls
                    + "\\\" checked/>\";\r\n");
            writeTxt(lrec.toString());
            trackOnclickFuncCalls = "";
            trackDescription = "";
        }
    }

    private int m_PreviousTime = 0;
    private String trackDescription = "";
    
    private String getTimeStr(final GPSRecord s) {
        if (activeFields.utc != 0) {
            return 
            // Day
            ((t.getDay() < 10) ? "0" : "" )
            + Convert.toString(t.getDay()) + "-"
            // Month
            + MONTHS_AS_TEXT[t.getMonth()-1] + "-"
            + (((t.getYear() % 100)) < 10 ? "0" : "")
            + Convert.toString(t.getYear() % 100)
            + " "
            // Hour
            + ((t.getHour() < 10) ? "0" : "")
            + Convert.toString(t.getHour()) + ":"
            // Minute
            + ((t.getMinute() < 10) ? "0" : "")
            + Convert.toString(t.getMinute()) //+":"
            //+(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
            ;
        } else {
            return "";
        }
    }
    
    
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord s) {
        super.writeRecord(s);

        if (activeFields != null) {

            if (!ptFilters[currentFilter].doFilter(s)) {
                // The track is interrupted by a removed log item.
                // Break the track in the output file
                if (!isWayType && !isNewTrack && !firstRecord) {
                    isNewTrack = true;
                    if (track.size() != 0) {
                        Trackpoint tp = track.get(track.size() - 1);
                        endTrack(goodTrackColor);
                        track.addTrackpoint(tp);
                    }
                    //"points.push(new GPoint(3.11492833333333,45.75697))";
                    //map.addOverlay(new GPolyline(points,"#960000",2,.75));
                }
            } else {
                // This log item is to be transcribed in the output file.
                
                //StringBuffer rec=new StringBuffer(1024);
                rec.setLength(0);
                if (isNewTrack) {
                    isNewTrack = false;
                    if ((activeFields.latitude != 0)
                        && (activeFields.longitude != 0)
                       ) {
                        if ((s.utc - m_PreviousTime) < trackSepTime) {
                            track.addTrackpoint(
                                    new Trackpoint(s.latitude, s.longitude));
                            endTrack(badTrackColor);
                        } else {
                            // points quite separated - 
                            //   No line, but separate track
                            splitOrEndTrack();
                        }
                    }
                    track = new Track();
                    //Vm.debug("Pos:"+getTimeStr(s));
                    if (trackDescription.length() == 0) {
                        trackDescription = getTimeStr(s);
                        //Vm.debug(m_TrackDescription);
                    }
                }
                
                if ((activeFields.utc != 0)) {
                    m_PreviousTime = s.utc;
                }
                //                "  <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
                //                "    <ele>12.863281</ele>"+
                //                "    <time>2005-05-16T11:49:06Z</time>"+
                //                "    <name>Cala Sant Vicen� - Mallorca</name>"+
                //                "    <sym>City</sym>"+
                //                "  </wpt>"+
                
                // lat="latitudeType [1] ?"
                // lon="longitudeType [1] ?">
//                if (m_isWayType) {
//                    rec.append("<wpt ");
//                } else {
//                    rec.append("<trkpt ");
//                }
                if ((activeFields.latitude != 0)
                    && (activeFields.longitude != 0)) {
//                    rec.append("points.push(new GLatLng(");
//                    rec.append(Convert.toString(s.latitude,6));
//                    rec.append(',');
//                    rec.append(Convert.toString(s.longitude,6));
//                    rec.append("));");
                    Trackpoint tp = new Trackpoint(s.latitude, s.longitude);
//                    
                    track.addTrackpoint(tp);
                    
                    // Update map boundaries
                    if (tp.getLatDouble() < minlat) {
                        minlat = tp.getLatDouble();
                    }
                    if (tp.getLatDouble() > maxlat) {
                        maxlat = tp.getLatDouble();
                    }
                    if (tp.getLonDouble() < minlon) {
                        minlon = tp.getLonDouble();
                    }
                    if (tp.getLonDouble() > maxlon) {
                        maxlon = tp.getLonDouble();
                    }
                }
                
                writeTxt(rec.toString());
                
            }
        } // activeFields!=null
    }
    
    /* (non-Javadoc)
     * @see gps.GPSFile#FinaliseFile()
     */
    public final void finaliseFile() {
        if (this.isOpen()) {
            String footer;
            writeDataFooter();
            footer = "clickString();\r\n"
                + "map.setCenter(new GLatLng("
                + Convert.toString((maxlat + minlat) / 2)
                + ","
                + Convert.toString((maxlon + minlon) / 2)
                + "));"
                + "map.setZoom(map.getBoundsZoomLevel(new GLatLngBounds(new GLatLng("
                + minlat + ',' + minlon
                + "),new GLatLng("
                + maxlat + ',' + maxlon
                + "))));"
                + "\r\n"
                + "   }\r\n"
                + "   else {\r\n"
                + "     document.getElementById(\"quicklinks\").innerHTML = \"Your web browser is not compatible with this website.\"\r\n"
                + "   }\r\n"
                + "//]]>\r\n"
                + "</script>\r\n"
                + " </div>\r\n"
                + "</body>\r\n"
                + "</html>";
            writeTxt(footer);
        }
        super.finaliseFile();
        
    }
    
    /**
     * @return Returns the googleKeyCode.
     */
    public final String getGoogleKeyCode() {
        return googleKeyCode;
    }
    /**
     * @param sGoogleKeyCode The googleKeyCode to set.
     */
    public final void setGoogleKeyCode(final String sGoogleKeyCode) {
        this.googleKeyCode = sGoogleKeyCode;
    }
}
