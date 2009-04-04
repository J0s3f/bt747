/**
 * 
 */
package gps.log.out;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 * 
 */
public class WayPointStyleSet {
    private final BT747Hashtable styles = JavaLibBridge.getHashtableInstance(5);
    private BT747Hashtable iterator;

    public final void add(final WayPointStyle style) {
        styles.put(style.getKey(), style);
    }

    public final void remove(final WayPointStyle style) {
        styles.remove(style);
    }

    public final boolean has(final String style) {
        return styles.get(style) != null;
    }

    public final WayPointStyle get(final String style) {
        return (WayPointStyle) styles.get(style);
    }

    public final WayPointStyleSet iterator() {
        iterator = styles.iterator();
        return this;
    }

    public final boolean hasNext() {
        return iterator.hasNext();
    }

    public final WayPointStyle next() {
        return (WayPointStyle) styles.get(iterator.nextKey());
    }

    public WayPointStyleSet(final String[][] stylesList) {
        WayPointStyle style = new WayPointStyle();
        try {
            for (int i = 0; i < stylesList.length; i++) {
                switch (stylesList[i].length) {
                case 3:
                    style.setIconUrl(stylesList[i][2]);
                    /* fall through */
                case 2:
                    style.setSymbolText(stylesList[i][1]);
                    style.setKey(stylesList[i][0]);
                    styles.put(style.getKey(), style);
                    style = new WayPointStyle();
                    break;
                case 1:
                default:
                    Generic.debug("Issue with record " + i + " of styleList");
                }
                for (int j = 0; j < stylesList[i].length; j++) {

                }
            }
        } catch (final Exception e) {
            Generic.debug("Issue with wayPointStyleSet initialisation", e);
        }
        style = null;
    }
}
