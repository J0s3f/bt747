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

/**
 * Class to write a Google Maps HTML file.
 * 
 * @author Mario De Weerd
 */
public class GPSGmapsHTMLEncodedFile extends GPSFile {
    /**
     * Local StringBuffer for output.
     */
    private StringBuffer rec = new StringBuffer(1024); // reused stringbuffer

    /**
     * When true, currently handling waypoints, otherwise trackpoints.
     */
    private boolean isWayType;
    private boolean isNewTrack = true;
    private int currentFilter;

    private Track track;

    private int trackIndex = 0; // Index for tracks

    private String trackOnClickFuncCalls = ""; // Javascript function calls if
    // click.
    private StringBuffer infoHtmls = new StringBuffer(1024);
    private String trackStartInfo = "";
    private String trackDescription = "";

    /**
     * 
     */
    public GPSGmapsHTMLEncodedFile() {
        super();
        numberOfPasses = 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public final void initialiseFile(final String basename, final String ext,
            final int card, final int oneFilePerDay) {
        super.initialiseFile(basename, ext, card, oneFilePerDay);
        currentFilter = GPSFilter.WAYPT;
        isWayType = true;

        trackDescription = "";
        resetTrack();
    }

    private void resetTrack() {
        track = new Track();
        infoHtmls.setLength(0);
    }

    public final boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
            // if (m_multipleFiles) {
            // closeFile();
            // }
            trackDescription = "";
            nbrOfPassesToGo--;
            previousDate = 0;
            isWayType = false;
            currentFilter = GPSFilter.TRKPT;
            // if (!m_multipleFiles) {
            // writeDataHeader();
            // }
            return true;
        } else {
            return false;
        }
    }

    private String googleKeyCode = ""; // Google key code for web

    private String keyCode() {
        if (googleKeyCode.length() != 0) {
            return ";key=" + googleKeyCode;
        }
        return ""; // default
    }

    protected final void writeFileHeader(final String trackName) {
        StringBuffer l_header = new StringBuffer(1700);
        l_header.setLength(0);
        l_header
                .append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
                        + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                        + "<html xmlns=\"http://www.w3.org/1999/xhtml\""
                        + " xmlns:v=\"urn:schemas-microsoft-com:vml\">\n"
                        + "<head>\n" + "<title>");
        l_header.append(trackName);

        l_header
                .append("</title>\n"
                        + "<meta http-equiv=\"Content-Type\" "
                        + "content=\"text/html; charset=utf-8\" />\n"
                        + "<meta name=\"description\" "
                        + "content=\"Tracks - Generated with BT747 V"
                        + Version.VERSION_NUMBER
                        + " http://sf.net/projects/bt747 - powered by Google Maps\" />\n"
                        + "\n"
                        + "<script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp");
        l_header.append(keyCode());
        l_header
                .append("\" type=\"text/javascript\">\n"
                        + "</script>\n"
                        + "<style type=\"text/css\">\n"
                        + " v\\:* {\n"
                        + "  behavior:url(#default#VML);\n"
                        + " }\n"
                        + " html, body, #map\n"
                        + "  {\n"
                        + "   width: 100%;\n"
                        + "   height: 100%;\n"
                        + "  }\n"
                        + " body {\n"
                        + "  margin-top: 0px;\n"
                        + "  margin-right: 0px;\n"
                        + "  margin-left: 0px;\n"
                        + "  margin-bottom: 0px;\n"
                        + " }\n"
                        + "\n"
                        + "</style>\n"
                        + "</head>\n"
                        + "\n"
                        + "<body onload=\"setFooter()\""
                        + " onresize=\"setFooter()\""
                        + " onunload=\"GUnload()\">\n"
                        + "<div id=\"map\"> </div>\n"
                        + "<div id=\"footer\">\n"
                        + "<span id=\"latlon\">Click on map to get position."
                        + "  </span>Tracks: \n"
                        + "<script type=\"text/javascript\">\n"
                        // call the info window opener for the given index
                        + "if (GBrowserIsCompatible()) {\n"
                        + "function latlonTxt(latlon) {\n"
                        + " if(latlon) {\n"
                        + "     var s=\'<b>Last click: \'+ latlon.toUrlValue()+\' </b>\';\n"
                        + "  document.getElementById(\"latlon\").innerHTML = s;\n"
                        + " }"
                        + "}"
                        + "function latlonFunc() {\n"
                        + " return function(overlay,latlon) {latlonTxt(latlon);}\n"
                        + "}\n"
                        + ""
                        + " function makeOpenerCaller(i) {"
                        + "  return function() { showMarkerInfo(i); };"
                        + " }\n" // open an info window
                        + "\n"
                        + "  function showMarkerInfo(i) {\n"
                        + "   markers[i].openInfoWindowHtml(infoHtmls[i]);\n"
                        + "  }\n" // create the map
                        // Code from http://www.pompage.net/pompe/pieds/
                        + "function getWindowHeight() {\n"
                        + " var windowHeight=0;\n"
                        + " if (typeof(window.innerHeight)==\'number\') {\n"
                        + "  windowHeight=window.innerHeight;\n"
                        + " } else {\n"
                        + "  if (document.documentElement&&"
                        + "document.documentElement.clientHeight) {\n"
                        + "  windowHeight = "
                        + "document.documentElement.clientHeight;\n"
                        + "  } else {\n"
                        + "   if (document.body&&document.body.clientHeight) {\n"
                        + "    windowHeight=document.body.clientHeight;\n"
                        + "   }\n"
                        + "  }\n"
                        + " }\n"
                        + " return windowHeight;\n"
                        + "}"
                        + "function setFooter() {\n"
                        + " if (document.getElementById) {\n"
                        + "  var windowHeight=getWindowHeight();\n"
                        + "  var footerElement=document.getElementById(\'footer\');\n"
                        + "  var footerHeight=footerElement.offsetHeight;\n"
                        + "  if (windowHeight-footerHeight>400) {\n"
                        + "   document.getElementById(\'map\').style.height=\n"
                        + "    (windowHeight-footerHeight)+\'px\';\n"
                        + "  } else {\n"
                        + "   document.getElementById(\'map\').style.height=400;\n"
                        // + " footerElement.style.position=\'static\';\n"
                        + "  }\n" // else
                        + " }\n"
                        + "}\n" // Function
                        + "function trackClick(trk,val) {\n"
                        + " if (val == 1) {\n"
                        + "  map.addOverlay(trk);\n"
                        + " } else {\n"
                        + "  map.removeOverlay(trk);\n"
                        + " } }\n"
                        + "function makeLatLonInfo(h) {\n"
                        + " return function(latlng) {\n"
                        + "  latlonTxt(latlng);\n"
                        + "  map.openInfoWindowHtml(latlng, h);\n"
                        + " };\n"
                        + "}"
                        + " var clickStr; clickStr=\"\";"
                        + " function clickString() {\n"
                        + "  document.write(clickStr);\n"
                        + " }\n"

                        // + "var blueIcon = new GIcon(G_DEFAULT_ICON);"
                        // + "blueIcon.image =
                        // \"http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png\";"
                        // + "markerOptions = { icon:blueIcon };" + // Set up
                        // our GMarkerOptions object

                        + " var map = new GMap2(document.getElementById(\"map\"));\n"
                        + " map.setCenter(new GLatLng(0,0));\n"
                        // + "var mgrOptions = { borderPadding: 50, maxZoom: 15,
                        // trackMarkers: true };\n"
                        + " var mgr = new GMarkerManager(map);\n"
                        + " map.setMapType(G_SATELLITE_MAP);\n"
                        + " map.addMapType(G_PHYSICAL_MAP);\n "
                        + " map.enableScrollWheelZoom();\n"
                        + " map.addControl(new GLargeMapControl());\n"
                        + " map.addControl(new GMapTypeControl());\n"
                        + " map.addControl(new GScaleControl());\n"
                        + " map.addControl(new GOverviewMapControl());\n"
                        // + " if (window.attachEvent) {\n"
                        // + " window.attachEvent(\"onresize\", function()
                        // {this.map.onResize()} );\n"
                        // + " } else {\n"
                        // + " window.addEventListener(\"resize\", function()
                        // {this.map.onResize()} , false);\n"
                        // + " }\n" + // add a polyline overlay
                        + "var OSM = new GMapType(\n"
                        + "[ new GTileLayer(null,1,18,\n"
                        + "{ tileUrlTemplate: 'http://tile.openstreetmap.org/{Z}/{X}/{Y}.png',\n"
                        + " isPng: true, opacity: 1.0 })],\n"
                        + "new GMercatorProjection(19),\n" + "'OSM',\n"
                        + "{ errorMessage:\"More OSM coming soon\"}\n" + ");\n"
                        + "map.addMapType(OSM)\n");
        // "points.push(new GPoint(3.11492833333333,45.75697))";
        // map.addOverlay(new GPolyline(points,"#960000",2,.75));
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
            trackOnClickFuncCalls += "trackClick(track" + trackIndex
                    + ",this.checked);";
            rec.append("var track");
            rec.append(trackIndex);

            rec.append(";\nmap.addOverlay(");
            rec.append("track");
            rec.append(trackIndex);
            rec.append("=new GPolyline.fromEncoded({\n" + "  color: \"#");
            rec.append(hexColor);
            rec.append("\",\n" + "  weight: 4,\n" + "  opacity: 0.8,\n"
                    + "  points: \"");
            rec.append(tmp);
            rec.append("\",\n" + "  levels: \"");
            rec.append(res.get("encodedLevels"));

            rec.append("\",\n" + "  zoomFactor: 2,\n" + "  numLevels: 18\n"
                    + "}));\n");

            rec.append("GEvent.addListener(track");
            rec.append(trackIndex);
            rec.append(",'click',makeLatLonInfo(\"<b>Track span</b><br/>"
                    + trackStartInfo + "<br/>");
            rec.append("<b>#" + previousRec + "# </b>"
                    + CommonOut.getTimeStr(previousTime));
            rec.append("\"));\n");
            // writeTxt(PolylineEncoder.replace(rec.toString(),"\\", "\\\\"));
            writeTxt(rec.toString());
            rec.setLength(0);
        }
        trackIndex++;
        resetTrack();
    }

    protected final void writeDataFooter() {
        if (isWayType) {
            if (track.size() != 0) {
                rec.setLength(0);
                rec.append("var markers;markers=[");
                for (int i = track.size() - 1; i >= 0; i--) {
                    rec.append("new GMarker(new GLatLng(");
                    rec
                            .append(Convert.toString(track.get(i)
                                    .getLatDouble(), 5));
                    rec.append(',');
                    rec
                            .append(Convert.toString(track.get(i)
                                    .getLonDouble(), 5));
                    rec.append("))");
                    if (i != 0) {
                        rec.append(',');
                    }
                }
                rec.append("];");
                rec.append("infoHtmls=[");
                rec.append(infoHtmls.toString());
                rec.append("];");
                rec.append("for (var i=0; i<markers.length; ++i) {\n");
                rec
                        .append("GEvent.addListener(markers[i],\'click\',makeOpenerCaller(i));\n");
                rec.append("}\n");
                rec.append("mgr.addMarkers(markers,0);mgr.refresh();\n");
                rec.append("\n");
                // Small popup:
                writeTxt(rec.toString());
            }
            resetTrack();
        } else {
            endTrack(goodTrackColor);
            splitOrEndTrack();
        }
    }

    private void splitOrEndTrack() {
        StringBuffer lrec = new StringBuffer();
        if (!isWayType && (trackOnClickFuncCalls.length() != 0)) {
            lrec.setLength(0);
            // Vm.debug("Do:"trackDescription);

            lrec.append("clickStr+= \"" + trackDescription
                    + "<input type=\\\"checkbox\\\"" + "onClick=\\\""
                    + trackOnClickFuncCalls + "\\\" checked/>\";\n");
            writeTxt(lrec.toString());
            trackOnClickFuncCalls = "";
            trackDescription = "";
        }
    }

    /**
     * UTC time of previous record.
     */
    private int previousTime = 0;
    /**
     * Record number of previous record
     */
    private int previousRec = 0;

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord s) {
        super.writeRecord(s);

        if (activeFields != null) {

            if (!ptFilters[currentFilter].doFilter(s)) {
                // The track is interrupted by a removed log item.
                // Break the track in the output file
                if (!isWayType && !isNewTrack && !firstRecord
                        && !ignoreBadPoints) {
                    isNewTrack = true;
                    if (track.size() != 0) {
                        Trackpoint tp = track.get(track.size() - 1);
                        endTrack(goodTrackColor);
                        track.addTrackpoint(tp);
                    }
                    // "points.push(new GPoint(3.11492833333333,45.75697))";
                    // map.addOverlay(new GPolyline(points,"#960000",2,.75));
                }
            } else {
                // This log item is to be transcribed in the output file.

                // StringBuffer rec=new StringBuffer(1024);
                boolean isTimeSPlit;
                isTimeSPlit = (activeFields.utc != 0)
                        && ((s.utc - previousTime) > trackSepTime);
                rec.setLength(0);
                if (isNewTrack || isTimeSPlit) {
                    isNewTrack = false;
                    if ((activeFields.latitude != 0)
                            && (activeFields.longitude != 0)) {
                        if (!isTimeSPlit) {
                            track.addTrackpoint(new Trackpoint(s.latitude,
                                    s.longitude));
                            endTrack(badTrackColor);
                        } else {
                            // points quite separated -
                            // No line, but separate track
                            // bt747.sys.Vm.debug(""+(s.utc-previousTime)+":"+isTimeSPlit);
                            endTrack(goodTrackColor);
                            splitOrEndTrack();
                        }
                    }
                    resetTrack();
                    // Vm.debug("Pos:"+getTimeStr(s));
                    trackStartInfo = "<b>#" + s.recCount + "# </b>"
                            + CommonOut.getTimeStr(activeFields, t);
                    if (trackDescription.length() == 0) {
                        trackDescription = CommonOut
                                .getTimeStr(activeFields, t);
                        // bt747.sys.Vm.debug(trackDescription);
                    }
                }

                if ((activeFields.utc != 0)) {
                    previousTime = s.utc;
                    previousRec = s.recCount;
                }
                // " <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
                // " <ele>12.863281</ele>"+
                // " <time>2005-05-16T11:49:06Z</time>"+
                // " <name>Cala Sant Vicen� - Mallorca</name>"+
                // " <sym>City</sym>"+
                // " </wpt>"+

                // lat="latitudeType [1] ?"
                // lon="longitudeType [1] ?">
                // if (m_isWayType) {
                // rec.append("<wpt ");
                // } else {
                // rec.append("<trkpt ");
                // }
                if ((activeFields.latitude != 0)
                        && (activeFields.longitude != 0)) {
                    // rec.append("points.push(new GLatLng(");
                    // rec.append(Convert.toString(s.latitude,6));
                    // rec.append(',');
                    // rec.append(Convert.toString(s.longitude,6));
                    // rec.append("));");
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

                if (isWayType) {
                    infoHtmls.append("\"");
                    CommonOut.getHtml(infoHtmls, s, activeFields,
                            selectedFileFields, t, recordNbrInLogs, imperial);
                    infoHtmls.append("\",\n");
                }

                writeTxt(rec.toString());

            }
        } // activeFields!=null
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#FinaliseFile()
     */
    public final void finaliseFile() {
        if (this.isOpen()) {
            String footer;
            writeDataFooter();
            footer = "clickString();\n"
                    + "GEvent.addListener(map,\'click\',latlonFunc());\n"
                    + "map.setCenter(new GLatLng("
                    + Convert.toString((maxlat + minlat) / 2)
                    + ","
                    + Convert.toString((maxlon + minlon) / 2)
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
                    + "\n"
                    + "map.enableContinuousZoom();\n"
                    + "map.enableDoubleClickZoom();\n"
                    + "   }\n"
                    + "   else {\n"
                    + "     document.getElementById(\"quicklinks\").innerHTML = \"Your web browser is not compatible with this website.\"\n"
                    + "   }\n"
                    + "//]]>\n"
                    + "</script>\n"
                    + " </div>\n"
                    + "</body>\n" + "</html>";
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
     * @param sGoogleKeyCode
     *            The googleKeyCode to set.
     */
    public final void setGoogleKeyCode(final String sGoogleKeyCode) {
        this.googleKeyCode = sGoogleKeyCode;
    }
}
