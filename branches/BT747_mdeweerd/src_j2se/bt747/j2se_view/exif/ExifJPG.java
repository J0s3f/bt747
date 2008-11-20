/**
 * 
 */
package bt747.j2se_view.exif;

/**
 * @author Mario De Weerd
 * 
 * Reading and writing the relevant EXIF data should not be too complicated.
 * 
 * Strategy is to read the header and the EXIF data.  Reading first 64kB + SOI size bytes
 * is sufficient.
 * 
 * EXIF data must be stored in a manner that allows reconstruction later.
 * 
 * Writing Exif data consists in replacing the APP1 Block and updating the SOI
 * Marker.
 * 
 * Specification at http://www.exif.org/Exif2-2.PDF
 */
public class ExifJPG {
    /** Follows SOI (Start Of Image) Marker<br>
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
    int test;
}
