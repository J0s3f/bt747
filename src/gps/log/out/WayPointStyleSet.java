/**
 * 
 */
package gps.log.out;

import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 *
 */
public final class WayPointStyleSet {
    private final BT747Hashtable styles = Interface.getHashtableInstance(5);
    private BT747Hashtable iterator;

    public final void add(final WayPointStyle style) {
        styles.put(style.getKey(),style);
    }

    public final void remove(final WayPointStyle style) {
        styles.remove(style);
    }

    public final boolean has(final String style) {
        return styles.get(style) != null;
    }

    public final WayPointStyleSet iterator() {
        iterator=styles.iterator();
        return this;
    }
    
    public final boolean hasNext() {
        return iterator.hasNext();
    }
    
    public final WayPointStyleSet next() {
        return (WayPointStyleSet) iterator.next();
    }
}
