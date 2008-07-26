/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class File {

    // public static final char separatorChar='/';
    // /**
    // * @param path
    // */
    String path = null;

    public File(String path) {
        this.path = path;
    }

    RandomAccessFile raf = null;

    public static final int DONT_OPEN = 0;
    public static final int READ_ONLY  = 1;
    public static final int WRITE_ONLY = 2;
    public static final int READ_WRITE = 3; // READ | WRITE
    public static final int CREATE = 4;

    public File(String path, int mode, int card) throws FileNotFoundException {
        this(path, mode);
    }

    public File(String path, int mode) throws FileNotFoundException {
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
    }

    public int getSize() throws IOException {
        if (raf == null) {
            return (int) (new java.io.File(path).length());
        } else {
            return (int) raf.length();
        }
    }

    public boolean exists() throws IOException {
        return (new java.io.File(path).exists());
    }

    public boolean delete() throws IOException {
        return new java.io.File(path).delete();
    }

    public boolean createDir() throws IOException {
        return new java.io.File(path).mkdir();
    }

    public static boolean isAvailable() {
        return true; // File system is available
    }

    public static boolean isCardInserted(int i) {
        return false;
    }

    public static char separatorChar = java.io.File.separatorChar;
    public static String separatorStr = String.valueOf(separatorChar);


    boolean isopen = false;

    public boolean close() throws IOException {
        try {
            if (isopen && raf != null) {
                isopen = false;
                raf.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exceptions
            return false;
        }
    }

    public boolean setPos(int pos) throws IOException {
        if (raf != null) {
            raf.seek((long) pos);
            return true;
        }
        return false;
    }

    public int lastError = 0;

    public boolean isOpen() {
        if (raf != null) {
            return isopen;
        } else {
            return false;
        }
    }

    public int writeBytes(byte[] b, int off, int len) throws IOException {
        raf.write(b, off, len);
        return len;
    }

    public int readBytes(byte[] b, int off, int len) throws IOException {
        return raf.read(b, off, len);
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
