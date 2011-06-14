
package net.sf.bt747.test.models;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gps.connection.NMEAWriter;
import gps.mvc.Holux260Model;
import gps.mvc.Holux260Model.HoluxTrackInfo;

import net.sf.bt747.test.HlxController;
import net.sf.bt747.test.IBlue747Model;

/**
 *
 * @author bl
 */
public class Hlx260Simulation extends HlxController {

	private static final String DEFAULT_TRACKS_FILE = "files/Holux260_tracks.bin";

	private byte[] trackMetaData;

	public Hlx260Simulation(final IBlue747Model mtkDeviceModel) {
		super(mtkDeviceModel);

		hlxData.holuxDeviceName = "GPSport260";
		mtkDeviceModel.mtkData.swVersion = "201";
	}

	@Override
	protected int analyseNMEA(StringBuffer response, int nmeaId, String[] p_nmea) {
		switch (nmeaId) {
			case 701: // Number of tracks
			addReply(response, 601, Integer.toString(
				getTrackMetaData().length / Holux260Model.TRACK_INFO_SIZE));
			return 0;

		case 702 : {
			// track metadata (from track n1 .. n2)?
			int n1 = Integer.parseInt(p_nmea[1]);
			int n2 = Integer.parseInt(p_nmea[2]);

			int v1 = 702;		// ACK type, matches request
			int v2 = 3;			// "flash type"?

			// 1st response: 702 ack, type: 3
			sendReply(900, Integer.toString(v1), Integer.toString(v2));

			byte[] data = getTrackMetaData();

			// 2nd response: [n] bytes, bcc: xxx
			addReply(response, 901,
				Integer.toString(data.length), getBCC(data));
			}
			return 0;

		case 827 :
			// unknown (seen on config request)
			addReply(response, 860);
			return 0;

		case 829 :
			addReply(response, 861, mtkDeviceModel.mtkData.swVersion);
			return 0;

		case 836 :
			// unknown (seen on config request)
			addReply(response, 869, "0");
			return 0;

		case 900 :
			// request track meta data
			int cmd = Integer.parseInt(p_nmea[1]);
			int prm = Integer.parseInt(p_nmea[2]);

			switch (cmd) {
			case 901 :
				// open meta data?
				int firstEnclosedBlock = 0;
				int totalSize = getTrackMetaData().length;

				addReply(response, 902,
					Integer.toString(firstEnclosedBlock),
					Integer.toString(totalSize),
					getBCC(getTrackMetaData()));
				break;

			case 902 :
				// read meta data?
				sendBinaryData(getTrackMetaData());
				break;
			}
			return 0;
		}
		return super.analyseNMEA(response, nmeaId, p_nmea);
	}

	private void sendReply(int id, String ... values) {
		StringBuffer reply = new StringBuffer();
		addReply(reply, id, values);

		mtkDeviceModel.sendPacket(reply.toString());
	}

	public final void sendBinaryData(byte[] data) {
		NMEAWriter.sendBinaryData(mtkDeviceModel.gpsRxTx, data);
	}

	private void addReply(StringBuffer reply, int id, String ... values) {
		reply.append("PHLX");
		reply.append(id);

		if (values != null) {
			for (String value : values) {
				reply.append(",");
				reply.append(value);
			}
		}
	}

	private byte[] getTrackMetaData() {
		if (trackMetaData == null) {
			trackMetaData = readTrackMetaData();
		}
		return trackMetaData;
	}

	private String getBCC(byte[] data) {
		// fixme:
		// don't know how to calculate
		return "C70DD553";
	}

	private static byte[] readTrackMetaData() {
		File file = new File(getResourcePath(DEFAULT_TRACKS_FILE));

		if (file.exists()) {
			FileInputStream is = null;

			try {
				is = new FileInputStream(file);

				// Expect everything ready, as it is a small, local file
				byte[] data = new byte[is.available()];

				is.read(data);
				is.close();

				return data;
			}
			catch (Exception ex) {
				// ignored
			}
		}
		return new byte[0];
	}

    private static String getResourcePath(String rsc) {
        return IBlue747Model.class.getResource(rsc).getPath();
    }

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");

	private static Calendar getCalendar(int yy, int mo, int dd, int hh, int mm) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, yy);
		cal.set(Calendar.MONTH, mo - 1);
		cal.set(Calendar.DAY_OF_MONTH, dd);
		cal.set(Calendar.HOUR_OF_DAY, hh);
		cal.set(Calendar.MINUTE, mm);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}

	private static String format(Date date) {
		return df.format(date);
	}

	public static void main(String[] av) {
		byte[] data = readTrackMetaData();
		int pos = 0;

		for (int n = 0; n < data.length; n++) {
			System.out.print(String.format("%02x", data[n] & 0xff));

			if (++pos >= 16) {
				pos = 0;
				System.out.println("");
			}
			else if ((pos % 2) == 0) {
				System.out.print(" ");
			}
		}
		System.out.println("");

		int track = 1;
		for (HoluxTrackInfo info : Holux260Model.getTrackInfo(data)) {
			System.out.println(String.format("%02d) %s, %8s, %s",
				track++,
				format(info.getStartDate()),
				info.getHumanReadableDistance(),
				info.getHumanReadableDuration()));
		}
	}
}
