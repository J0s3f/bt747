/**
 * 
 */
package bt747.sys.interfaces;

import bt747.sys.Generic;
import bt747.sys.I18N;

/**
 * @author Mario
 * 
 */
public class BT747Exception extends Error {
    /**
     * 
     */
    private static final long serialVersionUID = 6635539635823194664L;

    /**
     * 
     */
    public BT747Exception(final String msg) {
        super(msg);
        Generic.debug("Exception created: " + getMessage());
    }

    /**
     * 
     */
    public BT747Exception(final String msg, final Throwable e) {
        super(msg, e);
        Generic.debug("Exception created: " + getMessage(), e);
    }
}
