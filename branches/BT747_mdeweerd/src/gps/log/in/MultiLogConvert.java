/**
 * 
 */
package gps.log.in;

import gps.BT747Constants;
import gps.log.LogFileInfo;

import bt747.sys.Generic;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario
 * 
 */
public class MultiLogConvert extends GPSLogConvertInterface {

    /**
     * The converter that is currently converting.
     */
    private GPSLogConvertInterface currentConverter;
    /**
     * Table of {@link GPSLogConvertInterface} used to convert input files.
     */
    private BT747Hashtable converters;

    /**
     * Lookup list for LogFileInfo.
     */
    private BT747Hashtable logFileInfoLookup;

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

    public void setLogFiles(final BT747Vector logFiles) {

    }

    private GPSLogConvertInterface getConvertInstance(final String fileName,
            final GPSFileConverterInterface gpsFile, final int card) {
        final GPSLogConvertInterface o = new BT747LogConvert();
        o.setConvertWGS84ToMSL(factorConversionWGS84ToMSL);
        return o;
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

        converters = Interface.getHashtableInstance(logFiles.size() + 1);
        logFileInfoLookup = Interface
                .getHashtableInstance(logFiles.size() + 1);

        converters.put(fileName, getConvertInstance(fileName, gpsFile, card));
        logFileInfoLookup.put(fileName, new LogFileInfo(fileName, card));
        /*
         * Create a conversion instance for each file.
         */
        for (int fileIdx = 0; !stop && (fileIdx < logFiles.size()); fileIdx++) {
            final LogFileInfo li = (LogFileInfo) (logFiles.elementAt(fileIdx));
            final String fn = li.getPath();
            if (converters.get(fn) != null) {
                converters.put(fn,
                        getConvertInstance(fileName, gpsFile, card));
                logFileInfoLookup.put(fileName, li);
            }
        }

        
        /**
         * First pass to find time range of each log file.
         */
        BT747Hashtable iter = converters.iterator();
        while(!stop && iter.hasNext() ) {
            final Object key = iter.nextKey();
            final GPSLogConvertInterface i = (GPSLogConvertInterface) iter.get(key);
            // TODO: manage cards on different volumes.
            i.parseFile(i.getFileObject((String) key, card), gpsFile);
            // Get date range.
        }
        /*
         * Actual conversions.
         */
        boolean passToFindFieldsActivatedInLog;
//        passToFindFieldsActivatedInLog = gpsFile
//                .needPassToFindFieldsActivatedInLog();
//        if (passToFindFieldsActivatedInLog) {
//            error = parseFile(file, gpsFile);
//            // gpsFile.setActiveFileFields(GPSRecord
//            // .getLogFormatRecord(activeFileFields));
//        }
        passToFindFieldsActivatedInLog = false;
        if (error == BT747Constants.NO_ERROR) {
            do {
                while(!stop && iter.hasNext() ) {
                    final Object key = iter.nextKey();
                    final GPSLogConvertInterface i = (GPSLogConvertInterface) iter.get(key);
                    // TODO: manage cards on different volumes.
                    error = i.parseFile(i.getFileObject((String) key, card), gpsFile);
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

}
