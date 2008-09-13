package net.sf.bt747.j4me.app;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.TextBox;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;

public class LogConditionsConfigScreen extends Dialog implements ModelListener {

    private DeviceScreen previous;
    private AppController c;

    // private final RadioButton source; // (elements with source.append);
    private final TextBox tbTime;
    private final TextBox tbSpeed;
    private final TextBox tbDistance;
    private final TextBox tbFix;

    public LogConditionsConfigScreen(AppController c, DeviceScreen previous) {
        this.previous = previous;
        this.c = c;

        setTitle("Configure log conditions");

        tbTime = new TextBox();
        tbTime.setForDecimalOnly();
        tbTime.setLabel("Time period (s)");
        append(tbTime);

        tbSpeed = new TextBox();
        tbSpeed.setForNumericOnly();
        tbSpeed.setLabel("Speed threshold (s)");
        append(tbSpeed);

        tbDistance = new TextBox();
        tbDistance.setForDecimalOnly();
        tbDistance.setLabel("Distance interval (m)");
        append(tbDistance);

        tbFix = new TextBox();
        tbFix.setForNumericOnly();
        tbFix.setLabel("GPS Fix period (ms)");
        append(tbFix);

    }

    private final AppModel m() {
        return c.getAppModel();
    }

    private boolean isDataRequested = false;

    public void showNotify() {
        if(!isDataRequested) {
            c.reqLogReasonStatus(); // TODO: Should be done on actual initial entry
            c.reqFixInterval();
            updateButtons();
        }
        m().addListener(this); // Does not matter if double addition.
    }
    
    public void hideNotify() {
        m().removeListener(this); // Does not matter if double addition.
        super.hideNotify();
    }

    public final void updateButtons() {
        tbTime.setString(Convert.toString(
                ((float) m().getLogTimeInterval()) / 10, 1));
        tbSpeed.setString(Convert.toString(m().getLogSpeedInterval()));
        tbDistance.setString(Convert.toString((float) m()
                .getLogDistanceInterval() / 10, 1));
        tbFix.setString(Convert.toString(m().getLogFixPeriod()));
        repaint();
    }

    public final void setSettings() {
        c.setLogTimeInterval((int) (10 * Convert.toFloat(tbTime.getString())));
        c.setLogSpeedInterval(Convert.toInt(tbSpeed.getString()));
        c.setLogDistanceInterval((int) (10 * Convert.toFloat(tbDistance
                .getString())));
        c.setFixInterval((int) (Convert.toInt(tbFix.getString())));
        Log.debug("Log condition settings updated");
        // c.setFixInterval(Convert.toInt(edFix.getText()));
        c.reqLogReasonStatus();
        c.reqFixInterval();
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
        switch(e.getType()) {
        case ModelEvent.DATA_UPDATE:
        case ModelEvent.GPS_FIX_DATA:
            updateButtons();
            repaint();
            break;
        }
    }
}
