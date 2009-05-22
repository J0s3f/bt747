/**
 * 
 */
package net.sf.bt747.j2se.app.utils.test;

import gps.convert.Conv;
import junit.framework.TestCase;
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

    @Test
    public void testWgs84Separation() throws Exception {
        Geoid.setThisGeoidIF();
        assertEquals("Some value", -12.0, Conv.wgs84Separation(50., 50.));
        assertEquals("Boundary -90,-180", -30.0, Conv.wgs84Separation(-90.,
                -180.));
        assertEquals("Boundary 90,-180", 13.0, Conv.wgs84Separation(90.,
                -180.));
        assertEquals("Boundary -90,180", -30.0, Conv.wgs84Separation(-90.,
                180.));
        assertEquals("Boundary 90,180", 13.0, Conv.wgs84Separation(90., 180.));
        assertEquals("Boundary -56,-10", 23.0, Conv.wgs84Separation(-56., -10.));
    }
}
