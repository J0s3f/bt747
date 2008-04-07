package bt747.waba_view;
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
import waba.fx.Color;
import waba.ui.Button;
import waba.ui.Calendar;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;

import gps.GpsEvent;

import bt747.Txt;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Convert;
import bt747.util.Date;

/**
 * @author Mario De Weerd
 */
public class GPSLogGet extends Container {

    MyCheck m_chkLogOnOff;
    MyCheck m_chkLogOverwriteStop;
    Button m_btStartDate;
    Button m_btEndDate;
    Button m_btGetLog;
    MyCheck m_chkNoGeoid;
    Button m_btCancelGetLog;
    Button m_btToCSV;
    Button m_btToKML;
    Button m_btToGPX;
    Button m_btToTRK;
    Button m_btToPLT;
    Button m_btToNMEA;
    Button m_btToGMAP;
    Edit m_edTrkSep;
    ComboBox m_cbTimeOffsetHours;
    private static final String[] offsetStr = {
        "-12", "-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1",
        "+0",
        "+1", "+2", "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+10", "+11", "+12",
        "+13", "+14"
    };
    ComboBox m_cbColors;
    private static final String[] colors = {
        "FF0000",
        "0000FF",
        "800000",
        "000080",
        "00FF00",
        "008000"
    };
    MyCheck m_chkIncremental;
    ComboBox m_chkOneFilePerDay;
    private static final String[] fileStr = {
        Txt.ONE_FILE,
        Txt.ONE_FILE_DAY,
        Txt.ONE_FILE_TRK
    };
    Model m;
    Controller c;
    private Color BackupBackColor;
    private Label m_UsedLabel;
    private Label m_RecordsLabel;

    public GPSLogGet(
            Model m,
            Controller c) {
        this.m = m;
        this.c = c;
    }


    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Container#onStart() 
     */
    protected void onStart() {
        super.onStart();
        add(m_chkLogOnOff = new MyCheck(Txt.DEV_LOGONOFF), LEFT, TOP); //$NON-NLS-1$
        add(m_chkIncremental = new MyCheck(Txt.INCREMENTAL), RIGHT, SAME); //$NON-NLS-1$
        m_chkIncremental.setChecked(m.isIncremental());
        add(m_chkLogOverwriteStop = new MyCheck(Txt.LOG_OVRWR_FULL), LEFT, AFTER); //$NON-NLS-1$
        add(new Label(Txt.DATE_RANGE), LEFT, AFTER); //$NON-NLS-1$
        add(m_btStartDate = new Button(m.getStartDate().getDateString()), AFTER, SAME); //$NON-NLS-1$
        //m_btStartDate.setMode(Edit.DATE);
        add(m_btEndDate = new Button(m.getEndDate().getDateString()), RIGHT, SAME); //$NON-NLS-1$
        //m_btEndDate.setMode(Edit.DATE);
        add(m_btGetLog = new Button(Txt.GET_LOG), LEFT, AFTER + 2); //$NON-NLS-1$
        add(m_btCancelGetLog = new Button(Txt.CANCEL_GET), AFTER + 5, SAME); //$NON-NLS-1$

        m_cbColors = new ComboBox(colors);
        add(m_cbColors, RIGHT, SAME);
        add(new Label(Txt.NOFIX_COL), BEFORE, SAME);

        add(new Label(Txt.TRK_SEP), LEFT, AFTER); //$NON-NLS-1$
        add(m_edTrkSep = new Edit("00000"), AFTER, SAME); //$NON-NLS-1$
        m_edTrkSep.setValidChars(Edit.numbersSet);
        add(new Label(Txt.MIN), AFTER, SAME); //$NON-NLS-1$
        m_edTrkSep.setText(Convert.toString(m.getTrkSep()));
        m_edTrkSep.alignment = RIGHT;

        int offsetIdx = m.getTimeOffsetHours() + 12;
        if (offsetIdx > 26) {
            m.setTimeOffsetHours(0);  // TODO: Change in call to control
            offsetIdx = 12;
        }
        m_cbTimeOffsetHours = new ComboBox(offsetStr);
        add(m_cbTimeOffsetHours, RIGHT, SAME);
        m_cbTimeOffsetHours.select(offsetIdx);
        add(new Label(Txt.UTC), BEFORE, SAME);

        //add(new Label("End"),BEFORE,SAME);
        add(m_chkOneFilePerDay = new ComboBox(fileStr), LEFT, AFTER + 2);
        m_chkOneFilePerDay.select(m.getFileSeparationFreq());
        add(m_chkNoGeoid = new MyCheck(Txt.HGHT_GEOID_DIFF), AFTER + 5, SAME); //$NON-NLS-1$
        m_chkNoGeoid.setChecked(m.getNoGeoid());

        add(m_btToCSV = new Button(Txt.TO_CSV), LEFT, AFTER + 5); //$NON-NLS-1$
        add(m_btToGPX = new Button(Txt.TO_GPX), AFTER + 5, SAME); //$NON-NLS-1$
        add(m_btToKML = new Button(Txt.TO_KML), RIGHT, SAME); //$NON-NLS-1$
        add(m_btToTRK = new Button(Txt.TO_TRK), BEFORE - 5, SAME); //$NON-NLS-1$

        add(m_btToPLT = new Button(Txt.TO_PLT), LEFT, AFTER + 2); //$NON-NLS-1$
        add(m_btToGMAP = new Button(Txt.TO_GMAP), CENTER, SAME); //$NON-NLS-1$
        add(m_btToNMEA = new Button(Txt.TO_NMEA), RIGHT, SAME); //$NON-NLS-1$

        BackupBackColor = m_btToCSV.getBackColor();

        add(m_UsedLabel = new Label(""), LEFT, AFTER + 3);
        add(m_RecordsLabel = new Label(""), LEFT, AFTER + 3);

        String s = m.getColorInvalidTrack();
        m_cbColors.select(0);
        for (int i = 0; i < m_cbColors.size() - 1; i++) {
            if (s.equals((String) m_cbColors.getItemAt(i))) {
                m_cbColors.select(i);
                break;
            }
        }
    }
    
