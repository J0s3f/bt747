// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
// *** The application was written using the SuperWaba toolset. ***
// *** This is a proprietary development environment based in ***
// *** part on the Waba development environment developed by ***
// *** WabaSoft, Inc. ***
// ********************************************************************
package gps.log;

/**
 * This class implements a filter for a {@link GPSRecord}.
 * 
 * A {@link GPSRecord} can be presented to the doFilter method and it will
 * indicate whether the record must be kept or not.
 * 
 * This class simplifies setting filter options in the application.
 * 
 * @author Mario De Weerd
 */
public class GPSFilterAdvanced extends GPSFilter {
    protected float minSpeed = -1;
    protected float maxSpeed = -1;
    protected int maxPDOP = -1;
    protected int maxHDOP = -1;
    protected int maxVDOP = -1;
    protected double minDist = -1;
    protected double maxDist = -1;
    protected int minNSAT = -1;
    protected int minRecCount = -1;
    protected int maxRecCount = -1;

    public boolean doFilter(final GPSRecord r) {

        // Filter the record information
        final boolean result = super.doFilter(r)
                && (!r.hasRecCount()
                    || ((minRecCount <= 0) || (r.recCount >= minRecCount))
                    || ((maxRecCount <= 0) || (r.recCount <= maxRecCount)))
                && (!r.hasSpeed()
                    || ((minSpeed <= 0) || (r.speed >= minSpeed))
                    || ((maxSpeed <= 0) || (r.speed <= maxSpeed)))
                && (!r.hasDistance()
                    || ((minDist <= 0) || (r.distance >= minDist))
                    || ((maxDist <= 0) || (r.distance <= maxDist)))
                && (!r.hasPdop() || ((maxPDOP <= 0) || (r.pdop <= maxPDOP)))
                && (!r.hasHdop() || ((maxHDOP <= 0) || (r.hdop <= maxHDOP)))
                && (!r.hasVdop() || ((maxVDOP <= 0) || (r.vdop <= maxVDOP)))
                && (!r.hasNsat() || ((minNSAT <= 0) || (((r.nsat & 0xFF00) >> 8) >= minNSAT)));

        return result;
    }

    /**
     * @return Returns the maxDist.
     */
    public final float getMaxDist() {
        return (float) maxDist;
    }

    /**
     * @param maxDist
     *                The maxDist to set.
     */
    public final void setMaxDist(final float maxDist) {
        this.maxDist = maxDist;
    }

    /**
     * @return Returns the maxHDOP.
     */
    public final float getMaxHDOP() {
        return maxHDOP;
    }

    /**
     * @param maxHDOP
     *                The maxHDOP to set.
     */
    public final void setMaxHDOP(final int maxHDOP) {
        this.maxHDOP = maxHDOP;
    }

    /**
     * @return Returns the maxPDOP.
     */
    public final float getMaxPDOP() {
        return maxPDOP;
    }

    /**
     * @param maxPDOP
     *                The maxPDOP to set.
     */
    public final void setMaxPDOP(final int maxPDOP) {
        this.maxPDOP = maxPDOP;
    }

    /**
     * @return Returns the maxRecCnt.
     */
    public final int getMaxRecCount() {
        return maxRecCount;
    }

    /**
     * @param maxRecCnt
     *                The maxRecCnt to set.
     */
    public final void setMaxRecCount(final int maxRecCnt) {
        maxRecCount = maxRecCnt;
    }

    /**
     * @return Returns the maxSpeed.
     */
    public final float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * @param maxSpeed
     *                The maxSpeed to set.
     */
    public final void setMaxSpeed(final float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * @return Returns the maxVDOP.
     */
    public final float getMaxVDOP() {
        return maxVDOP;
    }

    /**
     * @param maxVDOP
     *                The maxVDOP to set.
     */
    public final void setMaxVDOP(final int maxVDOP) {
        this.maxVDOP = maxVDOP;
    }

    /**
     * @return Returns the minDist.
     */
    public final float getMinDist() {
        return (float) minDist;
    }

    /**
     * @param minDist
     *                The minDist to set.
     */
    public final void setMinDist(final float minDist) {
        this.minDist = minDist;
    }

    /**
     * @return Returns the minNSAT.
     */
    public final int getMinNSAT() {
        return minNSAT;
    }

    /**
     * @param minNSAT
     *                The minNSAT to set.
     */
    public final void setMinNSAT(final int minNSAT) {
        this.minNSAT = minNSAT;
    }

    /**
     * @return Returns the minRecCnt.
     */
    public final int getMinRecCnt() {
        return minRecCount;
    }

    /**
     * @param minRecCnt
     *                The minRecCnt to set.
     */
    public final void setMinRecCount(final int minRecCnt) {
        minRecCount = minRecCnt;
    }

    /**
     * @return Returns the minSpeed.
     */
    public final float getMinSpeed() {
        return minSpeed;
    }

    /**
     * @param minSpeed
     *                The minSpeed to set.
     */
    public final void setMinSpeed(final float minSpeed) {
        this.minSpeed = minSpeed;
    }
}
