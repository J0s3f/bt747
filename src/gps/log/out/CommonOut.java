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
package gps.log.out;

import gps.BT747Constants;
import gps.log.GPSRecord;

import bt747.sys.Convert;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Time;

public final class CommonOut {
    protected static final String[] MONTHS_AS_TEXT = { "JAN", "FEB", "MAR",
            "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

    public final static void getHtml(final StringBuffer rec, final GPSRecord s,
            final GPSRecord activeFields, final GPSRecord selectedFields,
            final BT747Time t, final boolean recordNbrInLogs, final boolean imperial) {
        if (recordNbrInLogs) {
            rec.append("IDX: ");
            rec.append(Convert.toString(s.recCount));
            rec.append("<br/>");
        }
        if ((activeFields.utc != 0) && (selectedFields.utc != 0)) {
            rec.append("TIME: ");
            rec.append(getTimeStr(t));
        }
        if ((activeFields.rcr != 0) && (selectedFields.rcr != 0)) {
            rec.append("<br/>RCR: ");
            rec.append(getRCRstr(s));
        }
        // if(activeFields.utc!=0) {
        // Time t=utcTime(s.utc);
        //                    
        // rec.append("<br />DATE: ");
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
        if ((activeFields.valid != 0) && (selectedFields.valid != 0)) {
            // rec.append("<br />VALID: ");
            rec.append("<br/>VALID: ");
            rec.append(getFixText(s.valid));
        }
        if ((activeFields.latitude != 0) && (selectedFields.latitude != 0)) {
            rec.append("<br />LATITUDE: ");
            if (s.latitude >= 0) {
                rec.append(Convert.toString(s.latitude, 6));
                rec.append(" N");
            } else {
                rec.append(Convert.toString(-s.latitude, 6));
                rec.append(" S");
            }
        }
        if ((activeFields.longitude != 0) && (selectedFields.longitude != 0)) {
            rec.append("<br />LONGITUDE: ");
            if (s.longitude >= 0) {
                rec.append(Convert.toString(s.longitude, 6));
                rec.append(" E");
            } else {
                rec.append(Convert.toString(-s.longitude, 6));
                rec.append(" W");
            }
        }
        if ((activeFields.height != 0) && (selectedFields.height != 0)) {
            rec.append("<br />HEIGHT: ");
            if (!imperial) {
                rec.append(Convert.toString(s.height, 3) + " m");
            } else {
                // / speed/distance/altitude in imperial units
                // (mph/miles/feet?).
                rec.append(Convert.toString(s.height * 3.28083989501312, 3)
                        + " feet");
            }
        }
        if ((activeFields.speed != 0) && (selectedFields.speed != 0)) {
            rec.append("<br />SPEED: ");
            if (!imperial) {
                rec.append(Convert.toString(s.speed, 3) + " km/h");
            } else {
                rec.append(Convert.toString(s.speed * 0.621371192237334, 3)
                        + " mph");
            }
        }
        if ((activeFields.heading != 0) && (selectedFields.heading != 0)) {
            rec.append("<br />HEADING: ");
            rec.append(Convert.toString(s.heading));
        }
        if ((activeFields.dsta != 0) && (selectedFields.dsta != 0)) {
            rec.append("<br />DSTA: ");
            rec.append(Convert.toString(s.dsta));
        }
        if ((activeFields.dage != 0) && (selectedFields.dage != 0)) {
            rec.append("<br />DAGE: ");
            rec.append(Convert.toString(s.dage));
        }
        if ((activeFields.pdop != 0) && (selectedFields.pdop != 0)) {
            rec.append("<br />PDOP: ");
            rec.append(Convert.toString(s.pdop / 100.0, 2));
        }
        if ((activeFields.hdop != 0) && (selectedFields.hdop != 0)) {
            rec.append("<br />HDOP: ");
            rec.append(Convert.toString(s.hdop / 100.0, 2));
        }
        if ((activeFields.vdop != 0) && (selectedFields.vdop != 0)) {
            rec.append("<br />VDOP: ");
            rec.append(Convert.toString(s.vdop / 100.0, 2));
        }
        if ((activeFields.distance != 0) && (selectedFields.distance != 0)) {
            rec.append("<br />DISTANCE: ");
            if (!imperial) {
                rec.append(Convert.toString(s.distance, 2));
                rec.append(" m");
            } else {
                rec
                        .append(Convert.toString(
                                s.distance * 3.2808398950131234, 2));
                rec.append(" feet");
            }
        }
    }

    public final static String getTimeStr(final int utcTime) {
        BT747Time t = Interface.getTimeInstance();
        t.setUTCTime(utcTime);
        return getTimeStr(t);
    }

    public final static String getTimeStr(final GPSRecord activeFields,
            final BT747Time time) {
        if ((activeFields.utc != 0)) {
            return getTimeStr(time);
        } else {
            return "";
        }
    }

    public final static String getTimeStr(final BT747Time time) {
        return // Day
        ((time.getDay() < 10) ? "0" : "")
                + Convert.toString(time.getDay())
                + "-"
                // Month
                + CommonOut.MONTHS_AS_TEXT[time.getMonth() - 1] + "-"
                + (((time.getYear() % 100)) < 10 ? "0" : "")
                + Convert.toString(time.getYear() % 100)
                + " "
                // Hour
                + ((time.getHour() < 10) ? "0" : "")
                + Convert.toString(time.getHour()) + ":"
                // Minute
                + ((time.getMinute() < 10) ? "0" : "")
                + Convert.toString(time.getMinute()) // +":"
        // +(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
        ;
    }

    public final static String getRCRstr(final GPSRecord s) {
        StringBuffer rcrStr = new StringBuffer(15);
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
}
