/**
 * This interface contains a service to send data via http to an
 * external sender. The interface is implementd by plattform specific
 * classes which implement the service for J2ME and J2SE environment.
 * Instance of these classes should be created using a Factory methood
 * in the JavaLibBridge class.
 * 
 * @author Florian Unger
 */

package bt747.sys.interfaces;

import fun.LocationSender;

public interface BT747HttpSender {

	/**
	 * Send out data to a server using http the protocol. The data is sent with
	 * a GET request. This method may only be called by an HttpLocationSender
	 * instance which is also used to send callbacks to. The implementing
	 * classes of this interface are obliged to implement the network access in
	 * an asynchronous way.
	 * 
	 * @param hostname
	 *            a hostname like "www.server.com"
	 * @param port
	 *            a port, typically 80 or 443 for protocols http or https resp.
	 * @param file
	 *            something trailing the server name like "cgi" in
	 *            "http://my.server.com:80/cgi"
	 * @param user
	 *            a username for authentication. There will be no authentication
	 *            if the username is empty or null.
	 * @param password
	 *            a password, only used when username ist not null and not
	 *            empty.
	 * @param data
	 *            a BT747Hashtable containing request parameters as key value
	 *            pairs. May be empty but must not be null.
	 * @param encodingOrNull
	 *            an encoding for the post parameters like "UTF-8" (which is
	 *            also the default)
	 * @param caller
	 *            An HttpLocationSender. The object that calls this method. This
	 *            is used for callbacks for the results.
	 */
	public void doRequest(String hostname, int port, String file, String user,
			String password, BT747Hashtable data, String encodingOrNull,
			LocationSender caller);

}
