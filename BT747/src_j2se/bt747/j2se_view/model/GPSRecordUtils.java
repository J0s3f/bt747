/**
 * 
 */
package bt747.j2se_view.model;

import bt747.sys.Generic;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

/**
 * @author Mario
 *
 */
public class GPSRecordUtils {

    public static final Object getValue(final GPSRecord g, final int type) {
        switch (type) {
        case DataTypes.LOGTIME:
            break;
        case DataTypes.LOGDIST:
            break;
        case DataTypes.LOGSPD:
            break;
        case DataTypes.NS:
            break;
        case DataTypes.EW:
            break;
        case DataTypes.RECORDNUMBER:
            if (g.hasRecCount()) {
                return Integer.valueOf(g.recCount);
            }
            break;
        case DataTypes.UTC_VALUE:
            if (g.hasUtc()) {
                return Long.valueOf(g.getUtc());
            }
            break;
        case DataTypes.GPS_DATE:
            if (g.hasUtc()) {
                return CommonOut.getDateStr(g.getUtc());
            }
            break;
        case DataTypes.GPS_TIME:
            if (g.hasUtc()) {
                return CommonOut.getTimeStr(g.getUtc());
            }
            break;
        case DataTypes.GPS_DATETIME:
            if (g.hasUtc()) {
                return CommonOut.getDateTimeStr(g.getUtc());
            }
            break;
    
        case DataTypes.TAG_DATE:
            if (g.hasTagUtc()) {
                return CommonOut.getDateStr(g.tagutc);
            }
            break;
        case DataTypes.TAG_TIME:
            if (g.hasTagUtc()) {
                return CommonOut.getTimeStr(g.tagutc);
            }
            break;
        case DataTypes.TAG_DATETIME:
            if (g.hasTagUtc()) {
                return CommonOut.getDateTimeStr(g.tagutc);
            }
            break;
        case DataTypes.FIX_VALID:
            if (g.hasValid()) {
                return CommonOut.getFixText(g.getValid());
            }
            break;
        case DataTypes.LATITUDE_POSITIVE:
            if (g.hasLatitude()) {
                if (g.getLatitude() < 0.) {
                    return -g.getLatitude();
                } else {
                    return g.getLatitude();
                }
            }
            break;
        case DataTypes.LONGITUDE_POSITIVE:
            if (g.hasLongitude()) {
                if (g.getLongitude() < 0.) {
                    return -g.getLongitude();
                } else {
                    return g.getLongitude();
                }
            }
            break;
        case DataTypes.LATITUDE:
            if (g.hasLatitude()) {
                return Double.valueOf(g.getLatitude());
            }
            break;
        case DataTypes.LONGITUDE:
            if (g.hasLongitude()) {
                return Double.valueOf(g.getLongitude());
            }
            break;
        case DataTypes.HEIGHT_METERS:
            if (g.hasHeight()) {
                return Float.valueOf(g.getHeight());
            }
            break;
        case DataTypes.HEIGHT_FEET:
            break;
        case DataTypes.SPEED:
            if (g.hasSpeed()) {
                return Float.valueOf(g.getSpeed());
            }
            break;
        case DataTypes.SPEED_MPH:
            break;
        case DataTypes.HEADING:
            if (g.hasHeading()) {
                return Float.valueOf(g.getHeading());
            }
            break;
        case DataTypes.DSTA:
            if (g.hasDsta()) {
                return Integer.valueOf(g.getDsta());
            }
            break;
        case DataTypes.DAGE:
            if (g.hasDage()) {
                return Integer.valueOf(g.getDage());
            }
            break;
        case DataTypes.PDOP:
            if (g.hasPdop()) {
                return Float.valueOf(g.getPdop() / 100.0f);
            }
            break;
        case DataTypes.HDOP:
            if (g.hasHdop()) {
                return Float.valueOf(g.getHdop() / 100.0f);
            }
            break;
        case DataTypes.VDOP:
            if (g.hasVdop()) {
                return Float.valueOf(g.getVdop() / 100.0f);
            }
            break;
        case DataTypes.NSAT:
            if (g.hasNsat()) {
                return Integer.valueOf(g.getNsat());
            }
            break;
        case DataTypes.FIXMODE:
            if (g.hasValid()) {
                return CommonOut.getFixText(g.getValid());
            }
            break;
        case DataTypes.SID:
    
            break;
        case DataTypes.VOX:
            if (g.hasVoxStr()) {
                return g.voxStr;
            }
            break;
        case DataTypes.RCR:
            if (g.hasRcr()) {
                return CommonOut.getRCRstr(g);
            }
            break;
        case DataTypes.RCR_DESCRIPTION:
            if (g.hasRcr()) {
                return CommonOut.getRcrSymbolText(g);
            }
            break;
        case DataTypes.MILLISECOND:
            if (g.hasMillisecond()) {
                return Integer.valueOf(g.milisecond);
            }
            break;
        case DataTypes.DISTANCE_METERS:
            if (g.hasDistance()) {
                return Double.valueOf(g.distance);
            }
            break;
        case DataTypes.DISTANCE_FEET:
            break;
        // Image specific
        }
        return null; // Default;
    }

    public static final boolean setValue(final Object value,
            final GPSRecord g, final int type) {
        try {
            switch (type) {
            case DataTypes.LOGTIME:
                break;
            case DataTypes.LOGDIST:
                break;
            case DataTypes.LOGSPD:
                break;
            case DataTypes.NS:
                break;
            case DataTypes.EW:
                break;
            case DataTypes.RECORDNUMBER:
                g.recCount = (Integer) value;
                break;
            case DataTypes.GPS_DATE:
                break;
            case DataTypes.GPS_TIME:
                break;
            case DataTypes.FIX_VALID:
                break;
            case DataTypes.LATITUDE_POSITIVE:
                break;
            case DataTypes.LONGITUDE_POSITIVE:
                break;
            case DataTypes.LATITUDE:
                g.latitude = (Double) value;
                break;
            case DataTypes.LONGITUDE:
                g.longitude = (Double) value;;
                break;
            case DataTypes.HEIGHT_METERS:
                break;
            case DataTypes.HEIGHT_FEET:
                break;
            case DataTypes.SPEED:
                break;
            case DataTypes.SPEED_MPH:
                break;
            case DataTypes.HEADING:
                break;
            case DataTypes.DSTA:
                break;
            case DataTypes.DAGE:
                break;
            case DataTypes.PDOP:
                break;
            case DataTypes.HDOP:
                break;
            case DataTypes.VDOP:
                break;
            case DataTypes.NSAT:
                break;
            case DataTypes.FIXMODE:
                break;
            case DataTypes.SID:
                break;
            case DataTypes.VOX:
                break;
            case DataTypes.RCR:
                break;
            case DataTypes.RCR_DESCRIPTION:
                break;
            case DataTypes.MILLISECOND:
                break;
            case DataTypes.DISTANCE_METERS:
                break;
            case DataTypes.DISTANCE_FEET:
                break;
            }
            return true;
        } catch (final Exception e) {
            Generic.debug("setValue " + type + " " + value, e);
        }
        return false;
    }

}
