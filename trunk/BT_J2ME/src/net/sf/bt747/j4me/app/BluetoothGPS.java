package net.sf.bt747.j4me.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.j4me.logging.Log;
import org.j4me.util.ConnectorHelper;

/**
 * Main class for communication with GPS receiver. Use this class to access GPS
 * receiver from other classes.
 */
class BluetoothGPS extends gps.connection.GPSPort {

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
     * The timeout value for Bluetooth connections in milliseconds. Since this
     * tries to connect on 10 Bluetooth channels, the total timeout for
     * connecting is 10x this number.
     * <p>
     * The emulator's default timeout is 10,000 ms.
     */
    private static final short BLUETOOTH_TIMEOUT = 3000;

    /**
     * Time in ms to sleep before each read. This seems to solve the problem or
     * read hangs.
     */
    public static final short SLEEP_BEFORE_READ = 100;

    /**
     * How long to wait before we just kill the read. We add the sleep value
     * since this sleep is performed before every read and we start the timer
     * before the pre-read sleep.
     * <p>
     * After experimenting with combinations of Bluetooth GPS devices and phones
     * 3 seconds seems to work. Motorola phones seem to be the only ones with
     * Bluetooth implementations that need this.
     */
    public static final short READ_TIMEOUT = SLEEP_BEFORE_READ + 3000;

    /**
     * Connection to bluetooth device.
     */
    private StreamConnection connection;

    /**
     * The input stream from the GPS device. Location data is received through
     * it.
     */
    private InputStream inputStream;

    /**
     * The output stream to the GPS device. Configuration commands are sent
     * through it.
     */
    private OutputStream outputStream;

    /**
     * URL used to connect to bluetooth device.
     */
    private String url;

    /**
     * Creates new receiver. Does not start automatically, use start() instead.
     * 
     * @param url -
     *            URL of bluetooth device to connect to.
     */
    public BluetoothGPS(String url) {
        this.url = url;
    }

    public BluetoothGPS() {
    }

    public static BluetoothGPS getInstance() {
        return new BluetoothGPS();
    }

    /**
     * Establishes a bluetooth serial connection (specified in GPS_BT_URL) and
     * opens an input stream.
     * 
     * @see #isConnected()
     * @see #disconnect()
     * 
     * @throws ConnectionNotFoundException -
     *             If the target of the name cannot be found, or if the
     *             requested protocol type is not supported.
     * @throws IOException -
     *             If error occurs while establishing bluetooth connection or
     *             opening input stream.
     * @throws SecurityException -
     *             May be thrown if access to the protocol handler is
     *             prohibited.
     */
    public synchronized void connect() throws ConnectionNotFoundException,
            IOException, SecurityException {

        if (!isConnected()) {
            // Connect to the GPS device.
            Log.info("Connecting to Bluetooth device at " + url);

            connection = (StreamConnection) ConnectorHelper.open(url,
                    Connector.READ_WRITE, BLUETOOTH_TIMEOUT);

            Log.debug("Bluetooth connection established");

            inputStream = connection.openInputStream();
            outputStream = connection.openOutputStream();
        }
    }

    /**
     * Closes input stream and bluetooth connection as well as sets the
     * corresponding objects to null.
     * 
     * @see #disconnect()
     */
    private synchronized void disconnect() {
        Log.debug("Disconnecting from GPS device");

        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            Log.warn("Problem closing GPS connection", e);
        }

        inputStream = null;
        outputStream = null;
        connection = null;
    }

    public final void close() {
        this.disconnect();
    }

    /**
     * @return True, if connected and input stream opened
     */
    public synchronized boolean isConnected() {
        return (connection != null) && (inputStream != null);
    }

    public final int readCheck() {
        try {
            return inputStream.available();
        } catch (Exception e) {
            Log.error("readCheck", e);
            return 0;
        }
    }

    public final int readBytes(final byte[] b, final int start, final int end) {
        try {
            return inputStream.read(b, start, end);
        } catch (Exception e) {
            // TODO: handle exception
            Log.error("getBytes", e);
            return 0;
        }
    }

    public void write(final String s) {
        try {
            synchronized (outputStream) {
                outputStream.write(s.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            Log.error("writeString", e);
        }
        ;

    }

    public void write(final byte[] b) {
        try {
            synchronized (outputStream) {
                outputStream.write(b);
                outputStream.flush();
            }
        } catch (IOException e) {
        }
        ;
    }

    public int openPort() {
        synchronized (FindingGPSDevicesAlert.bluetoothLock) {
            // First close any open provider.
            // For example if connected to one GPS device and are switching
            // to
            // another.
            if (isConnected()) {
                close();
            }
        }
        try {
            makeConnection(url);
        } catch (Exception e) {
            Log.debug("bt open", e);
            // TODO: handle exception
        }

        return 0;
    }

    public void closePort() {
        close();
    }

    public void setFreeTextPort(String s) {
        url = s;
    }

    public String getFreeTextPort() {
        return url;
    }

    String channelId = null;

    /**
     * Construct the instance of this class. If the <code>channelId</code> is
     * <code>null</code> this method will attempt to guess at the channel id.
     * 
     * @param remoteDeviceBTAddress -
     *            The remote GPS device bluetooth address
     * @param channelId -
     *            The channel id for the remote device. This may be
     *            <code>null</code>. If this is the case we will simply guess
     *            at the channel ID for the device.
     * @throws ConnectionNotFoundException -
     *             If the target of the name cannot be found, or if the
     *             requested protocol type is not supported.
     * @throws IOException -
     *             If error occurs while establishing bluetooth connection or
     *             opening input stream.
     * @throws SecurityException -
     *             May be thrown if access to the protocol handler is
     *             prohibited.
     */
    private void makeConnection(String url
    // , String channelId
    ) throws ConnectionNotFoundException, IOException, SecurityException {
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
                String localUrl;
                try {
                    localUrl = constructBTURL(this.url, Integer.toString(i));
                    this.url = localUrl;
                    this.connect();
                    break;
                } catch (IOException e) {
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
            url = constructBTURL(url, channelId);
            connect();
        }
    }

    /**
     * Construct the Bluetooth URL
     * 
     * @param deviceBluetoothAddress -
     *            The address of the remote device
     * @param channelId -
     *            The channel ID to use
     */
    protected static String constructBTURL(String deviceBluetoothAddress,
            String channelId) {
        if ((channelId == null) || (deviceBluetoothAddress == null)) {
            return null;
        }

        StringBuffer url = new StringBuffer();

        // Add the "btspp://" prefix (if not already there).
        if (deviceBluetoothAddress.substring(0, BLUETOOTH_PROTOCOL.length())
                .equalsIgnoreCase(BLUETOOTH_PROTOCOL) == false) {
            url.append(BLUETOOTH_PROTOCOL);
        }

        // Add the address.
        url.append(deviceBluetoothAddress);

        // Add the channel ID (if not already there).
        if (deviceBluetoothAddress
                .indexOf(':', BLUETOOTH_PROTOCOL.length() + 1) < 0) {
            url.append(':');
            url.append(channelId);
        }

        // Add the Bluetooth options (if not already there).
        if (deviceBluetoothAddress.indexOf(';') < 0) {
            url.append(BLUETOOTH_GPS_OPTIONS);
        }

        String bturl = url.toString();
        return bturl;
    }

}
