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
import java.text.Format;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
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
        updateGuiLogFilterSettings();
        
        
        txtPDOPMax.setText(String.format("%.2f",m.getFilterMaxPDOP()));
        txtHDOPMax.setText(String.format("%.2f",m.getFilterMaxHDOP()));
        txtVDOPMax.setText(String.format("%.2f",m.getFilterMaxVDOP()));
        txtNSATMin.setText(Integer.toString(m.getFilterMinNSAT()));
        txtRecCntMin.setText(Integer.toString(m.getFilterMinRecCount()));
        txtRecCntMax.setText(Integer.toString(m.getFilterMaxRecCount()));
        txtDistanceMin.setText(String.format("%.2f",m.getFilterMinDist()));
        txtDistanceMax.setText(String.format("%.2f",m.getFilterMaxDist()));
        txtSpeedMin.setText(String.format("%.2f",m.getFilterMinSpeed()));
        txtSpeedMax.setText(String.format("%.2f",m.getFilterMaxSpeed()));


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
        } else if (type == ModelEvent.TRK_VALID_CHANGE
                || type == ModelEvent.TRK_RCR_CHANGE
                || type == ModelEvent.WAY_VALID_CHANGE
                || type == ModelEvent.WAY_RCR_CHANGE) {
            updateGuiLogFilterSettings();
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

        cbTrkNoFix.setSelected((trkValid & BT747_dev.VALID_NO_FIX_MASK) != 0);
        cbTrkPPS.setSelected((trkValid & BT747_dev.VALID_PPS_MASK) != 0);
        cbTrkEstimate
                .setSelected((trkValid & BT747_dev.VALID_ESTIMATED_MASK) != 0);
        cbTrkManual.setSelected((trkValid & BT747_dev.VALID_MANUAL_MASK) != 0);
        cbTrkSPS.setSelected((trkValid & BT747_dev.VALID_SPS_MASK) != 0);
        cbTrkFRTK.setSelected((trkValid & BT747_dev.VALID_FRTK_MASK) != 0);
        cbTrkDGPS.setSelected((trkValid & BT747_dev.VALID_DGPS_MASK) != 0);
        cbTrkSimulate
                .setSelected((trkValid & BT747_dev.VALID_SIMULATOR_MASK) != 0);
        cbTrkRTK.setSelected((trkValid & BT747_dev.VALID_RTK_MASK) != 0);

        cbTrkTime.setSelected((BT747_dev.RCR_TIME_MASK & trkRCR) != 0);
        cbTrkSpeed.setSelected((BT747_dev.RCR_SPEED_MASK & trkRCR) != 0);
        cbTrkDistance.setSelected((BT747_dev.RCR_DISTANCE_MASK & trkRCR) != 0);
        cbTrkButton.setSelected((BT747_dev.RCR_BUTTON_MASK & trkRCR) != 0);
        cbTrkUser1.setSelected((BT747_dev.RCR_APP1_MASK & trkRCR) != 0);
        cbTrkUser2.setSelected((BT747_dev.RCR_APP2_MASK & trkRCR) != 0);
        cbTrkUser3.setSelected((BT747_dev.RCR_APP3_MASK & trkRCR) != 0);
        cbTrkUser4.setSelected((BT747_dev.RCR_APP4_MASK & trkRCR) != 0);
        cbTrkUser5.setSelected((BT747_dev.RCR_APP5_MASK & trkRCR) != 0);
        cbTrkUser6.setSelected((BT747_dev.RCR_APP6_MASK & trkRCR) != 0);
        cbTrkUser7.setSelected((BT747_dev.RCR_APP7_MASK & trkRCR) != 0);
        cbTrkUser8.setSelected((BT747_dev.RCR_APP8_MASK & trkRCR) != 0);
        cbTrkUser9.setSelected((BT747_dev.RCR_APP9_MASK & trkRCR) != 0);
        cbTrkUser10.setSelected((BT747_dev.RCR_APPX_MASK & trkRCR) != 0);
        cbTrkUser11.setSelected((BT747_dev.RCR_APPY_MASK & trkRCR) != 0);
        cbTrkUser12.setSelected((BT747_dev.RCR_APPZ_MASK & trkRCR) != 0);

        cbWayNoFix.setSelected((wayValid & BT747_dev.VALID_NO_FIX_MASK) != 0);
        cbWayPPS.setSelected((wayValid & BT747_dev.VALID_PPS_MASK) != 0);
        cbWayEstimate
                .setSelected((wayValid & BT747_dev.VALID_ESTIMATED_MASK) != 0);
        cbWayManual.setSelected((wayValid & BT747_dev.VALID_MANUAL_MASK) != 0);
        cbWaySPS.setSelected((wayValid & BT747_dev.VALID_SPS_MASK) != 0);
        cbWayFRTK.setSelected((wayValid & BT747_dev.VALID_FRTK_MASK) != 0);
        cbWayDGPS.setSelected((wayValid & BT747_dev.VALID_DGPS_MASK) != 0);
        cbWaySimulate
                .setSelected((wayValid & BT747_dev.VALID_SIMULATOR_MASK) != 0);
        cbWayRTK.setSelected((wayValid & BT747_dev.VALID_RTK_MASK) != 0);

        cbWayTime.setSelected((BT747_dev.RCR_TIME_MASK & wayRCR) != 0);
        cbWaySpeed.setSelected((BT747_dev.RCR_SPEED_MASK & wayRCR) != 0);
        cbWayDistance.setSelected((BT747_dev.RCR_DISTANCE_MASK & wayRCR) != 0);
        cbWayButton.setSelected((BT747_dev.RCR_BUTTON_MASK & wayRCR) != 0);
        cbWayUser1.setSelected((BT747_dev.RCR_APP1_MASK & wayRCR) != 0);
        cbWayUser2.setSelected((BT747_dev.RCR_APP2_MASK & wayRCR) != 0);
        cbWayUser3.setSelected((BT747_dev.RCR_APP3_MASK & wayRCR) != 0);
        cbWayUser4.setSelected((BT747_dev.RCR_APP4_MASK & wayRCR) != 0);
        cbWayUser5.setSelected((BT747_dev.RCR_APP5_MASK & wayRCR) != 0);
        cbWayUser6.setSelected((BT747_dev.RCR_APP6_MASK & wayRCR) != 0);
        cbWayUser7.setSelected((BT747_dev.RCR_APP7_MASK & wayRCR) != 0);
        cbWayUser8.setSelected((BT747_dev.RCR_APP8_MASK & wayRCR) != 0);
        cbWayUser9.setSelected((BT747_dev.RCR_APP9_MASK & wayRCR) != 0);
        cbWayUser10.setSelected((BT747_dev.RCR_APPX_MASK & wayRCR) != 0);
        cbWayUser11.setSelected((BT747_dev.RCR_APPY_MASK & wayRCR) != 0);
        cbWayUser12.setSelected((BT747_dev.RCR_APPZ_MASK & wayRCR) != 0);
    }

    void setTrkValidFilterSettings() {
        int trkValid = 0;
        if (cbTrkNoFix.isSelected())
            trkValid |= BT747_dev.VALID_NO_FIX_MASK;
        if (cbTrkPPS.isSelected())
            trkValid |= BT747_dev.VALID_PPS_MASK;
        if (cbTrkEstimate.isSelected())
            trkValid |= BT747_dev.VALID_ESTIMATED_MASK;
        if (cbTrkManual.isSelected())
            trkValid |= BT747_dev.VALID_MANUAL_MASK;
        if (cbTrkSPS.isSelected())
            trkValid |= BT747_dev.VALID_SPS_MASK;
        if (cbTrkFRTK.isSelected())
            trkValid |= BT747_dev.VALID_FRTK_MASK;
        if (cbTrkDGPS.isSelected())
            trkValid |= BT747_dev.VALID_DGPS_MASK;
        if (cbTrkSimulate.isSelected())
            trkValid |= BT747_dev.VALID_SIMULATOR_MASK;
        if (cbTrkRTK.isSelected())
            trkValid |= BT747_dev.VALID_RTK_MASK;
    }

    void setWayValidFilterSettings() {

        int wayValid = 0;
        if (cbWayNoFix.isSelected())
            wayValid |= BT747_dev.VALID_NO_FIX_MASK;
        if (cbWayPPS.isSelected())
            wayValid |= BT747_dev.VALID_PPS_MASK;
        if (cbWayEstimate.isSelected())
            wayValid |= BT747_dev.VALID_ESTIMATED_MASK;
        if (cbWayManual.isSelected())
            wayValid |= BT747_dev.VALID_MANUAL_MASK;
        if (cbWaySPS.isSelected())
            wayValid |= BT747_dev.VALID_SPS_MASK;
        if (cbWayFRTK.isSelected())
            wayValid |= BT747_dev.VALID_FRTK_MASK;
        if (cbWayDGPS.isSelected())
            wayValid |= BT747_dev.VALID_DGPS_MASK;
        if (cbWaySimulate.isSelected())
            wayValid |= BT747_dev.VALID_SIMULATOR_MASK;
        if (cbWayRTK.isSelected())
            wayValid |= BT747_dev.VALID_RTK_MASK;
    }

    void setTrkRCRFilterSettings() {
        int trkRCR = 0;
        if (cbTrkTime.isSelected())
            trkRCR |= BT747_dev.RCR_TIME_MASK;
        if (cbTrkSpeed.isSelected())
            trkRCR |= BT747_dev.RCR_SPEED_MASK;
        if (cbTrkDistance.isSelected())
            trkRCR |= BT747_dev.RCR_DISTANCE_MASK;
        if (cbTrkButton.isSelected())
            trkRCR |= BT747_dev.RCR_BUTTON_MASK;
        if (cbTrkUser1.isSelected())
            trkRCR |= BT747_dev.RCR_APP1_MASK;
        if (cbTrkUser2.isSelected())
            trkRCR |= BT747_dev.RCR_APP2_MASK;
        if (cbTrkUser3.isSelected())
            trkRCR |= BT747_dev.RCR_APP3_MASK;
        if (cbTrkUser4.isSelected())
            trkRCR |= BT747_dev.RCR_APP4_MASK;
        if (cbTrkUser5.isSelected())
            trkRCR |= BT747_dev.RCR_APP5_MASK;
        if (cbTrkUser6.isSelected())
            trkRCR |= BT747_dev.RCR_APP6_MASK;
        if (cbTrkUser7.isSelected())
            trkRCR |= BT747_dev.RCR_APP7_MASK;
        if (cbTrkUser8.isSelected())
            trkRCR |= BT747_dev.RCR_APP8_MASK;
        if (cbTrkUser9.isSelected())
            trkRCR |= BT747_dev.RCR_APP9_MASK;
        if (cbTrkUser10.isSelected())
            trkRCR |= BT747_dev.RCR_APPX_MASK;
        if (cbTrkUser11.isSelected())
            trkRCR |= BT747_dev.RCR_APPY_MASK;
        if (cbTrkUser12.isSelected())
            trkRCR |= BT747_dev.RCR_APPZ_MASK;
    }

    void setWayRCRFilterSettings() {
        int wayRCR = 0;
        if (cbWayTime.isSelected())
            wayRCR |= BT747_dev.RCR_TIME_MASK;
        if (cbWaySpeed.isSelected())
            wayRCR |= BT747_dev.RCR_SPEED_MASK;
        if (cbWayDistance.isSelected())
            wayRCR |= BT747_dev.RCR_DISTANCE_MASK;
        if (cbWayButton.isSelected())
            wayRCR |= BT747_dev.RCR_BUTTON_MASK;
        if (cbWayUser1.isSelected())
            wayRCR |= BT747_dev.RCR_APP1_MASK;
        if (cbWayUser2.isSelected())
            wayRCR |= BT747_dev.RCR_APP2_MASK;
        if (cbWayUser3.isSelected())
            wayRCR |= BT747_dev.RCR_APP3_MASK;
        if (cbWayUser4.isSelected())
            wayRCR |= BT747_dev.RCR_APP4_MASK;
        if (cbWayUser5.isSelected())
            wayRCR |= BT747_dev.RCR_APP5_MASK;
        if (cbWayUser6.isSelected())
            wayRCR |= BT747_dev.RCR_APP6_MASK;
        if (cbWayUser7.isSelected())
            wayRCR |= BT747_dev.RCR_APP7_MASK;
        if (cbWayUser8.isSelected())
            wayRCR |= BT747_dev.RCR_APP8_MASK;
        if (cbWayUser9.isSelected())
            wayRCR |= BT747_dev.RCR_APP9_MASK;
        if (cbWayUser10.isSelected())
            wayRCR |= BT747_dev.RCR_APPX_MASK;
        if (cbWayUser11.isSelected())
            wayRCR |= BT747_dev.RCR_APPY_MASK;
        if (cbWayUser12.isSelected())
            wayRCR |= BT747_dev.RCR_APPZ_MASK;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
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
        jPanel16 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
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
        jPanel13 = new javax.swing.JPanel();
        txtPDOPMax = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtHDOPMax = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtVDOPMax = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
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
        jMenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        SettingsMenu = new javax.swing.JMenu();
        InfoMenu = new javax.swing.JMenu();
        AboutBT747 = new javax.swing.JMenuItem();
        jMenuBar1 = new javax.swing.JMenuBar();
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
                    .add(tfRawLogFilePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
                    .add(tfWorkDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
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
                .addContainerGap(74, Short.MAX_VALUE))
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
                .add(cbPortName, 0, 433, Short.MAX_VALUE))
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

        pnTrackpoint.setBorder(javax.swing.BorderFactory.createTitledBorder("Trackpoint Filter"));

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Fix Type (Valid)"));

        cbTrkNoFix.setText("No fix");
        cbTrkNoFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TrkFixTypeAction(evt);
            }
        });

        cbTrkPPS.setText("PPS");

        cbTrkEstimate.setText("Estimate");

        cbTrkManual.setText("Manual");

        cbTrkSPS.setText("SPS");

        cbTrkFRTK.setText("FRTK");

        cbTrkDGPS.setText("DGPS");

        cbTrkSimulate.setText("Sim");

        cbTrkRTK.setText("RTK");

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

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Log Reason (RCR)"));

        cbTrkTime.setText("Time");
        cbTrkTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TrkRCRAction(evt);
            }
        });

        cbTrkSpeed.setText("Speed");

        cbTrkDistance.setText("Distance");

        cbTrkButton.setText("Button");

        cbTrkUser1.setText("User 1");

        cbTrkUser2.setText("User 2");

        cbTrkUser3.setText("User 3");

        cbTrkUser4.setText("User 4");

        cbTrkUser5.setText("User 5");

        cbTrkUser6.setText("User 6");

        cbTrkUser7.setText("User 7");

        cbTrkUser8.setText("User 8");

        cbTrkUser9.setText("User 9");

        cbTrkUser10.setText("User 10");

        cbTrkUser11.setText("User 11");

        cbTrkUser12.setText("User 12");

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
            .add(pnTrackpointLayout.createSequentialGroup()
                .add(pnTrackpointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Common Filter"));

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Other"));

        txtRecCntMin.setText("jTextField2");
        txtRecCntMin.setInputVerifier(IntVerifier);
        txtRecCntMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecCntMinActionPerformed(evt);
            }
        });

        txtDistanceMin.setText("jTextField3");
        txtDistanceMin.setInputVerifier(FloatVerifier);
        txtDistanceMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDistanceMinActionPerformed(evt);
            }
        });

        txtSpeedMin.setText("jTextField4");
        txtSpeedMin.setInputVerifier(FloatVerifier);
        txtSpeedMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSpeedMinActionPerformed(evt);
            }
        });

        lbDistanceFltr.setText("<= distance <=");

        lbSpeedFltr.setText("<= speed <=");

        txtRecCntMax.setText("jTextField2");
        txtRecCntMax.setInputVerifier(IntVerifier);
        txtRecCntMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecCntMaxActionPerformed(evt);
            }
        });

        txtDistanceMax.setText("jTextField3");
        txtDistanceMax.setInputVerifier(FloatVerifier);
        txtDistanceMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDistanceMaxActionPerformed(evt);
            }
        });

        txtSpeedMax.setText("jTextField4");
        txtSpeedMax.setInputVerifier(FloatVerifier);
        txtSpeedMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSpeedMaxActionPerformed(evt);
            }
        });

        lbNSATFltr.setText("<= NSAT    ");

        txtNSATMin.setText("jTextField4");
        txtNSATMin.setInputVerifier(IntVerifier);
        txtNSATMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNSATMinActionPerformed(evt);
            }
        });

        lbRecNbrFltr.setText("<= rec nbr <=");

        org.jdesktop.layout.GroupLayout jPanel14Layout = new org.jdesktop.layout.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtRecCntMin)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtDistanceMin)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtSpeedMin))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(lbDistanceFltr)
                            .add(lbSpeedFltr)
                            .add(lbNSATFltr)
                            .add(lbRecNbrFltr))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtRecCntMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtDistanceMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtSpeedMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(txtNSATMin)
                        .add(155, 155, 155))))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(txtRecCntMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtDistanceMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbDistanceFltr))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtSpeedMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbSpeedFltr)))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtRecCntMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbRecNbrFltr))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtDistanceMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtSpeedMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtNSATMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbNSATFltr, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Precision"));

        txtPDOPMax.setText("jTextField1");
        txtPDOPMax.setInputVerifier(FloatVerifier);
        txtPDOPMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPDOPMaxActionPerformed(evt);
            }
        });

        jLabel9.setText("PDOP <=");

        txtHDOPMax.setText("jTextField1");
        txtHDOPMax.setInputVerifier(FloatVerifier);
        txtHDOPMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHDOPMaxActionPerformed(evt);
            }
        });

        jLabel12.setText("HDOP <=");

        txtVDOPMax.setText("jTextField1");
        txtVDOPMax.setInputVerifier(FloatVerifier);
        txtVDOPMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVDOPMaxActionPerformed(evt);
            }
        });

        jLabel13.setText("VDOP <=");

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtPDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtHDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtVDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtPDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtHDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13))
                .addContainerGap())
        );

        jLabel7.setText("Values that are 0 are ignored for the filter");

        org.jdesktop.layout.GroupLayout jPanel16Layout = new org.jdesktop.layout.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel7)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel7)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnWaypoint.setBorder(javax.swing.BorderFactory.createTitledBorder("Waypoint Filter"));

        pnWayPointFix.setBorder(javax.swing.BorderFactory.createTitledBorder("Fix Type (Valid)"));

        cbWayNoFix.setText("No fix");
        cbWayNoFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WayFixTypeAction(evt);
            }
        });

        cbWayPPS.setText("PPS");

        cbWayEstimate.setText("Estimate");

        cbWayManual.setText("Manual");

        cbWaySPS.setText("SPS");

        cbWayFRTK.setText("FRTK");

        cbWayDGPS.setText("DGPS");

        cbWaySimulate.setText("Sim");

        cbWayRTK.setText("RTK");

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
                .add(30, 30, 30))
        );

        pnWayPointRCR.setBorder(javax.swing.BorderFactory.createTitledBorder("Log Reason (RCR)"));

        cbWayTime.setText("Time");
        cbWayTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WayRCRAction(evt);
            }
        });

        cbWaySpeed.setText("Speed");

        cbWayDistance.setText("Distance");

        cbWayButton.setText("Button");

        cbWayUser1.setText("User 1");

        cbWayUser2.setText("User 2");

        cbWayUser3.setText("User 3");

        cbWayUser4.setText("User 4");

        cbWayUser5.setText("User 5");

        cbWayUser6.setText("User 6");

        cbWayUser7.setText("User 7");

        cbWayUser8.setText("User 8");

        cbWayUser9.setText("User 9");

        cbWayUser10.setText("User 10");

        cbWayUser11.setText("User 11");

        cbWayUser12.setText("User 12");

        org.jdesktop.layout.GroupLayout pnWayPointRCRLayout = new org.jdesktop.layout.GroupLayout(pnWayPointRCR);
        pnWayPointRCR.setLayout(pnWayPointRCRLayout);
        pnWayPointRCRLayout.setHorizontalGroup(
            pnWayPointRCRLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWayPointRCRLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnWayPointRCRLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
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
                            .add(cbWayUser12))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .add(cbWayUser6)))
                .addContainerGap())
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
            .add(pnWaypointLayout.createSequentialGroup()
                .add(pnWaypointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnWayPointFix, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnWayPointRCR, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout LogFiltersPanelLayout = new org.jdesktop.layout.GroupLayout(LogFiltersPanel);
        LogFiltersPanel.setLayout(LogFiltersPanelLayout);
        LogFiltersPanelLayout.setHorizontalGroup(
            LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(LogFiltersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnTrackpoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnWaypoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        LogFiltersPanelLayout.setVerticalGroup(
            LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(LogFiltersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(LogFiltersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnTrackpoint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnWaypoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(331, Short.MAX_VALUE))
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
                .addContainerGap(36, Short.MAX_VALUE))
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
                .addContainerGap(736, Short.MAX_VALUE))
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
                .addContainerGap(237, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("GPS Decode", GPSDecodePanel);

        FileMenu.setText("File");
        jMenuBar.add(FileMenu);

        SettingsMenu.setText("Settings");
        jMenuBar.add(SettingsMenu);

        InfoMenu.setText("Info");

        AboutBT747.setText("About BT747");
        InfoMenu.add(AboutBT747);

        jMenuBar.add(InfoMenu);

        FileMenu1.setText("File");
        jMenuBar1.add(FileMenu1);

        SettingsMenu1.setText("Settings");
        jMenuBar1.add(SettingsMenu1);

        InfoMenu1.setText("Info");

        AboutBT748.setText("About BT747");
        InfoMenu1.add(AboutBT748);

        jMenuBar1.add(InfoMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Log download & Convert");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TrkRCRAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TrkRCRAction
        setTrkRCRFilterSettings();
    }//GEN-LAST:event_TrkRCRAction

    private void TrkFixTypeAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TrkFixTypeAction
        setTrkValidFilterSettings();
    }//GEN-LAST:event_TrkFixTypeAction

    private void WayFixTypeAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WayFixTypeAction
        setWayValidFilterSettings();
    }//GEN-LAST:event_WayFixTypeAction

    private void WayRCRAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WayRCRAction
        setWayRCRFilterSettings();
    }//GEN-LAST:event_WayRCRAction

    private void txtRecCntMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecCntMinActionPerformed
        m.setFilterMinRecCount(Integer.parseInt(txtRecCntMin.getText()));
    }//GEN-LAST:event_txtRecCntMinActionPerformed

    private void txtDistanceMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDistanceMinActionPerformed
        m.setFilterMinDist(Float.parseFloat(txtDistanceMin.getText()));
    }//GEN-LAST:event_txtDistanceMinActionPerformed

    private void txtSpeedMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSpeedMinActionPerformed
        m.setFilterMinSpeed(Float.parseFloat(txtSpeedMin.getText()));
    }//GEN-LAST:event_txtSpeedMinActionPerformed

    private void txtNSATMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNSATMinActionPerformed
        m.setFilterMinNSAT(Integer.parseInt(txtNSATMin.getText()));
    }//GEN-LAST:event_txtNSATMinActionPerformed

    private void txtRecCntMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecCntMaxActionPerformed
        m.setFilterMaxRecCount(Integer.parseInt(txtRecCntMax.getText()));
    }//GEN-LAST:event_txtRecCntMaxActionPerformed

    private void txtDistanceMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDistanceMaxActionPerformed
        m.setFilterMaxDist(Float.parseFloat(txtDistanceMax.getText()));
    }//GEN-LAST:event_txtDistanceMaxActionPerformed

    private void txtSpeedMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSpeedMaxActionPerformed
        m.setFilterMaxSpeed(Float.parseFloat(txtSpeedMax.getText()));
    }//GEN-LAST:event_txtSpeedMaxActionPerformed

    private void txtVDOPMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVDOPMaxActionPerformed
        m.setFilterMaxVDOP(Float.parseFloat(txtVDOPMax.getText()));
    }//GEN-LAST:event_txtVDOPMaxActionPerformed

    private void txtHDOPMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHDOPMaxActionPerformed
        m.setFilterMaxHDOP(Float.parseFloat(txtHDOPMax.getText()));
    }//GEN-LAST:event_txtHDOPMaxActionPerformed

    private void txtPDOPMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPDOPMaxActionPerformed
        m.setFilterMaxPDOP(Float.parseFloat(txtPDOPMax.getText()));
    }//GEN-LAST:event_txtPDOPMaxActionPerformed

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
        c.forceErase();
    }// GEN-LAST:event_btRecoverMemoryActionPerformed

    private void cbSIDItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbSIDItemStateChanged
        updateSatGuiItems();
    }// GEN-LAST:event_cbSIDItemStateChanged

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
        enable = cbSID.isSelected();
        cbSNR.setEnabled(enable);
        cbAzimuth.setEnabled(enable);
        cbElevation.setEnabled(enable);

    }

    private void updateLogFormatData() {
        int logFormat = m.getLogFormat();

        cbUTCTime.setSelected((logFormat & (1 << BT747_dev.FMT_UTC_IDX)) != 0);
        cbFixType
                .setSelected((logFormat & (1 << BT747_dev.FMT_VALID_IDX)) != 0);
        cbLat.setSelected((logFormat & (1 << BT747_dev.FMT_LATITUDE_IDX)) != 0);
        cbLong
                .setSelected((logFormat & (1 << BT747_dev.FMT_LONGITUDE_IDX)) != 0);
        cbHeight
                .setSelected((logFormat & (1 << BT747_dev.FMT_HEIGHT_IDX)) != 0);
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

        if (cbUTCTime.isSelected())
            logFormat |= (1 << BT747_dev.FMT_UTC_IDX);
        if (cbFixType.isSelected())
            logFormat |= (1 << BT747_dev.FMT_VALID_IDX);
        if (cbLat.isSelected())
            logFormat |= (1 << BT747_dev.FMT_LATITUDE_IDX);
        if (cbLong.isSelected())
            logFormat |= (1 << BT747_dev.FMT_LONGITUDE_IDX);
        if (cbHeight.isSelected())
            logFormat |= (1 << BT747_dev.FMT_HEIGHT_IDX);
        if (cbSpeed.isSelected())
            logFormat |= (1 << BT747_dev.FMT_SPEED_IDX);
        if (cbHeading.isSelected())
            logFormat |= (1 << BT747_dev.FMT_HEADING_IDX);
        if (cbDSTA.isSelected())
            logFormat |= (1 << BT747_dev.FMT_DSTA_IDX);
        if (cbDAGE.isSelected())
            logFormat |= (1 << BT747_dev.FMT_DAGE_IDX);
        if (cbPDOP.isSelected())
            logFormat |= (1 << BT747_dev.FMT_PDOP_IDX);
        if (cbHDOP.isSelected())
            logFormat |= (1 << BT747_dev.FMT_HDOP_IDX);
        if (cbVDOP.isSelected())
            logFormat |= (1 << BT747_dev.FMT_VDOP_IDX);
        if (cbNSAT.isSelected())
            logFormat |= (1 << BT747_dev.FMT_NSAT_IDX);
        if (cbSID.isSelected())
            logFormat |= (1 << BT747_dev.FMT_SID_IDX);
        if (cbElevation.isSelected())
            logFormat |= (1 << BT747_dev.FMT_ELEVATION_IDX);
        if (cbAzimuth.isSelected())
            logFormat |= (1 << BT747_dev.FMT_AZIMUTH_IDX);
        if (cbSNR.isSelected())
            logFormat |= (1 << BT747_dev.FMT_SNR_IDX);
        if (cbRCR.isSelected())
            logFormat |= (1 << BT747_dev.FMT_RCR_IDX);
        if (cbMilliSeconds.isSelected())
            logFormat |= (1 << BT747_dev.FMT_MILLISECOND_IDX);
        if (cbDistance.isSelected())
            logFormat |= (1 << BT747_dev.FMT_DISTANCE_IDX);
        if (cbHoluxM241.isSelected())
            logFormat |= (1 << BT747_dev.FMT_HOLUX_LOW_PRECISION_IDX);

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
    private javax.swing.JCheckBox cbUTCTime;
    private javax.swing.JCheckBox cbVDOP;
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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
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
    private javax.swing.JLabel lbDistanceFltr;
    private javax.swing.JLabel lbGeoid;
    private javax.swing.JLabel lbLatitude;
    private javax.swing.JLabel lbLongitude;
    private javax.swing.JLabel lbNSATFltr;
    private javax.swing.JLabel lbRecNbrFltr;
    private javax.swing.JLabel lbSpeedFltr;
    private javax.swing.JLabel lbTime;
    private javax.swing.JPanel pnTrackpoint;
    private javax.swing.JPanel pnWayPointFix;
    private javax.swing.JPanel pnWayPointRCR;
    private javax.swing.JPanel pnWaypoint;
    private javax.swing.JTextField tfOutputFileBaseName;
    private javax.swing.JTextField tfRawLogFilePath;
    private javax.swing.JTextField tfWorkDirectory;
    private javax.swing.JTextField txtDistanceMax;
    private javax.swing.JTextField txtDistanceMin;
    private javax.swing.JTextField txtHDOPMax;
    private javax.swing.JTextField txtNSATMin;
    private javax.swing.JTextField txtPDOPMax;
    private javax.swing.JTextField txtRecCntMax;
    private javax.swing.JTextField txtRecCntMin;
    private javax.swing.JTextField txtSpeedMax;
    private javax.swing.JTextField txtSpeedMin;
    private javax.swing.JTextField txtVDOPMax;
    // End of variables declaration//GEN-END:variables

}
