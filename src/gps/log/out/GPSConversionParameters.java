/**
 * 
 */
package gps.log.out;

import bt747.sys.Interface;

/**
 * Object to hold conversion parameters that are provided for conversion but
 * not necessarily used in all conversions. This is to allow for easier
 * extensibility and having more finegrained output settings.
 * 
 * This is basically a Hashset where the key is the parameter name.
 * 
 * Some constants may be defined here for known parameters types.
 * 
 * @author Mario De Weerd
 * 
 */
public final class GPSConversionParameters {
    public final static String GOOGLEMAPKEY_STRING = "googlemapkey";
    public final static String KML_TRACK_ALTITUDE_STRING = "kml-track-altitude";
    public final static String TRACK_SPLIT_IF_SMALL_BOOL = "trk-split-small";
    public final static String NMEA_OUTFIELDS = "nmea-out";

    private final bt747.sys.interfaces.BT747Hashtable h = Interface
            .getHashtableInstance(5);

    public final boolean hasParam(final String param) {
        return h.get(param) != null;
    }

    public final void setParam(final String param, final Object value) {
        h.put(param, value);
    }

    public final Object getParam(final String param) {
        return h.get(param);
    }

    public final String getStringParam(final String param) {
        return (String) getParam(param);
    }

    public final int getIntParam(final String param) {
        return ((MyInt) getParam(param)).i;
    }

    public final void setIntParam(final String param, final int i) {
        setParam(param, new MyInt(i));
    }

    public final boolean getBoolParam(final String param) {
        return ((MyInt) getParam(param)).i != 0;
    }

    public final void setBoolParam(final String param, final boolean b) {
        setParam(param, new MyInt(b ? 1 : 0));
    }

    // Superwaba does not have an integer class.
    private final static class MyInt {
        final int i;

        public MyInt(final int i) {
            this.i = i;
        }
    }
}
