/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.ui;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MessageBox extends javax.swing.JDialog implements ActionListener {

    /**
     * @param title
     * @param msg
     */
    public MessageBox(String title, String msg) {
        this(title, msg, null, 2, 2);
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     */
    public MessageBox(String title, String msg, String[] buttonCaptions) {
        this(title, msg, buttonCaptions, 2, 2);
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     * @param gap
     * @param insideGap
     */
    public MessageBox(String title, String msg, String[] buttonCaptions,
            int gap, int insideGap) {
        super((JFrame) null, title);
        // myPanel = new JPanel(new BoxLayout(null, BoxLayout.PAGE_AXIS));
        
        Box myBox;
        Box myButtonBox;
        myBox = Box.createVerticalBox();
        myButtonBox = Box.createHorizontalBox();
        myButtonBox.setAlignmentX(Box.CENTER_ALIGNMENT);

//        myPanel=new JPanel(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().setLayout(new GridLayout(2,1));
        getContentPane().add(myBox);
        //myPanel.add(myBox);
        myBox.add(new JLabel("<html><p align=center>"
                + msg.replaceAll("\\|", "<br>") + "</html>",JLabel.CENTER));
        if (buttonCaptions == null) {
            buttonCaptions = new String[0];
        }
        yesButton = new JButton(
                (buttonCaptions.length >= 1) ? buttonCaptions[0] : "Yes");
        yesButton.addActionListener(this);
        myButtonBox.add(yesButton);
        if (buttonCaptions.length == 0 || buttonCaptions.length >= 2) {
            noButton = new JButton(
                    (buttonCaptions.length >= 2) ? buttonCaptions[1] : "No");
            noButton.addActionListener(this);
            myButtonBox.add(noButton);
        }
        getContentPane().add(myButtonBox);
        pack();
    }

    public MessageBox(boolean n, String title, String Text, String[] Buttons) {
        this(title, Text, Buttons);
    }

    public void popupBlockingModal() {
        setModal(true);
        setVisible(true);
        toFront();
    }

    public void popupModal() {
        setModal(false);
        setVisible(true);
        toFront();
    }

    /**
     * @return Returns the popped.
     */
    public boolean isPopped() {
        return isVisible();
    }

    public void unpop() {
        setVisible(false);
        dispose();
    }

    public int getPressedButtonIndex() {
        return answer;
    }

    private JButton yesButton = null;
    private JButton noButton = null;
    private int answer = -1;

    public int getAnswer() {
        return answer;
    }

    public void actionPerformed(ActionEvent e) {
        if (yesButton == e.getSource()) {
            // System.err.println("User chose yes.");
            answer = 0;
            setVisible(false);
        } else if (noButton == e.getSource()) {
            // System.err.println("User chose no.");
            answer = 1;
            setVisible(false);
        }
    }

}
