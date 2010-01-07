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
// *** This layer was written for the SuperWaba toolset. ***
// *** This is a proprietary development environment based in ***
// *** part on the Waba development environment developed by ***
// *** WabaSoft, Inc. ***
// ********************************************************************
package net.sf.bt747.waba.system;

import waba.sys.Vm;

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

public final class WabaJavaTranslations implements JavaLibImplementation {
    public final BT747Date getDateInstance() {
        return new WabaDate();
    }

    public final BT747Date getDateInstance(final int d, final int m,
            final int y) {
        return new WabaDate(d, m, y);
    }

    public final BT747Date getDateInstance(final String strDate,
            final byte dateFormat) {
        return new WabaDate(strDate, dateFormat);
    }

    public final BT747Hashtable getHashtableInstance(final int initialCapacity) {
        return new WabaHashtable(initialCapacity);
    }

    public final BT747Vector getVectorInstance() {
        return new WabaVector();
    }

    public final BT747Time getTimeInstance() {
        return new WabaTime();
    }

    
    public final BT747RAFile getRAFileInstance(final BT747Path path) {
        if(path instanceof WabaPath) {
            final WabaPath p = (WabaPath) path;
            return new WabaFile(p.getPath(), p.getCard());
        } else {
            return new WabaFile(path.getPath());
        }
    }
    
    public final BT747File getFileInstance(final BT747Path path) {
        return getRAFileInstance(path);
    }

    public final BT747RAFile getRAFileInstance(final BT747Path path,
            final int mode) {
        int localMode;
        if (mode == bt747.sys.File.WRITE_ONLY) {
            // On SuperWaba, WRITE_ONLY might erase, so transforming in
            // READ_WRITE.
            // Must be in append mode too.
            localMode = waba.io.File.READ_WRITE;
        } else {
            localMode = mode;
        }
        if (path instanceof WabaPath) {
            final WabaPath p = (WabaPath) path;
            return new WabaFile(p.getPath(), localMode, p.getCard());
        } else {
            return new WabaFile(path.getPath(), localMode);
        }
    }

    public final BT747File getFileInstance(final BT747Path path,
            final int mode) {
        return getRAFileInstance(path, mode);
    }

//    public final BT747File getRAFileInstance(final String path, final int mode) {
//        int localMode;
//        if (mode == bt747.sys.File.WRITE_ONLY) {
//            // On SuperWaba, WRITE_ONLY might erase, so transforming in
//            // READ_WRITE.
//            // Must be in append mode too.
//            localMode = waba.io.File.READ_WRITE;
//        } else {
//            localMode = mode;
//        }
//        return new WabaFile(path, localMode);
//    }

    public final boolean isAvailable() {
        return waba.io.File.isAvailable();
    }

    public final void debug(final String s, final Throwable e) {
        Vm.debug(s);
        if (e != null) {
            e.printStackTrace();
        }
    }

    public final double pow(final double x, final double y) {
        return Math.pow(x, y);
    }

    public final double acos(final double x) {
        return Math.acos(x);
    }

    public final void addThread(final BT747Thread t, final boolean b) {
        WabaGeneric.addThread(t, b);
    }

    public final void removeThread(final BT747Thread t) {
        WabaGeneric.removeThread(t);
    }

    /**
     * Math
     * 
     */
    public final String toString(final boolean p) {
        return waba.sys.Convert.toString(p);
    }

    public String toString(final int p) {
        return waba.sys.Convert.toString(p);
    }

    public String toString(final float p) {
        return waba.sys.Convert.toString(p);
    }

    public final String toString(final double p) {
        return waba.sys.Convert.toString(p);
    }

    public final String toString(final double p, final int i) {
        return waba.sys.Convert.toString(p, i);
    }

    public final String unsigned2hex(final int p, final int i) {
        return waba.sys.Convert.unsigned2hex(p, i);
    }

    public final int toInt(final String s) {
        return waba.sys.Convert.toInt(s);
    }

    public final float toFloat(final String s) {
        return waba.sys.Convert.toFloat(s);
    }

    public final double toDouble(final String s) {
        return waba.sys.Convert.toDouble(s);
    }

    public final double longBitsToDouble(final long l) {
        return waba.sys.Convert.longBitsToDouble(l);
    }

    public final float toFloatBitwise(final int l) {
        return waba.sys.Convert.toFloatBitwise(l);
    }

    public final int toIntBitwise(final float f) {
        return waba.sys.Convert.toIntBitwise(f);
    }

    public final void debug(final String s) {
        // TODO if (Log.isDebugEnabled()) {
        waba.sys.Vm.debug(s);
    }

    public final int getTimeStamp() {
        return waba.sys.Vm.getTimeStamp();
    }

    /**
     * @return the appSettings
     */
    public final String getAppSettings() {
        return waba.sys.Settings.appSettings;
    }

    private static final void mySetAppSettings(final String appSettings) {
        waba.sys.Settings.appSettings = appSettings;
    }

    /**
     * @param appSettings
     *            the appSettings to set
     */
    public final void setAppSettings(final String appSettings) {
        mySetAppSettings(appSettings);
    }

    public final BT747Semaphore getSemaphoreInstance(final int value) {
        return new WabaSemaphore(value);
    }

    public final BT747StringTokenizer getStringTokenizer(final String a,
            final char b) {
        return new WabaStringTokenizer(a, b);
    }

    public final BT747HashSet getHashSetInstance() {
        return new WabaHashSet();
    }

    public BT747HttpSender getHttpSenderInstance() throws BT747Exception {
        throw new BT747Exception(
                "This feature is not implemented in this platform!");
    }
}
