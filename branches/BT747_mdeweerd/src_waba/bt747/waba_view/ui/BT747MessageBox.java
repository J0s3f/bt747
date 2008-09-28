/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.waba_view.ui;


/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BT747MessageBox extends waba.ui.MessageBox {

    /**
     * @param title
     * @param msg
     */
    public BT747MessageBox(String title, String msg) {
        super(title, msg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     */
    public BT747MessageBox(String title, String text, String[] buttonCaptions) {
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
    public BT747MessageBox(String title, String text, String[] buttonCaptions,
            int gap, int insideGap) {
        super(title, text, buttonCaptions, gap, insideGap);
        // TODO Auto-generated constructor stub
    }
    
    public BT747MessageBox(boolean n,String title,String Text,String[] Buttons) {
        this(title,
                waba.sys.Convert.insertLineBreak(waba.sys.Settings.screenWidth-6,
                        '|',
                        waba.ui.MainWindow.defaultFont.fm,
                        Text),Buttons);
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
