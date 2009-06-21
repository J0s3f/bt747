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
public class HoluxM1000CInputConversionTest extends TestConvertInBase {

    public static final String TEST_M1000_ALT_FILE = "logfiles/HoluxM1000C-alt.bin";
    public static final String TEST_M1000_SPDALT_FILE = "logfiles/HoluxM1000C-alt_spd.bin";
    public static final String TEST_M1000_SPD_FILE = "logfiles/HoluxM1000C-spd.bin";

    public String getResourcePath(String rsc) {
        return getClass().getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    public void testHoluxAltitudeOnly() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new BT747LogConvert());
        converterSetup();
        lc.setLoggerType(BT747Constants.GPS_TYPE_HOLUX_GR245);
        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_M1000_ALT_FILE));

        GPSRecord[] trackPoints = result.getTrackPoints();
        GPSRecord[] wayPoints = result.getWayPoints();
        GPSRecord trkPt1;
        trkPt1 = trackPoints[5];
        // Expected values for record:
        assertEquals("Height", 299.82812, trkPt1.height, 0.00001);
        //assertEquals("speed", 0.20044835, trkPt1.speed, 0.00000001);
        assertEquals("speed", false, trkPt1.hasSpeed());
    }

    public void testHoluxSpeedOnly() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new BT747LogConvert());
        converterSetup();
        lc.setLoggerType(BT747Constants.GPS_TYPE_HOLUX_GR245);
        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_M1000_SPD_FILE));

        GPSRecord[] trackPoints = result.getTrackPoints();
        GPSRecord[] wayPoints = result.getWayPoints();
        GPSRecord trkPt1;
        trkPt1 = trackPoints[8];
        // Expected values for record:
        //assertEquals("Height", 299.82812, trkPt1.height, 0.00001);
        assertEquals("false", false, trkPt1.hasHeight());
        assertEquals("speed", 0.036, trkPt1.speed, 0.0001);
        //assertEquals("speed", false, trkPt1.hasSpeed());
    }

    public void testHoluxSpeedAndAltitudeOnly() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new BT747LogConvert());
        converterSetup();
        lc.setLoggerType(BT747Constants.GPS_TYPE_HOLUX_GR245);
        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_M1000_SPDALT_FILE));

        GPSRecord[] trackPoints = result.getTrackPoints();
        GPSRecord[] wayPoints = result.getWayPoints();
        GPSRecord trkPt1;
        trkPt1 = trackPoints[7];
        // Expected values for record:
        assertEquals("Height", 300.1875, trkPt1.height, 0.00001);
        assertEquals("speed", 0.82799995, trkPt1.speed, 0.00000001);
        //assertEquals("speed", false, trkPt1.hasSpeed());
    }

}
