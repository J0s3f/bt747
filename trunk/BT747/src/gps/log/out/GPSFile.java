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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************       
package gps.log.out;

import gps.BT747Constants;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.Txt;
import bt747.io.File;
import bt747.io.BufFile;
import bt747.sys.Convert;
import bt747.sys.Time;
import bt747.sys.Vm;

/**
 * @author Mario De Weerd
 * 
 * This abstract class defines the 'interface' with the BT747LogConvert class.
 * Derived classes will be able to write the desired output in the formats they
 * implement.
 */
public abstract class GPSFile {
    protected GPSFilter[] m_Filters = null;

    protected boolean m_oneFilePerDay;

    protected GPSRecord activeFields;

    protected GPSRecord activeFileFields;

    protected boolean m_FirstRecord;

    protected String m_basename;

    protected String m_ext;

    protected int m_card;

    protected int m_nbrOfPassesToGo;

    protected int C_NUMBER_OF_PASSES = 1;

    private BufFile m_File = null;

    protected Time t = new Time(); // Time from log, already transformed

    protected int m_prevdate = 0;
    protected int m_prevtime = 0;
    protected boolean m_sepTrack = false;
    protected int m_TrackSepTime = 60 * 60; // Time needed between points to
                                            // separate segments.
    protected int filesCreated = 0;

    protected boolean m_oneFilePerTrack = false;
    protected boolean m_multipleFiles = false;

    protected boolean recordNbrInLogs = false;

    protected String badTrackColor = "FF0000";
    protected String goodTrackColor = "0000FF";

    protected boolean imperial = false; // If true, use English units

