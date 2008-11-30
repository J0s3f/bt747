//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package bt747.j2se_view.filefilters;

/**
 * @author Mario
 * 
 */
public final class KnownFileFilter extends ListFileFilter {

    /**
     * Lower case list of accepted extensions.
     */
    private static final String[] extensions = { ".bin", ".nmea", ".txt",
            ".log", ".nme", ".nma", ".trl", ".csv", ".sr" };

    private static final String description = "KNOWN_Description";

    /**
     * 
     */
    public KnownFileFilter() {
        super(extensions, description);
    }
}
