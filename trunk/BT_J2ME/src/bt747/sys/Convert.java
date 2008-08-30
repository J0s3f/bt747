/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.microedition.global.Formatter;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Convert {
    public static String toString(final boolean p) {
        return String.valueOf(p);
    }

    public static String toString(final int p) {
        return String.valueOf(p);
    }

    public static String toString(final float p) {
        return Float.toString(p);
    }

    public static String toString(final double p) {
        return Double.toString(p);
    }

    public static String toString(final double p, final int i) {
        String s;
        if (p >= 1.) {
            s = Double.toString(p);
        } else {
            s = Double.toString(p + 1.);
            s = "0" + s.substring(1);
        }

        int dotPos;
        dotPos = s.indexOf('.');
        if (dotPos == 0) {
            s.concat("." + ZEROSTRING.substring(0, i));
        } else if (dotPos + i + 1 > s.length()) {
            s.concat(ZEROSTRING.substring(0, dotPos + i + 1 - s.length()));
        } else {
            s = s.substring(0, dotPos + i + 1);
        }
        return s;
    }

    /**
     * Constant string of zeros used for padding.
     */
    private static final String ZEROSTRING = "0000000000000000";

    public static String unsigned2hex(final int p, final int i) {
        String s = Integer.toHexString(p).toUpperCase();
        if (s.length() == i) {
            return s;
        } else if (s.length() < i) {
            return ZEROSTRING.substring(ZEROSTRING.length() - i + s.length())
                    .concat(s);
        } else {
            return s.substring(s.length() - i);
        }
    }

    public static int toInt(final String s) {
        return Integer.valueOf(s).intValue();
    }

    public static float toFloat(final String s) {
        return Float.parseFloat(s);
    }

    public static double toDouble(final String s) {
        return Double.parseDouble(s);
    }

    public static double longBitsToDouble(final long l) {
        return Double.longBitsToDouble(l);
    }

    public static float toFloatBitwise(final int l) {
        return Float.intBitsToFloat(l);
    }

    public static int toIntBitwise(final float f) {
        return Float.floatToIntBits(f);
    }
}
