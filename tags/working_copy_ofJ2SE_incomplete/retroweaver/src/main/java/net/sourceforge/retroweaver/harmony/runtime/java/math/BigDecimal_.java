/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sourceforge.retroweaver.harmony.runtime.java.math;

import java.math.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.sourceforge.retroweaver.harmony.runtime.internal.nls.Messages;
//import org.apache.harmony.math.internal.nls.Messages;

/**
 * @author Intel Middleware Product Division
 * @author Instituto Tecnologico de Cordoba
 */
public class BigDecimal_ {

    private BigDecimal_() {
        // private constructor
    }

    /* Static Fields */

    /** @ar.org.fitc.spec_ref */
    public static final BigDecimal ZERO = BigDecimal.valueOf(0); //new BigDecimal(0, 0);

    /** @ar.org.fitc.spec_ref */
    public static final BigDecimal ONE = BigDecimal.valueOf(1); //new BigDecimal(1, 0);

    /** @ar.org.fitc.spec_ref */
    public static final BigDecimal TEN = BigDecimal.valueOf(10); //new BigDecimal(10, 0);

    /** The double closer to <code>Log10(2)</code>. */
    private static final double LOG10_2 = 0.3010299956639812;

    /**
     * An array with powers of five that fit in the type <code>long</code>
     * (<code>5^0,5^1,...,5^27</code>)
     */
    private static final BigInteger FIVE_POW[];

    /**
     * An array with powers of ten that fit in the type <code>long</code>
     * (<code>10^0,10^1,...,10^18</code>)
     */
    private static final BigInteger TEN_POW[];

    static {
        // To fill all static arrays.

        // Taking the references of useful powers.
        TEN_POW = Multiplication.bigTenPows;
        FIVE_POW = Multiplication.bigFivePows;
    }

    public static BigDecimal BigDecimal(char[] in, int offset, int len) {
    	return new BigDecimal(new String(in, offset, len));
    }

    public static BigDecimal BigDecimal(char[] in) {
    	return new BigDecimal(new String(in, 0, in.length));
    }

    public static BigDecimal BigDecimal(int val) {
        // replace int constructor with double one after convertion i2d, no loss of precision
        double d = val;
        return new BigDecimal(d);
    }

    public static BigDecimal BigDecimal(long val) {
        // longs cannot be converted to double, use toString() instead
        String s = Long.toString(val);
        return new BigDecimal(s);
    }

    public static BigDecimal valueOf(double val) {
        if (Double.isInfinite(val) || Double.isNaN(val)) {
            // math.03=Infinity or NaN
            throw new NumberFormatException(Messages.getString("math.03")); //$NON-NLS-1$
        }
        return new BigDecimal(Double.toString(val));
    }

    public static BigDecimal divide(BigDecimal o, BigDecimal divisor, int scale, RoundingMode roundingMode) {
        return o.divide(divisor, scale, roundingMode.ordinal());
    }

    public static BigDecimal divide(BigDecimal o, BigDecimal divisor, RoundingMode roundingMode) {
	    return o.divide(divisor, roundingMode.ordinal());
    }

