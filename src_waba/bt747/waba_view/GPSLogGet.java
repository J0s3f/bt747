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

import net.sf.bt747.waba.system.WabaDate;

import bt747.Txt;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;
import bt747.sys.Time;
import bt747.util.Date;

/**
 * @author Mario De Weerd
 */
public class GPSLogGet extends Container implements ModelListener {

    private MyCheck chkLogOnOff;
    private MyCheck chkLogOverwriteStop;
    private Button btStartDate;
    private Button m_btEndDate;
    private Button m_btGetLog;
    private MyCheck m_chkNoGeoid;
    private Button m_btCancelGetLog;
    private Button m_btToCSV;
    private Button m_btToKML;
    private Button m_btToGPX;
    private Button m_btToTRK;
    private Button m_btToPLT;
    private Button m_btToNMEA;
    private Button m_btToGMAP;
    private Edit m_edTrkSep;
    private ComboBox m_cbTimeOffsetHours;

    private static final String[] offsetStr = { "-12", "-11", "-10", "-9",
            "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "+0", "+1", "+2",
            "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+10", "+11", "+12",
            "+13", "+14" };
    private ComboBox m_cbColors;
    private static final String[] colors = { "FF0000", "0000FF", "800000",
            "000080", "00FF00", "008000" };
    private ComboBox cbDownload;
    private static final String[] downloadStr = { Txt.DOWNLOAD_NORMAL, Txt.DOWNLOAD_INCREMENTAL, 
        Txt.DOWNLOAD_FULL };
    private ComboBox m_chkOneFilePerDay;
    private static final String[] fileStr = { Txt.ONE_FILE, Txt.ONE_FILE_DAY,
            Txt.ONE_FILE_TRK };
    private Model m;
    private AppController c;
    private Color BackupBackColor;
    private Label m_UsedLabel;
    private Label m_RecordsLabel;

    public GPSLogGet(final Model m, final AppController c) {
        this.m = m;
        this.c = c;
    }

    private String convertUTCtoDateString(int utcTime) {
        Time t = new Time();
        String dateString;
        t.setUTCTime(utcTime);
        int day = t.getDay();
        int month = t.getMonth();
        int year = t.getYear();
        dateString = (day < 10 ? "0" : "") + day + "/"
                + (month < 10 ? "0" : "") + t.getMonth() + "/" + year;
        return dateString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Container#onStart()
     */
    protected final void onStart() {
        super.onStart();
        add(chkLogOnOff = new MyCheck(Txt.DEV_LOGONOFF), LEFT, TOP); //$NON-NLS-1$
        cbDownload = new ComboBox(downloadStr);
        add(cbDownload,RIGHT,SAME);
        cbDownload.select(m.getDownloadMethod());
        add(chkLogOverwriteStop = new MyCheck(Txt.LOG_OVRWR_FULL), LEFT, AFTER); //$NON-NLS-1$
        add(new Label(Txt.DATE_RANGE), LEFT, AFTER); //$NON-NLS-1$

        add(btStartDate = new Button(convertUTCtoDateString(m
                .getFilterStartTime())), AFTER, SAME); //$NON-NLS-1$
        // m_btStartDate.setMode(Edit.DATE);
        add(m_btEndDate = new Button(convertUTCtoDateString(m
                .getFilterEndTime())), RIGHT, SAME); //$NON-NLS-1$
        // m_btEndDate.setMode(Edit.DATE);
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
            c.setTimeOffsetHours(0); // TODO: Change in call to control
            offsetIdx = 12;
        }
        m_cbTimeOffsetHours = new ComboBox(offsetStr);
        add(m_cbTimeOffsetHours, RIGHT, SAME);
        m_cbTimeOffsetHours.select(offsetIdx);
        add(new Label(Txt.UTC), BEFORE, SAME);

        // add(new Label("End"),BEFORE,SAME);
        add(m_chkOneFilePerDay = new ComboBox(fileStr), LEFT, AFTER + 2);
        m_chkOneFilePerDay.select(m.getFileSeparationFreq());
        add(m_chkNoGeoid = new MyCheck(Txt.HGHT_GEOID_DIFF), AFTER + 5, SAME); //$NON-NLS-1$
        m_chkNoGeoid.setChecked(m.isConvertWGS84ToMSL());

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
        c.reqLogStatus();
        // Request log version from device
        c.reqMtkLogVersion();
        // Request mem size from device
        c.reqLogMemUsed();
        // Request number of log points
        c.reqLogMemPtsLogged();
        c.reqLogOverwrite();
    }

    public final void updateButtons() {
        chkLogOnOff.setChecked(m.isLoggingActive());
        // m_chkLogOnOff.repaintNow();
        chkLogOverwriteStop.setChecked(m.isLogFullOverwrite());
        // m_chkLogOverwriteStop.repaintNow();
        m_UsedLabel.setText(Txt.MEM_USED + Convert.toString(m.logMemUsed())
                + "(" + Convert.toString(m.logMemUsedPercent()) + "%)");
        // m_UsedLabel.repaintNow();
        m_RecordsLabel.setText(Txt.NBR_RECORDS
                + Convert.toString(m.logNbrLogPts()) + " ("
                + m.getEstimatedNbrRecordsFree(m.getLogFormat()) + " "
                + Txt.MEM_FREE + ")");
        // m_RecordsLabel.repaintNow();
    }

