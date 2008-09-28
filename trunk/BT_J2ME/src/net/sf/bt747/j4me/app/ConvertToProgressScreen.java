package net.sf.bt747.j4me.app;

import net.sf.bt747.j4me.app.screens.ProgressAlert;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.components.RadioButton;

/**
 * The "Initializing GPS..." alert screen. This screen is used to get the
 * <code>LocationProvider</code> for the application. It first tries to get a
 * provider on the device. But if it cannot it will get a GPS provider through a
 * Bluetooth connection.
 */
public class ConvertToProgressScreen extends ProgressAlert {
    /**
     * The location information for this application.
     */
    private final AppController c;

    /**
     * The screen that came before this one. If the user cancels the the process
     * or if it fails it will be returned to.
     */
    private final DeviceScreen previous;

    private RadioButton rbFormats;
    
    private int convertType;

    /**
     * Constructs the "Initializing GPS..." alert screen.
     * 
     * @param model
     *            is the application's location data.
     * @param previous
     *            is the screen that came before this one.
     */
    public ConvertToProgressScreen(final AppController c, final DeviceScreen previous, final int convertType) {
        super("Convert Log", "Converting Log File");
        this.convertType = convertType;
        this.c = c;
        this.previous = previous;

    }

    /**
     * Called when the user presses the alert's dismiss button.
     */
    public void onCancel() {
        Log.debug("Canceling Log conversion.");
        //
        // // Go back to the previous screen.
        // previous.show();
        c.stopLogConvert();
        previous.show();
    }

    // public void showNotify() {
    // }

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
            error = c.doConvertLog(convertType);

            if (error != 0) {
                Log.error(c.getLastError() + " " + c.getLastErrorInfo());
            }
        } catch (Exception e) {
            Log.error("Exception during log convert", e);
        }

        return next;
    }

}
