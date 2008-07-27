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
//  This class handles the discovery of Bluetooth devices and as well as searching
//  for specific Bluetooth GPS recevers.
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//      Initial release
//  2006/03/31  Martin D. Flynn
//      Changed package
//  2006/11/03  Elayne Man
//      Include JavaDocs
// ----------------------------------------------------------------------------
package org.opendmtp.j2me.client.custom.gps.jsr82;

import java.util.Vector;
import java.util.Enumeration;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.UUID;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.opendmtp.j2me.util.Log;
import org.opendmtp.j2me.util.CThread;
import org.opendmtp.j2me.util.StringTools;

/**
* Assists with Bluetooth discovery.
*/
public class BluetoothDiscovery
    implements DiscoveryListener
{

    // ------------------------------------------------------------------------

    private static final String LOG_NAME                = "BTD";

    // ------------------------------------------------------------------------
    // Bluetooth GPS receiver names (partial match OK)

    /**
    * Bluetooth GPS receiver names (partial match OK).
    */
    private static final String GPSDeviceNames[] = new String[] {
        "Pharos iGPS-BT",   // Pharos iGPS-BT (GPS-360)
        // add others as necessary
    };

    // ------------------------------------------------------------------------
    // Serial BT profile 0x1101
    // http://www.benhui.net/modules.php?name=Bluetooth&page=Connect_PC_Phone_Part_1.html
    // http://bluecove.sourceforge.net/
    // https://www.bluetooth.org/foundry/assignnumb/document/service_discovery
    // ------------------------------------------------------------------------

    private static final int SEARCH_NONE    = 0;
    private static final int SEARCH_DEVICE  = 1;
    private static final int SEARCH_SERVICE = 2;

    // ------------------------------------------------------------------------
    
    private DiscoveryAgent  agent = null;
    private int             searchMode = SEARCH_NONE;
    private Object          searchLock = new Object();
    private Vector          devices = null; // RemoteDevice
    private Vector          records = null; // ServiceRecord
    private int             searchId = -1;
    private RemoteDevice    gpsDevice = null;

    /**
    * Default constructor.
    * @throws BluetoothStateException For invalid Bluetooth States
    */
    public BluetoothDiscovery()
        throws BluetoothStateException
    {
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        Log.info(LOG_NAME, "Local name: " + localDevice.getFriendlyName());
        //Log.debug(LOG_NAME, "Addr: " + localDevice.getBluetoothAddress());
        this.agent = localDevice.getDiscoveryAgent();
    }

    // ------------------------------------------------------------------------

    /**
    * Begins the discovery of devices.
    * @return A vector of discovered devices
    */
    private Vector discoverDevices()
    {
        int accessCode = DiscoveryAgent.GIAC;
        Vector v = null;
        synchronized (this.searchLock) {
            if (this.searchMode == SEARCH_NONE) {
                this.devices = new Vector();
                try {
                    this.agent.startInquiry(accessCode, this);
                    this.searchMode = SEARCH_DEVICE;
                    while (this.searchMode == SEARCH_DEVICE) {
                        try { 
                            this.searchLock.wait(); 
                        } catch (InterruptedException ie) {
                            // is someone trying to tell us to quit?
                            if (CThread.threadShouldStop()) {
                                this.cancelSearch();
                                this.devices = null;
                                break;
                            }
                        }
                    }
                    v = this.devices; // may be null if error occured during inquiry
                } catch (Throwable t) {
                    // inquiry failed
                }
                this.devices = null;
            }
        }
        return v;
    }

    /**
    * Begins the discovery of serial server. For more, see
    * https://www.bluetooth.org/foundry/assignnumb/document/service_discovery .
    * @param rd The remote GPS device
    * @return The vector of discovered serial services
    */
    private Vector discoverSerialService(RemoteDevice rd)
    {
        Vector v = null;
        synchronized (this.searchLock) {
            if (this.searchMode == SEARCH_NONE) {
                // https://www.bluetooth.org/foundry/assignnumb/document/service_discovery
                this.records = new Vector();
                int attrSet[] = null; // new int[] { 0x0001 };
                //UUID uuidSet[] = new UUID[] { new UUID(0x0003), new UUID(0x0100), new UUID(0x1101) };
                UUID uuidSet[] = new UUID[] { new UUID(0x1101) };
                try {
                    this.searchId = this.agent.searchServices(attrSet, uuidSet, rd, this);
                    this.searchMode = SEARCH_SERVICE;
                } catch (Throwable t) {
                    // inquiry failed
                }
                if (this.searchMode == SEARCH_SERVICE) {
                    while (this.searchMode == SEARCH_SERVICE) {
                        try { 
                            this.searchLock.wait(); 
                        } catch (InterruptedException ie) {
                            // is someone trying to tell us to quit?
                            if (CThread.threadShouldStop()) {
                                this.cancelSearch();
                                this.records = null;
                                break;
                            }
                        }
                    }
                } else {
                    this.records = null;
                }
                v = this.records; // may be null if error occured during inquiry
                this.records = null;
            }
        }
        return v;
    }
    
    /**
    * Cancels the search.
    */
    private void cancelSearch() 
    {
        synchronized (this.searchLock) {
            if (this.searchMode == SEARCH_SERVICE) {
                this.agent.cancelInquiry(this);
            } else 
            if (this.searchMode == SEARCH_SERVICE) {
                if (this.searchId >= 0) {
                    this.agent.cancelServiceSearch(this.searchId);
                }
            }
        }
    }
    
    // ------------------------------------------------------------------------

    /**
    * Returns the Bluetooth GPS device.
    * @return The Bluetooth device
    */
    public Bluetooth getBluetoothGPSDevice()
    {

        /* discover devices */
        Log.info(LOG_NAME, "Finding devices ...");
        Vector dev = this.discoverDevices();
        if ((dev == null) || (dev.size() == 0)) {
            Log.error(LOG_NAME, "No devices found");
            return null;
        }
        
        /* find GPS receiver */
        RemoteDevice gpsDevice = BluetoothDiscovery.findGPSDevice(dev);
        if (gpsDevice == null) {
            Log.error(LOG_NAME, "No GPS receiver found");
            return null;
        }

        /* discover services */
        String gpsName = BluetoothDiscovery.getDeviceName(gpsDevice, false);
        ServiceRecord gpsService = null;
        Log.debug(LOG_NAME, "Scanning for services: " + gpsName);
        try {
            Vector srv = this.discoverSerialService(gpsDevice);
            if ((srv == null) || (srv.size() == 0)) {
                Log.error(LOG_NAME, "No services found: " + gpsName);
            } else {
                if (srv.size() > 1) {
                    Log.warn(LOG_NAME, "Services > 1!: " + gpsName);
                }
                gpsService = (ServiceRecord)srv.elementAt(0);
            }
        } catch (Throwable t) {
            Log.error(LOG_NAME, "Error discovering services: " + gpsName, t);
            t.printStackTrace();
        }
        
        return (gpsService != null)? new Bluetooth(gpsService) : null;

    }
    
    // ------------------------------------------------------------------------

    /**
    * Returns the device name.
    * @param rd The remote GPS device
    * @param alwaysAsk True if set to always ask.
    * @return The device name
    */
    public static String getDeviceName(RemoteDevice rd, boolean alwaysAsk)
    {
        if (rd != null) {
            try {
                return rd.getFriendlyName(alwaysAsk);
            } catch (Throwable t) {
                return "";
            }
        } else {
            return null;
        }
    }
    
    /**
    * Scans devices and returns the first GPS receiver found.
    * @param rmtDev The vector of remote devices
    * @return The first remote device found with a GPS receiver
    */
    public static RemoteDevice findGPSDevice(Vector rmtDev)
    {
        
        /* null/empty */
        if ((rmtDev == null) || (rmtDev.size() == 0)) {
            return null;
        }
        
        /* find a GPS receiver */
        for (Enumeration i = rmtDev.elements(); i.hasMoreElements();) {
            RemoteDevice rd = (RemoteDevice)i.nextElement();
            String name = BluetoothDiscovery.getDeviceName(rd, false);
            for (int dn = 0; dn < GPSDeviceNames.length; dn++) {
                if (StringTools.startsWithIgnoreCase(name,GPSDeviceNames[dn])) {
                    Log.info(LOG_NAME, "Found GPS: " + GPSDeviceNames[dn]);
                    return rd;
                }
            }
        }
        
        /* not found */
        return null;

    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // DiscoveryListener interface:

    /**
    * Determines if a device has been discovered.
    * @param btDevice the Remote device
    * @param cod the Device class of the device
    */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) 
    {
        //Log.debug(LOG_NAME, "Found device: " + btDevice);
        String name = BluetoothDiscovery.getDeviceName(btDevice, false);
        Log.debug(LOG_NAME, "Found name: " + name);
        if ((this.devices != null) && (this.devices.indexOf(btDevice) == -1)) {
            this.devices.addElement(btDevice);
        }
   }
    
    /**
    * Determines if the inquiry is completed.
    * @param discType The discovery type
    */
    public void inquiryCompleted(int discType)
    {
        //Log.debug(LOG_NAME, "Inquiry Completed: " + discType);
        synchronized (this.searchLock) {
            this.searchMode = SEARCH_NONE;
            switch (discType) {
                case INQUIRY_ERROR:
                    this.devices = null;
                    break;
                case INQUIRY_TERMINATED:
                    this.devices = null;
                    break;
                case INQUIRY_COMPLETED:
                    // devices captured in 'this.devices'
                    break;
                default:
                    this.devices = null;
                    break;
            }
            this.searchLock.notify();
        }
    }
    
    /**
    * Determines the services discovered.
    * @param transID the trans ID value
    * @param servRecord The service record
    */
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) 
    {
        //Log.debug(LOG_NAME, "Services Discovered ...");
        for (int i = 0; i < servRecord.length; i++) {
            this.records.addElement(servRecord[i]);
        }
    }
    
    /**
    * Determines if the search for services has been completed.
    * @param transID The trans ID value
    * @param respCode The respond code
    */
    public void serviceSearchCompleted(int transID, int respCode) 
    {
        //Log.debug(LOG_NAME, "Service Search Completed ...");
        synchronized (this.searchLock) {
            if (this.searchId != transID) {
                Log.error(LOG_NAME, "Bad Trans-ID: " + transID + " != " + this.searchId);
            }
            this.searchMode = SEARCH_NONE;
            this.searchId = -1;
            this.searchLock.notify();
        }
    }
    
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    
}
