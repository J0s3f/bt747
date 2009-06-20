/**
 * 
 */
package net.sf.bt747.util;

import bt747.sys.JavaLibBridge;

/**
 * @author Mario
 * 
 */
public class GpsConvert {

    private final static int REF_TIME_19800106 = bt747.sys.JavaLibBridge
            .getDateInstance(6, 1, 1980).dateToUTCepoch1970();

    public static final bt747.sys.interfaces.BT747Time toTime(final int GpsWeek,
            final int GpsSeconds) {
        final bt747.sys.interfaces.BT747Time t = JavaLibBridge
                .getTimeInstance();
        t
                .setUTCTime(GpsWeek * 7 * 24 * 3600 + GpsSeconds
                        + REF_TIME_19800106);
        return t;
    }
}
