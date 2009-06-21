/**
 * 
 */
package net.sf.bt747.gps.connection.test;

import gps.connection.GPSrxtx;
import gps.connection.MtkBinDecoderState;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import net.sf.bt747.test.MyVirtualPort;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * @author Mario
 * 
 */
public class TestMtkBinDecoderState {
    final static PipedInputStream appIs = new PipedInputStream();
    final static PipedInputStream modelIs = new PipedInputStream();
    static PipedOutputStream appOs;
    static PipedOutputStream modelOs;

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
            TestMtkBinDecoderState.appOs = new PipedOutputStream(
                    TestMtkBinDecoderState.modelIs);
            TestMtkBinDecoderState.modelOs = new PipedOutputStream(
                    TestMtkBinDecoderState.appIs);
            // final MyVirtualPort modelPort = new MyVirtualPort(modelIs,
            // modelOs);
            final MyVirtualPort appPort = new MyVirtualPort(
                    TestMtkBinDecoderState.appIs,
                    TestMtkBinDecoderState.appOs);
            GPSrxtx.setDefaultGpsPortInstance(appPort);
            // IBlue747Model.setGpsPort(modelPort);
        } catch (final Exception e) {
            System.err.println("Problem setting up ports");
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    private static final byte[] testRcv = { (byte) 0x04, (byte) 0x24,
            (byte) 0x0c, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0xfd,
            (byte) 0x00, (byte) 0x03, (byte) 0xf3, (byte) 0x0d, (byte) 0x0a };

    public static void main(final String[] args) {
        final GPSrxtx gpsRxTx = new GPSrxtx();
        gpsRxTx.openPort();
        try {
            TestMtkBinDecoderState.modelOs
                    .write(TestMtkBinDecoderState.testRcv);
        } catch (final Exception e) {
            // TODO: handle exception
        }
        final MtkBinDecoderState mtkState = new MtkBinDecoderState();
        mtkState.enterState(gpsRxTx);
        System.out.println(mtkState.getResponse(gpsRxTx));
    }
}
