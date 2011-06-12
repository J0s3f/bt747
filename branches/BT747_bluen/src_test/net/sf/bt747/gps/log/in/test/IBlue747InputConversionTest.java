/**
 * 
 */
package net.sf.bt747.gps.log.in.test;

import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;
import gps.log.in.BT747LogConvert;

/**
 * @author Mario
 * 
 */
public class IBlue747InputConversionTest extends TestConvertInBase {

    public static final String TEST_IBLUE747_ALLFIELDS_FILE = "logfiles/iBlue747_allfields.bin";

    public String getResourcePath(String rsc) {
        return getClass().getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    public void testiBlue747ConversionSimple() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new BT747LogConvert());
        converterSetup();
        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_IBLUE747_ALLFIELDS_FILE));

        GPSRecord[] trackPoints = result.getTrackPoints();
        GPSRecord[] wayPoints = result.getWayPoints();
        GPSRecord trkPt1;
        trkPt1 = trackPoints[1];
        // Expected values for record:
        assertEquals("Idx", 2, trkPt1.recCount);
        assertEquals("Azi len", 9, trkPt1.azi.length);
        assertEquals("Azi[2]", 318, trkPt1.azi[2]);
        assertEquals("Dage", 0, trkPt1.getDage());
        assertEquals("Dsta", 0, trkPt1.getDsta());
        assertEquals("Ele len", 9, trkPt1.ele.length);
        assertEquals("Ele[5]",49 , trkPt1.ele[5]);
        assertEquals("Geoid", false, trkPt1.hasGeoid());
        assertEquals("Hdop", 199, trkPt1.getHdop());
        assertEquals("Heading", 230.70735, trkPt1.getHeading(), .00001);
        assertEquals("Height", 142.15157, trkPt1.getHeight(), 0.00001);
        assertEquals("Logdistance", 100, trkPt1.logDistance);
        assertEquals("Logspeed", 0, trkPt1.logSpeed);
        assertEquals("LogPeriod", 10, trkPt1.logPeriod);
        assertEquals("Latitude", 48.939801305310034, trkPt1.getLatitude(),
                0.000000000000001);
        assertEquals("Longitude", 2.208577351858054, trkPt1.getLongitude(),
                0.000000000000001);
        assertEquals("Milisecond", 500, trkPt1.milisecond);
        assertEquals("Nsat", 1545, trkPt1.getNsat());
        assertEquals("PDOP", 447, trkPt1.getPdop());
        assertEquals("RCR", 1, trkPt1.rcr);
        assertEquals("recCount", 2, trkPt1.recCount);
        assertEquals("sid", 9, trkPt1.sid.length);
        assertEquals("sidinuse", 9, trkPt1.sidinuse.length);
        assertEquals("snr", 9, trkPt1.snr.length);
        assertEquals("sid[6]", 17, trkPt1.sid[6]);
        assertEquals("sidinuse[5]", true, trkPt1.sidinuse[5]);
        assertEquals("snr[8]", 21, trkPt1.snr[8]);
        assertEquals("speed", 0.20044835, trkPt1.getSpeed(), 0.00000001);
        assertEquals("tagutc", 0, trkPt1.tagutc);
        assertEquals("utc", 1217076501, trkPt1.getUtc());
        assertEquals("valid", 2, trkPt1.getValid());
        assertEquals("vdop", 401, trkPt1.getVdop());
        assertEquals("voxStr", false, trkPt1.hasVoxStr());
    }

}
