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
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.out;

import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747Path;

public final class GPSDefaultFileName implements BT747FileName {

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747FileName#getOutputFileName(java.lang.String,
     *      int, java.lang.String, java.lang.String)
     */
    public final BT747Path getOutputFileName(final BT747Path basePath,
            final int utcTimeSeconds, final String proposedExtension,
            final String proposedTimeSpec) {
        final String baseName = basePath.getPath();
        if (((baseName.length() == 0)
                || (baseName.charAt(baseName.length() - 1) == '/') || (baseName
                .charAt(baseName.length() - 1) == '\\'))
                && (proposedTimeSpec.charAt(0) == '-')) {
            return basePath.proto(baseName + proposedTimeSpec.substring(1) + proposedExtension);
        } else {
            return basePath.proto(baseName + proposedTimeSpec + proposedExtension);
        }
    }

}