package net.sf.bt747.j4me.app;

import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.CheckBox;
import org.j4me.ui.components.TextBox;

import bt747.model.AppSettings;

public class PosSrvAuthConfigScreen extends BT747Dialog {

	private boolean screenSetup = false;

	/**
	 * Textbox for the username to authenticate to the server.
	 */
	private TextBox tbUsername;

	/**
	 * Textbox for the password to authenticate to the server.
	 */
	private TextBox tbPassword;

	/**
	 * Create an instance of this Screen.
	 * 
	 * @param c
	 *            Controller The applications's Controller
	 * @param previous
	 *            The previously show screen to which we will return when this
	 *            screen is closed.
	 */
	public PosSrvAuthConfigScreen(final AppController c,
			final DeviceScreen previous) {
		this.c = c;
		this.previous = previous;
		setTitle("Auth. Configuration");
	}

	/**
	 * Retrieve the Model from the Controller and return that back. Required for
	 * access to the settings of the application.
	 * 
	 * @return Model The Model of this application.
	 */
	private final AppModel getModel() {
		return c.getAppModel();
	}

	/**
	 * Setup all GUI elements and populate the dialog with data.
	 */
	public void setupScreen() {
		if (!screenSetup) {
			screenSetup = true;
			deleteAll();

			setTitle("Auth. Configuration");
			// Set the menu bar options.
			setMenuText(UIManager.getTheme().getMenuTextForCancel(), "Ok");

			tbUsername = new TextBox();
			tbUsername.setLabel("Username");
			append(tbUsername);

			tbPassword = new TextBox();
			tbPassword.setLabel("Password");
			append(tbPassword);

			getSettings();
			invalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.j4me.ui.DeviceScreen#showNotify()
	 */
	public void showNotify() {
		Log.debug("PosSrvAuthConfigScreen: showNotify");
		setupScreen();
		super.showNotify();
	}

	/**
	 * Called when the user presses the "Ok" button.
	 * 
	 * @see org.j4me.ui.DeviceScreen#acceptNotify()
	 */
	protected final void acceptNotify() {
		Log.debug("PosSrvAuthConfigScreen: acceptNotify");
		setSettings();
		if (getModel().getBooleanOpt(AppSettings.POS_SRV_AUTOSTART)) {
			c.stopGPSPositionServing();
			c.startGPSPositionServing();
		}
		previous.show();
		// Continue processing the event.
		super.acceptNotify();
	}

	/**
	 * Called when the user presses the "Back" button.
	 * 
	 * @see org.j4me.ui.DeviceScreen#declineNotify()
	 */
	protected final void declineNotify() {
		Log.debug("PosSrvAuthConfigScreen: declineNotify");
		// Go back to the previous screen.
		if (previous != null) {
			previous.show();
			super.declineNotify();
		}
	}

	/**
	 * Retrieve values for the fields from settings and set values to the
	 * fields.
	 */
	private final void getSettings() {
		tbUsername.setString(getModel().getStringOpt(AppSettings.POS_SRV_USER));
		tbPassword.setString(getModel().getStringOpt(AppSettings.POS_SRV_PASS));
		repaint();
		Log.debug("PosSrvAuthConfigScreen: settings retrieved");
	}

	/**
	 * Persist the settings.
	 */
	private final void setSettings() {
		c.setStringOpt(AppSettings.POS_SRV_USER, tbUsername.getString());
		c.setStringOpt(AppSettings.POS_SRV_PASS, tbPassword.getString());
		Log.debug("PosSrvAuthConfigScreen: settings updated");
	}

}
