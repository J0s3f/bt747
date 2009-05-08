/**
 * 
 */
package net.sf.bt747.test;

import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.model.IBlue747Model;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import bt747.j2se_view.BT747Main;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * @author Mario
 * 
 */
public class TestModelConnect {
    final static PipedInputStream appIs = new PipedInputStream();
    final static PipedInputStream modelIs = new PipedInputStream();
    static PipedOutputStream appOs;
    static PipedOutputStream modelOs;
    static GPSPort appPort;

    /**
     * Initialize the bridge to the platform. Required for BT747 that runs on
     * at least 3 different platforms.
     */
    static {
        /* Get instance of implementation */
        final JavaLibImplementation imp = new net.sf.bt747.j2se.system.J2SEJavaTranslations();
        /* Declare the implementation */
        JavaLibBridge.setJavaLibImplementation(imp);
        /* Set the serial port class instance to use (also system specific). */

        try {
            appOs = new PipedOutputStream(modelIs);
            modelOs = new PipedOutputStream(appIs);
            final MyVirtualPort modelPort = new MyVirtualPort(modelIs,
                    modelOs);
            appPort = new MyVirtualPort(appIs, appOs);
            GPSrxtx.setDefaultGpsPortInstance(appPort);
            IBlue747Model.setGpsPort(modelPort);
        } catch (Exception e) {
            System.err.println("Problem setting up ports");
            e.printStackTrace();
            // TODO: handle exception
        }
    }
    
    public static void main(final String args[]) {
        IBlue747Model.main(new String[0]);
        GPSrxtx.setDefaultGpsPortInstance(appPort);
        BT747Main.main(args);
    }

}
