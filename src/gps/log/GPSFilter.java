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
package gps.log;

import bt747.sys.Convert;

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
public class GPSFilter {
    private int startTime = 0; // Seconds since 1/1/1970
    // TODO: could fix potential problem with negative values for date.
    private int endTime = 0x7FFFFFFF; // Seconds since 1/1/1970
    private int validMask = 0xFFFFFFFE; // Valid mask
    private int rcrMask = 0xFFFFFFFF;

    /**
     * Index to trackpoint filter (in array of GPSFilter).
     */
    public static final int TRKPT = 0;
    /**
     * Index to waypoint filter (in array of GPSFilter).
     */
    public static final int WAYPT = 1;

    /**
     * Get the first date of the period to include.
     * 
     * @return The first date of the period to include (UTC time value).
     */
    public final int getStartTime() {
        return startTime;
    }

    /**
     * Set the first date of the period to include (UTC time value).
     */
    public final void setStartTime(final int startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the last date of the period to include.
     * 
     * @return The last date of the period to include (UTC time value).
     */
    public final int getEndTime() {
        return endTime;
    }

    /**
     * Set the last date of the period to include (UTC time value).
     */
    public final void setEndTime(final int endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the 'RCR' filter mask for the given filter type and the currently
     * active filters.
     * 
     * Use the following constants:<br>-
     * {@link gps.BT747Constants#RCR_TIME_MASK}<br>-
     * {@link gps.BT747Constants#RCR_SPEED_MASK}<br>-
     * {@link gps.BT747Constants#RCR_DISTANCE_MASK}<br>-
     * {@link gps.BT747Constants#RCR_BUTTON_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP0_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP1_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP2_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP3_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP4_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP5_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP6_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP7_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP8_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APP9_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APPY_MASK}<br>-
     * {@link gps.BT747Constants#RCR_APPZ_MASK}<br>-
     * {@link gps.BT747Constants#RCR_ALL_APP_MASK}
     * 
     */
    public final int getRcrMask() {
        return rcrMask;
    }

    /**
     * Sets the 'RCR' filter mask for the given filter type and the currently
     * active filters.
     * 
     * @param rcrMask
     *                The filter mask to set for the rcr filter. Use the
     *                following constants:<br>-
     *                {@link gps.BT747Constants#RCR_TIME_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_SPEED_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_DISTANCE_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_BUTTON_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP0_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP1_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP2_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP3_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP4_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP5_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP6_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP7_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP8_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APP9_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APPY_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_APPZ_MASK}<br>-
     *                {@link gps.BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void setRcrMask(final int rcrMask) {
        this.rcrMask = rcrMask;
    }

    /**
     * Gets the 'Valid' filter mask.
     * 
     * Use the following constants:<br>-
     * {@link gps.BT747Constants#VALID_NO_FIX_MASK} <br>-
     * {@link gps.BT747Constants#VALID_SPS_MASK} <br>-
     * {@link gps.BT747Constants#VALID_DGPS_MASK} <br>-
     * {@link gps.BT747Constants#VALID_PPS_MASK} <br>-
     * {@link gps.BT747Constants#VALID_RTK_MASK} <br>-
     * {@link gps.BT747Constants#VALID_FRTK_MASK} <br>-
     * {@link gps.BT747Constants#VALID_ESTIMATED_MASK} <br>-
     * {@link gps.BT747Constants#VALID_MANUAL_MASK} <br>-
     * {@link gps.BT747Constants#VALID_SIMULATOR_MASK}
     * 
     */
    public final int getValidMask() {
        return validMask;
    }

    /**
     * Sets the 'Valid' filter mask.
     * 
     * @param validMask
     *                The filter mask to set for the validity filter. Use the
     *                following constants:<br>-
     *                {@link gps.BT747Constants#VALID_NO_FIX_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_SPS_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_DGPS_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_PPS_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_RTK_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_FRTK_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_ESTIMATED_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_MANUAL_MASK} <br>-
     *                {@link gps.BT747Constants#VALID_SIMULATOR_MASK}
     * 
     */
    public final void setValidMask(final int validMask) {
        this.validMask = validMask;
    }

    /**
     * Filter the GPS record
     * 
     * @param r
     *                The GPS record
     * @return true if the record is selected by the filter
     */
    public boolean doFilter(final GPSRecord r) {
        // Filter the record information
        final boolean result = (((r.utc >= 0) && (r.utc <= 3600 * 24)) || ((r.utc >= startTime) && (r.utc <= endTime)))
                && (!r.hasValid() || ((r.valid & validMask) != 0))
                && (!r.hasRcr() || ((r.rcr & rcrMask) != 0));

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return startTime + "<UTC<" + endTime + "\n" + "VALIDMASK="
                + Convert.unsigned2hex(validMask, 8) + "\n" + "RCRMASK="
                + Convert.unsigned2hex(rcrMask, 8) + "\n";
        // return super.toString();
    }
}
