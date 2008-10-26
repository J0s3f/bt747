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
package bt747.j2se_view;

import gnu.io.CommPortIdentifier;
import gps.BT747Constants;
import gps.convert.Conv;
import gps.log.GPSRecord;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.util.Enumeration;
import net.sf.bt747.j2se.system.J2SEGeneric;
import net.sf.bt747.j2se.system.J2SEMessageListener;

import bt747.Version;
import bt747.model.AppSettings;
import bt747.model.BT747View;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Convert;
import bt747.sys.Generic;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747Time;

/**
 * J2SE Implementation (GUI) of BT747.
 * 
 * @author Mario De Weerd
 */
public class BT747Main extends javax.swing.JFrame implements
        bt747.model.ModelListener, BT747View, WindowListener,
        J2SEMessageListener {

    /**
     * Initialise the lower level interface class. Needed for BT747 to work.
     */
    static {
        Interface
                .setJavaTranslationInterface(new net.sf.bt747.j2se.system.J2SEJavaTranslations());
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Model m;
    private J2SEAppController c;
    
    private static Image appIcon;
    private static final String iconPath = "icons/bt747_16x16.gif";

    private static Image app128Icon;
    private static final String icon128Path = "icons/bt747_128x128.gif";
    
    private static ComboBoxModel modelGpsType; 
    
    private static final void setAppIcon() {
        URL u = BT747Main.class.getResource("/"+iconPath);

        if(u!=null) {
            appIcon = Toolkit.getDefaultToolkit().getImage(u);
        } else {
            appIcon = Toolkit.getDefaultToolkit().getImage(iconPath);
        }

        u = BT747Main.class.getResource("/"+icon128Path);

        if(u!=null) {
            app128Icon = Toolkit.getDefaultToolkit().getImage(u);
        } else {
            app128Icon = Toolkit.getDefaultToolkit().getImage(icon128Path);
        }
        
        modelGpsType = new javax.swing.DefaultComboBoxModel(new String[] {
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("DEFAULT_DEVICE"), 
                "Holux M-241",
                "iTrackU-Nemerix",
                "PhotoTrackr",
                "iTrackU-SIRFIII" });
    }
    
    static {
        setAppIcon();
    }
    
    /** Creates new form BT747Main */
    public BT747Main() {        
        initComponents();
        initAppData();
    }

    public BT747Main(Model m, J2SEAppController c) {
        setModel(m);
        setController(c);
        initComponents();
        initAppData();
    }

    // public void createMessageBoxModal(String title, String msg,
    // String[] buttonCaptions) {
    // MessageBox mb;
    // mb = new MessageBox(title, msg, buttonCaptions);
    // // TODO: finish message box handling and send back events
    // // to controller.
    // }

    public void setController(final Controller c) {
        if (this.m != null) {
            this.m.removeListener(this);
        }
        this.c = (J2SEAppController) c; // Should check that c is an
        // AppController or do it differently
        if (this.m != null) {
            m.addListener(this);
        }
    }

    public void setModel(final Model m) {
        if (this.m != null) {
            this.m.removeListener(this);
        }
        this.m = m;
        this.m.addListener(this);
    }

    /**
     * Initialize application data. Gets the values from the model to set them
     * in the GUI.
     */
    private void initAppData() {
        c.setRootFrame(this);
        jTextArea1.setEnabled(true);
        jTextArea1.setFocusable(true);
        jTextArea1.append(java.lang.System.getProperty("os.name")); // NOI18N
        jTextArea1.append("\n"); // NOI18N
        jTextArea1.append(java.lang.System.getProperty("os.arch")); // NOI18N
        jTextArea1.append("\n"); // NOI18N
        jTextArea1.append(java.lang.System.getProperty("os.version")); // NOI18N
        jTextArea1.append("\n"); // NOI18N
        jTextArea1.append(java.lang.System.getProperty("java.version")); // NOI18N
        jTextArea1.append("\n"); // NOI18N
        jTextArea1.append(lookAndFeelMsg);
        LookAndFeelInfo[] a =UIManager.getInstalledLookAndFeels();
        for(int i=0;i<a.length;i++) {
            jTextArea1.append(a[i].getClassName()+"\n"); // NOI18N
        }
        progressBarUpdate();
        getWorkDirPath();
        getRawLogFilePath();
        getOutputFilePath();
        getIncremental();
        getDefaultPort();
        updateSerialSpeed();
        updateSatGuiItems();
        updateGuiLogFilterSettings();

        // TODO: need to get rid of ',' generated by format.
        txtPDOPMax.setText(String.format((Locale) null, "%.2f", m
                .getFilterMaxPDOP()));  // NOI18N
        txtHDOPMax.setText(String.format((Locale) null, "%.2f", m
                .getFilterMaxHDOP())); // NOI18N
        txtVDOPMax.setText(String.format((Locale) null, "%.2f", m
                .getFilterMaxVDOP())); // NOI18N
        txtNSATMin.setText(Integer.toString(m.getFilterMinNSAT()));
        txtRecCntMin.setText(Integer.toString(m.getFilterMinRecCount()));
        txtRecCntMax.setText(Integer.toString(m.getFilterMaxRecCount()));
        txtDistanceMin.setText(String.format((Locale) null, "%.2f", m
                .getFilterMinDist())); // NOI18N
        txtDistanceMax.setText(String.format((Locale) null, "%.2f", m
                .getFilterMaxDist())); // NOI18N
        txtSpeedMin.setText(String.format((Locale) null, "%.2f", m
                .getFilterMinSpeed())); // NOI18N
        txtSpeedMax.setText(String.format((Locale) null, "%.2f", m
                .getFilterMaxSpeed())); // NOI18N

        tfTrackSeparationTime.setText(Integer.toString(m.getTrkSep()));

        int index = 0;
        switch (m.getOutputFileSplitType()) {
        case 0:
            index = 2;
            break;
        case 1:
            index = 0;
            break;
        case 2:
            index = 1;
            break;
        }
        cbOneFilePerDay.setSelectedIndex(index);
        try {
            cbUTCOffset.setSelectedIndex(m.getTimeOffsetHours() + 12);
        } catch (Exception e) {
            Generic.debug(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Problem_with_UTC_offset"), e);
        }
        cbNotApplyUTCOffset.setSelected(m.getGpxUTC0());
        cbStopOrOverwriteWhenFull.setSelectedIndex(m.isLogFullOverwrite() ? 0
                : 1);
        cbGPXTrkSegWhenSmall.setSelected(m.getGpxTrkSegWhenBig());

        cbImperialUnits.setSelected(m.getBooleanOpt(Model.IMPERIAL));
        cbAdvancedActive.setSelected(m.isAdvFilterActive());
        updateAdvancedFilter();
        updateCbGPSType();

        // TODO: Correct next line
        cbDisableLoggingDuringDownload.setSelected(m.isIncremental());
        cbNotApplyUTCOffset.setSelected(m.getGpxUTC0());

        BT747Time d;
        d = Interface.getTimeInstance();
        d.setUTCTime(m.getFilterStartTime());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT")); // NO18N
        // cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(d.getYear(), d.getMonth() - 1, d.getDay(), 0, 0, 0);
        // startDate.getDateEditor().
        startDate.setDate(cal.getTime());
        // startDate.setCalendar(cal);
        d.setUTCTime(m.getFilterEndTime());
        cal = Calendar.getInstance();
        // cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(d.getYear(), d.getMonth() - 1, d.getDay(), 0, 0, 0);
        endDate.setDate(cal.getTime());
        // endDate.setCalendar(cal);
        // TODO: Deactivate debug by default
        c.setDebug(true);
        btGPSDebug.setSelected(Model.isDebug());
        c.setDebugConn(false);
        btGPSConnectDebug.setSelected(m.isDebugConn());
        // c.setChunkSize(256); // Small for debug
        txtHoluxName.setText(m.getHoluxName());
        cbRecordNumberInfoInLog.setSelected(m
                .getBooleanOpt(AppSettings.IS_RECORDNBR_IN_LOGS));
        setTitle(getTitle()+ " V" + Version.VERSION_NUMBER);
        cbHeightOverMeanSeaLevel.setSelectedIndex(m.isConvertWGS84ToMSL() ? 1
                : 0);
        updateFileFormatData();

        switch (m.getBinDecoder()) {
        case J2SEAppController.DECODER_ORG:
            cbDecoderChoice.setSelectedIndex(0);
            break;
        case J2SEAppController.DECODER_THOMAS:
            cbDecoderChoice.setSelectedIndex(1);
            break;
        }
        lbConversionTime.setVisible(false);

        updateEstimatedNbrRecords();

        getNMEAOutFile();

        addPortsToGui();

        updateColorButtons();

        addWindowListener(this);

        J2SEGeneric.addListener(this);

        AboutBT747.addActionListener(new java.awt.event.ActionListener() {
            /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        });
        Info.addActionListener(new java.awt.event.ActionListener() {
            /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                showLicense();
            }
        });
        
    }

    private final void updateSerialSpeed() {
        int speed = m.getBaudRate();
        cbSerialSpeed.setSelectedItem(new Integer(speed));
    }
    
    @SuppressWarnings("unchecked")
    private void addPortsToGui() {
        try {
            Enumeration<CommPortIdentifier> list = CommPortIdentifier
                    .getPortIdentifiers();
            while (list.hasMoreElements()) {
                CommPortIdentifier iden = list.nextElement();
                if (iden.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    ((javax.swing.DefaultComboBoxModel) cbPortName.getModel())
                            .addElement(iden.getName());
                }
            }
        } catch (Exception e) {
            Generic.debug(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("While_adding_ports"), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.system.J2SEMessageListener#postMessage(java.lang.String)
     */
    public final void postMessage(final String message) {
        synchronized (jTextArea1) {
            jTextArea1.append(message);
        }
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        c.saveSettings();
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    private long conversionStartTime;

    private void updateRMCData(final GPSRecord gps) {
        if (gps.utc > 0) {
            // Da
            // long utc=gps.utc*1000L;
            // utc=System.currentTimeMillis();
            // Date t=new java.util.Date(utc);
            // String TimeStr = new SimpleDateFormat().format(t);
            String TimeStr;
            // Date t=new Date(System.currentTimeMillis());
            // java.util.Date x=new Date(gps.utc*1000L);
            // x.setYear(2000);
            // TimeStr=x.toString();
            // x.setTime(gps.utc*1000L);
            // System.out.println(TimeStr);
            // TODO: Look for some ideas here:
            // http://java.sun.com/j2se/1.5.0/docs/api/java/util/Formatter.html#syntax
            BT747Time t = Interface.getTimeInstance();
            t.setUTCTime(gps.utc);
            TimeStr =
            // String.valueOf(t.getYear())+"/"
            // +( t.getMonth()<10?"0":"")+String.valueOf(t.getMonth())+"/"
            // +( t.getDay()<10?"0":"")+String.valueOf(t.getDay())+" " +
            (t.getHour() < 10 ? "0" : "") + String.valueOf(t.getHour()) + ":"
                    + (t.getMinute() < 10 ? "0" : "")
                    + String.valueOf(t.getMinute()) + ":"
                    + (t.getSecond() < 10 ? "0" : "")
                    + String.valueOf(t.getSecond());
            lbTime.setText(TimeStr); // NO18N
        }
        updateGPSData(gps);
    }
    
    private final void showAbout() {
        JOptionPane.showMessageDialog(this,
                "<html>BT747 V" + Version.VERSION_NUMBER + " (" + Version.DATE + ")<br>"  // NOI18N
                + "Build: "
                + Version.BUILD_STR + "<br><br>"
                + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("ABOUT_TEXT"),
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("ABOUT_TITLE"),
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(app128Icon)
                );
    }

    
    private final void showLicense() {
        JOptionPane.showMessageDialog(this,
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("LICENSE_TEXT"),
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("LICENSE_TITLE"),
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(app128Icon)
                );
    }

    private void updateGPSData(final GPSRecord gps) {

        lbLatitude.setText(String.format((Locale) null, "%.8f", gps.latitude)); // NOI18N
        // lbHeight.setText(String.valueOf(gps.height,3)+"m");
        lbLongitude
                .setText(String.format((Locale) null, "%.8f", gps.longitude)); // NOI18N
        lbGeoid.setText(String.format((Locale) null, "%.1f", gps.geoid) // NOI18N
                + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("m")
                + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("(calc:")
                + String.format((Locale) null, "%.1f", Conv.wgs84Separation(
                        gps.latitude, gps.longitude))
                        + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("m") 
                        + ")"); // NOI18N

    }

    boolean btConnectFunctionIsConnect = true;

    /**
     * 
     */
    private void doLogConversion() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate.getDate());
        BT747Date nd = Interface.getDateInstance(
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1
                        - Calendar.JANUARY, cal.get(Calendar.YEAR));
        int startTime = nd.dateToUTCepoch1970();
        cal.setTime(endDate.getDate());
        nd = Interface.getDateInstance(cal.get(Calendar.DAY_OF_MONTH), cal
                .get(Calendar.MONTH)
                + 1 - Calendar.JANUARY, cal.get(Calendar.YEAR));
        int endTime = nd.dateToUTCepoch1970();
        endTime += (24 * 3600 - 1); // Round to midnight / End of day
        // Offset requested split time
        long offset;
        offset = 60 * (sfTimeSplitHours.getValue() * 60 + spTimeSplitMinutes
                .getValue());
        startTime += offset;
        endTime += offset;
        // Now actually set time filter.
        c.setFilterStartTime((int) (startTime));
        c.setFilterEndTime((int) (endTime));
        c.convertLog(selectedFormat);
    }
    
    /**
     * Show error message that file could not be opened.
     * 
     * @param fileName
     *            The file that could not be opened.
     */
    private void couldNotOpenFileMessage(final String fileName) {
        JOptionPane.showMessageDialog(this, 
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Problem_opening_file"),
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("The_application_could_not_open_")
                        + fileName
                        + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("_check_if_loc_exists")
                        + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("in_case_it_is_an_output_file."),
                        JOptionPane.WARNING_MESSAGE,
                        null
                        );
    }

    public void modelEvent(ModelEvent e) {
        // TODO Auto-generated method stub
        int type = e.getType();
        switch (type) {
        case ModelEvent.GPRMC:
            updateRMCData((GPSRecord) e.getArg());
            break;
        case ModelEvent.UPDATE_LOG_MEM_USED:
            txtMemoryUsed.setText(Convert.toString(m.logMemUsed()) + " ("
                    + Convert.toString(m.logMemUsedPercent()) + "%)");  // NOI18N
            break;
        case ModelEvent.UPDATE_LOG_FLASH:
            lbFlashInfo.setText(((m.getFlashManuProdID() != 0) ? Convert
                    .unsigned2hex(m.getFlashManuProdID(), 8)
                    + " " + m.getFlashDesc() : "")); // NOI18N
            break;
        case ModelEvent.ERASE_ONGOING_NEED_POPUP:
            c.createErasePopup();
            break;
        case ModelEvent.ERASE_DONE_REMOVE_POPUP:
            c.removeErasePopup();
            break;

        case ModelEvent.UPDATE_MTK_VERSION:
        case ModelEvent.UPDATE_MTK_RELEASE:
            lbModel.setText(m.getModel());
            String fwString;
            fwString = "";
            lbFirmWare.setToolTipText("");
            if (m.getMainVersion().length()
                    + m.getFirmwareVersion().length() != 0) {
                if (m.getMainVersion().length()
                        + m.getFirmwareVersion().length() > 20) {
                    fwString = "<html>";
                    if (m.getMainVersion().length() > 20) {
                        fwString += m.getMainVersion().substring(0, 17) + "...";
                        lbFirmWare.setToolTipText(m.getMainVersion());
                    } else {
                        fwString += m.getMainVersion();
                    }
                    fwString += (m.getMainVersion().length()!=0?"<br>":"") + m.getFirmwareVersion(); // NOI18N
                } else {
                    fwString = m.getMainVersion() + " "
                            + m.getFirmwareVersion(); // NOI18N
                }
            }
            lbFirmWare.setText(fwString); // NOI18N
            break;
        case ModelEvent.UPDATE_LOG_VERSION:
            lbLoggerSWVersion.setText(m.getMtkLogVersion());
            break;
        case ModelEvent.UPDATE_SBAS:
            cbUseSBAS.setSelected(m.isSBASEnabled());
            break;
        case ModelEvent.UPDATE_SBAS_TEST:
            cbIncludeTestSBAS.setSelected(m.isSBASTestEnabled());
            break;
        // TODO
        case ModelEvent.UPDATE_DGPS_MODE:
            try {
                cbDGPSType.setSelectedIndex(m.getDgpsMode());
            } catch (Exception ee) {
                // TODO: handle exception
                Generic.debug(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Unknown_DGPS_Mode") + m.getDgpsMode(), ee);
            }

            // TODO
            // cbDGPSMode.select(m.getDgpsMode());
            // if (ENABLE_PWR_SAVE_CONTROL) {
            // chkPowerSaveOnOff.setChecked(m.isPowerSaveEnabled());
            // }
        case ModelEvent.UPDATE_DATUM:
            try {
                // jComboBox23.setSelectedIndex(m.getDatum());
            } catch (Exception ee) {
                // TODO: handle exception
                Generic.debug(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Unknown_DATUM") + m.getDatum(), ee);
            }
            // TODO
            // cbDatumMode.select(m.getDatum());
        case ModelEvent.UPDATE_LOG_TIME_INTERVAL:
            ckLogTimeActive.setSelected(m.getLogTimeInterval() != 0);
            txtLogTimeInterval.setText(Convert.toString(
                    m.getLogTimeInterval() / 10., 1));
            break;
        case ModelEvent.UPDATE_LOG_SPEED_INTERVAL:
            ckLogSpeedActive.setSelected(m.getLogSpeedInterval() != 0);
            txtLogSpeedInterval.setText(Convert.toString(m
                    .getLogSpeedInterval()));
            break;
        case ModelEvent.UPDATE_LOG_DISTANCE_INTERVAL:
            ckLogDistanceActive.setSelected(m.getLogDistanceInterval() != 0);
            txtLogDistanceInterval.setText(Convert.toString(m
                    .getLogDistanceInterval() / 10., 1));
            break;
        case ModelEvent.UPDATE_FIX_PERIOD:
            jTextField7.setText(Convert.toString(m.getLogFixPeriod()));
            break;
        case ModelEvent.GPGGA:
            updateGPSData((GPSRecord) e.getArg());
            break;
        case ModelEvent.UPDATE_LOG_FORMAT:
            updateLogFormatData();
            break;
        // case ModelEvent.LOGFILEPATH_UPDATE:
        // getRawLogFilePath();
        // break;
        // case ModelEvent.OUTPUTFILEPATH_UPDATE:
        // getOutputFilePath();
        // break;
        // case ModelEvent.WORKDIRPATH_UPDATE:
        // getWorkDirPath();
        // break;
        case ModelEvent.INCREMENTAL_CHANGE:
            getIncremental();
            break;
        case ModelEvent.UPDATE_OUTPUT_NMEA_PERIOD:
            getNMEAOutPeriods();
            break;
        case ModelEvent.TRK_VALID_CHANGE:
        case ModelEvent.TRK_RCR_CHANGE:
        case ModelEvent.WAY_VALID_CHANGE:
        case ModelEvent.WAY_RCR_CHANGE:
            updateGuiLogFilterSettings();
            break;
        case ModelEvent.CONVERSION_STARTED:
            conversionStartTime = System.currentTimeMillis();
            break;
        case ModelEvent.CONVERSION_ENDED:
            lbConversionTime
                    .setText(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Time_to_convert:_")
                            + ((int) (System.currentTimeMillis() - conversionStartTime))
                            + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("_ms"));
            lbConversionTime.setVisible(true);
            break;
        case ModelEvent.CONNECTED:
            btConnect.setText(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Disconnect"));
            btConnectFunctionIsConnect = false;

            // TODO: Find the way to do this on tab entry.
            c.reqHoluxName();
            c.reqFlashUserOption();
            c.reqNMEAPeriods();

            c.reqLogOverwrite();
            c.reqLogReasonStatus();
            c.reqSBASEnabled();
            c.reqSBASTestEnabled();
            c.reqFixInterval();
            c.reqBTAddr();
            c.reqMtkLogVersion();
            c.reqDeviceInfo();
            c.reqDatumMode();

            break;
        case ModelEvent.DISCONNECTED:
            btConnect.setText(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Connect"));
            btConnectFunctionIsConnect = true;
            break;
        case ModelEvent.DOWNLOAD_STATE_CHANGE:
        case ModelEvent.LOG_DOWNLOAD_DONE:
        case ModelEvent.LOG_DOWNLOAD_STARTED:
            progressBarUpdate();
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            int overwriteResp;
            overwriteResp =
            JOptionPane.showOptionDialog(this,
                    java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("OVERWRITE_DATA_QUESTION"),
                    java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("OVERWRITING_DATA"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null /* icon */,
                    null /* options */,
                    null /* initialValue */);
            c.replyToOkToOverwrite(overwriteResp==JOptionPane.OK_OPTION);
//            MessageBox mb;
//            final String[] yesNo = { java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Yes"), java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("No") };
//            mb = new MessageBox(
//                    java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("OVERWRITING_DATA"),
//                    java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("The_current_raw_data_file_is_not_empty,<br>")
//                            + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("and,_the_data_in_the_GPS_Logger_is_different!!!<br>")
//                            + java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("DO_YOU_CONFIRM_OVERWRITING_THE_CURRENT_DIFFERENT_DATA?"),
//                    yesNo);
//            mb.popupBlockingModal();
//            c.replyToOkToOverwrite(mb.getAnswer() == 0);
            break;
        case ModelEvent.COULD_NOT_OPEN_FILE:
            couldNotOpenFileMessage((String) e.getArg());
            break;
        case ModelEvent.UPDATE_FLASH_CONFIG:
            getFlashConfig();
            break;
        case ModelEvent.UPDATE_HOLUX_NAME:
            txtHoluxName.setText(m.getHoluxName());
            break;
        }

    }

    InputVerifier IntVerifier = new InputVerifier() {

        public boolean verify(JComponent comp) {
            boolean returnValue;
            JTextField textField = (JTextField) comp;
            try {
                Integer.parseInt(textField.getText());
                returnValue = true;
            } catch (NumberFormatException e) {
                returnValue = false;
            }
            return returnValue;
        }
    };
    InputVerifier FloatVerifier = new InputVerifier() {

        public boolean verify(JComponent comp) {
            boolean returnValue;
            JTextField textField = (JTextField) comp;
            try {
                Float.parseFloat(textField.getText());
                returnValue = true;
            } catch (NumberFormatException e) {
                returnValue = false;
            }
            return returnValue;
        }
    };

    void updateGuiLogFilterSettings() {
        int trkRCR = m.getTrkPtRCR();
        int trkValid = m.getTrkPtValid();
        int wayRCR = m.getWayPtRCR();
        int wayValid = m.getWayPtValid();

        cbTrkNoFix
                .setSelected((trkValid & BT747Constants.VALID_NO_FIX_MASK) != 0);
        cbTrkPPS.setSelected((trkValid & BT747Constants.VALID_PPS_MASK) != 0);
        cbTrkEstimate
                .setSelected((trkValid & BT747Constants.VALID_ESTIMATED_MASK) != 0);
        cbTrkManual
                .setSelected((trkValid & BT747Constants.VALID_MANUAL_MASK) != 0);
        cbTrkSPS.setSelected((trkValid & BT747Constants.VALID_SPS_MASK) != 0);
        cbTrkFRTK.setSelected((trkValid & BT747Constants.VALID_FRTK_MASK) != 0);
        cbTrkDGPS.setSelected((trkValid & BT747Constants.VALID_DGPS_MASK) != 0);
        cbTrkSimulate
                .setSelected((trkValid & BT747Constants.VALID_SIMULATOR_MASK) != 0);
        cbTrkRTK.setSelected((trkValid & BT747Constants.VALID_RTK_MASK) != 0);

        cbTrkTime.setSelected((BT747Constants.RCR_TIME_MASK & trkRCR) != 0);
        cbTrkSpeed.setSelected((BT747Constants.RCR_SPEED_MASK & trkRCR) != 0);
        cbTrkDistance
                .setSelected((BT747Constants.RCR_DISTANCE_MASK & trkRCR) != 0);
        cbTrkButton.setSelected((BT747Constants.RCR_BUTTON_MASK & trkRCR) != 0);
        cbTrkUser1.setSelected((BT747Constants.RCR_APP0_MASK & trkRCR) != 0);
        cbTrkUser2.setSelected((BT747Constants.RCR_APP1_MASK & trkRCR) != 0);
        cbTrkUser3.setSelected((BT747Constants.RCR_APP2_MASK & trkRCR) != 0);
        cbTrkUser4.setSelected((BT747Constants.RCR_APP3_MASK & trkRCR) != 0);
        cbTrkUser5.setSelected((BT747Constants.RCR_APP4_MASK & trkRCR) != 0);
        cbTrkUser6.setSelected((BT747Constants.RCR_APP5_MASK & trkRCR) != 0);
        cbTrkUser7.setSelected((BT747Constants.RCR_APP6_MASK & trkRCR) != 0);
        cbTrkUser8.setSelected((BT747Constants.RCR_APP7_MASK & trkRCR) != 0);
        cbTrkUser9.setSelected((BT747Constants.RCR_APP8_MASK & trkRCR) != 0);
        cbTrkUser10.setSelected((BT747Constants.RCR_APP9_MASK & trkRCR) != 0);
        cbTrkUser11.setSelected((BT747Constants.RCR_APPY_MASK & trkRCR) != 0);
        cbTrkUser12.setSelected((BT747Constants.RCR_APPZ_MASK & trkRCR) != 0);

        cbWayNoFix
                .setSelected((wayValid & BT747Constants.VALID_NO_FIX_MASK) != 0);
        cbWayPPS.setSelected((wayValid & BT747Constants.VALID_PPS_MASK) != 0);
        cbWayEstimate
                .setSelected((wayValid & BT747Constants.VALID_ESTIMATED_MASK) != 0);
        cbWayManual
                .setSelected((wayValid & BT747Constants.VALID_MANUAL_MASK) != 0);
        cbWaySPS.setSelected((wayValid & BT747Constants.VALID_SPS_MASK) != 0);
        cbWayFRTK.setSelected((wayValid & BT747Constants.VALID_FRTK_MASK) != 0);
        cbWayDGPS.setSelected((wayValid & BT747Constants.VALID_DGPS_MASK) != 0);
        cbWaySimulate
                .setSelected((wayValid & BT747Constants.VALID_SIMULATOR_MASK) != 0);
        cbWayRTK.setSelected((wayValid & BT747Constants.VALID_RTK_MASK) != 0);

        cbWayTime.setSelected((BT747Constants.RCR_TIME_MASK & wayRCR) != 0);
        cbWaySpeed.setSelected((BT747Constants.RCR_SPEED_MASK & wayRCR) != 0);
        cbWayDistance
                .setSelected((BT747Constants.RCR_DISTANCE_MASK & wayRCR) != 0);
        cbWayButton.setSelected((BT747Constants.RCR_BUTTON_MASK & wayRCR) != 0);
        cbWayUser1.setSelected((BT747Constants.RCR_APP0_MASK & wayRCR) != 0);
        cbWayUser2.setSelected((BT747Constants.RCR_APP1_MASK & wayRCR) != 0);
        cbWayUser3.setSelected((BT747Constants.RCR_APP2_MASK & wayRCR) != 0);
        cbWayUser4.setSelected((BT747Constants.RCR_APP3_MASK & wayRCR) != 0);
        cbWayUser5.setSelected((BT747Constants.RCR_APP4_MASK & wayRCR) != 0);
        cbWayUser6.setSelected((BT747Constants.RCR_APP5_MASK & wayRCR) != 0);
        cbWayUser7.setSelected((BT747Constants.RCR_APP6_MASK & wayRCR) != 0);
        cbWayUser8.setSelected((BT747Constants.RCR_APP7_MASK & wayRCR) != 0);
        cbWayUser9.setSelected((BT747Constants.RCR_APP8_MASK & wayRCR) != 0);
        cbWayUser10.setSelected((BT747Constants.RCR_APP9_MASK & wayRCR) != 0);
        cbWayUser11.setSelected((BT747Constants.RCR_APPY_MASK & wayRCR) != 0);
        cbWayUser12.setSelected((BT747Constants.RCR_APPZ_MASK & wayRCR) != 0);
    }

    void setTrkValidFilterSettings() {
        int trkValid = 0;
        if (cbTrkNoFix.isSelected()) {
            trkValid |= BT747Constants.VALID_NO_FIX_MASK;
        }
        if (cbTrkPPS.isSelected()) {
            trkValid |= BT747Constants.VALID_PPS_MASK;
        }
        if (cbTrkEstimate.isSelected()) {
            trkValid |= BT747Constants.VALID_ESTIMATED_MASK;
        }
        if (cbTrkManual.isSelected()) {
            trkValid |= BT747Constants.VALID_MANUAL_MASK;
        }
        if (cbTrkSPS.isSelected()) {
            trkValid |= BT747Constants.VALID_SPS_MASK;
        }
        if (cbTrkFRTK.isSelected()) {
            trkValid |= BT747Constants.VALID_FRTK_MASK;
        }
        if (cbTrkDGPS.isSelected()) {
            trkValid |= BT747Constants.VALID_DGPS_MASK;
        }
        if (cbTrkSimulate.isSelected()) {
            trkValid |= BT747Constants.VALID_SIMULATOR_MASK;
        }
        if (cbTrkRTK.isSelected()) {
            trkValid |= BT747Constants.VALID_RTK_MASK;
        }
        c.setTrkPtValid(trkValid);
    }

    void setWayValidFilterSettings() {

        int wayValid = 0;
        if (cbWayNoFix.isSelected()) {
            wayValid |= BT747Constants.VALID_NO_FIX_MASK;
        }
        if (cbWayPPS.isSelected()) {
            wayValid |= BT747Constants.VALID_PPS_MASK;
        }
        if (cbWayEstimate.isSelected()) {
            wayValid |= BT747Constants.VALID_ESTIMATED_MASK;
        }
        if (cbWayManual.isSelected()) {
            wayValid |= BT747Constants.VALID_MANUAL_MASK;
        }
        if (cbWaySPS.isSelected()) {
            wayValid |= BT747Constants.VALID_SPS_MASK;
        }
        if (cbWayFRTK.isSelected()) {
            wayValid |= BT747Constants.VALID_FRTK_MASK;
        }
        if (cbWayDGPS.isSelected()) {
            wayValid |= BT747Constants.VALID_DGPS_MASK;
        }
        if (cbWaySimulate.isSelected()) {
            wayValid |= BT747Constants.VALID_SIMULATOR_MASK;
        }
        if (cbWayRTK.isSelected()) {
            wayValid |= BT747Constants.VALID_RTK_MASK;
        }
        c.setWayPtValid(wayValid);
    }

    void setTrkRCRFilterSettings() {
        int trkRCR = 0;
        if (cbTrkTime.isSelected()) {
            trkRCR |= BT747Constants.RCR_TIME_MASK;
        }
        if (cbTrkSpeed.isSelected()) {
            trkRCR |= BT747Constants.RCR_SPEED_MASK;
        }
        if (cbTrkDistance.isSelected()) {
            trkRCR |= BT747Constants.RCR_DISTANCE_MASK;
        }
        if (cbTrkButton.isSelected()) {
            trkRCR |= BT747Constants.RCR_BUTTON_MASK;
        }
        if (cbTrkUser1.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP0_MASK;
        }
        if (cbTrkUser2.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP1_MASK;
        }
        if (cbTrkUser3.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP2_MASK;
        }
        if (cbTrkUser4.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP3_MASK;
        }
        if (cbTrkUser5.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP4_MASK;
        }
        if (cbTrkUser6.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP5_MASK;
        }
        if (cbTrkUser7.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP6_MASK;
        }
        if (cbTrkUser8.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP7_MASK;
        }
        if (cbTrkUser9.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP8_MASK;
        }
        if (cbTrkUser10.isSelected()) {
            trkRCR |= BT747Constants.RCR_APP9_MASK;
        }
        if (cbTrkUser11.isSelected()) {
            trkRCR |= BT747Constants.RCR_APPY_MASK;
        }
        if (cbTrkUser12.isSelected()) {
            trkRCR |= BT747Constants.RCR_APPZ_MASK;
        }
        c.setTrkPtRCR(trkRCR);
    }

    void setWayRCRFilterSettings() {
        int wayRCR = 0;
        if (cbWayTime.isSelected()) {
            wayRCR |= BT747Constants.RCR_TIME_MASK;
        }
        if (cbWaySpeed.isSelected()) {
            wayRCR |= BT747Constants.RCR_SPEED_MASK;
        }
        if (cbWayDistance.isSelected()) {
            wayRCR |= BT747Constants.RCR_DISTANCE_MASK;
        }
        if (cbWayButton.isSelected()) {
            wayRCR |= BT747Constants.RCR_BUTTON_MASK;
        }
        if (cbWayUser1.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP0_MASK;
        }
        if (cbWayUser2.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP1_MASK;
        }
        if (cbWayUser3.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP2_MASK;
        }
        if (cbWayUser4.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP3_MASK;
        }
        if (cbWayUser5.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP4_MASK;
        }
        if (cbWayUser6.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP5_MASK;
        }
        if (cbWayUser7.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP6_MASK;
        }
        if (cbWayUser8.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP7_MASK;
        }
        if (cbWayUser9.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP8_MASK;
        }
        if (cbWayUser10.isSelected()) {
            wayRCR |= BT747Constants.RCR_APP9_MASK;
        }
        if (cbWayUser11.isSelected()) {
            wayRCR |= BT747Constants.RCR_APPY_MASK;
        }
        if (cbWayUser12.isSelected()) {
            wayRCR |= BT747Constants.RCR_APPZ_MASK;
        }
        c.setWayPtRCR(wayRCR);
    }

    void getNMEAOutFile() {
        // c.setNMEAset(maskNMEAset);
        // Should be checkboxes...
        int outFormat = m.getNMEAset();
        ;

        lbNMEAFileGLL
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GLL_IDX)) != 0);

        lbNMEAFileRMC
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_RMC_IDX)) != 0);

        lbNMEAFileVTG
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_VTG_IDX)) != 0);

        lbNMEAFileGGA
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GGA_IDX)) != 0);

        lbNMEAFileGSA
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GSA_IDX)) != 0);

        lbNMEAFileGSV
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GSV_IDX)) != 0);

        lbNMEAFileGRS
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GRS_IDX)) != 0);

        lbNMEAFileGST
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GST_IDX)) != 0);

        lbNMEAFileType8
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE8_IDX)) != 0);

        lbNMEAFileType9
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE9_IDX)) != 0);

        lbNMEAFileType10
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE10_IDX)) != 0);

        lbNMEAFileType11
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE11_IDX)) != 0);

        lbNMEAFileType12
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE12_IDX)) != 0);

        lbNMEAFileMALM
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MALM_IDX)) != 0);

        lbNMEAFileMEPH
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MEPH_IDX)) != 0);

        lbNMEAFileMDGP
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MDGP_IDX)) != 0);

        lbNMEAFileMDBG
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MDBG_IDX)) != 0);

        lbNMEAFileZDA
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_ZDA_IDX)) != 0);

        lbNMEAFileMCHN
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MCHN_IDX)) != 0);
    }

    void setNMEAOutFile() {
        // c.setNMEAset(maskNMEAset);
        // Should be checkboxes...
        int outFormat = 0;

        if (lbNMEAFileGLL.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GLL_IDX);
        }
        if (lbNMEAFileRMC.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_RMC_IDX);
        }
        if (lbNMEAFileVTG.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_VTG_IDX);
        }
        if (lbNMEAFileGGA.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GGA_IDX);
        }
        if (lbNMEAFileGSA.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GSA_IDX);
        }
        if (lbNMEAFileGSV.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GSV_IDX);
        }
        if (lbNMEAFileGRS.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GRS_IDX);
        }
        if (lbNMEAFileGST.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GST_IDX);
        }
        if (lbNMEAFileType8.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE8_IDX);
        }
        if (lbNMEAFileType9.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE9_IDX);
        }
        if (lbNMEAFileType10.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE10_IDX);
        }
        if (lbNMEAFileType11.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE11_IDX);
        }
        if (lbNMEAFileType12.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE12_IDX);
        }
        if (lbNMEAFileMALM.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MALM_IDX);
        }
        if (lbNMEAFileMEPH.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MEPH_IDX);
        }
        if (lbNMEAFileMDGP.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MDGP_IDX);
        }
        if (lbNMEAFileMDBG.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MDBG_IDX);
        }
        if (lbNMEAFileZDA.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_ZDA_IDX);
        }
        if (lbNMEAFileMCHN.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MCHN_IDX);
        }

        c.setNMEAset(outFormat);
    }

    void getNMEAOutPeriods() {
        cbNMEAOutGLL.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GLL_IDX));
        cbNMEAOutRMC.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_RMC_IDX));
        cbNMEAOutVTG.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_VTG_IDX));
        cbNMEAOutGGA.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GGA_IDX));
        cbNMEAOutGSA.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GSA_IDX));
        cbNMEAOutGSV.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GSV_IDX));
        cbNMEAOutGRS.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GRS_IDX));
        cbNMEAOutGST.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GST_IDX));
        cbNMEAOutType8.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE8_IDX));
        cbNMEAOutType9.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE9_IDX));
        cbNMEAOutType10.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE10_IDX));
        cbNMEAOutType11.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE11_IDX));
        cbNMEAOutType12.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE12_IDX));
        cbNMEAOutMALM.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MALM_IDX));
        cbNMEAOutMEPH.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MEPH_IDX));
        cbNMEAOutMDGP.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MDGP_IDX));
        cbNMEAOutMDBG.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MDBG_IDX));
        cbNMEAOutZDA.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_ZDA_IDX));
        cbNMEAOutMCHN.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MCHN_IDX));
    }

    void setNMEAOutPeriods() {
        int[] Periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        Periods[BT747Constants.NMEA_SEN_GLL_IDX] = cbNMEAOutGLL
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_RMC_IDX] = cbNMEAOutRMC
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_VTG_IDX] = cbNMEAOutVTG
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GGA_IDX] = cbNMEAOutGGA
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GSA_IDX] = cbNMEAOutGSA
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GSV_IDX] = cbNMEAOutGSV
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GRS_IDX] = cbNMEAOutGRS
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GST_IDX] = cbNMEAOutGST
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE8_IDX] = cbNMEAOutType8
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE9_IDX] = cbNMEAOutType9
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE10_IDX] = cbNMEAOutType10
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE11_IDX] = cbNMEAOutType11
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE12_IDX] = cbNMEAOutType12
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MALM_IDX] = cbNMEAOutMALM
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MEPH_IDX] = cbNMEAOutMEPH
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MDGP_IDX] = cbNMEAOutMDGP
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MDBG_IDX] = cbNMEAOutMDBG
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_ZDA_IDX] = cbNMEAOutZDA
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MCHN_IDX] = cbNMEAOutMCHN
                .getSelectedIndex();

        c.setNMEAPeriods(Periods);
    }

    void getFlashConfig() {
        txtFlashTimesLeft.setText(Convert
                .toString(m.getDtUserOptionTimesLeft()));
        txtFlashUpdateRate.setText(Convert.toString(m.getDtUpdateRate()));
        txtFlashBaudRate.setText(Convert.toString(m.getDtBaudRate()));
        cbFlashGLL.setSelectedIndex(m.getDtGLL_Period());
        cbFlashRMC.setSelectedIndex(m.getDtRMC_Period());
        cbFlashVTG.setSelectedIndex(m.getDtVTG_Period());
        cbFlashGSA.setSelectedIndex(m.getDtGSA_Period());
        cbFlashGSV.setSelectedIndex(m.getDtGSV_Period());
        cbFlashGGA.setSelectedIndex(m.getDtGGA_Period());
        cbFlashZDA.setSelectedIndex(m.getDtZDA_Period());
        cbFlashMCHN.setSelectedIndex(m.getDtMCHN_Period());
    }

    void setFlashConfig() {
        boolean lock;
        int updateRate;
        int baudRate;
        int periodGLL;
        int periodRMC;
        int periodVTG;
        int periodGSA;
        int periodGSV;
        int periodGGA;
        int periodZDA;
        int periodMCHN;
        lock = false;
        updateRate = Convert.toInt(txtFlashUpdateRate.getText());
        baudRate = Convert.toInt(txtFlashBaudRate.getText());
        periodGLL = cbFlashGLL.getSelectedIndex();
        periodRMC = cbFlashRMC.getSelectedIndex();
        periodVTG = cbFlashVTG.getSelectedIndex();
        periodGSA = cbFlashGSA.getSelectedIndex();
        periodGSV = cbFlashGSV.getSelectedIndex();
        periodGGA = cbFlashGGA.getSelectedIndex();
        periodZDA = cbFlashZDA.getSelectedIndex();
        periodMCHN = cbFlashMCHN.getSelectedIndex();
        c.setFlashConfig(lock, updateRate, baudRate, periodGLL, periodRMC,
                periodVTG, periodGSA, periodGSV, periodGGA, periodZDA,
                periodMCHN);
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    private void initComponents() {//GEN-BEGIN:initComponents

        jDialog1 = new javax.swing.JDialog();
        pnBottomInformation = new javax.swing.JPanel();
        DownloadProgressBar = new javax.swing.JProgressBar();
        DownloadProgressLabel = new javax.swing.JLabel();
        cbPortName = new javax.swing.JComboBox();
        btConnect = new javax.swing.JButton();
        cbSerialSpeed = new javax.swing.JComboBox();
        lbSerialSpeed = new javax.swing.JLabel();
        tabbedPanelAll = new javax.swing.JTabbedPane();
        LogOperationsPanel = new javax.swing.JPanel();
        pnFiles = new javax.swing.JPanel();
        tfWorkDirectory = new javax.swing.JTextField();
        tfRawLogFilePath = new javax.swing.JTextField();
        tfOutputFileBaseName = new javax.swing.JTextField();
        btWorkingDirectory = new javax.swing.JButton();
        btRawLogFile = new javax.swing.JButton();
        btOutputFile = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        pnDownload = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        cbIncremental = new javax.swing.JCheckBox();
        cbDisableLoggingDuringDownload = new javax.swing.JCheckBox();
        btDownloadIBlue = new javax.swing.JButton();
        btDownloadFromNumerix = new javax.swing.JButton();
        pnConvert = new javax.swing.JPanel();
        btConvert = new javax.swing.JButton();
        cbFormat = new javax.swing.JComboBox();
        jPanel18 = new javax.swing.JPanel();
        jLabel44 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        startDate = new com.toedter.calendar.JDateChooser();
        endDate = new com.toedter.calendar.JDateChooser();
        sfTimeSplitHours = new com.toedter.components.JSpinField();
        jLabel5 = new javax.swing.JLabel();
        spTimeSplitMinutes = new com.toedter.components.JSpinField();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cbGPSType = new javax.swing.JComboBox();
        jLabel57 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        cbDecoderChoice = new javax.swing.JComboBox();
        lbConversionTime = new javax.swing.JLabel();
        GPSDecodePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbLatitude = new javax.swing.JLabel();
        lbLongitude = new javax.swing.JLabel();
        lbTime = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbGeoid = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lbFlashInfo = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbModel = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lbFirmWare = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lbMemoryUsed = new javax.swing.JLabel();
        lbLoggerSWVersion = new javax.swing.JLabel();
        txtMemoryUsed = new javax.swing.JLabel();
        FileSettingsPanel = new javax.swing.JPanel();
        pnVarious = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cbUTCOffset = new javax.swing.JComboBox();
        cbHeightOverMeanSeaLevel = new javax.swing.JComboBox();
        cbRecordNumberInfoInLog = new javax.swing.JCheckBox();
        cbImperialUnits = new javax.swing.JCheckBox();
        lbTimeZone = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cbStandardOrDaylightSaving = new javax.swing.JComboBox();
        cbOneFilePerDay = new javax.swing.JComboBox();
        cbGoodFixColor = new javax.swing.JButton();
        cbNoFixColor = new javax.swing.JButton();
        pnSeparation = new javax.swing.JPanel();
        lbNewTrackAfter = new javax.swing.JLabel();
        tfTrackSeparationTime = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        pnFileOutputFields = new javax.swing.JPanel();
        pnFileOutputFieldInner = new javax.swing.JPanel();
        pnFileReason = new javax.swing.JPanel();
        cbFileRCR = new javax.swing.JCheckBox();
        pnFileTime = new javax.swing.JPanel();
        cbFileUTCTime = new javax.swing.JCheckBox();
        cbFileMilliSeconds = new javax.swing.JCheckBox();
        pnFilePosition = new javax.swing.JPanel();
        cbFileLat = new javax.swing.JCheckBox();
        cbFileLong = new javax.swing.JCheckBox();
        cbFileHeight = new javax.swing.JCheckBox();
        cbFileSpeed = new javax.swing.JCheckBox();
        cbFileHeading = new javax.swing.JCheckBox();
        cbFileDistance = new javax.swing.JCheckBox();
        jPanel20 = new javax.swing.JPanel();
        pnFilePrecision = new javax.swing.JPanel();
        cbFileDSTA = new javax.swing.JCheckBox();
        cbFileDAGE = new javax.swing.JCheckBox();
        cbFilePDOP = new javax.swing.JCheckBox();
        cbFileHDOP = new javax.swing.JCheckBox();
        cbFileVDOP = new javax.swing.JCheckBox();
        cbFileFixType = new javax.swing.JCheckBox();
        pnFileSatInfo = new javax.swing.JPanel();
        cbFileNSAT = new javax.swing.JCheckBox();
        cbFileSID = new javax.swing.JCheckBox();
        cbFileElevation = new javax.swing.JCheckBox();
        cbFileAzimuth = new javax.swing.JCheckBox();
        cbFileSNR = new javax.swing.JCheckBox();
        LogFiltersPanel = new javax.swing.JPanel();
        pnTrackpoint = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        cbTrkNoFix = new javax.swing.JCheckBox();
        cbTrkPPS = new javax.swing.JCheckBox();
        cbTrkEstimate = new javax.swing.JCheckBox();
        cbTrkManual = new javax.swing.JCheckBox();
        cbTrkSPS = new javax.swing.JCheckBox();
        cbTrkFRTK = new javax.swing.JCheckBox();
        cbTrkDGPS = new javax.swing.JCheckBox();
        cbTrkSimulate = new javax.swing.JCheckBox();
        cbTrkRTK = new javax.swing.JCheckBox();
        jPanel12 = new javax.swing.JPanel();
        cbTrkTime = new javax.swing.JCheckBox();
        cbTrkSpeed = new javax.swing.JCheckBox();
        cbTrkDistance = new javax.swing.JCheckBox();
        cbTrkButton = new javax.swing.JCheckBox();
        cbTrkUser1 = new javax.swing.JCheckBox();
        cbTrkUser2 = new javax.swing.JCheckBox();
        cbTrkUser3 = new javax.swing.JCheckBox();
        cbTrkUser4 = new javax.swing.JCheckBox();
        cbTrkUser5 = new javax.swing.JCheckBox();
        cbTrkUser6 = new javax.swing.JCheckBox();
        cbTrkUser7 = new javax.swing.JCheckBox();
        cbTrkUser8 = new javax.swing.JCheckBox();
        cbTrkUser9 = new javax.swing.JCheckBox();
        cbTrkUser10 = new javax.swing.JCheckBox();
        cbTrkUser11 = new javax.swing.JCheckBox();
        cbTrkUser12 = new javax.swing.JCheckBox();
        pnCommonFilter = new javax.swing.JPanel();
        pnFilterOther = new javax.swing.JPanel();
        txtRecCntMin = new javax.swing.JTextField();
        txtDistanceMin = new javax.swing.JTextField();
        txtSpeedMin = new javax.swing.JTextField();
        lbDistanceFltr = new javax.swing.JLabel();
        lbSpeedFltr = new javax.swing.JLabel();
        txtRecCntMax = new javax.swing.JTextField();
        txtDistanceMax = new javax.swing.JTextField();
        txtSpeedMax = new javax.swing.JTextField();
        lbNSATFltr = new javax.swing.JLabel();
        txtNSATMin = new javax.swing.JTextField();
        lbRecNbrFltr = new javax.swing.JLabel();
        pnFilterPrecision = new javax.swing.JPanel();
        txtPDOPMax = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtHDOPMax = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtVDOPMax = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cbAdvancedActive = new javax.swing.JCheckBox();
        pnWaypoint = new javax.swing.JPanel();
        pnWayPointFix = new javax.swing.JPanel();
        cbWayNoFix = new javax.swing.JCheckBox();
        cbWayPPS = new javax.swing.JCheckBox();
        cbWayEstimate = new javax.swing.JCheckBox();
        cbWayManual = new javax.swing.JCheckBox();
        cbWaySPS = new javax.swing.JCheckBox();
        cbWayFRTK = new javax.swing.JCheckBox();
        cbWayDGPS = new javax.swing.JCheckBox();
        cbWaySimulate = new javax.swing.JCheckBox();
        cbWayRTK = new javax.swing.JCheckBox();
        pnWayPointRCR = new javax.swing.JPanel();
        cbWayTime = new javax.swing.JCheckBox();
        cbWaySpeed = new javax.swing.JCheckBox();
        cbWayDistance = new javax.swing.JCheckBox();
        cbWayButton = new javax.swing.JCheckBox();
        cbWayUser1 = new javax.swing.JCheckBox();
        cbWayUser2 = new javax.swing.JCheckBox();
        cbWayUser3 = new javax.swing.JCheckBox();
        cbWayUser4 = new javax.swing.JCheckBox();
        cbWayUser5 = new javax.swing.JCheckBox();
        cbWayUser6 = new javax.swing.JCheckBox();
        cbWayUser7 = new javax.swing.JCheckBox();
        cbWayUser8 = new javax.swing.JCheckBox();
        cbWayUser9 = new javax.swing.JCheckBox();
        cbWayUser10 = new javax.swing.JCheckBox();
        cbWayUser11 = new javax.swing.JCheckBox();
        cbWayUser12 = new javax.swing.JCheckBox();
        DeviceSettingsPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        pnGPSStart = new javax.swing.JPanel();
        btHotStart = new javax.swing.JButton();
        btWarmStart = new javax.swing.JButton();
        btColdStart = new javax.swing.JButton();
        btFactoryResetDevice = new javax.swing.JButton();
        pnSBAS = new javax.swing.JPanel();
        cbDGPSType = new javax.swing.JComboBox();
        cbUseSBAS = new javax.swing.JCheckBox();
        cbIncludeTestSBAS = new javax.swing.JCheckBox();
        btApplySBAS = new javax.swing.JButton();
        pnHoluxSettings = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        txtHoluxName = new javax.swing.JTextField();
        btSetHoluxName = new javax.swing.JButton();
        pnLogBy = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        ckLogSpeedActive = new javax.swing.JCheckBox();
        txtLogDistanceInterval = new javax.swing.JTextField();
        txtLogTimeInterval = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        ckLogTimeActive = new javax.swing.JCheckBox();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        ckLogDistanceActive = new javax.swing.JCheckBox();
        txtLogSpeedInterval = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        btLogByApply = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        cbStopOrOverwriteWhenFull = new javax.swing.JComboBox();
        pnLogFormat = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btFormatAndErase = new javax.swing.JButton();
        btErase = new javax.swing.JButton();
        btRecoverMemory = new javax.swing.JButton();
        btFormat = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        pnTime = new javax.swing.JPanel();
        cbUTCTime = new javax.swing.JCheckBox();
        cbMilliSeconds = new javax.swing.JCheckBox();
        pnPosition = new javax.swing.JPanel();
        cbLat = new javax.swing.JCheckBox();
        cbLong = new javax.swing.JCheckBox();
        cbHeight = new javax.swing.JCheckBox();
        cbSpeed = new javax.swing.JCheckBox();
        cbHeading = new javax.swing.JCheckBox();
        cbDistance = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        pnPrecision = new javax.swing.JPanel();
        cbDSTA = new javax.swing.JCheckBox();
        cbDAGE = new javax.swing.JCheckBox();
        cbPDOP = new javax.swing.JCheckBox();
        cbHDOP = new javax.swing.JCheckBox();
        cbVDOP = new javax.swing.JCheckBox();
        cbFixType = new javax.swing.JCheckBox();
        pnSatInfo = new javax.swing.JPanel();
        cbNSAT = new javax.swing.JCheckBox();
        cbSID = new javax.swing.JCheckBox();
        cbElevation = new javax.swing.JCheckBox();
        cbAzimuth = new javax.swing.JCheckBox();
        cbSNR = new javax.swing.JCheckBox();
        txtEstimatedRecords = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        cbOtherFormat = new javax.swing.JPanel();
        cbValidFixOnly = new javax.swing.JCheckBox();
        pnReason = new javax.swing.JPanel();
        cbRCR = new javax.swing.JCheckBox();
        AdvancedSettingsPanel = new javax.swing.JPanel();
        pnFlashSettings = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lbGLLOut = new javax.swing.JLabel();
        lbRMCOut = new javax.swing.JLabel();
        lbVTGOut = new javax.swing.JLabel();
        cbGSVOut = new javax.swing.JLabel();
        cbGSAOut = new javax.swing.JLabel();
        cbGGAOut = new javax.swing.JLabel();
        cbFlashGLL = new javax.swing.JComboBox();
        cbFlashRMC = new javax.swing.JComboBox();
        cbFlashVTG = new javax.swing.JComboBox();
        cbFlashGGA = new javax.swing.JComboBox();
        cbFlashGSA = new javax.swing.JComboBox();
        cbFlashGSV = new javax.swing.JComboBox();
        txtFlashTimesLeft = new javax.swing.JTextField();
        txtFlashUpdateRate = new javax.swing.JTextField();
        txtFlashBaudRate = new javax.swing.JTextField();
        lbFlashZDA = new javax.swing.JLabel();
        cbFlashZDA = new javax.swing.JComboBox();
        cbFlashMCHN = new javax.swing.JComboBox();
        btSetFlash = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        pnNMEAOutput = new javax.swing.JPanel();
        cbNMEAOutType10 = new javax.swing.JComboBox();
        lbNMEAOutType10 = new javax.swing.JLabel();
        cbNMEAOutType11 = new javax.swing.JComboBox();
        cbNMEAOutType12 = new javax.swing.JComboBox();
        cbNMEAOutMALM = new javax.swing.JComboBox();
        cbNMEAOutMDGP = new javax.swing.JComboBox();
        cbNMEAOutMEPH = new javax.swing.JComboBox();
        cbNMEAOutMDBG = new javax.swing.JComboBox();
        cbNMEAOutZDA = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lbNMEAOutZDA = new javax.swing.JLabel();
        btSetNMEAOutput = new javax.swing.JButton();
        btSetNMEAOutputDefaults = new javax.swing.JButton();
        lbGLLOut1 = new javax.swing.JLabel();
        cbNMEAOutGLL = new javax.swing.JComboBox();
        cbNMEAOutRMC = new javax.swing.JComboBox();
        lbRMCOut1 = new javax.swing.JLabel();
        cbVTGOut1 = new javax.swing.JLabel();
        cbNMEAOutVTG = new javax.swing.JComboBox();
        cbNMEAOutGGA = new javax.swing.JComboBox();
        cbGGAOut1 = new javax.swing.JLabel();
        cbGSAOut1 = new javax.swing.JLabel();
        cbNMEAOutGSA = new javax.swing.JComboBox();
        cbNMEAOutGSV = new javax.swing.JComboBox();
        cbGSVOut1 = new javax.swing.JLabel();
        cbGRSOut = new javax.swing.JLabel();
        cbGSTOut = new javax.swing.JLabel();
        cbType9Out = new javax.swing.JLabel();
        cbType8Out = new javax.swing.JLabel();
        cbNMEAOutType9 = new javax.swing.JComboBox();
        cbNMEAOutType8 = new javax.swing.JComboBox();
        cbNMEAOutGST = new javax.swing.JComboBox();
        cbNMEAOutGRS = new javax.swing.JComboBox();
        cbNMEAOutMCHN = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        AdvancedfileSettingsPanel = new javax.swing.JPanel();
        pnFileNMEAOutput = new javax.swing.JPanel();
        pnFileNMEAOutLeft = new javax.swing.JPanel();
        lbNMEAFileType9 = new javax.swing.JCheckBox();
        lbNMEAFileGST = new javax.swing.JCheckBox();
        lbNMEAFileVTG = new javax.swing.JCheckBox();
        lbNMEAFileGRS = new javax.swing.JCheckBox();
        lbNMEAFileGSV = new javax.swing.JCheckBox();
        lbNMEAFileGGA = new javax.swing.JCheckBox();
        lbNMEAFileRMC = new javax.swing.JCheckBox();
        lbNMEAFileGLL = new javax.swing.JCheckBox();
        lbNMEAFileType8 = new javax.swing.JCheckBox();
        lbNMEAFileGSA = new javax.swing.JCheckBox();
        pnFileNMEAOutRight = new javax.swing.JPanel();
        lbNMEAFileMDGP = new javax.swing.JCheckBox();
        lbNMEAFileType11 = new javax.swing.JCheckBox();
        lbNMEAFileMEPH = new javax.swing.JCheckBox();
        lbNMEAFileMALM = new javax.swing.JCheckBox();
        lbNMEAFileType10 = new javax.swing.JCheckBox();
        lbNMEAFileZDA = new javax.swing.JCheckBox();
        lbNMEAFileType12 = new javax.swing.JCheckBox();
        btSetNMEAFileOutput = new javax.swing.JButton();
        lbNMEAFileMDBG = new javax.swing.JCheckBox();
        lbNMEAFileMCHN = new javax.swing.JCheckBox();
        pnGPXFileSettings = new javax.swing.JPanel();
        cbNotApplyUTCOffset = new javax.swing.JCheckBox();
        cbGPXTrkSegWhenSmall = new javax.swing.JCheckBox();
        InfoPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jMenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        SettingsMenu = new javax.swing.JMenu();
        btGPSDebug = new javax.swing.JRadioButtonMenuItem();
        btGPSConnectDebug = new javax.swing.JRadioButtonMenuItem();
        InfoMenu = new javax.swing.JMenu();
        AboutBT747 = new javax.swing.JMenuItem();
        Info = new javax.swing.JMenuItem();

        org.jdesktop.layout.GroupLayout jDialog1Layout = new org.jdesktop.layout.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        setTitle(bundle.getString("BT747Main.title")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(appIcon);
        setName("BT747Frame"); // NOI18N

        DownloadProgressBar.setBackground(javax.swing.UIManager.getDefaults().getColor("nbProgressBar.Foreground"));
        DownloadProgressBar.setForeground(new java.awt.Color(204, 255, 204));
        DownloadProgressBar.setToolTipText(bundle.getString("BT747Main.DownloadProgressBar.toolTipText")); // NOI18N
        DownloadProgressBar.setFocusable(false);

        DownloadProgressLabel.setText(bundle.getString("BT747Main.DownloadProgressLabel.text")); // NOI18N

        cbPortName.setEditable(true);
        cbPortName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "USB (for Linux, Mac)", "BLUETOOTH (for Mac)", "COM1:", "COM2:", "COM3:", "COM4:", "COM5:", "COM6:", "COM7:", "COM8:", "COM9:", "COM10:", "COM11:", "COM12:", "COM13:", "COM14:", "COM15:", "COM16:" }));
        cbPortName.setToolTipText(bundle.getString("BT747Main.cbPortName.toolTipText")); // NOI18N
        cbPortName.setPreferredSize(new java.awt.Dimension(200, 22));
        cbPortName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPortNameActionPerformed(evt);
            }
        });

        btConnect.setText(bundle.getString("BT747Main.btConnect.text")); // NOI18N
        btConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConnectActionPerformed(evt);
            }
        });

        cbSerialSpeed.setEditable(true);
        cbSerialSpeed.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "38400", "115200" }));
        cbSerialSpeed.setToolTipText(bundle.getString("BT747Main.cbSerialSpeed.toolTipText")); // NOI18N
        cbSerialSpeed.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbSerialSpeedFocusLost(evt);
            }
        });

        lbSerialSpeed.setText(bundle.getString("BT747Main.lbSerialSpeed.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnBottomInformationLayout = new org.jdesktop.layout.GroupLayout(pnBottomInformation);
        pnBottomInformation.setLayout(pnBottomInformationLayout);
        pnBottomInformationLayout.setHorizontalGroup(
            pnBottomInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnBottomInformationLayout.createSequentialGroup()
                .add(btConnect)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbPortName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lbSerialSpeed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSerialSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(DownloadProgressLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DownloadProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnBottomInformationLayout.setVerticalGroup(
            pnBottomInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnBottomInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(btConnect)
                .add(cbPortName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(cbSerialSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lbSerialSpeed)
                .add(DownloadProgressLabel))
            .add(DownloadProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        DownloadProgressBar.getAccessibleContext().setAccessibleName(bundle.getString("DownloadProgessBar")); // NOI18N
        progressBarUpdate();

        pnFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFiles.border.title"))); // NOI18N

        tfWorkDirectory.setText(bundle.getString("BT747Main.tfWorkDirectory.text")); // NOI18N
        tfWorkDirectory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfWorkDirectoryFocusLost(evt);
            }
        });

        tfRawLogFilePath.setText(bundle.getString("BT747Main.tfRawLogFilePath.text")); // NOI18N
        tfRawLogFilePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfRawLogFilePathFocusLost(evt);
            }
        });

        tfOutputFileBaseName.setText(bundle.getString("BT747Main.tfOutputFileBaseName.text")); // NOI18N
        tfOutputFileBaseName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfOutputFileBaseNameFocusLost(evt);
            }
        });

        btWorkingDirectory.setText(bundle.getString("BT747Main.btWorkingDirectory.text")); // NOI18N
        btWorkingDirectory.setToolTipText(bundle.getString("BT747Main.btWorkingDirectory.toolTipText")); // NOI18N
        btWorkingDirectory.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btWorkingDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btWorkingDirectoryActionPerformed(evt);
            }
        });

        btRawLogFile.setText(bundle.getString("BT747Main.btRawLogFile.text")); // NOI18N
        btRawLogFile.setToolTipText(bundle.getString("BT747Main.btRawLogFile.toolTipText")); // NOI18N
        btRawLogFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btRawLogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRawLogFileActionPerformed(evt);
            }
        });

        btOutputFile.setText(bundle.getString("BT747Main.btOutputFile.text")); // NOI18N
        btOutputFile.setToolTipText(bundle.getString("BT747Main.btOutputFile.toolTipText")); // NOI18N
        btOutputFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btOutputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOutputFileActionPerformed(evt);
            }
        });

        jLabel15.setText(bundle.getString("BT747Main.jLabel15.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFilesLayout = new org.jdesktop.layout.GroupLayout(pnFiles);
        pnFiles.setLayout(pnFilesLayout);
        pnFilesLayout.setHorizontalGroup(
            pnFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btRawLogFile)
                    .add(btWorkingDirectory)
                    .add(btOutputFile))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, tfRawLogFilePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                    .add(pnFilesLayout.createSequentialGroup()
                        .add(tfOutputFileBaseName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel15))
                    .add(tfWorkDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnFilesLayout.setVerticalGroup(
            pnFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilesLayout.createSequentialGroup()
                .add(pnFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tfRawLogFilePath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btRawLogFile))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btWorkingDirectory)
                    .add(tfWorkDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(btOutputFile)
                    .add(tfOutputFileBaseName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnDownload.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnDownload.border.title"))); // NOI18N

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.jPanel17.border.title"))); // NOI18N

        cbIncremental.setText(bundle.getString("BT747Main.cbIncremental.text")); // NOI18N
        cbIncremental.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIncrementalActionPerformed(evt);
            }
        });

        cbDisableLoggingDuringDownload.setText(bundle.getString("BT747Main.cbDisableLoggingDuringDownload.text")); // NOI18N
        cbDisableLoggingDuringDownload.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbDisableLoggingDuringDownloadFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel17Layout = new org.jdesktop.layout.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbIncremental)
                    .add(cbDisableLoggingDuringDownload)))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .add(cbIncremental)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbDisableLoggingDuringDownload))
        );

        btDownloadIBlue.setText(bundle.getString("BT747Main.btDownloadIBlue.text")); // NOI18N
        btDownloadIBlue.setToolTipText(bundle.getString("BT747Main.btDownloadIBlue.toolTipText")); // NOI18N
        btDownloadIBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDownloadIBlueActionPerformed(evt);
            }
        });

        btDownloadFromNumerix.setText(bundle.getString("BT747Main.btDownloadFromNumerix.text")); // NOI18N
        btDownloadFromNumerix.setToolTipText(bundle.getString("BT747Main.btDownloadFromNumerix.toolTipText")); // NOI18N
        btDownloadFromNumerix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDownloadFromNumerixActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnDownloadLayout = new org.jdesktop.layout.GroupLayout(pnDownload);
        pnDownload.setLayout(pnDownloadLayout);
        pnDownloadLayout.setHorizontalGroup(
            pnDownloadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDownloadLayout.createSequentialGroup()
                .add(jPanel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnDownloadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btDownloadIBlue)
                    .add(btDownloadFromNumerix)))
        );
        pnDownloadLayout.setVerticalGroup(
            pnDownloadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnDownloadLayout.createSequentialGroup()
                .addContainerGap()
                .add(btDownloadIBlue)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btDownloadFromNumerix))
        );

        pnConvert.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnConvert.border.title"))); // NOI18N

        btConvert.setText(bundle.getString("BT747Main.btConvert.text")); // NOI18N
        btConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConvertActionPerformed(evt);
            }
        });

        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GPX", "CSV", "CompeGPS (.TRK,.WPT)", "KML", "OziExplorer (.PLT)", "NMEA", "Google Map (.html)" }));
        cbFormat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFormatItemStateChanged(evt);
            }
        });

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.jPanel18.border.title"))); // NOI18N

        jLabel44.setText(bundle.getString("BT747Main.jLabel44.text")); // NOI18N

        jLabel43.setText(bundle.getString("BT747Main.jLabel43.text")); // NOI18N

        sfTimeSplitHours.setMaximum(23);
        sfTimeSplitHours.setMinimum(0);
        sfTimeSplitHours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                sfTimeSplitHoursFocusLost(evt);
            }
        });

        jLabel5.setText(bundle.getString("BT747Main.jLabel5.text")); // NOI18N

        spTimeSplitMinutes.setMaximum(59);
        spTimeSplitMinutes.setMinimum(0);
        spTimeSplitMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spTimeSplitMinutesFocusLost(evt);
            }
        });

        jLabel8.setText(bundle.getString("BT747Main.jLabel8.text")); // NOI18N
        jLabel8.setToolTipText(bundle.getString("BT747Main.jLabel8.toolTipText")); // NOI18N

        jLabel11.setText(bundle.getString("BT747Main.jLabel11.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel18Layout = new org.jdesktop.layout.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel18Layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jLabel43)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel44)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sfTimeSplitHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTimeSplitMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel11))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jLabel44)
            .add(jLabel43)
            .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(sfTimeSplitHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jLabel8)
            .add(jLabel5)
            .add(spTimeSplitMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jLabel11)
        );

        cbGPSType.setModel(modelGpsType);
        cbGPSType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbGPSTypeFocusLost(evt);
            }
        });

        jLabel57.setText(bundle.getString("BT747Main.jLabel57.text")); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.jPanel9.border.title"))); // NOI18N

        cbDecoderChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Original", "Thomas's version" }));
        cbDecoderChoice.setToolTipText(bundle.getString("BT747Main.cbDecoderChoice.toolTipText")); // NOI18N
        cbDecoderChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDecoderChoiceActionPerformed(evt);
            }
        });

        lbConversionTime.setText(bundle.getString("BT747Main.lbConversionTime.text")); // NOI18N
        lbConversionTime.setToolTipText(bundle.getString("BT747Main.lbConversionTime.toolTipText")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(cbDecoderChoice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbConversionTime)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(cbDecoderChoice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lbConversionTime))
        );

        org.jdesktop.layout.GroupLayout pnConvertLayout = new org.jdesktop.layout.GroupLayout(pnConvert);
        pnConvert.setLayout(pnConvertLayout);
        pnConvertLayout.setHorizontalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvertLayout.createSequentialGroup()
                .add(pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnConvertLayout.createSequentialGroup()
                        .add(jPanel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnConvertLayout.createSequentialGroup()
                        .add(btConvert)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel57)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 0, 0))
        );
        pnConvertLayout.setVerticalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvertLayout.createSequentialGroup()
                .add(pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel9, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btConvert)
                    .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel57)
                    .add(cbGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        setSelectedFormat(cbFormat.getSelectedItem().toString());

        GPSDecodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.GPSDecodePanel.border.title"))); // NOI18N

        jLabel1.setText(bundle.getString("BT747Main.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("BT747Main.jLabel2.text")); // NOI18N

        jLabel3.setText(bundle.getString("BT747Main.jLabel3.text")); // NOI18N

        lbLatitude.setText(bundle.getString("BT747Main.lbLatitude.text")); // NOI18N

        lbLongitude.setText(bundle.getString("BT747Main.lbLongitude.text")); // NOI18N

        lbTime.setText(bundle.getString("BT747Main.lbTime.text")); // NOI18N

        jLabel4.setText(bundle.getString("BT747Main.jLabel4.text")); // NOI18N

        lbGeoid.setText(bundle.getString("BT747Main.lbGeoid.text")); // NOI18N

        jLabel6.setText(bundle.getString("BT747Main.jLabel6.text")); // NOI18N

        lbFlashInfo.setText(bundle.getString("BT747Main.lbFlashInfo.text")); // NOI18N
        lbFlashInfo.setToolTipText(bundle.getString("BT747Main.lbFlashInfo.toolTipText")); // NOI18N

        jLabel10.setText(bundle.getString("BT747Main.jLabel10.text")); // NOI18N

        lbModel.setText(bundle.getString("BT747Main.lbModel.text")); // NOI18N

        jLabel14.setText(bundle.getString("BT747Main.jLabel14.text")); // NOI18N

        lbFirmWare.setText(bundle.getString("BT747Main.lbFirmWare.text")); // NOI18N

        jLabel16.setText(bundle.getString("BT747Main.jLabel16.text")); // NOI18N

        lbMemoryUsed.setText(bundle.getString("BT747Main.lbMemoryUsed.text")); // NOI18N

        lbLoggerSWVersion.setText(bundle.getString("BT747Main.lbLoggerSWVersion.text")); // NOI18N
        lbLoggerSWVersion.setToolTipText(bundle.getString("BT747Main.lbLoggerSWVersion.toolTipText")); // NOI18N

        txtMemoryUsed.setText(bundle.getString("BT747Main.txtMemoryUsed.text")); // NOI18N

        org.jdesktop.layout.GroupLayout GPSDecodePanelLayout = new org.jdesktop.layout.GroupLayout(GPSDecodePanel);
        GPSDecodePanel.setLayout(GPSDecodePanelLayout);
        GPSDecodePanelLayout.setHorizontalGroup(
            GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GPSDecodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(jLabel10)
                    .add(jLabel14)
                    .add(lbMemoryUsed)
                    .add(jLabel16)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtMemoryUsed)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbLoggerSWVersion)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbFirmWare)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbModel)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbFlashInfo)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbGeoid)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbTime)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbLongitude)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lbLatitude))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        GPSDecodePanelLayout.setVerticalGroup(
            GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GPSDecodePanelLayout.createSequentialGroup()
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(lbLatitude))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbLongitude)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbTime)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbGeoid)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(lbFlashInfo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbModel)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(lbFirmWare))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(lbLoggerSWVersion))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbMemoryUsed)
                    .add(txtMemoryUsed))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout LogOperationsPanelLayout = new org.jdesktop.layout.GroupLayout(LogOperationsPanel);
        LogOperationsPanel.setLayout(LogOperationsPanelLayout);
        LogOperationsPanelLayout.setHorizontalGroup(
            LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(LogOperationsPanelLayout.createSequentialGroup()
                .add(LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnFiles, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnDownload, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, 0)
                .add(GPSDecodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pnConvert, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        LogOperationsPanelLayout.setVerticalGroup(
            LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(LogOperationsPanelLayout.createSequentialGroup()
                .add(LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(LogOperationsPanelLayout.createSequentialGroup()
                        .add(pnFiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnDownload, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(GPSDecodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnConvert, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.LogOperationsPanel.TabConstraints.tabTitle"), LogOperationsPanel); // NOI18N

        pnVarious.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnVarious.border.title"))); // NOI18N

        cbUTCOffset.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "UTC -12", "UTC -11", "UTC -10", "UTC -9", "UTC -8", "UTC -7", "UTC -6", "UTC -5", "UTC -4", "UTC -3", "UTC -2", "UTC -1", "UTC +0", "UTC +1", "UTC +2", "UTC +3", "UTC +4", "UTC +5", "UTC +6", "UTC +7", "UTC +8", "UTC +9", "UTC +10", "UTC +11", "UTC +12" }));
        cbUTCOffset.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbUTCOffsetFocusLost(evt);
            }
        });

        cbHeightOverMeanSeaLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Keep WGS84 height", "MSL height" }));
        cbHeightOverMeanSeaLevel.setToolTipText(bundle.getString("BT747Main.cbHeightOverMeanSeaLevel.toolTipText")); // NOI18N
        cbHeightOverMeanSeaLevel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbHeightOverMeanSeaLevelFocusLost(evt);
            }
        });

        cbRecordNumberInfoInLog.setText(bundle.getString("BT747Main.cbRecordNumberInfoInLog.text")); // NOI18N
        cbRecordNumberInfoInLog.setToolTipText(bundle.getString("BT747Main.cbRecordNumberInfoInLog.toolTipText")); // NOI18N
        cbRecordNumberInfoInLog.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbRecordNumberInfoInLogFocusLost(evt);
            }
        });

        cbImperialUnits.setText(bundle.getString("BT747Main.cbImperialUnits.text")); // NOI18N
        cbImperialUnits.setToolTipText(bundle.getString("BT747Main.cbImperialUnits.toolTipText")); // NOI18N
        cbImperialUnits.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbImperialUnitsFocusLost(evt);
            }
        });

        lbTimeZone.setText(bundle.getString("BT747Main.lbTimeZone.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lbTimeZone)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbUTCOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cbHeightOverMeanSeaLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbRecordNumberInfoInLog))
            .add(cbImperialUnits)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbUTCOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbTimeZone))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbHeightOverMeanSeaLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbRecordNumberInfoInLog)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbImperialUnits))
        );

        cbStandardOrDaylightSaving.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard Time", "Daylight Savings Time" }));
        cbStandardOrDaylightSaving.setEnabled(false);
        cbStandardOrDaylightSaving.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbStandardOrDaylightSavingFocusLost(evt);
            }
        });

        cbOneFilePerDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "One file per day", "One file per track", "Everything in one file" }));
        cbOneFilePerDay.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbOneFilePerDayFocusLost(evt);
            }
        });

        cbGoodFixColor.setText(bundle.getString("BT747Main.cbGoodFixColor.text")); // NOI18N
        cbGoodFixColor.setToolTipText(bundle.getString("BT747Main.cbGoodFixColor.toolTipText")); // NOI18N
        cbGoodFixColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGoodFixColorActionPerformed(evt);
            }
        });

        cbNoFixColor.setText(bundle.getString("BT747Main.cbNoFixColor.text")); // NOI18N
        cbNoFixColor.setToolTipText(bundle.getString("BT747Main.cbNoFixColor.toolTipText")); // NOI18N
        cbNoFixColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbNoFixColorActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNoFixColor)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbStandardOrDaylightSaving, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbGoodFixColor)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbOneFilePerDay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(cbStandardOrDaylightSaving, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbOneFilePerDay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGoodFixColor)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbNoFixColor))
        );

        org.jdesktop.layout.GroupLayout pnVariousLayout = new org.jdesktop.layout.GroupLayout(pnVarious);
        pnVarious.setLayout(pnVariousLayout);
        pnVariousLayout.setHorizontalGroup(
            pnVariousLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnVariousLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pnVariousLayout.setVerticalGroup(
            pnVariousLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pnSeparation.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnSeparation.border.title"))); // NOI18N
        pnSeparation.setMaximumSize(new java.awt.Dimension(300, 32767));
        pnSeparation.setOpaque(false);

        lbNewTrackAfter.setText(bundle.getString("BT747Main.lbNewTrackAfter.text")); // NOI18N

        tfTrackSeparationTime.setText(bundle.getString("BT747Main.tfTrackSeparationTime.text")); // NOI18N
        tfTrackSeparationTime.setToolTipText(bundle.getString("BT747Main.tfTrackSeparationTime.toolTipText")); // NOI18N
        tfTrackSeparationTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTrackSeparationTimeFocusLost(evt);
            }
        });

        jLabel48.setText(bundle.getString("BT747Main.jLabel48.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnSeparationLayout = new org.jdesktop.layout.GroupLayout(pnSeparation);
        pnSeparation.setLayout(pnSeparationLayout);
        pnSeparationLayout.setHorizontalGroup(
            pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSeparationLayout.createSequentialGroup()
                .add(lbNewTrackAfter)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfTrackSeparationTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel48)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnSeparationLayout.setVerticalGroup(
            pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(lbNewTrackAfter)
                .add(tfTrackSeparationTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel48))
        );

        pnFileOutputFields.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFileOutputFields.border.title"))); // NOI18N
        pnFileOutputFields.setToolTipText(bundle.getString("BT747Main.pnFileOutputFields.toolTipText")); // NOI18N

        pnFileReason.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFileReason.border.title"))); // NOI18N

        cbFileRCR.setText(bundle.getString("BT747Main.cbFileRCR.text")); // NOI18N
        cbFileRCR.setToolTipText(bundle.getString("BT747Main.cbFileRCR.toolTipText")); // NOI18N
        cbFileRCR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileRCRupdateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnFileReasonLayout = new org.jdesktop.layout.GroupLayout(pnFileReason);
        pnFileReason.setLayout(pnFileReasonLayout);
        pnFileReasonLayout.setHorizontalGroup(
            pnFileReasonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileReasonLayout.createSequentialGroup()
                .add(cbFileRCR)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnFileReasonLayout.setVerticalGroup(
            pnFileReasonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbFileRCR)
        );

        pnFileTime.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFileTime.border.title"))); // NOI18N

        cbFileUTCTime.setText(bundle.getString("BT747Main.cbFileUTCTime.text")); // NOI18N
        cbFileUTCTime.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileUTCTimeupdateLogRecordEstCount(evt);
            }
        });

        cbFileMilliSeconds.setText(bundle.getString("BT747Main.cbFileMilliSeconds.text")); // NOI18N
        cbFileMilliSeconds.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileMilliSecondsupdateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnFileTimeLayout = new org.jdesktop.layout.GroupLayout(pnFileTime);
        pnFileTime.setLayout(pnFileTimeLayout);
        pnFileTimeLayout.setHorizontalGroup(
            pnFileTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileTimeLayout.createSequentialGroup()
                .add(pnFileTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbFileUTCTime)
                    .add(cbFileMilliSeconds))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnFileTimeLayout.setVerticalGroup(
            pnFileTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileTimeLayout.createSequentialGroup()
                .add(cbFileUTCTime)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileMilliSeconds)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnFilePosition.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFilePosition.border.title"))); // NOI18N

        cbFileLat.setText(bundle.getString("BT747Main.cbFileLat.text")); // NOI18N
        cbFileLat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileLatupdateLogRecordEstCount(evt);
            }
        });

        cbFileLong.setText(bundle.getString("BT747Main.cbFileLong.text")); // NOI18N
        cbFileLong.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileLongupdateLogRecordEstCount(evt);
            }
        });

        cbFileHeight.setText(bundle.getString("BT747Main.cbFileHeight.text")); // NOI18N
        cbFileHeight.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileHeightupdateLogRecordEstCount(evt);
            }
        });

        cbFileSpeed.setText(bundle.getString("BT747Main.cbFileSpeed.text")); // NOI18N
        cbFileSpeed.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileSpeedupdateLogRecordEstCount(evt);
            }
        });

        cbFileHeading.setText(bundle.getString("BT747Main.cbFileHeading.text")); // NOI18N
        cbFileHeading.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileHeadingupdateLogRecordEstCount(evt);
            }
        });

        cbFileDistance.setText(bundle.getString("BT747Main.cbFileDistance.text")); // NOI18N
        cbFileDistance.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileDistanceupdateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnFilePositionLayout = new org.jdesktop.layout.GroupLayout(pnFilePosition);
        pnFilePosition.setLayout(pnFilePositionLayout);
        pnFilePositionLayout.setHorizontalGroup(
            pnFilePositionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilePositionLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnFilePositionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbFileLat)
                    .add(cbFileHeight)
                    .add(cbFileLong)
                    .add(cbFileSpeed)
                    .add(cbFileHeading)
                    .add(cbFileDistance))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        pnFilePositionLayout.setVerticalGroup(
            pnFilePositionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilePositionLayout.createSequentialGroup()
                .add(cbFileLat)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileLong)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileHeight)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileSpeed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileHeading)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileDistance))
        );

        pnFilePrecision.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFilePrecision.border.title"))); // NOI18N

        cbFileDSTA.setText(bundle.getString("BT747Main.cbFileDSTA.text")); // NOI18N
        cbFileDSTA.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileDSTAupdateLogRecordEstCount(evt);
            }
        });

        cbFileDAGE.setText(bundle.getString("BT747Main.cbFileDAGE.text")); // NOI18N
        cbFileDAGE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileDAGEupdateLogRecordEstCount(evt);
            }
        });

        cbFilePDOP.setText(bundle.getString("BT747Main.cbFilePDOP.text")); // NOI18N
        cbFilePDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFilePDOPupdateLogRecordEstCount(evt);
            }
        });

        cbFileHDOP.setText(bundle.getString("BT747Main.cbFileHDOP.text")); // NOI18N
        cbFileHDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileHDOPupdateLogRecordEstCount(evt);
            }
        });

        cbFileVDOP.setText(bundle.getString("BT747Main.cbFileVDOP.text")); // NOI18N
        cbFileVDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileVDOPupdateLogRecordEstCount(evt);
            }
        });

        cbFileFixType.setText(bundle.getString("BT747Main.cbFileFixType.text")); // NOI18N
        cbFileFixType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileFixTypeupdateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnFilePrecisionLayout = new org.jdesktop.layout.GroupLayout(pnFilePrecision);
        pnFilePrecision.setLayout(pnFilePrecisionLayout);
        pnFilePrecisionLayout.setHorizontalGroup(
            pnFilePrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbFileFixType)
            .add(cbFileDSTA)
            .add(cbFileDAGE)
            .add(cbFilePDOP)
            .add(cbFileHDOP)
            .add(cbFileVDOP)
        );
        pnFilePrecisionLayout.setVerticalGroup(
            pnFilePrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilePrecisionLayout.createSequentialGroup()
                .add(cbFileFixType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileDSTA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileDAGE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFilePDOP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileHDOP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileVDOP))
        );

        pnFileSatInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFileSatInfo.border.title"))); // NOI18N

        cbFileNSAT.setText(bundle.getString("BT747Main.cbFileNSAT.text")); // NOI18N
        cbFileNSAT.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileNSATupdateLogRecordEstCount(evt);
            }
        });

        cbFileSID.setText(bundle.getString("BT747Main.cbFileSID.text")); // NOI18N
        cbFileSID.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileSIDItemStateChanged(evt);
            }
        });

        cbFileElevation.setText(bundle.getString("BT747Main.cbFileElevation.text")); // NOI18N
        cbFileElevation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileElevationupdateLogRecordEstCount(evt);
            }
        });

        cbFileAzimuth.setText(bundle.getString("BT747Main.cbFileAzimuth.text")); // NOI18N
        cbFileAzimuth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileAzimuthupdateLogRecordEstCount(evt);
            }
        });

        cbFileSNR.setText(bundle.getString("BT747Main.cbFileSNR.text")); // NOI18N
        cbFileSNR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFileSNRupdateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnFileSatInfoLayout = new org.jdesktop.layout.GroupLayout(pnFileSatInfo);
        pnFileSatInfo.setLayout(pnFileSatInfoLayout);
        pnFileSatInfoLayout.setHorizontalGroup(
            pnFileSatInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbFileNSAT)
            .add(cbFileSID)
            .add(cbFileElevation)
            .add(cbFileAzimuth)
            .add(cbFileSNR)
        );
        pnFileSatInfoLayout.setVerticalGroup(
            pnFileSatInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileSatInfoLayout.createSequentialGroup()
                .add(cbFileNSAT)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileSID)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileElevation)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileAzimuth)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFileSNR)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel20Layout = new org.jdesktop.layout.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel20Layout.createSequentialGroup()
                .add(pnFilePrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFileSatInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilePrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnFileSatInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout pnFileOutputFieldInnerLayout = new org.jdesktop.layout.GroupLayout(pnFileOutputFieldInner);
        pnFileOutputFieldInner.setLayout(pnFileOutputFieldInnerLayout);
        pnFileOutputFieldInnerLayout.setHorizontalGroup(
            pnFileOutputFieldInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                .add(pnFileOutputFieldInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnFileTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnFilePosition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFileOutputFieldInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFileReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        pnFileOutputFieldInnerLayout.setVerticalGroup(
            pnFileOutputFieldInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                .add(pnFileTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilePosition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                .add(jPanel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(pnFileReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout pnFileOutputFieldsLayout = new org.jdesktop.layout.GroupLayout(pnFileOutputFields);
        pnFileOutputFields.setLayout(pnFileOutputFieldsLayout);
        pnFileOutputFieldsLayout.setHorizontalGroup(
            pnFileOutputFieldsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileOutputFieldInner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        pnFileOutputFieldsLayout.setVerticalGroup(
            pnFileOutputFieldsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileOutputFieldsLayout.createSequentialGroup()
                .add(pnFileOutputFieldInner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0))
        );

        org.jdesktop.layout.GroupLayout FileSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(FileSettingsPanel);
        FileSettingsPanel.setLayout(FileSettingsPanelLayout);
        FileSettingsPanelLayout.setHorizontalGroup(
            FileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FileSettingsPanelLayout.createSequentialGroup()
                .add(FileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnSeparation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnVarious, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFileOutputFields, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(222, Short.MAX_VALUE))
        );
        FileSettingsPanelLayout.setVerticalGroup(
            FileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FileSettingsPanelLayout.createSequentialGroup()
                .add(pnVarious, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnSeparation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pnFileOutputFields, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.FileSettingsPanel.TabConstraints.tabTitle"), FileSettingsPanel); // NOI18N

        pnTrackpoint.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnTrackpoint.border.title"))); // NOI18N
        pnTrackpoint.setToolTipText(bundle.getString("BT747Main.pnTrackpoint.toolTipText")); // NOI18N

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.jPanel11.border.title"))); // NOI18N

        cbTrkNoFix.setText(bundle.getString("BT747Main.cbTrkNoFix.text")); // NOI18N
        cbTrkNoFix.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkPPS.setText(bundle.getString("BT747Main.cbTrkPPS.text")); // NOI18N
        cbTrkPPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkEstimate.setText(bundle.getString("BT747Main.cbTrkEstimate.text")); // NOI18N
        cbTrkEstimate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkManual.setText(bundle.getString("BT747Main.cbTrkManual.text")); // NOI18N
        cbTrkManual.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkSPS.setText(bundle.getString("BT747Main.cbTrkSPS.text")); // NOI18N
        cbTrkSPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkFRTK.setText(bundle.getString("BT747Main.cbTrkFRTK.text")); // NOI18N
        cbTrkFRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkDGPS.setText(bundle.getString("BT747Main.cbTrkDGPS.text")); // NOI18N
        cbTrkDGPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkSimulate.setText(bundle.getString("BT747Main.cbTrkSimulate.text")); // NOI18N
        cbTrkSimulate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkRTK.setText(bundle.getString("BT747Main.cbTrkRTK.text")); // NOI18N
        cbTrkRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbTrkNoFix)
                    .add(cbTrkSPS)
                    .add(cbTrkDGPS)
                    .add(cbTrkPPS)
                    .add(cbTrkRTK)
                    .add(cbTrkFRTK)
                    .add(cbTrkEstimate)
                    .add(cbTrkManual)
                    .add(cbTrkSimulate))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(cbTrkNoFix)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkSPS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkDGPS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkPPS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkRTK)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkFRTK)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkEstimate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkManual)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkSimulate)
                .add(30, 30, 30))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.jPanel12.border.title"))); // NOI18N

        cbTrkTime.setText(bundle.getString("BT747Main.cbTrkTime.text")); // NOI18N
        cbTrkTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkSpeed.setText(bundle.getString("BT747Main.cbTrkSpeed.text")); // NOI18N
        cbTrkSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkDistance.setText(bundle.getString("BT747Main.cbTrkDistance.text")); // NOI18N
        cbTrkDistance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkButton.setText(bundle.getString("BT747Main.cbTrkButton.text")); // NOI18N
        cbTrkButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser1.setText(bundle.getString("BT747Main.cbTrkUser1.text")); // NOI18N
        cbTrkUser1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser2.setText(bundle.getString("BT747Main.cbTrkUser2.text")); // NOI18N
        cbTrkUser2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser3.setText(bundle.getString("BT747Main.cbTrkUser3.text")); // NOI18N
        cbTrkUser3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser4.setText(bundle.getString("BT747Main.cbTrkUser4.text")); // NOI18N
        cbTrkUser4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser5.setText(bundle.getString("BT747Main.cbTrkUser5.text")); // NOI18N
        cbTrkUser5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser6.setText(bundle.getString("BT747Main.cbTrkUser6.text")); // NOI18N
        cbTrkUser6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser7.setText(bundle.getString("BT747Main.cbTrkUser7.text")); // NOI18N
        cbTrkUser7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser8.setText(bundle.getString("BT747Main.cbTrkUser8.text")); // NOI18N
        cbTrkUser8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser9.setText(bundle.getString("BT747Main.cbTrkUser9.text")); // NOI18N
        cbTrkUser9.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser10.setText(bundle.getString("BT747Main.cbTrkUser10.text")); // NOI18N
        cbTrkUser10.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser11.setText(bundle.getString("BT747Main.cbTrkUser11.text")); // NOI18N
        cbTrkUser11.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkUser12.setText(bundle.getString("BT747Main.cbTrkUser12.text")); // NOI18N
        cbTrkUser12.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TrkRCRAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbTrkTime)
                    .add(cbTrkSpeed)
                    .add(cbTrkDistance)
                    .add(cbTrkButton)
                    .add(jPanel12Layout.createSequentialGroup()
                        .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbTrkUser1)
                            .add(cbTrkUser2)
                            .add(cbTrkUser3)
                            .add(cbTrkUser4)
                            .add(cbTrkUser5)
                            .add(cbTrkUser6))
                        .add(18, 18, 18)
                        .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbTrkUser7)
                            .add(cbTrkUser8)
                            .add(cbTrkUser9)
                            .add(cbTrkUser10)
                            .add(cbTrkUser11)
                            .add(cbTrkUser12))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(cbTrkTime)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkSpeed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkDistance)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel12Layout.createSequentialGroup()
                        .add(cbTrkUser7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser12))
                    .add(jPanel12Layout.createSequentialGroup()
                        .add(cbTrkUser1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbTrkUser6)))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pnTrackpointLayout = new org.jdesktop.layout.GroupLayout(pnTrackpoint);
        pnTrackpoint.setLayout(pnTrackpointLayout);
        pnTrackpointLayout.setHorizontalGroup(
            pnTrackpointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrackpointLayout.createSequentialGroup()
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pnTrackpointLayout.setVerticalGroup(
            pnTrackpointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
            .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnCommonFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnCommonFilter.border.title"))); // NOI18N
        pnCommonFilter.setToolTipText(bundle.getString("BT747Main.pnCommonFilter.toolTipText")); // NOI18N

        pnFilterOther.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFilterOther.border.title"))); // NOI18N

        txtRecCntMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecCntMin.setText(bundle.getString("BT747Main.txtRecCntMin.text")); // NOI18N
        txtRecCntMin.setInputVerifier(IntVerifier);
        txtRecCntMin.setMinimumSize(new java.awt.Dimension(50, 40));
        txtRecCntMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRecCntMinFocusLost(evt);
            }
        });

        txtDistanceMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDistanceMin.setText(bundle.getString("BT747Main.txtDistanceMin.text")); // NOI18N
        txtDistanceMin.setInputVerifier(FloatVerifier);
        txtDistanceMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDistanceMinFocusLost(evt);
            }
        });

        txtSpeedMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSpeedMin.setText(bundle.getString("BT747Main.txtSpeedMin.text")); // NOI18N
        txtSpeedMin.setInputVerifier(FloatVerifier);
        txtSpeedMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSpeedMinFocusLost(evt);
            }
        });

        lbDistanceFltr.setText(bundle.getString("BT747Main.lbDistanceFltr.text")); // NOI18N

        lbSpeedFltr.setText(bundle.getString("BT747Main.lbSpeedFltr.text")); // NOI18N

        txtRecCntMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecCntMax.setText(bundle.getString("BT747Main.txtRecCntMax.text")); // NOI18N
        txtRecCntMax.setInputVerifier(IntVerifier);
        txtRecCntMax.setMinimumSize(new java.awt.Dimension(50, 40));
        txtRecCntMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRecCntMaxFocusLost(evt);
            }
        });

        txtDistanceMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDistanceMax.setText(bundle.getString("BT747Main.txtDistanceMax.text")); // NOI18N
        txtDistanceMax.setInputVerifier(FloatVerifier);
        txtDistanceMax.setMinimumSize(new java.awt.Dimension(6, 40));
        txtDistanceMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDistanceMaxFocusLost(evt);
            }
        });

        txtSpeedMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSpeedMax.setText(bundle.getString("BT747Main.txtSpeedMax.text")); // NOI18N
        txtSpeedMax.setInputVerifier(FloatVerifier);
        txtSpeedMax.setMinimumSize(new java.awt.Dimension(6, 40));
        txtSpeedMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSpeedMaxFocusLost(evt);
            }
        });

        lbNSATFltr.setText(bundle.getString("BT747Main.lbNSATFltr.text")); // NOI18N

        txtNSATMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNSATMin.setText(bundle.getString("BT747Main.txtNSATMin.text")); // NOI18N
        txtNSATMin.setInputVerifier(IntVerifier);
        txtNSATMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNSATMinFocusLost(evt);
            }
        });

        lbRecNbrFltr.setText(bundle.getString("BT747Main.lbRecNbrFltr.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFilterOtherLayout = new org.jdesktop.layout.GroupLayout(pnFilterOther);
        pnFilterOther.setLayout(pnFilterOtherLayout);
        pnFilterOtherLayout.setHorizontalGroup(
            pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilterOtherLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtNSATMin)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtDistanceMin)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtSpeedMin)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtRecCntMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(lbDistanceFltr)
                    .add(lbSpeedFltr)
                    .add(lbNSATFltr)
                    .add(lbRecNbrFltr))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(txtSpeedMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(txtDistanceMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(txtRecCntMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnFilterOtherLayout.setVerticalGroup(
            pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilterOtherLayout.createSequentialGroup()
                .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFilterOtherLayout.createSequentialGroup()
                        .add(txtRecCntMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtDistanceMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbDistanceFltr))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtSpeedMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbSpeedFltr)))
                    .add(pnFilterOtherLayout.createSequentialGroup()
                        .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lbRecNbrFltr)
                            .add(txtRecCntMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtDistanceMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtSpeedMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterOtherLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbNSATFltr, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtNSATMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        pnFilterPrecision.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFilterPrecision.border.title"))); // NOI18N

        txtPDOPMax.setText(bundle.getString("BT747Main.txtPDOPMax.text")); // NOI18N
        txtPDOPMax.setInputVerifier(FloatVerifier);
        txtPDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPDOPMaxFocusLost(evt);
            }
        });

        jLabel9.setLabelFor(txtPDOPMax);
        jLabel9.setText(bundle.getString("BT747Main.jLabel9.text")); // NOI18N

        txtHDOPMax.setText(bundle.getString("BT747Main.txtHDOPMax.text")); // NOI18N
        txtHDOPMax.setInputVerifier(FloatVerifier);
        txtHDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHDOPMaxFocusLost(evt);
            }
        });

        jLabel12.setLabelFor(txtHDOPMax);
        jLabel12.setText(bundle.getString("BT747Main.jLabel12.text")); // NOI18N

        txtVDOPMax.setText(bundle.getString("BT747Main.txtVDOPMax.text")); // NOI18N
        txtVDOPMax.setInputVerifier(FloatVerifier);
        txtVDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVDOPMaxFocusLost(evt);
            }
        });

        jLabel13.setLabelFor(txtVDOPMax);
        jLabel13.setText(bundle.getString("BT747Main.jLabel13.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFilterPrecisionLayout = new org.jdesktop.layout.GroupLayout(pnFilterPrecision);
        pnFilterPrecision.setLayout(pnFilterPrecisionLayout);
        pnFilterPrecisionLayout.setHorizontalGroup(
            pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFilterPrecisionLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFilterPrecisionLayout.createSequentialGroup()
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtPDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnFilterPrecisionLayout.createSequentialGroup()
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtHDOPMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                    .add(pnFilterPrecisionLayout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtVDOPMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnFilterPrecisionLayout.setVerticalGroup(
            pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilterPrecisionLayout.createSequentialGroup()
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(txtPDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(txtHDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setText(bundle.getString("BT747Main.jLabel7.text")); // NOI18N

        cbAdvancedActive.setText(bundle.getString("BT747Main.cbAdvancedActive.text")); // NOI18N
        cbAdvancedActive.setToolTipText(bundle.getString("BT747Main.cbAdvancedActive.toolTipText")); // NOI18N
        cbAdvancedActive.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbAdvancedActiveStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnCommonFilterLayout = new org.jdesktop.layout.GroupLayout(pnCommonFilter);
        pnCommonFilter.setLayout(pnCommonFilterLayout);
        pnCommonFilterLayout.setHorizontalGroup(
            pnCommonFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnCommonFilterLayout.createSequentialGroup()
                .add(pnCommonFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnCommonFilterLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel7))
                    .add(cbAdvancedActive))
                .addContainerGap())
            .add(pnFilterOther, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnCommonFilterLayout.createSequentialGroup()
                .add(pnFilterPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnCommonFilterLayout.setVerticalGroup(
            pnCommonFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnCommonFilterLayout.createSequentialGroup()
                .add(cbAdvancedActive)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterOther, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7))
        );

        pnWaypoint.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnWaypoint.border.title"))); // NOI18N
        pnWaypoint.setToolTipText(bundle.getString("BT747Main.pnWaypoint.toolTipText")); // NOI18N

        pnWayPointFix.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnWayPointFix.border.title"))); // NOI18N

        cbWayNoFix.setText(bundle.getString("BT747Main.cbWayNoFix.text")); // NOI18N
        cbWayNoFix.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWayPPS.setText(bundle.getString("BT747Main.cbWayPPS.text")); // NOI18N
        cbWayPPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWayEstimate.setText(bundle.getString("BT747Main.cbWayEstimate.text")); // NOI18N
        cbWayEstimate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWayManual.setText(bundle.getString("BT747Main.cbWayManual.text")); // NOI18N
        cbWayManual.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWaySPS.setText(bundle.getString("BT747Main.cbWaySPS.text")); // NOI18N
        cbWaySPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWayFRTK.setText(bundle.getString("BT747Main.cbWayFRTK.text")); // NOI18N
        cbWayFRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWayDGPS.setText(bundle.getString("BT747Main.cbWayDGPS.text")); // NOI18N
        cbWayDGPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWaySimulate.setText(bundle.getString("BT747Main.cbWaySimulate.text")); // NOI18N
        cbWaySimulate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        cbWayRTK.setText(bundle.getString("BT747Main.cbWayRTK.text")); // NOI18N
        cbWayRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayTypeFixAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnWayPointFixLayout = new org.jdesktop.layout.GroupLayout(pnWayPointFix);
        pnWayPointFix.setLayout(pnWayPointFixLayout);
        pnWayPointFixLayout.setHorizontalGroup(
            pnWayPointFixLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWayPointFixLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnWayPointFixLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbWayNoFix)
                    .add(cbWaySPS)
                    .add(cbWayDGPS)
                    .add(cbWayPPS)
                    .add(cbWayRTK)
                    .add(cbWayFRTK)
                    .add(cbWayEstimate)
                    .add(cbWayManual)
                    .add(cbWaySimulate))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnWayPointFixLayout.setVerticalGroup(
            pnWayPointFixLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWayPointFixLayout.createSequentialGroup()
                .add(cbWayNoFix)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWaySPS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayDGPS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayPPS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayRTK)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayFRTK)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayEstimate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayManual)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWaySimulate)
                .add(23, 23, 23))
        );

        pnWayPointRCR.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnWayPointRCR.border.title"))); // NOI18N

        cbWayTime.setText(bundle.getString("BT747Main.cbWayTime.text")); // NOI18N
        cbWayTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWaySpeed.setText(bundle.getString("BT747Main.cbWaySpeed.text")); // NOI18N
        cbWaySpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayDistance.setText(bundle.getString("BT747Main.cbWayDistance.text")); // NOI18N
        cbWayDistance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayButton.setText(bundle.getString("BT747Main.cbWayButton.text")); // NOI18N
        cbWayButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser1.setText(bundle.getString("BT747Main.cbWayUser1.text")); // NOI18N
        cbWayUser1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser2.setText(bundle.getString("BT747Main.cbWayUser2.text")); // NOI18N
        cbWayUser2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser3.setText(bundle.getString("BT747Main.cbWayUser3.text")); // NOI18N
        cbWayUser3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser4.setText(bundle.getString("BT747Main.cbWayUser4.text")); // NOI18N
        cbWayUser4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser5.setText(bundle.getString("BT747Main.cbWayUser5.text")); // NOI18N
        cbWayUser5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser6.setText(bundle.getString("BT747Main.cbWayUser6.text")); // NOI18N
        cbWayUser6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser7.setText(bundle.getString("BT747Main.cbWayUser7.text")); // NOI18N
        cbWayUser7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser8.setText(bundle.getString("BT747Main.cbWayUser8.text")); // NOI18N
        cbWayUser8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser9.setText(bundle.getString("BT747Main.cbWayUser9.text")); // NOI18N
        cbWayUser9.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser10.setText(bundle.getString("BT747Main.cbWayUser10.text")); // NOI18N
        cbWayUser10.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser11.setText(bundle.getString("BT747Main.cbWayUser11.text")); // NOI18N
        cbWayUser11.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWayUser12.setText(bundle.getString("BT747Main.cbWayUser12.text")); // NOI18N
        cbWayUser12.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WayRCRAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnWayPointRCRLayout = new org.jdesktop.layout.GroupLayout(pnWayPointRCR);
        pnWayPointRCR.setLayout(pnWayPointRCRLayout);
        pnWayPointRCRLayout.setHorizontalGroup(
            pnWayPointRCRLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbWayTime)
            .add(cbWaySpeed)
            .add(cbWayDistance)
            .add(cbWayButton)
            .add(pnWayPointRCRLayout.createSequentialGroup()
                .add(pnWayPointRCRLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbWayUser1)
                    .add(cbWayUser2)
                    .add(cbWayUser3)
                    .add(cbWayUser4)
                    .add(cbWayUser5)
                    .add(cbWayUser6))
                .add(18, 18, 18)
                .add(pnWayPointRCRLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbWayUser7)
                    .add(cbWayUser8)
                    .add(cbWayUser9)
                    .add(cbWayUser10)
                    .add(cbWayUser11)
                    .add(cbWayUser12)))
        );
        pnWayPointRCRLayout.setVerticalGroup(
            pnWayPointRCRLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWayPointRCRLayout.createSequentialGroup()
                .add(cbWayTime)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWaySpeed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayDistance)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbWayButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnWayPointRCRLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnWayPointRCRLayout.createSequentialGroup()
                        .add(cbWayUser7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser12))
                    .add(pnWayPointRCRLayout.createSequentialGroup()
                        .add(cbWayUser1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbWayUser6))))
        );

        org.jdesktop.layout.GroupLayout pnWaypointLayout = new org.jdesktop.layout.GroupLayout(pnWaypoint);
        pnWaypoint.setLayout(pnWaypointLayout);
        pnWaypointLayout.setHorizontalGroup(
            pnWaypointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWaypointLayout.createSequentialGroup()
                .add(pnWayPointFix, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnWayPointRCR, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pnWaypointLayout.setVerticalGroup(
            pnWaypointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWayPointRCR, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnWayPointFix, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 256, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout LogFiltersPanelLayout = new org.jdesktop.layout.GroupLayout(LogFiltersPanel);
        LogFiltersPanel.setLayout(LogFiltersPanelLayout);
        LogFiltersPanelLayout.setHorizontalGroup(
            LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(LogFiltersPanelLayout.createSequentialGroup()
                .add(pnTrackpoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnCommonFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnWaypoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        LogFiltersPanelLayout.setVerticalGroup(
            LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(LogFiltersPanelLayout.createSequentialGroup()
                .add(LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnCommonFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnTrackpoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnWaypoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.LogFiltersPanel.TabConstraints.tabTitle"), LogFiltersPanel); // NOI18N

        DeviceSettingsPanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                DeviceSettingsPanelFocusGained(evt);
            }
        });

        pnGPSStart.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnGPSStart.border.title"))); // NOI18N

        btHotStart.setText(bundle.getString("BT747Main.btHotStart.text")); // NOI18N
        btHotStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btHotStartActionPerformed(evt);
            }
        });

        btWarmStart.setText(bundle.getString("BT747Main.btWarmStart.text")); // NOI18N
        btWarmStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btWarmStartActionPerformed(evt);
            }
        });

        btColdStart.setText(bundle.getString("BT747Main.btColdStart.text")); // NOI18N
        btColdStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btColdStartActionPerformed(evt);
            }
        });

        btFactoryResetDevice.setText(bundle.getString("BT747Main.btFactoryResetDevice.text")); // NOI18N
        btFactoryResetDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFactoryResetDeviceActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnGPSStartLayout = new org.jdesktop.layout.GroupLayout(pnGPSStart);
        pnGPSStart.setLayout(pnGPSStartLayout);
        pnGPSStartLayout.setHorizontalGroup(
            pnGPSStartLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPSStartLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnGPSStartLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(btColdStart)
                    .add(btWarmStart)
                    .add(btHotStart)
                    .add(btFactoryResetDevice))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnGPSStartLayout.setVerticalGroup(
            pnGPSStartLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPSStartLayout.createSequentialGroup()
                .add(btHotStart)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btWarmStart)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btColdStart)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btFactoryResetDevice)
                .addContainerGap())
        );

        pnSBAS.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnSBAS.border.title"))); // NOI18N

        cbDGPSType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No DGPS", "RTCM", "WAAS" }));
        cbDGPSType.setToolTipText(bundle.getString("BT747Main.cbDGPSType.toolTipText")); // NOI18N

        cbUseSBAS.setText(bundle.getString("BT747Main.cbUseSBAS.text")); // NOI18N
        cbUseSBAS.setToolTipText(bundle.getString("BT747Main.cbUseSBAS.toolTipText")); // NOI18N

        cbIncludeTestSBAS.setText(bundle.getString("BT747Main.cbIncludeTestSBAS.text")); // NOI18N
        cbIncludeTestSBAS.setToolTipText(bundle.getString("BT747Main.cbIncludeTestSBAS.toolTipText")); // NOI18N

        btApplySBAS.setText(bundle.getString("BT747Main.btApplySBAS.text")); // NOI18N
        btApplySBAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btApplySBASActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnSBASLayout = new org.jdesktop.layout.GroupLayout(pnSBAS);
        pnSBAS.setLayout(pnSBASLayout);
        pnSBASLayout.setHorizontalGroup(
            pnSBASLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSBASLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnSBASLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnSBASLayout.createSequentialGroup()
                        .add(cbUseSBAS)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cbDGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(cbIncludeTestSBAS)
                    .add(btApplySBAS))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnSBASLayout.setVerticalGroup(
            pnSBASLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSBASLayout.createSequentialGroup()
                .add(pnSBASLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbUseSBAS)
                    .add(cbDGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbIncludeTestSBAS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btApplySBAS)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnHoluxSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnHoluxSettings.border.title"))); // NOI18N

        jLabel20.setText(bundle.getString("BT747Main.jLabel20.text")); // NOI18N

        txtHoluxName.setText(bundle.getString("BT747Main.txtHoluxName.text")); // NOI18N
        txtHoluxName.setToolTipText(bundle.getString("BT747Main.txtHoluxName.toolTipText")); // NOI18N

        btSetHoluxName.setText(bundle.getString("BT747Main.btSetHoluxName.text")); // NOI18N
        btSetHoluxName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetHoluxNameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnHoluxSettingsLayout = new org.jdesktop.layout.GroupLayout(pnHoluxSettings);
        pnHoluxSettings.setLayout(pnHoluxSettingsLayout);
        pnHoluxSettingsLayout.setHorizontalGroup(
            pnHoluxSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnHoluxSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel20)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtHoluxName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btSetHoluxName)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnHoluxSettingsLayout.setVerticalGroup(
            pnHoluxSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnHoluxSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel20)
                .add(btSetHoluxName)
                .add(txtHoluxName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnLogBy.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnLogBy.border.title"))); // NOI18N
        pnLogBy.setToolTipText(bundle.getString("BT747Main.pnLogBy.toolTipText")); // NOI18N

        jLabel35.setText(bundle.getString("BT747Main.jLabel35.text")); // NOI18N

        ckLogSpeedActive.setText(bundle.getString("BT747Main.ckLogSpeedActive.text")); // NOI18N

        txtLogDistanceInterval.setText(bundle.getString("BT747Main.txtLogDistanceInterval.text")); // NOI18N

        txtLogTimeInterval.setText(bundle.getString("BT747Main.txtLogTimeInterval.text")); // NOI18N

        jLabel36.setText(bundle.getString("BT747Main.jLabel36.text")); // NOI18N

        ckLogTimeActive.setText(bundle.getString("BT747Main.ckLogTimeActive.text")); // NOI18N

        jLabel37.setText(bundle.getString("BT747Main.jLabel37.text")); // NOI18N

        jLabel38.setText(bundle.getString("BT747Main.jLabel38.text")); // NOI18N

        jLabel39.setText(bundle.getString("BT747Main.jLabel39.text")); // NOI18N

        ckLogDistanceActive.setText(bundle.getString("BT747Main.ckLogDistanceActive.text")); // NOI18N

        txtLogSpeedInterval.setText(bundle.getString("BT747Main.txtLogSpeedInterval.text")); // NOI18N

        jLabel40.setText(bundle.getString("BT747Main.jLabel40.text")); // NOI18N

        btLogByApply.setText(bundle.getString("BT747Main.btLogByApply.text")); // NOI18N
        btLogByApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogByApplyActionPerformed(evt);
            }
        });

        jLabel41.setText(bundle.getString("BT747Main.jLabel41.text")); // NOI18N

        jTextField7.setText(bundle.getString("BT747Main.jTextField7.text")); // NOI18N

        jLabel42.setText(bundle.getString("BT747Main.jLabel42.text")); // NOI18N

        cbStopOrOverwriteWhenFull.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Stop when full", "Overwrite when full" }));
        cbStopOrOverwriteWhenFull.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbStopOrOverwriteWhenFullFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnLogByLayout = new org.jdesktop.layout.GroupLayout(pnLogBy);
        pnLogBy.setLayout(pnLogByLayout);
        pnLogByLayout.setHorizontalGroup(
            pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLogByLayout.createSequentialGroup()
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnLogByLayout.createSequentialGroup()
                        .add(58, 58, 58)
                        .add(jLabel41)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnLogByLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ckLogSpeedActive)
                            .add(ckLogDistanceActive)
                            .add(ckLogTimeActive))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel39)
                            .add(jLabel37)
                            .add(jLabel40))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnLogByLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtLogDistanceInterval, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                            .add(txtLogSpeedInterval, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                            .add(txtLogTimeInterval, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))))
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnLogByLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel42))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnLogByLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel35)
                            .add(jLabel38)
                            .add(jLabel36))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btLogByApply, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbStopOrOverwriteWhenFull, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        pnLogByLayout.setVerticalGroup(
            pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLogByLayout.createSequentialGroup()
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel41)
                    .add(jLabel42)
                    .add(btLogByApply)
                    .add(jTextField7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel35)
                    .add(jLabel40, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(ckLogTimeActive)
                    .add(txtLogTimeInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel37)
                    .add(jLabel36)
                    .add(ckLogSpeedActive)
                    .add(txtLogSpeedInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel39)
                    .add(jLabel38)
                    .add(ckLogDistanceActive)
                    .add(txtLogDistanceInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbStopOrOverwriteWhenFull, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(pnGPSStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnSBAS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pnHoluxSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnLogBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnGPSStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnSBAS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnHoluxSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnLogFormat.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnLogFormat.border.title"))); // NOI18N
        pnLogFormat.setToolTipText(bundle.getString("BT747Main.pnLogFormat.toolTipText")); // NOI18N

        btFormatAndErase.setText(bundle.getString("BT747Main.btFormatAndErase.text")); // NOI18N
        btFormatAndErase.setToolTipText(bundle.getString("BT747Main.btFormatAndErase.toolTipText")); // NOI18N
        btFormatAndErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFormatAndEraseActionPerformed(evt);
            }
        });

        btErase.setText(bundle.getString("BT747Main.btErase.text")); // NOI18N
        btErase.setToolTipText(bundle.getString("BT747Main.btErase.toolTipText")); // NOI18N
        btErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEraseActionPerformed(evt);
            }
        });

        btRecoverMemory.setText(bundle.getString("BT747Main.btRecoverMemory.text")); // NOI18N
        btRecoverMemory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRecoverMemoryActionPerformed(evt);
            }
        });

        btFormat.setText(bundle.getString("BT747Main.btFormat.text")); // NOI18N
        btFormat.setToolTipText(bundle.getString("BT747Main.btFormat.toolTipText")); // NOI18N
        btFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFormatActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(btFormatAndErase)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btFormat))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(btErase)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btRecoverMemory)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btFormatAndErase)
                    .add(btFormat))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btErase)
                    .add(btRecoverMemory))
                .addContainerGap())
        );

        pnTime.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnTime.border.title"))); // NOI18N

        cbUTCTime.setText(bundle.getString("BT747Main.cbUTCTime.text")); // NOI18N
        cbUTCTime.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbMilliSeconds.setText(bundle.getString("BT747Main.cbMilliSeconds.text")); // NOI18N
        cbMilliSeconds.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnTimeLayout = new org.jdesktop.layout.GroupLayout(pnTime);
        pnTime.setLayout(pnTimeLayout);
        pnTimeLayout.setHorizontalGroup(
            pnTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTimeLayout.createSequentialGroup()
                .add(pnTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbUTCTime)
                    .add(cbMilliSeconds))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnTimeLayout.setVerticalGroup(
            pnTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTimeLayout.createSequentialGroup()
                .add(cbUTCTime)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbMilliSeconds)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnPosition.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnPosition.border.title"))); // NOI18N

        cbLat.setText(bundle.getString("BT747Main.cbLat.text")); // NOI18N
        cbLat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbLong.setText(bundle.getString("BT747Main.cbLong.text")); // NOI18N
        cbLong.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbHeight.setText(bundle.getString("BT747Main.cbHeight.text")); // NOI18N
        cbHeight.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbSpeed.setText(bundle.getString("BT747Main.cbSpeed.text")); // NOI18N
        cbSpeed.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbHeading.setText(bundle.getString("BT747Main.cbHeading.text")); // NOI18N
        cbHeading.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbDistance.setText(bundle.getString("BT747Main.cbDistance.text")); // NOI18N
        cbDistance.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnPositionLayout = new org.jdesktop.layout.GroupLayout(pnPosition);
        pnPosition.setLayout(pnPositionLayout);
        pnPositionLayout.setHorizontalGroup(
            pnPositionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnPositionLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnPositionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbLat)
                    .add(cbHeight)
                    .add(cbLong)
                    .add(cbSpeed)
                    .add(cbHeading)
                    .add(cbDistance))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        pnPositionLayout.setVerticalGroup(
            pnPositionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnPositionLayout.createSequentialGroup()
                .add(cbLat)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbLong)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbHeight)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSpeed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbHeading)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbDistance))
        );

        pnPrecision.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnPrecision.border.title"))); // NOI18N

        cbDSTA.setText(bundle.getString("BT747Main.cbDSTA.text")); // NOI18N
        cbDSTA.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbDAGE.setText(bundle.getString("BT747Main.cbDAGE.text")); // NOI18N
        cbDAGE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbPDOP.setText(bundle.getString("BT747Main.cbPDOP.text")); // NOI18N
        cbPDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbHDOP.setText(bundle.getString("BT747Main.cbHDOP.text")); // NOI18N
        cbHDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbVDOP.setText(bundle.getString("BT747Main.cbVDOP.text")); // NOI18N
        cbVDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbFixType.setText(bundle.getString("BT747Main.cbFixType.text")); // NOI18N
        cbFixType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnPrecisionLayout = new org.jdesktop.layout.GroupLayout(pnPrecision);
        pnPrecision.setLayout(pnPrecisionLayout);
        pnPrecisionLayout.setHorizontalGroup(
            pnPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbFixType)
            .add(cbDSTA)
            .add(cbDAGE)
            .add(cbPDOP)
            .add(cbHDOP)
            .add(cbVDOP)
        );
        pnPrecisionLayout.setVerticalGroup(
            pnPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnPrecisionLayout.createSequentialGroup()
                .add(cbFixType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbDSTA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbDAGE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbPDOP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbHDOP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbVDOP))
        );

        pnSatInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnSatInfo.border.title"))); // NOI18N

        cbNSAT.setText(bundle.getString("BT747Main.cbNSAT.text")); // NOI18N
        cbNSAT.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbSID.setText(bundle.getString("BT747Main.cbSID.text")); // NOI18N
        cbSID.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbSIDItemStateChanged(evt);
            }
        });

        cbElevation.setText(bundle.getString("BT747Main.cbElevation.text")); // NOI18N
        cbElevation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbAzimuth.setText(bundle.getString("BT747Main.cbAzimuth.text")); // NOI18N
        cbAzimuth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        cbSNR.setText(bundle.getString("BT747Main.cbSNR.text")); // NOI18N
        cbSNR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnSatInfoLayout = new org.jdesktop.layout.GroupLayout(pnSatInfo);
        pnSatInfo.setLayout(pnSatInfoLayout);
        pnSatInfoLayout.setHorizontalGroup(
            pnSatInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbNSAT)
            .add(cbSID)
            .add(cbElevation)
            .add(cbAzimuth)
            .add(cbSNR)
        );
        pnSatInfoLayout.setVerticalGroup(
            pnSatInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSatInfoLayout.createSequentialGroup()
                .add(cbNSAT)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSID)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbElevation)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbAzimuth)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSNR)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtEstimatedRecords.setText(bundle.getString("BT747Main.txtEstimatedRecords.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(pnPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnSatInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(txtEstimatedRecords)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnSatInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(txtEstimatedRecords)
                .addContainerGap())
        );

        cbOtherFormat.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.cbOtherFormat.border.title"))); // NOI18N

        cbValidFixOnly.setText(bundle.getString("BT747Main.cbValidFixOnly.text")); // NOI18N
        cbValidFixOnly.setToolTipText(bundle.getString("BT747Main.cbValidFixOnly.toolTipText")); // NOI18N
        cbValidFixOnly.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout cbOtherFormatLayout = new org.jdesktop.layout.GroupLayout(cbOtherFormat);
        cbOtherFormat.setLayout(cbOtherFormatLayout);
        cbOtherFormatLayout.setHorizontalGroup(
            cbOtherFormatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbOtherFormatLayout.createSequentialGroup()
                .add(cbValidFixOnly)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        cbOtherFormatLayout.setVerticalGroup(
            cbOtherFormatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbValidFixOnly, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pnReason.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnReason.border.title"))); // NOI18N

        cbRCR.setText(bundle.getString("BT747Main.cbRCR.text")); // NOI18N
        cbRCR.setToolTipText(bundle.getString("BT747Main.cbRCR.toolTipText")); // NOI18N
        cbRCR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                updateLogRecordEstCount(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnReasonLayout = new org.jdesktop.layout.GroupLayout(pnReason);
        pnReason.setLayout(pnReasonLayout);
        pnReasonLayout.setHorizontalGroup(
            pnReasonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnReasonLayout.createSequentialGroup()
                .add(cbRCR)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnReasonLayout.setVerticalGroup(
            pnReasonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbRCR)
        );

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(pnReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbOtherFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbOtherFormat, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnReason, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnPosition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(pnTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnPosition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout pnLogFormatLayout = new org.jdesktop.layout.GroupLayout(pnLogFormat);
        pnLogFormat.setLayout(pnLogFormatLayout);
        pnLogFormatLayout.setHorizontalGroup(
            pnLogFormatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLogFormatLayout.createSequentialGroup()
                .add(pnLogFormatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnLogFormatLayout.setVerticalGroup(
            pnLogFormatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLogFormatLayout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout DeviceSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(DeviceSettingsPanel);
        DeviceSettingsPanel.setLayout(DeviceSettingsPanelLayout);
        DeviceSettingsPanelLayout.setHorizontalGroup(
            DeviceSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(DeviceSettingsPanelLayout.createSequentialGroup()
                .add(pnLogFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        DeviceSettingsPanelLayout.setVerticalGroup(
            DeviceSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLogFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.DeviceSettingsPanel.TabConstraints.tabTitle"), DeviceSettingsPanel); // NOI18N

        pnFlashSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFlashSettings.border.title"))); // NOI18N
        pnFlashSettings.setToolTipText(bundle.getString("BT747Main.pnFlashSettings.toolTipText")); // NOI18N

        jLabel30.setLabelFor(tfOutputFileBaseName);
        jLabel30.setText(bundle.getString("BT747Main.jLabel30.text")); // NOI18N

        jLabel31.setText(bundle.getString("BT747Main.jLabel31.text")); // NOI18N

        jLabel32.setText(bundle.getString("BT747Main.jLabel32.text")); // NOI18N

        lbGLLOut.setText(bundle.getString("BT747Main.lbGLLOut.text")); // NOI18N

        lbRMCOut.setText(bundle.getString("BT747Main.lbRMCOut.text")); // NOI18N

        lbVTGOut.setText(bundle.getString("BT747Main.lbVTGOut.text")); // NOI18N

        cbGSVOut.setText(bundle.getString("BT747Main.cbGSVOut.text")); // NOI18N

        cbGSAOut.setText(bundle.getString("BT747Main.cbGSAOut.text")); // NOI18N

        cbGGAOut.setLabelFor(cbFlashGGA);
        cbGGAOut.setText(bundle.getString("BT747Main.cbGGAOut.text")); // NOI18N

        cbFlashGLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashRMC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashVTG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashGGA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashGSA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashGSV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        txtFlashTimesLeft.setEditable(false);
        txtFlashTimesLeft.setText(bundle.getString("BT747Main.txtFlashTimesLeft.text")); // NOI18N

        txtFlashUpdateRate.setText(bundle.getString("BT747Main.txtFlashUpdateRate.text")); // NOI18N

        txtFlashBaudRate.setEditable(false);
        txtFlashBaudRate.setText(bundle.getString("BT747Main.txtFlashBaudRate.text")); // NOI18N

        lbFlashZDA.setText(bundle.getString("BT747Main.lbFlashZDA.text")); // NOI18N

        cbFlashZDA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashMCHN.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        btSetFlash.setText(bundle.getString("BT747Main.btSetFlash.text")); // NOI18N
        btSetFlash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetFlashActionPerformed(evt);
            }
        });

        jLabel29.setText(bundle.getString("BT747Main.jLabel29.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFlashSettingsLayout = new org.jdesktop.layout.GroupLayout(pnFlashSettings);
        pnFlashSettings.setLayout(pnFlashSettingsLayout);
        pnFlashSettingsLayout.setHorizontalGroup(
            pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFlashSettingsLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel30)
                    .add(jLabel32)
                    .add(lbRMCOut)
                    .add(lbVTGOut)
                    .add(cbGGAOut)
                    .add(cbGSAOut)
                    .add(lbFlashZDA)
                    .add(jLabel31)
                    .add(cbGSVOut)
                    .add(lbGLLOut)
                    .add(jLabel29))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFlashSettingsLayout.createSequentialGroup()
                        .add(cbFlashMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btSetFlash))
                    .add(cbFlashGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtFlashTimesLeft)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtFlashBaudRate)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtFlashUpdateRate)))
                .addContainerGap())
        );
        pnFlashSettingsLayout.setVerticalGroup(
            pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFlashSettingsLayout.createSequentialGroup()
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel30)
                    .add(txtFlashTimesLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel31)
                    .add(txtFlashUpdateRate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel32)
                    .add(txtFlashBaudRate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbGLLOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbRMCOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbVTGOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbGGAOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbGSAOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbFlashZDA))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbGSVOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel29)
                    .add(btSetFlash))
                .add(19, 19, 19))
        );

        pnNMEAOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnNMEAOutput.border.title"))); // NOI18N
        pnNMEAOutput.setToolTipText(bundle.getString("BT747Main.pnNMEAOutput.toolTipText")); // NOI18N

        cbNMEAOutType10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        lbNMEAOutType10.setText(bundle.getString("BT747Main.lbNMEAOutType10.text")); // NOI18N

        cbNMEAOutType11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutType12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMALM.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMDGP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMEPH.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMDBG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutZDA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        jLabel22.setText(bundle.getString("BT747Main.jLabel22.text")); // NOI18N

        jLabel23.setText(bundle.getString("BT747Main.jLabel23.text")); // NOI18N

        jLabel24.setText(bundle.getString("BT747Main.jLabel24.text")); // NOI18N

        jLabel25.setText(bundle.getString("BT747Main.jLabel25.text")); // NOI18N

        jLabel26.setText(bundle.getString("BT747Main.jLabel26.text")); // NOI18N

        jLabel27.setText(bundle.getString("BT747Main.jLabel27.text")); // NOI18N

        lbNMEAOutZDA.setText(bundle.getString("BT747Main.lbNMEAOutZDA.text")); // NOI18N

        btSetNMEAOutput.setText(bundle.getString("BT747Main.btSetNMEAOutput.text")); // NOI18N
        btSetNMEAOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetNMEAOutputActionPerformed(evt);
            }
        });

        btSetNMEAOutputDefaults.setText(bundle.getString("BT747Main.btSetNMEAOutputDefaults.text")); // NOI18N
        btSetNMEAOutputDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetNMEAOutputDefaultsActionPerformed(evt);
            }
        });

        lbGLLOut1.setText(bundle.getString("BT747Main.lbGLLOut1.text")); // NOI18N

        cbNMEAOutGLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutRMC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        lbRMCOut1.setText(bundle.getString("BT747Main.lbRMCOut1.text")); // NOI18N

        cbVTGOut1.setText(bundle.getString("BT747Main.cbVTGOut1.text")); // NOI18N

        cbNMEAOutVTG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGGA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbGGAOut1.setText(bundle.getString("BT747Main.cbGGAOut1.text")); // NOI18N

        cbGSAOut1.setText(bundle.getString("BT747Main.cbGSAOut1.text")); // NOI18N

        cbNMEAOutGSA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGSV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbGSVOut1.setText(bundle.getString("BT747Main.cbGSVOut1.text")); // NOI18N

        cbGRSOut.setText(bundle.getString("BT747Main.cbGRSOut.text")); // NOI18N

        cbGSTOut.setText(bundle.getString("BT747Main.cbGSTOut.text")); // NOI18N

        cbType9Out.setText(bundle.getString("BT747Main.cbType9Out.text")); // NOI18N

        cbType8Out.setText(bundle.getString("BT747Main.cbType8Out.text")); // NOI18N

        cbNMEAOutType9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutType8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGST.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGRS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMCHN.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        jLabel34.setText(bundle.getString("BT747Main.jLabel34.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnNMEAOutputLayout = new org.jdesktop.layout.GroupLayout(pnNMEAOutput);
        pnNMEAOutput.setLayout(pnNMEAOutputLayout);
        pnNMEAOutputLayout.setHorizontalGroup(
            pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnNMEAOutputLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnNMEAOutputLayout.createSequentialGroup()
                        .add(lbNMEAOutType10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbNMEAOutType10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnNMEAOutputLayout.createSequentialGroup()
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lbGLLOut1)
                            .add(cbGSVOut1)
                            .add(cbGSAOut1)
                            .add(cbGGAOut1)
                            .add(cbVTGOut1)
                            .add(lbRMCOut1)
                            .add(cbGRSOut)
                            .add(cbType9Out)
                            .add(cbType8Out)
                            .add(cbGSTOut))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbNMEAOutGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutGRS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutGST, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutType8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutType9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(jLabel22)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutType11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(jLabel23)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutType12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(jLabel24)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMALM, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(jLabel25)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMEPH, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(jLabel26)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMDGP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(jLabel27)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMDBG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbNMEAOutZDA)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(jLabel34)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(btSetNMEAOutput)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btSetNMEAOutputDefaults))
                .addContainerGap())
        );
        pnNMEAOutputLayout.setVerticalGroup(
            pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnNMEAOutputLayout.createSequentialGroup()
                .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnNMEAOutputLayout.createSequentialGroup()
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbGLLOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbRMCOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbVTGOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGGAOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGSAOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGSVOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGRS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGRSOut))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGST, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGSTOut))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbType8Out)
                            .add(jLabel34)))
                    .add(pnNMEAOutputLayout.createSequentialGroup()
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbNMEAOutType10))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel22))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel23))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMALM, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel24))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMEPH, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel25))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMDGP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel26))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMDBG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel27))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbNMEAOutZDA))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbNMEAOutMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(cbNMEAOutType9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(cbType9Out))
                    .add(btSetNMEAOutput))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btSetNMEAOutputDefaults)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout AdvancedSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(AdvancedSettingsPanel);
        AdvancedSettingsPanel.setLayout(AdvancedSettingsPanelLayout);
        AdvancedSettingsPanelLayout.setHorizontalGroup(
            AdvancedSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(AdvancedSettingsPanelLayout.createSequentialGroup()
                .add(pnFlashSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        AdvancedSettingsPanelLayout.setVerticalGroup(
            AdvancedSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnFlashSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.AdvancedSettingsPanel.TabConstraints.tabTitle"), AdvancedSettingsPanel); // NOI18N

        pnFileNMEAOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFileNMEAOutput.border.title"))); // NOI18N
        pnFileNMEAOutput.setToolTipText(bundle.getString("BT747Main.pnFileNMEAOutput.toolTipText")); // NOI18N

        lbNMEAFileType9.setText(bundle.getString("BT747Main.lbNMEAFileType9.text")); // NOI18N

        lbNMEAFileGST.setText(bundle.getString("BT747Main.lbNMEAFileGST.text")); // NOI18N

        lbNMEAFileVTG.setText(bundle.getString("BT747Main.lbNMEAFileVTG.text")); // NOI18N

        lbNMEAFileGRS.setText(bundle.getString("BT747Main.lbNMEAFileGRS.text")); // NOI18N

        lbNMEAFileGSV.setText(bundle.getString("BT747Main.lbNMEAFileGSV.text")); // NOI18N

        lbNMEAFileGGA.setText(bundle.getString("BT747Main.lbNMEAFileGGA.text")); // NOI18N

        lbNMEAFileRMC.setText(bundle.getString("BT747Main.lbNMEAFileRMC.text")); // NOI18N

        lbNMEAFileGLL.setText(bundle.getString("BT747Main.lbNMEAFileGLL.text")); // NOI18N

        lbNMEAFileType8.setText(bundle.getString("BT747Main.lbNMEAFileType8.text")); // NOI18N

        lbNMEAFileGSA.setText(bundle.getString("BT747Main.lbNMEAFileGSA.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFileNMEAOutLeftLayout = new org.jdesktop.layout.GroupLayout(pnFileNMEAOutLeft);
        pnFileNMEAOutLeft.setLayout(pnFileNMEAOutLeftLayout);
        pnFileNMEAOutLeftLayout.setHorizontalGroup(
            pnFileNMEAOutLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lbNMEAFileGLL)
            .add(lbNMEAFileRMC)
            .add(lbNMEAFileVTG)
            .add(lbNMEAFileGGA)
            .add(lbNMEAFileGSA)
            .add(lbNMEAFileGSV)
            .add(lbNMEAFileGRS)
            .add(lbNMEAFileGST)
            .add(lbNMEAFileType8)
            .add(lbNMEAFileType9)
        );
        pnFileNMEAOutLeftLayout.setVerticalGroup(
            pnFileNMEAOutLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutLeftLayout.createSequentialGroup()
                .add(lbNMEAFileGLL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileRMC)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileVTG)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGGA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGSA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGSV)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGRS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGST)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType9))
        );

        lbNMEAFileMDGP.setText(bundle.getString("BT747Main.lbNMEAFileMDGP.text")); // NOI18N

        lbNMEAFileType11.setText(bundle.getString("BT747Main.lbNMEAFileType11.text")); // NOI18N

        lbNMEAFileMEPH.setText(bundle.getString("BT747Main.lbNMEAFileMEPH.text")); // NOI18N

        lbNMEAFileMALM.setText(bundle.getString("BT747Main.lbNMEAFileMALM.text")); // NOI18N

        lbNMEAFileType10.setText(bundle.getString("BT747Main.lbNMEAFileType10.text")); // NOI18N

        lbNMEAFileZDA.setText(bundle.getString("BT747Main.lbNMEAFileZDA.text")); // NOI18N

        lbNMEAFileType12.setText(bundle.getString("BT747Main.lbNMEAFileType12.text")); // NOI18N

        btSetNMEAFileOutput.setText(bundle.getString("BT747Main.btSetNMEAFileOutput.text")); // NOI18N
        btSetNMEAFileOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetNMEAFileOutputActionPerformed(evt);
            }
        });

        lbNMEAFileMDBG.setText(bundle.getString("BT747Main.lbNMEAFileMDBG.text")); // NOI18N

        lbNMEAFileMCHN.setText(bundle.getString("BT747Main.lbNMEAFileMCHN.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFileNMEAOutRightLayout = new org.jdesktop.layout.GroupLayout(pnFileNMEAOutRight);
        pnFileNMEAOutRight.setLayout(pnFileNMEAOutRightLayout);
        pnFileNMEAOutRightLayout.setHorizontalGroup(
            pnFileNMEAOutRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutRightLayout.createSequentialGroup()
                .add(pnFileNMEAOutRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lbNMEAFileType10)
                    .add(lbNMEAFileType11)
                    .add(lbNMEAFileType12)
                    .add(lbNMEAFileMALM)
                    .add(lbNMEAFileMEPH)
                    .add(lbNMEAFileMDGP)
                    .add(lbNMEAFileMDBG)
                    .add(lbNMEAFileZDA)
                    .add(lbNMEAFileMCHN)
                    .add(pnFileNMEAOutRightLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(btSetNMEAFileOutput)))
                .addContainerGap())
        );
        pnFileNMEAOutRightLayout.setVerticalGroup(
            pnFileNMEAOutRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutRightLayout.createSequentialGroup()
                .add(lbNMEAFileType10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMALM)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMEPH)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMDGP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMDBG)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileZDA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMCHN)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btSetNMEAFileOutput)
                .add(0, 0, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnFileNMEAOutputLayout = new org.jdesktop.layout.GroupLayout(pnFileNMEAOutput);
        pnFileNMEAOutput.setLayout(pnFileNMEAOutputLayout);
        pnFileNMEAOutputLayout.setHorizontalGroup(
            pnFileNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutputLayout.createSequentialGroup()
                .add(pnFileNMEAOutLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFileNMEAOutRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnFileNMEAOutputLayout.setVerticalGroup(
            pnFileNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutputLayout.createSequentialGroup()
                .add(pnFileNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFileNMEAOutLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnFileNMEAOutRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnGPXFileSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnGPXFileSettings.border.title"))); // NOI18N
        pnGPXFileSettings.setToolTipText(bundle.getString("BT747Main.pnGPXFileSettings.toolTipText")); // NOI18N

        cbNotApplyUTCOffset.setText(bundle.getString("BT747Main.cbNotApplyUTCOffset.text")); // NOI18N
        cbNotApplyUTCOffset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbNotApplyUTCOffsetStateChanged(evt);
            }
        });

        cbGPXTrkSegWhenSmall.setText(bundle.getString("BT747Main.cbGPXTrkSegWhenSmall.text")); // NOI18N
        cbGPXTrkSegWhenSmall.setToolTipText(bundle.getString("BT747Main.cbGPXTrkSegWhenSmall.toolTipText")); // NOI18N
        cbGPXTrkSegWhenSmall.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbGPXTrkSegWhenSmallStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnGPXFileSettingsLayout = new org.jdesktop.layout.GroupLayout(pnGPXFileSettings);
        pnGPXFileSettings.setLayout(pnGPXFileSettingsLayout);
        pnGPXFileSettingsLayout.setHorizontalGroup(
            pnGPXFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPXFileSettingsLayout.createSequentialGroup()
                .add(pnGPXFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbNotApplyUTCOffset)
                    .add(cbGPXTrkSegWhenSmall))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnGPXFileSettingsLayout.setVerticalGroup(
            pnGPXFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPXFileSettingsLayout.createSequentialGroup()
                .add(cbNotApplyUTCOffset)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGPXTrkSegWhenSmall)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout AdvancedfileSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(AdvancedfileSettingsPanel);
        AdvancedfileSettingsPanel.setLayout(AdvancedfileSettingsPanelLayout);
        AdvancedfileSettingsPanelLayout.setHorizontalGroup(
            AdvancedfileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(AdvancedfileSettingsPanelLayout.createSequentialGroup()
                .add(pnFileNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnGPXFileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        AdvancedfileSettingsPanelLayout.setVerticalGroup(
            AdvancedfileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPXFileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnFileNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.AdvancedfileSettingsPanel.TabConstraints.tabTitle"), AdvancedfileSettingsPanel); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setOpaque(false);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText(bundle.getString("BT747Main.jTextArea1.text")); // NOI18N
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setFocusable(false);
        jTextArea1.setOpaque(false);
        jScrollPane1.setViewportView(jTextArea1);

        org.jdesktop.layout.GroupLayout InfoPanelLayout = new org.jdesktop.layout.GroupLayout(InfoPanel);
        InfoPanel.setLayout(InfoPanelLayout);
        InfoPanelLayout.setHorizontalGroup(
            InfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(InfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
                .addContainerGap())
        );
        InfoPanelLayout.setVerticalGroup(
            InfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(InfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.InfoPanel.TabConstraints.tabTitle"), InfoPanel); // NOI18N

        FileMenu.setText(bundle.getString("BT747Main.FileMenu.text")); // NOI18N
        jMenuBar.add(FileMenu);

        SettingsMenu.setText(bundle.getString("BT747Main.SettingsMenu.text")); // NOI18N

        btGPSDebug.setText(bundle.getString("BT747Main.btGPSDebug.text")); // NOI18N
        btGPSDebug.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btGPSDebugStateChanged(evt);
            }
        });
        SettingsMenu.add(btGPSDebug);

        btGPSConnectDebug.setText(bundle.getString("BT747Main.btGPSConnectDebug.text")); // NOI18N
        btGPSConnectDebug.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btGPSConnectDebugStateChanged(evt);
            }
        });
        SettingsMenu.add(btGPSConnectDebug);

        jMenuBar.add(SettingsMenu);

        InfoMenu.setText(bundle.getString("BT747Main.InfoMenu.text")); // NOI18N

        AboutBT747.setText(bundle.getString("BT747Main.AboutBT747.text")); // NOI18N
        InfoMenu.add(AboutBT747);

        Info.setText(bundle.getString("BT747Main.Info.text")); // NOI18N
        InfoMenu.add(Info);

        jMenuBar.add(InfoMenu);

        setJMenuBar(jMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnBottomInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tabbedPanelAll, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(tabbedPanelAll)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnBottomInformation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedPanelAll.getAccessibleContext().setAccessibleName(bundle.getString("Log_download_&_Convert")); // NOI18N

        getAccessibleContext().setAccessibleName("MTK Datalogger Control (BT747)");

        pack();
    }//GEN-END:initComponents

private void cbSerialSpeedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbSerialSpeedFocusLost
// TODO add your handling code here:
}//GEN-LAST:event_cbSerialSpeedFocusLost

    private void DeviceSettingsPanelFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_DeviceSettingsPanelFocusGained
        c.reqLogOverwrite();
        c.reqLogReasonStatus();
        c.reqSBASEnabled();
        c.reqSBASTestEnabled();
        c.reqFixInterval();
        c.reqBTAddr();
        c.reqMtkLogVersion();
        c.reqDatumMode();
    }// GEN-LAST:event_DeviceSettingsPanelFocusGained

    private void cbNotApplyUTCOffsetStateChanged( javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_cbNotApplyUTCOffsetStateChanged
        c.setGpxUTC0(cbNotApplyUTCOffset.isSelected());
    }// GEN-LAST:event_cbNotApplyUTCOffsetStateChanged

    private void cbStopOrOverwriteWhenFullFocusLost( java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbStopOrOverwriteWhenFullFocusLost
        c.setLogOverwrite(cbStopOrOverwriteWhenFull.getSelectedIndex() == 1);
    }// GEN-LAST:event_cbStopOrOverwriteWhenFullFocusLost

    private void cbGPXTrkSegWhenSmallStateChanged( javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_cbGPXTrkSegWhenSmallStateChanged
        c.setGpxTrkSegWhenBig(cbGPXTrkSegWhenSmall.isSelected());
    }// GEN-LAST:event_cbGPXTrkSegWhenSmallStateChanged

    private void cbOneFilePerDayFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbOneFilePerDayFocusLost
        int type = 0;
        switch (cbOneFilePerDay.getSelectedIndex()) {
        case 0:
            type = 1;
            break;
        case 1:
            type = 2;
            break;
        case 2:
            type = 0;
            break;
        }
        c.setOutputFileSplitType(type);
    }// GEN-LAST:event_cbOneFilePerDayFocusLost

    private void cbRecordNumberInfoInLogFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbRecordNumberInfoInLogFocusLost
        c.setRecordNbrInLogs(cbRecordNumberInfoInLog.isSelected());
    }// GEN-LAST:event_cbRecordNumberInfoInLogFocusLost

    private void cbImperialUnitsFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbImperialUnitsFocusLost
        c.setImperial(cbImperialUnits.isSelected());
    }// GEN-LAST:event_cbImperialUnitsFocusLost

    private void tfTrackSeparationTimeFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_tfTrackSeparationTimeFocusLost
        c.setTrkSep(Integer.parseInt(tfTrackSeparationTime.getText()));
    }// GEN-LAST:event_tfTrackSeparationTimeFocusLost

    private void cbNoFixColorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbFixColorActionPerformed
        // TODO add your handling code here:
        Color myColor = new Color(Conv.hex2Int(m.getColorInvalidTrack()));
        myColor = JColorChooser.showDialog(this,
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Choose_the_color_for_a_'bad_track'_(pure_blue_to_ignore)"),
                myColor);
        if (myColor != null) {
            c.setColorInvalidTrack(Convert.unsigned2hex(
                    myColor.getRGB() & 0xFFFFFF, 6));
        }
        updateColorButtons();
    }// GEN-LAST:event_cbFixColorActionPerformed

    private void cbGoodFixColorActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbFixColorActionPerformed
        // TODO add your handling code here:
        Color myColor = new Color(Conv.hex2Int(m.getColorValidTrack()));
        myColor = JColorChooser.showDialog(this,
                java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Choose_the_color_for_a_'good_track'_(pure_blue_to_ignore)"),
                myColor);
        if (myColor != null) {
            c.setColorValidTrack(Convert.unsigned2hex(
                    myColor.getRGB() & 0xFFFFFF, 6));
        }
        updateColorButtons();
    }// GEN-LAST:event_cbFixColorActionPerformed
    
    private void updateColorButtons() {
        Color myColor = new Color(Conv.hex2Int(m.getColorValidTrack()));
        cbGoodFixColor.setBackground(myColor);
        cbGoodFixColor.setForeground(new Color(255-myColor.getRed(),255-myColor.getGreen(),255-myColor.getBlue()));
        myColor = new Color(Conv.hex2Int(m.getColorInvalidTrack()));
        cbNoFixColor.setBackground(myColor);
        cbNoFixColor.setForeground(new Color(255-myColor.getRed(),255-myColor.getGreen(),255-myColor.getBlue()));
    }

    private void txtPDOPMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtPDOPMaxFocusLost
        c.setFilterMaxPDOP(Float.parseFloat(txtPDOPMax.getText()));
    }// GEN-LAST:event_txtPDOPMaxFocusLost

    private void txtHDOPMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtHDOPMaxFocusLost
        c.setFilterMaxHDOP(Float.parseFloat(txtHDOPMax.getText()));
    }// GEN-LAST:event_txtHDOPMaxFocusLost

    private void txtVDOPMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtVDOPMaxFocusLost
        c.setFilterMaxVDOP(Float.parseFloat(txtVDOPMax.getText()));
    }// GEN-LAST:event_txtVDOPMaxFocusLost

    private void txtRecCntMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtRecCntMinFocusLost
        c.setFilterMinRecCount(Integer.parseInt(txtRecCntMin.getText()));
    }// GEN-LAST:event_txtRecCntMinFocusLost

    private void txtDistanceMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtDistanceMinFocusLost
        c.setFilterMinDist(Float.parseFloat(txtDistanceMin.getText()));
    }// GEN-LAST:event_txtDistanceMinFocusLost

    private void txtSpeedMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtSpeedMinFocusLost
        c.setFilterMinSpeed(Float.parseFloat(txtSpeedMin.getText()));
    }// GEN-LAST:event_txtSpeedMinFocusLost

    private void txtNSATMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtNSATMinFocusLost
        c.setFilterMinNSAT(Integer.parseInt(txtNSATMin.getText()));
    }// GEN-LAST:event_txtNSATMinFocusLost

    private void txtSpeedMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtSpeedMaxFocusLost
        c.setFilterMaxSpeed(Float.parseFloat(txtSpeedMax.getText()));
    }// GEN-LAST:event_txtSpeedMaxFocusLost

    private void txtDistanceMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtDistanceMaxFocusLost
        c.setFilterMaxDist(Float.parseFloat(txtDistanceMax.getText()));
    }// GEN-LAST:event_txtDistanceMaxFocusLost

    private void txtRecCntMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtRecCntMaxInputMethodTextChanged
        c.setFilterMaxRecCount(Integer.parseInt(txtRecCntMax.getText()));
    }// GEN-LAST:event_txtRecCntMaxInputMethodTextChanged

    private void TrkFixTypeAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_TrkFixTypeAction
        setTrkValidFilterSettings();
    }// GEN-LAST:event_TrkFixTypeAction

    private void WayTypeFixAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_WayTypeFixAction
        setWayValidFilterSettings();
    }// GEN-LAST:event_WayTypeFixAction

    private void WayRCRAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_WayRCRAction
        setWayRCRFilterSettings();
    }// GEN-LAST:event_WayRCRAction

    private void TrkRCRAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_TrkRCRAction
        setTrkRCRFilterSettings();
    }// GEN-LAST:event_TrkRCRAction

    private void btSetFlashActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        setFlashConfig();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btSetHoluxNameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.setHoluxName(txtHoluxName.getText());
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btApplySBASActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.setSBASEnabled(cbUseSBAS.isSelected());
        c.setSBASTestEnabled(cbIncludeTestSBAS.isSelected());
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btSetNMEAOutputActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        setNMEAOutPeriods();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btSetNMEAOutputDefaultsActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.setNMEADefaultPeriods();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btSetNMEAFileOutputActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        setNMEAOutFile();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btHotStartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.doHotStart();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btWarmStartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.doWarmStart();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btColdStartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.doColdStart();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btFactoryResetDeviceActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.doFactoryReset();
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btLogByApplyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        try {
            int value;
            if (ckLogTimeActive.isSelected()) {
                value = (int) (Convert.toDouble(txtLogTimeInterval.getText()) * 10);
            } else {
                value = 0;
            }
            c.setLogTimeInterval(value);
            if (ckLogSpeedActive.isSelected()) {
                value = (int) (Convert.toDouble(txtLogSpeedInterval.getText()));
            } else {
                value = 0;
            }
            c.setLogSpeedInterval(value);
            if (ckLogDistanceActive.isSelected()) {
                value = (int) (Convert.toDouble(txtLogDistanceInterval
                        .getText()) * 10);
            } else {
                value = 0;
            }
            c.setLogDistanceInterval(value);
            c.setFixInterval(Convert.toInt(jTextField7.getText()));
        } catch (Exception e) {
            Generic
                    .debug(
                            java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Problem_in_Apply_Log_conditions_-_probably_non-numeric_value"),
                            e);
        }
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btDownloadFromNumerixActionPerformed( java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btDownloadFromNumerixActionPerformed
        c.startDPL700Download();
    }// GEN-LAST:event_btDownloadFromNumerixActionPerformed

    private void cbGPSTypeFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbGPSTypeFocusLost
        int type = Controller.GPS_TYPE_DEFAULT;
        boolean forceHolux = false;
        switch(cbGPSType.getSelectedIndex()) {
        case 1:
            type = Controller.GPS_TYPE_DEFAULT;
            forceHolux = true;
            break;
        case 2:
            type = Controller.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX;
            break;
        case 3:
            type = Controller.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR;
            break;
        case 4:
            type = Controller.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII;
            break;
        case 0:
        default:
            type = Controller.GPS_TYPE_DEFAULT;
        }
        c.setGPSType(type);
        c.setForceHolux241(forceHolux);
    }// GEN-LAST:event_cbGPSTypeFocusLost
    
    private final void updateCbGPSType() {
        if(m.getBooleanOpt(AppSettings.IS_HOLUXM241)) {
            cbGPSType.setSelectedIndex(1);
        } else {
            int index = 0;
            switch(m.getGPSType()) {
            case 0:
                index = 0;
                break;
            case 1:
                index = 2;
                break;
            case 2:
                index = 3;
                break;
            case 3:
                index = 4;
                break;
            }
            cbGPSType.setSelectedIndex(index);
        }
        
    }

    private void updateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_updateLogRecordEstCount

    private void cbDisableLoggingDuringDownloadFocusLost(
            java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbDisableLoggingDuringDownloadFocusLost
        c.setIncremental(cbDisableLoggingDuringDownload.isSelected());
    }// GEN-LAST:event_cbDisableLoggingDuringDownloadFocusLost

    private void cbUTCOffsetFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbUTCOffsetFocusLost
        // TODO: Initialise offset
        String tmp = (String) cbUTCOffset.getSelectedItem();
        if (tmp.charAt(4) == '+') {
            c.setTimeOffsetHours(Integer.parseInt((String) tmp.substring(5)));
        } else {
            c.setTimeOffsetHours(Integer.parseInt(tmp.substring(4)));
        }

    }// GEN-LAST:event_cbUTCOffsetFocusLost

    private void cbStandardOrDaylightSavingFocusLost( java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbStandardOrDaylightSavingFocusLost
        // TODO: implement
    }// GEN-LAST:event_cbStandardOrDaylightSavingFocusLost

    private void cbHeightOverMeanSeaLevelFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbHeightOverMeanSeaLevelFocusLost
        c
                .setConvertWGS84ToMSL(cbHeightOverMeanSeaLevel
                        .getSelectedIndex() == 1);
    }// GEN-LAST:event_cbHeightOverMeanSeaLevelFocusLost

    private void btGPSDebugStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_btGPSDebugStateChanged
        c.setDebug(btGPSDebug.isSelected());
    }// GEN-LAST:event_btGPSDebugStateChanged

    private void btGPSConnectDebugStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_btGPSConnectDebugStateChanged
        c.setDebugConn(btGPSConnectDebug.isSelected());
    }// GEN-LAST:event_btGPSConnectDebugStateChanged

    private void cbAdvancedActiveStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_cbAdvancedActiveStateChanged
        c.setAdvFilterActive(cbAdvancedActive.isSelected());
        updateAdvancedFilter();
    }// GEN-LAST:event_cbAdvancedActiveStateChanged
    
    private void updateAdvancedFilter() {
        boolean en = m.isAdvFilterActive();
        Component[] l;
        l = pnFilterPrecision.getComponents();
        for (Component component : l) {
            component.setEnabled(en);
        }
        l = pnFilterOther.getComponents();
        for (Component component : l) {
            component.setEnabled(en);
        }
    }

    private void tfRawLogFilePathFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_tfRawLogFilePathFocusLost

        c.setStringOpt(AppSettings.LOGFILEPATH, tfRawLogFilePath.getText());
    }// GEN-LAST:event_tfRawLogFilePathFocusLost

    private void btFormatAndEraseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btFormatAndEraseActionPerformed

        c.changeLogFormatAndErase(getUserLogFormat());
    }// GEN-LAST:event_btFormatAndEraseActionPerformed

    private void btFormatActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btFormatActionPerformed

        c.changeLogFormat(getUserLogFormat());
    }// GEN-LAST:event_btFormatActionPerformed

    private void btEraseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btEraseActionPerformed

        c.eraseLogFormat();
    }// GEN-LAST:event_btEraseActionPerformed

    private void btRecoverMemoryActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btRecoverMemoryActionPerformed

        c.recoveryErase();
    }// GEN-LAST:event_btRecoverMemoryActionPerformed

    private void cbSIDItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbSIDItemStateChanged

        updateSatGuiItems();
    }// GEN-LAST:event_cbSIDItemStateChanged

    private void cbPortNameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbPortNameActionPerformed
        // selectPort(jComboBox1.getSelectedItem().toString());
    }// GEN-LAST:event_cbPortNameActionPerformed

    private void btConnectActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btConnectActionPerformed

        if (btConnectFunctionIsConnect) {
            openPort(cbPortName.getSelectedItem().toString());
        } else {
            c.closeGPS();
            ;
        }
    }// GEN-LAST:event_btConnectActionPerformed

    private void cbIncrementalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbIncrementalActionPerformed

        c.setIncremental(cbIncremental.isSelected());
    }// GEN-LAST:event_cbIncrementalActionPerformed

    private void btDownloadIBlueActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed

        c.startDefaultDownload();
    }// GEN-LAST:event_jButton1ActionPerformed

    private void cbDecoderChoiceActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbDecoderChoiceActionPerformed
        switch (cbDecoderChoice.getSelectedIndex()) {
        case 0:
            c.setBinDecoder(J2SEAppController.DECODER_ORG);
            break;
        case 1:
            c.setBinDecoder(J2SEAppController.DECODER_THOMAS);
            break;
        }
    }// GEN-LAST:event_cbDecoderChoiceActionPerformed

    private void cbFormatItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFormatItemStateChanged
        switch (evt.getStateChange()) {
        case java.awt.event.ItemEvent.SELECTED:
            setSelectedFormat(evt.getItem().toString());
            break;
        case java.awt.event.ItemEvent.DESELECTED:
            break;
        }
    }// GEN-LAST:event_cbFormatItemStateChanged

    private void btConvertActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btConvertActionPerformed
        doLogConversion();
    }// GEN-LAST:event_btConvertActionPerformed

    private void btOutputFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btOutputFileActionPerformed
        javax.swing.JFileChooser OutputFileChooser;
        File f = getOutputFilePath();
        OutputFileChooser = new javax.swing.JFileChooser(f.getParent());
        OutputFileChooser.setSelectedFile(f);
        OutputFileChooser
                .setToolTipText(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Select_the_basename_of_the_output_file."));
        // if (curDir.exists()) {
        // OutputFileChooser.setCurrentDirectory(getOutputFilePath());
        // }
        if (OutputFileChooser.showDialog(this, java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Set_basename")) == JFileChooser.APPROVE_OPTION) {
            try {
                c.setOutputFileRelPath(gps.convert.FileUtil.getRelativePath(
                        (new File(m.getStringOpt(AppSettings.OUTPUTDIRPATH)))
                                .getCanonicalPath(), OutputFileChooser
                                .getSelectedFile().getCanonicalPath(),
                        File.separatorChar));
            } catch (Exception e) {
                Generic.debug(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("OutputFileChooser"), e);
            }
        }
    }// GEN-LAST:event_btOutputFileActionPerformed

    private void btRawLogFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btRawLogFileActionPerformed
        javax.swing.JFileChooser RawLogFileChooser;
        File f= getRawLogFilePath();
        RawLogFileChooser = new javax.swing.JFileChooser(f.getParent());

        RawLogFileChooser.setSelectedFile(f);
        f=null;
        // getRawLogFilePath();
        // if (curDir.exists()) {
        // RawLogFileChooser.setCurrentDirectory(getRawLogFilePath());
        // }
        if (RawLogFileChooser.showDialog(this,java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Set")) == JFileChooser.APPROVE_OPTION) {
            try {
            c.setStringOpt(AppSettings.LOGFILEPATH, RawLogFileChooser.getSelectedFile()
                    .getCanonicalPath());
            } catch (Exception e) {
                Generic.debug(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("RawFileChooser"),e);
            }
        }
    }// GEN-LAST:event_btRawLogFileActionPerformed

    private void btWorkingDirectoryActionPerformed( java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btWorkingDirectoryActionPerformed
        javax.swing.JFileChooser WorkingDirectoryChooser;
        File curDir = getWorkDirPath();
        // if (curDir.exists()) {
        WorkingDirectoryChooser = new javax.swing.JFileChooser(curDir);
        // } else {
        // WorkingDirectoryChooser = new javax.swing.JFileChooser();
        // }

        WorkingDirectoryChooser.setDialogTitle(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Choose_Working_Directory"));
        WorkingDirectoryChooser
                .setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        if (WorkingDirectoryChooser.showDialog(this, java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Set")) == JFileChooser.APPROVE_OPTION) {
            try {
                c.setStringOpt(AppSettings.OUTPUTDIRPATH, WorkingDirectoryChooser.getSelectedFile()
                                        .getCanonicalPath());
            } catch (Exception e) {
                Generic.debug(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("WorkingDirChooser"), e);
            }
        }
    }// GEN-LAST:event_btWorkingDirectoryActionPerformed

    private int selectedFormat = Model.NO_LOG_LOGTYPE;

    private final void setSelectedFormat(final String selected) {
        if (selected.startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("CSV"))) {
            selectedFormat = Model.CSV_LOGTYPE;
        } else if (selected.startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Google_Map"))) {
            selectedFormat = Model.GMAP_LOGTYPE;
        } else if (selected.startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("GPX"))) {
            selectedFormat = Model.GPX_LOGTYPE;
        } else if (selected.startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("KML"))) {
            selectedFormat = Model.KML_LOGTYPE;
        } else if (selected.startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("NMEA"))) {
            selectedFormat = Model.NMEA_LOGTYPE;
        } else if (selected.startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Ozi"))) {
            selectedFormat = Model.PLT_LOGTYPE;
        } else if (selected.startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Compe"))) {
            selectedFormat = Model.TRK_LOGTYPE;
        } else {
            selectedFormat = Model.NO_LOG_LOGTYPE;
        }
    }

    private void updateSatGuiItems() {
        boolean enable;
        enable = cbSID.isSelected();
        cbSNR.setEnabled(enable);
        cbAzimuth.setEnabled(enable);
        cbElevation.setEnabled(enable);

    }

    private void updateLogFormatData() {
        int logFormat = m.getLogFormat();

        cbUTCTime
                .setSelected((logFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0);
        cbFixType
                .setSelected((logFormat & (1 << BT747Constants.FMT_VALID_IDX)) != 0);
        cbLat
                .setSelected((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0);
        cbLong
                .setSelected((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0);
        cbHeight
                .setSelected((logFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0);
        cbSpeed
                .setSelected((logFormat & (1 << BT747Constants.FMT_SPEED_IDX)) != 0);
        cbHeading
                .setSelected((logFormat & (1 << BT747Constants.FMT_HEADING_IDX)) != 0);
        cbDSTA
                .setSelected((logFormat & (1 << BT747Constants.FMT_DSTA_IDX)) != 0);
        cbDAGE
                .setSelected((logFormat & (1 << BT747Constants.FMT_DAGE_IDX)) != 0);
        cbPDOP
                .setSelected((logFormat & (1 << BT747Constants.FMT_PDOP_IDX)) != 0);
        cbHDOP
                .setSelected((logFormat & (1 << BT747Constants.FMT_HDOP_IDX)) != 0);
        cbVDOP
                .setSelected((logFormat & (1 << BT747Constants.FMT_VDOP_IDX)) != 0);
        cbNSAT
                .setSelected((logFormat & (1 << BT747Constants.FMT_NSAT_IDX)) != 0);
        cbSID.setSelected((logFormat & (1 << BT747Constants.FMT_SID_IDX)) != 0);
        cbElevation
                .setSelected((logFormat & (1 << BT747Constants.FMT_ELEVATION_IDX)) != 0);
        cbAzimuth
                .setSelected((logFormat & (1 << BT747Constants.FMT_AZIMUTH_IDX)) != 0);
        cbSNR.setSelected((logFormat & (1 << BT747Constants.FMT_SNR_IDX)) != 0);
        cbRCR.setSelected((logFormat & (1 << BT747Constants.FMT_RCR_IDX)) != 0);
        cbMilliSeconds
                .setSelected((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) != 0);
        cbDistance
                .setSelected((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) != 0);
        cbValidFixOnly
                .setSelected((logFormat & (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX)) != 0);

    }

    private void updateEstimatedNbrRecords() {
        txtEstimatedRecords.setText(m
                .getEstimatedNbrRecords(getUserLogFormat())
                + " records estimated");
    }

    private int getUserLogFormat() {
        int logFormat = 0;

        if (cbUTCTime.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
        }
        if (cbFixType.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_VALID_IDX);
        }
        if (cbLat.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);
        }
        if (cbLong.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
        }
        if (cbHeight.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_HEIGHT_IDX);
        }
        if (cbSpeed.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_SPEED_IDX);
        }
        if (cbHeading.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_HEADING_IDX);
        }
        if (cbDSTA.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_DSTA_IDX);
        }
        if (cbDAGE.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_DAGE_IDX);
        }
        if (cbPDOP.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_PDOP_IDX);
        }
        if (cbHDOP.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_HDOP_IDX);
        }
        if (cbVDOP.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_VDOP_IDX);
        }
        if (cbNSAT.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_NSAT_IDX);
        }
        if (cbSID.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_SID_IDX);
        }
        if (cbElevation.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_ELEVATION_IDX);
        }
        if (cbAzimuth.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_AZIMUTH_IDX);
        }
        if (cbSNR.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_SNR_IDX);
        }
        if (cbRCR.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_RCR_IDX);
        }
        if (cbMilliSeconds.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_MILLISECOND_IDX);
        }
        if (cbDistance.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_DISTANCE_IDX);
        }
        if (cbValidFixOnly.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX);
        }
        return logFormat;
    }

    private void updateFileFormatData() {
        int logFormat = m.getIntOpt(Model.FILEFIELDFORMAT);

        cbFileUTCTime
                .setSelected((logFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0);
        cbFileFixType
                .setSelected((logFormat & (1 << BT747Constants.FMT_VALID_IDX)) != 0);
        cbFileLat
                .setSelected((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0);
        cbFileLong
                .setSelected((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0);
        cbFileHeight
                .setSelected((logFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0);
        cbFileSpeed
                .setSelected((logFormat & (1 << BT747Constants.FMT_SPEED_IDX)) != 0);
        cbFileHeading
                .setSelected((logFormat & (1 << BT747Constants.FMT_HEADING_IDX)) != 0);
        cbFileDSTA
                .setSelected((logFormat & (1 << BT747Constants.FMT_DSTA_IDX)) != 0);
        cbFileDAGE
                .setSelected((logFormat & (1 << BT747Constants.FMT_DAGE_IDX)) != 0);
        cbFilePDOP
                .setSelected((logFormat & (1 << BT747Constants.FMT_PDOP_IDX)) != 0);
        cbFileHDOP
                .setSelected((logFormat & (1 << BT747Constants.FMT_HDOP_IDX)) != 0);
        cbFileVDOP
                .setSelected((logFormat & (1 << BT747Constants.FMT_VDOP_IDX)) != 0);
        cbFileNSAT
                .setSelected((logFormat & (1 << BT747Constants.FMT_NSAT_IDX)) != 0);
        cbFileSID
                .setSelected((logFormat & (1 << BT747Constants.FMT_SID_IDX)) != 0);
        cbFileElevation
                .setSelected((logFormat & (1 << BT747Constants.FMT_ELEVATION_IDX)) != 0);
        cbFileAzimuth
                .setSelected((logFormat & (1 << BT747Constants.FMT_AZIMUTH_IDX)) != 0);
        cbFileSNR
                .setSelected((logFormat & (1 << BT747Constants.FMT_SNR_IDX)) != 0);
        cbFileRCR
                .setSelected((logFormat & (1 << BT747Constants.FMT_RCR_IDX)) != 0);
        cbFileMilliSeconds
                .setSelected((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) != 0);
        cbFileDistance
                .setSelected((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) != 0);
    }

    private void setFileOutputFields() {
        c.setIntOpt(Model.FILEFIELDFORMAT, getFileLogFormat());
    }

    private int getFileLogFormat() {
        int logFormat = 0;

        if (cbFileUTCTime.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
        }
        if (cbFileFixType.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_VALID_IDX);
        }
        if (cbFileLat.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);
        }
        if (cbFileLong.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
        }
        if (cbFileHeight.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_HEIGHT_IDX);
        }
        if (cbFileSpeed.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_SPEED_IDX);
        }
        if (cbFileHeading.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_HEADING_IDX);
        }
        if (cbFileDSTA.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_DSTA_IDX);
        }
        if (cbFileDAGE.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_DAGE_IDX);
        }
        if (cbFilePDOP.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_PDOP_IDX);
        }
        if (cbFileHDOP.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_HDOP_IDX);
        }
        if (cbFileVDOP.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_VDOP_IDX);
        }
        if (cbFileNSAT.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_NSAT_IDX);
        }
        if (cbFileSID.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_SID_IDX);
        }
        if (cbFileElevation.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_ELEVATION_IDX);
        }
        if (cbFileAzimuth.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_AZIMUTH_IDX);
        }
        if (cbFileSNR.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_SNR_IDX);
        }
        if (cbFileRCR.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_RCR_IDX);
        }
        if (cbFileMilliSeconds.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_MILLISECOND_IDX);
        }
        if (cbFileDistance.isSelected()) {
            logFormat |= (1 << BT747Constants.FMT_DISTANCE_IDX);
        }
        return logFormat;
    }

    private void progressBarUpdate() {
        DownloadProgressBar.setVisible(m.isDownloadOnGoing());
        DownloadProgressLabel.setVisible(m.isDownloadOnGoing());
        if (m.isDownloadOnGoing()) {
            DownloadProgressBar.setMinimum(m.getStartAddr());
            DownloadProgressBar.setMaximum(m.getEndAddr());
            DownloadProgressBar.setValue(m.getNextReadAddr());
        }
        // this.invalidate();
        // this.paintAll(this.getGraphics());
    }

    private File getOutputFilePath() {
        File curDir;
        curDir = new File(m.getReportFileBasePath());
        tfOutputFileBaseName
                .setText(m.getStringOpt(AppSettings.REPORTFILEBASE));
        return curDir;
    }

    private File getRawLogFilePath() {
        File curDir;
        curDir = new File(m.getStringOpt(AppSettings.LOGFILEPATH));
        tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
        return curDir;
    }

    private File getWorkDirPath() {
        File curDir;
        curDir = new File(m.getStringOpt(AppSettings.OUTPUTDIRPATH));
        tfWorkDirectory.setText(curDir.getPath());
        return curDir;
    }

    private void getIncremental() {
        cbIncremental.setSelected(m.isIncremental());
    }

    private void openPort(String s) {
        boolean foundPort = false;
        int port = 0;
        try {
            port = Integer.parseInt(s);
            if (String.valueOf(port).equals(s)) {
                foundPort = true;
            }
        } catch (Exception e) {
            // Ignore exception
        }
        try {
            if (!foundPort && s.toUpperCase().startsWith("COM")) {
                if (s.length() == 5 && s.charAt(4) == ':') {
                    port = Integer.parseInt(s.substring(3, 4));
                } else if (s.length() == 6 && s.charAt(5) == ':') {
                    port = Integer.parseInt(s.substring(3, 5));
                } else {
                    port = Integer.parseInt(s.substring(3));
                }
                if (s.toUpperCase().equals("COM" + port)
                        || s.toUpperCase().equals("COM" + port + ":")) {
                }
                {
                    foundPort = true;
                }
            }
        } catch (Exception e) {
            // Ignore exception
        }
        try {
            c.setBaudRate(Integer.parseInt((String)cbSerialSpeed.getSelectedItem()));
        } catch (Exception e) {
            
        }
        if (foundPort) {
            c.setPort(port);
        } else {
            if (s.toUpperCase().startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("BLUETOOTH"))) {
                c.setBluetooth();
            } else if (s.toUpperCase().startsWith(java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("USB"))) {
                c.setUsb();
            } else {
                c.openFreeTextPort(s);
            }
        }
    }

    private void getDefaultPort() {
        if (m.getFreeTextPort().length() != 0) {
            cbPortName.setSelectedItem(m.getFreeTextPort());
        } else if (m.getPortnbr() >= 0) {
            cbPortName.setSelectedItem("COM" + m.getPortnbr() + ":"); // NOI18N

        } else {
            // Do nothing
        }
    }

    /***************************************************************************
     * Find the appropriate look and feel for the system
     **************************************************************************/
    private static final String[] lookAndFeels = {
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", // NOI18N
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel", // NOI18N
            "com.sun.java.swing.plaf.gtk.GTKLookAndFeel", // NOI18N
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel", // NOI18N
            "javax.swing.plaf.metal.MetalLookAndFeel", // NOI18N
            "javax.swing.plaf.mac.MacLookAndFeel", // NOI18N
            "com.apple.mrj.swing.MacLookAndFeel", // NOI18N
            "apple.laf.AquaLookAndFeel" // NOI18N
            }; // NOI18N
    /* Index for Mac look and feel */
    private static final int C_MAC_LOOKANDFEEL_IDX = lookAndFeels.length - 3;
    private static String lookAndFeel=""; // NOI18N
    private static String lookAndFeelMsg=""; // NOI18N

    /**
     * Try setting a look and feel for the system - catch the Exception when not
     * found.
     * 
     * @return true if successfull
     */
    private final static boolean tryLookAndFeel(String s) {
        try {
            UIManager.setLookAndFeel(s);
            lookAndFeel = s;
            lookAndFeelMsg += java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Success_") + s + "\n";
            return true;
        } catch (Exception e) {
        }
        lookAndFeelMsg += java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle").getString("Fail_") + s + "\n";
        return false;
    }

    /**
     * Set a good look and feel for the system.
     */
    public static void myLookAndFeel() {
        boolean lookAndFeelIsSet = false;
        if (java.lang.System.getProperty("os.name").toLowerCase().startsWith(
                "mac")) { // NOI18N
            for (int i = C_MAC_LOOKANDFEEL_IDX; !lookAndFeelIsSet && (i < lookAndFeels.length); i++) {
                lookAndFeelIsSet = tryLookAndFeel(lookAndFeels[i]);
            }
        }
        for (int i = 0; !lookAndFeelIsSet && (i < lookAndFeels.length); i++) {
            lookAndFeelIsSet = tryLookAndFeel(lookAndFeels[i]);
        }
        if (!lookAndFeelIsSet) {
            tryLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(final String args[]) {
        myLookAndFeel();
        java.awt.EventQueue.invokeLater(new Runnable() {

            final Model m = new Model();
            final J2SEAppController c = new J2SEAppController(m);

            public final void run() {
                final BT747Main app = new BT747Main(m, c);
                app.setVisible(true);
            }
        });
    }

    private void cbNMEAOutGLLActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbNMEAOutGLLActionPerformed
        // TODO Remove
    }// GEN-LAST:event_cbNMEAOutGLLActionPerformed

    private void AdvancedfileSettingsPanelFocusGained( java.awt.event.FocusEvent evt) {// GEN-FIRST:event_AdvancedfileSettingsPanelFocusGained
        // Remove
    }// GEN-LAST:event_AdvancedfileSettingsPanelFocusGained

    private void tfWorkDirectoryFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_tfWorkDirectoryFocusLost
        c.setStringOpt(AppSettings.OUTPUTDIRPATH, new File(tfWorkDirectory.getText()).getPath());
    }// GEN-LAST:event_tfWorkDirectoryFocusLost

    private void cbFileUTCTimeupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileUTCTimeupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileUTCTimeupdateLogRecordEstCount

    private void cbFileMilliSecondsupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileMilliSecondsupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileMilliSecondsupdateLogRecordEstCount

    private void cbFileLatupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileLatupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileLatupdateLogRecordEstCount

    private void cbFileLongupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileLongupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileLongupdateLogRecordEstCount

    private void cbFileHeightupdateLogRecordEstCount(
            java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileHeightupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileHeightupdateLogRecordEstCount

    private void cbFileSpeedupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileSpeedupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileSpeedupdateLogRecordEstCount

    private void cbFileHeadingupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileHeadingupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileHeadingupdateLogRecordEstCount

    private void cbFileDistanceupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileDistanceupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileDistanceupdateLogRecordEstCount

    private void cbFileDSTAupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileDSTAupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileDSTAupdateLogRecordEstCount

    private void cbFileDAGEupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFileDAGEupdateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFileDAGEupdateLogRecordEstCount

    private void cbFileHDOPupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbPDOP1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbPDOP1updateLogRecordEstCount

    private void cbFilePDOPupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbHDOP1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbHDOP1updateLogRecordEstCount

    private void cbFileVDOPupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbVDOP1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbVDOP1updateLogRecordEstCount

    private void cbFileFixTypeupdateLogRecordEstCount(
            java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFixType1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbFixType1updateLogRecordEstCount

    private void cbFileNSATupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbNSAT1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbNSAT1updateLogRecordEstCount

    private void cbFileSIDItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbSID1ItemStateChanged
        setFileOutputFields();
    }// GEN-LAST:event_cbSID1ItemStateChanged

    private void cbFileElevationupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbElevation1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbElevation1updateLogRecordEstCount

    private void cbFileAzimuthupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbAzimuth1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbAzimuth1updateLogRecordEstCount

    private void cbFileSNRupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbSNR1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbSNR1updateLogRecordEstCount

    private void cbFileRCRupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbRCR1updateLogRecordEstCount
        setFileOutputFields();
    }// GEN-LAST:event_cbRCR1updateLogRecordEstCount

    private void sfTimeSplitHoursFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_sfTimeSplitHoursFocusLost
    // TODO add your handling code here:
    }// GEN-LAST:event_sfTimeSplitHoursFocusLost

    private void spTimeSplitMinutesFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_spTimeSplitMinutesFocusLost
    // TODO add your handling code here:
    }// GEN-LAST:event_spTimeSplitMinutesFocusLost

    private void tfOutputFileBaseNameFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_tfOutputFileBaseNameFocusLost
        c.setOutputFileRelPath(tfOutputFileBaseName.getText());
    }// GEN-LAST:event_tfOutputFileBaseNameFocusLost

    // public static void main(String args) {
    // main((String[])null);
    // }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutBT747;
    private javax.swing.JPanel AdvancedSettingsPanel;
    private javax.swing.JPanel AdvancedfileSettingsPanel;
    private javax.swing.JPanel DeviceSettingsPanel;
    private javax.swing.JProgressBar DownloadProgressBar;
    private javax.swing.JLabel DownloadProgressLabel;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JPanel FileSettingsPanel;
    private javax.swing.JPanel GPSDecodePanel;
    private javax.swing.JMenuItem Info;
    private javax.swing.JMenu InfoMenu;
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JPanel LogFiltersPanel;
    private javax.swing.JPanel LogOperationsPanel;
    private javax.swing.JMenu SettingsMenu;
    private javax.swing.JButton btApplySBAS;
    private javax.swing.JButton btColdStart;
    private javax.swing.JButton btConnect;
    private javax.swing.JButton btConvert;
    private javax.swing.JButton btDownloadFromNumerix;
    private javax.swing.JButton btDownloadIBlue;
    private javax.swing.JButton btErase;
    private javax.swing.JButton btFactoryResetDevice;
    private javax.swing.JButton btFormat;
    private javax.swing.JButton btFormatAndErase;
    private javax.swing.JRadioButtonMenuItem btGPSConnectDebug;
    private javax.swing.JRadioButtonMenuItem btGPSDebug;
    private javax.swing.JButton btHotStart;
    private javax.swing.JButton btLogByApply;
    private javax.swing.JButton btOutputFile;
    private javax.swing.JButton btRawLogFile;
    private javax.swing.JButton btRecoverMemory;
    private javax.swing.JButton btSetFlash;
    private javax.swing.JButton btSetHoluxName;
    private javax.swing.JButton btSetNMEAFileOutput;
    private javax.swing.JButton btSetNMEAOutput;
    private javax.swing.JButton btSetNMEAOutputDefaults;
    private javax.swing.JButton btWarmStart;
    private javax.swing.JButton btWorkingDirectory;
    private javax.swing.JCheckBox cbAdvancedActive;
    private javax.swing.JCheckBox cbAzimuth;
    private javax.swing.JCheckBox cbDAGE;
    private javax.swing.JComboBox cbDGPSType;
    private javax.swing.JCheckBox cbDSTA;
    private javax.swing.JComboBox cbDecoderChoice;
    private javax.swing.JCheckBox cbDisableLoggingDuringDownload;
    private javax.swing.JCheckBox cbDistance;
    private javax.swing.JCheckBox cbElevation;
    private javax.swing.JCheckBox cbFileAzimuth;
    private javax.swing.JCheckBox cbFileDAGE;
    private javax.swing.JCheckBox cbFileDSTA;
    private javax.swing.JCheckBox cbFileDistance;
    private javax.swing.JCheckBox cbFileElevation;
    private javax.swing.JCheckBox cbFileFixType;
    private javax.swing.JCheckBox cbFileHDOP;
    private javax.swing.JCheckBox cbFileHeading;
    private javax.swing.JCheckBox cbFileHeight;
    private javax.swing.JCheckBox cbFileLat;
    private javax.swing.JCheckBox cbFileLong;
    private javax.swing.JCheckBox cbFileMilliSeconds;
    private javax.swing.JCheckBox cbFileNSAT;
    private javax.swing.JCheckBox cbFilePDOP;
    private javax.swing.JCheckBox cbFileRCR;
    private javax.swing.JCheckBox cbFileSID;
    private javax.swing.JCheckBox cbFileSNR;
    private javax.swing.JCheckBox cbFileSpeed;
    private javax.swing.JCheckBox cbFileUTCTime;
    private javax.swing.JCheckBox cbFileVDOP;
    private javax.swing.JCheckBox cbFixType;
    private javax.swing.JComboBox cbFlashGGA;
    private javax.swing.JComboBox cbFlashGLL;
    private javax.swing.JComboBox cbFlashGSA;
    private javax.swing.JComboBox cbFlashGSV;
    private javax.swing.JComboBox cbFlashMCHN;
    private javax.swing.JComboBox cbFlashRMC;
    private javax.swing.JComboBox cbFlashVTG;
    private javax.swing.JComboBox cbFlashZDA;
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JLabel cbGGAOut;
    private javax.swing.JLabel cbGGAOut1;
    private javax.swing.JComboBox cbGPSType;
    private javax.swing.JCheckBox cbGPXTrkSegWhenSmall;
    private javax.swing.JLabel cbGRSOut;
    private javax.swing.JLabel cbGSAOut;
    private javax.swing.JLabel cbGSAOut1;
    private javax.swing.JLabel cbGSTOut;
    private javax.swing.JLabel cbGSVOut;
    private javax.swing.JLabel cbGSVOut1;
    private javax.swing.JButton cbGoodFixColor;
    private javax.swing.JCheckBox cbHDOP;
    private javax.swing.JCheckBox cbHeading;
    private javax.swing.JCheckBox cbHeight;
    private javax.swing.JComboBox cbHeightOverMeanSeaLevel;
    private javax.swing.JCheckBox cbImperialUnits;
    private javax.swing.JCheckBox cbIncludeTestSBAS;
    private javax.swing.JCheckBox cbIncremental;
    private javax.swing.JCheckBox cbLat;
    private javax.swing.JCheckBox cbLong;
    private javax.swing.JCheckBox cbMilliSeconds;
    private javax.swing.JComboBox cbNMEAOutGGA;
    private javax.swing.JComboBox cbNMEAOutGLL;
    private javax.swing.JComboBox cbNMEAOutGRS;
    private javax.swing.JComboBox cbNMEAOutGSA;
    private javax.swing.JComboBox cbNMEAOutGST;
    private javax.swing.JComboBox cbNMEAOutGSV;
    private javax.swing.JComboBox cbNMEAOutMALM;
    private javax.swing.JComboBox cbNMEAOutMCHN;
    private javax.swing.JComboBox cbNMEAOutMDBG;
    private javax.swing.JComboBox cbNMEAOutMDGP;
    private javax.swing.JComboBox cbNMEAOutMEPH;
    private javax.swing.JComboBox cbNMEAOutRMC;
    private javax.swing.JComboBox cbNMEAOutType10;
    private javax.swing.JComboBox cbNMEAOutType11;
    private javax.swing.JComboBox cbNMEAOutType12;
    private javax.swing.JComboBox cbNMEAOutType8;
    private javax.swing.JComboBox cbNMEAOutType9;
    private javax.swing.JComboBox cbNMEAOutVTG;
    private javax.swing.JComboBox cbNMEAOutZDA;
    private javax.swing.JCheckBox cbNSAT;
    private javax.swing.JButton cbNoFixColor;
    private javax.swing.JCheckBox cbNotApplyUTCOffset;
    private javax.swing.JComboBox cbOneFilePerDay;
    private javax.swing.JPanel cbOtherFormat;
    private javax.swing.JCheckBox cbPDOP;
    private javax.swing.JComboBox cbPortName;
    private javax.swing.JCheckBox cbRCR;
    private javax.swing.JCheckBox cbRecordNumberInfoInLog;
    private javax.swing.JCheckBox cbSID;
    private javax.swing.JCheckBox cbSNR;
    private javax.swing.JComboBox cbSerialSpeed;
    private javax.swing.JCheckBox cbSpeed;
    private javax.swing.JComboBox cbStandardOrDaylightSaving;
    private javax.swing.JComboBox cbStopOrOverwriteWhenFull;
    private javax.swing.JCheckBox cbTrkButton;
    private javax.swing.JCheckBox cbTrkDGPS;
    private javax.swing.JCheckBox cbTrkDistance;
    private javax.swing.JCheckBox cbTrkEstimate;
    private javax.swing.JCheckBox cbTrkFRTK;
    private javax.swing.JCheckBox cbTrkManual;
    private javax.swing.JCheckBox cbTrkNoFix;
    private javax.swing.JCheckBox cbTrkPPS;
    private javax.swing.JCheckBox cbTrkRTK;
    private javax.swing.JCheckBox cbTrkSPS;
    private javax.swing.JCheckBox cbTrkSimulate;
    private javax.swing.JCheckBox cbTrkSpeed;
    private javax.swing.JCheckBox cbTrkTime;
    private javax.swing.JCheckBox cbTrkUser1;
    private javax.swing.JCheckBox cbTrkUser10;
    private javax.swing.JCheckBox cbTrkUser11;
    private javax.swing.JCheckBox cbTrkUser12;
    private javax.swing.JCheckBox cbTrkUser2;
    private javax.swing.JCheckBox cbTrkUser3;
    private javax.swing.JCheckBox cbTrkUser4;
    private javax.swing.JCheckBox cbTrkUser5;
    private javax.swing.JCheckBox cbTrkUser6;
    private javax.swing.JCheckBox cbTrkUser7;
    private javax.swing.JCheckBox cbTrkUser8;
    private javax.swing.JCheckBox cbTrkUser9;
    private javax.swing.JLabel cbType8Out;
    private javax.swing.JLabel cbType9Out;
    private javax.swing.JComboBox cbUTCOffset;
    private javax.swing.JCheckBox cbUTCTime;
    private javax.swing.JCheckBox cbUseSBAS;
    private javax.swing.JCheckBox cbVDOP;
    private javax.swing.JLabel cbVTGOut1;
    private javax.swing.JCheckBox cbValidFixOnly;
    private javax.swing.JCheckBox cbWayButton;
    private javax.swing.JCheckBox cbWayDGPS;
    private javax.swing.JCheckBox cbWayDistance;
    private javax.swing.JCheckBox cbWayEstimate;
    private javax.swing.JCheckBox cbWayFRTK;
    private javax.swing.JCheckBox cbWayManual;
    private javax.swing.JCheckBox cbWayNoFix;
    private javax.swing.JCheckBox cbWayPPS;
    private javax.swing.JCheckBox cbWayRTK;
    private javax.swing.JCheckBox cbWaySPS;
    private javax.swing.JCheckBox cbWaySimulate;
    private javax.swing.JCheckBox cbWaySpeed;
    private javax.swing.JCheckBox cbWayTime;
    private javax.swing.JCheckBox cbWayUser1;
    private javax.swing.JCheckBox cbWayUser10;
    private javax.swing.JCheckBox cbWayUser11;
    private javax.swing.JCheckBox cbWayUser12;
    private javax.swing.JCheckBox cbWayUser2;
    private javax.swing.JCheckBox cbWayUser3;
    private javax.swing.JCheckBox cbWayUser4;
    private javax.swing.JCheckBox cbWayUser5;
    private javax.swing.JCheckBox cbWayUser6;
    private javax.swing.JCheckBox cbWayUser7;
    private javax.swing.JCheckBox cbWayUser8;
    private javax.swing.JCheckBox cbWayUser9;
    private javax.swing.JCheckBox ckLogDistanceActive;
    private javax.swing.JCheckBox ckLogSpeedActive;
    private javax.swing.JCheckBox ckLogTimeActive;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel lbConversionTime;
    private javax.swing.JLabel lbDistanceFltr;
    private javax.swing.JLabel lbFirmWare;
    private javax.swing.JLabel lbFlashInfo;
    private javax.swing.JLabel lbFlashZDA;
    private javax.swing.JLabel lbGLLOut;
    private javax.swing.JLabel lbGLLOut1;
    private javax.swing.JLabel lbGeoid;
    private javax.swing.JLabel lbLatitude;
    private javax.swing.JLabel lbLoggerSWVersion;
    private javax.swing.JLabel lbLongitude;
    private javax.swing.JLabel lbMemoryUsed;
    private javax.swing.JLabel lbModel;
    private javax.swing.JCheckBox lbNMEAFileGGA;
    private javax.swing.JCheckBox lbNMEAFileGLL;
    private javax.swing.JCheckBox lbNMEAFileGRS;
    private javax.swing.JCheckBox lbNMEAFileGSA;
    private javax.swing.JCheckBox lbNMEAFileGST;
    private javax.swing.JCheckBox lbNMEAFileGSV;
    private javax.swing.JCheckBox lbNMEAFileMALM;
    private javax.swing.JCheckBox lbNMEAFileMCHN;
    private javax.swing.JCheckBox lbNMEAFileMDBG;
    private javax.swing.JCheckBox lbNMEAFileMDGP;
    private javax.swing.JCheckBox lbNMEAFileMEPH;
    private javax.swing.JCheckBox lbNMEAFileRMC;
    private javax.swing.JCheckBox lbNMEAFileType10;
    private javax.swing.JCheckBox lbNMEAFileType11;
    private javax.swing.JCheckBox lbNMEAFileType12;
    private javax.swing.JCheckBox lbNMEAFileType8;
    private javax.swing.JCheckBox lbNMEAFileType9;
    private javax.swing.JCheckBox lbNMEAFileVTG;
    private javax.swing.JCheckBox lbNMEAFileZDA;
    private javax.swing.JLabel lbNMEAOutType10;
    private javax.swing.JLabel lbNMEAOutZDA;
    private javax.swing.JLabel lbNSATFltr;
    private javax.swing.JLabel lbNewTrackAfter;
    private javax.swing.JLabel lbRMCOut;
    private javax.swing.JLabel lbRMCOut1;
    private javax.swing.JLabel lbRecNbrFltr;
    private javax.swing.JLabel lbSerialSpeed;
    private javax.swing.JLabel lbSpeedFltr;
    private javax.swing.JLabel lbTime;
    private javax.swing.JLabel lbTimeZone;
    private javax.swing.JLabel lbVTGOut;
    private javax.swing.JPanel pnBottomInformation;
    private javax.swing.JPanel pnCommonFilter;
    private javax.swing.JPanel pnConvert;
    private javax.swing.JPanel pnDownload;
    private javax.swing.JPanel pnFileNMEAOutLeft;
    private javax.swing.JPanel pnFileNMEAOutRight;
    private javax.swing.JPanel pnFileNMEAOutput;
    private javax.swing.JPanel pnFileOutputFieldInner;
    private javax.swing.JPanel pnFileOutputFields;
    private javax.swing.JPanel pnFilePosition;
    private javax.swing.JPanel pnFilePrecision;
    private javax.swing.JPanel pnFileReason;
    private javax.swing.JPanel pnFileSatInfo;
    private javax.swing.JPanel pnFileTime;
    private javax.swing.JPanel pnFiles;
    private javax.swing.JPanel pnFilterOther;
    private javax.swing.JPanel pnFilterPrecision;
    private javax.swing.JPanel pnFlashSettings;
    private javax.swing.JPanel pnGPSStart;
    private javax.swing.JPanel pnGPXFileSettings;
    private javax.swing.JPanel pnHoluxSettings;
    private javax.swing.JPanel pnLogBy;
    private javax.swing.JPanel pnLogFormat;
    private javax.swing.JPanel pnNMEAOutput;
    private javax.swing.JPanel pnPosition;
    private javax.swing.JPanel pnPrecision;
    private javax.swing.JPanel pnReason;
    private javax.swing.JPanel pnSBAS;
    private javax.swing.JPanel pnSatInfo;
    private javax.swing.JPanel pnSeparation;
    private javax.swing.JPanel pnTime;
    private javax.swing.JPanel pnTrackpoint;
    private javax.swing.JPanel pnVarious;
    private javax.swing.JPanel pnWayPointFix;
    private javax.swing.JPanel pnWayPointRCR;
    private javax.swing.JPanel pnWaypoint;
    private com.toedter.components.JSpinField sfTimeSplitHours;
    private com.toedter.components.JSpinField spTimeSplitMinutes;
    private com.toedter.calendar.JDateChooser startDate;
    private javax.swing.JTabbedPane tabbedPanelAll;
    private javax.swing.JTextField tfOutputFileBaseName;
    private javax.swing.JTextField tfRawLogFilePath;
    private javax.swing.JTextField tfTrackSeparationTime;
    private javax.swing.JTextField tfWorkDirectory;
    private javax.swing.JTextField txtDistanceMax;
    private javax.swing.JTextField txtDistanceMin;
    private javax.swing.JLabel txtEstimatedRecords;
    private javax.swing.JTextField txtFlashBaudRate;
    private javax.swing.JTextField txtFlashTimesLeft;
    private javax.swing.JTextField txtFlashUpdateRate;
    private javax.swing.JTextField txtHDOPMax;
    private javax.swing.JTextField txtHoluxName;
    private javax.swing.JTextField txtLogDistanceInterval;
    private javax.swing.JTextField txtLogSpeedInterval;
    private javax.swing.JTextField txtLogTimeInterval;
    private javax.swing.JLabel txtMemoryUsed;
    private javax.swing.JTextField txtNSATMin;
    private javax.swing.JTextField txtPDOPMax;
    private javax.swing.JTextField txtRecCntMax;
    private javax.swing.JTextField txtRecCntMin;
    private javax.swing.JTextField txtSpeedMax;
    private javax.swing.JTextField txtSpeedMin;
    private javax.swing.JTextField txtVDOPMax;
    // End of variables declaration//GEN-END:variables

}
