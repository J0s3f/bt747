/**
 * 
 */
package gps.convert;

/**
 * @author Mario
 * 
 */
public final class Geoid implements GeoidIF {
    public final static GeoidIF getInstance() {
        return new Geoid();
    }

    private Geoid() {

    }

    private static final int GEOID_ROW = 19;
    private static final int GEOID_COL = 37;
    private static final byte[] geoid_delta = {
    /*
     * 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26
     * 27 28 29 30 31 32 33 34 35 36
     */
    /*
     * -180 -170 -160 -150 -140 -130 -120 -110 -100 -90 -80 -70 -60 -50 -40
     * -30 -20 -10 0 10 20 30 40 50 60 70 80 90 100 110 120 130 140 150 160
     * 170 180
     */
    /* 90S */(byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte) -30, (byte) -30, (byte) -30,
            (byte) -30, (byte) -30, (byte)
            /* 80S */-53, (byte) -54, (byte) -55, (byte) -52, (byte) -48,
            (byte) -42, (byte) -38, (byte) -38, (byte) -29, (byte) -26,
            (byte) -26, (byte) -24, (byte) -23, (byte) -21, (byte) -19,
            (byte) -16, (byte) -12, (byte) -8, (byte) -4, (byte) -1,
            (byte) 1, (byte) 4, (byte) 4, (byte) 6, (byte) 5, (byte) 4,
            (byte) 2, (byte) -6, (byte) -15, (byte) -24, (byte) -33,
            (byte) -40, (byte) -48, (byte) -50, (byte) -53, (byte) -52,
            (byte) -53, (byte)
            /* 70S */-61, (byte) -60, (byte) -61, (byte) -55, (byte) -49,
            (byte) -44, (byte) -38, (byte) -31, (byte) -25, (byte) -16,
            (byte) -6, (byte) 1, (byte) 4, (byte) 5, (byte) 4, (byte) 2,
            (byte) 6, (byte) 12, (byte) 16, (byte) 16, (byte) 17, (byte) 21,
            (byte) 20, (byte) 26, (byte) 26, (byte) 22, (byte) 16, (byte) 10,
            (byte) -1, (byte) -16, (byte) -29, (byte) -36, (byte) -46,
            (byte) -55, (byte) -54, (byte) -59, (byte) -61, (byte)
            /* 60S */-45, (byte) -43, (byte) -37, (byte) -32, (byte) -30,
            (byte) -26, (byte) -23, (byte) -22, (byte) -16, (byte) -10,
            (byte) -2, (byte) 10, (byte) 20, (byte) 20, (byte) 21, (byte) 24,
            (byte) 22, (byte) 17, (byte) 16, (byte) 19, (byte) 25, (byte) 30,
            (byte) 35, (byte) 35, (byte) 33, (byte) 30, (byte) 27, (byte) 10,
            (byte) -2, (byte) -14, (byte) -23, (byte) -30, (byte) -33,
            (byte) -29, (byte) -35, (byte) -43, (byte) -45, (byte)
            /* 50S */-15, (byte) -18, (byte) -18, (byte) -16, (byte) -17,
            (byte) -15, (byte) -10, (byte) -10, (byte) -8, (byte) -2,
            (byte) 6, (byte) 14, (byte) 13, (byte) 3, (byte) 3, (byte) 10,
            (byte) 20, (byte) 27, (byte) 25, (byte) 26, (byte) 34, (byte) 39,
            (byte) 45, (byte) 45, (byte) 38, (byte) 39, (byte) 28, (byte) 13,
            (byte) -1, (byte) -15, (byte) -22, (byte) -22, (byte) -18,
            (byte) -15, (byte) -14, (byte) -10, (byte) -15, (byte)
            /* 40S */21, (byte) 6, (byte) 1, (byte) -7, (byte) -12,
            (byte) -12, (byte) -12, (byte) -10, (byte) -7, (byte) -1,
            (byte) 8, (byte) 23, (byte) 15, (byte) -2, (byte) -6, (byte) 6,
            (byte) 21, (byte) 24, (byte) 18, (byte) 26, (byte) 31, (byte) 33,
            (byte) 39, (byte) 41, (byte) 30, (byte) 24, (byte) 13, (byte) -2,
            (byte) -20, (byte) -32, (byte) -33, (byte) -27, (byte) -14,
            (byte) -2, (byte) 5, (byte) 20, (byte) 21, (byte)
            /* 30S */46, (byte) 22, (byte) 5, (byte) -2, (byte) -8,
            (byte) -13, (byte) -10, (byte) -7, (byte) -4, (byte) 1, (byte) 9,
            (byte) 32, (byte) 16, (byte) 4, (byte) -8, (byte) 4, (byte) 12,
            (byte) 15, (byte) 22, (byte) 27, (byte) 34, (byte) 29, (byte) 14,
            (byte) 15, (byte) 15, (byte) 7, (byte) -9, (byte) -25,
            (byte) -37, (byte) -39, (byte) -23, (byte) -14, (byte) 15,
            (byte) 33, (byte) 34, (byte) 45, (byte) 46, (byte)
            /* 20S */51, (byte) 27, (byte) 10, (byte) 0, (byte) -9,
            (byte) -11, (byte) -5, (byte) -2, (byte) -3, (byte) -1, (byte) 9,
            (byte) 35, (byte) 20, (byte) -5, (byte) -6, (byte) -5, (byte) 0,
            (byte) 13, (byte) 17, (byte) 23, (byte) 21, (byte) 8, (byte) -9,
            (byte) -10, (byte) -11, (byte) -20, (byte) -40, (byte) -47,
            (byte) -45, (byte) -25, (byte) 5, (byte) 23, (byte) 45,
            (byte) 58, (byte) 57, (byte) 63, (byte) 51, (byte)
            /* 10S */36, (byte) 22, (byte) 11, (byte) 6, (byte) -1,
            (byte) -8, (byte) -10, (byte) -8, (byte) -11, (byte) -9,
            (byte) 1, (byte) 32, (byte) 4, (byte) -18, (byte) -13, (byte) -9,
            (byte) 4, (byte) 14, (byte) 12, (byte) 13, (byte) -2, (byte) -14,
            (byte) -25, (byte) -32, (byte) -38, (byte) -60, (byte) -75,
            (byte) -63, (byte) -26, (byte) 0, (byte) 35, (byte) 52,
            (byte) 68, (byte) 76, (byte) 64, (byte) 52, (byte) 36, (byte)
            /* 00N */22, (byte) 16, (byte) 17, (byte) 13, (byte) 1,
            (byte) -12, (byte) -23, (byte) -20, (byte) -14, (byte) -3,
            (byte) 14, (byte) 10, (byte) -15, (byte) -27, (byte) -18,
            (byte) 3, (byte) 12, (byte) 20, (byte) 18, (byte) 12, (byte) -13,
            (byte) -9, (byte) -28, (byte) -49, (byte) -62, (byte) -89,
            (byte) -102, (byte) -63, (byte) -9, (byte) 33, (byte) 58,
            (byte) 73, (byte) 74, (byte) 63, (byte) 50, (byte) 32, (byte) 22,
            (byte)
            /* 10N */13, (byte) 12, (byte) 11, (byte) 2, (byte) -11,
            (byte) -28, (byte) -38, (byte) -29, (byte) -10, (byte) 3,
            (byte) 1, (byte) -11, (byte) -41, (byte) -42, (byte) -16,
            (byte) 3, (byte) 17, (byte) 33, (byte) 22, (byte) 23, (byte) 2,
            (byte) -3, (byte) -7, (byte) -36, (byte) -59, (byte) -90,
            (byte) -95, (byte) -63, (byte) -24, (byte) 12, (byte) 53,
            (byte) 60, (byte) 58, (byte) 46, (byte) 36, (byte) 26, (byte) 13,
            (byte)
            /* 20N */5, (byte) 10, (byte) 7, (byte) -7, (byte) -23,
            (byte) -39, (byte) -47, (byte) -34, (byte) -9, (byte) -10,
            (byte) -20, (byte) -45, (byte) -48, (byte) -32, (byte) -9,
            (byte) 17, (byte) 25, (byte) 31, (byte) 31, (byte) 26, (byte) 15,
            (byte) 6, (byte) 1, (byte) -29, (byte) -44, (byte) -61,
            (byte) -67, (byte) -59, (byte) -36, (byte) -11, (byte) 21,
            (byte) 39, (byte) 49, (byte) 39, (byte) 22, (byte) 10, (byte) 5,
            (byte)
            /* 30N */-7, (byte) -5, (byte) -8, (byte) -15, (byte) -28,
            (byte) -40, (byte) -42, (byte) -29, (byte) -22, (byte) -26,
            (byte) -32, (byte) -51, (byte) -40, (byte) -17, (byte) 17,
            (byte) 31, (byte) 34, (byte) 44, (byte) 36, (byte) 28, (byte) 29,
            (byte) 17, (byte) 12, (byte) -20, (byte) -15, (byte) -40,
            (byte) -33, (byte) -34, (byte) -34, (byte) -28, (byte) 7,
            (byte) 29, (byte) 43, (byte) 20, (byte) 4, (byte) -6, (byte) -7,
            (byte)
            /* 40N */-12, (byte) -10, (byte) -13, (byte) -20, (byte) -31,
            (byte) -34, (byte) -21, (byte) -16, (byte) -26, (byte) -34,
            (byte) -33, (byte) -35, (byte) -26, (byte) 2, (byte) 33,
            (byte) 59, (byte) 52, (byte) 51, (byte) 52, (byte) 48, (byte) 35,
            (byte) 40, (byte) 33, (byte) -9, (byte) -28, (byte) -39,
            (byte) -48, (byte) -59, (byte) -50, (byte) -28, (byte) 3,
            (byte) 23, (byte) 37, (byte) 18, (byte) -1, (byte) -11,
            (byte) -12, (byte)
            /* 50N */-8, (byte) 8, (byte) 8, (byte) 1, (byte) -11,
            (byte) -19, (byte) -16, (byte) -18, (byte) -22, (byte) -35,
            (byte) -40, (byte) -26, (byte) -12, (byte) 24, (byte) 45,
            (byte) 63, (byte) 62, (byte) 59, (byte) 47, (byte) 48, (byte) 42,
            (byte) 28, (byte) 12, (byte) -10, (byte) -19, (byte) -33,
            (byte) -43, (byte) -42, (byte) -43, (byte) -29, (byte) -2,
            (byte) 17, (byte) 23, (byte) 22, (byte) 6, (byte) 2, (byte) -8,
            (byte)
            /* 60N */2, (byte) 9, (byte) 17, (byte) 10, (byte) 13, (byte) 1,
            (byte) -14, (byte) -30, (byte) -39, (byte) -46, (byte) -42,
            (byte) -21, (byte) 6, (byte) 29, (byte) 49, (byte) 65, (byte) 60,
            (byte) 57, (byte) 47, (byte) 41, (byte) 21, (byte) 18, (byte) 14,
            (byte) 7, (byte) -3, (byte) -22, (byte) -29, (byte) -32,
            (byte) -32, (byte) -26, (byte) -15, (byte) -2, (byte) 13,
            (byte) 17, (byte) 19, (byte) 6, (byte) 2, (byte)
            /* 70N */2, (byte) 2, (byte) 1, (byte) -1, (byte) -3, (byte) -7,
            (byte) -14, (byte) -24, (byte) -27, (byte) -25, (byte) -19,
            (byte) 3, (byte) 24, (byte) 37, (byte) 47, (byte) 60, (byte) 61,
            (byte) 58, (byte) 51, (byte) 43, (byte) 29, (byte) 20, (byte) 12,
            (byte) 5, (byte) -2, (byte) -10, (byte) -14, (byte) -12,
            (byte) -10, (byte) -14, (byte) -12, (byte) -6, (byte) -2,
            (byte) 3, (byte) 6, (byte) 4, (byte) 2, (byte)
            /* 80N */3, (byte) 1, (byte) -2, (byte) -3, (byte) -3,
            (byte) -3, (byte) -1, (byte) 3, (byte) 1, (byte) 5, (byte) 9,
            (byte) 11, (byte) 19, (byte) 27, (byte) 31, (byte) 34, (byte) 33,
            (byte) 34, (byte) 33, (byte) 34, (byte) 28, (byte) 23, (byte) 17,
            (byte) 13, (byte) 9, (byte) 4, (byte) 4, (byte) 1, (byte) -2,
            (byte) -2, (byte) 0, (byte) 2, (byte) 3, (byte) 2, (byte) 1,
            (byte) 1, (byte) 3, (byte)
            /* 90N */13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13, (byte) 13,
            (byte) 13, (byte) 13 };

