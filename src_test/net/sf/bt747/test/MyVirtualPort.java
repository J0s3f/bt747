/**
 * 
 */
package net.sf.bt747.test;

import gps.connection.GPSPort;

import java.io.IOException;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public class MyVirtualPort extends GPSPort {

    private final java.io.InputStream is;
    private final java.io.OutputStream os;

    public MyVirtualPort(final java.io.InputStream is,
            final java.io.OutputStream os) {
        this.is = is;
        this.os = os;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#write(java.lang.String)
     */
    @Override
    public void write(final String s) {
        write(s.getBytes());
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#write(byte[])
     */
    @Override
    public void write(final byte[] b) {
        try {
            os.write(b);
        } catch (final IOException e) {
            Generic.debug("MyPort write", e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#readCheck()
     */
    @Override
    public int readCheck() {
        try {
            return is.available();
        } catch (final IOException e) {
            Generic.debug("MyPort available", e);
        }
        return 0;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#readBytes(byte[], int, int)
     */
    @Override
    public int readBytes(final byte[] b, final int start, final int max) {
        try {
            return is.read(b, start, max);
        } catch (final IOException e) {
            Generic.debug("MyPort read", e);
        }
        return 0;
    }

    private boolean isOpen = false;

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#openPort()
     */
    @Override
    public int openPort() {
        isOpen = true;
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#closePort()
     */
    @Override
    public void closePort() {
        isOpen = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.GPSPort#isConnected()
     */
    @Override
    public boolean isConnected() {
        return isOpen;
    }
}
