/**
 * 
 */
package bt747.sys.interfaces;

import bt747.sys.Generic;


/**
 * Class implementing a BT747 Error exception.
 * 
 * Will be gradually used during refactoring.
 * 
 * @author Mario
 *
 */
public class BT747Exception extends Throwable {
    private static final long serialVersionUID = -7054475155290308509L;

    public BT747Exception(final String msg) {
        super(msg);
        Generic.debug("Exception created: "+ msg);
    }
}
