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
package gps;

import waba.io.File;
import waba.sys.Convert;
import waba.sys.Time;
import waba.ui.MessageBox;
import waba.util.Date;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class GPSFile {
    protected GPSFilter[] m_Filters = null;

    protected int m_recCount;

    protected boolean m_oneFilePerDay;

    protected GPSRecord activeFields;

    protected GPSRecord activeFileFields;

    protected boolean m_FirstRecord;

    protected String m_basename;

    protected String m_ext;

    protected int m_card;

    protected int m_nbrOfPassesToGo;

    protected int C_NUMBER_OF_PASSES = 1;

    protected File m_File = null;

    protected Time t = new Time(); // Time from log, already transformed

    protected int m_prevdate = 0;

    public void initialiseFile(final String basename, final String ext,
            final int Card, boolean oneFilePerDay) {
        m_FirstRecord = true;
        m_recCount = 0;
        m_nbrOfPassesToGo = C_NUMBER_OF_PASSES - 1;
        m_ext = ext;
        m_basename = basename;
        m_card = Card;
        m_oneFilePerDay = oneFilePerDay;
    };

    public void setActiveFileFields(final GPSRecord full) {
        activeFileFields = full;
    }

    public void writeLogFmtHeader(final GPSRecord f) {
        activeFields = new GPSRecord(f);
    };

    public void setFilters(final GPSFilter[] filters) {
        m_Filters = filters;
    };

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

    public void writeRecord(GPSRecord s) {
        String extraExt; // Extra extension for log file
        boolean newDate = false;
        int dateref = 0;

        m_recCount++;

        if (activeFields.utc != 0) {
            setTime(s.utc); // Initialisation needed later too!
            if (m_oneFilePerDay) {
                dateref = (t.year << 14) + (t.month << 7) + t.day; // year *
                                                                   // 16384 +
                                                                   // month *
                                                                   // 128 + day
                newDate = (dateref > m_prevdate);
            }
        }

        if ((((m_oneFilePerDay && newDate) && activeFields.utc != 0) || m_FirstRecord)
                && recordIsNeeded(s)) {
            boolean createOK = true;
            m_prevdate = dateref;
            if (activeFields.utc != 0) {
                if (t.year > 2000) {
                    extraExt = "-" + Convert.toString(t.year)
                            + (t.month < 10 ? "0" : "")
                            + Convert.toString(t.month)
                            + (t.day < 10 ? "0" : "") + Convert.toString(t.day);
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
                    finaliseFile();
                } else {
                    closeFile();
                }
            } else {
                m_FirstRecord = !createOK;
            }

            if (createOK) {
                createFile(extraExt);
            }
        }
    };

    public void finaliseFile() {
        // TODO Auto-generated method stub
        if (m_File != null) {
            m_File.close();
            m_File = null;
        }
    }

    public boolean nextPass() {
        m_prevdate = 0;
        return false;
    };

    protected void writeFileHeader(final String Name) {
    };

    protected void writeDataHeader() {
    };

    protected void writeDataFooter() {
    };

    protected void createFile(final String extra_ext) {
        String fileName = m_basename + extra_ext + m_ext;
        boolean createNewFile = C_NUMBER_OF_PASSES - 1 == m_nbrOfPassesToGo;

        m_File = new File(fileName, File.DONT_OPEN, m_card);
        if (createNewFile && m_File.exists()) {
            m_File.delete();
        }
        m_File = new File(fileName, createNewFile ? File.CREATE
                : File.READ_WRITE, m_card);
        if (!m_File.isOpen()) {
            // TODO: provide a single message to the user)
            waba.sys.Vm.debug("Could not open " + fileName);
            (new MessageBox("Error", "Could not open|" + fileName))
                        .popupBlockingModal();
            m_File = null;
        } else {
            if (createNewFile) {
                // New file
                writeFileHeader("GPS" + extra_ext); // First time this file is
                                                    // opened.
            } else {
                // Append to existing file
                m_File.setPos(m_File.getSize());
            }
            writeLogFmtHeader(activeFields);
            writeDataHeader();
        }
    }

    protected void closeFile() {
        writeDataFooter();
        m_File.close();
    }

    private final static int DAYS_BETWEEN_1970_1983 = 4748;

    private final void setTime(final int utc_int) {
        //long utc=utc_int&0xFFFFFFFFL;
        int utc = utc_int;
        //Time t=new Time();
        t.second = (int) utc % 60;
        utc /= 60;
        t.minute = (int) utc % 60;
        utc /= 60;
        t.hour = (int) utc % 24;
        utc /= 24;
        // Now days since 1/1/1970
        Date d = new Date(1, 1, 1983); //Minimum = 1983
        d.advance(((int) utc) - DAYS_BETWEEN_1970_1983);
        t.year = d.getYear();
        t.month = d.getMonth();
        t.day = d.getDay();
    }

    protected void writeTxt(final String s) {
        try {
            if (m_File != null) {
                m_File.writeBytes(s.getBytes(), 0, s.length());
            }
        } catch (Exception e) {

        }
    }

    public boolean needPassToFindFieldsActivatedInLog() {
        return false;
    }

}
