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
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
package gps.log.out;

import gps.BT747Constants;
import gps.Txt;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.generic.Generic;
import bt747.io.File;
import bt747.sys.Convert;
import bt747.sys.Time;

/**
 * @author Mario De Weerd
 * 
 * This abstract class defines the 'interface' with the BT747LogConvert class.
 * Derived classes will be able to write the desired output in the formats they
 * implement.
 */
public abstract class GPSFile {
    protected GPSFilter[] ptFilters = null;

    protected boolean oneFilePerDay;

    protected GPSRecord activeFields;

    protected GPSRecord activeFileFields;

    /**
     * The fields that are selected for the file output.
     */
    protected GPSRecord selectedFileFields;

    protected boolean firstRecord;

    protected String basename;

    protected String ext;

    protected int card;

    protected int nbrOfPassesToGo;

    protected int numberOfPasses = 1;

    private File outFile = null;

    protected Time t = new Time(); // Time from log, already transformed

    protected int previousDate = 0;
    protected int previousTime = 0;
    protected boolean separateTrack = false;
    protected int trackSepTime = 60 * 60; // Time needed between points to
    // separate segments.
    protected int filesCreated = 0;

    protected boolean oneFilePerTrack = false;
    protected boolean isMultipleFiles = false;

    protected boolean recordNbrInLogs = false;

    // By default we ignore bad points (no red interconnect)
    protected String badTrackColor = "0000FF";
    protected String goodTrackColor = "0000FF";

    protected boolean imperial = false; // If true, use English units

    public void initialiseFile(final String baseName, final String extension,
            final int fileCard, final int fileSeparationFreq) {
        firstRecord = true;
        nbrOfPassesToGo = numberOfPasses - 1;
        ext = extension;
        basename = baseName;
        card = fileCard;
        oneFilePerDay = false;
        oneFilePerTrack = false;
        switch (fileSeparationFreq) {
        case 1:
            oneFilePerDay = true;
            isMultipleFiles = true;
            break;
        case 2:
            oneFilePerTrack = true;
            isMultipleFiles = true;
            break;
        default:
            break;
        }
    };

    public final void setOneFilePerTrack(final boolean isOneFilePerTrack) {
        oneFilePerTrack = isOneFilePerTrack;
    }

    public final void setTrackSepTime(final int time) {
        trackSepTime = time;
    }

    public final void setActiveFileFields(final GPSRecord full) {
        activeFileFields = full;
    }

    public final void setOutputFields(final GPSRecord fields) {
        selectedFileFields = fields;
    }

    public final void writeLogFmtHeader(final GPSRecord f) {
        activeFields = new GPSRecord(f);
    };

    public final void setFilters(final GPSFilter[] ourFilters) {
        ptFilters = ourFilters;
    };

    public final void setImperial(final boolean useImperial) {
        this.imperial = useImperial;
    }

    /**
     * Returns true when the record is used by the format. Checks all the
     * filters by default.
     * 
     */
    protected boolean recordIsNeeded(final GPSRecord s) {
        boolean result = false;
        for (int i = ptFilters.length - 1; i >= 0; i--) {
            if (ptFilters[i].doFilter(s)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void writeRecord(final GPSRecord s) {
        String extraExt; // Extra extension for log file
        boolean newDate = false;
        int dateref = 0;

        if (activeFields.utc != 0) {
            t.setUTCTime(s.utc); // Initialisation needed later too!
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
                && activeFields.utc != 0 && (s.utc > previousTime
                + trackSepTime)))
                && recordIsNeeded(s)) {
            boolean createOK = true;
            previousDate = dateref;

            if (activeFields.utc != 0) {
                if ((s.utc < 24 * 3600) // No date provided by log.
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
                createFile(extraExt);
            }
        }

        if (activeFields.utc != 0 && recordIsNeeded(s)) {
            previousTime = s.utc;
        }
    };

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

    public boolean nextPass() {
        previousDate = 0;
        firstRecord = true;
        if (nbrOfPassesToGo == 0) {
            // Last Pass done
            finaliseFile();
        } else {
            // More passes to go.
            if (isOpen()) {
                closeFile();
            }
        }
        return false;
    };

    protected void writeFileHeader(final String Name) {
    };

    protected void writeDataHeader() {
    };

    protected void writeDataFooter() {
    };

    protected int createFile(final String extra_ext) {
        String fileName = basename + extra_ext + ext;
        boolean createNewFile = numberOfPasses - 1 == nbrOfPassesToGo;
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
        } else {
            filesCreated += 1;
            try {
                if (createNewFile) {
                    // New file
                    writeFileHeader("GPS" + extra_ext); // First time this file
                    // is
                    // opened.
                } else {
                    // Append to existing file (file open must be append)
                    // outFile.setPos(outFile.getSize());
                }
                writeLogFmtHeader(activeFields);
                writeDataHeader();
            } catch (Exception e) {
                Generic.debug("Initial header or append", e);
                // TODO: handle exception
            }
        }
        return error;
    }

    protected void closeFile() {
        writeDataFooter();
        try {
            outFile.close();
        } catch (Exception e) {
            Generic.debug("closeFile", e);
            // TODO: handle exception
        }
    }

    protected final boolean isOpen() {
        return outFile != null;
    }

    protected final void writeTxt(final String s) {
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

    public final void setIncludeTrkComment(boolean isTrkComment) {
        this.isTrkComment = isTrkComment;
    }

    protected boolean isIncludeTrkName = true;

    public final void setIncludeTrkName(boolean isIncludeTrkName) {
        this.isIncludeTrkName = isIncludeTrkName;
    }

}