/**
 * 
 */
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747Path;

/*
 * *
 * 
 * @author Mario
 */
public interface GPSFileConfInterface {

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
    public void initialiseFile(final BT747Path baseName, final String extension,
            final int fileSeparationFreq);

    /**
     * Set the track separation time.
     * 
     * @param time
     *            Seconds of unavailable positions require before deciding to
     *            hava a new track.
     */
    public void setTrackSepTime(final int time);

    /**
     * Called to set the fields that are active in the current input. This is
     * set by the internal algorithm that analyses the input file.
     * 
     * @param activeFileFieldsFormat
     */
    public void setActiveFileFields(final GPSRecord activeFileFieldsFormat);

    /**
     * Set the fields that should be written to the output. This can be a
     * superset of the active file fields, but only the fields that are both
     * active and selected for output will be available in the output files.
     * This is to be configured from the application.
     * 
     * @param selectedOutputFields
     */
    public void setOutputFields(final GPSRecord selectedOutputFields);

    /**
     * Set the track point and way point filters. To be called from the
     * application.
     * 
     * @param ourFilters
     *            Track point and way point filters.
     */
    public void setFilters(final GPSFilter[] ourFilters);

    /**
     * Indicate whether Imperial units (miles, knots, mph) should be used.
     * 
     * @param useImperial
     *            when true, use imperial units.
     */
    public void setImperial(final boolean useImperial);

    /**
     * Entry must be an ordered list (UTC time).
     * 
     * @param list
     */
    public void setUserWayPointList(final GPSRecord[] list);

    public void setWayPointTimeCorrection(final int seconds);

    /**
     * @param badTrackColor
     *            The badTrackColor to set.
     */
    public void setBadTrackColor(final String badTrackColor);

    /**
     * @param goodTrackColor
     *            The goodTrackColor to set.
     */
    public void setGoodTrackColor(final String goodTrackColor);

    /**
     * @param recordNbrInLogs
     *            The recordNbrInLogs to set.
     */
    public void setRecordNbrInLogs(final boolean recordNbrInLogs);

    public void setAddLogConditionInfo(final boolean addLogConditionInfo);

    public void setIncludeTrkComment(final boolean isTrkComment);

    public void setIncludeTrkName(final boolean isIncludeTrkName);

    public void setFilenameBuilder(final BT747FileName filenameBuilder);

    /**
     * The time offset to apply to the output records in seconds.
     * 
     * @param offset
     *            The time offset in seconds.
     */
    public void setTimeOffset(final int offset);

    /**
     * @param maxDiff
     *            the maxDiff to set
     */
    public void setMaxDiff(final int maxDiff);

    /**
     * @param overridePreviousTag
     *            the overridePreviousTag to set
     */
    public void setOverridePreviousTag(final boolean overridePreviousTag);

    public void setParamObject(final GPSConversionParameters paramObject);

    public GPSConversionParameters getParamObject();

}
