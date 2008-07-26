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
public class Convert {
    public static String toString(boolean p)
    {
       return waba.sys.Convert.toString(p);
    }

    public static String toString(int p)
    {
       return waba.sys.Convert.toString(p);
    }

    public static String toString(float p)
    {
       return waba.sys.Convert.toString(p);
    }
    public static String toString(double p)
    {
       return waba.sys.Convert.toString(p);
    }
    
    public static String toString(double p, int i)
    {
       return waba.sys.Convert.toString(p,i);
    }
    public static String unsigned2hex(int p, int i)
    {
       return waba.sys.Convert.unsigned2hex(p,i);
    }
    public static int toInt(String s) {
        return waba.sys.Convert.toInt(s);
    }
    public static float toFloat(String s) {
        return waba.sys.Convert.toFloat(s);
    }
    public static double toDouble(String s) {
        return waba.sys.Convert.toDouble(s);
    }
    public static double longBitsToDouble(long l) {
        return waba.sys.Convert.longBitsToDouble(l);
    }

    public static float toFloatBitwise(int l) {
        return waba.sys.Convert.toFloatBitwise(l);
    }
    public static int toIntBitwise(float f) {
        return waba.sys.Convert.toIntBitwise(f);
    }
}
