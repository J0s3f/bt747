/**
 * Reimplementation of Mark McClures Javascript PolylineEncoder
 * All the mathematical logic is more or less copied by McClure
 *  
 * @author Mark Rambow
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
        this.latDouble = lat;
        this.lonDouble = lon;
        if (latDouble > 90.0 || latDouble < -90.0) {
            this.latDouble = 0.0;
        }
        if (lonDouble > 180.0 || latDouble < -180.0) {
            this.lonDouble = 0.0;
        }
    }

    public Trackpoint(
            final double lat,
            final double lon,
            final double altitude) {
        this.latDouble = lat;
        this.lonDouble = lon;
        this.altitude = altitude;
        if (latDouble > 90.0 || latDouble < -90.0) {
            this.latDouble = 0.0;
        }
        if (lonDouble > 180.0 || latDouble < -180.0) {
            this.lonDouble = 0.0;
        }
    }

    public final void setLatDouble(final double latDouble) {
        this.latDouble = latDouble;
        if (latDouble > 90.0 || latDouble < -90.0) {
            this.latDouble = 0.0;
        }
    }

    public final void setLonDouble(final double lonDouble) {
        this.lonDouble = lonDouble;
        if (lonDouble > 180.0 || latDouble < -180.0) {
            this.lonDouble = 0.0;
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