    private void reqLogInfo() {
        // Request device info for this control
        // Request log version from device
        c.reqLogVersion();
        // Request mem size from device
        c.reqLogMemUsed();
        // Request number of log points
        c.reqLogMemPoints();
        c.reqLogOverwrite();
    }

    public void updateButtons() {
        m_chkLogOnOff.setChecked(m.loggingIsActive());
//        m_chkLogOnOff.repaintNow();
        m_chkLogOverwriteStop.setChecked(m.logFullOverwrite());
//        m_chkLogOverwriteStop.repaintNow();
        m_UsedLabel.setText(Txt.MEM_USED + Convert.toString(m.logMemUsed()) + "(" + Convert.toString(m.logMemUsedPercent()) + "%)");
//        m_UsedLabel.repaintNow();
        m_RecordsLabel.setText(Txt.NBR_RECORDS + Convert.toString(m.logNbrLogPts()));
//        m_RecordsLabel.repaintNow();
    }
    private Calendar cal;
    private Button calBt;

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Control#onEvent(waba.ui.Event)
     *      configureable.
     */
    public void onEvent(Event event) {
        //Vm.debug("Event:"+event.type+" "+event.consumed);
        super.onEvent(event);
        switch (event.type) {
            case ControlEvent.PRESSED:
                event.consumed = true;
                if (event.target == m_btGetLog) {
                    c.startDownload();
                    //m_btGetLog.press(false);
                } else if (event.target == m_btCancelGetLog) {
                    c.cancelGetLog();
                } else if (event.target == m_chkIncremental) {
                    c.setIncremental(m_chkIncremental.getChecked());
                } else if (event.target == m_chkLogOnOff) {
                    if (m_chkLogOnOff.getChecked()) {
                        c.startLog();
                    } else {
                        c.stopLog();
                    }
                } else if (event.target == m_cbColors) {
                    c.setColorInvalidTrack((String) m_cbColors.getSelectedItem());
                } else if (event.target == m_cbTimeOffsetHours) {
                    // Work around superwaba bug
                    String tmp = (String) m_cbTimeOffsetHours.getSelectedItem();
                    if (tmp.charAt(0) == '+') {
                        m.setTimeOffsetHours(Convert.toInt((String) tmp.substring(1)));
                    } else {
                        m.setTimeOffsetHours(Convert.toInt(tmp));
                    }
                } else if (event.target == m_chkLogOverwriteStop) {
                    c.setLogOverwrite(m_chkLogOverwriteStop.getChecked());
                } else if (event.target == m_chkOneFilePerDay) {
                    m.setOneFilePerDay(m_chkOneFilePerDay.getSelectedIndex());
                } else if (event.target == m_chkNoGeoid) {
                    m.setNoGeoid(m_chkNoGeoid.getChecked());
                } else if (event.target == m_btEndDate) {
                    if (cal == null) {
                        cal = new Calendar();
                    }
                    calBt = m_btEndDate;
                    cal.popupModal();
                } else if (event.target == m_btStartDate) {
                    if (cal == null) {
                        cal = new Calendar();
                    }
                    calBt = m_btStartDate;
                    cal.popupModal();
                } else if (event.target == m_btToCSV || event.target == m_btToKML || event.target == m_btToPLT || event.target == m_btToGPX || event.target == m_btToTRK || event.target == m_btToGMAP || event.target == m_btToNMEA) {
                    int logType = Model.C_NO_LOG;
                    if (event.target == m_btToCSV) {
                        logType = Model.C_CSV_LOG;
                    } else if (event.target == m_btToTRK) {
                        logType = Model.C_TRK_LOG;
                    } else if (event.target == m_btToKML) {
                        logType = Model.C_KML_LOG;
                    } else if (event.target == m_btToPLT) {
                        logType = Model.C_PLT_LOG;
                    } else if (event.target == m_btToGPX) {
                        logType = Model.C_GPX_LOG;
                    } else if (event.target == m_btToNMEA) {
                        logType = Model.C_NMEA_LOG;
                    } else if (event.target == m_btToGMAP) {
                        logType = Model.C_GMAP_LOG;
                    }
                    c.writeLog(logType);
                } else if (event.target == this) {
                    // Enters focus
                    reqLogInfo();
                } else {
                    event.consumed = false;
                }
                break;
            case ControlEvent.FOCUS_OUT:
                if (event.target == m_edTrkSep) {
                    c.setTrkSep(Convert.toInt(m_edTrkSep.getText()));
                    m_edTrkSep.setText(Convert.toString(m.getTrkSep()));
                }
                break;

            case ControlEvent.WINDOW_CLOSED:
                if (event.target == cal) {
                    waba.util.Date d= cal.getSelectedDate();
                    if (d != null) {
                        calBt.setText(d.toString());
                        // Can't change the value of the date, changing all
                        c.setStartDate(new Date(m_btStartDate.getText()));
                        c.setEndDate(new Date(m_btEndDate.getText()));
                    }
                }
                /*cal = null;
                Note: If your program uses the Calendar control only a few
                times, i suggest that you set cal to null so it can get garbage
                collected. Calendar objects waste memory and it is always a
                good idea to save memory when possible*/
                break;
            default:
                if (event.type == GpsEvent.DATA_UPDATE) {
                    updateButtons();
                } else if (event.type == ModelEvent.INCREMENTAL_CHANGE) {
                    m_chkIncremental.setChecked(m.isIncremental());
                } else if (event.type == ModelEvent.CONVERSION_ENDED
                        || event.type == ModelEvent.CONVERSION_STARTED) {
                    Button b = null;
                    switch (m.getLastConversionOngoing()) {
                        case Model.C_CSV_LOG:
                            b = m_btToCSV;
                            break;
                        case Model.C_TRK_LOG:
                            b = m_btToTRK;
                            break;
                        case Model.C_KML_LOG:
                            b = m_btToKML;
                            break;
                        case Model.C_PLT_LOG:
                            b = m_btToPLT;
                            break;
                        case Model.C_GPX_LOG:
                            b = m_btToGPX;
                            break;
                        case Model.C_NMEA_LOG:
                            b = m_btToNMEA;
                            break;
                        case Model.C_GMAP_LOG:
                            b = m_btToGMAP;
                            break;
                    }
                    if (b != null) {
                        if (m.isConversionOngoing()) {
                            b.setBackColor(Color.GREEN);
                        } else {
                            b.setBackColor(BackupBackColor);
                        }
                        b.repaintNow();
                    }
                }
        }
    }
}
