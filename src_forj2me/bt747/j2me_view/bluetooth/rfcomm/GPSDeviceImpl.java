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
//  via an 'rfcomm' serial connection ('rfcm' device).
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
package org.opendmtp.j2me.client.custom.gps.rfcomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.InterruptedIOException;

import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;

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
    
    private static final String LOG_NAME = "RFCM";

    // ----------------------------------------------------------------------------
    // PalmOS error codes:
    //   http://www.palmos.com/dev/support/docs/palmos/PalmOSReference/ErrorCodes.html
    // System.getProperty("microedition.commports") <-- list all comports
    // Connector.open("comm:rfcm;baudrate=4800;bitsperchar=8;stopbits=1;parity=none")
    
    private CommConnection      connection  = null;
    private DataInputStream     inputStream = null;
    
    /**
    * Default constructor.
    * @throws GPSException For GPS exceptions
    */
    public GPSDeviceImpl() 
        throws GPSException
    {
        super();
    }
    
    // ----------------------------------------------------------------------------

    /**
    * Opens a connection.
    * @return the connection opened.
    * @throws IOException IO error.
    */
    private CommConnection _openConnection() 
        throws IOException 
    {
        if (this.connection == null) {
            Log.setMessage(0, "Open GPS 'rfcm' ...");
            String url = "comm:rfcm;baudrate=4800;bitsperchar=8;stopbits=1;parity=none";
            Log.debug(LOG_NAME, "URL] " + url);
            this.connection = (CommConnection)Connector.open(url, Connector.READ_WRITE, true);
            //Log.setMessage(0, "GPS 'rfcm' is open ...");
        }
        return this.connection;
    }
    
    // ----------------------------------------------------------------------------

    /**
    * Opens a device.
    * @throws GPSException  For GPS exceptions
    * @return true, if successful
    */
    public boolean openDevice()
        throws GPSException 
    {
        try {
            this._openConnection();
            return true;
        } catch (IOException ioe) {
            throw new GPSException("IOException", ioe);
        }
    }
    
    /**
    * Determines if a connection is open.
    * @return true, if connection is open.
    */
    public boolean isOpen() 
    {
        return (this.connection != null);
    }
    
    // ----------------------------------------------------------------------------

    /**
    * Returns the input stream.
    * @return the input stream
    * @throws GPSException GPS error.
    */
    private InputStream _getInputStream()
        throws GPSException 
    {
        if (this.inputStream == null) {
            try {
                CommConnection comm = this._openConnection();
                this.inputStream = comm.openDataInputStream();
            } catch (Throwable t) {
                throw new GPSException("Exception", t);
            }
        }
        return this.inputStream;
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
        if (this.inputStream != null) {
            try { this.inputStream.close(); } catch (Throwable t) {}
            this.inputStream = null;
        }
        if (this.connection != null) {
            try { this.connection.close(); } catch (Throwable t) {}
            this.connection = null;
        }
    }
    
    // ----------------------------------------------------------------------------

}
