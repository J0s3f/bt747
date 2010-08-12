// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
// Thanks to Marcus Schmidke for modifications to the output format
// making it compatible with MapSource that requires specific field
// ordering.<br>This was undone, but implemented differently
// later by using XML validation.
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.Version;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to write a GPX file.
 * 
 * @author Mario De Weerd
 */
public class GPSGPXFile extends GPSFile {
	private final StringBuffer rec = new StringBuffer(1024); // reused
	// stringbuffer

	private boolean isWayType;
	private boolean isNewTrack = true;
	private int currentFilter;
	private String trackName = "";
	private boolean isTrkSegSplitOnlyWhenSmall = false;
	/**
	 * GPX1.1 or GPX1.0
	 */
	private boolean isGPX1_0 = true;
	/**
	 * If true, waypoints will have link to 'vox' when available.
	 */
	private boolean addLinkData = true;

	/**
     * 
     */
	public GPSGPXFile() {
		super();
		numberOfPasses = 2;
	}

	private int posDigits;
	private int heightDigits;

	private boolean isHasComment;
	private boolean isHasSymbol;

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
	 */
	public void initialiseFile(final BT747Path basename, final String ext,
			final int oneFilePerDay) {
		super.initialiseFile(basename, ext, oneFilePerDay);
		if (getParamObject().hasParam(
				GPSConversionParameters.TRACK_SPLIT_IF_SMALL_BOOL)) {
			isTrkSegSplitOnlyWhenSmall = getParamObject().getBoolParam(
					GPSConversionParameters.TRACK_SPLIT_IF_SMALL_BOOL);
		}
		if (getParamObject().hasParam(GPSConversionParameters.GPX_LINK_TEXT)) {
			addLinkData = getParamObject().getBoolParam(
					GPSConversionParameters.GPX_LINK_TEXT);
		}
		if (getParamObject().hasParam(GPSConversionParameters.GPX_1_1)) {
			isGPX1_0 = !getParamObject().getBoolParam(
					GPSConversionParameters.GPX_1_1);
		}
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
		if (getParamObject().hasParam(GPSConversionParameters.GPX_NO_COMMENT)) {
			isHasComment = !getParamObject().getBoolParam(
					GPSConversionParameters.GPX_NO_COMMENT);
		} else {
			isHasComment = true;
		}
		if (getParamObject().hasParam(GPSConversionParameters.GPX_NO_SYMBOL)) {
			isHasSymbol = !getParamObject().getBoolParam(
					GPSConversionParameters.GPX_NO_SYMBOL);
		} else {
			isHasSymbol = true;
		}

		currentFilter = GPSFilter.WAYPT;
		isWayType = true;
	}

	public boolean nextPass() {
		super.nextPass();
		if (nbrOfPassesToGo > 0) {
			// if(m_multipleFiles) {
			// closeFile();
			// }
			nbrOfPassesToGo--;
			previousDate = 0;
			isWayType = false;
			currentFilter = GPSFilter.TRKPT;
			// if(!m_multipleFiles) {
			// writeDataHeader();
			// }
			return true;
		} else {
			return false;
		}
	}

	private final String TOPO_1_0_SCHEMA = "http://www.topografix.com/GPX/1/0";
	private final String TOPO_1_1_SCHEMA = "http://www.topografix.com/GPX/1/1";

	protected final void writeFileHeader(final String Name) {
		String header;
		String schema;
		String version;
		if (isGPX1_0) {
			schema = TOPO_1_0_SCHEMA;
			version = "1.0";
		} else {
			schema = TOPO_1_1_SCHEMA;
			version = "1.1";
		}
		String name = "";
		if (!isGPX1_0) {
			name += "<metadata>";
		}
		name += "<name>" + Name + "</name>";
		if (!isGPX1_0) {
			name += "</metadata>";
		}

		header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\r\n"
				+ "<gpx xmlns=\""
				+ schema
				+

				"\"\r\n"
				+ " creator=\"BT747 V"
				+ Version.VERSION_NUMBER
				+ "\""
				+ " version=\""
				+ version
				+ "\"\r\n" // GPX version
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ " xsi:schemaLocation=\"" + schema

				+ " " + schema + "/gpx.xsd\">\r\n"
				// MS20080327 Filename element isn't understood by MapSource?!
				+ name;
		writeTxt(header);
	}

	private boolean isDataHeaderWritten = false;

	private final void writeActualDataHeader() {
		if (isWayType) {
		} else {
			String header;
			header = "<trk>";
			if (isIncludeTrkName) {
				header += "<name>" + trackName + "</name>";
			}
			header += "<trkseg>" + "\r\n";

			isDataHeaderWritten = true;
			writeTxt(header);
		}
	}

