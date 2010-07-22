// ********************************************************************
// *** BT 747 ***
// *** (c)2007-2009 Mario De Weerd ***
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
package bt747.sys.interfaces;

/**
 * Defines the interface needed for the system translation functions.
 * 
 * @author Mario De Weerd
 * 
 */
public interface BT747File extends BT747InputStream, BT747OutputStream {

    /**
     * Get the current file size.
     * 
     * @return File size.
     */
    int getSize();

    /**
     * Determine if the file exists.
     * 
     * @return true when the file exists.
     */
    boolean exists();

    /**
     * Deletes the file
     * 
     * @return true if success.
     */
    boolean delete();

    /**
     * Creates a directory.
     * 
     * @return true if success.
     */
    boolean createDir();

    /**
     * Close the file.
     * 
     * @return true if success.
     */
    boolean close();

    /**
     * Determine if the file handle is open.
     * 
     * @return true when open.
     */
    boolean isOpen();

    /**
     * Get the file's path.
     * 
     * @return File path as a string.
     */
    String getPath();

    /**
     * Error number of the last error that occurred.
     * 
     * @return The last error type.
     */
    int getLastError();

    /**
     * Get modification time (UTC)
     */
    int getModificationTime();
    
    /**
     * Set modification time (UTC).
     * - may not work on all platforms.
     */
    void setModificationTime(int utc);
}
