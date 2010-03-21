package net.sf.bt747.j4me.app;

import gps.BT747Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import net.sf.bt747.j4me.app.conn.BluetoothPort;
import net.sf.bt747.j4me.app.screens.FileManager;

import org.j4me.logging.Log;

import bt747.model.AppSettings;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.sys.File;
import bt747.sys.Settings;

public class AppController extends Controller {

    private AppModel m;
    
    private static boolean staticNeedsResetSettings = false;

    public AppController(final AppModel m) {
        this.m = m;
        super.setModel(m);

        BluetoothPort.setController(this);

        appInit();
        super.init();
        Log.info("Basedir set to:"
                + m.getStringOpt(AppSettings.OUTPUTDIRPATH));

    }

    public final AppModel getAppModel() {
        return m;
    }

    private void appInit() {
        m.init();
        if(staticNeedsResetSettings) {
            staticNeedsResetSettings = false;
            resetSettings();
            saveSettings();
        }
        initAppSettingsPart2();
        setBooleanOpt(AppSettings.DISABLELOGDURINGDOWNLOAD, true);
        // TODO: Should load settings for Model^
    }

    public static void initAppSettings() {
        Settings.setAppSettings(new String(new byte[AppSettings.SIZE]));
        RecordStore recordStore;
        try {
            recordStore = RecordStore.openRecordStore(RECORDSTORENAME, false);
            byte[] bytes = recordStore.getRecord(1);
            if (bytes.length >= 2048) {
                Settings.setAppSettings(new String(bytes));
                Log.debug("Recovered settings");
            } else {
                // Log.debug("Initialising settings");
                staticNeedsResetSettings = true;
            }
            recordStore.closeRecordStore();
        } catch (final RecordStoreException exception) {
            return;
        }
    }

