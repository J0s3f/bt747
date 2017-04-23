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
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;
import gps.tracks.PolylineEncoder;
import gps.tracks.Track;
import gps.tracks.Trackpoint;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Vector;

/**
 * Class to write a Google Maps Static URL
 * 
 * @author Mario De Weerd
 */
public final class GPSGoogleStaticMapUrl extends GPSFile {
	private BT747Vector urls = JavaLibBridge.getVectorInstance();
	/**
	 * Local StringBuffer for output.
	 */
	private final StringBuffer rec = new StringBuffer(1024); // reused
	// stringbuffer

	/**
	 * When true, currently handling waypoints, otherwise trackpoints.
	 */
	private boolean isWayType;
	private boolean isNewTrack = true;
	private int currentFilter;

	private final Track track = new Track();
	private final Track waypoints = new Track();

	@SuppressWarnings("unused")
	private int trackIndex = 0; // Index for tracks

	/**
     * 
     */
	public GPSGoogleStaticMapUrl() {
		super();
		numberOfPasses = 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
	 */
	public final void initialiseFile(final BT747Path basename,
			final String ext, final int oneFilePerDay) {
		super.initialiseFile(basename, ext, oneFilePerDay);
		currentFilter = GPSFilter.WAYPT;
		isWayType = true;

		resetTrack();
	}

	private final void resetTrack() {
		track.removeAll();
	}

	public final boolean nextPass() {
		super.nextPass();
		if (nbrOfPassesToGo > 0) {
			// if (m_multipleFiles) {
			// closeFile();
			// }
			nbrOfPassesToGo--;
			previousDate = 0;
			isWayType = false;
			currentFilter = GPSFilter.TRKPT;
			// if (!m_multipleFiles) {
			// writeDataHeader();
			// }
			return true;
		} else {
			return false;
		}
	}

	protected final void writeFileHeader(final String trackName) {
	}

	private double minlat;
	private double maxlat;
	private double minlon;
	private double maxlon;

	protected final void writeDataHeader() {
		if (isWayType) {
		} else {
			isNewTrack = true;
			minlat = 90;
			maxlat = -90;
			minlon = 180;
			maxlon = -180;
		}
	}

	private int xSize = 400;
	private int ySize = 400;
	private int maxLen = 2000;

	@SuppressWarnings("unused")
	protected final void endTrack(final String hexColor) {
		final PolylineEncoder a = new PolylineEncoder();
		BT747Hashtable res;
		String tmp;
		int maxloops = 10; // Upper limit for loops.
		do {
			if (false) {
				res = a.createEncodings(track, 17, 4);
			} else {
				res = a.dpEncode(track);
			}
			tmp = (String) res.get("encodedPoints");
			// tmp = PolylineEncoder.replace(tmp, "?", "%3D");
			tmp = PolylineEncoder.replace(tmp, "|", "%7C");
			tmp = PolylineEncoder.replace(tmp, "\\\\", "\\");
		} while ((tmp.length() > maxLen) && (maxloops-- > 0));
		// tmp = PolylineEncoder.replace(tmp, "{", "%7B");
		// tmp = PolylineEncoder.replace(tmp, "}", "%7D");
		// tmp = PolylineEncoder.replace(tmp, "`", "%60");

		if (tmp.length() >= 2) {
			rec.setLength(0);
			// Sample URL:
			// http://maps.google.com/maps/api/staticmap?size=400x400&path=weight:3|color:orange|enc:polyline_data\n"
			// +
			rec
					.append("http://maps.google.com/maps/api/staticmap?sensor=false&size=");
			rec.append(xSize);
			rec.append("x");
			rec.append(ySize);
			rec.append(keyCode());
			rec.append("&path=weight:3");
			rec.append("|color:0x");
			rec.append(goodTrackColor);
			rec.append("|enc:");
			rec.append(tmp);
			// Track assignment
			urls.addElement(rec.toString());
			Generic.debug(rec.toString());
			rec.setLength(0);
		}
		trackIndex++;
		resetTrack();
	}

	private final String keyCode() {
		final String googleKeyCode = getParamObject().getStringParam(
				GPSConversionParameters.GOOGLEMAPKEY_STRING);
		if ((googleKeyCode != null) && (googleKeyCode.length() != 0)) {
			return "&key=" + googleKeyCode;
		}
		return ""; // default
	}

	private final BT747Hashtable icons = JavaLibBridge.getHashtableInstance(10);

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.out.GPSFile#writeDataFooter()
	 */
	protected final void writeDataFooter() {
		if (isWayType) {
			if (waypoints.size() != 0) {
				rec.setLength(0);
				rec.append("var baseIcon = new GIcon(G_DEFAULT_ICON);\n"
						+ "baseIcon.iconSize = new GSize(32, 32);\n");
				final BT747Hashtable iter = icons.iterator();
				while (iter.hasNext()) {
					final Object key = iter.nextKey();
					rec.append("var ICON");
					rec.append((String) key);
					rec.append("=new GIcon(baseIcon);");
					rec.append("ICON");
					rec.append((String) key);
					rec.append(".image='");
					rec.append((String) iter.get(key));
					rec.append("';\n");
				}

				// Handle waypoints.
				for (int i = 0; i < waypoints.size(); i++) {
					rec.append(JavaLibBridge.toString(waypoints.get(i)
							.getLatDouble(), 5));
					rec.append(JavaLibBridge.toString(waypoints.get(i)
							.getLonDouble(), 5));
				}
				rec.setCharAt(rec.length() - 1, ']'); // Delete last ','
				rec.setLength(0);
				waypoints.removeAll();
			}
			resetTrack();
		} else {
			endTrack(goodTrackColor);
			splitOrEndTrack();
		}
	}

	private final void splitOrEndTrack() {
	}

	/**
	 * UTC time of previous record.
	 */
	@SuppressWarnings("unused")
	private int previousTime = 0;
	/**
	 * Record number of previous record
	 */
	@SuppressWarnings("unused")
	private int previousRec = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.GPSFile#WriteRecord()
	 */
	public final void writeRecord(final GPSRecord r) {
		super.writeRecord(r);

		if (!ptFilters[currentFilter].doFilter(r)) {
			// The track is interrupted by a removed log item.
			// Break the track in the output file
			if (!isWayType && !isNewTrack && !firstRecord && !ignoreBadPoints) {
				isNewTrack = true;
				if (track.size() != 0) {
					final Trackpoint tp = track.get(track.size() - 1);
					endTrack(goodTrackColor);
					track.addTrackpoint(tp);
				}
				// "points.push(new GPoint(3.11492833333333,45.75697))";
				// map.addOverlay(new GPolyline(points,"#960000",2,.75));
			}
			if (!isWayType && cachedRecordIsNeeded(r)) {
				// Update map boundaries
				if (r.getLatitude() < minlat) {
					minlat = r.getLatitude();
				}
				if (r.getLatitude() > maxlat) {
					maxlat = r.getLatitude();
				}
				if (r.getLongitude() < minlon) {
					minlon = r.getLongitude();
				}
				if (r.getLongitude() > maxlon) {
					maxlon = r.getLongitude();
				}
			}
		} else {
			// This log item is to be transcribed in the output file.

			if (!isWayType) {
				rec.setLength(0);
				if (isNewTrack || needsToSplitTrack) {
					isNewTrack = false;
					if ((r.hasLatitude()) && (r.hasLongitude())) {
						if (!needsToSplitTrack) {
							track.addTrackpoint(new Trackpoint(r.getLatitude(),
									r.getLongitude()));
							if ((r.hasUtc())) {
								previousTime = r.getUtc();
								previousRec = r.recCount;
							}
							endTrack(badTrackColor);
						} else {
							// points quite separated -
							// No line, but separate track
							// bt747.sys.Vm.debug(""+(s.utc-previousTime)+":"+isTimeSPlit);
							endTrack(goodTrackColor);
							splitOrEndTrack();
						}
					}
					resetTrack();
				}
			}

			if ((r.hasUtc())) {
				previousTime = r.getUtc();
				previousRec = r.getRecCount();
			}
			// " <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
			// " <ele>12.863281</ele>"+
			// " <time>2005-05-16T11:49:06Z</time>"+
			// " <name>Cala Sant Vicen� - Mallorca</name>"+
			// " <sym>City</sym>"+
			// " </wpt>"+

			// lat="latitudeType [1] ?"
			// lon="longitudeType [1] ?">
			// if (m_isWayType) {
			// rec.append("<wpt ");
			// } else {
			// rec.append("<trkpt ");
			// }
			if ((r.hasLatitude()) && (r.hasLongitude())) {
				// rec.append("points.push(new GLatLng(");
				// rec.append(JavaLibBridge.toString(s.latitude,6));
				// rec.append(',');
				// rec.append(JavaLibBridge.toString(s.longitude,6));
				// rec.append("));");
				final Trackpoint tp = new Trackpoint(r.getLatitude(), r
						.getLongitude());
				//                    
				track.addTrackpoint(tp);

				// Update map boundaries
				if (tp.getLatDouble() < minlat) {
					minlat = tp.getLatDouble();
				}
				if (tp.getLatDouble() > maxlat) {
					maxlat = tp.getLatDouble();
				}
				if (tp.getLonDouble() < minlon) {
					minlon = tp.getLonDouble();
				}
				if (tp.getLonDouble() > maxlon) {
					maxlon = tp.getLonDouble();
				}
			}

			if (isWayType && (r.hasLatitude()) && (r.hasLongitude())) {
				waypoints.addTrackpoint(new Trackpoint(r.getLatitude(), r
						.getLongitude()));
				final String rcrStr = CommonOut.getRCRstr(r);
				@SuppressWarnings("unused")
				String icon = "";
				if (icons.get(rcrStr) == null) {
					WayPointStyle style;
					if ((rcrStr.length() > 0) && (rcrStr.charAt(0) == 'X')) {
						style = CommonOut.getWayPointStyles().get(
								rcrStr.substring(1));
					} else if (rcrStr.length() > 1) {
						style = CommonOut.getWayPointStyles().get("M");
					} else {
						style = CommonOut.getWayPointStyles().get(rcrStr);
					}
					if (style != null) {
						final String url = style.getIconUrl();
						icons.put(rcrStr, url);
						icon = "ICON" + rcrStr;
					}
				} else {
					icon = "ICON" + rcrStr;
				}
			}

			// writeTxt(rec.toString());
			rec.setLength(0);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.GPSFile#FinaliseFile()
	 */
	public final void finaliseFile() {
		if (isOpen()) {
			for (int i = 0; i < urls.size(); i++) {
				writeTxt((String) urls.elementAt(i));
				writeTxt("\r\n");
			}
			writeDataFooter();
		}
		super.finaliseFile();

	}
}
