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
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***

package gps.log.in;

import gps.BT747Constants;
import gps.log.GPSRecord;

import bt747.model.Model;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Time;

/**
 * Conversion of Wonde Proud logs (Phototrackr, ...) This class is used to
 * convert the binary log to a new format. Basically this class interprets the
 * log and creates a {@link GPSRecord}. The {@link GPSRecord} is then sent to
 * the {@link GPSFileConverterInterface} class object to write it to the output.
 * 
 * @author Mario De Weerd
 */
public final class SkytraqLogConvert extends GPSLogConvertInterface {
	private static final int X_FF = 0xFF;
	@SuppressWarnings("unused")
	private int recordSize = 16;
	private static final int logFormat = (1 << BT747Constants.FMT_UTC_IDX)
			| (1 << BT747Constants.FMT_LATITUDE_IDX)
			| (1 << BT747Constants.FMT_LONGITUDE_IDX)
			| (1 << BT747Constants.FMT_SPEED_IDX)
			| (1 << BT747Constants.FMT_HEIGHT_IDX)
			| (1 << BT747Constants.FMT_RCR_IDX);
	protected boolean passToFindFieldsActivatedInLog = false;
	protected int activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
			| (1 << BT747Constants.FMT_LATITUDE_IDX)
			| (1 << BT747Constants.FMT_LONGITUDE_IDX)
			| (1 << BT747Constants.FMT_SPEED_IDX)
			| (1 << BT747Constants.FMT_HEIGHT_IDX)
			| (1 << BT747Constants.FMT_RCR_IDX);

	// static final int ITRACKU_NUMERIX = 0;
	// static final int PHOTOTRACKR = 1;
	// static final int ITRACKU_SIRFIII = 2;

	// private int logType = WPLogConvert.ITRACKU_NUMERIX;

	/**
	 * The size of the file read buffer
	 */
	private static final int BUF_SIZE = 4096;

	public SkytraqLogConvert() {
		super();
	}

	public void setLoggerType(final int logType) {
		super.setLoggerType(logType);
		switch (getLoggerType()) {
		case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
		case BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
			activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
					| (1 << BT747Constants.FMT_LATITUDE_IDX)
					| (1 << BT747Constants.FMT_LONGITUDE_IDX)
					| (1 << BT747Constants.FMT_HEIGHT_IDX)
					| (1 << BT747Constants.FMT_SPEED_IDX);
			recordSize = 16;
			break;
		case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
		default:
			activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
					| (1 << BT747Constants.FMT_LATITUDE_IDX)
					| (1 << BT747Constants.FMT_LONGITUDE_IDX)
					| (1 << BT747Constants.FMT_HEIGHT_IDX);
			recordSize = 16;
			break;
		}
	}

	/**
	 * Creates utc time value of Phototrackr specific format.
	 * 
	 * @param rawTime
	 *            Phototracker time representation.
	 * @return long type representing utc time.
	 */
	public static final int longToUtcTime(final int rawTime) {
		final int seconds = rawTime & 0x3F;
		final int minutes = (rawTime >> 6) & 0x3F;
		final int hour = (rawTime >> 12) & 0x1F;
		final int day = (rawTime >> 17) & 0x1F;
		final int month = (rawTime >> 22) & 0x0F;
		final int year = (rawTime >> 26) & 0x3F;
		final int utc = (JavaLibBridge.getDateInstance(day, month, year + 2000))
				.dateToUTCepoch1970()
				+ 3600 * hour + 60 * minutes + seconds;
		return utc;
	}

