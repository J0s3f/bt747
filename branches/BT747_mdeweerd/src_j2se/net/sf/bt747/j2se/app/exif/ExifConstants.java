// ********************************************************************
// *** BT747 ***
// *** (c)2007-2008 Mario De Weerd ***
// *** seesite@bt747.org ***
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

/**
 * @author Mario
 * 
 */
public class ExifConstants {
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
    public static final int TAG_IMAGEWIDTH = 0x100;
    /** Image height ImageLength 257 101 M M M J */
    public static final int TAG_IMAGELENGTH = 0x101;
    /** Number of bits per component BitsPerSample 258 102 M M M J */
    public static final int TAG_BITSPERSAMPLE = 0x102;
    /** Compression scheme Compression 259 103 M M M J */
    public static final int TAG_COMPRESSION = 0x103;
    /** Pixel composition PhotometricInterpretation 262 106 M M M N */
    public static final int TAG_PHOTOMETRICINTERPRETATION = 0x106;
    /** Image title ImageDescription 270 10E R R R R */
    public static final int TAG_IMAGEDESCRIPTION = 0x10E;

    /** Manufacturer of image input */
    /** equipment Make 271 10F R R R R */
    public static final int TAG_MAKE = 0x10F;
    /** Model of image input equipment Model 272 110 R R R R */
    public static final int TAG_MODEL = 0x110;
    /** Image data location StripOffsets 273 111 M M M N */
    public static final int TAG_STRIPOFFSETS = 0x111;
    /** Orientation of image Orientation 274 112 R R R R */
    public static final int TAG_ORIENTATION = 0x112;
    /** Number of components SamplesPerPixel 277 115 M M M J */
    /** Number of rows per strip RowsPerStrip 278 116 M M M N */
    /** Bytes per compressed strip StripByteCounts 279 117 M M M N */
    /** Image resolution in width direction XResolution 282 11A M M M M */
    /** Image resolution in height direction YResolution 283 11B M M M M */
    /** Image data arrangement PlanarConfiguration 284 11C O M O J */
    /** Unit of X and Y resolution ResolutionUnit 296 128 M M M M */
    /** Transfer function TransferFunction 301 12D R R R R */
    /** Software used Software 305 131 O O O O */
    public static final int TAG_SOFTWARE = 0x131;
    /** File change date and time DateTime 306 132 R R R R */
    public static final int TAG_DATETIME = 0x132;
    /** Person who created the image Artist 315 13B O O O O */
    /** White point chromaticity WhitePoint 318 13E O O O O */
    /** Chromaticities of primaries PrimaryChromaticities 319 13F O O O O */
    /** Offset to JPEG SOI JPEGInterchangeFormat 513 201 N N N N */
    public static final int TAG_JPEGINTERCHANGEFORMAT = 0x201;
    /** Bytes of JPEG data JPEGInterchangeFormatLength 514 202 N N N N */
    public static final int TAG_JPEGINTERCHANGEFORMATLENGTH = 0x202;
    /** Color space transformation matrix */
    /** coefficients YCbCrCoefficients 529 211 N N O O */
    /** Subsampling ratio of Y to C YCbCrSubSampling 530 212 N N M J */
    /** Y and C positioning YCbCrPositioning 531 213 N N M M */
    /** Pair of black and white reference */
    /** values ReferenceBlackWhite 532 214 O O O O */
    /** Copyright holder Copyright 33432 8298 O O O O */
    public static final int TAG_COPYRIGHT = 0x8298;
    /** Exif tag Exif IFD Pointer 34665 8769 M M M M */
    public static final int TAG_EXIF = 0x8769;
    /** GPS tag GPSInfo IFD Pointer 34853 8825 O O O O */
    public static final int TAG_GPSINFO = 0x8825;
    /** Exposure time ExposureTime 33434 829A R R R R */
    /** F number FNumber 33437 829D O O O O */
    public static final int TAG_FNUMBER = 0x829D;
    /** Exposure program ExposureProgram 34850 8822 O O O O */
    /** Spectral sensitivity SpectralSensitivity 34852 8824 O O O O */
    /** ISO speed ratings ISOSpeedRatings 34855 8827 O O O O */
    /** Optoelectric coefficient OECF 34856 8828 O O O O */
    /** Exif Version ExifVersion 36864 9000 M M M M */
    public static final int TAG_EXIFVERSION = 0x9000;
    /** Date and time original image was */
    /** generated DateTimeOriginal 36867 9003 O O O O */
    public static final int TAG_DATETIMEORIGINAL = 0x9003;
    /** Date and time image was made */
    /** digital data DateTimeDigitized 36868 9004 O O O O */
    public static final int TAG_DATETIMEDIGITIZED = 0x9004;
    /** Meaning of each component ComponentsConfiguration 37121 9101 N N N M */
    public static final int TAG_COMPONENTSCONFIGURATION = 0x9101;
    /** Image compression mode CompressedBitsPerPixel 37122 9102 N N N O */
    /** Shutter speed ShutterSpeedValue 37377 9201 O O O O */
    /** Aperture ApertureValue 37378 9202 O O O O */
    public static final int TAG_APERTUREVALUE = 0x9202;
    /** Brightness BrightnessValue 37379 9203 O O O O */
    /** Exposure bias ExposureBiasValue 37380 9204 O O O O */
    /** Maximum lens aperture MaxApertureValue 37381 9205 O O O O */
    /** Subject distance SubjectDistance 37382 9206 O O O O */
    /** Metering mode MeteringMode 37383 9207 O O O O */
    /** Light source LightSource 37384 9208 O O O O */
    /** Flash Flash 37385 9209 R R R R */
    /** Lens focal length FocalLength 37386 920A O O O O */
    /** Subject area SubjectArea 37396 9214 O O O O */
    /** Manufacturer notes MakerNote 37500 927C O O O O */
    public static final int TAG_MAKERNOTE = 0x927C;
    /** User comments UserComment 37510 9286 O O O O */
    public static final int TAG_USERCOMMENT = 0x9286;
    /** DateTime subseconds SubSecTime 37520 9290 O O O O */
    public static final int TAG_SUBSECTIME = 0x9290;
    /** DateTimeOriginal subseconds SubSecTimeOriginal 37521 9291 O O O O */
    public static final int TAG_SUBSECTIMEORIGINAL = 0x9291;
    /** DateTimeDigitized subseconds SubSecTimeDigitized 37522 9292 O O O O */
    public static final int TAG_SUBSECTIMEDIGITIZED = 0x9292;
    /** Supported Flashpix version FlashpixVersion 40960 A000 M M M M */
    public static final int TAG_FLASHPIXVERSION = 0xA000;
    /** Color space information ColorSpace 40961 A001 M M M M */
    public static final int TAG_COLORSPACE = 0xA001;
    /** Valid image width PixelXDimension 40962 A002 N N N M */
    public static final int TAG_PIXELXDIMENSION = 0xA002;
    /** Valid image height PixelYDimension 40963 A003 N N N M */
    public static final int TAG_PIXELYDIMENSION = 0xA003;
    /** Related audio file RelatedSoundFile 40964 A004 O O O O */
    public static final int TAG_RELATEDSOUNDFILE = 0xA004;
    /** Interoperability tag Interoperability IFD Pointer 40965 A005 N N N O */
    public static final int TAG_INTEROPERABILITY_IFD_POINTER = 0xA005;
    /** Flash energy FlashEnergy 41483 A20B O O O O */
    /** Spatial frequency response SpatialFrequencyResponse 41484 A20C O O O O */
    /** Focal plane X resolution FocalPlaneXResolution 41486 A20E O O O O */
    /** Focal plane Y resolution FocalPlaneYResolution 41487 A20F O O O O */
    /** Focal plane resolution unit FocalPlaneResolutionUnit 41488 A210 O O O O */
    /** Subject location SubjectLocation 41492 A214 O O O O */
    /** Exposure index ExposureIndex 41493 A215 O O O O */
    /** Sensing method SensingMethod 41495 A217 O O O O */
    /** File source FileSource 41728 A300 O O O O */
    /** Scene type SceneType 41729 A301 O O O O */
    /** CFA pattern CFAPattern 41730 A302 O O O O */
    /** Custom image processing CustomRendered 41985 A401 O O O O */
    public static final int TAG_CUSTOMRENDERED = 0xA401;
    /** Exposure mode ExposureMode 41986 A402 R R R R */
    public static final int TAG_EXPOSUREMODE = 0xA402;
    /** White balance WhiteBalance 41987 A403 R R R R */
    public static final int TAG_WHITEBALANCE = 0xA403;
    /** Digital zoom ratio DigitalZoomRatio 41988 A404 O O O O */
    public static final int TAG_DIGITALZOOMRATIO = 0xA404;
    /** Focal length in 35 mm film FocalLengthIn35mmFilm 41989 A405 O O O O */
    public static final int TAG_FOCALLENGTHIN35MMFILM = 0xA405;
    /** Scene capture type SceneCaptureType 41990 A406 R R R R */
    public static final int TAG_SCENECAPTURETYPE = 0xA406;
    /** Gain control GainControl 41991 A407 O O O O */
    public static final int TAG_GAINCONTROL = 0xA406;
    /** Contrast Contrast 41992 A408 O O O O */
    /** Saturation Saturation 41993 A409 O O O O */
    /** Sharpness Sharpness 41994 A40A O O O O */
    /** Device settings description DeviceSettingDescription 41995 A40B O O O O */
    /** Subject distance range SubjectDistanceRange 41996 A40C O O O O */
    /** Unique image ID ImageUniqueID 42016 A420 O O O O */
    public static final int TAG_IMAGEUNIQUEID = 0xA420;

