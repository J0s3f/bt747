// ********************************************************************
// *** (c)2007-2009 Mario De Weerd ***
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
 * Defines the interface for a Random Access File. It only adds 'setPos' to
 * the interface, but this feature has a big impact on the fact that the file
 * can be buffered easily or not on J2SE platforms.
 * 
 * @author Mario De Weerd
 * 
 */
public interface BT747RAFile extends BT747File {
    /**
     * Set the file position.
     * 
     * @param pos
     * @return true if success.
     */
    boolean setPos(int pos);
}
