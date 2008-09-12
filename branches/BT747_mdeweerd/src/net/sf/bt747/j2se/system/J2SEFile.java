/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import bt747.interfaces.BT747File;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class J2SEFile implements BT747File {

    // public static final char separatorChar='/';
    // /**
    // * @param path
    // */
    String path = null;

    public J2SEFile(String path) {
        this.path = path;
    }

    RandomAccessFile raf = null;

    public static final int DONT_OPEN = 0;
    public static final int READ_ONLY = 1;
    public static final int WRITE_ONLY = 2;
    public static final int READ_WRITE = 3; // READ | WRITE
    public static final int CREATE = 4;

    public J2SEFile(String path, int mode, int card) {
        this(path, mode);
    }

    public J2SEFile(String path, int mode) {
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
                    // TODO: handle exception
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
                System.out.println("Opened file " + path);
                isopen = true;
            }
        } catch (Exception e) {
            path = null;
            lastError = -1;
        }
    }

    public final int getSize() {
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

    public final boolean exists() {
        try {
            return (new java.io.File(path).exists());
        } catch (Exception e) {
            Generic.debug("exists", e);
            return false;
        }
    }

    public final boolean delete() {
        try {
            return new java.io.File(path).delete();
        } catch (Exception e) {
            Generic.debug("delete", e);
            return false;
        }
    }

    public final boolean createDir() {
        try {
            return new java.io.File(path).mkdir();
        } catch (Exception e) {
            Generic.debug("createDir", e);
            return false;
        }
    }

    public static char separatorChar = java.io.File.separatorChar;
    public static String separatorStr = String.valueOf(separatorChar);

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

    public boolean setPos(int pos) {
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

    public final int getLastError() {
        return lastError;
    }

    public final boolean isOpen() {
        if (raf != null) {
            return isopen;
        } else {
            return false;
        }
    }

    public final int writeBytes(byte[] b, int off, int len) {
        try {
            raf.write(b, off, len);
            return len;
        } catch (Exception e) {
            Generic.debug("writeBytes", e);
            // TODO: handle exceptions
            return 0;
        }
    }

    public final int readBytes(byte[] b, int off, int len) {
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

    // super(path);
    // // TODO Auto-generated constructor stub
    // }
    //
    // /**
    // * @param path
    // * @param mode
    // */
    // public File(String path, int mode) {
    // super(path, mode);
    // // TODO Auto-generated constructor stub
    // }
    //
    // /**
    // * @param path
    // * @param mode
    // * @param slot
    // */
    // public File(String path, int mode, int slot) {
    // super(path, mode, slot);
    // // TODO Auto-generated constructor stub
    // }
}
