package net.sf.bt747.j4me.app;


import org.j4me.logging.Log;

import bt747.model.Model;

public class AppModel extends Model {

    /**
     * The friendly name of the Bluetooth GPS device. This is a human-readable
     * string that can be shown to the user.
     */
    private String gpsBluetoothName;

    /**
     * The URL for communicating with the user's Bluetooth GPS device.
     * <p>
     * A separate Bluetooth GPS device is only used when the current device does
     * not have accurate enough GPS. When the device's GPS is used this will be
     * <code>null</code>.
     */
    private String gpsBluetoothURL;

    private BluetoothGPS gpsBluetoothConnection;

    public AppModel() {
        // Must still get settings.
    }

    /**
     * @return the gpsBluetoothConnection
     */
    public final BluetoothGPS getGpsBluetoothConnection() {
        return gpsBluetoothConnection;
    }

    /**
     * @param gpsBluetoothConnection
     *            the gpsBluetoothConnection to set
     */
    public final void setGpsBluetoothConnection(
            BluetoothGPS gpsBluetoothConnection) {
        this.gpsBluetoothConnection = gpsBluetoothConnection;
    }

    /**
     * Records the URL of the Bluetooth GPS device so that on next startup it
     * can be used again automatically. If the device supports GPS with accurate
     * enough resolution (+/- 1 meter) this will be ignored.
     * 
     * @param name
     *            is the friendly name of the Bluetooth GPS device.
     * @param url
     *            is the connection URL to the Bluetooth GPS device.
     */
    public void setBluetoothGPS(String name, String url) {
        // Record the Bluetooth information.
        this.gpsBluetoothName = name;
        this.gpsBluetoothURL = url;

        Log.info("GPS set to Bluetooth " + name);
    }

    /**
     * Gets the human-readable name of the Bluetooth GPS device. This can be
     * displayed on screens to the user.
     * <p>
     * Before calling this method you should check that Bluetooth GPS is being
     * used. If <code>isGPSOnDevice</code> returns <code>Boolean.FALSE</code>
     * then you can call this method. Otherwise GPS information has not been set
     * or the GPS is on the local device.
     * 
     * @return The human-readable name of the Bluetooth GPS device.
     */
    public String getBluetoothGPSName() {
        return gpsBluetoothName;
    }

    /**
     * Returns the URL of the Bluetooth GPS device. This can be used to connect
     * to the remote GPS device through Bluetooth.
     * <p>
     * Before calling this method you should check that Bluetooth GPS is being
     * used. If <code>isGPSOnDevice</code> returns <code>Boolean.FALSE</code>
     * then you can call this method. Otherwise GPS information has not been set
     * or the GPS is on the local device.
     * 
     * @return The URL of the Bluetooth GPS device.
     */
    public String getBluetoothGPSURL() {
        return gpsBluetoothURL;
    }

}
