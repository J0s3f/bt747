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

import net.sf.bt747.j2se.app.utils.Utils;
import gps.log.in.WindowedFile;

import bt747.Version;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Path;

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

    private BT747Path Path; // Path to the file

    public final boolean setFilePath(final BT747Path p) {
        Path = p;
        return getInfo();
    }

//    private boolean getInfo() {
//        WindowedFile p = null;
//        boolean success = false;
//
//        try {
//            // bt747.sys.Generic.debug(Path);
//            p = new WindowedFile(Path, File.READ_ONLY);
//            if ((p != null) && p.isOpen()) {
//                byte[] buffer;
//                int sz;
//                p.setBufferSize(64 * 1024 + 10);
//                p.fillBuffer(0);
//                buffer = p.getBuffer();
//                sz = p.getBufferFill();
//                p.close();
//                p = null;
//                success = examineBuffer(buffer, sz);
//                // bt747.sys.Generic.debug(this.toString());
//            }
//        } catch (final Exception e) {
//            // TODO: handle exception
//            Generic.debug("EXIFJpg", e);
//        }
//        if (p != null) {
//            p.close();
//            p = null;
//        }
//        return success;
//    }

    
    private boolean getInfo() {
        byte[] buffer;
        boolean success = false;
        buffer = getHeader();
        if (buffer != null) {
            success = examineBuffer(buffer, buffer.length);
        }
        return success;
    }

    private byte[] getHeader() {
        WindowedFile p = null;
        byte[] buffer = null;

        try {
            // bt747.sys.Generic.debug(Path);
            p = new WindowedFile(Path, File.READ_ONLY);
            if ((p != null) && p.isOpen()) {
                int sz;
                int bytesRead;
                p.getSize();
                sz = Math.min(p.getSize(), 64 * 1024 + 10);
                p.setBufferSize(sz);
                p.fillBuffer(0);
                buffer = p.getBuffer();
                bytesRead = p.getBufferFill();
                p.close();
                p = null;
                if (bytesRead != sz) {
                    buffer = null;
                }
            }
        } catch (final Exception e) {
            // TODO: handle exception
            Generic.debug("EXIFJpg", e);
        }
        if (p != null) {
            p.close();
            p = null;
            buffer = null;
        }
        return buffer;
    }


    private ExifApp1 exifApp1;

