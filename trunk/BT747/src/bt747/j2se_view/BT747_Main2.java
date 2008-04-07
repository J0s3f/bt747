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
import gps.convert.FileUtil;
import gps.log.GPSRecord;

import java.io.File;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.UIManager;

import bt747.Txt;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Convert;

/**
 * 
 * @author Mario De Weerd
 */
public class BT747_Main2 extends javax.swing.JFrame implements
        bt747.model.ModelListener, GPSListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Model m;
    Controller c;

    /** Creates new form BT747_Main2 */
    public BT747_Main2() {
        initComponents();
    }

    public BT747_Main2(Model m, Controller c) {
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

        // TODO: Deactivate debug by default
        c.setDebug(true);
        c.setDebugConn(true);

    }

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
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        WorkingDirectoryChooser = new javax.swing.JFileChooser();
        RawLogFileChooser = new javax.swing.JFileChooser();
        OutputFileChooser = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        tabPane = new javax.swing.JTabbedPane();
        connectionPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        whatToLogPanel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox20 = new javax.swing.JCheckBox();
        jComboBox4 = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox21 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox22 = new javax.swing.JCheckBox();
        jPanel12 = new javax.swing.JPanel();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox13 = new javax.swing.JCheckBox();
        jCheckBox14 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jPanel13 = new javax.swing.JPanel();
        jCheckBox16 = new javax.swing.JCheckBox();
        jCheckBox19 = new javax.swing.JCheckBox();
        jCheckBox15 = new javax.swing.JCheckBox();
        jCheckBox17 = new javax.swing.JCheckBox();
        jCheckBox18 = new javax.swing.JCheckBox();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        whenToLogPanel = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jCheckBox24 = new javax.swing.JCheckBox();
        jTextField3 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jCheckBox23 = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jCheckBox25 = new javax.swing.JCheckBox();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jCheckBox26 = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jCheckBox27 = new javax.swing.JCheckBox();
        jButton8 = new javax.swing.JButton();
        downloadPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        tfWorkDirectory = new javax.swing.JTextField();
        tfRawLogFilePath = new javax.swing.JTextField();
        tfOutputFileBaseName = new javax.swing.JTextField();
        btWorkingDirectory = new javax.swing.JButton();
        btRawLogFile = new javax.swing.JButton();
        btOutputFile = new javax.swing.JButton();
        btConvert = new javax.swing.JButton();
        cbFormat = new javax.swing.JComboBox();
        jButton4 = new javax.swing.JButton();
        jComboBox6 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        cbPortName = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jCheckBox28 = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        cbIncremental = new javax.swing.JCheckBox();
        DownloadProgressBar = new javax.swing.JProgressBar();
        jLabel20 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jComboBox8 = new javax.swing.JComboBox();
        jPanel18 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox();
        gpsDecodePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbLatitude = new javax.swing.JLabel();
        lbLongitude = new javax.swing.JLabel();
        lbTime = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbGeoid = new javax.swing.JLabel();
        LogFiltersPanel = new javax.swing.JPanel();
        pnTrackpoint = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        cbTrkNoFix = new javax.swing.JCheckBox();
        cbTrkPPS = new javax.swing.JCheckBox();
        cbTrkEstimate = new javax.swing.JCheckBox();
        cbTrkManual = new javax.swing.JCheckBox();
        cbTrkSPS = new javax.swing.JCheckBox();
        cbTrkFRTK = new javax.swing.JCheckBox();
        cbTrkDGPS = new javax.swing.JCheckBox();
        cbTrkSimulate = new javax.swing.JCheckBox();
        cbTrkRTK = new javax.swing.JCheckBox();
        jPanel19 = new javax.swing.JPanel();
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
        jPanel20 = new javax.swing.JPanel();
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
        jPanel21 = new javax.swing.JPanel();
        txtPDOPMax = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtHDOPMax = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtVDOPMax = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
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
        WorkingDirectoryChooser
                .setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
        WorkingDirectoryChooser
                .setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BT747 Application");

        jCheckBox1.setText("Bluetooth");
        jCheckBox1.setToolTipText("Connect to the Logger via Bluetooh");

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(
                jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel6Layout.createSequentialGroup().addContainerGap().add(
                        jCheckBox1).addContainerGap(
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)));
        jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel6Layout.createSequentialGroup().addContainerGap().add(
                        jCheckBox1).addContainerGap(
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Port");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Speed");

        jCheckBox2.setText("Serial");
        jCheckBox2
                .setToolTipText("Connect to the Logger via a (virtual) serial Port. This is the case if you connect it to your computer via a USB cable.");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(
                jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout
                .setHorizontalGroup(jPanel7Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel7Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel7Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jCheckBox2)
                                                        .add(
                                                                jPanel7Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                23,
                                                                                23,
                                                                                23)
                                                                        .add(
                                                                                jLabel6)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jComboBox2,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .add(
                                                                                86,
                                                                                86,
                                                                                86)
                                                                        .add(
                                                                                jLabel5)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jComboBox1,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap()));
        jPanel7Layout
                .setVerticalGroup(jPanel7Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel7Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel7Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel7Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jCheckBox2)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jPanel7Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                                        .add(
                                                                                                jLabel6)
                                                                                        .add(
                                                                                                jComboBox2,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                        .add(
                                                                                                jComboBox1,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                jPanel7Layout
                                                                        .createSequentialGroup()
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                34,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .add(
                                                                                jLabel5)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)))
                                        .addContainerGap()));

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(
                jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout
                .setHorizontalGroup(jPanel5Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel5Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel5Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel6,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                jPanel7,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(830, 830, 830)));
        jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel5Layout.createSequentialGroup().addContainerGap().add(
                        jPanel6,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(6,
                        6, 6).add(jPanel7,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59,
                        Short.MAX_VALUE).addContainerGap()));

        jButton2.setText("(Re-)Connect");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(
                jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel8Layout.createSequentialGroup().addContainerGap(
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(jButton2).addContainerGap(547,
                        Short.MAX_VALUE)));
        jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel8Layout.createSequentialGroup().addContainerGap().add(
                        jButton2).addContainerGap()));

        jButton3.setText("Disconnect");

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(
                jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(jPanel9Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel9Layout.createSequentialGroup().addContainerGap().add(
                        jButton3).addContainerGap(245, Short.MAX_VALUE)));
        jPanel9Layout.setVerticalGroup(jPanel9Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel9Layout.createSequentialGroup().addContainerGap().add(
                        jButton3).addContainerGap()));

        org.jdesktop.layout.GroupLayout connectionPanelLayout = new org.jdesktop.layout.GroupLayout(
                connectionPanel);
        connectionPanel.setLayout(connectionPanelLayout);
        connectionPanelLayout
                .setHorizontalGroup(connectionPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel5,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                1137, Short.MAX_VALUE)
                        .add(
                                connectionPanelLayout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel8,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .add(139, 139, 139)
                                        .add(
                                                jPanel9,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
        connectionPanelLayout
                .setVerticalGroup(connectionPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                org.jdesktop.layout.GroupLayout.TRAILING,
                                connectionPanelLayout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel5,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                connectionPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel8,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                jPanel9,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(196, 196, 196)));

        tabPane.addTab("Connect to... ", connectionPanel);

        jPanel10.setBorder(javax.swing.BorderFactory
                .createTitledBorder("General"));

        jCheckBox4.setText("Valid");
        jCheckBox4.setToolTipText("Log if the positional data is valid.");

        jCheckBox20.setText("RCR");
        jCheckBox20.setToolTipText("Log the record creation reason.");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "Stop when full", "Stop when full" }));

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(
                jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(jPanel10Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel10Layout.createSequentialGroup().addContainerGap().add(
                        jCheckBox4).add(43, 43, 43).add(jCheckBox20)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED, 432,
                                Short.MAX_VALUE).add(jComboBox4,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                155,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        jPanel10Layout
                .setVerticalGroup(jPanel10Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel10Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel10Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jCheckBox4)
                                                        .add(jCheckBox20)
                                                        .add(
                                                                jComboBox4,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        jPanel11.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Positional Data"));

        jCheckBox7.setText("Height");
        jCheckBox7.setToolTipText("Log the height");

        jCheckBox6.setText("Longitude");
        jCheckBox6.setToolTipText("Log the longitude");

        jCheckBox9.setText("Heading");
        jCheckBox9
                .setToolTipText("Log the heading (average from prevoius log record to current)");

        jCheckBox21.setText("Millisecond");
        jCheckBox21
                .setToolTipText("Log the milliseconds of the log record  creation time.");

        jCheckBox5.setText("Latitude");
        jCheckBox5.setToolTipText("Log the latitude");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        jCheckBox10.setText("Speed");
        jCheckBox10
                .setToolTipText("Log the speed (average from previous log record to current)");
        jCheckBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox10ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("UTC");
        jCheckBox3
                .setToolTipText("Log the time of the record creation (as UTC)");

        jCheckBox22.setText("Distance");
        jCheckBox22
                .setToolTipText("Log the distance (from the previous log record)");
        jCheckBox22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox22ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(
                jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout
                .setHorizontalGroup(jPanel11Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel11Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel11Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jCheckBox3).add(
                                                                jCheckBox5)
                                                        .add(jCheckBox22))
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel11Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel11Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jPanel11Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                jCheckBox6)
                                                                                        .add(
                                                                                                jCheckBox10))
                                                                        .add(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .add(
                                                                                jPanel11Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                jCheckBox9)
                                                                                        .add(
                                                                                                jCheckBox7)))
                                                        .add(jCheckBox21))
                                        .addContainerGap(503, Short.MAX_VALUE)));
        jPanel11Layout
                .setVerticalGroup(jPanel11Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel11Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel11Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jCheckBox3).add(
                                                                jCheckBox21))
                                        .add(15, 15, 15)
                                        .add(
                                                jPanel11Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jCheckBox5).add(
                                                                jCheckBox6)
                                                        .add(jCheckBox7))
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel11Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jCheckBox22).add(
                                                                jCheckBox10)
                                                        .add(jCheckBox9))
                                        .addContainerGap()));

        jPanel12.setBorder(javax.swing.BorderFactory
                .createTitledBorder("GPS Technical"));

        jCheckBox8.setText("D-GPS status");
        jCheckBox8
                .setToolTipText("Log if this record is based on a differential gps measurement. If you don't know what this is you probably don't need it.");
        jCheckBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox8ActionPerformed(evt);
            }
        });

        jCheckBox11.setText("D-GPS age");
        jCheckBox11
                .setToolTipText("Log the age of the D-GPS reference signal. If you don't know what this is you probably don't need it.");

        jCheckBox13.setText("HDOP");
        jCheckBox13
                .setToolTipText("Log the horizontal dillusion of position. If you don't know what this is you probably don't need it.");

        jCheckBox14.setText("VDOP");
        jCheckBox14
                .setToolTipText("Log the vertical dillusion of position. If you don't know what this is you probably don't need it.");

        jCheckBox12.setText("PDOP");
        jCheckBox12
                .setToolTipText("Log the 3D-positional dillusion of position. If you don't know what this is you probably don't need it.");

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(
                jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout
                .setHorizontalGroup(jPanel12Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel12Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel12Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jCheckBox8).add(
                                                                jCheckBox12))
                                        .add(
                                                jPanel12Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel12Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                8,
                                                                                8,
                                                                                8)
                                                                        .add(
                                                                                jCheckBox13)
                                                                        .add(
                                                                                50,
                                                                                50,
                                                                                50)
                                                                        .add(
                                                                                jCheckBox14))
                                                        .add(
                                                                jPanel12Layout
                                                                        .createSequentialGroup()
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                                        .add(
                                                                                jCheckBox11,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                91,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap(491, Short.MAX_VALUE)));
        jPanel12Layout
                .setVerticalGroup(jPanel12Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel12Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel12Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jCheckBox8)
                                                        .add(
                                                                jCheckBox11,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                22,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel12Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jCheckBox12).add(
                                                                jCheckBox14)
                                                        .add(jCheckBox13))
                                        .addContainerGap()));

        jPanel13.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Satellite Data"));

        jCheckBox16.setText("SID");
        jCheckBox16.setToolTipText("For each satellite: Log it's ID.");

        jCheckBox19.setText("SNR");
        jCheckBox19
                .setToolTipText("For each satellite: Log it's Signal-To-Noise-Ratio.");

        jCheckBox15.setText("NSat");
        jCheckBox15
                .setToolTipText("Log the number of satellites in sight for the measurement.");

        jCheckBox17.setText("Elevation");
        jCheckBox17
                .setToolTipText("For each satellite: Log it's elevation over the horizont  (in degree).");

        jCheckBox18.setText("Azimuth");
        jCheckBox18
                .setToolTipText("For each satellite: Log it's azimuth (in angular degree).");

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(
                jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(jPanel13Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel13Layout.createSequentialGroup().addContainerGap().add(
                        jPanel13Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING).add(
                                jPanel13Layout.createSequentialGroup().add(
                                        jCheckBox16).add(18, 18, 18).add(
                                        jCheckBox17).add(18, 18, 18).add(
                                        jCheckBox18).add(18, 18, 18).add(
                                        jCheckBox19)).add(jCheckBox15))
                        .addContainerGap(470, Short.MAX_VALUE)));
        jPanel13Layout.setVerticalGroup(jPanel13Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel13Layout.createSequentialGroup().add(jCheckBox15).add(17,
                        17, 17).add(
                        jPanel13Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(
                                jCheckBox16).add(jCheckBox17).add(jCheckBox18)
                                .add(jCheckBox19)).addContainerGap()));

        jButton5.setText("Set Log Format");

        jButton6.setText("Clear Log");

        jButton7.setText("Clear Log & Set new Log Format");

        org.jdesktop.layout.GroupLayout whatToLogPanelLayout = new org.jdesktop.layout.GroupLayout(
                whatToLogPanel);
        whatToLogPanel.setLayout(whatToLogPanelLayout);
        whatToLogPanelLayout
                .setHorizontalGroup(whatToLogPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                whatToLogPanelLayout
                                        .createSequentialGroup()
                                        .add(
                                                whatToLogPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                whatToLogPanelLayout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .add(
                                                                                jPanel11,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                        .add(
                                                                whatToLogPanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                20,
                                                                                20,
                                                                                20)
                                                                        .add(
                                                                                jPanel10,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                        .add(
                                                                whatToLogPanelLayout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .add(
                                                                                jButton6)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jButton5)
                                                                        .add(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .add(
                                                                                jButton7))
                                                        .add(
                                                                whatToLogPanelLayout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .add(
                                                                                whatToLogPanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                jPanel13,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                        .add(
                                                                                                jPanel12,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))))
                                        .addContainerGap()));

        whatToLogPanelLayout.linkSize(new java.awt.Component[] { jButton5,
                jButton6, jButton7 },
                org.jdesktop.layout.GroupLayout.HORIZONTAL);

        whatToLogPanelLayout
                .setVerticalGroup(whatToLogPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                whatToLogPanelLayout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel10,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel11,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel12,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel13,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(40, 40, 40)
                                        .add(
                                                whatToLogPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jButton6).add(
                                                                jButton5).add(
                                                                jButton7))
                                        .addContainerGap(142, Short.MAX_VALUE)));

        tabPane.addTab("What to log", whatToLogPanel);

        jPanel14.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Log by . . ."));

        jLabel7.setText("seconds");

        jCheckBox24.setText("Speed");

        jTextField3.setText("jTextField3");

        jTextField1.setText("jTextField1");

        jLabel10.setText("km/h");

        jCheckBox23.setText("Time");

        jLabel9.setText("above");

        jLabel12.setText("m");

        jLabel11.setText("every");

        jCheckBox25.setText("Distance");

        jTextField2.setText("jTextField2");

        jLabel8.setText("every");

        org.jdesktop.layout.GroupLayout jPanel14Layout = new org.jdesktop.layout.GroupLayout(
                jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout
                .setHorizontalGroup(jPanel14Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel14Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel14Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jCheckBox25).add(
                                                                jCheckBox23)
                                                        .add(jCheckBox24))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel14Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.TRAILING)
                                                        .add(jLabel8).add(
                                                                jLabel11).add(
                                                                jLabel9))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel14Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jTextField1,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                jTextField3,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                jTextField2,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel14Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jLabel12).add(
                                                                jLabel7).add(
                                                                jLabel10))
                                        .addContainerGap(541, Short.MAX_VALUE)));
        jPanel14Layout.setVerticalGroup(jPanel14Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel14Layout.createSequentialGroup().add(
                        jPanel14Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(
                                jCheckBox23).add(jLabel8,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                28, Short.MAX_VALUE).add(jTextField1,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel7)).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED).add(
                        jPanel14Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(
                                jCheckBox24).add(jLabel9).add(jTextField2,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel10)).add(10, 10, 10).add(
                        jPanel14Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(
                                jCheckBox25).add(jLabel11).add(jTextField3,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel12)).addContainerGap()));

        jPanel15.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Get the position"));

        jLabel13.setText("every");

        jTextField4.setText("jTextField4");

        jCheckBox26.setText("Use SBAS");

        jLabel14.setText("ms");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "Item 1", "Item 2", "Item 3", "Item 4" }));

        jCheckBox27.setText("incl. Test SBAS");

        org.jdesktop.layout.GroupLayout jPanel15Layout = new org.jdesktop.layout.GroupLayout(
                jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout
                .setHorizontalGroup(jPanel15Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel15Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel15Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jCheckBox27)
                                                        .add(
                                                                jPanel15Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jLabel13)
                                                                        .add(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .add(
                                                                                jTextField4,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jLabel14))
                                                        .add(
                                                                jPanel15Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jCheckBox26)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                                        .add(
                                                                                jComboBox3,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap(606, Short.MAX_VALUE)));
        jPanel15Layout
                .setVerticalGroup(jPanel15Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel15Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel15Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel15Layout
                                                                        .createParallelGroup(
                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                        .add(
                                                                                jLabel13)
                                                                        .add(
                                                                                jTextField4,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .add(
                                                                                jLabel14))
                                                        .add(
                                                                jPanel15Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                46,
                                                                                46,
                                                                                46)
                                                                        .add(
                                                                                jPanel15Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                                        .add(
                                                                                                jCheckBox26)
                                                                                        .add(
                                                                                                jComboBox3,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jCheckBox27).addContainerGap(35,
                                                Short.MAX_VALUE)));

        jButton8.setText("Apply");

        org.jdesktop.layout.GroupLayout whenToLogPanelLayout = new org.jdesktop.layout.GroupLayout(
                whenToLogPanel);
        whenToLogPanel.setLayout(whenToLogPanelLayout);
        whenToLogPanelLayout
                .setHorizontalGroup(whenToLogPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                org.jdesktop.layout.GroupLayout.TRAILING,
                                whenToLogPanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                whenToLogPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.TRAILING)
                                                        .add(
                                                                jButton8,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                118,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                jPanel15,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                jPanel14,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));
        whenToLogPanelLayout
                .setVerticalGroup(whenToLogPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                whenToLogPanelLayout
                                        .createSequentialGroup()
                                        .add(20, 20, 20)
                                        .add(
                                                jPanel14,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(
                                                jPanel15,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jButton8).add(303, 303, 303)));

        tabPane.addTab("When/how to log", whenToLogPanel);

        jPanel1.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Where to store"));

        tfWorkDirectory.setText("jTextField1");
        tfWorkDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfWorkDirectoryActionPerformed(evt);
            }
        });
        tfWorkDirectory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfWorkDirectoryFocusLost(evt);
            }
        });

        tfRawLogFilePath.setText("jTextField2");

        tfOutputFileBaseName.setText("jTextField3");

        btWorkingDirectory.setText("Working Directory");
        btWorkingDirectory
                .setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btWorkingDirectory
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btWorkingDirectoryActionPerformed1(evt);
                        btWorkingDirectoryActionPerformed(evt);
                    }
                });

        btRawLogFile.setText("Raw Log File");
        btRawLogFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btRawLogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRawLogFileActionPerformed(evt);
            }
        });

        btOutputFile.setText("Output File");
        btOutputFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        btOutputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOutputFileActionPerformed(evt);
            }
        });

        btConvert.setText("Convert To Format");
        btConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConvertActionPerformed(evt);
            }
        });
        btConvert.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btConvertMouseReleased(evt);
            }
        });

        cbFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "GPX", "CSV", "CompeGPS (.TRK,.WPT)", "KML",
                "OziExplorer (.PLT)", "NMEA", "Google Map (.html)" }));
        cbFormat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFormatItemStateChanged(evt);
            }
        });
        cbFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFormatActionPerformed(evt);
            }
        });

        jButton4.setText("No-Fix Color");

        jComboBox6
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                        "write height over mean sea level",
                        "write height over Geoid" }));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
                jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout
                .setHorizontalGroup(jPanel1Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                org.jdesktop.layout.GroupLayout.TRAILING,
                                jPanel1Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.CENTER)
                                                        .add(
                                                                btRawLogFile,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                184,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                btWorkingDirectory,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                184,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                btOutputFile,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                184,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                btConvert,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                184,
                                                                Short.MAX_VALUE))
                                        .add(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel1Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                8,
                                                                                8,
                                                                                8)
                                                                        .add(
                                                                                jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                false)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                jComboBox6,
                                                                                                0,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                cbFormat,
                                                                                                0,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                1204,
                                                                                Short.MAX_VALUE)
                                                                        .add(
                                                                                jButton4))
                                                        .add(
                                                                jPanel1Layout
                                                                        .createSequentialGroup()
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jPanel1Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                false)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                tfOutputFileBaseName)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                tfRawLogFilePath)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                tfWorkDirectory,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                519,
                                                                                                Short.MAX_VALUE))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                962,
                                                                                Short.MAX_VALUE)))
                                        .addContainerGap()));
        jPanel1Layout
                .setVerticalGroup(jPanel1Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel1Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(btWorkingDirectory)
                                                        .add(
                                                                tfWorkDirectory,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                28,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(btRawLogFile)
                                                        .add(
                                                                tfRawLogFilePath,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(btOutputFile)
                                                        .add(
                                                                tfOutputFileBaseName,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(
                                                jPanel1Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(btConvert)
                                                        .add(
                                                                cbFormat,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(jButton4))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(
                                                jComboBox6,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        tfWorkDirectory.setText(m.getBaseDirPath());
        setSelectedFormat(cbFormat.getSelectedItem().toString());

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(
                jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 1249,
                Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 155,
                Short.MAX_VALUE));

        cbPortName.setEditable(true);
        cbPortName.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "USB (for Linux, Mac)", "BLUETOOTH (for Mac)", "COM1:",
                "COM2:", "COM3:", "COM4:", "COM5:", "COM6:", "COM7:", "COM8:",
                "COM9:", "COM10:", "COM11:", "COM12:", "COM13:", "COM14:",
                "COM15:", "COM16:" }));
        cbPortName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPortNameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(
                jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel3Layout.createSequentialGroup().add(371, 371, 371).add(
                        cbPortName, 0, 0, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel3Layout.createSequentialGroup().addContainerGap().add(
                        cbPortName,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(66, Short.MAX_VALUE)));

        jPanel4.setBorder(javax.swing.BorderFactory
                .createTitledBorder("What to download"));

        jLabel15.setText("From date");

        jTextField5.setText("jTextField5");

        jLabel16.setText("to date");

        jTextField6.setText("jTextField6");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(
                jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel4Layout.createSequentialGroup().add(jLabel15)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                jTextField5,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18).add(jLabel16).add(2, 2, 2).add(
                                jTextField6,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(1445, Short.MAX_VALUE)));
        jPanel4Layout
                .setVerticalGroup(jPanel4Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel4Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel4Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel15)
                                                        .add(
                                                                jTextField5,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(jLabel16)
                                                        .add(
                                                                jTextField6,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(12, Short.MAX_VALUE)));

        jPanel17.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Various"));

        jCheckBox28.setText("disable logging while downloading");

        jLabel17.setText("Progress");

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

        DownloadProgressBar.setBackground(javax.swing.UIManager.getDefaults()
                .getColor("nbProgressBar.Foreground"));
        DownloadProgressBar.setForeground(new java.awt.Color(204, 255, 204));
        DownloadProgressBar.setFocusable(false);

        jLabel20.setText("TimeZone");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "UTC -12", "UTC -11", "UTC -10", "UTC -9", "UTC -8", "UTC -7",
                "UTC -6", "UTC -5", "UTC -4", "UTC -3", "UTC -2", "UTC -1",
                "UTC +0", "UTC +1", "UTC +2", "UTC +3", "UTC +4", "UTC +5",
                "UTC +6", "UTC +7", "UTC +8", "UTC +9", "UTC +10", "UTC +11",
                "UTC +12" }));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "Standard Time", "Daylight Savings Time" }));

        org.jdesktop.layout.GroupLayout jPanel17Layout = new org.jdesktop.layout.GroupLayout(
                jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout
                .setHorizontalGroup(jPanel17Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel17Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel17Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel17Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jButton1)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jLabel17)
                                                                        .add(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .add(
                                                                                DownloadProgressBar,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                240,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                        .add(jCheckBox28)
                                                        .add(cbIncremental)
                                                        .add(
                                                                jPanel17Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jLabel20)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                                        .add(
                                                                                jComboBox7,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                171,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                                        .add(
                                                                                jComboBox8,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                175,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap(1342, Short.MAX_VALUE)));
        jPanel17Layout
                .setVerticalGroup(jPanel17Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel17Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel17Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel20)
                                                        .add(
                                                                jComboBox7,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                jComboBox8,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(16, 16, 16)
                                        .add(jCheckBox28)
                                        .add(3, 3, 3)
                                        .add(cbIncremental)
                                        .add(7, 7, 7)
                                        .add(
                                                jPanel17Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jButton1).add(
                                                                jLabel17))
                                        .addContainerGap(30, Short.MAX_VALUE))
                        .add(
                                org.jdesktop.layout.GroupLayout.TRAILING,
                                jPanel17Layout
                                        .createSequentialGroup()
                                        .addContainerGap(101, Short.MAX_VALUE)
                                        .add(
                                                DownloadProgressBar,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(41, 41, 41)));

        DownloadProgressBar.getAccessibleContext().setAccessibleName(
                "DownloadProgessBar");
        progressBarUpdate();

        jPanel18.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Separation"));

        jLabel18.setText("New Track after");

        jTextField7.setText("jTextField7");

        jLabel19.setText("min. pause");

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "One file per day", "One file per track",
                "Everything in one file" }));

        org.jdesktop.layout.GroupLayout jPanel18Layout = new org.jdesktop.layout.GroupLayout(
                jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(jPanel18Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel18Layout.createSequentialGroup().add(jLabel18)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                jTextField7,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                jLabel19).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED, 1293,
                                Short.MAX_VALUE).add(jComboBox5,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                265,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        jPanel18Layout
                .setVerticalGroup(jPanel18Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel18Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel18Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel18)
                                                        .add(
                                                                jTextField7,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(jLabel19)
                                                        .add(
                                                                jComboBox5,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(105, Short.MAX_VALUE)));

        org.jdesktop.layout.GroupLayout downloadPanelLayout = new org.jdesktop.layout.GroupLayout(
                downloadPanel);
        downloadPanel.setLayout(downloadPanelLayout);
        downloadPanelLayout
                .setHorizontalGroup(downloadPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                downloadPanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                downloadPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                downloadPanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                downloadPanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                downloadPanelLayout
                                                                                                        .createSequentialGroup()
                                                                                                        .addPreferredGap(
                                                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                        .add(
                                                                                                                jPanel17,
                                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addPreferredGap(
                                                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                        .add(
                                                                                                                jPanel2,
                                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addPreferredGap(
                                                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                        .add(
                                                                                                                jPanel3,
                                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))
                                                                                        .add(
                                                                                                downloadPanelLayout
                                                                                                        .createParallelGroup(
                                                                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                                false)
                                                                                                        .add(
                                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                jPanel1,
                                                                                                                0,
                                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE)
                                                                                                        .add(
                                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                jPanel4,
                                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE)))
                                                                        .add(
                                                                                1265,
                                                                                1265,
                                                                                1265))
                                                        .add(
                                                                downloadPanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jPanel18,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .addContainerGap(
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)))));

        downloadPanelLayout.linkSize(new java.awt.Component[] { jPanel17,
                jPanel18 }, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        downloadPanelLayout
                .setVerticalGroup(downloadPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                org.jdesktop.layout.GroupLayout.TRAILING,
                                downloadPanelLayout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel4,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel1,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(
                                                jPanel18,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .add(14, 14, 14)
                                        .add(
                                                downloadPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                downloadPanelLayout
                                                                        .createParallelGroup(
                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                        .add(
                                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                jPanel2,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .add(
                                                                                jPanel3,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                        .add(
                                                                jPanel17,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap()));

        tabPane.addTab("Download", downloadPanel);

        jLabel1.setText("Latitude :");

        jLabel2.setText("Longitude :");

        jLabel3.setText("GPS Time :");

        lbLatitude.setText("jLabel4");

        lbLongitude.setText("jLabel4");

        lbTime.setText("jLabel5");

        jLabel4.setText("Geoid :");

        lbGeoid.setText("jLabel5");

        org.jdesktop.layout.GroupLayout gpsDecodePanelLayout = new org.jdesktop.layout.GroupLayout(
                gpsDecodePanel);
        gpsDecodePanel.setLayout(gpsDecodePanelLayout);
        gpsDecodePanelLayout
                .setHorizontalGroup(gpsDecodePanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                gpsDecodePanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                gpsDecodePanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                gpsDecodePanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                gpsDecodePanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                jLabel1)
                                                                                        .add(
                                                                                                jLabel2))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                gpsDecodePanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                lbLatitude)
                                                                                        .add(
                                                                                                lbLongitude)))
                                                        .add(
                                                                gpsDecodePanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                gpsDecodePanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                jLabel3)
                                                                                        .add(
                                                                                                jLabel4))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                                        .add(
                                                                                gpsDecodePanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                lbGeoid)
                                                                                        .add(
                                                                                                lbTime))))
                                        .addContainerGap(677, Short.MAX_VALUE)));
        gpsDecodePanelLayout
                .setVerticalGroup(gpsDecodePanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                gpsDecodePanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                gpsDecodePanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel1).add(
                                                                lbLatitude))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                gpsDecodePanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel2).add(
                                                                lbLongitude))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                gpsDecodePanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                gpsDecodePanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jLabel3)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jLabel4))
                                                        .add(
                                                                gpsDecodePanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                lbTime)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                lbGeoid)))
                                        .addContainerGap(561, Short.MAX_VALUE)));

        tabPane.addTab("GPS Decode", gpsDecodePanel);

        pnTrackpoint.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Trackpoint Filter"));

        jPanel16.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Fix Type (Valid)"));

        cbTrkNoFix.setText("No fix");
        cbTrkNoFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTrkNoFixTrkFixTypeAction(evt);
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

        org.jdesktop.layout.GroupLayout jPanel16Layout = new org.jdesktop.layout.GroupLayout(
                jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(jPanel16Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel16Layout.createSequentialGroup().addContainerGap().add(
                        jPanel16Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING).add(
                                cbTrkNoFix).add(cbTrkSPS).add(cbTrkDGPS).add(
                                cbTrkPPS).add(cbTrkRTK).add(cbTrkFRTK).add(
                                cbTrkEstimate).add(cbTrkManual).add(
                                cbTrkSimulate)).addContainerGap(
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)));
        jPanel16Layout.setVerticalGroup(jPanel16Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel16Layout.createSequentialGroup().add(cbTrkNoFix)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkSPS).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkDGPS).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkPPS).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkRTK).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkFRTK).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkEstimate).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkManual).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbTrkSimulate).add(30, 30, 30)));

        jPanel19.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Log Reason (RCR)"));

        cbTrkTime.setText("Time");
        cbTrkTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTrkTimeTrkRCRAction(evt);
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

        org.jdesktop.layout.GroupLayout jPanel19Layout = new org.jdesktop.layout.GroupLayout(
                jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout
                .setHorizontalGroup(jPanel19Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel19Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel19Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(cbTrkTime)
                                                        .add(cbTrkSpeed)
                                                        .add(cbTrkDistance)
                                                        .add(cbTrkButton)
                                                        .add(
                                                                jPanel19Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jPanel19Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                cbTrkUser1)
                                                                                        .add(
                                                                                                cbTrkUser2)
                                                                                        .add(
                                                                                                cbTrkUser3)
                                                                                        .add(
                                                                                                cbTrkUser4)
                                                                                        .add(
                                                                                                cbTrkUser5)
                                                                                        .add(
                                                                                                cbTrkUser6))
                                                                        .add(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .add(
                                                                                jPanel19Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                cbTrkUser7)
                                                                                        .add(
                                                                                                cbTrkUser8)
                                                                                        .add(
                                                                                                cbTrkUser9)
                                                                                        .add(
                                                                                                cbTrkUser10)
                                                                                        .add(
                                                                                                cbTrkUser11)
                                                                                        .add(
                                                                                                cbTrkUser12))))
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));
        jPanel19Layout
                .setVerticalGroup(jPanel19Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel19Layout
                                        .createSequentialGroup()
                                        .add(cbTrkTime)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(cbTrkSpeed)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(cbTrkDistance)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(cbTrkButton)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel19Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel19Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                cbTrkUser7)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser8)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser9)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser10)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser11)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser12))
                                                        .add(
                                                                jPanel19Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                cbTrkUser1)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser2)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser3)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser4)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser5)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbTrkUser6)))
                                        .addContainerGap()));

        org.jdesktop.layout.GroupLayout pnTrackpointLayout = new org.jdesktop.layout.GroupLayout(
                pnTrackpoint);
        pnTrackpoint.setLayout(pnTrackpointLayout);
        pnTrackpointLayout
                .setHorizontalGroup(pnTrackpointLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnTrackpointLayout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel16,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel19,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
        pnTrackpointLayout
                .setVerticalGroup(pnTrackpointLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnTrackpointLayout
                                        .createSequentialGroup()
                                        .add(
                                                pnTrackpointLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                false)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                jPanel16,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                jPanel19,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap(21, Short.MAX_VALUE)));

        pnCommonFilter.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Common Filter"));

        jPanel20.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Other"));

        txtRecCntMin.setText("jTextField2");
        txtRecCntMin.setInputVerifier(IntVerifier);
        txtRecCntMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRecCntMinFocusLost(evt);
            }
        });

        txtDistanceMin.setText("jTextField3");
        txtDistanceMin.setInputVerifier(FloatVerifier);
        txtDistanceMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDistanceMinFocusLost(evt);
            }
        });

        txtSpeedMin.setText("jTextField4");
        txtSpeedMin.setInputVerifier(FloatVerifier);
        txtSpeedMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSpeedMinFocusLost(evt);
            }
        });

        lbDistanceFltr.setText("<= distance <=");

        lbSpeedFltr.setText("<= speed <=");

        txtRecCntMax.setText("jTextField2");
        txtRecCntMax.setInputVerifier(IntVerifier);
        txtRecCntMax
                .addInputMethodListener(new java.awt.event.InputMethodListener() {
                    public void caretPositionChanged(
                            java.awt.event.InputMethodEvent evt) {
                    }

                    public void inputMethodTextChanged(
                            java.awt.event.InputMethodEvent evt) {
                        txtRecCntMaxInputMethodTextChanged(evt);
                    }
                });

        txtDistanceMax.setText("jTextField3");
        txtDistanceMax.setInputVerifier(FloatVerifier);
        txtDistanceMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDistanceMaxFocusLost(evt);
            }
        });

        txtSpeedMax.setText("jTextField4");
        txtSpeedMax.setInputVerifier(FloatVerifier);
        txtSpeedMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSpeedMaxFocusLost(evt);
            }
        });

        lbNSATFltr.setText("<= NSAT    ");

        txtNSATMin.setText("jTextField4");
        txtNSATMin.setInputVerifier(IntVerifier);
        txtNSATMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNSATMinFocusLost(evt);
            }
        });

        lbRecNbrFltr.setText("<= rec nbr <=");

        org.jdesktop.layout.GroupLayout jPanel20Layout = new org.jdesktop.layout.GroupLayout(
                jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout
                .setHorizontalGroup(jPanel20Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel20Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                jPanel20Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel20Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jPanel20Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                false)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                txtRecCntMin)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                txtDistanceMin)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                txtSpeedMin))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jPanel20Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.CENTER)
                                                                                        .add(
                                                                                                lbDistanceFltr)
                                                                                        .add(
                                                                                                lbSpeedFltr)
                                                                                        .add(
                                                                                                lbNSATFltr)
                                                                                        .add(
                                                                                                lbRecNbrFltr))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jPanel20Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.CENTER)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                txtRecCntMax,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                txtDistanceMax,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                        .add(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                txtSpeedMax,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                        .addContainerGap(
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                        .add(
                                                                jPanel20Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                txtNSATMin)
                                                                        .add(
                                                                                155,
                                                                                155,
                                                                                155)))));
        jPanel20Layout
                .setVerticalGroup(jPanel20Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                jPanel20Layout
                                        .createSequentialGroup()
                                        .add(
                                                jPanel20Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel20Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                txtRecCntMin,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jPanel20Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                                        .add(
                                                                                                txtDistanceMin,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                        .add(
                                                                                                lbDistanceFltr))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                jPanel20Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                                        .add(
                                                                                                txtSpeedMin,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                        .add(
                                                                                                lbSpeedFltr)))
                                                        .add(
                                                                jPanel20Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jPanel20Layout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                                        .add(
                                                                                                txtRecCntMax,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                        .add(
                                                                                                lbRecNbrFltr))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                txtDistanceMax,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                txtSpeedMax,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel20Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(
                                                                txtNSATMin,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                lbNSATFltr,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                14,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))));

        jPanel21.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Precision"));

        txtPDOPMax.setText("jTextField1");
        txtPDOPMax.setInputVerifier(FloatVerifier);
        txtPDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPDOPMaxFocusLost(evt);
            }
        });

        jLabel21.setText("PDOP <=");

        txtHDOPMax.setText("jTextField1");
        txtHDOPMax.setInputVerifier(FloatVerifier);
        txtHDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHDOPMaxFocusLost(evt);
            }
        });

        jLabel22.setText("HDOP <=");

        txtVDOPMax.setText("jTextField1");
        txtVDOPMax.setInputVerifier(FloatVerifier);
        txtVDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVDOPMaxFocusLost(evt);
            }
        });

        jLabel23.setText("VDOP <=");

        org.jdesktop.layout.GroupLayout jPanel21Layout = new org.jdesktop.layout.GroupLayout(
                jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout
                .setHorizontalGroup(jPanel21Layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                org.jdesktop.layout.GroupLayout.TRAILING,
                                jPanel21Layout
                                        .createSequentialGroup()
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .add(
                                                jPanel21Layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                jPanel21Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jLabel21)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                txtPDOPMax,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                jPanel21Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jLabel22)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                txtHDOPMax,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                jPanel21Layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                jLabel23)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                txtVDOPMax,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))));
        jPanel21Layout.setVerticalGroup(jPanel21Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel21Layout.createSequentialGroup().add(
                        jPanel21Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(
                                txtPDOPMax,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel21)).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED).add(
                        jPanel21Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(
                                txtHDOPMax,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel22)).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        jPanel21Layout.createParallelGroup(
                                org.jdesktop.layout.GroupLayout.BASELINE).add(
                                txtVDOPMax,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel23)).addContainerGap()));

        jLabel24.setText("Values that are 0 are ignored for the filter");

        cbAdvancedActive.setText("Activate Common Filter");
        cbAdvancedActive
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        cbAdvancedActiveStateChanged(evt);
                    }
                });

        org.jdesktop.layout.GroupLayout pnCommonFilterLayout = new org.jdesktop.layout.GroupLayout(
                pnCommonFilter);
        pnCommonFilter.setLayout(pnCommonFilterLayout);
        pnCommonFilterLayout
                .setHorizontalGroup(pnCommonFilterLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnCommonFilterLayout
                                        .createSequentialGroup()
                                        .add(
                                                pnCommonFilterLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                jPanel20,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                pnCommonFilterLayout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .add(
                                                                                jLabel24))
                                                        .add(
                                                                jPanel21,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(cbAdvancedActive))
                                        .addContainerGap()));
        pnCommonFilterLayout
                .setVerticalGroup(pnCommonFilterLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnCommonFilterLayout
                                        .createSequentialGroup()
                                        .add(cbAdvancedActive)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel21,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                98,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                jPanel20,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel24)
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        pnWaypoint.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Waypoint Filter"));

        pnWayPointFix.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Fix Type (Valid)"));

        cbWayNoFix.setText("No fix");
        cbWayNoFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbWayNoFixWayFixTypeAction(evt);
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

        org.jdesktop.layout.GroupLayout pnWayPointFixLayout = new org.jdesktop.layout.GroupLayout(
                pnWayPointFix);
        pnWayPointFix.setLayout(pnWayPointFixLayout);
        pnWayPointFixLayout
                .setHorizontalGroup(pnWayPointFixLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnWayPointFixLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                pnWayPointFixLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(cbWayNoFix).add(
                                                                cbWaySPS).add(
                                                                cbWayDGPS).add(
                                                                cbWayPPS).add(
                                                                cbWayRTK).add(
                                                                cbWayFRTK).add(
                                                                cbWayEstimate)
                                                        .add(cbWayManual).add(
                                                                cbWaySimulate))
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));
        pnWayPointFixLayout.setVerticalGroup(pnWayPointFixLayout
                .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(
                        pnWayPointFixLayout.createSequentialGroup().add(
                                cbWayNoFix).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWaySPS).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWayDGPS).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWayPPS).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWayRTK).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWayFRTK).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWayEstimate).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWayManual).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                cbWaySimulate).add(23, 23, 23)));

        pnWayPointRCR.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Log Reason (RCR)"));

        cbWayTime.setText("Time");
        cbWayTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbWayTimeWayRCRAction(evt);
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

        org.jdesktop.layout.GroupLayout pnWayPointRCRLayout = new org.jdesktop.layout.GroupLayout(
                pnWayPointRCR);
        pnWayPointRCR.setLayout(pnWayPointRCRLayout);
        pnWayPointRCRLayout
                .setHorizontalGroup(pnWayPointRCRLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnWayPointRCRLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                pnWayPointRCRLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(cbWayTime)
                                                        .add(cbWaySpeed)
                                                        .add(cbWayDistance)
                                                        .add(cbWayButton)
                                                        .add(
                                                                pnWayPointRCRLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                pnWayPointRCRLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                cbWayUser1)
                                                                                        .add(
                                                                                                cbWayUser2)
                                                                                        .add(
                                                                                                cbWayUser3)
                                                                                        .add(
                                                                                                cbWayUser4)
                                                                                        .add(
                                                                                                cbWayUser5)
                                                                                        .add(
                                                                                                cbWayUser6))
                                                                        .add(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .add(
                                                                                pnWayPointRCRLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                                                        .add(
                                                                                                cbWayUser7)
                                                                                        .add(
                                                                                                cbWayUser8)
                                                                                        .add(
                                                                                                cbWayUser9)
                                                                                        .add(
                                                                                                cbWayUser10)
                                                                                        .add(
                                                                                                cbWayUser11)
                                                                                        .add(
                                                                                                cbWayUser12))))
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));
        pnWayPointRCRLayout
                .setVerticalGroup(pnWayPointRCRLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnWayPointRCRLayout
                                        .createSequentialGroup()
                                        .add(cbWayTime)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(cbWaySpeed)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(cbWayDistance)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(cbWayButton)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                pnWayPointRCRLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                pnWayPointRCRLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                cbWayUser7)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser8)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser9)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser10)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser11)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser12))
                                                        .add(
                                                                pnWayPointRCRLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                cbWayUser1)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser2)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser3)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser4)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser5)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                cbWayUser6)))));

        org.jdesktop.layout.GroupLayout pnWaypointLayout = new org.jdesktop.layout.GroupLayout(
                pnWaypoint);
        pnWaypoint.setLayout(pnWaypointLayout);
        pnWaypointLayout
                .setHorizontalGroup(pnWaypointLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnWaypointLayout
                                        .createSequentialGroup()
                                        .add(
                                                pnWayPointFix,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                pnWayPointRCR,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap()));
        pnWaypointLayout
                .setVerticalGroup(pnWaypointLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnWaypointLayout
                                        .createSequentialGroup()
                                        .add(
                                                pnWaypointLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                false)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                pnWayPointFix,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                pnWayPointRCR,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap(28, Short.MAX_VALUE)));

        org.jdesktop.layout.GroupLayout LogFiltersPanelLayout = new org.jdesktop.layout.GroupLayout(
                LogFiltersPanel);
        LogFiltersPanel.setLayout(LogFiltersPanelLayout);
        LogFiltersPanelLayout
                .setHorizontalGroup(LogFiltersPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                LogFiltersPanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                pnTrackpoint,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                pnCommonFilter,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                pnWaypoint,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));
        LogFiltersPanelLayout
                .setVerticalGroup(LogFiltersPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                LogFiltersPanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .add(
                                                LogFiltersPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.TRAILING,
                                                                false)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                pnWaypoint,
                                                                0,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                pnTrackpoint,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .add(
                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                pnCommonFilter,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap(325, Short.MAX_VALUE)));

        tabPane.addTab("Filters", LogFiltersPanel);

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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                layout.createSequentialGroup().addContainerGap(
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(tabPane,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 787,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(tabPane,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 671,
                        Short.MAX_VALUE).addContainerGap()));

        tabPane.getAccessibleContext().setAccessibleName(
                "Log download & Convert");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbIncrementalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbIncrementalActionPerformed
        c.setIncremental(cbIncremental.isSelected());
    }// GEN-LAST:event_cbIncrementalActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        c.startDownload();
    }// GEN-LAST:event_jButton1ActionPerformed

    private void cbPortNameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbPortNameActionPerformed
        // selectPort(jComboBox1.getSelectedItem().toString());
    }// GEN-LAST:event_cbPortNameActionPerformed

    private void cbFormatActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbFormatActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_cbFormatActionPerformed

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

    private void btConvertMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btConvertMouseReleased

    }// GEN-LAST:event_btConvertMouseReleased

    private void btConvertActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btConvertActionPerformed
        c.writeLog(selectedFormat);
    }// GEN-LAST:event_btConvertActionPerformed

    private void btOutputFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btOutputFileActionPerformed
        getOutputFilePath();
        if (OutputFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            c.setOutputFileBasePath(FileUtil.getRelativePath(
                    m.getBaseDirPath(), OutputFileChooser.getSelectedFile()
                            .getAbsolutePath(), File.separatorChar));
        }
    }// GEN-LAST:event_btOutputFileActionPerformed

    private void btRawLogFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btRawLogFileActionPerformed
        getRawLogFilePath();
        if (RawLogFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            c.setLogFilePath(FileUtil.getRelativePath(m.getBaseDirPath(),
                    RawLogFileChooser.getSelectedFile().getAbsolutePath(),
                    File.separatorChar));
        }
    }// GEN-LAST:event_btRawLogFileActionPerformed

    private void btWorkingDirectoryActionPerformed1(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btWorkingDirectoryActionPerformed1
        getWorkDirPath();
        if (WorkingDirectoryChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            c.setBaseDirPath(WorkingDirectoryChooser.getSelectedFile()
                    .getAbsolutePath());
        }
    }// GEN-LAST:event_btWorkingDirectoryActionPerformed1

    private void btWorkingDirectoryActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btWorkingDirectoryActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_btWorkingDirectoryActionPerformed

    private void tfWorkDirectoryFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_tfWorkDirectoryFocusLost
        c.setBaseDirPath(tfWorkDirectory.getText());
    }// GEN-LAST:event_tfWorkDirectoryFocusLost

    private void tfWorkDirectoryActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tfWorkDirectoryActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_tfWorkDirectoryActionPerformed

    private void jCheckBox8ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox8ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jCheckBox8ActionPerformed

    private void jCheckBox22ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox22ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jCheckBox22ActionPerformed

    private void jCheckBox10ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox10ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jCheckBox10ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox5ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jCheckBox5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jButton2ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jCheckBox2ActionPerformed

    private void cbTrkNoFixTrkFixTypeAction(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbTrkNoFixTrkFixTypeAction
        setTrkValidFilterSettings();
    }// GEN-LAST:event_cbTrkNoFixTrkFixTypeAction

    private void cbTrkTimeTrkRCRAction(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbTrkTimeTrkRCRAction
        setTrkRCRFilterSettings();
    }// GEN-LAST:event_cbTrkTimeTrkRCRAction

    private void txtRecCntMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtRecCntMinFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMinRecCount(Integer.parseInt(txtRecCntMin.getText()));
    }// GEN-LAST:event_txtRecCntMinFocusLost

    private void txtDistanceMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtDistanceMinFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMinDist(Float.parseFloat(txtDistanceMin.getText()));
    }// GEN-LAST:event_txtDistanceMinFocusLost

    private void txtSpeedMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtSpeedMinFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMinSpeed(Float.parseFloat(txtSpeedMin.getText()));
    }// GEN-LAST:event_txtSpeedMinFocusLost

    private void txtRecCntMaxInputMethodTextChanged(
            java.awt.event.InputMethodEvent evt) {// GEN-FIRST:event_txtRecCntMaxInputMethodTextChanged
        // TODO : Need to use control instead of model
        m.setFilterMaxRecCount(Integer.parseInt(txtRecCntMax.getText()));
    }// GEN-LAST:event_txtRecCntMaxInputMethodTextChanged

    private void txtDistanceMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtDistanceMaxFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMaxDist(Float.parseFloat(txtDistanceMax.getText()));
    }// GEN-LAST:event_txtDistanceMaxFocusLost

    private void txtSpeedMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtSpeedMaxFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMaxSpeed(Float.parseFloat(txtSpeedMax.getText()));
    }// GEN-LAST:event_txtSpeedMaxFocusLost

    private void txtNSATMinFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtNSATMinFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMinNSAT(Integer.parseInt(txtNSATMin.getText()));
    }// GEN-LAST:event_txtNSATMinFocusLost

    private void txtPDOPMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtPDOPMaxFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMaxPDOP(Float.parseFloat(txtPDOPMax.getText()));
    }// GEN-LAST:event_txtPDOPMaxFocusLost

    private void txtHDOPMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtHDOPMaxFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMaxHDOP(Float.parseFloat(txtHDOPMax.getText()));
    }// GEN-LAST:event_txtHDOPMaxFocusLost

    private void txtVDOPMaxFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtVDOPMaxFocusLost
        // TODO : Need to use control instead of model
        m.setFilterMaxVDOP(Float.parseFloat(txtVDOPMax.getText()));
    }// GEN-LAST:event_txtVDOPMaxFocusLost

    private void cbAdvancedActiveStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_cbAdvancedActiveStateChanged
        // TODO : Need to use control instead of model
        m.setAdvFilterActive(cbAdvancedActive.isSelected());
    }// GEN-LAST:event_cbAdvancedActiveStateChanged

    private void cbWayNoFixWayFixTypeAction(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbWayNoFixWayFixTypeAction
        setWayValidFilterSettings();
    }// GEN-LAST:event_cbWayNoFixWayFixTypeAction

    private void cbWayTimeWayRCRAction(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cbWayTimeWayRCRAction
        setWayRCRFilterSettings();
    }// GEN-LAST:event_cbWayTimeWayRCRAction

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

    private void progressBarUpdate() {
        DownloadProgressBar.setMinimum(m.getStartAddr());
        DownloadProgressBar.setMaximum(m.getEndAddr());
        DownloadProgressBar.setValue(m.getNextReadAddr());
        DownloadProgressBar.setVisible(m.isDownloadOnGoing());
        // TODO: DownloadProgressLabel.setVisible(m.isDownloadOnGoing());
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
                BT747_Main2 app = new BT747_Main2(m, c);
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
    private javax.swing.JProgressBar DownloadProgressBar;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu FileMenu1;
    private javax.swing.JMenu InfoMenu;
    private javax.swing.JMenu InfoMenu1;
    private javax.swing.JPanel LogFiltersPanel;
    private javax.swing.JFileChooser OutputFileChooser;
    private javax.swing.JFileChooser RawLogFileChooser;
    private javax.swing.JMenu SettingsMenu;
    private javax.swing.JMenu SettingsMenu1;
    private javax.swing.JFileChooser WorkingDirectoryChooser;
    private javax.swing.JButton btConvert;
    private javax.swing.JButton btOutputFile;
    private javax.swing.JButton btRawLogFile;
    private javax.swing.JButton btWorkingDirectory;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbAdvancedActive;
    private javax.swing.JComboBox cbFormat;
    private javax.swing.JCheckBox cbIncremental;
    private javax.swing.JComboBox cbPortName;
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
    private javax.swing.JPanel connectionPanel;
    private javax.swing.JPanel downloadPanel;
    private javax.swing.JPanel gpsDecodePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox14;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox16;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox18;
    private javax.swing.JCheckBox jCheckBox19;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox20;
    private javax.swing.JCheckBox jCheckBox21;
    private javax.swing.JCheckBox jCheckBox22;
    private javax.swing.JCheckBox jCheckBox23;
    private javax.swing.JCheckBox jCheckBox24;
    private javax.swing.JCheckBox jCheckBox25;
    private javax.swing.JCheckBox jCheckBox26;
    private javax.swing.JCheckBox jCheckBox27;
    private javax.swing.JCheckBox jCheckBox28;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel lbDistanceFltr;
    private javax.swing.JLabel lbGeoid;
    private javax.swing.JLabel lbLatitude;
    private javax.swing.JLabel lbLongitude;
    private javax.swing.JLabel lbNSATFltr;
    private javax.swing.JLabel lbRecNbrFltr;
    private javax.swing.JLabel lbSpeedFltr;
    private javax.swing.JLabel lbTime;
    private javax.swing.JPanel pnCommonFilter;
    private javax.swing.JPanel pnTrackpoint;
    private javax.swing.JPanel pnWayPointFix;
    private javax.swing.JPanel pnWayPointRCR;
    private javax.swing.JPanel pnWaypoint;
    private javax.swing.JTabbedPane tabPane;
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
    private javax.swing.JPanel whatToLogPanel;
    private javax.swing.JPanel whenToLogPanel;
    // End of variables declaration//GEN-END:variables

}
