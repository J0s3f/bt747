// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
// *** The application was written using the SuperWaba toolset. ***
// *** This is a proprietary development environment based in ***
// *** part on the Waba development environment developed by ***
// *** WabaSoft, Inc. ***
// ********************************************************************
package bt747.waba_view;

import waba.ui.MessageBox;

import gps.connection.GPSPort;

import bt747.sys.Generic;

/**
 * Implements the serial port for the standard Waba SerialPort
 * 
 * @author Mario De Weerd
 */
public final class GPSWabaPort extends GPSPort {
    private waba.io.SerialPort sp = null;

    private boolean portIsOK = false;

    
    @Override
    public GPSPort clone() {
    	return new GPSWabaPort();
    }
    /**
     * Indicates if the device is connected or not.
     * 
     * @return <code>true</code> if the device is connected.
     */
    public final boolean isConnected() {
        return ((sp != null) && sp.isOpen());
    }

    /**
     * Close the connection.
     * 
     * 
     */
    public final void closePort() {
        if ((sp != null) && sp.isOpen()) {
            portIsOK = false;
            sp.close();
        }
    }

    /**
     * Open a connection
     * 
     * @return status result of the opening of the serial port.
     */
    public final int openPort() {
        int result = -1;
        closePort();
        try {
            Generic.debug("opening " + spPortNbr + "@" + spSpeed, null);
            sp = new waba.io.SerialPort(spPortNbr, spSpeed);
            result = sp.lastError;
            Generic.debug(sp.getStatus(), null);
            portIsOK = sp.isOpen();
            if (portIsOK) {
                // Read time out gives problems on windows: data is
                // skipped!!!O
                sp.setReadTimeout(0);// small to read data in chunks and
                                        // have
                // good resp.
                // sp.setReadTimeout(50);//small to read data in chunks and
                // have
                // good resp.
                // sp.setFlowControl(true);
            } else {
                sp = null;
            }
        } catch (final Exception e) {
            new MessageBox("waba.io.SerialPort open",
                    "Unexpected exception catched").popupBlockingModal();
            // port\n");};
            sp = null;
        }
        return result;
    }

    /**
     * Set a bluetooth connection.
     */
    public final void setBlueTooth() {
        spPortNbr = waba.io.SerialPort.BLUETOOTH;
    }

    /**
     * Set an USB connection.
     */
    public final void setUSB() {
        spPortNbr = waba.io.SerialPort.USB;
    }

    /**
     * getter to retrieve the last error report by the serial port driver.
     * 
     * @return last error from the waba.io.SerialPort driver
     */
    public final int error() {
        return sp.lastError;
    }

    public final void write(final byte[] b) {
        sp.writeBytes(b, 0, b.length);
        if (GPS_FILE_LOG && (debugFile != null)) {
            try {
                debugFile.writeBytes("W:".getBytes(), 0, 2);
                debugFile.writeBytes(b, 0, b.length);
            } catch (final Exception e) {
                // TODO: handle exception
            }
        }
    }

    public final void write(final String s) {
        write(s.getBytes());
    }

    public final int readCheck() {
        if (sp != null) {
            // return 0x800;
            return sp.readCheck();
        } else {
            return -1;
        }
    }

    public final int readBytes(final byte[] b, final int start, final int max) {
        return sp.readBytes(b, start, max);
    }
}
