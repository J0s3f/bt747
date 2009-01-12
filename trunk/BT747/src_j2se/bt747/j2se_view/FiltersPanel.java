/*
 * FiltersPanel.java
 *
 * Created on 21 novembre 2008, 20:40
 */

package bt747.j2se_view;

import gps.BT747Constants;

import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import java.awt.Component;
import java.util.Locale;

/**
 *
 * @author  Mario
 */
@SuppressWarnings("serial")
public class FiltersPanel extends javax.swing.JPanel implements ModelListener {

    private J2SEAppController c;
    private Model m;

    /** Creates new form FiltersPanel */
    public FiltersPanel() {
        initComponents();
    }

    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);
        
        updateGuiLogFilterSettings();
        cbAdvancedActive.setSelected(m.getBooleanOpt(AppSettings.ADVFILTACTIVE));
        updateAdvancedFilter();

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

    }

    public void modelEvent(final ModelEvent e) {
        // TODO Auto-generated method stub
        int type = e.getType();
        switch (type) {
        case ModelEvent.TRK_VALID_CHANGE:
        case ModelEvent.TRK_RCR_CHANGE:
        case ModelEvent.WAY_VALID_CHANGE:
        case ModelEvent.WAY_RCR_CHANGE:
            updateGuiLogFilterSettings();
            break;
        }
    }

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
        cbTrkUser1.setSelected((BT747Constants.RCR_ALL_APP_MASK & trkRCR) != 0);

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
        cbWayUser1.setSelected((BT747Constants.RCR_ALL_APP_MASK & wayRCR) != 0);
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
            trkRCR |= BT747Constants.RCR_ALL_APP_MASK;
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
            wayRCR |= BT747Constants.RCR_ALL_APP_MASK;
        }
        c.setWayPtRCR(wayRCR);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        pnTrackpoint = new javax.swing.JPanel();
        pnTrkFixType = new javax.swing.JPanel();
        cbTrkNoFix = new javax.swing.JCheckBox();
        cbTrkPPS = new javax.swing.JCheckBox();
        cbTrkEstimate = new javax.swing.JCheckBox();
        cbTrkManual = new javax.swing.JCheckBox();
        cbTrkSPS = new javax.swing.JCheckBox();
        cbTrkFRTK = new javax.swing.JCheckBox();
        cbTrkDGPS = new javax.swing.JCheckBox();
        cbTrkSimulate = new javax.swing.JCheckBox();
        cbTrkRTK = new javax.swing.JCheckBox();
        pnTrkLogReason = new javax.swing.JPanel();
        cbTrkTime = new javax.swing.JCheckBox();
        cbTrkSpeed = new javax.swing.JCheckBox();
        cbTrkDistance = new javax.swing.JCheckBox();
        cbTrkButton = new javax.swing.JCheckBox();
        cbTrkUser1 = new javax.swing.JCheckBox();
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
        lbPDOPMax = new javax.swing.JLabel();
        txtHDOPMax = new javax.swing.JTextField();
        lbHDOPLimit = new javax.swing.JLabel();
        txtVDOPMax = new javax.swing.JTextField();
        lbVDOPLimit = new javax.swing.JLabel();
        lbIgnore0Values = new javax.swing.JLabel();
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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        pnTrackpoint.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnTrackpoint.border.title"))); // NOI18N
        pnTrackpoint.setToolTipText(bundle.getString("BT747Main.pnTrackpoint.toolTipText")); // NOI18N

        pnTrkFixType.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnTrkFixType.border.title"))); // NOI18N

        cbTrkNoFix.setText(bundle.getString("BT747Main.cbTrkNoFix.text")); // NOI18N
        cbTrkNoFix.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkNoFixTrkFixTypeAction(evt);
            }
        });

        cbTrkPPS.setText(bundle.getString("BT747Main.cbTrkPPS.text")); // NOI18N
        cbTrkPPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkPPSTrkFixTypeAction(evt);
            }
        });

        cbTrkEstimate.setText(bundle.getString("BT747Main.cbTrkEstimate.text")); // NOI18N
        cbTrkEstimate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkEstimateTrkFixTypeAction(evt);
            }
        });

        cbTrkManual.setText(bundle.getString("BT747Main.cbTrkManual.text")); // NOI18N
        cbTrkManual.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkManualTrkFixTypeAction(evt);
            }
        });

        cbTrkSPS.setText(bundle.getString("BT747Main.cbTrkSPS.text")); // NOI18N
        cbTrkSPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkSPSTrkFixTypeAction(evt);
            }
        });

        cbTrkFRTK.setText(bundle.getString("BT747Main.cbTrkFRTK.text")); // NOI18N
        cbTrkFRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkFRTKTrkFixTypeAction(evt);
            }
        });

        cbTrkDGPS.setText(bundle.getString("BT747Main.cbTrkDGPS.text")); // NOI18N
        cbTrkDGPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkDGPSTrkFixTypeAction(evt);
            }
        });

        cbTrkSimulate.setText(bundle.getString("BT747Main.cbTrkSimulate.text")); // NOI18N
        cbTrkSimulate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkSimulateTrkFixTypeAction(evt);
            }
        });

        cbTrkRTK.setText(bundle.getString("BT747Main.cbTrkRTK.text")); // NOI18N
        cbTrkRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkRTKTrkFixTypeAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnTrkFixTypeLayout = new org.jdesktop.layout.GroupLayout(pnTrkFixType);
        pnTrkFixType.setLayout(pnTrkFixTypeLayout);
        pnTrkFixTypeLayout.setHorizontalGroup(
            pnTrkFixTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrkFixTypeLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnTrkFixTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
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

        pnTrkFixTypeLayout.linkSize(new java.awt.Component[] {cbTrkDGPS, cbTrkEstimate, cbTrkFRTK, cbTrkManual, cbTrkNoFix, cbTrkPPS, cbTrkRTK, cbTrkSPS, cbTrkSimulate}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnTrkFixTypeLayout.setVerticalGroup(
            pnTrkFixTypeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrkFixTypeLayout.createSequentialGroup()
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
                .addContainerGap())
        );

        pnTrkLogReason.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnTrkLogReason.border.title"))); // NOI18N
        pnTrkLogReason.setMaximumSize(new java.awt.Dimension(100, 32767));
        pnTrkLogReason.setVerifyInputWhenFocusTarget(false);

        cbTrkTime.setText(bundle.getString("BT747Main.cbTrkTime.text")); // NOI18N
        cbTrkTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkTimeTrkRCRAction(evt);
            }
        });

        cbTrkSpeed.setText(bundle.getString("BT747Main.cbTrkSpeed.text")); // NOI18N
        cbTrkSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkSpeedTrkRCRAction(evt);
            }
        });

        cbTrkDistance.setText(bundle.getString("BT747Main.cbTrkDistance.text")); // NOI18N
        cbTrkDistance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkDistanceTrkRCRAction(evt);
            }
        });

        cbTrkButton.setText(bundle.getString("BT747Main.cbTrkButton.text")); // NOI18N
        cbTrkButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkButtonTrkRCRAction(evt);
            }
        });

        cbTrkUser1.setText(bundle.getString("BT747Main.cbTrkUser1.text")); // NOI18N
        cbTrkUser1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbTrkUser1TrkRCRAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnTrkLogReasonLayout = new org.jdesktop.layout.GroupLayout(pnTrkLogReason);
        pnTrkLogReason.setLayout(pnTrkLogReasonLayout);
        pnTrkLogReasonLayout.setHorizontalGroup(
            pnTrkLogReasonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrkLogReasonLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnTrkLogReasonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbTrkTime)
                    .add(cbTrkSpeed)
                    .add(cbTrkDistance)
                    .add(cbTrkButton)
                    .add(cbTrkUser1))
                .add(0, 0, Short.MAX_VALUE))
        );

        pnTrkLogReasonLayout.linkSize(new java.awt.Component[] {cbTrkButton, cbTrkDistance, cbTrkSpeed, cbTrkTime, cbTrkUser1}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnTrkLogReasonLayout.setVerticalGroup(
            pnTrkLogReasonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrkLogReasonLayout.createSequentialGroup()
                .add(cbTrkTime)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkSpeed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkDistance)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTrkUser1))
        );

        org.jdesktop.layout.GroupLayout pnTrackpointLayout = new org.jdesktop.layout.GroupLayout(pnTrackpoint);
        pnTrackpoint.setLayout(pnTrackpointLayout);
        pnTrackpointLayout.setHorizontalGroup(
            pnTrackpointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrackpointLayout.createSequentialGroup()
                .add(pnTrkFixType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(pnTrkLogReason, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnTrackpointLayout.setVerticalGroup(
            pnTrackpointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrkFixType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnTrkLogReason, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );

        pnCommonFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnCommonFilter.border.title"))); // NOI18N
        pnCommonFilter.setToolTipText(bundle.getString("BT747Main.pnCommonFilter.toolTipText")); // NOI18N

        pnFilterOther.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFilterOther.border.title"))); // NOI18N

        txtRecCntMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecCntMin.setText(bundle.getString("BT747Main.txtRecCntMin.text")); // NOI18N
        txtRecCntMin.setInputVerifier(c.IntVerifier);
        txtRecCntMin.setMinimumSize(new java.awt.Dimension(50, 40));
        txtRecCntMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRecCntMinFocusLost(evt);
            }
        });

        txtDistanceMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDistanceMin.setText(bundle.getString("BT747Main.txtDistanceMin.text")); // NOI18N
        txtDistanceMin.setInputVerifier(c.FloatVerifier);
        txtDistanceMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDistanceMinFocusLost(evt);
            }
        });

        txtSpeedMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSpeedMin.setText(bundle.getString("BT747Main.txtSpeedMin.text")); // NOI18N
        txtSpeedMin.setInputVerifier(c.FloatVerifier);
        txtSpeedMin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSpeedMinFocusLost(evt);
            }
        });

        lbDistanceFltr.setText(bundle.getString("BT747Main.lbDistanceFltr.text")); // NOI18N

        lbSpeedFltr.setText(bundle.getString("BT747Main.lbSpeedFltr.text")); // NOI18N

        txtRecCntMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecCntMax.setText(bundle.getString("BT747Main.txtRecCntMax.text")); // NOI18N
        txtRecCntMax.setInputVerifier(c.IntVerifier);
        txtRecCntMax.setMinimumSize(new java.awt.Dimension(50, 40));
        txtRecCntMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRecCntMaxFocusLost(evt);
            }
        });

        txtDistanceMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDistanceMax.setText(bundle.getString("BT747Main.txtDistanceMax.text")); // NOI18N
        txtDistanceMax.setInputVerifier(c.FloatVerifier);
        txtDistanceMax.setMinimumSize(new java.awt.Dimension(6, 40));
        txtDistanceMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDistanceMaxFocusLost(evt);
            }
        });

        txtSpeedMax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSpeedMax.setText(bundle.getString("BT747Main.txtSpeedMax.text")); // NOI18N
        txtSpeedMax.setInputVerifier(c.FloatVerifier);
        txtSpeedMax.setMinimumSize(new java.awt.Dimension(6, 40));
        txtSpeedMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSpeedMaxFocusLost(evt);
            }
        });

        lbNSATFltr.setText(bundle.getString("BT747Main.lbNSATFltr.text")); // NOI18N

        txtNSATMin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNSATMin.setText(bundle.getString("BT747Main.txtNSATMin.text")); // NOI18N
        txtNSATMin.setInputVerifier(c.IntVerifier);
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
        txtPDOPMax.setInputVerifier(c.FloatVerifier);
        txtPDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPDOPMaxFocusLost(evt);
            }
        });

        lbPDOPMax.setText(bundle.getString("BT747Main.lbPDOPMax.text")); // NOI18N

        txtHDOPMax.setText(bundle.getString("BT747Main.txtHDOPMax.text")); // NOI18N
        txtHDOPMax.setInputVerifier(c.FloatVerifier);
        txtHDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHDOPMaxFocusLost(evt);
            }
        });

        lbHDOPLimit.setText(bundle.getString("BT747Main.lbHDOPLimit.text")); // NOI18N

        txtVDOPMax.setText(bundle.getString("BT747Main.txtVDOPMax.text")); // NOI18N
        txtVDOPMax.setInputVerifier(c.FloatVerifier);
        txtVDOPMax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVDOPMaxFocusLost(evt);
            }
        });

        lbVDOPLimit.setText(bundle.getString("BT747Main.lbVDOPLimit.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFilterPrecisionLayout = new org.jdesktop.layout.GroupLayout(pnFilterPrecision);
        pnFilterPrecision.setLayout(pnFilterPrecisionLayout);
        pnFilterPrecisionLayout.setHorizontalGroup(
            pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFilterPrecisionLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFilterPrecisionLayout.createSequentialGroup()
                        .add(lbPDOPMax)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtPDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnFilterPrecisionLayout.createSequentialGroup()
                        .add(lbHDOPLimit)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtHDOPMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                    .add(pnFilterPrecisionLayout.createSequentialGroup()
                        .add(lbVDOPLimit)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtVDOPMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnFilterPrecisionLayout.setVerticalGroup(
            pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFilterPrecisionLayout.createSequentialGroup()
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbPDOPMax)
                    .add(txtPDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbHDOPLimit)
                    .add(txtHDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFilterPrecisionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVDOPMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbVDOPLimit))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbIgnore0Values.setText(bundle.getString("BT747Main.lbIgnore0Values.text")); // NOI18N

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
                        .add(lbIgnore0Values))
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
                .add(0, 0, 0)
                .add(pnFilterOther, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbIgnore0Values))
        );

        pnWaypoint.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnWaypoint.border.title"))); // NOI18N
        pnWaypoint.setToolTipText(bundle.getString("BT747Main.pnWaypoint.toolTipText")); // NOI18N

        pnWayPointFix.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnWayPointFix.border.title"))); // NOI18N

        cbWayNoFix.setText(bundle.getString("BT747Main.cbWayNoFix.text")); // NOI18N
        cbWayNoFix.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayNoFixWayTypeFixAction(evt);
            }
        });

        cbWayPPS.setText(bundle.getString("BT747Main.cbWayPPS.text")); // NOI18N
        cbWayPPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayPPSWayTypeFixAction(evt);
            }
        });

        cbWayEstimate.setText(bundle.getString("BT747Main.cbWayEstimate.text")); // NOI18N
        cbWayEstimate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayEstimateWayTypeFixAction(evt);
            }
        });

        cbWayManual.setText(bundle.getString("BT747Main.cbWayManual.text")); // NOI18N
        cbWayManual.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayManualWayTypeFixAction(evt);
            }
        });

        cbWaySPS.setText(bundle.getString("BT747Main.cbWaySPS.text")); // NOI18N
        cbWaySPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWaySPSWayTypeFixAction(evt);
            }
        });

        cbWayFRTK.setText(bundle.getString("BT747Main.cbWayFRTK.text")); // NOI18N
        cbWayFRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayFRTKWayTypeFixAction(evt);
            }
        });

        cbWayDGPS.setText(bundle.getString("BT747Main.cbWayDGPS.text")); // NOI18N
        cbWayDGPS.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayDGPSWayTypeFixAction(evt);
            }
        });

        cbWaySimulate.setText(bundle.getString("BT747Main.cbWaySimulate.text")); // NOI18N
        cbWaySimulate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWaySimulateWayTypeFixAction(evt);
            }
        });

        cbWayRTK.setText(bundle.getString("BT747Main.cbWayRTK.text")); // NOI18N
        cbWayRTK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayRTKWayTypeFixAction(evt);
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

        pnWayPointFixLayout.linkSize(new java.awt.Component[] {cbWayDGPS, cbWayEstimate, cbWayFRTK, cbWayManual, cbWayNoFix, cbWayPPS, cbWayRTK, cbWaySPS, cbWaySimulate}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

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
                .addContainerGap())
        );

        pnWayPointRCR.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnWayPointRCR.border.title"))); // NOI18N
        pnWayPointRCR.setMaximumSize(new java.awt.Dimension(100, 32767));

        cbWayTime.setText(bundle.getString("BT747Main.cbWayTime.text")); // NOI18N
        cbWayTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayTimeWayRCRAction(evt);
            }
        });

        cbWaySpeed.setText(bundle.getString("BT747Main.cbWaySpeed.text")); // NOI18N
        cbWaySpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWaySpeedWayRCRAction(evt);
            }
        });

        cbWayDistance.setText(bundle.getString("BT747Main.cbWayDistance.text")); // NOI18N
        cbWayDistance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayDistanceWayRCRAction(evt);
            }
        });

        cbWayButton.setText(bundle.getString("BT747Main.cbWayButton.text")); // NOI18N
        cbWayButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayButtonWayRCRAction(evt);
            }
        });

        cbWayUser1.setText(bundle.getString("BT747Main.cbWayUser1.text")); // NOI18N
        cbWayUser1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbWayUser1WayRCRAction(evt);
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
            .add(cbWayUser1)
        );

        pnWayPointRCRLayout.linkSize(new java.awt.Component[] {cbWayButton, cbWayDistance, cbWaySpeed, cbWayTime, cbWayUser1}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

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
                .add(cbWayUser1)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pnWaypointLayout = new org.jdesktop.layout.GroupLayout(pnWaypoint);
        pnWaypoint.setLayout(pnWaypointLayout);
        pnWaypointLayout.setHorizontalGroup(
            pnWaypointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWaypointLayout.createSequentialGroup()
                .add(pnWayPointFix, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(pnWayPointRCR, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnWaypointLayout.setVerticalGroup(
            pnWaypointLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnWayPointRCR, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .add(pnWayPointFix, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnTrackpoint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(pnCommonFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(pnWaypoint, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnCommonFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnTrackpoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnWaypoint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
    }//GEN-END:initComponents

    private final void TrkFixTypeAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_TrkFixTypeAction
        setTrkValidFilterSettings();
    }// GEN-LAST:event_TrkFixTypeAction

    private final void WayTypeFixAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_WayTypeFixAction
        setWayValidFilterSettings();
    }// GEN-LAST:event_WayTypeFixAction

    private final void WayRCRAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_WayRCRAction
        setWayRCRFilterSettings();
    }// GEN-LAST:event_WayRCRAction

    private final void TrkRCRAction(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_TrkRCRAction
        setTrkRCRFilterSettings();
    }// GEN-LAST:event_TrkRCRAction

private void cbTrkNoFixTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkNoFixTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkNoFixTrkFixTypeAction

private void cbTrkPPSTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkPPSTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkPPSTrkFixTypeAction

private void cbTrkEstimateTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkEstimateTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkEstimateTrkFixTypeAction

private void cbTrkManualTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkManualTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkManualTrkFixTypeAction

private void cbTrkSPSTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkSPSTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkSPSTrkFixTypeAction

private void cbTrkFRTKTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkFRTKTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkFRTKTrkFixTypeAction

private void cbTrkDGPSTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkDGPSTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkDGPSTrkFixTypeAction

private void cbTrkSimulateTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkSimulateTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkSimulateTrkFixTypeAction

private void cbTrkRTKTrkFixTypeAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkRTKTrkFixTypeAction
    TrkFixTypeAction(evt);
}//GEN-LAST:event_cbTrkRTKTrkFixTypeAction

private void cbTrkTimeTrkRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkTimeTrkRCRAction
    TrkRCRAction(evt);
}//GEN-LAST:event_cbTrkTimeTrkRCRAction

private void cbTrkSpeedTrkRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkSpeedTrkRCRAction
    TrkRCRAction(evt);
}//GEN-LAST:event_cbTrkSpeedTrkRCRAction

private void cbTrkDistanceTrkRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkDistanceTrkRCRAction
    TrkRCRAction(evt);
}//GEN-LAST:event_cbTrkDistanceTrkRCRAction

