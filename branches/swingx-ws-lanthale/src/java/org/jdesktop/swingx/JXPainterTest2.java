/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

/**
 *
 * @author Clemens Lanthaler clemens.lanthaler@itarchitects.at
 */
public class JXPainterTest2 extends WaypointPainter<JXMapViewer> {

    

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        super.doPaint(g, map, width, height);
        g = (Graphics2D) g.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        //convert geo to world bitmap pixel
        GeoPosition gp = new GeoPosition(51.5, 0);
        Point2D center = map.getTileFactory().geoToPixel(gp, map.getZoom());
        Ellipse2D el = new Ellipse2D.Double(center.getX()-5, center.getY()-5, 10, 10);
        //do the drawing
        g.setColor(Color.black);
        g.fill(el);
        g.draw(el);
        g.dispose();
    }



}
