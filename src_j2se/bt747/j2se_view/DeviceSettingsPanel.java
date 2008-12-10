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

import gps.BT747Constants;

import javax.swing.JPanel;

import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;
import bt747.sys.Generic;

/**
 *
 * @author  Mario
 */
public class DeviceSettingsPanel extends javax.swing.JPanel implements
        ModelListener {

    /**
     * 
     */
    private static final long serialVersionUID = 7501598981469723355L;
    private J2SEAppController c;
    private Model m;
    
    /** Creates new form DeviceSettingsPanel */
    public DeviceSettingsPanel() {
        initComponents();
        
        cbDGPSType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("No_DGPS"), getString("RTCM"), getString("WAAS") }));
        cbStopOrOverwriteWhenFull
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                        getString("Stop_when_full"), getString("Overwrite_when_full") }));

    }


    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);

        updateSatGuiItems();
        cbStopOrOverwriteWhenFull.setSelectedIndex(m.isLogFullOverwrite() ? 1
                : 0);
        txtHoluxName.setText(m.getHoluxName());
        updateEstimatedNbrRecords();
        updateConnected(m.isConnected());


    }

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


    private final void updateStoreButtons() {
        btStoreSettings.setEnabled(c.isEnableStoreOK());
        btRestoreSettings.setEnabled(m.isStoredSetting1());
    }
    
    public void modelEvent(final ModelEvent e) {
        // TODO Auto-generated method stub
        int type = e.getType();
        switch(type) {
        case ModelEvent.UPDATE_FIX_PERIOD:
        case ModelEvent.UPDATE_DGPS_MODE:
        case ModelEvent.UPDATE_SBAS:
        case ModelEvent.UPDATE_SBAS_TEST:
        case ModelEvent.UPDATE_DATUM:
        case ModelEvent.UPDATE_LOG_TIME_INTERVAL:
        case ModelEvent.UPDATE_LOG_SPEED_INTERVAL:
        case ModelEvent.UPDATE_LOG_DISTANCE_INTERVAL:
        case ModelEvent.UPDATE_LOG_FORMAT:
        case ModelEvent.UPDATE_OUTPUT_NMEA_PERIOD:
            updateStoreButtons();
            break;
        }


        switch (type) {
        case ModelEvent.CONNECTED:
            updateConnected(true);
            break;
        case ModelEvent.DISCONNECTED:
            updateConnected(false);
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
                Generic.debug(getString("Unknown_DGPS_Mode") + m.getDgpsMode(), ee);
            }
            break;

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
                Generic.debug(getString("Unknown_DATUM") + m.getDatum(), ee);
            }
            break;
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
            txtFixPeriod.setText(Convert.toString(m.getLogFixPeriod()));
            break;
        case ModelEvent.UPDATE_LOG_FORMAT:
            updateLogFormatData();
            break;
        case ModelEvent.UPDATE_LOG_REC_METHOD:
            cbStopOrOverwriteWhenFull.setSelectedIndex(m.isLogFullOverwrite() ? 1
                    : 0);
            break;
        case ModelEvent.UPDATE_HOLUX_NAME:
            txtHoluxName.setText(m.getHoluxName());
            break;
        }
    }

    private final void updateConnected(final boolean connected) {
        JPanel[] panels = { 
                pnLogFormat, pnGPSStart, pnLogBy, pnSBAS,
                pnHoluxSettings};

        for (JPanel panel : panels) {
            J2SEAppController.disablePanel(panel,connected);
        }
        if(connected) {
            updateSatGuiItems();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        pnGPSSettings = new javax.swing.JPanel();
        pnHoluxSettings = new javax.swing.JPanel();
        lbHoluxName = new javax.swing.JLabel();
        txtHoluxName = new javax.swing.JTextField();
        btSetHoluxName = new javax.swing.JButton();
        pnLogBy = new javax.swing.JPanel();
        txtTimeSeconds = new javax.swing.JLabel();
        ckLogSpeedActive = new javax.swing.JCheckBox();
        txtLogDistanceInterval = new javax.swing.JTextField();
        txtLogTimeInterval = new javax.swing.JTextField();
        lbKMH = new javax.swing.JLabel();
        ckLogTimeActive = new javax.swing.JCheckBox();
        lbAbove = new javax.swing.JLabel();
        lbDistancePeriodM = new javax.swing.JLabel();
        lbDistanceEvery = new javax.swing.JLabel();
        ckLogDistanceActive = new javax.swing.JCheckBox();
        txtLogSpeedInterval = new javax.swing.JTextField();
        txtTimeEvery = new javax.swing.JLabel();
        btLogByApply = new javax.swing.JButton();
        lbFixEvery = new javax.swing.JLabel();
        txtFixPeriod = new javax.swing.JTextField();
        lbFixMs = new javax.swing.JLabel();
        cbStopOrOverwriteWhenFull = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        pnVolatileSettings = new javax.swing.JPanel();
        btStoreSettings = new javax.swing.JButton();
        btRestoreSettings = new javax.swing.JButton();
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

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        pnHoluxSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnHoluxSettings.border.title"))); // NOI18N

        lbHoluxName.setText(bundle.getString("BT747Main.lbHoluxName.text")); // NOI18N

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
                .add(lbHoluxName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtHoluxName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btSetHoluxName)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnHoluxSettingsLayout.setVerticalGroup(
            pnHoluxSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnHoluxSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(lbHoluxName)
                .add(btSetHoluxName)
                .add(txtHoluxName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnLogBy.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnLogBy.border.title"))); // NOI18N
        pnLogBy.setToolTipText(bundle.getString("BT747Main.pnLogBy.toolTipText")); // NOI18N

        txtTimeSeconds.setText(bundle.getString("BT747Main.txtTimeSeconds.text")); // NOI18N

        ckLogSpeedActive.setText(bundle.getString("BT747Main.ckLogSpeedActive.text")); // NOI18N

        txtLogDistanceInterval.setText(bundle.getString("BT747Main.txtLogDistanceInterval.text")); // NOI18N
        txtLogDistanceInterval.setInputVerifier(c.FloatVerifier);

        txtLogTimeInterval.setText(bundle.getString("BT747Main.txtLogTimeInterval.text")); // NOI18N
        txtLogTimeInterval.setInputVerifier(c.FloatVerifier);

        lbKMH.setText(bundle.getString("BT747Main.lbKMH.text")); // NOI18N

        ckLogTimeActive.setText(bundle.getString("BT747Main.ckLogTimeActive.text")); // NOI18N

        lbAbove.setText(bundle.getString("BT747Main.lbAbove.text")); // NOI18N

        lbDistancePeriodM.setText(bundle.getString("BT747Main.lbDistancePeriodM.text")); // NOI18N

        lbDistanceEvery.setText(bundle.getString("BT747Main.lbDistanceEvery.text")); // NOI18N

        ckLogDistanceActive.setText(bundle.getString("BT747Main.ckLogDistanceActive.text")); // NOI18N

        txtLogSpeedInterval.setText(bundle.getString("BT747Main.txtLogSpeedInterval.text")); // NOI18N
        txtLogSpeedInterval.setInputVerifier(c.IntVerifier);

        txtTimeEvery.setText(bundle.getString("BT747Main.txtTimeEvery.text")); // NOI18N

        btLogByApply.setText(bundle.getString("BT747Main.btLogByApply.text")); // NOI18N
        btLogByApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogByApplyActionPerformed(evt);
            }
        });

        lbFixEvery.setText(bundle.getString("BT747Main.lbFixEvery.text")); // NOI18N

        txtFixPeriod.setText(bundle.getString("BT747Main.txtFixPeriod.text")); // NOI18N

        lbFixMs.setText(bundle.getString("BT747Main.lbFixMs.text")); // NOI18N

        cbStopOrOverwriteWhenFull.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Stop when full", "Overwrite when full" }));
        cbStopOrOverwriteWhenFull.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbStopOrOverwriteWhenFullItemStateChanged(evt);
            }
        });
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
                        .add(lbFixEvery)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtFixPeriod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnLogByLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ckLogSpeedActive)
                            .add(ckLogDistanceActive)
                            .add(ckLogTimeActive))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lbDistanceEvery)
                            .add(lbAbove)
                            .add(txtTimeEvery))
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
                        .add(lbFixMs))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnLogByLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtTimeSeconds)
                            .add(lbDistancePeriodM)
                            .add(lbKMH))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(btLogByApply, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbStopOrOverwriteWhenFull, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        pnLogByLayout.setVerticalGroup(
            pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLogByLayout.createSequentialGroup()
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbFixEvery)
                    .add(lbFixMs)
                    .add(btLogByApply)
                    .add(txtFixPeriod, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtTimeSeconds)
                    .add(txtTimeEvery, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(ckLogTimeActive)
                    .add(txtLogTimeInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbAbove)
                    .add(lbKMH)
                    .add(ckLogSpeedActive)
                    .add(txtLogSpeedInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogByLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbDistanceEvery)
                    .add(lbDistancePeriodM)
                    .add(ckLogDistanceActive)
                    .add(txtLogDistanceInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbStopOrOverwriteWhenFull, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnVolatileSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("DeviceSettingsPanel.pnVolatileSettings.border.title"))); // NOI18N
        pnVolatileSettings.setToolTipText(bundle.getString("DeviceSettingsPanel.pnVolatileSettings.toolTipText")); // NOI18N

        btStoreSettings.setText(bundle.getString("DeviceSettingsPanel.btStoreSettings.text")); // NOI18N
        btStoreSettings.setToolTipText(bundle.getString("DeviceSettingsPanel.btStoreSettings.toolTipText")); // NOI18N
        btStoreSettings.setEnabled(false);
        btStoreSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStoreSettingsActionPerformed(evt);
            }
        });

        btRestoreSettings.setText(bundle.getString("DeviceSettingsPanel.btRestoreSettings.text")); // NOI18N
        btRestoreSettings.setToolTipText(bundle.getString("DeviceSettingsPanel.btRestoreSettings.toolTipText")); // NOI18N
        btRestoreSettings.setEnabled(false);
        btRestoreSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRestoreSettingsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnVolatileSettingsLayout = new org.jdesktop.layout.GroupLayout(pnVolatileSettings);
        pnVolatileSettings.setLayout(pnVolatileSettingsLayout);
        pnVolatileSettingsLayout.setHorizontalGroup(
            pnVolatileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnVolatileSettingsLayout.createSequentialGroup()
                .add(btStoreSettings)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btRestoreSettings))
        );
        pnVolatileSettingsLayout.setVerticalGroup(
            pnVolatileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnVolatileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(btStoreSettings)
                .add(btRestoreSettings))
        );

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
                .add(btApplySBAS))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(pnGPSStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnVolatileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnSBAS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPSStart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jPanel1Layout.createSequentialGroup()
                .add(pnSBAS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnVolatileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout pnGPSSettingsLayout = new org.jdesktop.layout.GroupLayout(pnGPSSettings);
        pnGPSSettings.setLayout(pnGPSSettingsLayout);
        pnGPSSettingsLayout.setHorizontalGroup(
            pnGPSSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPSSettingsLayout.createSequentialGroup()
                .add(pnGPSSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnHoluxSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnLogBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20))
        );
        pnGPSSettingsLayout.setVerticalGroup(
            pnGPSSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPSSettingsLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(pnHoluxSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnLogBy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                cbUTCTimeupdateLogRecordEstCount(evt);
            }
        });

        cbMilliSeconds.setText(bundle.getString("BT747Main.cbMilliSeconds.text")); // NOI18N
        cbMilliSeconds.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMilliSecondsupdateLogRecordEstCount(evt);
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
                cbLatupdateLogRecordEstCount(evt);
            }
        });

        cbLong.setText(bundle.getString("BT747Main.cbLong.text")); // NOI18N
        cbLong.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbLongupdateLogRecordEstCount(evt);
            }
        });

        cbHeight.setText(bundle.getString("BT747Main.cbHeight.text")); // NOI18N
        cbHeight.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbHeightupdateLogRecordEstCount(evt);
            }
        });

        cbSpeed.setText(bundle.getString("BT747Main.cbSpeed.text")); // NOI18N
        cbSpeed.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbSpeedupdateLogRecordEstCount(evt);
            }
        });

        cbHeading.setText(bundle.getString("BT747Main.cbHeading.text")); // NOI18N
        cbHeading.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbHeadingupdateLogRecordEstCount(evt);
            }
        });

        cbDistance.setText(bundle.getString("BT747Main.cbDistance.text")); // NOI18N
        cbDistance.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDistanceupdateLogRecordEstCount(evt);
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
                cbDSTAupdateLogRecordEstCount(evt);
            }
        });

        cbDAGE.setText(bundle.getString("BT747Main.cbDAGE.text")); // NOI18N
        cbDAGE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDAGEupdateLogRecordEstCount(evt);
            }
        });

        cbPDOP.setText(bundle.getString("BT747Main.cbPDOP.text")); // NOI18N
        cbPDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbPDOPupdateLogRecordEstCount(evt);
            }
        });

        cbHDOP.setText(bundle.getString("BT747Main.cbHDOP.text")); // NOI18N
        cbHDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbHDOPupdateLogRecordEstCount(evt);
            }
        });

        cbVDOP.setText(bundle.getString("BT747Main.cbVDOP.text")); // NOI18N
        cbVDOP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbVDOPupdateLogRecordEstCount(evt);
            }
        });

        cbFixType.setText(bundle.getString("BT747Main.cbFixType.text")); // NOI18N
        cbFixType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFixTypeupdateLogRecordEstCount(evt);
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
                cbNSATupdateLogRecordEstCount(evt);
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
                cbElevationupdateLogRecordEstCount(evt);
            }
        });

        cbAzimuth.setText(bundle.getString("BT747Main.cbAzimuth.text")); // NOI18N
        cbAzimuth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAzimuthupdateLogRecordEstCount(evt);
            }
        });

        cbSNR.setText(bundle.getString("BT747Main.cbSNR.text")); // NOI18N
        cbSNR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbSNRupdateLogRecordEstCount(evt);
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
                cbValidFixOnlyupdateLogRecordEstCount(evt);
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
                cbRCRupdateLogRecordEstCount(evt);
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnLogFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnGPSSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPSSettings, 0, 355, Short.MAX_VALUE)
            .add(pnLogFormat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }//GEN-END:initComponents

