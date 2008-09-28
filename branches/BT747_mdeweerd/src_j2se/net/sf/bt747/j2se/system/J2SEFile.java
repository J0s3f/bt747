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
package net.sf.bt747.j2se.system;

import java.io.RandomAccessFile;

import bt747.interfaces.BT747File;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class J2SEFile implements BT747File {

    private String path = null;

    public J2SEFile(final String path) {
        this.path = path;
    }

    private RandomAccessFile raf = null;

    public static final int DONT_OPEN = bt747.io.File.DONT_OPEN;
    public static final int READ_ONLY = bt747.io.File.READ_ONLY;
    public static final int WRITE_ONLY = bt747.io.File.WRITE_ONLY;
    public static final int READ_WRITE = bt747.io.File.READ_WRITE;
    public static final int CREATE = bt747.io.File.CREATE;

    public J2SEFile(final String path, final int mode, final int card) {
        this(path, mode);
    }

    public J2SEFile(final String path, final int mode) {
        try {
            String modeStr = "";
            this.path = path;
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
                    java.io.File tmp = new java.io.File(path);
                    tmp.createNewFile();
                } catch (Exception e) {
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
                if(mode==WRITE_ONLY) {
                    raf.seek(raf.length()); // To append 
                }
                System.out.println("Opened file " + path + " in mode " + mode
                        + " " + modeStr);
                isopen = true;
            }
        } catch (Exception e) {
            Generic.debug("Problem in file open " + path, e);
            this.path = null;
            lastError = -1;
        }
    }

    public int getSize() {
        try {
            if (raf == null) {
                return (int) (new java.io.File(path).length());
            } else {
                return (int) raf.length();
            }
        } catch (Exception e) {
            Generic.debug("getSize", e);
            return 0;
        }
    }

    public boolean exists() {
        try {
            return (new java.io.File(path).exists());
        } catch (Exception e) {
            Generic.debug("exists", e);
            return false;
        }
    }

    public boolean delete() {
        try {
            return new java.io.File(path).delete();
        } catch (Exception e) {
            Generic.debug("delete", e);
            return false;
        }
    }

    public boolean createDir() {
        try {
            return new java.io.File(path).mkdir();
        } catch (Exception e) {
            Generic.debug("createDir", e);
            return false;
        }
    }

    //public static char separatorChar = java.io.File.separatorChar;
    //public static String separatorStr = String.valueOf(separatorChar);

    boolean isopen = false;

    public boolean close() {
        try {
            if (isopen && raf != null) {
                isopen = false;
                raf.close();
            }
            return true;
        } catch (Exception e) {
            Generic.debug("close", e);
            e.printStackTrace();
            // TODO: handle exceptions
            return false;
        }
    }

    public boolean setPos(final int pos) {
        try {
            if (raf != null) {
                raf.seek((long) pos);
                return true;
            }
            return false;
        } catch (Exception e) {
            Generic.debug("setPos", e);
            // TODO: handle exceptions
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
        } catch (Exception e) {
            Generic.debug("writeBytes", e);
            // TODO: handle exceptions
            return 0;
        }
    }

    public int readBytes(final byte[] b, final int off, final int len) {
        try {
            return raf.read(b, off, len);
        } catch (Exception e) {
            Generic.debug("writeBytes", e);
            // TODO: handle exceptions
            return 0;
        }
    }

    public static String getCardVolumePath() {
        return null;
    }

    public String getPath() {
        return path;
    }
}
