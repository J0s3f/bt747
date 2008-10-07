package net.sf.bt747.j4me.app;

import org.j4me.ui.components.CheckBox;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.RadioButton;
import org.j4me.ui.components.TextBox;

import bt747.model.Model;
import bt747.sys.Convert;

/**
 * The "Initializing GPS..." alert screen. This screen is used to get the
 * <code>LocationProvider</code> for the application. It first tries to get a
 * provider on the device. But if it cannot it will get a GPS provider through a
 * Bluetooth connection.
 */
public final class ConvertToScreen extends net.sf.bt747.j4me.app.screens.BT747Dialog {
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
    private CheckBox cbHeightCorrection;

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

    private boolean screenIsSetup = false;

    /**
     * Sets up the screen.
     */
    private void setUpScreen() {
        if (!screenIsSetup) {
            screenIsSetup = true;
            deleteAll();
            setTitle("Convert Log");
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
            l = new Label("'" + getRightMenuText() + "' to start conversion.");
            append(l);
            l = new Label("WARNING: Conversion is very slow.");
            append(l);

            rbFiles = new RadioButton();
            rbFiles.append("One file");
            rbFiles.append("One file/day");
            rbFiles.append("One file/track");
            rbFiles.setSelectedIndex(c.getModel().getOutputFileSplitType());
            append(rbFiles);

            cbHeightCorrection = new CheckBox();
            cbHeightCorrection.setLabel("Correct Height");
            cbHeightCorrection.setChecked(c.getModel().isConvertWGS84ToMSL());
            append(cbHeightCorrection);

            tbTrackSeparation = new TextBox();
            tbTrackSeparation.setForNumericOnly();
            tbTrackSeparation.setLabel("Trk separation time (min)");
            tbTrackSeparation.setString(Integer.toString(c.getModel()
                    .getTrkSep()));
            append(tbTrackSeparation);

            rbTimeOffsetHours = new RadioButton();
            for (int i = 0; i < offsetStr.length; i++) {
                rbTimeOffsetHours.append(offsetStr[i]);
            }
            int offsetIdx = c.getModel().getTimeOffsetHours() + 12;
            if (offsetIdx > 26) {
                c.setTimeOffsetHours(0); // TODO: Change in call to control
                offsetIdx = 12;
            }
            rbTimeOffsetHours.setSelectedIndex(offsetIdx);
            append(rbTimeOffsetHours);
        }
    }

    public void show() {
        setUpScreen();
        super.show();
    }

    protected void acceptNotify() {
        ConvertToProgressScreen progressScreen;
        c.getAppModel().setSelectedOutputFormat(getSelectedLogType());
        c.setOutputFileSplitType(rbFiles.getSelectedIndex());
        c.setConvertWGS84ToMSL(cbHeightCorrection.isChecked());
        c.setTrkSep(Integer.parseInt(tbTrackSeparation.getString()));
        int index = 0;
        // Work around superwaba bug
        String tmp = (String) rbTimeOffsetHours.getSelectedValue();
        if (tmp.charAt(0) == '+') {
            index = 1;
        }
        c.setTimeOffsetHours(Convert.toInt((String) tmp.substring(index)));

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