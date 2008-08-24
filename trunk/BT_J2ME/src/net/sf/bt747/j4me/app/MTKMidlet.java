package net.sf.bt747.j4me.app;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.j4me.logging.Level;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
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
    
    AppModel m;
    AppController c;
    /**
     * Constructs the midlet.  This is called before &lt;code&gt;startApp&lt;/code&gt;.
     */
    public MTKMidlet ()
    {
        instance = this;
        Log.setLevel(Level.DEBUG);
        Log.debug("Started");
        m = new AppModel();
        c = new AppController(m);

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
        
        // FindingGPSDevicesAlert creates a list of devices, then calls
        // SelectGPSScreen which will in turn call
        // InitializingGPSAlert
        DeviceScreen next = new InitializingGPSAlert(c,null);
        DeviceScreen first = new FindingGPSDevicesAlert(c,next);
        first.show();
        Log.debug("Start app end");
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