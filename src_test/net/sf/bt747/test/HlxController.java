/**
 *
 */
package net.sf.bt747.test;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * @author Mario De Weerd
 *
 */
public class HlxController {
	/**
	 * This extends the mtkDeviceModel;
	 */
	protected IBlue747Model mtkDeviceModel;

	protected byte[] binaryResponse;

	/**
	 * Initiate the instance.
	 *
	 * @param mtkDeviceModel
	 *            Reference to basic mtkDevice modeling.
	 */
	public HlxController(final IBlue747Model mtkDeviceModel) {
		this.mtkDeviceModel = mtkDeviceModel;
	}

	public static class HlxDataModel {
		public String holuxDeviceName = "BT747 Model";
		public int logCriteriaType = 0;
		public int logTimeCriteria = 5;
		public int logDistanceCriteria = 100;
		public int sportsMode = 0;
	}

	protected final HlxDataModel hlxData = new HlxDataModel();

	/**
	 * Check if the controller responds to the NMEA command.
	 *
	 * @return true if this controller can handle the command.
	 */
	public final boolean handles(final String nmea0) {
		switch (mtkDeviceModel.mtkData.modelType) {
		case HOLUXM1000C:
		case HOLUX_GR260:
			return nmea0.startsWith("PHLX");
		default:
			return false;
		}
	}

	public final void analyseResponse(final Object response) {
		analyseNMEA((String[]) response);
	}

	public int analyseNMEA(final String[] p_nmea) {
		int z_Cmd;
		int z_Result = 0;

		final StringBuffer nmea = new StringBuffer();
		final StringBuffer response = new StringBuffer();

		for (final String s : p_nmea) {
			nmea.append(s);
			nmea.append(',');
		}
		binaryResponse = null;

		Generic.debug(nmea.toString());

		if (p_nmea[0].startsWith("PHLX")) {
			z_Cmd = JavaLibBridge.toInt(p_nmea[0].substring(4));
			z_Result = analyseNMEA(response, z_Cmd, p_nmea);
		}
		if (response.length() > 0) {
			mtkDeviceModel.sendPacket(response.toString());
			if (z_Result == -1) {
				z_Result = 0;
			}
		}
		else if (z_Result > 0) {
			if (binaryResponse != null) {

			}
		}

		if (z_Result < 0) {
			Generic.debug("No response from holux model to " + nmea.toString());
		}
		return z_Result;
	} // End method

	protected int analyseNMEA(StringBuffer response, int nmeaId, String[] p_nmea) {
		int retc = -1;		// Suppose cmd not treated

		switch (nmeaId) {
		case 701: // Number of tracks.
			response.append("PHLX601,30"); // 30 is number of tracks.
			break;
		case 709: // Memory used in percent query.
			response.append("PHLX873,2"); // 2 is the percentage
			break;
		case 810:
			switch (mtkDeviceModel.mtkData.modelType) {
			case HOLUX_GR260:
			case HOLUXM1000C:
				response.append("PHLX852,");
				response.append(getDeviceID());
				break;
			}
			break;
		case 826: // Activates usb symbol
			response.append("PHLX859");
			break;
		case 828:
			break;
		case 829: // Firmware request.
			response.append("PHLX861,");
			response.append(mtkDeviceModel.mtkData.logVersion);
			break;
		case 830: // HoluxConstants.PHLX_NAME_SET_REQUEST:
			if (p_nmea.length == 2) {
				hlxData.holuxDeviceName = p_nmea[1];
				// PHLX_NAME_SET_ACK
				response.append("PHLX862");
			}
			break;
		case 831:
			response.append("PHLX863,");
			response.append(hlxData.holuxDeviceName);
			break;
		case 832:
			break;
		case 833:
			response.append("PHLX866,");
			response.append(hlxData.logCriteriaType);
			response.append(",");
			response.append(hlxData.logTimeCriteria);
			response.append(",");
			response.append(hlxData.logDistanceCriteria);
			break;
		case 841: // Sports mode
			if (p_nmea[1].equals("1")) {
				hlxData.sportsMode = Integer.parseInt(p_nmea[3]);
				response.append("PHLX900,841,3");
			} else {
				response.append("PHLX871,");
				response.append(getDeviceID());
				response.append(",");
				response.append(hlxData.sportsMode);
			}
			break;
		case 842: // Overwrite mode
			if (p_nmea[1].equals("1")) {
				mtkDeviceModel.mtkData.recMethod = Integer
						.parseInt(p_nmea[3]) == 0 ? 2 : 1;
				response.append("PHLX900,842,3");
			} else {
				response.append("PHLX872,");
				response.append(getDeviceID());
				response.append(",");
				response.append(hlxData.sportsMode);
			}
		}
		return retc;
	}

	private String getDeviceID() {

		switch (mtkDeviceModel.mtkData.modelType) {
		case HOLUXM1000C:
			return "M1000C";
		case HOLUX_GR260:
			return "GR260";
		}
		return "UNKNOWN";
	}
}
