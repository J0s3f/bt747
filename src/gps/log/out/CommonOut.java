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
import gps.convert.Conv;
import gps.log.GPSRecord;

import bt747.sys.I18N;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Time;

public final class CommonOut {
    public final static String getRCRKey(final String r) {
        if ((r.length() > 0) && (r.charAt(0) == 'X')) {
            return (r.substring(1));
        } else if (r.length() > 1) {
            return "M";
        } else {
            return r;
        }
    }

    public final static String getRcrSymbolText(final GPSRecord r) {
        final WayPointStyle w = CommonOut.wayPointStyles.get(CommonOut
                .getRCRKey(CommonOut.getRCRstr(r)));
        if (w != null) {
            return CommonOut.wayPointStyles.get(
                    CommonOut.getRCRKey(CommonOut.getRCRstr(r)))
                    .getSymbolText();
        } else {
            return null;
        }
    }

    public final static String getHtml(final GPSRecord s) {
        StringBuffer rec = new StringBuffer(200);
        CommonOut
                .getHtml(rec, s, s, s.getBT747Time(), true, false /* imperial */);
        final String r = rec.toString();
        rec = null;
        return r;
    }

    /**
     * Does not add http:// or file://, but only './' and '.wav'.
     * 
     * @param r
     * @return URL to reference in GPSRecord if one.
     */
    public final static String getLink(final GPSRecord r,
            final boolean isToLower) {
        final String vox = r.getVoxStr();
        String result;
        if (vox == null || vox.length() == 0 || vox.charAt(0) == '/'
                || vox.charAt(0) == '\\' || vox.charAt(0) == '.'
                || vox.indexOf(':') >= 0) {
            // This is a root path or already a URL
            result = vox;
            ;
        } else {
            result = "./" + vox;
            if (r.voxStr.startsWith("VOX")) {
                result += ".wav";
            }
        }
        if (isToLower) {
            result = result.toLowerCase();
        }
        return result;
    }

    public final static void getHtml(final StringBuffer rec,
            final GPSRecord s, final GPSRecord selectedFields,
            final BT747Time t, final boolean recordNbrInLogs,
            final boolean imperial) {
    	getHtml(rec, s, selectedFields, t, recordNbrInLogs, imperial, false);
    }
    
