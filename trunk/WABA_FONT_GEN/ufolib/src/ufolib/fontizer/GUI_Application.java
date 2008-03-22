        /*
         * Application.java
         *
         * Created on March 31, 2003, 1:05 AM
         */

        package ufolib.fontizer;

        import javax.swing.table.*;
        import javax.swing.*;
        import java.awt.*;


        /**
         *
         * @author  vera
         *
         * new java.awt.Dimension( m_panelsizeProfile - 2, m_panelsizeTableheight - 2 )  wg uff tabelle
         */
        public class GUI_Application extends javax.swing.JFrame implements ufolib.fontizer.UserInterface {

            // **************************************************************
            // implementing UserInterface

            public void preInit( Controller controller ) {
                m_controller = controller;
            }

            private boolean m_sensitiveEncoding = false;  // if the encoding combobox is event sensitve
            
            public void postInit() {
                
                // encoding converter
                String[] allc = m_controller.getCharConverterList();
                for ( int index = 0; index < allc.length; index++ ) {
                    jSrcEncoding.addItem( allc[ index ] );
                }
                m_sensitiveEncoding = true;
                
                visualSrc();
                visualPro();
                show();

                // alpha 
                //showErrorPane( "This is alpha software.\nAlmost all features are _not_ implemented yet!\nThis is just a test suite and not usable yet.", false );
                showQuick();
            }

            public void exit() {
                hide();
            }

            public void uffRangeShow( int index ) {
                // ???
            }

            // **************************************************************
            // custom

            class RangeTableModel extends javax.swing.table.AbstractTableModel {

                public RangeTableModel( boolean multiselect, boolean singleselect, GUI_Application guiapp, JTable table ) {
                    this.multiselect = multiselect;
                    this.singleselect = singleselect;
                    this.editable12 = ( multiselect && ! singleselect );
                    m_guiapp = guiapp;
                    m_table = table;
                }

                private boolean multiselect, singleselect, editable12;
                private FontRange m_range;
                private GUI_Application m_guiapp;
                private JTable m_table;

                // custom: 

                public void setRange( FontRange range, GUI_Application app ) {
                    int sold = ( m_range != null ? m_range.size() : 0 );
                    m_range = range;
                    int srange = m_range.size();
                    fireTableRowsUpdated( 0, Math.max( sold, srange ) );
                    if ( m_table != null ) {
                        m_table.setPreferredSize( new Dimension( 400, srange * m_table.getRowHeight() ) ); 
                        m_table.revalidate();
                    }
                }

                // ********************************************************************************
                // implementing Table Model

                public int getRowCount() {
                    if ( m_range != null ) {
                        return m_range.size();
                    }
                    else {
                        return 0;
                    }
                }

                public int getColumnCount() {
                    return 4;
                }

                /**
                 * called by GUI after editing cell ?!
                 */
                public void setValueAt( Object value, int row, int col ) {
                    if ( m_range != null ) {
                        int ri = col;
                        if ( value instanceof Integer ) {
                            int val = ( (Integer) value ).intValue();
                            if ( editable12 && ( 1 <= ri ) && ( ri <= 2 ) ) {
                                if ( ri == 1 ) {
                                    m_range.setRangeStart( row, val );
                                }
                                else if ( ri == 2 ) {
                                    m_range.setRangeEnd( row, val );
                                }
                            }
                        }
                    }
                    fireTableCellUpdated(row, col);
                    if ( m_guiapp != null ) {
                        m_guiapp.visualPro();
                        m_guiapp.dirtyUffRange();
                    }

                }

                public Object getValueAt( int row, int column ) {
                    //mess( 4, "getValue from Model: row " + row + ", col " + column + ", range is" + m_range ); 
                    if ( m_range == null ) {
                        return null;
                    }

                    int icol = column;
                    //mess( 4, "getValue from Model: icol " + icol );
                    int reti = 0;
                    switch ( icol ) {
                        case 0:
                            reti = row + 1;
                            break;
                        case 1:
                            reti = m_range.getRangeStart( row );
                            break;
                        case 2:
                            reti = m_range.getRangeEnd( row );
                            break;
                        case 3:
                            reti = m_range.getRangeEnd( row ) - m_range.getRangeStart( row ) + 1;
                            break;
                    }
                    if ( editable12 ) {
                        if ( ( 1 <= icol ) && ( icol <= 2 ) ) {
                            return new Integer( reti );
                        }
                        else {
                            return new Integer( reti );
                        }
                    }
                    else {
                        return new Integer( reti );
                    }
                }

                public String getColumnName( int column ) {
                    int icol = column;
                    switch ( icol ) {
                        case 0:
                            return "No.";
                        case 1:
                            return "From";
                        case 2:
                            return "To";
                        case 3:
                            return "Quantity";
                    }
                    // else:
                    return "";
                }


                public Class getColumnClass(int columnIndex) {
                    int icol = columnIndex; 
                    //mess( 4, "getColumnClass: icol " + icol );
                    if ( editable12 ) {
                        if ( ( 1 <= icol ) && ( icol <= 2 ) ) {
                            return Integer.class;
                        }
                        else {
                            return Integer.class;
                        }
                    }
                    else {
                        return Integer.class;
                    }
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    // only Profile ranges form&to are editable
                    int ri = columnIndex;
                    if ( editable12 && ( 1 <= ri ) && ( ri <= 2 ) ) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }

            // ***************************************************************************************
            // custom methods


            private void visualSrc() {
                jSrcType.setText( m_controller.getSrcDataType() );
                jSrcType.setToolTipText( m_controller.getSrcDataType() );
                jSrcFilename.setText( m_controller.getSrcDataFilename() );
                jSrcFilename.setToolTipText( m_controller.getSrcDataFilename() );
                jSrcComment.setText( m_controller.getSrcDataComment() );
                jSrcComment.setToolTipText( m_controller.getSrcDataComment() );
                jSrcFamily.setText( m_controller.getSrcDataCoreFamily() );
                jSrcFamily.setToolTipText( m_controller.getSrcDataCoreFamily() );
                jSrcPixelsize.setText( "" + m_controller.getSrcDataCoreSize() );
                jSrcRequest.setText( "" + m_controller.getSrcDataRequestSize() );
                jSrcRequest.setEditable( ! m_controller.getSrcDataIsFixedSize() );
                jSrcShape.setText( m_controller.getSrcDataCoreShape() );
                // ranges
                FontRange r;
                if ( ( r = m_controller.getSrcDataRange() ) != null ) {
                    mess( 5, "visual Src:" + r.toString() );
                    ( (ufolib.fontizer.GUI_Application.RangeTableModel) tableSrc.getModel() ).setRange( r, this );
                    jSrcRanges.setText( "" + r.size() );
                    jSrcChars.setText( "" + r.sum() );
                }
                else {
                    jSrcRanges.setText( "-" );
                    jSrcChars.setText( "-" );
                }

            }

            private void visualPro() {
                jProComment.setText( m_controller.getProfileDataComment() );
                jProComment.setToolTipText( m_controller.getProfileDataComment() );
                jProFamily.setText( m_controller.getProfileDataCoreFamily() );
                jProFamily.setToolTipText( m_controller.getProfileDataCoreFamily() );
                jProPixelsize.setText( "" + m_controller.getProfileDataCoreSize() );
                jProShape.setText( m_controller.getProfileDataCoreShape() );
                // ranges
                FontRange r;
                if ( ( r = m_controller.getProfileDataRange() ) != null ) {
                    mess( 5, "visual Profile:" + r.toString() );
                    ( (ufolib.fontizer.GUI_Application.RangeTableModel) tableProfile.getModel() ).setRange( r, this );
                    jProRanges.setText( "" + r.size() );
                    jProChars.setText( "" + r.sum() );
                }
                else {
                    jProRanges.setText( "-" );
                    jProChars.setText( "-" );
                }
            }

            private void visualUffEtc() {
                jUffFamily.setText( m_controller.getUffDataFamily() );
                jUffFamily.setToolTipText( m_controller.getUffDataFamily() );
                jUffPixelsize.setText( "" + m_controller.getUffDataSize() );
                jUffShape.setText( m_controller.getUffDataShape() );
                jUffExtra.setText( "" + m_controller.getUffDataExtraWS() );
                jUffLeading.setText( "" + m_controller.getUffDataExtraLeading() );
            }

            private void visualUffFilename() {
                jUffFilename.setEditable( ! m_controller.getUffDataIsSchemaName() );
                jUffFilename.setBackground( ( m_controller.getUffDataIsSchemaName() ? m_colorBackViewable : m_colorBackEditable ) );
                jUffFilename.setText( m_controller.getUffDataSCName() );
                jUffFilename.setToolTipText( m_controller.getUffDataSCName() ); 
            }

            private void visualUffRanges() {
                // ranges
                FontRange r;
                if ( ( r = m_controller.getUffDataRange() ) != null ) {
                    mess( 5, "visualUff, visualUffRanges, Uff:" + r.toString() );
                    ( (ufolib.fontizer.GUI_Application.RangeTableModel) tableUff.getModel() ).setRange( r, this );
                    jUffRanges.setText( "" + r.size() );
                    jUffChars.setText( "" + r.sum() );
                }
                else {
                    jProRanges.setText( "-" );
                    jProChars.setText( "-" );
                }
            }

            private void visualUffFilenameEtc() {
                visualUffEtc();
                visualUffFilename();
            }

            private void visualUff() {
                visualUffEtc();
                visualUffFilename();
                visualUffRanges();
            }


            /**
             * called from someboy how has made uff ranges dirty
             **/
            private void dirtyUffRange() {
                FontRange r;
                if ( ! m_controller.calculateUffRanges() ) {
                    showErrorPane( "Generating Uff ranges failed", false );
                }
                else {
                    // uff ranges changed
                    visualUffRanges();
                }
            }

            // **************************************************************
            // generated

            /** Creates new form Application */
            public GUI_Application() {
                initComponents();
            }

                /** This method is called from within the constructor to
             * initialize the form.
             * WARNING: Do NOT modify this code. The content of this method is
             * always regenerated by the Form Editor.
             */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        fcOpenSrc = new javax.swing.JFileChooser();
        fcOpenPro = new javax.swing.JFileChooser();
        fcSavePro = new javax.swing.JFileChooser();
        fcOpenUff = new javax.swing.JFileChooser();
        fcSaveUff = new javax.swing.JFileChooser();
        jp_top = new javax.swing.JPanel();
        jp_topSrc = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        buttonOpenSrc = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabelEnc = new javax.swing.JLabel();
        jSrcComment = new javax.swing.JTextField();
        jSrcRequest = new javax.swing.JTextField();
        jSrcShape = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jSrcType = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jSrcRanges = new javax.swing.JTextField();
        jLabel811 = new javax.swing.JLabel();
        jSrcChars = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jSrcFilename = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        jSrcPixelsize = new javax.swing.JTextField();
        jSrcFamily = new javax.swing.JTextField();
        jLabel112 = new javax.swing.JLabel();
        jSrcEncoding = new javax.swing.JComboBox();
        jSeparator11 = new javax.swing.JSeparator();
        jp_topProfile = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        bOpenProfile = new javax.swing.JButton();
        bSaveProfileAs = new javax.swing.JButton();
        bSaveProfile = new javax.swing.JButton();
        bUseSrcProfile = new javax.swing.JButton();
        bUffAsProfile = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jProFamily = new javax.swing.JTextField();
        jProPixelsize = new javax.swing.JTextField();
        jProShape = new javax.swing.JTextField();
        jLabel1112 = new javax.swing.JLabel();
        jProComment = new javax.swing.JTextField();
        jLabel912 = new javax.swing.JLabel();
        jLabel913 = new javax.swing.JLabel();
        jProChars = new javax.swing.JTextField();
        jProRanges = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jp_topUff = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        bSaveUff = new javax.swing.JButton();
        bNameFromSrc = new javax.swing.JButton();
        jPanel211 = new javax.swing.JPanel();
        jLabel711 = new javax.swing.JLabel();
        jLabel911 = new javax.swing.JLabel();
        jLabel1111 = new javax.swing.JLabel();
        jUffFamily = new javax.swing.JTextField();
        jUffPixelsize = new javax.swing.JTextField();
        jUffLeading = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jUffRadioFilename = new javax.swing.JRadioButton();
        jUffRadioCustom = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jUffFilename = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel812 = new javax.swing.JLabel();
        jUffRanges = new javax.swing.JTextField();
        jLabel8111 = new javax.swing.JLabel();
        jUffChars = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel9111 = new javax.swing.JLabel();
        jUffShape = new javax.swing.JTextField();
        jLabel9112 = new javax.swing.JLabel();
        jUffExtra = new javax.swing.JTextField();
        jp_ranges = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPaneSrc = new javax.swing.JScrollPane();
        tableSrc = new javax.swing.JTable();
        jSeparator111 = new javax.swing.JSeparator();
        jp_bottomProfile = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jScrollPaneProfile = new javax.swing.JScrollPane();
        tableProfile = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jButtonSplitRange = new javax.swing.JButton();
        jButtonJoinRange = new javax.swing.JButton();
        jButtonDeleteRange = new javax.swing.JButton();
        jButtonAddRange = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JSeparator();
        jPanel111 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jScrollPaneUff = new javax.swing.JScrollPane();
        tableUff = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem1 = new javax.swing.JMenuItem();

        fcOpenSrc.setDialogTitle("Open font source");
        fcOpenPro.setDialogTitle("Open profile");
        fcSavePro.setDialogTitle("Save profile");
        fcSavePro.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fcOpenUff.setDialogTitle("Use UFF data as profile ranges");
        fcSaveUff.setDialogTitle("Save UFF");

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        setTitle("Fontizer");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jp_top.setLayout(new javax.swing.BoxLayout(jp_top, javax.swing.BoxLayout.X_AXIS));

        jp_topSrc.setLayout(new javax.swing.BoxLayout(jp_topSrc, javax.swing.BoxLayout.Y_AXIS));

        jp_topSrc.setPreferredSize(new java.awt.Dimension( m_panelsizeSrc, m_panelsizeHeight ));
        jPanel8.setLayout(new java.awt.GridLayout(0, 1));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Font Source");
        jLabel1.setAlignmentX(0.5F);
        jPanel8.add(jLabel1);

        buttonOpenSrc.setBackground(m_colorBackEditable);
        buttonOpenSrc.setText("Open file ...");
        buttonOpenSrc.setAlignmentX(0.5F);
        buttonOpenSrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenSrcActionPerformed(evt);
            }
        });

        jPanel8.add(buttonOpenSrc);

        jp_topSrc.add(jPanel8);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Comment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel5, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel7.setText("Request Size");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel7, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel9.setText("Shape");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel9, gridBagConstraints);

        jLabelEnc.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabelEnc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelEnc.setText("Encoding");
        jLabelEnc.setToolTipText("Encoding found in font file or encoding to assume");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabelEnc, gridBagConstraints);

        jSrcComment.setBackground(m_colorBackViewable);
        jSrcComment.setColumns(m_textsizeInCol);
        jSrcComment.setEditable(false);
        jSrcComment.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcComment.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcComment.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcComment.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcComment, gridBagConstraints);

        jSrcRequest.setBackground(m_colorBackEditable);
        jSrcRequest.setColumns(m_textsizeInCol);
        jSrcRequest.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcRequest.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcRequest.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcRequest.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jSrcRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSrcRequestActionPerformed(evt);
            }
        });

        jSrcRequest.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jSrcRequestFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcRequest, gridBagConstraints);

        jSrcShape.setBackground(m_colorBackViewable);
        jSrcShape.setColumns(m_textsizeInCol);
        jSrcShape.setEditable(false);
        jSrcShape.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcShape.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcShape.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcShape.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcShape, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel8, gridBagConstraints);

        jSrcType.setBackground(m_colorBackViewable);
        jSrcType.setColumns(m_textsizeInCol);
        jSrcType.setEditable(false);
        jSrcType.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcType.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcType.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcType.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcType, gridBagConstraints);

        jLabel81.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel81.setText("Ranges");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel81, gridBagConstraints);

        jSrcRanges.setBackground(m_colorBackViewable);
        jSrcRanges.setColumns(m_textsizeInCol);
        jSrcRanges.setEditable(false);
        jSrcRanges.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcRanges.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcRanges.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcRanges.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcRanges, gridBagConstraints);

        jLabel811.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel811.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel811.setText("Chars");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel811, gridBagConstraints);

        jSrcChars.setBackground(m_colorBackViewable);
        jSrcChars.setColumns(m_textsizeInCol);
        jSrcChars.setEditable(false);
        jSrcChars.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcChars.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcChars.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcChars.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcChars, gridBagConstraints);

        jLabel51.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel51.setText("Filename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel51, gridBagConstraints);

        jSrcFilename.setBackground(m_colorBackViewable);
        jSrcFilename.setColumns(m_textsizeInCol);
        jSrcFilename.setEditable(false);
        jSrcFilename.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcFilename.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcFilename.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcFilename.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcFilename, gridBagConstraints);

        jLabel72.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel72.setText("Pixelsize");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel72, gridBagConstraints);

        jSrcPixelsize.setBackground(m_colorBackViewable);
        jSrcPixelsize.setColumns(m_textsizeInCol);
        jSrcPixelsize.setEditable(false);
        jSrcPixelsize.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcPixelsize.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcPixelsize.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcPixelsize.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcPixelsize, gridBagConstraints);

        jSrcFamily.setBackground(m_colorBackViewable);
        jSrcFamily.setColumns(m_textsizeInCol);
        jSrcFamily.setEditable(false);
        jSrcFamily.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jSrcFamily.setMaximumSize(new java.awt.Dimension(5000, 30));
        jSrcFamily.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcFamily.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcFamily, gridBagConstraints);

        jLabel112.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel112.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel112.setText("Family");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel112, gridBagConstraints);

        jSrcEncoding.setBackground(m_colorBackEditable);
        jSrcEncoding.setMaximumRowCount(11);
        jSrcEncoding.setToolTipText("Reencode the font file");
        jSrcEncoding.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jSrcEncoding.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jSrcEncoding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSrcEncodingActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(jSrcEncoding, gridBagConstraints);

        jp_topSrc.add(jPanel2);

        jp_top.add(jp_topSrc);

        jSeparator11.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator11.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator11.setMaximumSize(new java.awt.Dimension(3, 32767));
        jp_top.add(jSeparator11);

        jp_topProfile.setLayout(new javax.swing.BoxLayout(jp_topProfile, javax.swing.BoxLayout.Y_AXIS));

        jp_topProfile.setPreferredSize(new java.awt.Dimension( m_panelsizeProfile, m_panelsizeHeight ));
        jPanel7.setLayout(new java.awt.GridLayout(0, 1));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Selection/Profile");
        jLabel2.setAlignmentX(0.5F);
        jPanel7.add(jLabel2);

        bOpenProfile.setBackground(m_colorBackEditable);
        bOpenProfile.setText("Open profile ...");
        bOpenProfile.setToolTipText("Select a file containing the desired font ranges");
        bOpenProfile.setAlignmentX(0.5F);
        bOpenProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOpenProfileActionPerformed(evt);
            }
        });

        jPanel7.add(bOpenProfile);

        bSaveProfileAs.setBackground(m_colorBackEditable);
        bSaveProfileAs.setText("Save profile as ...");
        bSaveProfileAs.setToolTipText("Save this ranges and comment to a file, select the filename first");
        bSaveProfileAs.setAlignmentX(0.5F);
        bSaveProfileAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveProfileAsActionPerformed(evt);
            }
        });

        jPanel7.add(bSaveProfileAs);

        bSaveProfile.setBackground(m_colorBackEditable);
        bSaveProfile.setText("Save profile");
        bSaveProfile.setToolTipText("Save this ranges and comment to a file, save under the old name");
        bSaveProfile.setAlignmentX(0.5F);
        bSaveProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveProfileActionPerformed(evt);
            }
        });

        jPanel7.add(bSaveProfile);

        bUseSrcProfile.setBackground(m_colorBackEditable);
        bUseSrcProfile.setText("Use Source as Profile");
        bUseSrcProfile.setToolTipText("Use the ranges as seen in Font Source here");
        bUseSrcProfile.setAlignmentX(0.5F);
        bUseSrcProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUseSrcProfileActionPerformed(evt);
            }
        });

        jPanel7.add(bUseSrcProfile);

        bUffAsProfile.setBackground(m_colorBackEditable);
        bUffAsProfile.setText("Use other UFF as profile ...");
        bUffAsProfile.setToolTipText("Select an existing UFF file, inspect it for the ranges and you the ranges here");
        bUffAsProfile.setAlignmentX(0.5F);
        bUffAsProfile.setEnabled(false);
        bUffAsProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUffAsProfileActionPerformed(evt);
            }
        });

        jPanel7.add(bUffAsProfile);

        jp_topProfile.add(jPanel7);

        jPanel21.setLayout(new java.awt.GridBagLayout());

        jLabel71.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel71.setText("Pixelsize");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel21.add(jLabel71, gridBagConstraints);

        jLabel91.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel91.setText("Shape");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel21.add(jLabel91, gridBagConstraints);

        jLabel111.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel111.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel111.setText("Family");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel21.add(jLabel111, gridBagConstraints);

        jProFamily.setBackground(m_colorBackViewable);
        jProFamily.setColumns(m_textsizeInCol);
        jProFamily.setEditable(false);
        jProFamily.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jProFamily.setMaximumSize(new java.awt.Dimension(5000, 30));
        jProFamily.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jProFamily.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel21.add(jProFamily, gridBagConstraints);

        jProPixelsize.setBackground(m_colorBackViewable);
        jProPixelsize.setColumns(m_textsizeInCol);
        jProPixelsize.setEditable(false);
        jProPixelsize.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jProPixelsize.setMaximumSize(new java.awt.Dimension(5000, 30));
        jProPixelsize.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jProPixelsize.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel21.add(jProPixelsize, gridBagConstraints);

        jProShape.setBackground(m_colorBackViewable);
        jProShape.setColumns(m_textsizeInCol);
        jProShape.setEditable(false);
        jProShape.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jProShape.setMaximumSize(new java.awt.Dimension(5000, 30));
        jProShape.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jProShape.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel21.add(jProShape, gridBagConstraints);

        jLabel1112.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1112.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1112.setText("Comment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel21.add(jLabel1112, gridBagConstraints);

        jProComment.setBackground(m_colorBackEditable);
        jProComment.setColumns(m_textsizeInCol);
        jProComment.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jProComment.setMaximumSize(new java.awt.Dimension(5000, 30));
        jProComment.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jProComment.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jProComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jProCommentActionPerformed(evt);
            }
        });

        jProComment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jProCommentFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel21.add(jProComment, gridBagConstraints);

        jLabel912.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel912.setText("Chars");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel21.add(jLabel912, gridBagConstraints);

        jLabel913.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel913.setText("Ranges");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel21.add(jLabel913, gridBagConstraints);

        jProChars.setBackground(m_colorBackViewable);
        jProChars.setColumns(m_textsizeInCol);
        jProChars.setEditable(false);
        jProChars.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jProChars.setMaximumSize(new java.awt.Dimension(5000, 30));
        jProChars.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jProChars.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel21.add(jProChars, gridBagConstraints);

        jProRanges.setBackground(m_colorBackViewable);
        jProRanges.setColumns(m_textsizeInCol);
        jProRanges.setEditable(false);
        jProRanges.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jProRanges.setMaximumSize(new java.awt.Dimension(5000, 30));
        jProRanges.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jProRanges.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel21.add(jProRanges, gridBagConstraints);

        jp_topProfile.add(jPanel21);

        jp_top.add(jp_topProfile);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMaximumSize(new java.awt.Dimension(3, 32767));
        jp_top.add(jSeparator1);

        jp_topUff.setLayout(new javax.swing.BoxLayout(jp_topUff, javax.swing.BoxLayout.Y_AXIS));

        jp_topUff.setPreferredSize(new java.awt.Dimension( m_panelsizeUff, m_panelsizeHeight ));
        jPanel9.setLayout(new java.awt.GridLayout(0, 1));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("UFF Font");
        jLabel3.setAlignmentX(0.5F);
        jPanel9.add(jLabel3);

        bSaveUff.setBackground(m_colorBackEditable);
        bSaveUff.setText("Save UFF ...");
        bSaveUff.setAlignmentX(0.5F);
        bSaveUff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveUffActionPerformed(evt);
            }
        });

        jPanel9.add(bSaveUff);

        bNameFromSrc.setBackground(m_colorBackEditable);
        bNameFromSrc.setText("Get name from Source");
        bNameFromSrc.setAlignmentX(0.5F);
        bNameFromSrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNameFromSrcActionPerformed(evt);
            }
        });

        jPanel9.add(bNameFromSrc);

        jp_topUff.add(jPanel9);

        jPanel211.setLayout(new java.awt.GridBagLayout());

        jLabel711.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel711.setText("Pixelsize");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel711, gridBagConstraints);

        jLabel911.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel911.setText("Leading");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel911, gridBagConstraints);

        jLabel1111.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1111.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1111.setText("Family");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel1111, gridBagConstraints);

        jUffFamily.setBackground(m_colorBackEditable);
        jUffFamily.setColumns(m_textsizeInCol);
        jUffFamily.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jUffFamily.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffFamily.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffFamily.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jUffFamily.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffFamilyActionPerformed(evt);
            }
        });

        jUffFamily.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jUffFamilyFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffFamily, gridBagConstraints);

        jUffPixelsize.setBackground(m_colorBackEditable);
        jUffPixelsize.setColumns(m_textsizeInCol);
        jUffPixelsize.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jUffPixelsize.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffPixelsize.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffPixelsize.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jUffPixelsize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffPixelsizeActionPerformed(evt);
            }
        });

        jUffPixelsize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jUffPixelsizeFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffPixelsize, gridBagConstraints);

        jUffLeading.setBackground(m_colorBackEditable);
        jUffLeading.setColumns(m_textsizeInCol);
        jUffLeading.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jUffLeading.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffLeading.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffLeading.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jUffLeading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffLeadingActionPerformed(evt);
            }
        });

        jUffLeading.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jUffLeadingFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffLeading, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBackground(m_colorBackEditable);
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setBackground(m_colorBackEditable);
        jUffRadioFilename.setBackground(m_colorBackEditable);
        jUffRadioFilename.setFont(new java.awt.Font("Dialog", 0, 12));
        jUffRadioFilename.setSelected(true);
        jUffRadioFilename.setText("Filename by schema");
        buttonGroup1.add(jUffRadioFilename);
        jUffRadioFilename.setMargin(new java.awt.Insets(2, 2, 0, 2));
        jUffRadioFilename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffRadioFilenameActionPerformed(evt);
            }
        });

        jPanel4.add(jUffRadioFilename);

        jUffRadioCustom.setBackground(m_colorBackEditable);
        jUffRadioCustom.setFont(new java.awt.Font("Dialog", 0, 12));
        jUffRadioCustom.setText("Custom filename");
        buttonGroup1.add(jUffRadioCustom);
        jUffRadioCustom.setMargin(new java.awt.Insets(0, 2, 2, 2));
        jUffRadioCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffRadioCustomActionPerformed(evt);
            }
        });

        jPanel4.add(jUffRadioCustom);

        jPanel1.add(jPanel4, new java.awt.GridBagConstraints());

        jPanel5.setBackground(m_colorBackEditable);
        jPanel1.add(jPanel5, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jPanel1, gridBagConstraints);

        jUffFilename.setBackground(m_colorBackViewable);
        jUffFilename.setColumns(m_textsizeInCol);
        jUffFilename.setEditable(false);
        jUffFilename.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffFilename.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffFilename.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jUffFilename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffFilenameActionPerformed(evt);
            }
        });

        jUffFilename.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jUffFilenameFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffFilename, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Filename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel6, gridBagConstraints);

        jLabel812.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel812.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel812.setText("Ranges");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel812, gridBagConstraints);

        jUffRanges.setBackground(m_colorBackViewable);
        jUffRanges.setColumns(m_textsizeInCol);
        jUffRanges.setEditable(false);
        jUffRanges.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jUffRanges.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffRanges.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffRanges.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffRanges, gridBagConstraints);

        jLabel8111.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel8111.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8111.setText("Chars");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel8111, gridBagConstraints);

        jUffChars.setBackground(m_colorBackViewable);
        jUffChars.setColumns(m_textsizeInCol);
        jUffChars.setEditable(false);
        jUffChars.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jUffChars.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffChars.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffChars.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffChars, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel10.setText("+MCS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        jPanel211.add(jLabel10, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel12.setText("+MCS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        jPanel211.add(jLabel12, gridBagConstraints);

        jLabel9111.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel9111.setText("Shape");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel9111, gridBagConstraints);

        jUffShape.setBackground(m_colorBackEditable);
        jUffShape.setColumns(m_textsizeInCol);
        jUffShape.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jUffShape.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffShape.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffShape.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jUffShape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffShapeActionPerformed(evt);
            }
        });

        jUffShape.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jUffShapeFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffShape, gridBagConstraints);

        jLabel9112.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel9112.setText("Extra WS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel211.add(jLabel9112, gridBagConstraints);

        jUffExtra.setBackground(m_colorBackEditable);
        jUffExtra.setColumns(m_textsizeInCol);
        jUffExtra.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jUffExtra.setMaximumSize(new java.awt.Dimension(5000, 30));
        jUffExtra.setMinimumSize(new Dimension( (int) ( m_textsizeInPixel * m_textsizeMinFactor ), m_textsizeHeight ));
        jUffExtra.setPreferredSize(new Dimension( m_textsizeInPixel, m_textsizeHeight ));
        jUffExtra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUffExtraActionPerformed(evt);
            }
        });

        jUffExtra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jUffExtraFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel211.add(jUffExtra, gridBagConstraints);

        jp_topUff.add(jPanel211);

        jp_top.add(jp_topUff);

        getContentPane().add(jp_top);

        jp_ranges.setLayout(new javax.swing.BoxLayout(jp_ranges, javax.swing.BoxLayout.X_AXIS));

        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.Y_AXIS));

        jPanel14.setPreferredSize(new java.awt.Dimension( m_panelsizeSrc, m_panelsizeHeight2 ));
        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setText("Ranges");
        jLabel4.setAlignmentX(0.5F);
        jPanel14.add(jLabel4);

        jScrollPaneSrc.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneSrc.setPreferredSize(new java.awt.Dimension( m_panelsizeSrc - 2, m_panelsizeTableheight - 2 ));
        tableSrc.setBackground(m_colorBackViewable);
        tableSrc.setModel(new RangeTableModel( false, false, this, tableSrc ));
        tableSrc.setMaximumSize(new java.awt.Dimension(1000, 1000));
        tableSrc.setPreferredSize(new java.awt.Dimension(450, 400));
        tableSrc.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        jScrollPaneSrc.setViewportView(tableSrc);

        jPanel14.add(jScrollPaneSrc);

        jp_ranges.add(jPanel14);

        jSeparator111.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator111.setMaximumSize(new java.awt.Dimension(3, 32767));
        jp_ranges.add(jSeparator111);

        jp_bottomProfile.setLayout(new javax.swing.BoxLayout(jp_bottomProfile, javax.swing.BoxLayout.Y_AXIS));

        jp_bottomProfile.setPreferredSize(new java.awt.Dimension( m_panelsizeProfile, m_panelsizeHeight2 ));
        jLabel41.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel41.setText("Ranges");
        jLabel41.setAlignmentX(0.5F);
        jp_bottomProfile.add(jLabel41);

        jScrollPaneProfile.setPreferredSize(new java.awt.Dimension( m_panelsizeProfile - 2, m_panelsizeTableheight - 2 ));
        tableProfile.setBackground(m_colorBackViewable);
        tableProfile.setModel(new RangeTableModel( true, false, this, tableProfile ));
        tableProfile.setGridColor(new java.awt.Color(153, 153, 153));
        tableProfile.setMaximumSize(new java.awt.Dimension(1000, 1000));
        tableProfile.setPreferredScrollableViewportSize(new java.awt.Dimension(1, 1));
        tableProfile.setPreferredSize(new java.awt.Dimension(450, 400));
        tableProfile.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        tableProfile.setDefaultRenderer( Integer.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus,  int row, int column) {
                setEnabled(table == null || table.isEnabled()); // see question above

                if ( ( 1 <= column ) && ( column <= 2 ) ) {
                    if ( isSelected ) {
                        setBackground( ( hasFocus ? m_colorBackEdit : m_colorBackSelection ) );
                    }
                    else {
                        setBackground( m_colorBackEditable );
                    }
                    setHorizontalAlignment( JLabel.RIGHT );
                }
                else {
                    if ( isSelected ) {
                        setBackground( m_colorBackSelection );
                    }
                    else {
                        setBackground( m_colorBackViewable );
                    }
                    setHorizontalAlignment( JLabel.RIGHT );
                }

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if ( ( 1 <= column ) && ( column <= 2 ) ) {
                    if ( isSelected ) {
                        setBackground( ( hasFocus ? m_colorBackEdit : m_colorBackSelection ) );
                    }
                    else {
                        setBackground( m_colorBackEditable );
                    }
                    setHorizontalAlignment( JLabel.RIGHT );
                }
                else {
                    if ( isSelected ) {
                        setBackground( m_colorBackSelection );
                    }
                    else {
                        setBackground( m_colorBackViewable );
                    }
                    setHorizontalAlignment( JLabel.RIGHT );
                }

                return this;
            }

        } );

        jScrollPaneProfile.setViewportView(tableProfile);

        jp_bottomProfile.add(jScrollPaneProfile);

        jPanel3.setLayout(new java.awt.GridLayout(0, 1));

        jButtonSplitRange.setBackground(m_colorBackEditable);
        jButtonSplitRange.setText("Select a row and split range");
        jButtonSplitRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSplitRangeActionPerformed(evt);
            }
        });

        jPanel3.add(jButtonSplitRange);

        jButtonJoinRange.setBackground(m_colorBackEditable);
        jButtonJoinRange.setText("Select some rows and join ranges");
        jButtonJoinRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonJoinRangeActionPerformed(evt);
            }
        });

        jPanel3.add(jButtonJoinRange);

        jButtonDeleteRange.setBackground(m_colorBackEditable);
        jButtonDeleteRange.setText("Select a row and delete range");
        jButtonDeleteRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteRangeActionPerformed(evt);
            }
        });

        jPanel3.add(jButtonDeleteRange);

        jButtonAddRange.setBackground(m_colorBackEditable);
        jButtonAddRange.setText("Add a range");
        jButtonAddRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddRangeActionPerformed(evt);
            }
        });

        jPanel3.add(jButtonAddRange);

        jp_bottomProfile.add(jPanel3);

        jp_ranges.add(jp_bottomProfile);

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator12.setMaximumSize(new java.awt.Dimension(3, 32767));
        jp_ranges.add(jSeparator12);

        jPanel111.setLayout(new javax.swing.BoxLayout(jPanel111, javax.swing.BoxLayout.Y_AXIS));

        jPanel111.setPreferredSize(new java.awt.Dimension( m_panelsizeUff, m_panelsizeHeight2 ));
        jLabel42.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel42.setText("Ranges");
        jLabel42.setAlignmentX(0.5F);
        jPanel111.add(jLabel42);

        jScrollPaneUff.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneUff.setPreferredSize(new java.awt.Dimension( m_panelsizeUff - 2, m_panelsizeTableheight - 2));
        tableUff.setBackground(m_colorBackViewable);
        tableUff.setModel(new RangeTableModel( false, true, this, tableUff ));
        tableUff.setMaximumSize(new java.awt.Dimension(1000, 1000));
        tableUff.setPreferredSize(new java.awt.Dimension(450, 400));
        tableUff.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        jScrollPaneUff.setViewportView(tableUff);

        jPanel111.add(jScrollPaneUff);

        jp_ranges.add(jPanel111);

        getContentPane().add(jp_ranges);

        fileMenu.setText("File");
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        helpMenu.setText("Help");
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);
        aboutMenuItem1.setText("Super Quick Tutorial");
        aboutMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItem1ActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem1);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        pack();
    }//GEN-END:initComponents

    private void encAction() {
        if ( m_sensitiveEncoding ) {
            String encS = (String) jSrcEncoding.getSelectedItem();
            if ( encS != null ) {
                if ( encS.trim().equals( "" ) ) {
                    encS = null;
                }
            }
            boolean suc = m_controller.setSrcDataEncoding( encS );
            visualEnc();
            if ( ! suc ) {
                showErrorPane( "Setting encoding failed", false );
            }
            else {
                visualSrc();
                dirtyUffRange();
            }
        }
    }
    
    private void visualEnc() {
        //jSrcEncoding.setText( m_controller.getSrcDataEncoding() );
    }

    private void jSrcEncodingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSrcEncodingActionPerformed
        encAction();
    }//GEN-LAST:event_jSrcEncodingActionPerformed

    private void aboutMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItem1ActionPerformed
        showQuick();
    }//GEN-LAST:event_aboutMenuItem1ActionPerformed

    private void showQuick() {
       ImageIcon amess = new javax.swing.ImageIcon( getClass().getResource("/ufolib/fontizer/fonti_half.png"));
       javax.swing.JOptionPane.showConfirmDialog(null, amess, "Super quick tutorial", javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE );
    }
    
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        String amess = "ufolib Fontizer (C) 2003, Oliver Erdmann\nConvert unicode font to SuperWaba/ufolib font.\n\n" + ufolib.engine.UfoCopyright.copyrightNL;
        javax.swing.JOptionPane.showConfirmDialog(null, amess, "About ...", javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE );
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void jButtonAddRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddRangeActionPerformed
        FontRange r;
        mess( 2, "jButtonAddRangeActionPerformed" );
        if ( ( r = m_controller.getProfileDataRange() ) != null ) {
            int addv = ( r.size() > 0 ? r.getRangeEnd( r.size() - 1 ) + 1 : 1 );
            boolean suc = r.addRange( addv, addv + 1 );
            if ( suc ) {
                mess( 2, "after add:" + r.toString() );
		visualPro();
		dirtyUffRange();
            }
            else {
                showErrorPane( "Add failed", false );
            }
        }
    }//GEN-LAST:event_jButtonAddRangeActionPerformed

    private void jButtonDeleteRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteRangeActionPerformed
        FontRange r;
        mess( 2, "jButtonDeleteRangeActionPerformed" );
        ListSelectionModel lsm = tableProfile.getSelectionModel();
        if ( ( ! lsm.isSelectionEmpty() ) && ( lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex() ) ) {  // exact one row is selected
            int lineindex = lsm.getMinSelectionIndex();
            mess( 2, "jButtonDeleteRangeActionPerformed: sel:(" + lsm.isSelectionEmpty() + "," + lsm.getMinSelectionIndex() + "," + lsm.getMaxSelectionIndex() + ") lineindex=" + lineindex );
            if ( ( r = m_controller.getProfileDataRange() ) != null ) {
                if ( r.deleteRange( lineindex ) ) {
                    lsm.clearSelection();
                    visualPro();
		    dirtyUffRange();
		}
		else {
		    showErrorPane( "Delete failed", false );
		}
            }
        }
    }//GEN-LAST:event_jButtonDeleteRangeActionPerformed

    private void jButtonJoinRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonJoinRangeActionPerformed
        FontRange r;
        mess( 2, "jButtonJoinRangeActionPerformed" );
        ListSelectionModel lsm = tableProfile.getSelectionModel();
        if ( ( ! lsm.isSelectionEmpty() ) && ( lsm.getMinSelectionIndex() < lsm.getMaxSelectionIndex() ) ) {  // some rows are selected
            int lineindex1 = lsm.getMinSelectionIndex();
            int lineindex2 = lsm.getMaxSelectionIndex();
            mess( 2, "jButtonJoinRangeActionPerformed: sel:(" + lsm.isSelectionEmpty() + "," + lsm.getMinSelectionIndex() + "," + lsm.getMaxSelectionIndex() + ") lineindex=" + lineindex1 + "," + lineindex2 );
            if ( ( r = m_controller.getProfileDataRange() ) != null ) {
                if ( r.combineRanges( lineindex1, lineindex2 ) ) {
                    lsm.clearSelection();
                    visualPro();
		    dirtyUffRange();
		}
		else {
		    showErrorPane( "Join failed", false );
		}
            }
        }
    }//GEN-LAST:event_jButtonJoinRangeActionPerformed

    private void jButtonSplitRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSplitRangeActionPerformed
        FontRange r;
        mess( 2, "jButtonSplitRangeActionPerformed" );
        ListSelectionModel lsm = tableProfile.getSelectionModel();
        if ( ( ! lsm.isSelectionEmpty() ) && ( lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex() ) ) {  // exact one row is selected
            int lineindex = lsm.getMinSelectionIndex();
            mess( 2, "jButtonSplitRangeActionPerformed: sel:(" + lsm.isSelectionEmpty() + "," + lsm.getMinSelectionIndex() + "," + lsm.getMaxSelectionIndex() + ") lineindex=" + lineindex );
            if ( ( r = m_controller.getProfileDataRange() ) != null ) {
		if ( r.splitRange( lineindex, r.getRangeStart( lineindex ) ) ) {
                    lsm.clearSelection();
                    visualPro();
		    dirtyUffRange();
		}
		else {
		    showErrorPane( "Split failed", false );
		}
            }
        }
    }//GEN-LAST:event_jButtonSplitRangeActionPerformed

    
    private void jSrcEncoding2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSrcEncoding2FocusLost
        encAction();
    }//GEN-LAST:event_jSrcEncoding2FocusLost

    private void jSrcEncoding2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSrcEncoding2ActionPerformed
        encAction();
    }//GEN-LAST:event_jSrcEncoding2ActionPerformed

    private void srcReqAction() {
        int req = s2i( jSrcRequest.getText() );
        int retQ = m_controller.setSrcDataRequestSize( req );
        jSrcRequest.setText( "" + retQ );                
    }
    
    private void jSrcRequestFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSrcRequestFocusLost
        srcReqAction();
    }//GEN-LAST:event_jSrcRequestFocusLost

    private void jSrcRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSrcRequestActionPerformed
        srcReqAction();
    }//GEN-LAST:event_jSrcRequestActionPerformed

    private void jUffFilenameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUffFilenameFocusLost
        m_controller.setUffDataCustomName( jUffFilename.getText() );
    }//GEN-LAST:event_jUffFilenameFocusLost

    private void jUffLeadingFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUffLeadingFocusLost
        m_controller.setUffDataExtraLeading( s2i( jUffLeading.getText() ) );
    }//GEN-LAST:event_jUffLeadingFocusLost

    private void jUffLeadingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffLeadingActionPerformed
        m_controller.setUffDataExtraLeading( s2i( jUffLeading.getText() ) );
    }//GEN-LAST:event_jUffLeadingActionPerformed

    private void jUffExtraFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUffExtraFocusLost
        m_controller.setUffDataExtraWS( s2i( jUffExtra.getText() ) );
    }//GEN-LAST:event_jUffExtraFocusLost

    private void jUffExtraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffExtraActionPerformed
        m_controller.setUffDataExtraWS( s2i( jUffExtra.getText() ) );
    }//GEN-LAST:event_jUffExtraActionPerformed

    private void jUffShapeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUffShapeFocusLost
        m_controller.setUffDataShape( jUffShape.getText() );
        visualUffFilename();
    }//GEN-LAST:event_jUffShapeFocusLost

    private void jUffPixelsizeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUffPixelsizeFocusLost
        m_controller.setUffDataSize( s2i( jUffPixelsize.getText() ) );
        visualUffFilename();
    }//GEN-LAST:event_jUffPixelsizeFocusLost

    private void jUffShapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffShapeActionPerformed
        m_controller.setUffDataShape( jUffShape.getText() );
        visualUffFilename();
    }//GEN-LAST:event_jUffShapeActionPerformed

    private void jUffPixelsizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffPixelsizeActionPerformed
        m_controller.setUffDataSize( s2i( jUffPixelsize.getText() ) );
        visualUffFilename();
    }//GEN-LAST:event_jUffPixelsizeActionPerformed

    private void jUffFamilyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jUffFamilyFocusLost
        m_controller.setUffDataFamily( jUffFamily.getText() );
        visualUffFilename();
    }//GEN-LAST:event_jUffFamilyFocusLost

    private void jUffFamilyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffFamilyActionPerformed
        m_controller.setUffDataFamily( jUffFamily.getText() );
        visualUffFilename();
    }//GEN-LAST:event_jUffFamilyActionPerformed

    private void jUffFilenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffFilenameActionPerformed
        m_controller.setUffDataCustomName( jUffFilename.getText() );
    }//GEN-LAST:event_jUffFilenameActionPerformed

    private void jProCommentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jProCommentFocusLost
        m_controller.setProfileDataComment( jProComment.getText() );
    }//GEN-LAST:event_jProCommentFocusLost

    private void jProCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jProCommentActionPerformed
        m_controller.setProfileDataComment( jProComment.getText() );
    }//GEN-LAST:event_jProCommentActionPerformed

    private void jUffRadioCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffRadioCustomActionPerformed
        m_controller.setUffDataCustomName( jUffFilename.getText() );
        visualUffFilenameEtc();
    }//GEN-LAST:event_jUffRadioCustomActionPerformed

    private void jUffRadioFilenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUffRadioFilenameActionPerformed
        m_controller.setUffDataSchemaName();
        visualUffFilenameEtc();
    }//GEN-LAST:event_jUffRadioFilenameActionPerformed

    private void bNameFromSrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNameFromSrcActionPerformed
        if ( m_controller.setUffDataFamily( m_controller.getSrcDataCoreFamily() ) &&
            m_controller.setUffDataSize( m_controller.getSrcDataCoreSize() ) &&
            m_controller.setUffDataShape( m_controller.getSrcDataCoreShape() ) &&
            m_controller.setUffDataShape( m_controller.getSrcDataCoreShape() ) &&
            m_controller.setUffDataSchemaName() ) {
            visualUffFilenameEtc();
        }
    }//GEN-LAST:event_bNameFromSrcActionPerformed

    private void bSaveUffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveUffActionPerformed
        // check some values?
        // ???
        // save
        if ( ! m_controller.writeUff() ) {
            showErrorPane( "Writing UFF failed", false );
        }
    }//GEN-LAST:event_bSaveUffActionPerformed

    private void bSaveProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveProfileActionPerformed
        if ( m_proFilename != null ) {
            boolean suc = m_controller.saveProfile( m_proFilename );
            if ( ! suc ) {
                showErrorPane( "Save profile failed.", false );
            }
            else {
                // ok.
            }
        }
    }//GEN-LAST:event_bSaveProfileActionPerformed

    private void bUffAsProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUffAsProfileActionPerformed
        int re = fcOpenUff.showOpenDialog( this );
        String filename = null;
        if ( re == javax.swing.JFileChooser.APPROVE_OPTION ) {
            filename = fcOpenUff.getSelectedFile().getPath();
        }
        if ( filename != null ) {
            boolean suc = m_controller.setProfileRangesByUff( filename );
            if ( ! suc ) {
                showErrorPane( "Set Profile by UFF file failed", false );
            }
            else {
                // open done, now:
                // 1. fields
                visualPro();
                // 2. dependings at UFF
                m_controller.calculateUffRanges();
                visualUff();
                // ???
            }
        }
    }//GEN-LAST:event_bUffAsProfileActionPerformed

    private void bSaveProfileAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveProfileAsActionPerformed
        int re = fcSavePro.showSaveDialog( this );
        String filename = null;
        if ( re == javax.swing.JFileChooser.APPROVE_OPTION ) {
            filename = fcSavePro.getSelectedFile().getPath();
        }
        if ( filename != null ) {
            boolean suc = m_controller.saveProfile( filename );
            if ( ! suc ) {
                showErrorPane( "Save profile failed.", false );
            }
            else {
                // ok.
                m_proFilename = filename;
            }
        }
    }//GEN-LAST:event_bSaveProfileAsActionPerformed

    private void bUseSrcProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUseSrcProfileActionPerformed
        FontRange rs, rp;
        boolean suc = false;
        if ( ( rs = m_controller.getSrcDataRange() ) != null ) {
            if ( ( rp = m_controller.getProfileDataRange() ) != null ) {
                mess( 2, "UseSrcProfile: source=" + rs );
                suc = rp.setCompleteRanges( rs );
                mess( 2, "UseSrcProfile: " +suc + ",=" + rp );
            }
        }
        if ( ! suc ) {
            showErrorPane( "Copying Font Source ranges failed", false );
        }
        else {
            visualPro();
            dirtyUffRange();
        }
    }//GEN-LAST:event_bUseSrcProfileActionPerformed

    private void bOpenProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOpenProfileActionPerformed
        int re = fcOpenPro.showOpenDialog( this );
        String filename = null;
        if ( re == javax.swing.JFileChooser.APPROVE_OPTION ) {
            filename = fcOpenPro.getSelectedFile().getPath();
            m_proFilename = filename;
        }
        if ( filename != null ) {
            boolean suc = m_controller.openProfile( filename );
            if ( ! suc ) {
                showErrorPane( "Open profile failed", false );
            }
            else {
                // open done, now:
                // 1. fields
                visualPro();
                // 2. dependings at UFF
                dirtyUffRange();
                visualUff();
                // ???
            }
        }
        
    }//GEN-LAST:event_bOpenProfileActionPerformed

    private void buttonOpenSrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenSrcActionPerformed
        int re = fcOpenSrc.showOpenDialog( this );
        String filename = null;
        if ( re == javax.swing.JFileChooser.APPROVE_OPTION ) {
            filename = fcOpenSrc.getSelectedFile().getPath();
        }
        if ( filename != null ) {
            boolean suc = m_controller.openSrc( filename );
            if ( ! suc ) {
                showErrorPane( "Open font source failed", false );
            }
            else {
                // open done, now:
                // 1. fields
                visualSrc();
                // 2. dependings at Profile/UFF
                dirtyUffRange();
                // ???
            }
        }
    }//GEN-LAST:event_buttonOpenSrcActionPerformed
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        m_controller.friendlyExit();
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        m_controller.friendlyExit();
    }//GEN-LAST:event_exitForm

    private void showErrorPane(java.lang.String mess, boolean warning) {
        javax.swing.JOptionPane.showConfirmDialog(null, mess, "Please confirm...", javax.swing.JOptionPane.DEFAULT_OPTION, ( warning ? javax.swing.JOptionPane.WARNING_MESSAGE : javax.swing.JOptionPane.ERROR_MESSAGE ) );
    }         

    private int debuglevel = 1;
    private void mess( int level, java.lang.String mes ) {
        if ( level <= debuglevel ) {
            System.err.println( "(via) GUI_Application: " + mes );
        }
    }         
    
    private int s2i( String s ) {
        int ret = 0;
        if ( s != null ) {
            try {
                ret = Integer.parseInt( s );
            }
            catch ( NumberFormatException e ) {
                ret = 0;
            }
        }
        return ret;
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JTextField jProComment;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jp_topUff;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JFileChooser fcSavePro;
    private javax.swing.JTextField jUffFamily;
    private javax.swing.JLabel jLabel812;
    private javax.swing.JLabel jLabel1111;
    private javax.swing.JTextField jProPixelsize;
    private javax.swing.JLabel jLabel9111;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jp_top;
    private javax.swing.JTextField jUffLeading;
    private javax.swing.JPanel jPanel211;
    private javax.swing.JButton jButtonJoinRange;
    private javax.swing.JLabel jLabelEnc;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JFileChooser fcOpenUff;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JScrollPane jScrollPaneProfile;
    private javax.swing.JButton bUseSrcProfile;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTable tableUff;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JTextField jSrcFilename;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JButton bOpenProfile;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jProChars;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel jLabel913;
    private javax.swing.JLabel jLabel911;
    private javax.swing.JPanel jp_topProfile;
    private javax.swing.JButton bSaveProfile;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JButton buttonOpenSrc;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JTextField jProFamily;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JRadioButton jUffRadioCustom;
    private javax.swing.JTextField jUffPixelsize;
    private javax.swing.JPanel jPanel111;
    private javax.swing.JScrollPane jScrollPaneUff;
    private javax.swing.JSeparator jSeparator111;
    private javax.swing.JTextField jSrcRequest;
    private javax.swing.JTextField jSrcRanges;
    private javax.swing.JButton bNameFromSrc;
    private javax.swing.JFileChooser fcOpenPro;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JTextField jProShape;
    private javax.swing.JButton bSaveProfileAs;
    private javax.swing.JTable tableProfile;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton jButtonAddRange;
    private javax.swing.JButton jButtonDeleteRange;
    private javax.swing.JButton bSaveUff;
    private javax.swing.JTextField jSrcType;
    private javax.swing.JTextField jUffChars;
    private javax.swing.JPanel jp_ranges;
    private javax.swing.JLabel jLabel811;
    private javax.swing.JLabel jLabel1112;
    private javax.swing.JPanel jp_topSrc;
    private javax.swing.JTextField jSrcComment;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField jSrcChars;
    private javax.swing.JLabel jLabel9112;
    private javax.swing.JTextField jUffRanges;
    private javax.swing.JMenuItem aboutMenuItem1;
    private javax.swing.JTextField jUffFilename;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jp_bottomProfile;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JFileChooser fcOpenSrc;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JFileChooser fcSaveUff;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jUffRadioFilename;
    private javax.swing.JTextField jUffShape;
    private javax.swing.JTable tableSrc;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel jLabel912;
    private javax.swing.JLabel jLabel711;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JTextField jSrcShape;
    private javax.swing.JTextField jUffExtra;
    private javax.swing.JButton bUffAsProfile;
    private javax.swing.JButton jButtonSplitRange;
    private javax.swing.JTextField jSrcFamily;
    private javax.swing.JTextField jProRanges;
    private javax.swing.JComboBox jSrcEncoding;
    private javax.swing.JTextField jSrcPixelsize;
    private javax.swing.JScrollPane jScrollPaneSrc;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel8111;
    // End of variables declaration//GEN-END:variables

    private int m_panelsizeSrc = 320;    
    
    private int m_panelsizeProfile = 320;
    
    private int m_panelsizeUff = 320;
    
    private int m_panelsizeHeight = 350;
    
    private int m_panelsizeTableheight = 190;
    
    private int m_panelsizeHeight2 = 350;
    
    private ufolib.fontizer.Controller m_controller = null;
    
    private int m_textsizeInCol = 19;
    
    private int m_textsizeInPixel = m_panelsizeSrc / 2;
    
    private int m_textsizeHeight = 16;
    
    private double m_textsizeMinFactor = 0.9;
    
    private String m_proFilename;
    
    private static final Color m_colorBackEdit = new Color( 1f, 1f, 1f );

    private static final Color m_colorBackEditable = new Color( 0.93f, 0.93f, 0.9f );
    
    private static final Color m_colorBackSelection = new Color( 0.75f, 0.75f, 0.93f );
    
    private static final Color m_colorBackViewable = new Color( 0.80f, 0.83f, 0.83f );
    
}
