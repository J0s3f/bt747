/**
 * 
 */
package net.sf.bt747.j4me.app.conn;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public class JSR179Connection {

    private static boolean isAvailableTested = false;
    private static boolean isAvailable;

    public static final boolean isAvailable() {
        if (!isAvailableTested) {
            isAvailableTested = true;
            try {
                Class c = Class
                        .forName("javax.microedition.location.LocationProvider");
                isAvailable = c != null;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return isAvailable;
    }

    /**
     * 
     */
    private static JSR179Connection singleton;
    
    private static Object provider;

    public static JSR179Connection getInstance() {
        if (isAvailable && singleton == null) {
            final javax.microedition.location.Criteria jsr179 = new javax.microedition.location.Criteria();

            jsr179.setAddressInfoRequired(false);
            jsr179.setAltitudeRequired(false);
            jsr179.setCostAllowed(true);
            // jsr179.setHorizontalAccuracy(false);
            // jsr179.setPreferredResponseTime();
            jsr179.setSpeedAndCourseRequired(false);
            // jsr179.setVerticalAccuracy();
            //jsr179.setPreferredPowerConsumption(javax.microedition.location.Criteria.POWER_USAGE_MEDIUM);

            try {
                final javax.microedition.location.LocationProvider jsr179provider = javax.microedition.location.LocationProvider
                        .getInstance(jsr179);
                provider = jsr179provider;
            } catch (Exception e) {
                Generic.debug("Problem getting location provider", e);
                // TODO: handle exception
            }
        }
        return singleton;
    }

}
