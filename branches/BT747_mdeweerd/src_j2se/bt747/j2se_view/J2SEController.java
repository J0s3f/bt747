/**
 * 
 */
package bt747.j2se_view;

import gps.convert.ExternalUtils;
import gps.convert.GeoidIF;
import gps.log.in.GPSInputConversionFactory;
import gps.log.in.GPSLogConvertInterface;

import java.io.File;
import java.io.IOException;

import net.sf.bt747.j2se.app.agps.J2SEAGPS;

import bt747.Version;
import bt747.j2se_view.helpers.TaggedFilePathFactory;
import bt747.j2se_view.model.ImageData;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 * 
 */
public class J2SEController extends Controller {

    /**
     * Reference to the model.
     */
    private Model m;

    /**
     * @param model
     */
    public J2SEController(final Model model) {
        super(model);
        m = model;
    }

    /**
     * 
     */
    public J2SEController() {
        super();

        // Some information gathered regarding proxies.
        // In JDK 5 apparently you can set the java.net.useSystemProxies to
        // true and Java will pickup and use the proxies set in Windows (or
        // Gnome). That's a great feature! (From Kohsuke Kawaguchi).
        //
        // http://java.sun.com/javase/6/docs/technotes/guides/net/proxies.html
        //         
        //         
        // System.getProperties().put( "proxySet", "true" );
        // System.getProperties().put( "proxyHost", "myProxyMachineName" );
        // System.getProperties().put( "proxyPort", "85" );
        //         
        //         
        // URLConnection connection = url.openConnection();
        // String password = "username:password";
        // String encodedPassword = base64Encode( password );
        // connection.setRequestProperty( "Proxy-Authorization",
        // encodedPassword );
        //         
        //         
        //
        // defaultProperties.put( "ftpProxySet", "true" );
        // defaultProperties.put( "ftpProxyHost", "proxy-host-name" );
        // defaultProperties.put( "ftpProxyPort", "85" );
        //
        //         
        //
        // URL url = new
        // URL("ftp://ftp.netscape.com/pub/navigator/3.04/windows/readme.txt"
        // );
        //
        // http://www.java-tips.org/java.net/how-to-detect-proxy-settings-for-internet-connection.html

        // Currently usingn the simple hint:
        System.setProperty("java.net.useSystemProxies", "true");
    }

    /**
     * 
     */
    public void setModel(final Model m) {
        super.setModel(m);
        this.m = m;
        updateUseGeoid();
    }

    private static final class GPXHandlerFactory extends
            GPSInputConversionFactory {
        /*
         * (non-Javadoc)
         * 
         * @see
         * bt747.model.GPSOutputFactory#getInputConversionInstance(java.lang
         * .String)
         */
        @Override
        public final GPSLogConvertInterface getInputConversionInstance(
                final BT747Path logFile, final int loggerType) {
            final String logFileLC = logFile.getPath().toLowerCase();
            if (logFileLC.endsWith(".gpx")) {
                return new GPXLogConvert();
            } else {
                return super.getInputConversionInstance(logFile, loggerType);
            }
        }
    }

    static {
        GPSInputConversionFactory.addHandler(new GPXHandlerFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.Controller#modelEvent(bt747.model.ModelEvent)
     */
    @Override
    public void modelEvent(ModelEvent e) {
        super.modelEvent(e);
        final int type = e.getType();
        switch (type) {

        case ModelEvent.SETTING_CHANGE:
            final int arg = Integer.valueOf((String) e.getArg());
            switch (arg) {
            case Model.IS_USE_PRECISE_GEOID:

                updateUseGeoid();

                break;
            }
            break;
        }

    }

    private void updateUseGeoid() {
        GeoidIF geoidIF = null;
        if (m.getBooleanOpt(Model.IS_USE_PRECISE_GEOID)) {
            geoidIF = net.sf.bt747.j2se.app.utils.Geoid.getInstance();
            if (geoidIF == null) {
                Generic.debug("Precise Geoid loading failed");
                super.setBooleanOpt(Model.IS_USE_PRECISE_GEOID, false);
            }
        } else {
            geoidIF = gps.convert.Geoid.getInstance();
        }
        // Generic.debug("Set geoid to "+geoidIF);
        if (geoidIF != null) {
            ExternalUtils.setGeoidIF(geoidIF);
        }
    }

    /**
     * @param fpf
     * @param img
     * @throws IOException
     */
    public final static void tagImage(final TaggedFilePathFactory fpf,
            final ImageData img) throws IOException {

        if (!img.getGpsRecord().hasPosition()) {
            // Do not do anything for images without a position
            return;
        }
        final String p = img.getFilePath().getPath();
        final String newPath = fpf.getTaggedFilePath(p, img);

        final String f1 = (new File(newPath)).getCanonicalPath();
        final String f2 = (new File(p)).getCanonicalPath();
        String orgPath = p;
        if (f1.equals(f2)) {
            // Target path and origin path are the same.
            // We may need to move the original file.
            orgPath = fpf.getOrgFilePath(p, img);
            final File d = new File(orgPath);
            if (!d.exists()) {
                (new File(p)).renameTo(d);
            }
        }
        // now convert from orgPath to newPath.
        img.writeImage(new BT747Path(orgPath), new BT747Path(newPath));
        img.setModifiedTime(new BT747Path(newPath));
    }

    /** Add a log file with GPS data to the list of files.
     * @param f
     */
    public final static void addLogFile(final File f) {
        try {
            addLogFile(new BT747Path(f.getCanonicalPath()));
        } catch (final Exception e) {
            bt747.sys.Generic.debug("Problem adding log file", e);
        }
    }

    /**
     * Get the AGPS data from the default Url and upload to the device.
     */
    public final void downloadAndUploadAgpsData() {
        final String urlTxt = Version.AURL + "MTK8d.EPO";
        bt747.sys.Generic.debug("Getting MTK7d.EPO data.");
        downloadAndUploadAgpsData(urlTxt);
    }

    /**
     * Get AGPS data from the given URL and upload to the device.
     * 
     * @param url
     */
    public final void downloadAndUploadAgpsData(final String url) {
        final String urlTxt = url;
        final Thread t = new Thread(new Runnable() {
            final String urlT = urlTxt;

            public final void run() {
                // final String urlTxt = m.getStringOpt(AppSettings.AGPSURL);
                // bt747.sys.Generic.debug("Getting data from <" + urlTxt +
                // ">");
                try {
                    final byte[] agpsData = J2SEAGPS.getBytesFromUrl(urlT);
                    // bt747.sys.Generic.debug("Finished getting data from <"
                    // + urlTxt + ">");
                    setAgpsData(agpsData);
                    bt747.sys.Generic.debug("MTK7d.EPO data fetched.");
                } catch (final BT747Exception b) {
                    m.postEvent(new ModelEvent(ModelEvent.EXCEPTION, b));
                }
            }
        });
        t.start();
    }
}
