package net.sf.bt747.j4me.app;

import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Menu;
import org.j4me.ui.components.Label;

import bt747.model.AppSettings;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;

public class PosSrvMonitorScreen extends BT747Dialog implements ModelListener {

	private static final String VISUALIZATION_SUCCESS_CHAR = ".";

	private static final String VISUALIZATION_FAIL_CHAR = "!";

	private static final int MAX_CHARS_FOR_VISUALZATION = 20;

	private boolean screenSetup = false;

	private PosSrvAuthConfigScreen authConfigScreen = null;
	private PosSrvServerConfigScreen serverConfigScreen = null;
	private PosSrvUpdateConfigScreen updateConfigScreen = null;

	/**
	 * This visualizes the position service activity.
	 */
	private Label lbPositionServerUpdates = new Label("");

	/**
	 * Display whether Sending is active and some settings.
	 */
	private Label lbHeader = new Label("");

	/**
	 * Create an instance of this Screen.
	 * 
	 * @param c
	 *            Controller The applications's Controller
	 * @param previous
	 *            The previously show screen to which we will return when this
	 *            screen is closed.
	 */
	public PosSrvMonitorScreen(final AppController c,
			final DeviceScreen previous) {
		this.c = c;
		c.getModel().addListener(this);
		this.previous = previous;
		setTitle("Position Server");
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

			setTitle("Position Server");
			// Set the menu bar options.
			setMenuText("Back", "Menu");

			setHeadingText();
			append(lbHeader);
			append(lbPositionServerUpdates);

			invalidate();

			authConfigScreen = new PosSrvAuthConfigScreen(c, this);
			serverConfigScreen = new PosSrvServerConfigScreen(c, this);
			updateConfigScreen = new PosSrvUpdateConfigScreen(c, this);
		}
	}

	private void setHeadingText() {
		if (getModel().getBooleanOpt(AppSettings.POS_SRV_AUTOSTART)) {
			String period = JavaLibBridge.toString(getModel().getIntOpt(
					AppSettings.POS_SRV_PERIOD));
			lbHeader.setLabel("Sending active every " + period + " seconds.");
		} else {
			lbHeader.setLabel("Sending inactive.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.j4me.ui.DeviceScreen#showNotify()
	 */
	public void showNotify() {
		Log.debug("PosSrvConfigScreen: showNotify");
		setupScreen();
		setHeadingText();
		invalidate();
		super.showNotify();
	}

	/**
	 * Called when the user presses the "Menu" button.
	 * 
	 * @see org.j4me.ui.DeviceScreen#acceptNotify()
	 */
	protected final void acceptNotify() {
		// logScreen.show();

		final Menu menu = new Menu("Configuration", this) {
			public final void showNotify() {
				super.showNotify();
			}
		};
		menu.appendMenuOption("General", updateConfigScreen);
		menu.appendMenuOption("Server", serverConfigScreen);
		menu.appendMenuOption("Authentication", authConfigScreen);
		menu.show();
	}

	/**
	 * Called when the user presses the "Back" button.
	 * 
	 * @see org.j4me.ui.DeviceScreen#declineNotify()
	 */
	protected final void declineNotify() {
		Log.debug("PosSrvConfigScreen: declineNotify");
		// Go back to the previous screen.
		if (previous != null) {
			previous.show();
			super.declineNotify();
		}
	}

	/**
	 * Handle the event coming from the GPS model.
	 * 
	 * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
	 */
	public final void modelEvent(final ModelEvent e) {
		final int type = e.getType();
		String c = "";
		switch (type) {
		case ModelEvent.POS_SRV_FAILURE:
			c = lbPositionServerUpdates.getLabel();
			if (c.length() > MAX_CHARS_FOR_VISUALZATION) {
				c = "";
			}
			c += VISUALIZATION_FAIL_CHAR;
			lbPositionServerUpdates.setLabel(c);
			break;
		case ModelEvent.POS_SRV_SUCCESS:
			c = lbPositionServerUpdates.getLabel();
			if (c.length() > MAX_CHARS_FOR_VISUALZATION) {
				c = "";
			}
			c += VISUALIZATION_SUCCESS_CHAR;
			lbPositionServerUpdates.setLabel(c);
			break;
		default:
			break;
		}

	}
}
