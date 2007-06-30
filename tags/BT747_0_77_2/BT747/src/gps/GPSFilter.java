/*
 * Created on 26 juin 2007
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
public class GPSFilter {
    public int startDate=0; // Seconds since 1/1/1970
    // TODO: could fix problem with negative values for date.
    public int endDate=0x7FFFFFFF; // Seconds since 1/1/1970
    public int validMask=0xFFFFFFFE; // Valid mask
    public int rcrMask=0xFFFFFFFF;
    
    public boolean doFilter(GPSRecord r) {
        // Filter the record information
        boolean z_Result;
        z_Result=(r.utc>=startDate) && (r.utc<=endDate)
                &&((validMask==0)||(r.valid&validMask)!=0)
                &&((rcrMask==0)||(r.rcr&rcrMask)!=0);
        
        return z_Result;
    }

}
