package net.sf.bt747.j4me.app.conn;

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.ConnectionNotFoundException;

import org.j4me.logging.Log;

import bt747.model.Controller;

/**
 * Maps the communications with a Bluetooth GPS device to the
 * <code>LocationProvider</code> interface.
 */
public class BluetoothPort extends gps.connection.GPSPort{

    /**
     * The protocol portion of the URL for Bluetooth addresses.
     */
    private static final String BLUETOOTH_PROTOCOL = "btspp://";

    /**
     * The Bluetooth connection string options for communicating with a GPS
     * device.
     * <ul>
     * <li>master is false because the current device is the master
     * <li>encryption is false because no sensitive data is transmitted
     * <li>authentication is false because the data is not personalized
     * </ul>
     */
    private static final String BLUETOOTH_GPS_OPTIONS = ";master=false;encrypt=false;authenticate=false";

    /**
     * The instance of this class
     */
    private static BluetoothPort instance = null;

    /**
     * Holds the connection to the GPS device. Used to get coordinates from
     * the GPS device.
     */
    private BluetoothGPS gps = null;

    /**
     * URL used to connect
     */
    private String bluetoothURL = null;

    private final int TEMPORARILY_UNAVAILABLE = 0;
    private final int OUT_OF_SERVICE = 1;
    /**
     * The state of the location provider
     */
    private int state = TEMPORARILY_UNAVAILABLE;

    public void setFreeTextPort(final String s) {
        Log.debug("Free text port is:" + s);
        bluetoothURL = s;
    }

    public String getFreeTextPort() {
        return bluetoothURL;
    }

    private static Controller gpsController;

    /**
     * Returns a <code>LocationProvider</code> for the GPS device connected
     * to via Bluetooth.
     */
    public final static synchronized BluetoothPort getInstance()
            throws IOException {
        // Make sure we haven't given out our one Bluetooth GPS provider.
        // Bluetooth will only support a connection to a single other GPS
        // device so we cap out at one provider.
        if (BluetoothPort.instance == null) {
            BluetoothPort.instance = new BluetoothPort();
        }

        return BluetoothPort.instance;
    }

