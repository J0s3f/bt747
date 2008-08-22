//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              
package gps.convert;


/**
 * Implement some conversion functions
 * 
 * @author Mario De Weerd
 */
public final class Conv {
    /** Convert a string in hexadecimal to a list of bytes
     * 
     * @param hexStr Hexadecimal representation of bytes
     * @return list of bytes 
     */
    public static int hexStringToBytes(
            final String hexStr,
            final byte[] buffer) {
        char[] data = hexStr.toCharArray();
        int length = data.length & 0xFFFFFFFE;
        byte byteVal;
        for (int i = 0; i < length; i += 2) {
            switch (data[i]) {
                case '0': byteVal = (byte) 0x00; break;
                case '1': byteVal = (byte) 0x10; break;
                case '2': byteVal = (byte) 0x20; break;
                case '3': byteVal = (byte) 0x30; break;
                case '4': byteVal = (byte) 0x40; break;
                case '5': byteVal = (byte) 0x50; break;
                case '6': byteVal = (byte) 0x60; break;
                case '7': byteVal = (byte) 0x70; break;
                case '8': byteVal = (byte) 0x80; break;
                case '9': byteVal = (byte) 0x90; break;
                case 'A': byteVal = (byte) 0xA0; break;
                case 'B': byteVal = (byte) 0xB0; break;
                case 'C': byteVal = (byte) 0xC0; break;
                case 'D': byteVal = (byte) 0xD0; break;
                case 'E': byteVal = (byte) 0xE0; break;
                case 'F': byteVal = (byte) 0xF0; break;
                case 'a': byteVal = (byte) 0xA0; break;
                case 'b': byteVal = (byte) 0xB0; break;
                case 'c': byteVal = (byte) 0xC0; break;
                case 'd': byteVal = (byte) 0xD0; break;
                case 'e': byteVal = (byte) 0xE0; break;
                case 'f': byteVal = (byte) 0xF0; break;
                default: byteVal = 0;
            }
            switch (data[i + 1]) {
                case '0': byteVal |= (byte) 0; break;
                case '1': byteVal |= (byte) 1; break;
                case '2': byteVal |= (byte) 2; break;
                case '3': byteVal |= (byte) 3; break;
                case '4': byteVal |= (byte) 4; break;
                case '5': byteVal |= (byte) 5; break;
                case '6': byteVal |= (byte) 6; break;
                case '7': byteVal |= (byte) 7; break;
                case '8': byteVal |= (byte) 8; break;
                case '9': byteVal |= (byte) 9; break;
                case 'A': byteVal |= (byte) 0xA; break;
                case 'B': byteVal |= (byte) 0xB; break;
                case 'C': byteVal |= (byte) 0xC; break;
                case 'D': byteVal |= (byte) 0xD; break;
                case 'E': byteVal |= (byte) 0xE; break;
                case 'F': byteVal |= (byte) 0xF; break;
                case 'a': byteVal |= (byte) 0xA; break;
                case 'b': byteVal |= (byte) 0xB; break;
                case 'c': byteVal |= (byte)0xC; break;
                case 'd': byteVal |= (byte)0xD; break;
                case 'e': byteVal |= (byte)0xE; break;
                case 'f': byteVal |= (byte)0xF; break;
                default:
                    break;
            }
            buffer[i >> 1] = byteVal;
        }

        return length;
    }
    
