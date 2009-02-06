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
import gps.log.GPSRecord;

import bt747.sys.Convert;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Time;

public final class CommonOut {
    private static final String[] MONTHS_AS_TEXT = { "JAN", "FEB", "MAR",
            "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

    protected static final String idxToMonthStr(final int i) {
        return MONTHS_AS_TEXT[i];
    }

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
                .getHtml(rec, s, s, s, s.getBT747Time(), true, false /* imperial */);
        final String r = rec.toString();
        rec = null;
        return r;
    }

    public final static void getHtml(final StringBuffer rec,
            final GPSRecord s, final GPSRecord activeFields,
            final GPSRecord selectedFields, final BT747Time t,
            final boolean recordNbrInLogs, final boolean imperial) {
        if (recordNbrInLogs) {
            rec.append("IDX: ");
            rec.append(s.recCount);
            rec.append("<br>");
        }
        if ((activeFields.hasUtc()) && (selectedFields.hasUtc())) {
            rec.append("TIME: ");
            rec.append(CommonOut.getDateTimeStr(t));
        }
        WayPointStyle style = null;
        if ((activeFields.hasRcr()) && (selectedFields.hasRcr())) {
            rec.append("<br>RCR: ");
            final String rcr = CommonOut.getRCRstr(s);
            rec.append(rcr);
            style = CommonOut.wayPointStyles.get(CommonOut.getRCRKey(rcr));
        }
        if (s.voxStr != null) {
            final String upperVox = s.voxStr.toUpperCase();
            final boolean isPicture = upperVox.endsWith(".JPG")
                    || upperVox.endsWith("PNG");
            rec.append("<br>");
            if (style != null) {
                rec.append(style.getSymbolText());
                rec.append(':');
                if (isPicture) {
                    rec.append("<br>");
                }
            }
            rec.append("<a target='_new' href='");
            rec.append(s.voxStr);
            if (s.voxStr.startsWith("VOX")) {
                rec.append(".wav");
            }
            rec.append("'>");
            if (isPicture) {
                rec.append("<img height=150 src='");
                rec.append(s.voxStr);
                rec.append("' >");
            } else {
                rec.append("Click here");
            }
            rec.append("</a>");

        } else {
            if (style != null) {
                rec.append(" <b>(");
                rec.append(style.getSymbolText());
                rec.append(")</b>");
            }
        }
        // if(activeFields.utc!=0) {
        // Time t=utcTime(s.utc);
        //                    
        // rec.append("<br>DATE: ");
        // rec.append(Convert.toString(t.getYear())+"/"
        // +(
        // t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"/"
        // +(
        // t.getDay()<10?"0":"")+Convert.toString(t.getDay())+"<br
        // />"
        // +"TIME: "
        // +(
        // t.getHour()<10?"0":"")+Convert.toString(t.getHour())+":"
        // +(t.getMinute()<10?"0":"")+Convert.toString(t.getMinute())+":"
        // +(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
        // );
        // }
        if ((activeFields.hasValid()) && (selectedFields.hasValid())) {
            // rec.append("<br>VALID: ");
            rec.append("<br>VALID: ");
            rec.append(CommonOut.getFixText(s.valid));
        }
        if ((activeFields.hasLatitude()) && (selectedFields.hasLatitude())) {
            rec.append("<br>LATITUDE: ");
            if (s.latitude >= 0) {
                rec.append(Convert.toString(s.latitude, 6));
                rec.append(" N");
            } else {
                rec.append(Convert.toString(-s.latitude, 6));
                rec.append(" S");
            }
        }
        if ((activeFields.hasLongitude()) && (selectedFields.hasLongitude())) {
            rec.append("<br>LONGITUDE: ");
            if (s.longitude >= 0) {
                rec.append(Convert.toString(s.longitude, 6));
                rec.append(" E");
            } else {
                rec.append(Convert.toString(-s.longitude, 6));
                rec.append(" W");
            }
        }
        if ((activeFields.hasHeight()) && (selectedFields.hasHeight())) {
            rec.append("<br>HEIGHT: ");
            if (!imperial) {
                rec.append(Convert.toString(s.height, 3) + " m");
            } else {
                // / speed/distance/altitude in imperial units
                // (mph/miles/feet?).
                rec.append(Convert.toString(s.height * 3.28083989501312, 3)
                        + " feet");
            }
        }
        if ((activeFields.hasSpeed()) && (selectedFields.hasSpeed())) {
            rec.append("<br>SPEED: ");
            if (!imperial) {
                rec.append(Convert.toString(s.speed, 3) + " km/h");
            } else {
                rec.append(Convert.toString(s.speed * 0.621371192237334, 3)
                        + " mph");
            }
        }
        if ((activeFields.hasHeading()) && (selectedFields.hasHeading())) {
            rec.append("<br>HEADING: ");
            rec.append(Convert.toString(s.heading));
        }
        if ((activeFields.hasDsta()) && (selectedFields.hasDsta())) {
            rec.append("<br>DSTA: ");
            rec.append(s.dsta);
        }
        if ((activeFields.hasDage()) && (selectedFields.hasDage())) {
            rec.append("<br>DAGE: ");
            rec.append(s.dage);
        }
        if ((activeFields.hasPdop()) && (selectedFields.hasPdop())) {
            rec.append("<br>PDOP: ");
            rec.append(Convert.toString(s.pdop / 100.0, 2));
        }
        if ((activeFields.hasHdop()) && (selectedFields.hasHdop())) {
            rec.append("<br>HDOP: ");
            rec.append(Convert.toString(s.hdop / 100.0, 2));
        }
        if ((activeFields.hasVdop()) && (selectedFields.hasVdop())) {
            rec.append("<br>VDOP: ");
            rec.append(Convert.toString(s.vdop / 100.0, 2));
        }
        if ((activeFields.hasDistance()) && (selectedFields.hasDistance())) {
            rec.append("<br>DISTANCE: ");
            if (!imperial) {
                rec.append(Convert.toString(s.distance, 2));
                rec.append(" m");
            } else {
                rec.append(Convert.toString(s.distance * 3.2808398950131234,
                        2));
                rec.append(" feet");
            }
        }
    }

    public final static String getDateTimeStr(final int utcTime) {
        final BT747Time t = Interface.getTimeInstance();
        t.setUTCTime(utcTime);
        return CommonOut.getDateTimeStr(t);
    }

    public final static String getDateStr(final int utcTime) {
        final BT747Time t = Interface.getTimeInstance();
        t.setUTCTime(utcTime);
        return CommonOut.getDateTxtStr(t);
    }

    public final static String getTimeStr(final int utcTime) {
        final BT747Time t = Interface.getTimeInstance();
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
                + CommonOut.MONTHS_AS_TEXT[time.getMonth() - 1] + "-"
                + (((time.getYear() % 100)) < 10 ? "0" : "") + time.getYear()
                % 100;
    }

    public final static String getDateNumStr(final BT747Time time) {
        return time.getYear() + "/"
        // Month
                + ((time.getMonth() < 10) ? "0" : "") + time.getMonth()
                // Day
                + ((time.getDay() < 10) ? "0" : "") + time.getDay();
    }

    public final static String getDateTimeStr(final BT747Time time) {
        return CommonOut.getDateTxtStr(time) + " "
                + CommonOut.getTimeStr(time);
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
            rcrStr.append(Convert.unsigned2hex(s.rcr, 4));
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
     *                the wayPointStyles to set
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
