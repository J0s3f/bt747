/**
 * 
 */
package net.sf.bt747.j2se.app.app.exiftool.test;

import gps.log.GPSRecord;
import net.sf.bt747.j2se.app.exiftool.ExiftoolData;
import net.sf.bt747.test.TestUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

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
        ed.setPath(imgPath);
        GPSRecord r = ed.getGpsRecord();
        GPSRecord ref = GPSRecord.getLogFormatRecord(0);
        ref.tagutc = 1235139881;
        ref.valid = 0x00000080;
        ref.latitude = 45.83736936118868;
        ref.longitude = 6.573693555725945;
        ref.rcr = 260;  // To check what the correct value is - ImageData says 257
        ref.height = 1080.29f;

        TestUtils.assertEquals(imgPath, ref, r);
    }
}
