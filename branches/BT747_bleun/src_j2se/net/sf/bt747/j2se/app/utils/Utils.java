/**
 * 
 */
package net.sf.bt747.j2se.app.utils;

import java.util.Formatter;
import java.util.Locale;

/**
 * Utility class.
 * 
 * @author Mario De Weerd
 * 
 */
public final class Utils {
    /**
     * Gcj does not know String.format, so adding local implementation. This
     * is needed for Java compilation.
     * 
     * @param format
     *            The format in the usual way ("%s,%i" for instance).
     * @param args
     *            Arguments for format.
     * @return formatted string.
     */
    public final static String format(String format, Object ... args) {
        return new Formatter().format((Locale) null, format, args).toString(); 
    }
}
