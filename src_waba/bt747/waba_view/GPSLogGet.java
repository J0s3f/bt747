package bt747.waba_view;

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
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
// *** The application was written using the SuperWaba toolset. ***
// *** This is a proprietary development environment based in ***
// *** part on the Waba development environment developed by ***
// *** WabaSoft, Inc. ***
// ********************************************************************
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

import gps.mvc.MtkModel;

import net.sf.bt747.waba.system.WabaDate;

import bt747.Txt;
import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 */
public final class GPSLogGet extends Container implements ModelListener {

    private final Model m;
    private final AppController c;

    private MyCheck chkLogOnOff;
    private MyCheck chkLogOverwriteStop;
    private Button btStartDate;
    private Button btEndDate;
    private Button btGetLog;
    private ComboBox cbHeightConversion;
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
    private static final String[] downloadStr = {
            Txt.getString(Txt.DOWNLOAD_NORMAL),
            Txt.getString(Txt.DOWNLOAD_INCREMENTAL),
            Txt.getString(Txt.DOWNLOAD_FULL) };
    private ComboBox cbFileSplitType;
    private static final String[] fileStr = { Txt.getString(Txt.ONE_FILE),
            Txt.getString(Txt.ONE_FILE_DAY), Txt.getString(Txt.ONE_FILE_TRK) };
    private static final String[] heightCorrectStr = {
            Txt.getString(Txt.HEIGHT_CONV_AUTOMATIC),
            Txt.getString(Txt.HEIGHT_CONV_WGS84_TO_MSL),
            Txt.getString(Txt.HEIGHT_CONV_MSL_TO_WGS84),
            Txt.getString(Txt.HEIGHT_CONV_NONE), };
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

