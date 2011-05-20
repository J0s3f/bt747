/**
 * June 10, 2010.
 * Copyright 2007-2010 Mario De Weerd.
 *******************************************************************
 * The General Public License Version 3 applies to this file
 * unless you have another written agreement from the copyright
 * owner.
 *
 * Software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE
 * IS ASSUMED BY THE USER.
 * See the GNU General Public License Version 3 for details.
 */
package gps.convert;

import gps.log.out.CommonOut;
import bt747.sys.I18N;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Time;

/**
 * Implement some conversion functions
 * 
 * @author Mario De Weerd
 */
public final class Conv {
	/**
	 * Days in a month that is not a leap year. January = index 0.
	 */
	final static private byte daysInMonth[] = new byte[] { 31, 28, 31, 30, 31,
			30, 31, 31, 30, 31, 30, 31 };
	private static final String[] MONTHS_AS_SHORTTEXT = { "JAN", "FEB", "MAR",
			"APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	private static final String[] MONTHS_AS_LONGTEXT = { "January", "February",
			"March", "April", "May", "June", "July", "August", "September",
			"October", "November", "December" };
	private static final String[] WEEKDAYS_AS_SHORTTEXT = { "Sun", "Mon",
			"Tue", "Wed", "Thu", "Fri", "Sat" };
	private static final String[] WEEKDAYS_AS_LONGTEXT = { "Sunday", "Monday",
			"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

	/**
	 * Convert a string in hexadecimal to a list of bytes
	 * 
	 * @param hexStr
	 *            Hexadecimal representation of bytes
	 * @return list of bytes
	 */
	public static final int hexStringToBytes(final String hexStr,
			final byte[] buffer) {
		final int length = hexStr.length() & 0xFFFFFFFE;
		byte byteVal;
		for (int i = 0; i < length; i += 2) {
			switch (hexStr.charAt(i)) {
			case '0':
				byteVal = (byte) 0x00;
				break;
			case '1':
				byteVal = (byte) 0x10;
				break;
			case '2':
				byteVal = (byte) 0x20;
				break;
			case '3':
				byteVal = (byte) 0x30;
				break;
			case '4':
				byteVal = (byte) 0x40;
				break;
			case '5':
				byteVal = (byte) 0x50;
				break;
			case '6':
				byteVal = (byte) 0x60;
				break;
			case '7':
				byteVal = (byte) 0x70;
				break;
			case '8':
				byteVal = (byte) 0x80;
				break;
			case '9':
				byteVal = (byte) 0x90;
				break;
			case 'a':
			case 'A':
				byteVal = (byte) 0xA0;
				break;
			case 'b':
			case 'B':
				byteVal = (byte) 0xB0;
				break;
			case 'c':
			case 'C':
				byteVal = (byte) 0xC0;
				break;
			case 'd':
			case 'D':
				byteVal = (byte) 0xD0;
				break;
			case 'e':
			case 'E':
				byteVal = (byte) 0xE0;
				break;
			case 'f':
			case 'F':
				byteVal = (byte) 0xF0;
				break;
			default:
				byteVal = 0;
			}
			switch (hexStr.charAt(i + 1)) {
			case '0':
				// byteVal |= (byte) 0;
				break;
			case '1':
				byteVal |= (byte) 1;
				break;
			case '2':
				byteVal |= (byte) 2;
				break;
			case '3':
				byteVal |= (byte) 3;
				break;
			case '4':
				byteVal |= (byte) 4;
				break;
			case '5':
				byteVal |= (byte) 5;
				break;
			case '6':
				byteVal |= (byte) 6;
				break;
			case '7':
				byteVal |= (byte) 7;
				break;
			case '8':
				byteVal |= (byte) 8;
				break;
			case '9':
				byteVal |= (byte) 9;
				break;
			case 'a':
			case 'A':
				byteVal |= (byte) 0xA;
				break;
			case 'b':
			case 'B':
				byteVal |= (byte) 0xB;
				break;
			case 'c':
			case 'C':
				byteVal |= (byte) 0xC;
				break;
			case 'd':
			case 'D':
				byteVal |= (byte) 0xD;
				break;
			case 'e':
			case 'E':
				byteVal |= (byte) 0xE;
				break;
			case 'f':
			case 'F':
				byteVal |= (byte) 0xF;
				break;
			default:
				break;
			}
			buffer[i >> 1] = byteVal;
		}

		return length;
	}

	/**
	 * Convert a string in hexadecimal to the corresponding int.
	 * 
	 * @param hexStr
	 *            Hexadecimal representation of bytes
	 * @return list of bytes
	 */
	public static final int hex2Int(final String hexStr) {
		final int length = hexStr.length();
		int result = 0;
		for (int i = 0; i < length; i++) {
			result <<= 4;
			switch (hexStr.charAt(i)) {
			case '0':
				result += 0;
				break;
			case '1':
				result += 1;
				break;
			case '2':
				result += 2;
				break;
			case '3':
				result += 3;
				break;
			case '4':
				result += 4;
				break;
			case '5':
				result += 5;
				break;
			case '6':
				result += 6;
				break;
			case '7':
				result += 7;
				break;
			case '8':
				result += 8;
				break;
			case '9':
				result += 9;
				break;
			case 'a':
			case 'A':
				result += 0xA;
				break;
			case 'b':
			case 'B':
				result += 0xB;
				break;
			case 'c':
			case 'C':
				result += 0xC;
				break;
			case 'd':
			case 'D':
				result += 0xD;
				break;
			case 'e':
			case 'E':
				result += 0xE;
				break;
			case 'f':
			case 'F':
				result += 0xF;
				break;
			default:
				result += 0;
			}
		}
		return result;
	}

	/**
	 * Convert a string in hexadecimal to the corresponding int. Consider hex
	 * signed.
	 * 
	 * 
	 * @param hexStr
	 *            Hexadecimal representation of bytes
	 * @return list of bytes
	 */
	public static final int hex2SignedInt(final String hexStr) {
		int result = Conv.hex2Int(hexStr);
		if (result > 0) {
			final int highNibble = (result >> (4 * (hexStr.length() - 1)));
			if (highNibble >= 8) {
				// sign extension
				result |= (-1 << (32 - 4 * hexStr.length()));
			}
		}
		return result;
	}

	/**
	 * Expand date specification in string.<br>
	 * Follows 'php' style for date() except that each field is preceded with %.<br>
	 * 
	 * <pre>
	 * format character	Description	Example returned values
	 * Day	---	---
	 * d	Day of the month, 2 digits with leading zeros	01 to 31
	 * D	A textual representation of a day, three letters	Mon through Sun
	 * j	Day of the month without leading zeros	1 to 31
	 * l (lower case 'L')	A full textual representation of the day of the week	Sunday through Saturday
	 * N	ISO-8601 numeric representation of the day of the week (added in PHP 5.1.0)	1 (for Monday) through 7 (for Sunday)
	 * S	English ordinal suffix for the day of the month, 2 characters	st, nd, rd or th. Works well with j
	 * w	Numeric representation of the day of the week	0 (for Sunday) through 6 (for Saturday)
	 * z	The day of the year (starting from 0)	0 through 365
	 * Week	---	---
	 * W	ISO-8601 week number of year, weeks starting on Monday (added in PHP 4.1.0)	Example: 42 (the 42nd week in the year)
	 * Month	---	---
	 * F	A full textual representation of a month, such as January or March	January through December
	 * m	Numeric representation of a month, with leading zeros	01 through 12
	 * M	A short textual representation of a month, three letters	Jan through Dec
	 * n	Numeric representation of a month, without leading zeros	1 through 12
	 * t	Number of days in the given month	28 through 31
	 * Year	---	---
	 * L	Whether it's a leap year	1 if it is a leap year, 0 otherwise.
	 * o	ISO-8601 year number. This has the same value as Y, except that if the ISO week number (W) belongs to the previous or next year, that year is used instead. (added in PHP 5.1.0)	Examples: 1999 or 2003
	 * Y	A full numeric representation of a year, 4 digits	Examples: 1999 or 2003
	 * y	A two digit representation of a year	Examples: 99 or 03
	 * Time	---	---
	 * a	Lowercase Ante meridiem and Post meridiem	am or pm
	 * A	Uppercase Ante meridiem and Post meridiem	AM or PM
	 * B	Swatch Internet time	000 through 999
	 * g	12-hour format of an hour without leading zeros	1 through 12
	 * G	24-hour format of an hour without leading zeros	0 through 23
	 * h	12-hour format of an hour with leading zeros	01 through 12
	 * H	24-hour format of an hour with leading zeros	00 through 23
	 * i	Minutes with leading zeros	00 to 59
	 * s	Seconds, with leading zeros	00 through 59
	 * u	Microseconds (added in PHP 5.2.2)	Example: 654321
	 * Timezone	---	---
	 * e	Timezone identifier (added in PHP 5.1.0)	Examples: UTC, GMT, Atlantic/Azores
	 * I (capital i)	Whether or not the date is in daylight saving time	1 if Daylight Saving Time, 0 otherwise.
	 * O	Difference to Greenwich time (GMT) in hours	Example: +0200
	 * P	Difference to Greenwich time (GMT) with colon between hours and minutes (added in PHP 5.1.3)	Example: +02:00
	 * T	Timezone abbreviation	Examples: EST, MDT ...
	 * Z	Timezone offset in seconds. The offset for timezones west of UTC is always negative, and for those east of UTC is always positive.	-43200 through 50400
	 * Full Date/Time	---	---
	 * c	ISO 8601 date (added in PHP 5)	2004-02-12T15:19:21+00:00
	 * r	» RFC 2822 formatted date	Example: Thu, 21 Dec 2000 16:01:07 +0200
	 * U	Seconds since the Unix Epoch (January 1 1970 00:00:00 GMT)	See also time()
	 * </pre>
	 * 
	 * @param orgStr
	 * @return
	 */
	public static final String expandDate(final String orgStr, final BT747Time t) {
		StringBuffer newStr = new StringBuffer(20);
		for (int i = 0; i < orgStr.length(); i++) {
			char c = orgStr.charAt(i);
			if (c != '%') {
				newStr.append(c);
			} else {
				if (i + 1 >= orgStr.length()) {
					// Last character in string.
					newStr.append(c);
				} else {
					// Ranse already checked.
					i++;
					char f = orgStr.charAt(i);
					switch (f) {
					// Decode special token
					// Day --- ---
					case 'd': // Day of the month, 2 digits with leading zeros
						// 01 to 31
					{
						int d = t.getDay();
						if (d < 10) {
							newStr.append('0');
						}
						newStr.append(d);
					}
						break;
					case 'D': // A textual representation of a day, three
						// letters Mon through Sun
						newStr.append(idxToWeekdayShortStr(ExternalUtils
								.getDayOfWeek(t.getDay(), t.getMonth(), t
										.getYear())));
						break;
					case 'j': // Day of the month without leading zeros 1 to 31
					{
						int d = t.getDay();
						newStr.append(d);
					}
						break;
					case 'l': // (lower case 'L') A full textual representation
						// of the day of the week Sunday through
						// Saturday
						newStr.append(idxToWeekdayStr(ExternalUtils
								.getDayOfWeek(t.getDay(), t.getMonth(), t
										.getYear())));
						break;
					case 'N': // ISO-8601 numeric representation of the day of
						// the week (added in PHP 5.1.0) 1 (for Monday)
						// through 7 (for Sunday)
					{
						int d = ExternalUtils.getDayOfWeek(t.getDay(), t
								.getMonth(), t.getYear());
						if (d == 0) {
							d = 7;
						}
						newStr.append(d);
					}
						break;
					case 'S': // English ordinal suffix for the day of the
						// month, 2 characters st, nd, rd or th. Works
						// well with j
					{
						int d = t.getDay();
						switch (d) {
						case 1:
						case 21:
						case 31:
							newStr.append("st");
							break;
						case 2:
						case 14:
						case 15:
						case 16:
						case 17:
						case 18:
						case 19:
						case 22:
							newStr.append("nd");
							break;
						case 3:
						case 23:
							newStr.append("rd");
							break;
						case 4:
						case 5:
						case 6:
						case 7:
						case 8:
						case 9:
						case 10:
						case 11:
						case 12:
						case 20:
						case 24:
						case 25:
						case 26:
						case 27:
						case 28:
						case 29:
						case 30:
							newStr.append("th");
							break;
						}
					}
						break;
					case 'w': // Numeric representation of the day of the week 0
						// (for Sunday) through 6 (for Saturday)
					{
						int d = ExternalUtils.getDayOfWeek(t.getDay(), t
								.getMonth(), t.getYear());
						newStr.append(d);
					}
						break;
					case 'z': // The day of the year (starting from 0) 0 through
						// 365
					{
						int diff = JavaLibBridge.getDateInstance(t.getDay(),
								t.getMonth(), t.getYear()).dateToUTCepoch1970()
								- JavaLibBridge.getDateInstance(1, 1,
										t.getYear()).dateToUTCepoch1970();
						newStr.append(diff);
					}
						break;
					// Week --- ---
					case 'W': // ISO-8601 week number of year, weeks starting on
						// Monday (added in PHP 4.1.0) Example: 42 (the
						// 42nd week in the year)
					{
						int weekday = ExternalUtils.getDayOfWeek(1, 1, t
								.getYear());
						int diff = JavaLibBridge.getDateInstance(t.getDay(),
								t.getMonth(), t.getYear()).dateToUTCepoch1970()
								/ (24 * 3600)
								- JavaLibBridge.getDateInstance(1, 1,
										t.getYear()).dateToUTCepoch1970()
								/ (24 * 3600);
						newStr.append((diff + 7 - weekday) / 7);
					}
						break;
					// Month --- ---
					case 'F': // A full textual representation of a month, such
						// as January or March January through December
						newStr.append(idxToMonthStr(t.getMonth() - 1));
						break;
					case 'm': // Numeric representation of a month, with leading
						// zeros 01 through 12
					{
						int d = t.getMonth();
						if (d < 10) {
							newStr.append('0');
						}
						newStr.append(d);
					}
						break;
					case 'M': // A short textual representation of a month,
						// three letters Jan through Dec
					{
						String a = idxToShortMonthStr(t.getMonth() - 1);
						newStr.append(a.charAt(0));
						newStr.append(a.substring(1).toLowerCase());
					}
						break;
					case 'n': // Numeric representation of a month, without
						// leading zeros 1 through 12
					{
						int d = t.getDay();
						newStr.append(d);
					}
						break;
					case 't': // Number of days in the given month 28 through 31
						newStr
								.append(getDaysInMonth(t.getMonth(), t
										.getYear()));
						break;
					// Year --- ---
					case 'L': // Whether it's a leap year 1 if it is a leap
						// year, 0 otherwise.
					{
						// TODO:
					}
						break;
					case 'o': // ISO-8601 year number. This has the same value
						// as Y, except that if the ISO week number (W)
						// belongs to the previous or next year, that
						// year is used instead. (added in PHP 5.1.0)
						// Examples: 1999 or 2003
					{
						// TODO:
					}
						break;
					case 'Y': // A full numeric representation of a year, 4
						// digits Examples: 1999 or 2003
						newStr.append(t.getYear());
						break;
					case 'y': // A two digit representation of a year Examples:
						// 99 or 03
						newStr.append(t.getYear() % 100);
						break; // * Time --- ---
					case 'a': // Lowercase Ante meridiem and Post meridiem am or
						// pm
						if (t.getHour() < 12) {
							newStr.append("am");
						} else {
							newStr.append("pm");
						}
						break;
					case 'A': // Uppercase Ante meridiem and Post meridiem AM or
						// PM
						if (t.getHour() < 12) {
							newStr.append("AM");
						} else {
							newStr.append("PM");
						}
						break;
					case 'B': // Swatch Internet time 000 through 999
					{
						// TODO:
					}
						break;
					case 'g': // 12-hour format of an hour without leading zeros
						// 1 through 12
						newStr.append(t.getHour() % 12);
						break;
					case 'G': // 24-hour format of an hour without leading zeros
						// 0 through 23
						newStr.append(t.getHour());
						break;
					case 'h': // 12-hour format of an hour with leading zeros 01
						// through 12
					{
						int h = t.getHour() % 12;
						if (h < 10) {
							newStr.append('0');
						}
						newStr.append(h);
					}
						break;
					case 'H': // 24-hour format of an hour with leading zeros 00
						// through 23
					{
						int h = t.getHour();
						if (h < 10) {
							newStr.append('0');
						}
						newStr.append(h);
					}
						break;
					case 'i': // Minutes with leading zeros 00 to 59
					{
						int h = t.getMinute();
						if (h < 10) {
							newStr.append('0');
						}
						newStr.append(h);
					}
						break;
					case 's': // Seconds, with leading zeros 00 through 59
					{
						int h = t.getSecond();
						if (h < 10) {
							newStr.append('0');
						}
						newStr.append(h);
					}
						break;
					case 'u': // Microseconds (added in PHP 5.2.2) Example:
						// 654321
					{
						// TODO:
					}
						break;
					// Timezone --- ---
					case 'e': // Timezone identifier (added in PHP 5.1.0)
						// Examples: UTC, GMT, Atlantic/Azores
					{
						// TODO:
					}
						break;
					case 'I': // (capital i) Whether or not the date is in
						// daylight saving time 1 if Daylight Saving
						// Time, 0 otherwise.
					{
						// TODO:
					}
						break;
					case 'O': // Difference to Greenwich time (GMT) in hours
						// Example: +0200
					case 'P': // Difference to Greenwich time (GMT) with colon
						// between hours and minutes (added in PHP
						// 5.1.3) Example: +02:00
					{
						// TODO:
					}
						break;
					case 'T': // Timezone abbreviation Examples: EST, MDT ...
					{
						// TODO:
					}
						break;
					case 'Z': // Timezone offset in seconds. The offset for
						// timezones west of UTC is always negative, and
						// for those east of UTC is always positive.
						// -43200 through 50400
					{
						// TODO:
					}
						break;
					// Full Date/Time --- ---
					case 'c': // ISO 8601 date (added in PHP 5)
						// 2004-02-12T15:19:21+00:00
						newStr.append(CommonOut.getDateTimeISO8601(t, 0));
						// TODO: Timezone
						break;
					case 'r': // RFC 2822 formatted date Example: Thu, 21 Dec
						// 2000 16:01:07 +0200
					{
						// TODO:
					}
						break;
					case 'U': // Seconds since the Unix Epoch (January 1 1970
						// 00:00:00 GMT) See also time()
					{
						int s = JavaLibBridge.getDateInstance(t.getDay(),
								t.getMonth(), t.getYear()).dateToUTCepoch1970()
								+ t.getHour()
								* 3600
								+ t.getMinute()
								* 60
								+ t.getSecond();
						newStr.append(s);
					}
						break;
					default:
						newStr.append(f);
					}
				}
			}
		}
		return newStr.toString();
	}

	final static private int getDaysInMonth(final int month, final int year) {
		final int idx = month - 1;
		int leapMonth;
		if (idx == 1 && Conv.isLeapYear(year)) {
			leapMonth = 1;
		} else {
			leapMonth = 0;
		}
		return daysInMonth[idx] + leapMonth;
	}

	private final static boolean isLeapYear(final int year) {
		// Leap year happens when year can be divided by 4, or by 400 but not on
		// other years where it can be divided by 100.
		return ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)));
	}

	public static final String idxToShortMonthStr(final int i) {
		return I18N.i18n(MONTHS_AS_SHORTTEXT[i]);
	}

	public static final String idxToMonthStr(final int i) {
		return I18N.i18n(MONTHS_AS_LONGTEXT[i]);
	}

	public static final String idxToWeekdayShortStr(final int i) {
		return I18N.i18n(WEEKDAYS_AS_SHORTTEXT[i]);
	}

	public static final String idxToWeekdayStr(final int i) {
		return I18N.i18n(WEEKDAYS_AS_LONGTEXT[i]);
	}
}
