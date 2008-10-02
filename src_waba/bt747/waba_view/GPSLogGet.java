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
import waba.sys.Settings;
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

/**
 * @author Mario De Weerd
 */
public final class GPSLogGet extends Container implements ModelListener {

    private Model m;
    private AppController c;

    private MyCheck chkLogOnOff;
    private MyCheck chkLogOverwriteStop;
    private Button btStartDate;
    private Button btEndDate;
    private Button btGetLog;
    private MyCheck chkConvertWGSToMSL;
    private Button btCancelGetLog;
    private Button btToCSV;
    private Button btToKML;
    private Button btToGPX;
    private Button btToTRK;
    private Button btToPLT;
    private Button btToNMEA;
    private Button btToGMAP;
    private Edit edTrkSep;
    private ComboBox cbTimeOffsetHours;

    private Calendar cal;
    private Button btCal;

    private static final String[] offsetStr = { "-12", "-11", "-10", "-9",
            "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "+0", "+1", "+2",
            "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+10", "+11", "+12",
            "+13", "+14" };
    private ComboBox cbColors;
    private static final String[] colors = { "FF0000", "0000FF", "800000",
            "000080", "00FF00", "008000" };
    private ComboBox cbDownload;
    private static final String[] downloadStr = { Txt.DOWNLOAD_NORMAL,
            Txt.DOWNLOAD_INCREMENTAL, Txt.DOWNLOAD_FULL };
    private ComboBox cbFileSplitType;
    private static final String[] fileStr = { Txt.ONE_FILE, Txt.ONE_FILE_DAY,
            Txt.ONE_FILE_TRK };
    private Color savedBackColor;
    private Label lbUsedMem;
    private Label lbUsedRecords;

    /**
     * The number of seconds in a day.
     */
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    public GPSLogGet(final Model m, final AppController c) {
        this.m = m;
        this.c = c;
    }

