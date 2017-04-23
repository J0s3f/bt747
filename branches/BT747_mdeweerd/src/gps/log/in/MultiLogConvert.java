/**
 * 
 */
package gps.log.in;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.LogFileInfo;
import gps.log.out.GPSFile;

import bt747.model.Controller;
import bt747.model.Model;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario
 * 
 */
public final class MultiLogConvert extends GPSLogConvertInterface {

	/**
	 * The converter that is currently converting.
	 */
	private GPSLogConvertInterface currentConverter;

	protected Object getFileObject(final BT747Path path) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#closeFileObject(java.lang.Object)
	 */
	protected void closeFileObject(final Object o) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seegps.log.in.GPSLogConvertInterface#parseFile(gps.log.in.
	 * GPSFileConverterInterface)
	 */
	public int parseFile(final Object file,
			final GPSFileConverterInterface gpsFile) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#stopConversion()
	 */
	public void stopConversion() {
		super.stopConversion();
		if (currentConverter != null) {
			currentConverter.stopConversion();
		}
	}

	private BT747Vector logFiles;

	/**
	 * Set the logfiles to convert.
	 * 
	 * @param logFiles
	 *            Vector of LogFileInfo
	 */
	public void setLogFiles(final BT747Vector logFiles) {
		this.logFiles = logFiles;
	}

	private final GPSLogConvertInterface getConvertInstance(
			final BT747Path path, final GPSFileConverterInterface gpsFile) {
		final GPSLogConvertInterface lc = GPSInputConversionFactory
				.getHandler().getInputConversionInstance(path, getLoggerType());
		final int sourceHeightReference = BT747Constants.getHeightReference(lc
				.getType());
		final int destinationHeightReference = BT747Constants
				.getHeightReference(getType());

		Controller.setHeightConversionMode(this.mode, lc,
				sourceHeightReference, destinationHeightReference);

		lc.setLoggerType(getLoggerType());
		return lc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#toGPSFile(java.lang.String,
	 * gps.log.in.GPSFileConverterInterface, int)
	 */
	public int toGPSFile(final BT747Path fileName,
			final GPSFileConverterInterface gpsFile) {
		int error = BT747Constants.NO_ERROR;
		/**
		 * Table of {@link GPSLogConvertInterface} used to convert input files.
		 * The key is the logFilePath.
		 */
		final BT747Hashtable converters = JavaLibBridge
				.getHashtableInstance(logFiles.size() + 1);
		/**
		 * Lookup list for LogFileInfo.
		 */
		final BT747Hashtable logFileInfoLookup = JavaLibBridge
				.getHashtableInstance(logFiles.size() + 1);

		/*
		 * Create a conversion instance for each file.
		 */
		for (int fileIdx = 0; !stop && (fileIdx < logFiles.size()); fileIdx++) {
			final LogFileInfo li = (LogFileInfo) (logFiles.elementAt(fileIdx));
			final BT747Path fn = li.getBT747Path();
			if (converters.get(fn.getPath()) == null) {
				converters.put(fn.getPath(), getConvertInstance(fn, gpsFile));
				logFileInfoLookup.put(fn.getPath(), li);
			}
		}

		if ((fileName != null) && (fileName.getPath().length() != 0)
				&& (new bt747.sys.File(fileName).exists())) {
			if (converters.get(fileName.getPath()) == null) {
				converters.put(fileName.getPath(), getConvertInstance(fileName, gpsFile));
				logFileInfoLookup.put(fileName.getPath(), new LogFileInfo(fileName));
			}
		}

		final GPSRecord activeFileFields = GPSRecord.getLogFormatRecord(0);
		/**
		 * First pass to find time range of each log file.
		 */
		{
			final BT747Hashtable iter = converters.iterator();
			final TrackStatsConverter statsConv = new TrackStatsConverter();
			if (gpsFile instanceof GPSFile) {
				final GPSFile gf = (GPSFile) gpsFile;
				statsConv.setUserWayPointList(gf.getUserWayPointList());
			}
			while (!stop && iter.hasNext()) {
				final String key = (String) iter.nextKey();
				final GPSLogConvertInterface i = (GPSLogConvertInterface) iter
						.get(key);
				final LogFileInfo loginfo = (LogFileInfo) logFileInfoLookup
						.get(key);
				statsConv.initStats();
				// TODO: manage cards on different volumes.
				currentConverter = i;
				i.parseFile(i.getFileObject(loginfo.getBT747Path()), statsConv);
				// Get date range.
				currentConverter = null;
				loginfo.setStartTime(statsConv.minTime);
				loginfo.setEndTime(statsConv.maxTime);
				loginfo.setActiveFileFields(statsConv.getActiveFileFields());

				activeFileFields.cloneActiveFields(loginfo
						.getActiveFileFields());
			}
		}

		/**
		 * Not expecting a lot of logs, so very simple ordering.
		 */
		final BT747Vector orderedLogs = JavaLibBridge.getVectorInstance();
		{
			final BT747Hashtable iter = logFileInfoLookup.iterator();
			while (iter.hasNext()) {
				final String key = (String) iter.nextKey();
				final LogFileInfo loginfo = (LogFileInfo) logFileInfoLookup
						.get(key);
				final int startTime = loginfo.getStartTime();
				int insertIdx = 0;
				while ((insertIdx < orderedLogs.size())) {
					if (startTime < ((LogFileInfo) orderedLogs
							.elementAt(insertIdx)).getStartTime()) {
						break;
					}
					insertIdx++;
				}
				orderedLogs.insertElementAt(loginfo, insertIdx);
			}
		}

		/**
		 * Actual reading.
		 */
		gpsFile.setActiveFileFields(activeFileFields);
		if (error == BT747Constants.NO_ERROR) {
			do {
				for (int j = 0; !stop && (j < orderedLogs.size()); j++) {
					final BT747Path path = ((LogFileInfo) orderedLogs.elementAt(j))
					.getBT747Path();
					final String key = path.getPath();
					final GPSLogConvertInterface i = (GPSLogConvertInterface) converters
							.get(key);
					currentConverter = i;
					// TODO: manage cards on different volumes.
					error = i.parseFile(i.getFileObject(path),
							gpsFile);
					currentConverter = null;
					// Get date range.
				}
			} while ((error == BT747Constants.NO_ERROR) && gpsFile.nextPass());
		}
		gpsFile.finaliseFile();
		if (gpsFile.getNbrFilesCreated() == 0) {
			error = BT747Constants.ERROR_NO_FILES_WERE_CREATED;
		}

		/* Finish file conversions */
		Generic.debug("Conversion done", null);
		return error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#getType()
	 */
	public int getType() {
		return Model.MULTI_LOGTYPE;
	}

	private static final class TrackStatsConverter extends GPSFile {
		protected int minTime;
		protected int maxTime;
		protected GPSRecord fileFields;

		protected final void initStats() {
			minTime = 0x7FFFFFFF;
			maxTime = 0;
			fileFields = GPSRecord.getLogFormatRecord(0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see gps.log.out.GPSFile#writeLogFmtHeader(gps.log.GPSRecord)
		 */
		public void writeLogFmtHeader(final GPSRecord f) {
			fileFields.cloneActiveFields(f);
			super.writeLogFmtHeader(f);
		}

		protected final GPSRecord getActiveFileFields() {
			return fileFields;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see gps.log.out.GPSFile#finaliseFile()
		 */
		public void finaliseFile() {
			// TODO Auto-generated method stub
			super.finaliseFile();
		}

		
		private static final int UTC2000 = JavaLibBridge.getDateInstance(1, 1, 2000).dateToUTCepoch1970();
		/*
		 * (non-Javadoc)
		 * 
		 * @see gps.log.out.GPSFile#writeRecord(gps.log.GPSRecord)
		 */
		public final void addLogRecord(final GPSRecord r) {
			if (r.hasUtc()
					&& (!r.hasValid() || ((r.valid & BT747Constants.VALID_NO_FIX_MASK) == 0))) {
				final int time = r.getUtc() + timeOffsetSeconds;
				if(time > UTC2000) {
				if (time < minTime) {
					minTime = time;
				}
				if (time > maxTime) {
					maxTime = time;
				}
				}
			}
			super.addLogRecord(r);
		}
	}
}
