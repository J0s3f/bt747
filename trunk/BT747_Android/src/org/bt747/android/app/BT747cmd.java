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
/*
 * To run: Path must include RXTX. In Eclipse, set in environment, for example
 * (on windows): PATH
 * ${project_loc:BT747}/lib/rxtx-2.1-7-bins-r2/Windows/i368-mingw32/;%PATH%
 * classpath must include: libBT747.jar collections-superwaba.jar (if the
 * libBT747 is a debug library).
 */
package org.bt747.android.app;

import gps.BT747Constants;
import gps.connection.GPSrxtx;
import gps.convert.Conv;
import gps.log.LogFileInfo;
import gps.mvc.MtkController;
import gps.mvc.MtkModel;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.bt747.android.system.AndroidTranslations;

import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.JavaLibBridge;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747Int;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Time;
/**
 * 
 * @author Mario De Weerd
 */
public class BT747cmd implements bt747.model.ModelListener {

	/**
     * 
     */
	private static final String OPT_OVERWRITE = "overwrite";
	/**
     * 
     */
	private static final String OPT_DOWNLOAD_METHOD = "download-method";
	/**
     * 
     */
	private static final String OPT_TRKPTNAME = "trkptname";
	/**
     * 
     */
	private static final String OPT_TRKPTINFO = "trkptinfo";
	/**
     * 
     */
	private static final String OPT_OUTPUT_TYPE = "outtype";
	/**
     * 
     */
	private static final String OPT_CREATE_GPX_WAYPOINTS = "w";
	/**
     * 
     */
	private static final String OPT_VERSION_ONLY = "v";
	/**
     * 
     */
	private static final String OPT_CREATE_GPX_TRACKS = "t";
	/**
     * 
     */
	private static final String OPT_SET_LOG_CRITERIA = "r";
	/**
     * 
     */
	private static final String OPT_RECOVER_LOGGER = "R";
	/**
     * 
     */
	private static final String OPT_SET_LOG_FIELDS = "o";
	/**
     * 
     */
	private static final String OPT_OVERLAP_STOP_SETTING = "m";
	/**
     * 
     */
	private static final String OPT_LOGGING_ON_OFF = "l";
	/**
     * 
     */
	private static final String OPT_ERASE_MEMORY = "E";
	/**
     * 
     */
	private static final String OPT_DOWNLOAD = "a";
	/**
     * 
     */
	private static final String OPT_HELP = "h";
	/**
     * 
     */
	private static final String OPT_DEVICETYPE = "device";
	/**
     * 
     */
	private static final String OPT_MACADDR = "mac-address";
	/**
     * 
     */
	private static final String OPT_TIMESPLIT = "timesplit";
	/**
     * 
     */
	private static final String OPT_SPLITTYPE = "splittype";
	/**
     * 
     */
	private static final String OPT_BADCOLOR = "badcolor";
	/**
     * 
     */
	private static final String OPT_COLOR = "color";
	/**
     * 
     */
	private static final String OPT_UTC = "UTC";
	/**
     * 
     */
	private static final String OPT_HEIGHT = "height";
	/**
     * 
     */
	private static final String OPT_SERIAL_PORT = "p";
	/**
     * 
     */
	private static final String OPT_SERIAL_SPEED = "s";
	/**
     * 
     */
	private static final String OPT_BINARY_FILE = "b";
	/**
     * 
     */
	private static final String OPT_FILE_BASENAME = "f";
	/** Debug option. */
	private static final String OPT_DEBUG = "d";
	/** Photo time offset option. */
	private static final String OPT_FILE_TIMEZONE = "tz";
	/** Specify the format for the tagged filename. */
	private static final String OPT_TARGET_TAGGED_FILENAME = "template-taggedfilename";
	/** Specify AGPS url (and upload). */
	private static final String OPT_AGPS_URL = "agps-url";
	/** Request a clear of the AGPS data. */
	private static final String OPT_AGPS_CLEAR = "agps-clear";
	/** Request the status of the AGPS data. */
	private static final String OPT_AGPS_STATUS = "agps-status";
	/** Request AGPS upload from the default URL. */
	private static final String OPT_AGPS = "agps";
	/** Perform a cold, warm, hot start or factory reset */
	private static final String OPT_START = "start";
	/** Cold parameter for start. */
	private static final String OPT_COLD = "cold";
	/** Cold parameter for start. */
	private static final String OPT_WARM = "warm";
	/** Cold parameter for start. */
	private static final String OPT_HOT = "hot";
	/** Cold parameter for start. */
	private static final String OPT_FACTORY = "factory";

	private int eraseTimeoutMs = 60000;

	/**
	 * Set up system specific classes.
	 */
	static {
		// Set up the low level functions interface.
		JavaLibBridge
				.setJavaLibImplementation(org.bt747.android.system.AndroidTranslations
						.getInstance());
		// Set the serial port class instance to use (also system specific).
		if (!GPSrxtx.hasDefaultPortInstance()) {
			GPSrxtx.setDefaultGpsPortInstance(new org.bt747.android.rxtx.GPSRxTxPort());
		}
	}

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private Model m;
	private AndroidController c;

	class OptionSet {
		public boolean has(String optionRef) {
			return false;
		}
		public Object valueOf(String optionRef) {
			return null;
		}
		public String argumentOf(String optionRef) {
			return null;
		}
		public final List<?> valuesOf(String optionRef) {
			return null;
		}
	};
	
	public BT747cmd(final Model m, final AndroidController c,
			final OptionSet options) {
		Settings.setAppSettings(new String(new byte[AppSettings.SIZE]));
		setController(c);
		setModel(m);
		handleOptions(options);
	}

	public void setController(final AndroidController c) {
		this.c = c; // Should check that c is an AppController or do it
	}

	public void setModel(final Model m) {
		if (this.m != null) {
			this.m.removeListener(this);
		}
		this.m = m;
	}

