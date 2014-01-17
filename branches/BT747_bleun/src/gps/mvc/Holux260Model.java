
package gps.mvc;

import gps.HoluxConstants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Holux GPSsport 260
 *
 * @author bl
 */
public class Holux260Model extends HoluxModel {
	public static final int TRACK_INFO_SIZE = 64;

	private static final int POS_NAME = 4;
	private static final int POS_DATE = 16;
	private static final int POS_DURA = POS_DATE + 4;
	private static final int POS_DIST = POS_DURA + 4;
	private static final int POS_MAXS = 36;
	private static final int POS_AVGS = POS_MAXS + 2;

	private static final boolean ANALYZE_SUCCESS = true;

	// fixme:
	// Move to HoluxConstants if verified
	public static final int ACK_TYPE_METADATA = 702;

	private int flashType;

	public Holux260Model(GpsModel context, GpsLinkHandler handler) {
		super(context, handler);
	}

	@Override
	protected boolean analysePHLXCommand(String[] sNmea) {
        String reply = sNmea[0];

		if (HoluxConstants.PHLX_ACK_GENERIC_ACK.equals(reply)) {
			switch (Integer.parseInt(sNmea[1])) {
			case ACK_TYPE_METADATA :
				setFlashType(Integer.parseInt(sNmea[2]));
				return ANALYZE_SUCCESS;
			}
		}
		else if (HoluxConstants.PHLX_DT_LOG_DOWNLOAD_ANNOUNCE_CHUNK.equals(reply)) {
			int startBlock = Integer.parseInt(sNmea[1]);
			int dataSize = Integer.parseInt(sNmea[2]);
			int bcc = Integer.parseInt(sNmea[3], 16);

			// fixme:
			// announce expected binary data block
		}
		return super.analysePHLXCommand(sNmea);
	}

	protected void setFlashType(int type) {
		setAvailable(MtkModel.DATA_FLASH_TYPE);
		flashType = type;
	}

	/**
	 * Track info records are stored in 64 bytes with the following format:
	 * Offset Content
	 * 00 uuUuNNNNNNNNNNNu
	 * 16 TTTTDDDDLLLLSSSS
	 * 32 ZZZZXXAAIIuuuuuu
	 * 48 uuuuuuuuuuuuuuuu
	 *
	 * Starts with FF00 00FF/03FF.
	 * The 03 or 00 could indicate the 'upload' status (03 if already
	 * transfered).
	 *
	 * U  1 Byte	Upload status (0: new, 3: already transfered)
	 * N 11 Char	Name, terminated with 0x00, plain ASCII
	 * T 32 Bit		start date (seconds since 1981-01-01, 00:00:00), LSb first
	 * D 32 Bit		Duration (s) LSb first
     * S 32 Bit		OFFSET in LOG  (bytes or position index)
     * Z 32 Bit		Size in LOG   [S+Z = the S value in the next block]
	 * L 32 Bit		Track length (m)
	 * X 16 Bit		Max. speed (km/h * 10)
	 * A 16 Bit		Avg. speed (km/h * 10)
	 * I 16 Bit		Intensity (calorie)
	 *
	 * u		unknown
	 */
	public static List<HoluxTrackInfo> getTrackInfo(byte[] data) {
		List<HoluxTrackInfo> trackInfoList = new ArrayList(data.length / TRACK_INFO_SIZE);
		StringBuilder sb = new StringBuilder(12);

		int pos = 0;
		while (pos + TRACK_INFO_SIZE <= data.length) {
			HoluxTrackInfo info = new HoluxTrackInfo();

			sb.setLength(0);
			for (int index = POS_NAME; index < 11; index++) {
				int ch = data[pos + index] & 0xff;

				if (ch == 0 || ch == 0xff) {
					break;
				}
				sb.append((char)ch);
			}
			if (sb.length() > 0) {
				info.setTrackName(sb.toString());
			}
			info.setStartDate(getCalendar(get32BitIntLH(data, pos + POS_DATE)).getTime());
			info.setDuration(get32BitIntLH(data, pos + POS_DURA));
			info.setDistance(get32BitIntLH(data, pos + POS_DIST));
			info.setMaxSpeed(getScaled(get16BitIntLH(data, pos + POS_MAXS), 10));
			info.setAvgSpeed(getScaled(get16BitIntLH(data, pos + POS_AVGS), 10));

			pos += TRACK_INFO_SIZE;
			trackInfoList.add(info);
		}
		return trackInfoList;
	}

	private static double getScaled(int value, int scale) {
		return ((double)value) / scale;
	}

	private static int get32BitIntLH(byte[] data, int pos) {
		int l = get16BitIntLH(data, pos);
		int h = get16BitIntLH(data, pos + 2);

		return (h << 16) + l;
	}

	private static int get16BitIntLH(byte[] data, int pos) {
		int l = data[pos + 0] & 0xff;
		int h = data[pos + 1] & 0xff;

		return (h << 8) + l;
	}

	public static Calendar getCalendar(long timeSpec) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeSpec * 1000);
		cal.add(Calendar.YEAR, 30);

		return cal;
	}

	public static class HoluxTrackInfo {
		private String trackName;
		private long distance;			// track distance in meter
		private int duration;			// duration in seconds
		private Date startDate;			// date and time of track start
		private double maxSpeed;
		private double avgSpeed;

		public double getAvgSpeed() {
			return avgSpeed;
		}

		public void setAvgSpeed(double avgSpeed) {
			this.avgSpeed = avgSpeed;
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public long getDistance() {
			return distance;
		}

		public void setDistance(long distance) {
			this.distance = distance;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public double getMaxSpeed() {
			return maxSpeed;
		}

		public void setMaxSpeed(double maxSpeed) {
			this.maxSpeed = maxSpeed;
		}

		public String getTrackName() {
			return trackName;
		}

		public void setTrackName(String trackName) {
			this.trackName = trackName;
		}

		public String getName() {
			if (!isEmpty(trackName)) {
				return trackName;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);

			return String.format("%04d-%02d-%02d, %02d:%02d",
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE));
		}

		public String getHumanReadableDistance() {
			if (distance > 1000) {
				return String.format("%.2fkm", ((double)distance) / 1000);
			}
			return Long.toString(distance) + "m";
		}

		public String getHumanReadableDuration() {
			int hh = duration / 3600;
			int mm = (duration % 3600) / 60;
			int ss = duration - (hh * 3600) - (mm * 60);

			return String.format("%02d:%02d:%02d", hh,mm,ss);
		}
	}

	private static boolean isEmpty(String str) {
		if (str != null) {
			int length = str.length();

			for (int n = 0; n < length; n++) {
				if (!Character.isWhitespace(str.charAt(n))) {
					return false;
				}
			}
		}
		return true;
	}
}