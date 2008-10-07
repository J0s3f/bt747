package net.sf.bt747.j4me.app;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.CheckBox;

public final class DebugConfigScreen extends Dialog {

    private DeviceScreen previous;
    private AppController c;

    // private final RadioButton source; // (elements with source.append);
    private CheckBox cbConsoleToFile;
    private CheckBox cbGpsRawDebug;
    private CheckBox cbGeneralDebug;
    private CheckBox cbPersistantDebug;

    private boolean screenSetup = false;

    public DebugConfigScreen(AppController c, DeviceScreen previous) {
        this.previous = previous;
        this.c = c;
    }
    
    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            setTitle("Configure log conditions");

            cbConsoleToFile = new CheckBox();
            cbConsoleToFile.setChecked(c.isUseConsoleFile());
            cbConsoleToFile.setLabel("Write console to file");
            append(cbConsoleToFile);

            cbGpsRawDebug = new CheckBox();
            cbGpsRawDebug.setChecked(m().isDebugConn());
            cbGpsRawDebug.setLabel("Write all serial communication to file");
            append(cbGpsRawDebug);

            cbGeneralDebug = new CheckBox();
            cbGeneralDebug.setChecked(AppModel.isDebug());
            cbGeneralDebug.setLabel("Enable extra debug console info");
            append(cbGeneralDebug);

            cbPersistantDebug = new CheckBox();
            cbPersistantDebug.setChecked(c.isPersistentDebug());
            cbPersistantDebug.setLabel("Keep debug options on startup.");
            append(cbPersistantDebug);

        }
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    public final void showNotify() {
        setupScreen();
    }

    public final void hideNotify() {
        super.hideNotify();
    }

    public final void updateButtons() {
        repaint();
    }

    public final void setSettings() {
        c.setDebug(cbGeneralDebug.isChecked());
        c.setDebugConn(cbGpsRawDebug.isChecked());
        c.setPersistentDebug(cbPersistantDebug.isChecked());
        c.setUseConsoleFile(cbConsoleToFile.isChecked());
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
