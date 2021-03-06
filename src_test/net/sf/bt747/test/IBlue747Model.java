// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package net.sf.bt747.test;

import gps.BT747Constants;
import gps.connection.DecoderStateFactory;
import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.connection.NMEAWriter;
import gps.convert.Conv;
import gps.log.GPSRecord;
import gps.log.out.GPSNMEAFile;
import gps.mvc.commands.GpsLinkExecCommand;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;
import net.sf.bt747.test.models.mtk.commands.Acknowledge;
import net.sf.bt747.test.models.mtk.commands.EpoReply;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * Implement a model of the BT747 (to run on PC).
 * 
 * @author Mario De Weerd
 */
public class IBlue747Model {

	private static GPSPort modelPort;

	public final static void setGpsPort(final GPSPort port) {
		IBlue747Model.modelPort = port;
	}

	public enum DeviceModelType {
		ML7, QST1300, IBLUE747PLUS, PHOTOMATE887, QST1000,
		QST1000X, M241, IBLUE821, IBLUE747, HOLUXM1000C,
		MBT1100, IBLUE747PLUS_TYPE2, BTCD110m, TSI_887_Lite,
		HOLUX_GR260
	};

	/**
	 * The default model type used.
	 */
	public static DeviceModelType defaultModelType =
	DeviceModelType.IBLUE747PLUS;
	// DeviceModelType.IBLUE747PLUS_TYPE2;
	// DeviceModelType.TSI_887_Lite;
	//DeviceModelType.HOLUX_GR260;

	public GPSrxtx gpsRxTx = null;

	private HlxController hlxController = new HlxController(this);
	private BTCD110mController cd110Controller = new BTCD110mController(this);

	private byte[] logData = null;
    private static final String DEFAULT_LOG_FILE = "files/iBlue747_allfields.bin";
    
	private static String logFile = getResourcePath(DEFAULT_LOG_FILE);


