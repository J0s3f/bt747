// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.out;

import gps.BT747Constants;
import gps.Txt;
import gps.convert.ExternalUtils;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.model.AppSettings;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 * 
 *         This abstract class defines the 'interface' with the
 *         BT747LogConvert class. Derived classes will be able to write the
 *         desired output in the formats they implement.
 * 
 *         Refactoring / Discussion: Either each implementation of this
 *         abstract class is a Strategy Design Pattern. Or this abstract class
 *         could be a concrete class and be a Strategy itself. The concrete
 *         classes currently deriving from this class would be Builders. This
 *         will be given some thought. It is not urgent to make the change.
 */
public abstract class GPSFile implements GPSFileInterface {
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
    private GPSRecord activeFields = GPSRecord.getLogFormatRecord(0);

    /**
     * Indicates the fields active in the files (useful for
     * {@link #writeFileHeader(String)} for example).
     */
    protected GPSRecord activeFileFields;

    /**
     * The fields that are selected for the file output.
     */
    protected GPSRecord selectedFileFields;

    private GPSRecord mySelectedFileFields;

    /**
     * True if the current record is the first record - needed for initial
     * file creation and track handling in extending classes.
     */
    protected boolean firstRecord;

    /**
     * The basename for the output file.
     */
    protected BT747Path basename;

    /**
     * The extension for the output file.
     */
    protected String ext;

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
     * The time of the current record as a Time object. This will avoid
     * creating multiple time objects.
     */
    protected final BT747Time t = JavaLibBridge.getTimeInstance();

    protected int previousDate = 0;
    protected int previousTime = 0;
    protected int nextPreviousTime = 0;

    private double previousLat = 0;
    private double previousLon = 0;

    /**
     * When two points are more than this time apart, a new track segment is
     * created. The default value is Time needed between points to separate
     * segments.<br>
     * The value is in seconds.
     */
    protected int trackSepTime = 60 * 60;
    /**
     * The number of files that were created. This value is also used to
     * detect if any record has been written.
     */
    protected int filesCreated = 0;

    /**
     * When true, one file is created for each detected track. Default value
     * is false.
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
     * The filename builder allows a big flexibility in defining the output
     * file name.
     */
    protected BT747FileName filenameBuilder = new GPSDefaultFileName();

    public final static int FILE_SPLIT_NONE = 0;
    public final static int FILE_SPLIT_ONE_FILE_PER_DAY = 1;
    public final static int FILE_SPLIT_ONE_FILE_PER_TRACK = 2;

    private boolean calcDistance = false;
    private int splitDistance = 0;
    protected double distanceWithPrevious;
    private int distCalcMode;

    /**
     * When true, create missing fields in the record.
     */
    private boolean createFields = false;
    
    private final BT747HashSet filenames = JavaLibBridge.getHashSetInstance();

