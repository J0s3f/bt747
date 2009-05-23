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
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package net.sf.bt747.j4me.app;

import gps.GpsEvent;

import javax.microedition.lcdui.Graphics;

import net.sf.bt747.j4me.app.conn.InitializingGPSAlert;
import net.sf.bt747.j4me.app.log.LogScreen;
import net.sf.bt747.j4me.app.screens.DelayedDialog;
import net.sf.bt747.j4me.app.screens.ErrorAlert;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.ProgressBar;
import org.j4me.ui.components.TextBox;

import bt747.model.AppSettings;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * This is a base class for alert screens. It provides a background thread for
 * doing lengthy tasks such as retrieving data from the network. While the
 * task runs this screen shows an indefinite progress bar (a spinner) with
 * some text about what operation is going on. When the background thread
 * completes the screen dismisses itself and goes to the next screen.
 * <p>
 * Alerts have a "Cancel" button on them if the user wants to stop the
 * operation.
 */
public final class LogDownloadScreen extends Dialog implements ModelListener,
        Runnable {
    /**
     * The label that displays the alert's text.
     */
    private Label label = new Label();
    private Label bytesDownloaded = new Label();
    private Label bytes = new Label();
    private Label file = new Label();
    private Label status = new Label();
    private Label stats = new Label();

    private TextBox tb = new TextBox();

    private DeviceScreen logDownload = this;

    /**
     * An progress bar that informs the user about the download progress.
     */
    private ProgressBar bar;

    private DeviceScreen previous;

    private LogScreen logScreen;
    /**
     * This applications' controller
     */
    private AppController c;

    /**
     * Constructs an alert screen.
     * 
     * @param title
     *                is the alert's title.
     * @param text
     *                is the alert message.
     */
    public LogDownloadScreen(final AppController c,
            final DeviceScreen previous) {
        this.c = c;
        this.previous = previous;
        setTitle("Download Log");
    }

    boolean screenSetup = false;

    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            // Set the title and text.

            // Add the label to the form.
            label.setHorizontalAlignment(Graphics.HCENTER);
            label.setLabel("Log download progress");
            label.visible(false);
            append(label);

            // Add a progress bar.
            bar = new ProgressBar();
            bar.setHorizontalAlignment(Graphics.HCENTER);
            bar.setMaxValue(1);
            bar.setValue(0);
            append(bar);

            bytes.setLabel("Bytes downloaded:");
            append(bytes);

            bytesDownloaded.setLabel("0");
            append(bytesDownloaded);
            append(stats);

            // createNewSection("Log conditions");
            file.setLabel("Bin file:");
            final AppModel r = m();
            file.setLabel(r.getStringOpt(AppSettings.LOGFILEPATH));
            append(file);

            status.setLabel("Download inactive");
            append(status);

            tb.setString(null); // Indicates unused
            // Add the menu buttons.
            setMenuText(UIManager.getTheme().getMenuTextForCancel(), "Menu");

            logScreen = new LogScreen(this);
        }

    }

    /**
     * Executes the worker thread. This method synchronizes with the main UI
     * thread to avoid any race conditions involved with the order screens are
     * set.
     */
    public final void run() {
        try {
            m().addListener(this);
            c.startDefaultDownload();
            Log.info("Download requested");
        } catch (final Throwable t) {
            Log.error("Unhandled exception in UI worker thread for "
                    + getTitle(), t);

            // Display the error.
            final ErrorAlert error = new ErrorAlert("Unhandled Exception", t
                    .toString(), this);
            error.show();
        }
    }

    private final void startDownload() {
        if (!m().isDownloadOnGoing()) {
            m().addListener(this);
            final Thread worker = new Thread(this);
            worker.start();
        }
    }

    /**
     * Launches the worker thread when the screen is shown on the device.
     * 
     * @see DeviceScreen#showNotify()
     */
    public final void showNotify() {
        setupScreen();
        if (m().isDownloadOnGoing()) {
            progressUpdate();
            // Log.info("Download ongoing");
        } else {
            final AppModel r = m();
            file.setLabel(r.getStringOpt(AppSettings.LOGFILEPATH));
            file.repaint();
        }

        // Continue processing the event.
        super.showNotify();
    }

    public void hideNotify() {
        if (!m().isDownloadOnGoing()) {
        }
        super.hideNotify();
    }

    /**
     * Goes to the next screen after the user hits the cancel button.
     */
    protected final void acceptNotify() {
        // logScreen.show();

        final Menu menu = new Menu("Log menu", this) {
            public final void showNotify() {
                if (tb.getString().length() != 0) {
                    c
                            .setStringOpt(AppSettings.LOGFILERELPATH, tb
                                    .getString());
                    c.setPaths();
                    tb.setString(null);
                    logDownload.show();
                }
                super.showNotify();
            }
        };

        menu.appendMenuOption(new MenuItem() {
            public final String getText() {
                return "Start download";
            }

            public final void onSelection() {
                startDownload();
                logDownload.show();
            }
        });

        menu.appendMenuOption(new MenuItem() {
            public final String getText() {
                return "Binary filename";
            }

            public final void onSelection() {
                tb.setForAnyText();
                tb.setString(m().getStringOpt(AppSettings.LOGFILERELPATH));
                // Simulate selection for entry
                tb.keyPressed(DeviceScreen.FIRE);
                tb.keyReleased(DeviceScreen.FIRE);
            }
        });

        menu.appendMenuOption("Download Settings", new DelayedDialog(
                ScreenFactory.LOGDOWNLOADSCREEN, c, this, this));

        menu.appendMenuOption(new MenuItem() {
            public final String getText() {
                return "Cancel download";
            }

            public final void onSelection() {
                c.cancelGetLog();
                logDownload.show();
            }
        });

        menu.appendMenuOption("Application Log", logScreen);
        menu.appendMenuOption(new MenuItem() {
            public final String getText() {
                return "To background";
            }

            public final void onSelection() {
                previous.show();
            }
        });
        menu.appendMenuOption("Reconnect", new InitializingGPSAlert(c, this));

        menu.show();
    }

    /**
     * Goes to the next screen after the user hits the cancel button.
     */
    protected final void declineNotify() {
        previous.show();
    }

    protected void keyReleased(final int keyCode) {
        if (keyCode == DeviceScreen.RIGHT) {
            // Show the menu
            declineNotify();
        } else if (keyCode == DeviceScreen.LEFT) {
            // Show the menu
            acceptNotify();
        } else if (keyCode == DeviceScreen.FIRE) {
            startDownload();
        }
        super.keyReleased(keyCode);
    }

    private void downloadDone() {
        m().removeListener(this);
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    private long nextUpdate = 0;
    private long smallIntervalStartTime;
    private long smallIntervalStartDataIndex;
    private long smallIntervalEndTime;
    private long downloadStartTime;
    private long startDataIndex;

    private void initStats() {
        smallIntervalStartTime = System.currentTimeMillis();
        downloadStartTime = smallIntervalStartTime;
        smallIntervalEndTime = smallIntervalStartTime + 5000;
    }

    /**
     * Update the progress status
     */
    private void progressUpdate() {
        // int min;
        int max;
        int value;

        // min = m().getStartAddr();
        max = m().getEndAddr();
        value = m().getNextReadAddr();
        if ((value > 0) && (value < startDataIndex)) {
            startDataIndex = value;
        }

        final long currentTime = System.currentTimeMillis();
        if (!isShown() || !m().isDownloadOnGoing()
                || (currentTime >= nextUpdate)) {
            nextUpdate = currentTime + 50L;
            bar.setMaxValue(max);
            bar.setValue(value);
            bytesDownloaded.setLabel(Integer.toString(value));
            if (isShown()) {
                bar.repaint();
                bytesDownloaded.repaint();
            }

            if ((currentTime > smallIntervalEndTime)
                    || !m().isDownloadOnGoing()) {
                long smallSpeedBytesPerSecond;
                long bytesPerSecond;
                int minutes;
                long seconds = 0;
                int minutesDone;
                int secondsDone;
                smallSpeedBytesPerSecond = (long) ((1000. * (value - smallIntervalStartDataIndex)) / (currentTime - smallIntervalStartTime));
                bytesPerSecond = (long) ((1000. * (value - startDataIndex)) / (currentTime - downloadStartTime));
                // Log.debug(bytesPerSecond + " " + value + " "+
                // startDataIndex
                // + " " +currentTime + " " + downloadStartTime);
                if (bytesPerSecond > 0) {
                    seconds = (max - value) / (bytesPerSecond);
                }
                minutes = (int) (seconds / 60);
                seconds %= 60;
                secondsDone = (int) ((currentTime - downloadStartTime) / 1000);
                minutesDone = secondsDone / 60;
                secondsDone %= 60;

                stats.setLabel(bytesPerSecond + " B/s  "
                        + smallSpeedBytesPerSecond + " B/s\n" + minutesDone
                        + " min " + secondsDone + " s done " + minutes
                        + " min " + seconds + " s left");
                smallIntervalEndTime = currentTime + 5000;
                smallIntervalStartTime = currentTime;
                smallIntervalStartDataIndex = value;
                stats.repaint();
            }
        }
    }

    private Object lock = new Object();
    private boolean success = false;

    public final void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case GpsEvent.LOG_DOWNLOAD_STARTED:
            Log.debug("Download started");
            synchronized (lock) {
                success = false;
                smallIntervalStartTime = 0;
                initStats();
            }
            status.setLabel("Downloading");
            status.repaint();
            progressUpdate();
            break;
        /* fall through */
        case GpsEvent.DOWNLOAD_STATE_CHANGE:
            // Log.debug("Progress update");
            progressUpdate();
            break;
        case GpsEvent.LOG_DOWNLOAD_DONE:
            synchronized (lock) {
                progressUpdate();
                if (!success) {
                    status.setLabel(status.getLabel()
                            + "\n**Download interrupted**");
                    this.repaint();
                }
            }
            downloadDone();
            break;
        case GpsEvent.LOG_DOWNLOAD_SUCCESS:
            synchronized (lock) {
                success = true;
            }
            status.setLabel("Download success");
            status.repaint();
            Log.debug("Download success");
            break;
        default:
            break;
        }
    }

}
