package net.sf.bt747.j4me.app.conn;

import java.io.IOException;

import net.sf.bt747.j4me.app.AppController;
import net.sf.bt747.j4me.app.screens.ErrorAlert;
import net.sf.bt747.j4me.app.screens.ProgressAlert;

import org.j4me.bluetoothgps.LocationProvider;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.components.Label;

/**
 * The "Finding GPS Devices..." alert screen. This checks for nearby Bluetooth
 * devices.
 * <p>
 * This screen is shown to the user while the application uses Bluetooth
 * dynamic discovery to create a list of nearby devices. It disappears to give
 * them a selection once it is done. This usually takes a few seconds, but
 * sometimes as many as 30.
 */
public class FindingGPSDevicesAlert extends ProgressAlert implements BT747DiscoveryListener {
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
            final DeviceScreen previous, final DeviceScreen next) {
        super("Finding GPS...", "Looking for nearby Bluetooth devices.");

        this.c = c;
        this.previous = previous;
        this.next = next;
    }

    /* (non-Javadoc)
     * @see net.sf.bt747.j4me.app.screens.ProgressAlert#cancel()
     */
    public void cancel() {
        Log.info("Canceling Bluetooth device discovery.");
        super.cancel();
    }
    /**
     * Called when the user presses the alert's dismiss button.
     */
    public final void onCancel() {
        Log.info("Going to "+previous.getTitle());
        previous.show();
    }
    
    /**
     * Uses Bluetooth device discovery to get a list of the nearby (within 10
     * meters) Bluetooth GPS devices that are turned on. If more than one
     * device is returned, the user should select which is their GPS device.
     * <p>
     * The address of the Bluetooth device should be set using the
     * <code>Criteria.setRemoteDeviceAddress</code> method. That
     * <code>Criteria</code> object can then be used as the argument to the
     * <code>getInstance</code> factory method.
     * <p>
     * Discovering Bluetooth devices is a lengthy operation. It usually takes
     * more than ten seconds. Therefore this method call normally should be
     * made from a separate thread to keep the application responsive.
     * 
     * @return An array of all the nearby Bluetooth devices that will accept a
     *         connection, not just GPS devices. Each array element returns
     *         another <code>String[2]</code> where the first element is the
     *         device's human readable name and the second is the address
     *         (devices that do not support human readable names will be set
     *         to the address). If the operation completed successfully, and
     *         no devices are nearby, the returned array will have length 0.
     *         If the operation terminated, for example because the device
     *         does not support the Bluetooth API, the returned array will be
     *         <code>null</code>.
     * @throws IOException
     *                 if any Bluetooth I/O errors occur. For example if
     *                 another Bluetooth discovery operation is already in
     *                 progess.
     * @throws SecurityException
     *                 if the user did not grant access to Bluetooth on the
     *                 device.
     */
    public static String[][] discoverBluetoothDevices(BT747DiscoveryListener l) throws IOException,
            SecurityException {
        String[][] devices = null;

        // If the device doesn't support Bluetooth, just return null.
        if (LocationProvider.supportsBluetoothAPI() == false) {
            Log.info("Device does not support Bluetooth");
            return null;
        }

        // Discover devices.
        BT747BluetoothDeviceDiscovery discoverer = null;

        try {
            discoverer = new BT747BluetoothDeviceDiscovery(l);
        } catch (final Exception e) {
            // Some kind of exception creating the BluetoothDeviceDiscovery
            // object.
            // This can happen on some platforms, such as pre-JSR-82
            // BlackBerry devices.
            Log.warn("Cannot discover Bluetooth devices", e);
            return null;
        }

        devices = discoverer.discoverNearbyDeviceNamesAndAddresses();

        return devices;
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
            final BluetoothPort provider = c.getAppModel()
                    .getGpsBluetoothConnection();

            if (provider != null) {
                provider.close();
                c.getAppModel().setGpsBluetoothConnection(null);
            }

            // Search for Bluetooth devices (this takes several seconds).
            try {
                Log.info("Discovering Bluetooth devices.");

                devices = discoverBluetoothDevices(this);

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
                SelectGPSScreen selectGPS = null;
                try {
                    selectGPS = new SelectGPSScreen(c, previous, next);
                    try {
                        selectGPS.setAvailableDevices(devices);
                    } catch (final OutOfMemoryError e) {
                        Log.error("Out of memory during setAvailableDevices",
                                e);
                        errorText = "This memory problem is currently misunderstood."
                                + "Contact the author of the application.";
                    }
                } catch (final OutOfMemoryError e) {
                    Log.error("Out of memory during selectGPS instantiation",
                            e);
                    errorText = "This memory problem is currently misunderstood."
                            + "Contact the author of the application.";
                }
                if (selectGPS != null) {
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
            }

            Log.info("Returning "+next.getTitle());
            return next;
        }
    }

    /* (non-Javadoc)
     * @see net.sf.bt747.j4me.app.conn.BT747DiscoveryListener#deviceDiscovered(java.lang.String)
     */
    public void deviceDiscovered(String deviceDescription) {
        // TODO Auto-generated method stub
        append(new Label(deviceDescription));
    }
}
