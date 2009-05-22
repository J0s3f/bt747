// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.convert;

/**
 * Implement some conversion functions
 * 
 * @author Mario De Weerd
 */
public final class Conv {
    /**
     * Convert a string in hexadecimal to a list of bytes
     * 
     * @param hexStr
     *                Hexadecimal representation of bytes
     * @return list of bytes
     */
    public static final int hexStringToBytes(final String hexStr,
            final byte[] buffer) {
        final int length = hexStr.length() & 0xFFFFFFFE;
        byte byteVal;
        for (int i = 0; i < length; i += 2) {
            switch (hexStr.charAt(i)) {
            case '0':
                byteVal = (byte) 0x00;
                break;
            case '1':
                byteVal = (byte) 0x10;
                break;
            case '2':
                byteVal = (byte) 0x20;
                break;
            case '3':
                byteVal = (byte) 0x30;
                break;
            case '4':
                byteVal = (byte) 0x40;
                break;
            case '5':
                byteVal = (byte) 0x50;
                break;
            case '6':
                byteVal = (byte) 0x60;
                break;
            case '7':
                byteVal = (byte) 0x70;
                break;
            case '8':
                byteVal = (byte) 0x80;
                break;
            case '9':
                byteVal = (byte) 0x90;
                break;
            case 'a':
            case 'A':
                byteVal = (byte) 0xA0;
                break;
            case 'b':
            case 'B':
                byteVal = (byte) 0xB0;
                break;
            case 'c':
            case 'C':
                byteVal = (byte) 0xC0;
                break;
            case 'd':
            case 'D':
                byteVal = (byte) 0xD0;
                break;
            case 'e':
            case 'E':
                byteVal = (byte) 0xE0;
                break;
            case 'f':
            case 'F':
                byteVal = (byte) 0xF0;
                break;
            default:
                byteVal = 0;
            }
            switch (hexStr.charAt(i + 1)) {
            case '0':
                byteVal |= (byte) 0;
                break;
            case '1':
                byteVal |= (byte) 1;
                break;
            case '2':
                byteVal |= (byte) 2;
                break;
            case '3':
                byteVal |= (byte) 3;
                break;
            case '4':
                byteVal |= (byte) 4;
                break;
            case '5':
                byteVal |= (byte) 5;
                break;
            case '6':
                byteVal |= (byte) 6;
                break;
            case '7':
                byteVal |= (byte) 7;
                break;
            case '8':
                byteVal |= (byte) 8;
                break;
            case '9':
                byteVal |= (byte) 9;
                break;
            case 'a':
            case 'A':
                byteVal |= (byte) 0xA;
                break;
            case 'b':
            case 'B':
                byteVal |= (byte) 0xB;
                break;
            case 'c':
            case 'C':
                byteVal |= (byte) 0xC;
                break;
            case 'd':
            case 'D':
                byteVal |= (byte) 0xD;
                break;
            case 'e':
            case 'E':
                byteVal |= (byte) 0xE;
                break;
            case 'f':
            case 'F':
                byteVal |= (byte) 0xF;
                break;
            default:
                break;
            }
            buffer[i >> 1] = byteVal;
        }

        return length;
    }

    /**
     * Convert a string in hexadecimal to the corresponding int.
     * 
     * @param hexStr
     *                Hexadecimal representation of bytes
     * @return list of bytes
     */
    public static final int hex2Int(final String hexStr) {
        final int length = hexStr.length();
        int result = 0;
        for (int i = 0; i < length; i++) {
            result <<= 4;
            switch (hexStr.charAt(i)) {
            case '0':
                result += 0;
                break;
            case '1':
                result += 1;
                break;
            case '2':
                result += 2;
                break;
            case '3':
                result += 3;
                break;
            case '4':
                result += 4;
                break;
            case '5':
                result += 5;
                break;
            case '6':
                result += 6;
                break;
            case '7':
                result += 7;
                break;
            case '8':
                result += 8;
                break;
            case '9':
                result += 9;
                break;
            case 'a':
            case 'A':
                result += 0xA;
                break;
            case 'b':
            case 'B':
                result += 0xB;
                break;
            case 'c':
            case 'C':
                result += 0xC;
                break;
            case 'd':
            case 'D':
                result += 0xD;
                break;
            case 'e':
            case 'E':
                result += 0xE;
                break;
            case 'f':
            case 'F':
                result += 0xF;
                break;
            default:
                result += 0;
            }
        }
        return result;
    }

    /**
     * Convert a string in hexadecimal to the corresponding int. Consider hex
     * signed.
     * 
     * 
     * @param hexStr
     *                Hexadecimal representation of bytes
     * @return list of bytes
     */
    public static final int hex2SignedInt(final String hexStr) {
        int result = Conv.hex2Int(hexStr);
        if (result > 0) {
            final int highNibble = (result >> (4 * (hexStr.length() - 1)));
            if (highNibble >= 8) {
                // sign extension
                result |= (-1 << (32 - 4 * hexStr.length()));
            }
        }
        return result;
    }

    // The following license applies to the bilinear and wgs84_separation
    // methods that were transformed from code in the GPSD project.
    // Originally: Geoid separation code by Oleg Gusev, from data by Peter
    // Dana.

