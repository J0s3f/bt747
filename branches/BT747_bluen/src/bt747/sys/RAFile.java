// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package bt747.sys;

import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747RAFile;

/**
 * @author Mario De Weerd
 */
public final class RAFile extends File implements BT747RAFile {
    public RAFile(final BT747Path path) {
        super(JavaLibBridge.getRAFileInstance(path));
    }

    public RAFile(final BT747Path path, final int mode) {
        super(JavaLibBridge.getRAFileInstance(path, mode));
    }

    public final boolean setPos(final int pos) {
        return ((BT747RAFile) getFile()).setPos(pos);
    }

}
