/**
 * 
 */
package bt747.sys;

/**
 * Wrapper class of an integer so that it can be an object.
 * 
 * @author Mario
 * 
 */
public final class BT747Int {
    private final int i;

    /**
     * 
     */
    public BT747Int(final int i) {
        this.i = i;
    }

    public final int getValue() {
        return i;
    }
    
    public static final BT747Int get(final int i) {
        return new BT747Int(i);
    }
}
