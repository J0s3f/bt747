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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class Convert {
    public static String toString(boolean p)
    {
       return String.valueOf(p);
    }

    public static String toString(int p)
    {
       return String.valueOf(p);
    }

    public static String toString(float p)
    {
       return Float.toString(p);
    }
    public static String toString(double p)
    {
       return Double.toString(p);
    }
    
    public static String toString(double p, int i)
    {
       return Double.toString(p); // TODO: implement digits after .
    }
    public static String unsigned2hex(int p, int i)
    {
        String s=Integer.toHexString(p);
       return "00000000".substring(8-s.length()).concat(s);
    }
    public static int toInt(String s) {
        return Integer.valueOf(s).intValue();
    }
    public static float toFloat(String s) {
        return Float.parseFloat(s);
    }
    public static double toDouble(String s) {
        return Double.parseDouble(s);
    }
    public static double longBitsToDouble(long l) {
        return Double.longBitsToDouble(l);
    }

    public static float toFloatBitwise(int l) {
        return Float.intBitsToFloat(l);
    }
    public static int toIntBitwise(float f) {
        return Float.floatToIntBits(f);
    }
}
