package net.sf.bt747.j4me.app;

import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.TextBox;

import bt747.model.AppSettings;
import bt747.sys.JavaLibBridge;

public class PosSrvServerConfigScreen extends BT747Dialog {

	private boolean screenSetup = false;

	/**
	 * Textbox for the Port (lke 80).
	 */
	private TextBox tbPort;

	/**
	 * Textbox for the server name to send location data to.
	 */
	private TextBox tbHostname;

	/**
	 * Textbox, contains last part of the URL.
	 */
	private TextBox tbFile;

	/**
	 * Create an instance of this Screen.
	 * 
	 * @param c
	 *            Controller The applications's Controller
	 * @param previous
	 *            The previously show screen to which we will return when this
	 *            screen is closed.
	 */
	public PosSrvServerConfigScreen(final AppController c,
			final DeviceScreen previous) {
		this.c = c;
		this.previous = previous;
		setTitle("Server Configuration");
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

			setTitle("Server Configuration");
			// Set the menu bar options.
			setMenuText(UIManager.getTheme().getMenuTextForCancel(), "Ok");

			tbHostname = new TextBox();
			tbHostname.setLabel("Hostname");
			append(tbHostname);

			tbPort = new TextBox();
			tbPort.setForNumericOnly();
			tbPort.setLabel("Port");
			append(tbPort);

			tbFile = new TextBox();
			tbFile.setLabel("URL");
			append(tbFile);

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
		Log.debug("PosSrvServerConfigScreen: showNotify");
		setupScreen();
		super.showNotify();
	}

	/**
	 * Called when the user presses the "Ok" button.
	 * 
	 * @see org.j4me.ui.DeviceScreen#acceptNotify()
	 */
	protected final void acceptNotify() {
		Log.debug("PosSrvServerConfigScreen: acceptNotify");
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
		Log.debug("PosSrvServerConfigScreen: declineNotify");
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
		tbHostname.setString(getModel().getStringOpt(
				AppSettings.POS_SRV_HOSTNAME));
		tbPort.setString(JavaLibBridge.toString(getModel().getIntOpt(
				AppSettings.POS_SRV_PORT)));
		tbFile.setString(getModel().getStringOpt(AppSettings.POS_SRV_FILE));
		repaint();
		Log.debug("PosSrvServerConfigScreen: settings retrieved");
	}

	/**
	 * Persist the settings.
	 */
	private final void setSettings() {
		c.setStringOpt(AppSettings.POS_SRV_HOSTNAME, tbHostname.getString());
		c.setIntOpt(AppSettings.POS_SRV_PORT, JavaLibBridge.toInt(tbPort
				.getString()));
		c.setStringOpt(AppSettings.POS_SRV_FILE, tbFile.getString());
		Log.debug("PosSrvServerConfigScreen: settings updated");
	}

}
