/**
 * 
 */
package net.sf.bt747.j2se.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

/**
 * @author Mario
 *
 */
public class WaypointComponent extends JComponent {

    private Color color = new Color(0,255,255,200);

    float x;
    float y;
    /**
     * 
     */
    public WaypointComponent(int x, int y) {
       this.x = x;
       this.y = y;
    }
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // TODO Auto-generated method stub
        final float scale = 4f;
        GeneralPath gp = new GeneralPath();
        Line2D l1 = new Line2D.Float(0.f,0.f,-1*scale,-2*scale);
        gp.append(l1,true);
        final float diam = 2*scale;
        Arc2D cc = new Arc2D.Float(-scale,-2*scale-(diam/2),diam,diam,180.f,-180.f,Arc2D.OPEN);
        //        Float(
//        100.0f, 100.0f, // starting point
//        125.0f, 125.0f, // control point 1
//        150.0f, 125.0f, // control point 2
//        175.0f, 100.0f // ending point );
        gp.append(cc,true);
//        Line2D l2 = new Line2D.Float(100.0f,100.0f,
//        0.0f,0.0f);
        //gp.append(l2,true);
        gp.closePath();
        color = new Color(255,0,0,125);
        g2d.setColor(color);
        g2d.translate(10, 2);
        g2d.draw(gp);
        //g2d.translate(-g2d.getTransform().getTranslateX(), -g2d.getTransform().getTranslateY());
        g2d.translate(-25, 25);
        color = new Color(0,255,0,125);
        g2d.setColor(color);
        g2d.draw(gp);
        g2d.translate(100,-150);
        g2d.getTransform().getTranslateX();
        color = new Color(0,0,255,125);
        g2d.setColor(color);
        //g2d.draw(gp);
        g2d.fill(gp);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        // TODO Auto-generated method stub
        return new Dimension(10,10);
    }
}