	public int parseFile(final Object file,
			final GPSFileConverterInterface gpsFile) {
		try {
			final WindowedFile inFile = (WindowedFile) file;
			GPSRecord r = GPSRecord.getLogFormatRecord(0);
			final int C_BUF_SIZE = 4096; // block size is 4096
			byte[] bytes;
			int sizeToRead;
			int nextAddrToRead;
			int recCount;
			int fileSize;
			SkytraqPositionRecord previousR = new SkytraqPositionRecord();

			if (!passToFindFieldsActivatedInLog) {
				gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
			}

			// recordSize = 15;

			recCount = 0;
			nextAddrToRead = 0;
			fileSize = inFile.getSize();
			while (!stop && (nextAddrToRead + 8 + 1 < fileSize)) {
				sizeToRead = C_BUF_SIZE;
				if ((sizeToRead + nextAddrToRead) > fileSize) {
					sizeToRead = fileSize - nextAddrToRead;
				}

				/* Read the bytes from the file */
				int offsetInBuffer = 0;

				try {
					bytes = inFile.fillBuffer(nextAddrToRead);
				} catch (final Exception e) {
					// TODO: Should check sizeToRead vs fill in buffer.
					Generic.debug("Problem reading file", e);
					bytes = null;
				}
				if (bytes == null) {
					Generic.debug("fillBuffer failed", null);

					errorInfo = inFile.getPath() + "|" + inFile.getLastError();
					return BT747Constants.ERROR_READING_FILE;
				}

				nextAddrToRead += sizeToRead;

				/*************************************************************
				 * Interpret the data read in the Buffer as long as the records
				 * are complete
				 */
				while (!stop && offsetInBuffer < sizeToRead) {
					int recType = 0x7 & (bytes[offsetInBuffer] >> 5);
					switch (recType) {
					case 0x7:
						/* Empty storage. Skip */
						offsetInBuffer += 2;
						break;
					case 0x2: /* Fix full */
					case 0x3: /* Fix POI */
						if (offsetInBuffer + 18 <= sizeToRead) {
							previousR = getFull(bytes, offsetInBuffer);
							r = toGpsRecord(previousR);
							r.rcr = BT747Constants.RCR_TIME_MASK;
							if (recType == 2) {
								r.rcr = BT747Constants.RCR_TIME_MASK;
							} else {
								r.rcr = BT747Constants.RCR_BUTTON_MASK;
							}
							r.recCount = ++recCount;
							gpsFile.addLogRecord(r);
							offsetInBuffer += 18;
						} else {
							nextAddrToRead += offsetInBuffer - sizeToRead;
							sizeToRead = offsetInBuffer;
						}
						break;
					case 0x4:
						if (offsetInBuffer + 8 <= sizeToRead) {
							/* Fix compact */
							previousR = getCompact(previousR, bytes,
									offsetInBuffer);
							r = toGpsRecord(previousR);
							r.rcr = BT747Constants.RCR_TIME_MASK;
							r.recCount = ++recCount;
							gpsFile.addLogRecord(r);
							offsetInBuffer += 8;
						} else {
							nextAddrToRead += offsetInBuffer - sizeToRead;
							sizeToRead = offsetInBuffer;
						}
						break;

					default:
						/* Fix full POI. */
						offsetInBuffer += 18;
						break;
					}
				}

			} /* nextAddrToRead<fileSize */
		} catch (final Exception e) {
			Generic.debug("", e);
		}
		return BT747Constants.NO_ERROR;
	}

	/**
	 * Leap second information from
	 * http://hpiers.obspm.fr/eop-pc/earthor/utc/UTC-offsets_tab.html
	 */
	final static private int[] totalLeapSecond = { 0, /* 1980 Jan. */
	1, /* 1981 Jul. */
	2, /* 1982 Jul. */
	3, /* 1983 Jul. */
	4, /* 1984 */
	4, /* 1985 Jul. */
	5, /* 1986 */
	5, /* 1987 */
	5, /* 1988 Jan. */
	6, /* 1989 */
	6, /* 1990 Jan. */
	7, /* 1991 Jan. */
	8, /* 1992 Jul. */
	9, /* 1993 Jul. */
	10, /* 1994 Jul. */
	11, /* 1995 */
	11, /* 1996 Jan. */
	12, /* 1997 Jul. */
	13, /* 1998 */
	13, /* 1999 Jan. */
	14, /* 2000 */
	14, /* 2001 */
	14, /* 2002 */
	14, /* 2003 */
	14, /* 2004 */
	14, /* 2005 */
	14, /* 2006 Jan. */
	15, /* 2007 */
	15, /* 2008 */
	15, /* 2009 Jan. */
	16, /* 2010 */
	16, /* 2011 */
	16, /* 2012 Juil. */
	17, /* 2013 */
	17, /* 2014 */
	18, /* 2015 Juil. */
	18, /* 2016 */
	19, /* 2017 Jan. */
	};
	/* First year = 1980 */
	final static private int[] leapMonth = { 1, /* 1980 Jan. */
	7, /* 1981 Jul. */
	7, /* 1982 Jul. */
	7, /* 1983 Jul. */
	100, /* 1984 */
	7, /* 1985 Jul. */
	100, /* 1986 */
	100, /* 1987 */
	1, /* 1988 Jan. */
	100, /* 1989 */
	1, /* 1990 Jan. */
	1, /* 1991 Jan. */
	7, /* 1992 Jul. */
	7, /* 1993 Jul. */
	7, /* 1994 Jul. */
	100, /* 1995 */
	1, /* 1996 Jan. */
	7, /* 1997 Jul. */
	100, /* 1998 */
	1, /* 1999 Jan. */
	100, /* 2000 */
	100, /* 2001 */
	100, /* 2002 */
	100, /* 2003 */
	100, /* 2004 */
	100, /* 2005 */
	1, /* 2006 Jan. */
	100, /* 2007 */
	100, /* 2008 */
	1, /* 2009 Jan. */
	100, /* 2010 */
	100, /* 2011 */
	7, /* 2012 Juil. */
	100, /* 2013 */
	100, /* 2014 */
	7, /* 2015 Juil. */
	100, /* 2016 */
	1, /* 2017 Jan. */
	100, /* 2018 */
	/*
	 * Last in table must not be a leap second year for algorithm to work, if
	 * not, add one year.
	 */
	};

