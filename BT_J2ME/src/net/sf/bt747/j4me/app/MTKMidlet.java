package net.sf.bt747.j4me.app;

import java.io.PrintStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import net.sf.bt747.j2me.system.J2MEJavaTranslations;
import net.sf.bt747.j4me.app.log.LogScreen;

import org.j4me.logging.Level;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.UIManager;

import bt747.sys.Interface;

/**
 * The entry point for the application.
 */
public class MTKMidlet extends MIDlet implements CommandListener {
    /**
     * The one and only instance of this class.
     */
    private static MTKMidlet instance;

    static {
        Interface.setJavaTranslationInterface(new J2MEJavaTranslations());
    }

    private static AppModel m;
    private static AppController c;

    /**
     * Constructs the midlet. This is called before
     * &lt;code&gt;startApp&lt;/code&gt;.
     */
    public MTKMidlet() {
        try {
            MTKMidlet.setInstance(this);
            Log.setLevel(Level.DEBUG);
            m = new AppModel();
            c = new AppController(m);
        } catch (Throwable t) {
            Log.warn("Unhandled exception ", t);
            LogScreen l = new LogScreen(null);
            l.show();
        }

    }

    private static void setInstance(final MTKMidlet midlet) {
        instance = midlet;
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

    private Command commandExit;

    /**
     * Called when the application starts. Shows the first screen.
     * 
     * @see javax.microedition.midlet.MIDlet#startApp()
     */
    protected final void startApp() throws MIDletStateChangeException {
        Throwable m = null;
        // Initialize the J4ME UI manager.
        try {
            UIManager.init(this);
            DeviceScreen main = new MainScreen(c, this);
            // Change the theme.
            // Show the first screen.

            // FindingGPSDevicesAlert creates a list of devices, then calls
            // SelectGPSScreen which will in turn call
            // InitializingGPSAlert

            // (new ConvertTo(c, main)).doWork(); // Debug conversion
            main.show();
            // (new ConvertTo(c, main)).show();
        } catch (Throwable t) {
            Log.warn("Unhandled exception ", t);
            m = t;
        }
        if(m!=null) {
            // LogScreen l = new LogScreen(null);
            // l.show();
            Display disp;
            Form form;

            // fait un lien avec l'affichage
            disp = Display.getDisplay(this);

            // creation d'un objet formulaire sur lequel on peut placer des
            // composants

            form = new Form("BT747 " + getAppProperty("MIDlet-Version")
                    + " Exception");

            // creation d'un bouton pour sortir du programme
            commandExit = new Command("Exit", Command.SCREEN, 1);

            // Next lines for future reference (must be deleted here)
            // javax.microedition.lcdui.DateField d;
            // d = new DateField("help",DateField.DATE);
            javax.microedition.lcdui.TextField tb;
            tb = new TextField("", "", 250, TextField.ANY);
            String s = m.getMessage();
            String b = m.toString();
            String f = "";
            if (b != null) {
                f += b;
            }
            if (s != null) {
                if (f.length() > 0) {
                    f += "\n";
                }
                f += s;
            }
            tb = new TextField(f, f, 250, TextField.ANY);

            // creation d'un champ de texte contenant notre Hello World
            // ajout des composants au formulaire
            form.addCommand(commandExit);
            form.append(tb);
            // tb.setLabel(t.getMessage());
            // tb.setString(t.getMessage());
            form.setCommandListener(this);

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
        if (c != null) {
            c.saveSettings();
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
     *      javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command c, Displayable s) {
        // lors du clic sur le bouton Exit
        if (c == commandExit) {
            // appel manuel à la fonction de fermeture
            try {
                destroyApp(false);
            } catch (Exception e) {
                // TODO: handle exception
            }
            // on demande au manager de fermer l'application
            notifyDestroyed();
        }
    }

}