    private final static String getResourcePath(String rsc) {
        return IBlue747Model.class.getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

	/**
	 * Set up system specific classes.
	 */
	static {
		// Set up the low level functions interface.
		JavaLibBridge
				.setJavaLibImplementation(net.sf.bt747.j2se.system.J2SEJavaTranslations
						.getInstance());
		// Set the serial port class instance to use (also system specific).
		if (!GPSrxtx.hasDefaultPortInstance()) {
			GPSrxtx.setDefaultGpsPortInstance(new gps.connection.GPSRxTxPort());
		}
		//logFile = "C:\\BT747\\20090629_747A+GNB.bin";
	}

	/**
     * 
     */
	public IBlue747Model() {

		// setupModel(DeviceModelType.HOLUXM1000C);
		setupModel(defaultModelType);
		// setupModel(DeviceModelType.MBT1100);
	}

	/**
	 * Main entry code of class.
	 * 
	 * Should be called after instantiation.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * 
	 */
	public void onStart() throws FileNotFoundException, IOException {
		if (IBlue747Model.modelPort != null) {
			gpsRxTx = new GPSrxtx(IBlue747Model.modelPort);
		} else {
			gpsRxTx = new GPSrxtx();
		}

		gpsRxTx.setDefaults(20, 115200);
		gpsRxTx.openPort();
		gpsRxTx.newState(new TextOrNMEADecoderState());
		gpsTimerTask.setGpsRxTx(gpsRxTx);
		// addTimer(10); // Palm minimum timer resolution= 10 ms

		try {
			getLogData();
		} catch (Exception e) {
			Generic.debug("Issue getting virtual log data", e);
		}

		TimerTask t;
		t = new TimerTask() {

			@Override
			public void run() {
				try {
					if (gpsRxTx.isConnected()) { // gpsRxTx.getGpsPort().readCheck()
						final Object lastResponse = gpsRxTx.getResponse();
						if (lastResponse != null) {
							if (lastResponse instanceof String[]) {
								final String[] resp = (String[]) lastResponse;
								final StringBuffer sb = new StringBuffer(255);
								for (String s : resp) {
									sb.append(s);
									sb.append(',');
								}
								Generic
										.debug("Model received:"
												+ sb.toString());
							} else {
								Generic.debug("Model received:" + lastResponse);
							}

							analyseResponse(lastResponse);
						}
					} else {
						gpsRxTx.setFreeTextPortAndOpen("COM20");
					}
				} catch (final Exception e) {
					Generic.debug("Model run loop problem", e);
				}
			}
		};
		final Timer tm = new Timer();
		tm.scheduleAtFixedRate(t, 100, 100);

		final Timer gpsTm = new Timer();
		gpsTm.scheduleAtFixedRate(gpsTimerTask, 1000, 1000);
	}

	private GpsTimerTask gpsTimerTask = new GpsTimerTask();

	private class GpsTimerTask extends TimerTask {
		GPSRecord r = GPSRecord.getLogFormatRecord(0);
		GPSrxtx gpsRxTx = null;
		boolean sendPosition = true;

		public void setGpsRxTx(final GPSrxtx gpsRxTx) {
			this.gpsRxTx = gpsRxTx;
		}

		@SuppressWarnings("unused")
		public void setSendGpsPosition(final boolean sendPosition) {
			this.sendPosition = sendPosition;
		}

		/**
         * 
         */
		public GpsTimerTask() {
			r.setLatitude(51.0);
			r.setLongitude(4.0);
			r.setHdop(98);
			r.setVdop(99);
			r.setPdop(120);
			r.setNsat((10 << 8) | 10);
			r.setEle(new int[] { 1, 2 });
			r.setAzi(new int[] { 1, 2 });
			r.setSid(new int[] { 1, 2 });
			r.setSnr(new int[] { 5, 1 });
			r.setGeoid(42);
			r.setHeading(50);
			r.setMilisecond(0);
			r.setSpeed(10);
			r.setValid(BT747Constants.VALID_SPS_MASK);
			r.setUtc(JavaLibBridge.getDateInstance().dateToUTCepoch1970());
		}

		private final void sendPacketWithChecksum(final String n) {
			if (gpsRxTx != null) {
				NMEAWriter.sendPacket(gpsRxTx, n);
			}
		}

		volatile int pauseTime = 0;

	    @SuppressWarnings("unused")
		public void pause(int timems) {
			pauseTime = timems;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			synchronized (r) {

				int timepassed = 1000;
				r.milisecond += timepassed % 1000;
				r.utc += r.milisecond / 1000;
				r.latitude += 0.00001;
				r.longitude += 0.00001;
				pauseTime -= timepassed;
				if (pauseTime < 0) {
					pauseTime = 0;
					updatePeriods(mtkData);
					if (sendPosition
							&& (mtkData.deviceMode == DeviceMode.DEVICE_MODE_NMEA)) {
						if (mtkData.currentPeriods[BT747Constants.NMEA_SEN_GGA_IDX] == 1) {
							sendPacketWithChecksum(GPSNMEAFile.toGGA(r));
						}
						if (mtkData.currentPeriods[BT747Constants.NMEA_SEN_RMC_IDX] == 1) {
							sendPacketWithChecksum(GPSNMEAFile.toRMC(r));
						}
					}
				}

			}
		}
	};

	/**
	 * Possible communication modes the device is in.
	 * 
	 * @author Mario
	 * 
	 */
	public static enum DeviceMode {
		DEVICE_MODE_NMEA, DEVICE_MODE_MTKBIN, DEVICE_MODE_WP, DEVICE_MODE_SIRFIII
	};

	/**
	 * Set the communication mode of the device. The device can communicate in
	 * NMEA mode or MTKBIN mode.
	 * 
	 * @param mode
	 */
	public final void setDeviceMode(final DeviceMode mode) {
		if (mtkData.deviceMode != mode) {
			mtkData.deviceMode = mode;
			switch (mode) {
			case DEVICE_MODE_NMEA:
				gpsRxTx.newState(new TextOrNMEADecoderState());
				break;
			case DEVICE_MODE_MTKBIN:
				gpsRxTx.newState(DecoderStateFactory.MTKBIN_STATE);
				break;
			case DEVICE_MODE_WP:
				gpsRxTx.newState(new WPDeviceDecoderState());
				break;
			case DEVICE_MODE_SIRFIII:
				gpsRxTx.newState(DecoderStateFactory.SIRFIII_STATE);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Analyzes data receive on the serial link. The data is already in a
	 * specific format.
	 * 
	 * @param response
	 */
	private final void analyseResponse(final Object response) {
		if (response instanceof MtkBinTransportMessageModel) {
			analyseMtkBinData((MtkBinTransportMessageModel) response);
		}
		if (cd110Controller.analyseResponse(response) == 0) {
		} else if (response instanceof String[]) {
			analyseNMEA((String[]) response);
		}
	}

	private GpsLinkExecCommand previousEPOReply = null;

	/**
	 * Analyze and respond to binary MTK data.
	 * 
	 * @param msg
	 */
	private final void analyseMtkBinData(final MtkBinTransportMessageModel msg) {
		GpsLinkExecCommand reply = null;
		switch (msg.getType()) {
		case BT747Constants.PMTK_SET_EPO_DATA:
			Generic.debug("Model received AGPS DATA" + msg.toString());
			final EpoReply r = new EpoReply(msg);
			reply = new EpoReply(msg);
			if (r.getPacketNbr() == 0x101) {
				// Reply as some devices do ...
				previousEPOReply.execute(gpsRxTx);
			}
			break;
		case BT747Constants.PMTK_SET_BIN_MODE:
			Generic.debug("Model received SET BIN MODE" + msg.toString());
			// TODO: should look at payload too .
			setDeviceMode(DeviceMode.DEVICE_MODE_NMEA);
			break;
		default:

		}
		if (reply != null) {
			reply.execute(gpsRxTx);
			previousEPOReply = reply;
		}
	}

	public static class MtkDataModel {
		protected int logStatus = 2; // 0x104;
		private String coreVersion = "";
		/**
		 * The curent communication mode of the device.
		 */
		public DeviceMode deviceMode = DeviceMode.DEVICE_MODE_NMEA;
		public int flashCode = 0xC22015C2;
		public int flashStatus = 1;
		/**
		 * The logger's format.
		 */
		public int logFormat = 0x000215FF;
		public int logPoints = 0x231;
		public int memUsed = 0x00019D0;
		public DeviceModelType modelType = DeviceModelType.IBLUE747PLUS;
		public String modelNumber = "";
		public String modelRef = null;
		public String swVersion = "1.0";
		public int logVersion = 139;
		public String mainVersion = null;
		public String releaseNumber = null;
		public int logTimeInterval = 0;
		public int logDistanceInterval = 100;
		public int logSpeedInterval = 50;
		public int recMethod = 2; // (2) Stop or (1) overwrite

		public int fixPeriod = 200;

		public int[] nmeaPeriods = new int[19];
		public int[] currentPeriods = new int[19];
	}

	private static void initNmeaPeriod(MtkDataModel md) {
		md.nmeaPeriods[BT747Constants.NMEA_SEN_GLL_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_RMC_IDX] = 1;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_VTG_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_GGA_IDX] = 1;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_GSA_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_GSV_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_GRS_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_GST_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_TYPE8_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_TYPE9_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_TYPE10_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_TYPE11_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_TYPE12_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_MALM_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_MEPH_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_MDGP_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_MDBG_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_ZDA_IDX] = 0;
		md.nmeaPeriods[BT747Constants.NMEA_SEN_MCHN_IDX] = 0;
	}

	private static final void updatePeriods(MtkDataModel md) {
		for (int i = 0; i < md.nmeaPeriods.length; i++) {
			md.currentPeriods[i] += 1;
			md.currentPeriods[i] %= (md.nmeaPeriods[i] + 1);
		}
	}

	public final MtkDataModel mtkData = new MtkDataModel();

	public void replyMTK_Ack(final String[] p_nmea) {
		switch (mtkData.deviceMode) {
		case DEVICE_MODE_NMEA:
			try {
				sendPacket("PMTK" + BT747Constants.PMTK_ACK_STR + ","
						+ p_nmea[0].substring(4) + ","
						+ BT747Constants.PMTK_ACK_SUCCEEDED);
			} catch (final Exception e) {
				Generic.debug("Send failed ", e);
			}
		case DEVICE_MODE_MTKBIN:

		case DEVICE_MODE_WP:
		default:
			break;
		}
	}

	public void replyMTK_Log_Ack(final String[] p_nmea, int p_ack_type) {
		try {
			sendPacket("PMTK" + BT747Constants.PMTK_ACK_STR + ","
					+ BT747Constants.PMTK_CMD_LOG + "," + p_nmea[1] + ","
					+ BT747Constants.PMTK_ACK_SUCCEEDED);
		} catch (final Exception e) {
			Generic.debug("Send failed ", e);
		}
	}

	// PMTK182,3,7,2 seems to interrupt the log transfer ...

	/**
	 * Respond to log specific functionality (PMTK182).
	 * 
	 * @param p_nmea
	 * @return 0 if all went ok.
	 */
	public int replyLogNmea(final String[] p_nmea) {
		int ack = BT747Constants.PMTK_ACK_SUCCEEDED;
		try {
			switch (JavaLibBridge.toInt(p_nmea[1])) {
			case BT747Constants.PMTK_LOG_SET:
				final int z_setType = JavaLibBridge.toInt(p_nmea[2]);
				if (p_nmea.length >= 3) {
					switch (z_setType) {
					case BT747Constants.PMTK_LOG_FORMAT:
						infoMsg("Set log format to " + p_nmea[3]);
						mtkData.logFormat = Conv.hex2Int(p_nmea[3]);
						break;
					case BT747Constants.PMTK_LOG_TIME_INTERVAL: // 3;
						infoMsg("Set log time interval to " + p_nmea[3]);
						mtkData.logTimeInterval = JavaLibBridge
								.toInt(p_nmea[3]);
						break;
					case BT747Constants.PMTK_LOG_DISTANCE_INTERVAL: // 4;
						infoMsg("Set log distance interval to " + p_nmea[3]);
						mtkData.logDistanceInterval = JavaLibBridge
								.toInt(p_nmea[3]);
						break;
					case BT747Constants.PMTK_LOG_SPEED_INTERVAL: // 5;
						infoMsg("Set log speed interval to " + p_nmea[3]);
						mtkData.logSpeedInterval = JavaLibBridge
								.toInt(p_nmea[3]);
						break;
					case BT747Constants.PMTK_LOG_REC_METHOD: // 5;
						infoMsg("Set log recording method to " + p_nmea[3]);
						mtkData.logSpeedInterval = JavaLibBridge
								.toInt(p_nmea[3]);
						break;
					default:
						break;
					}
				}
				break;
			case BT747Constants.PMTK_LOG_Q:
				// Parameter information
				// TYPE = Parameter type
				// DATA = Parameter data
				// $PMTK182,3,TYPE,DATA
				final int z_type = JavaLibBridge.toInt(p_nmea[2]);
				if (p_nmea.length >= 3) {
					switch (z_type) {
					case BT747Constants.PMTK_LOG_FLASH_STAT:
						infoMsg("Get Flash Status");
						sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
								+ BT747Constants.PMTK_LOG_DT + "," + p_nmea[2]
								+ "," + mtkData.flashStatus // Address
						);
						break;
					case BT747Constants.PMTK_LOG_FORMAT: // 2;
						// if(GPS_DEBUG) {
						// waba.sys.Vm.debug("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
						infoMsg("Get Log Format");
						sendPacket("PMTK"
								+ BT747Constants.PMTK_CMD_LOG
								+ ","
								+ BT747Constants.PMTK_LOG_DT
								+ ","
								+ p_nmea[2]
								+ ","
								+ JavaLibBridge.unsigned2hex(mtkData.logFormat,
										mtkData.logFormat <= 0xFF ? 2 : 3) // Address
						);
						break;
					case BT747Constants.PMTK_LOG_TIME_INTERVAL: // 3;
						infoMsg("Get Log Time Interval");
						sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
								+ BT747Constants.PMTK_LOG_DT + ","
								+ BT747Constants.PMTK_LOG_TIME_INTERVAL + ","
								+ mtkData.logTimeInterval);

						break;
					case BT747Constants.PMTK_LOG_DISTANCE_INTERVAL: // 4;
						// public int logTimeInterval = 0;
						// public int logDistanceInterval = 100;
						// public int logSpeedInterval = 50;
						infoMsg("Get Log Distance Interval");
						sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
								+ BT747Constants.PMTK_LOG_DT + ","
								+ BT747Constants.PMTK_LOG_DISTANCE_INTERVAL
								+ "," + mtkData.logDistanceInterval);
						break;
					case BT747Constants.PMTK_LOG_SPEED_INTERVAL: // 5;
						infoMsg("Get Log Speed Method");
						sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
								+ BT747Constants.PMTK_LOG_DT + ","
								+ BT747Constants.PMTK_LOG_SPEED_INTERVAL + ","
								+ mtkData.logSpeedInterval);
						break;
					case BT747Constants.PMTK_LOG_REC_METHOD: // 6;
						infoMsg("Get Log Record Method");
						sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
								+ BT747Constants.PMTK_LOG_DT + ","
								+ BT747Constants.PMTK_LOG_REC_METHOD + ","
								+ mtkData.recMethod);
						break;
					case BT747Constants.PMTK_LOG_LOG_STATUS: // 7; // bit 2
						infoMsg("Get Log Status");
						// =
						sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
								+ BT747Constants.PMTK_LOG_DT + ","
								+ BT747Constants.PMTK_LOG_LOG_STATUS + ","
								+ mtkData.logStatus); // 9F
						// logging
						// on/off
						break;
					case BT747Constants.PMTK_LOG_MEM_USED: // 8;
						infoMsg("Get Log Mem Used");
						sendPacket("PMTK"
								+ BT747Constants.PMTK_CMD_LOG
								+ ","
								+ BT747Constants.PMTK_LOG_DT
								+ ","
								+ BT747Constants.PMTK_LOG_MEM_USED
								+ ","
								+ JavaLibBridge
										.unsigned2hex(mtkData.memUsed, 8));
						break;
					case BT747Constants.PMTK_LOG_FLASH: // 9;a
						infoMsg("Get Log Flash");
						sendPacket("PMTK"
								+ BT747Constants.PMTK_CMD_LOG
								+ ","
								+ BT747Constants.PMTK_LOG_DT
								+ ","
								+ BT747Constants.PMTK_LOG_FLASH
								+ ","
								+ JavaLibBridge.unsigned2hex(mtkData.flashCode,
										8)); // PMTK182,3,9,C22015C2

						break;
					case BT747Constants.PMTK_LOG_NBR_LOG_PTS: // 10;
						infoMsg("Get Log Nbr Points");
						sendPacket("PMTK"
								+ BT747Constants.PMTK_CMD_LOG
								+ ","
								+ BT747Constants.PMTK_LOG_DT
								+ ","
								+ BT747Constants.PMTK_LOG_NBR_LOG_PTS
								+ ","
								+ JavaLibBridge.unsigned2hex(mtkData.logPoints,
										8));
						break;
					case BT747Constants.PMTK_LOG_FLASH_SECTORS: // 11;
						break;

					case BT747Constants.PMTK_LOG_VERSION:
						infoMsg("Get Log Version");
						sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
								+ BT747Constants.PMTK_LOG_DT + ","
								+ BT747Constants.PMTK_LOG_VERSION + ","
								+ mtkData.logVersion);

