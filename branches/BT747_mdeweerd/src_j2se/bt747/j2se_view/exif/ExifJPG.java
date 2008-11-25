/**
 * 
 */
package bt747.j2se_view.exif;

import com.sun.java_cup.internal.internal_error;

import gps.log.in.WindowedFile;

import bt747.sys.Convert;
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
    /**
     * Follows SOI (Start Of Image) Marker<br>
     * Maximum 64kB.
     * 
     * <code>
     * APP1 ("EXIF HEADER")<br> 
     * <br>
     * Position  Value  Meaning<br>
     * 00        0xFF   Marker Prefix+00 FF Marker Prefix<br>
     * 01        0xE1   APP1<br>
     * 02-03     0xXXXX Length of field<br>
     * 04        0x45   'E'<br>
     * 05        0x78   'x'<br>
     * 06        0x69   'i'<br>
     * 07        0x66   'f'<br>
     * 08        0x00   NULL<br>
     * 09        0x00   Padding<br>
     * 0A               Attribute information<br>
     * </code>
     */
    /**
     * Application markers used in EXIF<br>
     * SOI Start of Image FFD8.H Start of compressed data<br>
     * APP1 Application Segment 1 FFE1.H Exif attribute information<br>
     * APP2 Application Segment 2 FFE2.H Exif extended data<br>
     * DQT Define Quantization Table FFDB.H Quantization table definition<br>
     * DHT Define Huffman Table FFC4.H Huffman table definition<br>
     * DRI Define Restart Interoperability FFDD.H Restart Interoperability
     * definition<br>
     * SOF Start of Frame FFC0.H Parameter data relating to frame<br>
     * SOS Start of Scan FFDA.H Parameters relating to components<br>
     * EOI End of Image FFD9.H End of compressed data<br>
     */

    /** Image width ImageWidth 256 100 M M M J */
    private static final int TAG_IMAGEWIDTH = 0x100;
    /** Image height ImageLength 257 101 M M M J */
    private static final int TAG_IMAGELENGTH = 0x101;
    /** Number of bits per component BitsPerSample 258 102 M M M J */
    /** Compression scheme Compression 259 103 M M M J */
    /** Pixel composition PhotometricInterpretation 262 106 M M M N */
    /** Image title ImageDescription 270 10E R R R R */
    /** Manufacturer of image input */
    /** equipment Make 271 10F R R R R */
    /** Model of image input equipment Model 272 110 R R R R */
    /** Image data location StripOffsets 273 111 M M M N */
    /** Orientation of image Orientation 274 112 R R R R */
    /** Number of components SamplesPerPixel 277 115 M M M J */
    /** Number of rows per strip RowsPerStrip 278 116 M M M N */
    /** Bytes per compressed strip StripByteCounts 279 117 M M M N */
    /** Image resolution in width direction XResolution 282 11A M M M M */
    /** Image resolution in height direction YResolution 283 11B M M M M */
    /** Image data arrangement PlanarConfiguration 284 11C O M O J */
    /** Unit of X and Y resolution ResolutionUnit 296 128 M M M M */
    /** Transfer function TransferFunction 301 12D R R R R */
    /** Software used Software 305 131 O O O O */
    private static final int TAG_SOFTWARE = 0x8769;
    /** File change date and time DateTime 306 132 R R R R */
    /** Person who created the image Artist 315 13B O O O O */
    /** White point chromaticity WhitePoint 318 13E O O O O */
    /** Chromaticities of primaries PrimaryChromaticities 319 13F O O O O */
    /** Offset to JPEG SOI JPEGInterchangeFormat 513 201 N N N N */
    /** Bytes of JPEG data JPEGInterchangeFormatLength 514 202 N N N N */
    /** Color space transformation matrix */
    /** coefficients YCbCrCoefficients 529 211 N N O O */
    /** Subsampling ratio of Y to C YCbCrSubSampling 530 212 N N M J */
    /** Y and C positioning YCbCrPositioning 531 213 N N M M */
    /** Pair of black and white reference */
    /** values ReferenceBlackWhite 532 214 O O O O */
    /** Copyright holder Copyright 33432 8298 O O O O */
    /** Exif tag Exif IFD Pointer 34665 8769 M M M M */
    private static final int TAG_EXIF = 0x8769;
    /** GPS tag GPSInfo IFD Pointer 34853 8825 O O O O */
    private static final int TAG_GPSINFO = 0x8825;
    /**
     * 
     * 4.6.6 GPS Attribute Information<br>
     * The attribute information (field names and codes) recorded in the GPS
     * Info IFD is given in Table 12, followed by an<br>
     * explanation of the contents.<br>
     * Table 12 GPS Attribute Information<br>
     * Tag ID<br>
     * Tag Name<br>
     * Field Name<br>
     * Dec Hex<br>
     * Type Count<br>
     * A. Tags Relating to GPS<br>
     * GPS tag version GPSVersionID 0 0 BYTE 4<br>
     * North or South Latitude GPSLatitudeRef 1 1 ASCII 2<br>
     * Latitude GPSLatitude 2 2 RATIONAL 3<br>
     * East or West Longitude GPSLongitudeRef 3 3 ASCII 2<br>
     * Longitude GPSLongitude 4 4 RATIONAL 3<br>
     * Altitude reference GPSAltitudeRef 5 5 BYTE 1<br>
     * Altitude GPSAltitude 6 6 RATIONAL 1<br>
     * GPS time (atomic clock) GPSTimeStamp 7 7 RATIONAL 3<br>
     * GPS satellites used for measurement GPSSatellites 8 8 ASCII Any<br>
     * GPS receiver status GPSStatus 9 9 ASCII 2<br>
     * GPS measurement mode GPSMeasureMode 10 A ASCII 2<br>
     * Measurement precision GPSDOP 11 B RATIONAL 1<br>
     * Speed unit GPSSpeedRef 12 C ASCII 2<br>
     * Speed of GPS receiver GPSSpeed 13 D RATIONAL 1<br>
     * Reference for direction of movement GPSTrackRef 14 E ASCII 2<br>
     * Direction of movement GPSTrack 15 F RATIONAL 1<br>
     * Reference for direction of image GPSImgDirectionRef 16 10 ASCII 2<br>
     * Direction of image GPSImgDirection 17 11 RATIONAL 1<br>
     * Geodetic survey data used GPSMapDatum 18 12 ASCII Any<br>
     * Reference for latitude of destination GPSDestLatitudeRef 19 13 ASCII 2<br>
     * Latitude of destination GPSDestLatitude 20 14 RATIONAL 3<br>
     * Reference for longitude of destination GPSDestLongitudeRef 21 15 ASCII 2<br>
     * Longitude of destination GPSDestLongitude 22 16 RATIONAL 3<br>
     * Reference for bearing of destination GPSDestBearingRef 23 17 ASCII 2<br>
     * Bearing of destination GPSDestBearing 24 18 RATIONAL 1<br>
     * Reference for distance to destination GPSDestDistanceRef 25 19 ASCII 2<br>
     * Distance to destination GPSDestDistance 26 1A RATIONAL 1<br>
     * Name of GPS processing method GPSProcessingMethod 27 1B UNDEFINED Any<br>
     * Name of GPS area GPSAreaInformation 28 1C UNDEFINED Any<br>
     * GPS date GPSDateStamp 29 1D ASCII 11<br>
     * GPS differential correction GPSDifferential 30 1E SHORT 1
     */

    /**
     * EXIF FIeld types
     * 
     */

    int BYTE = 1;
    int ASCII = 2;
    int SHORT = 3;
    int LONG = 4; // 4 bytes
    int RATIONAL = 5; // First long is numerator, second is denominator
    int UNDEFINED = 7; // 9 bit byte that can take any value depending on field
    // def.
    int SLONG = 9; // 4 byte signed integer
    int SRATIONAL = 10; // First long is numerator, second is denominator

    /**
     * IFD Structure<br>
     * Bytes 0-1 Tag<br>
     * Bytes 2-3 Type<br>
     * Bytes 4-7 Count<br>
     * Bytes 8-11 Value Offset
     */

    private String Path; // Path to the file
    private int card = -1; // Card number on Palm

    public final void setPath(final String p) {
        Path = p;
        getInfo();
    }

    public final void setPath(final String p, int c) {
        card = c;
        Path = p;
        getInfo();
    }

    private void getInfo() {
        WindowedFile p;

        try {
            Generic.debug(Path);
            p = new WindowedFile(Path, File.READ_ONLY, card);
            if (p != null && p.isOpen()) {
                byte[] buffer;
                int sz;
                p.setBufferSize(64 * 1024 + 10);
                p.fillBuffer(0);
                buffer = p.getBuffer();
                sz = p.getBufferFill();
                examineBuffer(buffer, sz);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    final private void examineBuffer(final byte[] buffer, final int sz) {
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
            // For the moment we suppose that the EXIF information is
            // the first
            // Marker. If not, we will need to skip the markers until
            // the end
            // of the file.
            currentIdxInBuffer += 2;
            if ((buffer[currentIdxInBuffer++] & 0xFF) == 0xFF // Marker prefix
            ) {
                int marker = buffer[currentIdxInBuffer++] & 0xFF;
                int marker_length = buffer[currentIdxInBuffer++] << 8 + buffer[currentIdxInBuffer++];
                if ((marker == 0xE1) && (buffer[currentIdxInBuffer] == 'E')
                        && (buffer[currentIdxInBuffer + 1] == 'x')
                        && (buffer[currentIdxInBuffer + 2] == 'i')
                        && (buffer[currentIdxInBuffer + 3] == 'f')
                        && (buffer[currentIdxInBuffer + 4] == 0x00) // Padding
                        && (buffer[currentIdxInBuffer + 5] == 0x00) // Padding
                ) { // Exif APP1 marker
                    currentIdxInBuffer += 6;
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
                            return;
                        }
                    }
                    if (getShort2byte(buffer, tiffHeaderStart + 2, bigEndian) == 0x002A) {
                        // This must be the case
                    } else {
                        // TODO: bad format
                        return;
                    }
                    // TODO: not sure if this value uses defined
                    // endianess
                    int ifd0Offset = getLong4byte(buffer, tiffHeaderStart + 4,
                            bigEndian);
                    // Then IFDs.

                    // Go through IF0 to find EXIF information.
                    currentIdxInBuffer = ifd0Offset + tiffHeaderStart;

                    boolean continueIF0 = true;
                    int exifIFD = 0;
                    int gpsIFD = 0;
                    int tag;
                    int type;
                    int count;
                    int value = -1;
                    int interoperabilityNumber;
                    int idx;
                    interoperabilityNumber = getShort2byte(buffer,
                            currentIdxInBuffer, bigEndian);
                    idx = interoperabilityNumber;
                    currentIdxInBuffer += 2;
                    while ((--idx >= 0) && ((currentIdxInBuffer + 12) < sz)) {
                        tag = getShort2byte(buffer, currentIdxInBuffer,
                                bigEndian);
                        type = getShort2byte(buffer, currentIdxInBuffer + 2,
                                bigEndian);
                        count = getLong4byte(buffer, currentIdxInBuffer + 4,
                                bigEndian);
                        value = getLong4byte(buffer, currentIdxInBuffer + 8,
                                bigEndian);
                        currentIdxInBuffer += 12;

                        Generic.debug("Tag:" + Convert.unsigned2hex(tag, 4)
                                + " Type:" + Convert.unsigned2hex(type, 4)
                                + " Count:" + Convert.unsigned2hex(count, 8)
                                + " Value:" + Convert.unsigned2hex(value, 8));
                        switch (tag) {
                        case TAG_EXIF: // EXIF IFD Pointer
                            if (type == LONG && count == 1) {
                                exifIFD = value;
                            }
                            break;
                        case TAG_GPSINFO:
                            if (type == LONG && count == 1) {
                                gpsIFD = value;
                            }
                            break;
                        default:
                            break;
                        }
                    }


                    Generic.debug("EXIF Header");
                    if (exifIFD != 0) {
                        currentIdxInBuffer = exifIFD + tiffHeaderStart;
                        interoperabilityNumber = getShort2byte(buffer,
                                currentIdxInBuffer, bigEndian);
                        idx = interoperabilityNumber;
                        currentIdxInBuffer += 2;
                        while ((--idx >= 0) && ((currentIdxInBuffer + 12) < sz)) {
                            tag = getShort2byte(buffer, currentIdxInBuffer,
                                    bigEndian);
                            type = getShort2byte(buffer,
                                    currentIdxInBuffer + 2, bigEndian);
                            count = getLong4byte(buffer,
                                    currentIdxInBuffer + 4, bigEndian);
                            value = getLong4byte(buffer,
                                    currentIdxInBuffer + 8, bigEndian);
                            currentIdxInBuffer += 12;

                            Generic.debug("Tag:" + Convert.unsigned2hex(tag, 4)
                                    + " Type:" + Convert.unsigned2hex(type, 4)
                                    + " Count:"
                                    + Convert.unsigned2hex(count, 8)
                                    + " Value:"
                                    + Convert.unsigned2hex(value, 8));
                            switch (tag) {
                            case TAG_EXIF: // EXIF IFD Pointer
                                if (type == LONG && count == 1) {
                                    exifIFD = value;
                                }
                                break;
                            case TAG_GPSINFO:
                                if (type == LONG && count == 1) {
                                    gpsIFD = value;
                                }
                                break;
                            default:
                                break;
                            }
                        }

                        // exifIFD is found
                    }
                    
                    Generic.debug("GPS Header");
                    if (gpsIFD != 0) {
                        currentIdxInBuffer = gpsIFD + tiffHeaderStart;
                        interoperabilityNumber = getShort2byte(buffer,
                                currentIdxInBuffer, bigEndian);
                        idx = interoperabilityNumber;
                        currentIdxInBuffer += 2;
                        while ((--idx >= 0) && ((currentIdxInBuffer + 12) < sz)) {
                            tag = getShort2byte(buffer, currentIdxInBuffer,
                                    bigEndian);
                            type = getShort2byte(buffer,
                                    currentIdxInBuffer + 2, bigEndian);
                            count = getLong4byte(buffer,
                                    currentIdxInBuffer + 4, bigEndian);
                            value = getLong4byte(buffer,
                                    currentIdxInBuffer + 8, bigEndian);
                            currentIdxInBuffer += 12;

                            Generic.debug("Tag:" + Convert.unsigned2hex(tag, 4)
                                    + " Type:" + Convert.unsigned2hex(type, 4)
                                    + " Count:"
                                    + Convert.unsigned2hex(count, 8)
                                    + " Value:"
                                    + Convert.unsigned2hex(value, 8));
                            switch (tag) {
                            case TAG_EXIF: // EXIF IFD Pointer
                                if (type == LONG && count == 1) {
                                    exifIFD = value;
                                }
                                break;
                            case TAG_GPSINFO:
                                if (type == LONG && count == 1) {
                                    gpsIFD = value;
                                }
                                break;
                            default:
                                break;
                            }
                        }

                        // exifIFD is found
                    }

                    // EXIF offset is given in IFD0
                    Generic.debug("EXIF read");
                }

            }
        }

    }

    final private int getShort2byte(final byte[] buffer, final int offset,
            final boolean bigEndian) {
        if (bigEndian) {
            return ((buffer[offset] & 0xFF) << 8) | (buffer[offset + 1] & 0xFF);
        } else {
            return ((buffer[offset + 1] & 0xFF) << 8) | (buffer[offset] & 0xFF);
        }
    }

    final private int getLong4byte(final byte[] buffer, final int offset,
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

}
