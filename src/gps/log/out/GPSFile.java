//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package gps.log.out;

import gps.BT747Constants;
import gps.Txt;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.Convert;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.BT747FileName;

/**
 * @author Mario De Weerd
 * 
 * This abstract class defines the 'interface' with the BT747LogConvert class.
 * Derived classes will be able to write the desired output in the formats they
 * implement.
 */
public abstract class GPSFile {
    /**
     * The track filters used.
     */
    protected GPSFilter[] ptFilters = null;

    /**
     * When true, split file in one file per day.
     */
    protected boolean oneFilePerDay;

    /**
     * Indicates the fields active in the current record (for
     * {@link #writeRecord(GPSRecord)}).
     */
    protected GPSRecord activeFields;

    /**
     * Indicates the fields active in the files (useful for
     * {@link #writeFileHeader(String)} for example).
     */
    protected GPSRecord activeFileFields;

    /**
     * The fields that are selected for the file output.
     */
    protected GPSRecord selectedFileFields;

    /**
     * True if the current record is the first record - needed for initial file
     * creation and track handling in extending classes.
     */
    protected boolean firstRecord;

    /**
     * The basename for the output file.
     */
    protected String basename;

    /**
     * The extension for the output file.
     */
    protected String ext;

    /**
     * On certain systems, the card index.
     */
    protected int card;

    /**
     * The number of passes that must still be done on the source.
     */
    protected int nbrOfPassesToGo;

    /**
     * Total number of passes to do. This value can be changed in the
     * constructor of the extending class. The default value is 1.
     */
    protected int numberOfPasses = 1;

    /**
     * The handle to the output file.
     */
    private File outFile = null;

    /**
     * The time of the current record as a Time object. This will avoid creating
     * multiple time objects.
     */
    protected final BT747Time t = Interface.getTimeInstance();

    protected int previousDate = 0;
    protected int previousTime = 0;
    protected int nextPreviousTime = 0;
    protected boolean separateTrack = false;
    /**
     * When two points are more than this time apart, a new track segment is
     * created. The default value is Time needed between points to separate
     * segments.<br>
     * The value is in seconds.
     */
    protected int trackSepTime = 60 * 60;
    /**
     * The number of files that were created. This value is also used to detect
     * if any record has been written.
     */
    protected int filesCreated = 0;

    /**
     * When true, one file is created for each detected track. Default value is
     * false.
     * 
     * @see #trackSepTime
     */
    protected boolean oneFilePerTrack = false;
    protected boolean isMultipleFiles = false;

    /**
     * When true, the record number (record index) has to be written to the
     * output file (if the output format allows it).
     */
    protected boolean recordNbrInLogs = false;

    /**
     * The color to use if the track segment has unselected trackpoints.
     */
    protected String badTrackColor = "0000FF";
    /**
     * The color to use for valid tracks.
     */
    protected String goodTrackColor = "0000FF";

    /**
     * When true, use Imperial (English) units like yards, inches, mph, ... .
     */
    protected boolean imperial = false;

    /**
     * The filename builder allows a big flexibility in defining the output file
     * name.
     */
    protected BT747FileName filenameBuilder = new GPSDefaultFileName();

    public final static int FILE_SPLIT_NONE = 0;
    public final static int FILE_SPLIT_ONE_FILE_PER_DAY = 1;
    public final static int FILE_SPLIT_ONE_FILE_PER_TRACK = 2;

    /**
     * This function has to be called at some time to initialize the file
     * conversion. Other parameters can be set through other methods.
     * 
     * @param baseName
     *            Base name of the output file. This value will be provided to
     *            the filename builder.
     * @param extension
     *            Extension of the output file. This value will be provided to
     *            the filename builder.
     * @param fileCard
     *            Card number used on certain devices like a Palm.
     * @param fileSeparationFreq
     *            Indicates how the file must be separated.
     * 
     * @see #FILE_SPLIT_NONE
     * @see #FILE_SPLIT_ONE_FILE_PER_DAY
     * @see #FILE_SPLIT_ONE_FILE_PER_TRACK_
     * 
     */
    public void initialiseFile(final String baseName, final String extension,
            final int fileCard, final int fileSeparationFreq) {
        nbrOfPassesToGo = numberOfPasses - 1;
        ext = extension;
        basename = baseName;
        card = fileCard;
        switch (fileSeparationFreq) {
        case FILE_SPLIT_ONE_FILE_PER_DAY:
            oneFilePerDay = true;
            isMultipleFiles = true;
            break;
        case FILE_SPLIT_ONE_FILE_PER_TRACK:
            oneFilePerTrack = true;
            isMultipleFiles = true;
            break;
        case FILE_SPLIT_NONE:
        default:
            oneFilePerDay = false;
            oneFilePerTrack = false;
            break;
        }
        initPass();
    };

