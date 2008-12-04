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

import bt747.Version;
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
 * Specification at http://partners.adobe.com/public/developer/en/tiff/TIFF6.pdf
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
                p.close();
                p = null;
                examineBuffer(buffer, sz);
                // bt747.sys.Generic.debug(this.toString());
                success = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            Generic.debug("EXIFTiff", e);
        }
        return success;
    }

    private ExifApp1 exifApp1;

    private final void examineBuffer(final byte[] buffer, final int sz) {
        int currentIdxInBuffer = 0;

        int result;
        exifApp1 = new ExifApp1();
        result = exifApp1.read(buffer, currentIdxInBuffer);
        if (result < 0) {
            exifApp1 = null;
        }
        // bt747.sys.Generic.debug(this.toString());
    }

    private final byte[] getBuffer() {
        byte[] buffer = null;
        if (exifApp1 != null) {
            // Get required size
            int size;
            size = exifApp1.getByteSize();
            // Should check size of header < 64k
            buffer = new byte[size];
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
        if (exifApp1 == null) {
            exifApp1 = new ExifApp1();
        }
        exifApp1.setExifAttribute(atr);
    }

    public final void setGpsAttribute(final ExifAttribute atr) {
        if (exifApp1 == null) {
            exifApp1 = new ExifApp1();
        }
        exifApp1.setGpsAttribute(atr);
    }

    public final void setGpsPosition(final double lat, final double lon) {
        ExifAttribute atr;
        atr = new ExifAttribute(ExifConstants.TAG_GPSLATITUDEREF,
                ExifConstants.ASCII, 2);
        if (lat < 0) {
            atr.setStringValue("S");
        } else {
            atr.setStringValue("N");
        }

        setGpsAttribute(atr);

        atr = new ExifAttribute(ExifConstants.TAG_GPSLATITUDE,
                ExifConstants.RATIONAL, 3);
        atr.setGpsFloatValue(lat);
        setGpsAttribute(atr);

        atr = new ExifAttribute(ExifConstants.TAG_GPSLONGITUDEREF,
                ExifConstants.ASCII, 2);
        if (lat < 0) {
            atr.setStringValue("W");
        } else {
            atr.setStringValue("E");
        }
        setGpsAttribute(atr);

        atr = new ExifAttribute(ExifConstants.TAG_GPSLONGITUDE,
                ExifConstants.RATIONAL, 3);
        atr.setGpsFloatValue(lon);
        setGpsAttribute(atr);
    }

    private final static String SW = "BT747 " + Version.VERSION_NUMBER;

    public final void setUsedSoftWare() {
        ExifAttribute atr;
        String sw;
        Generic.debug(toString());
        atr = exifApp1.getIfd0Attribute(ExifConstants.TAG_SOFTWARE);
        if (atr != null) {
            sw = " / " + atr.getStringValue();
        } else {
            sw = "";
        }
        if (sw.indexOf(SW) < 0) {
            sw = SW + sw;
            atr = new ExifAttribute(ExifConstants.TAG_SOFTWARE,
                    ExifConstants.ASCII, sw.length() + 1);
            atr.setStringValue(sw);
            exifApp1.setIfd0Attribute(atr);
            Generic.debug(toString());
        }
    }

    public final boolean copyTo(final String path, final int card) {
        File toFile;
        WindowedFile fromFile;
        int currentIdxInBuffer = 0;
        boolean success = false;

        try {
            // bt747.sys.Generic.debug(Path);
            fromFile = new WindowedFile(Path, File.READ_ONLY, card);
            if (exifApp1 != null && fromFile != null && fromFile.isOpen()) {
                // setUsedSoftWare();
                byte[] buffer;
                int sz;
                // bt747.sys.Generic.debug(this.toString());
                fromFile.setBufferSize(64 * 1024);
                fromFile.fillBuffer(0);
                buffer = fromFile.getBuffer();

                sz = fromFile.getBufferFill();
                toFile = new File(path, File.CREATE, card);
                // buffer = null;
                byte[] exif;
                exif = getBuffer();
                // byte[] test = exif;
                // for (int i = 0; i < test.length; i++) {
                // if (test[i] != buffer[i + 2]) {
                // Generic.debug("D:" + i + ":" + test[i] + ":"
                // + buffer[i + 2]);
                // }
                // }
                toFile.writeBytes(exif, 0, exif.length);

                while (fromFile.getBufferFill() >= 0) {
                    toFile.writeBytes(fromFile.getBuffer(), 0, fromFile
                            .getBufferFill());
                    currentIdxInBuffer += fromFile.getBufferFill();
                    fromFile.fillBuffer(currentIdxInBuffer);
                }
                toFile.close();
                toFile = null;
                fromFile.close();
                fromFile = null;
                success = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            Generic.debug("EXIFTiff", e);
        }
        return success;

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
