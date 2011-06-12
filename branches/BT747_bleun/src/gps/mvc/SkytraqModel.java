/**
 * 
 */
package gps.mvc;

/**
 * The model for Skytraq Devices.
 * 
 * Pushing it a bit, because currently pointing to MtkModel.
 * That should be refactored.
 * 
 * @author Mario De Weerd
 *
 */
public class SkytraqModel extends MtkModel {
    
    /**
     * @param context
     * @param handler
     */
    public SkytraqModel(GpsModel context, GpsLinkHandler handler) {
        super(context, handler);
    }
}
