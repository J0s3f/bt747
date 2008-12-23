//********************************************************************
//***                           BT747                              ***
//***                 (c)2007-2008 Mario De Weerd                  ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***                                                              ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package bt747.j2se_view;

import gps.convert.Conv;
import gps.log.GPSRecord;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.ComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import net.sf.bt747.j2se.app.filefilters.BinFileFilter;
import net.sf.bt747.j2se.app.filefilters.CSVFileFilter;
import net.sf.bt747.j2se.app.filefilters.DPL700FileFilter;
import net.sf.bt747.j2se.app.filefilters.HoluxTRLFileFilter;
import net.sf.bt747.j2se.app.filefilters.KnownFileFilter;
import net.sf.bt747.j2se.app.filefilters.NMEAFileFilter;

import bt747.model.AppSettings;
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
    }
    
    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);
        initAppData();
    }


    private final String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    private void updateGuiData() {
        cbGPSType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("DEFAULT_DEVICE"), "Holux M-241", "iTrackU-Nemerix",
                "PhotoTrackr", "iTrackU-SIRFIII" }));
        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("TABLE_Description"), getString("GPX_Description"),
                getString("GMAP_Description"), getString("CSV_Description"),
                getString("KML_Description"), getString("KMZ_Description"),
                getString("NMEA_Description"), getString("OZI_Description"),
                getString("Compe_Description") }));
        cbDownloadMethod.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { getString("DOWNLOAD_INCREMENTAL"),
                        getString("DOWNLOAD_FULL"),
                        getString("DOWNLOAD_FILLED") }));
    }

    MyMap pnMap = new MyMap();

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

        updateCbGPSType();

        cbDisableLoggingDuringDownload.setSelected(m
                .getBooleanOpt(Model.DISABLELOGDURINGDOWNLOAD));

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

        updateConnected(m.isConnected());
    }

    private final void updateConnected(final boolean connected) {
        JPanel[] panels = { GPSDecodePanel };

        btDownloadFromNumerix.setEnabled(connected);
        btDownloadIBlue.setEnabled(connected);
        for (JPanel panel : panels) {
            J2SEAppController.disablePanel(panel, connected);
        }
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
        txtLongitude.setText(String
                .format((Locale) null, "%.8f", gps.longitude)); // NOI18N
        txtGeoid.setText(String.format((Locale) null, "%.1f", gps.geoid) // NOI18N
                + getString("m")
                + getString("(calc:")
                + String.format((Locale) null, "%.1f", Conv.wgs84Separation(
                        gps.latitude, gps.longitude)) + getString("m") + ")"); // NOI18N

    }

    boolean btConnectFunctionIsConnect = true;

    /**
     * 
     */
    private void doLogConversion(final int selectedFormat) {
        setLogConversionParameters();
        c.doLogConversion(selectedFormat);
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
        int offset;
        offset = 60 * ((Integer) spTimeSplitHours.getValue() * 60 + (Integer) spTimeSplitMinutes
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

        case ModelEvent.SETTING_CHANGE:
//                int arg = Integer.valueOf((String) e.getArg());
//                switch (arg) {
//                case Model.MAPTYPE:
//                    updateMapType();
//                    break;
//                }
            break;
        case ModelEvent.GPRMC:
            updateRMCData((GPSRecord) e.getArg());
            break;
        case ModelEvent.UPDATE_LOG_MEM_USED:
            txtMemoryUsed.setText(Convert.toString(m.logMemUsed()) + " ("
                    + Convert.toString(m.logMemUsedPercent()) + "%)"); // NOI18N
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
            if (m.getMainVersion().length() + m.getFirmwareVersion().length() != 0) {
                if (m.getMainVersion().length()
                        + m.getFirmwareVersion().length() > 20) {
                    fwString = "<html>";
                    if (m.getMainVersion().length() > 20) {
                        fwString += m.getMainVersion().substring(0, 17) + "...";
                        lbFirmWare.setToolTipText(m.getMainVersion());
                    } else {
                        fwString += m.getMainVersion();
                    }
                    fwString += (m.getMainVersion().length() != 0 ? "<br>" : "")
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
        case ModelEvent.UPDATE_LOG_NBR_LOG_PTS:
            // TODO
            // lbUsedMem.setText(Txt.MEM_USED + Convert.toString(m.logMemUsed())
            // + "("
            // + Convert.toString(m.logMemUsedPercent()) + "%)");
            // m_UsedLabel.repaintNow();
            // lbUsedRecords.setText(Txt.NBR_RECORDS
            // + Convert.toString(m.logNbrLogPts()) + " ("
            // + m.getEstimatedNbrRecordsFree(m.getLogFormat()) + " "
            // + Txt.MEM_FREE + ")");
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
            btConvert.setText(getString("BT747Main.btConvert.text"));
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            c.replyToOkToOverwrite(c.getRequestToOverwriteFromDialog());
            break;
        case ModelEvent.COULD_NOT_OPEN_FILE:
            c.couldNotOpenFileMessage((String) e.getArg());
            break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        btDownloadIBlue = new javax.swing.JButton();
        btDownloadFromNumerix = new javax.swing.JButton();
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        startDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                startDateFocusLost(evt);
            }
        });

        endDate.setToolTipText(bundle.getString("BT747Main.endDate.toolTipText")); // NOI18N
        endDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endDateActionPerformed(evt);
            }
        });
        endDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                endDateFocusLost(evt);
            }
        });

        spTimeSplitHours.setModel(new javax.swing.SpinnerNumberModel(0, 0, 24, 1));
        spTimeSplitHours.setToolTipText(bundle.getString("BT747Main.spTimeSplitHours.toolTipText")); // NOI18N
        spTimeSplitHours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spTimeSplitHoursFocusLost(evt);
            }
        });

        spTimeSplitMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 24, 1));
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

        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GPX", "CSV", "CompeGPS (.TRK,.WPT)", "KML", "KMZ", "OziExplorer (.PLT)", "NMEA" }));
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
                .add(pnLeftConversionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
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
                    .add(pnDateFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10))
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
                    .add(btNMEA)))
        );

        pnConvButtonsLayout.linkSize(new java.awt.Component[] {btCSV, btGPX, btHTML, btKML, btKMZ, btNMEA}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

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
        );

        org.jdesktop.layout.GroupLayout pnConvertLayout = new org.jdesktop.layout.GroupLayout(pnConvert);
        pnConvert.setLayout(pnConvertLayout);
        pnConvertLayout.setHorizontalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnConvertLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnConvButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLeftConversion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnConvertLayout.setVerticalGroup(
            pnConvertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLeftConversion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnConvButtons, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnFiles, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void cbLoggingActiveFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbLoggingActiveFocusLost
        c.setLoggingActive(cbLoggingActive.isSelected());
    }//GEN-LAST:event_cbLoggingActiveFocusLost

    private void cbDisableLoggingDuringDownloadFocusLost(
            java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbDisableLoggingDuringDownloadFocusLost
        c.setBooleanOpt(Model.DISABLELOGDURINGDOWNLOAD,
                cbDisableLoggingDuringDownload.isSelected());
    }//GEN-LAST:event_cbDisableLoggingDuringDownloadFocusLost

    private void btDownloadFromNumerixActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDownloadFromNumerixActionPerformed
        c.startDPL700Download();
    }//GEN-LAST:event_btDownloadFromNumerixActionPerformed

    private void cbGPSTypeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbGPSTypeFocusLost
        int type = Model.GPS_TYPE_DEFAULT;
        boolean forceHolux = false;
        switch (cbGPSType.getSelectedIndex()) {
        case 1:
            type = Model.GPS_TYPE_DEFAULT;
            forceHolux = true;
            break;
        case 2:
            type = Model.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX;
            break;
        case 3:
            type = Model.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR;
            break;
        case 4:
            type = Model.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII;
            break;
        case 0:
        default:
            type = Model.GPS_TYPE_DEFAULT;
        }
        c.setIntOpt(AppSettings.GPSTYPE, type);
        c.setBooleanOpt(Model.FORCE_HOLUXM241, forceHolux);
    }//GEN-LAST:event_cbGPSTypeFocusLost

    private final void updateCbGPSType() {
        if (m.getBooleanOpt(AppSettings.FORCE_HOLUXM241)) {
            cbGPSType.setSelectedIndex(1);
        } else {
            int index = 0;
            switch (m.getIntOpt(AppSettings.GPSTYPE)) {
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

    private void tfRawLogFilePathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfRawLogFilePathFocusLost

        c.setStringOpt(AppSettings.LOGFILEPATH, tfRawLogFilePath.getText());
    }//GEN-LAST:event_tfRawLogFilePathFocusLost

    private void btDownloadIBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO: next line must be done on button action
        setDownloadMethod();
        c.startDefaultDownload();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cbFormatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFormatItemStateChanged
        switch (evt.getStateChange()) {
        case java.awt.event.ItemEvent.SELECTED:
            // getSelectedFormat(evt.getItem().toString());
            break;
        case java.awt.event.ItemEvent.DESELECTED:
            break;
        }
    }//GEN-LAST:event_cbFormatItemStateChanged

    private void btConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConvertActionPerformed
        if (btConvert.getText().equals(getString("Stop_Convert.text"))) {
            c.stopLogConvert();
        } else {
            doLogConversion(getSelectedFormat(cbFormat.getSelectedItem()
                    .toString()));
        }
    }//GEN-LAST:event_btConvertActionPerformed

    private void btOutputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOutputFileActionPerformed
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
                if (relPath.lastIndexOf('.') == relPath.length() - 4) {
                    relPath = relPath.substring(0, relPath.length() - 4);
                }
                c.setOutputFileRelPath(relPath);
                getOutputFilePath();
                tfOutputFileBaseName.setCaretPosition(tfOutputFileBaseName
                        .getText().length());

            } catch (Exception e) {
                Generic.debug(getString("OutputFileChooser"), e);
            }
        }
    }//GEN-LAST:event_btOutputFileActionPerformed

    private void btRawLogFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRawLogFileActionPerformed
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
        RawLogFileChooser.addChoosableFileFilter(new CSVFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new HoluxTRLFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new NMEAFileFilter());
        RawLogFileChooser.addChoosableFileFilter(new DPL700FileFilter());
        KnownFileFilter ff = new KnownFileFilter();
        RawLogFileChooser.addChoosableFileFilter(ff);
        RawLogFileChooser.setFileFilter(ff);
        if (RawLogFileChooser.showDialog(this, getString("SetRawLogFile")) == JFileChooser.APPROVE_OPTION) {
            try {
                c.setStringOpt(AppSettings.LOGFILEPATH, RawLogFileChooser
                        .getSelectedFile().getCanonicalPath());
            } catch (Exception e) {
                Generic.debug(getString("RawFileChooser"), e);
            }
            tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
            tfRawLogFilePath.setCaretPosition(tfRawLogFilePath.getText()
                    .length());
        }
    }//GEN-LAST:event_btRawLogFileActionPerformed

    private void btWorkingDirectoryActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btWorkingDirectoryActionPerformed
        javax.swing.JFileChooser WorkingDirectoryChooser;
        File curDir = getWorkDirPath();
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
            } catch (Exception e) {
                Generic.debug(getString("WorkingDirChooser"), e);
            }
            tfWorkDirectory.setText(m.getStringOpt(AppSettings.OUTPUTDIRPATH));
            tfWorkDirectory
                    .setCaretPosition(tfWorkDirectory.getText().length());
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
        } else {
            selectedFormat = Model.NO_LOG_LOGTYPE;
        }
        return selectedFormat;
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

    private void getDownloadMethod() {
        switch (m.getDownloadMethod()) {
        case Model.DOWNLOAD_FILLED:
            cbDownloadMethod.setSelectedItem(getString("DOWNLOAD_FILLED"));
            break;
        case Model.DOWNLOAD_FULL:
            cbDownloadMethod.setSelectedItem(getString("DOWNLOAD_FULL"));
            break;
        case Model.DOWNLOAD_INCREMENTAL:
            cbDownloadMethod.setSelectedItem(getString("DOWNLOAD_INCREMENTAL"));
            break;
        }
    }

    private void setDownloadMethod() {
        String v = (String) cbDownloadMethod.getSelectedItem();
        if (v.equals(getString("DOWNLOAD_FILLED"))) {
            c.setDownloadMethod(Model.DOWNLOAD_FILLED);
        } else if (v.equals(getString("DOWNLOAD_FULL"))) {
            c.setDownloadMethod(Model.DOWNLOAD_FULL);
        } else if (v.equals(getString("DOWNLOAD_INCREMENTAL"))) {
            c.setDownloadMethod(Model.DOWNLOAD_INCREMENTAL);
        }
    }

    private void tfWorkDirectoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfWorkDirectoryFocusLost
        c.setStringOpt(AppSettings.OUTPUTDIRPATH, new File(tfWorkDirectory
                .getText()).getPath());
    }//GEN-LAST:event_tfWorkDirectoryFocusLost

    private void tfOutputFileBaseNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfOutputFileBaseNameFocusLost
        c.setOutputFileRelPath(tfOutputFileBaseName.getText());
    }//GEN-LAST:event_tfOutputFileBaseNameFocusLost

    private void startDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_startDateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_startDateFocusLost

    private void startDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startDateActionPerformed

    private void endDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_endDateActionPerformed

    private void endDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_endDateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_endDateFocusLost

    private void spTimeSplitHoursFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spTimeSplitHoursFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_spTimeSplitHoursFocusLost

    private void spTimeSplitMinutesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spTimeSplitMinutesFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_spTimeSplitMinutesFocusLost

    private void btGPXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGPXActionPerformed
        doLogConversion(Model.GPX_LOGTYPE);
    }//GEN-LAST:event_btGPXActionPerformed

    private void btKMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btKMLActionPerformed
        doLogConversion(Model.KML_LOGTYPE);
    }//GEN-LAST:event_btKMLActionPerformed

    private void btKMZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btKMZActionPerformed
        doLogConversion(Model.KMZ_LOGTYPE);
    }//GEN-LAST:event_btKMZActionPerformed

    private void btCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCSVActionPerformed
        doLogConversion(Model.CSV_LOGTYPE);
    }//GEN-LAST:event_btCSVActionPerformed

    private void btHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btHTMLActionPerformed
        doLogConversion(Model.GMAP_LOGTYPE);
    }//GEN-LAST:event_btHTMLActionPerformed

    private void btNMEAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNMEAActionPerformed
        doLogConversion(Model.NMEA_LOGTYPE);
    }//GEN-LAST:event_btNMEAActionPerformed

    // public static void main(String args) {
    // main((String[])null);
    // }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GPSDecodePanel;
    private javax.swing.JButton btCSV;
    private javax.swing.JButton btConvert;
    private javax.swing.JButton btDownloadFromNumerix;
    private javax.swing.JButton btDownloadIBlue;
    private javax.swing.JButton btGPX;
    private javax.swing.JButton btHTML;
    private javax.swing.JButton btKML;
    private javax.swing.JButton btKMZ;
    private javax.swing.JButton btNMEA;
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