	// Code snippet kept for future reference.
	// // We can do the same thing to an array for internal treatment
	// GPSRecord[] positions = c.doConvertLogToTrackPoints();
	// if (positions == null) {
	// // Error occured
	// reportError(c.getLastError(), c.getLastErrorInfo());
	// } else {
	// // Print the first ten positions
	// for (int i = 0; i < positions.length && i < 10; i++) {
	// GPSRecord record = positions[i];
	// System.out.println("Position " + i + ":" + record.latitude
	// + "," + record.longitude);
	// }

	private void reportError(final int error, final String errorInfo) {
		System.err.println("\n####    PROBLEM  !!! ####");

		switch (error) {
		case BT747Constants.ERROR_COULD_NOT_OPEN:
			System.err.println("ERROR - Could not open " + errorInfo);
			break;
		case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
			System.err
					.println("WARNING - No files were created - Check the input type.");
			break;
		case BT747Constants.ERROR_READING_FILE:
			System.err.println("ERROR - Problem reading" + errorInfo);
			break;
		default:
			break;
		}
	}

	public final static void notifyBT747Exception(final BT747Exception e) {
		String message = "";
		System.err.println("\n####    ERROR  !!! ####");
		if (e.getCause().toString().equals(BT747Exception.ERR_COULD_NOT_OPEN)) {
			message = "Error - Could not open " + e.getMessage();
		} else {
			message = e.getCause().toString() + '\n' + e.getMessage();
		}
		System.err.println(message);
	}

	// Code snippet kept for reference

	// public void modelEvent(ModelEvent e) {
	// // TODO Auto-generated method stub
	// int type = e.getType();
	// } else if (type == ModelEvent.CONVERSION_STARTED) {
	// // conversionStartTime = System.currentTimeMillis();
	// } else if (type == ModelEvent.CONVERSION_ENDED) {
	// // lbConversionTime
	// // .setText("Time to convert: "
	// // + ((int) (System.currentTimeMillis() - conversionStartTime))
	// // + " ms");
	// // lbConversionTime.setVisible(true);
	// } else if (type == ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY) {
	// // When the data on the device is not the same, overwrite
	// // automatically.
	// System.out
	// .println("Overwriting previously downloaded data that looks
	// different.");
	// c.replyToOkToOverwrite(true);
	// } else if (type == ModelEvent.DOWNLOAD_STATE_CHANGE
	// || type == ModelEvent.LOG_DOWNLOAD_STARTED) {
	// progressUpdate();
	// } else if (type == ModelEvent.LOG_DOWNLOAD_DONE) {
	// progressUpdate();
	// handleDownloadEnded();
	// } else if (type == ModelEvent.DEBUG_MSG) {
	// System.out.flush();
	// System.err.println((String) e.getArg());
	// System.err.flush();
	// progressUpdate();
	// } else if (type == ModelEvent.CONNECTED) {
	// // btConnect.setText("Disconnect");
	// // btConnectFunctionIsConnect = false;
	//
	// // Launching in another thread - not really needed.
	// java.awt.EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// afterConnection();
	// }
	// });
	//
	// } else if (type == ModelEvent.DISCONNECTED) {
	// // btConnect.setText("Connect");
	// // btConnectFunctionIsConnect = true;
	// }
	// }

	private volatile boolean downloadIsSuccessFull = true;
	private volatile Integer eraseStarted = 0;
	private volatile Integer eraseOngoing = 0;
	private long conversionStartTime;
	private long downloadStartTime;
	private volatile boolean overwriteDownloadOk = false;

	public void modelEvent(final ModelEvent e) {
		try {
			switch (e.getType()) {
			// case ModelEvent.DEBUG_MSG:
			// System.out.flush();
			// System.err.println((String) e.getArg());
			// System.err.flush();
			// break;
			case ModelEvent.LOG_DOWNLOAD_STARTED:
				downloadStartTime = System.currentTimeMillis();
				downloadIsSuccessFull = false;
				progressUpdate();
				break;
			case ModelEvent.DOWNLOAD_STATE_CHANGE:
				progressUpdate();
				break;
			case ModelEvent.LOG_DOWNLOAD_DONE:
				progressUpdate();
				if (!downloadIsSuccessFull) {
					System.out.println("\n#### DOWNLOAD FAILED ####");
				} else {
					System.out.println("\n#### DOWNLOAD SUCCESS ####");
				}
				System.out
						.println("Time to download data (ms): "
								+ ((int) (System.currentTimeMillis() - downloadStartTime))
								+ " ms");
				break;
			case ModelEvent.LOG_DOWNLOAD_SUCCESS:
				downloadIsSuccessFull = true;
				break;
			case ModelEvent.EXCEPTION:
				// TODO: better handling of exception in J2SEAGPS.java through
				// specialisation of exception
				if (!agpsUploadDone) {
					agpsUploadDone = true;
				}
				break;
			case ModelEvent.AGPS_UPLOAD_DONE:
				agpsProgressBarDone();
				break;
			case ModelEvent.AGPS_UPLOAD_PERCENT:
				agpsProgressBarUpdate(((BT747Int) e.getArg()).getValue());
				break;
			case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
				if (overwriteDownloadOk) {
					// // When the data on the device is not the same,
					// overwrite
					// // automatically.
					System.out.println("Overwriting previously downloaded data"
							+ " that looks different.");
					c.replyToOkToOverwrite(true);
				} else {
					System.out
							.println("\n#### DOWNLOAD ABORTED BECAUSE DATA ON DISK IS DIFFERENT ####");
					System.out
							.println("\n####    Change destination file or use '-overwrite'     ####");
					c.replyToOkToOverwrite(false);
				}
				break;
			case ModelEvent.ERASE_ONGOING_NEED_POPUP:
				setEraseOngoing(true);
				break;
			case ModelEvent.ERASE_DONE_REMOVE_POPUP:
				setEraseOngoing(false);
				break;
			case ModelEvent.CONVERSION_STARTED:
				conversionStartTime = System.currentTimeMillis();
				break;
			case ModelEvent.CONVERSION_ENDED:
				System.out
						.println("Time to convert data (ms): "
								+ ((int) (System.currentTimeMillis() - conversionStartTime))
								+ " ms");
				break;
			default:
				break;
			}

		} catch (BT747Exception b) {
			notifyBT747Exception(b);
		}
	}

