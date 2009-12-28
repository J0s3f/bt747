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
package bt747.sys.interfaces;

/**
 * Interface to a class that is used to determine the filename of an output
 * file.
 * 
 * @author Mario De Weerd
 * 
 */
public interface BT747FileName {
    /**
     * Build the filename from the provided parameters.
     * 
     * @param baseName
     *                proposed baseName.
     * @param utcTimeSeconds
     *                Time of first record in file.
     * @param proposedExtension
     *                Proposed extension of file.
     * @param proposedTimeSpec
     *                Proposed extension to filename.
     * @return
     */
    BT747Path getOutputFileName(final BT747Path baseNamePath, final int utcTimeSeconds,
            final String proposedExtension, final String proposedTimeSpec);
}
