package net.sf.bt747.j4me.app.conn;

import net.sf.bt747.j4me.app.AppController;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.MenuItem;
import org.j4me.ui.components.Component;
import org.j4me.ui.components.MenuOption;

/**
 * The "Select GPS Device" screen. When the GPS is an external Bluetooth
 * device, and more than one are available within the area of the user, this
 * screen will be shown to let them select which device is theirs.
 */
public final class SelectGPSScreen extends Dialog {
    /**
     * The location information for this application.
     */
    private final AppController c;

    /**
     * The screen to return to if the user cancels this one.
     */
    private final DeviceScreen previous;
    
    private final DeviceScreen next;

    /**
     * Constructs the "Select GPS Device" screen.
     * 
     * @param model
     *                is the application's location data.
     * @param previous
     *                is the screen to return to if this one is canceled.
     */
    public SelectGPSScreen(final AppController c, final DeviceScreen previous,
            final DeviceScreen next) {
        setTitle("Select GPS Device");

        this.c = c;
        this.previous = previous;
        this.next = next;
    }

    /**
     * Called by the Bluetooth device discovery process once completed to set
     * the list of nearby devices. These will be displayed to the user to
     * select from.
     * 
     * @param devices
     *                are the available devices as returned from the
     *                <c>LocationProvider.discoverBluetoothDevices</c>
     *                method. This may not be <c>null</c>.
     * @see org.j4me.bluetoothgps.LocationProvider#discoverBluetoothDevices()
     */
    public final void setAvailableDevices(final String[][] devices) {
        // Add a menu option for each Bluetooth device.
        deleteAll();

        for (int i = 0; i < devices.length; i++) {
            final String[] device = devices[i];
            final String name = device[0];
            final String address = device[1];

            final GPSDeviceOption option = new GPSDeviceOption(name, address);
            append(new MenuOption(option));
        }

        // Add a final option to "Try Again".
        append(new MenuOption(new MenuItem() {
            public String getText() {
                return "Try Again";
            }

            public void onSelection() {
                final FindingGPSDevicesAlert alert = new FindingGPSDevicesAlert(
                        c, previous, next);
                alert.show();
            }
        }));
    }

    /**
     * The left menu button takes the user back to the previous screen. If
     * there is no previous screen it has no effect.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected final void declineNotify() {
        // Go back to the previous screen.
        if (previous != null) {
            previous.show();
        }

        // Continue processing the event.
        super.declineNotify();
    }

    /**
     * The right menu button selects the highlighted menu item.
     */
    protected final void acceptNotify() {
        // Go to the highlighted screen.
        final int highlighted = getSelected();
        final Component component = get(highlighted);

        if (component instanceof MenuOption) {
            final MenuOption option = (MenuOption) component;
            option.select();
        }

        // Continue processing the event.
        super.acceptNotify();
    }

    /**
     * A menu option for selecting a GPS device.
     */
    private final class GPSDeviceOption implements MenuItem {
        /**
         * The user friendly name of the GPS device.
         */
        private final String name;

        /**
         * The Bluetooth URL for the device.
         */
        private final String address;

        /**
         * Constructs a menu option for choosing a Bluetooth device.
         * 
         * @param name
         *                is the name of the Bluetooth device.
         * @param address
         *                is the Bluetooth URL for the device.
         */
        public GPSDeviceOption(final String name, final String address) {
            this.name = name;
            this.address = address;
        }

        /**
         * @return The name of the Bluetooth GPS device.
         */
        public String getText() {
            return name;
        }

        /**
         * Called if the user selects this Bluetooth device. It records it on
         * the model and goes on to the next screen.
         */
        public void onSelection() {
            // Record the selected device.
            c.getAppModel().setBluetoothGPS(name, address);

            // Connect to the GPS.
            // InitializingGPSAlert alert = new InitializingGPSAlert( c,
            // SelectGPSScreen.this );
            // alert.show();

            // Device is selected
            next.show();
        }
    }
}
