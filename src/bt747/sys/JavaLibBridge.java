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
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package bt747.sys;

import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747File;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747HttpSender;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747StringTokenizer;
import bt747.sys.interfaces.BT747Thread;
import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.BT747Vector;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * This implements the Bridge for java functionality that is not the same
 * across java platforms.
 * 
 * For more information regarding a bridge, see "Design Patterns" theory.
 * 
 * @author Mario De Weerd
 * 
 */
public final class JavaLibBridge {
    /**
     * The pointer to the implementation.
     */
    private static JavaLibImplementation imp;

    /**
     * Set the implementation.<br>
     * The implementation must be provided by the application.<br>
     * 
     * @param imp
     *            is the class.
     */
    public static final void setJavaLibImplementation(
            final JavaLibImplementation imp) {
        JavaLibBridge.imp = imp;
    }

    public static final BT747Hashtable getHashtableInstance(
            final int initialCapacity) {
        return JavaLibBridge.imp.getHashtableInstance(initialCapacity);
    }

    public static final BT747Semaphore getSemaphoreInstance(final int value) {
        return JavaLibBridge.imp.getSemaphoreInstance(value);
    }

    public static final BT747Time getTimeInstance() {
        return JavaLibBridge.imp.getTimeInstance();
    }

    public static final BT747StringTokenizer getStringTokenizerInstance(
            final String a, final char b) {
        return JavaLibBridge.imp.getStringTokenizer(a, b);
    }

    public static final String[] toStringArrayAndEmpty(
            final BT747Vector vector) {
        final String[] result = new String[vector.size()];
        for (int i = vector.size() - 1; i >= 0; i--) {
            result[i] = (String) vector.elementAt(i);
        }
        vector.removeAllElements();
        return result;
    }

    public static final BT747Vector getVectorInstance() {
        return JavaLibBridge.imp.getVectorInstance();
    }

    public static final BT747HashSet getHashSetInstance() {
        return JavaLibBridge.imp.getHashSetInstance();
    }

    public static final BT747Date getDateInstance() {
        return JavaLibBridge.imp.getDateInstance();
    }

    public static final BT747Date getDateInstance(final int d, final int m,
            final int y) {
        return JavaLibBridge.imp.getDateInstance(d, m, y);
    }

    public static final BT747Date getDateInstance(final String date,
            final byte format) {
        return JavaLibBridge.imp.getDateInstance(date, format);
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
        JavaLibBridge.imp.addThread(o, highPrio);
    }

    /**
     * Remove a thread from the thread list.
     * 
     * @param o
     *            Thread to remove.
     */
    public static final void removeThread(final BT747Thread o) {
        JavaLibBridge.imp.removeThread(o);
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
        return JavaLibBridge.imp.acos(x);
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
        return JavaLibBridge.imp.pow(x, y);
    }

    public static final int getTimeStamp() {
        return JavaLibBridge.imp.getTimeStamp();
    }

    public static final void debug(final String s, final Throwable e) {
        if (s != null) {
            JavaLibBridge.imp.debug(s, e);
        }
    }

    public static final void debug(final String s) {
        JavaLibBridge.imp.debug(s);
    }

    /**
     * @return the appSettings
     */
    public static final String getAppSettings() {
        return JavaLibBridge.imp.getAppSettings();
    }

    /**
     * @param appSettings
     *            the appSettings to set
     */
    public static final void setAppSettings(final String appSettings) {
        JavaLibBridge.imp.setAppSettings(appSettings);
    }

    public static final BT747File getFileInstance(final BT747Path path) {
        return JavaLibBridge.imp.getFileInstance(path);
    }

    public static final BT747File getFileInstance(final BT747Path path,
            final int mode) {
        return JavaLibBridge.imp.getFileInstance(path, mode);
    }

    public static final boolean isAvailable() {
        return JavaLibBridge.imp.isAvailable();
    }

    public static final double longBitsToDouble(final long l) {
        return JavaLibBridge.imp.longBitsToDouble(l);
    }

    public static final double toDouble(final String s) {
        return JavaLibBridge.imp.toDouble(s);
    }

    public static final float toFloat(final String s) {
        return JavaLibBridge.imp.toFloat(s);
    }

    public static final float toFloatBitwise(final int l) {
        return JavaLibBridge.imp.toFloatBitwise(l);
    }

    public static final int toInt(final String s) {
        return JavaLibBridge.imp.toInt(s);
    }

    public static final int toIntBitwise(final float f) {
        return JavaLibBridge.imp.toIntBitwise(f);
    }

    public static final String toString(final boolean p) {
        return JavaLibBridge.imp.toString(p);
    }

    public static final String toString(final double p) {
        return JavaLibBridge.imp.toString(p);
    }

    public static final String toString(final double p, final int i) {
        return JavaLibBridge.imp.toString(p, i);
    }

    public static final String toString(final float p) {
        return JavaLibBridge.imp.toString(p);
    }

    public static final String toString(final int p) {
        return "" + p;
    }

    public static final String unsigned2hex(final int p, final int i) {
        return JavaLibBridge.imp.unsigned2hex(p, i);
    }

    /**
     * Retrieve an platform specific instance of a class implementing the
     * BT747HttpSender interface.
     * 
     * @return an instance of a class implementing BT747HttpSender
     * @author Florian Unger
     * @throws BT747Exception
     */
    public static final BT747HttpSender getHttpSenderInstance()
            throws BT747Exception {
        return JavaLibBridge.imp.getHttpSenderInstance();
    }
}
