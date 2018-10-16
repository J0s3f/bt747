// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to write a WPT file (OZI).
 * 
 * @author Mario De Weerd
 */
public final class GPSWPTFile extends GPSFile {

	private int posDigits;
	private int heightDigits;

	public void initialiseFile(BT747Path baseName, String extension,
			int fileSeparationFreq) {
		// TODO Auto-generated method stub
		super.initialiseFile(baseName, extension, fileSeparationFreq);
		if (getParamObject().hasParam(GPSConversionParameters.POSITION_DIGITS)) {
			posDigits = getParamObject().getIntParam(
					GPSConversionParameters.POSITION_DIGITS);
		} else {
			posDigits = 6;
		}

		if (getParamObject().hasParam(GPSConversionParameters.HEIGHT_DIGITS)) {
			heightDigits = getParamObject().getIntParam(
					GPSConversionParameters.HEIGHT_DIGITS);
		} else {
			heightDigits = 3;
		}
	}

	// Waypoint File (.wpt)
	//
	// Line 1 : File type and version information
	// Line 2 : Geodetic Datum used for the Lat/Lon positions for each waypoint
	// Line 3 : Reserved for future use
	// Line 4 : GPS Symbol set - not used yet
	//
	// Waypoint data
	//
	// One line per waypoint
	// each field separated by a comma
	// comma's not allowed in text fields, character 209 can be used instead and
	// a comma will be substituted.
	// non essential fields need not be entered but comma separators must still
	// be used (example ,,)
	// defaults will be used for empty fields
	// Any number of the last fields in a data line need not be included at all
	// not even the commas.
	// Field 1 : Number - for Lowrance/Eagles and Silva GPS receivers this is
	// the storage location (slot) of the waypoint in the gps, must be unique.
	// For other GPS receivers set this number to -1 (minus 1). For
	// Lowrance/Eagles and Silva if the slot number is not known (new waypoints)
	// set the number to -1.
	// Field 2 : Name - the waypoint name, use the correct length name to suit
	// the GPS type.
	// Field 3 : Latitude - decimal degrees.
	// Field 4 : Longitude - decimal degrees.
	// Field 5 : Date - see Date Format below, if blank a preset date will be
	// used
	// Field 6 : Symbol - 0 to number of symbols in GPS
	// Field 7 : Status - always set to 1
	// Field 8 : Map Display Format
	// Field 9 : Foreground Color (RGB value)
	// Field 10 : Background Color (RGB value)
	// Field 11 : Description (max 40), no commas
	// Field 12 : Pointer Direction
	// Field 13 : Garmin Display Format
	// Field 14 : Proximity Distance - 0 is off any other number is valid
	// Field 15 : Altitude - in feet (-777 if not valid)
	// Field 16 : Font Size - in points
	// Field 17 : Font Style - 0 is normal, 1 is bold.
	// Field 18 : Symbol Size - 17 is normal size
	// Field 19 : Proximity Symbol Position
	// Field 20 : Proximity Time
	// Field 21 : Proximity or Route or Both
	// Field 22 : File Attachment Name
	// Field 23 : Proximity File Attachment Name
	// Field 24 : Proximity Symbol Name

	public void writeFileHeader(final String s) {
		// Waypoint File (.wpt)
		//
		// Line 1 : File type and version information
		// Line 2 : Geodetic Datum used for the Lat/Lon positions for each
		// waypoint
		// Line 3 : Reserved for future use
		// Line 4 : GPS Symbol set - not used yet
		super.writeFileHeader(s);
		writeTxt("BT747 Track Point File http://www.bt747.org Version "
				+ bt747.Version.VERSION_NUMBER + "\r\n" + "WGS 84\r\n"
				+ "Reserved 3\r\n" + "\r\n"); // Symbol set.
		// 
	}

	/**
	 * Returns true when the record is used by the format.
	 * 
	 * Override parent class because only the trackpoint filter is used.
	 */
	protected boolean recordIsNeeded(final GPSRecord s) {
		return ptFilters[GPSFilter.TRKPT].doFilter(s);
	}

	// Trackpoint data
	//
	// One line per trackpoint
	// each field separated by a comma
	// non essential fields need not be entered but comma separators must
	// still
	// be used (example ,,)
	// defaults will be used for empty fields
	//
	//
	// Note that OziExplorer reads the Date/Time from field 5, the date and
	// time
	// in fields 6 & 7 are ignored.
	//
	// Example
	// -27.350436, 153.055540,1,-777,36169.6307194, 09-Jan-99, 3:08:14
	// -27.348610, 153.055867,0,-777,36169.6307194, 09-Jan-99, 3:08:14

