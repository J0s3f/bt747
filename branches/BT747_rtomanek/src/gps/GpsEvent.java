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
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package gps;

import gps.log.GPSRecord;

import bt747.model.Controller;
import bt747.sys.interfaces.BT747Int;

/**
 * Event implementation for signaling the application.
 * 
 * @author Mario De Weerd
 */
public class GpsEvent {
    // public static final int DATA_UPDATE = 1;
    /** logFormat update notification. */
    public static final int UPDATE_LOG_FORMAT = 2;
    /** GPRMC string received. Event argument has {@link GPSRecord} data. */
    public static final int GPRMC = 3;
    /** GPGGA string received. Event argument has {@link GPSRecord} data. */
    public static final int GPGGA = 4;
    /**
     * Indicates that the log download has started.
     */
    public static final int LOG_DOWNLOAD_STARTED = 5;
    /**
     * Indicates that there was some progress of the download - the
     * application can update its progress bar.
     */
    public static final int DOWNLOAD_STATE_CHANGE = 6;
    /**
     * Indicates the log download has finished.
     */
    public static final int LOG_DOWNLOAD_DONE = 7;
    /**
     * A log download was initiated. An incremental download was selected and
     * data was already present but does not correspond to the data currently
     * in the device. The application should request user confirmation to
     * overwrite this data and respond with
     * {@link Controller#replyToOkToOverwrite(boolean)}.
     */
    public static final int DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY = 8;
    /**
     * An erase started. The application should show a popup during the erase.
     */
    public static final int ERASE_ONGOING_NEED_POPUP = 9;
    /** The erase cycle ended. The application should remove the erase popup. */
    public static final int ERASE_DONE_REMOVE_POPUP = 10;
    public static final int COULD_NOT_OPEN_FILE = 11;
    // public static final int DEBUG_MSG = 12;
    /**
     * logFixPeriod update notification.
     * 
     * @see GpsController#getLogFixPeriod()
     */
    public static final int UPDATE_FIX_PERIOD = 13;
    public static final int LOG_DOWNLOAD_SUCCESS = 14;
    /** NMEA periods update notification. */
    public static final int UPDATE_OUTPUT_NMEA_PERIOD = 15;
    /**
     * Indicates flash configuration data is available.<br>
     * dtUserOptionTimesLeft , dtUpdateRate , dtBaudRate , dtGLL_Period ,
     * dtRMC_Period , dtVTG_Period , dtGSA_Period , dtGSV_Period ,
     * dtGGA_Period , dtZDA_Period , dtMCHN_Period ,
     */
    public static final int UPDATE_FLASH_CONFIG = 16;
    /**
     * Indicates the Holux Name is available.
     */
    public static final int UPDATE_HOLUX_NAME = 17;
    /** logTimeInterval update notification. */
    public static final int UPDATE_LOG_TIME_INTERVAL = 18;
    /** logSpeedInterval update notification. */
    public static final int UPDATE_LOG_SPEED_INTERVAL = 19;
    /** logDistanceInterval update notification. */
    public static final int UPDATE_LOG_DISTANCE_INTERVAL = 20;
    // AVAILABLE: public static final int UPDATE_FIX_INTERVAL = 21;
    /** logMemUsedPercent update notification. */
    public static final int UPDATE_LOG_MEM_USED = 22;
    /** flashManuProdID update notification. */
    public static final int UPDATE_LOG_FLASH = 23;
    /** logNbrLogPts update notification. */
    public static final int UPDATE_LOG_NBR_LOG_PTS = 24;
    /** Flash sector status update notification. */
    public static final int UPDATE_LOG_FLASH_SECTORS = 25;
    /** MtkLogVersion update notification. */
    public static final int UPDATE_LOG_VERSION = 26;
    /** logFullOverwrite update notification. */
    public static final int UPDATE_LOG_REC_METHOD = 27;
    /**
     * logStatus, loggingActive, loggerIsFull, loggerNeedsInit,
     * loggerIsDisabled update notification.
     * 
     * @see GpsController#isLoggingActive()
     */
    public static final int UPDATE_LOG_LOG_STATUS = 28;
    /** dgpsMode update notification. */
    public static final int UPDATE_DGPS_MODE = 29;
    /** SBASEnabled update notification */
    public static final int UPDATE_SBAS = 30;
    /** SBASTestEnabled update notification */
    public static final int UPDATE_SBAS_TEST = 31;
    /** The Power Save Mode is available or updated. */
    public static final int UPDATE_PWR_SAV_MODE = 32;
    /** The used GPS DATUM is available or updated. */
    public static final int UPDATE_DATUM = 33;
    /** The Bluetooth mac address is available. */
    public static final int UPDATE_BT_MAC_ADDR = 34;
    /** The version of the MTK chipset is available. (mainVersion) */
    public static final int UPDATE_MTK_VERSION = 35;
    /**
     * The release of the MTK chipset is available. (firmwareVersion, model,
     * device)
     */
    public static final int UPDATE_MTK_RELEASE = 36;

    /**
     * Indicates GPS data is available.<br>
     * The argument is a {@link BT747Int} identifying the data type.
     */
    public static final int DATA_UPDATE = 37;

    /**
     * An exception has been thrown. The parameter must be a BT747Exception.
     */
    public static final int EXCEPTION = 38;

    /**
     * The event's type.
     */
    private final int type;
    /**
     * The event's argument.
     */
    private final Object arg;

    /**
     * Constructor of new Event with given type and no argument.
     * 
     * @param type
     *            Type of event.
     */
    public GpsEvent(final int type) {
        this.type = type;
        arg = null;
    }

    /**
     * Constructor of new Event with given type and argument.
     * 
     * @param type
     *            Type number.
     * @param arg
     *            Argument.
     */
    public GpsEvent(final int type, final Object arg) {
        this.type = type;
        this.arg = arg;
    }

    /**
     * Get the event's type.
     * 
     * @return event type.
     */
    public final int getType() {
        return type;
    }

    /**
     * Get the event's argument.
     * 
     * @return event argument. 'null' if no argument.
     */
    public final Object getArg() {
        return arg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return super.toString() + " Event: " + getType() + " Arg:" + getArg();
    }

}
