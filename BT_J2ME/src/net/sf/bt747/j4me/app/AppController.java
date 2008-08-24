package net.sf.bt747.j4me.app;

import gps.connection.GPSrxtx;

import bt747.io.File;
import bt747.model.AppSettings;
import bt747.model.Controller;
import bt747.sys.Settings;

public class AppController extends Controller {

    AppModel m;
    
    public AppController(AppModel m) {
       this.m = m;
       init();
       super.setModel(m);
       init();
    }
    
    public final AppModel getAppModel() {
        return m;
    }
    
    private void init() {
        GPSrxtx.setGpsPortInstance(new BluetoothGPS());
        initAppSettings();
        // TODO: Should load settings for Model
    }
    
    private void initAppSettings() {
            Settings.setAppSettings(new String(new byte[2048]));
    }
    
    public final void saveSettings() {
    }
}
