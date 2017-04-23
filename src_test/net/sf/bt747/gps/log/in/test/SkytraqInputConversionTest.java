/**
 * 
 */
package net.sf.bt747.gps.log.in.test;

import bt747.sys.interfaces.BT747Path;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;
import gps.log.in.SkytraqLogConvert;

/**
 * Testing a Skytraq log file conversion.
 * 
 * @author Mario De Weerd
 * 
 */
public class SkytraqInputConversionTest extends TestConvertInBase {

    public static final String TEST_SKYTRAQ_RAW_FILE = "logfiles/DataSkytraq.log";

    public String getResourcePath(String rsc) {
        return getClass().getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    /**
     * 
     */
    public void testSkytraqCSV() throws Exception {
        setInputConverter(new SkytraqLogConvert());
        csvConverterSetup(
        new BT747Path(getResourcePath(TEST_SKYTRAQ_RAW_FILE)+".csv"));
        lc.toGPSFile(new BT747Path(getResourcePath(TEST_SKYTRAQ_RAW_FILE)),gpsFile);
    }
    
    public void testSkytraqConversionSimple() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new SkytraqLogConvert());
        converterSetup();
        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_SKYTRAQ_RAW_FILE));

        GPSRecord[] trackPoints = result.getTrackPoints();
        @SuppressWarnings("unused")
        GPSRecord[] wayPoints = result.getWayPoints();
        GPSRecord trkPt1;
//        for(GPSRecord i: trackPoints) {
//            System.out.println(i.toString());
//        }
        trkPt1 = trackPoints[1];
        // Expected values for record:
        assertEquals("Idx", 2, trkPt1.recCount);
        assertEquals("Azi len", false, trkPt1.hasAzi());
        assertEquals("Dage", false, trkPt1.hasDage());
        assertEquals("Dsta", false, trkPt1.hasDsta());
        assertEquals("Ele len", false, trkPt1.hasEle());
        assertEquals("Geoid", false, trkPt1.hasGeoid());
        assertEquals("Hdop", false, trkPt1.hasHdop());
        assertEquals("Heading", false, trkPt1.hasHeading());
        assertEquals("Height", -1.0539155, trkPt1.getHeight(), 0.00001);
        assertEquals("Logdistance", false, trkPt1.hasLogDistance());
        assertEquals("Logspeed", false, trkPt1.hasLogSpeed());
        assertEquals("LogPeriod", false, trkPt1.hasLogPeriod());
        assertEquals("Latitude", 48.938388321113145, trkPt1.getLatitude(),
                0.000000000000001);
        assertEquals("Longitude", 2.2084993689287105, trkPt1.getLongitude(),
                0.000000000000001);
        assertEquals("Milisecond", false, trkPt1.hasMillisecond());
        assertEquals("Nsat", false, trkPt1.hasNsat());
        assertEquals("PDOP", false, trkPt1.hasPdop());
        assertEquals("RCR", true, trkPt1.hasRcr());
        assertEquals("RCR", BT747Constants.RCR_TIME_MASK, trkPt1.getRcr());
        assertEquals("recCount", 2, trkPt1.recCount);
        assertEquals("sid", false, trkPt1.hasSid());
        assertEquals("sidinuse", false, trkPt1.hasSidInUse());
        assertEquals("snr", false, trkPt1.hasSnr());
        assertEquals("speed", 0.0, trkPt1.getSpeed(), 0.00000001);
        assertEquals("tagutc", 0, trkPt1.tagutc);
        assertEquals("utc", 1263668369-16, trkPt1.getUtc());
        assertEquals("valid", false, trkPt1.hasValid());
        assertEquals("vdop", false, trkPt1.hasVdop());
        assertEquals("voxStr", false, trkPt1.hasVoxStr());
    }

}
