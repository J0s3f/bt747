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

import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.Interface;

/**
 * @author Mario
 * 
 */
public class GPXLogConvert implements GPSLogConvertInterface {

    private String errorInfo = "";
    private GPSRecord activeFields = GPSRecord.getLogFormatRecord(0);

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getErrorInfo()
     */
    public String getErrorInfo() {
        return errorInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#parseFile(gps.log.in.GPSFileConverterInterface)
     */
    public int parseFile(final GPSFileConverterInterface gpsFile) {
        Node gpx;
        try {
            gpx = (Node) xpath.evaluate("//gpx", doc, XPathConstants.NODE);
        } catch (final Exception e) {
            Generic.debug("Did not find 'gpx' path", e);
            errorInfo = "Did not find 'gpx' path in gpx file";
            return BT747Constants.ERROR_READING_FILE;
        }
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
                                if (!passToFindFieldsActivatedInLog) {
                                    if (!r.equalsFormat(activeFields)) {
                                        activeFields = r.cloneRecord();
                                        gpsFile
                                                .writeLogFmtHeader(activeFields);
                                    }
                                    CommonIn.adjustHeight(r,
                                            factorConversionWGS84ToMSL);
                                    gpsFile.addLogRecord(r);
                                }
                                r.recCount = reccount++;
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

    private int factorConversionWGS84ToMSL = 0;

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#setConvertWGS84ToMSL(int)
     */
    public void setConvertWGS84ToMSL(final int mode) {
        factorConversionWGS84ToMSL = mode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#stopConversion()
     */
    public void stopConversion() {
        stop = true;
    }

    private volatile boolean stop;
    private java.io.File mFile = null;

    private enum infoType {
        ELE, TIME, SPEED, HDOP, VDOP, PDOP, FIX, TYPE, SYM, COURSE, CMT, SAT, NAME, DGPSID, AGEOFDGPSDATA
    };

    private Document doc;
    private XPath xpath;
    private boolean passToFindFieldsActivatedInLog;
    private final GPSRecord activeFileFields = GPSRecord
            .getLogFormatRecord(0);

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#toGPSFile(java.lang.String,
     *      gps.log.in.GPSFileConverterInterface, int)
     */
    public int toGPSFile(final String fileName,
            GPSFileConverterInterface gpsFile, final int card) {
        int error = BT747Constants.NO_ERROR;
        stop = false;
        if (File.isAvailable()) {
            mFile = new java.io.File(fileName);
            try {
                DocumentBuilder builder = DocumentBuilderFactory
                        .newInstance().newDocumentBuilder();
                doc = builder.parse(mFile);
                builder = null;
                xpath = XPathFactory.newInstance().newXPath();
            } catch (final Exception e) {
                Generic.debug("Initialising GPX reading " + fileName, e);
                errorInfo = fileName + "\n" + e.getMessage();
                error = BT747Constants.ERROR_COULD_NOT_OPEN;
                doc = null;
                xpath = null;
                return error;
            }

            passToFindFieldsActivatedInLog = gpsFile
                    .needPassToFindFieldsActivatedInLog();
            if (passToFindFieldsActivatedInLog) {
                error = parseFile(gpsFile);
                gpsFile.setActiveFileFields(activeFileFields);
            }
            passToFindFieldsActivatedInLog = false;
            do {
                error = parseFile(gpsFile);
            } while ((error == BT747Constants.NO_ERROR) && gpsFile.nextPass());
        }
        doc = null;
        xpath = null;
        gpsFile.finaliseFile();
        if (gpsFile.getFilesCreated() == 0) {
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
                    r.latitude = Double.valueOf(lat.getNodeValue());
                }
            } catch (final Exception e) {
                // TODO: handle exception
            }
            try {
                final Node lon = latlon.getNamedItem("lon");
                if (lon != null) {
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
                            if (nodeText.length() > "2008-09-15T20:25:03Z"
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
                                int utc = Interface.getDateInstance(day,
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
                                    r.rcr = 0x0300; // TODO: change in better
                                    // value
                                }
                                if (nodeText.indexOf('C', 0) != -1) {
                                    // Way Point on VGPS-900
                                    r.rcr = 0x0500; // TODO: change in better
                                    // value
                                }
                            } else {

                                if (nodeText.length() == 5) {
                                    r.rcr = Conv.hex2Int(nodeText
                                            .substring(1));
                                }
                            }

                            break;
                        case SYM:
                            break;
                        case CMT:
                            break;
                        case SAT:
                            r.nsat = Integer.valueOf(nodeText);
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
                        default:
                            break;
                        }
                    } catch (final Exception e) {
                        Generic.debug("Problem in trackpoint info:"
                                + infoName, e);
                    }
                }
            }
        }
        return r;
    }

    public static void main(final String[] args) {
        Interface.setJavaTranslationInterface(new J2SEJavaTranslations());
        final GPXLogConvert x = new GPXLogConvert();
        x.toGPSFile("c:/BT747/20080915_2010.gpx", null, 0);
    }

}