    /**
     * Set the track separation time.
     * 
     * @param time
     *            Seconds of unavailable positions require before deciding to
     *            hava a new track.
     */
    public final void setTrackSepTime(final int time) {
        trackSepTime = time;
    }

    /**
     * Called to set the fields that are active in the current input. This is
     * set by the internal algorithm that analyses the input file.
     * 
     * @param activeFileFieldsFormat
     */
    public final void setActiveFileFields(final GPSRecord activeFileFieldsFormat) {
        // TODO: Take into account the waypoints.
        activeFileFields = activeFileFieldsFormat;
    }

    /**
     * Set the fields that should be written to the output. This can be a
     * superset of the active file fields, but only the fields that are both
     * active and selected for output will be available in the output files.
     * This is to be configured from the application.
     * 
     * @param selectedOutputFields
     */
    public final void setOutputFields(final GPSRecord selectedOutputFields) {
        selectedFileFields = selectedOutputFields;
    }

    /**
     * Called every time the log format changes so that the appropriate header
     * information can be written to the output files. Updates the
     * {@link #activeFields} so 'super()' should be called by the extending
     * class that must add a hook to this method to write the intermediate
     * header (if needed for the output format).
     * 
     * @param f
     */
    public final void writeLogFmtHeader(final GPSRecord f) {
        activeFields = new GPSRecord(f);
    };

    /**
     * Set the track point and way point filters. To be called from the
     * application.
     * 
     * @param ourFilters
     *            Track point and way point filters.
     */
    public final void setFilters(final GPSFilter[] ourFilters) {
        ptFilters = ourFilters;
    };

    /**
     * Indicate whether Imperial units (miles, knots, mph) should be used.
     * 
     * @param useImperial
     *            when true, use imperial units.
     */
    public final void setImperial(final boolean useImperial) {
        this.imperial = useImperial;
    }

