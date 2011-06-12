package gps.log.in;

import gps.BT747Constants;
import gps.log.GPSRecord;
import bt747.model.Model;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Vector;

/**
 * Class to read data from array in conversion process.
 * 
 * @author mdeweerd
 * 
 */
public class ArrayLogConvert extends GPSLogConvertInterface {

	/**
	 * {@link BT747Vector} of tracks ({@link BT747Vector}) of trackpoints
	 * {@link GPSRecord}. Get a vector with
	 * {@link JavaLibBridge#getVectorInstance()}. Either this is null or the
	 * array is null
	 */
	private BT747Vector trackVector = null;
	private GPSRecord[] trackArray = null;

	public final void setTrackVector(final BT747Vector trackVector) {
		this.trackVector = trackVector;
	}

	public final void setTrackArray(final GPSRecord[] trackArray) {
		this.trackArray = trackArray;
	}

	private final GPSRecord getRecord(final int idx) {
		try {
			if (trackVector != null) {
				return (GPSRecord) trackVector.elementAt(idx);
			} else if (trackArray != null && idx < trackArray.length) {
				return trackArray[idx];
			}
		} catch (Exception e) {
			Generic.debug("ArrayLogConvert - get record", e);
		}
		return null;
	}

	private final int size() {
		if (trackVector != null) {
			return trackVector.size();
		} else if (trackArray != null) {
			return trackArray.length;
		}
		return 0;
	}

	protected void closeFileObject(Object file) {
		// Does not apply for array, but could be used to clean up internal
		// data.

	}

	protected Object getFileObject(BT747Path path) {
		// Does not apply for the array
		// Return self.
		return this;
	}

	public int getType() {
		return Model.ARRAY_LOGTYPE;
	}

	private boolean passToFindFieldsActivatedInLog = false;
	private int activeFileFields = 0;

	public int parseFile(Object file, GPSFileConverterInterface gpsFile) {
		final int n = size();
		for (int i = 0; !stop && i < n; i++) {
			GPSRecord r = getRecord(i);
			if (r != null) {
				if (!passToFindFieldsActivatedInLog) {
					r = r.cloneRecord();
					CommonIn.convertHeight(r, factorConversionWGS84ToMSL);
					gpsFile.addLogRecord(r.cloneRecord());
				} else {
					activeFileFields |= r.getLogFormat();
				}

			}
		}
		return BT747Constants.NO_ERROR;
	}

	public int toGPSFile(BT747Path fileName, GPSFileConverterInterface gpsFile) {
		int error = BT747Constants.NO_ERROR;
		passToFindFieldsActivatedInLog = gpsFile
				.needPassToFindFieldsActivatedInLog();
		if (passToFindFieldsActivatedInLog) {
			error = parseFile(null, gpsFile);

		}
		passToFindFieldsActivatedInLog = false;
		gpsFile.setActiveFileFields(GPSRecord
				.getLogFormatRecord(activeFileFields));
		if (error == BT747Constants.NO_ERROR) {
			do {
				error = parseFile(null, gpsFile);
			} while ((error == BT747Constants.NO_ERROR) && gpsFile.nextPass());
		}
		gpsFile.finaliseFile();
		if (gpsFile.getNbrFilesCreated() == 0) {
			error = BT747Constants.ERROR_NO_FILES_WERE_CREATED;
		}
		try {
			closeFileObject(null);
		} catch (final Exception e) {
			Generic.debug("close", e);
			// TODO: handle exception
		}

		Generic.debug("Conversion done", null);
		return error;
	}

}
