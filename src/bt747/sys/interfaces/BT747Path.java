/**
 * 
 */
package bt747.sys.interfaces;

/**
 * "Abstract" class so that a file path can be encapsulated to math different
 * platforms.
 * 
 * For instance, SuperWaba requires a 'card' integer index, while a regular
 * java platform only uses a String.
 * 
 * @author Mario De Weerd
 * 
 */
public class BT747Path {

    /**
     * The path to the file. Every platform should have at least one string
     * representation.
     */
    private final String path;

    /**
     * Constructor with just a path parameter.
     * 
     * @param path
     */
    public BT747Path(final String path) {
        this.path = path;
    }

    /**
     * Create new path using this instance as a prototype. Must be overriden
     * in derived classes. This helps to keep settings like the card number
     * that is a separate value on SuperWaba systems.
     * 
     * @param path
     * @return Instance which is a copy of the current one, except for the
     *         path.
     */
    public BT747Path proto(final String path) {
        return new BT747Path(path);
    }

    /**
     * Constructor with just a path parameter.
     * 
     * @param path
     */
    public BT747Path(final BT747Path path) {
        this.path = path.path;
    }

    /**
     * Returns the path. No setter because everything has to be set on
     * construction.
     */
    public final String getPath() {
        return path;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getPath();
    }
}
