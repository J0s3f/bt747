/**
 * 
 */
package net.sf.bt747.j2se.app.utils;

import gps.convert.Conv;
import gps.convert.GeoidIF;

import java.io.InputStream;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public final class Geoid implements GeoidIF {

    private static byte[] geoid_delta;
    private static GeoidIF instance;
    private static final String GEOID_RESOURCE = "/net/sf/bt747/j2se/app/utils/geoid1DEG.bin";
    private static final int SIZE = 360 * 179;

    public final static GeoidIF getInstance() {
        if (instance == null) {
            init();
            if (geoid_delta != null) {
                instance = new Geoid();
            }
        }
        return instance;
    }

    private Geoid() {

    }

    /**
     * @return true if success
     */
    private final static boolean init() {
        try {
            if (geoid_delta == null) {
                // Get the resource.
                final InputStream is = Geoid.class
                        .getResourceAsStream(GEOID_RESOURCE);
                // System.err.println(Geoid.class.getResource(GEOID_RESOURCE)
                // .getPath());
                if (is.available() != SIZE) {
                    Generic.debug(GEOID_RESOURCE + " is bad size - expected "
                            + SIZE + " got " + is.available(), null);
                } else {
                    final byte[] tmp_geoidDelta = new byte[SIZE];
                    int cnt;
                    int offset = 0;
                    do {
                        cnt = is.read(tmp_geoidDelta, offset, SIZE);
                        if (cnt > 0) {
                            offset += cnt;
                        }
                    } while (offset < SIZE && cnt >= 0);
                    is.close();
                    // Only assigned in case no exception is thrown.
                    geoid_delta = tmp_geoidDelta;
                    // for (int i = SIZE - 1; i > 0; i--) {
                    // geoid_delta[i] -= 120;
                    // }
                    //
                    // final byte[] newGeoid = new byte[SIZE];
                    // for (int lat = -89; lat <= 89; lat++) {
                    // for (int lon = -180; lon < 180; lon++) {
                    // newGeoid[(lon + 180) + (lat + 89) * 360] =
                    // geoid_delta[(89 - lat)
                    // * 360 + ((lon + 360) % 360)];
                    // }
                    // }
                    // geoid_delta = newGeoid;

                    // final OutputStream os = new FileOutputStream(
                    // "c:/bt747/"+GEOID_RESOURCE, false);
                    // os.write(geoid_delta);
                    // os.flush();
                    // os.close();
                    // Generic.debug(GEOID_RESOURCE + "loaded");
                }
            }
        } catch (Exception e) {
            geoid_delta = null;
            Generic.debug("Geoid resource loading", e);
        }
        // System.err.println("Result: "
        // + ((geoid_delta != null) ? geoid_delta.length : "null"));
        // for (byte x : geoid_delta) {
        // System.err.print(String.format("%02x", x));
        // }
        System.err.println();
        return geoid_delta != null;
    }

    private static final int GEOID_ROW = 179;
    private static final int GEOID_COL = 360;

    /*
     * return geoid separation (MSL - WGS84) in meters, given a lat/lot in
     * degrees
     */
    /*
     * (non-Javadoc)
     * 
     * @see gps.convert.GeoidIF#wgs84Separation(double, double)
     */
    /* @ +charint @ */
    public final double wgs84Separation(final double lat, final double lon) {
        /* @ -charint @ */
        int ilat1, ilat2, ilon1, ilon2;

        ilat1 = (int) Math.floor((lat + 89.));
        ilon1 = (int) Math.floor((lon + 180.));

        if (ilat1 > GEOID_ROW - 1) {
            ilat1 = GEOID_ROW - 1;
        }
        ilat2 = (ilat1 < GEOID_ROW - 1) ? (ilat1 + 1) : GEOID_ROW - 1;
        ilon2 = ilon1 + 1;
        if (ilat1 < 0) {
            ilat1 = 0;
        }
        final double lon1 = ilon1 - 180.;
        final double lon2 = ilon2 - 180.;
        final double lat1 = ilat1 - 89.0;
        final double lat2 = ilat2 - 89.0;

        if (ilon1 < 0) {
            ilon1 = 0;
        } else if (ilon1 > GEOID_COL - 1) {
            ilon1 -= GEOID_COL;
        }
        if (ilon2 > GEOID_COL - 1) {
            ilon2 -= GEOID_COL;
        }

        try {
            return Conv.bilinear(lon1, lat1, lon2, lat2, lon, lat,
                    geoid_delta[ilon1 + (ilat1 * GEOID_COL)],
                    geoid_delta[ilon2 + (ilat1 * GEOID_COL)],
                    geoid_delta[ilon1 + (ilat2 * GEOID_COL)],
                    geoid_delta[ilon2 + (ilat2 * GEOID_COL)]);
        } catch (final Exception e) {
            return -999;
        }
    }
}
