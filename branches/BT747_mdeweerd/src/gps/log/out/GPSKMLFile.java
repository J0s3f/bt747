// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
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

import gps.BT747Constants;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.Version;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Time;

/**
 * Class to write a KML file.
 * 
 * @author Mario De Weerd
 * @author Herbert Geus (Waypoint code&Track code)
 */
public class GPSKMLFile extends GPSFile {
    private final StringBuffer rec = new StringBuffer(1024); // reused
    // stringbuffer
    /**
     * Values for {@link GPSConversionParameters#KML_TRACK_ALTITUDE_STRING}.
     */
    public static final String ABSOLUTE_HEIGHT = "absolute";
    public static final String RELATIVE_HEIGHT = "relativeToGround";
    public static final String CLAMPED_HEIGHT = "clampToGround";

    private boolean isWayType;
    private boolean isTrackType;
    private boolean isPathType;
    private int currentFilter;
    private String trackName;
    private int altitudeMode = 0; // 0 = altitude.
    private String altitudeModeIfHeight = GPSKMLFile.ABSOLUTE_HEIGHT;

    private int fileStartTime;
    private int lastTime;

    /**
     * 
     */
    public GPSKMLFile() {
        super();
        numberOfPasses = 3; // Three passes are needed.
        fileStartTime = 0;
        lastTime = 0;
    }
    
