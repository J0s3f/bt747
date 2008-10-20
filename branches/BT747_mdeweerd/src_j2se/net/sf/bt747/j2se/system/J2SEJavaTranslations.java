//********************************************************************
//***                           BT 747                             ***
//***                  (c)2008 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//********************************************************************
package net.sf.bt747.j2se.system;

import java.text.NumberFormat;
import java.util.Locale;

import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747File;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747StringTokenizer;
import bt747.sys.interfaces.BT747Thread;
import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.BT747Vector;
import bt747.sys.interfaces.JavaTranslationsInterface;

public final class J2SEJavaTranslations implements JavaTranslationsInterface {
    public final BT747Date getDateInstance() {
        return new J2SEDate();
    }

    public final BT747Date getDateInstance(final int d, final int m, final int y) {
        return new J2SEDate(d, m, y);
    }

    public final BT747Date getDateInstance(final String strDate,
            final byte dateFormat) {
        return new J2SEDate(strDate, dateFormat);
    }

    public final BT747Hashtable getHashtableInstance(final int initialCapacity) {
        return new J2SEHashtable(initialCapacity);
    }

    public final BT747Vector getVectorInstance() {
        return new J2SEVector();
    }

    public final BT747Time getTimeInstance() {
        return new J2SETime();
    }

    public final BT747File getFileInstance(String path) {
        return new J2SEFile(path);
    }

    public final BT747File getFileInstance(String path, int mode, int card) {
        return new J2SEFile(path, mode, card);
    }

    public final BT747File getFileInstance(String path, int mode) {
        return new J2SEFile(path, mode);
    }

    public final boolean isAvailable() {
        return true;
    }

    public final void debug(final String s, final Throwable e) {
        J2SEGeneric.debug(s, e);
    }

    public final double pow(final double x, final double y) {
        return Math.pow(x, y);
    }

    public final double acos(final double x) {
        return Math.acos(x);
    }

    public final void addThread(final BT747Thread t, final boolean b) {
        J2SEGeneric.addThread(t, b);
    }

    public final void removeThread(final BT747Thread t) {
        J2SEGeneric.removeThread(t);
    }

    /**
     * Math
     * 
     */
    public final String toString(final boolean p) {
        return String.valueOf(p);
    }

    public final String toString(final int p) {
        return String.valueOf(p);
    }

    public final String toString(final float p) {
        return Float.toString(p);
    }

    public final String toString(final double p) {
        return Double.toString(p);
    }

    private static final int MAX_FRACTION = 16;
    private static final NumberFormat[] nf = new NumberFormat[MAX_FRACTION + 1];

    static {
        for (int i = 0; i < nf.length; i++) {
            nf[i] = NumberFormat.getNumberInstance(Locale.US);
            nf[i].setMaximumFractionDigits(i);
            nf[i].setMinimumFractionDigits(i);
        }
    }

    public final synchronized String toString(final double p, final int i) {
        return nf[i].format(p);
    }

    private static final String ZEROSTRING = "0000000000000000";

    public final String unsigned2hex(final int p, final int i) {
        String s = Integer.toHexString(p).toUpperCase();
        if (s.length() == i) {
            return s;
        } else if (s.length() < i) {
            return ZEROSTRING.substring(ZEROSTRING.length() - i + s.length())
                    .concat(s);
        } else {
            return s.substring(s.length() - i);
        }
    }

    public final int toInt(final String s) {
        return Integer.valueOf(s).intValue();
    }

    public final float toFloat(final String s) {
        return Float.parseFloat(s);
    }

    public final double toDouble(final String s) {
        return Double.parseDouble(s);
    }

    public final double longBitsToDouble(final long l) {
        return Double.longBitsToDouble(l);
    }

    public final float toFloatBitwise(final int l) {
        return Float.intBitsToFloat(l);
    }

    public final int toIntBitwise(final float f) {
        return Float.floatToIntBits(f);
    }

    public final void debug(final String s) {
        debug(s, null);
    }

    private static final long appStartTime = System.currentTimeMillis();

    public final int getTimeStamp() {
        return (int) (System.currentTimeMillis() - appStartTime);
    }

    private static String appSettings = ""; // TODO: Implement other solution

    /**
     * @return the appSettings
     */
    public final String getAppSettings() {
        return appSettings;
    }

    /**
     * @param appSettings
     *            the appSettings to set
     */
    public final void setAppSettings(final String settings) {
        appSettings = settings;
    }

    public final BT747Semaphore getSemaphoreInstance(final int value) {
        return new J2SESemaphore(value);
    }

    public final BT747StringTokenizer getStringTokenizer(final String a, final char b) {
        return new J2SEStringTokenizer(a,b);
    }
    
    public final BT747HashSet getHashSetInstance() {
        return new J2SEHashSet();
    }


}
