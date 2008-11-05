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
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package gps.log.out;

import gps.BT747Constants;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.Version;
import bt747.sys.Convert;

/**
 * Class to write a KML file.
 * 
 * @author Mario De Weerd
 * @author Herbert Geus (Waypoint code&Track code)
 */
public class GPSKMLFile extends GPSFile {
    private final StringBuffer rec = new StringBuffer(1024); // reused
                                                                // stringbuffer

    private boolean isWayType;
    private boolean isTrackType;
    private boolean isPathType;
    private int currentFilter;
    private String trackName;
    private int altitudeMode = 0; // 0 = altitude.

    /**
     * 
     */
    public GPSKMLFile() {
        super();
        numberOfPasses = 3; // Three passes are needed.
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext,
            final int ard, final int oneFilePerDay) {
        super.initialiseFile(basename, ext, ard, oneFilePerDay);
        currentFilter = GPSFilter.WAYPT;
        isWayType = true;
        isTrackType = false;
        isPathType = false;
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
                isWayType = false;
                isPathType = true;
            } else if (isPathType) {
                isTrackType = true;
                isPathType = false;
            } else if (isTrackType) {
                isTrackType = false;
            }
            currentFilter = GPSFilter.TRKPT;
            // if(!m_multipleFiles) {
            // writeDataHeader();
            // }
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeFileHeader(java.lang.String)
     */
    public final void writeFileHeader(final String name) {
        StringBuffer header = new StringBuffer(2048);
        trackName = name;
        header.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                // + "<kml xmlns=\"http://schemas.opengis.net/kml/2.2.0\""
                // opengis was not understood by google maps
                + "<kml xmlns=\"http://earth.google.com/kml/2.2\"" + " >\r\n"
                + "<Document"
                + " xmlns:atom=\"http://www.w3.org/2005/Atom\" >\r\n"
                + "<atom:generator uri=\"http://sf.net/projects/bt747\" "
                + "version=\"" + Version.VERSION_NUMBER + "\"" + ">" + "BT747"
                + "</atom:generator>\r\n" + "<name>" + name + "</name>\r\n"
                + "  <open>1</open>\r\n");

