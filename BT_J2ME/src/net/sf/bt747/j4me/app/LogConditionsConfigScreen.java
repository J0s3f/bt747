package net.sf.bt747.j4me.app;

import gps.GpsEvent;
import gps.mvc.MtkModel;
import net.sf.bt747.j4me.app.screens.BT747Dialog;
import net.sf.bt747.j4me.app.screens.ErrorAlert;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.components.Label;
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

    private final boolean isTimeAndDistanceExclusive() {
        final MtkModel mtk = m().mtkModel();
        return mtk.isTimeDistanceLogConditionExclusive();
    }

    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            setTitle("Configure log conditions");

            if (isTimeAndDistanceExclusive()) {
                append(new Label(
                        "Time and Distance conditions are mutually exclusive!"));
            }
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
            c.setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL); // TODO:
            // Should
            // be
            // done
            // on
            // actual
            // initial
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
        tbTime.setString(JavaLibBridge.toString(((float) m()
                .getLogTimeInterval()) / 10, 1));
        tbSpeed.setString("" + m().getLogSpeedInterval());
        tbDistance.setString(JavaLibBridge.toString((float) m()
                .getLogDistanceInterval() / 10, 1));
        tbFix.setString("" + m().getLogFixPeriod());
        repaint();
    }

    public DeviceScreen setSettings() {
        DeviceScreen nextScreen = null;
        c.setLogSpeedInterval(JavaLibBridge.toInt(tbSpeed.getString()));
        int timeCond;
        int distCond;
        timeCond = (int) (10 * JavaLibBridge.toFloat(tbTime.getString()));
        distCond = (int) (10 * JavaLibBridge.toFloat(tbDistance.getString()));
        if (isTimeAndDistanceExclusive() && timeCond != 0 && distCond != 0) {
            distCond = 0;
            nextScreen = new ErrorAlert(
                    "Time & Distance",
                    "Time and distance conditions mutually exclusive."
                            + "  Only time has been used, distance was ignored.",
                    previous);
        }
        if (!isTimeAndDistanceExclusive() || distCond == 0) {
            c.setLogTimeInterval((int) (10 * JavaLibBridge.toFloat(tbTime
                    .getString())));
        }
        if (!isTimeAndDistanceExclusive() || distCond != 0) {
            c.setLogDistanceInterval((int) (10 * JavaLibBridge
                    .toFloat(tbDistance.getString())));
        }
        c.setFixInterval((JavaLibBridge.toInt(tbFix.getString())));
        Log.debug("Log condition settings updated");
        // c.setFixInterval(JavaLibBridge.toInt(edFix.getText()));
        c.setMtkDataNeeded(MtkModel.DATA_LOG_TIME_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_LOG_SPEED_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_FIX_PERIOD);
        return nextScreen;
    }

    protected void acceptNotify() {
        m().removeListener(this);
        DeviceScreen errorScreen;
        errorScreen = setSettings();
        if (errorScreen != null) {
            errorScreen.show();
        } else {
            previous.show();
        }
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
