package net.sf.bt747.j4me.app;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.HorizontalRule;
import org.j4me.ui.components.Label;

/**
 * The "Log" screen. This shows the contents of the application's log. It is
 * an advanced screen intended for us to diagnose the application.
 */
public final class ConfirmScreen extends Dialog {
    private Label lbText;

    private boolean confirmation = false;
    private boolean isAcceptNotifyAConfirmation;

    private String next;
    private DeviceScreen previous;

    private String textYes;
    private String textNo;

    private boolean screenSetup = false;

    /**
     * Constructs the "Log" screen.
     * 
     * @param previous
     *                is the screen that invoked this one. If this is <c>null</c>
     *                the application will exit when this screen is dismissed.
     */
    public ConfirmScreen(final String title, final String textYes,
            final String textNo, final DeviceScreen ds) {
        setTitle(title);
        previous = ds;
        this.textYes = textYes;
        this.textNo = textNo;
    }

    private void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            append(new HorizontalRule());

            if (textYes != null) {
                lbText = new Label(textYes);
                setMenuText("No", "Confirm");
                isAcceptNotifyAConfirmation = true;
                next = textNo;
            } else if (textNo != null) {
                lbText = new Label(textNo);
                setMenuText("Yes", "No");
                isAcceptNotifyAConfirmation = false;
                next = null;
            }
            lbText.setFont(UIManager.getTheme().getMenuFont());

            append(lbText);
            append(new HorizontalRule());

            // Add the menu buttons.
            setFullScreenMode(false);
        }
    }

    public final boolean getConfirmation() {
        return confirmation;
    }

    /**
     * Called when this screen is going to be displayed.
     * 
     * @see DeviceScreen#showNotify()
     */
    public final void showNotify() {
        setupScreen();
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    /**
     * Called when the user presses the "Log" button.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected final void declineNotify() {

        if (!isAcceptNotifyAConfirmation) {
            confirmation = true;
            previous.show();
        } else {
            confirmation = false;
            previous.show();
        }
        // Continue processing the event.
        super.declineNotify();
    }

    /**
     * Called when the user presses the "Other" button.
     * 
     * @see DeviceScreen#acceptNotify()
     */
    protected final void acceptNotify() {
        if (!isAcceptNotifyAConfirmation) {
            confirmation = false;
            previous.show();
        } else {
            if (next != null) {
                setMenuText("Yes", "No");
                isAcceptNotifyAConfirmation = false;
                lbText.setLabel(next);
                next = null;
                show();
            } else {
                confirmation = true;
                previous.show();
            }

        }
    }

    protected void returnNotify() {
        confirmation = false;
        previous.show();
    }

}
