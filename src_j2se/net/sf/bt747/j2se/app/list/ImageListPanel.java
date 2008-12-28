/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/*
 * ImageListPanel.java
 * 
 * Created on 27 déc. 2008, 21:04:25
 */

package net.sf.bt747.j2se.app.list;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

/**
 * 
 * @author Mario
 */
@SuppressWarnings("serial")
public class ImageListPanel extends javax.swing.JPanel {

    /** Creates new form ImageListPanel */
    public ImageListPanel() {
        initComponents();
        imageIcon.setOpaque(true);
        imageText.setOpaque(true);
        setOpaque(true);
    }

    public void setIcon(Icon icon) {
        imageIcon.setIcon(icon);
        setPreferredSize(null);
        imageIcon.invalidate();
    }
    
    public void setIconPreferredSize(Dimension dim) {
        this.setPreferredSize( new Dimension(
                (int)dim.getWidth(),
                (int)(imageText.getPreferredSize().getHeight()+dim.getHeight())));
        revalidate();
    }

    public void setLabel(String label) {
        imageText.setText(label);
        revalidate();
    }

   
    public void setColors(Color fg, Color bg) {
        imageText.setBackground(bg);
        imageText.setForeground(fg);
        //imageIcon.setForeground(fg);
        setBackground(bg);
        setForeground(bg);
        imageIcon.setBackground(bg);
        //imageIcon.setBackground(fg);
    }


    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        imageIcon = new javax.swing.JLabel();
        imageText = new javax.swing.JLabel();

        imageIcon.setLabelFor(this);

        imageText.setLabelFor(this);
        imageText.setText(null);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imageIcon)
            .add(imageText)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(imageIcon)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(imageText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
        );
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel imageIcon;
    private javax.swing.JLabel imageText;
    // End of variables declaration//GEN-END:variables

}
