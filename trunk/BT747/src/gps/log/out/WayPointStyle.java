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
     * The key for the way point. T, S, D or B, or a hex number of four letters.
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
        return this.key;
    }

    /**
     * @param key
     *            the key to set
     */
    public final void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the description
     */
    public final String getSymbolText() {
        return this.symbol;
    }

    /**
     * @param symbol
     *            the description to set
     */
    public final void setSymbolText(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the iconUrl
     */
    public final String getIconUrl() {
        if (this.iconUrl != null) {
            return this.iconUrl;
        } else {
            return DEFAULT_URL;
        }
    }

    /**
     * @param iconUrl
     *            the iconUrl to set
     */
    public final void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

}