    public final static void getHtml(final StringBuffer rec,
            final GPSRecord s, final GPSRecord selectedFields,
            final BT747Time t, final boolean recordNbrInLogs,
            final boolean imperial, final boolean convertToLower) {

        // Need lowercase ;-(.
        // final boolean convertToLower = true;
        final String rcr = CommonOut.getRCRstr(s);
        WayPointStyle style = null;
        style = CommonOut.wayPointStyles.get(CommonOut.getRCRKey(rcr));
        rec.append("<table width=400>");
        if (s.voxStr != null) {
            rec.append("<tr><td colspan=2 align='center'>"); // Table row
            // and first
            // column
            // start
            // rec.append("</td><td>"); // Column split (span 2 so skipped)
            final String upperVox = s.voxStr.toUpperCase();
            final boolean isPicture = upperVox.endsWith(".JPG")
                    || upperVox.endsWith("PNG");
            rec.append("<br>");
            if (style != null) {
                rec.append(I18N.i18n(style.getSymbolText()));
                rec.append(':');
                if (isPicture) {
                    rec.append("<br>");
                }
            }
            String vox;
            if (convertToLower) {
                vox = s.voxStr.toLowerCase();
            } else {
                vox = s.voxStr;
            }
            rec.append("<a target='_new' href='");
            rec.append(getLink(s, convertToLower));
            rec.append("'>");
            if (isPicture) {
                rec.append("<img height=150 src='");

                rec.append(vox);

                rec.append("' >");
            } else {
                rec.append(I18N.i18n("Click here") + " (" + vox + ")");
            }
            rec.append("</a><br><br>");
            rec.append("</td></tr>"); // Column end and end row.
        }

        if (recordNbrInLogs && s.hasRecCount()) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("IDX"));
            rec.append(":</td><td>"); // Column split
            rec.append(s.getRecCount());
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasUtc()) && (selectedFields.hasUtc())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("TIME"));
            rec.append(":</td><td>"); // Column split
            rec.append(CommonOut.getDateTimeStr(t));
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasRcr()) && (selectedFields.hasRcr())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("RCR"));
            rec.append(":</td><td>"); // Column split
            rec.append(rcr);

            if (s.voxStr == null && style != null) {
                rec.append(" <b>(");
                rec.append(I18N.i18n(style.getSymbolText()));
                rec.append(")</b>");
            }
            rec.append("</td></tr>"); // Column end and end row.
        }
        // if(s.utc!=0) {
        // Time t=utcTime(s.utc);
        //                    
        // rec.append("<br>DATE: ");
        // rec.append(JavaLibBridge.toString(t.getYear())+"/"
        // +(
        // t.getMonth()<10?"0":"")+JavaLibBridge.toString(t.getMonth())+"/"
        // +(
        // t.getDay()<10?"0":"")+JavaLibBridge.toString(t.getDay())+"<br
        // />"
        // +"TIME: "
        // +(
        // t.getHour()<10?"0":"")+JavaLibBridge.toString(t.getHour())+":"
        // +(t.getMinute()<10?"0":"")+JavaLibBridge.toString(t.getMinute())+":"
        // +(t.getSecond()<10?"0":"")+JavaLibBridge.toString(t.getSecond())
        // );
        // }
        if ((s.hasValid()) && (selectedFields.hasValid())) {
            rec.append("<tr><td>"); // Table row and first column start
            // rec.append("<br>VALID: ");
            rec.append(I18N.i18n("VALID"));
            rec.append(":</td><td>"); // Column split
            rec.append(I18N.i18n(CommonOut.getFixText(s.getValid())));
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasLatitude()) && (selectedFields.hasLatitude())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("LATITUDE"));
            rec.append(":</td><td>"); // Column split
            if (s.getLatitude() >= 0) {
                rec.append(JavaLibBridge.toString(s.getLatitude(), 6));
                rec.append(" N");
            } else {
                rec.append(JavaLibBridge.toString(-s.getLatitude(), 6));
                rec.append(" S");
            }
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasLongitude()) && (selectedFields.hasLongitude())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("LONGITUDE"));
            rec.append(":</td><td>"); // Column split
            if (s.getLongitude() >= 0) {
                rec.append(JavaLibBridge.toString(s.getLongitude(), 6));
                rec.append(" E");
            } else {
                rec.append(JavaLibBridge.toString(-s.getLongitude(), 6));
                rec.append(" W");
            }
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasHeight()) && (selectedFields.hasHeight())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("HEIGHT"));
            rec.append(":</td><td>"); // Column split
            if (!imperial) {
                rec.append(JavaLibBridge.toString(s.getHeight(), 3) + " m");
            } else {
                // / speed/distance/altitude in imperial units
                // (mph/miles/feet?).
                rec.append(JavaLibBridge.toString(
                        s.getHeight() * 3.28083989501312f, 3)
                        + " feet");
            }
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasSpeed()) && (selectedFields.hasSpeed())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("SPEED"));
            rec.append(":</td><td>"); // Column split
            if (!imperial) {
                rec.append(JavaLibBridge.toString(s.getSpeed(), 3) + " km/h");
            } else {
                rec.append(JavaLibBridge.toString(
                        s.getSpeed() * 0.621371192237334f, 3)
                        + " mph");
            }
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasHeading()) && (selectedFields.hasHeading())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("HEADING"));
            rec.append(":</td><td>"); // Column split
            rec.append(JavaLibBridge.toString(s.getHeading()));
            rec.append("&#176;</td></tr>"); // Column end and end row.
        }
        if ((s.hasDsta()) && (selectedFields.hasDsta())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("DSTA"));
            rec.append(":</td><td>"); // Column split
            rec.append(s.getDsta());
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasDage()) && (selectedFields.hasDage())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("DAGE"));
            rec.append(":</td><td>"); // Column split
            rec.append(s.getDage());
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasPdop()) && (selectedFields.hasPdop())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("PDOP"));
            rec.append(":</td><td>"); // Column split
            rec.append(JavaLibBridge.toString(s.getPdop() / 100.0f, 2));
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasHdop()) && (selectedFields.hasHdop())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("HDOP"));
            rec.append(":</td><td>"); // Column split
            rec.append(JavaLibBridge.toString(s.getHdop() / 100.0f, 2));
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasVdop()) && (selectedFields.hasVdop())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("VDOP"));
            rec.append(":</td><td>"); // Column split
            rec.append(JavaLibBridge.toString(s.getVdop() / 100.0f, 2));
            rec.append("</td></tr>"); // Column end and end row.
        }
        if ((s.hasDistance()) && (selectedFields.hasDistance())) {
            rec.append("<tr><td>"); // Table row and first column start
            rec.append(I18N.i18n("DISTANCE"));
            rec.append(":</td><td>"); // Column split
            if (!imperial) {
                rec.append(JavaLibBridge.toString(s.distance, 2));
                rec.append(' ');
                rec.append(I18N.i18n("m"));
            } else {
                rec.append(JavaLibBridge.toString(
                        s.distance * 3.2808398950131234, 2));
                rec.append(' ');
                rec.append(I18N.i18n("feet"));
            }
            rec.append("</td></tr>"); // Column end and end row.
        }
        rec.append("</table>");
    }

    public final static String getDateTimeStr(final int utcTime) {
        final BT747Time t = JavaLibBridge.getTimeInstance();
        t.setUTCTime(utcTime);
        return CommonOut.getDateTimeStr(t);
    }

    public final static String getDateStr(final int utcTime) {
        final BT747Time t = JavaLibBridge.getTimeInstance();
        t.setUTCTime(utcTime);
        return CommonOut.getDateTxtStr(t);
    }

    public final static String getTimeStr(final int utcTime) {
        final BT747Time t = JavaLibBridge.getTimeInstance();
        t.setUTCTime(utcTime);
        return CommonOut.getTimeStr(t);
    }

    public final static String getDateTimeStr(final GPSRecord activeFields,
            final BT747Time time) {
        if ((activeFields.hasUtc())) {
            return CommonOut.getDateTimeStr(time);
        } else {
            return "";
        }
    }

    public final static String getTimeStr(final BT747Time time) {
        return ((time.getHour() < 10) ? "0" : "") + time.getHour() + ":"
                // Minute
                + ((time.getMinute() < 10) ? "0" : "")
                + time.getMinute() // +":"
                + ":" + ((time.getSecond() < 10) ? "0" : "")
                + time.getSecond() // +":"
        ;
    }

    public final static String getDateTxtStr(final BT747Time time) {
        return ((time.getDay() < 10) ? "0" : "") + time.getDay()
                + "-"
                // Month
                + Conv.idxToShortMonthStr(time.getMonth() - 1) + "-"
                + (((time.getYear() % 100)) < 10 ? "0" : "") + time.getYear()
                % 100;
    }
    
    public final static String getDateNumStr(final BT747Time time) {
        return time.getYear() + "/"
        // Month
                + ((time.getMonth() < 10) ? "0" : "") + time.getMonth() + "/"
                // Day
                + ((time.getDay() < 10) ? "0" : "") + time.getDay();
    }

    public final static String getDateTimeStr(final BT747Time time) {
        return CommonOut.getDateTxtStr(time) + " "
                + CommonOut.getTimeStr(time);
    }

    public final static String getDateTimeISO8601(final BT747Time t,
            final int milisecond) {
        return getDateTimeISO8601(t, milisecond, "Z");
    }
    /**
     * @param t
     * @param milisecond
     *            if miliseconds is negative, miliseconds are not added.
     * @return
     */
    public final static String getDateTimeISO8601(final BT747Time t,
            final int milisecond, final String timezone) {
        final StringBuffer timeStr = new StringBuffer(20);
        timeStr.append(t.getYear());
        timeStr.append('-');
        if (t.getMonth() < 10) {
            timeStr.append('0');
        }
        timeStr.append(t.getMonth());
        timeStr.append('-');
        if (t.getDay() < 10) {
            timeStr.append('0');
        }
        timeStr.append(t.getDay());
        timeStr.append('T');
        if (t.getHour() < 10) {
            timeStr.append('0');
        }
        timeStr.append(t.getHour());
        timeStr.append(':');
        if (t.getMinute() < 10) {
            timeStr.append('0');
        }
        timeStr.append(t.getMinute());
        timeStr.append(':');

        if (t.getSecond() < 10) {
            timeStr.append('0');
        }
        timeStr.append(t.getSecond());

        if (milisecond >= 0 && milisecond < 1000) {
            timeStr.append('.');
            if (milisecond < 100) {
                timeStr.append('0');
                if (milisecond < 10) {
                    timeStr.append('0');
                }
            }
            timeStr.append(milisecond);
        }
        timeStr.append(timezone);

        return timeStr.toString();
    }

    public final static String getRCRtype(final GPSRecord s) {
        if (s.rcr == AllWayPointStyles.GEOTAG_VOICE_KEY) {
            return "Voice";
        }
        // For GPX.
        return getRCRstr(s);
    }

    public final static String getRCRstr(final GPSRecord s) {
        final StringBuffer rcrStr = new StringBuffer(15);
        rcrStr.setLength(0);
        if ((s.rcr & BT747Constants.RCR_ALL_APP_MASK) == 0) {
            if ((s.rcr & BT747Constants.RCR_TIME_MASK) != 0) {
                rcrStr.append("T");
            }
            if ((s.rcr & BT747Constants.RCR_SPEED_MASK) != 0) {
                rcrStr.append("S");
            }
            if ((s.rcr & BT747Constants.RCR_DISTANCE_MASK) != 0) {
                rcrStr.append("D");
            }
            if ((s.rcr & BT747Constants.RCR_BUTTON_MASK) != 0) {
                rcrStr.append("B");
            }
        } else {
            rcrStr.append("X");
            rcrStr.append(JavaLibBridge.unsigned2hex(s.rcr, 4));
        }

        // // Still 16-4 = 12 possibilities.
        // // Taking numbers from 0 to 9
        // // Then letters X, Y and Z
        // char c = '0';
        // int i;
        // for (i = 0x10; c <= '9'; i <<= 1, c++) {
        // if ((s.rcr & i) != 0) {
        // rcrStr.append(c);
        // }
        // }
        // c = 'X';
        // for (; i < 0x10000; i <<= 1, c++) {
        // if ((s.rcr & i) != 0) {
        // rcrStr.append(c);
        // }
        // }

        return rcrStr.toString();
    }

    public final static String getFixText(final int valid) {
        switch (valid) {
        case 0x0001:
            return ("No fix");
        case 0x0002:
            return ("SPS");
        case 0x0004:
            return ("DGPS");
        case 0x0008:
            return ("PPS");
        case 0x0010:
            return ("RTK");
        case 0x0020:
            return ("FRTK");
        case 0x0040:
            return ("Estimated mode");
        case 0x0080:
            return ("Manual input mode");
        case 0x0100:
            return ("Simulator mode");
        default:
            return ("Unknown mode");
        }
    }

    /**
     * Table of records related to icons for waypoints:<br>
     * id Label URL x y w h
     * 
     * The table must at least contain the ids T,D,S,B and M. The other IDs
     * are related to application/user specific way points and must be the hex
     * representation: 0102 for instance.
     */

    private static final String[][] IconStyles = {
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
                    "http://maps.google.com/mapfiles/kml/pal4/icon29.png" }, };

    private static WayPointStyleSet wayPointStyles = new WayPointStyleSet(
            CommonOut.IconStyles);

    /**
     * @param wayPointStyles
     *            the wayPointStyles to set
     */
    public static final void setWayPointStyles(
            final WayPointStyleSet wayPointStyles) {
        CommonOut.wayPointStyles = wayPointStyles;
    }

    /**
     * @return the wayPointStyles
     */
    public static final WayPointStyleSet getWayPointStyles() {
        return CommonOut.wayPointStyles;
    }

}
