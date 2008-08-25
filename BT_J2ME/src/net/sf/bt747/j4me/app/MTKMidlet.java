package net.sf.bt747.j4me.app;

import gps.log.GPSRecord;

import java.util.Enumeration;

import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.j4me.examples.log.LogScreen;
import org.j4me.logging.Level;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.UIManager;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * The entry point for the application.
 */
public class MTKMidlet extends MIDlet implements ModelListener {
    /**
     * The one and only instance of this class.
     */
    private static MTKMidlet instance;

    AppModel m;
    AppController c;

    /**
     * Constructs the midlet. This is called before
     * &lt;code&gt;startApp&lt;/code&gt;.
     */
    public MTKMidlet() {
        try {
            instance = this;
            Log.setLevel(Level.DEBUG);
            m = new AppModel();
            c = new AppController(m);
        } catch (Throwable t) {
            Log.warn("Unhandled exception ", t);
            LogScreen l = new LogScreen(null);
            l.show();
        }

    }

    /**
     * Called when the application is minimized. For example when their is an
     * incoming call that is accepted or when the phone&#x27;s hangup key is
     * pressed.
     * 
     * @see javax.microedition.midlet.MIDlet#pauseApp()
     */
    protected void pauseApp() {
    }

    /**
     * Called when the application starts. Shows the first screen.
     * 
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    protected void startApp() throws MIDletStateChangeException {
        // Initialize the J4ME UI manager.
        try {
        UIManager.init(this);

        Enumeration roots = FileSystemRegistry.listRoots();
        String dir = "";
        while (roots.hasMoreElements()) {
            dir = "/" + (String) roots.nextElement();
        }
        if (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }
        c.setBaseDirPath(dir);
        Log.info("Basedir set to:" + m.getBaseDirPath());
        c.setDebug(true);
        c.setDebugConn(false);
        m.addListener(this);

        // Change the theme.
        // UIManager.setTheme( new org.j4me.examples.ui.themes.RedTheme() );

        // Show the first screen.

        // FindingGPSDevicesAlert creates a list of devices, then calls
        // SelectGPSScreen which will in turn call
        // InitializingGPSAlert
        DeviceScreen last = new GPSInfo(c);
        DeviceScreen next = new InitializingGPSAlert(c, last);
        DeviceScreen first = new FindingGPSDevicesAlert(c, next);
        first.show();
        } catch (Throwable t) {
            Log.warn("Unhandled exception ",t);
            LogScreen l = new LogScreen(null);
            l.show();
        }
    }

    /**
     * Called when the application is exiting.
     * 
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        // Add cleanup code here.

        // Exit the application.
        notifyDestroyed();
    }

    /**
     * Programmatically exits the application.
     */
    public static void exit() {
        try {
            instance.destroyApp(true);
        } catch (MIDletStateChangeException e) {
            // Ignore.
        }
    }

    public void modelEvent(ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.DEBUG_MSG:
            Log.debug((String) e.getArg());
            break;
        case ModelEvent.GPGGA:
            GPSRecord g = (GPSRecord) e.getArg();
            Log.debug("Lat:" + g.latitude + " Lon:" + g.longitude);
        }
    }
}