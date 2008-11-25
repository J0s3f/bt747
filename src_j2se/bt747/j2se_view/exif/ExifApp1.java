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
public class ExifApp1 {

    private ExifIfdBlock Ifd0;
    private ExifIfdBlock exifBlock;
    private ExifIfdBlock gpsBlock;
    private ExifIfdBlock Ifd1;

    public final int read(final byte[] buffer, final int initialIdxInBuffer) {
        int currentIdxInBuffer = initialIdxInBuffer;
        int tiffHeaderStart = currentIdxInBuffer;
        boolean bigEndian;
        // Next is Tiff header (8 bytes)
        // Byte order
        if ((buffer[tiffHeaderStart] == 'I')
                && (buffer[tiffHeaderStart + 1] == 'I')) {
            // little endian (LSB first)
            bigEndian = false;
        } else {
            if ((buffer[tiffHeaderStart] == 'M')
                    && (buffer[tiffHeaderStart + 1] == 'M')) {
                bigEndian = true;
            } else {
                // TODO: bad format
                return -1;
            }
        }
        if (ExifUtils.getShort2byte(buffer, tiffHeaderStart + 2, bigEndian) == 0x002A) {
            // This must be the case
        } else {
            // TODO: bad format
            return -1;
        }
        // TODO: not sure if this value uses defined
        // endianess
        int ifd0Offset = ExifUtils.getLong4byte(buffer, tiffHeaderStart + 4,
                bigEndian);
        // Then IFDs.

        // Go through IF0 to find EXIF information.
        currentIdxInBuffer = ifd0Offset + tiffHeaderStart;

        Ifd0 = new ExifIfdBlock();
        int ifd1Offset;
        ifd1Offset = Ifd0.read(buffer, currentIdxInBuffer, tiffHeaderStart,
                bigEndian);

        //bt747.sys.Generic.debug("EXIF Header");

        ExifAttribute exifAtr;
        exifAtr = Ifd0.get(ExifConstants.TAG_EXIF);
        if (exifAtr != null) {
            exifBlock = new ExifIfdBlock();
            exifBlock.read(buffer, exifAtr.getIntValue(0) + tiffHeaderStart,
                    tiffHeaderStart, bigEndian);
        }

        //bt747.sys.Generic.debug("GPS Header");
        exifAtr = Ifd0.get(ExifConstants.TAG_GPSINFO);
        if (exifAtr != null) {
            gpsBlock = new ExifIfdBlock();
            gpsBlock.read(buffer, exifAtr.getIntValue(0) + tiffHeaderStart,
                    tiffHeaderStart, bigEndian);
        }

        if (ifd1Offset != 0) {
            //bt747.sys.Generic.debug("Ifd1 Block");
            Ifd1 = new ExifIfdBlock();
            Ifd1.read(buffer, ifd1Offset + tiffHeaderStart, tiffHeaderStart,
                    bigEndian);

            // EXIF offset is given in IFD0
            //bt747.sys.Generic.debug("EXIF read");
        }
        return 0;
    }

    public final ExifAttribute getExifAttribute(final int tag) {
        if (exifBlock != null) {
            return exifBlock.get(tag);
        } else {
            return null;
        }
    }

    public final ExifAttribute getGpsAttribute(final int tag) {
        if (gpsBlock != null) {
            return gpsBlock.get(tag);
        } else {
            return null;
        }
    }

    public final void setExifAttribute(final ExifAttribute atr) {
        if (exifBlock == null) {
            exifBlock = new ExifIfdBlock();
        }
        exifBlock.set(atr);
    }
}