//    /**
//     * @param buffer
//     * @param sz
//     * @return true if JPEG
//     */
//    private final boolean examineBuffer(final byte[] buffer, final int sz) {
//        ExifOffsets offsets;
//        
//        offsets = getOffsets(buffer, sz);
//
//        
//        int currentIdxInBuffer = 0;
//        if (((sz - currentIdxInBuffer) > 16) // Enough bytes to check
//                // header
//                && ((buffer[currentIdxInBuffer] & 0xFF) == 0xFF) // First
//                // two
//                // bytes
//                // identify
//                // JPG
//                // SOI
//                && ((buffer[currentIdxInBuffer + 1] & 0xFF) == 0xD8)) {
//            // Looks like a JPG file.
//            // If present, EXIF information is
//            // the first
//            // Marker. If not, we will need to skip the markers until
//            // the end
//            // of the file.
//            currentIdxInBuffer += 2;
//            int marker;
//            int marker_length;
//            int skipMarkerPosition;
//            boolean skipMarkers;
//            do {
//                skipMarkers = false;
//                marker = ((buffer[currentIdxInBuffer++] & 0xFF) << 8)
//                        + (buffer[currentIdxInBuffer++] & 0xFF);
//                marker_length = ((buffer[currentIdxInBuffer++] & 0xFF) << 8)
//                        + (buffer[currentIdxInBuffer++] & 0xFF);
//                skipMarkerPosition = marker_length + currentIdxInBuffer - 2;
//
//                if ((marker == 0xFFE1) // APP1
//                        && (buffer[currentIdxInBuffer] == 'E')
//                        && (buffer[currentIdxInBuffer + 1] == 'x')
//                        && (buffer[currentIdxInBuffer + 2] == 'i')
//                        && (buffer[currentIdxInBuffer + 3] == 'f')
//                        && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
//                        && (buffer[currentIdxInBuffer + 5] == 0x00) // Padding
//                ) { // Exif APP1 marker
//                    currentIdxInBuffer += 6;
//                    int result;
//                    exifApp1 = new ExifApp1();
//                    Generic.debug(Utils.format("App1Offset %d %d", currentIdxInBuffer, offsets.app1Offset));
//                    result = exifApp1.read(buffer, currentIdxInBuffer);
//                    if (result < 0) {
//                        exifApp1 = null;
//                    }
//                    // bt747.sys.Generic.debug(this.toString());
//                } else if ((marker == 0xFFE0) // APP0
//                        && (buffer[currentIdxInBuffer] == 'J')
//                        && (buffer[currentIdxInBuffer + 1] == 'F')
//                        && (buffer[currentIdxInBuffer + 2] == 'I')
//                        && (buffer[currentIdxInBuffer + 3] == 'F')
//                        && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
//                ) { // APP0 JFIF marker
//                    skipMarkers = true;
//
//                }
//                if (skipMarkers) {
//                    currentIdxInBuffer = skipMarkerPosition;
//                }
//            } while (skipMarkers);
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * @param buffer
     * @param sz
     * @return true if JPEG
     */
    private final boolean examineBuffer(final byte[] buffer, final int sz) {
        ExifOffsets offsets;
        
        offsets = getOffsets(buffer, sz);
        if(offsets!=null) {
            if(offsets.app1Offset>=0) {
                int result;
                exifApp1 = new ExifApp1();
                result = exifApp1.read(buffer, offsets.app1Offset);
                if (result < 0) {
                    exifApp1 = null;
                }
            }
            return true;
        }
        return false;
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
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSLATITUDEREF,
                ExifConstants.ASCII, 2);
        if (lat < 0) {
            atr.setStringValue("S");
        } else {
            atr.setStringValue("N");
        }

        setGpsAttribute(atr);

        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSLATITUDE,
                ExifConstants.RATIONAL, 3);
        atr.setGpsFloatValue(lat);
        setGpsAttribute(atr);

        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSLONGITUDEREF,
                ExifConstants.ASCII, 2);
        if (lon < 0) {
            atr.setStringValue("W");
        } else {
            atr.setStringValue("E");
        }
        setGpsAttribute(atr);

        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSLONGITUDE,
                ExifConstants.RATIONAL, 3);
        atr.setGpsFloatValue(lon);
        setGpsAttribute(atr);
    }

    public final void setGpsAltitudeMSL(final float altitude) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSALTITUDEREF,
                ExifConstants.BYTE, 1);
        ExifAttribute altitudeAtr;
        altitudeAtr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSALTITUDE,
                ExifConstants.RATIONAL, 1);
        if (altitude < 0) {
            atr.setIntValue(0, 1);
            altitudeAtr.setFloatValue(0, (int) (-altitude * 100), 100);
        } else {
            atr.setIntValue(0, 0);
            altitudeAtr.setFloatValue(0, (int) (altitude * 100), 100);
        }
        setGpsAttribute(atr);
        setGpsAttribute(altitudeAtr);
    }

    public final void setGpsTime(final int year, final int month,
            final int day, final int hour, final int minutes,
            final int seconds) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSTIMESTAMP,
                ExifConstants.RATIONAL, 3);
        atr.setFloatValue(0, hour, 1);
        atr.setFloatValue(1, minutes, 1);
        atr.setFloatValue(2, seconds, 1);
        setGpsAttribute(atr);

        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSDATESTAMP,
                ExifConstants.ASCII, 11);

        String dateStr = "" + year + ":";

        if (month < 10) {
            dateStr += "0" + month + ":";
        } else {
            dateStr += month + ":";
        }

        if (day < 10) {
            dateStr += "0" + day;
        } else {
            dateStr += day;
        }
        atr.setStringValue(dateStr);
        setGpsAttribute(atr);
    }

    public final void setDifferential(final boolean isDifferential) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSTIMESTAMP,
                ExifConstants.SHORT, 1);
        atr.setIntValue(0, isDifferential ? 1 : 0);
    }

    public final void setGpsSatInformation(final String str) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSSATELLITES,
                ExifConstants.ASCII, str.length() + 1);
        atr.setStringValue(str);
        setGpsAttribute(atr);
    }

    /**
     * Attention : related to HDOP/PDOP reading.
     * 
     * @param measurementInProgressA
     */
    public final void setGpsStatus(final boolean measurementInProgressA) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSSTATUS,
                ExifConstants.ASCII, 2);
        if (measurementInProgressA) {
            atr.setStringValue("A");
        } else {
            atr.setStringValue("V");
        }
        setGpsAttribute(atr);
    }

    /**
     * Attention: also
     */
    public final void setGpsMeasureMode(final boolean is3d) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSMEASUREMODE,
                ExifConstants.ASCII, 2);
        if (is3d) {
            atr.setStringValue("3");
        } else {
            atr.setStringValue("2");
        }
        setGpsAttribute(atr);
    }

    /**
     * Attention: also set GPS HDOP (also sets GpsMeasureMode)
     */
    public final void setGpsHDOP(final float hdopX100) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSDOP,
                ExifConstants.RATIONAL, 1);
        atr.setFloatValue(0, (int) hdopX100, 100);
        setGpsAttribute(atr);
        setGpsMeasureMode(false);
    }

    /**
     * Attention: also set GPS PDOP (also sets GpsMeasureMode)
     */
    public final void setGpsPDOP(final int pdopX100) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSDOP,
                ExifConstants.RATIONAL, 1);
        atr.setFloatValue(0, pdopX100, 100);
        setGpsAttribute(atr);
        setGpsMeasureMode(true);
    }

    /**
     * Sets the speed
     */
    public final void setGpsSpeedKmH(final float speed) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSSPEEDREF,
                ExifConstants.ASCII, 2);
        ExifAttribute speedAtr;
        speedAtr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSSPEED,
                ExifConstants.RATIONAL, 1);
        atr.setStringValue("K");
        speedAtr.setFloatValue(0, (int) (speed * 100), 100);
        setGpsAttribute(atr);
        setGpsAttribute(speedAtr);
    }

    /**
     * Sets the heading.
     */
    public final void setGpsTrack(final float heading) {
        ExifAttribute atr;
        atr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSTRACKREF,
                ExifConstants.ASCII, 2);
        ExifAttribute trackAtr;
        trackAtr = exifApp1.newExifAttribute(ExifConstants.TAG_GPSTRACK,
                ExifConstants.RATIONAL, 1);
        atr.setStringValue("T");
        trackAtr.setFloatValue(0, (int) (heading * 100), 100);
        setGpsAttribute(atr);
        setGpsAttribute(trackAtr);
    }

    private final static String SW = "BT747 " + Version.VERSION_NUMBER;

    public final void setUsedSoftWare() {
        ExifAttribute atr;
        String sw;
        // Generic.debug(toString());
        atr = exifApp1.getIfd0Attribute(ExifConstants.TAG_SOFTWARE);
        if (atr != null) {
            sw = " / " + atr.getStringValue();
        } else {
            sw = "";
        }
        if (sw.indexOf(SW) < 0) {
            sw = SW + sw;
            atr = exifApp1.newExifAttribute(ExifConstants.TAG_SOFTWARE,
                    ExifConstants.ASCII, sw.length() + 1);
            atr.setStringValue(sw);
            exifApp1.setIfd0Attribute(atr);
            // Generic.debug(toString());
        }
    }

    public final boolean copyTo(final BT747Path path) {
        File toFile = null;
        WindowedFile fromFile = null;
        int currentIdxInBuffer = 0;
        boolean success = false;

        try {
            // bt747.sys.Generic.debug(Path);
            fromFile = new WindowedFile(Path, File.READ_ONLY);
            if ((exifApp1 != null) && (fromFile != null) && fromFile.isOpen()) {
                // setUsedSoftWare();
                byte[] buffer;
                int sz;
                // bt747.sys.Generic.debug(this.toString());
                fromFile.setBufferSize(64 * 1024);
                fromFile.fillBuffer(0);
                buffer = fromFile.getBuffer();

                sz = fromFile.getBufferFill();
                
                final ExifOffsets offsets = getOffsets(buffer, sz);
                
                if (((sz - currentIdxInBuffer) > 16) // Enough bytes to
                        // check
                        // header
                        && ((buffer[currentIdxInBuffer] & 0xFF) == 0xFF) // First
                        // two
                        // bytes
                        // identify
                        // JPG
                        // SOI
                        && ((buffer[currentIdxInBuffer + 1] & 0xFF) == 0xD8)) {
                    currentIdxInBuffer += 2;
                    int marker;
                    int marker_length;
                    int skipMarkerPosition;
                    int currentMarkerStart;
                    boolean skipMarkers;
                    do {
                        skipMarkers = false;
                        currentMarkerStart = currentIdxInBuffer;
                        marker = ((buffer[currentIdxInBuffer] & 0xFF) << 8)
                                + (buffer[currentIdxInBuffer + 1] & 0xFF);
                        marker_length = ((buffer[currentIdxInBuffer + 2] & 0xFF) << 8)
                                + (buffer[currentIdxInBuffer + 3] & 0xFF);
                        skipMarkerPosition = marker_length
                                + currentIdxInBuffer + 2;
                        currentIdxInBuffer += 4;

                        if ((marker == 0xFFE1) // APP1
                                && (buffer[currentIdxInBuffer] == 'E')
                                && (buffer[currentIdxInBuffer + 1] == 'x')
                                && (buffer[currentIdxInBuffer + 2] == 'i')
                                && (buffer[currentIdxInBuffer + 3] == 'f')
                                && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
                                && (buffer[currentIdxInBuffer + 5] == 0x00)) { // Padding
                            currentIdxInBuffer = skipMarkerPosition;
                        } else if ((marker == 0xFFE0) // APP0
                                && (buffer[currentIdxInBuffer] == 'J')
                                && (buffer[currentIdxInBuffer + 1] == 'F')
                                && (buffer[currentIdxInBuffer + 2] == 'I')
                                && (buffer[currentIdxInBuffer + 3] == 'F')
                                && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
                        ) { // APP0 JFIF marker
                            skipMarkers = true;
                        }
                        if (skipMarkers) {
                            currentIdxInBuffer = skipMarkerPosition;
                        }
                    } while (skipMarkers);
                    // TODO May have to delete the file first.
                    toFile = new File(path, File.CREATE);
                    
                    // For debug of new method
                    if(offsets.app1Marker!=currentMarkerStart) {
                       Generic.debug(Utils.format("App1Marker %d %d ", offsets.app1Marker, currentMarkerStart));
                    }
                    toFile.writeBytes(buffer, 0, currentMarkerStart);
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
                    // Copy rest of file.
                    // For debug of new system.
                    if(offsets.afterApp1Offset!=skipMarkerPosition) {
                        Generic.debug(Utils.format("App1Marker %d %d ", offsets.afterApp1Offset, skipMarkerPosition));
                     }
                    currentIdxInBuffer = skipMarkerPosition;
                    fromFile.fillBuffer(skipMarkerPosition);
                    while (fromFile.getBufferFill() > 0) {
                        toFile.writeBytes(fromFile.getBuffer(), 0, fromFile
                                .getBufferFill());
                        currentIdxInBuffer += fromFile.getBufferFill();
                        fromFile.fillBuffer(currentIdxInBuffer);
                    }
                    toFile.close();
                    toFile = null;
                }
                fromFile.close();
                fromFile = null;
                success = true;
            }
        } catch (final Exception e) {
            // TODO: handle exception
            Generic.debug("EXIFJpg", e);
        }
        if (fromFile != null) {
            fromFile.close();
            fromFile = null;
        }
        if (toFile != null) {
            toFile.close();
            toFile = null;
        }
        return success;

    }

    private static class ExifOffsets {
        int app1Marker = -1;
        int app1Offset = -1;
        int afterApp1Offset = -1;
    }
    
    /** Currently in test.
     * @param buffer
     * @param sz
     * @return true if JPEG
     */
    private final ExifOffsets getOffsets(final byte[] buffer, final int sz) {
        ExifOffsets offsets = new ExifOffsets();
        int currentIdxInBuffer = 0;
        if (((sz - currentIdxInBuffer) > 16) // Enough bytes to check
                // header
                && ((buffer[currentIdxInBuffer] & 0xFF) == 0xFF) // First
                // two
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
            int marker;
            int marker_length;
            int skipMarkerPosition;
            int currentMarkerStart;
            boolean skipMarkers;
            do {
                skipMarkers = false;
                currentMarkerStart = currentIdxInBuffer;
                marker = ((buffer[currentIdxInBuffer++] & 0xFF) << 8)
                        + (buffer[currentIdxInBuffer++] & 0xFF);
                marker_length = ((buffer[currentIdxInBuffer++] & 0xFF) << 8)
                        + (buffer[currentIdxInBuffer++] & 0xFF);
                skipMarkerPosition = marker_length + currentIdxInBuffer - 2;

                if ((marker == 0xFFE1) // APP1
                        && (buffer[currentIdxInBuffer] == 'E')
                        && (buffer[currentIdxInBuffer + 1] == 'x')
                        && (buffer[currentIdxInBuffer + 2] == 'i')
                        && (buffer[currentIdxInBuffer + 3] == 'f')
                        && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
                        && (buffer[currentIdxInBuffer + 5] == 0x00) // Padding
                ) { // Exif APP1 marker
                    offsets.app1Marker = currentMarkerStart;
                    currentIdxInBuffer += 6;
                    offsets.app1Offset = currentIdxInBuffer;
                    offsets.afterApp1Offset = skipMarkerPosition;
//                    int result;
//                    exifApp1 = new ExifApp1();
//                    result = exifApp1.read(buffer, currentIdxInBuffer);
//                    if (result < 0) {
//                        exifApp1 = null;
//                    }
                    // bt747.sys.Generic.debug(this.toString());
                } else if ((marker == 0xFFE0) // APP0
                        && (buffer[currentIdxInBuffer] == 'J')
                        && (buffer[currentIdxInBuffer + 1] == 'F')
                        && (buffer[currentIdxInBuffer + 2] == 'I')
                        && (buffer[currentIdxInBuffer + 3] == 'F')
                        && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
                ) { // APP0 JFIF marker
                    skipMarkers = true;
                }
                if (skipMarkers) {
                    currentIdxInBuffer = skipMarkerPosition;
                }
            } while (skipMarkers);
            return offsets;
        } else {
            return null;
        }
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
