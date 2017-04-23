/**
 * 
 */
package net.sf.bt747.gps.log.in.test;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;
import gps.log.in.BT747LogConvert;

/**
 * @author Mario
 * 
 */
public class HoluxGPSportInputConversionTest extends TestConvertInBase {

    public static final String TEST_HoluxGPSport_FILE = "logfiles/HoluxGPSport.bin";

    public String getResourcePath(String rsc) {
        return getClass().getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    public void testiBlue747ConversionSimple() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new BT747LogConvert());
        converterSetup();
        lc.setLoggerType(BT747Constants.GPS_TYPE_HOLUX_GR245);
        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_HoluxGPSport_FILE));

        GPSRecord[] trackPoints = result.getTrackPoints();
        @SuppressWarnings("unused")
        GPSRecord[] wayPoints = result.getWayPoints();
        GPSRecord trkPt1;
        trkPt1 = trackPoints[1];
        // Expected values for record:
        assertEquals("Idx", 2, trkPt1.recCount);
        assertEquals("Azi len", false, trkPt1.hasAzi());
        //assertEquals("Azi[2]", 318, trkPt1.azi[2]);
        //assertEquals("Dage", 0, trkPt1.dage);
        assertEquals("Dage", false, trkPt1.hasDage());
        //assertEquals("Dsta", 0, trkPt1.dsta);
        assertEquals("Dsta", false, trkPt1.hasDsta());
        //assertEquals("Ele len", 9, trkPt1.ele.length);
        //assertEquals("Ele[5]",49 , trkPt1.ele[5]);
//        assertEquals("Geoid", false, trkPt1.hasGeoid());
//        assertEquals("Hdop", 199, trkPt1.hdop);
//        assertEquals("Heading", 230.70735, trkPt1.heading, .00001);
        assertEquals("Height", 23.029865, trkPt1.getHeight(), 0.00001);
        assertEquals("Logdistance", 0, trkPt1.logDistance);
        assertEquals("Logspeed", 0, trkPt1.logSpeed);
        assertEquals("LogPeriod", 50, trkPt1.logPeriod);
        assertEquals("Latitude", 33.87514877319336, trkPt1.getLatitude(),
                0.000000000000001);
        assertEquals("Longitude", -117.91815948486328, trkPt1.getLongitude(),
                0.000000000000001);
        assertEquals("Milisecond", 0, trkPt1.milisecond);
        //assertEquals("Nsat", 1545, trkPt1.getNsat());
        //assertEquals("PDOP", 447, trkPt1.pdop);
        assertEquals("RCR", 1, trkPt1.rcr);
        assertEquals("recCount", 2, trkPt1.recCount);
        //assertEquals("sid", 9, trkPt1.sid.length);
        //assertEquals("sidinuse", 9, trkPt1.sidinuse.length);
        //assertEquals("snr", 9, trkPt1.snr.length);
        //assertEquals("sid[6]", 17, trkPt1.sid[6]);
        //assertEquals("sidinuse[5]", true, trkPt1.sidinuse[5]);
        //assertEquals("snr[8]", 21, trkPt1.snr[8]);
        assertEquals("speed", 1.6559999, trkPt1.getSpeed(), 0.00000001);
        assertEquals("tagutc", 0, trkPt1.tagutc);
        assertEquals("utc", 1235679127, trkPt1.getUtc());
        assertEquals("valid", false, trkPt1.hasValid());
        //assertEquals("vdop", 401, trkPt1.vdop);
        assertEquals("voxStr", false, trkPt1.hasVoxStr());
    }

}
