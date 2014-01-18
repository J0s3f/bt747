package net.sf.bt747.j2se.app.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.*;

// Requires JavaMail - http://www.oracle.com/technetwork/java/javamail/index-138643.html
// Require JAF on systems before Java 1.6 - http://www.oracle.com/technetwork/java/javase/downloads/index-135046.html
// Reference dsn.jar, imap.jar, mailapi.jar, pop3.jar, smtp.jar
// For SSL see http://www.oracle.com/technetwork/java/javaee/sslnotes-220866.txt .
// Also see http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/

public class Email {

	public static void SendEmail() throws AddressException, MessagingException {
		String host = "smtp.gmail.com";
		String from = "username";
		String pass = "password";
		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true"); // added this line
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		String[] to = { "to@gmail.com" }; // added this line

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));

		InternetAddress[] toAddress = new InternetAddress[to.length];

		// To get the array of addresses
		for (int i = 0; i < to.length; i++) { // changed from a while loop
			toAddress[i] = new InternetAddress(to[i]);
		}
		System.out.println(Message.RecipientType.TO);

		for (int i = 0; i < toAddress.length; i++) { // changed from a while
			// loop
			message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		}
		message.setSubject("sending in a group");
		message.setText("Welcome to JavaMail");
		Transport transport = session.getTransport("smtp");
		transport.connect(host, from, pass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	/**
	 * DummySSLSocketFactory
	 */
	public static class DummySSLSocketFactory extends SSLSocketFactory {
		private SSLSocketFactory factory;

		public DummySSLSocketFactory() {
			try {
				SSLContext sslcontext = SSLContext.getInstance("TLS");
				sslcontext.init(null,
						new TrustManager[] { new DummyTrustManager() }, null);
				factory = (SSLSocketFactory) sslcontext.getSocketFactory();
			} catch (Exception ex) {
				// ignore
			}
		}

		public static SocketFactory getDefault() {
			return new DummySSLSocketFactory();
		}

		public Socket createSocket() throws IOException {
			return factory.createSocket();
		}

		public Socket createSocket(Socket socket, String s, int i, boolean flag)
				throws IOException {
			return factory.createSocket(socket, s, i, flag);
		}

		public Socket createSocket(InetAddress inaddr, int i,
				InetAddress inaddr1, int j) throws IOException {
			return factory.createSocket(inaddr, i, inaddr1, j);
		}

		public Socket createSocket(InetAddress inaddr, int i)
				throws IOException {
			return factory.createSocket(inaddr, i);
		}

		public Socket createSocket(String s, int i, InetAddress inaddr, int j)
				throws IOException {
			return factory.createSocket(s, i, inaddr, j);
		}

		public Socket createSocket(String s, int i) throws IOException {
			return factory.createSocket(s, i);
		}

		public String[] getDefaultCipherSuites() {
			return factory.getDefaultCipherSuites();
		}

		public String[] getSupportedCipherSuites() {
			return factory.getSupportedCipherSuites();
		}
	}

	/**
	 * DummyTrustManager - NOT SECURE
	 */
	public static class DummyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] cert, String authType) {
			// everything is trusted
		}

		public void checkServerTrusted(X509Certificate[] cert, String authType) {
			// everything is trusted
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

}
