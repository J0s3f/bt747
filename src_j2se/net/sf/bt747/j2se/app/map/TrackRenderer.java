package net.sf.bt747.j2se.app.map;

import gps.log.GPSRecord;

import java.awt.Graphics2D;
import java.util.Collection;

import org.jdesktop.swingx.JXMapViewer;

/**
 * An interface that draws tracks.
 */
public interface TrackRenderer {
    /**
     * paint the specified waypoint on the specified map and graphics context
     * 
     * @param g
     * @param map
     * @param waypoint
     * @return
     */
    public boolean paintTrack(Graphics2D g, JXMapViewer map,
            Collection<GPSRecord> track);

}
