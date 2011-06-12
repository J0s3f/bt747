package net.sf.bt747.j2se.app.trackgraph;

import info.monitorenter.gui.chart.ZoomableChart;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * A listener for TrackDisplay component.
 * 
 * The only default behavior is a zoom reset on right-click.
 * 
 * TODO: Maybe add zoom functionality for mouse wheel
 * 
 * @author Matthias
 * 
 */
public abstract class TrackDisplayMouseListener extends MouseAdapter {

    protected ZoomableChart chart;

    /**
     * Customizable behavior for left-click.
     * 
     * @param ev
     */
    abstract protected void leftClick(MouseEvent ev);

    public TrackDisplayMouseListener(ZoomableChart ch) {
        chart = ch;
    }

    @Override
    public void mouseClicked(MouseEvent ev) {
        int but = ev.getButton();

        if (but == MouseEvent.BUTTON1)
            this.leftClick(ev);
        else if (but == MouseEvent.BUTTON3)
            chart.zoomAll();
    }
}