	private static final int getLeapSecondsSince1980(final int year,
			final int month) {
		int index = year - 1980;
		int leapSeconds;
		if (index > 0) {
			if (index < totalLeapSecond.length) {
				leapSeconds = totalLeapSecond[index];
				if (month >= leapMonth[index]) {
					leapSeconds += 1;
				}
			} else {
				leapSeconds = totalLeapSecond[totalLeapSecond.length-1];
			}
		} else {
			leapSeconds = 0;
		}
		return leapSeconds;
	}

	// private static long prevX=0, prevY=0, prevZ=0;
	private final static GPSRecord toGpsRecord(SkytraqPositionRecord sRec) {
		GPSRecord rRec = new GPSRecord();

		/**
		 * Convert Earth-Centered Earth-Fixed coordinates into longitude,
		 * latitude and height.
		 */

		ecef2WGS84(rRec, sRec.x, sRec.y, sRec.z);
		BT747Date d = JavaLibBridge.getDateInstance(1, 1, 1970);
		d.advance(10825 + sRec.wn * 7);
		/* First year = 1980. Leap second at start of year - before leap. */
		BT747Time t = JavaLibBridge.getTimeInstance();

		int utc = (10825 * 24 * 3600) + sRec.wn * 7 * 24 * 3600 + +sRec.tow;
		t.setUTCTime(utc);
		utc -= getLeapSecondsSince1980(t.getYear(), t.getMonth());
		rRec.setUtc(utc);

		rRec.setSpeed(sRec.speed);
		// if(sRec.wn<=542 && sRec.tow<=589475) {
		// System.out.println(""+(1024+sRec.wn)+" "+sRec.tow+" "+t.getHour()+":"+t.getMinute()+":"+t.getSecond()+
		// " "
		// + t.getMonth()
		// +"/"+t.getDay()+"/"+t.getYear()+" "
		// +(sRec.x-prevX)+" "
		// +(sRec.y-prevY)+" "
		// +(sRec.z-prevZ)+" "
		// +(sRec.x)+" "
		// +(sRec.y)+" "
		// +(sRec.z)+" "
		// +sRec.speed+" "
		// +JavaLibBridge.toString(rRec.getLongitude(), 6)+" "
		// +JavaLibBridge.toString(rRec.getLatitude(), 6)+" "
		// +JavaLibBridge.toString(rRec.getHeight(), 6)
		// );
		// }
		// prevX = sRec.x;
		// prevY= sRec.y;
		// prevZ = sRec.z;
		// System.out.println("Full at "+r.recCount);
		return rRec;
	}