        WayPointStyleSet iter;
        iter = CommonOut.wayPointStyles.iterator();
        while (iter.hasNext()) {
            WayPointStyle style = iter.next();
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

        iter = CommonOut.wayPointStyles.iterator();
        while (iter.hasNext()) {
            WayPointStyle style = iter.next();
            String x = "";
            if (style.getKey().length() > 1) {
                x = "X";
            }
            header.append("  <StyleMap id=\"Style" + x + style.getKey()
                    + "\">\r\n" + "    <Pair>\r\n"
                    + "      <key>normal</key>\r\n" + "      <styleUrl>#Style"
                    + style.getKey() + "0</styleUrl>\r\n" + "    </Pair>\r\n"
                    + "    <Pair>\r\n" + "      <key>highlight</key>\r\n"
                    + "      <styleUrl>#Style" + style.getKey()
                    + "1</styleUrl>\r\n" + "    </Pair>\r\n"
                    + "  </StyleMap>\r\n");
        }

        writeTxt(header.toString());
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
        } else if (isTrackType) {
            header = "  <Folder>\r\n"// " <name>My Tracks</name>\r\n"+
                    + "    <name>My Trackpoints</name>\r\n"
                    + "    <Folder>\r\n"// " <name>Track-"+m_name+"</name>\r\n"+
                    + "    <name>Trackpoints-" + trackName
                    + "</name>\r\n"
                    + "      <open>0</open>\r\n" + "\r\n";
        } else {
            String color;
            if (goodTrackColor.length() == 6) {
                color = goodTrackColor.substring(4)
                        + goodTrackColor.substring(2, 4)
                        + goodTrackColor.substring(0, 2);
            } else {
                color = "FFFFFF";
            }
            header = "  <Folder>\r\n" + "  <name>My Tracks</name>\r\n"
                    + "  <open>0</open>\r\n"
                    + "    <name>Track-" + trackName + "</name>\r\n"
                    + "<Placemark>\r\n" + "    <Style>\r\n"
                    + "      <LineStyle>\r\n" + "        <color>ff" + color
                    + "</color>\r\n" + "        <width>3.0</width>\r\n"
                    + "      </LineStyle>\r\n" + "    </Style>\r\n"
                    + "    <LineString>\r\n" + "    <extrude>1</extrude>\r\n"
                    + "    <tessellate>1</tessellate>\r\n"
                    + "    <altitudeMode>absolute</altitudeMode>\r\n"
                    + "    <coordinates>\r\n";
        }
        writeTxt(header);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeDataFooter()
     */
    protected final void writeDataFooter() {
        String footer;
        if (isWayType) {
            footer = "  </Folder>\r\n" + "\r\n";
        } else if (isTrackType) {
            footer = "    </Folder>\r\n" + "  </Folder>\r\n" + "\r\n";
        } else {
            footer = "      </coordinates>\r\n" + "     </LineString>\r\n"
                    + "    </Placemark>\r\n"
                    + "  </Folder>\r\n";
        }
        writeTxt(footer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord s) {
        super.writeRecord(s);

        if (activeFields != null) {
            rec.setLength(0);
            if (ptFilters[currentFilter].doFilter(s)) {
                if (isWayType
                        || (isTrackType && (isIncludeTrkName || isTrkComment))) {
                    rec.append("<Placemark>\r\n");
                    if (!isTrackType || isIncludeTrkName) {
                        rec.append("<name>");
                        if ((activeFields.utc != 0)
                                && (selectedFileFields.utc != 0)) {
                            rec.append("TIME: " + (t.getHour() < 10 ? "0" : "")
                                    + Convert.toString(t.getHour()) + ":"
                                    + (t.getMinute() < 10 ? "0" : "")
                                    + Convert.toString(t.getMinute()) + ":"
                                    + (t.getSecond() < 10 ? "0" : "")
                                    + Convert.toString(t.getSecond()));
                        } else {
                            rec.append("IDX: ");
                            rec.append(Convert.toString(s.recCount));
                        }
                        rec.append("</name>\r\n");
                    }
                    if (isTrackType) {
                        rec.append("<visibility>0</visibility>\r\n");
                    }

                    if (isWayType || isTrkComment) {
                        rec.append("<description>");
                        rec.append("<![CDATA[");
                        CommonOut.getHtml(rec, s, activeFields,
                                selectedFileFields, t, recordNbrInLogs,
                                imperial);
                        rec.append("]]>");
                        rec.append("</description>");
                    }

                    if (((activeFields.utc != 0) && (selectedFileFields.utc != 0))) {
                        rec.append("<TimeStamp><when>");
                        if ((activeFields.utc != 0)
                                && (selectedFileFields.utc != 0)) {
                            rec.append(Convert.toString(t.getYear()) + "-"
                                    + (t.getMonth() < 10 ? "0" : "")
                                    + Convert.toString(t.getMonth()) + "-"
                                    + (t.getDay() < 10 ? "0" : "")
                                    + Convert.toString(t.getDay()) + "T"
                                    + (t.getHour() < 10 ? "0" : "")
                                    + Convert.toString(t.getHour()) + ":"
                                    + (t.getMinute() < 10 ? "0" : "")
                                    + Convert.toString(t.getMinute()) + ":"
                                    + (t.getSecond() < 10 ? "0" : ""));
                            if (activeFields.milisecond == 0) {
                                rec.append(Convert.toString(t.getSecond()));
                            } else {
                                rec.append(Convert.toString((float) t
                                        .getSecond()
                                        + s.milisecond / 1000.0, 3));
                            }
                            rec.append("Z");
                        }
                        rec.append("</when></TimeStamp>\r\n");
                    }

                    rec.append("<styleUrl>");
                    if ((activeFields.rcr != 0)
                            && (selectedFileFields.rcr != 0)) {
                        String style = CommonOut.getRCRstr(s);

                        if (style.length() > 1
                                && ((s.rcr & BT747Constants.RCR_ALL_APP_MASK) == 0)) {
                            style = "M";
                        }
                        rec.append("#Style");
                        rec.append(style);
                    }
                    rec.append("</styleUrl>\r\n");

                    if (((activeFields.longitude != 0) && (selectedFileFields.longitude != 0))
                            && ((activeFields.latitude != 0) && (selectedFileFields.latitude != 0))) {
                        rec.append("<Point>\r\n");
                        rec.append("<coordinates>");
                        rec.append(Convert.toString(s.longitude, 6));
                        rec.append(",");
                        rec.append(Convert.toString(s.latitude, 6));
                        if ((activeFields.height != 0)
                                && (selectedFileFields.height != 0)) {
                            rec.append(",");
                            rec.append(Convert.toString(s.height, 3));
                        }
                        rec.append("</coordinates>");
                        rec.append("</Point>\r\n");
                    }

                    rec.append("</Placemark>\r\n");
                    writeTxt(rec.toString());
                } else if (isPathType) {
                    rec.setLength(0);
                    if ((activeFields.longitude != 0)
                            && (selectedFileFields.longitude != 0)
                            && (activeFields.latitude != 0)
                            && (selectedFileFields.latitude != 0)) {
                        if (((activeFields.height != 0) && (selectedFileFields.height != 0)) != (altitudeMode == 0)) {
                            rec.append("</coordinates>");
                            rec.append("    </LineString><LineString>\r\n"
                                    + "    <extrude>1</extrude>\r\n"
                                    + "    <tessellate>1</tessellate>\r\n"
                                    + "    <altitudeMode>");
                            if ((activeFields.height != 0)
                                    && (selectedFileFields.height != 0)) {
                                altitudeMode = 0;
                                // clampToGround, relativeToGround, absolute
                                rec.append("absolute");
                            } else {
                                rec.append("clampToGround");
                                altitudeMode = 1;
                            }
                            rec.append("</altitudeMode><coordinates>\r\n");
                        }
                        rec.append("        ");
                        rec.append(Convert.toString(s.longitude, 6));
                        rec.append(",");
                        rec.append(Convert.toString(s.latitude, 6));
                        if ((activeFields.height != 0)
                                && (selectedFileFields.height != 0)) {
                            rec.append(",");
                            rec.append(Convert.toString(s.height, 3));
                        }
                        rec.append("\r\n");
                        writeTxt(rec.toString());
                    }
                }

            }
        } // activeFields!=null
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#FinaliseFile()
     */
    public void finaliseFile() {
        if (this.isOpen()) {
            String footer;
            writeDataFooter();
            footer = "</Document>\r\n" + "</kml>";
            writeTxt(footer);
        }
        super.finaliseFile();
    }

}
