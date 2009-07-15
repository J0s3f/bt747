package bt747.j2se_view;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPasswordField;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;

public class LocationServerPanel extends javax.swing.JPanel implements
		bt747.model.ModelListener {

	private static final long serialVersionUID = 1L;
	// TODO generate real serial

	private Model m;
	private J2SEAppController c;

	public LocationServerPanel() {
		initComponents();
	}

	/**
	 * Called by BT747Main upon completion of the application startup.
	 * 
	 * @param pC
	 *            the application controller
	 */
	public void init(final J2SEAppController pC) {
		c = pC;
		m = c.getModel();
		m.addListener(this);
		initAppData();
	}

	/**
	 * Initialize the data this concrete Panel is working with and which is not
	 * yet setup by BT747Main.
	 */
	private void initAppData() {
		tfHostname.setText(m.getStringOpt(AppSettings.POS_SRV_HOSTNAME));
		tfPort.setText("" + m.getIntOpt(AppSettings.POS_SRV_PORT));
		tfFile.setText(m.getStringOpt(AppSettings.POS_SRV_FILE));
		tfPeriod.setText("" + m.getIntOpt(AppSettings.POS_SRV_PERIOD));
		tfUsername.setText(m.getStringOpt(AppSettings.POS_SRV_USER));
		tfPassword.setText(m.getStringOpt(AppSettings.POS_SRV_PASS));
		cbServeOnConnect.setSelected(m
				.getBooleanOpt(AppSettings.POS_SRV_AUTOSTART));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
	 */
	public void modelEvent(ModelEvent e) {
		final int type = e.getType();
		switch (type) {
		case ModelEvent.CONNECTED:
		case ModelEvent.DISCONNECTED:
			break;
		case ModelEvent.POS_SRV_FAILURE:
			lbVisualizeServing.setText(lbVisualizeServing.getText() + "!");
			break;
		case ModelEvent.POS_SRV_SUCCESS:
			lbVisualizeServing.setText(lbVisualizeServing.getText() + ".");
			break;
		}
	}

	private javax.swing.JTextField tfHostname = null;
	private javax.swing.JLabel lbHostname = null;
	private javax.swing.JTextField tfPort = null;
	private javax.swing.JLabel lbPort = null;
	private javax.swing.JTextField tfFile = null;
	private javax.swing.JLabel lbFile = null;
	private javax.swing.JTextField tfPeriod = null;
	private javax.swing.JLabel lbPeriod = null;
	private javax.swing.JTextField tfUsername = null;
	private javax.swing.JLabel lbUsername = null;
	private javax.swing.JTextField tfPassword = null;
	private javax.swing.JLabel lbPassword = null;
	private javax.swing.JCheckBox cbServeOnConnect = null;
	private javax.swing.JLabel lbServeOnConnect = null;
	private javax.swing.JLabel lbVisualizeServing = null;

	/**
	 * Actually setup the GUI.
	 */
	private void initComponents() {
		tfHostname = new javax.swing.JTextField();
		lbHostname = new javax.swing.JLabel();
		tfPort = new javax.swing.JTextField();
		lbPort = new javax.swing.JLabel();
		tfFile = new javax.swing.JTextField();
		lbFile = new javax.swing.JLabel();
		tfPeriod = new javax.swing.JTextField();
		lbPeriod = new javax.swing.JLabel();
		tfUsername = new javax.swing.JTextField();
		lbUsername = new javax.swing.JLabel();
		tfPassword = new javax.swing.JPasswordField();
		lbPassword = new javax.swing.JLabel();
		cbServeOnConnect = new javax.swing.JCheckBox();
		lbServeOnConnect = new javax.swing.JLabel();
		lbVisualizeServing = new javax.swing.JLabel();

		setName("LocationServerPanel"); // NOI18N

		lbHostname.setText("Hostname");
		tfHostname.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				tfHostnameFocusLost(evt);
			}
		});

		lbPort.setText("Port");
		tfPort.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				tfPortFocusLost(evt);
			}
		});

		lbFile.setText("URL");
		tfFile.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				tfFileFocusLost(evt);
			}
		});

		lbPeriod.setText("sec betw. updates");
		tfPeriod.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				tfPeriodFocusLost(evt);
			}
		});

		lbUsername.setText("Username");
		tfUsername.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				tfUsernameFocusLost(evt);
			}
		});

		lbPassword.setText("Password");
		tfPassword.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				tfPasswordFocusLost(evt);
			}
		});

		lbServeOnConnect.setText("Activate serving locations");
		cbServeOnConnect.setText(""); // NOI18N
		cbServeOnConnect
				.setToolTipText("Activate serving locations (automatically when GPS is connected)"); // NOI18N
		cbServeOnConnect.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent evt) {
				cbServeOnConnectChange();
			}
		});
		cbServeOnConnect.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				cbServeOnConnectChange();
			}
		});
		cbServeOnConnect.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				cbServeOnConnectChange();
			}
		});

		lbVisualizeServing.setText("");

		this.setLayout(null);

		this.add(lbPort);
		this.add(tfPort);
		this.add(lbHostname);
		this.add(tfHostname);
		this.add(lbServeOnConnect);
		this.add(cbServeOnConnect);
		this.add(lbFile);
		this.add(tfFile);
		this.add(cbServeOnConnect);
		this.add(lbPeriod);
		this.add(tfPeriod);
		this.add(tfUsername);
		this.add(lbUsername);
		this.add(tfPassword);
		this.add(lbPassword);
		this.add(lbVisualizeServing);

		Insets insets = this.getInsets();
		lbHostname.setBounds(5 + insets.left, 30 + insets.top, 100, 20);
		lbPort.setBounds(5 + insets.left, 55 + insets.top, 100, 20);
		lbFile.setBounds(5 + insets.left, 80 + insets.top, 100, 20);
		lbPeriod.setBounds(5 + insets.left, 105 + insets.top, 100, 20);
		lbUsername.setBounds(5 + insets.left, 130 + insets.top, 100, 20);
		lbPassword.setBounds(5 + insets.left, 155 + insets.top, 100, 20);

		tfHostname.setBounds(110 + insets.left, 30 + insets.top, 200, 20);
		tfPort.setBounds(110 + insets.left, 55 + insets.top, 200, 20);
		tfFile.setBounds(110 + insets.left, 80 + insets.top, 200, 20);
		tfPeriod.setBounds(110 + insets.left, 105 + insets.top, 200, 20);
		tfUsername.setBounds(110 + insets.left, 130 + insets.top, 200, 20);
		tfPassword.setBounds(110 + insets.left, 155 + insets.top, 200, 20);

		cbServeOnConnect.setBounds(5 + insets.left, 180 + insets.top, 20, 20);
		lbServeOnConnect.setBounds(25 + insets.left, 180 + insets.top, 300, 20);

		lbVisualizeServing
				.setBounds(5 + insets.left, 230 + insets.top, 300, 20);

	}

	protected void tfFileFocusLost(FocusEvent evt) {
		if (!m.getStringOpt(AppSettings.POS_SRV_FILE).equals(tfFile.getText())) {
			c.setStringOpt(AppSettings.POS_SRV_FILE, tfFile.getText());
			this.locationServingParametersChanged();
		}
	}

	protected void tfPortFocusLost(FocusEvent evt) {
		int p = 80; // default for parse problem
		try {
			p = Integer.parseInt(tfPort.getText());
		} catch (NumberFormatException nfe) {
			// User has entered somethin weird
			// TODO "real" error handling
			tfPort.setText("80");
		}
		if (m.getIntOpt(AppSettings.POS_SRV_PORT) != p) {
			c.setIntOpt(AppSettings.POS_SRV_PORT, p);
			this.locationServingParametersChanged();
		}
	}

	protected void tfPeriodFocusLost(FocusEvent evt) {
		int p = 300; // seconds, default for parse problem
		try {
			p = Integer.parseInt(tfPeriod.getText());
		} catch (NumberFormatException nfe) {
			// User has entered somethin weird
			// TODO "real" error handling
			tfPeriod.setText("300");
		}
		if (m.getIntOpt(AppSettings.POS_SRV_PERIOD) != p) {
			c.setIntOpt(AppSettings.POS_SRV_PERIOD, p);
			this.locationServingParametersChanged();
		}
	}

	protected void tfHostnameFocusLost(FocusEvent evt) {
		if (!m.getStringOpt(AppSettings.POS_SRV_HOSTNAME).equals(
				tfHostname.getText())) {
			c.setStringOpt(AppSettings.POS_SRV_HOSTNAME, tfHostname.getText());
			this.locationServingParametersChanged();
		}
	}

	protected void tfPasswordFocusLost(FocusEvent evt) {
		if (!m.getStringOpt(AppSettings.POS_SRV_PASS).equals(
				tfPassword.getText())) {
			c.setStringOpt(AppSettings.POS_SRV_PASS, tfPassword.getText());
			this.locationServingParametersChanged();
		}
	}

	protected void tfUsernameFocusLost(FocusEvent evt) {
		if (!m.getStringOpt(AppSettings.POS_SRV_USER).equals(
				tfUsername.getText())) {
			c.setStringOpt(AppSettings.POS_SRV_USER, tfUsername.getText());
			this.locationServingParametersChanged();
		}
	}

	private void locationServingParametersChanged() {
		if (c.isLocationServingActive()) {
			c.setBooleanOpt(AppSettings.POS_SRV_AUTOSTART, false);
			cbServeOnConnect.setSelected(false);
			c.stopGPSPositionServing();
		}
	}

	private void cbServeOnConnectChange() {
		if (m.getBooleanOpt(AppSettings.POS_SRV_AUTOSTART) != cbServeOnConnect
				.isSelected()) {
			c.setBooleanOpt(AppSettings.POS_SRV_AUTOSTART, cbServeOnConnect
					.isSelected());
		}
		if (cbServeOnConnect.isSelected()) {
			// Checkbox is set.
			lbVisualizeServing.setText("");
			c.startGPSPositionServing();
		} else {
			// Checkbox is not set
			c.stopGPSPositionServing();
		}
	}

}
