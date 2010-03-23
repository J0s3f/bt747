/**
 * 
 */
package net.sf.bt747.j4me.app.conn;

/**
 * @author Mario
 *
 */
public interface BT747DiscoveryListener {
    /* (non-Javadoc)
     * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
     */
    public void deviceDiscovered(String deviceDescription);
}
