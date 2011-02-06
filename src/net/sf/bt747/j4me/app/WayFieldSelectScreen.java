// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package net.sf.bt747.j4me.app;

import gps.BT747Constants;
import gps.Txt;
import gps.mvc.MtkModel;
import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.components.CheckBox;

import bt747.model.AppSettings;

public final class WayFieldSelectScreen extends BT747Dialog {

    private static final int C_LOG_FMT_COUNT = 21;

    private CheckBox[] chkLogFmtItems = new CheckBox[WayFieldSelectScreen.C_LOG_FMT_COUNT];

    private boolean screenSetup = false;

    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            deleteAll();
            setTitle("Set GPS Log Format");

            for (int i = 0; i < WayFieldSelectScreen.C_LOG_FMT_COUNT; i++) {
                chkLogFmtItems[i] = new CheckBox();
                chkLogFmtItems[i].setLabel(Txt.logFmtItems[i]);
                append(chkLogFmtItems[i]);
            }
        }
    }

    /** Get the format set by the user in the user interface. */
    private int getSelectedLogFormat() {
        int bitMask = 1;
        int logFormat = 0;
        for (int i = 0; i < WayFieldSelectScreen.C_LOG_FMT_COUNT - 1; i++) {
            if (chkLogFmtItems[i].isChecked()) {
                logFormat |= bitMask;
            }
            bitMask <<= 1;
        }
        // Special case : valid fix only
        if (chkLogFmtItems[WayFieldSelectScreen.C_LOG_FMT_COUNT - 1]
                .isChecked()) {
            logFormat |= (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX);
        }
        Log.debug("Way Point Format:"
                + bt747.sys.JavaLibBridge.unsigned2hex(logFormat, 8));

        return logFormat;
    }

    public void showNotify() {
        setupScreen();
        AppModel r = m();
        updateLogFormat(r.getIntOpt(AppSettings.WAYPT_RCR));        
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
        Log.debug("updateLogFormat");
        int bitMask = 1;
        for (int i = 0; i < WayFieldSelectScreen.C_LOG_FMT_COUNT; i++) {
            chkLogFmtItems[i].setChecked((pLogFormat & bitMask) != 0);
            // chkLogFmtItems[i].repaintNow();
            bitMask <<= 1;
        }
        chkLogFmtItems[WayFieldSelectScreen.C_LOG_FMT_COUNT - 1]
                .setChecked((pLogFormat & (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX)) != 0);
    }

    private void saveLogFormat() {
        c.setLogFormat(getSelectedLogFormat());
        c.setMtkDataNeeded(MtkModel.DATA_LOG_FORMAT);
        // Log.debug("FileFieldFormat:" +
        // bt747.sys.JavaLibBridge.unsigned2hex(getSelectedLogFormat(), 8));
    }

    private final AppModel m() {
        return c.getAppModel();
    }

}
