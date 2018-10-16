//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     seesite@bt747.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************  
package bt747.waba_view;

import gps.connection.GPSPort;

import bt747.waba_view.ui.BT747MessageBox;

/**
 * Implements the serial port for the standard Waba SerialPort
 * 
 * @author Mario De Weerd
 */
public final class GPSWabaSocketPort extends GPSPort {
    private waba.io.Socket sp = null;
    private String MacAddr = "btspp://000B0D87E7CA"; // "00:0B:0D:87:E7:CA";
                                                        // //
                                                        // "btspp://000B0D87E7CA";

    private boolean portIsOK = false;
    
    @Override
    public GPSPort clone() {
    	return new GPSWabaSocketPort();
    }

    /**
     * Indicates if the device is connected or not.
     * 
     * @return <code>true</code> if the device is connected.
     */
    public final boolean isConnected() {
        return (sp != null && sp.isOpen());
    }

    /**
     * Close the connection.
     * 
     * 
     */
    public final void closePort() {
        if (sp != null && sp.isOpen()) {
            portIsOK = false;
            sp.close();
        }
    }

    /**
     * Open a connection
     * 
     * @return status result of the opening of the serial port.
     */
    public int openPort() {
        int result = -1;
        closePort();

        try {
            sp = new waba.io.Socket(MacAddr, 1);
            result = sp.lastError;
            new BT747MessageBox("waba.io.Socket open", "Result:" + result)
                    .popupBlockingModal();
            portIsOK = sp.isOpen();
            if (portIsOK) {
                // Read time out gives problems on windows: data is skipped!!!O
                sp.setReadTimeout(20);// small to read data in chunks and have
                // good resp.
                // sp.setReadTimeout(50);//small to read data in chunks and have
                // good resp.
                // sp.setFlowControl(true);
            } else {
                sp = null;
            }
        } catch (Exception e) {
            new BT747MessageBox("waba.io.Socket open",
                    "Unexpected exception catched").popupBlockingModal();
            // port\n");};
            sp = null;
        }
        return result;
    }

    /**
     * Set a bluetooth connection
     * 
     * 
     */
    public final void setBlueTooth() {
        spPortNbr = waba.io.SerialPort.BLUETOOTH;
    }

    /**
     * Set an USB connection
     * 
     * 
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
                debugFile.writeBytes(b, 0, b.length);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public final void write(final String s) {
        write(s.getBytes());
    }

    public final int readCheck() {
        if (sp != null) {
            return 255; // return sp.readCheck();
        } else {
            return -1;
        }
    }

    public final int readBytes(final byte[] b, final int start, final int max) {
        return sp.readBytes(b, start, max);
    }
}
