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

import java.awt.FontMetrics;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.sf.bt747.j2se.app.filefilters.JpgFileFilter;
import net.sf.bt747.j2se.app.filefilters.KnownFileFilter;

import bt747.j2se_view.helpers.TaggedFilePathFactory;
import bt747.j2se_view.model.BT747Waypoint;
import bt747.j2se_view.model.MapWaypoint;
import bt747.j2se_view.model.FileTableModel;
import bt747.j2se_view.model.ImageData;
import bt747.j2se_view.model.PositionData.UserWayPointListModel;
import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Generic;

/**
 * 
 * @author Mario
 */
@SuppressWarnings("serial")
public class FileTablePanel extends javax.swing.JPanel implements
        ModelListener {

    /** Creates new form FileTablePanel. */
    public FileTablePanel() {
        initComponents();
        lbBusySpinner.setVisible(false);
    }
    
    private J2SEAppController c;
    private J2SEAppModel m;

    private UserWayPointListModel wpListModel;

    public final void init(final J2SEAppController pC) {
        c = pC;
        m = c.getAppModel();
        m.addListener(this);

        wpListModel = m.getPositionData().getWaypointListModel();
        fileTableModel = new FileTableModel(wpListModel);
        tbImageList.setModel(fileTableModel);
        tbImageList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setupSelectionListener(tbImageList);
        // m.addListener(this);
        final FontMetrics fm = tbImageList.getFontMetrics(tbImageList.getFont());
        for (int i = tbImageList.getColumnCount() - 1; i >= 0; i--) {
            final TableColumn col = tbImageList.getColumnModel().getColumn(i);
            col.setPreferredWidth(fileTableModel.getPreferredWidth(fm, i) + 4);
        }

        new TagFilePopupMenu(this,tbImageList);
        // dt = new DropTarget(tbImageList,this);
        // tbImageList.setDropTarget(dt);

        btSelectDestinationDir.setVisible(false);
        // btTagFromFile.setVisible(false);
        btTagFromTable.setVisible(false);
        tfDestinationDirectory.setVisible(false);

        int offset = m.getIntOpt(AppSettings.FILETIMEOFFSET);
        spTimeOffsetHours.setValue((offset / (3600)));
        if (offset < 0) {
            offset = -offset;
        }
        offset %= 3600;
        spTimeOffsetMinutes.setValue((offset / 60));
        spTimeOffsetSeconds.setValue(offset % 60);

        tfMaxTimeDiff.setText(""
                + m.getIntOpt(AppSettings.TAG_MAXTIMEDIFFERENCE));
        cbOverridePositions.setSelected(m
                .getBooleanOpt(AppSettings.TAG_OVERRIDEPOSITIONS));
        updateGuiData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public void modelEvent(final ModelEvent e) {
        // TODO Auto-generated method stub
        switch (e.getType()) {
        case ModelEvent.CONVERSION_STARTED:
            lbBusySpinner.setVisible(true);
            lbBusySpinner.setEnabled(true);
            lbBusySpinner.setBusy(true);
            lbBusySpinner.repaint();
            break;
        case ModelEvent.CONVERSION_ENDED:
            lbBusySpinner.setVisible(false);
            lbBusySpinner.setBusy(false);
            lbBusySpinner.repaint();
            break;
        default:
            break;
        }
    }

    private final static String OPT_INPLACE_TAGGING = "TAG_INPLACE";
    private final static String OPT_RENAME_TAGGING = "TAG_RENAME";
    private final static String SAMPLE_TEMPLATE = "TAG_EXAMPLETEMPLATE";


    private void updateGuiData() {
        cbTargetTemplate.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
                    getString(OPT_INPLACE_TAGGING), getString(OPT_RENAME_TAGGING),
                    getString(SAMPLE_TEMPLATE)}));
        cbTargetTemplate.setSelectedItem(getString(OPT_INPLACE_TAGGING));
        setTargetTemplateComboBox();
    }
    
    private void updateTargetTemplate() {
        String s = (String) cbTargetTemplate.getSelectedItem();
        String t;
        if (s.equals(getString(OPT_INPLACE_TAGGING))) {
            t = "%p%f%e";
        } else if (s.equals(getString(OPT_RENAME_TAGGING))) {
            t = "%p%f_tagged%e";
        } else {
            t = s;
        }       
        c.setStringOpt(J2SEAppModel.TAGGEDFILE_TEMPLATE, t);
    }
    
    private void setTargetTemplateComboBox() {
        String t = m.getStringOpt(J2SEAppModel.TAGGEDFILE_TEMPLATE);
        if(t.equals("%p%f%e")) {
            cbTargetTemplate.setSelectedItem(getString(OPT_INPLACE_TAGGING));
        } else if(t.equals("%p%f_tagged%e")) {
            cbTargetTemplate.setSelectedItem(getString(OPT_RENAME_TAGGING));
        } else {
            cbTargetTemplate.setSelectedItem(t);
        }
    }
    
    private void doSavePositions() {

        for (final MapWaypoint w : m.getPositionData().getUserWayPoints()) {
            try {
                final BT747Waypoint wpt = w.getBT747Waypoint(); 
                if (wpt instanceof ImageData) {
                    final ImageData id = (ImageData) wpt;
                    final TaggedFilePathFactory fpf = new TaggedFilePathFactory();
                    final String t = m.getStringOpt(J2SEAppModel.TAGGEDFILE_TEMPLATE);
                    fpf.setDestTemplate(t);

                    J2SEController.tagImage(fpf, id);
                }
            } catch (Exception e) {
                Generic.debug("Problem while converting", e);
            }
        }
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
                .getStringOpt(AppSettings.IMAGEDIR)));
        ImageFileChooser.addChoosableFileFilter(new JpgFileFilter());
        ImageFileChooser.addChoosableFileFilter(new KnownFileFilter());
        ImageFileChooser.setAcceptAllFileFilterUsed(true);
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
                final File[] files = ImageFileChooser.getSelectedFiles();
                c.setStringOpt(AppSettings.IMAGEDIR, ImageFileChooser
                        .getCurrentDirectory().getCanonicalPath());
                m.getPositionData().addFiles(files);
            } catch (final Exception e) {
                Generic.debug(getString("FilesToTagFileChooser"), e);
            }
            // tfRawLogFilePath.setText(m.getStringOpt(AppSettings.LOGFILEPATH));
            // tfRawLogFilePath.setCaretPosition(tfRawLogFilePath.getText().length());
        }
    }

    private final void updateOffset() {
        int offset;
        final int hourOffset = 3600 * ((Integer) spTimeOffsetHours.getValue())
                .intValue();

        offset = ((Integer) (spTimeOffsetMinutes.getValue())).intValue() * 60
                + ((Integer) (spTimeOffsetSeconds.getValue())).intValue();
        if (hourOffset < 0) {
            offset = hourOffset - offset;
        } else {
            offset += hourOffset;
        }
        c.setIntOpt(AppSettings.FILETIMEOFFSET, offset);
    }

    private final String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    private FileTableModel fileTableModel;

    private void setupSelectionListener(final JTable table) {
        SelectionListener listener = new SelectionListener(table);
        table.getSelectionModel().addListSelectionListener(listener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(
                listener);

        // TODO: should listen to selection model changes ...
    }

    private Object previousSelected;
    
    public class SelectionListener implements ListSelectionListener {
        JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table) {
            this.table = table;
        }

        public void valueChanged(ListSelectionEvent e) {
            // If cell selection is enabled, both row and column change events
            // are fired
            if (e.getSource() == table.getSelectionModel()
                    && table.getRowSelectionAllowed()) {
                // Column selection changed
                //int first = e.getFirstIndex();
                //int last = e.getLastIndex();
                int selected = table.getSelectedRow();
                Object wp = wpListModel.getElementAt(selected);
                if(previousSelected!=wp) {
                    if(previousSelected!=null) {
                        visitSetSelected(previousSelected, false);
                    }
                    visitSetSelected(wp, true);
                    previousSelected = wp;
                }

            } else if (e.getSource() == table.getColumnModel()
                    .getSelectionModel()
                    && table.getColumnSelectionAllowed()) {
                // Row selection changed
                int first = e.getFirstIndex();
                int last = e.getLastIndex();                
            }
            
            if (e.getValueIsAdjusting()) {
                // The mouse button has not yet been released
            }
        }
    }
    
    private void visitSetSelected(Object o, final boolean selected) {
        try {
            Method m = o.getClass().getMethod("setSelected", boolean.class);
            m.invoke(o, selected);
        } catch (IllegalArgumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }
    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        spValues = new javax.swing.JScrollPane();
        tbImageList = new javax.swing.JTable();
        btnPanel = new javax.swing.JPanel();
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
        jPanel1 = new javax.swing.JPanel();
        cbTargetTemplate = new javax.swing.JComboBox();
        lbBusySpinner = new org.jdesktop.swingx.JXBusyLabel();
        btSaveTaggedFiles = new javax.swing.JButton();
        btTagFromFile = new javax.swing.JButton();
        btSelectDestinationDir = new javax.swing.JButton();
        btClearList = new javax.swing.JButton();
        btSelectImages = new javax.swing.JButton();
        btTagFromTable = new javax.swing.JButton();
        tfDestinationDirectory = new javax.swing.JTextField();

        tbImageList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbImageList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbImageList.setColumnSelectionAllowed(true);
        spValues.setViewportView(tbImageList);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle"); // NOI18N
        pnTimeOffset.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FileTablePanel.pnTimeOffset.border.title"))); // NOI18N
        pnTimeOffset.setToolTipText(bundle.getString("FileTablePanel.pnTimeOffset.toolTipText")); // NOI18N

        lbMinutes.setText(bundle.getString("FileTablePanel.lbMinutes.text")); // NOI18N

        lbHour.setText(bundle.getString("FileTablePanel.lbHour.text")); // NOI18N

        lbSeconds.setText(bundle.getString("FileTablePanel.lbSeconds.text")); // NOI18N

        spTimeOffsetHours.setModel(new javax.swing.SpinnerNumberModel(0, -48, 48, 1));
        spTimeOffsetHours.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spTimeOffsetHoursStateChanged(evt);
            }
        });

        spTimeOffsetMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spTimeOffsetMinutes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spTimeOffsetMinutesStateChanged(evt);
            }
        });

        spTimeOffsetSeconds.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spTimeOffsetSeconds.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spTimeOffsetSecondsStateChanged(evt);
            }
        });

        jLabel1.setText(bundle.getString("FileTablePanel.jLabel1.text")); // NOI18N

        tfMaxTimeDiff.setText(bundle.getString("FileTablePanel.tfMaxTimeDiff.text")); // NOI18N
        tfMaxTimeDiff.setToolTipText(bundle.getString("FileTablePanel.tfMaxTimeDiff.toolTipText")); // NOI18N
        tfMaxTimeDiff.setInputVerifier(J2SEAppController.IntVerifier);
        tfMaxTimeDiff.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfMaxTimeDiffFocusLost(evt);
            }
        });

        lbSeconds1.setText(bundle.getString("FileTablePanel.lbSeconds1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout pnTimeOffsetLayout = new org.jdesktop.layout.GroupLayout(pnTimeOffset);
        pnTimeOffset.setLayout(pnTimeOffsetLayout);
        pnTimeOffsetLayout.setHorizontalGroup(
            pnTimeOffsetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTimeOffsetLayout.createSequentialGroup()
                .add(spTimeOffsetHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbHour, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTimeOffsetMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbMinutes)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spTimeOffsetSeconds, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbSeconds))
            .add(pnTimeOffsetLayout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tfMaxTimeDiff, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbSeconds1))
        );
        pnTimeOffsetLayout.setVerticalGroup(
            pnTimeOffsetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnTimeOffsetLayout.createSequentialGroup()
                .add(pnTimeOffsetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(spTimeOffsetHours, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbHour)
                    .add(spTimeOffsetMinutes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbMinutes)
                    .add(spTimeOffsetSeconds, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbSeconds))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTimeOffsetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(lbSeconds1)
                    .add(tfMaxTimeDiff, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        pnTagOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FileTablePanel.pnTagOptions.border.title"))); // NOI18N

        cbOverridePositions.setText(bundle.getString("FileTablePanel.cbOverridePositions.text")); // NOI18N
        cbOverridePositions.setToolTipText(bundle.getString("FileTablePanel.cbOverridePositions.toolTipText")); // NOI18N
        cbOverridePositions.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbOverridePositionsStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnTagOptionsLayout = new org.jdesktop.layout.GroupLayout(pnTagOptions);
        pnTagOptions.setLayout(pnTagOptionsLayout);
        pnTagOptionsLayout.setHorizontalGroup(
            pnTagOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbOverridePositions)
        );
        pnTagOptionsLayout.setVerticalGroup(
            pnTagOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbOverridePositions)
        );

        cbTargetTemplate.setEditable(true);
        cbTargetTemplate.setToolTipText(bundle.getString("FileTablePanel.cbTargetTemplate.toolTipText")); // NOI18N
        cbTargetTemplate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbTargetTemplateItemStateChanged(evt);
            }
        });

        lbBusySpinner.setText(bundle.getString("FileTablePanel.lbBusySpinner.text")); // NOI18N
        lbBusySpinner.setOpaque(true);

        btSaveTaggedFiles.setText(bundle.getString("FileTablePanel.btSaveTaggedFiles.text")); // NOI18N
        btSaveTaggedFiles.setToolTipText(bundle.getString("FileTablePanel.btSaveTaggedFiles.toolTipText")); // NOI18N
        btSaveTaggedFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveTaggedFilesActionPerformed(evt);
            }
        });

        btTagFromFile.setText(bundle.getString("ImageTablePanel.btTagFromFile.text")); // NOI18N
        btTagFromFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTagFromFileActionPerformed(evt);
            }
        });

        btSelectDestinationDir.setText(bundle.getString("ImageTablePanel.btSelectDestinationDir.text")); // NOI18N
        btSelectDestinationDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSelectDestinationDirActionPerformed(evt);
            }
        });

        btClearList.setText(bundle.getString("ImageTablePanel.btClearList.text")); // NOI18N
        btClearList.setToolTipText(bundle.getString("FileTablePanel.btClearList.toolTipText")); // NOI18N
        btClearList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btClearListActionPerformed(evt);
            }
        });

        btSelectImages.setText(bundle.getString("ImageTablePanel.btSelectImages.text")); // NOI18N
        btSelectImages.setToolTipText(bundle.getString("FileTablePanel.btSelectImages.toolTipText")); // NOI18N
        btSelectImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSelectImagesActionPerformed(evt);
            }
        });

        btTagFromTable.setText(bundle.getString("ImageTablePanel.btTagFromTable.text")); // NOI18N
        btTagFromTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btTagFromTableActionPerformed(evt);
            }
        });

        tfDestinationDirectory.setText(bundle.getString("ImageTablePanel.tfDestinationDirectory.text")); // NOI18N
        tfDestinationDirectory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfDestinationDirectoryFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(btTagFromFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lbBusySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btTagFromTable)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbTargetTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(btSelectImages)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btClearList)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btSaveTaggedFiles))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(btSelectDestinationDir)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tfDestinationDirectory)))
                .add(105, 105, 105))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btSelectImages)
                    .add(btClearList)
                    .add(btSaveTaggedFiles))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btSelectDestinationDir)
                    .add(tfDestinationDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(btTagFromFile)
                        .add(btTagFromTable)
                        .add(cbTargetTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lbBusySpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        org.jdesktop.layout.GroupLayout btnPanelLayout = new org.jdesktop.layout.GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
            btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, btnPanelLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(pnTagOptions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnTimeOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        btnPanelLayout.setVerticalGroup(
            btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(btnPanelLayout.createSequentialGroup()
                .add(btnPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnTagOptions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pnTimeOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(0, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(btnPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(spValues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(spValues, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }//GEN-END:initComponents

    private void cbOverridePositionsStateChanged(
            final javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbOverridePositionsStateChanged
        c.setBooleanOpt(AppSettings.TAG_OVERRIDEPOSITIONS,
                cbOverridePositions.isSelected());
    }//GEN-LAST:event_cbOverridePositionsStateChanged

    private void spTimeOffsetHoursStateChanged(
            final javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spTimeOffsetHoursStateChanged
        updateOffset();
    }//GEN-LAST:event_spTimeOffsetHoursStateChanged

    private void spTimeOffsetMinutesStateChanged(
            final javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spTimeOffsetMinutesStateChanged
        updateOffset();
    }//GEN-LAST:event_spTimeOffsetMinutesStateChanged

    private void tfMaxTimeDiffFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMaxTimeDiffFocusLost
        c.setIntOpt(AppSettings.TAG_MAXTIMEDIFFERENCE, Integer.valueOf(
                tfMaxTimeDiff.getText()).intValue());
    }//GEN-LAST:event_tfMaxTimeDiffFocusLost

    private void spTimeOffsetSecondsStateChanged(
            final javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spTimeOffsetSecondsStateChanged
        updateOffset();
    }//GEN-LAST:event_spTimeOffsetSecondsStateChanged

    private void btSelectImagesActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectImagesActionPerformed
        selectImages();

    }//GEN-LAST:event_btSelectImagesActionPerformed

    private void btSelectDestinationDirActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectDestinationDirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btSelectDestinationDirActionPerformed

    private void btClearListActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btClearListActionPerformed
        fileTableModel.clear();
    }//GEN-LAST:event_btClearListActionPerformed

    private void btTagFromTableActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTagFromTableActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btTagFromTableActionPerformed

    private void btTagFromFileActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btTagFromFileActionPerformed
        c.setChangeToMap(false);
        c.setLogConversionParameters();
        c.doLogConversion(Model.ARRAY_LOGTYPE);
    }//GEN-LAST:event_btTagFromFileActionPerformed

    private void btSaveTaggedFilesActionPerformed(
            final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveTaggedFilesActionPerformed
        doSavePositions();
    }//GEN-LAST:event_btSaveTaggedFilesActionPerformed

    private void tfDestinationDirectoryFocusLost(
            final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfDestinationDirectoryFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDestinationDirectoryFocusLost

    private void cbTargetTemplateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbTargetTemplateItemStateChanged
        updateTargetTemplate();
    }//GEN-LAST:event_cbTargetTemplateItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClearList;
    private javax.swing.JButton btSaveTaggedFiles;
    private javax.swing.JButton btSelectDestinationDir;
    private javax.swing.JButton btSelectImages;
    private javax.swing.JButton btTagFromFile;
    private javax.swing.JButton btTagFromTable;
    private javax.swing.JPanel btnPanel;
    private javax.swing.JCheckBox cbOverridePositions;
    private javax.swing.JComboBox cbTargetTemplate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXBusyLabel lbBusySpinner;
    private javax.swing.JLabel lbHour;
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
