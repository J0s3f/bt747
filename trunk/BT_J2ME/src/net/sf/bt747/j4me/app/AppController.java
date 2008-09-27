package net.sf.bt747.j4me.app;

import gps.BT747Constants;
import gps.connection.GPSrxtx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import net.sf.bt747.j4me.app.screens.FileManager;

import org.j4me.logging.Log;

import bt747.model.Controller;
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
            if (bytes.length >= 2048) {
                Settings.setAppSettings(new String(bytes));
                m.init();
                Log.debug("Recovered settings");
            } else {
                //Log.debug("Initialising settings");
                m.init();
                resetSettings();
                saveSettings();
            }
            bytes = recordStore.getRecord(2);
            //Log.debug("Size:"+bytes.length);
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(
                    bytes));
            try {
                String BtHost = restoreNull(is.readUTF());
                String BtURL = restoreNull(is.readUTF());
                m.setBluetoothGPS(BtHost, BtURL);
                Log.debug("Recovered BT URL " + BtHost + " " + BtURL);

                m.setSelectedOutputFormat(is.readInt());
            } catch (Exception e) {
            }
            recordStore.closeRecordStore();
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
        } catch (RecordStoreException exception) {
            m.setBluetoothGPS(null, null);
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
            m.init();
            resetSettings();
            saveSettings();
            return;
        }
    }

    private void resetSettings() {
        try {
            FileManager fm = new FileManager();
            Enumeration roots = fm.listRoots();
            String dir = "";
            while (roots.hasMoreElements()) {
                dir = "/" + (String) roots.nextElement();
            }
            if (dir.endsWith("/")) {
                dir = dir.substring(0, dir.length() - 1);
            }
            fm.close();
            //Log.info("Setting basedir set to:" + dir);
            setBaseDirPath(dir);
        } catch (Exception e) {
            Log.debug("Problem finding root", e);
            // TODO: handle exception
        }
        setChunkSize(500);
        setLogRequestAhead(4); // For trial, small size for data.
        Log.info("Reset basedir set to:" + m.getBaseDirPath());
        // Input is "/BT747/BT747_sample.bin"
        setLogFileRelPath("BT747_sample.bin");
        // Output is "/BT747/GPSDATA*"
        setOutputFileRelPath("GPSDATA");
        setDebug(true);
        setDebugConn(false);

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
//        setIntOpt(Model.FILEFIELDFORMAT, (1 << BT747Constants.FMT_LATITUDE_IDX)
//                | (1 << BT747Constants.FMT_LONGITUDE_IDX)
//                | (1 << BT747Constants.FMT_HEIGHT_IDX));
    }

    String RECORDSTORENAME = "BT747";

    public final void saveSettings() {
        RecordStore recordStore;
        Log.debug("Store settings");
        m.getIntOpt(AppModel.FILEFIELDFORMAT);
        try {
            byte[] bytes;
            recordStore = RecordStore.openRecordStore(RECORDSTORENAME, true);

            // Save original BT747 settings
            bytes = Settings.getAppSettings().getBytes();
            if (recordStore.getNumRecords() == 0)
                recordStore.addRecord(bytes, 0, bytes.length);
            else
                recordStore.setRecord(1, bytes, 0, bytes.length);

            // Save application settings
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(bos);
            try {
                os.writeUTF(removeNull(m.getBluetoothGPSName()));
                os.writeUTF(removeNull(m.getBluetoothGPSURL()));
                os.writeInt(m.getSelectedOutputFormat());
                os.flush();
                bytes = bos.toByteArray();
                if (recordStore.getNumRecords() == 1) {
                    recordStore.addRecord(bytes, 0, bytes.length);
                    //Log.debug("1 Size:"+bytes.length);
                }
                else {
                    //Log.debug("2 Size:"+bytes.length);
                    recordStore.setRecord(2, bytes, 0, bytes.length);
                }
                os.close();
                os = null;
                bos = null;
                Log.debug("Stored BT URL " + m.getBluetoothGPSName() + " "
                        + m.getBluetoothGPSURL());
            } catch (Exception exception) {
                Log.error("While BT save", exception);
            }
            recordStore.closeRecordStore();
        } catch (Throwable exception) {
            Log.error("Problem saving settings", exception);
        }
        m.getIntOpt(AppModel.FILEFIELDFORMAT);
    }

    private String removeNull(String text) {
        return text != null ? text : "";
    }

    private String restoreNull(String text) {
        return text.length() > 0 ? text : null;
    }
}
