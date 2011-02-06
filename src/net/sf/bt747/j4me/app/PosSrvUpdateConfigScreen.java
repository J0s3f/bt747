package net.sf.bt747.j4me.app;

import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.CheckBox;
import org.j4me.ui.components.TextBox;

import bt747.model.AppSettings;
import bt747.sys.JavaLibBridge;

public class PosSrvUpdateConfigScreen extends BT747Dialog {

	private boolean screenSetup = false;

	/**
	 * Checkbox to switch on / off the position service feature.
	 */
	private CheckBox cbAutoStart;

	/**
	 * Textbox for the period between updates, for example 60 seconds.
	 */
	private TextBox tbPeriod;

	/**
	 * Create an instance of this Screen.
	 * 
	 * @param c
	 *            Controller The applications's Controller
	 * @param previous
	 *            The previously show screen to which we will return when this
	 *            screen is closed.
	 */
	public PosSrvUpdateConfigScreen(final AppController c,
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

			cbAutoStart = new CheckBox();
			cbAutoStart.setLabel("Updating active");
			append(cbAutoStart);

			tbPeriod = new TextBox();
			tbPeriod.setForNumericOnly();
			tbPeriod.setLabel("Update period");
			append(tbPeriod);

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
		Log.debug("PosSrvUpdateConfigScreen: showNotify");
		setupScreen();
		super.showNotify();
	}

	/**
	 * Called when the user presses the "Ok" button.
	 * 
	 * @see org.j4me.ui.DeviceScreen#acceptNotify()
	 */
	protected final void acceptNotify() {
		Log.debug("PosSrvUpdateConfigScreen: acceptNotify");
		setSettings();
		if (cbAutoStart.isChecked()) {
			c.startGPSPositionServing();
		} else {
			c.stopGPSPositionServing();
		}
		// Continue processing the event.
		previous.show();
		super.acceptNotify();
	}

	/**
	 * Called when the user presses the "Back" button.
	 * 
	 * @see org.j4me.ui.DeviceScreen#declineNotify()
	 */
	protected final void declineNotify() {
		Log.debug("PosSrvUpdateConfigScreen: declineNotify");
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
		tbPeriod.setString(JavaLibBridge.toString(getModel().getIntOpt(
				AppSettings.POS_SRV_PERIOD)));
		cbAutoStart.setChecked(getModel().getBooleanOpt(
				AppSettings.POS_SRV_AUTOSTART));
		repaint();
		Log.debug("PosSrvUpdateConfigScreen: settings retrieved");
	}

	/**
	 * Persist the settings.
	 */
	private final void setSettings() {
		c.setIntOpt(AppSettings.POS_SRV_PERIOD, JavaLibBridge.toInt(tbPeriod
				.getString()));
		c.setBooleanOpt(AppSettings.POS_SRV_AUTOSTART, cbAutoStart.isChecked());
		Log.debug("PosSrvUpdateConfigScreen: settings updated");
	}

}