					default:
					}
				}
				break;
			case BT747Constants.PMTK_LOG_Q_LOG:
				infoMsg("Get Log Data");
				// Send data from the log
				// $PMTK182,7,START_ADDRESS,DATA
				getLogData();
				int address = Conv.hex2Int(p_nmea[2]);
				final StringBuffer s = new StringBuffer(
						Conv.hex2Int(p_nmea[3]) * 2);
				for (int length = Conv.hex2Int(p_nmea[3]); length > 0;) {
					int payload;
					if (length <= 0x800) {
						payload = length;
					} else {
						payload = 0x800;
					}
					length -= payload;
					s.setLength(0);
					int cur_addr = address;
					for (; (cur_addr < logData.length)
							&& (cur_addr < address + payload); cur_addr++) {
						s.append(JavaLibBridge.unsigned2hex(logData[cur_addr],
								2));
					}
					while (cur_addr++ < address + payload) {
						s.append("FF");
					}
					System.err.println("PMTK" + BT747Constants.PMTK_CMD_LOG_STR
							+ "," + BT747Constants.PMTK_LOG_DT_LOG + ","
							+ JavaLibBridge.unsigned2hex(address, 8) // Address
					// + "," + s
							);
					sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
							+ BT747Constants.PMTK_LOG_DT_LOG + ","
							+ JavaLibBridge.unsigned2hex(address, 8) // Address
							+ "," + s);
					address += payload;
					if (((length & 1) != 0) && (address >= logData.length)) {
						break;
					}
				}
				break;
			case BT747Constants.PMTK_LOG_ON:
				infoMsg("Set Log On");
				mtkData.logStatus |= BT747Constants.PMTK_LOG_STATUS_LOGONOF_MASK;
				break;
			case BT747Constants.PMTK_LOG_OFF:
				infoMsg("Set Log Off");
				if ((mtkData.logStatus & BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK) != 0) {
					sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
							+ BT747Constants.PMTK_LOG_DT + ","
							+ BT747Constants.PMTK_LOG_FLASH_STAT + "," + "3");
					ack = BT747Constants.PMTK_ACK_FAILED;
				} else {
					mtkData.logStatus &= ~BT747Constants.PMTK_LOG_STATUS_LOGONOF_MASK;
				}
				break;
			case BT747Constants.PMTK_LOG_DISABLE:
				infoMsg("Set Log Disable");
				mtkData.logStatus &= ~BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK;
				mtkData.logStatus |= ~BT747Constants.PMTK_LOG_STATUS_LOGENABLED_MASK;
				break;
			case BT747Constants.PMTK_LOG_ENABLE:
				infoMsg("Set Log Enable");
				mtkData.logStatus |= BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK;
				mtkData.logStatus &= ~BT747Constants.PMTK_LOG_STATUS_LOGENABLED_MASK;
				break;

			default:
				// Nothing - unexpected
			}
		} catch (final Exception e) {
			// TODO: handle exception
		}
		replyMTK_Log_Ack(p_nmea, ack);
		return 0; // Done.

	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void getLogData() throws FileNotFoundException, IOException {
		if (logData == null) {
			final File f = new File(IBlue747Model.logFile);
			final FileInputStream fi = new FileInputStream(f);
			logData = new byte[fi.available()];
			fi.read(logData);
			fi.close();
			int i;
			for (i = logData.length - 1; i > 0 && ((logData[i] & 0xFF) == 0xFF); i--)
				;
			mtkData.memUsed = i + 1;
		}
	}

	public int analyseNMEA(final String[] p_nmea) {
		int z_Cmd;
		int z_Result = 0;
		// if(GPS_DEBUG) {
		// waba.sys.Vm.debug("ANA:"+p_nmea[0]+","+p_nmea[1]+"\n");}
		final StringBuffer nmea = new StringBuffer();
		String response = null;
		Acknowledge acknowledge = null;
		for (final String s : p_nmea) {
			nmea.append(s);
			nmea.append(',');
		}

		Generic.debug(nmea.toString());

		try {
			if (p_nmea[0].startsWith("PMTK")) {
				z_Cmd = JavaLibBridge.toInt(p_nmea[0].substring(4));

				if (z_Cmd != BT747Constants.PMTK_CMD_LOG) {
					acknowledge = new Acknowledge(p_nmea);
				}

				z_Result = -1; // Suppose cmd not treated
				switch (z_Cmd) {
				case BT747Constants.PMTK_CMD_LOG: // CMD 182;
					z_Result = replyLogNmea(p_nmea);
					break;
				case BT747Constants.PMTK_TEST: // CMD 000
				case BT747Constants.PMTK_ACK: // CMD 001
					// Device does not reply with this
					break;
				case BT747Constants.PMTK_SYS_MSG: // CMD 010
				case BT747Constants.PMTK_CMD_HOT_START: // CMD 101
				case BT747Constants.PMTK_CMD_WARM_START: // CMD 102
				case BT747Constants.PMTK_CMD_COLD_START: // CMD 103
				case BT747Constants.PMTK_CMD_FULL_COLD_START: // CMD 104
				case BT747Constants.PMTK_SET_NMEA_BAUD_RATE: // CMD 251
					break;
				case BT747Constants.PMTK_SET_BIN_MODE: // CMD 253
					infoMsg("Enter binary mode");
					setDeviceMode(DeviceMode.DEVICE_MODE_MTKBIN);
					break;
				case BT747Constants.PMTK_API_SET_FIX_CTL: // CMD 300
					infoMsg("Set fix to " + p_nmea[3]);
					mtkData.fixPeriod = JavaLibBridge.toInt(p_nmea[3]);
					break;
				case BT747Constants.PMTK_API_SET_DGPS_MODE: // CMD 301
				case BT747Constants.PMTK_API_SET_SBAS: // CMD 313
					break;
				case BT747Constants.PMTK_API_SET_NMEA_OUTPUT: // CMD 314
					z_Result = 0;
					try {
						if (Integer.valueOf(p_nmea[1]) == -1) {
							initNmeaPeriod(mtkData);
						} else {
							for (int i = 1; i < p_nmea.length; i++) {
								// TODO: check if value in range.
								mtkData.nmeaPeriods[i - 1] = Integer
										.valueOf(p_nmea[i]);
							}
						}
					} catch (Exception e) {
						Generic.debug("Problem with " + p_nmea[0], e);
					}
					break;
				case BT747Constants.PMTK_API_SET_PWR_SAV_MODE: // CMD 320
				case BT747Constants.PMTK_API_SET_DATUM: // CMD 330
				case BT747Constants.PMTK_API_SET_DATUM_ADVANCE: // CMD 331
				case BT747Constants.PMTK_API_SET_USER_OPTION: // CMD 390
					break;
				case BT747Constants.PMTK_API_Q_FIX_CTL: // CMD 400
					response = "PMTK500," + mtkData.fixPeriod;
					break;
				case BT747Constants.PMTK_API_Q_DGPS_MODE: // CMD 401
					break;
				case BT747Constants.PMTK_API_Q_SBAS: // CMD 413
					break;
				case BT747Constants.PMTK_API_Q_NMEA_OUTPUT: // CMD 414
					StringBuffer nmeaOutResp = new StringBuffer();
					nmeaOutResp.append("PMTK514");
					for (int i = 0; i < mtkData.nmeaPeriods.length; i++) {
						nmeaOutResp.append(',');
						nmeaOutResp.append(mtkData.nmeaPeriods[i]);
					}
					response = nmeaOutResp.toString();
					break;
				case BT747Constants.PMTK_API_Q_PWR_SAV_MOD: // CMD 420
				case BT747Constants.PMTK_API_Q_DATUM: // CMD 430
				case BT747Constants.PMTK_API_Q_DATUM_ADVANCE: // CMD 431
					break;
				case BT747Constants.PMTK_API_Q_GET_USER_OPTION: // CMD 490
					response = "PMTK590,0,1,115200,0,1,0,1,1,1,0,0,0,2,9600";
					break;
				// case BT747_dev.PMTK_DT_FIX_CTL: // CMD 500
				// case BT747_dev.PMTK_DT_DGPS_MODE: // CMD 501
				// case BT747_dev.PMTK_DT_SBAS: // CMD 513
				// case BT747Constants.PMTK_DT_NMEA_OUTPUT: // CMD 514
				// case BT747_dev.PMTK_DT_PWR_SAV_MODE: // CMD 520
				// case BT747_dev.PMTK_DT_DATUM: // CMD 530
				// case BT747Constants.PMTK_DT_FLASH_USER_OPTION: // CMD 590
				// break;
				case BT747Constants.PMTK_Q_RELEASE: // CMD 605
					// m_sendPacket("PMTK" +
					// BT747Constants.PMTK_DT_RELEASE
					// + "," + "AXN_1.0-B_1.3_C01" + "," + "0001" + ","
					// + "TSI_747A+" + "," + "1.0");
					if (mtkData.modelRef != null) {
						response = "PMTK" + BT747Constants.PMTK_DT_RELEASE
								+ "," + mtkData.coreVersion + ","
								+ mtkData.modelNumber + "," + mtkData.modelRef
								+ "," + mtkData.swVersion;
					}
					switch (mtkData.modelType) {
					case TSI_887_Lite:
						acknowledge = null;
					default:
						break;
					}
					break;
				case BT747Constants.PMTK_Q_VERSION: // 604
					if (mtkData.mainVersion != null) {
						response = "PMTK" + BT747Constants.PMTK_DT_VERSION
								+ "," + mtkData.mainVersion + ","
								+ mtkData.releaseNumber + ","
								+ mtkData.modelRef;
					} else {
						acknowledge = new Acknowledge(p_nmea,
								BT747Constants.PMTK_ACK_UNSUPPORTED);
					}
					// PMTK704,FWVrsn1, FWVrsn2, FWVrsn3
					// Vrsn: MainVersion_ReleaseNumber
					// Example:
					// $PMTK704,1.881_06,0606_m0138,0000*52<CR><LF>
					break;
				case BT747Constants.PMTK_Q_EPO_INFO:
					response = "PMTK707,28,1511,518400,1512,496800,1511,540000,1511,540000";
					break;
				case BT747Constants.PMTK_SET_EPO_DATA: // CMD 722
					break;
				default:
					acknowledge = new Acknowledge(p_nmea,
							BT747Constants.PMTK_ACK_UNSUPPORTED);
					System.err.println("Not supported in model:" + z_Cmd);
				} // End switch
			} else if (p_nmea[0].startsWith("PTSI")) {
				z_Cmd = JavaLibBridge.toInt(p_nmea[0].substring(4));

				replyMTK_Ack(p_nmea);

				switch (z_Cmd) {
				case 000:
					if (p_nmea[1].equals("TSI")) {
						response = "PTSI001," + mtkData.modelRef;
					}
					break;
				case 2: {
					int opt = JavaLibBridge.toInt(p_nmea[1]);
					switch (opt) {
					case 2:

						break;

					default:
						break;
					}
				}
					break;
				case 11:
					break;
				case 999:
					if (p_nmea[1].equals("IAMAP")) {
						response = "PTSI999,IAMAP";
					}
					break;

				default:
					break;
				}
			} else if (hlxController.handles(p_nmea[0])) {
				// Delegate to holux controller. The MtkModel will respond too
				// so
				// we do not care about the result.
				hlxController.analyseNMEA(p_nmea); // End if
			}
		} catch (Exception e) {
			// TODO: handle exception
			Generic.debug("Problem in reception", e);
		}
		if (response != null) {
			if (response.length() != 0) {
				sendPacket(response);
				if (z_Result == -1) {
					z_Result = 0;
				}
			}
		}
		if (acknowledge != null) {
			acknowledge.execute(gpsRxTx);
		}
		if (z_Result < 0) {
			Generic.debug("No response from MTK model to " + nmea.toString());
		}
		return z_Result;
	} // End method

	private final void infoMsg(final String msg) {
		System.err.println("[INFO " + msg + "]");
	}

	private final void setupModel(final DeviceModelType modelType) {
		mtkData.modelType = modelType;

		/* Default values */
		mtkData.flashCode = 0xC22015C2;
		mtkData.memUsed = 0x00119D0;
		mtkData.logPoints = 0x231;
		mtkData.coreVersion = "M-core_1.8";
		mtkData.modelNumber = "0011";
		mtkData.modelRef = "";
		mtkData.swVersion = "";
		initNmeaPeriod(mtkData);

		/* Specific values */
		switch (modelType) {//
		case IBLUE747:
			mtkData.coreVersion = "M-core_1.8";
			mtkData.modelNumber = "0011";
			mtkData.modelRef = "";
			mtkData.swVersion = "";
			break;
		case IBLUE747PLUS:
			mtkData.coreVersion = "AXN_1.0-B_1.3_C01";
			mtkData.modelNumber = "0006";
			mtkData.modelRef = "TSI_747A+";
			mtkData.swVersion = "1.0";
			mtkData.logVersion = 139;
			mtkData.flashCode = 0x1C20161C;
			mtkData.flashCode = 0x1C30161C;
			break;
		case IBLUE747PLUS_TYPE2:
			mtkData.coreVersion = "AXN_1.0-B_1.3_C01";
			mtkData.modelNumber = "000F";
			mtkData.modelRef = "TSI_747A+";
			mtkData.swVersion = "1.0";
			mtkData.logVersion = 139;
			mtkData.flashCode = 0x1C31161C;
			mtkData.memUsed = 3 * 1024 * 1024; // 0x00019D0;
			break;
		case ML7:
			mtkData.coreVersion = "M-core_2.02";
			mtkData.modelNumber = "231B";
			mtkData.modelRef = "";
			mtkData.swVersion = "1.0";
			break;
		case HOLUXM1000C:
			// PMTK705,AXN_1.0-B_1.3_C01,0035,01029-00A,1.0,
			mtkData.coreVersion = "AXN_1.0-B_1.3_C01";
			mtkData.modelNumber = "0035";
			mtkData.modelRef = "01029-00A";
			mtkData.swVersion = "1.0";
			break;
		case PHOTOMATE887:
			mtkData.coreVersion = "AXN_1.0-B_1.3_C01";
			mtkData.modelNumber = "0001";
			mtkData.modelRef = "TSI_887";
			mtkData.swVersion = "1.0";
			break;
		case IBLUE821:
			mtkData.coreVersion = "B-core_1.1";
			mtkData.modelNumber = "0001";
			mtkData.modelRef = "TSI_821";
			mtkData.swVersion = "1.0";
			break;
		case QST1000:
			mtkData.coreVersion = "M-core_1.94";
			mtkData.modelNumber = "001B";
			mtkData.modelRef = "";
			mtkData.swVersion = "";
			break;
		case QST1000X:
			mtkData.coreVersion = "AXN_1.0-B_1.3_C01";
			mtkData.modelNumber = "0001";
			mtkData.modelRef = "QST1000";
			mtkData.swVersion = "1.0";
			break;
		case MBT1100:
			mtkData.coreVersion = "M-core_1.94"; // Not sure
			mtkData.modelNumber = "000A";
			mtkData.modelRef = "MBT-1100";
			mtkData.swVersion = "1.0"; // Not confirmed
			mtkData.logVersion = 139;
			mtkData.flashCode = 0x20201710;
			break;
		case TSI_887_Lite:
			mtkData.coreVersion = "AXN_1.30-B_1.3_C01";
			mtkData.modelNumber = "0003";
			mtkData.modelRef = "TSI_887Lite";
			mtkData.swVersion = "1.0";
			mtkData.flashCode = 0x1C30161C;
			logFile = "C:\\Users\\mdeweerd\\Downloads\\887Test.bin";
			break;
		case HOLUX_GR260:
			mtkData.coreVersion = "";
			mtkData.modelNumber = "";
			mtkData.swVersion = "1.03";
			mtkData.modelRef = "GR260";
			mtkData.flashCode = 0x00000000;
			break;
		case QST1300:
		default:
			mtkData.coreVersion = "AXN_1.0-B_1.3_C01";
			mtkData.modelNumber = "8805";
			mtkData.modelRef = "QST1300";
			mtkData.swVersion = "1.0";

			// AXN_0.3-B_1.3_C01
			break;
		}

		switch (modelType) {
		case IBLUE747:
		case IBLUE821:
		case IBLUE747PLUS:
		case M241:
		case ML7:
		case PHOTOMATE887:
		case QST1000:
		case QST1000X:
		case QST1300:
		default:
			break;

		}
	}

	public final void sendPacket(final String p) {
		System.out.println(p);
		NMEAWriter.sendPacket(gpsRxTx, p);
	}

	/**
	 * This class's main in case it is run independently.
	 * 
	 * @param args
	 */
	public static void main(final String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			// Model m = new Model();
			// Controller c = new Controller(m);

			public void run() {
				try {
					(new IBlue747Model()).onStart();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Generic.setDebugLevel(1);
	}

}
