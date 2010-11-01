/**
 * This class implements a service to send location data to an external
 * server. The class is not specific in terms of which technology (http,
 * https, SMS, etc.) will be used to connect to the other server. This
 * class is abstract and these technology specific aspects are
 * implemented by subclasses.<br>
 * Instances keep several location data fields like latitude, longitude,
 * heading, etc. These fields are updated with every GPS event that is
 * sent to those instances. The class also has a field for the bluetooth
 * address of the GPS device which will also be sent to external
 * servers.<br>
 * Implementation note: While it is perfectly possible to create more
 * than one instance in this case it may be worth changing the
 * implementation so that the saved location data is common between
 * these ins (make the fields static).<br>
 * This class features an update period which can be set using an
 * accessor. Updates to the external server will be done with that
 * frequency. For example the update period can be set to 60 * 1000
 * milliseconds resulting in updates to the external server every 60
 * seconds.<br>
 * It is strongly recommended to keep the update frequency low enough
 * to not slow down the application on devices with limited computing
 * capability. The default is set to 60 * 60 * 1000 resulting in one
 * update per hour.<br>
 * Implementation note: While instances of this class will get every
 * GPS event the updates to the external server are decoupled from this.
 * Also updates to external server are done asynchronously to keep the
 * event mechanism fast. This must be implemented by subclasses or their
 * service classes.<br>
 * Instance of this class must be created for a specific Model. Whenever
 * an update to an external server occurs one out of several possible
 * results occur (success, failure, fatal failure). Any of these events
 * is propagated to the Model so it can be used for visualization of the
 * results to the user. Along with the results instances keep a result
 * code and message for the last faiilure that occured. This can be used
 * to allow a user to understand which corrections he needs to make.
 * Instances are not automatically registered as ModelListener at this
 * Model when created. This must be done externally. Therefore it is
 * possible to have an instance of this class and switch on or off the
 * external updates by registering and deregistering the class.<br>
 * 
 * @author Florian Unger
 */
package net.sf.bt747.loc;

import gps.GpsEvent;
import gps.log.GPSRecord;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Exception;

public abstract class LocationSender implements ModelListener {

	/**
	 * The default update period (in milliseconds), default is 60 minutes.
	 */
	private static final int DEFAULT_UPDATE_PERIOD = 60 * 60 * 1000;

	/**
	 * The update period of this instance. Default value is 60 minutes between
	 * updates.
	 */
	protected int updatePeriod = DEFAULT_UPDATE_PERIOD;

	Model model = null;

	/**
	 * Timestamp (milliseconds) of system time when the next update should
	 * occur.
	 */
	protected int next_update_time = 0;

	/**
	 * Last latitude value obtained from a GPS device.
	 */
	protected double latitude;

	/**
	 * Last longitude value obtained from a GPS device.
	 */
	protected double longitude;

	/**
	 * Last speed value obtained from a GPS device.
	 */
	protected float speed;

	/**
	 * Last heading value obtained from a GPS device.
	 */
	protected float heading;

	/**
	 * Last altiitude value obtained from a GPS device.
	 */
	protected float altitude;
	/**
	 * Last hdop value obtained from a GPS device.
	 */
	protected int hdop;

	/**
	 * Last nsat value obtained from a GPS device.
	 */
	protected int nsat;

	/**
	 * The (unique) bluetooth address of the GPS device. May be empty.
	 */
	protected String bluetoothAdress = "";

	/**
	 * A message about the last error that occured during sending location data.
	 */
	private String lastError = "";

	/**
	 * Indicates whether or not the sending is disabled due to errors.
	 */
	private boolean disabled = false;

	/**
	 * Create and answer a new instance of LocationSender.
	 * 
	 * @param m
	 *            Model The Model that this instance will belong to
	 */
	public LocationSender(Model m) {
		super();
		model = m;
	}

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public void modelEvent(final ModelEvent e) {
        if ((extractValuesFromEvent(e)) && (checkUpdateFrequency())) {
            try {
                sendOutData();
            } catch (BT747Exception e1) {
                Generic.debug("Location sending", e1);
            }
        }
    }