    private Calendar cal;
    private Button calBt;

    /**
     * The number of seconds in a day.
     */
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Control#onEvent(waba.ui.Event) configureable.
     */
    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Control#onEvent(waba.ui.Event)
     */
    public final void onEvent(final Event event) {
        // Vm.debug("Event:"+event.type+" "+event.consumed);
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed = true;
            if (event.target == m_btGetLog) {
                c.startDownload();
                // m_btGetLog.press(false);
            } else if (event.target == m_btCancelGetLog) {
                c.cancelGetLog();
            } else if (event.target == cbDownload) {
                c.setDownloadMethod(cbDownload.getSelectedIndex());
            } else if (event.target == chkLogOnOff) {
                if (chkLogOnOff.getChecked()) {
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
                    c.setTimeOffsetHours(Convert.toInt((String) tmp
                            .substring(1)));
                } else {
                    c.setTimeOffsetHours(Convert.toInt(tmp));
                }
            } else if (event.target == chkLogOverwriteStop) {
                c.setLogOverwrite(chkLogOverwriteStop.getChecked());
            } else if (event.target == m_chkOneFilePerDay) {
                c.setOutputFileSplitType(m_chkOneFilePerDay.getSelectedIndex());
            } else if (event.target == m_chkNoGeoid) {
                c.setNoGeoid(m_chkNoGeoid.getChecked());
            } else if (event.target == m_btEndDate) {
                if (cal == null) {
                    cal = new Calendar();
                }
                calBt = m_btEndDate;
                cal.popupModal();
            } else if (event.target == btStartDate) {
                if (cal == null) {
                    cal = new Calendar();
                }
                calBt = btStartDate;
                cal.popupModal();
            } else if (event.target == m_btToCSV || event.target == m_btToKML
                    || event.target == m_btToPLT || event.target == m_btToGPX
                    || event.target == m_btToTRK || event.target == m_btToGMAP
                    || event.target == m_btToNMEA) {
                int logType = Model.NO_LOG_LOGTYPE;
                if (event.target == m_btToCSV) {
                    logType = Model.CSV_LOGTYPE;
                } else if (event.target == m_btToTRK) {
                    logType = Model.TRK_LOGTYPE;
                } else if (event.target == m_btToKML) {
                    logType = Model.KML_LOGTYPE;
                } else if (event.target == m_btToPLT) {
                    logType = Model.PLT_LOGTYPE;
                } else if (event.target == m_btToGPX) {
                    logType = Model.GPX_LOGTYPE;
                } else if (event.target == m_btToNMEA) {
                    logType = Model.NMEA_LOGTYPE;
                } else if (event.target == m_btToGMAP) {
                    logType = Model.GMAP_LOGTYPE;
                }
                c.convertLog(logType);
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
                waba.util.Date d = cal.getSelectedDate();
                if (d != null) {
                    calBt.setText(d.toString());
                    // Can't change the value of the date, changing all
                    c.setStartDate((new WabaDate(btStartDate.getText()))
                            .dateToUTCepoch1970());
                    c.setEndDate((new WabaDate(m_btEndDate.getText()))
                            .dateToUTCepoch1970()
                            + SECONDS_PER_DAY - 1);
                }
            }
            /*
             * cal = null; Note: If your program uses the Calendar control only
             * a few times, i suggest that you set cal to null so it can get
             * garbage collected. Calendar objects waste memory and it is always
             * a good idea to save memory when possible
             */
            break;
        }
    }

    public final void modelEvent(ModelEvent event) {
        int eventType = event.getType();
        if (eventType == ModelEvent.DATA_UPDATE) {
            updateButtons();
        } else if (eventType == ModelEvent.DOWNLOAD_METHOD_CHANGE) {
            cbDownload.select(m.getDownloadMethod());
        } else if (eventType == ModelEvent.CONVERSION_ENDED
                || eventType == ModelEvent.CONVERSION_STARTED) {
            Button b = null;
            switch (m.getLastConversionOngoing()) {
            case Model.CSV_LOGTYPE:
                b = m_btToCSV;
                break;
            case Model.TRK_LOGTYPE:
                b = m_btToTRK;
                break;
            case Model.KML_LOGTYPE:
                b = m_btToKML;
                break;
            case Model.PLT_LOGTYPE:
                b = m_btToPLT;
                break;
            case Model.GPX_LOGTYPE:
                b = m_btToGPX;
                break;
            case Model.NMEA_LOGTYPE:
                b = m_btToNMEA;
                break;
            case Model.GMAP_LOGTYPE:
                b = m_btToGMAP;
                break;
            default:
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