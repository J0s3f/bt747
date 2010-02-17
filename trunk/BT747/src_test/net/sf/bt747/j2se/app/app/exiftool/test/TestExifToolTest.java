/**
 * 
 */
package net.sf.bt747.j2se.app.app.exiftool.test;

import gps.log.GPSRecord;
import gps.log.out.AllWayPointStyles;
import net.sf.bt747.j2se.app.exiftool.ExifTool;
import net.sf.bt747.j2se.app.exiftool.ExiftoolData;
import net.sf.bt747.test.TestUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

import bt747.j2se_view.model.ImageData;
import bt747.sys.File;
import bt747.sys.interfaces.BT747Path;

import junit.framework.TestCase;
import junit.framework.Assert.*;

/**
 * @author Mario
 * 
 */
public class TestExifToolTest extends TestCase {

    static {
        TestUtils.setupEnvironment();
    }

    @Test
    public void testExifToolRead() throws Exception {
        String imgPath = TestUtils.getTestResourcePath("IMG_0307_tagged.jpg");
        ExiftoolData ed = new ExiftoolData();
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
    public void testExifToolWrite() throws Exception {
        String imgPath = TestUtils.getTestResourcePath("IMG_0307_tagged.jpg");
        String imgTestPath = imgPath + "tst.jpg";
        ExiftoolData ed = new ExiftoolData();
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

    
    @Test
    public void testJpexExifWriteExifToolRead() throws Exception {
        String imgPath = TestUtils.getTestResourcePath("IMG_0307_Tagged.jpg");
        String imgTestPath = imgPath + "javatst.jpg";
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

        
        (new File(new BT747Path(imgPath+".html"))).delete();
        (new File(new BT747Path(imgTestPath+".html"))).delete();
        String[] p = {"-htmldump","-w","%d%f.%e.html",imgPath,imgTestPath};
        byte[] result;
        result = ExifTool.execExifTool(p);
        System.out.write(result);
        
        ExiftoolData id = new ExiftoolData();
        id.setFilePath(new BT747Path(imgTestPath));
        GPSRecord r = id.getGpsRecord();

        ref.tagutc -= 100;
        ref.rcr = AllWayPointStyles.GEOTAG_PICTURE_KEY;
        TestUtils.assertEquals(imgPath, ref, r);

        (new File(new BT747Path(imgTestPath))).delete();
    }

}
