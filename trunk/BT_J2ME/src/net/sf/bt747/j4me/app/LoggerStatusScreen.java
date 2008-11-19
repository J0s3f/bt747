package net.sf.bt747.j4me.app;

import java.util.Date;

import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.HorizontalRule;
import org.j4me.ui.components.Label;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Semaphore;

public final class LoggerStatusScreen extends BT747Dialog implements
        ModelListener, Runnable {

    private FieldValue logActive = new FieldValue("Logging is:");
    private FieldValue logRCRTime = new FieldValue("Time interval (s)");
    private FieldValue logRCRSpeed = new FieldValue("Speed interval (km/h)");
    private FieldValue logRCRDistance = new FieldValue("Distance interval (m)");

    private FieldValue memoryTotal = new FieldValue("Size (bytes)");
    private FieldValue memoryUsed = new FieldValue("Used (bytes)");
    private FieldValue memoryUsedPercent = new FieldValue("Used (%)");
    private FieldValue memoryUsedRecords = new FieldValue("Logged pos (#)");
    private FieldValue memoryAvailRecords = new FieldValue("Available pos (#)");

    private boolean screenSetup = false;

    public void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            deleteAll();

            // Set the menu bar options.
            setMenuText("Back", null);

            // Show the state of the location provider.
            // state.setHorizontalAlignment( Graphics.HCENTER );
            // setStateLabel( model.getLocationProvider().getState() );
            // append( state );

            // Create a UI section for memory information.
            createNewSection("Memory");

            append(memoryTotal);
            append(memoryUsed);
            append(memoryUsedPercent);
            append(memoryUsedRecords);
            append(memoryAvailRecords);

            // Create a UI section for pedometer information.
            createNewSection("Log conditions");
            append(logActive);
            append(logRCRTime);
            append(logRCRSpeed);
            append(logRCRDistance);
        }
    }

    /**
     * Adds components for a new section of information.
     * 
     * @param title
     *            is the name of the section.
     */
    private void createNewSection(final String title) {
        append(new HorizontalRule());

        Label header = new Label();
        header.setFont(UIManager.getTheme().getMenuFont());
        header.setLabel(title);
        append(header);
    }

    public final void showNotify() {
        setupScreen();
        c.getAppModel().addListener(this);
        reqLogInfo();
        planUpdateLock.down();
        planUpdate = true;
        planUpdateLock.up();
        Thread worker = new Thread(this);
        worker.start();
        // updateData();
        super.showNotify();
    }

    public void hideNotify() {
        c.getAppModel().removeListener(this);
        super.hideNotify();
    }

    private void reqLogInfo() {
        c.reqLogReasonStatus();
        // Request device info for this control
        c.reqLogStatus();
        // Request log version from device
        // c.reqMtkLogVersion();
        // Request mem size from device
        c.reqLogMemUsed();
        // Request number of log points
        c.reqLogMemPtsLogged();
        // c.reqLogOverwrite();
    }

    /**
     * Called when the user presses the "Back" button.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected final void declineNotify() {
        // Go back to the previous screen.
        if (previous != null) {
            previous.show();
        }
    }

    /**
     * Shows a field and its value such as "Speed (m/s): 5.0".
     */
    private static final class FieldValue extends Label {
        private final String name;

        public FieldValue(final String name) {
            this.name = name;
        }

        public void setLabel(final String label) {
            super.setLabel(name + ":  " + label);
        }

        public void setLabel(final double d) {
            String s = Double.toString(d);
            setLabel(s);
        }

        public void setLabel(final float f) {
            String s = Float.toString(f);
            setLabel(s);
        }

        public void setLabel(final Date d) {
            String s = d.toString();
            setLabel(s);
        }

        public void setLabel(final int i) {
            String s = Integer.toString(i);
            setLabel(s);
        }
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    private void updateData() {
        try {
            logActive.setLabel(m().isLoggingActive()?"ON":"OFF");
            // Log.debug(System.currentTimeMillis()+" Update data");
            logRCRTime.setLabel(Convert.toString(
                    m().getLogTimeInterval() / 10., 1));
            logRCRSpeed.setLabel(m().getLogSpeedInterval());
            logRCRDistance.setLabel(Convert.toString(m()
                    .getLogDistanceInterval() / 10., 1));

            memoryTotal.setLabel(m().logMemSize());
            memoryUsed.setLabel(m().logMemUsed());
            memoryUsedPercent.setLabel(m().logMemUsedPercent());
            memoryUsedRecords.setLabel(m().logNbrLogPts());
            memoryAvailRecords.setLabel(m().getEstimatedNbrRecordsFree(
                    m().getLogFormat()));
        } catch (Exception e) {
            Log.error("updateData", e);
        }
        repaint();
    }

    private BT747Semaphore planUpdateLock = Interface.getSemaphoreInstance(1);
    private boolean planUpdate = true;

    public final void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.UPDATE_LOG_TIME_INTERVAL:
        case ModelEvent.UPDATE_LOG_SPEED_INTERVAL:
        case ModelEvent.UPDATE_LOG_DISTANCE_INTERVAL:
        case ModelEvent.UPDATE_LOG_MEM_USED:
        case ModelEvent.UPDATE_LOG_FLASH:
        case ModelEvent.UPDATE_LOG_NBR_LOG_PTS:
            planUpdateLock.down();
            planUpdate = true;
            planUpdateLock.up();
            break;
        default:
            break;
        }

    }

    public void run() {
        while (isShown()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                // TODO: handle exception
            }
            planUpdateLock.down();
            if (planUpdate) {
                planUpdateLock.up();
                updateData();
                planUpdate = false;
            } else {
                planUpdateLock.up();
            }
        }
    }
}