    private String convertUTCtoDateString(final int utcTime) {
        Time t = new Time();
        String dateString;
        t.setUTCTime(utcTime);
        int day = t.getDay();
        int month = t.getMonth();
        int year = t.getYear();
        dateString = (day < 10 ? "0" : "") + day + "/"
                + (month < 10 ? "0" : "") + t.getMonth() + "/" + year;
        t = null; // Release object.
        return dateString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Container#onStart()
     */
    protected void onStart() {
        super.onStart();
        add(chkLogOnOff = new MyCheck(Txt.DEV_LOGONOFF), LEFT, TOP); //$NON-NLS-1$
        cbDownload = new ComboBox(downloadStr);
        add(cbDownload, RIGHT, SAME);
        cbDownload.select(m.getDownloadMethod());
        add(chkLogOverwriteStop = new MyCheck(Txt.LOG_OVRWR_FULL), LEFT, AFTER); //$NON-NLS-1$
        add(new Label(Txt.DATE_RANGE), LEFT, AFTER); //$NON-NLS-1$

        add(btStartDate = new Button(convertUTCtoDateString(m
                .getFilterStartTime())), AFTER, SAME); //$NON-NLS-1$
        // m_btStartDate.setMode(Edit.DATE);
        add(
                btEndDate = new Button(convertUTCtoDateString(m
                        .getFilterEndTime())), RIGHT, SAME); //$NON-NLS-1$
        // m_btEndDate.setMode(Edit.DATE);
        add(btGetLog = new Button(Txt.GET_LOG), LEFT, AFTER + 2); //$NON-NLS-1$
        add(btCancelGetLog = new Button(Txt.CANCEL_GET), AFTER + 5, SAME); //$NON-NLS-1$

        cbColors = new ComboBox(colors);
        add(cbColors, RIGHT, SAME);
        add(new Label(Txt.NOFIX_COL), BEFORE, SAME);

        add(new Label(Txt.TRK_SEP), LEFT, AFTER); //$NON-NLS-1$
        add(edTrkSep = new Edit("00000"), AFTER, SAME); //$NON-NLS-1$
        edTrkSep.setValidChars(Edit.numbersSet);
        add(new Label(Txt.MIN), AFTER, SAME); //$NON-NLS-1$
        edTrkSep.setText(Convert.toString(m.getTrkSep()));
        edTrkSep.alignment = RIGHT;

        int offsetIdx = m.getTimeOffsetHours() + 12;
        if (offsetIdx > 26) {
            c.setTimeOffsetHours(0); // TODO: Change in call to control
            offsetIdx = 12;
        }
        cbTimeOffsetHours = new ComboBox(offsetStr);
        add(cbTimeOffsetHours, RIGHT, SAME);
        cbTimeOffsetHours.select(offsetIdx);
        add(new Label(Txt.UTC), BEFORE, SAME);

        // add(new Label("End"),BEFORE,SAME);
        add(cbFileSplitType = new ComboBox(fileStr), LEFT, AFTER + 2);
        cbFileSplitType.select(m.getOutputFileSplitType());
        add(chkConvertWGSToMSL = new MyCheck(Txt.HGHT_GEOID_DIFF), AFTER + 5,
                SAME); //$NON-NLS-1$
        chkConvertWGSToMSL.setChecked(m.isConvertWGS84ToMSL());

        add(btToCSV = new Button(Txt.TO_CSV), LEFT, AFTER + 5); //$NON-NLS-1$
        add(btToGPX = new Button(Txt.TO_GPX), AFTER + 5, SAME); //$NON-NLS-1$
        add(btToKML = new Button(Txt.TO_KML), RIGHT, SAME); //$NON-NLS-1$
        add(btToTRK = new Button(Txt.TO_TRK), BEFORE - 5, SAME); //$NON-NLS-1$

        add(btToPLT = new Button(Txt.TO_PLT), LEFT, AFTER + 2); //$NON-NLS-1$
        add(btToGMAP = new Button(Txt.TO_GMAP), CENTER, SAME); //$NON-NLS-1$
        add(btToNMEA = new Button(Txt.TO_NMEA), RIGHT, SAME); //$NON-NLS-1$

        savedBackColor = btToCSV.getBackColor();

        add(lbUsedMem = new Label(""), LEFT, AFTER + 3);
        add(lbUsedRecords = new Label(""), LEFT, AFTER + 3);

        String s = m.getColorInvalidTrack();
        cbColors.select(0);
        for (int i = 0; i < cbColors.size() - 1; i++) {
            if (s.equals((String) cbColors.getItemAt(i))) {
                cbColors.select(i);
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

    public void updateButtons() {
        chkLogOnOff.setChecked(m.isLoggingActive());
        // m_chkLogOnOff.repaintNow();
        chkLogOverwriteStop.setChecked(m.isLogFullOverwrite());
        // m_chkLogOverwriteStop.repaintNow();
        lbUsedMem.setText(Txt.MEM_USED + Convert.toString(m.logMemUsed()) + "("
                + Convert.toString(m.logMemUsedPercent()) + "%)");
        // m_UsedLabel.repaintNow();
        lbUsedRecords.setText(Txt.NBR_RECORDS
                + Convert.toString(m.logNbrLogPts()) + " ("
                + m.getEstimatedNbrRecordsFree(m.getLogFormat()) + " "
                + Txt.MEM_FREE + ")");
        // m_RecordsLabel.repaintNow();
    }

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
    public void onEvent(final Event event) {
        // Vm.debug("Event:"+event.type+" "+event.consumed);
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed = true;
            if (event.target == btGetLog) {
                c.startDownload();
                // m_btGetLog.press(false);
            } else if (event.target == btCancelGetLog) {
                c.cancelGetLog();
            } else if (event.target == cbDownload) {
                c.setDownloadMethod(cbDownload.getSelectedIndex());
            } else if (event.target == chkLogOnOff) {
                if (chkLogOnOff.getChecked()) {
                    c.startLog();
                } else {
                    c.stopLog();
                }
            } else if (event.target == cbColors) {
                c.setColorInvalidTrack((String) cbColors.getSelectedItem());
            } else if (event.target == cbTimeOffsetHours) {
                int index = 0;
                // Work around superwaba bug
                String tmp = (String) cbTimeOffsetHours.getSelectedItem();
                if (tmp.charAt(0) == '+') {
                    index = 1;
                }
                c.setTimeOffsetHours(Convert.toInt((String) tmp
                        .substring(index)));
            } else if (event.target == chkLogOverwriteStop) {
                c.setLogOverwrite(chkLogOverwriteStop.getChecked());
            } else if (event.target == cbFileSplitType) {
                c.setOutputFileSplitType(cbFileSplitType.getSelectedIndex());
            } else if (event.target == chkConvertWGSToMSL) {
                c.setConvertWGS84ToMSL(chkConvertWGSToMSL.getChecked());
            } else if (event.target == btEndDate) {
                if (cal == null) {
                    cal = new Calendar();
                }
                btCal = btEndDate;
                cal.popupModal();
            } else if (event.target == btStartDate) {
                if (cal == null) {
                    cal = new Calendar();
                }
                btCal = btStartDate;
                cal.popupModal();
            } else if (event.target == btToCSV || event.target == btToKML
                    || event.target == btToPLT || event.target == btToGPX
                    || event.target == btToTRK || event.target == btToGMAP
                    || event.target == btToNMEA) {
                int logType = Model.NO_LOG_LOGTYPE;
                if (event.target == btToCSV) {
                    logType = Model.CSV_LOGTYPE;
                } else if (event.target == btToTRK) {
                    logType = Model.TRK_LOGTYPE;
                } else if (event.target == btToKML) {
                    logType = Model.KML_LOGTYPE;
                } else if (event.target == btToPLT) {
                    logType = Model.PLT_LOGTYPE;
                } else if (event.target == btToGPX) {
                    logType = Model.GPX_LOGTYPE;
                } else if (event.target == btToNMEA) {
                    logType = Model.NMEA_LOGTYPE;
                } else if (event.target == btToGMAP) {
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
            if (event.target == edTrkSep) {
                c.setTrkSep(Convert.toInt(edTrkSep.getText()));
                edTrkSep.setText(Convert.toString(m.getTrkSep()));
            }
            break;

        case ControlEvent.WINDOW_CLOSED:
            if (event.target == cal) {
                waba.util.Date d = cal.getSelectedDate();
                if (d != null) {
                    btCal.setText(d.toString());
                    // Can't change the value of the date, changing all
                    c.setStartDate((new WabaDate(btStartDate.getText(),
                            Settings.dateFormat)).dateToUTCepoch1970());
                    c.setEndDate((new WabaDate(btEndDate.getText(),
                            Settings.dateFormat)).dateToUTCepoch1970()
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
            default:
                break;
        }
    }

    public void modelEvent(final ModelEvent event) {
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
                b = btToCSV;
                break;
            case Model.TRK_LOGTYPE:
                b = btToTRK;
                break;
            case Model.KML_LOGTYPE:
                b = btToKML;
                break;
            case Model.PLT_LOGTYPE:
                b = btToPLT;
                break;
            case Model.GPX_LOGTYPE:
                b = btToGPX;
                break;
            case Model.NMEA_LOGTYPE:
                b = btToNMEA;
                break;
            case Model.GMAP_LOGTYPE:
                b = btToGMAP;
                break;
            default:
                break;
            }
            if (b != null) {
                if (m.isConversionOngoing()) {
                    b.setBackColor(Color.GREEN);
                } else {
                    b.setBackColor(savedBackColor);
                }
                b.repaintNow();
            }
        }
    }
}
