/*
 * Created on 3 f�vr. 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gps;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface GPSLogConvert {
    public abstract void parseFile(final GPSFile gpsFile);

    public abstract void setTimeOffset(long offset);

    public abstract void setNoGeoid(boolean b);

    public abstract void toGPSFile(final String fileName,
            final GPSFile gpsFile, final int Card);
}