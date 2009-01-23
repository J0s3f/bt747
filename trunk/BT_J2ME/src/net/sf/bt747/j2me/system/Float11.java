package net.sf.bt747.j2me.system;

/*
 * <p>Title: Class for float-point calculations in J2ME applications CLDC 1.1</p>
 * <p>Description: Useful methods for float-point calculations which absent
 * in native Math class</p> <p>Copyright: Copyright (c) 2004 Nick Henson</p>
 * <p>Company: UNTEH</p> <p>License: Free use only for non-commercial
 * purpose</p> <p>If you want to use all or part of this class for
 * commercial applications then take into account these conditions:</p> <p>1.
 * I need a one copy of your product which includes my class with license key
 * and so on</p> <p>2. Please append my copyright information
 * henson.midp.Float (C) by Nikolay Klimchuk on 'About' screen of your product</p>
 * <p>3. If you have web site please append link <a
 * href="http://henson.newmail.ru">Nikolay Klimchuk</a> on the page with
 * description of your product</p> <p>That's all, thank you!</p> @author
 * Nikolay Klimchuk http://henson.newmail.ru
 * 
 * @version 0.5
 */

final class Float11 {
    /* Square root from 3 */
    public static final double SQRT3 = 1.732050807568877294;
    /* Log10 constant */
    public static final double LOG10 = 2.302585092994045684;
    /* ln(0.5) constant */
    public static final double LOGdiv2 = -0.6931471805599453094;

    //
    public static double acos(final double x) {
        final double f = Float11.asin(x);
        if (f == Double.NaN) {
            return f;
        }
        return Math.PI / 2 - f;
    }

    public static double asin(final double x) {
        if ((x < -1.) || (x > 1.)) {
            return Double.NaN;
        }
        if (x == -1.) {
            return -Math.PI / 2;
        }
        if (x == 1) {
            return Math.PI / 2;
        }
        return Float11.atan(x / Math.sqrt(1 - x * x));
    }

    public static double atan(final double arg) {
        double x;
        boolean signChange = false;
        boolean Invert = false;
        int sp = 0;
        double x2, a;
        // check up the sign change
        if (arg < 0.) {
            x = -arg;
            signChange = true;
        } else {
            x = arg;
        }
        // check up the invertation
        if (x > 1.) {
            x = 1 / x;
            Invert = true;
        }
        // process shrinking the domain until x<PI/12
        while (x > Math.PI / 12) {
            sp++;
            a = x + Float11.SQRT3;
            a = 1 / a;
            x = x * Float11.SQRT3;
            x = x - 1;
            x = x * a;
        }
        // calculation core
        x2 = x * x;
        a = x2 + 1.4087812;
        a = 0.55913709 / a;
        a = a + 0.60310579;
        a = a - (x2 * 0.05160454);
        a = a * x;
        // process until sp=0
        while (sp > 0) {
            a = a + Math.PI / 6;
            sp--;
        }
        // invertation took place
        if (Invert) {
            a = Math.PI / 2 - a;
        }
        // sign change took place
        if (signChange) {
            a = -a;
        }
        //
        return a;
    }

    public static double atan2(final double y, final double x) {
        // if x=y=0
        if ((y == 0.) && (x == 0.)) {
            return 0.;
        }
        // if x>0 atan(y/x)
        if (x > 0.) {
            return Float11.atan(y / x);
        }
        // if x<0 sign(y)*(pi - atan(|y/x|))
        if (x < 0.) {
            if (y < 0.) {
                return -(Math.PI - Float11.atan(y / x));
            } else {
                return Math.PI - Float11.atan(-y / x);
            }
        }
        // if x=0 y!=0 sign(y)*pi/2
        if (y < 0.) {
            return -Math.PI / 2.;
        } else {
            return Math.PI / 2.;
        }
    }

    public static double exp(double x) {
        if (x == 0.) {
            return 1.;
        }
        //
        double f = 1;
        final long d = 1;
        double k;
        final boolean isless = (x < 0.);
        if (isless) {
            x = -x;
        }
        k = x / d;
        //
        for (long i = 2; i < 50; i++) {
            f = f + k;
            k = k * x / i;
        }
        //
        if (isless) {
            return 1 / f;
        } else {
            return f;
        }
    }

    private static double _log(double x) {
        if (!(x > 0.)) {
            return Double.NaN;
        }
        //
        double f = 0.0;
        //
        int appendix = 0;
        while ((x > 0.0) && (x <= 1.0)) {
            x *= 2.0;
            appendix++;
        }
        //
        x /= 2.0;
        appendix--;
        //
        final double y1 = x - 1.;
        double y2 = x + 1.;
        final double y = y1 / y2;
        //
        double k = y;
        y2 = k * y;
        //
        for (long i = 1; i < 50; i += 2) {
            f += k / i;
            k *= y2;
        }
        //
        f *= 2.0;
        for (int i = 0; i < appendix; i++) {
            f += Float11.LOGdiv2;
        }
        //
        return f;
    }

    public static double log(double x) {
        if (!(x > 0.)) {
            return Double.NaN;
        }
        //
        if (x == 1.0) {
            return 0.0;
        }
        // Argument of _log must be (0; 1]
        if (x > 1.) {
            x = 1 / x;
            return -Float11._log(x);
        }//
        return Float11._log(x);
    }

    public static double log10(final double x) {
        return Float11.log(x) / Float11.LOG10;
    }

    /*
     * static public double log10(double x) { if(!(x>0.)) return Double.NaN; //
     * boolean neg=false; if(x<0) { neg=true; x=-x; } // int index=0;
     * if(x>1.) { // Great 1 while(x>1.) { x=x/10; index++; } } else { // Less
     * 1 while(x<1.) { x=x*10.; index--; } } // double res=index; if(x!=1.)
     * res=res+(log(x)/LOG10); // if(neg) return 1./res; else return res; }
     */
    public static double pow(final double x, final double y) {
        if (x == 0.) {
            return 0.;
        }
        if (x == 1.) {
            return 1.;
        }
        if (y == 0.) {
            return 1.;
        }
        if (y == 1.) {
            return x;
        }
        //
        long l = (long) Math.floor(y);
        final boolean integerValue = (y == l);
        //
        if (integerValue) {
            boolean neg = false;
            if (y < 0.) {
                neg = true;
            }
            //
            double result = x;
            for (long i = 1; i < (neg ? -l : l); i++) {
                result = result * x;
            }
            //
            if (neg) {
                return 1. / result;
            } else {
                return result;
            }
        } else {
            if (x > 0.) {
                return Float11.exp(y * Float11.log(x));
            } else {
                return Double.NaN;
            }
        }
    }
}
