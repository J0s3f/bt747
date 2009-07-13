package net.sf.bt747.j4me.app;

import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.components.RadioButton;
import org.j4me.ui.components.TextBox;

import bt747.model.AppSettings;
import bt747.sys.JavaLibBridge;

public final class LogDownloadConfigScreen extends BT747Dialog {
    private TextBox tbChunkSize;
    private TextBox tbChunkAhead;
    private RadioButton rbDownloadMethod;

    private boolean screenIsSetup = false;

    private void setupScreen() {
        if (!screenIsSetup) {
            screenIsSetup = true;
            deleteAll();
            setTitle("Log download configuration");

            tbChunkSize = new TextBox();
            tbChunkSize.setForDecimalOnly();
            tbChunkSize.setLabel("Download chunk size");
            append(tbChunkSize);

            tbChunkAhead = new TextBox();
            tbChunkAhead.setForNumericOnly();
            tbChunkAhead.setLabel("Request queue size");
            append(tbChunkAhead);

            rbDownloadMethod = new RadioButton();

            rbDownloadMethod.append("Normal download");
            rbDownloadMethod.append("Smart Download");
            rbDownloadMethod.append("Full download");
            append(rbDownloadMethod);
            updateButtons();
            invalidate();
        }
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    public void showNotify() {
        Log.debug("Log download settings");
        setupScreen();
    }

    private final void updateButtons() {
        tbChunkSize.setString("" + m().getChunkSize());
        tbChunkAhead.setString("" + m().getIntOpt(AppSettings.LOGAHEAD));
        rbDownloadMethod.setSelectedIndex(m().getDownloadMethod());
        repaint();
    }

    private final void setSettings() {
        c.setChunkSize(JavaLibBridge.toInt(tbChunkSize.getString()));
        c.setLogRequestAhead(JavaLibBridge.toInt(tbChunkAhead.getString()));
        c.setDownloadMethod(rbDownloadMethod.getSelectedIndex());
        Log.debug("Log download settings updated");
        // c.setFixInterval(JavaLibBridge.toInt(edFix.getText()));
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