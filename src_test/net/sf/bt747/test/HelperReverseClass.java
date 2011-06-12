package net.sf.bt747.test;

import net.sf.bt747.j2se.system.*;

public class HelperReverseClass {

	private static final String ZEROSTRING = "0000000000000000";

	public static final String unsigned2hex(final long p, final int i) {
		final String s = Long.toHexString(p).toUpperCase();
		if (s.length() == i) {
			return s;
		} else if (s.length() < i) {
			return ZEROSTRING.substring(ZEROSTRING.length() - i + s.length())
					.concat(s);
		} else {
			return s.substring(s.length() - i);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		{
			Float a;
			a = 52.290949f;
			System.out.println(unsigned2hex(Float.floatToIntBits(a), 8));
			a = 4.866203f;
			System.out.println(unsigned2hex(Float.floatToIntBits(a), 8));
		}
		{
			Double a;
			a = 52.290949;
			System.out.println(unsigned2hex(Double.doubleToLongBits(a), 16));
			a = 4.866203;
			System.out.println(unsigned2hex(Double.doubleToLongBits(a), 16));
		}
	}
}
