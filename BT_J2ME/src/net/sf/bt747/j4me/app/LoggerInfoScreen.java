package net.sf.bt747.j4me.app;

import java.util.Date;

import javax.microedition.lcdui.Font;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.HorizontalRule;
import org.j4me.ui.components.Label;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;

public class LoggerInfoScreen extends Dialog implements ModelListener {

    /**
     * A large font used for section headings.
     */
    private static final Font LARGE_FONT = Font.getFont(Font.FACE_SYSTEM,
            Font.STYLE_BOLD, Font.SIZE_LARGE);

    /**
     * The normal font used for data.
     */
    private static final Font NORMAL_FONT = Font.getFont(Font.FACE_SYSTEM,
            Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

    private FieldValue logRCRTime = new FieldValue("Time log interval (ms)");
    private FieldValue logRCRSpeed = new FieldValue("Speed log interval (km/h)");
    private FieldValue logRCRDistance = new FieldValue(
            "Distance log interval (m)");

    private FieldValue memoryTotal = new FieldValue("Memory size (bytes)");
    private FieldValue memoryUsed = new FieldValue("Memory used (bytes)");
    private FieldValue memoryUsedPercent = new FieldValue("Memory used (%)");
    private FieldValue memoryUsedRecords = new FieldValue(
            "Logged positions (#)");
    private FieldValue memoryAvailRecords = new FieldValue(
            "Available positions (#)");

    private DeviceScreen previous;

    private AppController c;

    public LoggerInfoScreen(final AppController c, final DeviceScreen previous) {

        this.c = c;
        this.previous = previous;

        // Set the menu bar options.
        setMenuText("Back", null);

        // Show the state of the location provider.
        // state.setHorizontalAlignment( Graphics.HCENTER );
        // setStateLabel( model.getLocationProvider().getState() );
        // append( state );

        // Create a UI section for pedometer information.
        createNewSection("Log conditions");
        append(logRCRTime);
        append(logRCRSpeed);
        append(logRCRDistance);

        // Create a UI section for location information.
        createNewSection("Memory");

        append(memoryTotal);
        append(memoryUsed);
        append(memoryUsedPercent);
        append(memoryUsedRecords);
        append(memoryAvailRecords);

        // Register for location updates.
        // LocationProvider provider = model.getLocationProvider();
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
        header.setFont(LARGE_FONT);
        header.setLabel(title);
        append(header);
    }

    public final void showNotify() {
        c.getAppModel().addListener(this);
        reqLogInfo();
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

            setFont(NORMAL_FONT);
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
            logRCRTime.setLabel(Convert.toString(m().getLogTimeInterval()/10.,1));
            logRCRSpeed.setLabel(m().getLogSpeedInterval());
            logRCRDistance.setLabel(Convert.toString(m().getLogDistanceInterval()/10.,1));

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

    public final void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.DATA_UPDATE:
            updateData();
            break;
        default:
            break;
        }

    }

}
