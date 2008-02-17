//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package gps;

/** This class implements a filter for a {@link GPSRecord}.
 * 
 * A {@link GPSRecord} can be presented to the doFilter method and
 * it will indicate whether the record must be kept or not.
 * 
 * This class simplifies setting filter options in the application.
 * 
 * @author Mario De Weerd
 */
public class GPSFilter {
    private int startDate=0; // Seconds since 1/1/1970
    // TODO: could fix problem with negative values for date.
    private int endDate=0x7FFFFFFF; // Seconds since 1/1/1970
    private int validMask=0xFFFFFFFE; // Valid mask
    private int rcrMask=0xFFFFFFFF;
    
    public static final int C_TRKPT_IDX=0;
    public static final int C_WAYPT_IDX=1;

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
                &&((r.valid&validMask)!=0)
                &&((r.rcr&rcrMask)!=0);
        
        return z_Result;
    }

}
