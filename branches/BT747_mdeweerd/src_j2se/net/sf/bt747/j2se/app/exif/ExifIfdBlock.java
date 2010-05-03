// ********************************************************************
// *** BT747 ***
// *** (c)2007-2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package net.sf.bt747.j2se.app.exif;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario
 * 
 */
public class ExifIfdBlock {

    private BT747Hashtable atrs = JavaLibBridge.getHashtableInstance(10);
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
            len = atr.read(buffer, currentIdxInBuffer, tiffHeaderStart,
                    bigEndian);
            if (len != 0) {
                atrs.put(atr.getTag(), atr);
            }
            currentIdxInBuffer += len;
            // bt747.sys.Generic.debug(atr.toString());
        }
        if (currentIdxInBuffer + 4 <= buffer.length) {
            nextIfdBlockOffset = ExifUtils.getLong4byte(buffer,
                    currentIdxInBuffer, bigEndian);
            return nextIfdBlockOffset;
        }

        return currentIdxInBuffer - initialIdxInBuffer;
    }

    public final int getByteSize() {
        int size;
        // Interoperability = 2 bytes
        size = 2;
        // Each record individually = 12 bytes + payload
        final BT747Hashtable iter = atrs.iterator();
        while (iter.hasNext()) {
            size += 12;
            final ExifAttribute atr = (ExifAttribute) atrs
                    .get(iter.nextKey());
            size += atr.getPayloadSize();
        }

        size += 4; // NextIfdBlockOffset
        return size;
    }

    public final void fillBuffer(final byte[] buffer,
            final int tiffHeaderStart, final boolean bigEndian,
            final int offset, final int nextIfdOffset) {
        int payloadOffset;
        // Payload start is
        // Current position in buffer
        payloadOffset = offset;
        // .. plus bytes needed for interoperabilitynumber
        payloadOffset += 2;
        // .. plus bytes needed for records
        payloadOffset += 12 * atrs.size();
        // .. plus bytes needed for nextIfdOffset
        payloadOffset += 4;

        // Interoperability number (= count)
        // System.err.println(this.toString())
        ExifUtils.addShort2byte(buffer, offset, bigEndian, atrs.size());
        int recordOffset = offset + 2;
        // TODO - needs to be in sorted order.
        final BT747Hashtable iter = atrs.iterator();
        // Very simple sort.
        final int[] sortedKeys = new int[atrs.size()];
        int idx = 0;
        while (iter.hasNext()) {
            final int key = ((ExifAttribute) (atrs.get(iter.nextKey())))
                    .getTag();
            int i;
            for (i = idx - 1; i >= 0; i--) {
                if (sortedKeys[i] > key) {
                    sortedKeys[i + 1] = sortedKeys[i];
                } else {
                    break;
                }
            }
            sortedKeys[i + 1] = key;
            idx++;
        }
        for (int i = 0; i < sortedKeys.length; i++) {
            final int key = sortedKeys[i];
            final ExifAttribute atr = (ExifAttribute) (atrs.get(key));
            payloadOffset += atr.fillBuffer(buffer, recordOffset, bigEndian,
                    payloadOffset, tiffHeaderStart);
            recordOffset += 12;
        }
        ExifUtils
                .addLong4byte(buffer, recordOffset, bigEndian, nextIfdOffset);
    }

    public final boolean hasTag(final int tag) {
        return atrs.get(tag) != null;
    }

    public final ExifAttribute get(final int tag) {
        return (ExifAttribute) atrs.get(tag);
    }
    
    public final BT747Hashtable getAttrIterator() {
    	return atrs.iterator();
    }

    public final void set(final ExifAttribute atr) {
        atrs.put(atr.getTag(), atr);
    }

    /**
     * @return the nextIfdBlockOffset
     */
    public final int getNextIfdBlockOffset() {
        return nextIfdBlockOffset;
    }

    /**
     * @param nextIfdBlockOffset
     *                the nextIfdBlockOffset to set
     */
    public final void setNextIfdBlockOffset(final int nextIfdBlockOffset) {
        this.nextIfdBlockOffset = nextIfdBlockOffset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer r = new StringBuffer();
        final BT747Hashtable iter = atrs.iterator();
        while (iter.hasNext()) {
            r.append(atrs.get(iter.nextKey()).toString());
            r.append('\n');
        }
        // TODO Auto-generated method stub
        return r.toString();
    }
}
