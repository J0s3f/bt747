/**
 * 
 */
package gps.mvc;

import bt747.sys.BT747Exception;

/**
 * Defines the interface for a coordinated operation that is managed with the
 * device. For example log download or AGPS data upload.
 * 
 * @author Mario
 * 
 */
public interface DeviceOperationHandlerIF {
    /**
     * This method is called on regular intervals (every 10ms or so) so that
     * this Handler can send commands to the device as needed.
     * 
     * @param handler
     *                The handler to use the serial link.
     * @return true if handler must be maintained. If false, handler will no
     *         longer be called and dereferenced in the link controller.
     * @throws BT747Exception 
     */
    public boolean notifyRun(GPSLinkHandler handler) throws BT747Exception;

    /**
     * This method is called when a packet of some kind was received from the
     * serial link.
     * 
     * @param o
     *                The receive packet.
     * @return true if the packet was treated and should not be treated
     *         elsewhere in the application.
     */
    public boolean analyseResponse(Object o);
}
