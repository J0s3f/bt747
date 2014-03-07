/**
 * 
 */
package net.sf.bt747.j2se.app.agps;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.jibble.simpleftp.SimpleFTP;

import bt747.sys.I18N;
import bt747.sys.interfaces.BT747Exception;

/**
 * @author Mario
 * 
 */
public class J2SEAGPS {
    // Transystem download information:
    // ftp://tsi0001:xxxxxx@www.transystem.com.tw/
    // Files: MTK14.EPO and MTK7d.EPO

    private final static String TRANS_FTP_SITE = "http://www.bt747.org/";
    private final static String TRANS_AGPS_14d = TRANS_FTP_SITE + "MTK14.EPO";
    private final static String TRANS_AGPS_7d = TRANS_FTP_SITE + "MTK7d.EPO";

    private static int timeout = 3*60000;

    static {
        try {
            timeout = Integer.valueOf(java.lang.System.getProperty(
                    "agpsTimeOut", String.valueOf(timeout)));
        } catch (Throwable e) {
            // Do nothing.
        }
    }

    public static final byte[] getAGPS7d() throws BT747Exception {
        return getBytesFromUrl(TRANS_AGPS_7d);
    }

    public static final byte[] getBytesFromUrl(final String urlString)
            throws BT747Exception {
        byte[] result = null;
        try {
        	return J2SEAGPS.getAgpsUsingSimpleFtp(urlString);
        } catch(Exception e) {
        }
        try {
            if(result==null) {
                bt747.sys.Generic.debug("Trying to get AGPS using standard library");
                final URL url = new URL(urlString);
                System.setProperty("java.net.useSystemProxies", "true");
                System.setProperty("java.net.preferIPv4Stack", "true");
                
                bt747.sys.Generic.debug("Information - listing proxies");
                List<Proxy> pl = ProxySelector.getDefault().select(new URI(urlString));
                for (Proxy p : pl)
                    System.out.println(p);
                Proxy p = null;
                if (pl.size() > 0) //uses first one
                    p = pl.get(0);
                if(p.address()!=null) {
                	bt747.sys.Generic.debug(p.address().toString());
                }
                bt747.sys.Generic.debug("Done");

	            final URLConnection urlc = url.openConnection();
	            /*
					if(urlc instanceof FtpURLConnection) {
						FtpURLConnection ftpc=(FtpURLConnection)urlc;
					}
	           */
	            urlc.setConnectTimeout(timeout);
	            urlc.setReadTimeout(timeout);
	            final InputStream ins = urlc.getInputStream(); // To download
	            // OutputStream os = urlc.getOutputStream(); // To upload
	            final ByteArrayOutputStream bout = new ByteArrayOutputStream(
	                    120 * 1024);
	            final byte[] buf = new byte[1024];
	            while (true) {
	                final int n = ins.read(buf);
	                if (n == -1) {
	                    break;
	                }
	                bout.write(buf, 0, n);
	            }
	            result = bout.toByteArray();
	            bout.close();
            }
        } catch (final Exception e) {
            throw new BT747Exception(I18N
                    .i18n("Problem downloading AGPS data."), e);
        }
        return result;
    }
    
    
    
    public static final byte[] getAgpsUsingSimpleFtp(final String url) throws java.io.IOException , BT747Exception{
        if (url.startsWith("ftp://")) {
            bt747.sys.Generic.debug("Trying to get AGPS using Simple Ftp");
        	byte[] agpsData;
            final int colonIdx = url.indexOf(':', 6);
            final int atIdx = url.indexOf('@');
            // final int slashIdx = url.indexOf('/', 6);

            String hostUrl;
            String user = "anonymous";
            String pass = "anonymous";
            if (atIdx < 0 || (colonIdx > 0 && colonIdx > atIdx)) {
                hostUrl = url.substring(6);
            } else {
                if (colonIdx > 0 && colonIdx < atIdx) {
                    // Username and password.
                    user = url.substring(6, colonIdx);
                    pass = url.substring(colonIdx + 1, atIdx);
                } else {
                    // Only username
                    user = url.substring(6, atIdx);
                    pass = "";
                }
                hostUrl = url.substring(atIdx + 1);
            }
            final int hostSlash = hostUrl.indexOf('/');
            if (hostSlash > 0) {
                final String hostname = hostUrl.substring(0,
                        hostSlash);
                final String path = hostUrl
                        .substring(hostSlash + 1);
                final int pathSlash = path.indexOf('/');
                SimpleFTP ftp;
                String dir;
                String name;
                if (pathSlash > 0) {
                    dir = path.substring(0, pathSlash);
                    name = path.substring(pathSlash + 1);
                } else {
                    dir = "";
                    name = path;
                }
                /*
                if (Log.isDebugEnabled()) {
                    Log.debug("<User>"
                            + user // + "<Pass>" + pass
                            + "<Site>" + hostname + "<Dir>" + dir
                            + "<name>" + name);
                }
                */
                ftp = new SimpleFTP();
                ftp.connect(hostname, 21, user, pass);
                if (dir.length() > 0) {
                    ftp.connect(dir);
                }
                final ByteArrayOutputStream os = new ByteArrayOutputStream(
                        120 * 1024);
                ftp.bin();
                ftp.retr(os, name);
                ftp.disconnect();
                agpsData = os.toByteArray();
                os.close();
                return agpsData;
            }
        } /*else {
        	return J2SEAGPS.getBytesFromUrl(url);
        }*/
        return null;
    }
}