private void cbTrkButtonTrkRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkButtonTrkRCRAction
    TrkRCRAction(evt);
}//GEN-LAST:event_cbTrkButtonTrkRCRAction

private void cbTrkUser1TrkRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbTrkUser1TrkRCRAction
    TrkRCRAction(evt);
}//GEN-LAST:event_cbTrkUser1TrkRCRAction

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

    
private void cbAdvancedActiveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbAdvancedActiveStateChanged
        c.setAdvFilterActive(cbAdvancedActive.isSelected());
        updateAdvancedFilter();
}//GEN-LAST:event_cbAdvancedActiveStateChanged

private void updateAdvancedFilter() {
        boolean en = m.getBooleanOpt(AppSettings.ADVFILTACTIVE);
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

private void cbWayNoFixWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayNoFixWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWayNoFixWayTypeFixAction

private void cbWayPPSWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayPPSWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWayPPSWayTypeFixAction

private void cbWayEstimateWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayEstimateWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWayEstimateWayTypeFixAction

private void cbWayManualWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayManualWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWayManualWayTypeFixAction

private void cbWaySPSWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWaySPSWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWaySPSWayTypeFixAction

private void cbWayFRTKWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayFRTKWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWayFRTKWayTypeFixAction

private void cbWayDGPSWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayDGPSWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWayDGPSWayTypeFixAction

private void cbWaySimulateWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWaySimulateWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWaySimulateWayTypeFixAction

private void cbWayRTKWayTypeFixAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayRTKWayTypeFixAction
    WayTypeFixAction(evt);
}//GEN-LAST:event_cbWayRTKWayTypeFixAction

