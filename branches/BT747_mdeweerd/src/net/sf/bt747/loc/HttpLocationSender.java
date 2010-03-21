/**
 * This class is a subclass of LocationSender and implements the
 * functionality specific to senddding data to external http servers.<br>
 * The parameter names for the parameters altitude, speed, heading, longitude,
 * latitude have been adapted to the myLieu project (c't computer magazine,
 * heise Verlag, Germany.)
 * 
 * @author Florian Unger
 */
package net.sf.bt747.loc;

import bt747.model.Model;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747HttpSender;

public class HttpLocationSender extends LocationSender {

	/**
	 * Parameter name used for the latitude value in the request to an http
	 * server.
	 */
	private static final String PAR_NAME_LATITUDE = "latitude";

	/**
	 * Parameter name used for the longitude value in the request to an http
	 * server.
	 */
	private static final String PAR_NAME_LONGITUDE = "longitude";

        /**
         * Parameter used for position : lat and lon seperated by comma.
         */
        private static final String PAR_NAME_POSITION = "position";
	
	
	/**
	 * Parameter name used for the speed value in the request to an http server.
	 */
	private static final String PAR_NAME_SPEED = "speed";

	/**
	 * Parameter name used for the heading value in the request to an http
	 * server.
	 */
	private static final String PAR_NAME_DIR = "dir";

	/**
	 * Parameter name used for the altitude value in the request to an http
	 * server.
	 */
	private static final String PAR_NAME_ALTITUDE = "alt";

	/**
	 * Parameter name used for the hdop value in the request to an http server.
	 */
	private static final String PAR_NAME_HDOP = "hdop";

	/**
	 * Parameter name used for the nsat value in the request to an http server.
	 */
	private static final String PAR_NAME_NSAT = "numsat";

	/**
	 * Parameter name of the http request parameter for the bliuetooth address.
	 */
	private static final String PAR_NAME_BT_ADDR = "btaddr";

	/**
         * Parameter name of the http request parameter for the username.
         */
        private static final String PAR_NAME_USER = "user";

	/**
	 * The hostname of the target server for the http connections. Default value
	 * is a failsafe "localhost".
	 */
	private String targetHostname = "localhost";

	/**
	 * Port to use when connecting to the http server. Default is 80.
	 */
	private int targetPort = 80;

	/**
	 * Target file in the URL. Default is empty.
	 */
	private String targetFile = "";

	/**
	 * Username for authentication.
	 */
	private String user = null;

	/**
	 * Password for authentication.
	 */
	private String password = null;

	/**
	 * Create and answer a new instance of HttpLocationSender.
	 * 
	 * @param m
	 *            Model The Model that this instance will belong to
	 */
	public HttpLocationSender(Model m) {
		super(m);
	}

	/**
	 * Actually Send out data to the external server. All location data,
	 * authentication data, etc. are prepared into a Hashtable. The sending is
	 * implementd using a platform specific class (J2ME or J2SE) because the
	 * implementation of Http protocol needs to be different.<br>
	 * This is the concrete implementation of the abstract method in the
	 * superclass.
	 * @throws BT747Exception 
	 * 
	 * @see net.sf.bt747.loc.LocationSender#sendOutData()
	 */
	protected void sendOutData() throws BT747Exception {
		final BT747Hashtable data = prepareDataToSend();
		final BT747HttpSender hsi = JavaLibBridge.getHttpSenderInstance();
		hsi.doRequest(targetHostname, targetPort, targetFile, user, password,
				data, null, this);
	}

	/**
	 * @return a BT747Hashtable with all the GPS data fields added as key value
	 *         pairs.
	 */
	private BT747Hashtable prepareDataToSend() {
		BT747Hashtable data = JavaLibBridge.getHashtableInstance(6);
		data.put(PAR_NAME_DIR, JavaLibBridge.toString(this.heading,0));
		data.put(PAR_NAME_LATITUDE, JavaLibBridge.toString(this.latitude,5));
		data.put(PAR_NAME_LONGITUDE, JavaLibBridge.toString(this.longitude,5));
                data.put(PAR_NAME_POSITION, JavaLibBridge.toString(this.longitude,5)+","+JavaLibBridge.toString(this.latitude,5));
		data.put(PAR_NAME_SPEED, JavaLibBridge.toString(this.speed,1));
		data.put(PAR_NAME_HDOP, JavaLibBridge.toString(this.hdop,2));
		data.put(PAR_NAME_ALTITUDE, JavaLibBridge.toString(this.altitude,1));
		// TODO correct extraction of data from this nsat field
		data.put(PAR_NAME_NSAT, JavaLibBridge.toString(this.nsat));
		data.put(PAR_NAME_BT_ADDR, this.bluetoothAdress);
                data.put(PAR_NAME_USER, this.user);
		return data;
	}

	/**
	 * Retrieve the target hostname.
	 * 
	 * @return String the targt hostname
	 */
	public String getTargetHostname() {
		return targetHostname;
	}

	/**
	 * Set the target hostname.
	 * 
	 * @param targetHostname
	 *            String The target hostname
	 */
	public void setTargetHostname(String targetHostname) {
		this.targetHostname = targetHostname;
	}

	/**
	 * Retrieve the target port.
	 * 
	 * @return int The port
	 */
	public int getTargetPort() {
		return targetPort;
	}

	/**
	 * Set the port.
	 * 
	 * @param targetPort
	 *            int the Port to set.
	 */
	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	/**
	 * Retrieve the part of the URL after the port.
	 * 
	 * @return String the file
	 */
	public String getTargetFile() {
		return targetFile;
	}

	/**
	 * Set the part of the URL after the port.
	 * 
	 * @param targetFile
	 *            String the file
	 */
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	/**
	 * Retrieve the username.
	 * 
	 * @return the username
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the username.
	 * 
	 * @param user
	 *            the Username
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Retrieve the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Retrieve the password.
	 * 
	 * @param password
	 *            the password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