    protected static final String[] C_MONTHS = { "JAN", "FEB", "MAR", "APR",
            "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

    public void initialiseFile(final String basename, final String ext,
            final int Card, int fileSeparationFreq) {
        m_FirstRecord = true;
        m_nbrOfPassesToGo = C_NUMBER_OF_PASSES - 1;
        m_ext = ext;
        m_basename = basename;
        m_card = Card;
        m_oneFilePerDay = false;
        m_oneFilePerTrack = false;
        switch (fileSeparationFreq) {
        case 1:
            m_oneFilePerDay = true;
            m_multipleFiles = true;
            break;
        case 2:
            m_oneFilePerTrack = true;
            m_multipleFiles = true;
            break;
        }
    };

    public final void setOneFilePerTrack(final boolean oneFilePerTrack) {
        m_oneFilePerTrack = oneFilePerTrack;
    }

    public final void setTrackSepTime(final int time) {
        m_TrackSepTime = time;
    }

    public final void setActiveFileFields(final GPSRecord full) {
        activeFileFields = full;
    }

    public final void writeLogFmtHeader(final GPSRecord f) {
        activeFields = new GPSRecord(f);
    };

    public final void setFilters(final GPSFilter[] filters) {
        m_Filters = filters;
    };

    public final void setImperial(final boolean imperial) {
        this.imperial = imperial;
    }

    /**
     * Returns true when the record is used by the format. Checks all the
     * filters by default.
     * 
     */
    protected boolean recordIsNeeded(GPSRecord s) {
        boolean result = false;
        for (int i = m_Filters.length - 1; i >= 0; i--) {
            if (m_Filters[i].doFilter(s)) {
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
            if (m_oneFilePerDay || m_oneFilePerTrack) {
                dateref = (t.getYear() << 14) + (t.getMonth() << 7)
                        + t.getDay(); // year *
                // 16384 +
                // month *
                // 128 + day
                newDate = (dateref > m_prevdate);
            }

        }

        if (((((m_oneFilePerDay && newDate) && activeFields.utc != 0) || m_FirstRecord) || (m_oneFilePerTrack
                && activeFields.utc != 0 && (s.utc > m_prevtime
                + m_TrackSepTime)))
                && recordIsNeeded(s)) {
            boolean createOK = true;
            m_prevdate = dateref;

            if (activeFields.utc != 0) {
                if (t.getYear() > 2000) {
                    extraExt = "-" + Convert.toString(t.getYear())
                            + (t.getMonth() < 10 ? "0" : "")
                            + Convert.toString(t.getMonth())
                            + (t.getDay() < 10 ? "0" : "")
                            + Convert.toString(t.getDay());
                    if (m_oneFilePerTrack) {
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
            if (!m_FirstRecord && (extraExt.length() != 0)) {
                // newDate -> close previous file
                if (m_nbrOfPassesToGo == 0) {
                    // Vm.debug("Finalize:"+m_File.getPath());
                    finaliseFile();
                } else {
                    // Vm.debug("Close"+m_File.getPath());
                    closeFile();
                }
            } else {
                m_FirstRecord = !createOK;
            }

            if (createOK) {
                createFile(extraExt);
            }
        }

        if (activeFields.utc != 0 && recordIsNeeded(s)) {
            m_prevtime = s.utc;
        }
    };

    public void finaliseFile() {
        if (m_File != null) {
            try {
                m_File.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
            m_File = null;
        }
    }

    public boolean nextPass() {
        m_prevdate = 0;
        m_FirstRecord = true;
        if (m_nbrOfPassesToGo == 0) {
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
        String fileName = m_basename + extra_ext + m_ext;
        boolean createNewFile = C_NUMBER_OF_PASSES - 1 == m_nbrOfPassesToGo;
        int error=BT747Constants.NO_ERROR;

        try {
            m_File = new BufFile(fileName, File.DONT_OPEN, m_card);
            if (createNewFile && m_File.exists()) {
                m_File.delete();
            }
            m_File = new BufFile(fileName, createNewFile ? File.CREATE
                    : File.READ_WRITE, m_card);
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (m_File != null && !m_File.isOpen()) {
            errorInfo=fileName + "|" + m_File.lastError;
            error=BT747Constants.ERROR_COULD_NOT_OPEN;
            m_File = null;
        } else {
            filesCreated += 1;
            try {
                if (createNewFile) {
                    // New file
                    writeFileHeader("GPS" + extra_ext); // First time this file
                                                        // is
                    // opened.
                } else {
                    // Append to existing file
                    m_File.setPos(m_File.getSize());
                }
                writeLogFmtHeader(activeFields);
                writeDataHeader();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return error;
    }

    protected void closeFile() {
        writeDataFooter();
        try {
            m_File.close();
        } catch (Exception e) {
            // TODO: handle exception
            Vm.debug(Txt.CLOSE_FAILED);
        }
    }

    protected final boolean isOpen() {
        return m_File != null;
    }

    StringBuffer rcrStr = new StringBuffer(16);

    protected final String getRCRstr(final GPSRecord s) {
        rcrStr.setLength(0);
        if ((s.rcr & BT747Constants.RCR_TIME_MASK) != 0) {
            rcrStr.append("T");
        }
        if ((s.rcr & BT747Constants.RCR_SPEED_MASK) != 0) {
            rcrStr.append("S");
        }
        if ((s.rcr & BT747Constants.RCR_DISTANCE_MASK) != 0) {
            rcrStr.append("D");
        }
        if ((s.rcr & BT747Constants.RCR_BUTTON_MASK) != 0) {
            rcrStr.append("B");
        }

        // Still 16-4 = 12 possibilities.
        // Taking numbers from 1 to 9
        // Then letters X, Y and Z
        char c = '1';
        int i;
        for (i = 0x10; c <= '9'; i <<= 1, c++) {
            if ((s.rcr & i) != 0) {
                rcrStr.append(c);
            }
        }
        c = 'X';
        for (; i < 0x10000; i <<= 1, c++) {
            if ((s.rcr & i) != 0) {
                rcrStr.append(c);
            }
        }

        return rcrStr.toString();
    }

    protected final void writeTxt(final String s) {
        try {
            if (m_File != null) {
                m_File.writeBytes(s.getBytes(), 0, s.length());
            } else {
                Vm.debug(Txt.WRITING_CLOSED);
            }
        } catch (Exception e) {

        }
    }

    public boolean needPassToFindFieldsActivatedInLog() {
        return false;
    }

    /**
     * @return Returns the badTrackColor.
     */
    public String getBadTrackColor() {
        return badTrackColor;
    }

    /**
     * @param badTrackColor
     *            The badTrackColor to set.
     */
    public void setBadTrackColor(String badTrackColor) {
        this.badTrackColor = badTrackColor;
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
    public final void setGoodTrackColor(String goodTrackColor) {
        this.goodTrackColor = goodTrackColor;
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
    public final void setRecordNbrInLogs(boolean recordNbrInLogs) {
        this.recordNbrInLogs = recordNbrInLogs;
    }

    protected boolean addLogConditionInfo = false;
    
    public final boolean isAddLogConditionInfo() {
        return addLogConditionInfo;
    }


    public final void setAddLogConditionInfo(boolean addLogConditionInfo) {
        this.addLogConditionInfo = addLogConditionInfo;
    }

    protected String errorInfo;
    public final String getErrorInfo() {
        return errorInfo;
    }

}
