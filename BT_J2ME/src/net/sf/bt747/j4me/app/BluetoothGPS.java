// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package net.sf.bt747.j4me.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.j4me.logging.Log;
import org.j4me.util.ConnectorHelper;

/**
 * Main class for communication with GPS receiver. Use this class to access
 * GPS receiver from other classes.
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
     * Time in ms to sleep before each read. This seems to solve the problem
     * or read hangs.
     */
    public static final short SLEEP_BEFORE_READ = 100;

    /**
     * How long to wait before we just kill the read. We add the sleep value
     * since this sleep is performed before every read and we start the timer
     * before the pre-read sleep.
     * <p>
     * After experimenting with combinations of Bluetooth GPS devices and
     * phones 3 seconds seems to work. Motorola phones seem to be the only
     * ones with Bluetooth implementations that need this.
     */
    public static final short READ_TIMEOUT = BluetoothGPS.SLEEP_BEFORE_READ + 3000;

    /**
     * The delay in milliseconds between reconnects.
     */
    private static final long DELAY_BETWEEN_OPENTRIALS = 3000L;

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
     * Creates new receiver. Does not start automatically, use start()
     * instead.
     * 
     * @param url -
     *                URL of bluetooth device to connect to.
     */
    public BluetoothGPS(final String url) {
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
     *                 If the target of the name cannot be found, or if the
     *                 requested protocol type is not supported.
     * @throws IOException -
     *                 If error occurs while establishing bluetooth connection
     *                 or opening input stream.
     * @throws SecurityException -
     *                 May be thrown if access to the protocol handler is
     *                 prohibited.
     */
    public synchronized void connect() throws ConnectionNotFoundException,
            IOException, SecurityException {

        if (!isConnected() && url != null) {
            // Connect to the GPS device.
            Log.info("Connecting to Bluetooth device at " + url);

            connection = (StreamConnection) ConnectorHelper.open(url,
                    Connector.READ_WRITE, BluetoothGPS.BLUETOOTH_TIMEOUT);

            Log.debug("Bluetooth connection established");

            inputStream = connection.openInputStream();
            outputStream = connection.openOutputStream();
        }
    }

    /**
     * Indicates if the connection is supposed to be open or not. This is to
     * enable the automatic reconnect functionality.
     */
    private boolean internalIsConnected = false;

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
        } catch (final IOException e) {
            Log.warn("Problem closing GPS connection", e);
        }

        inputStream = null;
        outputStream = null;
        connection = null;
    }

    public final void close() {
        internalIsConnected = false;
        disconnect();
    }

    /**
     * @return True, if connected and input stream opened
     */
    public synchronized boolean isConnected() {
        return (connection != null) && (inputStream != null);
    }

    private boolean isReconnectAutomatically = true;

    public final int readCheck() {
        try {
            if (isConnected()) {
                return inputStream.available();
            } else {
                Log.error("readCheck on closed stream");
                return 0;
            }
        } catch (final IOException e) {
            Log.error("readCheck", e);
            reconnectPort();
            return 0;
        }
    }

    public final int readBytes(final byte[] b, final int start, final int end) {
        try {
            return inputStream.read(b, start, end);
        } catch (final Exception e) {
            // TODO: handle exception
            Log.error("getBytes", e);
            return 0;
        }
    }

    public void write(final String s) {
        write(s.getBytes());
    }

    public synchronized void write(final byte[] b) {
        try {
            outputStream.write(b);
            outputStream.flush();
        } catch (final IOException e) {
            Log.error("writeByte", e);
            reconnectPort();
        }
    }

    private long nextOpentrial = 0;

    private void reconnectPort() {
        if (internalIsConnected && isReconnectAutomatically
                && (nextOpentrial <= System.currentTimeMillis())) {
            nextOpentrial = System.currentTimeMillis() + 10
                    * BluetoothGPS.DELAY_BETWEEN_OPENTRIALS;
            try {
                Log.info("Reconnecting after apparent disconnect");
                try {
                    disconnect();
                } catch (final Exception e) {
                    Log.error("Failure (expected) on disconnect", e);
                }
                connect();
            } catch (final Exception e) {
                Log.error("Failure on reconnect", e);
            }
        }
    }

    public int openPort() {
        internalIsConnected = true;

        synchronized (FindingGPSDevicesAlert.BLUETOOTH_LOCK) {
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
        } catch (final Exception e) {
            Log.debug("bt open", e);
            // TODO: handle exception
        }

        return 0;
    }

    public void closePort() {
        close();
    }

    public void setFreeTextPort(final String s) {
        url = s;
    }

    public String getFreeTextPort() {
        return url;
    }

    String channelId = null;

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
    private void makeConnection(final String urlProvided
    // , String channelId
    ) throws IOException, SecurityException {
        // The number of channels to try connecting on.
        // Bluetooth address have channels 1-9 typically. However, GPS
        // devices seem to only have 1 channel. We'll use two just to
        // be safe in case the Bluetooth GPS device allows multiple
        // connections.
        final int maxTries = 2;

        nextOpentrial = System.currentTimeMillis()
                + BluetoothGPS.DELAY_BETWEEN_OPENTRIALS;
        // If the channel id is null, we need to guess at the channel id
        if (channelId == null) {
            // Try a few channels
            for (int i = 1; i <= maxTries; i++) {
                String localUrl;
                try {
                    localUrl = BluetoothGPS.constructBTURL(url, Integer
                            .toString(i));
                    url = localUrl;
                    connect();
                    break;
                } catch (final IOException e) {
                    if (Log.isDebugEnabled()) {
                        Log.debug("Channel ID = " + i + " failed:  ", e);
                    }

                    // If there are still more to try, then try them
                    if (i == maxTries) {
                        throw e;
                    }
                } catch (final SecurityException e) {
                    if (Log.isDebugEnabled()) {
                        Log.debug("Channel ID = " + i + " failed:  ", e);
                    }
                }
            }
        } else {
            // Connect to the remote GPS device
            url = BluetoothGPS.constructBTURL(urlProvided, channelId);
            connect();
        }
        if (tt == null) {
            tt = new TimerTask() {
                public void run() {
                    checkPort();
                }
            };
            tm.schedule(tt, 3500, 3500);
            Log.debug("Added task to schedule");
        }
    }

    private java.util.Timer tm = new Timer();
    private TimerTask tt;

    private void checkPort() {
        Log.debug("checkPort");
        boolean ok = true;
        try {
            ok = ok && (inputStream.available() >= 0);
            outputStream.flush();
            // ok = ok && connection.
        } catch (final Exception e) {
            ok = false;
        }
        if (ok == false) {
            reconnectPort();
        }
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
                BluetoothGPS.BLUETOOTH_PROTOCOL.length()).equalsIgnoreCase(
                BluetoothGPS.BLUETOOTH_PROTOCOL) == false) {
            url.append(BluetoothGPS.BLUETOOTH_PROTOCOL);
        }

        // Add the address.
        url.append(deviceBluetoothAddress);

        // Add the channel ID (if not already there).
        if (deviceBluetoothAddress.indexOf(':',
                BluetoothGPS.BLUETOOTH_PROTOCOL.length() + 1) < 0) {
            url.append(':');
            url.append(channelId);
        }

        // Add the Bluetooth options (if not already there).
        if (deviceBluetoothAddress.indexOf(';') < 0) {
            url.append(BluetoothGPS.BLUETOOTH_GPS_OPTIONS);
        }

        final String bturl = url.toString();
        return bturl;
    }

}