private void cbWayTimeWayRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayTimeWayRCRAction
   WayRCRAction(evt);
}//GEN-LAST:event_cbWayTimeWayRCRAction

private void cbWaySpeedWayRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWaySpeedWayRCRAction
   WayRCRAction(evt);
}//GEN-LAST:event_cbWaySpeedWayRCRAction

private void cbWayDistanceWayRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayDistanceWayRCRAction
   WayRCRAction(evt);
}//GEN-LAST:event_cbWayDistanceWayRCRAction

private void cbWayButtonWayRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayButtonWayRCRAction
   WayRCRAction(evt);
}//GEN-LAST:event_cbWayButtonWayRCRAction

private void cbWayUser1WayRCRAction(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbWayUser1WayRCRAction
   WayRCRAction(evt);
}//GEN-LAST:event_cbWayUser1WayRCRAction


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbAdvancedActive;
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
    private javax.swing.JLabel lbDistanceFltr;
    private javax.swing.JLabel lbHDOPLimit;
    private javax.swing.JLabel lbIgnore0Values;
    private javax.swing.JLabel lbNSATFltr;
    private javax.swing.JLabel lbPDOPMax;
    private javax.swing.JLabel lbRecNbrFltr;
    private javax.swing.JLabel lbSpeedFltr;
    private javax.swing.JLabel lbVDOPLimit;
    private javax.swing.JPanel pnCommonFilter;
    private javax.swing.JPanel pnFilterOther;
    private javax.swing.JPanel pnFilterPrecision;
    private javax.swing.JPanel pnTrackpoint;
    private javax.swing.JPanel pnTrkFixType;
    private javax.swing.JPanel pnTrkLogReason;
    private javax.swing.JPanel pnWayPointFix;
    private javax.swing.JPanel pnWayPointRCR;
    private javax.swing.JPanel pnWaypoint;
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
