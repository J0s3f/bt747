/**
 * 
 */
package net.sf.bt747.test;

import java.io.File;

import net.sf.bt747.j2se.app.exiftool.ExifTool;

import gps.log.GPSRecord;
import junit.framework.Assert;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * @author Mario
 * 
 */
public class TestUtils {

    public static final void setupEnvironment() {
        /* Get instance of implementation */
        final JavaLibImplementation imp = net.sf.bt747.j2se.system.J2SEJavaTranslations.getInstance();
        /* Declare the implementation */
        JavaLibBridge.setJavaLibImplementation(imp);
        /* Set the serial port class instance to use (also system specific). */
        setExifPath();
    }

    public static final String getTestResourcePath(final String rsc) {
        String path;
        path = TestUtils.class.getResource("files/" + rsc).getPath();
        if(path.charAt(0)=='/' && path.charAt(2)==':') {
            path = path.substring(1);
        }
        return path; // getClass().getResource("test1.csv")
    }

    public static final void assertEquals(final String msg,
            final GPSRecord expected, final GPSRecord actual)
            throws Exception {
        Assert.assertEquals(msg + " hasLatitude", expected.hasLatitude(),
                actual.hasLatitude());
        Assert.assertEquals(msg + " lat", expected.latitude, actual.latitude,
                0.0000001);
        Assert.assertEquals(msg + " hasLongitude", expected.hasLongitude(),
                actual.hasLongitude());
        Assert.assertEquals(msg + " lon", expected.longitude,
                actual.longitude, 0.0000001);
        Assert.assertEquals(msg + " hasValid", expected.hasValid(), actual
                .hasValid());
        Assert.assertEquals(msg + " valid", expected.valid, actual.valid);
        Assert.assertEquals(msg + " hasUtc", expected.hasUtc(), actual
                .hasUtc());
        Assert.assertEquals(msg + " utc", expected.utc, actual.utc);
        Assert.assertEquals(msg + " hasRcr", expected.hasRcr(), actual
                .hasRcr());
        Assert.assertEquals(msg + " rcr", expected.rcr, actual.rcr);
        Assert.assertEquals(msg + "hasHeight", expected.hasHeight(), actual
                .hasHeight());
        Assert.assertEquals(msg + " height", expected.height, actual.height,
                0.01);
        Assert.assertEquals(msg + " hasTagUtc", expected.hasTagUtc(), actual
                .hasTagUtc());
        Assert.assertEquals(msg + " tagutc", expected.tagutc, actual.tagutc);
    }

    
    /**
     * List of observed path.
     * You can add your own path to the list for testing.
     */
    private static String[] exifPaths = { "c:/Download/exiftool.exe",
        "c:/Program Files/hugin/bin/exiftool.exe"};

    /**
     * Finds the best exifpath.
     */
    private static void setExifPath() {
        /**
         * The actual path to the exiftool.
         */
        String exifPath;
        exifPath = "exiftool"; // Default
        for (String p : exifPaths) {
            if ((new File(p)).exists()) {
                exifPath = p;
                break;
            }
        }
        ExifTool.setExifToolPath(exifPath);
    }
}