    private static final String convertUTCtoDateString(final int utcTime) {
        BT747Time t = JavaLibBridge.getTimeInstance();
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
    protected final void onStart() {
        super.onStart();
        add(chkLogOnOff = new MyCheck(Txt.getString(Txt.DEV_LOGONOFF)), LEFT,
                TOP); //$NON-NLS-1$
        cbDownload = new ComboBox(downloadStr);
        add(cbDownload, RIGHT, SAME);
        cbDownload.select(m.getDownloadMethod());
        add(chkLogOverwriteStop = new MyCheck(Txt
                .getString(Txt.LOG_OVRWR_FULL)), LEFT, AFTER); //$NON-NLS-1$
        add(new Label(Txt.getString(Txt.DATE_RANGE)), LEFT, AFTER); //$NON-NLS-1$

        add(btStartDate = new Button(convertUTCtoDateString(m
                .getFilterStartTime())), AFTER, SAME); //$NON-NLS-1$
        // m_btStartDate.setMode(Edit.DATE);
        add(btEndDate = new Button(convertUTCtoDateString(m
                .getFilterEndTime())), RIGHT, SAME); //$NON-NLS-1$
        // m_btEndDate.setMode(Edit.DATE);
        add(btGetLog = new Button(Txt.getString(Txt.GET_LOG)), LEFT,
                AFTER + 2); //$NON-NLS-1$
        add(btCancelGetLog = new Button(Txt.getString(Txt.CANCEL_GET)),
                AFTER + 5, SAME); //$NON-NLS-1$

        cbColors = new ComboBox(colors);
        add(cbColors, RIGHT, SAME);
        add(new Label(Txt.getString(Txt.NOFIX_COL)), BEFORE, SAME);

        add(new Label(Txt.getString(Txt.TRK_SEP)), LEFT, AFTER); //$NON-NLS-1$
        add(edTrkSep = new Edit("00000"), AFTER, SAME); //$NON-NLS-1$
        edTrkSep.setValidChars(Edit.numbersSet);
        add(new Label(Txt.getString(Txt.MIN)), AFTER, SAME); //$NON-NLS-1$
        edTrkSep.setText("" + m.getIntOpt(AppSettings.TRKSEP));
        edTrkSep.alignment = RIGHT;

        int offsetIdx = m.getIntOpt(AppSettings.GPSTIMEOFFSETHOURS) + 12;
        if (offsetIdx > 26) {
            c.setIntOpt(AppSettings.GPSTIMEOFFSETHOURS, 0);
            offsetIdx = 12;
        }
        cbTimeOffsetHours = new ComboBox(offsetStr);
        add(cbTimeOffsetHours, RIGHT, SAME);
        cbTimeOffsetHours.select(offsetIdx);
        add(new Label(Txt.getString(Txt.UTC)), BEFORE, SAME);

        // add(new Label("End"),BEFORE,SAME);
        add(cbFileSplitType = new ComboBox(fileStr), LEFT, AFTER + 2);
        cbFileSplitType.select(m.getOutputFileSplitType());
        add(cbHeightConversion = new ComboBox(heightCorrectStr), RIGHT, SAME);//$NON-NLS-1$
        setHeightConversionModeFromSettings();
        add(btToCSV = new Button(Txt.getString(Txt.TO_CSV)), LEFT, AFTER + 5); //$NON-NLS-1$
        add(btToGPX = new Button(Txt.getString(Txt.TO_GPX)), AFTER + 5, SAME); //$NON-NLS-1$
        add(btToKML = new Button(Txt.getString(Txt.TO_KML)), RIGHT, SAME); //$NON-NLS-1$
        add(btToTRK = new Button(Txt.getString(Txt.TO_TRK)), BEFORE - 5, SAME); //$NON-NLS-1$

        add(btToPLT = new Button(Txt.getString(Txt.TO_PLT)), LEFT, AFTER + 2); //$NON-NLS-1$
        add(btToGMAP = new Button(Txt.getString(Txt.TO_GMAP)), CENTER, SAME); //$NON-NLS-1$
        add(btToNMEA = new Button(Txt.getString(Txt.TO_NMEA)), RIGHT, SAME); //$NON-NLS-1$

        savedBackColor = btToCSV.getBackColor();

        add(lbUsedMem = new Label(""), LEFT, AFTER + 3);
        add(lbUsedRecords = new Label(""), LEFT, AFTER + 3);

        String s = m.getStringOpt(AppSettings.COLOR_INVALIDTRACK);
        cbColors.select(0);
        for (int i = 0; i < cbColors.size() - 1; i++) {
            if (s.equals((String) cbColors.getItemAt(i))) {
                cbColors.select(i);
                break;
            }
        }
    }

    private final void updateHeightConversionModeFromControl() {
        int mode;
        switch (cbHeightConversion.getSelectedIndex()) {
        default:
        case 0:
            mode = Model.HEIGHT_AUTOMATIC;
            break;
        case 1:
            mode = Model.HEIGHT_WGS84_TO_MSL;
            break;
        case 2:
            mode = Model.HEIGHT_MSL_TO_WGS84;
            break;
        case 3:
            mode = Model.HEIGHT_NOCHANGE;
            break;
        }
        c.setHeightConversionMode(mode);
    }

    private final void setHeightConversionModeFromSettings() {
        int idx;
        switch (m.getHeightConversionMode()) {
        default:
        case Model.HEIGHT_AUTOMATIC:
            idx = 0;
            break;
        case Model.HEIGHT_WGS84_TO_MSL:
            idx = 1;
            break;
        case Model.HEIGHT_MSL_TO_WGS84:
            idx = 2;
            break;
        case Model.HEIGHT_NOCHANGE:
            idx = 3;
            break;
        }
        cbHeightConversion.select(idx);
    }

    private final void reqLogInfo() {
        // Request device info for this control
        c.setMtkDataNeeded(MtkModel.DATA_LOG_STATUS);
        // Request log version from device
        c.setMtkDataNeeded(MtkModel.DATA_LOG_VERSION);
        // Request mem size from device
        c.setMtkDataNeeded(MtkModel.DATA_MEM_USED);
        // Request number of log points
        c.setMtkDataNeeded(MtkModel.DATA_MEM_PTS_LOGGED);
        c.setMtkDataNeeded(MtkModel.DATA_LOG_OVERWRITE_STATUS);
    }

    private final void updateButtons() {
        chkLogOnOff.setChecked(m.isLoggingActive());
        // m_chkLogOnOff.repaintNow();
        chkLogOverwriteStop.setChecked(m.isLogFullOverwrite());
        // m_chkLogOverwriteStop.repaintNow();
        lbUsedMem.setText(Txt.getString(Txt.MEM_USED) + m.logMemUsed() + "("
                + m.logMemUsedPercent() + "%)");
        // m_UsedLabel.repaintNow();
        lbUsedRecords.setText(Txt.getString(Txt.NBR_RECORDS)
                + m.logNbrLogPts() + " ("
                + m.getEstimatedNbrRecordsFree(m.getLogFormat()) + " "
                + Txt.getString(Txt.MEM_FREE) + ")");
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
    public final void onEvent(final Event event) {
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
                c.setLoggingActive(chkLogOnOff.getChecked());
            } else if (event.target == cbColors) {
                c.setStringOpt(AppSettings.COLOR_INVALIDTRACK,
                        ((String) cbColors.getSelectedItem()));
            } else if (event.target == cbTimeOffsetHours) {
                int index = 0;
                // Work around superwaba bug
                String tmp = (String) cbTimeOffsetHours.getSelectedItem();
                if (tmp.charAt(0) == '+') {
                    index = 1;
                }
                c.setIntOpt(AppSettings.GPSTIMEOFFSETHOURS, JavaLibBridge
                        .toInt((String) tmp.substring(index)));
            } else if (event.target == chkLogOverwriteStop) {
                c.setLogOverwrite(chkLogOverwriteStop.getChecked());
            } else if (event.target == cbFileSplitType) {
                c.setOutputFileSplitType(cbFileSplitType.getSelectedIndex());
            } else if (event.target == cbHeightConversion) {
                updateHeightConversionModeFromControl();
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
                c.setIntOpt(AppSettings.TRKSEP, JavaLibBridge.toInt(edTrkSep
                        .getText()));
                edTrkSep.setText("" + m.getIntOpt(AppSettings.TRKSEP));
            }
            break;

        case ControlEvent.WINDOW_CLOSED:
            if (event.target == cal) {
                waba.util.Date d = cal.getSelectedDate();
                if (d != null) {
                    btCal.setText(d.toString());
                    // Can't change the value of the date, changing all
                    c.setFilterStartTime((new WabaDate(btStartDate.getText(),
                            Settings.dateFormat)).dateToUTCepoch1970());
                    c.setFilterEndTime((new WabaDate(btEndDate.getText(),
                            Settings.dateFormat)).dateToUTCepoch1970()
                            + SECONDS_PER_DAY - 1);
                }
            }
            /*
             * cal = null; Note: If your program uses the Calendar control
             * only a few times, i suggest that you set cal to null so it can
             * get garbage collected. Calendar objects waste memory and it is
             * always a good idea to save memory when possible
             */
            break;
        default:
            break;
        }
    }

    public final void modelEvent(final ModelEvent event) {
        switch (event.getType()) {
        case ModelEvent.UPDATE_LOG_LOG_STATUS:
        case ModelEvent.UPDATE_LOG_REC_METHOD:
        case ModelEvent.UPDATE_LOG_MEM_USED:
        case ModelEvent.UPDATE_LOG_NBR_LOG_PTS:
            updateButtons();
            break;
        case ModelEvent.DOWNLOAD_METHOD_CHANGE:
            cbDownload.select(m.getDownloadMethod());
            break;
        case ModelEvent.CONVERSION_ENDED:
        case ModelEvent.CONVERSION_STARTED: {
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
            break;
        default:
            break;
        }
    }
}
