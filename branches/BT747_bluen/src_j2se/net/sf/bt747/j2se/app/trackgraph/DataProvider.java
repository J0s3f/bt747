package net.sf.bt747.j2se.app.trackgraph;

import info.monitorenter.gui.chart.TracePoint2D;

import java.util.Iterator;

public interface DataProvider {

	public Iterator<TracePoint2D> getHeightIter();
	public Iterator<TracePoint2D> getSpeedIter();

}