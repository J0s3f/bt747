package net.sf.bt747.j4me.app;

import gps.Txt;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.CheckBox;

public class LogFieldSelectScreen extends Dialog {

    private static final int C_LOG_FMT_COUNT = 21 - 1;

    private CheckBox[] chkLogFmtItems = new CheckBox[C_LOG_FMT_COUNT];
    private DeviceScreen previous;
    private AppController c;

    public LogFieldSelectScreen(final AppController c,
            final DeviceScreen previous) {
        setTitle("Set GPS Log Format");
        this.c = c;
        this.previous = previous;

        for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
            chkLogFmtItems[i] = new CheckBox();
            chkLogFmtItems[i].setLabel(Txt.logFmtItems[i]);
            append(chkLogFmtItems[i]);
        }
    }

    /** Get the format set by the user in the user interface. */
    private int getSelectedLogFormat() {
        int bitMask = 1;
        int logFormat = 0;
        for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
            if (chkLogFmtItems[i].isChecked()) {
                logFormat |= bitMask;
            }
            bitMask <<= 1;
        }
        // Special case : valid fix only
        return logFormat;
    }

    public void show() {
        updateLogFormat(m().getLogFormat());
        invalidate();
        super.show();
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
     *            LogFormat to set
     */
    private void updateLogFormat(final int pLogFormat) {
        // Log.debug("Update FileFieldFormat:" +
        // bt747.sys.Convert.unsigned2hex(pLogFormat, 8));
        int bitMask = 1;
        for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
            chkLogFmtItems[i].setChecked((pLogFormat & bitMask) != 0);
            // chkLogFmtItems[i].repaintNow();
            bitMask <<= 1;
        }
        invalidate();
        repaint();
    }

    private void saveLogFormat() {
        c.setLogFormat(getSelectedLogFormat());
        c.reqLogFormat();
        // Log.debug("FileFieldFormat:" +
        // bt747.sys.Convert.unsigned2hex(getSelectedLogFormat(), 8));
    }

    private final AppModel m() {
        return c.getAppModel();
    }

}