	/**
	 * Reused StringBuffer for output construction.
	 */
	private final StringBuffer rec = new StringBuffer(1024);

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.GPSFile#WriteRecord()
	 */
	public final void writeRecord(final GPSRecord r) {
		super.writeRecord(r);

		if ((r != null) && ptFilters[GPSFilter.WAYPT].doFilter(r)) {
			rec.setLength(0);

			// Field 1 : Number - for Lowrance/Eagles and Silva GPS receivers
			// this is
			// the storage location (slot) of the waypoint in the gps, must be
			// unique.
			// For other GPS receivers set this number to -1 (minus 1). For
			// Lowrance/Eagles and Silva if the slot number is not known (new
			// waypoints)
			// set the number to -1.
			rec.append("-1,");

			// Field 2 : Name - the waypoint name, use the correct length name
			// to suit
			// the GPS type.
			rec.append(CommonOut.getRCRtype(r));
			rec.append(',');
			// Field 3 : Latitude - decimal degrees.
			if ((r.hasLatitude()) && (selectedFileFields.hasLatitude())) {
				rec.append(JavaLibBridge.toString(r.getLatitude(), posDigits));
			}
			rec.append(',');
			// Field 4 : Longitude - decimal degrees.
			if ((r.hasLongitude()) && (selectedFileFields.hasLongitude())) {
				rec.append(JavaLibBridge.toString(r.getLongitude(), posDigits));
			}
			rec.append(',');
			// Field 5 : Date - see Date Format below, if blank a preset date
			// will be
			// used
			// Date Format
			//
			// Delphi stores date and time values in the TDateTime type. The
			// integral part of a TDateTime value is the number of days that
			// have passed since 12/30/1899. The fractional part of a TDateTime
			// value is the time of day.
			//
			// Following are some examples of TDateTime values and their
			// corresponding dates and times:
			//
			// 0 - 12/30/1899 12:00 am
			// 2.75 - 1/1/1900 6:00 pm
			// -1.25 - 12/29/1899 6:00 am
			// 35065 - 1/1/1996 12:00 am
			if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
				rec
						.append(JavaLibBridge
								.toString(
										(r.getUtc() + ((r.hasMillisecond())
												&& (selectedFileFields
														.hasMillisecond()) ? (r.milisecond / 1000.0)
												: 0)) / 86400.0 + 25569, // Days
										// since
										// 30/12/1899
										7)); // 7 fractional digits
			}
			rec.append(',');
			// Field 6 : Symbol - 0 to number of symbols in GPS
			rec.append(r.getRcr());
			rec.append(',');
			// Field 7 : Status - always set to 1
			rec.append("1,");
			// Field 8 : Map Display Format
			rec.append(',');
			// Field 9 : Foreground Color (RGB value)
			rec.append(',');
			// Field 10 : Background Color (RGB value)
			rec.append(',');
			// Field 11 : Description (max 40), no commas
			if (r.hasUtc()) {
				String timeStr = "";
				timeStr += t.getYear() + "-" + (t.getMonth() < 10 ? "0" : "")
						+ t.getMonth() + "-" + (t.getDay() < 10 ? "0" : "")
						+ t.getDay() + " " + (t.getHour() < 10 ? "0" : "")
						+ t.getHour() + ":" + (t.getMinute() < 10 ? "0" : "")
						+ t.getMinute() + ":" + (t.getSecond() < 10 ? "0" : "")
						+ t.getSecond();

				if ((r.hasMillisecond())
						&& (selectedFileFields.hasMillisecond())) {
					timeStr += ".";
					timeStr += (r.milisecond < 100) ? "0" : "";
					timeStr += (r.milisecond < 10) ? "0" : "";
					timeStr += r.milisecond;
				}
				rec.append(timeStr);
				rec.append(' ');
			}
			rec.append(CommonOut.getRcrSymbolText(r));
			rec.append(',');
			// Field 12 : Pointer Direction
			// Field 13 : Garmin Display Format
			// Field 14 : Proximity Distance - 0 is off any other number is
			// valid
			// Field 15 : Altitude - in feet (-777 if not valid)
			if ((r.hasHeight()) && (selectedFileFields.hasHeight())) {
				rec.append(JavaLibBridge.toString(r.getHeight(), heightDigits));
				rec.append(',');
			} else {
				rec.append("-777,");
			}
			// Field 16 : Font Size - in points
			rec.append(',');
			// Field 17 : Font Style - 0 is normal, 1 is bold.
			rec.append(',');
			// Field 18 : Symbol Size - 17 is normal size
			rec.append(',');
			// Field 19 : Proximity Symbol Position
			rec.append(',');
			// Field 20 : Proximity Time
			rec.append(',');
			// Field 21 : Proximity or Route or Both
			rec.append(',');
			if (r.hasVoxStr()) {
				rec.append(r.getVoxStr());
			}
			// Field 22 : File Attachment Name
			rec.append(',');
			// Field 23 : Proximity File Attachment Name
			rec.append(',');
			// Field 24 : Proximity Symbol Name

			rec.append("\r\n");
			writeTxt(rec.toString());
			rec.setLength(0);
		} // s!=null
	}
}
