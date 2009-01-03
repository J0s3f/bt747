/**
 * 
 */
package bt747.j2se_view;

import gps.log.in.GPSLogConvertInterface;

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

}
