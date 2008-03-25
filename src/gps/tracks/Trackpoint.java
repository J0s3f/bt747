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
    }
	public Trackpoint(final double lat, final double lon, final double altitude) {
        this.latDouble = lat;
        this.lonDouble = lon;
        this.altitude  = altitude;
    }

    public final void setLatDouble(final double latDouble) {
        this.latDouble = latDouble;
    }

    public final void setLonDouble(final double lonDouble) {
        this.lonDouble = lonDouble;
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
