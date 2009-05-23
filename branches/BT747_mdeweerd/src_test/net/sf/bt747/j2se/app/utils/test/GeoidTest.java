/**
 * 
 */
package net.sf.bt747.j2se.app.utils.test;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import gps.convert.Conv;
import gps.convert.GeoidIF;
import junit.framework.TestCase;
import net.sf.bt747.j2se.app.filefilters.JpgFileFilter;
import net.sf.bt747.j2se.app.utils.Geoid;

import org.junit.Test;

/**
 * @author Mario
 * 
 */
@SuppressWarnings("all")
public class GeoidTest extends TestCase {

    public GeoidTest(String s) {
        super(s);
    }

    /**
     * Checks the Wgs84Seperation function.
     * 
     * @throws Exception
     */
    @Test
    public void testWgs84Separation() throws Exception {
        GeoidIF gIF = Geoid.getInstance();
        assertEquals("Some value", -12.0, gIF.wgs84Separation(50., 50.));
        assertEquals("Boundary -90,-180", -30.0, gIF.wgs84Separation(-90.,
                -180.));
        assertEquals("Boundary 90,-180", 13.0, gIF
                .wgs84Separation(90., -180.));
        assertEquals("Boundary -90,180", -30.0, gIF.wgs84Separation(-90.,
                180.));
        assertEquals("Boundary 90,180", 13.0, gIF.wgs84Separation(90., 180.));
        assertEquals("Boundary -56,-10", 23.0, gIF
                .wgs84Separation(-56., -10.));
    }

    /**
     * Creates an image giving an idea of the differences between different
     * models.
     * 
     * @throws Exception
     */
    public void createDifferenceImage() throws Exception {
        GeoidIF gIF = Geoid.getInstance();
        GeoidIF gOrgIF = gps.convert.Geoid.getInstance();
        BufferedImage im = new BufferedImage(360, 181,
                BufferedImage.TYPE_INT_RGB);
        for (int lat = -90; lat <= 90; lat++) {
            for (int lon = -180; lon < 180; lon++) {
                float diff = (float) (gIF.wgs84Separation(lat, lon) - gOrgIF
                        .wgs84Separation(lat, lon));
                if (diff > 2) {
                    int a = 2;
                }

                im.setRGB(lon + 180, 90 - lat, getGradientColor(diff)
                        .getRGB());
            }
        }
        String type = "png";
        File f = new File("c:/bt747/geoidDiff." + type);
        ImageIO.write(im, type, f);
    }

    private final static int[] limits = { 0, 3, 7, 12, 18, 25 };
    private final static int[] hLimits = { 239, 201, 160, 82, 33, 0 };
    private final static float[] scale = {
            (hLimits[0] - hLimits[1]) / (limits[0] - limits[1]),
            (hLimits[1] - hLimits[2]) / (limits[1] - limits[2]),
            (hLimits[2] - hLimits[3]) / (limits[2] - limits[3]),
            (hLimits[3] - hLimits[4]) / (limits[3] - limits[4]),
            (hLimits[4] - hLimits[5]) / (limits[4] - limits[5]), };

    /**
     * Determines a color for the difference. This may be be derived in the
     * future to for coloured tracks.
     * 
     * @param value
     * @return
     */
    private Color getGradientColor(final float value) {
        float H;
        int idx;

        if (value < limits[0]) {
            idx = -1;
        } else if (value <= limits[1]) {
            idx = 0;
        } else if (value <= limits[2]) {
            idx = 1;
        } else if (value <= limits[3]) {
            idx = 2;
        } else if (value <= limits[4]) {
            idx = 3;
        } else if (value <= limits[5]) {
            idx = 4;
        } else {
            idx = 5;
        }
        if (idx < 0) {
            H = hLimits[0];
        } else if (idx > 4) {
            H = hLimits[5];
        } else {
            H = (value - limits[idx]) * scale[idx] + hLimits[idx];
        }
        H /= 360;

        return Color.getHSBColor(H, 1.f, 1.f); // String.format("%06x",Color.getHSBColor(H,
        // 1.f, 1.f).getRGB())

    }

}
