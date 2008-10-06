/**
 * Reimplementation of Mark McClures Javascript PolylineEncoder
 * All the mathematical logic is more or less copied by McClure
 *  
 * @author Mark Rambow, Mario De Weerd
 * @e-mail markrambow[at]gmail[dot]com
 * @version 0.1
 * 
 */

package gps.tracks;

public final class Trackpoint {
    private double latDouble;
    private double lonDouble;
    private double altitude;

    public Trackpoint(final double lat, final double lon) {
        setLatDouble(lat);
        setLonDouble(lon);
    }

    public Trackpoint(
            final double lat,
            final double lon,
            final double altitude) {
        setLatDouble(lat);
        setLonDouble(lon);
        this.altitude = altitude;
    }

    public final void setLatDouble(final double latDouble) {
        if (latDouble > 90.0 || latDouble < -90.0) {
            this.latDouble = 0.0;
        } else {
            this.latDouble = latDouble;
        }
    }

    public final void setLonDouble(final double lonDouble) {
        if (lonDouble > 180.0 || latDouble < -180.0) {
            this.lonDouble = 0.0;
        } else {
            this.lonDouble = lonDouble;
        }
    }

    public final double getLatDouble() {
        return latDouble;
    }

    public final double getLonDouble() {
        return lonDouble;
    }

    public final double getAltitude() {
        return altitude;
    }

    public final void setAltitude(final double altitude) {
        this.altitude = altitude;
    }

}
