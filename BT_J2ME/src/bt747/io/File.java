/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

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

    FileConnection fileConnection;

    // FileSystemRegistry.listRoots();

    public static final int DONT_OPEN = 0;
    public static final int READ_ONLY = 1;
    public static final int WRITE_ONLY = 2;
    public static final int READ_WRITE = 3; // READ | WRITE
    public static final int CREATE = 4;

    public File(String path, int mode, int card) throws IOException {
        this(path, mode);
    }

    public File(String path, int mode) throws IOException {
        int lMode;
        this.path = path;
        switch (mode) {
        case READ_ONLY:
            lMode = Connector.READ;
            break;
        case WRITE_ONLY:
            lMode = Connector.WRITE;
            break;
        case CREATE:
            lMode = Connector.READ_WRITE;
            tmpFileConnection(path).create();
            break;
        case READ_WRITE:
            lMode = Connector.READ_WRITE;
            break;
        default:
            lMode = Connector.READ;
        }
        if (mode != DONT_OPEN) {
            fileConnection = (FileConnection) Connector.open("file://" + path,
                    lMode);
            System.out.println("Opened file " + path);
            isopen = true;
            switch (mode) {
            case READ_ONLY:
                is = fileConnection.openInputStream();
                break;
            case WRITE_ONLY:
                os = fileConnection.openOutputStream();
                break;
            case CREATE:
                is = fileConnection.openInputStream();
                break;
            case READ_WRITE:
                is = fileConnection.openInputStream();
                os = fileConnection.openOutputStream();
                break;
            default:
            }
        }
    }

    private FileConnection tmpFileConnection(final String path)
            throws IOException {
        return ((FileConnection) Connector.open("file://" + path));
    }

    public int getSize() throws IOException {
        if (fileConnection == null) {
            return (int) tmpFileConnection(path).fileSize();
        } else {
            return (int) fileConnection.fileSize();
        }
    }

    public boolean exists() throws IOException {
        return tmpFileConnection(path).exists();
    }

    public boolean delete() throws IOException {
        tmpFileConnection(path).delete();
        return true;
    }

    public boolean createDir() throws IOException {
        tmpFileConnection(path).mkdir();
        return true;
    }

    public static boolean isAvailable() {
        return true; // File system is available
    }

    public static boolean isCardInserted(int i) {
        return false;
    }

    public static char separatorChar = '/';
    public static String separatorStr = "/";

    boolean isopen = false;

    public boolean close() throws IOException {
        try {
            if (isopen && fileConnection != null) {
                isopen = false;
                fileConnection.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exceptions
            return false;
        }
    }

    private InputStream is;
    private OutputStream os;

    public boolean setPos(int pos) throws IOException {
        if (fileConnection != null) {
            is.reset();
            is.skip(pos);
            // TODO: set outputstream offset ?
            // fileConnection.
            return true;
        }
        return false;
    }

    public int lastError = 0;

    public boolean isOpen() {
        if (fileConnection != null) {
            return isopen;
        } else {
            return false;
        }
    }

    public int writeBytes(byte[] b, int off, int len) throws IOException {
        os.write(b, off, len);
        return len;
    }

    public int readBytes(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
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
