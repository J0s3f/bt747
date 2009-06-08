/**
 * 
 */
package bt747.j2se_view;

import gps.convert.Conv;
import gps.convert.GeoidIF;
import gps.log.in.GPSInputConversionFactory;
import gps.log.in.GPSLogConvertInterface;

import java.io.File;
import java.io.IOException;

import net.sf.bt747.j2se.app.agps.J2SEAGPS;

import bt747.j2se_view.model.ImageData;
import bt747.model.AppSettings;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.interfaces.BT747Exception;

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
         * @see bt747.model.GPSOutputFactory#getInputConversionInstance(java.lang.String)
         */
        @Override
        public final GPSLogConvertInterface getInputConversionInstance(
                final String logFile) {
            final String logFileLC = logFile.toLowerCase();
            if (logFileLC.endsWith(".gpx")) {
                return new GPXLogConvert();
            } else {
                return super.getInputConversionInstance(logFile);
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

        } else {
            geoidIF = gps.convert.Geoid.getInstance();
        }
        //Generic.debug("Set geoid to "+geoidIF);
        if (geoidIF != null) {
            Conv.setGeoidIF(geoidIF);
        }
    }

    /**
     * @param fpf
     * @param w
     * @throws IOException
     */
    public final static void tagImage(final TaggedFilePathFactory fpf,
            final ImageData img) throws IOException {

        if (!img.getGpsRecord().hasPosition()) {
            // Do not do anything for images without a position
            return;
        }
        final String p = img.getPath();
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
        img.writeImage(orgPath, newPath, 0);
    }

    public final static void addLogFile(final File f) {
        try {
            addLogFile(f.getCanonicalPath(), 0);
        } catch (final Exception e) {
            bt747.sys.Generic.debug("Problem adding log file", e);
        }
    }

    public final void downloadAndUploadAgpsData() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public final void run() {
                final String urlTxt = m.getStringOpt(AppSettings.AGPSURL);
                bt747.sys.Generic.debug("Getting data from <" + urlTxt + ">");
                try {
                final byte[] agpsData = J2SEAGPS.getBytesFromUrl(urlTxt);
                bt747.sys.Generic.debug("Finished getting data from <"
                        + urlTxt + ">");
                setAgpsData(agpsData);
                } catch (final BT747Exception b) {
                    m.postEvent(new ModelEvent(ModelEvent.EXCEPTION, b));
                }
            }
        });

    }
}
