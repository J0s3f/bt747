//********************************************************************
//***                            BT747                             ***
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

import gps.log.in.WindowedFile;

import bt747.sys.File;
import bt747.sys.Generic;

/**
 * @author Mario De Weerd
 * 
 * Reading and writing the relevant EXIF data should not be too complicated.
 * 
 * Strategy is to read the header and the EXIF data. Reading first 64kB + SOI
 * size bytes is sufficient.
 * 
 * EXIF data must be stored in a manner that allows reconstruction later.
 * 
 * Writing Exif data consists in replacing the APP1 Block and updating the SOI
 * Marker.
 * 
 * Specification at http://www.exif.org/Exif2-2.PDF
 */
public class ExifJPG {

    private String Path; // Path to the file
    private int card = -1; // Card number on Palm

    public final boolean setPath(final String p) {
        Path = p;
        return getInfo();
    }

    public final boolean setPath(final String p, final int c) {
        card = c;
        Path = p;
        return getInfo();
    }

    private boolean getInfo() {
        WindowedFile p;
        boolean success = false;

        try {
            // bt747.sys.Generic.debug(Path);
            p = new WindowedFile(Path, File.READ_ONLY, card);
            if (p != null && p.isOpen()) {
                byte[] buffer;
                int sz;
                p.setBufferSize(64 * 1024 + 10);
                p.fillBuffer(0);
                buffer = p.getBuffer();
                sz = p.getBufferFill();
                examineBuffer(buffer, sz);
                success = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            Generic.debug("EXIFJpg", e);
        }
        return success;
    }

    private ExifApp1 exifApp1;

    private final void examineBuffer(final byte[] buffer, final int sz) {
        int currentIdxInBuffer = 0;
        if (((sz - currentIdxInBuffer) > 16) // Enough bytes to check
                // header
                && ((buffer[currentIdxInBuffer] & 0xFF) == 0xFF) // First two
                // bytes
                // identify
                // JPG
                // SOI
                && ((buffer[currentIdxInBuffer + 1] & 0xFF) == 0xD8)) {
            // Looks like a JPG file.
            // If present, EXIF information is
            // the first
            // Marker. If not, we will need to skip the markers until
            // the end
            // of the file.
            currentIdxInBuffer += 2;
            if ((buffer[currentIdxInBuffer++] & 0xFF) == 0xFF // Marker prefix
            ) {
                int marker = buffer[currentIdxInBuffer++] & 0xFF;
                int marker_length = buffer[currentIdxInBuffer++] << 8 + buffer[currentIdxInBuffer++];
                if ((marker == 0xE1) // APP1
                        && (buffer[currentIdxInBuffer] == 'E')
                        && (buffer[currentIdxInBuffer + 1] == 'x')
                        && (buffer[currentIdxInBuffer + 2] == 'i')
                        && (buffer[currentIdxInBuffer + 3] == 'f')
                        && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
                        && (buffer[currentIdxInBuffer + 5] == 0x00) // Padding
                ) { // Exif APP1 marker
                    currentIdxInBuffer += 6;
                    int result;
                    exifApp1 = new ExifApp1();
                    result = exifApp1.read(buffer, currentIdxInBuffer);
                    if (result < 0) {
                        exifApp1 = null;
                    }
                    //bt747.sys.Generic.debug(this.toString());
                } else if ((marker == 0xE0) // APP0
                        && (buffer[currentIdxInBuffer] == 'J')
                        && (buffer[currentIdxInBuffer + 1] == 'F')
                        && (buffer[currentIdxInBuffer + 2] == 'I')
                        && (buffer[currentIdxInBuffer + 3] == 'F')
                        && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
                ) { // APP0 JFIF marker

                }
            }
        }
//        byte[] test = getBuffer();
//        for (int i = 0; i < test.length; i++) {
//            if (test[i] != buffer[i + 2]) {
//                Generic.debug("D:" + i + ":" + test[i] + ":" + buffer[i + 2]);
//            }
//        }
    }

    private final byte[] getBuffer() {
        byte[] buffer = null;
        if (exifApp1 != null) {
            // Get required size
            int size;
            // Exif header =
            // - Application Marker = 2
            // - Marker lenght = 2
            // - Identifier Exif = 6
            // Total = 10
            size = 10;
            size += exifApp1.getByteSize();
            // Should check size of header < 64k
            buffer = new byte[size];
            // Application Marker
            buffer[0] = (byte) 0xFF;
            buffer[1] = (byte) 0xE1;
            // Marker lenght (includes marker?)
            buffer[2] = (byte) ((size - 2) >> 8);
            buffer[3] = (byte) ((size - 2) & 0xFF);
            buffer[4] = 'E';
            buffer[5] = 'x';
            buffer[6] = 'i';
            buffer[7] = 'f';
            buffer[8] = (byte) 0x00;
            buffer[9] = (byte) 0x00;
            // Exif itself.
            exifApp1.fillBuffer(buffer, 10);

        }
        return buffer;
    }

    public final ExifAttribute getExifAttribute(final int tag) {
        if (exifApp1 != null) {
            return exifApp1.getExifAttribute(tag);
        } else {
            return null;
        }
    }

    public final ExifAttribute getGpsAttribute(final int tag) {
        if (exifApp1 != null) {
            return exifApp1.getGpsAttribute(tag);
        } else {
            return null;
        }
    }

    public final void setExifAttribute(final ExifAttribute atr) {
        if (exifApp1 != null) {
            exifApp1 = new ExifApp1();
        }
        exifApp1.setExifAttribute(atr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return exifApp1.toString();
    }
}
