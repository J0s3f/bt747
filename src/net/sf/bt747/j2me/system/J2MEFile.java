/*
 * Created on 11 nov. 2007
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2me.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

import org.j4me.logging.Log;

import bt747.sys.interfaces.BT747RAFile;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class J2MEFile implements BT747RAFile {
    private String path = null;

    public J2MEFile(final String path) {
        this.path = path;
    }

    private Object fileObject;

    public J2MEFile(final String path, final int mode, final int card) {
        this(path, mode);
    }

    public J2MEFile(final String path, final int mode) {
        try {
            int lMode;
            this.path = path;
            switch (mode) {
            case bt747.sys.File.READ_ONLY:
                lMode = Connector.READ;
                break;
            case bt747.sys.File.WRITE_ONLY:
                lMode = Connector.READ_WRITE;
                break;
            case bt747.sys.File.CREATE:
                lMode = Connector.READ_WRITE;
                javax.microedition.io.file.FileConnection f = (javax.microedition.io.file.FileConnection) tmpFileConnection(path);
                f.create();
                f.close();
                f = null;
                break;
            case bt747.sys.File.READ_WRITE:
                lMode = Connector.READ_WRITE;
                break;
            default:
                lMode = Connector.READ;
            }
            if (mode != bt747.sys.File.DONT_OPEN) {
                final String urlPath = "file://" + path;
                Log.debug("Try to open " + path);
                final javax.microedition.io.file.FileConnection fileConnection = (javax.microedition.io.file.FileConnection) Connector
                        .open(urlPath, lMode);
                fileObject = fileConnection;
                isopen = true;
                switch (mode) {
                case bt747.sys.File.READ_ONLY:
                    is = fileConnection.openInputStream();
                    break;
                case bt747.sys.File.WRITE_ONLY:
                    os = fileConnection.openOutputStream(getSize());
                    break;
                case bt747.sys.File.CREATE:
                    os = fileConnection.openOutputStream(getSize());
                    break;
                case bt747.sys.File.READ_WRITE:
                    is = fileConnection.openInputStream();
                    os = fileConnection.openOutputStream(getSize());
                    break;
                default:
                }
            }
        } catch (final Exception e) {
            Log.error("new", e);
            lastError = -1;
        }
    }

    private Object tmpFileConnection(final String path) throws IOException {
        try {
            final String urlPath = "file://" + path;
            Log.debug("File: " + urlPath);
            return (Connector.open(urlPath));
        } catch (final Exception e) {
            Log.error("file connection", e);
            return null;
        }
    }

    public int getSize() {
        try {
            final javax.microedition.io.file.FileConnection fileConnection = (javax.microedition.io.file.FileConnection) fileObject;
            if (fileConnection == null) {
                return (int) ((javax.microedition.io.file.FileConnection) tmpFileConnection(path))
                        .fileSize();
            } else {
                return (int) fileConnection.fileSize();
            }
        } catch (final Exception e) {
            Log.error("getSize", e);
            lastError = -1;
            return 0;
        }
    }

    public boolean exists() {
        try {
            return ((javax.microedition.io.file.FileConnection) tmpFileConnection(path))
                    .exists();
        } catch (final Exception e) {
            Log.error("exists", e);
            lastError = -1;
            return false;
        }
    }

    public boolean delete() {
        try {
            ((javax.microedition.io.file.FileConnection) tmpFileConnection(path))
                    .delete();
            return true;
        } catch (final Exception e) {
            Log.error("delete", e);
            lastError = -1;
            return false;
        }
    }

    public boolean createDir() {
        try {
            ((javax.microedition.io.file.FileConnection) tmpFileConnection(path))
                    .mkdir();
            return true;
        } catch (final Exception e) {
            Log.error("createDir", e);
            lastError = -1;
            return false;
        }
    }

    public static boolean isAvailable() {
        return true; // File system is available
    }

    public static boolean isCardInserted(final int i) {
        return false;
    }

    public static char separatorChar = '/';
    public static String separatorStr = "/";

    private boolean isopen = false;

    public boolean close() {
        try {
            javax.microedition.io.file.FileConnection fileConnection = (javax.microedition.io.file.FileConnection) fileObject;
            if (isopen && (fileConnection != null)) {
                isopen = false;
                try {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                } catch (final Exception e) {
                    Log.error("inputstream close", e);
                    // TODO: handle exceptions
                    return false;
                }
                try {
                    if (os != null) {
                        os.close();
                        os = null;
                    }
                } catch (final Exception e) {
                    Log.error("outputstream close", e);
                    // TODO: handle exceptions
                    return false;
                }
                fileConnection.close();
                fileConnection = null;
            }
            return true;
        } catch (final Exception e) {
            Log.error("File close", e);
            return false;
        }
    }

    private InputStream is;
    private OutputStream os;

    public boolean setPos(final int pos) {
        try {
            final javax.microedition.io.file.FileConnection fileConnection = (javax.microedition.io.file.FileConnection) fileObject;
            if (fileConnection != null) {
                if (is != null) {
                    is.close();
                    is = fileConnection.openInputStream();
                    is.skip(pos);
                }
                if (os != null) {
                    os.close();
                    os = fileConnection.openOutputStream(pos);
                }
                return true;
            }
            return false;
        } catch (final Exception e) {
            Log.error("setPos", e);
            return false;
        }
    }

    public int lastError = 0;

    public boolean isOpen() {
        if (fileObject != null) {
            return isopen;
        } else {
            return false;
        }
    }

    public int writeBytes(final byte[] b, final int off, final int len) {
        try {
            if (os != null) {
                os.write(b, off, len);
                return len;
            } else {
                Log.error("Write stream is closed");
                return 0;
            }
        } catch (final Exception e) {
            Log.error("writeBytes", e);
            lastError = -1;
            return 0;
        }
    }

    public int readBytes(final byte[] b, final int off, final int len) {
        try {
            if (is != null) {
                return is.read(b, off, len);
            } else {
                Log.error("Read stream is closed");
                return 0;
            }
        } catch (final Exception e) {
            Log.error("readBytes", e);
            lastError = -1;
            return 0;
        }

    }

    public String getPath() {
        return path;
    }

    public int getLastError() {
        return lastError;
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747File#getModificationTime()
     */
    public int getModificationTime() {
        try {
            return (int) (((javax.microedition.io.file.FileConnection) tmpFileConnection(path))
                    .lastModified() / 1000L);
        } catch (final Exception e) {
            return 0;
        }
    }
}
