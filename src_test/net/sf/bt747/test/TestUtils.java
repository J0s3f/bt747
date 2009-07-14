/**
 * 
 */
package net.sf.bt747.test;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * @author Mario
 *
 */
public class TestUtils {

    public static final void setupEnvironment() {
        /* Get instance of implementation */
        final JavaLibImplementation imp = new net.sf.bt747.j2se.system.J2SEJavaTranslations();
        /* Declare the implementation */
        JavaLibBridge.setJavaLibImplementation(imp);
        /* Set the serial port class instance to use (also system specific). */

    }
}
