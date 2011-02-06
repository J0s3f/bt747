package net.sf.bt747.j4me.app;

import gps.Txt;
import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.ui.components.CheckBox;

import bt747.model.AppSettings;

public final class FileFieldSelectScreen extends BT747Dialog {

    private static final int C_LOG_FMT_COUNT = 21 - 1;

    private CheckBox[] chkLogFmtItems = new CheckBox[FileFieldSelectScreen.C_LOG_FMT_COUNT];
    private CheckBox commentCheck;
    private CheckBox nameCheck;

    private boolean screenSetup = false;

    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            deleteAll();
            for (int i = 0; i < FileFieldSelectScreen.C_LOG_FMT_COUNT; i++) {
                chkLogFmtItems[i] = new CheckBox();
                chkLogFmtItems[i].setLabel(Txt.logFmtItems[i]);
                append(chkLogFmtItems[i]);
            }
            commentCheck = new CheckBox();
            commentCheck.setLabel("Trk point comment");
            nameCheck = new CheckBox();
            nameCheck.setLabel("Trk point name");
            append(commentCheck);
            append(nameCheck);
        }
    }

    /** Get the format set by the user in the user interface. */
    private int getSelectedLogFormat() {
        int bitMask = 1;
        int logFormat = 0;
        for (int i = 0; i < FileFieldSelectScreen.C_LOG_FMT_COUNT; i++) {
            if (chkLogFmtItems[i].isChecked()) {
                logFormat |= bitMask;
            }
            bitMask <<= 1;
        }
        // Special case : valid fix only
        return logFormat;
    }

    public void showNotify() {
        setupScreen();
        updateLogFormat(m().getIntOpt(AppSettings.FILEFIELDFORMAT));
        commentCheck.setChecked(m().getBooleanOpt(
                AppSettings.IS_WRITE_TRACKPOINT_COMMENT));
        nameCheck.setChecked(m().getBooleanOpt(
                AppSettings.IS_WRITE_TRACKPOINT_NAME));
        invalidate();
    }

    protected void acceptNotify() {
        saveLogFormat();
        previous.show();
    }

    protected void declineNotify() {
        previous.show();
    }

    /**
     * Updates the format options shown the the user.<br>
     * This is typically done when the device responded with the current
     * settings.
     * 
     * @param pLogFormat
     *                LogFormat to set
     */
    private void updateLogFormat(final int pLogFormat) {
        // Log.debug("Update FileFieldFormat:" +
        // bt747.sys.Convert.unsigned2hex(pLogFormat, 8));
        int bitMask = 1;
        for (int i = 0; i < FileFieldSelectScreen.C_LOG_FMT_COUNT; i++) {
            chkLogFmtItems[i].setChecked((pLogFormat & bitMask) != 0);
            // chkLogFmtItems[i].repaintNow();
            bitMask <<= 1;
        }
        commentCheck.setChecked(m().getBooleanOpt(
                AppSettings.IS_WRITE_TRACKPOINT_COMMENT));
        nameCheck.setChecked(m().getBooleanOpt(
                AppSettings.IS_WRITE_TRACKPOINT_NAME));
        invalidate();
        repaint();
    }

    private void saveLogFormat() {
        c.setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_COMMENT, commentCheck
                .isChecked());
        c.setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_NAME, nameCheck
                .isChecked());
        c.setIntOpt(AppSettings.FILEFIELDFORMAT, getSelectedLogFormat());
        // Log.debug("FileFieldFormat:" +
        // bt747.sys.Convert.unsigned2hex(getSelectedLogFormat(), 8));
    }

    private final AppModel m() {
        return c.getAppModel();
    }

}
