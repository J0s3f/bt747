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
package bt747.sys;

import bt747.sys.interfaces.BT747File;

/**
 * @author Mario De Weerd
 */
public final class File {
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

    protected BT747File file;

    protected File() {
    }

    public File(final String path) {
        file = Interface.tr.getFileInstance(path);
    }

    public File(final String path, final int mode, final int card) {
        file = Interface.tr.getFileInstance(path, mode, card);
    }

    public File(final String path, final int mode) {
        file = Interface.tr.getFileInstance(path, mode);
    }

    public int getSize() {
        return file.getSize();
    }

    public boolean exists() {
        return file.exists();
    }

    public boolean delete() {
        return file.delete();
    }

    public boolean createDir() {
        return file.createDir();
    }

    public static boolean isAvailable() {
        return Interface.tr.isAvailable();
    }

    public static final char separatorChar = '/';
    public static final String separatorStr = "/";

    public boolean close() {
        return file.close();
    }

    public boolean setPos(final int pos) {
        return file.setPos(pos);
    }

    public boolean isOpen() {
        return file.isOpen();
    }

    public int writeBytes(final byte[] b, final int off, final int len) {
        return file.writeBytes(b, off, len);
    }

    public int readBytes(final byte[] b, final int off, final int len) {
        return file.readBytes(b, off, len);
    }

    public String getPath() {
        return file.getPath();
    }

    /**
     * @return the lastError
     */
    public int getLastError() {
        return file.getLastError();
    }
}
