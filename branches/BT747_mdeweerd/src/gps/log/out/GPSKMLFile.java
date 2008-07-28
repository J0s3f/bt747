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
    private StringBuffer rec = new StringBuffer(1024); // reused stringbuffer

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
        numberOfPasses = 3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext,
            final int ard, int oneFilePerDay) {
        super.initialiseFile(basename, ext, ard, oneFilePerDay);
        currentFilter = GPSFilter.WAYPT;
        isWayType = true;
        isTrackType = false;
        isPathType = false;
    }

    public boolean nextPass() {
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
                isTrackType = true;
            } else if (isTrackType) {
                isTrackType = false;
                isPathType = true;
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

    public void writeFileHeader(final String name) {
        String header;
        trackName = name;
        header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\""
                + " xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n"
                + "<Document>\r\n"
                + "<atom:generator uri=\"http://sf.net/projects/bt747\" "
                + "version=\""
                + Version.VERSION_NUMBER
                + "\""
                + ">"
                + "BT747"
                + "</atom:generator>\r\n"
                + "<name>"
                + name
                + "</name>\r\n"
                + "  <open>1</open>\r\n"
                + "  <Style id=\"TimeStamp0\">\r\n"
                + "    <IconStyle>\r\n"
                + "      <Icon>\r\n"
                + "        <href>root://icons/palette-3.png</href>\r\n"
                + "      </Icon>\r\n"
                + "    </IconStyle>\r\n"
                + "    <LabelStyle>\r\n"
                + "      <scale>0</scale>\r\n"
                + "    </LabelStyle>\r\n"
                + "  </Style>\r\n"
                + "  <Style id=\"TimeStamp1\">\r\n"
                + "    <IconStyle>\r\n"
                + "      <Icon>\r\n"
                + "        <href>root://icons/palette-3.png</href>\r\n"
                + "      </Icon>\r\n"
                + "    </IconStyle>\r\n"
                + "    <LabelStyle>\r\n"
                + "      <scale>1</scale>\r\n"
                + "    </LabelStyle>\r\n"
                + "  </Style>\r\n"
                + "  <StyleMap id=\"StyleT\">\r\n"
                + "    <Pair>\r\n"
                + "      <key>normal</key>\r\n"
                + "      <styleUrl>#TimeStamp0</styleUrl>\r\n"
                + "    </Pair>\r\n"
                + "    <Pair>\r\n"
                + "      <key>highlight</key>\r\n"
                + "      <styleUrl>#TimeStamp1</styleUrl>\r\n"
                + "    </Pair>\r\n"
                + "  </StyleMap>\r\n"
                + "    <Style id=\"DistanceStamp0\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-2.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>0</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <Style id=\"DistanceStamp1\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-2.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>1</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <StyleMap id=\"StyleD\">\r\n"
                + "      <Pair>\r\n"
                + "        <key>normal</key>\r\n"
                + "        <styleUrl>#DistanceStamp0</styleUrl>\r\n"
                + "      </Pair>\r\n"
                + "      <Pair>\r\n"
                + "        <key>highlight</key>\r\n"
                + "        <styleUrl>#DistanceStamp1</styleUrl>\r\n"
                + "      </Pair>\r\n"
                + "    </StyleMap>\r\n"
                + "    <Style id=\"SpeedStamp0\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-4.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>0</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <Style id=\"SpeedStamp1\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-4.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>1</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <StyleMap id=\"StyleS\">\r\n"
                + "      <Pair>\r\n"
                + "        <key>normal</key>\r\n"
                + "        <styleUrl>#SpeedStamp0</styleUrl>\r\n"
                + "      </Pair>\r\n"
                + "      <Pair>\r\n"
                + "        <key>highlight</key>\r\n"
                + "        <styleUrl>#SpeedStamp1</styleUrl>\r\n"
                + "      </Pair>\r\n"
                + "    </StyleMap>\r\n"
                + "    <Style id=\"Unknown0\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-4.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>0</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <Style id=\"Unknown1\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-4.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>1</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <StyleMap id=\"StyleB\">\r\n"
                + "      <Pair>\r\n"
                + "        <key>normal</key>\r\n"
                + "        <styleUrl>#Unknown0</styleUrl>\r\n"
                + "      </Pair>\r\n"
                + "      <Pair>\r\n"
                + "        <key>highlight</key>\r\n"
                + "        <styleUrl>#Unknown1</styleUrl>\r\n"
                + "      </Pair>\r\n"
                + "    </StyleMap>\r\n"
                + "    <Style id=\"MixStamp0\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-4.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>0</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <Style id=\"MixStamp1\">\r\n"
                + "      <IconStyle>\r\n"
                + "        <Icon>\r\n"
                + "          <href>root://icons/palette-4.png</href>\r\n"
                + "        </Icon>\r\n"
                + "      </IconStyle>\r\n"
                + "      <LabelStyle>\r\n"
                + "        <scale>1</scale>\r\n"
                + "      </LabelStyle>\r\n"
                + "    </Style>\r\n"
                + "    <StyleMap id=\"StyleM\">\r\n"
                + "      <Pair>\r\n"
                + "        <key>normal</key>\r\n"
                + "        <styleUrl>#MixStamp0</styleUrl>\r\n"
                + "      </Pair>\r\n"
                + "      <Pair>\r\n"
                + "        <key>highlight</key>\r\n"
                + "        <styleUrl>#MixStamp1</styleUrl>\r\n"
                + "      </Pair>\r\n" + "    </StyleMap>\r\n";
        writeTxt(header);
    }

    protected void writeDataHeader() {
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
            header = "  <Folder>\r\n" + "  <name>My Tracks</name>\r\n"
                    + "  <open>0</open>\r\n" + "  <Placemark>\r\n"
                    + "    <name>Track-" + trackName + "</name>\r\n"
                    + "    <Style>\r\n" + "      <LineStyle>\r\n"
                    + "        <color>ffff0000</color>\r\n"
                    + "        <width>3.0</width>\r\n"
                    + "      </LineStyle>\r\n" + "    </Style>\r\n"
                    + "    <LineString>\r\n"
                    + "    <extrude>1</extrude>\r\n"
                    + "    <tessellate>1</tessellate>\r\n"
                    + "    <altitudeMode>absolute</altitudeMode>\r\n"
                    + "    <coordinates>\r\n";
        }
        writeTxt(header);
    }

    protected void writeDataFooter() {
        String footer;
        if (isWayType) {
            footer = "  </Folder>\r\n" + "\r\n";
        } else if (isTrackType) {
            footer = "    </Folder>\r\n" + "  </Folder>\r\n" + "\r\n";
        } else {
            footer = "      </coordinates>\r\n" + "     </LineString>\r\n"
                    + "    </Placemark>\r\n" + "  </Folder>\r\n";
        }
        writeTxt(footer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(final GPSRecord s) {
        super.writeRecord(s);

        if (activeFields != null) {
            rec.setLength(0);
            if (ptFilters[currentFilter].doFilter(s)) {
                if (isWayType || isTrackType) {
                    rec.append("<Placemark>\r\n");
                    rec.append("<name>");
                    if (activeFields.utc != 0) {
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

                    if (isTrackType) {
                        rec.append("<visibility>0</visibility>\r\n");
                    }

                    rec.append("<description>");
                    rec.append("<![CDATA[");
                    CommonOut.getHtml(rec, s, activeFields, selectedFileFields,
                            t, recordNbrInLogs, imperial);
                    rec.append("]]>");
                    rec.append("</description>");

                    if ((activeFields.utc != 0)) {
                        rec.append("<TimeStamp><when>");
                        if (activeFields.utc != 0) {
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
                    if (activeFields.rcr != 0) {
                        String style = getRCRstr(s);

                        if ((s.rcr & BT747Constants.RCR_TIME_MASK) != 0) {
                            style += "T";
                        }
                        if ((s.rcr & BT747Constants.RCR_SPEED_MASK) != 0) {
                            style += "S";
                        }
                        if ((s.rcr & BT747Constants.RCR_DISTANCE_MASK) != 0) {
                            style += "D";
                        }
                        if ((s.rcr & BT747Constants.RCR_BUTTON_MASK) != 0) {
                            style += "B";
                        }
                        if (style.length() > 1
                                || ((s.rcr & BT747Constants.RCR_ALL_APP_MASK) != 0)) {
                            style = "M";
                        }
                        rec.append("#Style");
                        rec.append(style);
                    }
                    rec.append("</styleUrl>\r\n");

                    if ((activeFields.longitude != 0)
                            && (activeFields.latitude != 0)) {
                        rec.append("<Point>\r\n");
                        rec.append("<coordinates>");
                        rec.append(Convert.toString(s.longitude, 6));
                        rec.append(",");
                        rec.append(Convert.toString(s.latitude, 6));
                        if (activeFields.height != 0) {
                            rec.append(",");
                            rec.append(Convert.toString(s.height, 3));
                        }
                        rec.append("</coordinates>");
                        rec.append("</Point>\r\n");
                    }

                    rec.append("</Placemark>\r\n");

                    rec.append("\r\n");
                    writeTxt(rec.toString());
                } else if (isPathType) {
                    rec.setLength(0);
                    if (activeFields.longitude != 0
                            && activeFields.latitude != 0) {
                        if ((activeFields.height != 0) != (altitudeMode == 0)) {
                            rec.append("</coordinates>");
                            rec.append("    </LineString><LineString>\r\n"
                                    + "    <extrude>1</extrude>\r\n"
                                    + "    <tessellate>1</tessellate>\r\n"
                                    + "    <altitudeMode>");
                            if (activeFields.height != 0) {
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
                        if (activeFields.height != 0) {
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
