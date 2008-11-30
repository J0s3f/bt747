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

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;

import javax.swing.JFileChooser;

import bt747.j2se_view.filefilters.JpgFileFilter;
import bt747.model.Model;
import bt747.sys.Generic;

/**
 * 
 * @author Mario
 */
public class ImageTablePanel extends javax.swing.JPanel
implements DropTargetListener
{

    /**
     * 
     */
    private static final long serialVersionUID = 7184807249568014595L;
    private J2SEAppController c;
    private Model m;

    /** Creates new form ImageTablePanel */
    public ImageTablePanel() {
        initComponents();
    }

    private DropTarget dt;
    
    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getModel();

        imageTableModel = new ImageTableModel();
        tbImageList.setModel(imageTableModel);
        // m.addListener(this);
        
      
        //dt = new DropTarget(tbImageList,this);
        //tbImageList.setDropTarget(dt);

        btSelectImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectImages();
            }
        });

        btClearList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageTableModel.clear();
            }
        });

        btTagFromTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doConvert();
            }
        });

        btSelectDestinationDir.setVisible(false);
        btTagFromFile.setVisible(false);
        btTagFromTable.setVisible(false);
        tfDestinationDirectory.setVisible(false);
    }

    private void doConvert() {
        // doLogConversion(getSelectedFormat(cbFormat.getSelectedItem().toString()));
        // c.doLogConversion(Model.GMAP_LOGTYPE);
    }

    private void selectImages() {// GEN-FIRST:event_btRawLogFileActionPerformed
        javax.swing.JFileChooser ImageFileChooser;
        // File f= getRawLogFilePath();
        ImageFileChooser = new javax.swing.JFileChooser();

        // RawLogFileChooser.setSelectedFile(f);
        // f=null;
        // getRawLogFilePath();
        // if (curDir.exists()) {
        // RawLogFileChooser.setCurrentDirectory(getRawLogFilePath());
        // }
        ImageFileChooser.setCurrentDirectory(new File(m.getStringOpt(Model.IMAGEDIR)));
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
        if (ImageFileChooser.showDialog(this, getString("SelectImages")) == JFileChooser.APPROVE_OPTION) {
            try {
                String path;
                File[] files = ImageFileChooser.getSelectedFiles();
                for (int i = 0; i < files.length; i++) {
                    imageTableModel.add(files[i].getCanonicalPath());
                }
                c.setStringOpt(Model.IMAGEDIR, ImageFileChooser
                        .getCurrentDirectory().getCanonicalPath());
                c.setUserWayPoints(imageTableModel.getSortedGPSRecords());
            } catch (Exception e) {
                Generic.debug(getString("ImageFileChooser"), e);
            }
            // tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
            // tfRawLogFilePath.setCaretPosition(tfRawLogFilePath.getText().length());
        }
    }// GEN-LAST:event_btRawLogFileActionPerformed

    private final String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    
    /**
     * Drop actions.
     */
    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    public void drop(DropTargetDropEvent dtde) {
        //dtde.acceptDrop(dropAction);
        //dtde.rejectDrop();
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
        
    }

    
    private ImageTableModel imageTableModel;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {// GEN-BEGIN:initComponents

        btnPanel = new javax.swing.JPanel();
        btSelectImages = new javax.swing.JButton();
        btSelectDestinationDir = new javax.swing.JButton();
        tfDestinationDirectory = new javax.swing.JTextField();
        btClearList = new javax.swing.JButton();
        btTagFromTable = new javax.swing.JButton();
        btTagFromFile = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbImageList = new javax.swing.JTable();
        lbInformationMessage = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle
                .getBundle("bt747/j2se_view/Bundle"); // NOI18N
        btSelectImages.setText(bundle
                .getString("ImageTablePanel.btSelectImages.text")); // NOI18N

        btSelectDestinationDir.setText(bundle
                .getString("ImageTablePanel.btSelectDestinationDir.text")); // NOI18N

        tfDestinationDirectory.setText(bundle
                .getString("ImageTablePanel.tfDestinationDirectory.text")); // NOI18N

        btClearList.setText(bundle
                .getString("ImageTablePanel.btClearList.text")); // NOI18N

        btTagFromTable.setText(bundle
                .getString("ImageTablePanel.btTagFromTable.text")); // NOI18N

        btTagFromFile.setText(bundle
                .getString("ImageTablePanel.btTagFromFile.text")); // NOI18N

        org.jdesktop.layout.GroupLayout btnPanelLayout = new org.jdesktop.layout.GroupLayout(
                btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(btnPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                btnPanelLayout.createSequentialGroup().add(btSelectImages)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                btSelectDestinationDir).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                tfDestinationDirectory,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                217, Short.MAX_VALUE).addContainerGap()).add(
                btnPanelLayout.createSequentialGroup().add(btClearList)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                btTagFromTable).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                btTagFromFile)));
        btnPanelLayout
                .setVerticalGroup(btnPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                btnPanelLayout
                                        .createSequentialGroup()
                                        .add(
                                                btnPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(btSelectImages)
                                                        .add(
                                                                btSelectDestinationDir)
                                                        .add(
                                                                tfDestinationDirectory,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                btnPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(btClearList).add(
                                                                btTagFromTable)
                                                        .add(btTagFromFile))));

        tbImageList.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] { { null, null, null, null },
                        { null, null, null, null }, { null, null, null, null },
                        { null, null, null, null } }, new String[] { "Title 1",
                        "Title 2", "Title 3", "Title 4" }));
        tbImageList.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tbImageList);

        lbInformationMessage.setText(bundle
                .getString("ImageTablePanel.lbInformationMessage.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                this);
        this.setLayout(layout);
        layout
                .setHorizontalGroup(layout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                layout
                                        .createSequentialGroup()
                                        .add(
                                                layout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                12,
                                                                                12,
                                                                                12)
                                                                        .add(
                                                                                jScrollPane1,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                509,
                                                                                Short.MAX_VALUE))
                                                        .add(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .add(
                                                                                lbInformationMessage))
                                                        .add(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addContainerGap()
                                                                        .add(
                                                                                btnPanel,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)))
                                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(12, 12, 12).add(
                        jScrollPane1,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 187,
                        Short.MAX_VALUE).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED).add(
                        lbInformationMessage).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.UNRELATED).add(
                        btnPanel,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
    }// GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btSelectImages;
    private javax.swing.JButton btClearList;
    private javax.swing.JButton btSelectDestinationDir;
    private javax.swing.JButton btTagFromFile;
    private javax.swing.JButton btTagFromTable;
    private javax.swing.JPanel btnPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbInformationMessage;
    private javax.swing.JTable tbImageList;
    private javax.swing.JTextField tfDestinationDirectory;
    // End of variables declaration//GEN-END:variables


}
