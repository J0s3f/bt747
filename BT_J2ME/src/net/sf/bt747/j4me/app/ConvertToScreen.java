package net.sf.bt747.j4me.app;

import gps.BT747Constants;
import gps.log.GPSFilter;

import org.j4me.examples.ui.screens.*;
import org.j4me.logging.*;
import org.j4me.ui.*;

import bt747.model.Model;

/**
 * The "Initializing GPS..." alert screen. This screen is used to get the
 * <code>LocationProvider</code> for the application. It first tries to get a
 * provider on the device. But if it cannot it will get a GPS provider through a
 * Bluetooth connection.
 */
public class ConvertToScreen extends ProgressAlert {
    /**
     * The location information for this application.
     */
    private final AppController c;

    /**
     * The screen that came before this one. If the user cancels the the process
     * or if it fails it will be returned to.
     */
    private final DeviceScreen previous;

    /**
     * Constructs the "Initializing GPS..." alert screen.
     * 
     * @param model
     *            is the application's location data.
     * @param previous
     *            is the screen that came before this one.
     */
    public ConvertToScreen(final AppController c, final DeviceScreen previous) {
        super("Convert Log", "Converting Log File");

        this.c = c;
        this.previous = previous;
    }

    /**
     * Called when the user presses the alert's dismiss button.
     */
    public void onCancel() {
        // Log.debug("Canceling GPS initialization.");
        //
        // // Go back to the previous screen.
        // previous.show();
    }

    /**
     * A worker thread that gets the GPS <c>LocationProvider</c>. The thread
     * will set the next screen when it is done.
     */
    protected final DeviceScreen doWork() {
        DeviceScreen next = previous;

        // String text = getText() + "\n" + "Using device: " + deviceName;
        // setText(text);

        try {
            Log.info("Starting log conversion");

            // We select all positions that do not have an invalid fix or
            // estimated
            // fix.

            int error;
            error = c.doConvertLog(Model.GPX_LOGTYPE);

            if (error != 0) {
                Log.error(c.getLastError() + " " + c.getLastErrorInfo());
            }
        } catch (Exception e) {
            Log.error("Exception during log convert", e);
        }

        return next;
    }
}
