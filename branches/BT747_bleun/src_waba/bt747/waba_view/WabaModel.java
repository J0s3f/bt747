/**
 * 
 */
package bt747.waba_view;

import net.sf.bt747.waba.system.WabaPath;

import bt747.model.AppSettings;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 * 
 */
public class WabaModel extends AppSettings {

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.AppSettings#getPath(int)
     */
    public BT747Path getPath(final int pathType) {
        switch (pathType) {
        case LOGFILEPATH:
        case REPORTFILEBASEPATH:
            return new WabaPath(getStringOpt(LOGFILEPATH),
                    getIntOpt(AppSettings.CARD));
        default:
            return super.getPath(pathType);
        }
    }

}
