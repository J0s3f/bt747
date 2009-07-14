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

package bt747.model;

import gps.GpsEvent;

/**
 * Defines some events for the gps package.
 * 
 * @author Mario De Weerd
 */
public class ModelEvent extends GpsEvent {
	public static final int CONVERSION_STARTED = 256;
	public static final int CONVERSION_ENDED = 257;
	// public static final int WORKDIRPATH_UPDATE = 258;
	// public static final int OUTPUTFILEPATH_UPDATE = 259;
	// public static final int LOGFILEPATH_UPDATE = 260;
	/**
	 * @deprecated Use {@link #DOWNLOAD_METHOD_CHANGE} instead.
	 */
	public static final int INCREMENTAL_CHANGE = 261;
	public static final int TRK_VALID_CHANGE = 262;
	public static final int TRK_RCR_CHANGE = 263;
	public static final int WAY_VALID_CHANGE = 264;
	public static final int WAY_RCR_CHANGE = 265;
	public static final int CONNECTED = 266;
	public static final int DISCONNECTED = 267;
	public static final int FILE_LOG_FORMAT_UPDATE = 268;
	/**
	 * A setting changed - int param = index of changed setting
	 */
	public static final int SETTING_CHANGE = 269;
	/**
	 * Notifies a change in the download method setting.
	 */
	public static final int DOWNLOAD_METHOD_CHANGE = 270;
	/**
	 * For use by the application.
	 * 
	 */
	public static final int UPDATE_LOG_FILE_LIST = 271;
	/** Notifies a change in the AGPS upload (start/stop) */
	public static final int AGPS_UPLOAD_DONE = 272;
	/** Notifies a change in the AGPS advancement. */
	public static final int AGPS_UPLOAD_PERCENT = 273;

	/**
	 * Indicates an event of fatal failure to send data to the target sender.<br>
	 * This kind of event is exclusively sent by instances of LocationSender and
	 * should have the sending instance as second parameter.
	 * 
	 * @author Florian Unger
	 */
	public static final int POS_SRV_FATAL_FAILURE = 274;
	/**
	 * Indicates an event of failure to send data to the target server which
	 * need not necessarily have its reason in the configuration we use.<br>
	 * This kind of event is exclusively sent by instances of LocationSender and
	 * should have the sending instance as second parameter.
	 * 
	 * @author Florian Unger
	 */
	public static final int POS_SRV_FAILURE = 275;
	/**
	 * Indicates a successful sending of location data to a target server.<br>
	 * This kind of event is exclusively sent by instances of LocationSender and
	 * should have the sending instance as second parameter.
	 * 
	 * @author Florian Unger
	 */
	public static final int POS_SRV_SUCCESS = 276;

	public ModelEvent(final int type, final Object arg) {
		super(type, arg);
	}

	public ModelEvent(final GpsEvent event) {
		super(event.getType(), event.getArg());
	}
}
