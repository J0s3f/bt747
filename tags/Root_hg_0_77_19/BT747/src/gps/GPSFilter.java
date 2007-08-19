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
    private int startDate=0; // Seconds since 1/1/1970
    // TODO: could fix problem with negative values for date.
    private int endDate=0x7FFFFFFF; // Seconds since 1/1/1970
    private int validMask=0xFFFFFFFE; // Valid mask
    private int rcrMask=0xFFFFFFFF;
    
    public final static int C_TRKPT_IDX=0;
    public final static int C_WAYPT_IDX=1;

    public int getEndDate() {
        return endDate;
    }
    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }
    public int getRcrMask() {
        return rcrMask;
    }
    public void setRcrMask(int rcrMask) {
        this.rcrMask = rcrMask;
    }
    public int getStartDate() {
        return startDate;
    }
    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }
    public int getValidMask() {
        return validMask;
    }
    public void setValidMask(int validMask) {
        this.validMask = validMask;
    }
    
    public boolean doFilter(GPSRecord r) {
        // Filter the record information
        boolean z_Result;
        z_Result=(r.utc>=startDate) && (r.utc<=endDate)
                &&((validMask==0)||(r.valid&validMask)!=0)
                &&((rcrMask==0)||(r.rcr&rcrMask)!=0);
        
        return z_Result;
    }

}