	private void flushOutstandingCmds() {
		while (m.getOutstandingCommandsCount() > 0) {
			// Thread t=Thread.currentThread();
			try {
				// System.out.println("Waiting for cmds "
				// + m.getOutstandingCommandsCount());
				// System.out.flush();
				Thread.sleep(50);
			} catch (final Exception e) {
				e.printStackTrace();
				// Do nothing
			}
		}
	}

	/**
	 * Previous downloaded percentage
	 */
	private int prevPercent = -1;

	/**
	 * Update the progress status
	 */
	private void progressUpdate() {
		int min;
		int max;
		int value;
		int percent = prevPercent;

		if (m.isDownloadOnGoing()) {
			min = m.getStartAddr();
			max = m.getEndAddr();
			value = m.getNextReadAddr();
			if (max != min) {
				percent = (value - min) * 100 / (max - min);
			}
			if (percent != prevPercent) {
				while (prevPercent < percent) {
					prevPercent++;
					System.out.print('*');
					if ((prevPercent % 10) == 0) {
						System.out.print("#" + percent + "%#");
						System.out.flush();
					}
				}
			}
		}
	}

	private int prevAgpsPercent = 0;

	/**
	 * Update the progress status
	 */
	private void agpsProgressBarUpdate(final int percent) {
		if (percent != prevAgpsPercent) {
			while (prevAgpsPercent < percent) {
				prevAgpsPercent++;
				System.out.print('*');
				if ((prevAgpsPercent % 10) == 0) {
					System.out.print("#" + percent + "%#");
					System.out.flush();
				}
			}
		}
	}

	volatile boolean agpsUploadDone = false;

	private void agpsProgressBarDone() {
		System.out.println();
		agpsUploadDone = true;
	}

	private String waitForErase() {
		final byte[] progressStr = { '-', '\\', '|', '/' };
		final int sleepPeriod = 50;
		final int progressLimit = 512;
		AndroidTranslations.getInstance().getTimeStamp();
		long eraseTimeoutTime = 0;
		int progress = 0;
		int progressIdx = 0;
		flushOutstandingCmds();
		System.out.print(progressStr[progressIdx]);
		while ((eraseTimeoutTime == 0) // 0 as long as erase did not start
				|| (getEraseOngoing() // Erase still going on
				// And no time out.
				&& (eraseTimeoutTime > System.currentTimeMillis()))) {
			if ((eraseStarted == 0) && eraseTimeoutTime == 0) {
				eraseTimeoutTime = System.currentTimeMillis() + eraseTimeoutMs;
			}

			try {
				Thread.sleep(sleepPeriod);
				progress += sleepPeriod;
				if (progress > progressLimit) {
					progress -= progressLimit;
					progressIdx++;
					progressIdx &= 0x3; // Limit to 3.
					System.out.print("\r"); // Cariage return
					System.out.print((char) progressStr[progressIdx]);
					System.out.flush();
				}
			} catch (final Exception e) {
				e.printStackTrace();
				// Do nothing
			}
		}
		if (getEraseOngoing()) {
			return "WARNING: Waiting for end of erase timed out";
		} else {
			return null;
		}
	}

	public final int convertLog(final int logType) {
		int error = 0;
		System.out.println("Input file: " + m.getStringOpt(Model.LOGFILEPATH));
		if (Model.logFiles.size() != 0) {
			for (int i = 0; i < Model.logFiles.size(); i++) {
				LogFileInfo lfi = (LogFileInfo) Model.logFiles.elementAt(i);
				System.out.println("Input file: " + lfi.getPath().getPath());
			}
		}

		System.out.println("Output directory: "
				+ m.getStringOpt(Model.OUTPUTDIRPATH));
		System.out.println("Output basename: "
				+ m.getStringOpt(Model.REPORTFILEBASE));

		if (Model.logFiles.size() != 0) {
			c.setStringOpt(Model.LOGFILEPATH, "");
		}

			error = c.doConvertLog(logType);
		return error;
	}

