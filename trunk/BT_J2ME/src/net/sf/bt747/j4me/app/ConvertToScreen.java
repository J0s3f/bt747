package net.sf.bt747.j4me.app;

import net.sf.bt747.j4me.app.screens.PathSelectionScreen;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.MenuItem;
import org.j4me.ui.components.CheckBox;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.RadioButton;
import org.j4me.ui.components.TextBox;

import com.sun.midp.io.j2me.storage.File;

import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.sys.Convert;

/**
 * The "Initializing GPS..." alert screen. This screen is used to get the
 * <code>LocationProvider</code> for the application. It first tries to get a
 * provider on the device. But if it cannot it will get a GPS provider through a
 * Bluetooth connection.
 */
public final class ConvertToScreen extends
        net.sf.bt747.j4me.app.screens.BT747Dialog {
    /**
     * Output format selection.
     */
    private RadioButton rbFormats;

    /**
     * Select how files are split.
     */
    private RadioButton rbFiles;

    /**
     * Select whether height must be corrected or not.
     */
    private RadioButton rbHeightCorrection;

    /**
     * Select the time in minutes that will separate a track.
     */
    private TextBox tbTrackSeparation;

    /**
     * List of UTC offsets.
     */
    private static final String[] offsetStr = { "-12", "-11", "-10", "-9",
            "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "+0", "+1", "+2",
            "+3", "+4", "+5", "+6", "+7", "+8", "+9", "+10", "+11", "+12",
            "+13", "+14" };

    private RadioButton rbTimeOffsetHours;

    private TextBox tbBinFile;

    private boolean screenIsSetup = false;

    /**
     * Output format selection.
     */
    private RadioButton rbDevice;

    /**
     * Sets up the screen.
     */
    private void setUpScreen() {
        if (!screenIsSetup) {
            screenIsSetup = true;
            deleteAll();
            setTitle("Convert Log");

            Label l;
            l = new Label("'" + getRightMenuText() + "' to start conversion of:");
            append(l);
            tbBinFile = new TextBox() {
                public void keyReleased(int keyCode) {
                    if ((keyCode > 0) || (keyCode == DeviceScreen.FIRE)) {
                        selectBaseFile();
                    }
                }
            };
            tbBinFile.setString(c.getAppModel().getStringOpt(
                    AppModel.LOGFILERELPATH));
            append(tbBinFile);
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
            l = new Label("WARNING: Conversion is slow!");
            append(l);

            rbDevice = new RadioButton();
            rbDevice.append("Default device");
            rbDevice.append("Holux M-241");
            if (!c.getAppModel().getBooleanOpt(Model.FORCE_HOLUXM241)) {
                rbDevice.setSelectedIndex(0);
            } else {
                rbDevice.setSelectedIndex(1);
            }
            append(rbDevice);

            rbFiles = new RadioButton();
            rbFiles.append("One file");
            rbFiles.append("One file/day");
            rbFiles.append("One file/track");
            rbFiles.setSelectedIndex(m().getOutputFileSplitType());
            append(rbFiles);

            rbHeightCorrection = new RadioButton();
            rbHeightCorrection.append("Automatic height");
            rbHeightCorrection.append("Keep height");
            rbHeightCorrection.append("WGS84 to MSL");
            rbHeightCorrection.append("MSL to WGS84");
            switch(m().getHeightConversionMode()) {
            case Model.HEIGHT_AUTOMATIC:
                rbFiles.setSelectedIndex(0);
                break;
            case Model.HEIGHT_NOCHANGE:
                rbFiles.setSelectedIndex(1);
                break;
            case Model.HEIGHT_WGS84_TO_MSL:
                rbFiles.setSelectedIndex(2);
                break;
            case Model.HEIGHT_MSL_TO_WGS84:
                rbFiles.setSelectedIndex(3);
                break;
            }
            append(rbHeightCorrection);

            tbTrackSeparation = new TextBox();
            tbTrackSeparation.setForNumericOnly();
            tbTrackSeparation.setLabel("Trk separation time (min)");
            tbTrackSeparation.setString(Integer.toString(m()
                    .getTrkSep()));
            append(tbTrackSeparation);

            rbTimeOffsetHours = new RadioButton();
            for (int i = 0; i < offsetStr.length; i++) {
                rbTimeOffsetHours.append(offsetStr[i]);
            }
            int offsetIdx = m().getIntOpt(Model.GPSTIMEOFFSETHOURS) + 12;
            if (offsetIdx > 26) {
                c.setIntOpt(AppSettings.GPSTIMEOFFSETHOURS, 0); // TODO: Change in call to control
                offsetIdx = 12;
            }
            rbTimeOffsetHours.setSelectedIndex(offsetIdx);
            append(rbTimeOffsetHours);
        }
    }

    private final AppModel m() {
        return c.getAppModel();
    }
    public void show() {
        setUpScreen();
        super.show();
    }

    protected void acceptNotify() {
        ConvertToProgressScreen progressScreen;
        c.getAppModel().setSelectedOutputFormat(getSelectedLogType());
        c.setOutputFileSplitType(rbFiles.getSelectedIndex());
        switch (rbHeightCorrection.getSelectedIndex()) {
        case 0:
            c.setHeightConversionMode(Model.HEIGHT_AUTOMATIC);
            break;
        case 1:
            c.setHeightConversionMode(Model.HEIGHT_NOCHANGE);
            break;
        case 2:
            c.setHeightConversionMode(Model.HEIGHT_WGS84_TO_MSL);
            break;
        case 3:
            c.setHeightConversionMode(Model.HEIGHT_MSL_TO_WGS84);
            break;
        }
        c.setTrkSep(Integer.parseInt(tbTrackSeparation.getString()));
        c.setBooleanOpt(Model.FORCE_HOLUXM241, rbDevice.getSelectedIndex() == 1);
        int index = 0;
        // Work around superwaba bug
        String tmp = (String) rbTimeOffsetHours.getSelectedValue();
        if (tmp.charAt(0) == '+') {
            index = 1;
        }
        c.setIntOpt(AppSettings.GPSTIMEOFFSETHOURS, Convert.toInt((String) tmp.substring(index)));

        progressScreen = new ConvertToProgressScreen(c, previous,
                getSelectedLogType());
        deleteAll(); // Clean up screen.
        progressScreen.show();
        progressScreen = null;
    }

    protected void declineNotify() {
        deleteAll();
        previous.show();
        super.declineNotify();
    }

    // TODO: to generalise
    private void selectBaseFile() {
        DeviceScreen d;
        d = new PathSelectionScreen("Input file", this, m()
                .getStringOpt(AppModel.LOGFILEPATH), false) {
            protected void notifyPathSelected(final String path) {
                c.setStringOpt(AppModel.LOGFILERELPATH, gps.convert.FileUtil
                        .getRelativePath(m().getStringOpt(
                                AppModel.OUTPUTDIRPATH), path, '/'));
                c.setPaths();
                tbBinFile.setString(m().getStringOpt(
                        AppModel.LOGFILERELPATH));
            }
        };
        d.show();
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
