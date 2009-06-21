package net.sf.bt747.j4me.app.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.j4me.logging.Log;
import org.j4me.util.ConnectorHelper;

import bt747.sys.Generic;

/**
 * Main class for communication with GPS receiver. Use this class to access
 * GPS receiver from other classes.
 */
public class BluetoothGPS  {

    /**
     * The timeout value for Bluetooth connections in milliseconds. Since this
     * tries to connect on 10 Bluetooth channels, the total timeout for
     * connecting is 10x this number.
     * <p>
     * The emulator's default timeout is 10,000 ms.
     */
    private static final short BLUETOOTH_TIMEOUT = 3000;

    /**
     * Time in ms to wait until resume to receive.
     */
    private static final short BREAK = 500;

    /**
     * Wait after calling disconnect
     */
    private static final short DISCONNECT_WAIT = 1000;

    /**
     * Time in ms to sleep before each read. This seems to solve the problem
     * our read hangs.
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
     * How long to wait to initialize the bluetooth connection
     */
    public static final short BLUETOOTH_CONNECTION_INIT_SLEEP = 200;

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
     * Flag to indicate that the runner thread should stop
     */
    private boolean stop = false;

    /**
     * URL used to connect to bluetooth device.
     */
    private String url;
    
    private final BluetoothPort provider;

    /**
     * Creates new receiver. Does not start automatically, use start()
     * instead.
     * 
     * @param url -
     *                URL of bluetooth device to connect to.
     */
    public BluetoothGPS(final BluetoothPort provider,
            final String url) {
        this.url = url;
        this.provider = provider;
    }

    private volatile Thread connectRunner;

    private void connectInThread() {
        if ((connectRunner == null) || !connectRunner.isAlive()) {
            connectRunner = new connectThread(this);
            connectRunner.start();
        }
    }

    /**
     * Allows starting connection in a separate thread.
     * 
     * @author Mario
     * 
     */
    private static class connectThread extends Thread {
        BluetoothGPS inst;

        protected connectThread(final BluetoothGPS inst) {
            this.inst = inst;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        public void run() {
            try {
                inst.connect();
            } catch (final Exception e) {
                Log.error("Problem on connection", e);
            }
        }
    }

    private volatile boolean connecting = false;

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

        if (!connecting && !isConnected()) {
            try {
                connecting = true;
                // Connect to the GPS device.
                Log.info("Connecting to Bluetooth device at " + url);

                connection = (StreamConnection) ConnectorHelper.open(url,
                        Connector.READ_WRITE, BluetoothGPS.BLUETOOTH_TIMEOUT);

                Log.debug("Bluetooth connection established");

                inputStream = connection.openInputStream();
                outputStream = connection.openOutputStream();
                nextTimeOut = 0; // Reinit timeout
                provider.connected(true);
            } catch (final ConnectionNotFoundException e) {
                Log.debug("Bluetooth connection not found", e);
                connecting = false;
                throw e;
            } catch (IOException e) {
                Log.debug("Bluetooth IO Exception", e);
                connecting = false;
                throw e;
            } catch (SecurityException e) {
                Log.debug("Bluetooth Security Exception", e);
                connecting = false;
                throw e;
            }
        }
    }

    /**
     * Closes input stream and bluetooth connection as well as sets the
     * corresponding objects to null.
     * 
     * @see #disconnect()
     */
    public synchronized void disconnect() {
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
        provider.connected(false);
    }

    /**
     * @return True, if connected and input stream opened
     */
    public synchronized boolean isConnected() {
        return (connection != null) && (inputStream != null);
    }

    private int nextTimeOut = 0;
    private boolean firstItr = true;
    private BluetoothReadTimeoutThread btrtt;

    public synchronized int available() throws IOException {
        final int available = 0;
        final int currentTime = Generic.getTimeStamp();
        if (nextTimeOut == 0) {
            // Just after connection - sleep a bit.
            nextTimeOut = currentTime + BluetoothGPS.READ_TIMEOUT;
            // Create the thread to interrupt reads that we believe are
            // hung

        }
        if (currentTime >= nextTimeOut) {
            if (!isConnected()) {
                connectInThread();
                return 0;
            }

            return inputStream.available();
        }
        return available;
    }

    /**
     * 
     */
    public int read(byte[] b, int offset, int len) throws IOException {
        int result;
        result = available();
        if (result > 0) {
            result = 0;
            // For whatever reason we need to sleep (not wait) before
            // every read.
            try {
                Thread.sleep(BluetoothGPS.SLEEP_BEFORE_READ);
            } catch (final InterruptedException e) {
            }
            try {
                // Start a thread that monitors that the read does not hang
                if (firstItr) {
                    btrtt = new BluetoothReadTimeoutThread(Thread.currentThread(),
                            BluetoothGPS.READ_TIMEOUT);
                    // First iteration so just start the thread
                    btrtt.start();
                    firstItr = false;
                } else {
                    // Thread has already been started so just wake it up
                    btrtt.restart();
                }

                result = inputStream.read(b, offset, len);

                if (result > 0) {
                    // Notify the bluetooth read timeout thread that we have
                    // completed
                    // as successful read
                    btrtt.setReadSuccess(true);
                }
            } catch (final Throwable t) {
                if (t instanceof InterruptedIOException) {
                    // The read was taking too long so we interrupted it
                    Log.info("Bluetooth GPS stalled."
                            + "  Disconnecting and reconnecting.");
                } else if (t instanceof IOException) {
                    // Also captures BluetoothConnectionException.
                    Log.info("Bluetooth device dropped connection."
                            + "  Reconnecting.");
                } else if (t instanceof InterruptedException) {
                    // Closing the application down.
                    // Don't care process = false;
                } else {
                    // Not sure what happened. Log the error and
                    // disconnect.
                    // IOException : Either
                    // thrown while connecting or while reading.
                    // Wait some time before continuing.
                    Log.warn("Unexpected GPS read error", t);
                }

                // Disconnect so that we automatically connect again.
                // The runner will do the reconnection.
                disconnect();

                synchronized (this) {
                    // Give time to disconnect
                    try {
                        wait(BluetoothGPS.DISCONNECT_WAIT);
                    } catch (final InterruptedException e) {
                    }
                }
            }
        }

        if (result < 0) {
            // The Bluetooth GPS device closed the connection.
            throw new IOException("Bluetooth device closed connection");
        }
        return result;
    }


    public final OutputStream getOutputStream() {
        return outputStream;
    }

    public final InputStream getInputStream() {
        return inputStream;
    }
    
}
