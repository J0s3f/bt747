/**
 * This class is the J2ME specific implementation of the BT747HttpSender interface.<br>
 * Im comparison to a J2SE implementation there are several limitations:<br>
 * Parameter encodingOrNull is not used because there is no real URL
 * encoding done anyway. If the data to send has blanks or other characters
 * not allowed in URLs then this class will not work.<br>
 * Erros are not identified not as detailed as in the J2SE implementation.
 * notifyFatalFailure() is not used because the difference between fatal
 * errors and normal connection errors are not identified.
 */

package net.sf.bt747.j2me.system;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747HttpSender;
import fun.LocationSender;

public class J2MEHttpSenderImpl implements BT747HttpSender {
	/**
	 * We always use the http protocoll.
	 */
	private static final String HTTP_PROTOCOL = "http";

	/*
	 * (non-Javadoc)
	 * 
	 * @see bt747.sys.interfaces.BT747HttpSender#doRequest(java.lang.String,
	 * int, java.lang.String, java.lang.String, java.lang.String,
	 * bt747.sys.interfaces.BT747Hashtable, java.lang.String,
	 * fun.LocationSender)
	 */
	public void doRequest(final String hostname, final int port,
			final String file, final String user, final String password,
			final BT747Hashtable data, String encodingOrNull,
			final LocationSender caller) {
		Thread t = new Thread() {
			public void run() {
				doRequestAsynchronously(hostname, port, file, user, password,
						data, caller);
				super.run();
			}
		};
		t.run();
	}

	/**
	 * Do the request asynchronously.
	 * 
	 * @param hostname
	 *            the server to conect to
	 * @param port
	 *            the port
	 * @param file
	 *            the rest of the URL
	 * @param user
	 *            a username for http authentication, may be empty or null
	 * @param password
	 *            a password for http authentication, only used when user is
	 *            set. may be empty or null
	 * @param data
	 *            the data to send as BT747Hashtable
	 * @param caller
	 *            the calling KocationSender instance. This object will get the
	 *            notifications about success or failure of the request.
	 */
	private void doRequestAsynchronously(final String hostname, final int port,
			final String file, final String user, final String password,
			final BT747Hashtable data, final LocationSender caller) {

		HttpConnection conn = null;
		InputStream inputStream = null;
		StringBuffer b = new StringBuffer();

		String encodedData = encodeRequestData(data);

		String url = createURL(hostname, port, file, encodedData);

		try {
			// create a socket and send the url as stream-data away
			conn = (HttpConnection) Connector.open(url);
			conn.setRequestMethod(HttpConnection.GET);
			conn.setRequestProperty("Connection", "close");

			String encodedUserAndPass = BasicAuth.encode(user, password);
			conn.setRequestProperty("Authorization", "Basic "
					+ encodedUserAndPass);

			inputStream = conn.openInputStream();

			// read the anser
			int ch = 0;
			while ((ch = inputStream.read()) != -1) {
				b.append((char) ch);
			}
			caller.notifySuccess();

		} catch (IOException ioe) {
			caller
					.notifyConnectionFailure("Could not connect to target server.");
		}

		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (IOException ex1) {
					// ignore
				}
			}
			if (inputStream != null) {
				try {

					inputStream.close();
				} catch (IOException ex1) {
					// ignore
				}
			}
		}
	}

	/**
	 * Create an URL object from the data provided.
	 * 
	 * @param hostname
	 *            the hostname
	 * @param port
	 *            a port like 80
	 * @param file
	 *            the rest of the URL, may be empty, must not be null
	 * @param encodedData
	 *            the data as URL-encoded String
	 * @return a String containing the URL
	 */
	private String createURL(String hostname, int port, String file,
			String encodedData) {
		if ((file.length() > 0) && (file.charAt(0) != '/')) {
			file = "/" + file;
		}
		String url = HTTP_PROTOCOL + "://" + hostname + ":" + port + file + "?"
				+ encodedData;
		return url.trim();
	}

	/**
	 * Encodes the request parameters into a String representation.
	 * 
	 * @param data
	 *            a BT747Hashtable containing the key value pairs for the
	 *            parameters.
	 * @return the request as String representation
	 */
	private String encodeRequestData(BT747Hashtable data) throws BT747Exception {
		String encodedData = "";
		BT747Hashtable it = data.iterator();
		while (it.hasNext()) {

			if (encodedData.length() > 0) {
				// append a parameter
				encodedData += "&";
			}
			String key = (String) it.nextKey();
			encodedData += key;
			encodedData += "=";
			String value = (String) data.get(key);
			encodedData += value;
		}
		return encodedData;
	}

	/**
	 * This class encodes a user name and password in the format (base 64) that
	 * HTTP Basic Authorization requires. Copyright (c) 2000-2001 Sun
	 * Microsystems, Inc. All Rights Reserved.
	 */
	private static class BasicAuth {
		// make sure no one can instantiate this class
		private BasicAuth() {
		}

		// conversion table
		private static byte[] cvtTable = { (byte) 'A', (byte) 'B', (byte) 'C',
				(byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H',
				(byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M',
				(byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R',
				(byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W',
				(byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b',
				(byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
				(byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
				(byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q',
				(byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v',
				(byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0',
				(byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
				(byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+',
				(byte) '/' };

		/**
		 * Encode a name/password pair appropriate to use in an HTTP header for
		 * Basic Authentication. name the user's name passwd the user's password
		 * returns String the base64 encoded name:password
		 */
		static String encode(String name, String passwd) {
			byte input[] = (name + ":" + passwd).getBytes();
			byte[] output = new byte[((input.length / 3) + 1) * 4];
			int ridx = 0;
			int chunk = 0;

			/**
			 * Loop through input with 3-byte stride. For each 'chunk' of
			 * 3-bytes, create a 24-bit value, then extract four 6-bit indices.
			 * Use these indices to extract the base-64 encoding for this 6-bit
			 * 'character'
			 */
			for (int i = 0; i < input.length; i += 3) {
				int left = input.length - i;

				// have at least three bytes of data left
				if (left > 2) {
					chunk = (input[i] << 16) | (input[i + 1] << 8)
							| input[i + 2];
					output[ridx++] = cvtTable[(chunk & 0xFC0000) >> 18];
					output[ridx++] = cvtTable[(chunk & 0x3F000) >> 12];
					output[ridx++] = cvtTable[(chunk & 0xFC0) >> 6];
					output[ridx++] = cvtTable[(chunk & 0x3F)];
				} else if (left == 2) {
					// down to 2 bytes. pad with 1 '='
					chunk = (input[i] << 16) | (input[i + 1] << 8);
					output[ridx++] = cvtTable[(chunk & 0xFC0000) >> 18];
					output[ridx++] = cvtTable[(chunk & 0x3F000) >> 12];
					output[ridx++] = cvtTable[(chunk & 0xFC0) >> 6];
					output[ridx++] = '=';
				} else {
					// down to 1 byte. pad with 2 '='
					chunk = input[i] << 16;
					output[ridx++] = cvtTable[(chunk & 0xFC0000) >> 18];
					output[ridx++] = cvtTable[(chunk & 0x3F000) >> 12];
					output[ridx++] = '=';
					output[ridx++] = '=';
				}
			}
			return new String(output);
		}
	}

}
