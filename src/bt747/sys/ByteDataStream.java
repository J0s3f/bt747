/**
 * 
 */
package bt747.sys;

import bt747.sys.interfaces.BT747InputStream;

public final class ByteDataStream implements BT747InputStream {
    final private byte[] data;
    private int pos;

    public ByteDataStream(final byte[] data) {
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747InputStream#readBytes(byte[], int,
     *      int)
     */
    public final int readBytes(final byte[] b, final int off,
            final int len) {
        if (pos < data.length) {
            int cnt = 0;
            for (int i = off; (pos < data.length) && (i < off + len); i++, pos++) {
                b[i] = data[pos];
                cnt++;
            }
            return cnt;
        } else {
            return -1;
        }
    }

}

