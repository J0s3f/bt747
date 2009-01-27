/**
 * 
 */
package bt747.j2se_view.model;

import java.awt.FontMetrics;

import bt747.j2se_view.J2SEAppController;

/**
 * Different data types used in tables and specialisation functions
 * 
 * @author Mario De Weerd
 * 
 */
public final class DataTypes {
    public static final int NONE = 0;
    public static final int FIX_VALID = 1;
    public static final int LATITUDE = 2;
    public static final int LONGITUDE = 3;
    public static final int HEIGHT_METERS = 4;
    public static final int SPEED = 5;
    public static final int HEADING = 6;
    public static final int DSTA = 7;
    public static final int DAGE = 8;
    public static final int PDOP = 9;
    public static final int HDOP = 10;
    public static final int VDOP = 11;
    public static final int NSAT = 12;
    public static final int SID = 13;
    public static final int ELEVATION = 14;
    public static final int AZIMUTH = 15;
    public static final int SNR = 16;
    public static final int RCR = 17;
    public static final int MILLISECOND = 18;
    public static final int DISTANCE = 19;
    public static final int LOGDIST = 20;
    public static final int LOGSPD = 21;
    public static final int LOGTIME = 22;
    public static final int DISTANCE_FEET = 23;
    public static final int EW = 24;
    public static final int FIXMODE = 25;
    public static final int HEIGHT_FEET = 26;
    public static final int LATITUDE_POSITIVE = 27;
    public static final int LONGITUDE_POSITIVE = 28;
    public static final int NS = 29;
    public static final int RCR_DESCRIPTION = 30;
    public static final int RECORDNUMBER = 31;
    public static final int SPEED_MPH = 32;
    public static final int UTC_VALUE = 33;
    public static final int VOX = 34;
    public static final int IMAGE_PATH = 35;
    public static final int IMAGE_WIDTH = 36;
    public static final int IMAGE_HEIGHT = 37;
    public static final int GEOMETRY = 38;
    public static final int GPS_DATETIME = 39;
    public static final int GPS_DATE = 40;
    public static final int GPS_TIME = 41;
    public static final int TAG_DATETIME = 42;
    public static final int TAG_DATE = 43;
    public static final int TAG_TIME = 44;
    public static final int FILE_DATETIME = 45;
    public static final int FILE_DATE = 46;
    public static final int FILE_TIME = 47;
    public static final int LOG_START_DATETIME = 48;
    public static final int LOG_END_DATETIME = 49;
    public static final int LOG_COLOR = 50;
    public static final int LOG_FILENAME = 51;

    public static final Class<?> getDataDisplayClass(final int datatype) {
        switch (datatype) {
        case NONE:
            return null;
        case IMAGE_PATH:
            return String.class;
        case IMAGE_WIDTH:
            return Integer.class;
        case IMAGE_HEIGHT:
            return Integer.class;
        case GEOMETRY:
            return String.class;
        case LATITUDE:
            return Object.class;// return Double.class;
        case LONGITUDE:
            return Object.class;// return Double.class;
        case FILE_DATE:
        case FILE_DATETIME:
        case FILE_TIME:
        case TAG_DATE:
        case TAG_DATETIME:
        case TAG_TIME:
        case GPS_DATETIME:
        case GPS_DATE:
        case GPS_TIME:
            return String.class;
        case LOGTIME:
            return Float.class;
        case LOGDIST:
            return Float.class;
        case LOGSPD:
            return Float.class;
        case NS:
            return String.class;
        case EW:
            return String.class;
        case RECORDNUMBER:
            return Integer.class;
        case UTC_VALUE:
            return Long.class;
        case FIX_VALID:
            return String.class;
        case LATITUDE_POSITIVE:
            return String.class;
        case LONGITUDE_POSITIVE:
            return String.class;
        case HEIGHT_METERS:
            break;
        case HEIGHT_FEET:
            break;
        case SPEED:
            return Float.class;
        case SPEED_MPH:
            return Float.class;
        case HEADING:
            return Float.class;
        case DSTA:
            return Integer.class;
        case DAGE:
            return Integer.class;
        case PDOP:
            return Float.class;
        case HDOP:
            return Float.class;
        case VDOP:
            return Float.class;
        case NSAT:
            return Integer.class;
        case FIXMODE:
            return String.class;
        case SID:
            break;
        case VOX:
            return String.class;
        case RCR:
            return String.class;
        case RCR_DESCRIPTION:
            return String.class;
        case MILLISECOND:
            return Integer.class;
        case DISTANCE:
            return Float.class;
        case DISTANCE_FEET:
            return Float.class;
        case LOG_START_DATETIME:
        case LOG_END_DATETIME:
            return String.class;
        case LOG_COLOR:
            return Object.class;
        case LOG_FILENAME:
            return String.class;
        default:
            return Object.class;
        }
        return Object.class;
    }

