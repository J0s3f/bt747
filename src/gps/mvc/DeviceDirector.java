/**
 * 
 */
package gps.mvc;

import gps.ProtocolConstants;
import gps.connection.GPSrxtx;

/**
 * This class maintains consistency between the used Model and Controllers.
 * 
 * Intermediate step is to use this class as a reference to the Model and
 * Controller. Later, the intention is to implement come listener strategy to
 * update Models and Controllers.
 * 
 * Somewhat dirty, but cleaner than it was.
 * 
 * @author Mario
 * 
 */
public final class DeviceDirector implements ProtocolConstants {
    public Model model;
    public MtkModel mtkModel;
    public gps.mvc.Controller devController;
    public GPSrxtx gpsRxTx;
    public MtkController mtkControl;

    public void setProtocol(final GPSrxtx gpsRxTx, final int protocol) {
        this.gpsRxTx = gpsRxTx;
        setProtocol(protocol);
    }

    public void setProtocol(int protocol) {
        if (model == null) {
            model = new gps.mvc.Model(gpsRxTx, protocol);
        } else {
            model.setProtocol(protocol);
        }
        mtkModel = model.getMtkModel();
        if (devController == null) {
            devController = gps.mvc.Controller.getInstance(model, protocol);
        } else {
            devController.setProtocol(protocol);
        }
        mtkControl = devController.getMtkController();
    }
}