private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
   DeviceSettingsPanelFocusGained(evt);
}//GEN-LAST:event_formFocusGained

private void btRestoreSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRestoreSettingsActionPerformed
  c.restoreSetting1();
}//GEN-LAST:event_btRestoreSettingsActionPerformed

private void btStoreSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStoreSettingsActionPerformed
   c.storeSetting1();
}//GEN-LAST:event_btStoreSettingsActionPerformed

private void cbStopOrOverwriteWhenFullItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbStopOrOverwriteWhenFullItemStateChanged
    if(cbStopOrOverwriteWhenFull.hasFocus()) {
        sendStopOrOverwrite();
    }
}//GEN-LAST:event_cbStopOrOverwriteWhenFullItemStateChanged

    private void btSetHoluxNameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.setHoluxName(txtHoluxName.getText());
    }// GEN-LAST:event_btHotStartActionPerformed

    private void btApplySBASActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btHotStartActionPerformed
        c.setSBASEnabled(cbUseSBAS.isSelected());
        c.setSBASTestEnabled(cbIncludeTestSBAS.isSelected());
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
            c.setFixInterval(Convert.toInt(txtFixPeriod.getText()));
        } catch (Exception e) {
            Generic
                    .debug(
                            getString("Problem_in_Apply_Log_conditions_-_probably_non-numeric_value"),
                            e);
        }
    }// GEN-LAST:event_btHotStartActionPerformed

    
    private void sendStopOrOverwrite() {
        boolean newSetting = (cbStopOrOverwriteWhenFull.getSelectedIndex() == 1);
        if(newSetting!=m.isLogFullOverwrite()) {
            c.setLogOverwrite(newSetting);
        }
    }
    private void cbStopOrOverwriteWhenFullFocusLost( java.awt.event.FocusEvent evt) {// GEN-FIRST:event_cbStopOrOverwriteWhenFullFocusLost
        sendStopOrOverwrite();
    }// GEN-LAST:event_cbStopOrOverwriteWhenFullFocusLost

    
    private void cbUTCTimeupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbUTCTimeupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbUTCTimeupdateLogRecordEstCount

    private void cbMilliSecondsupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbMilliSecondsupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbMilliSecondsupdateLogRecordEstCount

    private void cbLatupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbLatupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbLatupdateLogRecordEstCount

    private void cbLongupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbLongupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbLongupdateLogRecordEstCount

    private void cbHeightupdateLogRecordEstCount(
            java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbHeightupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbHeightupdateLogRecordEstCount

    private void cbSpeedupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbSpeedupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbSpeedupdateLogRecordEstCount

    private void cbHeadingupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbHeadingupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbHeadingupdateLogRecordEstCount

    private void cbDistanceupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbDistanceupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbDistanceupdateLogRecordEstCount

    private void cbDSTAupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbDSTAupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbDSTAupdateLogRecordEstCount

    private void cbDAGEupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbDAGEupdateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbDAGEupdateLogRecordEstCount

    private void cbHDOPupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbPDOP1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbPDOP1updateLogRecordEstCount

    private void cbPDOPupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbHDOP1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbHDOP1updateLogRecordEstCount

    private void cbVDOPupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbVDOP1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbVDOP1updateLogRecordEstCount

    private void cbFixTypeupdateLogRecordEstCount(
            java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbFixType1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbFixType1updateLogRecordEstCount

    private void cbNSATupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbNSAT1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbNSAT1updateLogRecordEstCount

    private void cbElevationupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbElevation1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbElevation1updateLogRecordEstCount

    private void cbAzimuthupdateLogRecordEstCount( java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbAzimuth1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbAzimuth1updateLogRecordEstCount

    private void cbSNRupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbSNR1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbSNR1updateLogRecordEstCount

    private void cbRCRupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbRCR1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbRCR1updateLogRecordEstCount

    private void cbValidFixOnlyupdateLogRecordEstCount(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbRCR1updateLogRecordEstCount
        updateEstimatedNbrRecords();
    }// GEN-LAST:event_cbRCR1updateLogRecordEstCount

    private void btFormatAndEraseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btFormatAndEraseActionPerformed

        c.changeLogFormatAndErase(getUserLogFormat());
    }// GEN-LAST:event_btFormatAndEraseActionPerformed

    private void btFormatActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btFormatActionPerformed

        c.changeLogFormat(getUserLogFormat());
    }// GEN-LAST:event_btFormatActionPerformed

    private void btEraseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btEraseActionPerformed

        c.eraseLogWithDialogs();
    }// GEN-LAST:event_btEraseActionPerformed

    private void btRecoverMemoryActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btRecoverMemoryActionPerformed

        c.recoveryErase();
    }// GEN-LAST:event_btRecoverMemoryActionPerformed

    private void cbSIDItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cbSIDItemStateChanged
        updateSatGuiItems();
    }// GEN-LAST:event_cbSIDItemStateChanged

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
                + getString("RECORDS_ESTIMATED"));
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

    private static final String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btApplySBAS;
    private javax.swing.JButton btColdStart;
    private javax.swing.JButton btErase;
    private javax.swing.JButton btFactoryResetDevice;
    private javax.swing.JButton btFormat;
    private javax.swing.JButton btFormatAndErase;
    private javax.swing.JButton btHotStart;
    private javax.swing.JButton btLogByApply;
    private javax.swing.JButton btRecoverMemory;
    private javax.swing.JButton btRestoreSettings;
    private javax.swing.JButton btSetHoluxName;
    private javax.swing.JButton btStoreSettings;
    private javax.swing.JButton btWarmStart;
    private javax.swing.JCheckBox cbAzimuth;
    private javax.swing.JCheckBox cbDAGE;
    private javax.swing.JComboBox cbDGPSType;
    private javax.swing.JCheckBox cbDSTA;
    private javax.swing.JCheckBox cbDistance;
    private javax.swing.JCheckBox cbElevation;
    private javax.swing.JCheckBox cbFixType;
    private javax.swing.JCheckBox cbHDOP;
    private javax.swing.JCheckBox cbHeading;
    private javax.swing.JCheckBox cbHeight;
    private javax.swing.JCheckBox cbIncludeTestSBAS;
    private javax.swing.JCheckBox cbLat;
    private javax.swing.JCheckBox cbLong;
    private javax.swing.JCheckBox cbMilliSeconds;
    private javax.swing.JCheckBox cbNSAT;
    private javax.swing.JPanel cbOtherFormat;
    private javax.swing.JCheckBox cbPDOP;
    private javax.swing.JCheckBox cbRCR;
    private javax.swing.JCheckBox cbSID;
    private javax.swing.JCheckBox cbSNR;
    private javax.swing.JCheckBox cbSpeed;
    private javax.swing.JComboBox cbStopOrOverwriteWhenFull;
    private javax.swing.JCheckBox cbUTCTime;
    private javax.swing.JCheckBox cbUseSBAS;
    private javax.swing.JCheckBox cbVDOP;
    private javax.swing.JCheckBox cbValidFixOnly;
    private javax.swing.JCheckBox ckLogDistanceActive;
    private javax.swing.JCheckBox ckLogSpeedActive;
    private javax.swing.JCheckBox ckLogTimeActive;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel lbAbove;
    private javax.swing.JLabel lbDistanceEvery;
    private javax.swing.JLabel lbDistancePeriodM;
    private javax.swing.JLabel lbFixEvery;
    private javax.swing.JLabel lbFixMs;
    private javax.swing.JLabel lbHoluxName;
    private javax.swing.JLabel lbKMH;
    private javax.swing.JPanel pnGPSSettings;
    private javax.swing.JPanel pnGPSStart;
    private javax.swing.JPanel pnHoluxSettings;
    private javax.swing.JPanel pnLogBy;
    private javax.swing.JPanel pnLogFormat;
    private javax.swing.JPanel pnPosition;
    private javax.swing.JPanel pnPrecision;
    private javax.swing.JPanel pnReason;
    private javax.swing.JPanel pnSBAS;
    private javax.swing.JPanel pnSatInfo;
    private javax.swing.JPanel pnTime;
    private javax.swing.JPanel pnVolatileSettings;
    private javax.swing.JLabel txtEstimatedRecords;
    private javax.swing.JTextField txtFixPeriod;
    private javax.swing.JTextField txtHoluxName;
    private javax.swing.JTextField txtLogDistanceInterval;
    private javax.swing.JTextField txtLogSpeedInterval;
    private javax.swing.JTextField txtLogTimeInterval;
    private javax.swing.JLabel txtTimeEvery;
    private javax.swing.JLabel txtTimeSeconds;
    // End of variables declaration//GEN-END:variables

}