    /** Provide sample data to determine the width of the column
     * @param dataType
     * @return
     */
    public static final String getSampleData(final int dataType) {
        String sample = " ";
        switch (dataType) {
        case NONE:
            sample = " ";
            break;
        case IMAGE_PATH:
            sample = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
            break;
        case IMAGE_WIDTH:
            sample = "0000";
            break;
        case IMAGE_HEIGHT:
            sample = "0000";
            break;
        case GEOMETRY:
            sample = "0000x0000";
            break;
        case LATITUDE:
        case LONGITUDE:
            sample = "000.00000000";
            break;
        case FILE_DATETIME:
        case GPS_DATETIME:
        case TAG_DATETIME:
        case LOG_START_DATETIME:
        case LOG_END_DATETIME:
            sample = "2000/00/00 00:00:00";
            break;
        case FILE_DATE:
        case TAG_DATE:
        case GPS_DATE:
            sample = "2000/00/00";
            break;
        case FILE_TIME:
        case TAG_TIME:
        case GPS_TIME:
            sample = "00:00:00";
            break;
        case LOGTIME:
            sample = "0000";
            break;
        case LOGDIST:
            sample = "TAB_TITLE_Log_Dist";
            break;
        case LOGSPD:
            sample = "TAB_TITLE_Log_speed";
            break;
        case NS:
            sample = "TAB_TITLE_NS";
            break;
        case EW:
            sample = "TAB_TITLE_EW";
            break;
        case RECORDNUMBER:
            sample = "#######";
            break;
        case UTC_VALUE:
            sample = "00000000";
            break;
        case FIX_VALID:
            sample = "#0000";
            break;
        case LATITUDE_POSITIVE:
            break;
        case LONGITUDE_POSITIVE:
            break;
        case HEIGHT_METERS:
        case HEIGHT_FEET:
        case SPEED:
        case SPEED_MPH:
            sample = "-0000.00000";
            break;
        case HEADING:
            sample = "000.0";
            break;
        case DSTA:
            sample = "00";
            break;
        case DAGE:
            sample = "00";
            break;
        case PDOP:
        case HDOP:
        case VDOP:
            sample = "XDOP";
            break;
        case NSAT:
            sample = "00";
            break;
        case FIXMODE:
            sample = "XXXX";
            break;
        case SID:
            sample = "00";
            break;
        case VOX:
            sample = "XXXXXXXXXXXXXXXXXXXXXXXX";
            break;
        case RCR:
            sample = "XXXXX";
            break;
        case RCR_DESCRIPTION:
            sample = "XXXXXXXXXXXXXXXXXXXXXXXX";
            break;
        case MILLISECOND:
            sample = "000";
            break;
        case DISTANCE:
        case DISTANCE_FEET:
            sample = "000000";
            break;
        case LOG_COLOR:
            sample = "XXXXXXXX";
            break;
        case LOG_FILENAME:
            sample = "XXXXXXXXXXXXXXXXXXXXXXXX";
            break;
        }
        return sample;
    }

