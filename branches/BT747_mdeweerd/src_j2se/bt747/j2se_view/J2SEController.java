/**
 * 
 */
package bt747.j2se_view;

import java.io.File;
import java.io.IOException;

import gps.log.in.GPSLogConvertInterface;

import bt747.j2se_view.model.ImageData;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.sys.Generic;

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
    public J2SEController(Model model) {
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
    public void setModel(Model m) {
        super.setModel(m);
        this.m = m;
    }

    public GPSLogConvertInterface getInputConversionInstance(final int logType) {
        String logFileLC = m.getStringOpt(Model.LOGFILEPATH).toLowerCase();
        if (logFileLC.endsWith(".gpx")) {
            GPSLogConvertInterface lc = new GPXLogConvert();
            String parameters = "";
            int sourceHeightReference = getHeightReference(Model.GPX_LOGTYPE);
            int destinationHeightReference = getHeightReference(logType);
    
            switch (m.getHeightConversionMode()) {
            case Model.HEIGHT_AUTOMATIC:
                if (sourceHeightReference == HEIGHT_MSL
                        && destinationHeightReference == HEIGHT_WGS84) {
                    /* Need to add the height in automatic mode */
                    lc.setConvertWGS84ToMSL(+1);
                } else if (sourceHeightReference == HEIGHT_WGS84
                        && destinationHeightReference == HEIGHT_MSL) {
                    /* Need to substract the height in automatic mode */
                    lc.setConvertWGS84ToMSL(-1);
                } else {
                    /* Do nothing */
                    lc.setConvertWGS84ToMSL(0);
                }
                break;
            case Model.HEIGHT_WGS84_TO_MSL:
                lc.setConvertWGS84ToMSL(-1);
                break;
            case Model.HEIGHT_NOCHANGE:
                lc.setConvertWGS84ToMSL(0);
                break;
            case Model.HEIGHT_MSL_TO_WGS84:
                lc.setConvertWGS84ToMSL(1);
                break;
            }
    
            if (Generic.isDebug()) {
                Generic.debug(parameters);
            }
    
            return lc;
        } else {
            return super.getInputConversionInstance(logType);
        }
    }

    /**
     * @param fpf
     * @param w
     * @throws IOException
     */
    public final static void convertImage(final TaggedFilePathFactory fpf, final ImageData img)
            throws IOException {
        final String p = img.getPath();
        final String newPath = fpf.getTaggedFilePath(p, img);

        String f1 = (new File(newPath)).getCanonicalPath();
        String f2 = (new File(p)).getCanonicalPath();
        String orgPath = p;
        if (f1.equals(f2)) {
            // Target path and origin path are the same.
            // We may need to move the original file.
            orgPath = fpf.getOrgFilePath(p, img);
            File d = new File(orgPath);
            if (!d.exists()) {
                (new File(p)).renameTo(d);
            }
        }
        // now convert from orgPath to newPath.
        img.writeImage(orgPath, newPath, 0);
    }

}
