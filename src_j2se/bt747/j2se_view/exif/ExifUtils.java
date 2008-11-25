//********************************************************************
//***                           BT747                              ***
//***                 (c)2007-2008 Mario De Weerd                  ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***                                                              ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package bt747.j2se_view.exif;

/**
 * @author Mario De Weerd
 *
 */
public final class ExifUtils {

    public static int getByte(final byte[] buffer, final int offset,
            final boolean bigEndian) {
        return (buffer[offset] & 0xFF);
    }

    public static int getLong4byte(final byte[] buffer, final int offset,
            final boolean bigEndian) {
        int i;
        if (bigEndian) {
            i = (buffer[offset] & 0xFF) << 24
                    | (buffer[offset + 1] & 0xFF) << 16
                    | (buffer[offset + 1] & 0xFF) << 8
                    | (buffer[offset + 3] & 0xFF);
        } else {
            i = (buffer[offset + 3] & 0xFF) << 24
                    | (buffer[offset + 2] & 0xFF) << 16
                    | (buffer[offset + 1] & 0xFF) << 8
                    | (buffer[offset] & 0xFF);
        }
        return i;
    }

    public static int getShort2byte(final byte[] buffer, final int offset,
            final boolean bigEndian) {
        if (bigEndian) {
            return ((buffer[offset] & 0xFF) << 8) | (buffer[offset + 1] & 0xFF);
        } else {
            return ((buffer[offset + 1] & 0xFF) << 8) | (buffer[offset] & 0xFF);
        }
    }

}
