/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.ui;

import javax.swing.JFrame;



/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessageBox extends javax.swing.JDialog {

    /**
     * @param title
     * @param msg
     */
    public MessageBox(String title, String msg) {
        super(new JFrame(),"title");
        // TODO Auto-generated constructor stub
    }
    
    public int popupBlockingModal() {
        return 0;
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     */
    public MessageBox(String title, String text, String[] buttonCaptions) {
        super(new JFrame(),"title");
//        super(title, text, buttonCaptions);
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
        //super(title, text, buttonCaptions, gap, insideGap);
        this(title, "");
        // TODO Auto-generated constructor stub
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
        // TODO Auto-generated method stub
        return 0;
    }
}
