/**
 * 
 */
package bt747.j2se_view;

import gps.BT747Constants;
import gps.convert.Conv;
import gps.log.GPSRecord;
import gps.log.in.CommonIn;
import gps.log.in.GPSFileConverterInterface;
import gps.log.in.GPSLogConvertInterface;
import gps.log.out.AllWayPointStyles;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.bt747.j2se.system.J2SEJavaTranslations;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bt747.model.Model;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 * 
 */
public class GPXLogConvert extends GPSLogConvertInterface {

	private GPSRecord activeFields = GPSRecord.getLogFormatRecord(0);

	/*
	 * (non-Javadoc)
	 * 
	 * @seegps.log.in.GPSLogConvertInterface#parseFile(gps.log.in.
	 * GPSFileConverterInterface)
	 */
	public int parseFile(final Object file,
			final GPSFileConverterInterface gpsFile) {
		Node gpx = (Node) file;
		final NodeList gpxElements = gpx.getChildNodes();
		int reccount = 0;
		for (int i = 0; i < gpxElements.getLength(); i++) {
			final Node n = gpxElements.item(i);
			if (n.getNodeName().equalsIgnoreCase("trk")) {
				final NodeList trk = n.getChildNodes();
				for (int j = 0; j < trk.getLength(); j++) {
					final Node seg = trk.item(j);
					if (seg.getNodeName().equalsIgnoreCase("trkseg")) {
						final NodeList segs = seg.getChildNodes();
						for (int x = 0; x < segs.getLength(); x++) {
							final Node pt = segs.item(x);
							if (pt.getNodeName().equalsIgnoreCase("trkpt")) {
								final GPSRecord r = convertNodeToGPSRecord(pt);
								r.recCount = ++reccount;
								if (!r.equalsFormat(activeFields)) {
									activeFields = r.cloneRecord();
									if (!passToFindFieldsActivatedInLog) {
										gpsFile.writeLogFmtHeader(activeFields);

									}
								}
								if (!passToFindFieldsActivatedInLog) {
									CommonIn.adjustHeight(r,
											factorConversionWGS84ToMSL);
									gpsFile.addLogRecord(r);
								}
								// System.err.println(r.toString());
								if (stop) {
									errorInfo = "Conversion aborted";
									return BT747Constants.ERROR_READING_FILE;
								}
							}
						}
					}

				}
			}
			// n.getChildNodes().item(2);
			// System.err.println(n);
		}
		return 0;
	}

	private enum infoType {
		ELE, TIME, SPEED, HDOP, VDOP, PDOP, FIX, TYPE, SYM, COURSE, CMT, SAT, NAME, DGPSID, AGEOFDGPSDATA, LINK
	};

	private boolean passToFindFieldsActivatedInLog;
	private final GPSRecord activeFileFields = GPSRecord.getLogFormatRecord(0);

