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
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author Clemens Lanthaler clemens.lanthaler@itarchitects.at
 */
public class JXPainterTest implements Painter<JXMapViewer> {

    public void paint(Graphics2D gd, JXMapViewer t, int i, int i1) {
        gd = (Graphics2D) gd.create();
        gd.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //convert from viewport to world bitmap
        Rectangle rect = t.getViewportBounds();
        gd.translate(-rect.x, -rect.y);

        //convert geo to world bitmap pixel
        GeoPosition gp = new GeoPosition(51.5, 0);
        Point2D center = t.getTileFactory().geoToPixel(gp, t.getZoom());
        Ellipse2D el = new Ellipse2D.Double(center.getX()-5, center.getY()-5, 10, 10);
        //do the drawing
        gd.setColor(Color.black);
        gd.fill(el);
        gd.draw(el);
        gd.dispose();
    }

}
