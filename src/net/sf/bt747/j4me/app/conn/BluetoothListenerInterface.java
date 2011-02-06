package net.sf.bt747.j4me.app.conn;

public interface BluetoothListenerInterface {

    
    public void deviceFound(String deviceDescription, String deviceRef);
    
    public void discoveryDone();
}
