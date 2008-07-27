/*
 * Created on 3 févr. 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.bt747.gps.log.in;

import net.sourceforge.bt747.gps.log.out.GPSFile;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface GPSLogConvert {
    int parseFile(final GPSFile gpsFile);

    void setTimeOffset(final long offset);

    void setConvertWGS84ToMSL(final boolean b);

    int toGPSFile(final String fileName, final GPSFile gpsFile, final int card);

    String getErrorInfo();
}