    /**
     * Check whether there is any need at all for the current record for any of
     * the position filters.
     * 
     * @return true if the record is needed.
     */
    protected boolean recordIsNeeded(final GPSRecord r) {
        boolean result = false;
        for (int i = ptFilters.length - 1; i >= 0; i--) {
            if (ptFilters[i].doFilter(r)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static GPSRecord previousRecord = null;
    private static boolean previousResult = false;

    public final boolean cachedRecordIsNeeded(final GPSRecord r) {
        if (r == previousRecord) {
            return previousResult;
        } else {
            previousRecord = r;
            return (previousResult = recordIsNeeded(r));
        }
    }

    private GPSRecord[] userWayPointList;

    /**
     * Entry must be an ordered list (UTC time).
     * 
     * @param list
     */
    public void setUserWayPointList(final GPSRecord[] list) {
        userWayPointList = list;
        initWayPointUserListSetup();
    }

    private void initWayPointUserListSetup() {
        if (userWayPointList != null && userWayPointList.length >= 1) {
            currentWayPointListIdx = 0;
        } else {
            currentWayPointListIdx = -1;
        }
    }

    public GPSRecord[] getUserWayPointList() {
        return userWayPointList;
    }

    private GPSRecord prevRecord = null;

    private int waypointTimeCorrection = 0;

    public void setWayPointTimeCorrection(final int seconds) {
        waypointTimeCorrection = seconds;
    }

    private int currentWayPointListIdx = -1;

    /**
     * Difference in number of seconds that is allowed for tagging.
     */
    private int maxDiff = 300;

    /**
     * When true, ignore any previously applied tags
     */
    private boolean overridePreviousTag = false;

    /**
     * A record is added from the input log. User way points are geotagged here
     * and inserted at the right spot.
     * <p>
     * This is not to be extended.
     * <p>
     * The provided record should no longer be referenced in the caller.<br>
     * It is supposed to be unique.
     * 
     * @param r
     */
    public final void addLogRecord(final GPSRecord r) {
        if (currentWayPointListIdx >= 0 && cachedRecordIsNeeded(r)) {
            GPSRecord prevActiveFields = activeFields;
            if (prevRecord != null) {
                boolean continueLoop;
                do {
                    continueLoop = false;
                    GPSRecord userWayPoint = userWayPointList[currentWayPointListIdx];
                    int userWayPointUTC = userWayPoint.utc
                            + waypointTimeCorrection;
                    int diffPrevious = userWayPointUTC - prevRecord.utc;
                    int diffNext = userWayPointUTC - r.utc;

                    if ((diffPrevious > 0) && (diffNext < 0)) {
                        GPSRecord ref;
                        int diff;
                        // WayPoint is in between two points.
                        if (diffPrevious < -diffNext) {
                            ref = prevRecord;
                            diff = diffPrevious;
                        } else {
                            ref = r;
                            diff = diffPrevious;
                        }
                        if ((diff <= maxDiff)
                                && (overridePreviousTag || (!userWayPoint
                                        .hasLatitude() || !userWayPoint
                                        .hasLongitude()))) {
                            userWayPoint.latitude = ref.latitude;
                            userWayPoint.longitude = ref.longitude;
                        }
                        // Update log format
                        if (!activeFields.equalsFormat(userWayPoint)) {
                            writeLogFmtHeader(userWayPoint);
                        }

                        // logFormat = userWayPoint.getLogFormat();
                        GPSRecord utcCorrectedRecord = userWayPoint
                                .cloneRecord();
                        utcCorrectedRecord.utc = userWayPointUTC;
                        writeRecord(utcCorrectedRecord);
                        nextWayPointIdx();

                        continueLoop = currentWayPointListIdx != -1;
                    }
                } while (continueLoop);
            } else {
                boolean continueLoop = false;
                do {
                    GPSRecord userWayPoint = userWayPointList[currentWayPointListIdx];
                    int userWayPointUTC = userWayPoint.utc
                            + waypointTimeCorrection;
                    // bt747.sys.Generic.debug(userWayPoint.toString());
                    continueLoop = false;
                    if (userWayPointUTC < r.utc) {
                        if (!activeFields.equalsFormat(userWayPoint)) {
                            writeLogFmtHeader(userWayPoint);
                        }
                        GPSRecord utcCorrectedRecord = userWayPoint
                                .cloneRecord();
                        utcCorrectedRecord.utc = userWayPointUTC;
                        writeRecord(utcCorrectedRecord);
                        nextWayPointIdx();
                        if (currentWayPointListIdx >= 0) {
                            continueLoop = true;
                        }
                    }
                } while (continueLoop);

            }
            if (prevActiveFields != activeFields) {
                // Change occurred - revert.
                if (!activeFields.equalsFormat(prevActiveFields)) {
                    writeLogFmtHeader(prevActiveFields);
                }
            }
        }
        if (activeFields.utc != 0) {
            r.utc += timeOffsetSeconds;
        }
        writeRecord(r);
        if (cachedRecordIsNeeded(r)) {
            prevRecord = r;
        }
    }

    private final void addUntreatedWayPoints() {
        while (currentWayPointListIdx >= 0) {
            GPSRecord userWayPoint = userWayPointList[currentWayPointListIdx];
            if (userWayPoint.hasLatitude() && userWayPoint.hasLongitude()) {
                if (!activeFields.equalsFormat(userWayPoint)) {
                    writeLogFmtHeader(userWayPoint);
                }
                int userWayPointUTC = userWayPoint.utc + waypointTimeCorrection;
                GPSRecord utcCorrectedRecord = userWayPoint.cloneRecord();
                utcCorrectedRecord.utc = userWayPointUTC;
                writeRecord(utcCorrectedRecord);
            }
            nextWayPointIdx();
        }
    }

    private void nextWayPointIdx() {
        if (currentWayPointListIdx < userWayPointList.length - 1) {
            currentWayPointListIdx++;
        } else {
            currentWayPointListIdx = -1;
        }
    }

    /**
     * Called for any new position. This method is called by the input analysis
     * class and should be extended for the output format. The extension must
     * call super(). That will make sure that the appropriate files are opened,
     * and the {@link #t} property set.
     * 
     * @param r
     *            information regarding the position.
     */
    public void writeRecord(final GPSRecord r) {
        String extraExt; // Extra extension for log file
        boolean newDate = false;
        int dateref = 0;
        previousTime = nextPreviousTime;
        // bt747.sys.Generic.debug("Adding\n"+r.toString());

        if (activeFields.utc != 0) {
            t.setUTCTime(r.utc); // Initialization needed later too!
            if (oneFilePerDay || oneFilePerTrack) {
                dateref = (t.getYear() << 14) + (t.getMonth() << 7)
                        + t.getDay(); // year *
                // 16384 +
                // month *
                // 128 + day
                newDate = (dateref > previousDate);
            }

        }

        if (((((oneFilePerDay && newDate) && activeFields.utc != 0) || firstRecord) || (oneFilePerTrack
                && activeFields.utc != 0 && (r.utc > previousTime
                + trackSepTime)))
                && cachedRecordIsNeeded(r)) {
            boolean createOK = true;
            previousDate = dateref;

            if (activeFields.utc != 0) {
                if ((r.utc < 24 * 3600) // No date provided by log.
                        || (t.getYear() > 2000)) {
                    extraExt = "-" + Convert.toString(t.getYear())
                            + (t.getMonth() < 10 ? "0" : "")
                            + Convert.toString(t.getMonth())
                            + (t.getDay() < 10 ? "0" : "")
                            + Convert.toString(t.getDay());
                    if (oneFilePerTrack) {
                        extraExt += "_" + (t.getHour() < 10 ? "0" : "")
                                + Convert.toString(t.getHour())
                                + (t.getMinute() < 10 ? "0" : "")
                                + Convert.toString(t.getMinute());
                    }
                } else {
                    extraExt = "";
                    createOK = false;
                }
            } else {
                extraExt = "";
            }
            if (!firstRecord && (extraExt.length() != 0)) {
                // newDate -> close previous file
                if (nbrOfPassesToGo == 0) {
                    // Vm.debug("Finalize:"+m_File.getPath());
                    finaliseFile();
                } else {
                    // Vm.debug("Close"+m_File.getPath());
                    closeFile();
                }
            } else {
                firstRecord = !createOK;
            }

            if (createOK) {
                boolean createNewFile = numberOfPasses - 1 == nbrOfPassesToGo;

                int error = createFile(r.utc, extraExt, createNewFile);

                if (error == BT747Constants.NO_ERROR) {
                    filesCreated += 1;
                    try {
                        if (createNewFile) {
                            // New file
                            writeFileHeader("GPS" + extraExt); // First time
                            // this file
                            // is
                            // opened.
                        } else {
                            // Append to existing file (file open must be
                            // append)
                            // outFile.setPos(outFile.getSize());
                        }
                        writeLogFmtHeader(activeFields);
                        writeDataHeader();
                    } catch (Exception e) {
                        Generic.debug("Initial header or append", e);
                        // TODO: handle exception
                    }
                }

            }
        }

        if (activeFields.utc != 0 && recordIsNeeded(r)) {
            nextPreviousTime = r.utc;
        }
    };

    /**
     * Method called when all parsing is finished and the last file has to be
     * closed.<br>
     * This method can be extended. It closes the output file by default, so
     * super() should be called once other handling is finished.
     */
    public void finaliseFile() {
        if (outFile != null) {
            try {
                outFile.close();
            } catch (Exception e) {
                Generic.debug("finaliseFile", e);
                // TODO: handle exception
            }
            outFile = null;
        }
    }

    /**
     * Called when the input file has been parsed. This method checks if other
     * passes are needed and performs some closing operations if that is not the
     * case.<br>
     * This method must be extended, call super.nextPass() and then update
     * nbrOfPassesToGo.
     * 
     * @return true if analysis must go on.
     */
    public boolean nextPass() {
        // bt747.sys.Generic.debug("Next pass");
        addUntreatedWayPoints();
        if (nbrOfPassesToGo == 0) {
            // Last Pass done
            finaliseFile();
        } else {
            // More passes to go.
            if (isOpen()) {
                closeFile();
            }
        }
        initPass();
        return false;
    };

    /**
     * Initialise settings before a pass.
     */
    private final void initPass() {
        previousDate = 0;
        previousTime = 0;
        nextPreviousTime = 0;
        firstRecord = true;
        initWayPointUserListSetup();
        prevRecord = null;
    }

    /**
     * This method is to be extended as needed and is called when the file
     * header must be written. This method is called only once for each output
     * file.
     * 
     * @param Name
     *            Identifier for the output file that can be used in the header.
     */
    protected void writeFileHeader(final String Name) {
    };

    /**
     * Called at the start of a new data section.
     */
    protected void writeDataHeader() {
    };

    /**
     * Called at the end of a data section.
     */
    protected void writeDataFooter() {
    };

    protected int createFile(final int utc, final String extra_ext,
            final boolean createNewFile) {
        String fileName;
        fileName = filenameBuilder.getOutputFileName(basename, utc, ext,
                extra_ext);
        int error = BT747Constants.NO_ERROR;

        try {
            if (createNewFile) {
                File tmpFile = new File(fileName, File.DONT_OPEN, card);
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
        } catch (Exception e) {
            Generic.debug("File deletion", e);
            // TODO: handle problem
        }
        try {
            int mode = createNewFile ? File.CREATE : File.WRITE_ONLY;
            outFile = new File(fileName, mode, card);
        } catch (Exception e) {
            Generic.debug("File creation", e);
            // TODO: handle exception
        }
        if (outFile != null && !outFile.isOpen()) {
            errorInfo = fileName + "|" + outFile.getLastError();
            error = BT747Constants.ERROR_COULD_NOT_OPEN;
            outFile = null;
        }
        return error;
    }

    protected void closeFile() {
        writeDataFooter();
        try {
            if (outFile != null) {
                outFile.close();
            }
        } catch (Exception e) {
            Generic.debug("closeFile", e);
            // TODO: handle exception
        }
    }

    protected boolean isOpen() {
        return outFile != null;
    }

    protected void writeTxt(final String s) {
        try {
            if (outFile != null) {
                outFile.writeBytes(s.getBytes(), 0, s.length());
            } else {
                Generic.debug(Txt.WRITING_CLOSED, null);
            }
        } catch (Exception e) {
            Generic.debug("writeTxt", e);
        }
    }

    public boolean needPassToFindFieldsActivatedInLog() {
        return false;
    }

    /**
     * @return Returns the badTrackColor.
     */
    public final String getBadTrackColor() {
        return badTrackColor;
    }

    /**
     * @param badTrackColor
     *            The badTrackColor to set.
     */
    public final void setBadTrackColor(final String badTrackColor) {
        this.badTrackColor = badTrackColor;
        checkColors();
    }

    /**
     * @return Returns the goodTrackColor.
     */
    public final String getGoodTrackColor() {
        return goodTrackColor;
    }

    /**
     * @param goodTrackColor
     *            The goodTrackColor to set.
     */
    public final void setGoodTrackColor(final String goodTrackColor) {
        this.goodTrackColor = goodTrackColor;
        checkColors();
    }

    protected boolean ignoreBadPoints = false;

    private void checkColors() {
        ignoreBadPoints = goodTrackColor.equalsIgnoreCase(badTrackColor);
    }

    /**
     * @return Returns the filesCreated.
     */
    public final int getFilesCreated() {
        return filesCreated;
    }

    /**
     * @return Returns the recordNbrInLogs.
     */
    public final boolean isRecordNbrInLogs() {
        return recordNbrInLogs;
    }

    /**
     * @param recordNbrInLogs
     *            The recordNbrInLogs to set.
     */
    public final void setRecordNbrInLogs(final boolean recordNbrInLogs) {
        this.recordNbrInLogs = recordNbrInLogs;
    }

    protected boolean addLogConditionInfo = false;

    public final boolean isAddLogConditionInfo() {
        return addLogConditionInfo;
    }

    public final void setAddLogConditionInfo(final boolean addLogConditionInfo) {
        this.addLogConditionInfo = addLogConditionInfo;
    }

    protected String errorInfo;

    public final String getErrorInfo() {
        return errorInfo;
    }

    protected boolean isTrkComment = true;

    public final void setIncludeTrkComment(final boolean isTrkComment) {
        this.isTrkComment = isTrkComment;
    }

    protected boolean isIncludeTrkName = true;

    public final void setIncludeTrkName(final boolean isIncludeTrkName) {
        this.isIncludeTrkName = isIncludeTrkName;
    }

    public final void setFilenameBuilder(final BT747FileName filenameBuilder) {
        this.filenameBuilder = filenameBuilder;
    }

    private int timeOffsetSeconds = 0;

    /**
     * The time offset to apply to the output records in seconds.
     * 
     * @param offset
     *            The time offset in seconds.
     */
    public final void setTimeOffset(final int offset) {
        timeOffsetSeconds = offset;
    }

}
