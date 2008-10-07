/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.waba_view.ui;

/**
 * @author Mario De Weerd
 */
public final class BT747MessageBox extends waba.ui.MessageBox {

    /**
     * @param title
     * @param msg
     */
    public BT747MessageBox(final String title, final String msg) {
        super(title, msg);
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     */
    public BT747MessageBox(final String title, final String text,
            final String[] buttonCaptions) {
        super(title, text, buttonCaptions);
    }

    /**
     * @param title
     * @param text
     * @param buttonCaptions
     * @param gap
     * @param insideGap
     */
    public BT747MessageBox(final String title, final String text,
            final String[] buttonCaptions, final int gap, final int insideGap) {
        super(title, text, buttonCaptions, gap, insideGap);
    }

    public BT747MessageBox(final boolean n, final String title,
            final String Text, final String[] Buttons) {
        this(title, waba.sys.Convert.insertLineBreak(
                waba.sys.Settings.screenWidth - 6, '|',
                waba.ui.MainWindow.defaultFont.fm, Text), Buttons);
    }

    private boolean popped = false;

    /**
     * @return Returns the popped.
     */
    public final boolean isPopped() {
        return popped;
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Window#onPopup()
     */
    protected final void onPopup() {
        // TODO Auto-generated method stub
        super.onPopup();
        popped = true;
        // ((btMainAppInterf)MainWindow.getMainWindow()).IncrementPopups();
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Window#onUnpop()
     */
    protected final void onUnpop() {
        // TODO Auto-generated method stub
        super.onUnpop();

        // ((btMainAppInterf)MainWindow.getMainWindow()).DecrementPopups();
        popped = false;
    }
}
