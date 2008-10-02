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

import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747File;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747Thread;
import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.BT747Vector;
import bt747.sys.interfaces.JavaTranslationsInterface;

public final class JavaTranslations implements JavaTranslationsInterface {
    public BT747Date getDateInstance() {
        return new WabaDate();
    }

    public BT747Date getDateInstance(final int d, final int m, final int y) {
        return new WabaDate(d, m, y);
    }

    public BT747Date getDateInstance(final String strDate,
            final byte dateFormat) {
        return new WabaDate(strDate, dateFormat);
    }

    public BT747Hashtable getHashtableInstance(final int initialCapacity) {
        return new WabaHashtable(initialCapacity);
    }

    public BT747Vector getVectorInstance() {
        return new WabaVector();
    }

    public BT747Time getTimeInstance() {
        return new WabaTime();
    }

    public BT747File getFileInstance(final String path) {
        return new WabaFile(path);
    }

    public BT747File getFileInstance(final String path, final int mode, final int card) {
        int localMode;
        if (mode == bt747.sys.File.WRITE_ONLY) {
            // On SuperWaba, WRITE_ONLY might erase, so transforming in READ_WRITE.
            // Must be in append mode too.
            localMode = waba.io.File.READ_WRITE;
        } else {
            localMode = mode;
        }
        return new WabaFile(path, localMode, card);
    }

    public BT747File getFileInstance(final String path, final int mode) {
        int localMode;
        if (mode == bt747.sys.File.WRITE_ONLY) {
            // On SuperWaba, WRITE_ONLY might erase, so transforming in READ_WRITE.
            // Must be in append mode too.
            localMode = waba.io.File.READ_WRITE;
        } else {
            localMode = mode;
        }
        return new WabaFile(path, localMode);
    }


    public boolean isAvailable() {
        return waba.io.File.isAvailable();
    }

    public void debug(final String s, final Throwable e) {
        WabaGeneric.debug(s, e);
    }

    public double pow(final double x, final double y) {
        return Math.pow(x, y);
    }

    public double acos(final double x) {
        return Math.acos(x);
    }

    public void addThread(final BT747Thread t, final boolean b) {
        WabaGeneric.addThread(t, b);
    }

    public void removeThread(final BT747Thread t) {
        WabaGeneric.removeThread(t);
    }

    /**
     * Math
     * 
     */
    public String toString(final boolean p) {
        return waba.sys.Convert.toString(p);
    }

    public String toString(final int p) {
        return waba.sys.Convert.toString(p);
    }

    public String toString(final float p) {
        return waba.sys.Convert.toString(p);
    }

    public String toString(final double p) {
        return waba.sys.Convert.toString(p);
    }

    public String toString(final double p, final int i) {
        return waba.sys.Convert.toString(p, i);
    }

    public String unsigned2hex(final int p, final int i) {
        return waba.sys.Convert.unsigned2hex(p, i);
    }

    public int toInt(final String s) {
        return waba.sys.Convert.toInt(s);
    }

    public float toFloat(final String s) {
        return waba.sys.Convert.toFloat(s);
    }

    public double toDouble(final String s) {
        return waba.sys.Convert.toDouble(s);
    }

    public double longBitsToDouble(final long l) {
        return waba.sys.Convert.longBitsToDouble(l);
    }

    public float toFloatBitwise(final int l) {
        return waba.sys.Convert.toFloatBitwise(l);
    }

    public int toIntBitwise(final float f) {
        return waba.sys.Convert.toIntBitwise(f);
    }

    public void debug(final String s) {
        // TODO if (Log.isDebugEnabled()) {
        waba.sys.Vm.debug(s);
    }

    public int getTimeStamp() {
        return waba.sys.Vm.getTimeStamp();
    }

    /**
     * @return the appSettings
     */
    public String getAppSettings() {
        return waba.sys.Settings.appSettings;
    }

    /**
     * @param appSettings
     *            the appSettings to set
     */
    public void setAppSettings(final String appSettings) {
        waba.sys.Settings.appSettings = appSettings;
    }

    public BT747Semaphore getSemaphoreInstance(final int value) {
        return new WabaSemaphore(value);
    }

    // Open resource
    // Vm.getFile
}
