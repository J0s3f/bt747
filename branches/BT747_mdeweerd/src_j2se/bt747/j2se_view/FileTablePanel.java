/*
 * FileTablePanel.java
 *
 * Created on 2 décembre 2008, 02:06
 */

package bt747.j2se_view;

import java.io.File;

import javax.swing.JFileChooser;

import bt747.j2se_view.filefilters.JpgFileFilter;
import bt747.j2se_view.image.ImageData;
import bt747.model.Model;
import bt747.sys.Generic;

/**
 *
 * @author  Mario
 */
public class FileTablePanel extends javax.swing.JPanel {

    /** Creates new form FileTablePanel */
    public FileTablePanel() {
        initComponents();
    }

    private J2SEAppController c;
    private Model m;

    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getModel();

        fileTableModel = new FileTableModel();
        tbImageList.setModel(fileTableModel);
        // m.addListener(this);

        // dt = new DropTarget(tbImageList,this);
        // tbImageList.setDropTarget(dt);

        btSelectDestinationDir.setVisible(false);
        btTagFromFile.setVisible(false);
        btTagFromTable.setVisible(false);
        tfDestinationDirectory.setVisible(false);
    }

    private void doSavePositions() {
        for (int i = 0; i < fileTableModel.getRowCount(); i++) {
            ImageData img = fileTableModel.getImageData(i);
            String p = img.getPath();
            int ptIndex = p.lastIndexOf('.');
            String newPath;
            newPath = p.substring(0, ptIndex);
            newPath += "_tagged";
            newPath += p.substring(ptIndex);
            img.writeImage(newPath, 0);
        }
        ;
        // doLogConversion(getSelectedFormat(cbFormat.getSelectedItem().toString()));
        // c.doLogConversion(Model.GMAP_LOGTYPE);
    }

    private void selectImages() {
        javax.swing.JFileChooser ImageFileChooser;
        // File f= getRawLogFilePath();
        ImageFileChooser = new javax.swing.JFileChooser();

        // RawLogFileChooser.setSelectedFile(f);
        // f=null;
        // getRawLogFilePath();
        // if (curDir.exists()) {
        // RawLogFileChooser.setCurrentDirectory(getRawLogFilePath());
        // }
        ImageFileChooser.setCurrentDirectory(new File(m
                .getStringOpt(Model.IMAGEDIR)));
        ImageFileChooser.setAcceptAllFileFilterUsed(true);
        ImageFileChooser.addChoosableFileFilter(new JpgFileFilter());
        // RawLogFileChooser.addChoosableFileFilter(new CSVFileFilter());
        // RawLogFileChooser.addChoosableFileFilter(new HoluxTRLFileFilter());
        // RawLogFileChooser.addChoosableFileFilter(new NMEAFileFilter());
        // RawLogFileChooser.addChoosableFileFilter(new DPL700FileFilter());
        // KnownFileFilter ff = new KnownFileFilter();
        // RawLogFileChooser.addChoosableFileFilter(ff);
        // RawLogFileChooser.setFileFilter(ff);
        ImageFileChooser.setMultiSelectionEnabled(true);
        if (ImageFileChooser.showDialog(this, getString("SelectFilesToTag")) == JFileChooser.APPROVE_OPTION) {
            try {
                String path;
                File[] files = ImageFileChooser.getSelectedFiles();
                for (int i = 0; i < files.length; i++) {
                    fileTableModel.add(files[i].getCanonicalPath());
                }
                c.setStringOpt(Model.IMAGEDIR, ImageFileChooser
                        .getCurrentDirectory().getCanonicalPath());
                c.setUserWayPoints(fileTableModel.getSortedGPSRecords());
            } catch (Exception e) {
                Generic.debug(getString("FilesToTagFileChooser"), e);
            }
            // tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
            // tfRawLogFilePath.setCaretPosition(tfRawLogFilePath.getText().length());
        }
    }

    private final String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    private FileTableModel fileTableModel;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        spValues = new javax.swing.JScrollPane();
        tbImageList = new javax.swing.JTable();
        btnPanel = new javax.swing.JPanel();
        lbInformationMessage = new javax.swing.JLabel();
        btSelectImages = new javax.swing.JButton();
        btSelectDestinationDir = new javax.swing.JButton();
        tfDestinationDirectory = new javax.swing.JTextField();
        btClearList = new javax.swing.JButton();
        btTagFromTable = new javax.swing.JButton();
        btTagFromFile = new javax.swing.JButton();
        btSaveTaggedFiles = new javax.swing.JButton();

        tbImageList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbImageList.setColumnSelectionAllowed(true);
        spValues.setViewportView(tbImageList);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        lbInformationMessage.setText(bundle.getString("ImageTablePanel.lbInformationMessage.text")); // NOI18N

        btSelectImages.setText(bundle.getString("ImageTablePanel.btSelectImages.text")); // NOI18N
        btSelectImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSelectImagesActionPerformed(evt);
            }
        });

        btSelectDestinationDir.setText(bundle.getString("ImageTablePanel.btSelectDestinationDir.text")); // NOI18N
        btSelectDestinationDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSelectDestinationDirActionPerformed(evt);
            }
        });

        tfDestinationDirectory.setText(bundle.getString("ImageTablePanel.tfDestinationDirectory.text")); // NOI18N

        btClearList.setText(bundle.getString("ImageTablePanel.btClearList.text")); // NOI18N
        btClearList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btClearListActionPerformed(evt);
            }
        });

        btTagFromTable.setText(bundle.getString("ImageTablePanel.btTagFromTable.text")); // NOI18N
        btTagFromTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTagFromTableActionPerformed(evt);
            }
        });

        btTagFromFile.setText(bundle.getString("ImageTablePanel.btTagFromFile.text")); // NOI18N
        btTagFromFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTagFromFileActionPerformed(evt);
            }
        });

        btSaveTaggedFiles.setText(bundle.getString("FileTablePanel.btSaveTaggedFiles.text")); // NOI18N
        btSaveTaggedFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveTaggedFilesActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout btnPanelLayout = new org.jdesktop.layout.GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
            btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(btnPanelLayout.createSequentialGroup()
                .add(btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnPanelLayout.createSequentialGroup()
                        .add(btSelectImages)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btSelectDestinationDir)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tfDestinationDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
                    .add(btnPanelLayout.createSequentialGroup()
                        .add(btClearList)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btTagFromTable)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btTagFromFile)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btSaveTaggedFiles))
                    .add(lbInformationMessage))
                .addContainerGap())
        );
        btnPanelLayout.setVerticalGroup(
            btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(btnPanelLayout.createSequentialGroup()
                .add(btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btSelectImages)
                    .add(btSelectDestinationDir)
                    .add(tfDestinationDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btClearList)
                    .add(btTagFromTable)
                    .add(btTagFromFile)
                    .add(btSaveTaggedFiles))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbInformationMessage)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(btnPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(spValues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(spValues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }//GEN-END:initComponents

private void btSelectImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectImagesActionPerformed
                selectImages();

}//GEN-LAST:event_btSelectImagesActionPerformed

private void btSelectDestinationDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectDestinationDirActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btSelectDestinationDirActionPerformed

private void btClearListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btClearListActionPerformed
                fileTableModel.clear();
}//GEN-LAST:event_btClearListActionPerformed

private void btTagFromTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTagFromTableActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btTagFromTableActionPerformed

private void btTagFromFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTagFromFileActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_btTagFromFileActionPerformed

private void btSaveTaggedFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveTaggedFilesActionPerformed
                doSavePositions();
}//GEN-LAST:event_btSaveTaggedFilesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClearList;
    private javax.swing.JButton btSaveTaggedFiles;
    private javax.swing.JButton btSelectDestinationDir;
    private javax.swing.JButton btSelectImages;
    private javax.swing.JButton btTagFromFile;
    private javax.swing.JButton btTagFromTable;
    private javax.swing.JPanel btnPanel;
    private javax.swing.JLabel lbInformationMessage;
    private javax.swing.JScrollPane spValues;
    private javax.swing.JTable tbImageList;
    private javax.swing.JTextField tfDestinationDirectory;
    // End of variables declaration//GEN-END:variables

}