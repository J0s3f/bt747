/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.net.bt747.waba.system;

import bt747.interfaces.BT747File;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class WabaFile extends waba.io.File implements BT747File {
    public static char separatorChar = '/';
    public static String separatorStr = "/";

    /**
     * @param path
     */
    public WabaFile(String path) {
        super(path);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param path
     * @param mode
     */
    public WabaFile(String path, int mode) {
        super(path, mode);
        if (mode == waba.io.File.READ_WRITE || mode == waba.io.File.WRITE_ONLY) {
            setPos(getSize()); // Default is append
        }
    }

    /**
     * @param path
     * @param mode
     * @param slot
     */
    public WabaFile(String path, int mode, int slot) {
        super(path, mode, slot);
        if (mode == waba.io.File.READ_WRITE || mode == waba.io.File.WRITE_ONLY) {
            setPos(getSize()); // Default is append
        }
    }

    public static String getCardVolumePath() {
        if (getCardVolume() != null) {
            return getCardVolume().getPath();
        } else {
            return null;
        }
    }

    public int getLastError() {
        return lastError;
    }

}