	protected final void writeDataHeader() {
		if (isWayType) {
		} else {
			isNewTrack = true;
		}
	}

	protected final void writeDataFooter() {
		String header;
		if (isWayType) {
		} else {
			if (isDataHeaderWritten) {
				header = "</trkseg>" + "</trk>" + "\r\n";
				writeTxt(header);
				isDataHeaderWritten = false;
			}
		}
	}

	protected final void writeTrkSegSplit() {
		String header;
		if (isWayType) {
		} else {
			if (isDataHeaderWritten) {
				header = "</trkseg><trkseg>";
				writeTxt(header);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.GPSFile#WriteRecord()
	 */
	private static final char[] zeros = "0000000".toCharArray();

	private boolean isNeedTrackSegment = false;

	public final void writeRecord(final GPSRecord r) {
		super.writeRecord(r);

		final boolean isNeededRecord = ptFilters[currentFilter].doFilter(r);
		boolean isNeedTrackSplit = needsToSplitTrack;
		/**
		 * Handle split of track.
		 */
		if (!isNeededRecord) {
			// The current position is not used in the track.
			// Three possibilies:
			// - No track splitting;
			// - Create a track segment;
			// - Create a new track.

			if (!isWayType // Do nothing if we are handling waypoints
			) {
				if (isTrkSegSplitOnlyWhenSmall) {
					isNeedTrackSegment = true;
				} else {
					isNeedTrackSplit |= !ignoreBadPoints;
				}
			}
		}
		if (!isNewTrack && !firstRecord) {
			// Only if we do not have a first track and we have written some
			// records.
			if (isNeedTrackSplit) {
				writeDataFooter();
				writeDataHeader();
			}
		}

		if (isNeededRecord) {
			// This log item is to be transcribed in the output file.

			String timeStr = ""; // String that will represent time
			String fixStr = ""; // String that will represent fix type
			String rcrStr = ""; // String that will represent log reason
			String hdopStr = ""; // String that will represent HDOP
			String nsatStr = ""; // String that will represent number
			// of
			// sats

			if ((r.hasValid()) && (selectedFileFields.hasValid())) {
				switch (r.getValid()) {
				case 0x0001:
					fixStr += "none"; // "No fix";
					break;
				case 0x0002:
					fixStr += "3d"; // "SPS";
					break;
				case 0x0004:
					fixStr += "dgps";
					break;
				case 0x0008:
					fixStr += "pps"; // Military signal
					break;
				case 0x0010:
					// tmp+="RTK";
					break;
				case 0x0020:
					// tmp+="FRTK";
					break;
				case 0x0040:
					// tmp+= "Estimated mode";
					break;
				case 0x0080:
					// tmp+= "Manual input mode";
					break;
				case 0x0100:
					// tmp+= "Simulator mode";
					break;
				default:
					// tmp+="Unknown mode";
				}
			}
			if ((r.hasRcr()) && (selectedFileFields.hasRcr())) {
				// For ways = track type
				rec.append("<type>");
				rcrStr = CommonOut.getRCRtype(r);
				rec.append("</type>\r\n");
			}

			if ((r.hasHdop()) && (selectedFileFields.hasHdop())) {
				hdopStr = JavaLibBridge.toString(r.getHdop() / 100.0f, 2);
			}
			if ((r.hasNsat()) && (selectedFileFields.hasNsat())) {
				nsatStr += (r.getNsat() / 256);
			}

			// StringBuffer rec=new StringBuffer(1024);
			rec.setLength(0);
			if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
				timeStr += t.getYear() + "-" + (t.getMonth() < 10 ? "0" : "")
						+ t.getMonth() + "-" + (t.getDay() < 10 ? "0" : "")
						+ t.getDay() + "T" + (t.getHour() < 10 ? "0" : "")
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
				timeStr += "Z";
			}
			if (isNewTrack) {
				final StringBuffer tx = new StringBuffer();
				if (r.hasRecCount()) {
					final String tmp = "" + r.getRecCount();
					int nZeros = 5 - tmp.length();
					if (nZeros < 0) {
						nZeros = 0;
					}
					tx.append(GPSGPXFile.zeros, 0, nZeros);
					trackName = "#" + tx.toString() + tmp + "#";
				}
				if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
					trackName += " " + timeStr;
				}

				writeActualDataHeader();
			} else if (isNeedTrackSegment) {
				writeTrkSegSplit();
			}
			// " <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
			// " <ele>12.863281</ele>"+
			// " <time>2005-05-16T11:49:06Z</time>"+
			// " <name>Cala Sant Vicen - Mallorca</name>"+
			// " <sym>City</sym>"+
			// " </wpt>"+

			// lat="latitudeType [1] ?"
			// lon="longitudeType [1] ?">
			if (isWayType) {
				rec.append("<wpt ");
			} else {
				rec.append("<trkpt ");
			}
			if ((r.hasLatitude()) && (selectedFileFields.hasLatitude())) {
				rec.append("lat=\"");
				rec.append(JavaLibBridge.toString(r.getLatitude(), posDigits));
				rec.append("\" ");
			}
			if ((r.hasLongitude()) && (selectedFileFields.hasLongitude())) {
				rec.append("lon=\"");
				rec.append(JavaLibBridge.toString(r.getLongitude(), posDigits));
				rec.append("\"");
			}
			rec.append(" >\r\n");
			// if (!isGPX1_0 && !isWayType && (r.hasHeading())
			// && (selectedFileFields.hasHeading())) {
			// rec.append("<degrees>");
			// rec.append(JavaLibBridge.toString(r.heading, 5));
			// rec.append("</degrees>\r\n");
			// }

			//
			// if(m_isWayType) {
			// rec.append("<name>"+JavaLibBridge.toString(m_recCount)+"</name>\r\n");
			// }
			// <ele> xsd:decimal </ele> [0..1] ? (elevation in meters)

			if ((r.hasHeight()) && (selectedFileFields.hasHeight())) {
				rec.append("<ele>");
				rec.append(JavaLibBridge.toString(r.getHeight(), heightDigits));
				rec.append("</ele>\r\n");
			}

			// <time> xsd:dateTime </time> [0..1] ? //2005-05-16T11:49:06Z
			if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
				rec.append("<time>");
				rec.append(timeStr);
				rec.append("</time>\r\n");
			}

			if (isGPX1_0 && !isWayType && (r.hasHeading())
					&& (selectedFileFields.hasHeading())) {
				rec.append("<course>");
				rec.append(JavaLibBridge.toString(r.getHeading(), 5));
				rec.append("</course>\r\n");
			}

			if (isGPX1_0 && !isWayType && (r.hasSpeed())
					&& (selectedFileFields.hasSpeed())) {
				rec.append("<speed>");
				rec.append(JavaLibBridge.toString(r.getSpeed() / 3.6f, 4)); // must
				// be
				// meters/second
				rec.append("</speed>\r\n");
			}

			// <magvar> degreesType </magvar> [0..1] ?
			// <geoidheight> xsd:decimal </geoidheight> [0..1] ?

			// <name> xsd:string </name> [0..1] ?
			if (isWayType || isIncludeTrkName) {
				rec.append("<name>");
				if (isWayType) {
					rec.append("wpt-");
				} else {
					rec.append("trkpt-");
				}
				if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
					rec.append(timeStr);
					if (isNewTrack) {
						trackName += " " + timeStr;
					}
				} else {
					rec.append(r.recCount);
				}
				rec.append("</name>\r\n");
			}
			// <cmt> xsd:string </cmt> [0..1] ?
			// No comments, so commented out.
			if (isHasComment
					&& ((!isWayType && isTrkComment) || (isWayType && isWayComment))
					&& (recordNbrInLogs || (fixStr.length() != 0)
							|| (rcrStr.length() != 0)
							|| (hdopStr.length() != 0) || (nsatStr.length() != 0))) {
				rec.append("<cmt>");
				rec.append("<![CDATA[");
				CommonOut.getHtml(rec, r, selectedFileFields, t,
						recordNbrInLogs, imperial);
				rec.append("]]>");
				rec.append("</cmt>\r\n");
			}

			// <desc> xsd:string </desc> [0..1] ?
			// A text description of the element. Holds additional
			// information about the element intended for the user, not
			// the
			// GPS.

			// <src> xsd:string </src> [0..1] ?
			// Source of data. Included to give user some idea of
			// reliability and accuracy of data. "Garmin eTrex", "USGS
			// quad
			// Boston North", e.g

			// <url> For waypt
			// <urlname> For waypt
			if (addLinkData) {
				if (r.hasVoxStr()
				// && selectedFileFields.hasVoxStr() //TODO
				) {
					if (isGPX1_0) {
						rec.append("<url>");
						rec.append(CommonOut.getLink(r, false));
						rec.append("</url><urlname>");
						rec.append(r.getVoxStr());
						rec.append("</urlname>\r\n");
					} else

					{
						rec.append("<link href=\"");
						rec.append(CommonOut.getLink(r, false));
						rec.append("\"><text>");
						rec.append(r.getVoxStr());
						rec.append("</text></link>\r\n");
					}
				}
			}

			// <sym> xsd:string </sym> [0..1] ?
			// Text of GPS symbol name. For interchange with other
			// programs,
			// use the exact spelling of the symbol as displayed on the
			// GPS.
			// If the GPS abbreviates words, spell them out.

			// <type> xsd:string </type> [0..1] ?
			// Type (classification) of route. (for tracks)
			// Type (classification) of waypoint. (for waypoints)

			if ((r.hasRcr()) && (selectedFileFields.hasRcr())) {
				if (isHasSymbol) {
					rec.append("<sym>");
					rec.append(CommonOut.getRcrSymbolText(r));
					rec.append("</sym>\r\n");
				}
				rec.append("<type>");
				rec.append(rcrStr);
				rec.append("</type>\r\n");
			}

			// <fix> fixType </fix> [0..1] ?
			if (fixStr.length() != 0) {
				rec.append("<fix>");
				rec.append(fixStr);
				rec.append("</fix>\r\n");
			}

			// <sat> xsd:nonNegativeInteger </sat> [0..1] ?
			if ((r.hasNsat()) && (selectedFileFields.hasNsat())) {
				rec.append("<sat>");
				rec.append(nsatStr); // Sat used
				rec.append("</sat>\r\n");
				// nsatStr+="(";
				// nsatStr+=JavaLibBridge.toString(s.nsat%256);
				// nsatStr+=")";
			}

			// <hdop> xsd:decimal </hdop> [0..1] ?
			if ((r.hasHdop()) && (selectedFileFields.hasHdop())) {
				rec.append("<hdop>");
				rec.append(hdopStr);
				rec.append("</hdop>\r\n");
			}
			// <vdop> xsd:decimal </vdop> [0..1] ?
			if ((r.hasVdop()) && (selectedFileFields.hasVdop())) {
				rec.append("<vdop>");
				rec.append(JavaLibBridge.toString(r.getVdop() / 100.0f, 2));
				rec.append("</vdop>\r\n");
			}
			// <pdop> xsd:decimal </pdop> [0..1] ?
			// // <pdop> xsd:decimal </pdop> [0..1] ?
			if ((r.hasPdop()) && (selectedFileFields.hasPdop())) {
				rec.append("<pdop>");
				rec.append(JavaLibBridge.toString(r.getPdop() / 100.0f, 2));
				rec.append("</pdop>\r\n");
			}

			// <ageofdgpsdata> xsd:decimal </ageofdgpsdata> [0..1] ?
			if ((r.hasDage()) && (selectedFileFields.hasDage())) {
				rec.append("<ageofdgpsdata>");
				rec.append(r.getDage());
				rec.append("</ageofdgpsdata>\r\n");
			}

			// <dgpsid> dgpsStationType </dgpsid> [0..1] ?
			if ((r.hasDsta()) && (selectedFileFields.hasDsta())) {
				rec.append("<dgpsid>");
				rec.append(r.getDsta());
				rec.append("</dgpsid>\r\n");
			}

			// <link> linkType </link> [0..*] ?

			if (false) {
				// <extensions> extensionsType </extensions> [0..1] ?
				if ((r.hasDistance()) && (selectedFileFields.hasDistance())) {
					rec.append("<extensions>");
					// MS 20080327 seems to be unsupported in GPX 1.0:
					// MS 20080327 don't know why but "speed" isn't
					// understood
					// by MapSource

					if ((r.hasDistance()) && (selectedFileFields.hasDistance())) {
						rec.append("<distance>");
						rec.append(JavaLibBridge.toString(r.distance, 2)); // +"
						// m\r\n"
						rec.append("</distance>\r\n");
					}
					rec.append("</extensions>");
				}
			}
			if (isWayType) {
				rec.append("</wpt>\r\n");
			} else {
				rec.append("</trkpt>\r\n");
			}
			final String ss = rec.toString();
			if (ss.length() > 21) {
				writeTxt(ss);
			}
			rec.setLength(0);

			isNewTrack = false;
			isNeedTrackSegment = false;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.GPSFile#FinaliseFile()
	 */
	public void finaliseFile() {
		if (isOpen()) {
			String footer;
			writeDataFooter();
			footer = "</gpx>";
			writeTxt(footer);
		}
		super.finaliseFile();
	}
}
