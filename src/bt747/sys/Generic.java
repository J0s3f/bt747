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
package bt747.sys;

import bt747.sys.interfaces.BT747Thread;

/**
 * @author Mario De Weerd
 */
public final class Generic {
    /**
     * The currently set debugLevel
     */
    private static int debugLevel = 0;

    public static final void debug(final String s, final Throwable e) {
        if (s != null) {
            Interface.tr.debug(s, e);
        }
    }

    /**
     * Get the debug level.
     * 
     * @return debug leve.
     */
    public static int getDebugLevel() {
        return debugLevel;
    }

    /**
     * Is debug information active
     * 
     * @return true if debug level is not 0.
     */
    public static final boolean isDebug() {
        return debugLevel != 0;
    }

    /**
     * Set the debug level.
     * 
     * @param level
     *            Level.
     */
    public static final void setDebugLevel(final int level) {
        debugLevel = level;
    }

    /**
     * Calculate x^^y. (x to the power of y)
     * 
     * @param x
     *            x.
     * @param y
     *            y.
     * @return x^^y.
     */
    public final static double pow(final double x, final double y) {
        return Interface.tr.pow(x, y);
    }

    /**
     * Calculate inverse cosinus
     * 
     * @param x
     *            x.
     * 
     * @return acos(x)
     */
    public final static double acos(final double x) {
        return Interface.tr.acos(x);
    }

    /**
     * Add a thread to the thread list.
     * 
     * @param o
     *            The thread.
     * @param highPrio
     *            True if high priority.
     */
    public final static void addThread(final BT747Thread o,
            final boolean highPrio) {
        Interface.tr.addThread(o, highPrio);
    }

    /**
     * Remove a thread from the thread list.
     * 
     * @param o
     *            Thread to remove.
     */
    public final static void removeThread(final BT747Thread o) {
        Interface.tr.removeThread(o);
    }
}