	private void handleOptions(final OptionSet options) {
		m.init();
		// Set up the paths
		// Common to in/out
		c.setStringOpt(Model.OUTPUTDIRPATH, ".");
		c.setOutputFileRelPath("GPSDATA");
		c.setIntOpt(Model.FILEFIELDFORMAT, 0xFFFFFFFF); // All fields
		c.setIntOpt(Model.TRKSEP, 60);
		c.setStringOpt(Model.COLOR_VALIDTRACK, "0000FF");
		c.setStringOpt(Model.COLOR_INVALIDTRACK, "0000FF");
		c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT, false);
		c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME, false);
		c.setIntOpt(AppSettings.OUTPUTFILESPLITTYPE, 0);
		c.setIntOpt(AppSettings.HEIGHT_CONVERSION_MODE, Model.HEIGHT_AUTOMATIC);

		// Next line gets arguments not related to option
		//options.nonOptionArguments();

		m.addListener(this);

		c.setChunkSize(0x00010000);

		if (options.has(OPT_DEBUG)) {
			Integer debugLevel;
			debugLevel = (Integer) (options.valueOf(OPT_DEBUG));
			switch (debugLevel) {
			case 1:
				c.setDebug(true);
				c.setDebugConn(false);
				break;
			case 2:
				c.setDebug(true);
				c.setDebugConn(true);
				break;
			default:
				c.setDebug(false);
				c.setDebugConn(false);
				break;
			}
		} else {
			c.setDebug(false);
			c.setDebugConn(false);
		}

		// Default value
		c.setStringOpt(Model.LOGFILEPATH, "BT747_log.bin");

		if (options.has(OPT_FILE_BASENAME)) {
			// Basename of files.
			final String fullname = options.argumentOf(OPT_FILE_BASENAME);
			String basename;
			int splitIdx;
			String path = "";
			splitIdx = fullname.lastIndexOf('/');
			splitIdx = Math.max(splitIdx, fullname.lastIndexOf('\\'));

			if (splitIdx > 0) {
				path = fullname.substring(0, splitIdx);
				basename = fullname.substring(splitIdx + 1);
			} else {
				path = "";
				basename = fullname;
			}
			if (path.length() != 0) {
				c.setStringOpt(Model.OUTPUTDIRPATH, path);
			}
			c.setStringOpt(Model.LOGFILEPATH, basename + ".bin");
			c.setOutputFileRelPath(basename);

		}

		// Input is "/BT747/BT747_sample.bin"
		if (options.has(OPT_BINARY_FILE)) {
			c.setStringOpt(Model.LOGFILEPATH, options
					.argumentOf(OPT_BINARY_FILE));
		}

		if (options.has(OPT_SERIAL_SPEED)) {
			c.setBaudRate((((Integer) options.valueOf(OPT_SERIAL_SPEED))
					.intValue()));
		}

		if (options.has(OPT_SERIAL_PORT)) {
			String portStr;
			portStr = (String) options.valueOf(OPT_SERIAL_PORT);
			c.setStringOpt(Model.FREETEXTPORT, portStr);
		} else {
			// c.setUsb();
		}

		if (options.has(OPT_HEIGHT)) {
			String heightOpt;
			heightOpt = ((String) options.valueOf(OPT_HEIGHT)).toUpperCase();
			if (heightOpt.equals("AUTOMATIC")) {
				c.setIntOpt(AppSettings.HEIGHT_CONVERSION_MODE,
						Model.HEIGHT_AUTOMATIC);
			} else if (heightOpt.equals("MSL_TO_WGS84")) {
				c.setIntOpt(AppSettings.HEIGHT_CONVERSION_MODE,
						Model.HEIGHT_MSL_TO_WGS84);
			} else if (heightOpt.equals("WGS84_TO_MSL")) {
				c.setIntOpt(AppSettings.HEIGHT_CONVERSION_MODE,
						Model.HEIGHT_WGS84_TO_MSL);
			} else if (heightOpt.equals("KEEP")) {
				c.setIntOpt(AppSettings.HEIGHT_CONVERSION_MODE,
						Model.HEIGHT_NOCHANGE);
			} else {
				System.err
						.println("Height parameter (" + heightOpt + "unknown");
			}
		}

		if (options.has(OPT_UTC)) {
			final Integer offset = (Integer) options.valueOf(OPT_UTC);
			// TODO: add minutes.
			c.setIntOpt(Model.GPSTIMEOFFSETQUARTERS, offset * 4 + 48);
			// Default value for filetime offset
			c.setIntOpt(Model.FILETIMEOFFSET, offset * 3600);
		}

		if (options.has(OPT_FILE_TIMEZONE)) {
			final String tz = (String) options.valueOf(OPT_FILE_TIMEZONE);
			int hour = 0;
			int minute = 0;
			int seconds = 0;
			if (tz.matches("(-?[0-9][0-9]):([0-9][0-9])")) {
				hour = Integer.valueOf(tz.substring(0, tz.length() - 4));
				minute = Integer.valueOf(tz.substring(tz.length() - 3));
			} else {
				if (tz.matches("(-?[0-9][0-9]):([0-9][0-9]):([0-9][0-9])")) {
					hour = Integer.valueOf(tz.substring(0, tz.length() - 7));
					minute = Integer.valueOf(tz.substring(tz.length() - 6, tz
							.length() - 3));
					seconds = Integer.valueOf(tz.substring(tz.length() - 3));
				}

			}
			c.setIntOpt(Model.FILETIMEOFFSET, hour * 3600 + minute * 60
					+ seconds);
		}

		if (options.has(OPT_COLOR)) {
			c.setStringOpt(Model.COLOR_VALIDTRACK, ((String) options
					.valueOf(OPT_COLOR)));
			// Default: bad color is the same
			c.setStringOpt(Model.COLOR_INVALIDTRACK, ((String) options
					.valueOf(OPT_COLOR)));
		}

		if (options.has(OPT_BADCOLOR)) {
			// Overrides previous default setting in "color"
			c.setStringOpt(Model.COLOR_INVALIDTRACK, ((String) options
					.valueOf(OPT_BADCOLOR)));
		}

		if (options.has(OPT_SPLITTYPE)) {
			final String option = ((String) options.valueOf(OPT_SPLITTYPE))
					.toUpperCase();
			/**
			 * The way we split the input track:<br>
			 * ONE_FILE = 0<br>
			 * ONE_FILE_PER_DAY = 1<br>
			 * ONE_FILE_PER_TRACK = 2
			 * 
			 * @return Current setting.
			 */
			if (option.equals("DAY")) {
				c.setIntOpt(AppSettings.OUTPUTFILESPLITTYPE, 1);
			} else if (option.equals("TRACK")) {
				c.setIntOpt(AppSettings.OUTPUTFILESPLITTYPE, 2);
			} else {
				c.setIntOpt(AppSettings.OUTPUTFILESPLITTYPE, 0);
			}
		}

		if (options.has(OPT_TIMESPLIT)) {
			final Integer split = (Integer) options.valueOf(OPT_TIMESPLIT);
			c.setIntOpt(Model.TRKSEP, split);
		}

		// Options for which a connection is needed.
		if (options.has(OPT_SERIAL_PORT) || (options.has(OPT_DOWNLOAD))
				|| options.has(OPT_LOGGING_ON_OFF)
				|| options.has(OPT_OVERLAP_STOP_SETTING)
				|| options.has(OPT_SET_LOG_CRITERIA)
				|| options.has(OPT_ERASE_MEMORY)
				|| options.has(OPT_SET_LOG_FIELDS)
				|| options.has(OPT_RECOVER_LOGGER)
				|| options.has(OPT_AGPS_CLEAR) || options.has(OPT_AGPS)
				|| options.has(OPT_AGPS_STATUS) || options.has(OPT_AGPS_URL)) {
			c.connectGPS();
		}

		if (options.has(OPT_DEVICETYPE)) {
			final String arg = options.argumentOf(OPT_DEVICETYPE).toLowerCase();
			// AppController.GPS_TYPE_DEFAULT:
			// AppController.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
			// AppController.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
			// AppController.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:

			int deviceType = BT747Constants.GPS_TYPE_DEFAULT;
			if (arg.equals("default")) {
				deviceType = BT747Constants.GPS_TYPE_DEFAULT;
			} else if (arg.equals("holux")) {
				deviceType = BT747Constants.GPS_TYPE_HOLUX_M241;
			} else if (arg.equals("holux245")) {
				deviceType = BT747Constants.GPS_TYPE_HOLUX_GR245;
			} else if (arg.equals("skytraq")) {
				deviceType = BT747Constants.GPS_TYPE_SKYTRAQ;
			}

			c.setIntOpt(Model.GPSTYPE, deviceType);
		}

		if (options.has(OPT_TRKPTINFO)) {
			c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT, true);
		}

		if (options.has(OPT_TRKPTNAME)) {
			c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME, true);
		}

		if (options.has(OPT_OVERWRITE)) {
			overwriteDownloadOk = true;
		}

		if (m.isConnected()) {
			// Connection is made.
			if (options.has(OPT_START)) {
				final String arg = options.argumentOf(OPT_START).toLowerCase();
				if (arg.equals(OPT_COLD)) {
					c.gpsCmd(MtkController.CMD_COLDSTART);
				} else if (arg.equals(OPT_WARM)) {
					c.gpsCmd(MtkController.CMD_WARMSTART);
				} else if (arg.equals(OPT_HOT)) {
					c.gpsCmd(MtkController.CMD_HOTSTART);
				} else if (arg.equals(OPT_FACTORY)) {
					c.gpsCmd(MtkController.CMD_FACTORYRESET);
				}
			}

			if (options.has(OPT_LOGGING_ON_OFF)) {
				final String arg = options.argumentOf(OPT_LOGGING_ON_OFF)
						.toLowerCase();
				if (arg.equals("on")) {
					System.out.println(">> Switch recording to ON\n");
					c.setLoggingActive(true);
				} else if (arg.equals("off")) {
					System.out.println(">> Switch recording to OFF\n");
					c.setLoggingActive(false);
				} else {
					System.err
							.println("Argument of '-l' must be 'ON' or 'OFF'");
				}
			}

			c.reqDeviceInfo();
			c.setMtkDataNeeded(MtkModel.DATA_MEM_USED);
			c.setMtkDataNeeded(MtkModel.DATA_LOG_OVERWRITE_STATUS);
			c.setMtkDataNeeded(MtkModel.DATA_LOG_VERSION);
			c.setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
			c.setMtkDataNeeded(MtkModel.DATA_LOG_TIME_INTERVAL);
			c.setMtkDataNeeded(MtkModel.DATA_LOG_SPEED_INTERVAL);
			c.setMtkDataNeeded(MtkModel.DATA_INITIAL_LOG);

			if (options.has(OPT_MACADDR)) {
				c.setMtkDataNeeded(MtkModel.DATA_BT_MAC_ADDR);
			}
			// c.req
			// c.reqMtkLogVersion();

			flushOutstandingCmds();
			System.out
					.println("MTK Firmware: Version: "
							+ m.getFirmwareVersion()
							+ ", ID(Device): "
							+ m.getModelStr()
							+ ((m.getMainVersion().length() != 0) ? (", MainVersion:" + m
									.getMainVersion())
									: ""));
			if (options.has(OPT_MACADDR)) {
				System.out.println("Bluetooth Mac Addr:" + m.getBTAddr());
			}
//			System.out.println(Utils.format(
//					"Log Conditions: Time:%.1f Distance:%.1f Speed:%d", m
//							.getLogTimeInterval() / 10., m
//							.getLogDistanceInterval() / 10., m
//							.getLogSpeedInterval()));

			// printf("Log format: (%s) %s\n", $1,
			// describe_log_format($log_format));
			// printf("Size in bytes of each log record: %u + (%u *
			// sats_in_view)\n", $size_wpt + 2, $size_sat);
			// printf("Logging TIME interval: %6.2f s\n", $1 / 10);
			// printf("Logging DISTANCE interval: %6.2f m\n", $1 / 10);
			// printf("Logging SPEED limit: %6.2f km/h\n", $1 / 10);
			// printf("Recording method on memory full: (%u) %s\n",
			// $rec_method,
			// describe_recording_method($rec_method));
			// printf("Log status: (%012b) %s\n", $log_status,
			// describe_log_status($log_status));
			// if ($log_status & $LOG_STATUS_NEED_FORMAT) {
			// printf("WARNING! Log status NEED_FORMAT, log data is not
			// valid!\n");
			// }
			// if ($log_status & $LOG_STATUS_DISABLE) {
			// printf("WARNING! Log status DISABLE_LOG, may too many failed
			// sectors!\n");
			// }
			// printf("Next write address: %u (0x%08X)\n",
			// $next_write_address,
			// $next_write_address);
			// printf("Number of records: %u\n", $expected_records_total);
			// printf("Memory health status (failed sectors mask): %s\n",
			// $fail_sectors);
			// printf(">> Retrieving %u (0x%08X) bytes of log data from
			// device...\n", $bytes_to_read, $bytes_to_read);

			if (options.has(OPT_AGPS_CLEAR)) {
				System.out.println(">> Clearing AGPS data\n");
				c.gpsCmd(MtkController.CMD_EPO_CLEAR);
			}

			String agpsUrl = null;
			if (options.has(OPT_AGPS_URL)) {
				agpsUrl = options.argumentOf(OPT_AGPS_URL);
			}

			if (options.has(OPT_AGPS) || agpsUrl != null) {
				agpsUploadDone = false;
				if (agpsUrl != null) {
					System.out.println(">> Using " + agpsUrl + "\n"
							+ " and uploading to device.");
//					c.downloadAndUploadAgpsData(agpsUrl);
				} else {
					// Default url.
//					c.downloadAndUploadAgpsData();
				}

				while (!agpsUploadDone) {
					// Thread t=Thread.currentThread();
					try {
						// System.out.println("Waiting for cmds "
						// + m.getOutstandingCommandsCount());
						// System.out.flush();
						Thread.sleep(50);
					} catch (final Exception e) {
						e.printStackTrace();
						// Do nothing
					}
				}

			}

			if (options.has(OPT_AGPS_STATUS)) {
				MtkModel mtk = m.mtkModel();
				if (!mtk.hasAgps()) {
					System.out.println("AGPS STATUS: Not supported\n");
				} else {
					System.out.println(">> Getting AGPS status\n");
					c.setMtkDataNeeded(MtkModel.DATA_AGPS_STORED_RANGE);
					flushOutstandingCmds();
					if (mtk.hasAgps()) {
//						final String text1 = Utils
//								.format(
//										"AGPS Range 1: %S blocks.  From %s to %S.",
//										mtk.getAgpsDataCount(), CommonOut
//												.getDateTimeStr(mtk
//														.getAgpsStartTime()),
//										CommonOut.getDateTimeStr(mtk
//												.getAgpsEndTime()));
//						final String text2 = Utils.format(
//								"AGPS Range 2: %s and %s (unknown meaning)",
//								CommonOut.getDateTimeStr(mtk
//										.getAgpsStart2Time()), CommonOut
//										.getDateTimeStr(mtk.getAgpsEnd2Time()));
//						System.out.println(text1);
//						System.out.println(text2);
					}
				}
			}

			if (options.has(OPT_SET_LOG_CRITERIA)) {
				final List<?> list = options.valuesOf(OPT_SET_LOG_CRITERIA);
				if (list.size() == 3) {
					System.out
							.println(">> Setting recording criteria: time, distance, speed\n");
					final int time = (Integer) list.get(0);
					final int speed = (Integer) list.get(1);
					final int distance = (Integer) list.get(2);
					System.out.println("Setting time interval to " + time);
					c.setLogTimeInterval(time * 10);
					System.out.println("Setting speed interval to " + speed);
					c.setLogSpeedInterval(speed);
					System.out.println("Setting distance interval to "
							+ distance);
					c.setLogDistanceInterval(distance * 10);
				} else {
					System.err.println("parameter for '-r' option is invalid");
				}
			}

			flushOutstandingCmds();

			if (options.has(OPT_SET_LOG_FIELDS)) {
				final List<?> list = options.valuesOf(OPT_SET_LOG_FIELDS);
				final Iterator<?> iter = list.iterator();

				int newLogFormat = m.getLogFormat();
				while (iter.hasNext()) {
					String field = ((String) iter.next()).toUpperCase();
					boolean enableField = true;
					int logField = 0;
					if (field.length() > 0) {
						if (field.charAt(0) == '-') {
							field = field.substring(1);
							enableField = false;
						}
						if (field.equals(OPT_UTC)) {
							logField = (1 << BT747Constants.FMT_UTC_IDX);
						} else if (field.equals("VALID")) {
							logField = (1 << BT747Constants.FMT_VALID_IDX);
						} else if (field.equals("LATITUDE")) {
							logField = (1 << BT747Constants.FMT_LATITUDE_IDX);
						} else if (field.equals("LONGITUDE")) {
							logField = (1 << BT747Constants.FMT_LONGITUDE_IDX);
						} else if (field.equals("HEIGHT")) {
							logField = (1 << BT747Constants.FMT_HEIGHT_IDX);
						} else if (field.equals("SPEED")) {
							logField = (1 << BT747Constants.FMT_SPEED_IDX);
						} else if (field.equals("HEADING")) {
							logField = (1 << BT747Constants.FMT_HEADING_IDX);
						} else if (field.equals("DSTA")) {
							logField = (1 << BT747Constants.FMT_DSTA_IDX);
						} else if (field.equals("DAGE")) {
							logField = (1 << BT747Constants.FMT_DAGE_IDX);
						} else if (field.equals("PDOP")) {
							logField = (1 << BT747Constants.FMT_PDOP_IDX);
						} else if (field.equals("HDOP")) {
							logField = (1 << BT747Constants.FMT_HDOP_IDX);
						} else if (field.equals("VDOP")) {
							logField = (1 << BT747Constants.FMT_VDOP_IDX);
						} else if (field.equals("NSAT")) {
							logField = (1 << BT747Constants.FMT_NSAT_IDX);
						} else if (field.equals("SID")) {
							logField = (1 << BT747Constants.FMT_SID_IDX);
						} else if (field.equals("ELEVATION")) {
							logField = (1 << BT747Constants.FMT_ELEVATION_IDX);
						} else if (field.equals("AZIMUTH")) {
							logField = (1 << BT747Constants.FMT_AZIMUTH_IDX);
						} else if (field.equals("SNR")) {
							logField = (1 << BT747Constants.FMT_SNR_IDX);
						} else if (field.equals("RCR")) {
							logField = (1 << BT747Constants.FMT_RCR_IDX);
						} else if (field.equals("MILLISECOND")) {
							logField = (1 << BT747Constants.FMT_MILLISECOND_IDX);
						} else if (field.equals("DISTANCE")) {
							logField = (1 << BT747Constants.FMT_DISTANCE_IDX);
						} else if (field.equals("VALID_ONLY")) {
							logField = (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX);
						} else {
							System.err.println("Field type" + field
									+ " unknown.");
						}
						if (logField != 0) {
							if (enableField) {
								newLogFormat |= logField;
							} else {
								newLogFormat &= 0xFFFFFFFF ^ logField;
							}

						}
					}
				}
				System.out.println(">> Setting log format\n");

				c.setLogFormat(newLogFormat);
			}

			if (options.has(OPT_OVERLAP_STOP_SETTING)) {
				final String arg = options.argumentOf(OPT_OVERLAP_STOP_SETTING)
						.toLowerCase();
				if (arg.equals("overlap")) {
					System.out
							.println(">> Setting method OVERLAP on memory full\n");
					c.setLogOverwrite(true);
				} else if (arg.equals("stop")) {
					System.out
							.println(">> Setting method STOP on memory full\n");
					c.setLogOverwrite(false);
				} else {
					System.err
							.println("Argument of '-p' must be 'STOP' or 'OVERLAP'");
				}
			}
			flushOutstandingCmds();

			System.out.println("Device reports " + m.logMemUsed()
					+ " bytes used (" + m.logMemUsedPercent() + "% of "
					+ m.logMemSize() + ").");
			System.out.println("Device is in "
					+ (m.isLogFullOverwrite() ? "OVERLAP" : "STOP") + " ("
					+ (m.isInitialLogOverwrite() ? "OVERLAP" : "STOP")
					+ " on erase or memory wrap)");

			if (options.has(OPT_DOWNLOAD)) {
				c.setDownloadMethod(Model.DOWNLOAD_SMART);
				if (options.has(OPT_DOWNLOAD_METHOD)) {
					final String arg = options.argumentOf(OPT_DOWNLOAD_METHOD)
							.toLowerCase();
					if (arg.equals("full")) {
						c.setDownloadMethod(Model.DOWNLOAD_FULL);
					} else if (arg.equals("smart")) {
						c.setDownloadMethod(Model.DOWNLOAD_SMART);
					} else if (arg.equals("reported")) {
						c.setDownloadMethod(Model.DOWNLOAD_FILLED);
					}
				}
				// printf(">> Retrieving %u (0x%08X) bytes of log data from
				// device...\n", $bytes_to_read, $bytes_to_read);
				System.out.println(">> Getting data from device");
				c.startDefaultDownload();

				downloadIsSuccessFull = false;
				while (m.isDownloadOnGoing()) {
					// Thread t=Thread.currentThread();
					try {
						// System.out.println("Waiting for cmds "
						// + m.getOutstandingCommandsCount());
						// System.out.flush();
						progressUpdate();
						Thread.sleep(50);
					} catch (final Exception e) {
						e.printStackTrace();
						// Do nothing
					}
				}

			}

			if (options.has(OPT_ERASE_MEMORY)) {
				if (downloadIsSuccessFull) {
					System.out.println(">> Erasing log memory...\n");
					c.eraseLog();
					String msg = waitForErase();
					if (msg != null) {
						System.out.println(msg);
					}
				} else {
					System.out
							.println("WARNING - Not erasing memory - download failed.\n");
				}
			}

			if (options.has(OPT_RECOVER_LOGGER)) {
				if (downloadIsSuccessFull) {
					System.out.println(">> Recover from disable log:"
							+ " ENABLE LOG and FORMAT LOG ALL...\n");
					c.recoveryEraseLog();
					waitForErase();
				} else {
					System.out
							.println("WARNING - Not recovering memory - download failed.\n");
				}
			}
			c.closeGPS();
		}