    // BSD LICENSE
    //
    // The GPSD code is Copyright (c) 1997, 1998, 1999, 2000, 2001, 2002 by
    // Remco Treffkorn. Portions of it are also Copyright (c) 2005 by Eric S.
    // Raymond. All rights reserved.
    //
    // Redistribution and use in source and binary forms, with or without
    // modification, are permitted provided that the following conditions
    // are met:<P>
    //
    // Redistributions of source code must retain the above copyright
    // notice, this list of conditions and the following disclaimer.<P>
    //
    // Redistributions in binary form must reproduce the above copyright
    // notice, this list of conditions and the following disclaimer in the
    // documentation and/or other materials provided with the distribution.<P>
    //
    // Neither name of the GPSD project nor the names of its contributors
    // may be used to endorse or promote products derived from this software
    // without specific prior written permission.
    //
    // THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    // ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    // LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    // A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
    // CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
    // EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
    // PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
    // PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    // LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    // NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    // SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
    //

    public static final double bilinear(final double x1, final double y1,
            final double x2, final double y2, final double x, final double y,
            final double z11, final double z12, final double z21,
            final double z22) {
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

        return (z22 * (y - y1) * (x - x1) + z12 * (y2 - y) * (x - x1) + z21
                * (y - y1) * (x2 - x) + z11 * (y2 - y) * (x2 - x))
                / delta;
    }

    /** The class to use for geoid calculation. */
    private static GeoidIF geoid = Geoid.getInstance();

    /**
     * Set the class to use for geoid calculation.
     * 
     * @param geoidIF
     */
    public static final void setGeiodIF(final GeoidIF geoidIF) {
        Conv.geoid = geoidIF;
    }

    /*
     * return geoid separation (MSL - WGS84) in meters, given a lat/lot in
     * degrees
     */
    /* @ +charint @ */
    public static final double wgs84Separation(final double lat,
            final double lon) {
        return geoid.wgs84Separation(lat, lon);
    }

    // Lifted from kismet
    //
    // Faust Code to convert rad to deg and find the distance between two
    // points
    // on the globe. Thanks, Faust.
    // const float M_PI = 3.14159;

    public static final double rad2deg(final double x) { /* FOLD00 */
        return x * Math.PI / 180.0;
    }

    public static final double earthDistance(final double lat1,
            final double lon1, final double lat2, final double lon2) { /* FOLD00 */

        /*
         * double calcedR1 = calcR(lat1); double calcedR2 = calcR(lat2);
         * 
         * double sinradi1 = sin(rad2deg(90-lat1)); double sinradi2 =
         * sin(rad2deg(90-lat2));
         * 
         * double x1 = calcedR1 * cos(rad2deg(lon1)) * sinradi1; double x2 =
         * calcedR2 * cos(rad2deg(lon2)) * sinradi2; double y1 = calcedR1 *
         * sin(rad2deg(lon1)) * sinradi1; double y2 = calcedR2 *
         * sin(rad2deg(lon2)) * sinradi2; double z1 = calcedR1 *
         * cos(rad2deg(90-lat1)); double z2 = calcedR2 *
         * cos(rad2deg(90-lat2));
         * 
         * double calcedR = calcR((double)(lat1+lat2)) / 2; double a =
         * acos((x1*x2 + y1*y2 + z1*z2)/square(calcedR));
         */

        final double x1 = Conv.calcR(lat1) * Math.cos(Conv.rad2deg(lon1))
                * Math.sin(Conv.rad2deg(90 - lat1));
        final double x2 = Conv.calcR(lat2) * Math.cos(Conv.rad2deg(lon2))
                * Math.sin(Conv.rad2deg(90 - lat2));
        final double y1 = Conv.calcR(lat1) * Math.sin(Conv.rad2deg(lon1))
                * Math.sin(Conv.rad2deg(90 - lat1));
        final double y2 = Conv.calcR(lat2) * Math.sin(Conv.rad2deg(lon2))
                * Math.sin(Conv.rad2deg(90 - lat2));
        final double z1 = Conv.calcR(lat1)
                * Math.cos(Conv.rad2deg(90 - lat1));
        final double z2 = Conv.calcR(lat2)
                * Math.cos(Conv.rad2deg(90 - lat2));
        final double a = bt747.sys.Generic
                .acos(((x1 * x2) + (y1 * y2) + (z1 * z2))
                        / bt747.sys.Generic.pow(
                                Conv.calcR((lat1 + lat2) / 2), 2));

        return Conv.calcR((lat1 + lat2) / 2) * a;
    }

    // Lifted from gpsdrive 1.7
    // CalcR gets the radius of the earth at a particular latitude
    // calcxy finds the x and y positions on a 1280x1024 image of a certain
    // scale
    // centered on a given lat/lon.

    // This pulls the "real radius" of a lat, instead of a global estimate
    public static final double calcR(final double p_lat) /* FOLD00 */
    {
        final double a = 6378.137;
        double r, sc, x, y, z;
        final double e2 = 0.081082 * 0.081082;
        double lat;
        /*
         * the radius of curvature of an ellipsoidal Earth in the plane of the
         * meridian is given by
         * 
         * R' = a * (1 - e^2) / (1 - e^2 * (sin(lat))^2)^(3/2)
         * 
         * where a is the equatorial radius, b is the polar radius, and e is
         * the eccentricity of the ellipsoid = sqrt(1 - b^2/a^2)
         * 
         * a = 6378 km (3963 mi) Equatorial radius (surface to center
         * distance) b = 6356.752 km (3950 mi) Polar radius (surface to center
         * distance) e = 0.081082 Eccentricity
         */

        lat = p_lat * Math.PI / 180.0;
        sc = Math.sin(lat);
        x = a * (1.0 - e2);
        z = 1.0 - e2 * sc * sc;
        y = bt747.sys.Generic.pow(z, 1.5);
        r = x / y;

        r = r * 1000.0;
        return r;
    }
}
