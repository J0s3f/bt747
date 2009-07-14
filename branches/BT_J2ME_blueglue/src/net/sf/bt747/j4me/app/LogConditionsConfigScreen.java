package net.sf.bt747.j4me.app;

import gps.GpsEvent;
import gps.mvc.MtkModel;
import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.components.TextBox;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;

public final class LogConditionsConfigScreen extends BT747Dialog implements
        ModelListener {

    // private final RadioButton source; // (elements with source.append);
    private TextBox tbTime;
    private TextBox tbSpeed;
    private TextBox tbDistance;
    private TextBox tbFix;

    boolean screenSetup = false;

    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            setTitle("Configure log conditions");

            tbTime = new TextBox();
            tbTime.setForDecimalOnly();
            tbTime.setLabel("Time period (s)");
            append(tbTime);

            tbSpeed = new TextBox();
            tbSpeed.setForNumericOnly();
            tbSpeed.setLabel("Speed threshold (km/h)");
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
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    private boolean isDataRequested = false;

    public void showNotify() {
        setupScreen();
        if (!isDataRequested) {
            isDataRequested = true;
            c.setMtkDataNeeded(MtkModel.DATA_LOG_TIME_INTERVAL);
            c.setMtkDataNeeded(MtkModel.DATA_LOG_SPEED_INTERVAL);
            c.setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL); // TODO: Should be done on actual initial
            // entry
            c.setMtkDataNeeded(MtkModel.DATA_FIX_PERIOD);
            updateButtons();
        }
        m().addListener(this); // Does not matter if double addition.
    }

    public void hideNotify() {
        m().removeListener(this); // Does not matter if double removal
        super.hideNotify();
    }

    public final void updateButtons() {
        tbTime.setString(JavaLibBridge.toString(
                ((float) m().getLogTimeInterval()) / 10, 1));
        tbSpeed.setString("" + m().getLogSpeedInterval());
        tbDistance.setString(JavaLibBridge.toString((float) m()
                .getLogDistanceInterval() / 10, 1));
        tbFix.setString("" + m().getLogFixPeriod());
        repaint();
    }

    public final void setSettings() {
        c
                .setLogTimeInterval((int) (10 * JavaLibBridge.toFloat(tbTime
                        .getString())));
        c.setLogSpeedInterval(JavaLibBridge.toInt(tbSpeed.getString()));
        c.setLogDistanceInterval((int) (10 * JavaLibBridge.toFloat(tbDistance
                .getString())));
        c.setFixInterval((JavaLibBridge.toInt(tbFix.getString())));
        Log.debug("Log condition settings updated");
        // c.setFixInterval(JavaLibBridge.toInt(edFix.getText()));
        c.setMtkDataNeeded(MtkModel.DATA_LOG_TIME_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_LOG_SPEED_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_FIX_PERIOD);
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

    public void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case GpsEvent.UPDATE_LOG_TIME_INTERVAL:
        case GpsEvent.UPDATE_LOG_DISTANCE_INTERVAL:
        case GpsEvent.UPDATE_LOG_SPEED_INTERVAL:
        case GpsEvent.UPDATE_FIX_PERIOD:
            updateButtons();
            repaint();
            break;
        }
    }
}