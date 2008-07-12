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
    int parseFile(final GPSFile gpsFile);

    void setTimeOffset(final long offset);

    void setNoGeoid(final boolean b);

    int toGPSFile(
            final String fileName,
            final GPSFile gpsFile,
            final int card);
    
    String getErrorInfo();
}