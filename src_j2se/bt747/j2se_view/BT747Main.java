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
import gps.convert.Conv;
import gps.log.GPSRecord;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.ComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
                getString("KMZ_Description"),
                getString("TABLE_Description")}));
    }
    
    private AdvancedDeviceSettingsPanel pnAdvancedSettingsPanel;
    private DeviceSettingsPanel pnDeviceSettingsPanel;
    private FiltersPanel pnFiltersPanel;
    private OutputSettingsPanel pnOutputSettingsPanel;
    private AdvancedFileSettingsPanel pnAdvancedFileSettingsPanel;
    /**
     * Initialize application data. Gets the values from the model to set them
     * in the GUI.
     */
    private void initAppData() {
        c.setRootFrame(this);

        pnOutputSettingsPanel = new OutputSettingsPanel();
        pnOutputSettingsPanel.init(c);
        tabbedPanelAll.insertTab(getString("BT747Main.FileSettingsPanel.TabConstraints.tabTitle"),
                null, pnOutputSettingsPanel, null, 1);
        
        pnFiltersPanel = new FiltersPanel();
        pnFiltersPanel.init(c);
        tabbedPanelAll.insertTab(getString("BT747Main.LogFiltersPanel.TabConstraints.tabTitle"),
                null, pnFiltersPanel, null, 2);
        
        
        pnDeviceSettingsPanel = new DeviceSettingsPanel();
        pnDeviceSettingsPanel.init(c);
        tabbedPanelAll.insertTab(getString("BT747Main.DeviceSettingsPanel.TabConstraints.tabTitle"),
                null, pnDeviceSettingsPanel, null, 3);

        pnAdvancedSettingsPanel = new AdvancedDeviceSettingsPanel();
        pnAdvancedSettingsPanel.init(c);
        tabbedPanelAll.insertTab(getString("BT747Main.AdvancedSettingsPanel.TabConstraints.tabTitle"),
                null, pnAdvancedSettingsPanel, null, 4);
        miExit.setVisible(false);
 
        pnAdvancedFileSettingsPanel = new AdvancedFileSettingsPanel();
        pnAdvancedFileSettingsPanel.init(c);
        tabbedPanelAll.insertTab(getString("BT747Main.AdvancedfileSettingsPanel.TabConstraints.tabTitle"),
                null, pnAdvancedFileSettingsPanel, null, 5);

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
        cbLoggingActive.setSelected(m.isLoggingActive());


        updateCbGPSType();

        // TODO: Correct next line
        cbDisableLoggingDuringDownload.setSelected(m.isIncremental());

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
        setTitle(getTitle()+ " V" + Version.VERSION_NUMBER);

        lbConversionTime.setVisible(false);
        
        // Looking for ports asynchronously
        new Thread() {
            public final void run() {
                addPortsToGui();
            }
        }.start();

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
        setLogConversionParameters();
        switch (selectedFormat) {
        case J2SEAppController.TABLE_LOGTYPE:
            int idx = tabbedPanelAll.getTabCount()-1;
            if(tabbedPanelAll.getComponentAt(idx).getClass()==PositionTablePanel.class) {
                tabbedPanelAll.removeTabAt(idx);
            }
            GPSRecord[] r;
            r = c.convertLogToTrackPoints();
            PositionTablePanel pt = new PositionTablePanel();
            pt.setGpsRecords(r);
            tabbedPanelAll.addTab(getString("Table"), pt);
            tabbedPanelAll.setSelectedIndex(tabbedPanelAll.getTabCount()-1);
            break;
        default:
            c.convertLog(selectedFormat);
            break;
        }
    }

    /**
     * 
     */
    private void setLogConversionParameters() {
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
            cbLoggingActive.setSelected(m.isLoggingActive());
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
            c.reqLogStatus();
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
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
        cbLoggingActive = new javax.swing.JCheckBox();
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

        cbLoggingActive.setText(bundle.getString("BT747Main.cbLoggingActive.text")); // NOI18N
        cbLoggingActive.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbLoggingActiveFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout GPSDecodePanelLayout = new org.jdesktop.layout.GroupLayout(GPSDecodePanel);
        GPSDecodePanel.setLayout(GPSDecodePanelLayout);
        GPSDecodePanelLayout.setHorizontalGroup(
            GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GPSDecodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(GPSDecodePanelLayout.createSequentialGroup()
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
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtLatitude)))
                    .add(cbLoggingActive))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbLoggingActive))
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

private void cbLoggingActiveFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbLoggingActiveFocusLost
    c.setLoggingActive(cbLoggingActive.isSelected());
}//GEN-LAST:event_cbLoggingActiveFocusLost

    
    private void cbDisableLoggingDuringDownloadFocusLost(
            java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbDisableLoggingDuringDownloadFocusLost
        c.setIncremental(cbDisableLoggingDuringDownload.isSelected());
    }// GEN-LAST:event_cbDisableLoggingDuringDownloadFocusLost

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
        } else if (selected.equals(getString("TABLE_Description"))) {
            selectedFormat = J2SEAppController.TABLE_LOGTYPE;
        } else {
            selectedFormat = Model.NO_LOG_LOGTYPE;
        }
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
    private javax.swing.JProgressBar DownloadProgressBar;
    private javax.swing.JLabel DownloadProgressLabel;
    private javax.swing.JMenu FileMenu;
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
    private javax.swing.JButton btWorkingDirectory;
    private javax.swing.JCheckBox cbDisableLoggingDuringDownload;
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JComboBox cbGPSType;
    private javax.swing.JCheckBox cbIncremental;
    private javax.swing.JCheckBox cbLoggingActive;
    private javax.swing.JComboBox cbPortName;
    private javax.swing.JComboBox cbSerialSpeed;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbConversionTime;
    private javax.swing.JLabel lbDeviceType;
    private javax.swing.JLabel lbFirmWare;
    private javax.swing.JLabel lbFlashInfo;
    private javax.swing.JLabel lbFromDate;
    private javax.swing.JLabel lbGeoid;
    private javax.swing.JLabel lbHour;
    private javax.swing.JLabel lbLatitude;
    private javax.swing.JLabel lbLoggerSWversion;
    private javax.swing.JLabel lbLongitude;
    private javax.swing.JLabel lbMemoryUsed;
    private javax.swing.JLabel lbMinutes;
    private javax.swing.JLabel lbModel;
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
    private javax.swing.JPanel pnFiles;
    private com.toedter.components.JSpinField sfTimeSplitHours;
    private com.toedter.components.JSpinField spTimeSplitMinutes;
    private com.toedter.calendar.JDateChooser startDate;
    private javax.swing.JTabbedPane tabbedPanelAll;
    private javax.swing.JTextField tfOutputFileBaseName;
    private javax.swing.JTextField tfRawLogFilePath;
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
    // End of variables declaration//GEN-END:variables

}
