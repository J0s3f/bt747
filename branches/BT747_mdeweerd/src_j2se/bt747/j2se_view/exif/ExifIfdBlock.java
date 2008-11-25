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

import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario
 * 
 */
public class ExifIfdBlock {

    private BT747Hashtable atrs = Interface.getHashtableInstance(10);
    private int nextIfdBlockOffset;

    public final int read(final byte[] buffer, final int initialIdxInBuffer,
            final int tiffHeaderStart, final boolean bigEndian) {
        ExifAttribute atr;
        int currentIdxInBuffer = initialIdxInBuffer;
        int interoperabilityNumber;
        int idx;

        interoperabilityNumber = ExifUtils.getShort2byte(buffer,
                currentIdxInBuffer, bigEndian);
        idx = interoperabilityNumber;
        currentIdxInBuffer += 2;
        while ((--idx >= 0) && ((currentIdxInBuffer + 12) <= buffer.length)) {
            int len;
            atr = new ExifAttribute();
            len = atr.read(buffer, currentIdxInBuffer,
                    tiffHeaderStart, bigEndian);
            if (len != 0) {
                atrs.put(atr.getTag(), atr);
            }
            currentIdxInBuffer += len;
            //bt747.sys.Generic.debug(atr.toString());
        }
        if (currentIdxInBuffer + 4 <= buffer.length) {
            nextIfdBlockOffset = ExifUtils.getLong4byte(buffer,
                    currentIdxInBuffer, bigEndian);
            return nextIfdBlockOffset;
        }

        return currentIdxInBuffer - initialIdxInBuffer;
    }

    public final boolean hasTag(final int tag) {
        return atrs.get(tag) != null;
    }

    public final ExifAttribute get(final int tag) {
        return (ExifAttribute) atrs.get(tag);
    }

    public final void set(final ExifAttribute atr) {
        atrs.put(atr.getTag(), atr);
    }


    /**
     * @return the nextIfdBlockOffset
     */
    public final int getNextIfdBlockOffset() {
        return this.nextIfdBlockOffset;
    }

    /**
     * @param nextIfdBlockOffset
     *            the nextIfdBlockOffset to set
     */
    public final void setNextIfdBlockOffset(final int nextIfdBlockOffset) {
        this.nextIfdBlockOffset = nextIfdBlockOffset;
    }
}
