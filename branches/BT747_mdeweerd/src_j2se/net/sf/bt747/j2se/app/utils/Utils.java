/**
 * 
 */
package net.sf.bt747.j2se.app.utils;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author Mario
 *
 */
public final class Utils {
    /** Gcj does not know String.format, so adding local implementation.
     * @param format
     * @param args
     * @return
     */
    public final static String format(String format, Object ... args) {
        return new Formatter().format((Locale) null, format, args).toString(); 
    }
}
