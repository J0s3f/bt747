/**
 * 
 */
package bt747.j2se_view.model.test;

import gps.log.GPSRecord;
import gps.log.out.AllWayPointStyles;
import net.sf.bt747.test.TestUtils;

import org.junit.Test;

import bt747.j2se_view.model.ImageData;
import bt747.sys.File;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 *
 */
public class TestImageDataJPG {
    static {
        TestUtils.setupEnvironment();
    }

    @Test
    public void testExifJPGRead() throws Exception {
        String imgPath = TestUtils.getTestResourcePath("IMG_0307_tagged.jpg");
        ImageData ed = new ImageData();
        ed.setFilePath(new BT747Path(imgPath));
        GPSRecord r = ed.getGpsRecord();
        GPSRecord ref = GPSRecord.getLogFormatRecord(0);
        ref.tagutc = 1235139881; // Should recover tagutc.
        ref.valid = 0x00000080;
        ref.latitude = 45.83736936118868;
        ref.longitude = 6.573693555725945;
        ref.rcr = AllWayPointStyles.GEOTAG_PICTURE_KEY; // To check what the
                                                        // correct value is -
                                                        // ImageData says 257
        ref.height = 1080.29f;

        TestUtils.assertEquals(imgPath, ref, r);
    }

    @Test
    public void testExifJPGWrite() throws Exception {
        String imgPath = TestUtils.getTestResourcePath("IMG_0307_tagged.jpg");
        String imgTestPath = imgPath + "tst.jpg";
        ImageData ed = new ImageData();
        ed.setFilePath(new BT747Path(imgPath));
        GPSRecord ref = ed.getGpsRecord();

        ref.latitude += 10;
        ref.longitude -= 10;
        ref.tagutc += 100;
        ref.rcr ^= 0x3;
        ref.height += 10;

        ed.setGpsRec(ref);

        (new File(new BT747Path(imgTestPath))).delete();
        ed.writeImage(new BT747Path(imgTestPath));

        ImageData id = new ImageData();
        id.setFilePath(new BT747Path(imgTestPath));
        GPSRecord r = id.getGpsRecord();

        ref.tagutc -= 100;
        ref.rcr = AllWayPointStyles.GEOTAG_PICTURE_KEY;
        TestUtils.assertEquals(imgPath, ref, r);

        (new File(new BT747Path(imgTestPath))).delete();
    }

}