    /** Convert a string in hexadecimal to the corresponding int
     * 
     * @param hexStr Hexadecimal representation of bytes
     * @return list of bytes 
     */
    public static int hex2Int(final String hexStr) {
        int result = 0;
        for (int i = 0; i < hexStr.length(); i++) {
            int nibble;
            switch (hexStr.charAt(i)) {
                case '0': nibble = 0; break;
                case '1': nibble = 1; break;
                case '2': nibble = 2; break;
                case '3': nibble = 3; break;
                case '4': nibble = 4; break;
                case '5': nibble = 5; break;
                case '6': nibble = 6; break;
                case '7': nibble = 7; break;
                case '8': nibble = 8; break;
                case '9': nibble = 9; break;
                case 'A': nibble = 0xA; break;
                case 'B': nibble = 0xB; break;
                case 'C': nibble = 0xC; break;
                case 'D': nibble = 0xD; break;
                case 'E': nibble = 0xE; break;
                case 'F': nibble = 0xF; break;
                case 'a': nibble = 0xA; break;
                case 'b': nibble = 0xB; break;
                case 'c': nibble = 0xC; break;
                case 'd': nibble = 0xD; break;
                case 'e': nibble = 0xE; break;
                case 'f': nibble = 0xF; break;
                default: nibble = 0;
            }
            result <<= 4;
            result += nibble;
        }
        return result;
    }

// The following license applies to the bilinear and wgs84_separation
// methods that were transformed from code in the GPSD project.
// Originally: Geoid separation code by Oleg Gusev, from data by Peter Dana.
   
    
//    BSD LICENSE
//
//    The GPSD code is Copyright (c) 1997, 1998, 1999, 2000, 2001, 2002 by
//    Remco Treffkorn. Portions of it are also Copyright (c) 2005 by Eric S.
//    Raymond. All rights reserved.
//
//    Redistribution and use in source and binary forms, with or without
//    modification, are permitted provided that the following conditions
//    are met:<P>
//
//    Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.<P>
//
//    Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.<P>
//
//    Neither name of the GPSD project nor the names of its contributors
//    may be used to endorse or promote products derived from this software
//    without specific prior written permission.
//
//    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//    ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
//    A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
//    CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
//    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
//    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
//    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
//    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

