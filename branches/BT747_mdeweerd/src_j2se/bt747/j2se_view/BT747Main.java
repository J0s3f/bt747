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
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package bt747.j2se_view;

import gnu.io.CommPortIdentifier;
import gps.BT747Constants;
import gps.convert.Conv;
import gps.log.GPSRecord;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.ComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
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
        
    // TODO: remove this part after updating GUI.  Handled in updateGui.
    private static final ComboBoxModel modelGpsType = new javax.swing.DefaultComboBoxModel(new String[] {
                ""
                 });
    
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

    
    private final String getString(final String s) {
        return J2SEAppController.getString(s);
    }
    
    private void updateGuiData() {
        cbGPSType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("DEFAULT_DEVICE"), 
                "Holux M-241",
                "iTrackU-Nemerix",
                "PhotoTrackr",
                "iTrackU-SIRFIII" }));
        cbPortName.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("USB_ForLinuxMac"),
                getString("BLUETOOTH_ForMac"),
                "COM1:",  // NOI18N
                "COM2:", "COM3:", "COM4:", "COM5:", "COM6:", "COM7:", "COM8:",  // NOI18N
                "COM9:", "COM10:", "COM11:", "COM12:", "COM13:", "COM14:",  // NOI18N
                "COM15:", "COM16:" }));  // NOI18N
        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("GPX_Description"),
                getString("CSV_Description"),
                getString("Compe_Description"),
                getString("KML_Description"),
                getString("OZI_Description"),
                getString("NMEA_Description"),
                getString("GMAP_Description"),
                getString("KMZ_Description") }));
        cbHeightOverMeanSeaLevel.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { getString("Keep_Height"), getString("WGS84_to_MSL"), getString("Automatic"),
                        getString("MSL_to_WGS84") }));
        cbStandardOrDaylightSaving
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                        getString("Standard_Time"), getString("Daylight_Savings_Time") }));
        cbOneFilePerDay.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { getString("One_file_per_day"), getString("One_file_per_track"),
                        getString("Everything_in_one_file") }));
    }
    
    private AdvancedDeviceSettingsPanel pnAdvancedSettingsPanel;
    private DeviceSettingsPanel pnDeviceSettingsPanel;
    private FiltersPanel pnFiltersPanel;
    /**
     * Initialize application data. Gets the values from the model to set them
     * in the GUI.
     */
    private void initAppData() {
        c.setRootFrame(this);

        pnFiltersPanel = new FiltersPanel();
        pnFiltersPanel.init(c);
        //tabbedPanelAll.addTab(getString("BT747Main.AdvancedSettingsPanel.TabConstraints.tabTitle"), pnFiltersPanel); // NOI18N
        tabbedPanelAll.insertTab(getString("BT747Main.LogFiltersPanel.TabConstraints.tabTitle"),
                null, pnFiltersPanel, null, 2);
        
        
        pnDeviceSettingsPanel = new DeviceSettingsPanel();
        pnDeviceSettingsPanel.init(c);
        //tabbedPanelAll.addTab(getString("BT747Main.AdvancedSettingsPanel.TabConstraints.tabTitle"), pnDeviceSettingsPanel); // NOI18N
        tabbedPanelAll.insertTab(getString("BT747Main.DeviceSettingsPanel.TabConstraints.tabTitle"),
                null, pnDeviceSettingsPanel, null, 3);

        pnAdvancedSettingsPanel = new AdvancedDeviceSettingsPanel();
        pnAdvancedSettingsPanel.init(c);
        //tabbedPanelAll.addTab(getString("BT747Main.AdvancedSettingsPanel.TabConstraints.tabTitle"), pnAdvancedSettingsPanel); // NOI18N
        tabbedPanelAll.insertTab(getString("BT747Main.AdvancedSettingsPanel.TabConstraints.tabTitle"),
                null, pnAdvancedSettingsPanel, null, 4);
        
        this.pack();
        
        updateGuiData(); // For internationalisation - not so easy in netbeans
        infoTextArea.setEnabled(true);
        infoTextArea.setFocusable(true);
        infoTextArea.append(java.lang.System.getProperty("os.name")); // NOI18N
        infoTextArea.append("\n"); // NOI18N
        infoTextArea.append(java.lang.System.getProperty("os.arch")); // NOI18N
        infoTextArea.append("\n"); // NOI18N
        infoTextArea.append(java.lang.System.getProperty("os.version")); // NOI18N
        infoTextArea.append("\n"); // NOI18N
        infoTextArea.append(java.lang.System.getProperty("java.version")); // NOI18N
        infoTextArea.append("\n"); // NOI18N
        infoTextArea.append(J2SEAppController.lookAndFeelMsg);
        LookAndFeelInfo[] a =UIManager.getInstalledLookAndFeels();
        for(int i=0;i<a.length;i++) {
            infoTextArea.append(a[i].getClassName()+"\n"); // NOI18N
        }
        progressBarUpdate();
        getWorkDirPath();
        getRawLogFilePath();
        getOutputFilePath();
        getIncremental();
        getDefaultPort();
        updateSerialSpeed();
        
        cbLanguage.setEditable(true);
        cbLanguage.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "","en", "fr", "nl" }));
        cbLanguage.setSelectedItem(m.getStringOpt(Model.LANGUAGE));
        // TODO: need to get rid of ',' generated by format.

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
            Generic.debug(getString("Problem_with_UTC_offset"), e);
        }
        cbNotApplyUTCOffset.setSelected(m.getGpxUTC0());
        cbGPXTrkSegWhenSmall.setSelected(m.getGpxTrkSegWhenBig());

        cbImperialUnits.setSelected(m.getBooleanOpt(Model.IMPERIAL));
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
        cbRecordNumberInfoInLog.setSelected(m
                .getBooleanOpt(AppSettings.IS_RECORDNBR_IN_LOGS));
        setTitle(getTitle()+ " V" + Version.VERSION_NUMBER);
        switch(m.getHeightConversionMode()) {
        case Model.HEIGHT_AUTOMATIC:
            cbHeightOverMeanSeaLevel.setSelectedIndex(2);
            break;
        case Model.HEIGHT_WGS84_TO_MSL:
            cbHeightOverMeanSeaLevel.setSelectedIndex(1);
            break;
        case Model.HEIGHT_NOCHANGE:
            cbHeightOverMeanSeaLevel.setSelectedIndex(0);
            break;
        case Model.HEIGHT_MSL_TO_WGS84:
            cbHeightOverMeanSeaLevel.setSelectedIndex(3);
            break;
        }
        updateFileFormatData();

        lbConversionTime.setVisible(false);

        getNMEAOutFile();

        
        // Looking for ports asynchronously
        new Thread() {
            public final void run() {
                addPortsToGui();
            }
        }.start();


        updateColorButtons();

        addWindowListener(this);

        J2SEGeneric.addListener(this);

        AboutBT747.addActionListener(new java.awt.event.ActionListener() {
            /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                c.showAbout();
            }
        });
        Info.addActionListener(new java.awt.event.ActionListener() {
            /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                c.showLicense();
            }
        });

        updateConnected(m.isConnected());
        
        cbAddTrackPointComment.setSelected(m
                .getBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT));
        cbAddTrackPointName.setSelected(m.getBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME));
    }

    
    private final void updateConnected(final boolean connected) {
        JPanel[] panels = { 
                GPSDecodePanel, 
                pnAdvancedSettingsPanel };

        btDownloadFromNumerix.setEnabled(connected);
        btDownloadIBlue.setEnabled(connected);
        for (JPanel panel : panels) {
            J2SEAppController.disablePanel(panel,connected);
        }
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
            Generic.debug(getString("While_adding_ports"), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.system.J2SEMessageListener#postMessage(java.lang.String)
     */
    public final void postMessage(final String message) {
        synchronized (infoTextArea) {
            infoTextArea.append(message);
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
            txtTime.setText(TimeStr); // NO18N
        }
        updateGPSData(gps);
    }
    
    private void updateGPSData(final GPSRecord gps) {

        txtLatitude.setText(String.format((Locale) null, "%.8f", gps.latitude)); // NOI18N
        // lbHeight.setText(String.valueOf(gps.height,3)+"m");
        txtLongitude
                .setText(String.format((Locale) null, "%.8f", gps.longitude)); // NOI18N
        txtGeoid.setText(String.format((Locale) null, "%.1f", gps.geoid) // NOI18N
                + getString("m")
                + getString("(calc:")
                + String.format((Locale) null, "%.1f", Conv.wgs84Separation(
                        gps.latitude, gps.longitude))
                        + getString("m") 
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
            txtFlashInfo.setText(((m.getFlashManuProdID() != 0) ? Convert
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
            txtModel.setText(m.getModel());
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
            txtFirmWare.setText(fwString); // NOI18N
            break;
        case ModelEvent.UPDATE_LOG_VERSION:
            txtLoggerSWVersion.setText(m.getMtkLogVersion());
            break;
        case ModelEvent.GPGGA:
            updateGPSData((GPSRecord) e.getArg());
            break;
        case ModelEvent.UPDATE_LOG_LOG_STATUS:
            // TODO: hkLogOnOff.setChecked(m.isLoggingActive());
            break;
        case ModelEvent.UPDATE_LOG_NBR_LOG_PTS:
            //TODO
            //lbUsedMem.setText(Txt.MEM_USED + Convert.toString(m.logMemUsed()) + "("
            //        + Convert.toString(m.logMemUsedPercent()) + "%)");
            // m_UsedLabel.repaintNow();
            // lbUsedRecords.setText(Txt.NBR_RECORDS
//                    + Convert.toString(m.logNbrLogPts()) + " ("
//                    + m.getEstimatedNbrRecordsFree(m.getLogFormat()) + " "
//                    + Txt.MEM_FREE + ")");
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
        case ModelEvent.CONVERSION_STARTED:
            conversionStartTime = System.currentTimeMillis();
            break;
        case ModelEvent.CONVERSION_ENDED:
            lbConversionTime
                    .setText(getString("Time_to_convert:_")
                            + ((int) (System.currentTimeMillis() - conversionStartTime))
                            + getString("_ms"));
            lbConversionTime.setVisible(true);
            break;
        case ModelEvent.CONNECTED:
            btConnect.setText(getString("Disconnect"));
            btConnectFunctionIsConnect = false;
            updateConnected(true);

            // TODO: Find the way to do this on tab entry.
            c.reqHoluxName();
            c.reqFlashUserOption();
            c.reqNMEAPeriods();

            c.reqLogOverwrite();
            c.reqLogReasonStatus();
            c.reqSBASEnabled();
            c.reqSBASTestEnabled();
            c.reqDGPSMode();
            c.reqFixInterval();
            c.reqBTAddr();
            c.reqMtkLogVersion();
            c.reqDeviceInfo();
            c.reqDatumMode();

            break;
        case ModelEvent.DISCONNECTED:
            btConnect.setText(getString("Connect"));
            btConnectFunctionIsConnect = true;
            updateConnected(false);
            break;
        case ModelEvent.DOWNLOAD_STATE_CHANGE:
        case ModelEvent.LOG_DOWNLOAD_DONE:
        case ModelEvent.LOG_DOWNLOAD_STARTED:
            progressBarUpdate();
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            c.replyToOkToOverwrite(c.getRequestToOverwriteFromDialog());
            break;
        case ModelEvent.COULD_NOT_OPEN_FILE:
            c.couldNotOpenFileMessage((String) e.getArg());
            break;
        }
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
        lbNoFileExt = new javax.swing.JLabel();
        pnDownload = new javax.swing.JPanel();
        pnDownloadMethod = new javax.swing.JPanel();
        cbIncremental = new javax.swing.JCheckBox();
        cbDisableLoggingDuringDownload = new javax.swing.JCheckBox();
        btDownloadIBlue = new javax.swing.JButton();
        btDownloadFromNumerix = new javax.swing.JButton();
        pnConvert = new javax.swing.JPanel();
        btConvert = new javax.swing.JButton();
        cbFormat = new javax.swing.JComboBox();
        pnDateFilter = new javax.swing.JPanel();
        lbToDate = new javax.swing.JLabel();
        lbFromDate = new javax.swing.JLabel();
        startDate = new com.toedter.calendar.JDateChooser();
        endDate = new com.toedter.calendar.JDateChooser();
        sfTimeSplitHours = new com.toedter.components.JSpinField();
        lbHour = new javax.swing.JLabel();
        spTimeSplitMinutes = new com.toedter.components.JSpinField();
        txtTimeSplit = new javax.swing.JLabel();
        lbMinutes = new javax.swing.JLabel();
        cbGPSType = new javax.swing.JComboBox();
        lbDeviceType = new javax.swing.JLabel();
        lbConversionTime = new javax.swing.JLabel();
        GPSDecodePanel = new javax.swing.JPanel();
        lbLatitude = new javax.swing.JLabel();
        lbLongitude = new javax.swing.JLabel();
        lbTime = new javax.swing.JLabel();
        txtLatitude = new javax.swing.JLabel();
        txtLongitude = new javax.swing.JLabel();
        txtTime = new javax.swing.JLabel();
        lbGeoid = new javax.swing.JLabel();
        txtGeoid = new javax.swing.JLabel();
        lbFlashInfo = new javax.swing.JLabel();
        txtFlashInfo = new javax.swing.JLabel();
        lbModel = new javax.swing.JLabel();
        txtModel = new javax.swing.JLabel();
        lbFirmWare = new javax.swing.JLabel();
        txtFirmWare = new javax.swing.JLabel();
        lbLoggerSWversion = new javax.swing.JLabel();
        lbMemoryUsed = new javax.swing.JLabel();
        txtLoggerSWVersion = new javax.swing.JLabel();
        txtMemoryUsed = new javax.swing.JLabel();
        FileSettingsPanel = new javax.swing.JPanel();
        pnVarious = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cbUTCOffset = new javax.swing.JComboBox();
        cbHeightOverMeanSeaLevel = new javax.swing.JComboBox();
        cbRecordNumberInfoInLog = new javax.swing.JCheckBox();
        cbImperialUnits = new javax.swing.JCheckBox();
        txtTimeZone = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cbStandardOrDaylightSaving = new javax.swing.JComboBox();
        cbOneFilePerDay = new javax.swing.JComboBox();
        cbGoodFixColor = new javax.swing.JButton();
        cbNoFixColor = new javax.swing.JButton();
        pnSeparation = new javax.swing.JPanel();
        lbNewTrackAfter = new javax.swing.JLabel();
        tfTrackSeparationTime = new javax.swing.JTextField();
        lbMinPause = new javax.swing.JLabel();
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
        pnTrackPoints = new javax.swing.JPanel();
        cbAddTrackPointComment = new javax.swing.JCheckBox();
        cbAddTrackPointName = new javax.swing.JCheckBox();
        pnLanguage = new javax.swing.JPanel();
        lbLanguage = new javax.swing.JLabel();
        cbLanguage = new javax.swing.JComboBox();
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
        infoTextArea = new javax.swing.JTextArea();
        jMenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        miExit = new javax.swing.JMenuItem();
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
                .add(DownloadProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
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

        lbNoFileExt.setText(bundle.getString("BT747Main.lbNoFileExt.text")); // NOI18N

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
                        .add(lbNoFileExt))
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
                    .add(lbNoFileExt)
                    .add(btOutputFile)
                    .add(tfOutputFileBaseName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnDownload.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnDownload.border.title"))); // NOI18N

        pnDownloadMethod.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnDownloadMethod.border.title"))); // NOI18N

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

        org.jdesktop.layout.GroupLayout pnDownloadMethodLayout = new org.jdesktop.layout.GroupLayout(pnDownloadMethod);
        pnDownloadMethod.setLayout(pnDownloadMethodLayout);
        pnDownloadMethodLayout.setHorizontalGroup(
            pnDownloadMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDownloadMethodLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnDownloadMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbIncremental)
                    .add(cbDisableLoggingDuringDownload)))
        );
        pnDownloadMethodLayout.setVerticalGroup(
            pnDownloadMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDownloadMethodLayout.createSequentialGroup()
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
                .add(pnDownloadMethod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnDownloadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btDownloadIBlue)
                    .add(btDownloadFromNumerix)))
        );
        pnDownloadLayout.setVerticalGroup(
            pnDownloadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDownloadMethod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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

        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GPX", "CSV", "CompeGPS (.TRK,.WPT)", "KML", "KMZ", "OziExplorer (.PLT)", "NMEA" }));
        cbFormat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFormatItemStateChanged(evt);
            }
        });

        pnDateFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnDateFilter.border.title"))); // NOI18N

        lbToDate.setText(bundle.getString("BT747Main.lbToDate.text")); // NOI18N

        lbFromDate.setText(bundle.getString("BT747Main.lbFromDate.text")); // NOI18N

        sfTimeSplitHours.setMaximum(23);
        sfTimeSplitHours.setMinimum(0);
        sfTimeSplitHours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                sfTimeSplitHoursFocusLost(evt);
            }
        });

        lbHour.setText(bundle.getString("BT747Main.lbHour.text")); // NOI18N

        spTimeSplitMinutes.setMaximum(59);
        spTimeSplitMinutes.setMinimum(0);
        spTimeSplitMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spTimeSplitMinutesFocusLost(evt);
            }
        });

        txtTimeSplit.setText(bundle.getString("BT747Main.txtTimeSplit.text")); // NOI18N
        txtTimeSplit.setToolTipText(bundle.getString("BT747Main.txtTimeSplit.toolTipText")); // NOI18N

        lbMinutes.setText(bundle.getString("BT747Main.lbMinutes.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnDateFilterLayout = new org.jdesktop.layout.GroupLayout(pnDateFilter);
        pnDateFilter.setLayout(pnDateFilterLayout);
        pnDateFilterLayout.setHorizontalGroup(
            pnDateFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDateFilterLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(lbFromDate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbToDate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(txtTimeSplit)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sfTimeSplitHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbHour, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTimeSplitMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbMinutes))
        );
        pnDateFilterLayout.setVerticalGroup(
            pnDateFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(lbToDate)
            .add(lbFromDate)
            .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(sfTimeSplitHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(txtTimeSplit)
            .add(lbHour)
            .add(spTimeSplitMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(lbMinutes)
        );

        cbGPSType.setModel(modelGpsType);
        cbGPSType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbGPSTypeFocusLost(evt);
            }
        });

        lbDeviceType.setText(bundle.getString("BT747Main.lbDeviceType.text")); // NOI18N

        lbConversionTime.setText(bundle.getString("BT747Main.lbConversionTime.text")); // NOI18N
        lbConversionTime.setToolTipText(bundle.getString("BT747Main.lbConversionTime.toolTipText")); // NOI18N

        org.jdesktop.layout.GroupLayout pnConvertLayout = new org.jdesktop.layout.GroupLayout(pnConvert);
        pnConvert.setLayout(pnConvertLayout);
        pnConvertLayout.setHorizontalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDateFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnConvertLayout.createSequentialGroup()
                .add(btConvert)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbDeviceType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbConversionTime))
        );
        pnConvertLayout.setVerticalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvertLayout.createSequentialGroup()
                .add(pnDateFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btConvert)
                    .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbDeviceType)
                    .add(cbGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbConversionTime)))
        );

        setSelectedFormat(cbFormat.getSelectedItem().toString());

        GPSDecodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.GPSDecodePanel.border.title"))); // NOI18N

        lbLatitude.setText(bundle.getString("BT747Main.lbLatitude.text")); // NOI18N

        lbLongitude.setText(bundle.getString("BT747Main.lbLongitude.text")); // NOI18N

        lbTime.setText(bundle.getString("BT747Main.lbTime.text")); // NOI18N

        txtLatitude.setText(bundle.getString("BT747Main.txtLatitude.text")); // NOI18N

        txtLongitude.setText(bundle.getString("BT747Main.txtLongitude.text")); // NOI18N

        txtTime.setText(bundle.getString("BT747Main.txtTime.text")); // NOI18N

        lbGeoid.setText(bundle.getString("BT747Main.lbGeoid.text")); // NOI18N

        txtGeoid.setText(bundle.getString("BT747Main.txtGeoid.text")); // NOI18N

        lbFlashInfo.setText(bundle.getString("BT747Main.lbFlashInfo.text")); // NOI18N

        txtFlashInfo.setText(bundle.getString("BT747Main.txtFlashInfo.text")); // NOI18N
        txtFlashInfo.setToolTipText(bundle.getString("BT747Main.txtFlashInfo.toolTipText")); // NOI18N

        lbModel.setText(bundle.getString("BT747Main.lbModel.text")); // NOI18N

        txtModel.setText(bundle.getString("BT747Main.txtModel.text")); // NOI18N

        lbFirmWare.setText(bundle.getString("BT747Main.lbFirmWare.text")); // NOI18N

        txtFirmWare.setText(bundle.getString("BT747Main.txtFirmWare.text")); // NOI18N

        lbLoggerSWversion.setText(bundle.getString("BT747Main.lbLoggerSWversion.text")); // NOI18N

        lbMemoryUsed.setText(bundle.getString("BT747Main.lbMemoryUsed.text")); // NOI18N

        txtLoggerSWVersion.setText(bundle.getString("BT747Main.txtLoggerSWVersion.text")); // NOI18N
        txtLoggerSWVersion.setToolTipText(bundle.getString("BT747Main.txtLoggerSWVersion.toolTipText")); // NOI18N

        txtMemoryUsed.setText(bundle.getString("BT747Main.txtMemoryUsed.text")); // NOI18N

        org.jdesktop.layout.GroupLayout GPSDecodePanelLayout = new org.jdesktop.layout.GroupLayout(GPSDecodePanel);
        GPSDecodePanel.setLayout(GPSDecodePanelLayout);
        GPSDecodePanelLayout.setHorizontalGroup(
            GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GPSDecodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lbFlashInfo)
                    .add(lbModel)
                    .add(lbFirmWare)
                    .add(lbMemoryUsed)
                    .add(lbLoggerSWversion)
                    .add(lbLatitude)
                    .add(lbLongitude)
                    .add(lbTime)
                    .add(lbGeoid))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtMemoryUsed)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtLoggerSWVersion)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtFirmWare)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtModel)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtFlashInfo)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtGeoid)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtTime)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtLongitude)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtLatitude))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        GPSDecodePanelLayout.setVerticalGroup(
            GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GPSDecodePanelLayout.createSequentialGroup()
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbLatitude)
                    .add(txtLatitude))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbLongitude)
                    .add(txtLongitude))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbTime)
                    .add(txtTime))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbGeoid)
                    .add(txtGeoid))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbFlashInfo)
                    .add(txtFlashInfo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbModel)
                    .add(txtModel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbFirmWare)
                    .add(txtFirmWare))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbLoggerSWversion)
                    .add(txtLoggerSWVersion))
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

        cbHeightOverMeanSeaLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Keep Height", "WGS84 -> MSL", "Automatic", "MSL -> WGS84" }));
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

        txtTimeZone.setText(bundle.getString("BT747Main.txtTimeZone.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(cbHeightOverMeanSeaLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jPanel1Layout.createSequentialGroup()
                            .add(txtTimeZone)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(cbUTCOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(cbRecordNumberInfoInLog))
                    .add(cbImperialUnits))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbUTCOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtTimeZone))
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

        lbMinPause.setText(bundle.getString("BT747Main.lbMinPause.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnSeparationLayout = new org.jdesktop.layout.GroupLayout(pnSeparation);
        pnSeparation.setLayout(pnSeparationLayout);
        pnSeparationLayout.setHorizontalGroup(
            pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSeparationLayout.createSequentialGroup()
                .add(lbNewTrackAfter)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfTrackSeparationTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbMinPause)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnSeparationLayout.setVerticalGroup(
            pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(lbNewTrackAfter)
                .add(tfTrackSeparationTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lbMinPause))
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

        pnTrackPoints.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnTrackPoints.border.title"))); // NOI18N

        cbAddTrackPointComment.setText(bundle.getString("BT747Main.cbAddTrackPointComment.text")); // NOI18N
        cbAddTrackPointComment.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAddTrackPointCommentItemStateChanged(evt);
            }
        });

        cbAddTrackPointName.setText(bundle.getString("BT747Main.cbAddTrackPointName.text")); // NOI18N
        cbAddTrackPointName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAddTrackPointNameItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnTrackPointsLayout = new org.jdesktop.layout.GroupLayout(pnTrackPoints);
        pnTrackPoints.setLayout(pnTrackPointsLayout);
        pnTrackPointsLayout.setHorizontalGroup(
            pnTrackPointsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrackPointsLayout.createSequentialGroup()
                .add(cbAddTrackPointComment)
                .add(18, 18, 18)
                .add(cbAddTrackPointName))
        );
        pnTrackPointsLayout.setVerticalGroup(
            pnTrackPointsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrackPointsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(cbAddTrackPointComment)
                .add(cbAddTrackPointName))
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
            .add(pnTrackPoints, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        pnFileOutputFieldInnerLayout.setVerticalGroup(
            pnFileOutputFieldInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                .add(pnFileOutputFieldInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                        .add(pnFileTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnFilePosition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                        .add(jPanel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(pnFileReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTrackPoints, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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

        pnLanguage.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnLanguage.border.title_1"))); // NOI18N

        lbLanguage.setText(bundle.getString("BT747Main.lbLanguage.text")); // NOI18N
        lbLanguage.setToolTipText(bundle.getString("BT747Main.lbLanguage.toolTipText_1")); // NOI18N

        cbLanguage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "en_US" }));
        cbLanguage.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbLanguageItemChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnLanguageLayout = new org.jdesktop.layout.GroupLayout(pnLanguage);
        pnLanguage.setLayout(pnLanguageLayout);
        pnLanguageLayout.setHorizontalGroup(
            pnLanguageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLanguageLayout.createSequentialGroup()
                .add(lbLanguage)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnLanguageLayout.setVerticalGroup(
            pnLanguageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLanguageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(lbLanguage)
                .add(cbLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout FileSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(FileSettingsPanel);
        FileSettingsPanel.setLayout(FileSettingsPanelLayout);
        FileSettingsPanelLayout.setHorizontalGroup(
            FileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FileSettingsPanelLayout.createSequentialGroup()
                .add(FileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnSeparation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnVarious, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFileOutputFields, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        FileSettingsPanelLayout.setVerticalGroup(
            FileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FileSettingsPanelLayout.createSequentialGroup()
                .add(FileSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(FileSettingsPanelLayout.createSequentialGroup()
                        .add(pnVarious, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnSeparation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnFileOutputFields, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.FileSettingsPanel.TabConstraints.tabTitle"), FileSettingsPanel); // NOI18N

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

        infoTextArea.setColumns(20);
        infoTextArea.setLineWrap(true);
        infoTextArea.setRows(5);
        infoTextArea.setText(bundle.getString("BT747Main.infoTextArea.text")); // NOI18N
        infoTextArea.setAutoscrolls(false);
        infoTextArea.setFocusable(false);
        infoTextArea.setOpaque(false);
        jScrollPane1.setViewportView(infoTextArea);

        org.jdesktop.layout.GroupLayout InfoPanelLayout = new org.jdesktop.layout.GroupLayout(InfoPanel);
        InfoPanel.setLayout(InfoPanelLayout);
        InfoPanelLayout.setHorizontalGroup(
            InfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(InfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                .addContainerGap())
        );
        InfoPanelLayout.setVerticalGroup(
            InfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(InfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPanelAll.addTab(bundle.getString("BT747Main.InfoPanel.TabConstraints.tabTitle"), InfoPanel); // NOI18N

        FileMenu.setText(bundle.getString("BT747Main.FileMenu.text")); // NOI18N

        miExit.setText(bundle.getString("BT747Main.miExit.text")); // NOI18N
        FileMenu.add(miExit);

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
                    .add(tabbedPanelAll))
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

private void cbAddTrackPointCommentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAddTrackPointCommentItemStateChanged
    c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT,cbAddTrackPointComment.isSelected());

}//GEN-LAST:event_cbAddTrackPointCommentItemStateChanged

private void cbAddTrackPointNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAddTrackPointNameItemStateChanged
    c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME,cbAddTrackPointName.isSelected());
}//GEN-LAST:event_cbAddTrackPointNameItemStateChanged

private void cbLanguageItemChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbLanguageFocusLost
    c.setStringOpt(Model.LANGUAGE, (String) cbLanguage.getSelectedItem());
}//GEN-LAST:event_cbLanguageFocusLost

    private void cbNotApplyUTCOffsetStateChanged( javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_cbNotApplyUTCOffsetStateChanged
        c.setGpxUTC0(cbNotApplyUTCOffset.isSelected());
    }// GEN-LAST:event_cbNotApplyUTCOffsetStateChanged


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
                getString("Choose_the_color_for_a_'bad_track'_(pure_blue_to_ignore)"),
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
                getString("Choose_the_color_for_a_'good_track'_(pure_blue_to_ignore)"),
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
        cbGoodFixColor.setOpaque(true);
        myColor = new Color(Conv.hex2Int(m.getColorInvalidTrack()));
        cbNoFixColor.setBackground(myColor);
        cbNoFixColor.setForeground(new Color(255-myColor.getRed(),255-myColor.getGreen(),255-myColor.getBlue()));
        cbNoFixColor.setOpaque(true);
    }
    
    private void cbDisableLoggingDuringDownloadFocusLost(
            java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbDisableLoggingDuringDownloadFocusLost
        c.setIncremental(cbDisableLoggingDuringDownload.isSelected());
    }// GEN-LAST:event_cbDisableLoggingDuringDownloadFocusLost



    private void btSetNMEAFileOutputActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        setNMEAOutFile();
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
        switch (cbHeightOverMeanSeaLevel.getSelectedIndex()) {
        case 0:
            c.setHeightConversionMode(Model.HEIGHT_NOCHANGE);
            break;
        case 1:
            c.setHeightConversionMode(Model.HEIGHT_WGS84_TO_MSL);
            break;
        case 2:
            c.setHeightConversionMode(Model.HEIGHT_AUTOMATIC);
            break;
        case 3:
            c.setHeightConversionMode(Model.HEIGHT_MSL_TO_WGS84);
            break;
        }
    }// GEN-LAST:event_cbHeightOverMeanSeaLevelFocusLost

    private void btGPSDebugStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_btGPSDebugStateChanged
        c.setDebug(btGPSDebug.isSelected());
    }// GEN-LAST:event_btGPSDebugStateChanged

    private void btGPSConnectDebugStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_btGPSConnectDebugStateChanged
        c.setDebugConn(btGPSConnectDebug.isSelected());
    }// GEN-LAST:event_btGPSConnectDebugStateChanged
   
    
    private void tfRawLogFilePathFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_tfRawLogFilePathFocusLost

        c.setStringOpt(AppSettings.LOGFILEPATH, tfRawLogFilePath.getText());
    }// GEN-LAST:event_tfRawLogFilePathFocusLost


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
                .setToolTipText(getString("Select_the_basename_of_the_output_file."));
        // if (curDir.exists()) {
        // OutputFileChooser.setCurrentDirectory(getOutputFilePath());
        // }
        if (OutputFileChooser.showDialog(this, getString("Set_basename")) == JFileChooser.APPROVE_OPTION) {
            try {
                String relPath = gps.convert.FileUtil.getRelativePath(
                        (new File(m.getStringOpt(AppSettings.OUTPUTDIRPATH)))
                                .getCanonicalPath(), OutputFileChooser
                                .getSelectedFile().getCanonicalPath(),
                        File.separatorChar);
                if(relPath.lastIndexOf('.')==relPath.length()-4) {
                    relPath = relPath.substring(0, relPath.length()-4);
                }
                c.setOutputFileRelPath(relPath);
                getOutputFilePath();
                tfOutputFileBaseName.setCaretPosition(tfOutputFileBaseName.getText().length());

            } catch (Exception e) {
                Generic.debug(getString("OutputFileChooser"), e);
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
        RawLogFileChooser.setAcceptAllFileFilterUsed(true);
        RawLogFileChooser.addChoosableFileFilter(new BinFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new CSVFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new HoluxTRLFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new NMEAFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new DPL700FileFilter());
        KnownFileFilter ff = new KnownFileFilter();
        RawLogFileChooser.addChoosableFileFilter(ff);
        RawLogFileChooser.setFileFilter(ff);
        if (RawLogFileChooser.showDialog(this,getString("SetRawLogFile")
                ) == JFileChooser.APPROVE_OPTION) {
            try {
            c.setStringOpt(AppSettings.LOGFILEPATH, RawLogFileChooser.getSelectedFile()
                    .getCanonicalPath());
            } catch (Exception e) {
                Generic.debug(getString("RawFileChooser"),e);
            }
            tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
            tfRawLogFilePath.setCaretPosition(tfRawLogFilePath.getText().length());
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

        WorkingDirectoryChooser.setDialogTitle(getString("Choose_Working_Directory"));
        WorkingDirectoryChooser
                .setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        if (WorkingDirectoryChooser.showDialog(this, getString("SetWorkDir")) == JFileChooser.APPROVE_OPTION) {
            try {
                c.setStringOpt(AppSettings.OUTPUTDIRPATH, WorkingDirectoryChooser.getSelectedFile()
                                        .getCanonicalPath());
            } catch (Exception e) {
                Generic.debug(getString("WorkingDirChooser"), e);
            }
            tfWorkDirectory.setText(m.getStringOpt(AppSettings.OUTPUTDIRPATH));
            tfWorkDirectory.setCaretPosition(tfWorkDirectory.getText().length());
        }
    }// GEN-LAST:event_btWorkingDirectoryActionPerformed

    private int selectedFormat = Model.NO_LOG_LOGTYPE;

    private final void setSelectedFormat(final String selected) {
        if (selected.equals(getString("CSV_Description"))) {
            selectedFormat = Model.CSV_LOGTYPE;
        } else if (selected.equals(getString("GMAP_Description"))) {
            selectedFormat = Model.GMAP_LOGTYPE;
        } else if (selected.equals(getString("GPX_Description"))) {
            selectedFormat = Model.GPX_LOGTYPE;
        } else if (selected.equals(getString("KML_Description"))) {
            selectedFormat = Model.KML_LOGTYPE;
        } else if (selected.equals(getString("NMEA_Description"))) {
            selectedFormat = Model.NMEA_LOGTYPE;
        } else if (selected.equals(getString("OZI_Description"))) {
            selectedFormat = Model.PLT_LOGTYPE;
        } else if (selected.equals(getString("Compe_Description"))) {
            selectedFormat = Model.TRK_LOGTYPE;
        } else if (selected.equals(getString("KMZ_Description"))) {
            selectedFormat = J2SEAppController.KMZ_LOGTYPE;
        } else {
            selectedFormat = Model.NO_LOG_LOGTYPE;
        }
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
            if (s.toUpperCase().startsWith(getString("BLUETOOTH"))) {
                c.setBluetooth();
            } else if (s.toUpperCase().startsWith(getString("USB"))) {
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

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(final String args[]) {
        if(args.length>=1) {
            if(args[0].equals("arch")) {
                System.out.print(java.lang.System.getProperty("os.arch"));
            } else {
                BT747cmd.main(args);
            }
            return;
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            final Model m = new Model();
            final J2SEAppController c = new J2SEAppController(m);

            public final void run() {
                final BT747Main app = new BT747Main(m, c);
                app.setVisible(true);
            }
        });
    }

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
    private javax.swing.JPanel AdvancedfileSettingsPanel;
    private javax.swing.JProgressBar DownloadProgressBar;
    private javax.swing.JLabel DownloadProgressLabel;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JPanel FileSettingsPanel;
    private javax.swing.JPanel GPSDecodePanel;
    private javax.swing.JMenuItem Info;
    private javax.swing.JMenu InfoMenu;
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JPanel LogOperationsPanel;
    private javax.swing.JMenu SettingsMenu;
    private javax.swing.JButton btConnect;
    private javax.swing.JButton btConvert;
    private javax.swing.JButton btDownloadFromNumerix;
    private javax.swing.JButton btDownloadIBlue;
    private javax.swing.JRadioButtonMenuItem btGPSConnectDebug;
    private javax.swing.JRadioButtonMenuItem btGPSDebug;
    private javax.swing.JButton btOutputFile;
    private javax.swing.JButton btRawLogFile;
    private javax.swing.JButton btSetNMEAFileOutput;
    private javax.swing.JButton btWorkingDirectory;
    private javax.swing.JCheckBox cbAddTrackPointComment;
    private javax.swing.JCheckBox cbAddTrackPointName;
    private javax.swing.JCheckBox cbDisableLoggingDuringDownload;
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
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JComboBox cbGPSType;
    private javax.swing.JCheckBox cbGPXTrkSegWhenSmall;
    private javax.swing.JButton cbGoodFixColor;
    private javax.swing.JComboBox cbHeightOverMeanSeaLevel;
    private javax.swing.JCheckBox cbImperialUnits;
    private javax.swing.JCheckBox cbIncremental;
    private javax.swing.JComboBox cbLanguage;
    private javax.swing.JButton cbNoFixColor;
    private javax.swing.JCheckBox cbNotApplyUTCOffset;
    private javax.swing.JComboBox cbOneFilePerDay;
    private javax.swing.JComboBox cbPortName;
    private javax.swing.JCheckBox cbRecordNumberInfoInLog;
    private javax.swing.JComboBox cbSerialSpeed;
    private javax.swing.JComboBox cbStandardOrDaylightSaving;
    private javax.swing.JComboBox cbUTCOffset;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbConversionTime;
    private javax.swing.JLabel lbDeviceType;
    private javax.swing.JLabel lbFirmWare;
    private javax.swing.JLabel lbFlashInfo;
    private javax.swing.JLabel lbFromDate;
    private javax.swing.JLabel lbGeoid;
    private javax.swing.JLabel lbHour;
    private javax.swing.JLabel lbLanguage;
    private javax.swing.JLabel lbLatitude;
    private javax.swing.JLabel lbLoggerSWversion;
    private javax.swing.JLabel lbLongitude;
    private javax.swing.JLabel lbMemoryUsed;
    private javax.swing.JLabel lbMinPause;
    private javax.swing.JLabel lbMinutes;
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
    private javax.swing.JLabel lbNewTrackAfter;
    private javax.swing.JLabel lbNoFileExt;
    private javax.swing.JLabel lbSerialSpeed;
    private javax.swing.JLabel lbTime;
    private javax.swing.JLabel lbToDate;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JPanel pnBottomInformation;
    private javax.swing.JPanel pnConvert;
    private javax.swing.JPanel pnDateFilter;
    private javax.swing.JPanel pnDownload;
    private javax.swing.JPanel pnDownloadMethod;
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
    private javax.swing.JPanel pnGPXFileSettings;
    private javax.swing.JPanel pnLanguage;
    private javax.swing.JPanel pnSeparation;
    private javax.swing.JPanel pnTrackPoints;
    private javax.swing.JPanel pnVarious;
    private com.toedter.components.JSpinField sfTimeSplitHours;
    private com.toedter.components.JSpinField spTimeSplitMinutes;
    private com.toedter.calendar.JDateChooser startDate;
    private javax.swing.JTabbedPane tabbedPanelAll;
    private javax.swing.JTextField tfOutputFileBaseName;
    private javax.swing.JTextField tfRawLogFilePath;
    private javax.swing.JTextField tfTrackSeparationTime;
    private javax.swing.JTextField tfWorkDirectory;
    private javax.swing.JLabel txtFirmWare;
    private javax.swing.JLabel txtFlashInfo;
    private javax.swing.JLabel txtGeoid;
    private javax.swing.JLabel txtLatitude;
    private javax.swing.JLabel txtLoggerSWVersion;
    private javax.swing.JLabel txtLongitude;
    private javax.swing.JLabel txtMemoryUsed;
    private javax.swing.JLabel txtModel;
    private javax.swing.JLabel txtTime;
    private javax.swing.JLabel txtTimeSplit;
    private javax.swing.JLabel txtTimeZone;
    // End of variables declaration//GEN-END:variables

}