//		if (options.has(OPT_TARGET_TAGGED_FILENAME)) {
//			fpf.setDestTemplate(options.argumentOf(OPT_TARGET_TAGGED_FILENAME));
//			System.out.println("Output filename template is \""
//					+ fpf.getDestTemplate() + "\"");
//		}

		if (options.has(OPT_CREATE_GPX_TRACKS)) {
			System.out.println("Converting to GPX (trackpoints)");
			c
					.setIntOpt(
							AppSettings.TRKPT_VALID,
							(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
			c
					.setIntOpt(
							AppSettings.WAYPT_VALID,
							(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
			c.setIntOpt(AppSettings.WAYPT_RCR, 0);
			c.setIntOpt(AppSettings.TRKPT_RCR, 0xFFFFFFFF);
			// The output filename does not depend on the time.
			c.setFileNameBuilder(new BT747FileName() {
				public BT747Path getOutputFileName(final BT747Path baseName,
						final int utcTimeSeconds,
						final String proposedExtension,
						final String proposedTimeSpec) {
					BT747Time t = JavaLibBridge.getTimeInstance();
					t.setUTCTime(utcTimeSeconds);
//					String base = Conv.expandDate(baseName.getPath(), t);
String base = "acb";
					boolean addTimeSpec;
					addTimeSpec = (baseName.getPath().indexOf('%') < 0);
					switch (m.getIntOpt(AppSettings.OUTPUTFILESPLITTYPE)) {
					case 0:
						addTimeSpec &= false;
					default:
						addTimeSpec &= true;
					}

					if (!addTimeSpec) {
						return new BT747Path(base + "_trk" + proposedExtension);
					} else {
						return new BT747Path(base + proposedTimeSpec + "_trk"
								+ proposedExtension);
					}
				}
			});

			final int error = convertLog(Model.GPX_LOGTYPE);
			if (error != 0) {
				reportError(c.getLastError(), c.getLastErrorInfo());
			}
		}

		if (options.has(OPT_CREATE_GPX_WAYPOINTS)) {
			System.out.println("Converting to GPX (waypoints)");
			c
					.setIntOpt(
							AppSettings.TRKPT_VALID,
							(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
			c
					.setIntOpt(
							AppSettings.WAYPT_VALID,
							(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
			c
					.setIntOpt(
							AppSettings.WAYPT_RCR,
							(BT747Constants.RCR_BUTTON_MASK | BT747Constants.RCR_ALL_APP_MASK));
			c.setIntOpt(AppSettings.TRKPT_RCR, 0);
			c.setFileNameBuilder(new BT747FileName() {
				public BT747Path getOutputFileName(final BT747Path baseName,
						final int utcTimeSeconds,
						final String proposedExtension,
						final String proposedTimeSpec) {
					BT747Time t = JavaLibBridge.getTimeInstance();
					t.setUTCTime(utcTimeSeconds);
//					String base = Conv.expandDate(baseName.getPath(), t);
String base = "abc";
					boolean addTimeSpec;
					addTimeSpec = (baseName.getPath().indexOf('%') < 0);
					switch (m.getIntOpt(AppSettings.OUTPUTFILESPLITTYPE)) {
					case 0:
						addTimeSpec &= false;
					default:
						addTimeSpec &= true;
					}
					if (addTimeSpec) {
						return new BT747Path(base + proposedTimeSpec + "_wpt"
								+ proposedExtension);
					} else {
						return new BT747Path(base + "_wpt" + proposedExtension);
					}
				}
			});
			final int error = convertLog(Model.GPX_LOGTYPE);
			if (error != 0) {
				reportError(c.getLastError(), c.getLastErrorInfo());
			}
		}

		if (options.has(OPT_OUTPUT_TYPE)) {
			final List<?> list = options.valuesOf(OPT_OUTPUT_TYPE);
			final Iterator<?> iter = list.iterator();

			while (iter.hasNext()) {
				final String typeStr = ((String) iter.next()).toUpperCase();
				int type = Model.NO_LOG_LOGTYPE;
				if (typeStr.equals("GPX")) {
					type = Model.GPX_LOGTYPE;
				} else if (typeStr.equals("NMEA")) {
					type = Model.NMEA_LOGTYPE;
				} else if (typeStr.equals("GMAP") || typeStr.equals("HTML")) {
					type = Model.GMAP_LOGTYPE;
				} else if (typeStr.equals("CSV")) {
					type = Model.CSV_LOGTYPE;
				} else if (typeStr.equals("KML")) {
					type = Model.KML_LOGTYPE;
				} else if (typeStr.equals("KMZ")) {
					type = Model.KMZ_LOGTYPE;
				} else if (typeStr.equals("PLT")) {
					type = Model.PLT_LOGTYPE;
				} else if (typeStr.equals("TRK")) {
					type = Model.TRK_LOGTYPE;
				} else if (typeStr.equals("GMAPURL")) {
					type = Model.GOOGLE_MAP_STATIC_URL_LOGTYPE;
				} else if (typeStr.equals("SQL")) {
					type = Model.SQL_LOGTYPE;
				} else if (typeStr.equals("POSTGIS")) {
					type = Model.POSTGIS_LOGTYPE;
				} else {
					System.err.println("Unknown outtype '" + typeStr + "'");
				}
				if (type != Model.NO_LOG_LOGTYPE) {
					System.out.println("Converting to " + typeStr);
					c
							.setIntOpt(
									AppSettings.TRKPT_VALID,
									(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
					c
							.setIntOpt(
									AppSettings.WAYPT_VALID,
									(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
					c
							.setIntOpt(
									AppSettings.WAYPT_RCR,
									(BT747Constants.RCR_BUTTON_MASK | BT747Constants.RCR_ALL_APP_MASK));
					c.setIntOpt(AppSettings.TRKPT_RCR, 0xFFFFFFFF);
					c.setFileNameBuilder(new BT747FileName() {
						public BT747Path getOutputFileName(
								final BT747Path baseName,
								final int utcTimeSeconds,
								final String proposedExtension,
								final String proposedTimeSpec) {
							BT747Time t = JavaLibBridge.getTimeInstance();
							t.setUTCTime(utcTimeSeconds);
//							String base = Conv
//									.expandDate(baseName.getPath(), t);
String base = "abc";
							boolean addTimeSpec;
							addTimeSpec = (baseName.getPath().indexOf('%') < 0);
							switch (m
									.getIntOpt(AppSettings.OUTPUTFILESPLITTYPE)) {
							case 0:
								addTimeSpec &= false;
							default:
								addTimeSpec &= true;
							}

							if (!addTimeSpec) {
								return new BT747Path(base + proposedExtension);
							} else {
								return new BT747Path(base + proposedTimeSpec
										+ proposedExtension);
							}
						}
					});
					final int error = convertLog(type);
					if (error != 0) {
						reportError(c.getLastError(), c.getLastErrorInfo());
					}
				}
			}
			if (options.has(OPT_DOWNLOAD) && !(options.has(OPT_BINARY_FILE))) {
				if (!downloadIsSuccessFull) {
					System.out
							.println("\n####    DOWNLOAD FAILED !!!!!!!!!!!!!!!!!!!   ####");
					System.out
							.println("#### [Conversions used partial or wrong data] ####");
				}
			}

		}
		System.exit(0);
	}


	private final boolean getEraseOngoing() {
		synchronized (eraseOngoing) {
			return eraseOngoing != 0;
		}
	}

	private final void setEraseOngoing(final boolean isEraseOngoing) {
		synchronized (eraseOngoing) {
			eraseStarted = 1;
			eraseOngoing = isEraseOngoing ? 1 : 0;
		}
	}

	// private void initAppSettings() {
	// // Model.defaultBaseDirPath = java.lang.System
	// // .getProperty("user.home");
	//
	// try {
	// Model.defaultBaseDirPath = (new File(".")).getCanonicalPath();
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// Settings.setAppSettings(new String(new byte[AppSettings.SIZE]));
	// m.init();
	// }

}