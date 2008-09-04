/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

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
        StringBuffer s;
        if (p >= 1.) {
            s = new StringBuffer(Double.toString(p));
        } else {
            s = new StringBuffer(Double.toString(p + 1.));
            s.setCharAt(0, '0');
        }

        int dotPos;
        int diff;
        dotPos = s.toString().indexOf('.');
        diff = (dotPos + i + 1 - s.length());
        if (diff == 0) {
            // Do nothing
        } else if (diff > 0) {
            s.append(ZEROCHARS, 1, diff);
        } else if (dotPos == 0) {
            s.append(ZEROCHARS, 0, i + 1);
        } else {
            // Truncrate - some limited notion of rounding.
            if (s.charAt(dotPos + i + 1) >= '5') {
                char c;
                c = s.charAt(dotPos + i);
                c += 1;
                if (c <= '9') {
                    s.setCharAt(dotPos + i, c);
                }
            }
            s.setLength(dotPos + i + 1);
        }
        return s.toString();
    }

    /**
     * Constant string of zeros used for padding.
     */
    private static final String ZEROSTRING = "0000000000000000";
    private static final char[] ZEROCHARS = { '.', '0', '0', '0', '0', '0',
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', };

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
