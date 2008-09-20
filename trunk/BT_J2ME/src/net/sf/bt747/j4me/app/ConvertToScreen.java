package net.sf.bt747.j4me.app;

import gps.BT747Constants;
import gps.log.out.GPSCSVFile;
import gps.log.out.GPSCompoGPSTrkFile;
import gps.log.out.GPSGPXFile;
import gps.log.out.GPSGmapsHTMLEncodedFile;
import gps.log.out.GPSKMLFile;
import gps.log.out.GPSNMEAFile;
import gps.log.out.GPSPLTFile;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.RadioButton;

import bt747.model.Model;

/**
 * The "Initializing GPS..." alert screen. This screen is used to get the
 * <code>LocationProvider</code> for the application. It first tries to get a
 * provider on the device. But if it cannot it will get a GPS provider through a
 * Bluetooth connection.
 */
public class ConvertToScreen extends Dialog {
    /**
     * The location information for this application.
     */
    private final AppController c;

    /**
     * The screen that came before this one. If the user cancels the the process
     * or if it fails it will be returned to.
     */
    private final DeviceScreen previous;

    private RadioButton rbFormats;

    /**
     * Constructs the "Initializing GPS..." alert screen.
     * 
     * @param model
     *            is the application's location data.
     * @param previous
     *            is the screen that came before this one.
     */
    public ConvertToScreen(final AppController c, final DeviceScreen previous) {
        setTitle("Convert Log");
        this.c = c;
        this.previous = previous;
        
        setUpScreen();
    }
    
    private void setUpScreen() {
        deleteAll();
        Label l;
        l = new Label("Select output format:");
        append(l);
        rbFormats = new RadioButton();
        rbFormats.append("CSV - Comma sep");
        rbFormats.append("GMAP - Google Map Html");
        rbFormats.append("GPX - ");
        rbFormats.append("KML - Google Earth");
        rbFormats.append("NMEA - NMEA strings");
        rbFormats.append("PLT - CompeGPS");
        rbFormats.append("TRK - ");
        int index;
        switch (c.getAppModel().getSelectedOutputFormat()) {
        case Model.CSV_LOGTYPE:
            index = 0;
            break;
        case Model.GMAP_LOGTYPE:
            index = 1;
            break;
        case Model.GPX_LOGTYPE:
            index = 2;
            break;
        case Model.KML_LOGTYPE:
            index = 3;
            break;
        case Model.NMEA_LOGTYPE:
            index = 4;
            break;
        case Model.PLT_LOGTYPE:
            index = 5;
            break;
        case Model.TRK_LOGTYPE:
            index = 6;
            break;
        default:
            index = 2;
        }
        rbFormats.setSelectedIndex(index);
        append(rbFormats);
        setSelected(rbFormats);
        l = new Label("'"+getRightMenuText()+"' to start conversion.");
        append(l);
        l = new Label("WARNING: Conversion is very slow.");
        append(l);
    }
    
    public void showNotify() {
        super.showNotify();
    }

    
    protected void acceptNotify() {
        ConvertToProgressScreen progressScreen;
        c.getAppModel().setSelectedOutputFormat(getSelectedLogType());
        progressScreen = new ConvertToProgressScreen(c, previous, getSelectedLogType());
        progressScreen.show();
        progressScreen = null;
    }

    protected void declineNotify() {
        previous.show();
        super.declineNotify();
    }

    private int getSelectedLogType() {
        switch (rbFormats.getSelectedIndex()) {
        case 0:
            /**
             * CSV log type (Comma Separated Values).
             */
            return (AppModel.CSV_LOGTYPE);
        case 1:
            /**
             * GMAP log type (Google Map - html output).
             */
            return (AppModel.GMAP_LOGTYPE);
        case 2:
            /**
             * GPX log type (gpx format).
             */
            return (AppModel.GPX_LOGTYPE);
        case 3:
            /**
             * KML log type ('Google Earth' format).
             */
            return (AppModel.KML_LOGTYPE);
        case 4:
            /**
             * NMEA log type (NMEA strings - text format - similar to GPS
             * output).
             */
            return (AppModel.NMEA_LOGTYPE);
        case 5:
            /**
             * Compe GPS log type (Writes PLT and WPT files).
             */
            return (AppModel.PLT_LOGTYPE);
        case 6:
            /**
             * log type (Writes TRK and WPT files).
             */
            return (AppModel.TRK_LOGTYPE);
        default:
            return (AppModel.CSV_LOGTYPE);
        }
    }
}
