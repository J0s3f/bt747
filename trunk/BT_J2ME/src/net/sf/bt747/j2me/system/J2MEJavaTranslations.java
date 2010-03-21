// ********************************************************************
// *** BT 747 ***
// *** (c)2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// ********************************************************************
package net.sf.bt747.j2me.system;

import org.j4me.logging.Log;

import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747File;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747HttpSender;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747RAFile;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747StringTokenizer;
import bt747.sys.interfaces.BT747Thread;
import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.BT747Vector;
import bt747.sys.interfaces.JavaLibImplementation;

;

public final class J2MEJavaTranslations implements JavaLibImplementation {
	public final BT747Date getDateInstance() {
		return new J2MEDate();
	}

	public final BT747Date getDateInstance(final int d, final int m, final int y) {
		return new J2MEDate(d, m, y);
	}

	public final BT747Date getDateInstance(final String strDate,
			final byte dateFormat) {
		return new J2MEDate(strDate, dateFormat);
	}

	public final BT747Hashtable getHashtableInstance(final int initialCapacity) {
		return new J2MEHashtable(initialCapacity);
	}

	public final BT747Vector getVectorInstance() {
		return new J2MEVector();
	}

	public final BT747Time getTimeInstance() {
		return new J2METime();
	}

    public final BT747File getFileInstance(final BT747Path path) {
        return new J2MEFile(path.getPath());
    }

    public final BT747File getFileInstance(final BT747Path path,
            final int mode) {
        return new J2MEFile(path.getPath(), mode);
    }

    public final BT747RAFile getRAFileInstance(final BT747Path path) {
        return new J2MEFile(path.getPath());
    }

    public final BT747RAFile getRAFileInstance(final BT747Path path,
            final int mode) {
        return new J2MEFile(path.getPath(), mode);
    }

	public final boolean isAvailable() {
		return true;
	}

	public final void debug(final String s, final Throwable e) {
		Log.debug(s, e);
	}

	public final double pow(final double x, final double y) {
		return Float11.pow(x, y);
	}

	public final double acos(final double x) {
		return Float11.acos(x);
	}

	public final void addThread(final BT747Thread t, final boolean b) {
		J2MEGeneric.addThread(t, b);
	}

	public final void removeThread(final BT747Thread t) {
		J2MEGeneric.removeThread(t);
	}

	/**
	 * Math
	 * 
	 */
	public final String toString(final boolean p) {
		return String.valueOf(p);
	}

	public final String toString(final int p) {
		return String.valueOf(p);
	}

	public final String toString(final float p) {
		return Float.toString(p);
	}

	public final String toString(final double p) {
		return Double.toString(p);
	}

	// private final String formatString = "#0.0000000000000000";
	// private int previousFormat = 0;
	// private DecimalFormat df = new DecimalFormat("#0");
	// public final String toString(final double p, final int i) {
	// if(i!=previousFormat) {
	// if(i==0) {
	// df = new DecimalFormat("#0");
	// } else {
	// df = new DecimalFormat(formatString.substring(0,3+i));
	// }
	// }
	// return df.format(p);
	// }

	public final String toString(final double p, final int i) {
		StringBuffer s;
		if ((p >= 1.) || (p <= -1.)) {
			s = new StringBuffer(Double.toString(p));
		} else if (p >= 0.) {
			s = new StringBuffer(Double.toString(p + 1.));
			s.setCharAt(0, '0');
		} else {
			s = new StringBuffer(Double.toString(p - 1.));
			s.setCharAt(1, '0');
		}

		int dotPos;
		int diff;
		dotPos = s.toString().indexOf('.');
		diff = (dotPos + i + 1 - s.length());
		if (diff == 0) {
			// Do nothing
		} else if (diff > 0) {
			s.append(J2MEJavaTranslations.ZEROCHARS, 1, diff);
		} else if (dotPos == 0) {
			s.append(J2MEJavaTranslations.ZEROCHARS, 0, i + 1);
		} else {
			// Truncate - some limited notion of rounding.
			// TODO: generalize this.
			if (s.charAt(dotPos + i + 1) >= '5') {
				// Rounding is needed
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

	public final String unsigned2hex(final int p, final int i) {
		final String s = Integer.toHexString(p).toUpperCase();
		if (s.length() == i) {
			return s;
		} else if (s.length() < i) {
			return J2MEJavaTranslations.ZEROSTRING.substring(
					J2MEJavaTranslations.ZEROSTRING.length() - i + s.length())
					.concat(s);
		} else {
			return s.substring(s.length() - i);
		}
	}

	public final int toInt(final String s) {
		return Integer.valueOf(s).intValue();
	}

	public final float toFloat(final String s) {
		return Float.parseFloat(s);
	}

	public final double toDouble(final String s) {
		return Double.parseDouble(s);
	}

	public final double longBitsToDouble(final long l) {
		return Double.longBitsToDouble(l);
	}

	public final float toFloatBitwise(final int l) {
		return Float.intBitsToFloat(l);
	}

	public final int toIntBitwise(final float f) {
		return Float.floatToIntBits(f);
	}

	public final void debug(final String s) {
		if (Log.isDebugEnabled()) {
			Log.debug(s);
		}
	}

	private static final long appStartTime = System.currentTimeMillis();

	public final int getTimeStamp() {
		// Returns the time in ms since the program started.
		return (int) (System.currentTimeMillis() - J2MEJavaTranslations.appStartTime);
	}

	private static String appSettings = ""; // TODO: Implement other solution

	/**
	 * @return the appSettings
	 */
	public final String getAppSettings() {
		return J2MEJavaTranslations.appSettings;
	}

	/**
	 * @param settings
	 *            the appSettings to set
	 */
	public final void setAppSettings(final String settings) {
		setSettings(settings); // Avoids static warning
	}

	private final static void setSettings(final String settings) {
		J2MEJavaTranslations.appSettings = settings;
	}

	public final BT747Semaphore getSemaphoreInstance(final int value) {
		return new J2MESemaphore(value);
	}

	public final BT747StringTokenizer getStringTokenizer(final String a,
			final char b) {
		// TODO Auto-generated method stub
		return new J2MEStringTokenizer(a, b);
	}

	public final BT747HashSet getHashSetInstance() {
		// TODO Auto-generated method stub
		return new J2MEHashSet();
	}

	private BT747HttpSender httpSenderInstance = null;

	public BT747HttpSender getHttpSenderInstance() {
		if (httpSenderInstance == null) {
			httpSenderInstance = new J2MEHttpSenderImpl();
		}
		return httpSenderInstance;
	}

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.JavaLibImplementation#atan(double)
     */
    public double atan(double x) {
        return Float11.atan(x);
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.JavaLibImplementation#atan2(double, double)
     */
    public double atan2(double x, double y) {
        return Float11.atan2(x, y);
    }

}
