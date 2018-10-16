// ********************************************************************
// *** BT747 ***
// *** (c)2007-2008 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package bt747.j2se_view;

import gps.BT747Constants;
import gps.ProtocolConstants;
import gps.convert.ExternalUtils;
import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.ComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import net.sf.bt747.j2se.app.filefilters.BinFileFilter;
import net.sf.bt747.j2se.app.filefilters.CSVFileFilter;
import net.sf.bt747.j2se.app.filefilters.ResultFileFilter;
import net.sf.bt747.j2se.app.filefilters.WPFileFilter;
import net.sf.bt747.j2se.app.filefilters.GPXFileFilter;
import net.sf.bt747.j2se.app.filefilters.HoluxTRLFileFilter;
import net.sf.bt747.j2se.app.filefilters.KnownFileFilter;
import net.sf.bt747.j2se.app.filefilters.NMEAFileFilter;
import net.sf.bt747.j2se.app.utils.Utils;

import org.jdesktop.swingx.JXDatePicker;

import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.JavaLibBridge;
import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Time;

/**
 * J2SE Implementation (GUI) of BT747.
 * 
 * @author Mario De Weerd
 */
public class LogOperationsPanel extends javax.swing.JPanel implements
        bt747.model.ModelListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Model m;
    private J2SEAppController c;

    // TODO: remove this part after updating GUI. Handled in updateGui.
    private static final ComboBoxModel modelGpsType = new javax.swing.DefaultComboBoxModel(
            new String[] { "" });

    /** Creates new form BT747Main */
    public LogOperationsPanel() {
        initComponents();
        lbBusySpinner.setVisible(false);
        //btOTHERFmt.setVisible(false);
    }

    public void init(final J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);
        initAppData();
    }

    private final String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    private final static String HOLUX_M241 = "Holux M-241/M1000C";
    private final static String HOLUX_GR245 = "Holux GPSport 245/RCV3000";
    private final static String SKYTRAQ = "Skytraq";
    
    private void updateGuiData() {
        cbGPSType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("DEFAULT_DEVICE"), HOLUX_M241, "iTrackU-Nemerix",
                "PhotoTrackr", "iTrackU-SIRFIII", HOLUX_GR245, SKYTRAQ }));
        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("TABLE_Description"), getString("GPX_Description"),
                getString("GPX_ForOSMDescription"),
                getString("GMAP_Description"), getString("CSV_Description"),
                getString("KML_Description"), getString("KMZ_Description"),
                getString("NMEA_Description"), getString("OZI_Description"),
                getString("Compe_Description"), getString("SQL_Description"),
                getString("GoogleStaticURL_Description"),
                getString("POSTGIS_Description"),
                getString("C_GPS2KML_Description"),
                }));
        cbDownloadMethod.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { getString("DOWNLOAD_INCREMENTAL"),
                        getString("DOWNLOAD_FULL"),
                        getString("DOWNLOAD_FILLED") }));
    }

    /**
     * Initialize application data. Gets the values from the model to set them
     * in the GUI.
     */
    private void initAppData() {
        updateGuiData(); // For internationalisation - not so easy in
        // netbeans
        getWorkDirPath();
        getRawLogFilePath();
        getOutputFilePath();
        getDownloadMethod();
        cbLoggingActive.setSelected(m.isLoggingActive());
        lbConversionTime.setVisible(false);

        updateCbGPSType();

        cbDisableLoggingDuringDownload.setSelected(m
                .getBooleanOpt(Model.DISABLELOGDURINGDOWNLOAD));

        BT747Time d;
        d = JavaLibBridge.getTimeInstance();
        d.setUTCTime(m.getFilterStartTime());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT")); // NO18N
        // cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(d.getYear(), d.getMonth() - 1, d.getDay(), 0, 0, 0);
        // startDate.getDateEditor().
        startDate.setDate(cal.getTime());
        updateStartDate();
        // startDate.setCalendar(cal);
        d.setUTCTime(m.getFilterEndTime());
        cal = Calendar.getInstance();
        // cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(d.getYear(), d.getMonth() - 1, d.getDay(), 0, 0, 0);
        endDate.setDate(cal.getTime());
        updateEndDate();
        setTimeSplit();
        // endDate.setCalendar(cal);
        // TODO: Deactivate debug by default
        // c.setDebug(true);

        updateConnected(m.isConnected());
    }

    private final void updateConnected(final boolean connected) {
        final JPanel[] panels = { GPSDecodePanel };

        if (!connected) {
            resetGPSData();
        }
        btDownload.setEnabled(connected);
        for (final JPanel panel : panels) {
            J2SEAppController.enableComponentHierarchy(panel, connected);
        }
    }

    private long conversionStartTime;

    private void updateRMCData(final GPSRecord gps) {
        if (gps.hasUtc()) {
            txtTime.setText(CommonOut.getTimeStr(gps.utc)); // NO18N
            txtTime.setToolTipText(CommonOut.getDateStr(gps.utc)); // NO18N
        }
        updateGPSData(gps);
    }

    private final void updateGPSData(final GPSRecord gps) {
        if (m.isConnected() && gps.hasPosition()) {
            txtLatitude.setText(Utils.format("%.8f", gps.latitude)); // NOI18N
            // lbHeight.setText(String.valueOf(gps.height,3)+"m");
            txtLongitude.setText(Utils.format("%.8f", gps.longitude)); // NOI18N
            if (gps.hasHeight()) {
                txtGeoid.setText(Utils.format("%.1f", gps.geoid) // NOI18N
                        + getString("m")
                        + " "
                        + getString("(calc:")
                        + Utils.format("%.1f", ExternalUtils.wgs84Separation(
                                gps.latitude, gps.longitude))
                        + getString("m") + ")"); // NOI18N
            }
        }
    }

    private final void resetGPSData() {
        txtLatitude.setText(getString("BT747Main.txtLatitude.text")); // NOI18N
        txtLongitude.setText(getString("BT747Main.txtLongitude.text")); // NOI18N
        txtTime.setText(getString("BT747Main.txtTime.text")); // NOI18N
        txtGeoid.setText(getString("BT747Main.txtGeoid.text")); // NOI18N
        // txtFlashInfo.setText(getString("BT747Main.txtFlashInfo.text")); //
        // NOI18N
        // txtModel.setText(getString("BT747Main.txtModel.text")); // NOI18N
        // txtFirmWare.setText(getString("BT747Main.txtFirmWare.text")); //
        // NOI18N
        // txtLoggerSWVersion.setText(getString("BT747Main.txtLoggerSWVersion.text"));
        // // NOI18N
        // txtMemoryUsed.setText(getString("BT747Main.txtMemoryUsed.text"));
        // // NOI18N
    }

    /**
     * 
     */
    private final void doLogConversion(final int selectedFormat) {
        c.setChangeToMap(true);
        c.setLogConversionParameters();
        c.doLogConversion(selectedFormat);
    }

    public void modelEvent(final ModelEvent e) {
        try {
        final int type = e.getType();
        switch (type) {

        case ModelEvent.SETTING_CHANGE:
            // int arg = Integer.valueOf((String) e.getArg());
            // switch (arg) {
            // case Model.MAPTYPE:
            // updateMapType();
            // break;
            // }
            break;
        case ModelEvent.GPRMC:
            updateRMCData((GPSRecord) e.getArg());
            break;
        case ModelEvent.UPDATE_LOG_NBR_LOG_PTS:
        case ModelEvent.UPDATE_LOG_MEM_USED:
            txtMemoryUsed.setToolTipText(m.logMemUsed()+" "
                    + getString("bytesused"));
            txtMemoryUsed.setText(m.logMemUsed() + " ("
                    + m.logMemUsedPercent() + "%)"); // NOI18N
            txtMemoryUsed.setText(m.logNbrLogPts() + " " + getString("pts")
                    + " (" + m.logMemUsedPercent() + "%)"); // NOI18N
            // TODO
            // + m.getEstimatedNbrRecordsFree(m.getLogFormat()) + " "
            break;
        case ModelEvent.UPDATE_LOG_FLASH:
            txtFlashInfo
                    .setText(((m.getFlashManuProdID() != 0) ? JavaLibBridge
                            .unsigned2hex(m.getFlashManuProdID(), 8)
                            + " " + m.getFlashDesc() : "")); // NOI18N
            break;
        // case ModelEvent.ERASE_ONGOING_NEED_POPUP:
        // c.createErasePopup();
        // break;
        // case ModelEvent.ERASE_DONE_REMOVE_POPUP:
        // c.removeErasePopup();
        // break;

        case ModelEvent.UPDATE_MTK_VERSION:
        case ModelEvent.UPDATE_MTK_RELEASE:
            txtModel.setText(m.getModelStr());
            String fwString;
            fwString = "";
            lbFirmWare.setToolTipText("");
            if (m.getMainVersion().length() + m.getFirmwareVersion().length() != 0) {
                final int LIMIT = 17;
                if (m.getMainVersion().length()
                        + m.getFirmwareVersion().length() > LIMIT) {
                    fwString = "<html>";
                    if (m.getMainVersion().length() > LIMIT) {
                        fwString += m.getMainVersion()
                                .substring(0, LIMIT - 3)
                                + "...";
                        lbFirmWare.setToolTipText(m.getMainVersion());
                    } else {
                        fwString += m.getMainVersion();
                    }
                    fwString += (m.getMainVersion().length() != 0 ? "<br>"
                            : "")
                            + m.getFirmwareVersion(); // NOI18N
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
        case ModelEvent.CONNECTED:
        case ModelEvent.DISCONNECTED:
            updateConnected(m.isConnected());
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
        case ModelEvent.DOWNLOAD_METHOD_CHANGE:
            getDownloadMethod();
            break;
        case ModelEvent.CONVERSION_STARTED:
            conversionStartTime = System.currentTimeMillis();
            lbConversionTime.setVisible(false);
            lbBusySpinner.setVisible(true);
            lbBusySpinner.setEnabled(true);
            lbBusySpinner.setBusy(true);
            lbBusySpinner.repaint();
            btConvert.setText(getString("Stop_Convert.text"));
            break;
        case ModelEvent.CONVERSION_ENDED:

            // lbConversionTime.setText(
            Generic
                    .debug(getString("Time_to_convert:_")
                            + ((int) (System.currentTimeMillis() - conversionStartTime))
                            + getString("_ms"));
            // lbConversionTime.setVisible(true);
            lbBusySpinner.setVisible(false);
            lbBusySpinner.setBusy(false);
            lbBusySpinner.repaint();
            btConvert.setText(getString("BT747Main.btConvert.text"));
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            c.replyToOkToOverwrite(c.getRequestToOverwriteFromDialog());
            break;
        case ModelEvent.COULD_NOT_OPEN_FILE:
            c.couldNotOpenFileMessage((String) e.getArg());
            break;
        }
        } catch (BT747Exception b) {
            J2SEAppController.notifyBT747Exception(b);
        }
    }

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

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
        cbDisableLoggingDuringDownload = new javax.swing.JCheckBox();
        cbDownloadMethod = new javax.swing.JComboBox();
        btDownload = new javax.swing.JButton();
        pnConvert = new javax.swing.JPanel();
        pnLeftConversion = new javax.swing.JPanel();
        lbConversionTime = new javax.swing.JLabel();
        lbDeviceType = new javax.swing.JLabel();
        btConvert = new javax.swing.JButton();
        pnDateFilter = new javax.swing.JPanel();
        lbToDate = new javax.swing.JLabel();
        lbFromDate = new javax.swing.JLabel();
        lbHour = new javax.swing.JLabel();
        txtTimeSplit = new javax.swing.JLabel();
        lbMinutes = new javax.swing.JLabel();
        startDate = new org.jdesktop.swingx.JXDatePicker();
        endDate = new org.jdesktop.swingx.JXDatePicker();
        spTimeSplitHours = new javax.swing.JSpinner();
        spTimeSplitMinutes = new javax.swing.JSpinner();
        cbFormat = new javax.swing.JComboBox();
        lbBusySpinner = new org.jdesktop.swingx.JXBusyLabel();
        cbGPSType = new javax.swing.JComboBox();
        pnConvButtons = new javax.swing.JPanel();
        btGPX = new javax.swing.JButton();
        btKML = new javax.swing.JButton();
        btKMZ = new javax.swing.JButton();
        btNMEA = new javax.swing.JButton();
        btHTML = new javax.swing.JButton();
        btCSV = new javax.swing.JButton();
        btGUI = new javax.swing.JButton();
        btOTHERFmt = new javax.swing.JButton();
        btOSMUp = new javax.swing.JButton();
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

        setName("BT747Frame"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        pnFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFiles.border.title"))); // NOI18N

        tfWorkDirectory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfWorkDirectoryFocusLost(evt);
            }
        });

        tfRawLogFilePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfRawLogFilePathFocusLost(evt);
            }
        });

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
                    .add(org.jdesktop.layout.GroupLayout.LEADING, tfRawLogFilePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .add(pnFilesLayout.createSequentialGroup()
                        .add(tfOutputFileBaseName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lbNoFileExt))
                    .add(tfWorkDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
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

        cbDisableLoggingDuringDownload.setText(bundle.getString("BT747Main.cbDisableLoggingDuringDownload.text")); // NOI18N
        cbDisableLoggingDuringDownload.setToolTipText(bundle.getString("BT747Main.cbDisableLoggingDuringDownload.toolTipText")); // NOI18N
        cbDisableLoggingDuringDownload.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbDisableLoggingDuringDownloadFocusLost(evt);
            }
        });

        cbDownloadMethod.setToolTipText(bundle.getString("BT747Main.cbDownloadMethod.toolTipText")); // NOI18N

        org.jdesktop.layout.GroupLayout pnDownloadMethodLayout = new org.jdesktop.layout.GroupLayout(pnDownloadMethod);
        pnDownloadMethod.setLayout(pnDownloadMethodLayout);
        pnDownloadMethodLayout.setHorizontalGroup(
            pnDownloadMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbDisableLoggingDuringDownload)
            .add(cbDownloadMethod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        pnDownloadMethodLayout.setVerticalGroup(
            pnDownloadMethodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnDownloadMethodLayout.createSequentialGroup()
                .add(cbDownloadMethod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(cbDisableLoggingDuringDownload))
        );

        btDownload.setText(bundle.getString("LogOperationsPanel.btDownload.text")); // NOI18N
        btDownload.setToolTipText(bundle.getString("LogOperationsPanel.btDownload.toolTipText")); // NOI18N
        btDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDownloadActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnDownloadLayout = new org.jdesktop.layout.GroupLayout(pnDownload);
        pnDownload.setLayout(pnDownloadLayout);
        pnDownloadLayout.setHorizontalGroup(
            pnDownloadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDownloadLayout.createSequentialGroup()
                .add(pnDownloadMethod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btDownload))
        );
        pnDownloadLayout.setVerticalGroup(
            pnDownloadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDownloadMethod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnDownloadLayout.createSequentialGroup()
                .addContainerGap()
                .add(btDownload))
        );

        pnConvert.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnConvert.border.title"))); // NOI18N

        lbConversionTime.setText(bundle.getString("BT747Main.lbConversionTime.text")); // NOI18N
        lbConversionTime.setToolTipText(bundle.getString("BT747Main.lbConversionTime.toolTipText")); // NOI18N

        lbDeviceType.setText(bundle.getString("BT747Main.lbDeviceType.text")); // NOI18N

        btConvert.setText(bundle.getString("BT747Main.btConvert.text")); // NOI18N
        btConvert.setToolTipText(bundle.getString("BT747Main.btConvert.toolTipText")); // NOI18N
        btConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConvertActionPerformed(evt);
            }
        });

        pnDateFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnDateFilter.border.title"))); // NOI18N

        lbToDate.setText(bundle.getString("BT747Main.lbToDate.text")); // NOI18N

        lbFromDate.setText(bundle.getString("BT747Main.lbFromDate.text")); // NOI18N

        lbHour.setText(bundle.getString("BT747Main.lbHour.text")); // NOI18N

        txtTimeSplit.setText(bundle.getString("BT747Main.txtTimeSplit.text")); // NOI18N
        txtTimeSplit.setToolTipText(bundle.getString("BT747Main.txtTimeSplit.toolTipText")); // NOI18N

        lbMinutes.setText(bundle.getString("BT747Main.lbMinutes.text")); // NOI18N

        startDate.setToolTipText(bundle.getString("BT747Main.startDate.toolTipText")); // NOI18N
        startDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDateActionPerformed(evt);
            }
        });

        endDate.setToolTipText(bundle.getString("BT747Main.endDate.toolTipText")); // NOI18N
        endDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endDateActionPerformed(evt);
            }
        });

        spTimeSplitHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        spTimeSplitHours.setToolTipText(bundle.getString("BT747Main.spTimeSplitHours.toolTipText")); // NOI18N
        spTimeSplitHours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spTimeSplitHoursFocusLost(evt);
            }
        });

        spTimeSplitMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spTimeSplitMinutes.setToolTipText(bundle.getString("BT747Main.spTimeSplitMinutes.toolTipText")); // NOI18N
        spTimeSplitMinutes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spTimeSplitMinutesFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnDateFilterLayout = new org.jdesktop.layout.GroupLayout(pnDateFilter);
        pnDateFilter.setLayout(pnDateFilterLayout);
        pnDateFilterLayout.setHorizontalGroup(
            pnDateFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDateFilterLayout.createSequentialGroup()
                .add(lbFromDate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbToDate)
                .add(4, 4, 4)
                .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTimeSplit)
                .add(4, 4, 4)
                .add(spTimeSplitHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbHour, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTimeSplitMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbMinutes))
        );
        pnDateFilterLayout.setVerticalGroup(
            pnDateFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDateFilterLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(lbFromDate)
                .add(startDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lbToDate)
                .add(endDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(txtTimeSplit)
                .add(spTimeSplitHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lbHour)
                .add(spTimeSplitMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lbMinutes))
        );

        cbFormat.setToolTipText(bundle.getString("BT747Main.cbFormat.toolTipText")); // NOI18N
        cbFormat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFormatItemStateChanged(evt);
            }
        });

        lbBusySpinner.setText(bundle.getString("BT747Main.lbBusySpinner.text")); // NOI18N
        lbBusySpinner.setOpaque(true);

        cbGPSType.setModel(modelGpsType);
        cbGPSType.setToolTipText(bundle.getString("BT747Main.cbGPSType.toolTipText")); // NOI18N
        cbGPSType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbGPSTypeFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnLeftConversionLayout = new org.jdesktop.layout.GroupLayout(pnLeftConversion);
        pnLeftConversion.setLayout(pnLeftConversionLayout);
        pnLeftConversionLayout.setHorizontalGroup(
            pnLeftConversionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLeftConversionLayout.createSequentialGroup()
                .add(lbBusySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btConvert)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lbDeviceType)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbConversionTime))
            .add(pnDateFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        pnLeftConversionLayout.setVerticalGroup(
            pnLeftConversionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLeftConversionLayout.createSequentialGroup()
                .add(pnDateFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLeftConversionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnLeftConversionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(btConvert)
                        .add(cbFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lbDeviceType)
                        .add(cbGPSType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lbConversionTime))
                    .add(lbBusySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        btGPX.setText(bundle.getString("BT747Main.btGPX.text")); // NOI18N
        btGPX.setToolTipText(bundle.getString("BT747Main.btGPX.toolTipText")); // NOI18N
        btGPX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGPXActionPerformed(evt);
            }
        });

        btKML.setText(bundle.getString("BT747Main.btKML.text")); // NOI18N
        btKML.setToolTipText(bundle.getString("BT747Main.btKML.toolTipText")); // NOI18N
        btKML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btKMLActionPerformed(evt);
            }
        });

        btKMZ.setText(bundle.getString("BT747Main.btKMZ.text")); // NOI18N
        btKMZ.setToolTipText(bundle.getString("BT747Main.btKMZ.toolTipText")); // NOI18N
        btKMZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btKMZActionPerformed(evt);
            }
        });

        btNMEA.setText(bundle.getString("BT747Main.btNMEA.text")); // NOI18N
        btNMEA.setToolTipText(bundle.getString("BT747Main.btNMEA.toolTipText")); // NOI18N
        btNMEA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNMEAActionPerformed(evt);
            }
        });

        btHTML.setText(bundle.getString("BT747Main.btHTML.text")); // NOI18N
        btHTML.setToolTipText(bundle.getString("BT747Main.btHTML.toolTipText")); // NOI18N
        btHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btHTMLActionPerformed(evt);
            }
        });

        btCSV.setText(bundle.getString("BT747Main.btCSV.text")); // NOI18N
        btCSV.setToolTipText(bundle.getString("BT747Main.btCSV.toolTipText")); // NOI18N
        btCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCSVActionPerformed(evt);
            }
        });

        btGUI.setText(bundle.getString("LogOperationsPanel.btGUI.text")); // NOI18N
        btGUI.setToolTipText(bundle.getString("LogOperationsPanel.btGUI.toolTipText")); // NOI18N
        btGUI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGUIActionPerformed(evt);
            }
        });

        btOTHERFmt.setText(bundle.getString("LogOperationsPanel.btOTHERFmt.text")); // NOI18N
        btOTHERFmt.setToolTipText(bundle.getString("LogOperationsPanel.btOTHERFmt.toolTipText")); // NOI18N
        btOTHERFmt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOTHERFmtActionPerformed(evt);
            }
        });

        btOSMUp.setText(bundle.getString("LogOperationsPanel.btOSMUp.text")); // NOI18N
        btOSMUp.setToolTipText(bundle.getString("LogOperationsPanel.btOSMUp.toolTipText")); // NOI18N
        btOSMUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOSMUpActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnConvButtonsLayout = new org.jdesktop.layout.GroupLayout(pnConvButtons);
        pnConvButtons.setLayout(pnConvButtonsLayout);
        pnConvButtonsLayout.setHorizontalGroup(
            pnConvButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvButtonsLayout.createSequentialGroup()
                .add(pnConvButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(btGPX)
                    .add(btKML)
                    .add(btKMZ))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnConvButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btCSV)
                    .add(btHTML)
                    .add(btNMEA))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnConvButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btOSMUp)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btGUI)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btOTHERFmt))
                .add(0, 0, 0))
        );

        pnConvButtonsLayout.linkSize(new java.awt.Component[] {btCSV, btGPX, btHTML, btKML, btKMZ, btNMEA}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnConvButtonsLayout.linkSize(new java.awt.Component[] {btGUI, btOSMUp, btOTHERFmt}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnConvButtonsLayout.setVerticalGroup(
            pnConvButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvButtonsLayout.createSequentialGroup()
                .add(btGPX)
                .add(0, 0, 0)
                .add(btKML)
                .add(0, 0, 0)
                .add(btKMZ))
            .add(pnConvButtonsLayout.createSequentialGroup()
                .add(btCSV)
                .add(0, 0, 0)
                .add(btHTML)
                .add(0, 0, 0)
                .add(btNMEA))
            .add(pnConvButtonsLayout.createSequentialGroup()
                .add(btOSMUp)
                .add(0, 0, 0)
                .add(btGUI)
                .add(0, 0, 0)
                .add(btOTHERFmt))
        );

        org.jdesktop.layout.GroupLayout pnConvertLayout = new org.jdesktop.layout.GroupLayout(pnConvert);
        pnConvert.setLayout(pnConvertLayout);
        pnConvertLayout.setHorizontalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvertLayout.createSequentialGroup()
                .add(pnConvButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLeftConversion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pnConvertLayout.setVerticalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvertLayout.createSequentialGroup()
                .add(pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnConvButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnLeftConversion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, 0))
        );

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
        cbLoggingActive.setToolTipText(bundle.getString("BT747Main.cbLoggingActive.toolTipText")); // NOI18N
        cbLoggingActive.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbLoggingActiveItemStateChanged(evt);
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnDownload, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(GPSDecodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pnConvert, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(pnFiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnDownload, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(GPSDecodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, 0)
                .add(pnConvert, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        getAccessibleContext().setAccessibleName("MTK Datalogger Control (BT747)");
    }//GEN-END:initComponents

    private void cbDisableLoggingDuringDownloadFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbDisableLoggingDuringDownloadFocusLost
        c.setBooleanOpt(Model.DISABLELOGDURINGDOWNLOAD,
                cbDisableLoggingDuringDownload.isSelected());
    }//GEN-LAST:event_cbDisableLoggingDuringDownloadFocusLost

    private void cbGPSTypeFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbGPSTypeFocusLost
        int type;
        switch (cbGPSType.getSelectedIndex()) {
        case 1:
            type = BT747Constants.GPS_TYPE_HOLUX_M241;
            break;
        case 2:
            type = BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX;
            break;
        case 3:
            type = BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR;
            break;
        case 4:
            type = BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII;
            break;
        case 5:
            type = BT747Constants.GPS_TYPE_HOLUX_GR245;
            break;
        case 6:
            type = BT747Constants.GPS_TYPE_SKYTRAQ;
            break;
        case 0:
        default:
            type = BT747Constants.GPS_TYPE_DEFAULT;
        }
        c.setIntOpt(AppSettings.GPSTYPE, type);
    }//GEN-LAST:event_cbGPSTypeFocusLost

    private final void updateCbGPSType() {
            int index = 0;
        switch (m.getIntOpt(AppSettings.GPSTYPE)) {
        case BT747Constants.GPS_TYPE_DEFAULT:
            index = 0;
            break;
        case BT747Constants.GPS_TYPE_HOLUX_M241:
            index = 1;
            break;
        case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
            index = 2;
            break;
        case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
            index = 3;
            break;
        case BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
            index = 4;
            break;
        case BT747Constants.GPS_TYPE_HOLUX_GR245:
            index = 5;
            break;
        case BT747Constants.GPS_TYPE_SKYTRAQ:
            index = 6;
            break;        
            }
        cbGPSType.setSelectedIndex(index);
    }

    private void tfRawLogFilePathFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfRawLogFilePathFocusLost

        c.setStringOpt(AppSettings.LOGFILEPATH, tfRawLogFilePath.getText());
    }//GEN-LAST:event_tfRawLogFilePathFocusLost

    private void cbFormatItemStateChanged(final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFormatItemStateChanged
        switch (evt.getStateChange()) {
        case java.awt.event.ItemEvent.SELECTED:
            // getSelectedFormat(evt.getItem().toString());
            break;
        case java.awt.event.ItemEvent.DESELECTED:
            break;
        }
    }//GEN-LAST:event_cbFormatItemStateChanged

    private void btConvertActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConvertActionPerformed
        if (btConvert.getText().equals(getString("Stop_Convert.text"))) {
            c.stopLogConvert();
        } else {
            doLogConversion(getSelectedFormat(cbFormat.getSelectedItem()
                    .toString()));
        }
    }//GEN-LAST:event_btConvertActionPerformed

    private void btOutputFileActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOutputFileActionPerformed
        javax.swing.JFileChooser OutputFileChooser;
        final File f = getOutputFilePath();
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
                if (relPath.lastIndexOf('.') == relPath.length() - 4) {
                    relPath = relPath.substring(0, relPath.length() - 4);
                }
                c.setOutputFileRelPath(relPath);
                getOutputFilePath();
                tfOutputFileBaseName.setCaretPosition(tfOutputFileBaseName
                        .getText().length());

            } catch (final Exception e) {
                Generic.debug(getString("OutputFileChooser"), e);
            }
        }
    }//GEN-LAST:event_btOutputFileActionPerformed

    private void btRawLogFileActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRawLogFileActionPerformed
        javax.swing.JFileChooser RawLogFileChooser;
        File f = getRawLogFilePath();
        RawLogFileChooser = new javax.swing.JFileChooser(f.getParent());

        RawLogFileChooser.setSelectedFile(f);
        f = null;
        // getRawLogFilePath();
        // if (curDir.exists()) {
        // RawLogFileChooser.setCurrentDirectory(getRawLogFilePath());
        // }
        RawLogFileChooser.setAcceptAllFileFilterUsed(true);
        RawLogFileChooser.addChoosableFileFilter(new BinFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new GPXFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new CSVFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new HoluxTRLFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new NMEAFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new WPFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new ResultFileFilter());
        final KnownFileFilter ff = new KnownFileFilter();
        RawLogFileChooser.addChoosableFileFilter(ff);
        RawLogFileChooser.setFileFilter(ff);
        if (RawLogFileChooser.showDialog(this, getString("SetRawLogFile")) == JFileChooser.APPROVE_OPTION) {
            try {
                c.setStringOpt(AppSettings.LOGFILEPATH, RawLogFileChooser
                        .getSelectedFile().getCanonicalPath());
            } catch (final Exception e) {
                Generic.debug(getString("RawFileChooser"), e);
            }
            tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
            tfRawLogFilePath.setCaretPosition(tfRawLogFilePath.getText()
                    .length());
        }
    }//GEN-LAST:event_btRawLogFileActionPerformed

    private void btWorkingDirectoryActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btWorkingDirectoryActionPerformed
        javax.swing.JFileChooser WorkingDirectoryChooser;
        final File curDir = getWorkDirPath();
        // if (curDir.exists()) {
        WorkingDirectoryChooser = new javax.swing.JFileChooser(curDir);
        // } else {
        // WorkingDirectoryChooser = new javax.swing.JFileChooser();
        // }

        WorkingDirectoryChooser
                .setDialogTitle(getString("Choose_Working_Directory"));
        WorkingDirectoryChooser
                .setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        if (WorkingDirectoryChooser.showDialog(this, getString("SetWorkDir")) == JFileChooser.APPROVE_OPTION) {
            try {
                c.setStringOpt(AppSettings.OUTPUTDIRPATH,
                        WorkingDirectoryChooser.getSelectedFile()
                                .getCanonicalPath());
            } catch (final Exception e) {
                Generic.debug(getString("WorkingDirChooser"), e);
            }
            tfWorkDirectory
                    .setText(m.getStringOpt(AppSettings.OUTPUTDIRPATH));
            tfWorkDirectory.setCaretPosition(tfWorkDirectory.getText()
                    .length());
        }
    }//GEN-LAST:event_btWorkingDirectoryActionPerformed

    private final int getSelectedFormat(final String selected) {
        int selectedFormat = Model.NO_LOG_LOGTYPE;
        if (selected.equals(getString("CSV_Description"))) {
            selectedFormat = Model.CSV_LOGTYPE;
        } else if (selected.equals(getString("GMAP_Description"))) {
            selectedFormat = Model.GMAP_LOGTYPE;
        } else if (selected.equals(getString("GPX_Description"))) {
            selectedFormat = Model.GPX_LOGTYPE;
        } else if (selected.equals(getString("GPX_ForOSMDescription"))) {
            selectedFormat = Model.OSM_LOGTYPE;
        } else if (selected.equals(getString("KML_Description"))) {
            selectedFormat = Model.KML_LOGTYPE;
        } else if (selected.equals(getString("NMEA_Description"))) {
            selectedFormat = Model.NMEA_LOGTYPE;
        } else if (selected.equals(getString("OZI_Description"))) {
            selectedFormat = Model.PLT_LOGTYPE;
        } else if (selected.equals(getString("Compe_Description"))) {
            selectedFormat = Model.TRK_LOGTYPE;
        } else if (selected.equals(getString("KMZ_Description"))) {
            selectedFormat = Model.KMZ_LOGTYPE;
        } else if (selected.equals(getString("TABLE_Description"))) {
            selectedFormat = Model.ARRAY_LOGTYPE;
        } else if (selected.equals(getString("POSTGIS_Description"))) {
            selectedFormat = Model.POSTGIS_LOGTYPE;
        } else if (selected.equals(getString("SQL_Description"))) {
            selectedFormat = Model.SQL_LOGTYPE;
        } else if (selected.equals(getString("GoogleStaticURL_Description"))) {
            selectedFormat = Model.GOOGLE_MAP_STATIC_URL_LOGTYPE;
        } else if (selected.equals(getString("C_GPS2KML_Description"))) {
            selectedFormat = Model.EXTERNAL_GPS2KML_LOGTYPE;
        } else {
            selectedFormat = Model.NO_LOG_LOGTYPE;
        }
        return selectedFormat;
    }

    private File getOutputFilePath() {
        File curDir;
        curDir = new File(m.getPath(AppSettings.REPORTFILEBASEPATH).getPath());
        tfOutputFileBaseName.setText(m
                .getStringOpt(AppSettings.REPORTFILEBASE));
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

    private void getDownloadMethod() {
        switch (m.getDownloadMethod()) {
        case Model.DOWNLOAD_FILLED:
            cbDownloadMethod.setSelectedItem(getString("DOWNLOAD_FILLED"));
            break;
        case Model.DOWNLOAD_FULL:
            cbDownloadMethod.setSelectedItem(getString("DOWNLOAD_FULL"));
            break;
        case Model.DOWNLOAD_SMART:
            cbDownloadMethod
                    .setSelectedItem(getString("DOWNLOAD_INCREMENTAL"));
            break;
        }
    }

    private void setDownloadMethod() {
        final String v = (String) cbDownloadMethod.getSelectedItem();
        if (v.equals(getString("DOWNLOAD_FILLED"))) {
            c.setDownloadMethod(Model.DOWNLOAD_FILLED);
        } else if (v.equals(getString("DOWNLOAD_FULL"))) {
            c.setDownloadMethod(Model.DOWNLOAD_FULL);
        } else if (v.equals(getString("DOWNLOAD_INCREMENTAL"))) {
            c.setDownloadMethod(Model.DOWNLOAD_SMART);
        }
    }

    private void tfWorkDirectoryFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfWorkDirectoryFocusLost
        c.setStringOpt(AppSettings.OUTPUTDIRPATH, new File(tfWorkDirectory
                .getText()).getPath());
    }//GEN-LAST:event_tfWorkDirectoryFocusLost

    private void tfOutputFileBaseNameFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfOutputFileBaseNameFocusLost
        c.setOutputFileRelPath(tfOutputFileBaseName.getText());
    }//GEN-LAST:event_tfOutputFileBaseNameFocusLost

    private void updateStartDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(startDate.getDate());
        final BT747Date nd = JavaLibBridge.getDateInstance(cal
                .get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1
                - Calendar.JANUARY, cal.get(Calendar.YEAR));
        final int startTime = nd.dateToUTCepoch1970();
        c.setStartTimeNoOffset(startTime);

    }

    private void updateEndDate() {
        final Calendar cal = Calendar.getInstance();
        BT747Date nd;
        cal.setTime(endDate.getDate());
        nd = JavaLibBridge.getDateInstance(cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1 - Calendar.JANUARY, cal
                        .get(Calendar.YEAR));
        int endTime = nd.dateToUTCepoch1970();
        endTime += (24 * 3600 - 1); // Round to midnight / End of day
        c.setEndTimeNoOffset(endTime);
    }

    private void spTimeSplitHoursFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spTimeSplitHoursFocusLost
        setTimeSplit();
    }//GEN-LAST:event_spTimeSplitHoursFocusLost

    private void setTimeSplit() {
        // Offset requested split time
        int offset;
        offset = 60 * ((Integer) spTimeSplitHours.getValue() * 60 + (Integer) spTimeSplitMinutes
                .getValue());
        c.setTimeOffset(offset);
    }

    private void spTimeSplitMinutesFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spTimeSplitMinutesFocusLost
        setTimeSplit();
    }//GEN-LAST:event_spTimeSplitMinutesFocusLost

    private void btGPXActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGPXActionPerformed
        doLogConversion(Model.GPX_LOGTYPE);
    }//GEN-LAST:event_btGPXActionPerformed

    private void btKMLActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btKMLActionPerformed
        doLogConversion(Model.KML_LOGTYPE);
    }//GEN-LAST:event_btKMLActionPerformed

    private void btKMZActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btKMZActionPerformed
        doLogConversion(Model.KMZ_LOGTYPE);
    }//GEN-LAST:event_btKMZActionPerformed

    private void btCSVActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCSVActionPerformed
        doLogConversion(Model.CSV_LOGTYPE);
    }//GEN-LAST:event_btCSVActionPerformed

    private void btHTMLActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btHTMLActionPerformed
        doLogConversion(Model.GMAP_LOGTYPE);
    }//GEN-LAST:event_btHTMLActionPerformed

    private void btNMEAActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNMEAActionPerformed
        doLogConversion(Model.NMEA_LOGTYPE);
    }//GEN-LAST:event_btNMEAActionPerformed

    private void startDateActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDateActionPerformed
        if (JXDatePicker.COMMIT_KEY.equals(evt.getActionCommand())) {
            updateStartDate();
        }
    }//GEN-LAST:event_startDateActionPerformed

    private void endDateActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endDateActionPerformed
        if (JXDatePicker.COMMIT_KEY.equals(evt.getActionCommand())) {
            updateEndDate();
        }
    }//GEN-LAST:event_endDateActionPerformed

    private void btDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDownloadActionPerformed
        switch (m.getIntOpt(Model.DEVICE_PROTOCOL)) {
        default:
        case ProtocolConstants.PROTOCOL_MTK: // MTK
        case ProtocolConstants.PROTOCOL_HOLUX_PHLX:
        case ProtocolConstants.PROTOCOL_HOLUX_PHLX260:
            setDownloadMethod();
            c.startDefaultDownload();
            break;
        case ProtocolConstants.PROTOCOL_WONDEPROUD:
            c.startWPDownload();
        	break;
        }
    }//GEN-LAST:event_btDownloadActionPerformed

    private void cbLoggingActiveItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbLoggingActiveItemStateChanged
        final boolean state = (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED);
        if (m.isLoggingActive() != state) {
            c.setLoggingActive(state);
        }
    }//GEN-LAST:event_cbLoggingActiveItemStateChanged

    private void btGUIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGUIActionPerformed
        doLogConversion(Model.ARRAY_LOGTYPE);
}//GEN-LAST:event_btGUIActionPerformed

    private void btOTHERFmtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOTHERFmtActionPerformed
        doLogConversion(Model.EXTERNAL_LOGTYPE);
}//GEN-LAST:event_btOTHERFmtActionPerformed

    private void btOSMUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOSMUpActionPerformed
        doLogConversion(Model.OSM_UPLOAD_LOGTYPE);
}//GEN-LAST:event_btOSMUpActionPerformed

    // public static void main(String args) {
    // main((String[])null);
    // }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GPSDecodePanel;
    private javax.swing.JButton btCSV;
    private javax.swing.JButton btConvert;
    private javax.swing.JButton btDownload;
    private javax.swing.JButton btGPX;
    private javax.swing.JButton btGUI;
    private javax.swing.JButton btHTML;
    private javax.swing.JButton btKML;
    private javax.swing.JButton btKMZ;
    private javax.swing.JButton btNMEA;
    private javax.swing.JButton btOSMUp;
    private javax.swing.JButton btOTHERFmt;
    private javax.swing.JButton btOutputFile;
    private javax.swing.JButton btRawLogFile;
    private javax.swing.JButton btWorkingDirectory;
    private javax.swing.JCheckBox cbDisableLoggingDuringDownload;
    private javax.swing.JComboBox cbDownloadMethod;
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JComboBox cbGPSType;
    private javax.swing.JCheckBox cbLoggingActive;
    private org.jdesktop.swingx.JXDatePicker endDate;
    private org.jdesktop.swingx.JXBusyLabel lbBusySpinner;
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
    private javax.swing.JLabel lbTime;
    private javax.swing.JLabel lbToDate;
    private javax.swing.JPanel pnConvButtons;
    private javax.swing.JPanel pnConvert;
    private javax.swing.JPanel pnDateFilter;
    private javax.swing.JPanel pnDownload;
    private javax.swing.JPanel pnDownloadMethod;
    private javax.swing.JPanel pnFiles;
    private javax.swing.JPanel pnLeftConversion;
    private javax.swing.JSpinner spTimeSplitHours;
    private javax.swing.JSpinner spTimeSplitMinutes;
    private org.jdesktop.swingx.JXDatePicker startDate;
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
