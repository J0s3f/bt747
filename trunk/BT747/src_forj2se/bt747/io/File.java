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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class File {
    
//    public static final char separatorChar='/';
//    /**
//     * @param path
//     */
    String path=null;
    public File(String path) {
        this.path=path;
    }

    RandomAccessFile raf=null;
    
    public final static int DONT_OPEN = 0;
    public final static int READ_ONLY = 1;
    public final static int CREATE = 2;
    public final static int WRITE_ONLY = 3;
    public final static int READ_WRITE = 4;

    public File(String path, int mode, int card)
    throws FileNotFoundException {
        this(path, mode);
    }

    public File(String path, int mode)
    throws FileNotFoundException {
        String modeStr="";
        this.path=path;
        switch(mode) {
        case READ_ONLY:
            modeStr="r";
            break;
        case WRITE_ONLY:
            modeStr="rw";
            break;
        case CREATE:
            modeStr="rw";
            break;
        case READ_WRITE:
            modeStr="rw";
            break;
        default:
            modeStr="r";
        }
        if(mode!=DONT_OPEN) {
            raf=new RandomAccessFile(path, modeStr);
            isopen=true;
        }
    }
    
    public int getSize() throws IOException {
        if(raf==null) {
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
    

    public static final char separatorChar = java.io.File.separatorChar; 
    
    boolean isopen=false;
    public void close() throws IOException {
        if(isopen && raf!=null) {
            isopen=false;
            raf.close();
        }
    }
    
    public void setPos(int pos) throws IOException {
        if(raf!=null) {
            raf.seek((long)pos);
        }
    }
    
    public int lastError=0;

   
    public boolean isOpen() {
        if(raf!=null) {
            return isopen;
        } else {
            return false;
        }
    }

    public int writeBytes(byte[] b, int off, int len)
    throws IOException {
        raf.write(b, off, len);
        return len;
    }
    
    public int readBytes(byte[] b, int off, int len) throws IOException {
        return raf.read(b, off, len);
    }
    
    public static File getCardVolume() {
        return null;
    }
    
    public String getPath() {
        return path;
    }

    
//        super(path);
//        // TODO Auto-generated constructor stub
//    }
//
//    /**
//     * @param path
//     * @param mode
//     */
//    public File(String path, int mode) {
//        super(path, mode);
//        // TODO Auto-generated constructor stub
//    }
//
//    /**
//     * @param path
//     * @param mode
//     * @param slot
//     */
//    public File(String path, int mode, int slot) {
//        super(path, mode, slot);
//        // TODO Auto-generated constructor stub
//    }

}
