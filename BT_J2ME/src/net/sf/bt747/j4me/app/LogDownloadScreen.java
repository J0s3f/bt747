package net.sf.bt747.j4me.app;

import javax.microedition.lcdui.Graphics;

import net.sf.bt747.j2me.system.MyThread;

import org.j4me.examples.log.LogScreen;
import org.j4me.examples.ui.screens.ErrorAlert;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.ProgressBar;
import org.j4me.ui.components.TextBox;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * This is a base class for alert screens. It provides a background thread for
 * doing lengthy tasks such as retrieving data from the network. While the task
 * runs this screen shows an indefinite progress bar (a spinner) with some text
 * about what operation is going on. When the background thread completes the
 * screen dismisses itself and goes to the next screen.
 * <p>
 * Alerts have a "Cancel" button on them if the user wants to stop the
 * operation.
 */
public class LogDownloadScreen extends Dialog implements ModelListener,
        Runnable {
    /**
     * The label that displays the alert's text.
     */
    private Label label = new Label();
    private Label bytesDownloaded = new Label();
    private Label bytes = new Label();
    private Label file = new Label();
    private Label status = new Label();

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
     *            is the alert's title.
     * @param text
     *            is the alert message.
     */
    public LogDownloadScreen(final AppController c, final DeviceScreen previous) {
        this.c = c;
        this.previous = previous;

        // Set the title and text.
        setTitle("Download Log");

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

        bytes = new Label("Bytes downloaded:");
        append(bytes);

        bytesDownloaded = new Label("0");
        append(bytesDownloaded);

        // createNewSection("Log conditions");
        file = new Label("Bin file:");
        file.setLabel(m().getLogFilePath());
        append(file);

        status = new Label("Download inactive");
        append(status);

        tb.setString(null); // Indicates unused
        // Add the menu buttons.
        setMenuText("Menu", "Back");

        logScreen = new LogScreen(this);
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
        } catch (Throwable t) {
            Log.error("Unhandled exception in UI worker thread for "
                    + getTitle(), t);

            // Display the error.
            ErrorAlert error = new ErrorAlert("Unhandled Exception", t
                    .toString(), this);
            error.show();
        }
    }

    private final void startDownload() {
        if (!m().isDownloadOnGoing()) {
            bar.visible(true);
            label.visible(true);
            bytesDownloaded.visible(true);
            bytes.visible(true);
            m().addListener(this);
            Thread worker = new Thread(this);
            worker.start();
        }
    }

    /**
     * Launches the worker thread when the screen is shown on the device.
     * 
     * @see DeviceScreen#showNotify()
     */
    public final void showNotify() {
        if (m().isDownloadOnGoing()) {
            progressUpdate();
            // Log.info("Download ongoing");
        } else {
            file.setLabel(m().getLogFilePath());
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
    protected final void declineNotify() {
        // logScreen.show();

        Menu menu = new Menu("Log menu", this) {
            public void showNotify() {
                if(tb.getString().length()!=0) {
                    c.setLogFileRelPath(tb.getString());
                    tb.setString(null);
                    logDownload.show();
                }
                super.showNotify();
            }
        };
        
        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "Start download";
            }

            public void onSelection() {
                startDownload();
                logDownload.show();
            }
        });

        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "Basename";
            }

            public void onSelection() {
                tb.setForAnyText();
                tb.setString(m().getLogFile());
                // Simulate selection for entry
                tb.keyPressed(DeviceScreen.FIRE);
            }
        });

        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "Cancel download";
            }

            public void onSelection() {
                c.cancelGetLog();
                logDownload.show();
            }
        });
        c.cancelGetLog();

        menu.appendMenuOption("Application Log", logScreen);
        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "To background";
            }

            public void onSelection() {
                previous.show();
            }
        });
        menu.appendMenuOption("Reconnect", new InitializingGPSAlert(c, this));

        menu.show();
        menu = null;
    }

    /**
     * Goes to the next screen after the user hits the cancel button.
     */
    protected final void acceptNotify() {
        previous.show();
    }

    protected void returnNotify() {
        // Override because declineNotify is not the correct default.
        acceptNotify();
    }

    private void downloadDone() {
        m().removeListener(this);
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    private long nextUpdate = 0;

    /**
     * Update the progress status
     */
    private void progressUpdate() {
        // int min;
        int max;
        int value;

        if (m().isDownloadOnGoing()) {
            // min = m().getStartAddr();
            max = m().getEndAddr();
            value = m().getNextReadAddr();

            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextUpdate || !isShown()) {
                nextUpdate = currentTime + 100L;
                bar.setMaxValue(max);
                bar.setValue(value);
                bytes.setLabel(Integer.toString(value));
                if (isShown()) {
                    bar.repaint();
                    bytes.repaint();
                }
            }
        }
    }

    private Object lock = new Object();
    private boolean success = false;

    public final void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.LOG_DOWNLOAD_STARTED:
            Log.debug("Download started");
            synchronized (lock) {
                success = false;
            }
            status.setLabel("Downloading");
            /* fall through */
        case ModelEvent.DOWNLOAD_STATE_CHANGE:
            // Log.debug("Progress update");
            progressUpdate();
            break;
        case ModelEvent.LOG_DOWNLOAD_DONE:
            synchronized (lock) {
                if (!success) {
                    status.setLabel(status.getLabel()
                            + "\n**Download interrupted**");
                }
            }
            downloadDone();
            break;
        case ModelEvent.LOG_DOWNLOAD_SUCCESS:
            synchronized (lock) {
                success = true;
            }
            status.setLabel("Download success");
            Log.debug("Download success");
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            // When the data on the device is not the same, overwrite
            // automatically.
            Log.info("No confirmation to overwrite different data");
            c.replyToOkToOverwrite(false);
            status.setLabel("Not overwriting different data."
                    + " Set NORMAL DOWNLOAD or erase file.");
            break;

        default:
            break;
        }
    }

}