    private static double bilinear(
            final double x1,
            final double y1,
            final double x2,
            final double y2,
            final double x,
            final double y,
            final double z11,
            final double z12,
            final double z21,
            final double z22
            ) {
        double delta;
        
        if ((y1 == y2) && (x1 == x2)) {
            return (z11);
        }
        if ((y1 == y2) && (x1 != x2)) {
            return (z22 * (x - x1) + z11 * (x2 - x)) / (x2 - x1);
        }
        if ((x1 == x2) && (y1 != y2)) {
            return (z22 * (y - y1) + z11 * (y2 - y)) / (y2 - y1);
        }
        
        delta = (y2 - y1) * (x2 - x1);
        
        return (z22 * (y - y1) * (x - x1)
                + z12 * (y2 - y) * (x - x1)
                + z21 * (y  - y1) * (x2 - x)
                + z11 * (y2 - y) *( x2 - x)
               )
                / delta;
    }

    
    private static final int GEOID_ROW = 19;
    private static final int GEOID_COL = 37;
    private static final int[] geoid_delta = {
            /* 90S */ -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30,  -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, -30, 
            /* 80S */ -53, -54, -55, -52, -48, -42, -38, -38, -29, -26, -26, -24, -23, -21, -19, -16, -12,  -8,  -4,  -1,   1,   4,   4,   6,   5,   4,    2,  -6, -15, -24, -33, -40, -48, -50, -53, -52, -53, 
            /* 70S */ -61, -60, -61, -55, -49, -44, -38, -31, -25, -16,  -6,   1,   4,   5,   4,   2,   6,  12,  16,  16,  17,  21,  20,  26,  26,  22,   16,  10,  -1, -16, -29, -36, -46, -55, -54, -59, -61, 
            /* 60S */ -45, -43, -37, -32, -30, -26, -23, -22, -16, -10,  -2,  10,  20,  20,  21,  24,  22,  17,  16,  19,  25,  30,  35,  35,  33,  30,   27,  10,  -2, -14, -23, -30, -33, -29, -35, -43, -45, 
            /* 50S */ -15, -18, -18, -16, -17, -15, -10, -10,  -8,  -2,   6,  14,  13,   3,   3,  10,  20,  27,  25,  26,  34,  39,  45,  45,  38,  39,   28,  13,  -1, -15, -22, -22, -18, -15, -14, -10, -15, 
            /* 40S */  21,   6,   1,  -7, -12, -12, -12, -10,  -7,  -1,   8,  23,  15,  -2,  -6,   6,  21,  24,  18,  26,  31,  33,  39,  41,  30,  24,   13,  -2, -20, -32, -33, -27, -14,  -2,   5,  20,  21, 
            /* 30S */  46,  22,   5,  -2,  -8, -13, -10,  -7,  -4,   1,   9,  32,  16,   4,  -8,   4,  12,  15,  22,  27,  34,  29,  14,  15,  15,   7,   -9, -25, -37, -39, -23, -14,  15,  33,  34,  45,  46, 
            /* 20S */  51,  27,  10,   0,  -9, -11,  -5,  -2,  -3,  -1,   9,  35,  20,  -5,  -6,  -5,   0,  13,  17,  23,  21,   8,  -9, -10, -11, -20,  -40, -47, -45, -25,   5,  23,  45,  58,  57,  63,  51, 
            /* 10S */  36,  22,  11,   6,  -1,  -8, -10,  -8, -11,  -9,   1,  32,   4, -18, -13,  -9,   4,  14,  12,  13,  -2, -14, -25, -32, -38, -60,  -75, -63, -26,   0,  35,  52,  68,  76,  64,  52,  36, 
            /* 00N */  22,  16,  17,  13,   1, -12, -23, -20, -14,  -3,  14,  10, -15, -27, -18,   3,  12,  20,  18,  12, -13,  -9, -28, -49, -62, -89, -102, -63,  -9,  33,  58,  73,  74,  63,  50,  32,  22, 
            /* 10N */  13,  12,  11,   2, -11, -28, -38, -29, -10,   3,   1, -11, -41, -42, -16,   3,  17,  33,  22,  23,   2,  -3,  -7, -36, -59, -90,  -95, -63, -24,  12,  53,  60,  58,  46,  36,  26,  13, 
            /* 20N */   5,  10,   7,  -7, -23, -39, -47, -34,  -9, -10, -20, -45, -48, -32,  -9,  17,  25,  31,  31,  26,  15,   6,   1, -29, -44, -61,  -67, -59, -36, -11,  21,  39,  49,  39,  22,  10,   5, 
            /* 30N */  -7,  -5,  -8, -15, -28, -40, -42, -29, -22, -26, -32, -51, -40, -17,  17,  31,  34,  44,  36,  28,  29,  17,  12, -20, -15, -40,  -33, -34, -34, -28,   7,  29,  43,  20,   4,  -6,  -7, 
            /* 40N */ -12, -10, -13, -20, -31, -34, -21, -16, -26, -34, -33, -35, -26,   2,  33,  59,  52,  51,  52,  48,  35,  40,  33,  -9, -28, -39,  -48, -59, -50, -28,   3,  23,  37,  18,  -1, -11, -12, 
            /* 50N */  -8,   8,   8,   1, -11, -19, -16, -18, -22, -35, -40, -26, -12,  24,  45,  63,  62,  59,  47,  48,  42,  28,  12, -10, -19, -33,  -43, -42, -43, -29,  -2,  17,  23,  22,   6,   2,  -8, 
            /* 60N */   2,   9,  17,  10,  13,   1, -14, -30, -39, -46, -42, -21,   6,  29,  49,  65,  60,  57,  47,  41,  21,  18,  14,   7,  -3, -22,  -29, -32, -32, -26, -15,  -2,  13,  17,  19,   6,   2, 
            /* 70N */   2,   2,   1,  -1,  -3,  -7, -14, -24, -27, -25, -19,   3,  24,  37,  47,  60,  61,  58,  51,  43,  29,  20,  12,   5,  -2, -10,  -14, -12, -10, -14, -12,  -6,  -2,   3,   6,   4,   2, 
            /* 80N */   3,   1,  -2,  -3,  -3,  -3,  -1,   3,   1,   5,   9,  11,  19,  27,  31,  34,  33,  34,  33,  34,  28,  23,  17,  13,   9,   4,    4,   1,  -2,  -2,   0,   2,   3,   2,   1,   1,   3, 
            /* 90N */  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13,   13,  13,  13,  13,  13,  13,  13,  13,  13,  13,  13};

    /* return geoid separtion (MSL - WGS84) in meters, given a lat/lot in degrees */
    /*@ +charint @*/
    public static double wgs84Separation(final double lat, final double lon)
    {
    /*@ -charint @*/
        int ilat, ilon;
        int ilat1, ilat2, ilon1, ilon2;

        ilat = (int) Math.floor(( 90. + lat) / 10);
        ilon = (int) Math.floor((180. + lon) / 10);

        ilat1 = ilat;
        ilon1 = ilon;
        ilat2 = (ilat < GEOID_ROW-1) ? (ilat + 1) : ilat;
        ilon2 = (ilon < GEOID_COL-1) ? (ilon + 1) : ilon;

        try {
            return bilinear(
                    (ilon1 * 10.) - 180., (ilat1 * 10.) - 90.,
                    (ilon2 * 10.) - 180., (ilat2 * 10.) - 90.,
                    lon,           lat,
                    (double)geoid_delta[ilon1 + (ilat1 * GEOID_COL)],
                    (double)geoid_delta[ilon2 + (ilat1 * GEOID_COL)],
                    (double)geoid_delta[ilon1 + (ilat2 * GEOID_COL)],
                    (double)geoid_delta[ilon2 + (ilat2 * GEOID_COL)]
            );
        } catch (Exception e) {
            return -999;
        }
    }

    

