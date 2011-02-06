/**
 * Copyright 2010 - Mario De Weerd
 * 
 * Licenced under the GPL.
 */
/*
 * A nice reference URL is http://bluecove.org/bluecove/apidocs/
 */
package net.sf.bt747.j4me.app.conn;

import java.io.IOException;
import java.util.Enumeration;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import org.j4me.logging.Log;

public class BluetoothFacade implements DiscoveryListener {

    /** Listens to bluetooth messages. */
    private BluetoothListenerInterface l = null;

    private DiscoveryAgent discoveryAgent = null;
    private final int DISCOVERY_INACTIVE = 0;
    private final int DISCOVERY_INQUIRY = 1;
    private final int DISCOVERY_SERVICES = 2;
    private int discoveryState = DISCOVERY_INACTIVE;

    private final static UUID serialUUIDs[] = { //new UUID(0x0003), // RFCOMM
            new UUID(0x1101), // SERIAL PORT
    };

    private java.util.Hashtable remoteDevices = new java.util.Hashtable();

    private static BluetoothFacade instance = null;

    public static BluetoothFacade getInstance() {
        if (instance == null) {
            instance = new BluetoothFacade();
        }
        return instance;
    }

    public void cancel(BluetoothListenerInterface l) {
        cancelAll();
    }
        
    public boolean findBluetoothDevices(BluetoothListenerInterface l) {
        boolean launched = false;
        cancelAll();

        this.l = l;
        try {
            /** Get a device discovery agent. */
            discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();

            remoteDevices.clear();

            // create/get a local device and discovery agent
            final LocalDevice localDevice = LocalDevice.getLocalDevice();
            discoveryAgent = localDevice.getDiscoveryAgent();

            Log.info("Getting known devices");
            // List the known devices.
            RemoteDevice[] rds;
            rds = discoveryAgent.retrieveDevices(DiscoveryAgent.PREKNOWN);
            if (rds != null) {
                for (int i = 0; i < rds.length; i++) {
                    String mac = rds[i].getBluetoothAddress();
                    remoteDevices.put(mac, rds[i]);
                }
            } else {
                Log.info("No preknown devices");
            }
            rds = discoveryAgent.retrieveDevices(DiscoveryAgent.CACHED);
            if (rds != null) {
                for (int i = 0; i < rds.length; i++) {
                    String mac = rds[i].getBluetoothAddress();
                    remoteDevices.put(mac, rds[i]);
                }
            } else {
                Log.info("No cached devices");
            }
            Log.info("Got known devices " + remoteDevices.size());
            if (false) {
                for (Enumeration iterator = remoteDevices.keys(); iterator
                        .hasMoreElements();) {
                    String maci = (String) (iterator.nextElement());
                    Log.info("Key " + maci);
                    RemoteDevice rd = (RemoteDevice) (remoteDevices.get(maci));
                    try {
                        String mac = rd.getBluetoothAddress();
                        Log.info("Checking " + mac);
                        // findSerialServices(rd);
                        String n = rd.getFriendlyName(false);
                        Log.info("Got " + n);
                        if (n == null) {
                            n = mac;
                        }
                    } catch (Exception IOException) {

                    }
                }
            }
            Log.info("Enumeration done, launching discovery");

            /** Launch the discovery agent. */
            launched = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
            if (launched) {
                discoveryState = DISCOVERY_INQUIRY;
                Log.info("Enumeration done, discovery launched");
            } else {
                Log.info("Enumeration done, discovery not launched");
            }
        } catch (BluetoothStateException e) {
            Log.info("Problem in searching the Bluetooth devices", e);
        }
        return launched;
    }

    private void cancelAll() {
        try {
            Log.info("Entered cancelAll");
            if (discoveryAgent != null) {
                switch (discoveryState) {
                case DISCOVERY_INQUIRY:
                    discoveryAgent.cancelInquiry(this);
                    break;
                case DISCOVERY_SERVICES:
                    for (int i = 0; i < serviceSearches.size(); i++) {
                        discoveryAgent
                                .cancelServiceSearch(((Integer) serviceSearches
                                        .elementAt(i)).intValue());
                    }
                    break;
                default:
                    break;
                }
                discoveryState = DISCOVERY_INACTIVE;
            }
        } catch (Exception e) {
            Log.info("Issue while canceling discovery activity", e);
        }
    }

    private void findSerialServices(RemoteDevice device) {
        //
        // search for L2CAP services, most services based on L2CAP
        //
        // note: Rococo simulator required a new instance of Listener for
        // every search. not sure if this is also the case in real devices
        try {
            int searchID = discoveryAgent.searchServices(null, serialUUIDs,
                    device, this);
            serviceSearches.addElement(new Integer(searchID));
        } catch (Exception e) {
            Log.info("Find serial services", e);
        }
    }

    java.util.Vector serviceSearches = new java.util.Vector();

    /**
     * Bluetooth discovery listeners.
     */
    public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) {
        try {
            Log.info("Discovered " + device.getBluetoothAddress() + " "
                    + device.getFriendlyName(false));
            // device.getBluetoothAddress());
            // device.getFriendlyName(true));

            findSerialServices(device);
        } catch (Exception e) {
            Log.info("Device Discovered Error: ", e);
        }
    }

    public void inquiryCompleted(int arg0) {
        Log.info("Inquiry completed");
        if (l != null) {
            l.discoveryDone();
        }
    }

    public void serviceSearchCompleted(int transaction, int response) {
        Log.info("Service Search Completed");
    }

    public void servicesDiscovered(int searchID, ServiceRecord[] services) {
        serviceSearches.removeElement(new Integer(searchID));
        Log.info("Services Discovered");
        for (int i = 0; i < services.length; i++) {
            RemoteDevice d = services[i].getHostDevice();
            Log.info(services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,true));
            int[] attr = services[i].getAttributeIDs();
            for (int j = 0; j < attr.length; j++) {
                Log.info("Attribute :" + attr[j]);
            }
            try {
                this.l.deviceFound(d.getFriendlyName(false), d.getBluetoothAddress());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.info("servicesDiscovered",e);
            }
        }
    }
}
