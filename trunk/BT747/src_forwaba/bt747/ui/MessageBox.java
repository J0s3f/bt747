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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessageBox extends waba.ui.MessageBox {

    /**
     * @param title
     * @param msg
     */
    public MessageBox(String title, String msg) {
        super(title, msg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     */
    public MessageBox(String title, String text, String[] buttonCaptions) {
        super(title, text, buttonCaptions);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     * @param gap
     * @param insideGap
     */
    public MessageBox(String title, String text, String[] buttonCaptions,
            int gap, int insideGap) {
        super(title, text, buttonCaptions, gap, insideGap);
        // TODO Auto-generated constructor stub
    }

    private boolean popped=false;
    
    
    /**
     * @return Returns the popped.
     */
    public boolean isPopped() {
        return popped;
    }
    /* (non-Javadoc)
     * @see waba.ui.Window#onPopup()
     */
    protected void onPopup() {
        // TODO Auto-generated method stub
        super.onPopup();
        popped=true;
//        ((btMainAppInterf)MainWindow.getMainWindow()).IncrementPopups();
    }
    /* (non-Javadoc)
     * @see waba.ui.Window#onUnpop()
     */
    protected void onUnpop() {
        // TODO Auto-generated method stub
        super.onUnpop();
        
//        ((btMainAppInterf)MainWindow.getMainWindow()).DecrementPopups();
        popped=false;
    }
}