	/**
	 * See http://www.colorado.edu/geography/gcraft/notes/datum/edlist.html for
	 * instance for numbers.
	 * 
	 * 
	 * See http://www.colorado.edu/geography/gcraft/notes/datum/gif/xyzllh.gif
	 * for calculation from ECEF to Datum.
	 * 
	 * 
	 * See http://www.colorado.edu/geography/gcraft/notes/datum/gif/molodens.gif
	 * for Datum transform without ECEF knowledge.
	 * 
	 * Code converted from SkyTraq GPS data logger download. Distributed under
	 * GPL by Jesper Zedlitz, jesper@zedlitz.de
	 * 
	 * @param rRec
	 * @param X
	 * @param Y
	 * @param Z
	 */
	private final static double SEMI_MAJOR_AXIS_WGS84 = 6378137.0;
	private final static double FLATTENING_WGS84 = 298.257223563;

	private static void ecef2WGS84(GPSRecord rRec, final double X,
			final double Y, final double Z) {
		/** Semi-major earth axis. */
		final double a = SEMI_MAJOR_AXIS_WGS84;
		/** Flattening. */
		final double f = 1 / FLATTENING_WGS84; /* reciprocal flattening */
		/** Semi-minor earch axis. [f = (a-b)/a => b = ...] */
		final double b = a * (1 - f);
		/** Squared excentricity. */
		final double e_2 = 2 * f - f * f;
		/** Second squared excentricity. */
		final double ep2 = f * (2 - f) / ((1 - f) * (1 - f));
		final double E2 = a * a - b * b;

		final double r2 = X * X + Y * Y;
		final double F = 54 * b * b * Z * Z;
		final double r = Math.sqrt(r2);
		final double G = r2 + (1 - e_2) * Z * Z - e_2 * E2;
		final double c = (e_2 * e_2 * F * r2) / (G * G * G);
		final double s = bt747.sys.Generic.pow((1 + c + Math
				.sqrt(c * c + 2 * c)), 1 / 3.);
		final double P = F / (3 * (s + 1 / s + 1) * (s + 1 / s + 1) * G * G);
		final double Q = Math.sqrt(1 + 2 * e_2 * e_2 * P);
		final double ro = -(e_2 * P * r)
				/ (1 + Q)
				+ Math.sqrt((a * a / 2) * (1 + 1 / Q) - ((1 - e_2) * P * Z * Z)
						/ (Q * (1 + Q)) - P * r2 / 2);
		final double tmp = (r - e_2 * ro) * (r - e_2 * ro);
		final double U = Math.sqrt(tmp + Z * Z);
		final double V = Math.sqrt(tmp + (1 - e_2) * Z * Z);
		final double zo = (b * b * Z) / (a * V);

		final float h = (float) (U * (1 - b * b / (a * V)));
		final double phi = JavaLibBridge.atan((Z + ep2 * zo) / r);
		final double lambda = JavaLibBridge.atan2(Y, X);

		rRec.setLongitude(lambda * 180 / Math.PI);
		rRec.setLatitude(phi * 180 / Math.PI);
		rRec.setHeight(h);
	}

	static private class SkytraqPositionRecord {
		public int tow;
		public int wn;
		public int x; // 32 bit
		public int y; // 32 bit
		public int z; // 32 bit
		public int speed;

	};

	private SkytraqPositionRecord getFull(byte[] bytes, int recIdx) {
		final SkytraqPositionRecord r = new SkytraqPositionRecord();
		r.speed = ((bytes[recIdx] & 0x3) << 8)
				+ (bytes[recIdx + 1] & SkytraqLogConvert.X_FF);
		r.tow = ((bytes[recIdx + 4] & 0xFF) << 12)
				+ ((bytes[recIdx + 5] & 0xFF) << 4)
				+ ((bytes[recIdx + 2] >> 4) & 0x0F);
		r.wn = ((bytes[recIdx + 2] & 0x3) << 8)
				+ (bytes[recIdx + 3] & SkytraqLogConvert.X_FF);
		r.x = ((bytes[recIdx + 6] & 0xFF) << 8)
				+ ((bytes[recIdx + 7] & 0xFF))
				+ ((bytes[recIdx + 8] & 0xFF) << 24)
				+ ((bytes[recIdx + 9] & 0xFF) << 16);
		r.y = ((bytes[recIdx + 10] & 0xFF) << 8)
				+ ((bytes[recIdx + 11] & 0xFF))
				+ ((bytes[recIdx + 12] & 0xFF) << 24)
				+ ((bytes[recIdx + 13] & 0xFF) << 16);
		r.z = ((bytes[recIdx + 14] & 0xFF) << 8)
				+ ((bytes[recIdx + 15] & 0xFF))
				+ ((bytes[recIdx + 16] & 0xFF) << 24)
				+ ((bytes[recIdx + 17] & 0xFF) << 16);
		return r;
	}

