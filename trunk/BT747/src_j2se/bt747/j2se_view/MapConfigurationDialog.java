/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MapConfigurationDialog.java
 *
 * Created on 24 oct. 2009, 16:54:29
 */

package bt747.j2se_view;

/**
 *
 * @author Mario
 */
public class MapConfigurationDialog extends javax.swing.JDialog {
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    /** Creates new form MapConfigurationDialog */
    public MapConfigurationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        pnTileSettings = new javax.swing.JPanel();
        lbShortName = new javax.swing.JLabel();
        txtShortName = new javax.swing.JTextField();
        txtMinimumZoomLevel = new javax.swing.JTextField();
        lbMinZoomLevel = new javax.swing.JLabel();
        lbXRightToLeft = new javax.swing.JLabel();
        txtTotalMapZoom = new javax.swing.JTextField();
        lbTotalMapZoom = new javax.swing.JLabel();
        lbYTopToBottom = new javax.swing.JLabel();
        txtMaxZoomLevel = new javax.swing.JTextField();
        lbMaxZoomLevel = new javax.swing.JLabel();
        txtUrl = new javax.swing.JTextField();
        lbUrl = new javax.swing.JLabel();
        cbXRightToLeft = new javax.swing.JCheckBox();
        cbYTopToBottom = new javax.swing.JCheckBox();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        okButton.setText(bundle.getString("MapConfigurationDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("MapConfigurationDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        pnTileSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("MapConfigurationDialog.pnTileSettings.border.title"))); // NOI18N

        lbShortName.setText(bundle.getString("MapConfigurationDialog.lbShortName.text")); // NOI18N

        txtShortName.setMinimumSize(new java.awt.Dimension(50, 20));

        txtMinimumZoomLevel.setInputVerifier(J2SEAppController.IntVerifier);

        lbMinZoomLevel.setText(bundle.getString("MapConfigurationDialog.lbMinZoomLevel.text")); // NOI18N

        lbXRightToLeft.setText(bundle.getString("MapConfigurationDialog.lbXRightToLeft.text")); // NOI18N

        txtTotalMapZoom.setInputVerifier(J2SEAppController.IntVerifier);

        lbTotalMapZoom.setText(bundle.getString("MapConfigurationDialog.lbTotalMapZoom.text")); // NOI18N

        lbYTopToBottom.setText(bundle.getString("MapConfigurationDialog.lbYTopToBottom.text")); // NOI18N

        txtMaxZoomLevel.setInputVerifier(J2SEAppController.IntVerifier);

        lbMaxZoomLevel.setText(bundle.getString("MapConfigurationDialog.lbMaxZoomLevel.text")); // NOI18N

        lbUrl.setText(bundle.getString("MapConfigurationDialog.lbUrl.text")); // NOI18N

        cbXRightToLeft.setText(null);

        cbYTopToBottom.setText(null);

        org.jdesktop.layout.GroupLayout pnTileSettingsLayout = new org.jdesktop.layout.GroupLayout(pnTileSettings);
        pnTileSettings.setLayout(pnTileSettingsLayout);
        pnTileSettingsLayout.setHorizontalGroup(
            pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTileSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbUrl)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbYTopToBottom)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbXRightToLeft)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbTotalMapZoom)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbMaxZoomLevel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbMinZoomLevel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbShortName))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cbXRightToLeft)
                    .add(cbYTopToBottom)
                    .add(txtUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtTotalMapZoom)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtMinimumZoomLevel)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtShortName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtMaxZoomLevel)))
                .addContainerGap())
        );
        pnTileSettingsLayout.setVerticalGroup(
            pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTileSettingsLayout.createSequentialGroup()
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbShortName)
                    .add(txtShortName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbMinZoomLevel)
                    .add(txtMinimumZoomLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbMaxZoomLevel)
                    .add(txtMaxZoomLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbTotalMapZoom)
                    .add(txtTotalMapZoom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbXRightToLeft)
                    .add(cbXRightToLeft))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbYTopToBottom)
                    .add(cbYTopToBottom))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTileSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbUrl)
                    .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(250, Short.MAX_VALUE)
                .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cancelButton)
                .addContainerGap())
            .add(pnTileSettings, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(new java.awt.Component[] {cancelButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(pnTileSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MapConfigurationDialog dialog = new MapConfigurationDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox cbXRightToLeft;
    private javax.swing.JCheckBox cbYTopToBottom;
    private javax.swing.JLabel lbMaxZoomLevel;
    private javax.swing.JLabel lbMinZoomLevel;
    private javax.swing.JLabel lbShortName;
    private javax.swing.JLabel lbTotalMapZoom;
    private javax.swing.JLabel lbUrl;
    private javax.swing.JLabel lbXRightToLeft;
    private javax.swing.JLabel lbYTopToBottom;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel pnTileSettings;
    private javax.swing.JTextField txtMaxZoomLevel;
    private javax.swing.JTextField txtMinimumZoomLevel;
    private javax.swing.JTextField txtShortName;
    private javax.swing.JTextField txtTotalMapZoom;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