    public static BigDecimal divide(BigDecimal o, BigDecimal divisor) {
        BigInteger p = o.unscaledValue();
        BigInteger q = divisor.unscaledValue();
        BigInteger gcd; // greatest common divisor between 'p' and 'q'
        BigInteger quotAndRem[];
        long diffScale = (long)o.scale() - divisor.scale();
        int newScale; // the new scale for final quotient
        int k; // number of factors "2" in 'q'
        int l = 0; // number of factors "5" in 'q'
        int i = 1;
        int lastPow = FIVE_POW.length - 1;

        if (isZero(divisor)) {
            // math.04=Division by zero
            throw new ArithmeticException(Messages.getString("math.04")); //$NON-NLS-1$
        }
        if (p.signum() == 0) {
            return zeroScaledBy(diffScale);
        }
        // To divide both by the GCD
        gcd = p.gcd(q);
        p = p.divide(gcd);
        q = q.divide(gcd);
        // To simplify all "2" factors of q, dividing by 2^k
        k = q.getLowestSetBit();
        q = q.shiftRight(k);
        // To simplify all "5" factors of q, dividing by 5^l
        do {
            quotAndRem = q.divideAndRemainder(FIVE_POW[i]);
            if (quotAndRem[1].signum() == 0) {
                l += i;
                if (i < lastPow) {
                    i++;
                }
                q = quotAndRem[0];
            } else {
                if (i == 1) {
                    break;
                }
                i = 1;
            }
        } while (true);
        // If  abs(q) != 1  then the quotient is periodic
        if (!q.abs().equals(BigInteger.ONE)) {
            // math.05=Non-terminating decimal expansion; no exact representable decimal result.
            throw new ArithmeticException(Messages.getString("math.05")); //$NON-NLS-1$
        }
        // The sign of the is fixed and the quotient will be saved in 'p'
        if (q.signum() < 0) {
            p = p.negate();
        }
        // Checking if the new scale is out of range
        newScale = toIntScale(diffScale + Math.max(k, l));
        // k >= 0  and  l >= 0  implies that  k - l  is in the 32-bit range
        i = k - l;
        
        p = (i > 0) ? Multiplication.multiplyByFivePow(p, i)
        : p.shiftLeft(-i);
        return new BigDecimal(p, newScale);
    }

    public static BigDecimal divideToIntegralValue(BigDecimal o, BigDecimal divisor) {
        BigInteger integralValue; // the integer of result
        BigInteger powerOfTen; // some power of ten
        BigInteger quotAndRem[] = {o.unscaledValue()};
        long newScale = (long)o.scale() - divisor.scale();
        long tempScale = 0;
        int i = 1;
        int lastPow = TEN_POW.length - 1;

        if (isZero(divisor)) {
            // math.04=Division by zero
            throw new ArithmeticException(Messages.getString("math.04")); //$NON-NLS-1$
        }
        if ((aproxPrecision(divisor) + newScale > aproxPrecision(o) + 1L)
        || (isZero(o))) {
            /* If the divisor's integer part is greater than this's integer part,
             * the result must be zero with the appropriate scale */
            integralValue = BigInteger.ZERO;
        } else if (newScale == 0) {
            integralValue = o.unscaledValue().divide( divisor.unscaledValue() );
        } else if (newScale > 0) {
            powerOfTen = Multiplication.powerOf10(newScale);
            integralValue = o.unscaledValue().divide( divisor.unscaledValue().multiply(powerOfTen) );
            integralValue = integralValue.multiply(powerOfTen);
        } else {// (newScale < 0)
            powerOfTen = Multiplication.powerOf10(-newScale);
            integralValue = o.unscaledValue().multiply(powerOfTen).divide( divisor.unscaledValue() );
            // To strip trailing zeros approximating to the preferred scale
            while (!integralValue.testBit(0)) {
                quotAndRem = integralValue.divideAndRemainder(TEN_POW[i]);
                if ((quotAndRem[1].signum() == 0)
                        && (tempScale - i >= newScale)) {
                    tempScale -= i;
                    if (i < lastPow) {
                        i++;
                    }
                    integralValue = quotAndRem[0];
                } else {
                    if (i == 1) {
                        break;
                    }
                    i = 1;
                }
            }
            newScale = tempScale;
        }
        return ((integralValue.signum() == 0)
        ? zeroScaledBy(newScale)
                : new BigDecimal(integralValue, toIntScale(newScale)));
    }

    public static BigDecimal[] divideAndRemainder(BigDecimal o, BigDecimal divisor) {
        BigDecimal quotAndRem[] = new BigDecimal[2];

        quotAndRem[0] = o.divideToIntegralValue(divisor);
        quotAndRem[1] = o.subtract( quotAndRem[0].multiply(divisor) );
        return quotAndRem;
    }

    private static boolean isZero(BigDecimal o) {
        //Watch out: -1 has a bitLength=0
        //return bitLength == 0 && this.smallValue != -1;
        return o.signum() == 0;
    }

