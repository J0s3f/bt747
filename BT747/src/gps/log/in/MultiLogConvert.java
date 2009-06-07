/**
 * 
 */
package gps.log.in;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.LogFileInfo;
import gps.log.out.GPSFile;

import bt747.model.Model;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
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

    protected Object getFileObject(final String fileName, final int card) {
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
     * @see gps.log.in.GPSLogConvertInterface#parseFile(gps.log.in.GPSFileConverterInterface)
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
     *                Vector of LogFileInfo
     */
    public void setLogFiles(final BT747Vector logFiles) {
        this.logFiles = logFiles;
    }

    private final GPSLogConvertInterface getConvertInstance(
            final String fileName, final GPSFileConverterInterface gpsFile,
            final int card) {
        final GPSLogConvertInterface lc = GPSInputConversionFactory
                .getHandler().getInputConversionInstance(fileName);
        final int sourceHeightReference = BT747Constants
                .getHeightReference(lc.getType());
        final int destinationHeightReference = BT747Constants
                .getHeightReference(getType());

        // Supposing automatic mode here... - should be consistent anyway.
        if ((sourceHeightReference == BT747Constants.HEIGHT_MSL)
                && (destinationHeightReference == BT747Constants.HEIGHT_WGS84)) {
            lc.setConvertWGS84ToMSL(+1);
        } else if ((sourceHeightReference == BT747Constants.HEIGHT_WGS84)
                && (destinationHeightReference == BT747Constants.HEIGHT_MSL)) {
            lc.setConvertWGS84ToMSL(-1);
        } else {
            /* Do nothing */
            lc.setConvertWGS84ToMSL(0);
        }
        return lc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#toGPSFile(java.lang.String,
     *      gps.log.in.GPSFileConverterInterface, int)
     */
    public int toGPSFile(final String fileName,
            final GPSFileConverterInterface gpsFile, final int card) {
        int error = BT747Constants.NO_ERROR;
        /**
         * Table of {@link GPSLogConvertInterface} used to convert input
         * files.
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
            final String fn = li.getPath();
            if (converters.get(fn) == null) {
                converters.put(fn, getConvertInstance(fn, gpsFile, card));
                logFileInfoLookup.put(fn, li);
            }
        }

        if ((fileName != null) && (fileName.length() != 0)) {
            if (converters.get(fileName) == null) {
                converters.put(fileName, getConvertInstance(fileName,
                        gpsFile, card));
                logFileInfoLookup.put(fileName, new LogFileInfo(fileName,
                        card));
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
                final Object key = iter.nextKey();
                final GPSLogConvertInterface i = (GPSLogConvertInterface) iter
                        .get(key);
                final LogFileInfo loginfo = (LogFileInfo) logFileInfoLookup
                        .get(key);
                statsConv.initStats();
                // TODO: manage cards on different volumes.
                currentConverter = i;
                i.parseFile(i.getFileObject((String) key, card), statsConv);
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
                final Object key = iter.nextKey();
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
                    final Object key = ((LogFileInfo) orderedLogs
                            .elementAt(j)).getPath();
                    final GPSLogConvertInterface i = (GPSLogConvertInterface) converters
                            .get(key);
                    currentConverter = i;
                    // TODO: manage cards on different volumes.
                    error = i.parseFile(i.getFileObject((String) key, card),
                            gpsFile);
                    currentConverter = null;
                    // Get date range.
                }
            } while ((error == BT747Constants.NO_ERROR) && gpsFile.nextPass());
        }
        gpsFile.finaliseFile();
        if (gpsFile.getFilesCreated() == 0) {
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

        /*
         * (non-Javadoc)
         * 
         * @see gps.log.out.GPSFile#writeRecord(gps.log.GPSRecord)
         */
        public final void addLogRecord(final GPSRecord r) {
            if (r.hasUtc()
                    && (!r.hasValid() || ((r.valid & BT747Constants.VALID_NO_FIX_MASK) == 0))) {
                final int time = r.getUtc() + timeOffsetSeconds;
                if (time < minTime) {
                    minTime = time;
                }
                if (time > maxTime) {
                    maxTime = time;
                }
            }
            // TODO Auto-generated method stub
            super.addLogRecord(r);
        }
    }
}
