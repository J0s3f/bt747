package net.sf.bt747.j2se.app.trackgraph;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.ZoomableChart;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import java.awt.Color;
import java.util.Iterator;

/**
 * Basic diagram for displaying height and speed for a GPS track.
 * 
 * @author Matthias
 * 
 */
public class TrackDisplay extends ZoomableChart {

    private static final long serialVersionUID = 4595807625415396401L;

    protected AAxis timeAxis;
    protected AAxis heightAxis = null;
    protected AAxis speedAxis = null;

    protected ITrace2D traceHeight;
    protected ITrace2D traceSpeed;

    public TrackDisplay() {
        super();

        timeAxis = new AxisLinear();
        heightAxis = new AxisLinear();
        speedAxis = new AxisLinear();

        // hint: hand over a SimpleDateFormat to show custom time formats
        timeAxis.setFormatter(new LabelFormatterDate());

        speedAxis.getAxisTitle().setTitleColor(Color.RED);
        heightAxis.getAxisTitle().setTitleColor(Color.GREEN);

        // TODO: L10N
        timeAxis.getAxisTitle().setTitle("Time");
        heightAxis.getAxisTitle().setTitle("Height");
        speedAxis.getAxisTitle().setTitle("Speed");

        setAxisXBottom(timeAxis);
        setAxisYLeft(heightAxis);
        setAxisYRight(speedAxis);

    }

    public void showHeightTrack(Iterator<TracePoint2D> pointiter) {
        hideHeightTrack();

        traceHeight = new Trace2DSimple();
        traceHeight.setColor(Color.GREEN);
        traceHeight.setName("");

        while (pointiter.hasNext())
            traceHeight.addPoint(pointiter.next());

        addTrace(traceHeight, timeAxis, heightAxis);

    }

    public void showSpeedTrack(Iterator<TracePoint2D> pointiter) {
        hideSpeedTrack();

        traceSpeed = new Trace2DSimple();
        traceSpeed.setColor(Color.RED);
        traceSpeed.setName("");

        while (pointiter.hasNext())
            traceSpeed.addPoint(pointiter.next());

        addTrace(traceSpeed, timeAxis, speedAxis);

    }

    public void hideSpeedTrack() {
        if (traceSpeed != null)
            removeTrace(traceSpeed);
    }

    public void hideHeightTrack() {
        if (traceHeight != null)
            removeTrace(traceHeight);
    }
}
