package net.sf.bt747.j4me.app;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.CheckBox;

import bt747.sys.File;

public class DebugConfigScreen extends Dialog {

    private DeviceScreen previous;
    private AppController c;

    // private final RadioButton source; // (elements with source.append);
    private final CheckBox cbConsoleToFile;
    private final CheckBox cbGpsRawDebug;
    private final CheckBox cbGeneralDebug;

    public DebugConfigScreen(AppController c, DeviceScreen previous) {
        this.previous = previous;
        this.c = c;

        setTitle("Configure log conditions");

        cbConsoleToFile = new CheckBox();
        cbConsoleToFile.setChecked(false);
        cbConsoleToFile.setLabel("Write console to file");
        append(cbConsoleToFile);

        cbGpsRawDebug = new CheckBox();
        cbGpsRawDebug.setChecked(false);
        cbGpsRawDebug.setLabel("Write all serial communication to file");
        append(cbGpsRawDebug);

        cbGeneralDebug = new CheckBox();
        cbGeneralDebug.setChecked(false);
        cbGeneralDebug.setLabel("Enable extra debug console info");
        append(cbGeneralDebug);
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    public void showNotify() {
    }

    public void hideNotify() {
        super.hideNotify();
    }

    public final void updateButtons() {
        repaint();
    }

    private boolean consoleIsOpen = false;

    private void setupConsoleFile(boolean doOpen) {
        Log.info("Setup up console to file");
        if (!doOpen) {
            if (consoleIsOpen) {
                Log.setOutputStream(null);
            }
            consoleIsOpen = false;
        } else {
            if (!consoleIsOpen) {
                try {
                    String fn = "file://" + m().getBaseDirPath()
                            + File.separatorStr + "BT747Console.log";
                    FileConnection fc;
                    try {
                        fc = (FileConnection) Connector.open(fn);
                        if (fc.exists()) {
                            fc.delete();
                        }
                        fc.close();
                    } catch (Throwable e) {
                        Log.debug("Delete", e);
                    }

                    try {
                        fc = (FileConnection) Connector.open(fn);
                        fc.create();
                        fc = (FileConnection) Connector.open(fn,
                                Connector.WRITE);
                        Log.setOutputStream(fc.openOutputStream());
                    } catch (IOException e) {
                        Log.debug("Open " + fn, e);
                    }
                    consoleIsOpen = true;
                } catch (Throwable e) {
                    Log.debug("Open console", e);
                }
            }
        }
    }

    public final void setSettings() {
        c.setDebug(cbGeneralDebug.isChecked());
        c.setDebugConn(cbGpsRawDebug.isChecked());

        setupConsoleFile(cbConsoleToFile.isChecked());
    }

    protected void acceptNotify() {
        setSettings();
        previous.show();
        super.acceptNotify();
    }

    protected void declineNotify() {
        previous.show();
        super.declineNotify();
    }

}
