/**
 * 
 */
package net.sf.bt747.j2se.app.app.exiftool.test;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import net.sf.bt747.j2se.app.exiftool.ExifTool;
import net.sf.bt747.test.TestUtils;

import org.junit.Test;

/**
 * @author Mario
 * 
 */
/**
 * @author Mario
 * 
 */
public class TestExifTool {

    static {
        TestUtils.setupEnvironment();
    }

    /**
     * Test method for {@link net.sf.bt747.j2se.app.exiftool.ExifTool}.
     */
    @Test
    public void testExifTool() {
        assertTrue("ExifTool discovery", ExifTool.hasExifTool());
        String version = ExifTool.getExifToolVersion();
        String match = "^[0-9]+.[0-9]+$";
        assertTrue("Version is " + version + ". Expected " + match, Pattern
                .matches(match, version));
    }

}