	private SkytraqPositionRecord getCompact(SkytraqPositionRecord previous,
			byte[] bytes, int recIdx) {
		final SkytraqPositionRecord r = previous;
		// r.tow = previous.tow;
		// r.x = previous.x;
		// r.y = previous.y;
		// r.z = previous.z;

		r.speed = ((bytes[recIdx] & 0x3) << 8)
				+ (bytes[recIdx + 1] & SkytraqLogConvert.X_FF);
		int delta_tow = ((bytes[recIdx + 2] & 0xFF) << 8)
				+ ((bytes[recIdx + 3] & 0xFF));
		int delta_x = ((bytes[recIdx + 4] & 0xFF) << 2)
				+ ((bytes[recIdx + 5] & 0xC0) >> 6);
		int delta_y = ((bytes[recIdx + 5] & 0x3F))
				+ ((bytes[recIdx + 6] & 0xF0) << 2);
		int delta_z = ((bytes[recIdx + 6] & 0x03) << 8)
				+ ((bytes[recIdx + 7] & 0xFF));

		if ((delta_x & 0x200) != 0) {
			delta_x = 0x1FF - delta_x;
		}
		if ((delta_y & 0x200) != 0) {
			delta_y = 0x1FF - delta_y;
		}
		if ((delta_z & 0x200) != 0) {
			delta_z = 0x1FF - delta_z;
		}
		r.tow += delta_tow;
		r.x += delta_x;
		r.y += delta_y;
		r.z += delta_z;
		return r;
	}

	private int error;

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#getFileObject()
	 */
	protected Object getFileObject(final BT747Path path) {
		WindowedFile mFile = null;
		if (File.isAvailable()) {
			try {
				mFile = new WindowedFile(path, File.READ_ONLY);
				mFile.setBufferSize(BUF_SIZE);
				errorInfo = path.toString() + "|" + mFile.getLastError();
			} catch (final Exception e) {
				Generic.debug("Error during initial open", e);
			}
			if ((mFile == null) || !mFile.isOpen()) {
				errorInfo = path.toString();
				if (mFile != null) {
					errorInfo += "|" + mFile.getLastError();
				}
				error = BT747Constants.ERROR_COULD_NOT_OPEN;
				mFile = null;
			}
		}
		return mFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#closeFileObject(java.lang.Object)
	 */
	protected void closeFileObject(final Object o) {
		((WindowedFile) o).close();
	}

	public int toGPSFile(final BT747Path path,
			final GPSFileConverterInterface gpsFile) {
		Object inFile;
		error = BT747Constants.NO_ERROR;

		try {
			inFile = getFileObject(path);
			if (inFile != null) {
				passToFindFieldsActivatedInLog = gpsFile
						.needPassToFindFieldsActivatedInLog();
				if (passToFindFieldsActivatedInLog) {
					gpsFile
							.setActiveFileFields(getLogFormatRecord(activeFileFields));
				}
				passToFindFieldsActivatedInLog = false;
				if (error == BT747Constants.NO_ERROR) {
					do {
						error = parseFile(inFile, gpsFile);
					} while (gpsFile.nextPass());
				}
				gpsFile.finaliseFile();

				closeFileObject(inFile);
			}
		} catch (final Exception e) {
			Generic.debug("", e);
		}
		return error;
	}

	public GPSRecord getLogFormatRecord(final int logFormat) {
		int logfmt = (1 << BT747Constants.FMT_UTC_IDX)
				| (1 << BT747Constants.FMT_LATITUDE_IDX)
				| (1 << BT747Constants.FMT_LONGITUDE_IDX)
				| (1 << BT747Constants.FMT_LONGITUDE_IDX | (1 << BT747Constants.FMT_SPEED_IDX));

		switch (getLoggerType()) {
		case BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
		case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
			logfmt |= (1 << BT747Constants.FMT_HEIGHT_IDX);
			break;
		case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
		default:
			break;
		}

		return GPSRecord.getLogFormatRecord(logfmt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#getType()
	 */
	public int getType() {
		return Model.SR_LOGTYPE;
	}
}