    private void initAppSettingsPart2() {
        RecordStore recordStore;
        try {
            recordStore = RecordStore.openRecordStore(RECORDSTORENAME, false);
            byte[] bytes;
            bytes = recordStore.getRecord(2);
            // Log.debug("Size:"+bytes.length);
            final DataInputStream is = new DataInputStream(
                    new ByteArrayInputStream(bytes));
            int settingsVersion = -1;
            try {
                final String BtHost = restoreNull(is.readUTF());
                final String BtURL = restoreNull(is.readUTF());
                m.setBluetoothGPS(BtHost, BtURL);
                Log.debug("Recovered BT URL " + BtHost + " " + BtURL);

                m.setSelectedOutputFormat(is.readInt());
                settingsVersion = is.readInt();
                switch (settingsVersion) {
                case 2:
                    setPersistentDebug(is.readBoolean());
                    if (isPersistentDebug()) {
                        setDebug(is.readBoolean());
                        setDebugConn(is.readBoolean());
                        setUseConsoleFile(is.readBoolean());
                    } else {
                        is.readBoolean();
                        is.readBoolean();
                        is.readBoolean();
                    }
                    setPersistentDebug(true);
                    setUseConsoleFile(true);
                    setDebug(true);
                    /** fall through */
                case -1:
                    // No other parameters
                    break;
                default:
                    break;
                }
            } catch (final Exception e) {
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
        } catch (final RecordStoreException exception) {
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

    public final void setPaths() {
        setStringOpt(AppSettings.LOGFILEPATH, m
                .getStringOpt(AppSettings.OUTPUTDIRPATH)
                + File.separatorStr
                + m.getStringOpt(AppSettings.LOGFILERELPATH));
    }

    private void resetSettings() {
        try {
            final FileManager fm = new FileManager();
            final Enumeration roots = fm.listRoots();
            String dir = "";
            while (roots.hasMoreElements()) {
                dir = "/" + (String) roots.nextElement();
            }
            if (dir.endsWith("/")) {
                dir = dir.substring(0, dir.length() - 1);
            }
            fm.close();
            // Log.info("Setting basedir set to:" + dir);
            setStringOpt(AppSettings.OUTPUTDIRPATH, dir);
        } catch (final Exception e) {
            Log.debug("Problem finding root", e);
            // TODO: handle exception
        }
        setChunkSize(500);
        setLogRequestAhead(4); // For trial, small size for data.
        Log.info("Reset basedir set to:"
                + m.getStringOpt(AppSettings.OUTPUTDIRPATH));
        // Input is "/BT747/BT747_sample.bin"
        setStringOpt(AppSettings.LOGFILERELPATH, "BT747_sample.bin");
        setPaths();

        // Output is "/BT747/GPSDATA*"
        setOutputFileRelPath("GPSDATA");
        setDebug(true);
        setDebugConn(false);

        setIntOpt(AppSettings.TRKPT_VALID, (0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
        setIntOpt(AppSettings.WAYPT_VALID,(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
        // Waypoints only when button pressed.
        setIntOpt(AppSettings.WAYPT_RCR,BT747Constants.RCR_BUTTON_MASK);
        // Trackpoints : anything
        setIntOpt(AppSettings.TRKPT_RCR, (BT747Constants.RCR_TIME_MASK
        | BT747Constants.RCR_DISTANCE_MASK
        | BT747Constants.RCR_SPEED_MASK
        | BT747Constants.RCR_BUTTON_MASK));
        // To limit the output data, we only select lat,lon and height.
        // setIntOpt(Model.FILEFIELDFORMAT, (1 <<
        // BT747Constants.FMT_LATITUDE_IDX)
        // | (1 << BT747Constants.FMT_LONGITUDE_IDX)
        // | (1 << BT747Constants.FMT_HEIGHT_IDX));
    }

    private final static String RECORDSTORENAME = "BT747";
    private final int appSettingsVersion = 2;

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.Controller#saveSettings()
     */
    public final void saveSettings() {
        RecordStore recordStore;
        Log.debug("Store settings");
        m.getIntOpt(AppSettings.FILEFIELDFORMAT);
        try {
            byte[] bytes;
            recordStore = RecordStore.openRecordStore(RECORDSTORENAME, true);

            // Save original BT747 settings
            bytes = Settings.getAppSettings().getBytes();
            if (recordStore.getNumRecords() == 0) {
                recordStore.addRecord(bytes, 0, bytes.length);
            } else {
                recordStore.setRecord(1, bytes, 0, bytes.length);
            }

            // Save application settings
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(bos);
            try {
                os.writeUTF(removeNull(m.getBluetoothGPSName()));
                os.writeUTF(removeNull(m.getBluetoothGPSURL()));
                os.writeInt(m.getSelectedOutputFormat());
                os.writeInt(appSettingsVersion);
                os.writeBoolean(isPersistentDebug());
                if (isPersistentDebug()) {
                    os.writeBoolean(Model.isDebug());
                    os.writeBoolean(m.isDebugConn());
                    os.writeBoolean(isUseConsoleFile());
                } else {
                    os.writeBoolean(false);
                    os.writeBoolean(false);
                    os.writeBoolean(false);
                }
                os.flush();
                bytes = bos.toByteArray();
                if (recordStore.getNumRecords() == 1) {
                    recordStore.addRecord(bytes, 0, bytes.length);
                    // Log.debug("1 Size:"+bytes.length);
                } else {
                    // Log.debug("2 Size:"+bytes.length);
                    recordStore.setRecord(2, bytes, 0, bytes.length);
                }
                os.close();
                os = null;
                bos = null;
                Log.debug("Stored BT URL " + m.getBluetoothGPSName() + " "
                        + m.getBluetoothGPSURL());
            } catch (final Exception exception) {
                Log.error("While BT save", exception);
            }
            recordStore.closeRecordStore();
        } catch (final Throwable exception) {
            Log.error("Problem saving settings", exception);
        }
        m.getIntOpt(AppSettings.FILEFIELDFORMAT);
    }

    private static String removeNull(final String text) {
        return text != null ? text : "";
    }

    private static String restoreNull(final String text) {
        return text.length() > 0 ? text : null;
    }

    private boolean consoleIsOpen = false;

    /**
     * If true, write debug information to console file.
     */
    private boolean useConsoleFile = false;

    public final void setUseConsoleFile(final boolean useConsoleFile) {
        this.useConsoleFile = useConsoleFile;
        setupConsoleFile(useConsoleFile);
    }

    public final void setupConsoleFile(final boolean doOpen) {
        Log.info("Setup up console to file");
        if (!doOpen) {
            if (consoleIsOpen) {
                Log.setOutputStream(null);
            }
            consoleIsOpen = false;
        } else {
            if (!consoleIsOpen) {
                try {
                    final String fn = "file://"
                            + m.getStringOpt(AppSettings.OUTPUTDIRPATH)
                            + File.separatorStr + "BT747Console.log";
                    javax.microedition.io.file.FileConnection fc;
                    try {
                        fc = (javax.microedition.io.file.FileConnection) Connector
                                .open(fn);
                        if (fc.exists()) {
                            fc.delete();
                        }
                        fc.close();
                    } catch (final Throwable e) {
                        Log.debug("Delete", e);
                    }

                    try {
                        fc = (javax.microedition.io.file.FileConnection) Connector
                                .open(fn);
                        fc.create();
                        fc = (javax.microedition.io.file.FileConnection) Connector
                                .open(fn, Connector.WRITE);
                        Log.setOutputStream(fc.openOutputStream());
                    } catch (final IOException e) {
                        Log.debug("Open " + fn, e);
                    }
                    consoleIsOpen = true;
                } catch (final Throwable e) {
                    Log.debug("Open console", e);
                }
            }
        }
    }

    /**
     * When true, the debug settings must be saved.
     */
    private boolean persistentDebug = false;

    public final boolean isPersistentDebug() {
        return persistentDebug;
    }

    public final void setPersistentDebug(final boolean persistentDebug) {
        this.persistentDebug = persistentDebug;
    }

    public final boolean isUseConsoleFile() {
        return useConsoleFile;
    }

}
