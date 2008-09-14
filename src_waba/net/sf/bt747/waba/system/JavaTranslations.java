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
//***  This layer was written for the SuperWaba toolset.           ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sf.bt747.waba.system;

import bt747.interfaces.BT747Date;
import bt747.interfaces.BT747File;
import bt747.interfaces.BT747Hashtable;
import bt747.interfaces.BT747Thread;
import bt747.interfaces.BT747Time;
import bt747.interfaces.BT747Vector;
import bt747.interfaces.Interface;
import bt747.interfaces.JavaTranslationsInterface;
import bt747.sys.Settings;

public class JavaTranslations implements JavaTranslationsInterface {
    public final BT747Date getDateInstance() {
        return new WabaDate();
    }

    public final BT747Date getDateInstance(final int d, final int m, final int y) {
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

    public final BT747File getFileInstance(String path) {
        return new WabaFile(path);
    }

    public final BT747File getFileInstance(String path, int mode, int card) {
        int localMode;
        if (mode == bt747.io.File.WRITE_ONLY) {
            // On SuperWaba, WRITE_ONLY might erase, so transforming in READ_WRITE.
            // Must be in append mode too.
            localMode = waba.io.File.READ_WRITE;
        } else {
            localMode = mode;
        }
        return new WabaFile(path, localMode, card);
    }

    public final BT747File getFileInstance(String path, int mode) {
        int localMode;
        if (mode == bt747.io.File.WRITE_ONLY) {
            // On SuperWaba, WRITE_ONLY might erase, so transforming in READ_WRITE.
            // Must be in append mode too.
            localMode = waba.io.File.READ_WRITE;
        } else {
            localMode = mode;
        }
        return new WabaFile(path, localMode);
    }


    public final boolean isAvailable() {
        return waba.io.File.isAvailable();
    }

    public final void debug(final String s, final Throwable e) {
        Generic.debug(s, e);
    }

    public final double pow(final double x, final double y) {
        return Math.pow(x, y);
    }

    public final double acos(final double x) {
        return Math.acos(x);
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
        return waba.sys.Convert.toString(p);
    }

    public final String toString(final int p) {
        return waba.sys.Convert.toString(p);
    }

    public final String toString(final float p) {
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

    public final int toInt(String s) {
        return waba.sys.Convert.toInt(s);
    }

    public final float toFloat(String s) {
        return waba.sys.Convert.toFloat(s);
    }

    public final double toDouble(String s) {
        return waba.sys.Convert.toDouble(s);
    }

    public final double longBitsToDouble(final long l) {
        return waba.sys.Convert.longBitsToDouble(l);
    }

    public final float toFloatBitwise(final int l) {
        return waba.sys.Convert.toFloatBitwise(l);
    }

    public final int toIntBitwise(float f) {
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

    /**
     * @param appSettings
     *            the appSettings to set
     */
    public final void setAppSettings(final String appSettings) {
        waba.sys.Settings.appSettings = appSettings;
    }

}
