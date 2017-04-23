/**
 * June 10, 2010.
 * Copyright 2010 Mario De Weerd.
 *******************************************************************
 * The General Public License Version 3 applies to this file
 * unless you have another written agreement from the copyright
 * owner.
 *
 * Software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE
 * IS ASSUMED BY THE USER.
 * See the GNU General Public License Version 3 for details.
 */
package gps.log.in;

import gps.BT747Constants;
import gps.log.GPSRecord;

import bt747.model.Model;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Conversion of Sony '.result' file format from 'Tracker' application.

 * @author Mario De Weerd
 */
public final class SonyResultConvert extends GPSLogConvertInterface {
	private static final int logFormat = (1 << BT747Constants.FMT_UTC_IDX)
			| (1 << BT747Constants.FMT_LATITUDE_IDX)
			| (1 << BT747Constants.FMT_LONGITUDE_IDX)
			| (1 << BT747Constants.FMT_HEIGHT_IDX);
	protected boolean passToFindFieldsActivatedInLog = false;
	protected int activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
			| (1 << BT747Constants.FMT_LATITUDE_IDX)
			| (1 << BT747Constants.FMT_LONGITUDE_IDX)
			| (1 << BT747Constants.FMT_HEIGHT_IDX);

	// static final int ITRACKU_NUMERIX = 0;
	// static final int PHOTOTRACKR = 1;
	// static final int ITRACKU_SIRFIII = 2;

	// private int logType = WPLogConvert.ITRACKU_NUMERIX;

	/**
	 * The size of the file read buffer
	 */
	private static final int BUF_SIZE = 4096;

	public SonyResultConvert() {
		super();
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
			@SuppressWarnings("unused")
			GPSRecord previousR = new GPSRecord();

			if (!passToFindFieldsActivatedInLog) {
				gpsFile.writeLogFmtHeader(GPSRecord
						.getLogFormatRecord(logFormat));
			}

			// recordSize = 15;

			recCount = 0;
			nextAddrToRead = 0;
			fileSize = inFile.getSize();
			while (!stop && (nextAddrToRead + 26 < fileSize)) {
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

				/*************************************************************
				 * Interpret the data read in the Buffer as long as the records
				 * are complete
				 */
				while (!stop && offsetInBuffer + 26 < sizeToRead) {
					int recType = ((bytes[offsetInBuffer]&0xFF) << 8)
							| (bytes[offsetInBuffer + 1]);
					switch (recType) {
					case 0x0000: // Size 2 (or 4, but next is 00 too)
						// No record, skip 2
						offsetInBuffer += 2;
						break;
					case 0x524C: // Size 12
						// Just a timestamp ?
						// Skip
						offsetInBuffer += 12;
						break;
					case 0x5244: // Size 26
						// Position record.
						r = getFull(bytes, offsetInBuffer);
						r.recCount = ++recCount;
						gpsFile.addLogRecord(r);
						offsetInBuffer += 26;
						break;
					case 0x5256: // Size8
					case 0x5241:
					case 0x5250:
						// Skip
						offsetInBuffer += 8;
						break;
					default:
						/* Unknown - skipping 2 and alerting */
						Generic.debug("Unknown entry "
								+ JavaLibBridge.unsigned2hex(recType, 4)
								+ "("
								+ JavaLibBridge.unsigned2hex(nextAddrToRead
										+ offsetInBuffer, 8) + ")", null);
						offsetInBuffer += 2;
						break;
					}
				}
				nextAddrToRead += offsetInBuffer;
			} /* nextAddrToRead<fileSize */
		} catch (final Exception e) {
			Generic.debug("", e);
		}
		return BT747Constants.NO_ERROR;
	}

	private GPSRecord getFull(byte[] bytes, int recIdx) {
		final GPSRecord r = GPSRecord.getLogFormatRecord(0);
		// Two bytes are record type
		recIdx += 2;
		// 1 byte is 00 (??)
		recIdx++;
		// 1 byte is ??
		recIdx++;
		// 8 bytes for latitude, double encoded
		final long longitude = (0xFFL & bytes[recIdx++]) << 56
				| (0xFFL & bytes[recIdx++]) << 48
				| (0xFFL & bytes[recIdx++]) << 40
				| (0xFFL & bytes[recIdx++]) << 32
				| (0xFFL & bytes[recIdx++]) << 24
				| (0xFFL & bytes[recIdx++]) << 16
				| (0xFFL & bytes[recIdx++]) << 8 | (0xFFL & bytes[recIdx++]);
		r.longitude = JavaLibBridge.longBitsToDouble(longitude);
		final long latitude = (0xFFL & bytes[recIdx++]) << 56
				| (0xFFL & bytes[recIdx++]) << 48
				| (0xFFL & bytes[recIdx++]) << 40
				| (0xFFL & bytes[recIdx++]) << 32
				| (0xFFL & bytes[recIdx++]) << 24
				| (0xFFL & bytes[recIdx++]) << 16
				| (0xFFL & bytes[recIdx++]) << 8 | (0xFFL & bytes[recIdx++]);
		r.latitude = JavaLibBridge.longBitsToDouble(latitude);

		// 20 bytes done already.
		r.height = ((((0xFF & bytes[recIdx++]) << 24) >> 16) | (0xFF & bytes[recIdx++]) << 0) * 10;
		// recIdx++;
		// recIdx++;
		// Time: 4 bytes.
		r.utc = (0xFF & bytes[recIdx++]) << 24 | (0xFF & bytes[recIdx++]) << 16
				| (0xFF & bytes[recIdx++]) << 8 | (0xFF & bytes[recIdx++]) << 0;
		r.milisecond = 10 * (r.utc % 100);
		r.utc = r.utc / 100;

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
					gpsFile.setActiveFileFields(GPSRecord
							.getLogFormatRecord(logFormat));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#getType()
	 */
	public int getType() {
		return Model.SONYTRACKERRESULT_LOGTYPE;
	}
}