	private Node gpx;
	private int error;

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#getFileObject()
	 */
	protected Object getFileObject(BT747Path fileName) {
		if (gpx == null && File.isAvailable()) {
			try {
				final java.io.File mFile = new java.io.File(fileName.getPath());
				final DocumentBuilder builder = DocumentBuilderFactory
						.newInstance().newDocumentBuilder();
				final Document doc = builder.parse(mFile);
				final XPath xpath = XPathFactory.newInstance().newXPath();
				try {
					gpx = (Node) xpath.evaluate("//gpx", doc,
							XPathConstants.NODE);
				} catch (final Exception e) {
					Generic.debug("Did not find 'gpx' path", e);
					errorInfo = "Did not find 'gpx' path in gpx file";
					return BT747Constants.ERROR_READING_FILE;
				}
			} catch (final Exception e) {
				Generic.debug("Initialising GPX reading " + fileName, e);
				errorInfo = fileName + "\n" + e.getMessage();
				error = BT747Constants.ERROR_COULD_NOT_OPEN;
				return error;
			}
		}
		return gpx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#closeFileObject(java.lang.Object)
	 */
	protected void closeFileObject(Object o) {
		gpx = null;
		// ((File) o).close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#toGPSFile(java.lang.String,
	 * gps.log.in.GPSFileConverterInterface, int)
	 */
	public int toGPSFile(final BT747Path fileName,
			GPSFileConverterInterface gpsFile) {
		error = BT747Constants.NO_ERROR;
		Object gpx;

		gpx = getFileObject(fileName);
		if (gpx == null) {
			return error;
		}
		passToFindFieldsActivatedInLog = gpsFile
				.needPassToFindFieldsActivatedInLog();
		if (passToFindFieldsActivatedInLog) {
			error = parseFile(gpx, gpsFile);
			gpsFile.setActiveFileFields(activeFileFields);
		}
		passToFindFieldsActivatedInLog = false;
		do {
			error = parseFile(gpx, gpsFile);
		} while ((error == BT747Constants.NO_ERROR) && gpsFile.nextPass());

		closeFileObject(gpx);
		gpsFile.finaliseFile();
		if (gpsFile.getNbrFilesCreated() == 0) {
			error = BT747Constants.ERROR_NO_FILES_WERE_CREATED;
		}
		gpsFile = null;
		Generic.debug("Conversion done", null);
		return error;
	}

	/**
	 * @param pt
	 * @return
	 */
	private GPSRecord convertNodeToGPSRecord(final Node pt) {
		final NodeList trkPtInfo = pt.getChildNodes();
		final GPSRecord r = GPSRecord.getLogFormatRecord(0);
		final NamedNodeMap latlon = pt.getAttributes();
		for (int i = 0; i < latlon.getLength(); i++) {
			try {
				final Node lat = latlon.getNamedItem("lat");
				if (lat != null) {
					activeFileFields.latitude = 0.0;
					r.latitude = Double.valueOf(lat.getNodeValue());
				}
			} catch (final Exception e) {
				// TODO: handle exception
			}
			try {
				final Node lon = latlon.getNamedItem("lon");
				if (lon != null) {
					activeFileFields.latitude = 0.0;
					r.longitude = Double.valueOf(lon.getNodeValue());
				}
			} catch (final Exception e) {
				// TODO: handle exception
			}
		}
		for (int y = 0; y < trkPtInfo.getLength(); y++) {
			final Node info = trkPtInfo.item(y);
			final String infoName = info.getNodeName().toUpperCase();
			if (!infoName.startsWith("#")) {
				final Node valueNode = info.getFirstChild();
				if ((valueNode != null)
						&& (valueNode.getNodeType() == Node.TEXT_NODE)) {
					final String nodeText = valueNode.getNodeValue();
					try {
						switch (infoType.valueOf(infoName)) {
						case SPEED:
							r.speed = Float.valueOf(nodeText) * 3.6f;
							activeFileFields.speed = 0;
							break;
						case COURSE:
							r.heading = Float.valueOf(nodeText);
							activeFileFields.heading = 0;
							break;
						case ELE:
							r.height = Float.valueOf(nodeText);
							activeFileFields.height = 0;
							break;
						case TIME:
							// >2008-09-15T20:25:03.000Z<
							// See NMEA???
							if (nodeText.length() >= "2008-09-15T20:25:03Z"
									.length()) {
								final int year = Integer.valueOf(nodeText
										.substring(0, 4));
								final int month = Integer.valueOf(nodeText
										.substring(5, 7));
								final int day = Integer.valueOf(nodeText
										.substring(8, 10));
								final int hour = Integer.valueOf(nodeText
										.substring(11, 13));
								final int minutes = Integer.valueOf(nodeText
										.substring(14, 16));
								final int seconds = Integer.valueOf(nodeText
										.substring(17, 19));
								int utc = JavaLibBridge.getDateInstance(day,
										month, year).dateToUTCepoch1970();
								utc += hour * 3600 + minutes * 60 + seconds;
								r.utc = utc;
								activeFileFields.utc = 50000;
								if (nodeText.length() >= "2008-09-15T20:25:03.500Z"
										.length()) {
									r.milisecond = Integer.valueOf(nodeText
											.substring(20,
													nodeText.length() - 1));
									activeFileFields.milisecond = 100;
								}
							}

							break;
						case HDOP:
							r.hdop = Math
									.round(Float.valueOf(nodeText) * 100.f);
							activeFileFields.hdop = 100;
							break;
						case VDOP:
							r.vdop = Math
									.round(Float.valueOf(nodeText) * 100.f);
							activeFileFields.vdop = 100;
							break;
						case PDOP:
							r.pdop = Math
									.round(Float.valueOf(nodeText) * 100.f);
							activeFileFields.pdop = 100;
							break;
						case FIX:
							final String fixStr = nodeText.toLowerCase();

							if (fixStr.equals("none")) {
								r.valid = BT747Constants.VALID_NO_FIX_MASK;
							} else if (fixStr.equals("3d")) {
								r.valid = BT747Constants.VALID_SPS_MASK;
							} else if (fixStr.equals("dgps")) {
								r.valid = BT747Constants.VALID_DGPS_MASK;
							} else if (fixStr.equals("pps")) {
								r.valid = BT747Constants.VALID_PPS_MASK;
							}
							activeFileFields.valid = 10;
							break;
						case TYPE:
							r.rcr = 0;
							activeFileFields.rcr = 1;
							if (nodeText.charAt(0) != 'X') {
								if (nodeText.indexOf('B', 0) != -1) {
									r.rcr |= BT747Constants.RCR_BUTTON_MASK;
								}
								if (nodeText.indexOf('T', 0) != -1) {
									r.rcr |= BT747Constants.RCR_TIME_MASK;
								}
								if (nodeText.indexOf('S', 0) != -1) {
									r.rcr |= BT747Constants.RCR_SPEED_MASK;
								}
								if (nodeText.indexOf('D', 0) != -1) {
									r.rcr |= BT747Constants.RCR_DISTANCE_MASK;
								}
								if (nodeText.indexOf('V', 0) != -1) {
									// Voice record on VGPS-900
									r.rcr = AllWayPointStyles.GEOTAG_VOICE_KEY; // TODO:
																				// change
																				// in
																				// better
									// value
								}
								if (nodeText.indexOf('C', 0) != -1) {
									// Way Point on VGPS-900
									r.rcr = AllWayPointStyles.GEOTAG_WAYPOINT_KEY; // TODO:
																					// change
																					// in
																					// better
									// value
								}
							} else {

								if (nodeText.length() == 5) {
									r.rcr = Conv.hex2Int(nodeText.substring(1));
								}
							}

							break;
						case SYM:
							break;
						case CMT:
							break;
						case SAT:
							r.nsat = Integer.valueOf(nodeText) << 8;
							activeFileFields.nsat = 10;
							break;
						case NAME:
							break;
						case AGEOFDGPSDATA:
							r.dage = Integer.valueOf(nodeText);
							break;
						case DGPSID:
							r.dsta = Integer.valueOf(nodeText);
							break;
						case LINK:
							r.voxStr = valueNode.getAttributes().getNamedItem(
									"href").getTextContent();
							break;
						default:
							break;
						}
					} catch (final Exception e) {
						Generic.debug("Problem in trackpoint info:" + infoName,
								e);
					}
				}
			}
		}
		return r;
	}

	public static void main(final String[] args) {
		JavaLibBridge.setJavaLibImplementation(J2SEJavaTranslations
				.getInstance());
		final GPXLogConvert x = new GPXLogConvert();
		x.toGPSFile(new BT747Path("c:/BT747/20080915_2010.gpx"), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gps.log.in.GPSLogConvertInterface#getType()
	 */
	public int getType() {
		return Model.GPX_LOGTYPE;
	}

}
