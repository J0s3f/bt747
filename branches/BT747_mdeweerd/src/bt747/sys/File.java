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
package bt747.sys;

import bt747.sys.interfaces.BT747File;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario De Weerd
 */
public class File {
    /**
     * Mode: Do not open the file.
     */
    public static final int DONT_OPEN = 0;
    /**
     * Mode: Opens the file as read only.
     */
    public static final int READ_ONLY = 1;
    /**
     * Mode: Opent the file as write only, append.
     */
    public static final int WRITE_ONLY = 2;
    /**
     * Mode: Open the file as read+write - avoid.
     */
    public static final int READ_WRITE = 3; // READ | WRITE

    /**
     * Create the file and open as write.
     */
    public static final int CREATE = 4;

    final BT747File file;
    
    protected final BT747File getFile() {
        return file;
    }

    protected File(final BT747File file) {
        this.file = file;
    }
    
    public File(final BT747Path path) {
        file = JavaLibBridge.getFileInstance(path);
    }

    public File(final BT747Path path, final int mode) {
        file = JavaLibBridge.getFileInstance(path, mode);
    }

    public final int getSize() {
        return file.getSize();
    }

    public final boolean exists() {
        return file.exists();
    }

    public final boolean delete() {
        return file.delete();
    }

    public final boolean createDir() {
        return file.createDir();
    }

    public static final boolean isAvailable() {
        return JavaLibBridge.isAvailable();
    }

    public static final char separatorChar = '/';
    public static final String separatorStr = "/";

    public final boolean close() {
        return file.close();
    }

    public final boolean isOpen() {
        return file.isOpen();
    }

    public final int writeBytes(final byte[] b, final int off, final int len) {
        return file.writeBytes(b, off, len);
    }

    public final int readBytes(final byte[] b, final int off, final int len) {
        return file.readBytes(b, off, len);
    }

    public final String getPath() {
        return file.getPath();
    }

    /**
     * @return the lastError
     */
    public final int getLastError() {
        return file.getLastError();
    }

    public final int getModificationTime() {
        return file.getModificationTime();
    }
    
    public final void setModificationTime(int utc) {
    	file.setModificationTime(utc);
    }
}
