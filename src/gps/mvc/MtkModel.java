/**
 * 
 */
package gps.mvc;

/**
 * Model for Mtk devices.
 * 
 * Refactoring ongoing.
 * 
 * @author Mario De Weerd.
 * 
 */
public class MtkModel {
    private final GPSLinkHandler handler;

    MtkModel(final GPSLinkHandler handler) {
        this.handler = handler;
    }

    public GPSLinkHandler getHandler() {
        return handler;
    }

}
