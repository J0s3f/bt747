/*
 * Created on 3 févr. 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gps.log.in;

import gps.log.out.GPSFile;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface GPSLogConvert {
    public abstract int parseFile(final GPSFile gpsFile);

    public abstract void setTimeOffset(long offset);

    public abstract void setNoGeoid(boolean b);

    public abstract int toGPSFile(final String fileName,
            final GPSFile gpsFile, final int Card);
    
    public abstract String getErrorInfo();
}