    private int posDigits;
    private int heightDigits;


    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);
        currentFilter = GPSFilter.WAYPT;
        isWayType = true;
        isTrackType = false;
        isPathType = false;
        final String am = getParamObject().getStringParam(
                GPSConversionParameters.KML_TRACK_ALTITUDE_STRING);
        if (am != null) {
            altitudeModeIfHeight = am;
        }
        if (getParamObject().hasParam(GPSConversionParameters.POSITION_DIGITS)) {
        	posDigits = getParamObject().getIntParam(GPSConversionParameters.POSITION_DIGITS);
        } else {
            posDigits = 6;
        }
        if (getParamObject().hasParam(GPSConversionParameters.HEIGHT_DIGITS)) {
        	heightDigits = getParamObject().getIntParam(GPSConversionParameters.HEIGHT_DIGITS);
        } else {
            heightDigits = 3;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#nextPass()
     */
    public final boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
            // if(m_multipleFiles) {
            // closeFile(); // Close every time - if single file, it gets
            // reopened.
            // }
            nbrOfPassesToGo--;
            previousDate = 0;
            // if(!m_multipleFiles) {
            // writeDataFooter();
            // }
            if (isWayType) {
            	// Next tracktype is path.
                isWayType = false;
                isPathType = true;
                currentFilter = GPSFilter.TRKPT;
            } else if (isPathType) {
            	// Next type is Track.
                isTrackType = true;
                isPathType = false;
                currentFilter = GPSFilter.TRKPT;
            } else if (isTrackType) {
                isTrackType = false;
                currentFilter = GPSFilter.TRKPT;
            }
            // if(!m_multipleFiles) {
            // writeDataHeader();
            // }
            return true;
        } else {
            return false;
        }
    }

    private final void addLookAt(final StringBuffer s, final int startTime,
            final int endTime) {
        final BT747Time time = JavaLibBridge.getTimeInstance();
        s.append("<LookAt>\r\n");
        s.append(" <gx:TimeSpan>\r\n");
        s.append("  <begin>");
        time.setUTCTime(startTime);
        s.append(CommonOut.getDateTimeISO8601(time, -1));
        s.append("</begin>\r\n");
        s.append("  <end>");
        time.setUTCTime(endTime);
        s.append(CommonOut.getDateTimeISO8601(time, -1));
        s.append("</end>\r\n");
        s.append(" </gx:TimeSpan>\r\n");
        s.append("</LookAt>\r\n");
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeFileHeader(java.lang.String)
     */
    public final void writeFileHeader(final String name) {
        final StringBuffer header = new StringBuffer(2048);
        header.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                // + "<kml xmlns=\"http://schemas.opengis.net/kml/2.2.0\""
                // opengis was not understood by google maps
                // + "<kml xmlns=\"http://earth.google.com/kml/2.2\"" +
                // " >\r\n"
                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\""
                + " xmlns:gx=\"http://www.google.com/kml/ext/2.2\""
                + " xmlns:kml=\"http://www.opengis.net/kml/2.2\""
                + " xmlns:atom=\"http://www.w3.org/2005/Atom\">"
                + "<Document"
                + " xmlns:atom=\"http://www.w3.org/2005/Atom\" >\r\n"
                + "<atom:generator uri=\"http://sf.net/projects/bt747\" "
                + "version=\"" + Version.VERSION_NUMBER + "\"" + ">"
                + "BT747" + "</atom:generator>\r\n" + "<name>" + name
                + "</name>\r\n" + "  <open>1</open>\r\n");

        WayPointStyleSet iter;
        iter = CommonOut.getWayPointStyles().iterator();
        while (iter.hasNext()) {
            final WayPointStyle style = iter.next();
            for (int j = 0; j < 2; j++) {
                header.append("  <Style id=\"Style" + style.getKey() + j
                        + "\">\r\n" + "    <IconStyle>\r\n"
                        + "      <Icon>\r\n" + "        <href>"
                        + style.getIconUrl() + "</href>\r\n"
                        + "      </Icon>\r\n" + "    </IconStyle>\r\n"
                        + "    <LabelStyle>\r\n" + "      <scale>" + j
                        + "</scale>\r\n" + "    </LabelStyle>\r\n"
                        + "  </Style>\r\n");
            }
        }

        iter = CommonOut.getWayPointStyles().iterator();
        while (iter.hasNext()) {
            final WayPointStyle style = iter.next();
            String x = "";
            if (style.getKey().length() > 1) {
                x = "X";
            }
            header.append("  <StyleMap id=\"Style" + x + style.getKey()
                    + "\">\r\n" + "    <Pair>\r\n"
                    + "      <key>normal</key>\r\n"
                    + "      <styleUrl>#Style" + style.getKey()
                    + "0</styleUrl>\r\n" + "    </Pair>\r\n"
                    + "    <Pair>\r\n" + "      <key>highlight</key>\r\n"
                    + "      <styleUrl>#Style" + style.getKey()
                    + "1</styleUrl>\r\n" + "    </Pair>\r\n"
                    + "  </StyleMap>\r\n");
        }

        writeTxt(header.toString());
        header.setLength(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeDataHeader()
     */
    protected final void writeDataHeader() {
        String header;
        if (isWayType) {
            header = "  <Folder>\r\n" + "    <name>My Waypoints</name>\r\n"
                    + "  <open>0</open>\r\n";
            isDateFolder = false;
        } else if (isTrackType) {
            header = "  <Folder>\r\n"// " <name>My Tracks</name>\r\n"+
                    + "    <name>My Trackpoints</name>\r\n"
                    + "    <Folder>\r\n"// "
                    // <name>Track-"+m_name+"</name>\r\n"+
                    + "    <name>Trackpoints" + trackName
                    + "</name>\r\n"
                    + "      <open>0</open>\r\n" + "\r\n";
        } else {
            header = "" // New folder for the tracks
                    + "  <Folder>\r\n"
                    // Folder name
                    + "  <name>My Tracks</name>\r\n"
                    // Folder is closed by default
                    + "  <open>0</open>\r\n"
                    // Name for the first track in the folder.
                    + "    <name>Track" + trackName + "</name>\r\n";
        }
        writeTxt(header);
    }

    private boolean trackStarted = false;

    private final void startTrack(final String name) {
        String color;
        String text;
        if (goodTrackColor.length() == 6) {
            color = goodTrackColor.substring(4)
                    + goodTrackColor.substring(2, 4)
                    + goodTrackColor.substring(0, 2);
        } else {
            color = "FFFFFF";
        }
        text = "<Placemark>" + "<name>" + JavaLibBridge.convertHTMLtoUTF8(name) + "</name>" + "\r\n"
                + "    <Style>\r\n" + "      <LineStyle>\r\n"
                + "        <color>ff" + color + "</color>\r\n"
                + "        <width>3.0</width>\r\n" + "      </LineStyle>\r\n"
                + "    </Style>\r\n";
        writeTxt(text);
        trackStarted = true;
    }

    private boolean isLineString = false;

    private final void endTrack() {
        if (isLineString) {
            writeTxt("      </coordinates>\r\n" + "     </LineString>\r\n");
            isLineString = false;
        }
        if (trackStarted) {
            writeTxt("    </Placemark>\r\n");
            trackStarted = false;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeDataFooter()
     */
    protected final void writeDataFooter() {
        String footer = "";
        if (isDateFolder) {
            footer = "   </Folder>\r\n";
            isDateFolder = false;
        }
        if (isWayType) {
            footer += "  </Folder>\r\n" + "\r\n";
        } else if (isTrackType) {
            footer += "    </Folder>\r\n" + "  </Folder>\r\n" + "\r\n";
        } else {
            endTrack();
            footer += "  </Folder>\r\n";
        }
        writeTxt(footer);
    }

    /**
     * Indicates if waypoint sub folder is opened.
     */
    private boolean isDateFolder;
    /**
     * Provides the current WayFolderDate
     */
    private String currentWayFolderDate = "";

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        rec.setLength(0);
        if (ptFilters[currentFilter].doFilter(r)) {
            String dateString = "";
            if (r.hasUtc()) {
                dateString = t.getYear() + "-"
                        + (t.getMonth() < 10 ? "0" : "") + t.getMonth() + "-"
                        + (t.getDay() < 10 ? "0" : "") + t.getDay();
                if (r.getUtc() > lastTime) {
                    lastTime = r.getUtc();
                    if (fileStartTime == 0) {
                        fileStartTime = lastTime;
                    }
                }
            }

            if (isWayType
                    || (isTrackType && (isIncludeTrkName || isTrkComment))) {
                if (isDateFolder) {
                    if (!dateString.equals(currentWayFolderDate)) {
                        rec.append("</Folder>");
                        isDateFolder = false;
                    }
                }
                if (!isDateFolder) {
                    rec.append("<Folder><name>");
                    currentWayFolderDate = dateString;
                    rec.append(dateString);
                    rec.append("</name>\r\n");
                    isDateFolder = true;
                }
                rec.append("<Placemark>\r\n");
                if (!isTrackType || isIncludeTrkName) {
                    rec.append("<name>");
                    if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
                        rec.append("TIME: " + (t.getHour() < 10 ? "0" : "")
                                + t.getHour() + ":"
                                + (t.getMinute() < 10 ? "0" : "")
                                + t.getMinute() + ":"
                                + (t.getSecond() < 10 ? "0" : "")
                                + t.getSecond());
                    } else {
                        if (r.hasRecCount()) {
                            rec.append("IDX: ");
                            rec.append(r.getRecCount());
                        }
                    }
                    rec.append("</name>\r\n");
                }
                if (isTrackType) {
                    rec.append("<visibility>0</visibility>\r\n");
                }

                if (((isTrackType && isTrkComment) || ( isWayType && isWayComment) ) ) {
                    rec.append("<description>");
                    rec.append("<![CDATA[");
                    // Lowercase is needed in GE.
                    // TODO: Maybe make lowercase option optional.
                    CommonOut.getHtml(rec, r, selectedFileFields, t,
                            recordNbrInLogs, imperial, true);
                    rec.append("]]>");
                    rec.append("</description>");
                }

                if (((r.hasUtc()) && (selectedFileFields.hasUtc()))) {
                    rec.append("<TimeStamp><when>");
                    rec.append(CommonOut.getDateTimeISO8601(t, r
                            .hasMillisecond() ? r.getMilisecond() : -1));
                    rec.append("</when></TimeStamp>\r\n");
                }

                if ((r.hasRcr())
                // && (selectedFileFields.hasRcr())
                ) {
                    rec.append("<styleUrl>");
                    String style = CommonOut.getRCRstr(r);

                    if ((style.length() > 1)
                            && ((r.rcr & BT747Constants.RCR_ALL_APP_MASK) == 0)) {
                        style = "M";
                    }
                    rec.append("#Style");
                    rec.append(style);
                    rec.append("</styleUrl>\r\n");
                }

                if (r.hasPosition() && selectedFileFields.hasPosition()) {
                    rec.append("<Point>\r\n");
                    rec.append("<coordinates>");
                    rec.append(JavaLibBridge.toString(r.getLongitude(), posDigits));
                    rec.append(",");
                    rec.append(JavaLibBridge.toString(r.getLatitude(), posDigits));
                    if ((r.hasHeight()) && (selectedFileFields.hasHeight())) {
                        rec.append(",");
                        rec.append(JavaLibBridge.toString(r.getHeight(), heightDigits));
                    }
                    rec.append("</coordinates>");
                    rec.append("</Point>\r\n");
                }

                rec.append("</Placemark>\r\n");
                writeTxt(rec.toString());
                rec.setLength(0);
            } else if (isPathType) {
                rec.setLength(0);
                if (r.hasPosition() && selectedFileFields.hasPosition()) {
                    boolean isChangeLineString = false;

                    if (needsToSplitTrack) {
                        endTrack();
                    }
                    if (!trackStarted) {
                        String name = "";
                        if (r.hasUtc()) {
                            name = CommonOut.getDateTimeStr(r.getUtc());
                        }
                        startTrack(name);
                    }
                    if (((r.hasHeight()) && (selectedFileFields.hasHeight())) != (altitudeMode == 0)) {
                        // Must change altitude mode because height
                        // availability changed.
                        isChangeLineString = true;
                    }

                    if (isLineString && (isChangeLineString)) {
                        rec.append("</coordinates>");
                        rec.append("</LineString>");
                        isLineString = false;
                    }
                    if (!isLineString) {
                        isLineString = true;
                        rec.append("<LineString>\r\n"
                                + "    <extrude>1</extrude>\r\n"
                                + "    <tessellate>1</tessellate>\r\n"
                                + "    <altitudeMode>");
                        if ((r.hasHeight())
                                && (selectedFileFields.hasHeight())) {
                            altitudeMode = 0;
                            // clampToGround, relativeToGround, absolute
                            rec.append(altitudeModeIfHeight);
                        } else {
                            rec.append(GPSKMLFile.CLAMPED_HEIGHT);
                            altitudeMode = 1;
                        }
                        rec.append("</altitudeMode><coordinates>\r\n");
                    }
                    rec.append("        ");
                    rec.append(JavaLibBridge.toString(r.getLongitude(), posDigits));
                    rec.append(",");
                    rec.append(JavaLibBridge.toString(r.getLatitude(), posDigits));
                    if ((r.hasHeight()) && (selectedFileFields.hasHeight())) {
                        rec.append(",");
                        rec.append(JavaLibBridge.toString(r.getHeight(), heightDigits));
                    }
                    rec.append("\r\n");
                    writeTxt(rec.toString());
                    rec.setLength(0);
                }
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#createFile(int, java.lang.String, boolean)
     * 
     * @Override
     */
    protected int createFile(final int utc, final String extra_ext,
            final boolean createNewFile) {
        /*
         * This is called every time a file is opened or reopened. Recover the
         * extension for the trackname label in the KML file.
         */
        trackName = extra_ext;
        return super.createFile(utc, extra_ext, createNewFile);
    }

    /**
     * Added to be able to set the track name in case the createFile method is
     * overridden.
     * 
     * @param extra_ext
     */
    public void setTrackName(final String extra_ext) {
        trackName = extra_ext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#FinaliseFile()
     */
    public void finaliseFile() {
        if (isOpen()) {
            writeDataFooter();
            rec.setLength(0);
            if (fileStartTime != 0) {
                //addLookAt(rec, fileStartTime, lastTime);
            }
            rec.append("</Document>\r\n</kml>");
            writeTxt(rec.toString());
        }
        super.finaliseFile();
    }

}
