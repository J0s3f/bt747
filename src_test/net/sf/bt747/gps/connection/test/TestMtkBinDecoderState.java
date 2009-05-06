/**
 * 
 */
package net.sf.bt747.gps.connection.test;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.JavaLibImplementation;

import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.connection.MtkBinDecoderState;

/**
 * @author Mario
 * 
 */
public class TestMtkBinDecoderState {

    private static myPort myP;

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
        myP = new myPort();
        GPSrxtx.setGpsPortInstance(myP);
    }
    

    private static class myPort extends GPSPort {

        /*
         * (non-Javadoc)
         * 
         * @see gps.connection.GPSPort#write(java.lang.String)
         */
        @Override
        public void write(String s) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see gps.connection.GPSPort#write(byte[])
         */
        @Override
        public void write(byte[] b) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see gps.connection.GPSPort#readCheck()
         */
        @Override
        public int readCheck() {
            return buffer.length - idx;

        }

        /*
         * (non-Javadoc)
         * 
         * @see gps.connection.GPSPort#readBytes(byte[], int, int)
         */
        @Override
        public int readBytes(byte[] b, int start, int max) {
            int count = 0;
            for (int i = start; i < max && idx < buffer.length;) {
                b[i++] = buffer[idx++];
                count++;
            }
            // TODO Auto-generated method stub
            return count;
        }

        private byte[] buffer;
        private int idx;

        private void setBuffer(byte[] b) {
            buffer = b;
            idx = 0;
        }
        
        private boolean isOpen = false;
        
        /* (non-Javadoc)
         * @see gps.connection.GPSPort#openPort()
         */
        @Override
        public int openPort() {
            isOpen = true;
            return 0;
        }
        
        /* (non-Javadoc)
         * @see gps.connection.GPSPort#closePort()
         */
        @Override
        public void closePort() {
            isOpen = false;
        }
        
        /* (non-Javadoc)
         * @see gps.connection.GPSPort#isConnected()
         */
        @Override
        public boolean isConnected() {
            return isOpen;
        }
    }

    private static final byte[] testRcv = { (byte) 0x04, (byte) 0x24,
            (byte) 0x0c, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0xfd,
            (byte) 0x00, (byte) 0x03, (byte) 0xf3, (byte) 0x0d, (byte) 0x0a };

    public static void main(String[] args) {
        GPSrxtx gpsRxTx = new GPSrxtx();
        gpsRxTx.openPort();
        myP.setBuffer(testRcv);
        MtkBinDecoderState mtkState = new MtkBinDecoderState();
        mtkState.enterState(gpsRxTx);
        System.out.println(mtkState.getResponse(gpsRxTx));
    }
}
