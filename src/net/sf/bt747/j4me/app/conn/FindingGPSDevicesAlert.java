package net.sf.bt747.j4me.app.conn;

import net.sf.bt747.j4me.app.AppController;
import net.sf.bt747.j4me.app.screens.ErrorAlert;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.HorizontalRule;
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
public class FindingGPSDevicesAlert extends Menu implements
        BluetoothListenerInterface, Runnable {
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

    private Label status = new Label();

    private DeviceScreen next;

    /**
     * Constructs the "Finding GPS Devices..." alert screen.
     * 
     * @param c
     *            is the application's controller.
     * @param previous
     *            is the screen that came before this one.
     */
    public FindingGPSDevicesAlert(final AppController c,
            final DeviceScreen previous, final DeviceScreen next) {
        setTitle("Finding BT GPS ...");

        this.c = c;
        this.previous = previous;
        this.next = next;
    }

    private boolean initialScreenSetup = false;

    private boolean done = false;

    private final int TIMEOUT = 1000;

    public void initialSetupScreen() {
        if (!initialScreenSetup) {
            createNewSection("Status");
            status.setLabel("Initialising");
            append(status);
            createNewSection("Devices");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j4me.app.screens.ProgressAlert#cancel()
     */
    public void cancel() {
        Log.info("Canceling Bluetooth device discovery.");
        BluetoothFacade.getInstance().cancel(this);
        status.setLabel("Cancelled");
    }

    /**
     * Called when the user presses the alert's dismiss button.
     */
    public final void onCancel() {
        cancel();
        Log.info("Going to " + previous.getTitle());
        previous.show();
    }

    private void startWorker() {
        // Start the worker thread.
        final Thread worker = new Thread(this);
        worker.start();
    }

    /**
     * Executes the worker thread. This method synchronizes with the main UI
     * thread to avoid any race conditions involved with the order screens are
     * set.
     */
    public void run() {
        try {
            // Do the background thread work.
            // final DeviceScreen next = doWork();
            doWork();
            // Go to the next screen.
            // if ((canceled == false) && (next != null)) {
            // next.show();
            // }
        } catch (final Throwable t) {
            Log.error("Exception " + getTitle(), t);

            // Display the error.
            final ErrorAlert error = new ErrorAlert("Unhandled Exception", t
                    .toString(), this);
            error.show();
        }
    }

    private void doWork() {
        done = false;
        String errorText;
        Log.info("Starting find Bluetooth");
        try {
            done = !BluetoothFacade.getInstance().findBluetoothDevices(this);
            status.setLabel("Searching");
        } catch (final SecurityException e) {
            // The user prevented communication with the server.
            // Inform them to allow it.
            Log.error("User denied Bluetooth access.", e);
            errorText = "You must allow access for the GPS to work.\n"
                    + "Please restart and allow all connections.";
            (new ErrorAlert("Discovery Error", errorText, previous)).show();
        }
        if (done) {
            // The operation failed for an unknown Bluetooth reason.
            Log.error("Bluetooth discovery not launched."
                    + "  Operation returned null.");
            errorText = "Bluetooth device discovery not launched.  Is bluetooth active?";
            // (new ErrorAlert("Discovery Error", errorText,
            // previous)).show();
        }
    }

    public void showNotify() {
        initialSetupScreen();
        startWorker();
        super.showNotify();
    }

    /**
     * A thread that finds nearby Bluetooth devices and sets them on a select
     * GPS device screen.
     * 
     * @return the screen to show after this thread finishes.
     */
    protected final DeviceScreen doNoWork() {
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

                // devices = discoverBluetoothDevices(this);

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
            }
            // catch (final IOException e) {
            // // There was an unknown I/O error preventing us from going
            // // farther.
            // Log.error("Problem with Bluetooth device discovery.", e);
            // errorText = "Bluetooth GPS device discovery failed.\n"
            // + "Exit the application and verify your phone's"
            // + " Bluetooth is on.  "
            // + "If it is please restart your phone and"
            // + " GPS device and try again.";
            // }

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

            Log.info("Returning " + next.getTitle());
            return next;
        }
    }

    int devicesFound = 0;

    private final static void connect(String deviceRef) {

    }

    public void deviceFound(final String deviceDescription,
            final String deviceRef) {
        devicesFound++;

        status.setLabel("Found " + devicesFound + " device(s)");
        Log.info("Found device " + deviceDescription);
        this.appendMenuOption(new MenuItem() {
            public final String getText() {
                return deviceDescription;
            }

            public final void onSelection() {
                connect(deviceRef);
                // confirmScreen.show();
            }
        });
        repaint();
    }

    public void discoveryDone() {
        Log.info("Discovery done ");
        status.setLabel("Done");

        synchronized (FindingGPSDevicesAlert.BLUETOOTH_LOCK) {
            done = true;
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

        final Label header = new Label();
        header.setFont(UIManager.getTheme().getMenuFont());
        header.setLabel(title);
        append(header);
    }
}
