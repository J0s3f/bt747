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
package org.bt747.android.system;

import java.io.RandomAccessFile;

import bt747.sys.Generic;
import bt747.sys.interfaces.BT747RAFile;

/**
 * Implement the system interface of BT747File.
 * 
 * @author Mario De Weerd
 */
public final class AndroidRAFile implements BT747RAFile {

    /**
     * Path corresponding to current file. The current file is not necessarily
     * open and the path must be memorized. The path does not necessarily
     * correspond to the system path either, yet the path returned by the
     * implementation must be the app's view of the path.
     */
    private String filePath = null;

    /**
     * When open, pointer to the system file.
     */
    private RandomAccessFile raf = null;

    /**
     * Initializer called by the Interface.
     * 
     * @param path
     *                The path to the file to operate on.
     */
    public AndroidRAFile(final String path) {
        filePath = path;
    }

    /**
     * Initializer called by the Interface.
     * 
     * @param path
     *                The path to the file to operate on.
     * @param mode
     *                The way the file should be opened.
     */
    public AndroidRAFile(final String path, final int mode) {
        try {
            String modeStr = "";
            filePath = path;
            switch (mode) {
            case READ_ONLY:
                modeStr = "r";
                break;
            case WRITE_ONLY:
                modeStr = "rw";
                break;
            case CREATE:
                modeStr = "rw";
                try {
                    final java.io.File tmp = new java.io.File(path);
                    tmp.createNewFile();
                } catch (final Exception e) {
                    Generic.debug("File creation failed:" + path, e);
                }
                break;
            case READ_WRITE:
                modeStr = "rw";
                break;
            default:
                modeStr = "r";
            }
            if (mode != DONT_OPEN) {
                raf = new RandomAccessFile(path, modeStr);
                if (mode == WRITE_ONLY) {
                    raf.seek(raf.length()); // To append
                }
                if (Generic.isDebug()) {
                    Generic.debug("Opened file " + path + " in mode " + mode
                            + " " + modeStr, null);
                }
                isopen = true;
            }
        } catch (final Exception e) {
            Generic.debug("Problem in file open " + path, e);
            filePath = null;
            lastError = -1;
        }
    }

    private static final int DONT_OPEN = bt747.sys.File.DONT_OPEN;
    private static final int READ_ONLY = bt747.sys.File.READ_ONLY;
    private static final int WRITE_ONLY = bt747.sys.File.WRITE_ONLY;
    private static final int READ_WRITE = bt747.sys.File.READ_WRITE;
    private static final int CREATE = bt747.sys.File.CREATE;

    public int getSize() {
        try {
            if (raf == null) {
                return (int) (new java.io.File(filePath).length());
            } else {
                return (int) raf.length();
            }
        } catch (final Exception e) {
            Generic.debug("getSize", e);
            return 0;
        }
    }

    public boolean exists() {
        try {
            return (new java.io.File(filePath).exists());
        } catch (final Exception e) {
            Generic.debug("exists", e);
            return false;
        }
    }

    public boolean delete() {
        try {
            return new java.io.File(filePath).delete();
        } catch (final Exception e) {
            Generic.debug("delete", e);
            return false;
        }
    }

    public boolean createDir() {
        try {
            return new java.io.File(filePath).mkdir();
        } catch (final Exception e) {
            Generic.debug("createDir", e);
            return false;
        }
    }

    private boolean isopen = false;

    public boolean close() {
        try {
            if (isopen && (raf != null)) {
                isopen = false;
                raf.close();
            }
            return true;
        } catch (final Exception e) {
            Generic.debug("close", e);
            e.printStackTrace();
            // TODO: handle exceptions
            return false;
        }
    }

    public boolean setPos(final int pos) {
        try {
            if (raf != null) {
                raf.seek(pos);
                return true;
            }
            return false;
        } catch (final Exception e) {
            Generic.debug("setPos", e);
            return false;
        }
    }

    private int lastError = 0;

    public int getLastError() {
        return lastError;
    }

    public boolean isOpen() {
        if (raf != null) {
            return isopen;
        } else {
            return false;
        }
    }

    public int writeBytes(final byte[] b, final int off, final int len) {
        try {
            raf.write(b, off, len);
            return len;
        } catch (final Exception e) {
            Generic.debug("writeBytes", e);
            // TODO: handle exceptions
            return 0;
        }
    }

    public int readBytes(final byte[] b, final int off, final int len) {
        try {
            return raf.read(b, off, len);
        } catch (final Exception e) {
            Generic.debug("writeBytes", e);
            // TODO: handle exceptions
            return 0;
        }
    }

    public static String getCardVolumePath() {
        return null;
    }

    public String getPath() {
        return filePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747File#getModificationTime()
     */
    public int getModificationTime() {
        return (int) (new java.io.File(filePath).lastModified() / 1000L);
    }

    // Kept for reference - unused.
    // public static char separatorChar = java.io.File.separatorChar;
    // public static String separatorStr = String.valueOf(separatorChar);
}
