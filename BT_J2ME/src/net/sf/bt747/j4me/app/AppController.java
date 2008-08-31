package net.sf.bt747.j4me.app;

import gps.BT747Constants;
import gps.connection.GPSrxtx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;

import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import org.j4me.logging.Log;

import bt747.model.Controller;
import bt747.model.Model;
import bt747.sys.Settings;

public class AppController extends Controller {

    private AppModel m;

    public AppController(final AppModel m) {
        this.m = m;
        super.setModel(m);
        appInit();
        super.init();
        Log.info("Basedir set to:" + m.getBaseDirPath());

    }

    public final AppModel getAppModel() {
        return m;
    }

    private void appInit() {
        GPSrxtx.setGpsPortInstance(new BluetoothGPS());
        initAppSettings();
        // TODO: Should load settings for Model
    }

    private void initAppSettings() {
        Settings.setAppSettings(new String(new byte[2048]));
        RecordStore recordStore;
        try {
            recordStore = RecordStore.openRecordStore(RECORDSTORENAME, false);
            byte[] bytes = recordStore.getRecord(1);
            recordStore.closeRecordStore();
            if (bytes.length >= 2048) {
                Settings.setAppSettings(new String(bytes));
                Log.debug("Recovered settings");
            } else {
                Log.debug("Initialising settings");
                resetSettings();
                saveSettings();
            }
            // mUnitType = inputStream.readInt();
            // mBacklightSeconds = inputStream.readInt();
            // mConnectType = inputStream.readInt();
            // mMinDistance = inputStream.readInt();
            // mMinTime = inputStream.readInt();
            // mBluetoothHost = restoreNull(inputStream.readUTF());
            // mBluetoothUrl = restoreNull(inputStream.readUTF());
            // mPort = restoreNull(inputStream.readUTF());
            // mBaud = restoreNull(inputStream.readUTF());
            // mObexHost = restoreNull(inputStream.readUTF());
            // mObexUrl = restoreNull(inputStream.readUTF());
            // mEmailAddress = restoreNull(inputStream.readUTF());
            // mUserName = restoreNull(inputStream.readUTF());
            // inputStream.close();
        } catch (Exception exception) {
            // mUnitType = UNITTYPE_METRIC;
            // mBacklightSeconds = 0;
            // mConnectType = CONNECTTYPE_NONE;
            // mMinDistance = 10;
            // mMinTime = 10;
            // mBluetoothHost = null;
            // mBluetoothUrl = null;
            // mPort = null;
            // mBaud = null;
            // mObexHost = null;
            // mObexUrl = null;
            // mEmailAddress = "";
            // mUserName = "guest";
            resetSettings();
            saveSettings();
            return;
        }
        m.init();
    }
    
    private void resetSettings() {
        Enumeration roots = FileSystemRegistry.listRoots();
        String dir = "";
        while (roots.hasMoreElements()) {
            dir = "/" + (String) roots.nextElement();
        }
        if (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }
        setBaseDirPath(dir);
        Log.info("Basedir set to:" + m.getBaseDirPath());
        // Input is "/BT747/BT747_sample.bin"
        setLogFileRelPath("BT747_sample.bin");
        // Output is "/BT747/GPSDATA*"
        setOutputFileRelPath("GPSDATA");
        setDebug(true);
        setDebugConn(false);
        setLogRequestAhead(0);
        setChunkSize(0x400);
        setChunkSize(0x100); // For trial, small size for data.

        setTrkPtValid(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
        setWayPtValid(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
        // Waypoints only when button pressed.
        setWayPtRCR(BT747Constants.RCR_BUTTON_MASK);
        // Trackpoints : anything
        setTrkPtRCR(BT747Constants.RCR_TIME_MASK
                | BT747Constants.RCR_DISTANCE_MASK
                | BT747Constants.RCR_SPEED_MASK
                | BT747Constants.RCR_BUTTON_MASK);
        // To limit the output data, we only select lat,lon and height.
        setIntOpt(Model.FILEFIELDFORMAT,
                (1 << BT747Constants.FMT_LATITUDE_IDX)
                        | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                        | (1 << BT747Constants.FMT_HEIGHT_IDX));

        setIntOpt(AppModel.FILEFIELDFORMAT, -1);

        setDownloadMethod(AppModel.DOWNLOAD_FILLED);
    }

    String RECORDSTORENAME = "BT747";

    public final void saveSettings() {
        RecordStore recordStore;
        try {
            recordStore = RecordStore.openRecordStore(RECORDSTORENAME, true);
        } catch (RecordStoreException rsc) {
            return;
        }
        try {
            // outputStream.writeInt(mUnitType);
            // outputStream.writeInt(mBacklightSeconds);
            // outputStream.writeInt(mConnectType);
            // outputStream.writeInt(mMinDistance);
            // outputStream.writeInt(mMinTime);
            // outputStream.writeUTF(removeNull(mBluetoothHost));
            // outputStream.writeUTF(removeNull(mBluetoothUrl));
            // outputStream.writeUTF(removeNull(mPort));
            // outputStream.writeUTF(removeNull(mBaud));
            // outputStream.writeUTF(removeNull(mObexHost));
            // outputStream.writeUTF(removeNull(mObexUrl));
            // outputStream.writeUTF(removeNull(mEmailAddress));
            // outputStream.writeUTF(removeNull(mUserName));
            byte[] bytes = Settings.getAppSettings().getBytes();
            if (recordStore.getNumRecords() == 0)
                recordStore.addRecord(bytes, 0, bytes.length);
            else
                recordStore.setRecord(1, bytes, 0, bytes.length);
            recordStore.closeRecordStore();
        } catch (Exception exception) {
        }
    }

}
