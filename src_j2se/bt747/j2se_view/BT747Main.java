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
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package bt747.j2se_view;

import gnu.io.CommPortIdentifier;
import gps.connection.GPSrxtx;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.iharder.dnd.DropListener;
import net.iharder.dnd.FileDrop;
import net.sf.bt747.j2se.app.utils.BareBonesBrowserLaunch;
import net.sf.bt747.j2se.system.J2SEGeneric;
import net.sf.bt747.j2se.system.J2SEMessageListener;

import bt747.Version;
import bt747.j2se_view.MyMap.MapType;
import bt747.model.AppSettings;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * J2SE Implementation (GUI) of BT747.
 * 
 * @author Mario De Weerd
 */
public class BT747Main extends javax.swing.JFrame implements
        bt747.model.ModelListener, WindowListener, J2SEMessageListener {

    /**
     * Initialize the lower level interface class. Needed for BT747 to work.
     */
    /**
     * Initialize the lower level interface class. Needed for BT747 to work.
     */
    static {
        JavaLibBridge
                .setJavaLibImplementation(new net.sf.bt747.j2se.system.J2SEJavaTranslations());
        // Set the serial port class instance to use (also system specific).
        if (!GPSrxtx.hasDefaultPortInstance()) {
            GPSrxtx
                    .setDefaultGpsPortInstance(new gps.connection.GPSRxTxPort());
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private J2SEAppModel m;
    private J2SEAppController c;

    /** Creates new form BT747Main */
    public BT747Main() {
        initComponents();
        initAppData();
    }

    public BT747Main(final J2SEAppModel m, final J2SEAppController c) {
        setModel(m);
        setController(c);
        initComponents();
        initAppData();
    }

    public void setController(final Controller c) {
        if (m != null) {
            m.removeListener(this);
        }
        this.c = (J2SEAppController) c; // Should check that c is an
        // AppController or do it differently
        if (m != null) {
            m.addListener(this);
        }
    }

    public void setModel(final J2SEAppModel m) {
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
        cbPortName.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] {
                        getString("USB_ForLinuxMac"),
                        getString("BLUETOOTH_ForMac"),
                        "COM1:", // NOI18N
                        "COM2:", "COM3:", "COM4:", "COM5:", "COM6:",
                        "COM7:",
                        "COM8:", // NOI18N
                        "COM9:", "COM10:", "COM11:", "COM12:", "COM13:",
                        "COM14:", // NOI18N
                        "COM15:", "COM16:" })); // NOI18N
    }

    private LogOperationsPanel pnLogOperationsPanel;
    private AdvancedDeviceSettingsPanel pnAdvancedSettingsPanel;
    private DeviceSettingsPanel pnDeviceSettingsPanel;
    private FiltersPanel pnFiltersPanel;
    private OutputSettingsPanel pnOutputSettingsPanel;
    private AdvancedFileSettingsPanel pnAdvancedFileSettingsPanel;
    // private FileTablePanel pnFilesToTagPanel;
    private FilesPanel pnFilesToTagPanel;
    private MyMap pnMap = new MyMap();
    private AgpsPanel pnAgpsPanel;

    private final Component inScrollPane(final JPanel p) {
        JScrollPane sp;
        sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.setMinimumSize(new Dimension(30, 30));
        return sp;
    }

    private final Icon getIcon(final String iconPath) {
        return J2SEAppController.getIcon(iconPath);
    }

    private final void updateToolBarButton() {
        Component sel = tabbedPanelAll.getSelectedComponent();
        if (sel instanceof JScrollPane) {
            // tab inside scrollpane
            final JScrollPane sp = (JScrollPane) sel;
            sel = sp.getViewport().getView();
        }
        btToolLogOps.setSelected(sel == pnLogOperationsPanel);
        btToolFilesToTag.setSelected(sel == pnFilesToTagPanel);
        btToolMap.setSelected(sel == pnMap);
        btToolOutputSettings.setSelected(sel == pnOutputSettingsPanel);
        btToolFilter.setSelected(sel == pnFiltersPanel);
        btToolDeviceSettings.setSelected(sel == pnDeviceSettingsPanel);
        btToolAdvancedDevSettings.setSelected(sel == pnAdvancedSettingsPanel);
        btToolAdvFileSettings.setSelected(sel == pnAdvancedFileSettingsPanel);
        btToolInfo.setSelected(sel == InfoPanel);
        btToolWaypoints.setSelected(sel == waypointPanel);
        btToolTrack.setSelected(sel == trackPanel);
    }

    private final void completeGui() {
        pnLogOperationsPanel = new LogOperationsPanel();
        pnLogOperationsPanel.init(c);
        // pnLogOperationsPanel.setPreferredSize(new
        // java.awt.Dimension(800,800));
        // getLayout().preferredLayoutSize(tabbedPanelAll);
        tabbedPanelAll.insertTab(getString("LogOperations.tabTitle"),
                getIcon("icon_home.gif"), inScrollPane(pnLogOperationsPanel),
                null, 0);
        tabbedPanelAll.setSelectedIndex(0);

        pnOutputSettingsPanel = new OutputSettingsPanel();
        tabbedPanelAll
                .insertTab(
                        getString("BT747Main.FileSettingsPanel.TabConstraints.tabTitle"),
                        getIcon("icon_outputsettungs.gif"),
                        inScrollPane(pnOutputSettingsPanel), null, 1);

        pnFiltersPanel = new FiltersPanel();
        tabbedPanelAll
                .insertTab(
                        getString("BT747Main.LogFiltersPanel.TabConstraints.tabTitle"),
                        getIcon("hourglass.png"),
                        inScrollPane(pnFiltersPanel), null, 2);

        pnDeviceSettingsPanel = new DeviceSettingsPanel();
        tabbedPanelAll
                .insertTab(
                        getString("BT747Main.DeviceSettingsPanel.TabConstraints.tabTitle"),
                        getIcon("icon_devicesettungs.gif"),
                        inScrollPane(pnDeviceSettingsPanel), null, 3);

        pnAdvancedSettingsPanel = new AdvancedDeviceSettingsPanel();
        tabbedPanelAll
                .insertTab(
                        getString("BT747Main.AdvancedSettingsPanel.TabConstraints.tabTitle"),
                        getIcon("icon_addevicesettungs.gif"),
                        inScrollPane(pnAdvancedSettingsPanel), null, 4);

        pnAdvancedFileSettingsPanel = new AdvancedFileSettingsPanel();
        pnAdvancedFileSettingsPanel.init(c);
        tabbedPanelAll
                .insertTab(
                        getString("BT747Main.AdvancedfileSettingsPanel.TabConstraints.tabTitle"),
                        getIcon("page_right.gif"),
                        inScrollPane(pnAdvancedFileSettingsPanel), null, 5);

        // pnFilesToTagPanel = new FileTablePanel();
        pnFilesToTagPanel = new FilesPanel();
        tabbedPanelAll.insertTab(getString("FilesToTagPanel.title"),
                getIcon("images.png"), pnFilesToTagPanel, null, 1);
        try {
            // Currently debuggin
            // JPanel pnMap = (JPanel) (Class.forName("bt747.j2se_view.MyMap")
            // .newInstance());
            tabbedPanelAll.insertTab(getString("map.tabTitle"),
                    getIcon("world.png"), pnMap, null, 2);
        } catch (final Exception e) {
            Generic.debug("During map setup", e);
            // TODO: handle exception
        }

        pnAgpsPanel = new AgpsPanel();
        pnAgpsPanel.init(c);
        tabbedPanelAll.insertTab(
                getString("BT747Main.AgpsPanel.TabConstraints.tabTitle"),
                null /* getIcon("icon_devicesettungs.gif") */,
                inScrollPane(pnAgpsPanel), null, 7);
        // System.err.println(tabbedPanelAll.getPreferredSize());
        // tabbedPanelAll.invalidate();
        // pnBottomInformation.invalidate();
        // this.invalidate();
        // this.pack();
    }

    private final static int PREFERRED_INITIAL_X_SIZE = 928;
    private final static int PREFERRED_INITIAL_Y_SIZE = 512;

    /**
     * Initialize application data. Gets the values from the model to set them
     * in the GUI.
     */
    private void initAppData() {
        c.setRootFrame(this);
        completeGui();
        final DropListener dl = new DropListener() {
            /*
             * (non-Javadoc)
             * 
             * @see net.iharder.dnd.FileDrop.Listener#filesDropped(java.io.File[])
             */
            public void filesDropped(final java.io.File[] files) {
                m.getPositionData().addFiles(files);
            }
        };
        new FileDrop(this, dl);

        pnMap.init(c);
        pnFilesToTagPanel.init(c);
        pnAdvancedSettingsPanel.init(c);
        pnDeviceSettingsPanel.init(c);
        pnFiltersPanel.init(c);
        pnOutputSettingsPanel.init(c);
        pack();
        int x = getWidth()
                + (int) tabbedPanelAll.getPreferredSize().getWidth()
                - tabbedPanelAll.getWidth();
        int y = getHeight()
                + (int) tabbedPanelAll.getPreferredSize().getHeight()
                - tabbedPanelAll.getHeight() + 10;
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if (x > dim.getWidth()) {
            x = (int) dim.getWidth();
        } else if ((dim.getWidth() - 100 > PREFERRED_INITIAL_X_SIZE)
                && (x < PREFERRED_INITIAL_X_SIZE)) {
            x = PREFERRED_INITIAL_X_SIZE;
        }
        if (y > dim.getHeight()) {
            y = (int) dim.getHeight();
        } else if ((dim.getHeight() - 100 > PREFERRED_INITIAL_Y_SIZE)
                && (y < PREFERRED_INITIAL_Y_SIZE)) {
            y = PREFERRED_INITIAL_Y_SIZE;
        }

        setSize(x, y);
        validate();
        updateGuiData(); // For internationalisation - not so easy in
        // netbeans
        infoTextArea.setEnabled(true);
        infoTextArea.setFocusable(true);

        J2SEGeneric.addListener(this);
        Generic.debug("BT747 " + Version.VERSION_NUMBER + " Build:"
                + Version.BUILD_STR);
        Generic.debug(java.lang.System.getProperty("os.name")); // NOI18N
        //infoTextArea.append("\n"); // NOI18N
        Generic.debug(java.lang.System.getProperty("os.arch")); // NOI18N
        //infoTextArea.append("\n"); // NOI18N
        Generic.debug(java.lang.System.getProperty("os.version")); // NOI18N
        //infoTextArea.append("\n"); // NOI18N
        Generic.debug(java.lang.System.getProperty("java.version")); // NOI18N
        //infoTextArea.append("\n"); // NOI18N
        Generic.debug(J2SEAppController.lookAndFeelMsg);

        progressBarUpdate();
        getDefaultPort();
        updateSerialSpeed();
        updateMapType();
        updateDownloadType();

        // c.setDebug(true);
        btGPSDebug.setSelected(Model.isDebug());
        // c.setDebugConn(false);
        btGPSConnectDebug.setSelected(m.isDebugConn());
        // c.setChunkSize(256); // Small for debug
        setTitle(getTitle() + " V" + Version.VERSION_NUMBER);

        addWindowListener(this);

        AboutBT747.addActionListener(new java.awt.event.ActionListener() {
            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(final ActionEvent e) {
                c.showAbout();
            }
        });
        Info.addActionListener(new java.awt.event.ActionListener() {
            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(final ActionEvent e) {
                c.showLicense();
            }
        });
    }

    private final void updateSerialSpeed() {
        final int speed = m.getIntOpt(AppSettings.BAUDRATE);
        cbSerialSpeed.setSelectedItem(Integer.valueOf(speed));
    }

    @SuppressWarnings("unchecked")
    private void addPortsToGui() {
        try {
            final Enumeration<CommPortIdentifier> list = CommPortIdentifier
                    .getPortIdentifiers();
            while (list.hasMoreElements()) {
                final CommPortIdentifier iden = list.nextElement();
                if (iden.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    ((javax.swing.DefaultComboBoxModel) cbPortName.getModel())
                            .addElement(iden.getName());
                }
            }
        } catch (final Exception e) {
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

    public void exitApplication() {
        c.saveSettings();
        java.awt.EventQueue.invokeLater(new Runnable() {
            /*
             * (non-Javadoc)
             * 
             * @see java.lang.Runnable#run()
             */
            public void run() {
                // Basically just in case and 'invoked later' to make sure
                // other operations finished.
                System.exit(0);
            }
        });

    }

    public void windowActivated(final WindowEvent e) {
    }

    public void windowClosed(final WindowEvent e) {
    }

    public void windowClosing(final WindowEvent e) {
        exitApplication();
    }

    public void windowDeactivated(final WindowEvent e) {
    }

    public void windowDeiconified(final WindowEvent e) {
    }

    public void windowIconified(final WindowEvent e) {
    }

    public void windowOpened(final WindowEvent e) {
    }

    private boolean btConnectFunctionIsConnect = true;

    private void updateMapType() {
        try {
            int idx;
            idx = m.getIntOpt(Model.MAPTYPE);
            MapType mt = MapType.OpenStreetMap;
            if (idx < MapType.values().length) {
                mt = MapType.values()[idx];
            }
            miMapnik.setSelected(mt == MapType.OpenStreetMap);
            miOsmarender.setSelected(mt == MapType.OsmaRender);
            miCycle.setSelected(mt == MapType.Cycle);
            mMap4.setSelected(mt == MapType.Map4);
        } catch (final Exception ex) {
            // TODO: handle exception
        }

    }

    private final void updateDownloadType() {
        final int type = m.getIntOpt(Model.DOWNLOAD_DEVICE);
        miDownloadIBlue.setSelected(type == 0);
        DownloadSirf.setSelected(type == 1);
    }

    public void modelEvent(final ModelEvent e) {
        // TODO Auto-generated method stub
        final int type = e.getType();
        switch (type) {

        case ModelEvent.SETTING_CHANGE:
            final int arg = Integer.valueOf((String) e.getArg());
            switch (arg) {
            case Model.MAPTYPE:
                updateMapType();
                break;
            case Model.DOWNLOAD_DEVICE:
                updateDownloadType();
            }
            break;
        case ModelEvent.ERASE_ONGOING_NEED_POPUP:
            c.createErasePopup();
            break;
        case ModelEvent.ERASE_DONE_REMOVE_POPUP:
            c.removeErasePopup();
            break;

        case ModelEvent.UPDATE_LOG_NBR_LOG_PTS:
            // TODO
            // lbUsedMem.setText(Txt.MEM_USED +
            // Convert.toString(m.logMemUsed())
            // + "("
            // + Convert.toString(m.logMemUsedPercent()) + "%)");
            // m_UsedLabel.repaintNow();
            // lbUsedRecords.setText(Txt.NBR_RECORDS
            // + Convert.toString(m.logNbrLogPts()) + " ("
            // + m.getEstimatedNbrRecordsFree(m.getLogFormat()) + " "
            // + Txt.MEM_FREE + ")");
            break;

        case ModelEvent.CONNECTED:
            updateConnectFunction(false);
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
            updateConnectFunction(true);
            break;
        case ModelEvent.DOWNLOAD_STATE_CHANGE:
        case ModelEvent.LOG_DOWNLOAD_DONE:
        case ModelEvent.LOG_DOWNLOAD_STARTED:
            progressBarUpdate();
            break;
        case J2SEAppModel.UPDATE_WAYPOINT_LIST:
            if (waypointPanel == null) {
                waypointPanel = new PositionTablePanel();
                int idx = tabbedPanelAll.indexOfComponent(trackPanel) + 1;
                if (idx <= 0) {
                    idx = tabbedPanelAll.indexOfComponent(pnMap) + 1;
                    if (idx <= 0) {
                        idx = tabbedPanelAll
                                .indexOfComponent(pnLogOperationsPanel) + 1;
                    }
                }
                waypointPanel.setGpsRecords(m.getPositionData()
                        .getWayPoints());
                tabbedPanelAll.insertTab(getString("WayPoints.tabTitle"),
                        getIcon("flag_blue.png"), waypointPanel, null, idx);
            } else {
                waypointPanel.setGpsRecords(m.getPositionData()
                        .getWayPoints());
            }
            break;

        case J2SEAppModel.UPDATE_TRACKPOINT_LIST:
            if (trackPanel == null) {
                trackPanel = new PositionTablePanel();
                trackPanel.setGpsRecords(m.getPositionData().getTracks());
                int idx = tabbedPanelAll.indexOfComponent(waypointPanel);
                if (idx < 0) {
                    idx = tabbedPanelAll.indexOfComponent(pnMap) + 1;
                    if (idx < 0) {
                        idx = tabbedPanelAll
                                .indexOfComponent(pnLogOperationsPanel) + 1;
                    }
                }
                tabbedPanelAll.insertTab(getString("Track.tabTitle"),
                        getIcon("database_table.png"), trackPanel, null, idx);
            } else {
                trackPanel.setGpsRecords(m.getPositionData().getTracks());
            }
            // for (int idx = tabbedPanelAll.getTabCount() - 1; idx >= 0;
            // idx--) {
            // if (tabbedPanelAll.getComponentAt(idx) == trackPanel) {
            // tabbedPanelAll.removeTabAt(idx);
            // }
            // }

            break;
        case J2SEAppModel.CHANGE_TO_MAP:
            tabbedPanelAll.setSelectedComponent(pnMap);
            break;
        }
    }

    private final void updateConnectFunction(final boolean isButtonConnect) {
        String btText;
        if (isButtonConnect) {
            btText = getString("Connect");
        } else {
            btText = getString("Disconnect");
        }
        btConnect.setText(btText);
        btConnectFunctionIsConnect = isButtonConnect;

    }

    private PositionTablePanel trackPanel = null;
    private PositionTablePanel waypointPanel = null;

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {// GEN-BEGIN:initComponents

        jPanel1 = new javax.swing.JPanel();
        pnBottomInformation = new javax.swing.JPanel();
        DownloadProgressBar = new javax.swing.JProgressBar();
        DownloadProgressLabel = new javax.swing.JLabel();
        cbPortName = new javax.swing.JComboBox();
        btConnect = new javax.swing.JButton();
        cbSerialSpeed = new javax.swing.JComboBox();
        lbSerialSpeed = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btToolLogOps = new javax.swing.JButton();
        btToolFilesToTag = new javax.swing.JButton();
        btToolMap = new javax.swing.JButton();
        btToolTrack = new javax.swing.JButton();
        btToolWaypoints = new javax.swing.JButton();
        btToolOutputSettings = new javax.swing.JButton();
        btToolFilter = new javax.swing.JButton();
        btToolDeviceSettings = new javax.swing.JButton();
        btToolAdvancedDevSettings = new javax.swing.JButton();
        btToolAdvFileSettings = new javax.swing.JButton();
        btToolInfo = new javax.swing.JButton();
        tabbedPanelAll = new javax.swing.JTabbedPane();
        InfoPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoTextArea = new javax.swing.JTextArea();
        jMenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        miFindSerialPorts = new javax.swing.JMenuItem();
        miMapCacheDir = new javax.swing.JMenuItem();
        miExit = new javax.swing.JMenuItem();
        SettingsMenu = new javax.swing.JMenu();
        btGPSDebug = new javax.swing.JRadioButtonMenuItem();
        btGPSConnectDebug = new javax.swing.JRadioButtonMenuItem();
        miMap = new javax.swing.JMenu();
        miMapnik = new javax.swing.JRadioButtonMenuItem();
        miOsmarender = new javax.swing.JRadioButtonMenuItem();
        miCycle = new javax.swing.JRadioButtonMenuItem();
        mMap4 = new javax.swing.JRadioButtonMenuItem();
        miDownloadDevice = new javax.swing.JMenu();
        miDownloadIBlue = new javax.swing.JRadioButtonMenuItem();
        DownloadSirf = new javax.swing.JRadioButtonMenuItem();
        InfoMenu = new javax.swing.JMenu();
        AboutBT747 = new javax.swing.JMenuItem();
        Info = new javax.swing.JMenuItem();
        mnSiteLink = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle
                .getBundle("bt747/j2se_view/Bundle"); // NOI18N
        setTitle(bundle.getString("BT747Main.title")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setName("BT747Frame"); // NOI18N

        jPanel1.setDoubleBuffered(false);

        DownloadProgressBar.setBackground(javax.swing.UIManager.getDefaults()
                .getColor("nbProgressBar.Foreground"));
        DownloadProgressBar.setForeground(new java.awt.Color(204, 255, 204));
        DownloadProgressBar.setToolTipText(bundle
                .getString("BT747Main.DownloadProgressBar.toolTipText")); // NOI18N
        DownloadProgressBar.setFocusable(false);
        DownloadProgressBar.setPreferredSize(new java.awt.Dimension(10, 16));

        DownloadProgressLabel.setText(bundle
                .getString("BT747Main.DownloadProgressLabel.text")); // NOI18N

        cbPortName.setEditable(true);
        cbPortName.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "USB (for Linux, Mac)", "BLUETOOTH (for Mac)",
                        "COM1:", "COM2:", "COM3:", "COM4:", "COM5:", "COM6:",
                        "COM7:", "COM8:", "COM9:", "COM10:", "COM11:",
                        "COM12:", "COM13:", "COM14:", "COM15:", "COM16:" }));
        cbPortName.setToolTipText(bundle
                .getString("BT747Main.cbPortName.toolTipText")); // NOI18N
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
        cbSerialSpeed.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "38400", "115200" }));
        cbSerialSpeed.setToolTipText(bundle
                .getString("BT747Main.cbSerialSpeed.toolTipText")); // NOI18N
        cbSerialSpeed.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbSerialSpeedFocusLost(evt);
            }
        });

        lbSerialSpeed.setText(bundle
                .getString("BT747Main.lbSerialSpeed.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnBottomInformationLayout = new org.jdesktop.layout.GroupLayout(
                pnBottomInformation);
        pnBottomInformation.setLayout(pnBottomInformationLayout);
        pnBottomInformationLayout
                .setHorizontalGroup(pnBottomInformationLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnBottomInformationLayout
                                        .createSequentialGroup()
                                        .add(btConnect)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                cbPortName,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                250,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(lbSerialSpeed)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                cbSerialSpeed,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(DownloadProgressLabel)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                DownloadProgressBar,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                90, Short.MAX_VALUE)));
        pnBottomInformationLayout
                .setVerticalGroup(pnBottomInformationLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnBottomInformationLayout
                                        .createSequentialGroup()
                                        .add(0, 0, 0)
                                        .add(
                                                pnBottomInformationLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                DownloadProgressBar,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                pnBottomInformationLayout
                                                                        .createParallelGroup(
                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                        .add(
                                                                                btConnect)
                                                                        .add(
                                                                                cbPortName,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .add(
                                                                                cbSerialSpeed,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .add(
                                                                                lbSerialSpeed)
                                                                        .add(
                                                                                DownloadProgressLabel)))));

        DownloadProgressBar.getAccessibleContext().setAccessibleName(
                bundle.getString("DownloadProgessBar")); // NOI18N
        progressBarUpdate();

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(1);
        jToolBar1.setRollover(true);

        btToolLogOps.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/bt747/j2se_view/resources/icon_home.gif"))); // NOI18N
        btToolLogOps.setText(bundle.getString("BT747Main.btToolLogOps.text")); // NOI18N
        btToolLogOps.setToolTipText(bundle
                .getString("BT747Main.btToolLogOps.toolTipText")); // NOI18N
        btToolLogOps.setFocusable(false);
        btToolLogOps
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolLogOps.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolLogOps
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolLogOps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btToolLogOpsActionPerformed(evt);
            }
        });
        jToolBar1.add(btToolLogOps);

        btToolFilesToTag.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/bt747/j2se_view/resources/images.png"))); // NOI18N
        btToolFilesToTag.setText(bundle
                .getString("BT747Main.btToolFilesToTag.text")); // NOI18N
        btToolFilesToTag.setToolTipText(bundle
                .getString("BT747Main.btToolFilesToTag.toolTipText")); // NOI18N
        btToolFilesToTag.setFocusable(false);
        btToolFilesToTag
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolFilesToTag.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolFilesToTag
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolFilesToTag
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btToolFilesToTagActionPerformed(evt);
                    }
                });
        jToolBar1.add(btToolFilesToTag);

        btToolMap.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/bt747/j2se_view/resources/world.png"))); // NOI18N
        btToolMap.setText(bundle.getString("BT747Main.btToolMap.text")); // NOI18N
        btToolMap.setToolTipText(bundle
                .getString("BT747Main.btToolMap.toolTipText")); // NOI18N
        btToolMap.setFocusable(false);
        btToolMap
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolMap.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btToolMapActionPerformed(evt);
            }
        });
        jToolBar1.add(btToolMap);

        btToolTrack.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/bt747/j2se_view/resources/database_table.png"))); // NOI18N
        btToolTrack.setText(bundle.getString("BT747Main.btToolTrack.text")); // NOI18N
        btToolTrack.setToolTipText(bundle
                .getString("BT747Main.btToolTrack.toolTipText")); // NOI18N
        btToolTrack.setFocusable(false);
        btToolTrack
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolTrack.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolTrack
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btToolTrackActionPerformed(evt);
            }
        });
        jToolBar1.add(btToolTrack);

        btToolWaypoints.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/bt747/j2se_view/resources/flag_blue.png"))); // NOI18N
        btToolWaypoints.setText(bundle
                .getString("BT747Main.btToolWaypoints.text")); // NOI18N
        btToolWaypoints.setToolTipText(bundle
                .getString("BT747Main.btToolWaypoints.toolTipText")); // NOI18N
        btToolWaypoints.setFocusable(false);
        btToolWaypoints
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolWaypoints.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolWaypoints
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolWaypoints
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btToolWaypointsActionPerformed(evt);
                    }
                });
        jToolBar1.add(btToolWaypoints);

        btToolOutputSettings
                .setIcon(new javax.swing.ImageIcon(getClass().getResource(
                        "/bt747/j2se_view/resources/icon_outputsettungs.gif"))); // NOI18N
        btToolOutputSettings.setText(bundle
                .getString("BT747Main.btToolOutputSettings.text")); // NOI18N
        btToolOutputSettings.setToolTipText(bundle
                .getString("BT747Main.btToolOutputSettings.toolTipText")); // NOI18N
        btToolOutputSettings.setFocusable(false);
        btToolOutputSettings
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolOutputSettings.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolOutputSettings
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolOutputSettings
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btToolOutputSettingsActionPerformed(evt);
                    }
                });
        jToolBar1.add(btToolOutputSettings);

        btToolFilter.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/bt747/j2se_view/resources/hourglass.png"))); // NOI18N
        btToolFilter.setText(bundle.getString("BT747Main.btToolFilter.text")); // NOI18N
        btToolFilter.setToolTipText(bundle
                .getString("BT747Main.btToolFilter.toolTipText")); // NOI18N
        btToolFilter.setFocusable(false);
        btToolFilter
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolFilter.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolFilter
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btToolFilterActionPerformed(evt);
            }
        });
        jToolBar1.add(btToolFilter);

        btToolDeviceSettings
                .setIcon(new javax.swing.ImageIcon(getClass().getResource(
                        "/bt747/j2se_view/resources/icon_devicesettungs.gif"))); // NOI18N
        btToolDeviceSettings.setText(bundle
                .getString("BT747Main.btToolDeviceSettings.text")); // NOI18N
        btToolDeviceSettings.setToolTipText(bundle
                .getString("BT747Main.btToolDeviceSettings.toolTipText")); // NOI18N
        btToolDeviceSettings.setFocusable(false);
        btToolDeviceSettings
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolDeviceSettings.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolDeviceSettings
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolDeviceSettings
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btToolDeviceSettingsActionPerformed(evt);
                    }
                });
        jToolBar1.add(btToolDeviceSettings);

        btToolAdvancedDevSettings
                .setIcon(new javax.swing.ImageIcon(
                        getClass()
                                .getResource(
                                        "/bt747/j2se_view/resources/icon_addevicesettungs.gif"))); // NOI18N
        btToolAdvancedDevSettings.setText(bundle
                .getString("BT747Main.btToolAdvancedDevSettings.text")); // NOI18N
        btToolAdvancedDevSettings
                .setToolTipText(bundle
                        .getString("BT747Main.btToolAdvancedDevSettings.toolTipText")); // NOI18N
        btToolAdvancedDevSettings.setFocusable(false);
        btToolAdvancedDevSettings
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolAdvancedDevSettings.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolAdvancedDevSettings
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolAdvancedDevSettings
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btToolAdvancedDevSettingsActionPerformed(evt);
                    }
                });
        jToolBar1.add(btToolAdvancedDevSettings);

        btToolAdvFileSettings.setIcon(new javax.swing.ImageIcon(getClass()
                .getResource("/bt747/j2se_view/resources/page_right.gif"))); // NOI18N
        btToolAdvFileSettings.setText(bundle
                .getString("BT747Main.btToolAdvFileSettings.text")); // NOI18N
        btToolAdvFileSettings.setToolTipText(bundle
                .getString("BT747Main.btToolAdvFileSettings.toolTipText")); // NOI18N
        btToolAdvFileSettings.setFocusable(false);
        btToolAdvFileSettings
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolAdvFileSettings.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolAdvFileSettings
                .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btToolAdvFileSettings
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btToolAdvFileSettingsActionPerformed(evt);
                    }
                });
        jToolBar1.add(btToolAdvFileSettings);

        btToolInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "/bt747/j2se_view/resources/information.png"))); // NOI18N
        btToolInfo.setText(bundle.getString("BT747Main.btToolInfo.text")); // NOI18N
        btToolInfo.setToolTipText(bundle
                .getString("BT747Main.btToolInfo.toolTipText")); // NOI18N
        btToolInfo.setFocusable(false);
        btToolInfo
                .setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btToolInfo.setMargin(new java.awt.Insets(3, 0, 3, 0));
        btToolInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btToolInfoActionPerformed(evt);
            }
        });
        jToolBar1.add(btToolInfo);

        tabbedPanelAll
                .setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPanelAll
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        tabbedPanelAllStateChanged(evt);
                    }
                });

        InfoPanel.setPreferredSize(null);

        jScrollPane1
                .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setOpaque(false);

        infoTextArea.setColumns(20);
        infoTextArea.setLineWrap(true);
        infoTextArea.setRows(5);
        infoTextArea.setText(bundle.getString("BT747Main.infoTextArea.text")); // NOI18N
        infoTextArea.setDragEnabled(true);
        infoTextArea.setMinimumSize(null);
        infoTextArea.setOpaque(false);
        infoTextArea.setPreferredSize(null);
        infoTextArea.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(infoTextArea);

        org.jdesktop.layout.GroupLayout InfoPanelLayout = new org.jdesktop.layout.GroupLayout(
                InfoPanel);
        InfoPanel.setLayout(InfoPanelLayout);
        InfoPanelLayout
                .setHorizontalGroup(InfoPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                InfoPanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jScrollPane1,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                629, Short.MAX_VALUE)
                                        .addContainerGap()));
        InfoPanelLayout.setVerticalGroup(InfoPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                InfoPanelLayout.createSequentialGroup().addContainerGap()
                        .add(jScrollPane1,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                301, Short.MAX_VALUE).addContainerGap()));

        tabbedPanelAll.addTab(bundle
                .getString("BT747Main.InfoPanel.TabConstraints.tabTitle"),
                new javax.swing.ImageIcon(getClass().getResource(
                        "/bt747/j2se_view/resources/information.png")),
                InfoPanel); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
                jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel2Layout.createSequentialGroup().add(jToolBar1,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        0, 0, 0).add(tabbedPanelAll,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 654,
                        Short.MAX_VALUE)));
        jPanel2Layout
                .setVerticalGroup(jPanel2Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel2Layout
                                        .createSequentialGroup()
                                        .add(0, 0, 0)
                                        .add(
                                                jPanel2Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel2Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                32,
                                                                                32,
                                                                                32)
                                                                        .add(
                                                                                jToolBar1,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                303,
                                                                                Short.MAX_VALUE)
                                                                        .addContainerGap(
                                                                                15,
                                                                                Short.MAX_VALUE))
                                                        .add(
                                                                tabbedPanelAll,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                350,
                                                                Short.MAX_VALUE))));

        tabbedPanelAll.getAccessibleContext().setAccessibleName(
                bundle.getString("Log_download_&_Convert")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
                jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING)
                .add(pnBottomInformation,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        jPanel1Layout.createSequentialGroup().add(jPanel2,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE).add(0, 0, 0)));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                jPanel1Layout.createSequentialGroup().add(jPanel2,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED).add(
                        pnBottomInformation,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

        FileMenu.setText(bundle.getString("BT747Main.FileMenu.text")); // NOI18N

        miFindSerialPorts.setText(bundle
                .getString("BT747Main.miFindSerialPorts.text")); // NOI18N
        miFindSerialPorts.setToolTipText(bundle
                .getString("BT747Main.miFindSerialPorts.toolTipText")); // NOI18N
        miFindSerialPorts
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        miFindSerialPortsActionPerformed(evt);
                    }
                });
        FileMenu.add(miFindSerialPorts);

        miMapCacheDir.setText(bundle
                .getString("BT747Main.miMapCacheDir.text")); // NOI18N
        miMapCacheDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miMapCacheDirActionPerformed(evt);
            }
        });
        FileMenu.add(miMapCacheDir);

        miExit.setText(bundle.getString("BT747Main.miExit.text")); // NOI18N
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
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

        btGPSConnectDebug.setText(bundle
                .getString("BT747Main.btGPSConnectDebug.text")); // NOI18N
        btGPSConnectDebug
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        btGPSConnectDebugStateChanged(evt);
                    }
                });
        SettingsMenu.add(btGPSConnectDebug);

        miMap.setText(bundle.getString("BT747Main.miMap.text")); // NOI18N

        miMapnik.setText(bundle.getString("BT747Main.miMapnik.text")); // NOI18N
        miMapnik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miMapnikActionPerformed(evt);
            }
        });
        miMap.add(miMapnik);

        miOsmarender.setText(bundle.getString("BT747Main.miOsmarender.text")); // NOI18N
        miOsmarender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOsmarenderActionPerformed(evt);
            }
        });
        miMap.add(miOsmarender);

        miCycle.setText(bundle.getString("BT747Main.miCycle.text")); // NOI18N
        miCycle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCycleActionPerformed(evt);
            }
        });
        miMap.add(miCycle);

        mMap4.setText(bundle.getString("BT747Main.mMap4.text")); // NOI18N
        mMap4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMap4ActionPerformed(evt);
            }
        });
        miMap.add(mMap4);

        SettingsMenu.add(miMap);

        miDownloadDevice.setText(bundle
                .getString("BT747Main.miDownloadDevice.text")); // NOI18N
        miDownloadDevice.setToolTipText(bundle
                .getString("BT747Main.miDownloadDevice.toolTipText")); // NOI18N

        miDownloadIBlue.setText(bundle
                .getString("BT747Main.btDownloadIBlue.text")); // NOI18N
        miDownloadIBlue.setToolTipText(bundle
                .getString("BT747Main.btDownloadIBlue.toolTipText")); // NOI18N
        miDownloadIBlue
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        miDownloadIBlueActionPerformed(evt);
                    }
                });
        miDownloadDevice.add(miDownloadIBlue);

        DownloadSirf.setText(bundle
                .getString("BT747Main.btDownloadFromNumerix.text")); // NOI18N
        DownloadSirf.setToolTipText(bundle
                .getString("BT747Main.btDownloadFromNumerix.toolTipText")); // NOI18N
        DownloadSirf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DownloadSirfActionPerformed(evt);
            }
        });
        miDownloadDevice.add(DownloadSirf);

        SettingsMenu.add(miDownloadDevice);

        jMenuBar.add(SettingsMenu);

        InfoMenu.setText(bundle.getString("BT747Main.InfoMenu.text")); // NOI18N

        AboutBT747.setText(bundle.getString("BT747Main.AboutBT747.text")); // NOI18N
        InfoMenu.add(AboutBT747);

        Info.setText(bundle.getString("BT747Main.Info.text")); // NOI18N
        InfoMenu.add(Info);

        mnSiteLink.setText(bundle.getString("BT747Main.mnSiteLink.text")); // NOI18N
        mnSiteLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSiteLinkActionPerformed(evt);
            }
        });
        InfoMenu.add(mnSiteLink);

        jMenuBar.add(InfoMenu);

        setJMenuBar(jMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                layout.createSequentialGroup().add(0, 0, 0).add(jPanel1,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE));

        getAccessibleContext().setAccessibleName(
                "MTK Datalogger Control (BT747)");
    }// GEN-END:initComponents

    private void cbSerialSpeedFocusLost(final java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbSerialSpeedFocusLost
        // TODO add your handling code here:
    }// GEN-LAST:event_cbSerialSpeedFocusLost

    private void miExitActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miExitActionPerformed
        exitApplication();
    }// GEN-LAST:event_miExitActionPerformed

    private void btGPSDebugStateChanged(
            final javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_btGPSDebugStateChanged
        c.setDebug(btGPSDebug.isSelected());
    }// GEN-LAST:event_btGPSDebugStateChanged

    private void btGPSConnectDebugStateChanged(
            final javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_btGPSConnectDebugStateChanged
        c.setDebugConn(btGPSConnectDebug.isSelected());
    }// GEN-LAST:event_btGPSConnectDebugStateChanged

    private void cbPortNameActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbPortNameActionPerformed
        // selectPort(jComboBox1.getSelectedItem().toString());
    }// GEN-LAST:event_cbPortNameActionPerformed

    private void btConnectActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btConnectActionPerformed

        if (btConnectFunctionIsConnect) {
            openPort(cbPortName.getSelectedItem().toString());
        } else {
            c.closeGPS();
        }
    }// GEN-LAST:event_btConnectActionPerformed

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

    private void openPort(final String s) {
        boolean foundPort = false;
        int port = 0;
        try {
            port = Integer.parseInt(s);
            if (String.valueOf(port).equals(s)) {
                foundPort = true;
            }
        } catch (final Exception e) {
            // Ignore exception
        }
        try {
            if (!foundPort && s.toUpperCase().startsWith("COM")) {
                if ((s.length() == 5) && (s.charAt(4) == ':')) {
                    port = Integer.parseInt(s.substring(3, 4));
                } else if ((s.length() == 6) && (s.charAt(5) == ':')) {
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
        } catch (final Exception e) {
            // Ignore exception
        }
        try {
            c.setBaudRate(Integer.parseInt(cbSerialSpeed.getSelectedItem()
                    .toString()));
        } catch (final Exception e) {

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
        if (m.getStringOpt(AppSettings.FREETEXTPORT).length() != 0) {
            cbPortName.setSelectedItem(m
                    .getStringOpt(AppSettings.FREETEXTPORT));
        } else if (m.getIntOpt(AppSettings.PORTNBR) >= 0) {
            cbPortName.setSelectedItem("COM"
                    + m.getIntOpt(AppSettings.PORTNBR) + ":"); // NOI18N

        } else {
            // Do nothing
        }
    }

    /**
     * @param args
     *                the command line arguments
     */
    public static void main(final String args[]) {
        if (args.length >= 1) {
            if (args[0].equals("arch")) {
                System.out.print(java.lang.System.getProperty("os.arch"));
            } else {
                BT747cmd.main(args);
            }
            return;
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            final J2SEAppModel m = new J2SEAppModel();
            final J2SEAppController c = new J2SEAppController(m);

            public final void run() {
                final BT747Main app = new BT747Main(m, c);
                try {
                    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } catch (final Exception e) {
                    // A security exception could be thrown - ignore it.
                }
                app.setVisible(true);
            }
        });
    }

    private void miMapCacheDirActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miMapCacheDirActionPerformed
        c.selectMapCacheDirectory();
    }// GEN-LAST:event_miMapCacheDirActionPerformed

    private void miFindSerialPortsActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miFindSerialPortsActionPerformed
        // Looking for ports asynchronously
        new Thread() {
            public final void run() {
                addPortsToGui();
            }
        }.start();

    }// GEN-LAST:event_miFindSerialPortsActionPerformed

    private void miMapnikActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miMapnikActionPerformed
        c.setIntOpt(Model.MAPTYPE, MyMap.MapType.OpenStreetMap.ordinal());
    }// GEN-LAST:event_miMapnikActionPerformed

    private void miOsmarenderActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miOsmarenderActionPerformed
        c.setIntOpt(Model.MAPTYPE, MyMap.MapType.OsmaRender.ordinal());
    }// GEN-LAST:event_miOsmarenderActionPerformed

    private void miCycleActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miCycleActionPerformed
        c.setIntOpt(Model.MAPTYPE, MyMap.MapType.Cycle.ordinal());
    }// GEN-LAST:event_miCycleActionPerformed

    private void mMap4ActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mMap4ActionPerformed
        c.setIntOpt(Model.MAPTYPE, MyMap.MapType.Map4.ordinal());
    }// GEN-LAST:event_mMap4ActionPerformed

    private void mnSiteLinkActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mnSiteLinkActionPerformed
        BareBonesBrowserLaunch.openURL("http://bt747.free.fr");
        // BrowserControl.displayURL("http://bt747.free.fr");
    }// GEN-LAST:event_mnSiteLinkActionPerformed

    private void btToolLogOpsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolLogOpsActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnLogOperationsPanel
                    .getParent().getParent());
        } catch (Exception e) {

        }
    }// GEN-LAST:event_btToolLogOpsActionPerformed

    private void btToolFilesToTagActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolFilesToTagActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnFilesToTagPanel);
        } catch (Exception e) {

        }

    }// GEN-LAST:event_btToolFilesToTagActionPerformed

    private void btToolMapActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolMapActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnMap);
        } catch (Exception e) {

        }

    }// GEN-LAST:event_btToolMapActionPerformed

    private void btToolTrackActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolTrackActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(trackPanel);
        } catch (Exception e) {

        }

    }// GEN-LAST:event_btToolTrackActionPerformed

    private void btToolWaypointsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolWaypointsActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(waypointPanel);
        } catch (Exception e) {

        }

    }// GEN-LAST:event_btToolWaypointsActionPerformed

    private void btToolOutputSettingsActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolOutputSettingsActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnOutputSettingsPanel
                    .getParent().getParent());
        } catch (Exception e) {

        }

    }// GEN-LAST:event_btToolOutputSettingsActionPerformed

    private void btToolFilterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolFilterActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnFiltersPanel.getParent()
                    .getParent());
        } catch (Exception e) {

        }

    }// GEN-LAST:event_btToolFilterActionPerformed

    private void btToolDeviceSettingsActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolDeviceSettingsActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnDeviceSettingsPanel
                    .getParent().getParent());
        } catch (Exception e) {

        }
    }// GEN-LAST:event_btToolDeviceSettingsActionPerformed

    private void btToolAdvancedDevSettingsActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolAdvancedDevSettingsActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnAdvancedSettingsPanel
                    .getParent().getParent());
        } catch (Exception e) {

        }

    }// GEN-LAST:event_btToolAdvancedDevSettingsActionPerformed

    private void btToolAdvFileSettingsActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolAdvFileSettingsActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(pnAdvancedFileSettingsPanel
                    .getParent().getParent());
        } catch (Exception e) {

        }
    }// GEN-LAST:event_btToolAdvFileSettingsActionPerformed

    private void btToolInfoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btToolInfoActionPerformed
        try {
            tabbedPanelAll.setSelectedComponent(InfoPanel);
        } catch (Exception e) {

        }
    }// GEN-LAST:event_btToolInfoActionPerformed

    private void miDownloadIBlueActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_miDownloadIBlueActionPerformed
        c.setIntOpt(Model.DOWNLOAD_DEVICE, 0);
    }// GEN-LAST:event_miDownloadIBlueActionPerformed

    private void DownloadSirfActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_DownloadSirfActionPerformed
        c.setIntOpt(Model.DOWNLOAD_DEVICE, 1);
    }// GEN-LAST:event_DownloadSirfActionPerformed

    private void tabbedPanelAllStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_tabbedPanelAllStateChanged
        updateToolBarButton();
    }// GEN-LAST:event_tabbedPanelAllStateChanged

    // public static void main(String args) {
    // main((String[])null);
    // }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutBT747;
    private javax.swing.JProgressBar DownloadProgressBar;
    private javax.swing.JLabel DownloadProgressLabel;
    private javax.swing.JRadioButtonMenuItem DownloadSirf;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuItem Info;
    private javax.swing.JMenu InfoMenu;
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JMenu SettingsMenu;
    private javax.swing.JButton btConnect;
    private javax.swing.JRadioButtonMenuItem btGPSConnectDebug;
    private javax.swing.JRadioButtonMenuItem btGPSDebug;
    private javax.swing.JButton btToolAdvFileSettings;
    private javax.swing.JButton btToolAdvancedDevSettings;
    private javax.swing.JButton btToolDeviceSettings;
    private javax.swing.JButton btToolFilesToTag;
    private javax.swing.JButton btToolFilter;
    private javax.swing.JButton btToolInfo;
    private javax.swing.JButton btToolLogOps;
    private javax.swing.JButton btToolMap;
    private javax.swing.JButton btToolOutputSettings;
    private javax.swing.JButton btToolTrack;
    private javax.swing.JButton btToolWaypoints;
    private javax.swing.JComboBox cbPortName;
    private javax.swing.JComboBox cbSerialSpeed;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbSerialSpeed;
    private javax.swing.JRadioButtonMenuItem mMap4;
    private javax.swing.JRadioButtonMenuItem miCycle;
    private javax.swing.JMenu miDownloadDevice;
    private javax.swing.JRadioButtonMenuItem miDownloadIBlue;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miFindSerialPorts;
    private javax.swing.JMenu miMap;
    private javax.swing.JMenuItem miMapCacheDir;
    private javax.swing.JRadioButtonMenuItem miMapnik;
    private javax.swing.JRadioButtonMenuItem miOsmarender;
    private javax.swing.JMenuItem mnSiteLink;
    private javax.swing.JPanel pnBottomInformation;
    private javax.swing.JTabbedPane tabbedPanelAll;
    // End of variables declaration//GEN-END:variables

}
