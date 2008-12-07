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

import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;
/**
 *
 * @author  Mario De Weerd
 */
public final class AdvancedDeviceSettingsPanel extends javax.swing.JPanel implements
    ModelListener
{

    /**
     * 
     */
    private static final long serialVersionUID = 9139717181351131626L;
    
    private J2SEAppController c;
    private Model m;
    /** Creates new form AdvancedDeviceSettingsPanel */
    public AdvancedDeviceSettingsPanel() {
        initComponents();
        
    }

    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);
    }

   
    public void modelEvent(final ModelEvent e) {
        // TODO Auto-generated method stub
        int type = e.getType();
        switch (type) {
        case ModelEvent.UPDATE_OUTPUT_NMEA_PERIOD:
            getNMEAOutPeriods();
            break;
        case ModelEvent.UPDATE_FLASH_CONFIG:
            getFlashConfig();
            break;
        case ModelEvent.UPDATE_BT_MAC_ADDR:
            tfBluetoothMacAddress.setText(m.getBTAddr());
        }
    }

    void getFlashConfig() {
        txtFlashTimesLeft.setText(Convert
                .toString(m.getDtUserOptionTimesLeft()));
        txtFlashUpdateRate.setText(Convert.toString(m.getDtUpdateRate()));
        txtFlashBaudRate.setText(Convert.toString(m.getDtBaudRate()));
        cbFlashGLL.setSelectedIndex(m.getDtGLL_Period());
        cbFlashRMC.setSelectedIndex(m.getDtRMC_Period());
        cbFlashVTG.setSelectedIndex(m.getDtVTG_Period());
        cbFlashGSA.setSelectedIndex(m.getDtGSA_Period());
        cbFlashGSV.setSelectedIndex(m.getDtGSV_Period());
        cbFlashGGA.setSelectedIndex(m.getDtGGA_Period());
        cbFlashZDA.setSelectedIndex(m.getDtZDA_Period());
        cbFlashMCHN.setSelectedIndex(m.getDtMCHN_Period());
    }

    void setFlashConfig() {
        boolean lock;
        int updateRate;
        int baudRate;
        int periodGLL;
        int periodRMC;
        int periodVTG;
        int periodGSA;
        int periodGSV;
        int periodGGA;
        int periodZDA;
        int periodMCHN;
        lock = false;
        updateRate = Convert.toInt(txtFlashUpdateRate.getText());
        baudRate = Convert.toInt(txtFlashBaudRate.getText());
        periodGLL = cbFlashGLL.getSelectedIndex();
        periodRMC = cbFlashRMC.getSelectedIndex();
        periodVTG = cbFlashVTG.getSelectedIndex();
        periodGSA = cbFlashGSA.getSelectedIndex();
        periodGSV = cbFlashGSV.getSelectedIndex();
        periodGGA = cbFlashGGA.getSelectedIndex();
        periodZDA = cbFlashZDA.getSelectedIndex();
        periodMCHN = cbFlashMCHN.getSelectedIndex();
        c.setFlashConfig(lock, updateRate, baudRate, periodGLL, periodRMC,
                periodVTG, periodGSA, periodGSV, periodGGA, periodZDA,
                periodMCHN);
    }


        void getNMEAOutPeriods() {
        cbNMEAOutGLL.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GLL_IDX));
        cbNMEAOutRMC.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_RMC_IDX));
        cbNMEAOutVTG.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_VTG_IDX));
        cbNMEAOutGGA.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GGA_IDX));
        cbNMEAOutGSA.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GSA_IDX));
        cbNMEAOutGSV.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GSV_IDX));
        cbNMEAOutGRS.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GRS_IDX));
        cbNMEAOutGST.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_GST_IDX));
        cbNMEAOutType8.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE8_IDX));
        cbNMEAOutType9.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE9_IDX));
        cbNMEAOutType10.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE10_IDX));
        cbNMEAOutType11.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE11_IDX));
        cbNMEAOutType12.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_TYPE12_IDX));
        cbNMEAOutMALM.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MALM_IDX));
        cbNMEAOutMEPH.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MEPH_IDX));
        cbNMEAOutMDGP.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MDGP_IDX));
        cbNMEAOutMDBG.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MDBG_IDX));
        cbNMEAOutZDA.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_ZDA_IDX));
        cbNMEAOutMCHN.setSelectedIndex(m
                .getNMEAPeriod(BT747Constants.NMEA_SEN_MCHN_IDX));
    }

    void setNMEAOutPeriods() {
        int[] Periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        Periods[BT747Constants.NMEA_SEN_GLL_IDX] = cbNMEAOutGLL
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_RMC_IDX] = cbNMEAOutRMC
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_VTG_IDX] = cbNMEAOutVTG
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GGA_IDX] = cbNMEAOutGGA
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GSA_IDX] = cbNMEAOutGSA
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GSV_IDX] = cbNMEAOutGSV
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GRS_IDX] = cbNMEAOutGRS
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_GST_IDX] = cbNMEAOutGST
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE8_IDX] = cbNMEAOutType8
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE9_IDX] = cbNMEAOutType9
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE10_IDX] = cbNMEAOutType10
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE11_IDX] = cbNMEAOutType11
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_TYPE12_IDX] = cbNMEAOutType12
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MALM_IDX] = cbNMEAOutMALM
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MEPH_IDX] = cbNMEAOutMEPH
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MDGP_IDX] = cbNMEAOutMDGP
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MDBG_IDX] = cbNMEAOutMDBG
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_ZDA_IDX] = cbNMEAOutZDA
                .getSelectedIndex();
        Periods[BT747Constants.NMEA_SEN_MCHN_IDX] = cbNMEAOutMCHN
                .getSelectedIndex();

        c.setNMEAPeriods(Periods);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        pnFlashSettings = new javax.swing.JPanel();
        txtTimesLeft = new javax.swing.JLabel();
        lbUpdateRate = new javax.swing.JLabel();
        lbBaudRate = new javax.swing.JLabel();
        lbGLLOut = new javax.swing.JLabel();
        lbRMCOut = new javax.swing.JLabel();
        lbVTGOut = new javax.swing.JLabel();
        cbGSVOut = new javax.swing.JLabel();
        cbGSAOut = new javax.swing.JLabel();
        cbGGAOut = new javax.swing.JLabel();
        cbFlashGLL = new javax.swing.JComboBox();
        cbFlashRMC = new javax.swing.JComboBox();
        cbFlashVTG = new javax.swing.JComboBox();
        cbFlashGGA = new javax.swing.JComboBox();
        cbFlashGSA = new javax.swing.JComboBox();
        cbFlashGSV = new javax.swing.JComboBox();
        txtFlashTimesLeft = new javax.swing.JTextField();
        txtFlashUpdateRate = new javax.swing.JTextField();
        txtFlashBaudRate = new javax.swing.JTextField();
        lbFlashZDA = new javax.swing.JLabel();
        cbFlashZDA = new javax.swing.JComboBox();
        cbFlashMCHN = new javax.swing.JComboBox();
        btSetFlash = new javax.swing.JButton();
        lbPeriodMCHN = new javax.swing.JLabel();
        pnNMEAOutput = new javax.swing.JPanel();
        cbNMEAOutType10 = new javax.swing.JComboBox();
        lbNMEAOutType10 = new javax.swing.JLabel();
        cbNMEAOutType11 = new javax.swing.JComboBox();
        cbNMEAOutType12 = new javax.swing.JComboBox();
        cbNMEAOutMALM = new javax.swing.JComboBox();
        cbNMEAOutMDGP = new javax.swing.JComboBox();
        cbNMEAOutMEPH = new javax.swing.JComboBox();
        cbNMEAOutMDBG = new javax.swing.JComboBox();
        cbNMEAOutZDA = new javax.swing.JComboBox();
        lbPeriodType11 = new javax.swing.JLabel();
        lbPeriodType12 = new javax.swing.JLabel();
        lbPeriodMALM = new javax.swing.JLabel();
        lbPeriodMEPH = new javax.swing.JLabel();
        lbPeriodMDGP = new javax.swing.JLabel();
        lbPeriodMDBG = new javax.swing.JLabel();
        lbNMEAOutZDA = new javax.swing.JLabel();
        btSetNMEAOutput = new javax.swing.JButton();
        btSetNMEAOutputDefaults = new javax.swing.JButton();
        lbGLLOut1 = new javax.swing.JLabel();
        cbNMEAOutGLL = new javax.swing.JComboBox();
        cbNMEAOutRMC = new javax.swing.JComboBox();
        lbRMCOut1 = new javax.swing.JLabel();
        cbVTGOut1 = new javax.swing.JLabel();
        cbNMEAOutVTG = new javax.swing.JComboBox();
        cbNMEAOutGGA = new javax.swing.JComboBox();
        cbGGAOut1 = new javax.swing.JLabel();
        cbGSAOut1 = new javax.swing.JLabel();
        cbNMEAOutGSA = new javax.swing.JComboBox();
        cbNMEAOutGSV = new javax.swing.JComboBox();
        cbGSVOut1 = new javax.swing.JLabel();
        cbGRSOut = new javax.swing.JLabel();
        cbGSTOut = new javax.swing.JLabel();
        cbType9Out = new javax.swing.JLabel();
        cbType8Out = new javax.swing.JLabel();
        cbNMEAOutType9 = new javax.swing.JComboBox();
        cbNMEAOutType8 = new javax.swing.JComboBox();
        cbNMEAOutGST = new javax.swing.JComboBox();
        cbNMEAOutGRS = new javax.swing.JComboBox();
        cbNMEAOutMCHN = new javax.swing.JComboBox();
        lbPeriodMCHN1 = new javax.swing.JLabel();
        pnBluetoothMacAdr = new javax.swing.JPanel();
        tfBluetoothMacAddress = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        pnFlashSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnFlashSettings.border.title"))); // NOI18N
        pnFlashSettings.setToolTipText(bundle.getString("BT747Main.pnFlashSettings.toolTipText")); // NOI18N

        txtTimesLeft.setText(bundle.getString("BT747Main.txtTimesLeft.text")); // NOI18N

        lbUpdateRate.setText(bundle.getString("BT747Main.lbUpdateRate.text")); // NOI18N

        lbBaudRate.setText(bundle.getString("BT747Main.lbBaudRate.text")); // NOI18N

        lbGLLOut.setText(bundle.getString("BT747Main.lbGLLOut.text")); // NOI18N

        lbRMCOut.setText(bundle.getString("BT747Main.lbRMCOut.text")); // NOI18N

        lbVTGOut.setText(bundle.getString("BT747Main.lbVTGOut.text")); // NOI18N

        cbGSVOut.setText(bundle.getString("BT747Main.cbGSVOut.text")); // NOI18N

        cbGSAOut.setText(bundle.getString("BT747Main.cbGSAOut.text")); // NOI18N

        cbGGAOut.setText(bundle.getString("BT747Main.cbGGAOut.text")); // NOI18N

        cbFlashGLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashRMC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashVTG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashGGA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashGSA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashGSV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        txtFlashTimesLeft.setEditable(false);
        txtFlashTimesLeft.setText(bundle.getString("BT747Main.txtFlashTimesLeft.text")); // NOI18N

        txtFlashUpdateRate.setText(bundle.getString("BT747Main.txtFlashUpdateRate.text")); // NOI18N

        txtFlashBaudRate.setEditable(false);
        txtFlashBaudRate.setText(bundle.getString("BT747Main.txtFlashBaudRate.text")); // NOI18N

        lbFlashZDA.setText(bundle.getString("BT747Main.lbFlashZDA.text")); // NOI18N

        cbFlashZDA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbFlashMCHN.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        btSetFlash.setText(bundle.getString("BT747Main.btSetFlash.text")); // NOI18N
        btSetFlash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetFlashActionPerformed(evt);
            }
        });

        lbPeriodMCHN.setText(bundle.getString("BT747Main.lbPeriodMCHN.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnFlashSettingsLayout = new org.jdesktop.layout.GroupLayout(pnFlashSettings);
        pnFlashSettings.setLayout(pnFlashSettingsLayout);
        pnFlashSettingsLayout.setHorizontalGroup(
            pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFlashSettingsLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(txtTimesLeft)
                    .add(lbBaudRate)
                    .add(lbRMCOut)
                    .add(lbVTGOut)
                    .add(cbGGAOut)
                    .add(cbGSAOut)
                    .add(lbFlashZDA)
                    .add(lbUpdateRate)
                    .add(cbGSVOut)
                    .add(lbGLLOut)
                    .add(lbPeriodMCHN))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnFlashSettingsLayout.createSequentialGroup()
                        .add(cbFlashMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btSetFlash))
                    .add(cbFlashGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbFlashGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtFlashTimesLeft)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtFlashBaudRate)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtFlashUpdateRate)))
                .addContainerGap())
        );
        pnFlashSettingsLayout.setVerticalGroup(
            pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnFlashSettingsLayout.createSequentialGroup()
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtTimesLeft)
                    .add(txtFlashTimesLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbUpdateRate)
                    .add(txtFlashUpdateRate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbBaudRate)
                    .add(txtFlashBaudRate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbGLLOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbRMCOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbVTGOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbGGAOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbGSAOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbFlashZDA))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbGSVOut))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnFlashSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbFlashMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbPeriodMCHN)
                    .add(btSetFlash))
                .add(19, 19, 19))
        );

        pnNMEAOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BT747Main.pnNMEAOutput.border.title"))); // NOI18N
        pnNMEAOutput.setToolTipText(bundle.getString("BT747Main.pnNMEAOutput.toolTipText")); // NOI18N

        cbNMEAOutType10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        lbNMEAOutType10.setText(bundle.getString("BT747Main.lbNMEAOutType10.text")); // NOI18N

        cbNMEAOutType11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutType12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMALM.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMDGP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMEPH.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMDBG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutZDA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        lbPeriodType11.setText(bundle.getString("BT747Main.lbPeriodType11.text")); // NOI18N

        lbPeriodType12.setText(bundle.getString("BT747Main.lbPeriodType12.text")); // NOI18N

        lbPeriodMALM.setText(bundle.getString("BT747Main.lbPeriodMALM.text")); // NOI18N

        lbPeriodMEPH.setText(bundle.getString("BT747Main.lbPeriodMEPH.text")); // NOI18N

        lbPeriodMDGP.setText(bundle.getString("BT747Main.lbPeriodMDGP.text")); // NOI18N

        lbPeriodMDBG.setText(bundle.getString("BT747Main.lbPeriodMDBG.text")); // NOI18N

        lbNMEAOutZDA.setText(bundle.getString("BT747Main.lbNMEAOutZDA.text")); // NOI18N

        btSetNMEAOutput.setText(bundle.getString("BT747Main.btSetNMEAOutput.text")); // NOI18N
        btSetNMEAOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetNMEAOutputActionPerformed(evt);
            }
        });

        btSetNMEAOutputDefaults.setText(bundle.getString("BT747Main.btSetNMEAOutputDefaults.text")); // NOI18N
        btSetNMEAOutputDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetNMEAOutputDefaultsActionPerformed(evt);
            }
        });

        lbGLLOut1.setText(bundle.getString("BT747Main.lbGLLOut1.text")); // NOI18N

        cbNMEAOutGLL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutRMC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        lbRMCOut1.setText(bundle.getString("BT747Main.lbRMCOut1.text")); // NOI18N

        cbVTGOut1.setText(bundle.getString("BT747Main.cbVTGOut1.text")); // NOI18N

        cbNMEAOutVTG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGGA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbGGAOut1.setText(bundle.getString("BT747Main.cbGGAOut1.text")); // NOI18N

        cbGSAOut1.setText(bundle.getString("BT747Main.cbGSAOut1.text")); // NOI18N

        cbNMEAOutGSA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGSV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbGSVOut1.setText(bundle.getString("BT747Main.cbGSVOut1.text")); // NOI18N

        cbGRSOut.setText(bundle.getString("BT747Main.cbGRSOut.text")); // NOI18N

        cbGSTOut.setText(bundle.getString("BT747Main.cbGSTOut.text")); // NOI18N

        cbType9Out.setText(bundle.getString("BT747Main.cbType9Out.text")); // NOI18N

        cbType8Out.setText(bundle.getString("BT747Main.cbType8Out.text")); // NOI18N

        cbNMEAOutType9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutType8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGST.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutGRS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        cbNMEAOutMCHN.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5" }));

        lbPeriodMCHN1.setText(bundle.getString("BT747Main.lbPeriodMCHN.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnNMEAOutputLayout = new org.jdesktop.layout.GroupLayout(pnNMEAOutput);
        pnNMEAOutput.setLayout(pnNMEAOutputLayout);
        pnNMEAOutputLayout.setHorizontalGroup(
            pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnNMEAOutputLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnNMEAOutputLayout.createSequentialGroup()
                        .add(lbNMEAOutType10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbNMEAOutType10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnNMEAOutputLayout.createSequentialGroup()
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lbGLLOut1)
                            .add(cbGSVOut1)
                            .add(cbGSAOut1)
                            .add(cbGGAOut1)
                            .add(cbVTGOut1)
                            .add(lbRMCOut1)
                            .add(cbGRSOut)
                            .add(cbType9Out)
                            .add(cbType8Out)
                            .add(cbGSTOut))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbNMEAOutGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbNMEAOutGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutGRS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutGST, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutType8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cbNMEAOutType9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbPeriodType11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutType11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbPeriodType12)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutType12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbPeriodMALM)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMALM, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbPeriodMEPH)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMEPH, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbPeriodMDGP)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMDGP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbPeriodMDBG)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMDBG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbNMEAOutZDA)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnNMEAOutputLayout.createSequentialGroup()
                                .add(lbPeriodMCHN1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbNMEAOutMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(btSetNMEAOutput)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btSetNMEAOutputDefaults))
                .addContainerGap())
        );
        pnNMEAOutputLayout.setVerticalGroup(
            pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnNMEAOutputLayout.createSequentialGroup()
                .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnNMEAOutputLayout.createSequentialGroup()
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGLL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbGLLOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutRMC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbRMCOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutVTG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbVTGOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGGA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGGAOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGSA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGSAOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGSV, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGSVOut1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGRS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGRSOut))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutGST, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbGSTOut))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cbType8Out)
                            .add(lbPeriodMCHN1)))
                    .add(pnNMEAOutputLayout.createSequentialGroup()
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbNMEAOutType10))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbPeriodType11))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutType12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbPeriodType12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMALM, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbPeriodMALM))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMEPH, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbPeriodMEPH))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMDGP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbPeriodMDGP))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutMDBG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbPeriodMDBG))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbNMEAOutZDA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbNMEAOutZDA))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbNMEAOutMCHN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnNMEAOutputLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(cbNMEAOutType9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(cbType9Out))
                    .add(btSetNMEAOutput))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btSetNMEAOutputDefaults)
                .addContainerGap())
        );

        pnBluetoothMacAdr.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("AdvancedDeviceSettingsPanel.pnBluetoothMacAdr.border.title"))); // NOI18N
        pnBluetoothMacAdr.setToolTipText(bundle.getString("AdvancedDeviceSettingsPanel.pnBluetoothMacAdr.toolTipText")); // NOI18N

        tfBluetoothMacAddress.setText(bundle.getString("AdvancedDeviceSettingsPanel.tfBluetoothMacAddress.text")); // NOI18N
        tfBluetoothMacAddress.setToolTipText(bundle.getString("AdvancedDeviceSettingsPanel.tfBluetoothMacAddress.toolTipText")); // NOI18N

        jButton1.setText(bundle.getString("AdvancedDeviceSettingsPanel.jButton1.text")); // NOI18N
        jButton1.setToolTipText(bundle.getString("AdvancedDeviceSettingsPanel.jButton1.toolTipText")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnBluetoothMacAdrLayout = new org.jdesktop.layout.GroupLayout(pnBluetoothMacAdr);
        pnBluetoothMacAdr.setLayout(pnBluetoothMacAdrLayout);
        pnBluetoothMacAdrLayout.setHorizontalGroup(
            pnBluetoothMacAdrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnBluetoothMacAdrLayout.createSequentialGroup()
                .add(pnBluetoothMacAdrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, tfBluetoothMacAddress)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnBluetoothMacAdrLayout.setVerticalGroup(
            pnBluetoothMacAdrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnBluetoothMacAdrLayout.createSequentialGroup()
                .add(tfBluetoothMacAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnFlashSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnBluetoothMacAdr, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnNMEAOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnFlashSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(pnBluetoothMacAdr, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
    }//GEN-END:initComponents

private void btSetFlashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetFlashActionPerformed
     setFlashConfig();
}//GEN-LAST:event_btSetFlashActionPerformed

private void btSetNMEAOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetNMEAOutputActionPerformed
     setNMEAOutPeriods();
}//GEN-LAST:event_btSetNMEAOutputActionPerformed

private void btSetNMEAOutputDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetNMEAOutputDefaultsActionPerformed
     c.setNMEADefaultPeriods();
}//GEN-LAST:event_btSetNMEAOutputDefaultsActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     c.setBTMacAddr(tfBluetoothMacAddress.getText());
}//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btSetFlash;
    private javax.swing.JButton btSetNMEAOutput;
    private javax.swing.JButton btSetNMEAOutputDefaults;
    private javax.swing.JComboBox cbFlashGGA;
    private javax.swing.JComboBox cbFlashGLL;
    private javax.swing.JComboBox cbFlashGSA;
    private javax.swing.JComboBox cbFlashGSV;
    private javax.swing.JComboBox cbFlashMCHN;
    private javax.swing.JComboBox cbFlashRMC;
    private javax.swing.JComboBox cbFlashVTG;
    private javax.swing.JComboBox cbFlashZDA;
    private javax.swing.JLabel cbGGAOut;
    private javax.swing.JLabel cbGGAOut1;
    private javax.swing.JLabel cbGRSOut;
    private javax.swing.JLabel cbGSAOut;
    private javax.swing.JLabel cbGSAOut1;
    private javax.swing.JLabel cbGSTOut;
    private javax.swing.JLabel cbGSVOut;
    private javax.swing.JLabel cbGSVOut1;
    private javax.swing.JComboBox cbNMEAOutGGA;
    private javax.swing.JComboBox cbNMEAOutGLL;
    private javax.swing.JComboBox cbNMEAOutGRS;
    private javax.swing.JComboBox cbNMEAOutGSA;
    private javax.swing.JComboBox cbNMEAOutGST;
    private javax.swing.JComboBox cbNMEAOutGSV;
    private javax.swing.JComboBox cbNMEAOutMALM;
    private javax.swing.JComboBox cbNMEAOutMCHN;
    private javax.swing.JComboBox cbNMEAOutMDBG;
    private javax.swing.JComboBox cbNMEAOutMDGP;
    private javax.swing.JComboBox cbNMEAOutMEPH;
    private javax.swing.JComboBox cbNMEAOutRMC;
    private javax.swing.JComboBox cbNMEAOutType10;
    private javax.swing.JComboBox cbNMEAOutType11;
    private javax.swing.JComboBox cbNMEAOutType12;
    private javax.swing.JComboBox cbNMEAOutType8;
    private javax.swing.JComboBox cbNMEAOutType9;
    private javax.swing.JComboBox cbNMEAOutVTG;
    private javax.swing.JComboBox cbNMEAOutZDA;
    private javax.swing.JLabel cbType8Out;
    private javax.swing.JLabel cbType9Out;
    private javax.swing.JLabel cbVTGOut1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel lbBaudRate;
    private javax.swing.JLabel lbFlashZDA;
    private javax.swing.JLabel lbGLLOut;
    private javax.swing.JLabel lbGLLOut1;
    private javax.swing.JLabel lbNMEAOutType10;
    private javax.swing.JLabel lbNMEAOutZDA;
    private javax.swing.JLabel lbPeriodMALM;
    private javax.swing.JLabel lbPeriodMCHN;
    private javax.swing.JLabel lbPeriodMCHN1;
    private javax.swing.JLabel lbPeriodMDBG;
    private javax.swing.JLabel lbPeriodMDGP;
    private javax.swing.JLabel lbPeriodMEPH;
    private javax.swing.JLabel lbPeriodType11;
    private javax.swing.JLabel lbPeriodType12;
    private javax.swing.JLabel lbRMCOut;
    private javax.swing.JLabel lbRMCOut1;
    private javax.swing.JLabel lbUpdateRate;
    private javax.swing.JLabel lbVTGOut;
    private javax.swing.JPanel pnBluetoothMacAdr;
    private javax.swing.JPanel pnFlashSettings;
    private javax.swing.JPanel pnNMEAOutput;
    private javax.swing.JTextField tfBluetoothMacAddress;
    private javax.swing.JTextField txtFlashBaudRate;
    private javax.swing.JTextField txtFlashTimesLeft;
    private javax.swing.JTextField txtFlashUpdateRate;
    private javax.swing.JLabel txtTimesLeft;
    // End of variables declaration//GEN-END:variables

}
