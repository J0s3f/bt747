/**
 * 
 */
package net.sf.bt747.j2se.app.app.exiftool.test;

import gps.log.GPSRecord;
import junit.framework.TestCase;
import net.sf.bt747.test.TestUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bt747.j2se_view.model.ImageData;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 * 
 */
public class TestImageData extends TestCase {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        TestUtils.setupEnvironment();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testImageDataRead() throws Exception {
        String imgPath = TestUtils.getTestResourcePath("IMG_0307_tagged.jpg");
        ImageData ed = new ImageData();
        ed.setFilePath(new BT747Path(imgPath));
        GPSRecord r = ed.getGpsRecord();
        GPSRecord ref = GPSRecord.getLogFormatRecord(0);
        ref.tagutc = 1235139881;
        ref.valid = 0x00000080;
        ref.latitude = 45.83736936118868;
        ref.longitude = 6.573693555725945;
        ref.rcr = 0x00000101;
        ref.height = 1080.29f;

        TestUtils.assertEquals(imgPath, ref, r);
    }
}
