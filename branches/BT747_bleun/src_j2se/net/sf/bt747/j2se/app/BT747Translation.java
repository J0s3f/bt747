/**
 * 
 */
package net.sf.bt747.j2se.app;

import java.util.ResourceBundle;

import bt747.sys.Generic;
import bt747.sys.interfaces.BT747I18N;

/**
 * Implements a translation implementation for the BT747 base library.
 * 
 * @author Mario
 * 
 */
public class BT747Translation implements BT747I18N {
    /**
     * The resource bundle used for localization.
     */
    private static ResourceBundle bundle = null;

    private static void initStaticsFirstTime() {
        if (BT747Translation.bundle == null) {
            BT747Translation.bundle = java.util.ResourceBundle
                    .getBundle("net/sf/bt747/j2se/app/resources/BT747base");
        }
    }

    static {
        BT747Translation.initStaticsFirstTime();
    }

    private static BT747Translation instance;

    public static final BT747Translation getInstance() {
        if (BT747Translation.instance == null) {
            BT747Translation.instance = new BT747Translation();
        }
        return BT747Translation.instance;
    }

    /**
     * I18N. Internationalization - get the localized string.
     * 
     * @param s
     *                String reference for localization.
     * @return Localized String.
     */
    public static final String getString(final String s) {
        try {
            return BT747Translation.bundle.getString(s);
        } catch (final Exception e) {
            Generic.debug("No BT747base translation found for \"" + s + "\"",
                    e);
            return s;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747I18N#i18n(java.lang.String)
     */
    public final String i18n(final String s) {
        return BT747Translation.getString(s);
    }

}
