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
public class GPSFilterAdvanced extends GPSFilter {
    protected float minSpeed=-1;
    protected float maxSpeed=-1;
    protected int maxPDOP=-1;
    protected int maxHDOP=-1;
    protected int maxVDOP=-1;
    protected double minDist=-1;
    protected double maxDist=-1;
    protected int minNSAT=-1;
    protected int minRecCount=-1;
    protected int maxRecCount=-1;
    
    public boolean doFilter(GPSRecord r) {
        
        // Filter the record information
        boolean z_Result;
        z_Result=super.doFilter(r)
            &&((r.recCount<=0)||((minRecCount<0)||(r.recCount>=minRecCount)))
            &&((r.recCount<=0)||((maxRecCount<0)||(r.recCount<=maxRecCount)))
            &&((r.speed<=0)||((minSpeed<0)||(r.speed>=minSpeed)))
            &&((r.speed<=0)||((maxSpeed<0)||(r.speed<=maxSpeed)))
            &&((r.distance<=0)||((minDist<0)||(r.distance>=minDist)))
            &&((r.distance<=0)||((maxDist<0)||(r.distance<=maxDist)))
            &&((r.pdop<=0)||((maxPDOP<0)||(r.pdop<=maxPDOP)))
            &&((r.hdop<=0)||((maxHDOP<0)||(r.hdop<=maxHDOP)))
            &&((r.vdop<=0)||((maxVDOP<0)||(r.vdop<=maxVDOP)))
            &&((r.nsat<0)||((minNSAT<0)||(((r.nsat&0xFF00)>>8)<=minNSAT)))
            ;
        
        return z_Result;
    }

    /**
     * @return Returns the maxDist.
     */
    public float getMaxDist() {
        return (float)maxDist;
    }
    /**
     * @param maxDist The maxDist to set.
     */
    public void setMaxDist(float maxDist) {
        this.maxDist = maxDist;
    }
    /**
     * @return Returns the maxHDOP.
     */
    public float getMaxHDOP() {
        return maxHDOP;
    }
    /**
     * @param maxHDOP The maxHDOP to set.
     */
    public void setMaxHDOP(int maxHDOP) {
        this.maxHDOP = maxHDOP;
    }
    /**
     * @return Returns the maxPDOP.
     */
    public float getMaxPDOP() {
        return maxPDOP;
    }
    /**
     * @param maxPDOP The maxPDOP to set.
     */
    public void setMaxPDOP(int maxPDOP) {
        this.maxPDOP = maxPDOP;
    }
    /**
     * @return Returns the maxRecCnt.
     */
    public int getMaxRecCount() {
        return maxRecCount;
    }
    /**
     * @param maxRecCnt The maxRecCnt to set.
     */
    public void setMaxRecCount(int maxRecCnt) {
        this.maxRecCount = maxRecCnt;
    }
    /**
     * @return Returns the maxSpeed.
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }
    /**
     * @param maxSpeed The maxSpeed to set.
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    /**
     * @return Returns the maxVDOP.
     */
    public float getMaxVDOP() {
        return maxVDOP;
    }
    /**
     * @param maxVDOP The maxVDOP to set.
     */
    public void setMaxVDOP(int maxVDOP) {
        this.maxVDOP = maxVDOP;
    }
    /**
     * @return Returns the minDist.
     */
    public float getMinDist() {
        return (float)minDist;
    }
    /**
     * @param minDist The minDist to set.
     */
    public void setMinDist(float minDist) {
        this.minDist = minDist;
    }
    /**
     * @return Returns the minNSAT.
     */
    public int getMinNSAT() {
        return minNSAT;
    }
    /**
     * @param minNSAT The minNSAT to set.
     */
    public void setMinNSAT(int minNSAT) {
        this.minNSAT = minNSAT;
    }
    /**
     * @return Returns the minRecCnt.
     */
    public int getMinRecCnt() {
        return minRecCount;
    }
    /**
     * @param minRecCnt The minRecCnt to set.
     */
    public void setMinRecCnt(int minRecCnt) {
        this.minRecCount = minRecCnt;
    }
    /**
     * @return Returns the minSpeed.
     */
    public float getMinSpeed() {
        return minSpeed;
    }
    /**
     * @param minSpeed The minSpeed to set.
     */
    public void setMinSpeed(float minSpeed) {
        this.minSpeed = minSpeed;
    }
}
