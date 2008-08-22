package net.sf.bt747.j4me.app;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.j4me.ui.UIManager;

/**
 * The entry point for the application.
 */
public class MTKMidlet extends MIDlet
{
    /**
     * The one and only instance of this class.
     */
    private static MTKMidlet instance;
    
    /**
     * Constructs the midlet.  This is called before &lt;code&gt;startApp&lt;/code&gt;.
     */
    public MTKMidlet ()
    {
        instance = this;
    }

    /**
     * Called when the application is minimized.  For example when their
     * is an incoming call that is accepted or when the phone&#x27;s hangup key
     * is pressed.
     * 
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    protected void pauseApp ()
    {
    }

    /**
     * Called when the application starts.  Shows the first screen.
     * 
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    protected void startApp () throws MIDletStateChangeException
    {
        // Initialize the J4ME UI manager.
        UIManager.init( this );
        
        // Change the theme.
        //UIManager.setTheme( new org.j4me.examples.ui.themes.RedTheme() );
        
        // Show the first screen.
        HelloWorldScreen screen = new HelloWorldScreen();
        screen.show();
    }

    /**
     * Called when the application is exiting.
     * 
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    protected void destroyApp (boolean arg0) throws MIDletStateChangeException
    {
        // Add cleanup code here.
        
        // Exit the application.
        notifyDestroyed();
    }
    
    /**
     * Programmatically exits the application.
     */
    public static void exit ()
    {
        try
        {
            instance.destroyApp( true );
        }
        catch (MIDletStateChangeException e)
        {
            // Ignore.
        }
    }
}