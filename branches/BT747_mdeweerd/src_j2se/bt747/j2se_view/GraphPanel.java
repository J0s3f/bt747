/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GraphPanel.java
 *
 * Created on 14 déc. 2008, 11:44:38
 */

package bt747.j2se_view;

import java.awt.Color;

import org.jdesktop.swingx.JXGraph;

/**
 *
 * @author Mario
 */
public class GraphPanel extends javax.swing.JPanel {

    /** Creates new form GraphPanel */
    public GraphPanel() {
        initComponents();
        
        jXGraph1.addPlots(Color.BLUE, new myPlot());
    }
    
    class myPlot extends JXGraph.Plot {

        /* (non-Javadoc)
         * @see org.jdesktop.swingx.JXGraph.Plot#compute(double)
         */
        @Override
        public double compute(double value) {
            // TODO Auto-generated method stub
            return 3*value-value*value;
        }
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        jXGraph1 = new org.jdesktop.swingx.JXGraph();

        org.jdesktop.layout.GroupLayout jXGraph1Layout = new org.jdesktop.layout.GroupLayout(jXGraph1);
        jXGraph1.setLayout(jXGraph1Layout);
        jXGraph1Layout.setHorizontalGroup(
            jXGraph1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        jXGraph1Layout.setVerticalGroup(
            jXGraph1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jXGraph1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jXGraph1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXGraph jXGraph1;
    // End of variables declaration//GEN-END:variables

}