    public static final String getDataDisplayName(final int datatype) {
        String label = null;
        switch (datatype) {
        case NONE:
            label = "TAB_TITLE_None";
            break;
        case IMAGE_PATH:
            label = "TAB_TITLE_Image_path";
            break;
        case IMAGE_WIDTH:
            label = "TAB_TITLE_Width";
            break;
        case IMAGE_HEIGHT:
            label = "TAB_TITLE_Height";
            break;
        case GEOMETRY:
            label = "TAB_TITLE_Geometry";
            break;
        case LATITUDE:
            label = "TAB_TITLE_Latitude";
            break;
        case LONGITUDE:
            label = "TAB_TITLE_Longitude";
            break;
        case FILE_DATETIME:
            label = "TAB_TITLE_File_Date_Time";
            break;
        case FILE_DATE:
            label = "TAB_TITLE_File_Date";
            break;
        case FILE_TIME:
            label = "TAB_TITLE_File_Time";
            break;
        case TAG_DATETIME:
            label = "TAB_TITLE_Tag_Date_Time";
            break;
        case TAG_DATE:
            label = "TAB_TITLE_Tag_Date";
            break;
        case TAG_TIME:
            label = "TAB_TITLE_Tag_Time";
            break;
        case GPS_DATETIME:
            label = "TAB_TITLE_GPS_Date_Time";
            break;
        case GPS_DATE:
            label = "TAB_TITLE_GPS_Date";
            break;
        case GPS_TIME:
            label = "TAB_TITLE_GPS_Time";
            break;
        case LOGTIME:
            label = "TAB_TITLE_Log_time";
            break;
        case LOGDIST:
            label = "TAB_TITLE_Log_Dist";
            break;
        case LOGSPD:
            label = "TAB_TITLE_Log_speed";
            break;
        case NS:
            label = "TAB_TITLE_NS";
            break;
        case EW:
            label = "TAB_TITLE_EW";
            break;
        case RECORDNUMBER:
            label = "TAB_TITLE_Record_nbr";
            break;
        case UTC_VALUE:
            label = "TAB_TITLE_UTC_Value";
            break;
        case FIX_VALID:
            label = "TAB_TITLE_Valid";
            break;
        case LATITUDE_POSITIVE:
            break;
        case LONGITUDE_POSITIVE:
            break;
        case HEIGHT_METERS:
            label = "TAB_TITLE_Height_m";
            break;
        case HEIGHT_FEET:
            label = "TAB_TITLE_Height_ft";
            break;
        case SPEED:
            label = "TAB_TITLE_Speed_kmh";
            break;
        case SPEED_MPH:
            label = "TAB_TITLE_Speed_mph";
            break;
        case HEADING:
            label = "TAB_TITLE_Heading";
            break;
        case DSTA:
            label = "TAB_TITLE_DSTA";
            break;
        case DAGE:
            label = "TAB_TITLE_DAGE";
            break;
        case PDOP:
            label = "TAB_TITLE_PDOP";
            break;
        case HDOP:
            label = "TAB_TITLE_HDOP";
            break;
        case VDOP:
            label = "TAB_TITLE_VDOP";
            break;
        case NSAT:
            label = "TAB_TITLE_NSAT";
            break;
        case FIXMODE:
            label = "TAB_TITLE_Fix";
            break;
        case SID:
            label = "TAB_TITLE_SID";
            break;
        case VOX:
            label = "TAB_TITLE_VOX_File";
            break;
        case RCR:
            label = "TAB_TITLE_RCR";
            break;
        case RCR_DESCRIPTION:
            label = "TAB_TITLE_RCR_Description";
            break;
        case MILLISECOND:
            label = "TAB_TITLE_MS";
            break;
        case DISTANCE:
            label = "TAB_TITLE_Distance_m";
            break;
        case DISTANCE_FEET:
            label = "TAB_TITLE_Distance_ft";
            break;
        case LOG_START_DATETIME:
            label = "TAB_TITLE_StartTime";
            break;
        case LOG_END_DATETIME:
            label = "TAB_TITLE_EndTime";
            break;
        case LOG_COLOR:
            label = "TAB_TITLE_Color";
            break;
        case LOG_FILENAME:
            label = "TAB_TITLE_FileName";
            break;
        }
        if (label != null) {
            label = J2SEAppController.getString(label);
        }
        return label;
    }

    /**
     * @param fontMetrics
     * @return the default date string width
     */
    public final static int defaultDataWidth(final int dataType, FontMetrics fontMetrics) {
      return fontMetrics.stringWidth(getSampleData(dataType)); //$NON-NLS-1$
    }

}
