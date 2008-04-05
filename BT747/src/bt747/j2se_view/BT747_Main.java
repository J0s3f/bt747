/*
 * BT747_Main.java
 *
 * Created on 23 mars 2008, 10:37
 */
package bt747.j2se_view;

import gps.BT747_dev;
import gps.GPSListener;
import gps.GpsEvent;
import gps.convert.Conv;
import gps.log.GPSRecord;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import bt747.Txt;
import bt747.control.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Convert;

/**
 * 
 * @author Mario De Weerd
 */
public class BT747_Main extends javax.swing.JFrame implements
        bt747.model.ModelListener, GPSListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Model m;
    Controller c;

    /** Creates new form BT747_Main */
    public BT747_Main() {
        initComponents();
    }

    public BT747_Main(Model m, Controller c) {
        this.m = m;
        this.c = c;
        initComponents();
        initAppData();
        m.addListener(this);
        c.addGPSListener(this);
    }

    public void newEvent(bt747.ui.Event e) {
        onEvent(e);
    }

    private void initAppData() {
        progressBarUpdate();
        getWorkDirPath();
        getRawLogFilePath();
        getOutputFilePath();
        getIncremental();
        getDefaultPort();
        updateSatGuiItems();

        // TODO: Deactivate debug by default
        c.setDebug(true);
        c.setDebugConn(true);
        // c.setChunkSize(256); // Small for debug

        switch (m.getBinDecoder()) {
        case Controller.DECODER_ORG:
            cbDecoderChoice.setSelectedIndex(0);
            break;
        case Controller.DECODER_THOMAS:
            cbDecoderChoice.setSelectedIndex(1);
            break;
        }
        lbConversionTime.setVisible(false);
    }

    private long conversionStartTime;

    public void onEvent(bt747.ui.Event e) {
        int type = e.getType();
        if (type == ModelEvent.DOWNLOAD_PROGRESS_UPDATE) {
            progressBarUpdate();
        } else if (type == ModelEvent.LOGFILEPATH_UPDATE) {
            getRawLogFilePath();
        } else if (type == ModelEvent.OUTPUTFILEPATH_UPDATE) {
            getOutputFilePath();
        } else if (type == ModelEvent.WORKDIRPATH_UPDATE) {
            getWorkDirPath();
        } else if (type == ModelEvent.INCREMENTAL_CHANGE) {
            getIncremental();
        } else if (type == ModelEvent.CONVERSION_STARTED) {
            conversionStartTime = System.currentTimeMillis();
        } else if (type == ModelEvent.CONVERSION_ENDED) {
            lbConversionTime
                    .setText("Time to convert: "
                            + ((int) (System.currentTimeMillis() - conversionStartTime))
                            + " ms");
            lbConversionTime.setVisible(true);
        }
    }

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
            bt747.sys.Time t = new bt747.sys.Time();
            t.setUTCTime(gps.utc);
            TimeStr =
            // Convert.toString(t.getYear())+"/"
            // +( t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"/"
            // +( t.getDay()<10?"0":"")+Convert.toString(t.getDay())+" " +
            (t.getHour() < 10 ? "0" : "") + Convert.toString(t.getHour()) + ":"
                    + (t.getMinute() < 10 ? "0" : "")
                    + Convert.toString(t.getMinute()) + ":"
                    + (t.getSecond() < 10 ? "0" : "")
                    + Convert.toString(t.getSecond());
            lbTime.setText(TimeStr);
        }
        updateGPSData(gps);
    }

    private void updateGPSData(final GPSRecord gps) {

        lbLatitude.setText(Convert.toString(gps.latitude, 5));
        // lbHeight.setText(Convert.toString(gps.height,3)+Txt.METERS_ABBR);
        lbLongitude.setText(Convert.toString(gps.longitude, 5));
        lbGeoid.setText(Convert.toString(gps.geoid, 3)
                + Txt.METERS_ABBR
                + Txt.CALC
                + Convert.toString(Conv.wgs84_separation(gps.latitude,
                        gps.longitude), 3) + Txt.METERS_ABBR + ")");

    }

    public void gpsEvent(GpsEvent e) {
        // TODO Auto-generated method stub
        int type = e.getType();
        if (type == GpsEvent.GPRMC) {
            updateRMCData((GPSRecord) e.getArg());
        } else if (type == GpsEvent.DATA_UPDATE) {
        } else if (type == GpsEvent.GPGGA) {
            updateGPSData((GPSRecord) e.getArg());
        } else if (type == GpsEvent.CONNECTED) {
        } else if (type == GpsEvent.LOG_FORMAT_UPDATE) {
            updateLogFormatData();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WorkingDirectoryChooser = new javax.swing.JFileChooser();
        RawLogFileChooser = new javax.swing.JFileChooser();
        OutputFileChooser = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        LogOperationsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        tfWorkDirectory = new javax.swing.JTextField();
        tfRawLogFilePath = new javax.swing.JTextField();
        tfOutputFileBaseName = new javax.swing.JTextField();
        btWorkingDirectory = new javax.swing.JButton();
        btRawLogFile = new javax.swing.JButton();
        btOutputFile = new javax.swing.JButton();
        btConvert = new javax.swing.JButton();
        cbFormat = new javax.swing.JComboBox();
        cbDecoderChoice = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        cbIncremental = new javax.swing.JCheckBox();
        DownloadProgressLabel = new javax.swing.JLabel();
        DownloadProgressBar = new javax.swing.JProgressBar();
        lbConversionTime = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btConnect = new javax.swing.JButton();
        cbPortName = new javax.swing.JComboBox();
        LogFiltersPanel = new javax.swing.JPanel();
        DeviceSettingsPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        cbLat = new javax.swing.JCheckBox();
        cbLong = new javax.swing.JCheckBox();
        cbHeight = new javax.swing.JCheckBox();
        cbSpeed = new javax.swing.JCheckBox();
        cbHeading = new javax.swing.JCheckBox();
        cbDistance = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        cbNSAT = new javax.swing.JCheckBox();
        cbSID = new javax.swing.JCheckBox();
        cbElevation = new javax.swing.JCheckBox();
        cbAzimuth = new javax.swing.JCheckBox();
        cbSNR = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        cbUTCTime = new javax.swing.JCheckBox();
        cbMilliSeconds = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        cbDSTA = new javax.swing.JCheckBox();
        cbDAGE = new javax.swing.JCheckBox();
        cbPDOP = new javax.swing.JCheckBox();
        cbHDOP = new javax.swing.JCheckBox();
        cbVDOP = new javax.swing.JCheckBox();
        cbFixType = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        cbRCR = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        cbHoluxM241 = new javax.swing.JCheckBox();
        btFormatAndErase = new javax.swing.JButton();
        btFormat = new javax.swing.JButton();
        btErase = new javax.swing.JButton();
        btRecoverMemory = new javax.swing.JButton();
        GPSDecodePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbLatitude = new javax.swing.JLabel();
        lbLongitude = new javax.swing.JLabel();
        lbTime = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbGeoid = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        SettingsMenu = new javax.swing.JMenu();
        InfoMenu = new javax.swing.JMenu();
        AboutBT747 = new javax.swing.JMenuItem();
        jMenuBar2 = new javax.swing.JMenuBar();
        FileMenu1 = new javax.swing.JMenu();
        SettingsMenu1 = new javax.swing.JMenu();
        InfoMenu1 = new javax.swing.JMenu();
        AboutBT748 = new javax.swing.JMenuItem();

        WorkingDirectoryChooser.setDialogTitle("Choose Working Directory");
        WorkingDirectoryChooser.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
        WorkingDirectoryChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BT747 Application");

        tfWorkDirectory.setText("jTextField1");

        tfRawLogFilePath.setText("jTextField2");

        tfOutputFileBaseName.setText("jTextField3");

        btWorkingDirectory.setText("Working Directory :");
        btWorkingDirectory.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btWorkingDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btWorkingDirectoryActionPerformed(evt);
            }
        });

        btRawLogFile.setText("Raw Log File :");
        btRawLogFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btRawLogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRawLogFileActionPerformed(evt);
            }
        });

        btOutputFile.setText("Output File :");
        btOutputFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btOutputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOutputFileActionPerformed(evt);
            }
        });

        btConvert.setText("Convert To");
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

        cbDecoderChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Original", "Thomas's version" }));
        cbDecoderChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDecoderChoiceActionPerformed(evt);
            }
        });

        jLabel5.setText("Decoder for raw data:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(btOutputFile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(btRawLogFile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(btWorkingDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tfRawLogFilePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                    .add(tfWorkDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(tfOutputFileBaseName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(btConvert))
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbDecoderChoice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tfWorkDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btWorkingDirectory))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(tfRawLogFilePath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btRawLogFile))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btOutputFile)
                    .add(tfOutputFileBaseName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btConvert)
                    .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbDecoderChoice, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        tfWorkDirectory.setText(m.getBaseDirPath());
        setSelectedFormat(cbFormat.getSelectedItem().toString());

        jButton1.setText("Download Log");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cbIncremental.setText("Incremental Download");
        cbIncremental.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIncrementalActionPerformed(evt);
            }
        });

        DownloadProgressLabel.setText("Download progress");

        DownloadProgressBar.setBackground(javax.swing.UIManager.getDefaults().getColor("nbProgressBar.Foreground"));
        DownloadProgressBar.setForeground(new java.awt.Color(204, 255, 204));
        DownloadProgressBar.setFocusable(false);

        lbConversionTime.setText("jLabel6");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 33, Short.MAX_VALUE)
                        .add(cbIncremental))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(DownloadProgressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(DownloadProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(lbConversionTime)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(cbIncremental))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lbConversionTime)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(DownloadProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(DownloadProgressLabel))
                .addContainerGap())
        );

        DownloadProgressBar.getAccessibleContext().setAccessibleName("DownloadProgessBar");
        progressBarUpdate();

        btConnect.setText("Connect");
        btConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConnectActionPerformed(evt);
            }
        });

        cbPortName.setEditable(true);
        cbPortName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "USB (for Linux, Mac)", "BLUETOOTH (for Mac)", "COM1:", "COM2:", "COM3:", "COM4:", "COM5:", "COM6:", "COM7:", "COM8:", "COM9:", "COM10:", "COM11:", "COM12:", "COM13:", "COM14:", "COM15:", "COM16:" }));
        cbPortName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPortNameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(btConnect)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbPortName, 0, 152, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btConnect)
                    .add(cbPortName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout LogOperationsPanelLayout = new org.jdesktop.layout.GroupLayout(LogOperationsPanel);
        LogOperationsPanel.setLayout(LogOperationsPanelLayout);
        LogOperationsPanelLayout.setHorizontalGroup(
            LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, LogOperationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(LogOperationsPanelLayout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        LogOperationsPanelLayout.setVerticalGroup(
            LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, LogOperationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(LogOperationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Log operations", LogOperationsPanel);

        org.jdesktop.layout.GroupLayout LogFiltersPanelLayout = new org.jdesktop.layout.GroupLayout(LogFiltersPanel);
        LogFiltersPanel.setLayout(LogFiltersPanelLayout);
        LogFiltersPanelLayout.setHorizontalGroup(
            LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 560, Short.MAX_VALUE)
        );
        LogFiltersPanelLayout.setVerticalGroup(
            LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 305, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Filters", LogFiltersPanel);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Log Format"));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Position"));

        cbLat.setText("Latitude");

        cbLong.setText("Longitude");

        cbHeight.setText("Height");

        cbSpeed.setText("Speed");

        cbHeading.setText("Heading");

        cbDistance.setText("Distance");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbLat)
                    .add(cbHeight)
                    .add(cbLong)
                    .add(cbSpeed)
                    .add(cbHeading)
                    .add(cbDistance))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
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

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Sat Info"));

        cbNSAT.setText("NSAT");

        cbSID.setText("SID");
        cbSID.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbSIDItemStateChanged(evt);
            }
        });

        cbElevation.setText("Elevation");

        cbAzimuth.setText("Azimuth");

        cbSNR.setText("SNR");

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbNSAT)
            .add(cbSID)
            .add(cbElevation)
            .add(cbAzimuth)
            .add(cbSNR)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
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

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Time"));

        cbUTCTime.setText("UTC Time");

        cbMilliSeconds.setText("Milliseconds");

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbUTCTime)
            .add(cbMilliSeconds)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(cbUTCTime)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbMilliSeconds)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Precision"));

        cbDSTA.setText("DSTA");

        cbDAGE.setText("DAGE");

        cbPDOP.setText("PDOP");

        cbHDOP.setText("HDOP");

        cbVDOP.setText("VDOP");

        cbFixType.setText("GPS Fix Type");

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbDSTA)
            .add(cbDAGE)
            .add(cbPDOP)
            .add(cbHDOP)
            .add(cbVDOP)
            .add(cbFixType)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(cbFixType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(cbDSTA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbDAGE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbPDOP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbHDOP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbVDOP)
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Reason"));

        cbRCR.setText("RCR");
        cbRCR.setToolTipText("Log reason (such as: time, speed, distance, button press");

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(cbRCR)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(cbRCR)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Other"));

        cbHoluxM241.setText("Holux M-241");
        cbHoluxM241.setToolTipText("Usually indicates that this is a Holux M241 device using a 'different format'.\n\nKeep the original device setting.");

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(cbHoluxM241)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(cbHoluxM241, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 27, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        btFormatAndErase.setText("Set Format & Erase");
        btFormatAndErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFormatAndEraseActionPerformed(evt);
            }
        });

        btFormat.setText("Set Format Only");
        btFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFormatActionPerformed(evt);
            }
        });

        btErase.setText("Erase only");
        btErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEraseActionPerformed(evt);
            }
        });

        btRecoverMemory.setText("Try to recover faulty memory");
        btRecoverMemory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRecoverMemoryActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout DeviceSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(DeviceSettingsPanel);
        DeviceSettingsPanel.setLayout(DeviceSettingsPanelLayout);
        DeviceSettingsPanelLayout.setHorizontalGroup(
            DeviceSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(DeviceSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(DeviceSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btFormat)
                    .add(btFormatAndErase)
                    .add(btErase)
                    .add(btRecoverMemory))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        DeviceSettingsPanelLayout.setVerticalGroup(
            DeviceSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(DeviceSettingsPanelLayout.createSequentialGroup()
                .add(DeviceSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(DeviceSettingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(DeviceSettingsPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(btFormatAndErase)
                        .add(18, 18, 18)
                        .add(btFormat)
                        .add(18, 18, 18)
                        .add(btErase)
                        .add(18, 18, 18)
                        .add(btRecoverMemory)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Device settings", DeviceSettingsPanel);

        jLabel1.setText("Latitude :");

        jLabel2.setText("Longitude :");

        jLabel3.setText("GPS Time :");

        lbLatitude.setText("jLabel4");

        lbLongitude.setText("jLabel4");

        lbTime.setText("jLabel5");

        jLabel4.setText("Geoid :");

        lbGeoid.setText("jLabel5");

        org.jdesktop.layout.GroupLayout GPSDecodePanelLayout = new org.jdesktop.layout.GroupLayout(GPSDecodePanel);
        GPSDecodePanel.setLayout(GPSDecodePanelLayout);
        GPSDecodePanelLayout.setHorizontalGroup(
            GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GPSDecodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(GPSDecodePanelLayout.createSequentialGroup()
                        .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lbLatitude)
                            .add(lbLongitude)))
                    .add(GPSDecodePanelLayout.createSequentialGroup()
                        .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lbGeoid)
                            .add(lbTime))))
                .addContainerGap(455, Short.MAX_VALUE))
        );
        GPSDecodePanelLayout.setVerticalGroup(
            GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(GPSDecodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(lbLatitude))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(lbLongitude))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(GPSDecodePanelLayout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4))
                    .add(GPSDecodePanelLayout.createSequentialGroup()
                        .add(lbTime)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lbGeoid)))
                .addContainerGap(220, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("GPS Decode", GPSDecodePanel);

        FileMenu.setText("File");
        jMenuBar1.add(FileMenu);

        SettingsMenu.setText("Settings");
        jMenuBar1.add(SettingsMenu);

        InfoMenu.setText("Info");

        AboutBT747.setText(null);
        InfoMenu.add(AboutBT747);

        jMenuBar1.add(InfoMenu);

        FileMenu1.setText("File");
        jMenuBar2.add(FileMenu1);

        SettingsMenu1.setText("Settings");
        jMenuBar2.add(SettingsMenu1);

        InfoMenu1.setText("Info");

        AboutBT748.setText(null);
        InfoMenu1.add(AboutBT748);

        jMenuBar2.add(InfoMenu1);

        setJMenuBar(jMenuBar2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Log download & Convert");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btFormatAndEraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFormatAndEraseActionPerformed
        c.changeLogFormatAndErase(getUserLogFormat());
    }//GEN-LAST:event_btFormatAndEraseActionPerformed

    private void btFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFormatActionPerformed
        c.changeLogFormat(getUserLogFormat());
    }//GEN-LAST:event_btFormatActionPerformed

    private void btEraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEraseActionPerformed
        c.eraseLogFormat();
    }//GEN-LAST:event_btEraseActionPerformed

    private void btRecoverMemoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRecoverMemoryActionPerformed
        c.forceErase();
    }//GEN-LAST:event_btRecoverMemoryActionPerformed

    private void cbSIDItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbSIDItemStateChanged
        updateSatGuiItems();
    }//GEN-LAST:event_cbSIDItemStateChanged

    
    private void cbPortNameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbPortNameActionPerformed
        // selectPort(jComboBox1.getSelectedItem().toString());
    }// GEN-LAST:event_cbPortNameActionPerformed

    private void btConnectActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btConnectActionPerformed
        openPort(cbPortName.getSelectedItem().toString());
    }// GEN-LAST:event_btConnectActionPerformed

    private void cbIncrementalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbIncrementalActionPerformed
        c.setIncremental(cbIncremental.isSelected());
    }// GEN-LAST:event_cbIncrementalActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        c.startDownload();
    }// GEN-LAST:event_jButton1ActionPerformed

    private void cbDecoderChoiceActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbDecoderChoiceActionPerformed
        // TODO add your handling code here:
        switch (cbDecoderChoice.getSelectedIndex()) {
        case 0:
            c.setBinDecoder(c.DECODER_ORG);
            break;
        case 1:
            c.setBinDecoder(c.DECODER_THOMAS);
            break;
        }
    }// GEN-LAST:event_cbDecoderChoiceActionPerformed


    private void cbFormatItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFormatItemStateChanged
        // TODO add your handling code here:
        switch (evt.getStateChange()) {
        case java.awt.event.ItemEvent.SELECTED:
            setSelectedFormat(evt.getItem().toString());
            break;
        case java.awt.event.ItemEvent.DESELECTED:
            break;
        }
    }// GEN-LAST:event_cbFormatItemStateChanged

    private void btConvertActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btConvertActionPerformed
        c.writeLog(selectedFormat);
    }// GEN-LAST:event_btConvertActionPerformed

    private void btOutputFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btOutputFileActionPerformed
        getOutputFilePath();
        if (OutputFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            c.setOutputFileBasePath(gps.convert.FileUtil.getRelativePath(m
                    .getBaseDirPath(), OutputFileChooser.getSelectedFile()
                    .getAbsolutePath(), File.separatorChar));
        }
    }// GEN-LAST:event_btOutputFileActionPerformed

    private void btRawLogFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btRawLogFileActionPerformed
        getRawLogFilePath();
        if (RawLogFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            c.setLogFilePath(gps.convert.FileUtil.getRelativePath(m
                    .getBaseDirPath(), RawLogFileChooser.getSelectedFile()
                    .getAbsolutePath(), File.separatorChar));
        }
    }// GEN-LAST:event_btRawLogFileActionPerformed

    private void btWorkingDirectoryActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btWorkingDirectoryActionPerformed
        getWorkDirPath();
        if (WorkingDirectoryChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            c.setBaseDirPath(WorkingDirectoryChooser.getSelectedFile()
                    .getAbsolutePath());
        }
    }// GEN-LAST:event_btWorkingDirectoryActionPerformed

    private int selectedFormat = Model.C_NO_LOG;

    private final void setSelectedFormat(final String selected) {
        if (selected.startsWith("CSV")) {
            selectedFormat = Model.C_CSV_LOG;
        } else if (selected.startsWith("Google Map")) {
            selectedFormat = Model.C_GMAP_LOG;
        } else if (selected.startsWith("GPX")) {
            selectedFormat = Model.C_GPX_LOG;
        } else if (selected.startsWith("KML")) {
            selectedFormat = Model.C_KML_LOG;
        } else if (selected.startsWith("NMEA")) {
            selectedFormat = Model.C_NMEA_LOG;
        } else if (selected.startsWith("Ozi")) {
            selectedFormat = Model.C_PLT_LOG;
        } else if (selected.startsWith("Compe")) {
            selectedFormat = Model.C_TRK_LOG;
        } else {
            selectedFormat = Model.C_NO_LOG;
        }
    }


    private void updateSatGuiItems() {
        boolean enable;
        enable=cbSID.isSelected();
        cbSNR.setEnabled(enable);
        cbAzimuth.setEnabled(enable);
        cbElevation.setEnabled(enable);

    }
    
    private void updateLogFormatData() {
        int logFormat = m.getLogFormat();

        cbUTCTime.setSelected((logFormat & (1 << BT747_dev.FMT_UTC_IDX)) != 0);
        cbFixType.setSelected((logFormat & (1 << BT747_dev.FMT_VALID_IDX)) != 0);
        cbLat.setSelected((logFormat & (1 << BT747_dev.FMT_LATITUDE_IDX)) != 0);
        cbLong
                .setSelected((logFormat & (1 << BT747_dev.FMT_LONGITUDE_IDX)) != 0);
        cbHeight.setSelected((logFormat & (1 << BT747_dev.FMT_HEIGHT_IDX)) != 0);
        cbSpeed.setSelected((logFormat & (1 << BT747_dev.FMT_SPEED_IDX)) != 0);
        cbHeading
                .setSelected((logFormat & (1 << BT747_dev.FMT_HEADING_IDX)) != 0);
        cbDSTA.setSelected((logFormat & (1 << BT747_dev.FMT_DSTA_IDX)) != 0);
        cbDAGE.setSelected((logFormat & (1 << BT747_dev.FMT_DAGE_IDX)) != 0);
        cbPDOP.setSelected((logFormat & (1 << BT747_dev.FMT_PDOP_IDX)) != 0);
        cbHDOP.setSelected((logFormat & (1 << BT747_dev.FMT_HDOP_IDX)) != 0);
        cbVDOP.setSelected((logFormat & (1 << BT747_dev.FMT_VDOP_IDX)) != 0);
        cbNSAT.setSelected((logFormat & (1 << BT747_dev.FMT_NSAT_IDX)) != 0);
        cbSID.setSelected((logFormat & (1 << BT747_dev.FMT_SID_IDX)) != 0);
        cbElevation
                .setSelected((logFormat & (1 << BT747_dev.FMT_ELEVATION_IDX)) != 0);
        cbAzimuth
                .setSelected((logFormat & (1 << BT747_dev.FMT_AZIMUTH_IDX)) != 0);
        cbSNR.setSelected((logFormat & (1 << BT747_dev.FMT_SNR_IDX)) != 0);
        cbRCR.setSelected((logFormat & (1 << BT747_dev.FMT_RCR_IDX)) != 0);
        cbMilliSeconds
                .setSelected((logFormat & (1 << BT747_dev.FMT_MILLISECOND_IDX)) != 0);
        cbDistance
                .setSelected((logFormat & (1 << BT747_dev.FMT_DISTANCE_IDX)) != 0);
        cbHoluxM241
                .setSelected((logFormat & (1 << BT747_dev.FMT_HOLUX_LOW_PRECISION_IDX)) != 0);

    }

    private int getUserLogFormat() {
        int logFormat = m.getLogFormat();

        if(cbUTCTime.isSelected()) logFormat|=(1<<BT747_dev.FMT_UTC_IDX);
        if(cbFixType.isSelected()) logFormat|=(1<<BT747_dev.FMT_VALID_IDX);
        if(cbLat.isSelected()) logFormat|=(1<<BT747_dev.FMT_LATITUDE_IDX);
        if(cbLong.isSelected()) logFormat|=(1<<BT747_dev.FMT_LONGITUDE_IDX);
        if(cbHeight.isSelected()) logFormat|=(1<<BT747_dev.FMT_HEIGHT_IDX);
        if(cbSpeed.isSelected()) logFormat|=(1<<BT747_dev.FMT_SPEED_IDX);
        if(cbHeading.isSelected()) logFormat|=(1<<BT747_dev.FMT_HEADING_IDX);
        if(cbDSTA.isSelected()) logFormat|=(1<<BT747_dev.FMT_DSTA_IDX);
        if(cbDAGE.isSelected()) logFormat|=(1<<BT747_dev.FMT_DAGE_IDX);
        if(cbPDOP.isSelected()) logFormat|=(1<<BT747_dev.FMT_PDOP_IDX);
        if(cbHDOP.isSelected()) logFormat|=(1<<BT747_dev.FMT_HDOP_IDX);
        if(cbVDOP.isSelected()) logFormat|=(1<<BT747_dev.FMT_VDOP_IDX);
        if(cbNSAT.isSelected()) logFormat|=(1<<BT747_dev.FMT_NSAT_IDX);
        if(cbSID.isSelected()) logFormat|=(1<<BT747_dev.FMT_SID_IDX);
        if(cbElevation.isSelected()) logFormat|=(1<<BT747_dev.FMT_ELEVATION_IDX);
        if(cbAzimuth.isSelected()) logFormat|=(1<<BT747_dev.FMT_AZIMUTH_IDX);
        if(cbSNR.isSelected()) logFormat|=(1<<BT747_dev.FMT_SNR_IDX);
        if(cbRCR.isSelected()) logFormat|=(1<<BT747_dev.FMT_RCR_IDX);
        if(cbMilliSeconds.isSelected()) logFormat|=(1<<BT747_dev.FMT_MILLISECOND_IDX);
        if(cbDistance.isSelected()) logFormat|=(1<<BT747_dev.FMT_DISTANCE_IDX);
        if(cbHoluxM241.isSelected()) logFormat|=(1<<BT747_dev.FMT_HOLUX_LOW_PRECISION_IDX);

        return logFormat;
    }

    private void progressBarUpdate() {
        DownloadProgressBar.setMinimum(m.getStartAddr());
        DownloadProgressBar.setMaximum(m.getEndAddr());
        DownloadProgressBar.setValue(m.getNextReadAddr());
        DownloadProgressBar.setVisible(m.isDownloadOnGoing());
        DownloadProgressLabel.setVisible(m.isDownloadOnGoing());
        // this.invalidate();
        // this.paintAll(this.getGraphics());
    }

    private void getOutputFilePath() {
        File curDir;
        curDir = new File(m.getReportFileBasePath());
        // if (curDir.exists()) {
        OutputFileChooser.setCurrentDirectory(curDir);
        // }
        tfOutputFileBaseName.setText(m.getReportFileBase());
    }

    private void getRawLogFilePath() {
        File curDir;
        curDir = new File(m.getLogFilePath());
        // if (curDir.exists()) {
        RawLogFileChooser.setCurrentDirectory(curDir);
        // }
        tfRawLogFilePath.setText(m.getLogFile());
    }

    private void getWorkDirPath() {
        File curDir;
        curDir = new File(m.getBaseDirPath());
        if (curDir.exists()) {
            WorkingDirectoryChooser.setCurrentDirectory(curDir);
        }
        tfWorkDirectory.setText(curDir.getPath());
    }

    private void getIncremental() {
        cbIncremental.setSelected(m.isIncremental());
    }

    private void openPort(String s) {
        boolean foundPort = false;
        int port = 0;
        try {
            port = Convert.toInt(s);
            if (Convert.toString(port).equals(s)) {
                foundPort = true;
            }
        } catch (Exception e) {
            // Ignore exception
        }
        try {
            if (!foundPort && s.toUpperCase().startsWith("COM")) {
                if (s.length() == 5 && s.charAt(4) == ':') {
                    port = Convert.toInt(s.substring(3, 4));
                } else if (s.length() == 6 && s.charAt(5) == ':') {
                    port = Convert.toInt(s.substring(3, 5));
                } else {
                    port = Convert.toInt(s.substring(3));
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
        if (foundPort) {
            c.setPort(port);
        } else {
            if (s.toUpperCase().equals("BLUETOOTH")) {
                c.setBluetooth();
            } else if (s.toUpperCase().startsWith("USB")) {
                c.setUsb();
            } else {
                c.setFreeTextPort(s);
            }
        }
    }

    private void getDefaultPort() {
        if (m.getFreeTextPort().length() != 0) {
            cbPortName.setSelectedItem(m.getFreeTextPort());
        } else if (m.getPortnbr() >= 0) {
            cbPortName.setSelectedItem("COM" + m.getPortnbr() + ":");// getSelectedItem().toString();
        } else {
            // Do nothing
        }
    }

    /***************************************************************************
     * Find the appropriate look and feel for the system
     **************************************************************************/
    private static final String[] lookAndFeels = {
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel",
            "com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
            "javax.swing.plaf.metal.MetalLookAndFeel",
            "javax.swing.plaf.mac.MacLookAndFeel" };
    /* Index for Mac look and feel */
    private static final int C_MAC_LOOKANDFEEL_IDX = lookAndFeels.length - 1;

    /**
     * Try setting a look and feel for the system - catch the Exception when not
     * found.
     * 
     * @return true if successfull
     */
    private final static boolean tryLookAndFeel(String s) {
        try {
            UIManager.setLookAndFeel(s);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Set a good look and feel for the system.
     */
    public static void myLookAndFeel() {
        boolean lookAndFeelIsSet = false;
        if (java.lang.System.getProperty("os.name").toLowerCase().startsWith(
                "mac")) {
            lookAndFeelIsSet = tryLookAndFeel(lookAndFeels[C_MAC_LOOKANDFEEL_IDX]);
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
    public static void main(String args[]) {
        myLookAndFeel();
        java.awt.EventQueue.invokeLater(new Runnable() {

            Model m = new Model();
            Controller c = new Controller(m);

            public void run() {
                BT747_Main app = new BT747_Main(m, c);
                app.setVisible(true);
            }
        });
    }

    // public static void main(String args) {
    // main((String[])null);
    // }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutBT747;
    private javax.swing.JMenuItem AboutBT748;
    private javax.swing.JPanel DeviceSettingsPanel;
    private javax.swing.JProgressBar DownloadProgressBar;
    private javax.swing.JLabel DownloadProgressLabel;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu FileMenu1;
    private javax.swing.JPanel GPSDecodePanel;
    private javax.swing.JMenu InfoMenu;
    private javax.swing.JMenu InfoMenu1;
    private javax.swing.JPanel LogFiltersPanel;
    private javax.swing.JPanel LogOperationsPanel;
    private javax.swing.JFileChooser OutputFileChooser;
    private javax.swing.JFileChooser RawLogFileChooser;
    private javax.swing.JMenu SettingsMenu;
    private javax.swing.JMenu SettingsMenu1;
    private javax.swing.JFileChooser WorkingDirectoryChooser;
    private javax.swing.JButton btConnect;
    private javax.swing.JButton btConvert;
    private javax.swing.JButton btErase;
    private javax.swing.JButton btFormat;
    private javax.swing.JButton btFormatAndErase;
    private javax.swing.JButton btOutputFile;
    private javax.swing.JButton btRawLogFile;
    private javax.swing.JButton btRecoverMemory;
    private javax.swing.JButton btWorkingDirectory;
    private javax.swing.JCheckBox cbAzimuth;
    private javax.swing.JCheckBox cbDAGE;
    private javax.swing.JCheckBox cbDSTA;
    private javax.swing.JComboBox cbDecoderChoice;
    private javax.swing.JCheckBox cbDistance;
    private javax.swing.JCheckBox cbElevation;
    private javax.swing.JCheckBox cbFixType;
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JCheckBox cbHDOP;
    private javax.swing.JCheckBox cbHeading;
    private javax.swing.JCheckBox cbHeight;
    private javax.swing.JCheckBox cbHoluxM241;
    private javax.swing.JCheckBox cbIncremental;
    private javax.swing.JCheckBox cbLat;
    private javax.swing.JCheckBox cbLong;
    private javax.swing.JCheckBox cbMilliSeconds;
    private javax.swing.JCheckBox cbNSAT;
    private javax.swing.JCheckBox cbPDOP;
    private javax.swing.JComboBox cbPortName;
    private javax.swing.JCheckBox cbRCR;
    private javax.swing.JCheckBox cbSID;
    private javax.swing.JCheckBox cbSNR;
    private javax.swing.JCheckBox cbSpeed;
    private javax.swing.JCheckBox cbUTCTime;
    private javax.swing.JCheckBox cbVDOP;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbConversionTime;
    private javax.swing.JLabel lbGeoid;
    private javax.swing.JLabel lbLatitude;
    private javax.swing.JLabel lbLongitude;
    private javax.swing.JLabel lbTime;
    private javax.swing.JTextField tfOutputFileBaseName;
    private javax.swing.JTextField tfRawLogFilePath;
    private javax.swing.JTextField tfWorkDirectory;
    // End of variables declaration//GEN-END:variables

}