    public final static void setController(final Controller c) {
        BluetoothPort.gpsController = c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#openPort()
     */
    public int openPort() {
        int success = -1;
        try {
            connect(bluetoothURL, null);
            success = 0;
        } catch (final Exception e) {
            Log.error("Problem opening " + bluetoothURL, e);
        }
        return success;
    }

    /**
     * Construct the instance of this class. If the <code>channelId</code>
     * is <code>null</code> this method will attempt to guess at the channel
     * id.
     * 
     * @param remoteDeviceBTAddress -
     *                The remote GPS device bluetooth address
     * @param channelId -
     *                The channel id for the remote device. This may be
     *                <code>null</code>. If this is the case we will simply
     *                guess at the channel ID for the device.
     * @throws ConnectionNotFoundException -
     *                 If the target of the name cannot be found, or if the
     *                 requested protocol type is not supported.
     * @throws IOException -
     *                 If error occurs while establishing bluetooth connection
     *                 or opening input stream.
     * @throws SecurityException -
     *                 May be thrown if access to the protocol handler is
     *                 prohibited.
     */
    private void connect(final String remoteDeviceBTAddress,
            final String channelId) throws ConnectionNotFoundException,
            IOException, SecurityException {

        // The number of channels to try connecting on.
        // Bluetooth address have channels 1-9 typically. However, GPS
        // devices seem to only have 1 channel. We'll use two just to
        // be safe in case the Bluetooth GPS device allows multiple
        // connections.
        final int maxTries = 2;

        // If the channel id is null, we need to guess at the channel id
        if (channelId == null) {
            // Try a few channels
            for (int i = 1; i <= maxTries; i++) {
                try {
                    bluetoothURL = BluetoothPort.constructBTURL(
                            remoteDeviceBTAddress, Integer.toString(i));
                    gps = connect(bluetoothURL);
                    break;
                } catch (final IOException e) {
                    if (Log.isDebugEnabled()) {
                        Log.debug("Channel ID = " + i + " failed:  "
                                + e.toString());
                    }

                    // If there are still more to try, then try them
                    if (i == maxTries) {
                        throw e;
                    }
                }
            }
        } else {
            // Connect to the remote GPS device
            bluetoothURL = BluetoothPort.constructBTURL(
                    remoteDeviceBTAddress, channelId);
            gps = connect(bluetoothURL);
        }
    }

    /**
     * Connect to the GPs device.
     * 
     * @param bturl -
     *                The url to the bluetooth GPS device
     * @throws ConnectionNotFoundException -
     *                 If the target of the name cannot be found, or if the
     *                 requested protocol type is not supported.
     * @throws IOException -
     *                 If error occurs while establishing bluetooth connection
     *                 or opening input stream.
     * @throws SecurityException -
     *                 May be thrown if access to the protocol handler is
     *                 prohibited.
     */
    private BluetoothGPS connect(final String bturl)
            throws ConnectionNotFoundException, IOException,
            SecurityException {
        Log.info("Try URL: " + bturl);
        // Connect to the Bluetooth GPS device.
        final BluetoothGPS gps = new BluetoothGPS(this, bturl);
        gps.connect();
        return gps;
    }

    /**
     * Construct the Bluetooth URL
     * 
     * @param deviceBluetoothAddress -
     *                The address of the remote device
     * @param channelId -
     *                The channel ID to use
     */
    protected static String constructBTURL(
            final String deviceBluetoothAddress, final String channelId) {
        if ((channelId == null) || (deviceBluetoothAddress == null)) {
            return null;
        }

        final StringBuffer url = new StringBuffer();

        // Add the "btspp://" prefix (if not already there).
        if (deviceBluetoothAddress.substring(0,
                BluetoothPort.BLUETOOTH_PROTOCOL.length())
                .equalsIgnoreCase(
                        BluetoothPort.BLUETOOTH_PROTOCOL) == false) {
            url.append(BluetoothPort.BLUETOOTH_PROTOCOL);
        }

        // Add the address.
        url.append(deviceBluetoothAddress);

        // Add the channel ID (if not already there).
        if (deviceBluetoothAddress.indexOf(':',
                BluetoothPort.BLUETOOTH_PROTOCOL.length() + 1) < 0) {
            url.append(':');
            url.append(channelId);
        }

        // Add the Bluetooth options (if not already there).
        if (deviceBluetoothAddress.indexOf(';') < 0) {
            url.append(BluetoothPort.BLUETOOTH_GPS_OPTIONS);
        }

        final String bturl = url.toString();
        return bturl;
    }

    /**
     * @see org.j4me.bluetoothgps.LocationProvider#getState()
     */
    public int getState() {
        return state;
    }

    /**
     * Set the state of the location provider
     * 
     * @param state
     *                the location provider's state
     */
    public void setState(final int state) {
        this.state = state;
    }

    /**
     * @return The address of the Bluetooth GPS device.
     */
    public String getBluetoothURL() {
        return bluetoothURL;
    }

    /**
     * @see org.j4me.bluetoothgps.LocationProvider#close()
     */
    public void close() {
        if (gps != null) {
            gps.disconnect();

            // Record we are no longer connected.
            state = OUT_OF_SERVICE;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#write(java.lang.String)
     */
    public void write(final String s) {
        write(s.getBytes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#write(byte[])
     */
    public void write(final byte[] b) {
        final OutputStream outputStream = gps.getOutputStream();
        if (outputStream != null) {
            try {
                outputStream.write(b);
                outputStream.flush();
            } catch (final IOException e) {
                Log.error("writeByte", e);
            }
        }
    }

    public void closePort() {
        close();
    }

    public final int readCheck() {
        try {
            if (isConnected()) {
                return gps.getInputStream().available();
            } else {
                Log.error("readCheck on closed stream");
                return 0;
            }
        } catch (final IOException e) {
            Log.error("readCheck", e);
            // reconnectPort();
            return 0;
        }
    }

    public final int readBytes(final byte[] b, final int start, final int end) {
        try {
            return gps.getInputStream().read(b, start, end);
        } catch (final Exception e) {
            // TODO: handle exception
            Log.error("getBytes", e);
            return 0;
        }
    }

    /**
     * @return True, if connected and input stream opened
     */
    public final boolean isConnected() {
        return gps != null && gps.isConnected();
    }

    public final void connected(final boolean status) {
        if ((BluetoothPort.gpsController != null) & status) {
            BluetoothPort.gpsController
                    .performOperationsAfterGPSConnect();
        }
    }
}
