/**
 * 
 */
package net.sf.bt747.j4me.app.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gps.connection.GPSPort;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.midlet.MIDlet;

import org.j4me.logging.Log;
import org.j4me.util.ConnectorHelper;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747StringTokenizer;

/**
 * @author Mario
 * 
 */
public class CommPort extends GPSPort {

    /**
     * The protocol portion of the URL for Bluetooth addresses.
     */
    private static final String COMM_PROTOCOL = "comm://";

    /**
     * The Bluetooth connection string options for communicating with a GPS
     * device.
     * <ul>
     * <li>master is false because the current device is the master
     * <li>encryption is false because no sensitive data is transmitted
     * <li>authentication is false because the data is not personalized
     * </ul>
     */
    private static final String COMM_GPS_OPTIONS = ";baudrate=115200";

    private static CommPort commPortSingleton;

    /**
     * Get and instance (singleton) of the CommPort.
     * 
     * @return
     */
    public final static synchronized CommPort getInstance() {
        if (commPortSingleton == null) {
            commPortSingleton = new CommPort();
        }
        return commPortSingleton;
    }

    private static MIDlet midlet;

    public final static void setMidlet(final MIDlet midlet) {
        CommPort.midlet = midlet;
    }

    /**
     * Gets the available comm ports in the system.
     * 
     * @return
     */
    public final static String[] getAvailablePorts() {
        String[] result = null;
        try {
            if (midlet != null) {
                final String ports = midlet
                        .getAppProperty("microedition.commports");
                if (ports != null) {
                    final BT747StringTokenizer fields = JavaLibBridge
                            .getStringTokenizerInstance(ports, ',');
                    result = new String[fields.countTokens()];
                    int idx = 0;
                    while (fields.hasMoreTokens()) {
                        result[idx++] = fields.nextToken();
                    }
                }
            }
        } catch (Exception e) {
            Log.debug("Exception in CommPort.getPorts", e);
        }
        return result;
    }

    /**
     * Construct the Bluetooth URL
     * 
     * @param deviceBluetoothAddress
     *            - The address of the remote device
     * @param channelId
     *            - The channel ID to use
     */
    protected final static String constructCommURL(final String commPort) {
        if (commPort == null) {
            return null;
        }

        final StringBuffer url = new StringBuffer();

        // Add the "comm://" prefix (if not already there).
        if (commPort.substring(0, COMM_PROTOCOL.length()).equalsIgnoreCase(
                COMM_PROTOCOL) == false) {
            url.append(COMM_PROTOCOL);
        }

        // Add the address.
        url.append(commPort);

        // Add the options (if not already there).
        if (commPort.indexOf(';') < 0) {
            url.append(COMM_GPS_OPTIONS);
        }

        return url.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#openPort()
     */
    public int openPort() {
        int success = -1;
        String port = getFreeTextPort();
        if (port != null || port.length() > 0) {
            final String url = constructCommURL(port);
            try {
                connect(url);
                success = 0;
            } catch (final Exception e) {
                Log.error("Problem opening " + url, e);
            }
        }

        return super.openPort();
    }

    /**
     * @see org.j4me.bluetoothgps.LocationProvider#close()
     */
    public void close() {
        disconnect();
    }

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
                return inputStream.available();
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
            return inputStream.read(b, start, end);
        } catch (final Exception e) {
            // TODO: handle exception
            Log.error("getBytes", e);
            return 0;
        }
    }

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
     * Connection to Comm device.
     */
    private StreamConnection connection;
    private boolean connecting = false;
    private int COMM_TIMEOUT = 3000;
    private int nextTimeOut = 0;

    public synchronized void connect(final String url)
            throws ConnectionNotFoundException, IOException,
            SecurityException {

        if (!connecting && !isConnected()) {
            try {
                connecting = true;
                // Connect to the GPS device.
                Log.info("Connecting to Comm device at " + url);

                connection = (StreamConnection) ConnectorHelper.open(url,
                        Connector.READ_WRITE, COMM_TIMEOUT);

                Log.debug("Bluetooth connection established");

                inputStream = connection.openInputStream();
                outputStream = connection.openOutputStream();
                nextTimeOut = 0; // Reinit timeout
            } catch (final ConnectionNotFoundException e) {
                Log.debug("Comm connection not found", e);
                connecting = false;
                throw e;
            } catch (IOException e) {
                Log.debug("Comm IO Exception", e);
                connecting = false;
                throw e;
            } catch (SecurityException e) {
                Log.debug("Comm Security Exception", e);
                connecting = false;
                throw e;
            }
        }
    }

    /**
     * @return True, if connected and input stream opened
     */
    public synchronized boolean isConnected() {
        return (connection != null) && (inputStream != null);
    }
}
