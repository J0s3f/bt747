/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.io;

import bt747.interfaces.BT747File;
import bt747.interfaces.Interface;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class File {
    public static final int DONT_OPEN = 0;
    public static final int READ_ONLY = 1;
    public static final int WRITE_ONLY = 2;
    public static final int READ_WRITE = 3; // READ | WRITE
    public static final int CREATE = 4;

    protected BT747File file;

    protected File() {
    }
    
    public File(String path) {
        file = Interface.tr.getFileInstance(path);
    }

    public File(String path, int mode, int card) {
        file = Interface.tr.getFileInstance(path,mode,card);
    }

    public File(String path, int mode)  {
        file = Interface.tr.getFileInstance(path,mode);
    }

    public final int getSize()  {
            return file.getSize();
    }

    public final boolean exists()  {
        return file.exists();
    }

    public final boolean delete()  {
        return file.delete();
    }

    public final boolean createDir()  {
        return file.createDir();
    }

    public static boolean isAvailable() {
        return Interface.tr.isAvailable();
    }
    
//    public static boolean isCardInserted(int i) {
//        return false;
//    }

    public static char separatorChar = '/';
    public static String separatorStr = "/";

    boolean isopen = false;

    public final boolean close()  {
        return file.close();
    }

    public final boolean setPos(int pos)  {
        return file.setPos(pos);
    }

    public final boolean isOpen() {
        return file.isOpen();
    }

    public final int writeBytes(byte[] b, int off, int len)  {
        return file.writeBytes(b, off, len);
    }

    public final int readBytes(byte[] b, int off, int len)  {
        return file.readBytes(b, off, len);
    }

//    public static final String getCardVolumePath() {
//        return null;
//    }

    public final String getPath() {
        return file.getPath();
    }

    /**
     * @return the lastError
     */
    public final int getLastError() {
        return file.getLastError();
    }
}
