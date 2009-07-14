/**
 * 
 */
package net.sf.bt747.j2se.app.app.exiftool.test;

import static org.junit.Assert.*;

import java.io.File;
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

    /**
     * List of observed path.
     * You can add your own path to the list for testing.
     */
    private String[] exifPaths = { "c:/Download/exiftool.exe" };

    /**
     * The actual path to the exiftool.
     */
    private String exifPath;
    
    static {
        TestUtils.setupEnvironment();
    }

    /**
     * Finds the best exifpath.
     */
    private void setExifPath() {
        exifPath = "exiftool"; // Default
        for (String p : exifPaths) {
            if ((new File(p)).exists()) {
                exifPath = p;
                return;
            }
        }
    }

    /**
     * Test method for {@link net.sf.bt747.j2se.app.exiftool.ExifTool}.
     */
    @Test
    public void testExifTool() {
        setExifPath();

        ExifTool.setExifToolPath(exifPath);
        assertTrue("ExifTool discovery", ExifTool.hasExifTool());
        String version = ExifTool.getExifToolVersion();
        String match = "^[0-9]+.[0-9]+$";
        assertTrue("Version is "+version+". Expected "+match, Pattern.matches(match, version));
    }

}
