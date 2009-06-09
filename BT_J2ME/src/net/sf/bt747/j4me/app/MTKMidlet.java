package net.sf.bt747.j4me.app;

import gps.connection.GPSrxtx;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import net.sf.bt747.j2me.system.J2MEJavaTranslations;
import net.sf.bt747.j4me.app.conn.BluetoothLocationProvider;

import org.j4me.logging.Level;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.UIManager;

import bt747.sys.JavaLibBridge;

/**
 * The entry point for the application.
 */
public class MTKMidlet extends MIDlet implements CommandListener {
    /**
     * The one and only instance of this class.
     */
    private static MTKMidlet instance;

    static {
        JavaLibBridge.setJavaLibImplementation(new J2MEJavaTranslations());
    }

    private static AppModel m;
    private static AppController c;

    private final static void setAppModel(final AppModel mm) {
        m = mm;
    }

    private final static void setAppController(final AppController cc)
            throws IOException {
        c = cc;
    }

    private volatile boolean ok = false;

    private static MTKMidlet appSingleton;

    /**
     * Constructs the midlet. This is called before
     * &lt;code&gt;startApp&lt;/code&gt;.
     */
    public MTKMidlet() {
        appSingleton = this;
        try {
            UIManager.init(this);
            MTKMidlet.setInstance(this);
            ok = true;
        } catch (final Throwable t) {
            displayThrowable(t, "MTKMidlet");
        }
    }

    private static void setInstance(final MTKMidlet midlet) {
        MTKMidlet.instance = midlet;
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

    protected Command commandExit;

    /**
     * Called when the application starts. Shows the first screen.
     * 
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    protected final void startApp() throws MIDletStateChangeException {
        if (!ok) {
            return;
        }
        // Initialize the J4ME UI manager.
        try {
//            Log.setLevel(Level.DEBUG);
//            Log.info("Before appModel");
            BluetoothLocationProvider provider = null;
            try {
                provider = BluetoothLocationProvider.getInstance();
//                Log.info("Provider ok " + provider);
            } catch (Exception e) {
                Log.error("Getting provider.", e);
            }
            try {
                if (provider != null) {
                    GPSrxtx.setDefaultGpsPortInstance(provider);
                }
//                Log.info("Port instance ok" + provider);
            } catch (Exception e) {
                Log.error("Setting port instance.", e);
            }

            AppController.initAppSettings();
            setAppModel(new AppModel());
//            Log.info("Before appController");
            setAppController(new AppController(MTKMidlet.m));

            final DeviceScreen main = new MainScreen(MTKMidlet.c, this);
            // Change the theme.
            // Show the first screen.

            Log.info("Before main.show");
            // FindingGPSDevicesAlert creates a list of devices, then calls
            // SelectGPSScreen which will in turn call
            // InitializingGPSAlert

            // (new ConvertTo(c, main)).doWork(); // Debug conversion
            main.show();
            // (new ConvertTo(c, main)).show();
        } catch (final Exception t) {
            displayThrowable(t, "MainScreen");
            // throw t;
        }
    }

    public final static void displayThrowable(final Throwable t,
            final String n) {
        if ((t != null) || (n != null)) {
            try {
                try {
                    UIManager.getDisplay();
                } catch (final Exception e) {
                    UIManager.init(appSingleton);
                    Log.setLevel(Level.DEBUG);
                    if (t != null) {
                        Log.warn("Exception thrown ", t);
                    }
                }

            } catch (final Exception e) {
                // Log.warn("Exception in display throwable");
                // Don't care
            }
            // Log.info("Display throwable",t);
            // LogScreen l = new LogScreen(null);
            // l.show();
            Display disp;
            Form form;

            // fait un lien avec l'affichage
            disp = Display.getDisplay(appSingleton);

            // creation d'un objet formulaire sur lequel on peut placer des
            // composants

            form = new Form("BT747 "
                    + appSingleton.getAppProperty("MIDlet-Version")
                    + " Exception");

            // creation d'un bouton pour sortir du programme
            appSingleton.commandExit = new Command("Exit", Command.SCREEN, 1);

            // Next lines for future reference (must be deleted here)
            // javax.microedition.lcdui.DateField d;
            // d = new DateField("help",DateField.DATE);
            javax.microedition.lcdui.TextField tb;
            String f = "";
            if (t != null) {
                final String s = t.getMessage();
                final String b = t.toString();
                if (b != null) {
                    f += b;
                }
                if (s != null) {
                    if (f.length() > 0) {
                        f += "\n";
                    }
                    f += s;
                }
            }
            if (n != null) {
                f += "\n" + n;
            }
            tb = new TextField(f, f, 250, TextField.ANY);

            // creation d'un champ de texte contenant notre Hello World
            // ajout des composants au formulaire
            form.addCommand(appSingleton.commandExit);
            form.append(tb);
            // tb.setLabel(t.getMessage());
            // tb.setString(t.getMessage());
            form.setCommandListener(appSingleton);

            // affichage du formulaire
            disp.setCurrent(form);
        }
    }

    /**
     * Called when the application is exiting.
     * 
     * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
     */
    protected final void destroyApp(final boolean arg0)
            throws MIDletStateChangeException {
        // Add cleanup code here.
        if (MTKMidlet.c != null) {
            MTKMidlet.c.saveSettings();
        }
        // Exit the application.
        notifyDestroyed();
    }

    /**
     * Programmatically exits the application.
     */
    public static void exit() {
        try {
            MTKMidlet.instance.destroyApp(true);
        } catch (final MIDletStateChangeException e) {
            // Ignore.
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
     *      javax.microedition.lcdui.Displayable)
     */
    public void commandAction(final Command c, final Displayable s) {
        // lors du clic sur le bouton Exit
        if (c == commandExit) {
            // appel manuel à la fonction de fermeture
            try {
                destroyApp(false);
            } catch (final Exception e) {
                // TODO: handle exception
            }
            // on demande au manager de fermer l'application
            notifyDestroyed();
        }
    }

}