    protected boolean isTrkComment = true;
    protected boolean isWayComment = true;

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
     * @param fileSeparationFreq
     *            Indicates how the file must be separated.
     * 
     * @see bt747.model.Model#SPLIT_ONE_FILE
     * @see bt747.model.Model#SPLIT_ONE_FILE_PER_DAY
     * @see bt747.model.Model#SPLIT_ONE_FILE_PER_TRACK
     */
    public void initialiseFile(final BT747Path baseName,
            final String extension, final int fileSeparationFreq) {
        nbrOfPassesToGo = numberOfPasses - 1;
        ext = extension;
        basename = baseName;
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
        if (getParamObject().hasParam(
                GPSConversionParameters.NEW_TRACK_WHEN_LOG_ON)) {
            isTrackSplitOnLogOn = getParamObject().getBoolParam(
                    GPSConversionParameters.NEW_TRACK_WHEN_LOG_ON);
        } else {
            isTrackSplitOnLogOn = false;
        }
        if (getParamObject().hasParam(GPSConversionParameters.SPLIT_DISTANCE)) {
            splitDistance = getParamObject().getIntParam(
                    GPSConversionParameters.SPLIT_DISTANCE);
        } else {
            splitDistance = 0;
        }
        if (getParamObject().hasParam(GPSConversionParameters.DISTANCE_CALC_MODE)) {
            distCalcMode = getParamObject().getIntParam(
                    GPSConversionParameters.DISTANCE_CALC_MODE);
        } else {
            distCalcMode = AppSettings.DISTANCE_CALC_MODE_NONE;
        }
        if (getParamObject().hasParam(GPSConversionParameters.CREATE_FIELDS)) {
        	createFields = getParamObject().getBoolParam(GPSConversionParameters.CREATE_FIELDS);
        } else {
            createFields = false;
        }
        
        if (splitDistance != 0 || distCalcMode==AppSettings.DISTANCE_CALC_MODE_ALWAYS) {
            // When splitting according to distance, we need to calculate
            // the distance between positions.
            calcDistance = true;
        }


        if (getParamObject().hasParam(GPSConversionParameters.WAY_COMMENT)) {
            isWayComment = getParamObject().getBoolParam(
                    GPSConversionParameters.WAY_COMMENT);
        }

        if (getParamObject().hasParam(GPSConversionParameters.TRK_COMMENT)) {
            isTrkComment = getParamObject().getBoolParam(
                    GPSConversionParameters.TRK_COMMENT);
        }

        initPass();
    };

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setTrackSepTime(int)
     */
    public final void setTrackSepTime(final int time) {
        trackSepTime = time;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setActiveFileFields(gps.log.GPSRecord)
     */
    public final void setActiveFileFields(
            final GPSRecord activeFileFieldsFormat) {
        // TODO: Take into account the waypoints.
        boolean willHaveDistanceInRecord;
        willHaveDistanceInRecord = (distCalcMode == AppSettings.DISTANCE_CALC_MODE_ALWAYS)
                || (distCalcMode == AppSettings.DISTANCE_CALC_MODE_WHEN_MISSING);

        activeFileFields = activeFileFieldsFormat.cloneRecord();

        if (willHaveDistanceInRecord) {
            activeFileFields.setDistance(0);
        }
        if(createFields) {
        	setMissingFields(activeFileFields);
        }
        updateFields();
    }

    private final void updateFields() {
        if (activeFileFields != null && mySelectedFileFields != null)
            selectedFileFields = GPSRecord.getCommonFormat(activeFileFields,
                    mySelectedFileFields);
        else {
            selectedFileFields = mySelectedFileFields;
            if (selectedFileFields == null) {
                selectedFileFields = activeFileFields;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setOutputFields(gps.log.GPSRecord)
     */
    public final void setOutputFields(final GPSRecord selectedOutputFields) {
        mySelectedFileFields = selectedOutputFields;
        updateFields();
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
    public void writeLogFmtHeader(final GPSRecord f) {
        activeFields = new GPSRecord(f);
        updateFields();
    };

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setFilters(gps.log.GPSFilter[])
     */
    public final void setFilters(final GPSFilter[] ourFilters) {
        ptFilters = ourFilters;
    };

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setImperial(boolean)
     */
    public final void setImperial(final boolean useImperial) {
        imperial = useImperial;
    }

    /**
     * Check whether there is any need at all for the current record for any
     * of the position filters.
     * 
     * @return true if the record is needed.
     */
    protected boolean recordIsNeeded(final GPSRecord r) {
        boolean result;
        if (ptFilters != null) {
            result = false;
            for (int i = ptFilters.length - 1; i >= 0; i--) {
                if (ptFilters[i].doFilter(r)) {
                    result = true;
                    break;
                }
            }
        } else {
            result = true;
        }
        return result;
    }

    private static GPSRecord previousRecord = null;
    private static boolean previousResult = false;

    public final boolean cachedRecordIsNeeded(final GPSRecord r) {
        if (r == GPSFile.previousRecord) {
            return GPSFile.previousResult;
        } else {
            GPSFile.previousRecord = r;
            return (GPSFile.previousResult = recordIsNeeded(r));
        }
    }

    private GPSRecord[] userWayPointList;

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setUserWayPointList(gps.log.GPSRecord
     * [])
     */
    public void setUserWayPointList(final GPSRecord[] list) {
        userWayPointList = list;
        initWayPointUserListSetup();
    }

    private void initWayPointUserListSetup() {
        if ((userWayPointList != null) && (userWayPointList.length >= 1)) {
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

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setWayPointTimeCorrection(int)
     */
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
     * When 'true', it was detected that the device switched on.
     */
    protected boolean logOn = false;
    /**
     * When 'true', then a track split must occur when the logger was switched
     * on.
     */
    private boolean isTrackSplitOnLogOn = false;
    /**
     * When 'true', the different conditions 'impose' a split of the track to
     * the derived classes.
     */
    protected boolean needsToSplitTrack = false;
    
    private final void setMissingFields(final GPSRecord r) {
		if(!r.hasLatitude()) {
			r.setLatitude(0.0);
		}
		if(!r.hasLongitude()) {
			r.setLongitude(0.0);
		}
		if(!r.hasRcr()) {
			r.setRcr(BT747Constants.RCR_TIME_MASK);
		}
		if(!r.hasPdop()) {
			r.setPdop(0);
		}
		if(!r.hasHdop()) {
			r.setHdop(0);
		}
		if(!r.hasVdop()) {
			r.setVdop(0);
		}
		if(!r.hasHeight()) {
			r.setHeight(0);
		}
		if(!r.hasSpeed()) {
			r.setSpeed(0);
		}
		if(!r.hasValid()) {
			if (r.getLatitude() != 0 || r.getLongitude() != 0) {
				r.setValid(BT747Constants.VALID_SPS_MASK);
			} else {
				r.setValid(BT747Constants.VALID_NO_FIX_MASK);
			}
		}
    }

    /**
     * A record is added from the input log. User way points are geotagged
     * here and inserted at the right spot.
     * <p>
     * This is not to be extended.
     * <p>
     * The provided record should no longer be referenced in the caller.<br>
     * It is supposed to be unique.
     * 
     * @param r
     */
    public void addLogRecord(final GPSRecord r) {
    	if(createFields) {
    		setMissingFields(r);
    	}
        if (r.hasVoxStr()) {
            if (r.isLogOn()) {
                logOn = true;
                return;
            }
        }
        if (r.hasUtc()) {
            r.utc += timeOffsetSeconds;
        }
        if ((currentWayPointListIdx >= 0) && cachedRecordIsNeeded(r)
                && r.hasUtc()) {
            final GPSRecord prevActiveFields = activeFields;
            if (prevRecord != null) {
                boolean continueLoop;
                do {
                    continueLoop = false;
                    final GPSRecord userWayPoint = userWayPointList[currentWayPointListIdx];
                    boolean doWriteRecord = false;
                    final int userWayPointUTC = userWayPoint.tagutc
                            + waypointTimeCorrection; // UTC time now //
                    // CommonOut.getDateTimeStr(userWayPointUTC)
                    final int diffPrevious = userWayPointUTC
                            - prevRecord.getUtc() // CommonOut.getDateTimeStr(prevRecord.utc)
                            + timeOffsetSeconds; // - prevRecord.utc +
                    // r.utc
                    // +timeOffsetSeconds
                    int diffNext = userWayPointUTC - r.getUtc()
                            + timeOffsetSeconds; // If > 0, current
                    // position is
                    // earlier.

                    if ((diffPrevious >= 0) && (diffNext < 0)) {
                        GPSRecord ref;
                        int diff;
                        int gpstime;
                        // WayPoint is in between two points.
                        if (diffPrevious < -diffNext) {
                            ref = prevRecord;
                            gpstime = prevRecord.getUtc() - timeOffsetSeconds;
                            diff = diffPrevious;
                        } else {
                            ref = r;
                            diff = -diffNext;
                            gpstime = r.getUtc() - timeOffsetSeconds;
                        }
                        if ((diff <= maxDiff)
                                && (overridePreviousTag || (!userWayPoint
                                        .hasPosition()))) {
                            userWayPoint.tagFromRecord(ref);
                            userWayPoint.utc = gpstime;
                        }
                        doWriteRecord = true;
                    } else if (diffPrevious < -maxDiff) {
                        // Skip record
                        doWriteRecord = true;
                        // Time added later.
                    }
                    if (doWriteRecord) {
                        if (userWayPoint.hasLatitude()
                                && userWayPoint.hasLongitude()) {
                            // Update log format
                            if (!activeFields.equalsFormat(userWayPoint)) {
                                writeLogFmtHeader(userWayPoint);
                            }
                            userWayPoint.utc -= timeOffsetSeconds;
                            if (userWayPoint.hasUtc()) {
                                userWayPoint.utc += timeOffsetSeconds;
                            }
                            writeRecord(userWayPoint);
                        }
                        nextWayPointIdx();
                        continueLoop = currentWayPointListIdx != -1;
                    }
                } while (continueLoop);
                // } else {
                // boolean continueLoop = false;
                // do {
                // GPSRecord userWayPoint =
                // userWayPointList[currentWayPointListIdx];
                // int userWayPointUTC = userWayPoint.tagutc
                // + waypointTimeCorrection;
                // // bt747.sys.Generic.debug(userWayPoint.toString());
                // continueLoop = false;
                // if (userWayPointUTC < r.utc) {
                // if (!activeFields.equalsFormat(userWayPoint)) {
                // writeLogFmtHeader(userWayPoint);
                // }
                // //userWayPoint.utc = userWayPointUTC;
                // writeUtc0Record(userWayPoint);
                // nextWayPointIdx();
                // if (currentWayPointListIdx >= 0) {
                // continueLoop = true;
                // }
                // }
                // } while (continueLoop);

            }
            if (prevActiveFields != activeFields) {
                // Change occurred - revert.
                if (!activeFields.equalsFormat(prevActiveFields)) {
                    writeLogFmtHeader(prevActiveFields);
                }
            }
        }
        writeRecord(r);
        if (cachedRecordIsNeeded(r)) {
            prevRecord = r;
            logOn = false; // Reset value
        }
    }

    /**
     * While a number of WayPoints (files, ...) have been inserted at the
     * right position in the tracklog, some are not in the scope of the track
     * and are 'appended' in this method.
     */
    private final void addUntreatedWayPoints() {
        while (currentWayPointListIdx >= 0) {
            final GPSRecord userWayPoint = userWayPointList[currentWayPointListIdx];
            if (userWayPoint.hasLatitude() && userWayPoint.hasLongitude()) {
                if (!activeFields.equalsFormat(userWayPoint)) {
                    writeLogFmtHeader(userWayPoint);
                }
                // int userWayPointUTC = userWayPoint.utc +
                // waypointTimeCorrection;
                // userWayPoint.utc = userWayPointUTC;
                writeRecord(userWayPoint);
            }
            nextWayPointIdx();
        }
    }

    /**
     * Calculates the next index in the user waypoint list.
     */
    private void nextWayPointIdx() {
        if (currentWayPointListIdx < userWayPointList.length - 1) {
            currentWayPointListIdx++;
        } else {
            currentWayPointListIdx = -1;
        }
    }

    /**
     * Called for any new position. This method is called by the input
     * analysis class and should be extended for the output format. The
     * extension must call super(). That will make sure that the appropriate
     * files are opened, and the {@link #t} property set.
     * 
     * @param r
     *            information regarding the position.
     */
    public void writeRecord(final GPSRecord r) {
        String extraExt; // Extra extension for log file
        boolean newDate = false;
        int dateref = 0;
        previousTime = nextPreviousTime;
        needsToSplitTrack = (logOn && isTrackSplitOnLogOn);
        boolean firstRecordForDistance = firstRecord;

        // bt747.sys.Generic.debug("Adding\n"+r.toString());

        if (r.hasUtc()) {
            t.setUTCTime(r.getUtc()); // Initialization needed later too!
            if (oneFilePerDay || oneFilePerTrack) {
                dateref = (t.getYear() << 14) + (t.getMonth() << 7)
                        + t.getDay(); // year *
                // 16384 +
                // month *
                // 128 + day
                newDate = (dateref > previousDate);
            }
            if (trackSepTime != 0) {
                needsToSplitTrack |= ((r

                .getUtc() > previousTime + trackSepTime));
            }
        }

        if (((((oneFilePerDay && newDate) && activeFields.hasUtc()) || firstRecord) || (oneFilePerTrack && needsToSplitTrack))
                && cachedRecordIsNeeded(r)) {
            boolean createOK = true;
            needsToSplitTrack = false; // Doing the split now.
            previousDate = dateref;

            if (r.hasUtc()) {
                if ((r.getUtc() < 24 * 3600) // No date provided by log.
                        || (t.getYear() > 2000)) {
                    extraExt = "-" + t.getYear()
                            + (t.getMonth() < 10 ? "0" : "") + t.getMonth()
                            + (t.getDay() < 10 ? "0" : "") + t.getDay();
                    if (oneFilePerTrack) {
                        extraExt += "_" + (t.getHour() < 10 ? "0" : "")
                                + t.getHour()
                                + (t.getMinute() < 10 ? "0" : "")
                                + t.getMinute();
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
                final boolean createNewFile = numberOfPasses - 1 == nbrOfPassesToGo;

                final int error = createFile(r.utc, extraExt, createNewFile);

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
                    } catch (final Exception e) {
                        Generic.debug("Initial header or append", e);
                        // TODO: handle exception
                    }
                }

            }
        }

        boolean updateDistanceInRecord;
        updateDistanceInRecord =
            !(distCalcMode==AppSettings.DISTANCE_CALC_MODE_NONE) && (
                    (distCalcMode==AppSettings.DISTANCE_CALC_MODE_ALWAYS) ||
            (!r.hasDistance() && distCalcMode == AppSettings.DISTANCE_CALC_MODE_WHEN_MISSING)
            );
        if (calcDistance
                || updateDistanceInRecord) {
            if (cachedRecordIsNeeded(r) && r.hasPosition()) {
                if (!firstRecordForDistance) {
                    distanceWithPrevious = ExternalUtils.earthDistance(
                            r.getLatitude(), r.getLongitude(), previousLat,
                            previousLon);
                    if (splitDistance != 0) {
                        needsToSplitTrack |= (splitDistance <= distanceWithPrevious);
                        // if ((splitDistance <= distanceWithPrevious)) {
                        //
                        // Generic.debug(r.getRecCount()
                        // + ":"
                        // + previousLat
                        // + " "
                        // + previousLon
                        // + " "
                        // + nextPreviousLat
                        // + " "
                        // + nextPreviousLon
                        // + " = "
                        // + Conv.earthDistance(previousLat,
                        // previousLon, nextPreviousLat,
                        // nextPreviousLon));
                        // }
                    }
                    if(updateDistanceInRecord) {
                        r.setDistance(distanceWithPrevious);
                    }
                }
            }
        }

        if (cachedRecordIsNeeded(r)) {
            if(r.hasPosition()) {
                previousLat = r.getLatitude();
                previousLon = r.getLongitude();
            }
            if ((r.hasUtc())) {
                nextPreviousTime = r.utc;
            }
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
            } catch (final Exception e) {
                Generic.debug("finaliseFile", e);
                // TODO: handle exception
            }
            outFile = null;
        }
    }

    /**
     * Called when the input file has been parsed. This method checks if other
     * passes are needed and performs some closing operations if that is not
     * the case.<br>
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
        previousLat = 0;
        previousLon = 0;
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
     *            Identifier for the output file that can be used in the
     *            header.
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

    private BT747Path currentFileName;

    protected BT747Path getCurrentFileName() {
        return currentFileName;
    }

    protected int createFile(final int utc, final String extra_ext,
            final boolean createNewFile) {
        currentFileName = null;
        BT747Path path;
        path = filenameBuilder.getOutputFileName(basename, utc, ext,
                extra_ext);
        int error = BT747Constants.NO_ERROR;

        try {
            if (createNewFile) {
                final File tmpFile = new File(path, File.DONT_OPEN);
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
        } catch (final Exception e) {
            Generic.debug("File deletion", e);
            // TODO: handle problem
        }
        try {
            final int mode = createNewFile ? File.CREATE : File.WRITE_ONLY;
            outFile = new File(path, mode);
        } catch (final Exception e) {
            Generic.debug("File creation", e);
            // TODO: handle exception
        }
        if ((outFile != null) && !outFile.isOpen()) {
            errorInfo = path + "|" + outFile.getLastError();
            error = BT747Constants.ERROR_COULD_NOT_OPEN;
            outFile = null;
        } else {
            currentFileName = path;
            filenames.add(currentFileName);
        }
        return error;
    }

    /**
     * Perform closing file operations (write footer, close file).
     */
    protected void closeFile() {
        writeDataFooter();
        try {
            if (outFile != null) {
                outFile.close();
            }
        } catch (final Exception e) {
            Generic.debug("closeFile", e);
            // TODO: handle exception
        }
    }

    /**
     * Check status of output file.
     * 
     * @return true if the output file is 'open'.
     */
    protected boolean isOpen() {
        return outFile != null;
    }

    /**
     * Write some text to the output file.
     * 
     * @param s
     */
    protected void writeTxt(final String s) {
        try {
            if (outFile != null) {
                outFile.writeBytes(s.getBytes(), 0, s.length());
            } else {
                Generic.debug(Txt.WRITING_CLOSED, null);
            }
        } catch (final Exception e) {
            Generic.debug("writeTxt", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.in.GPSFileConverterInterface#needPassToFindFieldsActivatedInLog
     * ()
     */
    public boolean needPassToFindFieldsActivatedInLog() {
        return false;
    }

    /**
     * @return Returns the badTrackColor.
     */
    public final String getBadTrackColor() {
        return badTrackColor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setBadTrackColor(java.lang.String)
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setGoodTrackColor(java.lang.String)
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
    public final int getNbrFilesCreated() {
        int nbr = filenames.count();
        if (nbr == 0) {
            nbr = filesCreated;  // Some output filters update this one.
        }
        return nbr;
    }

    /**
     * @return Returns the filesCreated.
     */
    public final BT747HashSet getFilesCreated() {
        return filenames;
    }

    /**
     * @return Returns the recordNbrInLogs.
     */
    public final boolean isRecordNbrInLogs() {
        return recordNbrInLogs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setRecordNbrInLogs(boolean)
     */
    public final void setRecordNbrInLogs(final boolean recordNbrInLogs) {
        this.recordNbrInLogs = recordNbrInLogs;
    }

    protected boolean addLogConditionInfo = false;

    public final boolean isAddLogConditionInfo() {
        return addLogConditionInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setAddLogConditionInfo(boolean)
     */
    public final void setAddLogConditionInfo(final boolean addLogConditionInfo) {
        this.addLogConditionInfo = addLogConditionInfo;
    }

    protected String errorInfo;

    public final String getErrorInfo() {
        return errorInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setIncludeTrkComment(boolean)
     */
    public final void setIncludeTrkComment(final boolean isTrkComment) {
        this.isTrkComment = isTrkComment;
    }

    protected boolean isIncludeTrkName = true;

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setIncludeTrkName(boolean)
     */
    public final void setIncludeTrkName(final boolean isIncludeTrkName) {
        this.isIncludeTrkName = isIncludeTrkName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setFilenameBuilder(bt747.sys.interfaces
     * .BT747FileName)
     */
    public final void setFilenameBuilder(final BT747FileName filenameBuilder) {
        this.filenameBuilder = filenameBuilder;
    }

    protected int timeOffsetSeconds = 0;

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setTimeOffset(int)
     */
    public final void setTimeOffset(final int offset) {
        timeOffsetSeconds = offset;
    }

    /**
     * @return the maxDiff
     */
    public final int getMaxDiff() {
        return maxDiff;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setMaxDiff(int)
     */
    public final void setMaxDiff(final int maxDiff) {
        this.maxDiff = maxDiff;
    }

    /**
     * @return the overridePreviousTag
     */
    public final boolean isOverridePreviousTag() {
        return overridePreviousTag;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setOverridePreviousTag(boolean)
     */
    public final void setOverridePreviousTag(final boolean overridePreviousTag) {
        this.overridePreviousTag = overridePreviousTag;
    }

    /**
     * Generic parameter object.
     */
    protected GPSConversionParameters paramObject = new GPSConversionParameters();

    public final GPSConversionParameters getParamObject() {
        return paramObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @seegps.log.out.GPSFileConfInterface#setParamObject(gps.log.out.
     * GPSConversionParameters)
     */
    public final void setParamObject(final GPSConversionParameters paramObject) {
        this.paramObject = paramObject;
    }

}
