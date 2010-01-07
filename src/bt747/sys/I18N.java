/**
 * 
 */
package bt747.sys;

import bt747.sys.interfaces.BT747I18N;

/**
 * @author Mario
 * 
 */
public final class I18N {

    private static BT747I18N imp = null;

    public static final void setI18N(BT747I18N imp) {
        I18N.imp = imp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747I18N#i18n(java.lang.String)
     */
    public final static String i18n(final String s) {
        if (imp == null) {
            return s;
        } else {
            return imp.i18n(s);
        }
    }

}
