/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FilesPanel.java
 *
 * Created on 21 janv. 2009, 23:00:15
 */

package bt747.j2se_view;

import java.awt.FontMetrics;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import bt747.j2se_view.model.LogFileTableModel;
import bt747.model.Controller;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 *
 * @author Mario
 */
public class FilesPanel extends javax.swing.JPanel implements ModelListener {

    private LogFileTableModel logFileModel = new LogFileTableModel();
    private FileTablePanel fileTablePanel = new FileTablePanel();
    
    /** Creates new form FilesPanel */
    public FilesPanel() {
        initComponents();
    }

    private J2SEAppController c;
    private J2SEAppModel m;

    public void init(final J2SEAppController pC) {
        c = pC;
        m = c.getAppModel();
        logFileModel.setLogfileInfos(Controller.logFiles);
        fileTablePanel.init(pC);
        tbLogFile.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        final FontMetrics fm = tbLogFile.getFontMetrics(tbLogFile.getFont());
        for (int i = tbLogFile.getColumnCount() - 1; i >= 0; i--) {
            final TableColumn col = tbLogFile.getColumnModel().getColumn(i);
            col.setPreferredWidth(logFileModel.getPreferredWidth(fm, i) + 4);
        }


        m.addListener(this);
    }
    
    public void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.UPDATE_LOG_FILE_LIST:
            logFileModel.notifyUpdate();
            break;
        }
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane1.setBottomComponent(fileTablePanel);
        jScrollPane1 = new javax.swing.JScrollPane();
        tbLogFile = new javax.swing.JTable();

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(100);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);

        tbLogFile.setModel(logFileModel);
        tbLogFile.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        jScrollPane1.setViewportView(tbLogFile);

        jSplitPane1.setTopComponent(jScrollPane1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 169, Short.MAX_VALUE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
        );
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable tbLogFile;
    // End of variables declaration//GEN-END:variables

}
