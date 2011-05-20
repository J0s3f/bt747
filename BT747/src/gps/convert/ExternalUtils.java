package gps.convert;

/**
 * This file groups several utility functions that were taken from other
 * projects. Only the GNU Public License applies to these functions in the BT747
 * project.
 */
public class ExternalUtils {

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

	/*
	 * return geoid separation (MSL - WGS84) in meters, given a lat/lot in
	 * degrees
	 */
	/* @ +charint @ */
	public static final double wgs84Separation(final double lat,
			final double lon) {
		return geoid.wgs84Separation(lat, lon);
	}

	/**
	 * Set the class to use for geoid calculation.
	 * 
	 * @param geoidIF
	 */
	public static final void setGeoidIF(final GeoidIF geoidIF) {
		ExternalUtils.geoid = geoidIF;
	}

	/** The class to use for geoid calculation. */
	private static GeoidIF geoid = Geoid.getInstance();

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
		 * cos(rad2deg(90-lat1)); double z2 = calcedR2 * cos(rad2deg(90-lat2));
		 * 
		 * double calcedR = calcR((double)(lat1+lat2)) / 2; double a =
		 * acos((x1*x2 + y1*y2 + z1*z2)/square(calcedR));
		 */

		final double x1 = ExternalUtils.calcR(lat1)
				* Math.cos(ExternalUtils.rad2deg(lon1))
				* Math.sin(ExternalUtils.rad2deg(90 - lat1));
		final double x2 = ExternalUtils.calcR(lat2)
				* Math.cos(ExternalUtils.rad2deg(lon2))
				* Math.sin(ExternalUtils.rad2deg(90 - lat2));
		final double y1 = ExternalUtils.calcR(lat1)
				* Math.sin(ExternalUtils.rad2deg(lon1))
				* Math.sin(ExternalUtils.rad2deg(90 - lat1));
		final double y2 = ExternalUtils.calcR(lat2)
				* Math.sin(ExternalUtils.rad2deg(lon2))
				* Math.sin(ExternalUtils.rad2deg(90 - lat2));
		final double z1 = ExternalUtils.calcR(lat1)
				* Math.cos(ExternalUtils.rad2deg(90 - lat1));
		final double z2 = ExternalUtils.calcR(lat2)
				* Math.cos(ExternalUtils.rad2deg(90 - lat2));
		final double powArg = ExternalUtils.calcR((lat1 + lat2) / 2);
		final double div = powArg * powArg;
		double a_acosArg = ((x1 * x2) + (y1 * y2) + (z1 * z2)) / div;
		// Due to roundings, argument could exceed 1,0 limits slightly.
		// Bring within limits to avoid NaN !!
		if (a_acosArg > 1.0) {
			a_acosArg = 1.0;
		} else if (a_acosArg < -1.0) {
			a_acosArg = -1.0;
		}
		final double a = bt747.sys.Generic.acos(a_acosArg);

		return ExternalUtils.calcR((lat1 + lat2) / 2) * a;
	}

	// This pulls the "real radius" of a lat, instead of a global estimate
	public static final double calcR(final double p_lat) /* FOLD00 */
	{
		final double a = 6378.137;
		final double e2 = 0.081082 * 0.081082;
		/*
		 * the radius of curvature of an ellipsoidal Earth in the plane of the
		 * meridian is given by
		 * 
		 * R' = a * (1 - e^2) / (1 - e^2 * (sin(lat))^2)^(3/2)
		 * 
		 * where a is the equatorial radius, b is the polar radius, and e is the
		 * eccentricity of the ellipsoid = sqrt(1 - b^2/a^2)
		 * 
		 * a = 6378 km (3963 mi) Equatorial radius (surface to center distance)
		 * b = 6356.752 km (3950 mi) Polar radius (surface to center distance) e
		 * = 0.081082 Eccentricity
		 */

		final double lat = p_lat * Math.PI / 180.0;
		final double sc = Math.sin(lat);
		final double x = a * (1.0 - e2);
		final double z = 1.0 - e2 * sc * sc;
		final double y = bt747.sys.Generic.pow(z, 1.5);
		final double r = 1000. * x / y;
		return r;
	}

	public static final double rad2deg(final double x) {
		return x * Math.PI / 180.0;
	}

	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;

	public static final int JANUARY = 1;
	public static final int FEBRUARY = 2;
	public static final int MARCH = 3;
	public static final int APRIL = 4;
	public static final int MAY = 5;
	public static final int JUNE = 6;
	public static final int JULY = 7;
	public static final int AUGUST = 8;
	public static final int SEPTEMBER = 9;
	public static final int OCTOBER = 10;
	public static final int NOVEMBER = 11;
	public static final int DECEMBER = 12;

	/**
	 * Taken from Superwaba/waba source which is GPL.<br>
	 * 
	 * Returns the day of week, where 0 is sunday and 6 is saturday.
	 * 
	 * @return integer representation of day of week. Integers refer to static
	 *         constants of day of week.
	 */
	public static final int getDayOfWeek(final int f, final int mm, final int yy) {
		final int a = (yy - 1582) * 365;
		final int b = (int) ((yy - 1581) / 4);
		final int c = (int) ((yy - 1501) / 100);
		final int d = (int) ((yy - 1201) / 400);
		final int e = (mm - 1) * 31;
		final int g = (int) ((mm + 7) / 10);
		final int h = (int) (((mm * 4 + 23) * g) / 10);
		final int i = (int) ((1 / ((yy % 4) + 1)) * g);
		final int j = (int) ((1 / ((yy % 100) + 1)) * g);
		final int k = (int) ((1 / ((yy % 400) + 1)) * g);
		final int l = a + b - c + d + e + f - h + i - j + k + 5;
		final int m = l % 7;
		return (int) ((m > 0) ? (m - 1) : 6);
	}

}
