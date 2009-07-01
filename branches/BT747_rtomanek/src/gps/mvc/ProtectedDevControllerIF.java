/**
 * 
 */
package gps.mvc;

/**
 * @author Mario
 * 
 */
interface ProtectedDevControllerIF extends DeviceControllerIF {
    /**
     * Request specific data from the device.
     * 
     * @param dataType
     * @return true if supported.
     */
    public boolean reqData(final int dataType);

    /**
     * Called regularly so that the controller can notify the log download
     * handler for instance.
     */
    public void notifyRun();
    
    /**
     * The environment indicates a disconnect happened.
     */
    public void notifyDisconnected();
    
    public void setAgpsData(final byte[] agpsData);
}
