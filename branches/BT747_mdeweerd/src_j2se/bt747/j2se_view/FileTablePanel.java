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

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.iharder.dnd.DropListener;
import net.iharder.dnd.FileDrop;
import net.sf.bt747.j2se.app.filefilters.JpgFileFilter;

import bt747.j2se_view.image.ImageData;
import bt747.sys.Generic;

/**
 * 
 * @author Mario
 */
public class FileTablePanel extends javax.swing.JPanel {

    /** Creates new form FileTablePanel */
    public FileTablePanel() {
        initComponents();
    }

    private J2SEAppController c;
    private J2SEAppModel m;

    private FileDrop fd;
    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getAppModel();

        fileTableModel = new FileTableModel();
        tbImageList.setModel(fileTableModel);
        // m.addListener(this);

        // dt = new DropTarget(tbImageList,this);
        // tbImageList.setDropTarget(dt);

        btSelectDestinationDir.setVisible(false);
        btTagFromFile.setVisible(false);
        btTagFromTable.setVisible(false);
        tfDestinationDirectory.setVisible(false);

        int offset = m.getIntOpt(J2SEAppModel.FILETIMEOFFSET);
        spTimeOffsetHours.setValue((int) (offset / (3600)));
        if (offset < 0) {
            offset = -offset;
        }
        offset %= 3600;
        spTimeOffsetMinutes.setValue((int) (offset / 60));
        spTimeOffsetSeconds.setValue(offset % 60);

        tfMaxTimeDiff.setText("" + m.getIntOpt(J2SEAppModel.TAG_MAXTIMEDIFFERENCE));
        cbOverridePositions.setSelected(m
                .getBooleanOpt(J2SEAppModel.TAG_OVERRIDEPOSITIONS));