    // Lifted from kismet
    //
    //  Faust Code to convert rad to deg and find the distance between two points
    //  on the globe.  Thanks, Faust.
    // const float M_PI = 3.14159;
    
    public static double rad2deg(final double x) { /*FOLD00*/
          return x * Math.PI / 180.0;
    }
    
    public static double earthDistance(
            final double lat1,
            final double lon1,
            final double lat2,
            final double lon2) { /*FOLD00*/
        
        /*
         double calcedR1 = calcR(lat1);
         double calcedR2 = calcR(lat2);
         
         double sinradi1 = sin(rad2deg(90-lat1));
         double sinradi2 = sin(rad2deg(90-lat2));
         
         double x1 = calcedR1 * cos(rad2deg(lon1)) * sinradi1;
         double x2 = calcedR2 * cos(rad2deg(lon2)) * sinradi2;
         double y1 = calcedR1 * sin(rad2deg(lon1)) * sinradi1;
         double y2 = calcedR2 * sin(rad2deg(lon2)) * sinradi2;
         double z1 = calcedR1 * cos(rad2deg(90-lat1));
         double z2 = calcedR2 * cos(rad2deg(90-lat2));
         
         double calcedR = calcR((double)(lat1+lat2)) / 2;
         double a = acos((x1*x2 + y1*y2 + z1*z2)/square(calcedR));
         */
        
        double x1 = calcR(lat1) * Math.cos(rad2deg(lon1)) * Math.sin(rad2deg(90 - lat1));
        double x2 = calcR(lat2) * Math.cos(rad2deg(lon2)) * Math.sin(rad2deg(90 - lat2));
        double y1 = calcR(lat1) * Math.sin(rad2deg(lon1)) * Math.sin(rad2deg(90 - lat1));
        double y2 = calcR(lat2) * Math.sin(rad2deg(lon2)) * Math.sin(rad2deg(90 - lat2));
        double z1 = calcR(lat1) * Math.cos(rad2deg(90 - lat1));
        double z2 = calcR(lat2) * Math.cos(rad2deg(90 - lat2));
        double a = bt747.generic.Generic.acos(((x1 * x2) + (y1 * y2) + (z1 * z2))/
                bt747.generic.Generic.pow(calcR((double) (lat1 + lat2) / 2),2));
        
        return calcR((double) (lat1 + lat2) / 2) * a;
    }

    

    //  Lifted from gpsdrive 1.7
    //  CalcR gets the radius of the earth at a particular latitude
    //  calcxy finds the x and y positions on a 1280x1024 image of a certain scale
    //   centered on a given lat/lon.
    
    //  This pulls the "real radius" of a lat, instead of a global guesstimate
    public static double calcR (final double p_lat) /*FOLD00*/
    {
        double a = 6378.137, r, sc, x, y, z;
        double e2 = 0.081082 * 0.081082;
        double lat;
        /*
         the radius of curvature of an ellipsoidal Earth in the plane of the
         meridian is given by
         
         R' = a * (1 - e^2) / (1 - e^2 * (sin(lat))^2)^(3/2)
         
         where a is the equatorial radius,
         b is the polar radius, and
         e is the eccentricity of the ellipsoid = sqrt(1 - b^2/a^2)
         
         a = 6378 km (3963 mi) Equatorial radius (surface to center distance)
         b = 6356.752 km (3950 mi) Polar radius (surface to center distance)
         e = 0.081082 Eccentricity
         */
        
        lat = p_lat * Math.PI / 180.0;
        sc = Math.sin(lat);
        x = a * (1.0 - e2);
        z = 1.0 - e2 * sc * sc;
        y = bt747.generic.Generic.pow(z, 1.5);
        r = x / y;
        
        r = r * 1000.0;
        return r;
    }

    public static int dateToUTCepoch1970(final bt747.util.Date d) {
        return d.dateToUTCepoch1970();
    }

}