	/**
	 * Evaluate a ModelEvent and check for events of type GpsEvent.GPRMC or
	 * GpsEvent.GPGGA and if it is of one of these types extract data and
	 * remember that data.
	 * 
	 * @param e
	 *            the ModelEvent
	 * @return true if the event was of type GpsEvent.GPRMC or GpsEvent.GPGGA
	 */
	protected boolean extractValuesFromEvent(final ModelEvent e) {
		final int type = e.getType();
		switch (type) {
		case GpsEvent.GPRMC:
		case GpsEvent.GPGGA:
			final GPSRecord g = (GPSRecord) e.getArg();
			this.latitude = g.latitude;
			this.longitude = g.longitude;
			switch (type) {
			case GpsEvent.GPRMC:
				this.speed = g.speed;
				this.heading = g.heading;
				this.altitude = g.height;
				break;
			case GpsEvent.GPGGA:
				this.hdop = g.hdop / 100;
				this.nsat = g.nsat / 256;
			default:
				break;
			}
		}
		return ((type == GpsEvent.GPRMC) || (type == GpsEvent.GPGGA));
	}

	/**
	 * @return true if enough time has elapsed to generate another update, false
	 *         otherwise
	 */
	protected boolean checkUpdateFrequency() {
		int ts = JavaLibBridge.getTimeStamp();
		if (ts >= this.next_update_time) {
			this.next_update_time = ts + this.updatePeriod;
			return true;
		}
		return false;
	}

	/**
	 * Actually do the sending of the data. Subclasses will override with
	 * specific technology.
	 * @throws BT747Exception 
	 */
	protected abstract void sendOutData() throws BT747Exception;

	public int getUpdatePeriod() {
		return updatePeriod;
	}

	public void setUpdatePeriod(int updatePeriod) {
		this.updatePeriod = updatePeriod;
	}

	public String getBluetoothAdress() {
		return bluetoothAdress;
	}

	public void setBluetoothAdress(String addr) {
		this.bluetoothAdress = addr;
	}

	/**
	 * Check whether this instance is disabled due to fatal errors.
	 * 
	 * @return true if the instance is disabled.
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Set or reset the disabled flag of this instance.
	 * 
	 * @param disabled
	 *            boolean If true the the instance will be disabled, if false
	 *            the instance is not disabled.
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Chekc for the las occured error due to communication problems.
	 * 
	 * @return String The last reported error.
	 */
	public String getLastError() {
		return lastError;
	}

	/**
	 * Set the last error after a communication prpblem. This is exclusively
	 * done by the instance itself.
	 * 
	 * @param lastError
	 *            Strinfg The last error message to be set.
	 */
	private void setLastError(String lastError) {
		this.lastError = lastError;
	}

	/**
	 * Callback method to be used by a BT747HttpSender (or other similar service
	 * class) which is asynchronously doing its work.<br>
	 * This method is called when the BT747HttpSender or other similar service
	 * class reports a fatal failure which requires stopping the sending
	 * completely like when an URL cannot be prepared from the parameters. The
	 * instance will the set itself as being disabled. <br>
	 * 
	 * @param reason
	 *            String An error message
	 */
	public void notifyFatalFailure(String reason) {
		this.setDisabled(true);
		this.setLastError(reason);
		model.postEvent(new ModelEvent(ModelEvent.POS_SRV_FATAL_FAILURE, this));
	}

	/**
	 * Callback method to be used by a BT747HttpSender (or other similar service
	 * class) which is asynchronously doing its work.<br>
	 * This method is called when the BT747HttpSender or similar service class
	 * reports a failure accessing the target server which does not require
	 * stopping the sending process. A reason may be that the target server is
	 * not availabale. Repeated failures should stop the sending process also to
	 * save network bandwidth. This is not done however in this class but may be
	 * implemented externally.
	 * 
	 * @param reason
	 *            String An error message
	 */
	public void notifyConnectionFailure(String reason) {
		this.setLastError(reason);
		model.postEvent(new ModelEvent(ModelEvent.POS_SRV_FAILURE, this));
	}

	/**
	 * Callback method to be used by a BT747HttpSender (or other similar service
	 * class) which is asynchronously doing its work.<br>
	 * This method is called when the BT747HttpSender or similar service class
	 * reports a successful communication to the target server. Success is for
	 * example getting a positive http response code from the targt server after
	 * sending the data.<br>
	 * This information may be used to provide visual feedback of the
	 * communication to the user.
	 */
	public void notifySuccess() {
		model.postEvent(new ModelEvent(ModelEvent.POS_SRV_SUCCESS, this));
	}

}