        DropListener dl;
        dl = new DropListener() {
            /* (non-Javadoc)
             * @see net.iharder.dnd.FileDrop.Listener#filesDropped(java.io.File[])
             */
            public void filesDropped(final java.io.File[] files) {
                addFiles(files);
            }
        };
        fd = new FileDrop((Component) this, dl);
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
        // c.doLogConversion(J2SEAppModel.GMAP_LOGTYPE);
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
                .getStringOpt(J2SEAppModel.IMAGEDIR)));
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
                c.setStringOpt(J2SEAppModel.IMAGEDIR, ImageFileChooser
                        .getCurrentDirectory().getCanonicalPath());
                addFiles(files);
            } catch (Exception e) {
                Generic.debug(getString("FilesToTagFileChooser"), e);
            }
            // tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
            // tfRawLogFilePath.setCaretPosition(tfRawLogFilePath.getText().length());
        }
    }
    
    private final void addFiles(File[] files) {
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                try {
                fileTableModel.add(files[i].getCanonicalPath());
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
            m.setUserWayPoints(fileTableModel.getSortedGPSRecords());
        }
    }

    private final void updateOffset() {
        int offset;
        int hourOffset = 3600 * ((Integer) spTimeOffsetHours.getValue())
                .intValue();

        offset = ((Integer) (spTimeOffsetMinutes.getValue())).intValue() * 60
                + ((Integer) (spTimeOffsetSeconds.getValue())).intValue();
        if (hourOffset < 0) {
            offset = hourOffset - offset;
        } else {
            offset += hourOffset;
        }
        c.setIntOpt(J2SEAppModel.FILETIMEOFFSET, offset);
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
        pnTimeOffset = new javax.swing.JPanel();
        lbMinutes = new javax.swing.JLabel();
        lbHour = new javax.swing.JLabel();
        lbSeconds = new javax.swing.JLabel();
        spTimeOffsetHours = new javax.swing.JSpinner();
        spTimeOffsetMinutes = new javax.swing.JSpinner();
        spTimeOffsetSeconds = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        tfMaxTimeDiff = new javax.swing.JTextField();
        lbSeconds1 = new javax.swing.JLabel();
        pnTagOptions = new javax.swing.JPanel();
        cbOverridePositions = new javax.swing.JCheckBox();

        tbImageList.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] { { null, null, null, null },
                        { null, null, null, null }, { null, null, null, null },
                        { null, null, null, null } }, new String[] { "Title 1",
                        "Title 2", "Title 3", "Title 4" }));
        tbImageList
                .setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbImageList.setColumnSelectionAllowed(true);
        spValues.setViewportView(tbImageList);

        java.util.ResourceBundle bundle = java.util.ResourceBundle
                .getBundle("bt747/j2se_view/Bundle"); // NOI18N
        lbInformationMessage.setText(bundle
                .getString("ImageTablePanel.lbInformationMessage.text")); // NOI18N

        btSelectImages.setText(bundle
                .getString("ImageTablePanel.btSelectImages.text")); // NOI18N
        btSelectImages.setToolTipText(bundle
                .getString("FileTablePanel.btSelectImages.toolTipText")); // NOI18N
        btSelectImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSelectImagesActionPerformed(evt);
            }
        });

        btSelectDestinationDir.setText(bundle
                .getString("ImageTablePanel.btSelectDestinationDir.text")); // NOI18N
        btSelectDestinationDir
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btSelectDestinationDirActionPerformed(evt);
                    }
                });

        tfDestinationDirectory.setText(bundle
                .getString("ImageTablePanel.tfDestinationDirectory.text")); // NOI18N
        tfDestinationDirectory
                .addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        tfDestinationDirectoryFocusLost(evt);
                    }
                });

        btClearList.setText(bundle
                .getString("ImageTablePanel.btClearList.text")); // NOI18N
        btClearList.setToolTipText(bundle
                .getString("FileTablePanel.btClearList.toolTipText")); // NOI18N
        btClearList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btClearListActionPerformed(evt);
            }
        });

        btTagFromTable.setText(bundle
                .getString("ImageTablePanel.btTagFromTable.text")); // NOI18N
        btTagFromTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTagFromTableActionPerformed(evt);
            }
        });

        btTagFromFile.setText(bundle
                .getString("ImageTablePanel.btTagFromFile.text")); // NOI18N
        btTagFromFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTagFromFileActionPerformed(evt);
            }
        });

        btSaveTaggedFiles.setText(bundle
                .getString("FileTablePanel.btSaveTaggedFiles.text")); // NOI18N
        btSaveTaggedFiles.setToolTipText(bundle
                .getString("FileTablePanel.btSaveTaggedFiles.toolTipText")); // NOI18N
        btSaveTaggedFiles
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        btSaveTaggedFilesActionPerformed(evt);
                    }
                });

        pnTimeOffset
                .setBorder(javax.swing.BorderFactory.createTitledBorder(bundle
                        .getString("FileTablePanel.pnTimeOffset.border.title"))); // NOI18N
        pnTimeOffset.setToolTipText(bundle
                .getString("FileTablePanel.pnTimeOffset.toolTipText")); // NOI18N

        lbMinutes.setText(bundle.getString("FileTablePanel.lbMinutes.text")); // NOI18N

        lbHour.setText(bundle.getString("FileTablePanel.lbHour.text")); // NOI18N

        lbSeconds.setText(bundle.getString("FileTablePanel.lbSeconds.text")); // NOI18N

        spTimeOffsetHours.setModel(new javax.swing.SpinnerNumberModel(0, -48,
                48, 1));
        spTimeOffsetHours
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        spTimeOffsetHoursStateChanged(evt);
                    }
                });

        spTimeOffsetMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0,
                59, 1));
        spTimeOffsetMinutes
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        spTimeOffsetMinutesStateChanged(evt);
                    }
                });

        spTimeOffsetSeconds.setModel(new javax.swing.SpinnerNumberModel(0, 0,
                59, 1));
        spTimeOffsetSeconds
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        spTimeOffsetSecondsStateChanged(evt);
                    }
                });

        jLabel1.setText(bundle.getString("FileTablePanel.jLabel1.text")); // NOI18N

        tfMaxTimeDiff.setText(bundle
                .getString("FileTablePanel.tfMaxTimeDiff.text")); // NOI18N
        tfMaxTimeDiff.setToolTipText(bundle
                .getString("FileTablePanel.tfMaxTimeDiff.toolTipText")); // NOI18N
        tfMaxTimeDiff.setInputVerifier(J2SEAppController.IntVerifier);
        tfMaxTimeDiff.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfMaxTimeDiffFocusLost(evt);
            }
        });

        lbSeconds1.setText(bundle.getString("FileTablePanel.lbSeconds1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnTimeOffsetLayout = new org.jdesktop.layout.GroupLayout(
                pnTimeOffset);
        pnTimeOffset.setLayout(pnTimeOffsetLayout);
        pnTimeOffsetLayout
                .setHorizontalGroup(pnTimeOffsetLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnTimeOffsetLayout
                                        .createSequentialGroup()
                                        .add(
                                                spTimeOffsetHours,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                lbHour,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                12,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                spTimeOffsetMinutes,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(lbMinutes)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                spTimeOffsetSeconds,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(lbSeconds))
                        .add(
                                pnTimeOffsetLayout
                                        .createSequentialGroup()
                                        .add(jLabel1)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                tfMaxTimeDiff,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                90, Short.MAX_VALUE)
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(lbSeconds1)));
        pnTimeOffsetLayout
                .setVerticalGroup(pnTimeOffsetLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                pnTimeOffsetLayout
                                        .createSequentialGroup()
                                        .add(
                                                pnTimeOffsetLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(
                                                                spTimeOffsetHours,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(lbHour)
                                                        .add(
                                                                spTimeOffsetMinutes,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(lbMinutes)
                                                        .add(
                                                                spTimeOffsetSeconds,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(lbSeconds))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                pnTimeOffsetLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                        .add(jLabel1)
                                                        .add(lbSeconds1)
                                                        .add(
                                                                tfMaxTimeDiff,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))));

        pnTagOptions
                .setBorder(javax.swing.BorderFactory.createTitledBorder(bundle
                        .getString("FileTablePanel.pnTagOptions.border.title"))); // NOI18N

        cbOverridePositions.setText(bundle
                .getString("FileTablePanel.cbOverridePositions.text")); // NOI18N
        cbOverridePositions.setToolTipText(bundle
                .getString("FileTablePanel.cbOverridePositions.toolTipText")); // NOI18N
        cbOverridePositions
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        cbOverridePositionsStateChanged(evt);
                    }
                });

        org.jdesktop.layout.GroupLayout pnTagOptionsLayout = new org.jdesktop.layout.GroupLayout(
                pnTagOptions);
        pnTagOptions.setLayout(pnTagOptionsLayout);
        pnTagOptionsLayout.setHorizontalGroup(pnTagOptionsLayout
                .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(cbOverridePositions));
        pnTagOptionsLayout.setVerticalGroup(pnTagOptionsLayout
                .createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(cbOverridePositions));

        org.jdesktop.layout.GroupLayout btnPanelLayout = new org.jdesktop.layout.GroupLayout(
                btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout
                .setHorizontalGroup(btnPanelLayout
                        .createParallelGroup(
                                org.jdesktop.layout.GroupLayout.LEADING)
                        .add(
                                btnPanelLayout
                                        .createSequentialGroup()
                                        .add(
                                                btnPanelLayout
                                                        .createParallelGroup(
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                btnPanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                btTagFromFile)
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                btTagFromTable))
                                                        .add(
                                                                btnPanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                btnPanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                false)
                                                                                        .add(
                                                                                                btnPanelLayout
                                                                                                        .createSequentialGroup()
                                                                                                        .add(
                                                                                                                btSelectImages)
                                                                                                        .addPreferredGap(
                                                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                        .add(
                                                                                                                btClearList)
                                                                                                        .addPreferredGap(
                                                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                        .add(
                                                                                                                btSaveTaggedFiles))
                                                                                        .add(
                                                                                                btnPanelLayout
                                                                                                        .createSequentialGroup()
                                                                                                        .add(
                                                                                                                btSelectDestinationDir)
                                                                                                        .addPreferredGap(
                                                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                        .add(
                                                                                                                tfDestinationDirectory)))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                pnTagOptions,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(
                                                pnTimeOffset,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(lbInformationMessage));
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
                                                                org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(
                                                                btnPanelLayout
                                                                        .createSequentialGroup()
                                                                        .add(
                                                                                btnPanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
                                                                                        .add(
                                                                                                btSelectImages)
                                                                                        .add(
                                                                                                btClearList)
                                                                                        .add(
                                                                                                btSaveTaggedFiles))
                                                                        .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                        .add(
                                                                                btnPanelLayout
                                                                                        .createParallelGroup(
                                                                                                org.jdesktop.layout.GroupLayout.BASELINE)
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
                                                                                        .add(
                                                                                                btTagFromFile)
                                                                                        .add(
                                                                                                btTagFromTable)))
                                                        .add(
                                                                pnTagOptions,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(
                                                                pnTimeOffset,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(lbInformationMessage)));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(10, 10, 10).add(btnPanel,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
                spValues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 614,
                Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                layout.createSequentialGroup().add(spValues,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154,
                        Short.MAX_VALUE).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED).add(btnPanel,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
    }//GEN-END:initComponents

    private void cbOverridePositionsStateChanged(
            javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbOverridePositionsStateChanged
        c.setBooleanOpt(J2SEAppModel.TAG_OVERRIDEPOSITIONS, cbOverridePositions
                .isSelected());
    }//GEN-LAST:event_cbOverridePositionsStateChanged

    private void spTimeOffsetHoursStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spTimeOffsetHoursStateChanged
        updateOffset();
    }//GEN-LAST:event_spTimeOffsetHoursStateChanged

    private void spTimeOffsetMinutesStateChanged(
            javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spTimeOffsetMinutesStateChanged
        updateOffset();
    }//GEN-LAST:event_spTimeOffsetMinutesStateChanged

    private void tfMaxTimeDiffFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMaxTimeDiffFocusLost
        c.setIntOpt(J2SEAppModel.TAG_MAXTIMEDIFFERENCE, Integer.valueOf(
                tfMaxTimeDiff.getText()).intValue());
    }//GEN-LAST:event_tfMaxTimeDiffFocusLost

    private void spTimeOffsetSecondsStateChanged(
            javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spTimeOffsetSecondsStateChanged
        updateOffset();
    }//GEN-LAST:event_spTimeOffsetSecondsStateChanged

    private void btSelectImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectImagesActionPerformed
        selectImages();

    }//GEN-LAST:event_btSelectImagesActionPerformed

    private void btSelectDestinationDirActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectDestinationDirActionPerformed
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

    private void tfDestinationDirectoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfDestinationDirectoryFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDestinationDirectoryFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClearList;
    private javax.swing.JButton btSaveTaggedFiles;
    private javax.swing.JButton btSelectDestinationDir;
    private javax.swing.JButton btSelectImages;
    private javax.swing.JButton btTagFromFile;
    private javax.swing.JButton btTagFromTable;
    private javax.swing.JPanel btnPanel;
    private javax.swing.JCheckBox cbOverridePositions;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbHour;
    private javax.swing.JLabel lbInformationMessage;
    private javax.swing.JLabel lbMinutes;
    private javax.swing.JLabel lbSeconds;
    private javax.swing.JLabel lbSeconds1;
    private javax.swing.JPanel pnTagOptions;
    private javax.swing.JPanel pnTimeOffset;
    private javax.swing.JSpinner spTimeOffsetHours;
    private javax.swing.JSpinner spTimeOffsetMinutes;
    private javax.swing.JSpinner spTimeOffsetSeconds;
    private javax.swing.JScrollPane spValues;
    private javax.swing.JTable tbImageList;
    private javax.swing.JTextField tfDestinationDirectory;
    private javax.swing.JTextField tfMaxTimeDiff;
    // End of variables declaration//GEN-END:variables

}