    public static int precision(BigDecimal o) {
//        // Checking if the precision already was calculated
//        if (precision > 0) {
//            return precision;
//        }
        int precision;
        int bitLength = o.unscaledValue().bitLength(); // see setUnscaledValue() was: this.bitLength;
        int decimalDigits = 1; // the precision to be calculated
        double doubleUnsc = 1;  // intVal in 'double'

        if (bitLength < 1024) {
            // To calculate the precision for small numbers
            if (bitLength >= 64) {
                doubleUnsc = o.unscaledValue().doubleValue();
            } else if (bitLength >= 1) {
                doubleUnsc = o.unscaledValue().longValue(); // see setUnscaledValue() was:smallValue;
            }
            decimalDigits += Math.log10(Math.abs(doubleUnsc));
        } else {// (bitLength >= 1024)
            /* To calculate the precision for large numbers
             * Note that: 2 ^(bitlength() - 1) <= intVal < 10 ^(precision()) */
            decimalDigits += (bitLength - 1) * LOG10_2;
            // If after division the number isn't zero, exists an aditional digit
            if (o.unscaledValue().divide(Multiplication.powerOf10(decimalDigits)).signum() != 0) {
                decimalDigits++;
            }
        }
        precision = decimalDigits;
        return precision;
    }

    public static BigDecimal setScale(BigDecimal o, int newScale, RoundingMode roundingMode) {
    	return o.setScale(newScale, roundingMode.ordinal());
    }

    public static BigDecimal stripTrailingZeros(BigDecimal o) {
        int i = 1; // 1 <= i <= 18
        int lastPow = TEN_POW.length - 1;
        long newScale = o.scale();

        if (isZero(o)) {
            return new BigDecimal("0");
        }
        BigInteger strippedBI = o.unscaledValue();
        BigInteger[] quotAndRem;
        
        // while the number is even...
        while (!strippedBI.testBit(0)) {
            // To divide by 10^i
            quotAndRem = strippedBI.divideAndRemainder(TEN_POW[i]);
            // To look the remainder
            if (quotAndRem[1].signum() == 0) {
                // To adjust the scale
                newScale -= i;
                if (i < lastPow) {
                    // To set to the next power
                    i++;
                }
                strippedBI = quotAndRem[0];
            } else {
                if (i == 1) {
                    // 'this' has no more trailing zeros
                    break;
                }
                // To set to the smallest power of ten
                i = 1;
            }
        }
        return new BigDecimal(strippedBI, toIntScale(newScale));
    }

    private static int aproxPrecision(BigDecimal o) {
      return o.precision();
    }

    /**
     * It tests if a scale of type <code>long</code> fits in 32 bits. 
     * It returns the same scale being casted to <code>int</code> type when 
     * is possible, otherwise throws an exception.
     * @param longScale a 64 bit scale.
     * @return a 32 bit scale when is possible.
     * @throws <code>ArithmeticException</code> when <code>scale</code> 
     *      doesn't fit in <code>int</code> type. 
     * @see #scale     
     */
    private static int toIntScale(long longScale) {
        if (longScale < Integer.MIN_VALUE) {
            // math.09=Overflow
            throw new ArithmeticException(Messages.getString("math.09")); //$NON-NLS-1$
        } else if (longScale > Integer.MAX_VALUE) {
            // math.0A=Underflow
            throw new ArithmeticException(Messages.getString("math.0A")); //$NON-NLS-1$
        } else {
            return (int)longScale;
        }
    }

    /**
     * It returns the value 0 with the most approximated scale of type 
     * <code>int</code>. if <code>longScale > Integer.MAX_VALUE</code> 
     * the scale will be <code>Integer.MAX_VALUE</code>; if 
     * <code>longScale < Integer.MIN_VALUE</code> the scale will be
     * <code>Integer.MIN_VALUE</code>; otherwise <code>longScale</code> is 
     * casted to the type <code>int</code>. 
     * @param longScale the scale to which the value 0 will be scaled.
     * @return the value 0 scaled by the closer scale of type <code>int</code>.
     * @see #scale
     */
    private static BigDecimal zeroScaledBy(long longScale) {
        if (longScale == (int) longScale) {
            return BigDecimal.valueOf(0,(int)longScale);
            }
        if (longScale >= 0) {
            return new BigDecimal( BigInteger.ZERO, Integer.MAX_VALUE);
        }
        return new BigDecimal( BigInteger.ZERO, Integer.MIN_VALUE);
    }

}

