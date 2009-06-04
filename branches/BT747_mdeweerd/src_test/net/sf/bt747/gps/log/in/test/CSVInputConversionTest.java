/**
 * 
 */
package net.sf.bt747.gps.log.in.test;

import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;
import gps.log.in.CSVLogConvert;


/**
 * @author Mario
 * 
 */
public class CSVInputConversionTest extends TestConvertInBase {
    
    public static final String TEST_HDOP_FILE = "testHDOP.csv";
    
    
    public String getResourcePath(String rsc) {
        return getClass()
        .getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }
        
    
    
    public void testCsvConversionSimple() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new CSVLogConvert());
        converterSetup();
        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_HDOP_FILE));
        
        GPSRecord[] trackPoints= result.getTrackPoints();
        GPSRecord trkPt1;
        trkPt1 = trackPoints[1];
        assertEquals( trkPt1.getLatitude(), 40.0, 0.0000001);
    }

}
