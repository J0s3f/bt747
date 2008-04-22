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
//  This class handles serial connections to specific Bluetooth devices.
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//      Initial release
//  2006/03/31  Martin D. Flynn
//      Changed package
//  2006/11/03  Elayne
//      Include JavaDocs
// ----------------------------------------------------------------------------
package org.opendmtp.j2me.client.custom.gps.jsr82;

import java.io.DataInputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.opendmtp.j2me.util.Log;

/**
* Represents the Bluetooth transport media.
*/
public class Bluetooth
{

    // ------------------------------------------------------------------------
    // Connector.open("comm:rfcm;baudrate=4800;bitsperchar=8;stopbits=1;parity=none")

    private static final String LOG_NAME    = "BT";

    // ------------------------------------------------------------------------
    
    private ServiceRecord       gpsService  = null;
    private RemoteDevice        gpsDevice   = null;
    private StreamConnection    connection  = null;
    private DataInputStream     inputStream = null;

    /**
    * Default constructor.
    * @param service the Service record
    */
    public Bluetooth(ServiceRecord service)
    {
        this.gpsService = service;
        this.gpsDevice  = (service != null)? service.getHostDevice() : null;
    }

    // ------------------------------------------------------------------------

    /**
    * Returns the name of the Bluetooth GPS device.
    * @return GPS device name
    */
    public String getName()
    {
        return BluetoothDiscovery.getDeviceName(this.gpsDevice, false);
    }

    // ------------------------------------------------------------------------

    /**
    * Opens a stream connection.
    * @return the opened Stream connection
    */
    public StreamConnection openStreamConnection()
    {
        if ((this.connection == null) && (this.gpsService != null)) {
            String url = this.gpsService.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            //Log.debug(LOG_NAME, "ConnectionURL: " + url);
            try {
                this.connection = (StreamConnection)Connector.open(url, Connector.READ_WRITE, true);
            } catch (Throwable t) {
                Log.error(LOG_NAME, "'Connector.open' error: " + url, t);
                return null;
            }
        }
        return this.connection;
    }
    
    /**
    * Returns the input stream.
    * @return the Data input stream
    * @throws IOException If there is an IO error
    */
    public DataInputStream getInputStream()
        throws IOException
    {
        if (this.inputStream == null) {
            StreamConnection conn = this.openStreamConnection();
            if (conn != null) {
                this.inputStream = conn.openDataInputStream();
            }
        }
        return this.inputStream;
    }
    
    /**
    * Closes the input stream.
    */
    public void close()
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
    
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    
}
