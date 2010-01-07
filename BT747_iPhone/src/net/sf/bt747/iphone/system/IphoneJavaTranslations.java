// ********************************************************************
// *** BT 747 ***
// *** (c)2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// ********************************************************************
package net.sf.bt747.iphone.system;

import java.text.DecimalFormat;
import java.util.Locale;

import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747File;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747HttpSender;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747RAFile;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747StringTokenizer;
import bt747.sys.interfaces.BT747Thread;
import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.BT747Vector;
import bt747.sys.interfaces.JavaLibImplementation;

public final class IphoneJavaTranslations implements JavaLibImplementation {
    public final BT747Date getDateInstance() {
        return new IphoneDate();
    }

    public final BT747Date getDateInstance(final int d, final int m,
            final int y) {
        return new IphoneDate(d, m, y);
    }

    public final BT747Date getDateInstance(final String strDate,
            final byte dateFormat) {
        return new IphoneDate(strDate, dateFormat);
    }

    public final BT747Hashtable getHashtableInstance(final int initialCapacity) {
        return new IphoneHashtable(initialCapacity);
    }

    public final BT747Vector getVectorInstance() {
        return new IphoneVector();
    }

    public final BT747Time getTimeInstance() {
        return new IphoneTime();
    }

    public final BT747File getFileInstance(final String path) {
        return new IphoneFile(path);
    }


    /* (non-Javadoc)
     * @see bt747.sys.interfaces.JavaLibImplementation#getFileInstance(bt747.sys.interfaces.BT747Path)
     */
    public BT747File getFileInstance(BT747Path path) {
        return new IphoneFile(path.getPath());
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.JavaLibImplementation#getRAFileInstance(bt747.sys.interfaces.BT747Path)
     */
    public BT747RAFile getRAFileInstance(BT747Path path) {
        return new IphoneFile(path.getPath());
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.JavaLibImplementation#getRAFileInstance(bt747.sys.interfaces.BT747Path, int)
     */
    public BT747RAFile getRAFileInstance(BT747Path path, int mode) {
        return new IphoneFile(path.getPath(), mode);
    }
    public final boolean isAvailable() {
        return true;
    }

    public final void debug(final String s, final Throwable e) {
        IphoneGeneric.debug(s, e);
    }

    public final double pow(final double x, final double y) {
        return Math.pow(x, y);
    }

    public final double acos(final double x) {
        return Math.acos(x);
    }

    public final void addThread(final BT747Thread t, final boolean b) {
        IphoneGeneric.addThread(t, b);
    }

    public final void removeThread(final BT747Thread t) {
        IphoneGeneric.removeThread(t);
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
    private static final DecimalFormat[] nf = new DecimalFormat[MAX_FRACTION + 1];
    private static final String DECIMALSTRING = "#######0.0000000000000000";

    static {
        final Locale l = Locale.getDefault();
        Locale.setDefault(Locale.US);
        for (int i = 0; i < nf.length; i++) {
            nf[i] = new DecimalFormat(DECIMALSTRING.substring(0, 9 + i));
            nf[i].setGroupingUsed(false);
        }
        Locale.setDefault(l);
    }

    public final synchronized String toString(final double p, final int i) {
        return nf[i].format(p);
    }

    private static final String ZEROSTRING = "0000000000000000";

    public final String unsigned2hex(final int p, final int i) {
        final String s = Integer.toHexString(p).toUpperCase();
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
    
    private final static void mySetAppSettings(final String settings) {
        appSettings = settings;
    }

    /**
     * @param appSettings
     *                the appSettings to set
     */
    public final void setAppSettings(final String settings) {
        mySetAppSettings(settings);
    }

    public final BT747Semaphore getSemaphoreInstance(final int value) {
        return new IphoneSemaphore(value);
    }

    public final BT747StringTokenizer getStringTokenizer(final String a,
            final char b) {
        return new IphoneStringTokenizer(a, b);
    }

    public final BT747HashSet getHashSetInstance() {
        return new IphoneHashSet();
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.JavaLibImplementation#getFileInstance(bt747.sys.interfaces.BT747Path, int)
     */
    public BT747File getFileInstance(BT747Path path, int mode) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.JavaLibImplementation#getHttpSenderInstance()
     */
    public BT747HttpSender getHttpSenderInstance() throws BT747Exception {
        // TODO Auto-generated method stub
        return null;
    }


}
