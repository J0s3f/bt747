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

import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * 
 * @author Mario
 */
public class AdvancedFileSettingsPanel extends javax.swing.JPanel implements
        ModelListener {

    /**
     * 
     */
    private static final long serialVersionUID = -5583562050145951708L;
    private J2SEAppController c;
    private Model m;

    private final static String getString(String s) {
        return J2SEAppController.getString(s);
    }
    /** Creates new form AdvancedFileSettingsPanel */
    public AdvancedFileSettingsPanel() {
        initComponents();
        cbAltitudeMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                getString("KML_CLAMPGROUND"), getString("KML_RELATIVE"),
                getString("KML_ABSOLUTE") }));

    }

    public void init(final J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);

        cbNotApplyUTCOffset.setSelected(m.getBooleanOpt(AppSettings.GPXUTC0));
        cbGPXTrkSegWhenSmall.setSelected(m
                .getBooleanOpt(AppSettings.GPXTRKSEGBIG));
        cbNotApplyUTCOffsetForNMEA.setSelected(m.getBooleanOpt(AppSettings.NMEAUTC0));
        try {
            cbAltitudeMode.setSelectedIndex(m
                    .getIntOpt(AppSettings.KML_ALTITUDEMODE));
        } catch (final Exception e) {
            // TODO: handle exception
        }
        cbGPXAddLink.setSelected(m.getBooleanOpt(AppSettings.GPX_LINK_INFO));
        cbGPX_1_1.setSelected(m.getBooleanOpt(AppSettings.IS_GPX_1_1));
        getNMEAOutFile();
    }

    public void modelEvent(final ModelEvent e) {
        // TODO Auto-generated method stub
        switch (e.getType()) {

        }
    }

    void getNMEAOutFile() {
        // c.setNMEAset(maskNMEAset);
        // Should be checkboxes...
        final int outFormat = m.getNMEAset();

        lbNMEAFileGLL
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GLL_IDX)) != 0);

        lbNMEAFileRMC
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_RMC_IDX)) != 0);

        lbNMEAFileVTG
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_VTG_IDX)) != 0);

        lbNMEAFileGGA
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GGA_IDX)) != 0);

        lbNMEAFileGSA
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GSA_IDX)) != 0);

        lbNMEAFileGSV
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GSV_IDX)) != 0);

        lbNMEAFileGRS
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GRS_IDX)) != 0);

        lbNMEAFileGST
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_GST_IDX)) != 0);

        lbNMEAFileType8
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE8_IDX)) != 0);

        lbNMEAFileType9
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE9_IDX)) != 0);

        lbNMEAFileType10
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE10_IDX)) != 0);

        lbNMEAFileType11
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE11_IDX)) != 0);

        lbNMEAFileType12
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_TYPE12_IDX)) != 0);

        lbNMEAFileMALM
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MALM_IDX)) != 0);

        lbNMEAFileMEPH
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MEPH_IDX)) != 0);

        lbNMEAFileMDGP
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MDGP_IDX)) != 0);

        lbNMEAFileMDBG
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MDBG_IDX)) != 0);

        lbNMEAFileZDA
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_ZDA_IDX)) != 0);

        lbNMEAFileMCHN
                .setSelected((outFormat & (1 << BT747Constants.NMEA_SEN_MCHN_IDX)) != 0);
    }

    void setNMEAOutFile() {
        // c.setNMEAset(maskNMEAset);
        // Should be checkboxes...
        int outFormat = 0;

        if (lbNMEAFileGLL.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GLL_IDX);
        }
        if (lbNMEAFileRMC.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_RMC_IDX);
        }
        if (lbNMEAFileVTG.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_VTG_IDX);
        }
        if (lbNMEAFileGGA.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GGA_IDX);
        }
        if (lbNMEAFileGSA.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GSA_IDX);
        }
        if (lbNMEAFileGSV.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GSV_IDX);
        }
        if (lbNMEAFileGRS.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GRS_IDX);
        }
        if (lbNMEAFileGST.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_GST_IDX);
        }
        if (lbNMEAFileType8.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE8_IDX);
        }
        if (lbNMEAFileType9.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE9_IDX);
        }
        if (lbNMEAFileType10.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE10_IDX);
        }
        if (lbNMEAFileType11.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE11_IDX);
        }
        if (lbNMEAFileType12.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_TYPE12_IDX);
        }
        if (lbNMEAFileMALM.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MALM_IDX);
        }
        if (lbNMEAFileMEPH.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MEPH_IDX);
        }
        if (lbNMEAFileMDGP.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MDGP_IDX);
        }
        if (lbNMEAFileMDBG.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MDBG_IDX);
        }
        if (lbNMEAFileZDA.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_ZDA_IDX);
        }
        if (lbNMEAFileMCHN.isSelected()) {
            outFormat |= (1 << BT747Constants.NMEA_SEN_MCHN_IDX);
        }

        c.setNMEAset(outFormat);
    }

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        pnFileNMEAOutput = new javax.swing.JPanel();
        pnFileNMEAOutLeft = new javax.swing.JPanel();
        lbNMEAFileType9 = new javax.swing.JCheckBox();
        lbNMEAFileGST = new javax.swing.JCheckBox();
        lbNMEAFileVTG = new javax.swing.JCheckBox();
        lbNMEAFileGRS = new javax.swing.JCheckBox();
        lbNMEAFileGSV = new javax.swing.JCheckBox();
        lbNMEAFileGGA = new javax.swing.JCheckBox();
        lbNMEAFileRMC = new javax.swing.JCheckBox();
        lbNMEAFileGLL = new javax.swing.JCheckBox();
        lbNMEAFileType8 = new javax.swing.JCheckBox();
        lbNMEAFileGSA = new javax.swing.JCheckBox();
        pnFileNMEAOutRight = new javax.swing.JPanel();
        lbNMEAFileMDGP = new javax.swing.JCheckBox();
        lbNMEAFileType11 = new javax.swing.JCheckBox();
        lbNMEAFileMEPH = new javax.swing.JCheckBox();
        lbNMEAFileMALM = new javax.swing.JCheckBox();
        lbNMEAFileType10 = new javax.swing.JCheckBox();
        lbNMEAFileZDA = new javax.swing.JCheckBox();
        lbNMEAFileType12 = new javax.swing.JCheckBox();
        btSetNMEAFileOutput = new javax.swing.JButton();
        lbNMEAFileMDBG = new javax.swing.JCheckBox();
        lbNMEAFileMCHN = new javax.swing.JCheckBox();
        cbNotApplyUTCOffsetForNMEA = new javax.swing.JCheckBox();
        pnGPXFileSettings = new javax.swing.JPanel();
        cbNotApplyUTCOffset = new javax.swing.JCheckBox();
        cbGPXTrkSegWhenSmall = new javax.swing.JCheckBox();
        cbGPXAddLink = new javax.swing.JCheckBox();
        cbGPX_1_1 = new javax.swing.JCheckBox();
        pnKMLFileSettings = new javax.swing.JPanel();
        lbAltitudeMode = new javax.swing.JLabel();
        cbAltitudeMode = new javax.swing.JComboBox();

        pnFileNMEAOutput.setBorder(javax.swing.BorderFactory.createTitledBorder("NMEA File Settings"));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        pnFileNMEAOutput.setToolTipText(bundle.getString("BT747Main.pnFileNMEAOutput.toolTipText")); // NOI18N

        lbNMEAFileType9.setText(bundle.getString("BT747Main.lbNMEAFileType9.text")); // NOI18N

        lbNMEAFileGST.setText(bundle.getString("BT747Main.lbNMEAFileGST.text")); // NOI18N

        lbNMEAFileVTG.setText(bundle.getString("BT747Main.lbNMEAFileVTG.text")); // NOI18N

        lbNMEAFileGRS.setText(bundle.getString("BT747Main.lbNMEAFileGRS.text")); // NOI18N

        lbNMEAFileGSV.setText(bundle.getString("BT747Main.lbNMEAFileGSV.text")); // NOI18N

        lbNMEAFileGGA.setText(bundle.getString("BT747Main.lbNMEAFileGGA.text")); // NOI18N

        lbNMEAFileRMC.setText(bundle.getString("BT747Main.lbNMEAFileRMC.text")); // NOI18N

        lbNMEAFileGLL.setText(bundle.getString("BT747Main.lbNMEAFileGLL.text")); // NOI18N

        lbNMEAFileType8.setText(bundle.getString("BT747Main.lbNMEAFileType8.text")); // NOI18N

        lbNMEAFileGSA.setText(bundle.getString("BT747Main.lbNMEAFileGSA.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFileNMEAOutLeftLayout = new org.jdesktop.layout.GroupLayout(pnFileNMEAOutLeft);
        pnFileNMEAOutLeft.setLayout(pnFileNMEAOutLeftLayout);
        pnFileNMEAOutLeftLayout.setHorizontalGroup(
            pnFileNMEAOutLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lbNMEAFileGLL)
            .add(lbNMEAFileRMC)
            .add(lbNMEAFileVTG)
            .add(lbNMEAFileGGA)
            .add(lbNMEAFileGSA)
            .add(lbNMEAFileGSV)
            .add(lbNMEAFileGRS)
            .add(lbNMEAFileGST)
            .add(lbNMEAFileType8)
            .add(lbNMEAFileType9)
        );
        pnFileNMEAOutLeftLayout.setVerticalGroup(
            pnFileNMEAOutLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutLeftLayout.createSequentialGroup()
                .add(lbNMEAFileGLL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileRMC)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileVTG)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGGA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGSA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGSV)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGRS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileGST)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType9))
        );

        lbNMEAFileMDGP.setText(bundle.getString("BT747Main.lbNMEAFileMDGP.text")); // NOI18N

        lbNMEAFileType11.setText(bundle.getString("BT747Main.lbNMEAFileType11.text")); // NOI18N

        lbNMEAFileMEPH.setText(bundle.getString("BT747Main.lbNMEAFileMEPH.text")); // NOI18N

        lbNMEAFileMALM.setText(bundle.getString("BT747Main.lbNMEAFileMALM.text")); // NOI18N

        lbNMEAFileType10.setText(bundle.getString("BT747Main.lbNMEAFileType10.text")); // NOI18N

        lbNMEAFileZDA.setText(bundle.getString("BT747Main.lbNMEAFileZDA.text")); // NOI18N

        lbNMEAFileType12.setText(bundle.getString("BT747Main.lbNMEAFileType12.text")); // NOI18N

        btSetNMEAFileOutput.setText(bundle.getString("BT747Main.btSetNMEAFileOutput.text")); // NOI18N
        btSetNMEAFileOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetNMEAFileOutputActionPerformed(evt);
            }
        });

        lbNMEAFileMDBG.setText(bundle.getString("BT747Main.lbNMEAFileMDBG.text")); // NOI18N

        lbNMEAFileMCHN.setText(bundle.getString("BT747Main.lbNMEAFileMCHN.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFileNMEAOutRightLayout = new org.jdesktop.layout.GroupLayout(pnFileNMEAOutRight);
        pnFileNMEAOutRight.setLayout(pnFileNMEAOutRightLayout);
        pnFileNMEAOutRightLayout.setHorizontalGroup(
            pnFileNMEAOutRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutRightLayout.createSequentialGroup()
                .add(pnFileNMEAOutRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lbNMEAFileType10)
                    .add(lbNMEAFileType11)
                    .add(lbNMEAFileType12)
                    .add(lbNMEAFileMALM)
                    .add(lbNMEAFileMEPH)
                    .add(lbNMEAFileMDGP)
                    .add(lbNMEAFileMDBG)
                    .add(lbNMEAFileZDA)
                    .add(lbNMEAFileMCHN)
                    .add(pnFileNMEAOutRightLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(btSetNMEAFileOutput)))
                .addContainerGap())
        );
        pnFileNMEAOutRightLayout.setVerticalGroup(
            pnFileNMEAOutRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutRightLayout.createSequentialGroup()
                .add(lbNMEAFileType10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileType12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMALM)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMEPH)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMDGP)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMDBG)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileZDA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbNMEAFileMCHN)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btSetNMEAFileOutput)
                .add(0, 0, Short.MAX_VALUE))
        );

        cbNotApplyUTCOffsetForNMEA.setText(bundle.getString("AdvancedFileSettingsPanel.cbNotApplyUTCOffsetForNMEA.text")); // NOI18N
        cbNotApplyUTCOffsetForNMEA.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbNotApplyUTCOffsetForNMEAStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnFileNMEAOutputLayout = new org.jdesktop.layout.GroupLayout(pnFileNMEAOutput);
        pnFileNMEAOutput.setLayout(pnFileNMEAOutputLayout);
        pnFileNMEAOutputLayout.setHorizontalGroup(
            pnFileNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutputLayout.createSequentialGroup()
                .add(pnFileNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFileNMEAOutputLayout.createSequentialGroup()
                        .add(pnFileNMEAOutLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnFileNMEAOutRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(cbNotApplyUTCOffsetForNMEA))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnFileNMEAOutputLayout.setVerticalGroup(
            pnFileNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnFileNMEAOutputLayout.createSequentialGroup()
                .addContainerGap(0, Short.MAX_VALUE)
                .add(cbNotApplyUTCOffsetForNMEA)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFileNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFileNMEAOutLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnFileNMEAOutRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        pnGPXFileSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("GPX File Settings"));
        pnGPXFileSettings.setToolTipText(bundle.getString("BT747Main.pnGPXFileSettings.toolTipText")); // NOI18N

        cbNotApplyUTCOffset.setText(bundle.getString("BT747Main.cbNotApplyUTCOffset.text")); // NOI18N
        cbNotApplyUTCOffset.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbNotApplyUTCOffsetStateChanged(evt);
            }
        });

        cbGPXTrkSegWhenSmall.setText(bundle.getString("BT747Main.cbGPXTrkSegWhenSmall.text")); // NOI18N
        cbGPXTrkSegWhenSmall.setToolTipText(bundle.getString("BT747Main.cbGPXTrkSegWhenSmall.toolTipText")); // NOI18N
        cbGPXTrkSegWhenSmall.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbGPXTrkSegWhenSmallStateChanged(evt);
            }
        });

        cbGPXAddLink.setText(bundle.getString("AdvancedFileSettingsPanel.cbGPXAddLink.text")); // NOI18N
        cbGPXAddLink.setToolTipText(bundle.getString("AdvancedFileSettingsPanel.cbGPXAddLink.toolTipText")); // NOI18N
        cbGPXAddLink.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbGPXAddLinkStateChanged(evt);
            }
        });

        cbGPX_1_1.setText(bundle.getString("AdvancedFileSettingsPanel.cbGPX_1_1.text")); // NOI18N
        cbGPX_1_1.setToolTipText(bundle.getString("AdvancedFileSettingsPanel.cbGPX_1_1.toolTipText")); // NOI18N
        cbGPX_1_1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbGPX_1_1StateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnGPXFileSettingsLayout = new org.jdesktop.layout.GroupLayout(pnGPXFileSettings);
        pnGPXFileSettings.setLayout(pnGPXFileSettingsLayout);
        pnGPXFileSettingsLayout.setHorizontalGroup(
            pnGPXFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPXFileSettingsLayout.createSequentialGroup()
                .add(pnGPXFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbNotApplyUTCOffset)
                    .add(cbGPXTrkSegWhenSmall)
                    .add(cbGPXAddLink)
                    .add(cbGPX_1_1))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        pnGPXFileSettingsLayout.setVerticalGroup(
            pnGPXFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnGPXFileSettingsLayout.createSequentialGroup()
                .add(cbNotApplyUTCOffset)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGPXTrkSegWhenSmall)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGPXAddLink)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGPX_1_1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnKMLFileSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("AdvancedFileSettingsPanel.pnKMLFileSettings.border.title"))); // NOI18N
        pnKMLFileSettings.setToolTipText(bundle.getString("AdvancedFileSettingsPanel.pnKMLFileSettings.toolTipText")); // NOI18N

        lbAltitudeMode.setText(bundle.getString("AdvancedFileSettingsPanel.lbAltitudeMode.text")); // NOI18N

        cbAltitudeMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Clamp To Ground", "Relative", "Absolute" }));
        cbAltitudeMode.setToolTipText(bundle.getString("AdvancedFileSettingsPanel.cbAltitudeMode.toolTipText")); // NOI18N
        cbAltitudeMode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAltitudeModeItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnKMLFileSettingsLayout = new org.jdesktop.layout.GroupLayout(pnKMLFileSettings);
        pnKMLFileSettings.setLayout(pnKMLFileSettingsLayout);
        pnKMLFileSettingsLayout.setHorizontalGroup(
            pnKMLFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnKMLFileSettingsLayout.createSequentialGroup()
                .add(lbAltitudeMode)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbAltitudeMode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnKMLFileSettingsLayout.setVerticalGroup(
            pnKMLFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnKMLFileSettingsLayout.createSequentialGroup()
                .add(pnKMLFileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbAltitudeMode)
                    .add(cbAltitudeMode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnFileNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnKMLFileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnGPXFileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnGPXFileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnKMLFileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pnFileNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
    }//GEN-END:initComponents

    private void btSetNMEAFileOutputActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetNMEAFileOutputActionPerformed
        setNMEAOutFile();
    }//GEN-LAST:event_btSetNMEAFileOutputActionPerformed

    private void cbNotApplyUTCOffsetStateChanged(
            final javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbNotApplyUTCOffsetStateChanged
        c.setGpxUTC0(cbNotApplyUTCOffset.isSelected());
    }//GEN-LAST:event_cbNotApplyUTCOffsetStateChanged

    private void cbGPXTrkSegWhenSmallStateChanged(
            final javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbGPXTrkSegWhenSmallStateChanged
        c.setGpxTrkSegWhenBig(cbGPXTrkSegWhenSmall.isSelected());
    }//GEN-LAST:event_cbGPXTrkSegWhenSmallStateChanged

    private void cbAltitudeModeItemStateChanged(
            final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAltitudeModeItemStateChanged
        c.setIntOpt(AppSettings.KML_ALTITUDEMODE, cbAltitudeMode
                .getSelectedIndex());
    }//GEN-LAST:event_cbAltitudeModeItemStateChanged

    private void cbGPXAddLinkStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbGPXAddLinkStateChanged
        c.setBooleanOpt(AppSettings.GPX_LINK_INFO, cbGPXAddLink.isSelected());
}//GEN-LAST:event_cbGPXAddLinkStateChanged

    private void cbGPX_1_1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbGPX_1_1StateChanged
        c.setBooleanOpt(AppSettings.IS_GPX_1_1, cbGPX_1_1.isSelected());
}//GEN-LAST:event_cbGPX_1_1StateChanged

    private void cbNotApplyUTCOffsetForNMEAStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbNotApplyUTCOffsetForNMEAStateChanged
        c.setBooleanOpt(AppSettings.NMEAUTC0, cbNotApplyUTCOffsetForNMEA.isSelected());
}//GEN-LAST:event_cbNotApplyUTCOffsetForNMEAStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btSetNMEAFileOutput;
    private javax.swing.JComboBox cbAltitudeMode;
    private javax.swing.JCheckBox cbGPXAddLink;
    private javax.swing.JCheckBox cbGPXTrkSegWhenSmall;
    private javax.swing.JCheckBox cbGPX_1_1;
    private javax.swing.JCheckBox cbNotApplyUTCOffset;
    private javax.swing.JCheckBox cbNotApplyUTCOffsetForNMEA;
    private javax.swing.JLabel lbAltitudeMode;
    private javax.swing.JCheckBox lbNMEAFileGGA;
    private javax.swing.JCheckBox lbNMEAFileGLL;
    private javax.swing.JCheckBox lbNMEAFileGRS;
    private javax.swing.JCheckBox lbNMEAFileGSA;
    private javax.swing.JCheckBox lbNMEAFileGST;
    private javax.swing.JCheckBox lbNMEAFileGSV;
    private javax.swing.JCheckBox lbNMEAFileMALM;
    private javax.swing.JCheckBox lbNMEAFileMCHN;
    private javax.swing.JCheckBox lbNMEAFileMDBG;
    private javax.swing.JCheckBox lbNMEAFileMDGP;
    private javax.swing.JCheckBox lbNMEAFileMEPH;
    private javax.swing.JCheckBox lbNMEAFileRMC;
    private javax.swing.JCheckBox lbNMEAFileType10;
    private javax.swing.JCheckBox lbNMEAFileType11;
    private javax.swing.JCheckBox lbNMEAFileType12;
    private javax.swing.JCheckBox lbNMEAFileType8;
    private javax.swing.JCheckBox lbNMEAFileType9;
    private javax.swing.JCheckBox lbNMEAFileVTG;
    private javax.swing.JCheckBox lbNMEAFileZDA;
    private javax.swing.JPanel pnFileNMEAOutLeft;
    private javax.swing.JPanel pnFileNMEAOutRight;
    private javax.swing.JPanel pnFileNMEAOutput;
    private javax.swing.JPanel pnGPXFileSettings;
    private javax.swing.JPanel pnKMLFileSettings;
    // End of variables declaration//GEN-END:variables

}