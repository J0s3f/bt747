package net.sf.bt747.j4me.app.conn;

import java.io.IOException;

import net.sf.bt747.j4me.app.AppController;
import net.sf.bt747.j4me.app.screens.ErrorAlert;
import net.sf.bt747.j4me.app.screens.ProgressAlert;

import org.j4me.bluetoothgps.LocationProvider;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;

/**
 * The "Finding GPS Devices..." alert screen. This checks for nearby Bluetooth
 * devices.
 * <p>
 * This screen is shown to the user while the application uses Bluetooth
 * dynamic discovery to create a list of nearby devices. It disappears to give
 * them a selection once it is done. This usually takes a few seconds, but
 * sometimes as many as 30.
 */
public class FindingGPSDevicesAlert extends ProgressAlert {
    /**
     * The location information for this application.
     */
    private final AppController c;

    /**
     * An object to use for locking the Bluetooth discovery process. Only one
     * can discovery process can run at a time. So if this screen is shown it
     * starts, then if the user goes back and screen and comes back to this
     * one, a second thread will start. This <code>bluetoothLock</code>
     * prevents the second thread from doing work until the first completes.
     * This is not the most efficient way of doing things because the first
     * thread's results will be discarded, but it is easy logic and only
     * requires the user wait a few more seconds and should almost never
     * happen.
     * <p>
     * This lock is declared <c>protected static</c> because it is shared
     * throughout the package.
     */
    public static final Object BLUETOOTH_LOCK = new Object();

    /**
     * The screen that came before this one. If the user cancels this alert,
     * it will go back to this screen.
     */
    private final DeviceScreen previous;
    
    private DeviceScreen next;

    /**
     * Constructs the "Finding GPS Devices..." alert screen.
     * 
     * @param c
     *                is the application's controller.
     * @param previous
     *                is the screen that came before this one.
     */
    public FindingGPSDevicesAlert(final AppController c,
            final DeviceScreen previous,
            final DeviceScreen next) {
        super("Finding GPS...", "Looking for nearby Bluetooth devices.");

        this.c = c;
        this.previous = previous;
        this.next = next;
    }

    /**
     * Called when the user presses the alert's dismiss button.
     */
    public final void onCancel() {
        Log.info("Canceling Bluetooth device discovery.");
        previous.show();
    }

    /**
     * A thread that finds nearby Bluetooth devices and sets them on a select
     * GPS device screen.
     * 
     * @return the screen to show after this thread finishes.
     */
    protected final DeviceScreen doWork() {
        String[][] devices = null;
        String errorText = null;

        synchronized (FindingGPSDevicesAlert.BLUETOOTH_LOCK) {
            // Stop any providers in case they were using Bluetooth and
            // therefore have
            // a lock on the Bluetooth socket.
            final BluetoothLocationProvider provider = c.getAppModel()
                    .getGpsBluetoothConnection();

            if (provider != null) {
                provider.close();
                c.getAppModel().setGpsBluetoothConnection(null);
            }

            // Search for Bluetooth devices (this takes several seconds).
            try {
                Log.info("Discovering Bluetooth devices.");

                devices = LocationProvider.discoverBluetoothDevices();

                if (devices == null) {
                    // The operation failed for an unknown Bluetooth reason.
                    Log.error("Problem with Bluetooh device discovery."
                            + "  Operation returned null.");
                    errorText = "Bluetooth GPS device discovery failed.";
                }
            } catch (final SecurityException e) {
                // The user prevented communication with the server.
                // Inform them to allow it.
                Log.error("User denied Bluetooth access.", e);
                errorText = "You must allow access for the GPS to work.\n"
                        + "Please restart and allow all connections.";
            } catch (final IOException e) {
                // There was an unknown I/O error preventing us from going
                // farther.
                Log.error("Problem with Bluetooth device discovery.", e);
                errorText = "Bluetooth GPS device discovery failed.\n"
                        + "Exit the application and verify your phone's"
                        + " Bluetooth is on.  "
                        + "If it is please restart your phone and"
                        + " GPS device and try again.";
            }

            // Go to the next screen.
            if (errorText != null) {
                // Inform the user why device discovery failed.
                next = new ErrorAlert("Discovery Error", errorText, previous);
            } else {
                // Successful device discovery.
                Log.info("Found list of " + devices.length
                        + " available devices and presenting"
                        + " them to the user.");

                final SelectGPSScreen selectGPS = new SelectGPSScreen(c,
                        previous,next);
                selectGPS.setAvailableDevices(devices);

                if (devices.length == 0) {
                    // No devices were found.
                    final String message = "No devices were found.\n"
                            + "Make sure your Bluetooth GPS device is on"
                            + " and within 3 meters (10 feet) of you.";

                    next = new ErrorAlert("Discovery Error", message,
                            selectGPS);
                } else {
                    // Let the user select from the devices found.
                    next = selectGPS;
                }
            }

            return next;
        }
    }
}
