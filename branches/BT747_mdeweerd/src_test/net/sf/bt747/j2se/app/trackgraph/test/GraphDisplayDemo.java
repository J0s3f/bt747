/**
 * 
 */
package net.sf.bt747.j2se.app.trackgraph.test;

import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;
import gps.log.in.BT747LogConvert;



import net.sf.bt747.gps.log.in.test.TestConvertInBase;
import net.sf.bt747.j2se.app.trackgraph.GPSRecordArrayDataProvider;
import net.sf.bt747.j2se.app.trackgraph.GraphUtils;

/**
 * @author Mario
 * 
 */
public class GraphDisplayDemo extends TestConvertInBase {
    public static final String TEST_IBLUE747_ALLFIELDS_FILE = "logfiles/iBlue747_allfields.bin";

    public String getResourcePath(String rsc) {
        return TestConvertInBase.class.getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    public GPSRecordArrayDataProvider getDemoDataProvider() throws Exception {
        TracksAndWayPoints result;
        setInputConverter(new BT747LogConvert());
        converterSetup();

        /* Any settings changing from test defaults */
        result = doConvert(getResourcePath(TEST_IBLUE747_ALLFIELDS_FILE));

        GPSRecord[] trackPoints = result.getTrackPoints();
        //assertEquals(trackPoints.length, 1);
        @SuppressWarnings("unused")
		GPSRecord[] wayPoints = result.getWayPoints();
        //assertEquals(wayPoints.length, 1);

        return new GPSRecordArrayDataProvider(trackPoints);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        final GraphDisplayDemo demo = new GraphDisplayDemo();

        // create a DataProvider and a CoordinateResolver
        final GPSRecordArrayDataProvider dp = demo.getDemoDataProvider();

        GraphUtils.showGraphFrame(dp);
    }

}
