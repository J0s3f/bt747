package net.sf.bt747.j4me.app;

import javax.microedition.lcdui.Graphics;

import org.j4me.examples.ui.screens.ErrorAlert;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Theme;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.ProgressBar;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * This is a base class for alert screens. It provides a background thread for
 * doing lengthy tasks such as retrieving data from the network. While the task
 * runs this screen shows an indefinate progress bar (a spinner) with some text
 * about what operation is going on. When the background thread completes the
 * screen dismisses itself and goes to the next screen.
 * <p>
 * Alerts have a "Cancel" button on them if the user wants to stop the
 * operation.
 */
public class DownloadLog extends Dialog implements ModelListener, Runnable {
    /**
     * The label that displays the alert's text.
     */
    private Label label = new Label();

    /**
     * An progress bar that informs the user about the download progress.
     */
    private ProgressBar bar;

    private DeviceScreen previous;
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
    public DownloadLog(AppController c, DeviceScreen previous) {
        this.c = c;
        this.previous = previous;

        // Set the title and text.
        setTitle("Download Log");

        // Add the label to the form.
        label.setHorizontalAlignment(Graphics.HCENTER);
        label.setLabel("Log download in progress");
        append(label);

        // Add a progress bar.
        bar = new ProgressBar();
        bar.setHorizontalAlignment(Graphics.HCENTER);
        append(bar);

        // Add the menu buttons.
        Theme theme = UIManager.getTheme();
        String cancel = theme.getMenuTextForCancel();
        setMenuText(cancel, null);
    }

    /**
     * Executes the worker thread. This method synchronizes with the main UI
     * thread to avoid any race conditions involved with the order screens are
     * set.
     */
    public void run() {
        try {
            c.startDefaultDownload();
            m().addListener(this);
        } catch (Throwable t) {
            Log.error("Unhandled exception in UI worker thread for "
                    + getTitle(), t);

            // Display the error.
            ErrorAlert error = new ErrorAlert("Unhandled Exception", t
                    .toString(), this);
            error.show();
        }
    }

    /**
     * Launches the worker thread when the screen is shown on the device.
     * 
     * @see DeviceScreen#showNotify()
     */
    public void showNotify() {
        Thread worker = new Thread(this);
        worker.start();

        // Continue processing the event.
        super.showNotify();
    }

    /**
     * Goes to the next screen after the user hits the cancel button.
     */
    protected void declineNotify() {
        c.cancelGetLog();
        downloadDone();

        // Continue processing the event.
        super.declineNotify();
    }

    private void downloadDone() {
        m().removeListener(this);
        previous.show();
    }

    private final AppModel m() {
        return c.getAppModel();
    }

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

            bar.setMaxValue(max);
            bar.setValue(value);
        }
    }

    public void modelEvent(ModelEvent e) {
        // TODO Auto-generated method stub
        switch (e.getType()) {
        case ModelEvent.DOWNLOAD_STATE_CHANGE:
        case ModelEvent.LOG_DOWNLOAD_STARTED:
            Log.debug("Progress update");
            progressUpdate();
            break;
        case ModelEvent.LOG_DOWNLOAD_DONE:
            downloadDone();
        }
    }

}
