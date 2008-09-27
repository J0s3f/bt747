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
package net.sf.bt747.j2me.system;

import org.j4me.logging.Log;

import bt747.interfaces.BT747Date;
import bt747.interfaces.BT747File;
import bt747.interfaces.BT747Hashtable;
import bt747.interfaces.BT747Semaphore;
import bt747.interfaces.BT747Thread;
import bt747.interfaces.BT747Time;
import bt747.interfaces.BT747Vector;
import bt747.interfaces.Interface;
import bt747.interfaces.JavaTranslationsInterface;

public class JavaTranslations implements JavaTranslationsInterface {
    public final BT747Date getDateInstance() {
        return new J2MEDate();
    }

    public final BT747Date getDateInstance(final int d, final int m, final int y) {
        return new J2MEDate(d, m, y);
    }

    public final BT747Date getDateInstance(final String strDate,
            final byte dateFormat) {
        return new J2MEDate(strDate, dateFormat);
    }

    public final BT747Hashtable getHashtableInstance(final int initialCapacity) {
        return new J2MEHashtable(initialCapacity);
    }

    public final BT747Vector getVectorInstance() {
        return new J2MEVector();
    }

    public final BT747Time getTimeInstance() {
        return new J2METime();
    }

    public final BT747File getFileInstance(String path) {
        return new J2MEFile(path);
    }

    public final BT747File getFileInstance(String path, int mode, int card) {
        return new J2MEFile(path, mode, card);
    }

    public final BT747File getFileInstance(String path, int mode) {
        return new J2MEFile(path, mode);
    }

    // Currently buffered files are the same as normal files.

    public final BT747File getBufFileInstance(String path) {
        return new J2MEFile(path);
    }

    public final BT747File getBufFileInstance(String path, int mode, int card) {
        return new J2MEFile(path, mode, card);
    }

    public final BT747File getBufFileInstance(String path, int mode) {
        return new J2MEFile(path, mode);
    }

    public final boolean isAvailable() {
        return true;
    }

    public final void debug(final String s, final Throwable e) {
        Generic.debug(s, e);
    }

    public final double pow(final double x, final double y) {
        return Float11.pow(x, y);
    }

    public final double acos(final double x) {
        return Float11.acos(x);
    }

    public final void addThread(final BT747Thread t, final boolean b) {
        Generic.addThread(t, b);
    }

    public final void removeThread(final BT747Thread t) {
        Generic.removeThread(t);
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

    public final String toString(final double p, final int i) {
        StringBuffer s;
        if (p >= 1. || p <= -1.) {
            s = new StringBuffer(Double.toString(p));
        } else if (p >= 0.) {
            s = new StringBuffer(Double.toString(p + 1.));
            s.setCharAt(0, '0');
        } else {
            s = new StringBuffer(Double.toString(p - 1.));
            s.setCharAt(1, '0');
        }

        int dotPos;
        int diff;
        dotPos = s.toString().indexOf('.');
        diff = (dotPos + i + 1 - s.length());
        if (diff == 0) {
            // Do nothing
        } else if (diff > 0) {
            s.append(ZEROCHARS, 1, diff);
        } else if (dotPos == 0) {
            s.append(ZEROCHARS, 0, i + 1);
        } else {
            // Truncate - some limited notion of rounding.
            // TODO: generalize this.
            if (s.charAt(dotPos + i + 1) >= '5') {
                // Rounding is needed
                char c;
                c = s.charAt(dotPos + i);
                c += 1;
                if (c <= '9') {
                    s.setCharAt(dotPos + i, c);
                }
            }
            s.setLength(dotPos + i + 1);
        }
        return s.toString();
    }

    /**
     * Constant string of zeros used for padding.
     */
    private final static String ZEROSTRING = "0000000000000000";
    private final static char[] ZEROCHARS = { '.', '0', '0', '0', '0', '0',
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', };

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
        if (Log.isDebugEnabled()) {
            Log.debug(s);
        }
    }

    private static final long appStartTime = System.currentTimeMillis();

    public final int getTimeStamp() {
        // Returns the time in ms since the program started.
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
        return new J2MESemaphore(value);
    }

}
