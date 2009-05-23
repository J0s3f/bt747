package net.sf.bt747.j4me.app.conn;

import net.sf.bt747.j4me.app.AppController;
import net.sf.bt747.j4me.app.LoggerStatusScreen;
import net.sf.bt747.j4me.app.screens.ErrorAlert;
import net.sf.bt747.j4me.app.screens.ProgressAlert;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;

/**
 * The "Initializing GPS..." alert screen. This screen is used to get the
 * <code>LocationProvider</code> for the application. It first tries to get
 * a provider on the device. But if it cannot it will get a GPS provider
 * through a Bluetooth connection.
 */
public class InitializingGPSAlert extends ProgressAlert {
    /**
     * The location information for this application.
     */
    private final AppController c;

    /**
     * The screen that came before this one. If the user cancels the the
     * process or if it fails it will be returned to.
     */
    private DeviceScreen previous;

    /**
     * Constructs the "Initializing GPS..." alert screen.
     * 
     * @param model
     *                is the application's location data.
     * @param previous
     *                is the screen that came before this one.
     */
    public InitializingGPSAlert(final AppController c,
            final DeviceScreen previous) {
        super("Initializing GPS...", "Connecting to the location provider.");

        this.c = c;
        this.previous = previous;
    }

    /**
     * Called when the user presses the alert's dismiss button.
     */
    public final void onCancel() {
        Log.debug("Canceling GPS initialization.");

        // Go back to the previous screen.
        previous.show();
    }

    /**
     * A worker thread that gets the GPS <c>LocationProvider</c>. The thread
     * will set the next screen when it is done.
     */
    protected final DeviceScreen doWork() {
        BluetoothLocationProvider provider = null;

        final String deviceName = c.getAppModel().getBluetoothGPSName();

        final String text = "Connecting to the location provider.\n"
                + "Using device:  " + deviceName;
        
        setText(text);
        
        DeviceScreen next = previous;

        try {
            // Get the GPS provider.
            // Synchronize on our Bluetooth lock in case the user hits the
            // cancel
            // button and tries to do Bluetooth device discovery to find new
            // GPS.
            synchronized (FindingGPSDevicesAlert.BLUETOOTH_LOCK) {
                // First close any open provider.
                // For example if connected to one GPS device and are
                // switching
                // to
                // another.
                final BluetoothLocationProvider old = c.getAppModel()
                        .getGpsBluetoothConnection();

                if (old != null) {
                    old.close();
                }

                // Get the new provider.
                try {
                    provider = BluetoothLocationProvider.getInstance();
                } catch (Exception e) {
                    Log.error("Can not get BT provider", e);
                    next = previous;
                }
            }

            // Set the provider on the model.
            // Note that if we are using on device GPS (i.e. JSR 179) this is
            // the method that will throw the SecurityException, not the above
            // getProvider() method (which would throw it for GPS through
            // Bluetooth).
            c.getAppModel().setGpsBluetoothConnection(provider);
            Log.info("Try to open port: "
                    + c.getAppModel().getBluetoothGPSURL());
            c.openFreeTextPort(c.getAppModel().getBluetoothGPSURL());
            // c.connectGPS();

            // Did we get a GPS location provider?
            if (provider != null) {
                // Alert the user we are waiting for a fix from the GPS.
                // TODO: continue application
                // next = new AcquiringLocationAlert(model, previous);
            } else {
                // There was no location provider that matched the criteria.
                Log.info("No location provider matched the criteria.");
                next = new ErrorAlert("GPS Error",
                        "No location provider matched the criteria.",
                        previous);
            }
        } catch (final SecurityException e) {
            Log.error("The user blocked access to the location provider.", e);
            next = new ErrorAlert(
                    "GPS Error",
                    "You must allow access for the application to work.\nPlease restart and allow all connections.",
                    previous);
        }

        return next;
    }
}
