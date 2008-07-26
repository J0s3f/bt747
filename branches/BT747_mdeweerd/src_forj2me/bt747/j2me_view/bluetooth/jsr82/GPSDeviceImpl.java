// ----------------------------------------------------------------------------
// Copyright 2006-2007, Martin D. Flynn
// All rights reserved
// ----------------------------------------------------------------------------
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ----------------------------------------------------------------------------
// Description:
//  This class is the specific implementation of a GPS device which is accessible
//  via the JSR-82 Bluetooth protocol.
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//      Initial release
//  2006/03/31  Martin D. Flynn
//      Changed package
//  2006/10/27  Martin D. Flynn
//      Added 'readLine'
//  2006/11/03  Elayne Man
//      Include JavaDocs
// ----------------------------------------------------------------------------
package org.opendmtp.j2me.client.custom.gps.jsr82;

import java.io.IOException;
import java.io.InputStream;

import java.io.InterruptedIOException;

import org.opendmtp.j2me.client.gps.GPSDevice;
import org.opendmtp.j2me.client.gps.GPSException;
import org.opendmtp.j2me.util.Log;

/**
* The implementation of a GPS device.
* PalmOS error codes: http://www.palmos.com/dev/support/docs/palmos/PalmOSReference/ErrorCodes.html .
*/
public class GPSDeviceImpl
    implements GPSDevice
{

    // ----------------------------------------------------------------------------

    private BluetoothDiscovery  btooth      = null;
    private Bluetooth           gpsService  = null;
    
    /**
    * Constructor method for GPSDeviceImpl.
    * @throws GPSException For GPS exceptions.
    */
    public GPSDeviceImpl() 
        throws GPSException 
    {
        try {
            this.btooth = new BluetoothDiscovery();
        } catch (NoClassDefFoundError ncdfe) {
            Log.setMessage(0, "Not supported!\n" + ncdfe);
            throw new GPSException("Bluetooth not supported", ncdfe);
        } catch (Throwable t) {
            Log.setMessage(0, "No Bluetooth!\n" + t);
            throw new GPSException("No device found", t);
        }            
    }
    
    // ----------------------------------------------------------------------------

    /**
    * Opens a device.
    * @return true,if successful
    * @throws GPSException For GPS exceptions
    */
    public boolean openDevice() 
        throws GPSException 
    {
        this.closeDevice();
        try {
            // time consuming operation
            this.gpsService = this.btooth.getBluetoothGPSDevice();
            return true;
        } catch (Throwable t) {
            throw new GPSException("Exception", t);
        }
    }
    
    // ----------------------------------------------------------------------------

    /**
    * Determines if a connection is open.
    * @return true, if connection is open.
    */
    public boolean isOpen() 
    {
        return (this.gpsService != null);
    }
    
    // ----------------------------------------------------------------------------

    /**
    * Returns the input stream.
    * @return the input stream
    * @throws GPSException For GPS exceptions
    */
    private InputStream _getInputStream() 
        throws GPSException 
    {
        if (this.gpsService != null) {
            try {
                InputStream dis = this.gpsService.getInputStream(); 
                if (dis == null) {
                    throw new GPSException("input stream is null");
                }
                return dis;
            } catch (GPSException gpse) {
                throw gpse;
            } catch (IOException ioe) {
                throw new GPSException("IOException", ioe);
            } catch (Throwable t) {
                throw new GPSException("Exception", t);
            }
        } else {
            throw new GPSException("Service not open");
        }
    }
    
    private static final long READ_CHAR_WAIT = 300L; // at 4800bps, 300 msec == ~144 characters.

    /**
    * Reads a character.
    * @param dis The input stream
    * @param timeoutMS The timeout period
    * @return the number of characters
    * @throws GPSException For GPS exceptions
    * @throws InterruptedIOException For Interrupted IO exceptions
    * @throws IOException For IO exceptions
    * @throws InterruptedException For general interrupted exceptions
    */
    private int _readChar(InputStream dis, long timeoutMS)
        throws GPSException, InterruptedIOException, IOException, InterruptedException
    {
        // This method could use better optimization
        // Note that if the platform does not support the 'available' method, then
        // it will likely always return '0', and this available test cannot be used.
        long accumTimerMS = 0L;
        while (dis.available() <= 0) {
            // nothing to read, wait a few milliseconds
            try { Thread.sleep(READ_CHAR_WAIT); } catch (Throwable t) {}
            accumTimerMS += READ_CHAR_WAIT;
            if (accumTimerMS > timeoutMS) {
                throw new GPSException("Read timeout!!");
            }
        }
        // finally, read the character
        return dis.read();
    }

    /**
    * Reads a line.
    * @param sb The line to be read
    * @param timeoutMS the timeout amount
    * @return the length of the line read
    * @throws GPSException For GPS exceptions
    * @throws IOException For IO exceptions
    * @throws InterruptedException For general interrupted exceptions
    * @throws SecurityException For security exceptions
    */
    public int readLine(StringBuffer sb, long timeoutMS) 
        throws GPSException, InterruptedException, IOException, SecurityException
    {
        
        /* get input stream */
        InputStream dis = this._getInputStream();
        if (dis == null) {
            throw new GPSException("Bluetooth is not connected");
        }
        
        /* read loop */
        int count = 0;
        for (;;) {
            int ch = this._readChar(dis, timeoutMS); // timeout per character?
            if (ch < 0) {
                throw new GPSException("Read error (EOF?)");
            } else
            if (ch == 0) {
                // ignore
            } else
            if ((ch == '\n') || (ch == '\r')) {
                if (count > 0) {
                    // end of line
                    return count;
                } else {
                    // continue reading
                }
            } else {
                sb.append((char)ch);
                count++;
            }
        }
        
    }

    // ----------------------------------------------------------------------------

    /**
    * Closes the device.
    */
    public void closeDevice() 
    {
        if (this.gpsService != null) {
            this.gpsService.close();
            this.gpsService = null;
        }
    }
    
    // ----------------------------------------------------------------------------

}
