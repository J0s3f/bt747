/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import bt747.interfaces.Interface;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Convert {
    public static String toString(final boolean p) {
        return Interface.tr.toString(p);
    }

    public static String toString(final int p) {
        return Interface.tr.toString(p);
    }

    public static String toString(final float p) {
        return Interface.tr.toString(p);
    }

    public static String toString(final double p) {
        return Interface.tr.toString(p);
    }

    public static String toString(final double p, final int i) {
        return Interface.tr.toString(p,i);
    }

    public static String unsigned2hex(final int p, final int i) {
        return Interface.tr.unsigned2hex(p, i);
    }

    public static int toInt(final String s) {
        return Interface.tr.toInt(s);
    }

    public static float toFloat(final String s) {
        return Interface.tr.toFloat(s);
    }

    public static double toDouble(final String s) {
        return Interface.tr.toDouble(s);
    }

    public static double longBitsToDouble(final long l) {
        return Interface.tr.longBitsToDouble(l);
    }

    public static float toFloatBitwise(final int l) {
        return Interface.tr.toFloatBitwise(l);
    }

    public static int toIntBitwise(final float f) {
        return Interface.tr.toIntBitwise(f);
    }
}
