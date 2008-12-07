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
package bt747.sys.interfaces;

/**
 * Defines the interface needed for the system translation functions.
 * 
 * @author Mario De Weerd
 * 
 */
public interface BT747File {

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
     * Set the file position.
     * 
     * @param pos
     * @return true if success.
     */
    boolean setPos(int pos);

    /**
     * Determine if the file handle is open.
     * 
     * @return true when open.
     */
    boolean isOpen();

    /**
     * Write bytes from byte buffer to file.
     * 
     * @param b
     *            Byte buffer.
     * @param off
     *            Start offset in buffer to start writing to file.
     * @param len
     *            Number of bytes to write to file.
     * @return number of bytes written.
     */
    int writeBytes(final byte[] b, final int off, final int len);

    /**
     * Read bytes from file to buffer.
     * 
     * @param b
     *            Byte buffer.
     * @param off
     *            Start offset in buffer to start reading from file.
     * @param len
     *            Number of bytes to read from file.
     * @return number of bytes read.
     */
    int readBytes(final byte[] b, final int off, final int len);

    /**
     * Get the file's path.
     * 
     * @return File path as a string.
     */
    String getPath();

    /**
     * Error number of the last error that occurred.
     * 
     * @return
     */
    int getLastError();
    
    /**
     * Get modification time (UTC)
     */
    int getModificationTime();
}