    /*
     * return geoid separation (MSL - WGS84) in meters, given a lat/lot in
     * degrees
     */
    /* @ +charint @ */
    public final double wgs84Separation(final double lat, final double lon) {
        /* @ -charint @ */
        int ilat, ilon;
        int ilat1, ilat2, ilon1, ilon2;

        ilat = (int) Math.floor((90. + lat) / 10);
        ilon = (int) Math.floor((180. + lon) / 10);

        ilat1 = ilat;
        ilon1 = ilon;
        ilat2 = (ilat < GEOID_ROW - 1) ? (ilat + 1) : ilat;
        ilon2 = (ilon < GEOID_COL - 1) ? (ilon + 1) : ilon;

        try {
            return Conv.bilinear((ilon1 * 10.) - 180., (ilat1 * 10.) - 90.,
                    (ilon2 * 10.) - 180., (ilat2 * 10.) - 90., lon, lat,
                    geoid_delta[ilon1 + (ilat1 * GEOID_COL)],
                    geoid_delta[ilon2 + (ilat1 * GEOID_COL)],
                    geoid_delta[ilon1 + (ilat2 * GEOID_COL)],
                    geoid_delta[ilon2 + (ilat2 * GEOID_COL)]);
        } catch (final Exception e) {
            return -999;
        }
    }

}