    /** A. Tags Relating to GPS IFD */
    /** GPS tag version GPSVersionID 0 0 BYTE 4 */
    public static final int TAG_GPSVERSIONID = 0x0000;
    /** North or South Latitude GPSLatitudeRef 1 1 ASCII 2 */
    public static final int TAG_GPSLATITUDEREF = 0x0001;
    /** Latitude GPSLatitude 2 2 RATIONAL 3 */
    public static final int TAG_GPSLATITUDE = 0x0002;
    /** East or West Longitude GPSLongitudeRef 3 3 ASCII 2 */
    public static final int TAG_GPSLONGITUDEREF = 0x0003;
    /** Longitude GPSLongitude 4 4 RATIONAL 3 */
    public static final int TAG_GPSLONGITUDE = 0x0004;
    /** Altitude reference GPSAltitudeRef 5 5 BYTE 1 */
    public static final int TAG_GPSALTITUDEREF = 0x0005;
    /** Altitude GPSAltitude 6 6 RATIONAL 1 */
    public static final int TAG_GPSALTITUDE = 0x0006;
    /** GPS time (atomic clock) GPSTimeStamp 7 7 RATIONAL 3 */
    public static final int TAG_GPSTIMESTAMP = 0x0007;
    /** GPS satellites used for measurement GPSSatellites 8 8 ASCII Any */
    public static final int TAG_GPSSATELLITES = 0x0008;
    /** GPS receiver status GPSStatus 9 9 ASCII 2 */
    public static final int TAG_GPSSTATUS = 0x0009;
    /** GPS measurement mode GPSMeasureMode 10 A ASCII 2 */
    public static final int TAG_GPSMEASUREMODE = 0x000A;
    /** Measurement precision GPSDOP 11 B RATIONAL 1 */
    public static final int TAG_GPSDOP = 0x000B;
    /** Speed unit GPSSpeedRef 12 C ASCII 2 */
    public static final int TAG_GPSSPEEDREF = 0x000C;
    /** Speed of GPS receiver GPSSpeed 13 D RATIONAL 1 */
    public static final int TAG_GPSSPEED = 0x000D;
    /** Reference for direction of movement GPSTrackRef 14 E ASCII 2 */
    public static final int TAG_GPSTRACKREF = 0x000E;
    /** Direction of movement GPSTrack 15 F RATIONAL 1 */
    public static final int TAG_GPSTRACK = 0x000F;
    /** Reference for direction of image GPSImgDirectionRef 16 10 ASCII 2 */
    public static final int TAG_GPSIMAGEDIRECTIONREF = 0x0010;
    /** Direction of image GPSImgDirection 17 11 RATIONAL 1 */
    public static final int TAG_GPSIMAGEDIRECTION = 0x0011;
    /** Geodetic survey data used GPSMapDatum 18 12 ASCII Any */
    public static final int TAG_GPSMAPDATUM = 0x0012;
    /** Reference for latitude of destination GPSDestLatitudeRef 19 13 ASCII 2 */
    public static final int TAG_GPSDESTLATITUTEREF = 0x0013;
    /** Latitude of destination GPSDestLatitude 20 14 RATIONAL 3 */
    public static final int TAG_GPSDESTLATITUDE = 0x0014;
    /**
     * Reference for longitude of destination GPSDestLongitudeRef 21 15 ASCII
     * 2
     */
    public static final int TAG_GPSDESTLONGITUDEREF = 0x0015;
    /** Longitude of destination GPSDestLongitude 22 16 RATIONAL 3 */
    public static final int TAG_GPSDESTLONGITUDE = 0x0016;
    /** Reference for bearing of destination GPSDestBearingRef 23 17 ASCII 2 */
    public static final int TAG_GPSDESTBEARINGREF = 0x0017;
    /** Bearing of destination GPSDestBearing 24 18 RATIONAL 1 */
    public static final int TAG_GPSDESTBEARING = 0x0018;
    /** Reference for distance to destination GPSDestDistanceRef 25 19 ASCII 2 */
    public static final int TAG_GPSDESTDISTANCREF = 0x0019;
    /** Distance to destination GPSDestDistance 26 1A RATIONAL 1 */
    public static final int TAG_GPSDESTDISTANCE = 0x001A;
    /** Name of GPS processing method GPSProcessingMethod 27 1B UNDEFINED Any */
    public static final int TAG_GPSPROCESSINGMETHOD = 0x001B;
    /** Name of GPS area GPSAreaInformation 28 1C UNDEFINED Any */
    public static final int TAG_GPSAREAINFORMATION = 0x001C;
    /** GPS date GPSDateStamp 29 1D ASCII 11 */
    public static final int TAG_GPSDATESTAMP = 0x001D;
    /** GPS differential correction GPSDifferential 30 1E SHORT 1 */
    public static final int TAG_GPSDIFFERENTIAL = 0x001E;

    
    
    /**
     * EXIF Field types
     * 
     */

    public static final int BYTE = 1;
    public static final int ASCII = 2;
    public static final int SHORT = 3;
    public static final int LONG = 4; // 4 bytes
    public static final int RATIONAL = 5; // First long is numerator, second
    // is denominator
    public static final int SBYTE = 6;
    public static final int UNDEFINED = 7; // 9 bit byte that can take any
    // value depending on field
    // def.
    public static final int SSHORT = 8;
    public static final int SLONG = 9; // 4 byte signed integer
    public static final int SRATIONAL = 10; // First long is numerator, second
    // is denominator
    public static final int FLOAT = 11;
    public static final int DOUBLE = 12;
    public static final int IFDBLOCK = 13; // Seems to be Olympus standard // Could be other too

    /**
     * IFD Structure<br>
     * Bytes 0-1 Tag<br>
     * Bytes 2-3 Type<br>
     * Bytes 4-7 Count<br>
     * Bytes 8-11 Value Offset
     */
}
