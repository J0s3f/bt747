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

import javax.microedition.lcdui.Graphics;

import net.sf.bt747.j4me.app.screens.ErrorAlert;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.ProgressBar;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.interfaces.BT747Int;

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
public final class AgpsUploadProgressScreen extends Dialog implements
        ModelListener {
    /**
     * The label that displays the alert's text.
     */
    private Label label = new Label();
    private Label bytesUploaded = new Label();

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
    public AgpsUploadProgressScreen(final AppController c,
            final DeviceScreen previous) {
        this.c = c;
        this.previous = previous;
        setTitle("Upload AGPS data");
    }

    boolean screenSetup = false;

    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            // Set the title and text.

            // Add the label to the form.
            label.setHorizontalAlignment(Graphics.HCENTER);
            label.setLabel("AGPS Upload Progress");
            // label.visible(false);
            append(label);

            // Add a progress bar.
            bar = new ProgressBar();
            bar.setHorizontalAlignment(Graphics.HCENTER);
            bar.setMaxValue(100);
            bar.setValue(0);
            append(bar);

            append(new Label("Please wait untill the AGPS data is uploaded."
                    + "If you abort before the upload is finished, "
                    + "exit the application and then switch your"
                    + " logger on and off."));

            // bytes.setLabel("Bytes downloaded:");
            // append(bytes);
            //
            // bytesDownloaded.setLabel("0");
            // append(bytesDownloaded);
            // append(stats);

            // Add the menu buttons.
            setMenuText("Abort wait", null);

            try {
                m().addListener(this);
            } catch (final Throwable t) {
                Log.error("Unhandled exception in UI worker thread for "
                        + getTitle(), t);

                // Display the error.
                final ErrorAlert error = new ErrorAlert(
                        "Unhandled Exception", t.toString(), this);
                error.show();
            }

        }

    }

    /**
     * Executes the worker thread. This method synchronizes with the main UI
     * thread to avoid any race conditions involved with the order screens are
     * set.
     */
    public final void run() {
    }

    /**
     * Launches the worker thread when the screen is shown on the device.
     * 
     * @see DeviceScreen#showNotify()
     */
    public final void showNotify() {
        setupScreen();
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
        // Do nothing
    }

    /**
     * Goes to the next screen after the user hits the cancel button.
     */
    protected final void declineNotify() {
        previous.show();
    }

    private void agpsUploadDone() {
        m().removeListener(this);
        setMenuText("Back", null);
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    /**
     * Update the progress status
     */
    private void progressUpdate(int value) {
        // int min;
        final int max = 100;

        // min = m().getStartAddr();
        // max = m().getEndAddr();

        bar.setMaxValue(max);
        bar.setValue(value);
        bytesUploaded.setLabel(Integer.toString(value) + "%");
        if (isShown()) {
            bar.repaint();
            bytesUploaded.repaint();
        }

    }

    public final void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.AGPS_UPLOAD_DONE:
            agpsUploadDone();
            break;
        case ModelEvent.AGPS_UPLOAD_PERCENT:
            progressUpdate(((BT747Int) e.getArg()).getValue());
            break;
        default:
            break;
        }
    }

}
