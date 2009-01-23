/**
 * 
 */
package gps.log.out;

/**
 * Definitions for a waypoint style.
 * 
 * @author Mario De Weerd
 * 
 */
public class WayPointStyle {

    /**
     * URL provided in case the application did not provide one.
     */
    private static final String DEFAULT_URL = "http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png";
    /**
     * The key for the way point. T, S, D or B, or a hex number of four
     * letters.
     */
    private String key;
    /**
     * A short description of the way point type.
     */
    private String symbol;
    /**
     * A link to the icon: http:// or file:// like.
     */
    private String iconUrl;

    /**
     * @return the key
     */
    public final String getKey() {
        return key;
    }

    /**
     * @param key
     *                the key to set
     */
    public final void setKey(final String key) {
        this.key = key;
    }

    /**
     * @return the description
     */
    public final String getSymbolText() {
        return symbol;
    }

    /**
     * @param symbol
     *                the description to set
     */
    public final void setSymbolText(final String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the iconUrl
     */
    public final String getIconUrl() {
        if (iconUrl != null) {
            return iconUrl;
        } else {
            return WayPointStyle.DEFAULT_URL;
        }
    }

    /**
     * @param iconUrl
     *                the iconUrl to set
     */
    public final void setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
    }

}
