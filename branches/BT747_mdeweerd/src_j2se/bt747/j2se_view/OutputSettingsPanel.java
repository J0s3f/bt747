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

import gps.BT747Constants;
import gps.convert.Conv;

import java.awt.Color;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;

import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;
import bt747.sys.Generic;

/**
 * 
 * @author Mario
 */
public class OutputSettingsPanel extends javax.swing.JPanel implements
        ModelListener {
    /**
     * 
     */
    private static final long serialVersionUID = -2219695831825370169L;
    private J2SEAppController c;
    private Model m;

    /** Creates new form OutputSettingsPanel */
    public OutputSettingsPanel() {
        initComponents();

        cbHeightOverMeanSeaLevel
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                        getString("Keep_Height"), getString("WGS84_to_MSL"),
                        getString("Automatic"), getString("MSL_to_WGS84") }));
        cbStandardOrDaylightSaving
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                        getString("Standard_Time"),
                        getString("Daylight_Savings_Time") }));
        cbOneFilePerDay.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { getString("One_file_per_day"),
                        getString("One_file_per_track"),
                        getString("Everything_in_one_file") }));
        String[] distanceStrings = new String[3];
        distanceStrings[Model.DISTANCE_CALC_MODE_NONE] = getString("DIST_CALC_MODE_NONE");
        distanceStrings[Model.DISTANCE_CALC_MODE_WHEN_MISSING] = getString("DIST_CALC_MODE_MISSING");
        distanceStrings[Model.DISTANCE_CALC_MODE_ALWAYS] = getString("DIST_CALC_MODE_ALWAYS");
        cbDistanceCalculation.setModel(new javax.swing.DefaultComboBoxModel(distanceStrings));
    }

    private static final String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    private boolean settingLocale = false;

    private synchronized void setLocale(final String locale) {
        if (!settingLocale) {
            settingLocale = true;

            try {
                final Locale l = J2SEAppController.localeFromString(locale);
                c.setStringOpt(Model.LANGUAGE, l.toString());
                cbLanguageChooser.setLocale(l);
                cbLanguage.setSelectedItem(l.toString());
            } catch (final Exception e) {
                // TODO: handle exception
            }
            settingLocale = false;
        }
    }

    public void init(final J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);

        updateScale();

        cbLanguage.setModel(new DefaultComboBoxModel(Calendar
                .getAvailableLocales()));
        setLocale(m.getStringOpt(Model.LANGUAGE));

        tfTrackSeparationTime.setText(Integer.toString(m
                .getIntOpt(Model.TRKSEP)));
        tfTrackSeparationDistance.setText(Integer.toString(m
                .getIntOpt(Model.SPLIT_DISTANCE)));
        int index = 0;
        switch (m.getIntOpt(Model.OUTPUTFILESPLITTYPE)) {
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
        
        
        cbUTCOffset.setModel(new javax.swing.DefaultComboBoxModel(
                BT747Constants.getUtcStrings("UTC ")));

        try {
            int utcOffsetIdx;
            utcOffsetIdx = BT747Constants.getUtcIdx(m.getIntOpt(Model.GPSTIMEOFFSETQUARTERS));
            cbUTCOffset.setSelectedIndex(utcOffsetIdx);
            updateUTCOffsetFromGUI();
        } catch (final Exception e) {
            Generic.debug(getString("Problem_with_UTC_offset"), e);
        }

        cbImperialUnits.setSelected(m.getBooleanOpt(Model.IMPERIAL));
        cbRecordNumberInfoInLog.setSelected(m
                .getBooleanOpt(Model.IS_RECORDNBR_IN_LOGS));

        adjustHeightCombo();
        cbAddTrackPointComment.setSelected(m
                .getBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT));
        cbAddTrackPointName.setSelected(m
                .getBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME));

        cbNewTrackWhenLogOn.setSelected(m.getBooleanOpt(Model.IS_NEW_TRACK_WHEN_LOG_ON));
        cbCreatMissingFields.setSelected(m.getBooleanOpt(Model.CREATE_MISSING_FIELDS));
        updateFileFormatData();
        updateColorButtons();
        updateDistanceCombo();
        updateLatLonDigitsCombo();
        updateHeightDigitsCombo();
    }

    private void adjustHeightCombo() {
        switch (m.getIntOpt(Model.HEIGHT_CONVERSION_MODE)) {
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
    }
    
    
    private void updateLatLonDigitsCombo() {
    	try {
    		cbLatLonDigits.setSelectedIndex(m.getIntOpt(Model.POSITIONDIGITS));
    	} catch (Exception e) {
    		// TODO: Notify exception
    	}
    }

    private void updateHeightDigitsCombo() {
    	try {
    		cbHeightDigits.setSelectedIndex(m.getIntOpt(Model.HEIGHTDIGITS));
    	} catch (Exception e) {
    		// TODO: Notify exception
    	}
        
    }
    
    /**
     * 
     */
    private void updateDistanceCombo() {
        cbDistanceCalculation.setSelectedIndex(m.getIntOpt(Model.DISTANCE_CALCULATION_MODE));
    }
    
    public void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.SETTING_CHANGE:
            switch (Integer.parseInt((String)e.getArg())) {
            case Model.FONTSCALE:
                updateScale();
                break;
            case Model.POSITIONDIGITS:
            	updateLatLonDigitsCombo();
            	break;
            case Model.HEIGHTDIGITS:
            	updateHeightDigitsCombo();
            }
        }
    }

    /**
     * @return the scale
     */
    private final void updateScale() {
        int scale = m.getIntOpt(Model.FONTSCALE)&0xFF;
        spScale.setValue(scale);

    }
    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        pnGUISettings = new javax.swing.JPanel();
        lbFontScale = new javax.swing.JLabel();
        spScale = new javax.swing.JSpinner();
        pnLanguage = new javax.swing.JPanel();
        cbLanguageChooser = new com.toedter.components.JLocaleChooser();
        cbLanguage = new javax.swing.JComboBox();
        lbCode = new javax.swing.JLabel();
        pnSeparation = new javax.swing.JPanel();
        lbNewTrackAfter = new javax.swing.JLabel();
        tfTrackSeparationTime = new javax.swing.JTextField();
        lbMinPause = new javax.swing.JLabel();
        cbNewTrackWhenLogOn = new javax.swing.JCheckBox();
        lbNewTrackAfterDistance = new javax.swing.JLabel();
        tfTrackSeparationDistance = new javax.swing.JTextField();
        lbMetersAfter = new javax.swing.JLabel();
        pnVarious = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cbUTCOffset = new javax.swing.JComboBox();
        cbHeightOverMeanSeaLevel = new javax.swing.JComboBox();
        cbRecordNumberInfoInLog = new javax.swing.JCheckBox();
        cbImperialUnits = new javax.swing.JCheckBox();
        txtTimeZone = new javax.swing.JLabel();
        txtHeight = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cbStandardOrDaylightSaving = new javax.swing.JComboBox();
        cbOneFilePerDay = new javax.swing.JComboBox();
        cbGoodFixColor = new javax.swing.JButton();
        cbNoFixColor = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cbDistanceCalculation = new javax.swing.JComboBox();
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
        cbAddWayPointComment = new javax.swing.JCheckBox();
        pnMissingPanel = new javax.swing.JPanel();
        cbCreatMissingFields = new javax.swing.JCheckBox();
        pnDigits = new javax.swing.JPanel();
        cbLatLonDigits = new javax.swing.JComboBox();
        lbLatLonDigits = new javax.swing.JLabel();
        cbHeightDigits = new javax.swing.JComboBox();
        lbHeightDigits = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        pnGUISettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("OutputSettingsPanel.pnGUISettings.border.title"))); // NOI18N

        lbFontScale.setText(bundle.getString("OutputSettingsPanel.lbFontScale.text")); // NOI18N

        spScale.setModel(new javax.swing.SpinnerNumberModel(100, 50, 200, 1));
        spScale.setInputVerifier(J2SEAppController.IntVerifier);
        spScale.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spScaleStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnGUISettingsLayout = new org.jdesktop.layout.GroupLayout(pnGUISettings);
        pnGUISettings.setLayout(pnGUISettingsLayout);
        pnGUISettingsLayout.setHorizontalGroup(
            pnGUISettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lbFontScale)
            .add(spScale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        pnGUISettingsLayout.setVerticalGroup(
            pnGUISettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGUISettingsLayout.createSequentialGroup()
                .add(lbFontScale)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spScale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnLanguage.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnLanguage.border.title_1"))); // NOI18N

        cbLanguageChooser.setToolTipText(bundle.getString("OutputSettingsPanel.cbLanguageChooser.toolTipText")); // NOI18N
        cbLanguageChooser.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbLanguageChooserItemStateChanged(evt);
            }
        });
        cbLanguageChooser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbLanguageChooserFocusLost(evt);
            }
        });

        cbLanguage.setEditable(true);
        cbLanguage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "en", "nl", "fr", "ru" }));
        cbLanguage.setToolTipText(bundle.getString("OutputSettingsPanel.cbLanguage.toolTipText")); // NOI18N
        cbLanguage.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbLanguageItemStateChanged(evt);
            }
        });

        lbCode.setText(bundle.getString("OutputSettingsPanel.lbCode.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnLanguageLayout = new org.jdesktop.layout.GroupLayout(pnLanguage);
        pnLanguage.setLayout(pnLanguageLayout);
        pnLanguageLayout.setHorizontalGroup(
            pnLanguageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLanguageLayout.createSequentialGroup()
                .add(cbLanguageChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbCode)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pnLanguageLayout.setVerticalGroup(
            pnLanguageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnLanguageLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(cbLanguageChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lbCode)
                .add(cbLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnSeparation.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnSeparation.border.title"))); // NOI18N
        pnSeparation.setMaximumSize(new java.awt.Dimension(300, 32767));
        pnSeparation.setOpaque(false);

        lbNewTrackAfter.setText(bundle.getString("BT747Main.lbNewTrackAfter.text")); // NOI18N

        tfTrackSeparationTime.setText(bundle.getString("BT747Main.tfTrackSeparationTime.text")); // NOI18N
        tfTrackSeparationTime.setToolTipText(bundle.getString("BT747Main.tfTrackSeparationTime.toolTipText")); // NOI18N
        tfTrackSeparationTime.setInputVerifier(c.IntVerifier);
        tfTrackSeparationTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTrackSeparationTimeFocusLost(evt);
            }
        });

        lbMinPause.setText(bundle.getString("BT747Main.lbMinPause.text")); // NOI18N

        cbNewTrackWhenLogOn.setText(bundle.getString("OutputSettingsPanel.cbNewTrackWhenLogOn.text")); // NOI18N
        cbNewTrackWhenLogOn.setToolTipText(bundle.getString("OutputSettingsPanel.cbNewTrackWhenLogOn.toolTipText")); // NOI18N
        cbNewTrackWhenLogOn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbNewTrackWhenLogOnItemStateChanged(evt);
            }
        });

        lbNewTrackAfterDistance.setText(bundle.getString("OutputSettingsPanel.lbNewTrackAfterDistance.text")); // NOI18N
        lbNewTrackAfterDistance.setToolTipText(bundle.getString("OutputSettingsPanel.tfTrackSeparationDistance.toolTipText")); // NOI18N

        tfTrackSeparationDistance.setText(bundle.getString("OutputSettingsPanel.tfTrackSeparationDistance.text")); // NOI18N
        tfTrackSeparationDistance.setToolTipText(bundle.getString("OutputSettingsPanel.tfTrackSeparationDistance.toolTipText")); // NOI18N
        tfTrackSeparationDistance.setInputVerifier(c.IntVerifier);
        tfTrackSeparationDistance.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTrackSeparationDistanceFocusLost(evt);
            }
        });

        lbMetersAfter.setText(bundle.getString("OutputSettingsPanel.lbMetersAfter.text")); // NOI18N
        lbMetersAfter.setToolTipText(bundle.getString("OutputSettingsPanel.tfTrackSeparationDistance.toolTipText")); // NOI18N

        org.jdesktop.layout.GroupLayout pnSeparationLayout = new org.jdesktop.layout.GroupLayout(pnSeparation);
        pnSeparation.setLayout(pnSeparationLayout);
        pnSeparationLayout.setHorizontalGroup(
            pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSeparationLayout.createSequentialGroup()
                .add(lbNewTrackAfter)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfTrackSeparationTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbMinPause))
            .add(pnSeparationLayout.createSequentialGroup()
                .add(lbNewTrackAfterDistance)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfTrackSeparationDistance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbMetersAfter))
            .add(cbNewTrackWhenLogOn)
        );
        pnSeparationLayout.setVerticalGroup(
            pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnSeparationLayout.createSequentialGroup()
                .add(pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbNewTrackAfter)
                    .add(tfTrackSeparationTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbMinPause))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnSeparationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbNewTrackAfterDistance)
                    .add(tfTrackSeparationDistance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbMetersAfter))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbNewTrackWhenLogOn))
        );

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

        txtHeight.setText(bundle.getString("OutputSettingsPanel.txtHeight.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(txtHeight)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbHeightOverMeanSeaLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(txtTimeZone)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbUTCOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(cbImperialUnits)
                        .add(cbRecordNumberInfoInLog))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbUTCOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtTimeZone))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbHeightOverMeanSeaLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtHeight))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbRecordNumberInfoInLog)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE)
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
        cbGoodFixColor.setBorderPainted(false);
        cbGoodFixColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGoodFixColorActionPerformed(evt);
            }
        });

        cbNoFixColor.setText(bundle.getString("BT747Main.cbNoFixColor.text")); // NOI18N
        cbNoFixColor.setToolTipText(bundle.getString("BT747Main.cbNoFixColor.toolTipText")); // NOI18N
        cbNoFixColor.setBorderPainted(false);
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

        cbDistanceCalculation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDistanceCalculationItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(cbDistanceCalculation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(246, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbDistanceCalculation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout pnVariousLayout = new org.jdesktop.layout.GroupLayout(pnVarious);
        pnVarious.setLayout(pnVariousLayout);
        pnVariousLayout.setHorizontalGroup(
            pnVariousLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnVariousLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        pnVariousLayout.setVerticalGroup(
            pnVariousLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnVariousLayout.createSequentialGroup()
                .add(pnVariousLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnVarious, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .add(pnGUISettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnSeparation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(pnVarious, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnSeparation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnGUISettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, 0)
                .add(pnLanguage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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

        cbAddWayPointComment.setText(bundle.getString("OutputSettingsPanel.cbAddWayPointComment.text")); // NOI18N
        cbAddWayPointComment.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAddWayPointCommentItemStateChanged(evt);
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
            .add(cbAddWayPointComment)
        );
        pnTrackPointsLayout.setVerticalGroup(
            pnTrackPointsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTrackPointsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(pnTrackPointsLayout.createSequentialGroup()
                    .add(cbAddTrackPointComment)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cbAddWayPointComment))
                .add(cbAddTrackPointName))
        );

        cbCreatMissingFields.setText(bundle.getString("OutputSettingsPanel.cbCreatMissingFields.text")); // NOI18N
        cbCreatMissingFields.setToolTipText(bundle.getString("OutputSettingsPanel.cbCreatMissingFields.toolTipText")); // NOI18N
        cbCreatMissingFields.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbCreatMissingFieldsItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnMissingPanelLayout = new org.jdesktop.layout.GroupLayout(pnMissingPanel);
        pnMissingPanel.setLayout(pnMissingPanelLayout);
        pnMissingPanelLayout.setHorizontalGroup(
            pnMissingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 125, Short.MAX_VALUE)
            .add(pnMissingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(pnMissingPanelLayout.createSequentialGroup()
                    .add(0, 0, Short.MAX_VALUE)
                    .add(cbCreatMissingFields)
                    .add(0, 0, Short.MAX_VALUE)))
        );
        pnMissingPanelLayout.setVerticalGroup(
            pnMissingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 29, Short.MAX_VALUE)
            .add(pnMissingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(pnMissingPanelLayout.createSequentialGroup()
                    .add(0, 3, Short.MAX_VALUE)
                    .add(cbCreatMissingFields)
                    .add(0, 3, Short.MAX_VALUE)))
        );

        pnDigits.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("OutputSettingsPanel.pnDigits.border.title"))); // NOI18N

        cbLatLonDigits.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        cbLatLonDigits.setToolTipText(bundle.getString("OutputSettingsPanel.cbLatLonDigits.toolTipText")); // NOI18N
        cbLatLonDigits.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbLatLonDigitsItemStateChanged(evt);
            }
        });

        lbLatLonDigits.setLabelFor(cbLatLonDigits);
        lbLatLonDigits.setText(bundle.getString("OutputSettingsPanel.lbLatLonDigits.text")); // NOI18N

        cbHeightDigits.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6" }));
        cbHeightDigits.setToolTipText(bundle.getString("OutputSettingsPanel.cbHeightDigits.toolTipText")); // NOI18N
        cbHeightDigits.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbHeightDigitsItemStateChanged(evt);
            }
        });

        lbHeightDigits.setLabelFor(cbHeightDigits);
        lbHeightDigits.setText(bundle.getString("OutputSettingsPanel.lbHeightDigits.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnDigitsLayout = new org.jdesktop.layout.GroupLayout(pnDigits);
        pnDigits.setLayout(pnDigitsLayout);
        pnDigitsLayout.setHorizontalGroup(
            pnDigitsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnDigitsLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnDigitsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lbLatLonDigits)
                    .add(lbHeightDigits))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnDigitsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbHeightDigits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbLatLonDigits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        pnDigitsLayout.setVerticalGroup(
            pnDigitsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnDigitsLayout.createSequentialGroup()
                .add(pnDigitsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbLatLonDigits)
                    .add(cbLatLonDigits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnDigitsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbHeightDigits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbHeightDigits)))
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
                    .add(pnMissingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                        .add(pnFileReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnDigits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                        .add(pnFileOutputFieldInnerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                                .add(1, 1, 1)
                                .add(pnFileReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnFileOutputFieldInnerLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pnDigits, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnMissingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFileOutputFields, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnFileOutputFields, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbLanguageChooserItemStateChanged(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbLanguageChooserItemStateChanged
        cbLanguageChooser.itemStateChanged(evt);
        setLocale(cbLanguageChooser.getLocale().toString());
    }//GEN-LAST:event_cbLanguageChooserItemStateChanged

    private void cbLanguageChooserFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbLanguageChooserFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_cbLanguageChooserFocusLost

    private void cbLanguageItemStateChanged(final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbLanguageItemStateChanged
        setLocale(cbLanguage.getSelectedItem().toString());
    }//GEN-LAST:event_cbLanguageItemStateChanged

    private void cbOneFilePerDayFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbOneFilePerDayFocusLost
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
        c.setIntOpt(Model.OUTPUTFILESPLITTYPE, type);
    }//GEN-LAST:event_cbOneFilePerDayFocusLost

    private void cbRecordNumberInfoInLogFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbRecordNumberInfoInLogFocusLost
        c.setBooleanOpt(Model.IS_RECORDNBR_IN_LOGS, cbRecordNumberInfoInLog
                .isSelected());
    }//GEN-LAST:event_cbRecordNumberInfoInLogFocusLost

    private void cbImperialUnitsFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbImperialUnitsFocusLost
        c.setBooleanOpt(Model.IMPERIAL, cbImperialUnits.isSelected());
    }//GEN-LAST:event_cbImperialUnitsFocusLost

    private void tfTrackSeparationTimeFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTrackSeparationTimeFocusLost
        c.setIntOpt(Model.TRKSEP, Integer
                .parseInt(tfTrackSeparationTime.getText()));
    }//GEN-LAST:event_tfTrackSeparationTimeFocusLost

    private void cbNoFixColorActionPerformed(
            final java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        Color myColor = new Color(Conv.hex2Int(m
                .getStringOpt(Model.COLOR_INVALIDTRACK)));
        myColor = JColorChooser
                .showDialog(
                        this,
                        getString("Choose_the_color_for_a_'bad_track'_(pure_blue_to_ignore)"),
                        myColor);
        if (myColor != null) {
            c.setStringOpt(Model.COLOR_INVALIDTRACK, JavaLibBridge
                    .unsigned2hex(myColor.getRGB() & 0xFFFFFF, 6));
        }
        updateColorButtons();
    }

    private void cbGoodFixColorActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFixColorActionPerformed
        // TODO add your handling code here:
        Color myColor = new Color(Conv.hex2Int(m
                .getStringOpt(Model.COLOR_VALIDTRACK)));
        myColor = JColorChooser
                .showDialog(
                        this,
                        getString("Choose_the_color_for_a_'good_track'_(pure_blue_to_ignore)"),
                        myColor);
        if (myColor != null) {
            c.setStringOpt(Model.COLOR_VALIDTRACK, JavaLibBridge
                    .unsigned2hex(myColor.getRGB() & 0xFFFFFF, 6));
        }
        updateColorButtons();
    }//GEN-LAST:event_cbFixColorActionPerformed

    private void updateColorButtons() {
        Color myColor = new Color(Conv.hex2Int(m
                .getStringOpt(Model.COLOR_VALIDTRACK)));
        cbGoodFixColor.setBackground(myColor);
        cbGoodFixColor.setForeground(new Color(255 - myColor.getRed(),
                255 - myColor.getGreen(), 255 - myColor.getBlue()));
        cbGoodFixColor.setOpaque(true);
        myColor = new Color(Conv.hex2Int(m
                .getStringOpt(Model.COLOR_INVALIDTRACK)));
        cbNoFixColor.setBackground(myColor);
        cbNoFixColor.setForeground(new Color(255 - myColor.getRed(),
                255 - myColor.getGreen(), 255 - myColor.getBlue()));
        cbNoFixColor.setOpaque(true);
    }

    private void updateUTCOffsetFromGUI() {
        int idx = cbUTCOffset.getSelectedIndex();
        c.setIntOpt(Model.GPSTIMEOFFSETQUARTERS, BT747Constants.timeZones[idx]);
    }
    
    private void cbUTCOffsetFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbUTCOffsetFocusLost
        updateUTCOffsetFromGUI();
    }//GEN-LAST:event_cbUTCOffsetFocusLost

    private void cbStandardOrDaylightSavingFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbStandardOrDaylightSavingFocusLost
        // TODO: implement
    }//GEN-LAST:event_cbStandardOrDaylightSavingFocusLost

    private void cbHeightOverMeanSeaLevelFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbHeightOverMeanSeaLevelFocusLost
        switch (cbHeightOverMeanSeaLevel.getSelectedIndex()) {
        case 0:
            c.setIntOpt(Model.HEIGHT_CONVERSION_MODE,Model.HEIGHT_NOCHANGE);
            break;
        case 1:
            c.setIntOpt(Model.HEIGHT_CONVERSION_MODE,Model.HEIGHT_WGS84_TO_MSL);
            break;
        case 2:
            c.setIntOpt(Model.HEIGHT_CONVERSION_MODE,Model.HEIGHT_AUTOMATIC);
            break;
        case 3:
            c.setIntOpt(Model.HEIGHT_CONVERSION_MODE,Model.HEIGHT_MSL_TO_WGS84);
            break;
        }
    }//GEN-LAST:event_cbHeightOverMeanSeaLevelFocusLost

    private void updateFileFormatData() {
        final int logFormat = m.getIntOpt(Model.FILEFIELDFORMAT);

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

    private void cbFileUTCTimeupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileUTCTimeupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileUTCTimeupdateLogRecordEstCount

    private void cbFileMilliSecondsupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileMilliSecondsupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileMilliSecondsupdateLogRecordEstCount

    private void cbFileLatupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileLatupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileLatupdateLogRecordEstCount

    private void cbFileLongupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileLongupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileLongupdateLogRecordEstCount

    private void cbFileHeightupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileHeightupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileHeightupdateLogRecordEstCount

    private void cbFileSpeedupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileSpeedupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileSpeedupdateLogRecordEstCount

    private void cbFileHeadingupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileHeadingupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileHeadingupdateLogRecordEstCount

    private void cbFileDistanceupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileDistanceupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileDistanceupdateLogRecordEstCount

    private void cbFileDSTAupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileDSTAupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileDSTAupdateLogRecordEstCount

    private void cbFileDAGEupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFileDAGEupdateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFileDAGEupdateLogRecordEstCount

    private void cbFileHDOPupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbPDOP1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbPDOP1updateLogRecordEstCount

    private void cbFilePDOPupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbHDOP1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbHDOP1updateLogRecordEstCount

    private void cbFileVDOPupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbVDOP1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbVDOP1updateLogRecordEstCount

    private void cbFileFixTypeupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFixType1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbFixType1updateLogRecordEstCount

    private void cbFileNSATupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbNSAT1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbNSAT1updateLogRecordEstCount

    private void cbFileSIDItemStateChanged(final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbSID1ItemStateChanged
        setFileOutputFields();
    }//GEN-LAST:event_cbSID1ItemStateChanged

    private void cbFileElevationupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbElevation1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbElevation1updateLogRecordEstCount

    private void cbFileAzimuthupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAzimuth1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbAzimuth1updateLogRecordEstCount

    private void cbFileSNRupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbSNR1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbSNR1updateLogRecordEstCount

    private void cbFileRCRupdateLogRecordEstCount(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbRCR1updateLogRecordEstCount
        setFileOutputFields();
    }//GEN-LAST:event_cbRCR1updateLogRecordEstCount

    private void cbAddTrackPointCommentItemStateChanged(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAddTrackPointCommentItemStateChanged
        c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT,
                cbAddTrackPointComment.isSelected());
    }//GEN-LAST:event_cbAddTrackPointCommentItemStateChanged

    private void cbAddTrackPointNameItemStateChanged(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAddTrackPointNameItemStateChanged
        c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME, cbAddTrackPointName
                .isSelected());
    }//GEN-LAST:event_cbAddTrackPointNameItemStateChanged

    private void cbNewTrackWhenLogOnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbNewTrackWhenLogOnItemStateChanged
        c.setBooleanOpt(Model.IS_NEW_TRACK_WHEN_LOG_ON,
                evt.getStateChange() == java.awt.event.ItemEvent.SELECTED);
    }//GEN-LAST:event_cbNewTrackWhenLogOnItemStateChanged

    private void tfTrackSeparationDistanceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfTrackSeparationDistanceFocusLost
                c.setIntOpt(Model.SPLIT_DISTANCE, Integer
                .parseInt(tfTrackSeparationDistance.getText()));
}//GEN-LAST:event_tfTrackSeparationDistanceFocusLost

    private void spScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spScaleStateChanged
        c.setIntOpt(Model.FONTSCALE, (Integer) spScale.getValue());
    }//GEN-LAST:event_spScaleStateChanged

    private void cbDistanceCalculationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDistanceCalculationItemStateChanged
        int orgValue = m.getIntOpt(Model.DISTANCE_CALCULATION_MODE);
        int newValue = cbDistanceCalculation.getSelectedIndex();
        if (orgValue != newValue) {
            c.setIntOpt(Model.DISTANCE_CALCULATION_MODE, newValue);
        }
    }//GEN-LAST:event_cbDistanceCalculationItemStateChanged

    private void cbCreatMissingFieldsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbCreatMissingFieldsItemStateChanged
        c.setBooleanOpt(Model.CREATE_MISSING_FIELDS, evt.getStateChange() == java.awt.event.ItemEvent.SELECTED);
    }//GEN-LAST:event_cbCreatMissingFieldsItemStateChanged

    private void cbLatLonDigitsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbLatLonDigitsItemStateChanged
        int orgValue = m.getIntOpt(Model.POSITIONDIGITS);
        int newValue = cbLatLonDigits.getSelectedIndex();
        if (orgValue != newValue) {
            c.setIntOpt(Model.POSITIONDIGITS, newValue);
        }
    }//GEN-LAST:event_cbLatLonDigitsItemStateChanged

    private void cbHeightDigitsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbHeightDigitsItemStateChanged
        int orgValue = m.getIntOpt(Model.HEIGHTDIGITS);
        int newValue = cbHeightDigits.getSelectedIndex();
        if (orgValue != newValue) {
            c.setIntOpt(Model.HEIGHTDIGITS, newValue);
        }
    }//GEN-LAST:event_cbHeightDigitsItemStateChanged

    private void cbAddWayPointCommentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAddWayPointCommentItemStateChanged
        c.setBooleanOpt(Model.IS_WRITE_WAYPOINT_COMMENT, cbAddWayPointComment
                .isSelected());
    }//GEN-LAST:event_cbAddWayPointCommentItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbAddTrackPointComment;
    private javax.swing.JCheckBox cbAddTrackPointName;
    private javax.swing.JCheckBox cbAddWayPointComment;
    private javax.swing.JCheckBox cbCreatMissingFields;
    private javax.swing.JComboBox cbDistanceCalculation;
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
    private javax.swing.JButton cbGoodFixColor;
    private javax.swing.JComboBox cbHeightDigits;
    private javax.swing.JComboBox cbHeightOverMeanSeaLevel;
    private javax.swing.JCheckBox cbImperialUnits;
    private javax.swing.JComboBox cbLanguage;
    private com.toedter.components.JLocaleChooser cbLanguageChooser;
    private javax.swing.JComboBox cbLatLonDigits;
    private javax.swing.JCheckBox cbNewTrackWhenLogOn;
    private javax.swing.JButton cbNoFixColor;
    private javax.swing.JComboBox cbOneFilePerDay;
    private javax.swing.JCheckBox cbRecordNumberInfoInLog;
    private javax.swing.JComboBox cbStandardOrDaylightSaving;
    private javax.swing.JComboBox cbUTCOffset;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lbCode;
    private javax.swing.JLabel lbFontScale;
    private javax.swing.JLabel lbHeightDigits;
    private javax.swing.JLabel lbLatLonDigits;
    private javax.swing.JLabel lbMetersAfter;
    private javax.swing.JLabel lbMinPause;
    private javax.swing.JLabel lbNewTrackAfter;
    private javax.swing.JLabel lbNewTrackAfterDistance;
    private javax.swing.JPanel pnDigits;
    private javax.swing.JPanel pnFileOutputFieldInner;
    private javax.swing.JPanel pnFileOutputFields;
    private javax.swing.JPanel pnFilePosition;
    private javax.swing.JPanel pnFilePrecision;
    private javax.swing.JPanel pnFileReason;
    private javax.swing.JPanel pnFileSatInfo;
    private javax.swing.JPanel pnFileTime;
    private javax.swing.JPanel pnGUISettings;
    private javax.swing.JPanel pnLanguage;
    private javax.swing.JPanel pnMissingPanel;
    private javax.swing.JPanel pnSeparation;
    private javax.swing.JPanel pnTrackPoints;
    private javax.swing.JPanel pnVarious;
    private javax.swing.JSpinner spScale;
    private javax.swing.JTextField tfTrackSeparationDistance;
    private javax.swing.JTextField tfTrackSeparationTime;
    private javax.swing.JLabel txtHeight;
    private javax.swing.JLabel txtTimeZone;
    // End of variables declaration//GEN-END:variables

}
