/**
 * 
 */
package net.sf.bt747.j2se.map;

import org.jdesktop.swingx.mapviewer.Waypoint;

import bt747.j2se_view.image.ImageData;

/**
 * @author Mario
 *
 */
public class ImageDataWaypointAdaptor extends Waypoint {
    private ImageData id;
    
    public ImageDataWaypointAdaptor(ImageData id) {
        this.id = id;
    }
    
    
}
