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
// Thanks to Marcus Schmidke for modifications to the output format
// making it compatible with MapSource that requires specific field
// ordering.<br>This was undone, but implemented differently
// later by using XML validation.
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.Version;
import bt747.sys.Convert;

/**
 * Class to write a GPX file.
 * 
 * @author Mario De Weerd
 */
public final class GPSGPXFile extends GPSFile {
    private final StringBuffer rec = new StringBuffer(1024); // reused
    // stringbuffer

    private boolean isWayType;
    private boolean isNewTrack = true;
    private int currentFilter;
    private String trackName = "";
    private boolean isTrkSegSplitOnlyWhenSmall = false;

    /**
     * 
     */
    public GPSGPXFile() {
        super();
        numberOfPasses = 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public final void initialiseFile(final String basename, final String ext,
            final int Card, final int oneFilePerDay) {
        super.initialiseFile(basename, ext, Card, oneFilePerDay);
        currentFilter = GPSFilter.WAYPT;
        isWayType = true;
    }

    public final boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
            // if(m_multipleFiles) {
            // closeFile();
            // }
            nbrOfPassesToGo--;
            previousDate = 0;
            isWayType = false;
            currentFilter = GPSFilter.TRKPT;
            // if(!m_multipleFiles) {
            // writeDataHeader();
            // }
            return true;
        } else {
            return false;
        }
    }

    protected final void writeFileHeader(final String Name) {
        String header;
        header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\r\n"
                + "<gpx xmlns=\"http://www.topografix.com/GPX/1/0\"\r\n"
                + " creator=\"BT747 V"
                + Version.VERSION_NUMBER
                + "\""
                + " version=\"1.0\"\r\n" // GPX version
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0"
                + " http://www.topografix.com/GPX/1/0/gpx.xsd\">\r\n"
                // MS20080327 Filename element isn't understood by MapSource?!
                + "<name>" + Name + "</name>";
        writeTxt(header);
    }

    private boolean isDataHeaderWritten = false;

    private final void writeActualDataHeader() {
        if (isWayType) {
        } else {
            String header;
            header = "<trk>";
            if (isIncludeTrkName) {
                header += "<name>" + trackName + "</name>";
            }
            header += "<trkseg>" + "\r\n";

            isDataHeaderWritten = true;
            writeTxt(header);
        }
    }

    protected final void writeDataHeader() {
        if (isWayType) {
        } else {
            isNewTrack = true;
        }
    }

    protected final void writeDataFooter() {
        String header;
        if (isWayType) {
        } else {
            if (isDataHeaderWritten) {
                header = "</trkseg>" + "</trk>" + "\r\n";
                writeTxt(header);
                isDataHeaderWritten = false;
            }
        }
    }

    protected final void writeTrkSegSplit() {
        String header;
        if (isWayType) {
        } else {
            if (isDataHeaderWritten) {
                header = "</trkseg><trkseg>";
                writeTxt(header);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    private static final char[] zeros = "0000000".toCharArray();

    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        if (activeFields != null) {

            if (!ptFilters[currentFilter].doFilter(r)) {
                // The track is interrupted by a removed log item.
                // Break the track in the output file
                if (!isWayType && !isNewTrack && !firstRecord
                        && !ignoreBadPoints) {
                    if (isTrkSegSplitOnlyWhenSmall
                            && ((activeFields.utc == 0) || (previousTime
                                    + trackSepTime > r.utc))) {
                        writeTrkSegSplit();
                    } else {
                        writeDataFooter();
                        writeDataHeader();
                    }
                }
            } else {
                // This log item is to be transcribed in the output file.

                String timeStr = ""; // String that will represent time
                String fixStr = ""; // String that will represent fix type
                String rcrStr = ""; // String that will represent log reason
                String hdopStr = ""; // String that will represent HDOP
                String nsatStr = ""; // String that will represent number of
                // sats

                if ((activeFields.hasValid())
                        && (selectedFileFields.hasValid())) {
                    switch (r.valid) {
                    case 0x0001:
                        fixStr += "none"; // "No fix";
                        break;
                    case 0x0002:
                        fixStr += "3d"; // "SPS";
                        break;
                    case 0x0004:
                        fixStr += "dgps";
                        break;
                    case 0x0008:
                        fixStr += "pps"; // Military signal
                        break;
                    case 0x0010:
                        // tmp+="RTK";
                        break;
                    case 0x0020:
                        // tmp+="FRTK";
                        break;
                    case 0x0040:
                        // tmp+= "Estimated mode";
                        break;
                    case 0x0080:
                        // tmp+= "Manual input mode";
                        break;
                    case 0x0100:
                        // tmp+= "Simulator mode";
                        break;
                    default:
                        // tmp+="Unknown mode";
                    }
                }
                if ((activeFields.hasRcr()) && (selectedFileFields.hasRcr())) {
                    // For ways = track type
                    rec.append("<type>");
                    rcrStr = CommonOut.getRCRstr(r);
                    rec.append("</type>\r\n");
                }

                if ((activeFields.hasHdop()) && (selectedFileFields.hasHdop())) {
                    hdopStr = Convert.toString(r.hdop / 100.0, 2);
                }
                if ((activeFields.hasNsat()) && (selectedFileFields.hasNsat())) {
                    nsatStr += (r.nsat / 256);
                }

                // StringBuffer rec=new StringBuffer(1024);
                rec.setLength(0);
                if ((activeFields.hasUtc()) && (selectedFileFields.hasUtc())) {
                    timeStr += t.getYear() + "-"
                            + (t.getMonth() < 10 ? "0" : "")
                            + t.getMonth() + "-"
                            + (t.getDay() < 10 ? "0" : "")
                            + t.getDay() + "T"
                            + (t.getHour() < 10 ? "0" : "")
                            + t.getHour() + ":"
                            + (t.getMinute() < 10 ? "0" : "")
                            + t.getMinute() + ":"
                            + (t.getSecond() < 10 ? "0" : "")
                            + t.getSecond();
                    if ((activeFields.hasMilisecond())
                            && (selectedFileFields.hasMilisecond())) {
                        timeStr += ".";
                        timeStr += (r.milisecond < 100) ? "0" : "";
                        timeStr += (r.milisecond < 10) ? "0" : "";
                        timeStr += r.milisecond;
                    }
                    timeStr += "Z";
                }
                if (isNewTrack) {
                    StringBuffer tx = new StringBuffer();
                    String tmp = "" + r.recCount;
                    int nZeros = 5 - tmp.length();
                    if (nZeros < 0) {
                        nZeros = 0;
                    }
                    tx.append(zeros, 0, nZeros);
                    trackName = "#" + tx.toString()
                            + r.recCount + "#";
                    if ((activeFields.hasUtc())
                            && (selectedFileFields.hasUtc())) {
                        trackName += " " + timeStr;
                    }

                    writeActualDataHeader();
                }
                // " <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
                // " <ele>12.863281</ele>"+
                // " <time>2005-05-16T11:49:06Z</time>"+
                // " <name>Cala Sant Vicen - Mallorca</name>"+
                // " <sym>City</sym>"+
                // " </wpt>"+

                // lat="latitudeType [1] ?"
                // lon="longitudeType [1] ?">
                if (isWayType) {
                    rec.append("<wpt ");
                } else {
                    rec.append("<trkpt ");
                }
                if ((activeFields.hasLatitude())
                        && (selectedFileFields.hasLatitude())) {
                    rec.append("lat=\"");
                    rec.append(Convert.toString(r.latitude, 8));
                    rec.append("\" ");
                }
                if ((activeFields.hasLongitude())
                        && (selectedFileFields.hasLongitude())) {
                    rec.append("lon=\"");
                    rec.append(Convert.toString(r.longitude, 8));
                    rec.append("\"");
                }
                rec.append(" >\r\n");
                //
                // if(m_isWayType) {
                // rec.append("<name>"+Convert.toString(m_recCount)+"</name>\r\n");
                // }
                // <ele> xsd:decimal </ele> [0..1] ? (elevation in meters)

                if ((activeFields.hasHeight())
                        && (selectedFileFields.hasHeight())) {
                    rec.append("<ele>");
                    rec.append(Convert.toString(r.height, 3));
                    rec.append("</ele>\r\n");
                }

                // <time> xsd:dateTime </time> [0..1] ? //2005-05-16T11:49:06Z
                if ((activeFields.hasUtc()) && (selectedFileFields.hasUtc())) {
                    rec.append("<time>");
                    rec.append(timeStr);
                    rec.append("</time>\r\n");
                }

                if ((activeFields.hasHeading())
                        && (selectedFileFields.hasHeading())) {
                    rec.append("<course>");
                    rec.append(Convert.toString(r.heading));
                    rec.append("</course>\r\n");
                }

                if ((activeFields.hasSpeed())
                        && (selectedFileFields.hasSpeed())) {
                    rec.append("<speed>");
                    rec.append(Convert.toString(r.speed / 3.6f, 4)); // must
                    // be
                    // meters/second
                    rec.append("</speed>\r\n");
                }

                // <magvar> degreesType </magvar> [0..1] ?
                // <geoidheight> xsd:decimal </geoidheight> [0..1] ?

                // <name> xsd:string </name> [0..1] ?
                if (isWayType || isIncludeTrkName) {
                    rec.append("<name>");
                    if (isWayType) {
                        rec.append("wpt-");
                    } else {
                        rec.append("trkpt-");
                    }
                    if ((activeFields.hasUtc())
                            && (selectedFileFields.hasUtc())) {
                        rec.append(timeStr);
                        if (isNewTrack) {
                            trackName += " " + timeStr;
                        }
                    } else {
                        rec.append(r.recCount);
                    }
                    rec.append("</name>\r\n");
                }
                // <cmt> xsd:string </cmt> [0..1] ?
                // No comments, so commented out.
                if (isTrkComment
                        && (recordNbrInLogs || fixStr.length() != 0
                                || rcrStr.length() != 0
                                || hdopStr.length() != 0 || nsatStr.length() != 0)) {
                    rec.append("<cmt>");
                    rec.append("<![CDATA[");
                    CommonOut.getHtml(rec, r, activeFields, selectedFileFields,
                            t, recordNbrInLogs, imperial);
                    rec.append("]]>");
                    rec.append("</cmt>\r\n");
                }

                // <desc> xsd:string </desc> [0..1] ?
                // A text description of the element. Holds additional
                // information about the element intended for the user, not the
                // GPS.

                // <src> xsd:string </src> [0..1] ?
                // Source of data. Included to give user some idea of
                // reliability and accuracy of data. "Garmin eTrex", "USGS quad
                // Boston North", e.g

                // <sym> xsd:string </sym> [0..1] ?
                // Text of GPS symbol name. For interchange with other programs,
                // use the exact spelling of the symbol as displayed on the GPS.
                // If the GPS abbreviates words, spell them out.

                // <type> xsd:string </type> [0..1] ?
                // Type (classification) of route. (for tracks)
                // Type (classification) of waypoint. (for waypoints)
                
                if ((activeFields.hasRcr()) && (selectedFileFields.hasRcr())) {
                    rec.append("<sym>");
                    rec.append(CommonOut.getRcrSymbolText(r));
                    rec.append("</sym>\r\n");
                    rec.append("<type>");
                    rec.append(rcrStr);
                    rec.append("</type>\r\n");
                }

                // <fix> fixType </fix> [0..1] ?
                if (fixStr.length() != 0) {
                    rec.append("<fix>");
                    rec.append(fixStr);
                    rec.append("</fix>\r\n");
                }

                // <sat> xsd:nonNegativeInteger </sat> [0..1] ?
                if ((activeFields.hasNsat()) && (selectedFileFields.hasNsat())) {
                    rec.append("<sat>");
                    rec.append(nsatStr); // Sat used
                    rec.append("</sat>\r\n");
                    // nsatStr+="(";
                    // nsatStr+=Convert.toString(s.nsat%256);
                    // nsatStr+=")";
                }

                // <hdop> xsd:decimal </hdop> [0..1] ?
                if ((activeFields.hasHdop()) && (selectedFileFields.hasHdop())) {
                    rec.append("<hdop>");
                    rec.append(hdopStr);
                    rec.append("</hdop>\r\n");
                }
                // <vdop> xsd:decimal </vdop> [0..1] ?
                if ((activeFields.hasVdop()) && (selectedFileFields.hasVdop())) {
                    rec.append("<vdop>");
                    rec.append(Convert.toString(r.vdop / 100.0, 2));
                    rec.append("</vdop>\r\n");
                }
                // <pdop> xsd:decimal </pdop> [0..1] ?
                // // <pdop> xsd:decimal </pdop> [0..1] ?
                if ((activeFields.hasPdop()) && (selectedFileFields.hasPdop())) {
                    rec.append("<pdop>");
                    rec.append(Convert.toString(r.pdop / 100.0, 2));
                    rec.append("</pdop>\r\n");
                }

                // <ageofdgpsdata> xsd:decimal </ageofdgpsdata> [0..1] ?
                if ((activeFields.hasDage()) && (selectedFileFields.hasDage())) {
                    rec.append("<ageofdgpsdata>");
                    rec.append(r.dage);
                    rec.append("</ageofdgpsdata>\r\n");
                }

                // <dgpsid> dgpsStationType </dgpsid> [0..1] ?
                if ((activeFields.hasDsta()) && (selectedFileFields.hasDsta())) {
                    rec.append("<dgpsid>");
                    rec.append(r.dsta);
                    rec.append("</dgpsid>\r\n");
                }

                // <link> linkType </link> [0..*] ?

                if (false) {
                    // <extensions> extensionsType </extensions> [0..1] ?
                    if ((activeFields.hasDistance())
                            && (selectedFileFields.hasDistance())) {
                        rec.append("<extensions>");
                        // MS 20080327 seems to be unsupported in GPX 1.0:
                        // MS 20080327 don't know why but "speed" isn't
                        // understood
                        // by MapSource

                        if ((activeFields.hasDistance())
                                && (selectedFileFields.hasDistance())) {
                            rec.append("<distance>");
                            rec.append(Convert.toString(r.distance, 2)); // +"
                            // m\r\n"
                            rec.append("</distance>\r\n");
                        }
                        rec.append("</extensions>");
                    }
                }
                if (isWayType) {
                    rec.append("</wpt>\r\n");
                } else {
                    rec.append("</trkpt>\r\n");
                }
                String ss = rec.toString();
                if (ss.length() > 21) {
                    writeTxt(ss);
                }
                rec.setLength(0);

                isNewTrack = false;

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
            footer = "</gpx>";
            writeTxt(footer);
        }
        super.finaliseFile();

    }

    /**
     * @return Returns the m_TrkSegSplitOnlyWhenSmall.
     */
    public final boolean isTrkSegSplitOnlyWhenSmall() {
        return isTrkSegSplitOnlyWhenSmall;
    }

    /**
     * @param trkSegSplitOnlyWhenSmall
     *            The m_TrkSegSplitOnlyWhenSmall to set.
     */
    public final void setTrkSegSplitOnlyWhenSmall(
            final boolean trkSegSplitOnlyWhenSmall) {
        isTrkSegSplitOnlyWhenSmall = trkSegSplitOnlyWhenSmall;
    }
}
