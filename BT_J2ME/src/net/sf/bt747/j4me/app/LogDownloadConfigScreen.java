package net.sf.bt747.j4me.app;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.RadioButton;
import org.j4me.ui.components.TextBox;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;

public class LogDownloadConfigScreen extends Dialog implements ModelListener {

    private final DeviceScreen previous;
    private final AppController c;

    // private final RadioButton source; // (elements with source.append);
    private final TextBox tbChunkSize;
    private final TextBox tbChunkAhead;
    private final RadioButton rbDownloadMethod;

    public LogDownloadConfigScreen(AppController c, DeviceScreen previous) {
        this.previous = previous;
        this.c = c;

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
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    public void showNotify() {
        m().addListener(this); // Does not matter if double addition.
    }
    
    public void hideNotify() {
        m().removeListener(this); // Does not matter if double addition.
        super.hideNotify();
    }

    public final void updateButtons() {
        tbChunkSize.setString(Convert.toString(m().getChunkSize()));
        tbChunkAhead.setString(Convert.toString(m().getLogRequestAhead()));
        rbDownloadMethod.setSelectedIndex(m().getDownloadMethod());
        repaint();
    }

    public final void setSettings() {
        c.setChunkSize( Convert.toInt(tbChunkSize.getString()));
        c.setLogRequestAhead(Convert.toInt(tbChunkAhead.getString()));
        c.setDownloadMethod(rbDownloadMethod.getSelectedIndex());
        Log.debug("Log download settings updated");
        // c.setFixInterval(Convert.toInt(edFix.getText()));
    }

    protected void acceptNotify() {
        m().removeListener(this);
        setSettings();
        previous.show();
        super.acceptNotify();
    }

    protected void declineNotify() {
        m().removeListener(this);
        previous.show();
        super.declineNotify();
    }

    public void modelEvent(ModelEvent e) {
//        if (e.getType() == ModelEvent.DATA_UPDATE) {
//            updateButtons();
//            repaint();
//        }
    }
}
