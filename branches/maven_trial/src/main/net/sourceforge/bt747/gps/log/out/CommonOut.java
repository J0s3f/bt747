package net.sourceforge.bt747.gps.log.out;

import net.sourceforge.bt747.gps.BT747Constants;
import net.sourceforge.bt747.gps.log.GPSRecord;

import net.sourceforge.bt747.bt747.sys.Convert;
import net.sourceforge.bt747.bt747.sys.Time;

final class CommonOut {
    protected static final String[] MONTHS_AS_TEXT = { "JAN", "FEB", "MAR",
            "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

    public static final void getHtml(final StringBuffer rec, final GPSRecord s,
            final GPSRecord activeFields, final GPSRecord selectedFields,
            Time t, final boolean recordNbrInLogs, final boolean imperial) {
        if (recordNbrInLogs) {
            rec.append("IDX: ");
            rec.append(Convert.toString(s.recCount));
            rec.append("<br/>");
        }
        if ((activeFields.utc != 0) && (selectedFields.utc != 0)) {
            rec.append("DATE: ");
            rec.append(Convert.toString(t.getYear()) + "-"
                    + (t.getMonth() < 10 ? "0" : "")
                    + Convert.toString(t.getMonth()) + "-"
                    + (t.getDay() < 10 ? "0" : "")
                    + Convert.toString(t.getDay()));
        }
        if ((activeFields.rcr != 0) && (selectedFields.rcr != 0)) {
            rec.append("<br />RCR: ");
            if ((s.rcr & BT747Constants.RCR_TIME_MASK) != 0) {
                rec.append("T");
            }
            if ((s.rcr & BT747Constants.RCR_SPEED_MASK) != 0) {
                rec.append("S");
            }
            if ((s.rcr & BT747Constants.RCR_DISTANCE_MASK) != 0) {
                rec.append("D");
            }
            if ((s.rcr & BT747Constants.RCR_BUTTON_MASK) != 0) {
                rec.append("B");
            }
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
            rec.append(", VALID: ");
            switch (s.valid) {
            case 0x0001:
                rec.append("No fix");
                break;
            case 0x0002:
                rec.append("SPS");
                break;
            case 0x0004:
                rec.append("DGPS");
                break;
            case 0x0008:
                rec.append("PPS");
                break;
            case 0x0010:
                rec.append("RTK");
                break;
            case 0x0020:
                rec.append("FRTK");
                break;
            case 0x0040:
                rec.append("Estimated mode");
                break;
            case 0x0080:
                rec.append("Manual input mode");
                break;
            case 0x0100:
                rec.append("Simulator mode");
                break;
            default:
                rec.append("Unknown mode");
            }
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

    public static final String getTimeStr(final int utcTime) {
        Time t = new Time();
        t.setUTCTime(utcTime);
        return getTimeStr(t);
    }

    public static final String getTimeStr(final GPSRecord activeFields,
            final Time time) {
        if ((activeFields.utc != 0)) {
            return getTimeStr(time);
        } else {
            return "";
        }
    }

    public static final String getTimeStr(final Time time) {
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
 }