// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package bt747.sys;

import bt747.model.EventPoster;
import bt747.model.ModelEvent;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Hashtable;
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
        JavaLibBridge.debug(s, e);
    }

    public static final void debug(final String s) {
        JavaLibBridge.debug(JavaLibBridge.getTimeStamp() + " - " + s);
    }

    private static EventPoster poster = null;

    public static final void setPoster(final EventPoster poster) {
        Generic.poster = poster;
    }

    public static final void exception(final String s, final Throwable e) {
        debug(s, e);
        if (poster != null) {
            poster.postEvent(new ModelEvent(ModelEvent.EXCEPTION,
                    new BT747Exception(s, e)));
        }
    }

    /**
     * Get the debug level.
     * 
     * @return debug level.
     */
    public static final int getDebugLevel() {
        return Generic.debugLevel;
    }

    /**
     * Is debug information active
     * 
     * @return true if debug level is not 0.
     */
    public static final boolean isDebug() {
        return Generic.debugLevel != 0;
    }

    /**
     * Set the debug level.
     * 
     * @param level
     *            Level.
     */
    public static final void setDebugLevel(final int level) {
        Generic.debugLevel = level;
    }

    public static final int getTimeStamp() {
        return JavaLibBridge.getTimeStamp();
    }

    /**
     * Add a thread to the thread list.
     * 
     * @param o
     *            The thread.
     * @param highPrio
     *            True if high priority.
     */
    public static final void addThread(final BT747Thread o,
            final boolean highPrio) {
        JavaLibBridge.addThread(o, highPrio);
    }

    /**
     * Remove a thread from the thread list.
     * 
     * @param o
     *            Thread to remove.
     */
    public static final void removeThread(final BT747Thread o) {
        JavaLibBridge.removeThread(o);
    }

    /**
     * Calculate inverse cosinus
     * 
     * @param x
     *            x.
     * 
     * @return acos(x)
     */
    public static final double acos(final double x) {
        return JavaLibBridge.acos(x);
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
    public static final double pow(final double x, final double y) {
        return JavaLibBridge.pow(x, y);
    }

    /**
     * Fill a tokens list like this (for instance):<br>
     * <code>
     *   tokens.put("p", baseDir);<br>
     *   tokens.put("e", fileExt);<br>
     *   tokens.put("f", fileBase);<br>
     * </code> and <code>%p</code>, <code>%e</code>, <code>%f</code> will be
     * replaced accordingly.
     * 
     * @param s
     *            String witn '%c' tokens where %c will be replaced if 'c' in
     *            the list of tokens.
     * @param tokens
     *            Hashtable with tokens.
     * @return String with replaced values.
     */
    public static final String expandPercentTokens(final String s,
            final BT747Hashtable tokens) {
        String r = s;
        int lastIndex = 0;
        int percentIndex;
        while ((percentIndex = r.indexOf('%', lastIndex)) >= 0) {
            if (percentIndex + 1 < r.length()) {
                final char type = r.charAt(percentIndex + 1);
                final String replaceStr = (String) tokens.get(String
                        .valueOf(type));
                if (replaceStr == null) {
                    lastIndex = percentIndex + 1;
                } else {
                    r = r.substring(0, percentIndex) + replaceStr
                            + r.substring(percentIndex + 2);
                }
            } else {
                lastIndex = percentIndex + 1;
            }
        }
        return r;
    }
}
