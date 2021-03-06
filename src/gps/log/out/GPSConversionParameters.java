/**
 * 
 */
package gps.log.out;

import bt747.sys.JavaLibBridge;

/**
 * Object to hold conversion parameters that are provided for conversion but not
 * necessarily used in all conversions. This is to allow for easier
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
	public final static String GPX_LINK_TEXT = "gpx-link";
	public final static String GPX_1_1 = "gpx-1_1";
	public final static String WAY_COMMENT = "waycmt";
	public final static String TRK_COMMENT = "trkcmt";
	public final static String NMEA_OUTFIELDS = "nmea-out";
	/**
	 * Defines the CSV field seperation character.
	 */
	public final static String CSV_FIELD_SEP_STRING = "csv-fieldsep";
	/**
	 * Defines the CSV field decimal point..
	 */
	public final static String CSV_DECIMALPOINT_STRING = "csv-decimal";
	/**
	 * Defines the CSV field seperation character.
	 */
	public final static String CSV_SAT_SEP_STRING = "csv-satsep";
	/**
	 * Defines the CSV date format (to be implemented).
	 */
	public final static String CSV_DATE_FORMAT_INT = "csv-datefmt";
	/**
	 * Defines the table name for Postgresql.
	 */
	public final static String SQL_TABLE_NAME = "sql-table";

	/**
	 * Defines the table name for Postgresql.
	 */
	public final static String POSTGIS_TABLE_NAME = "postgis-table";

	/**
	 * OSM login (string).
	 */
	public final static String OSM_LOGIN = "osm-login";
	/**
	 * OSM Password (string).
	 */
	public final static String OSM_PASS = "osm-pass";

	/**
	 * OSM Visibility<br>
	 * Valid values: private, public, trackable, identifiable (use constants
	 * {@link #OSM_PRIVATE}, {@link #OSM_PUBLIC}, {@link #OSM_TRACKABLE},
	 * {@link #OSM_IDENTIFIABLE}
	 */
	public final static String OSM_VISIBILITY = "osm-visibility";
	public final static String OSM_PRIVATE = "private";
	public final static String OSM_PUBLIC = "public";
	public final static String OSM_TRACKABLE = "trackable";
	public final static String OSM_IDENTIFIABLE = "identifiable";

	/**
	 * OSM Description (string)
	 */
	public final static String OSM_DESCRIPTION = "osm-description";

	/**
	 * OSM Tags (string)
	 */
	public final static String OSM_TAGS = "osm-tags";

	/**
	 * Command for external tool
	 */
	public final static String EXT_COMMAND = "ext-cmd";

	/**
	 * Indicates if a new track is needed when the logger is switched on.
	 */
	public final static String NEW_TRACK_WHEN_LOG_ON = "log-on-trk";

	/**
	 * Indicates if a new track is needed when the logger is switched on.
	 */
	public final static String SPLIT_DISTANCE = "split-d";

	/**
	 * Indicates the mode to use for distance values.
	 */

	public final static String DISTANCE_CALC_MODE = "dist-calc";

	/**
	 * When fields are missing, add a 0 or other 'neutral/normal' value in the
	 * output.
	 */
	public final static String CREATE_FIELDS = "create-fields";

	/**
	 * Number of digits to use for height fields.
	 */
	public final static String HEIGHT_DIGITS = "height-digits";

	/**
	 * Number of digits to use for height fields.
	 */
	public final static String CSV_HEADER_PREFIX = "data-csv-prefix";

	/**
	 * Number of digits to use for height fields.
	 */
	public final static String CSV_DATA_PREFIX = "header-csv-prefix";

	/**
	 * Number of digits to use for position fields (lat/lon).
	 */
	public final static String POSITION_DIGITS = "pos-digits";
	/**
	 * Add log conditions to certain output formats.
	 */
	public final static String ADD_LOG_CONDITIONS = "add-cond";
        /**
         * If true, do not add &lt;comment&gt; in GPX
         */
        public final static String GPX_NO_COMMENT = "gpxnc";
        /**
         * If true, do not add &lt;symbol&gt; in GPX
         */
        public final static String GPX_NO_SYMBOL = "gpxns";

	private final bt747.sys.interfaces.BT747Hashtable h = JavaLibBridge
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
