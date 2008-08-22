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
class BluetoothGPS {

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
            return 0;
        }
    }

    public final int getBytes(byte[] b, int start, int end) {
        try {
            return inputStream.read(b, start, end);
        } catch (Exception e) {
            // TODO: handle exception
            return 0;
        }
    }
}
