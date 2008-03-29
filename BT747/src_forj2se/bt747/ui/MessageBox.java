/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.ui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        //super(new JFrame(),"title");
        // TODO Auto-generated constructor stub
        super((JFrame)null, title);
        myPanel = new JPanel();
        getContentPane().add(myPanel);
        myPanel.add(new JLabel(msg));
        yesButton = new JButton("Yes");
        yesButton.addActionListener(this);
        myPanel.add(yesButton);        
        noButton = new JButton("No");
        noButton.addActionListener(this);
        myPanel.add(noButton);        
        pack();
        //setLocationRelativeTo(frame);
        //setVisible(true);
    }
    
    
    public void popupBlockingModal() {
        setModal(true);
        setVisible(true);
        toFront();
    }
    /**
     * @param title
     * @param text
     * @param buttonCaptions
     */
    public MessageBox(String title, String msg, String[] buttonCaptions) {
// super(title, text, buttonCaptions);
        // TODO Auto-generated constructor stub
        super((JFrame)null, title);
        myPanel = new JPanel();
        getContentPane().add(myPanel);
        myPanel.add(new JLabel(msg));
        yesButton = new JButton((buttonCaptions.length>=1)?buttonCaptions[0]:"Yes");
        yesButton.addActionListener(this);
        myPanel.add(yesButton);        
        noButton = new JButton((buttonCaptions.length>=2)?buttonCaptions[0]:"Yes");
        noButton.addActionListener(this);
        myPanel.add(noButton);        
        pack();
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
        super((JFrame)null, title);
        myPanel = new JPanel();
        getContentPane().add(myPanel);
        myPanel.add(new JLabel(msg));
        yesButton = new JButton("Yes");
        yesButton.addActionListener(this);
        myPanel.add(yesButton);        
        noButton = new JButton("No");
        noButton.addActionListener(this);
        myPanel.add(noButton);        
        pack();
        
    }

    
    private boolean popped=false;
    
    public void popupModal() {
        // TODO Auto-generated method stub
    }
    
    
    /**
     * @return Returns the popped.
     */
    public boolean isPopped() {
        return popped;
    }
    
    public void unpop() {
        
    }
    
    public int getPressedButtonIndex() {
        return answer;
    }
    
    
    private JPanel myPanel = null;
    private JButton yesButton = null;
    private JButton noButton = null;
    private int answer = -1;
    public int getAnswer() { return answer; }


    public void actionPerformed(ActionEvent e) {
        if(yesButton == e.getSource()) {
            //System.err.println("User chose yes.");
            answer = 0;
            setVisible(false);
        }
        else if(noButton == e.getSource()) {
            //System.err.println("User chose no.");
            answer = 1;
            setVisible(false);
        }
    }

}


