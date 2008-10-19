/**
 * 
 */
package gps.log.out;

/** Definitions for a waypoint style.
 * @author Mario De Weerd
 *
 */
public class WayPointStyle {

    /**
     * The key for the way point.  T, S, D or B, or a hex number of four letters.
     */
    private String key;
    /**
     * A short description of the way point type.
     */
    private String description;
    /**
     * A link to the icon:
     *   http://  or file:// like.
     */
    private String iconUrl;
    /**
     * @return the key
     */
    public final String getKey() {
        return this.key;
    }
    /**
     * @param key the key to set
     */
    public final void setKey(String key) {
        this.key = key;
    }
    /**
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }
    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the iconUrl
     */
    public final String getIconUrl() {
        return this.iconUrl;
    }
    /**
     * @param iconUrl the iconUrl to set
     */
    public final void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
}
