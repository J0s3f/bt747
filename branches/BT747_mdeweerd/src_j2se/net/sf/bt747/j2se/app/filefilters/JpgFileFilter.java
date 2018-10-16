//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     seesite@bt747.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package net.sf.bt747.j2se.app.filefilters;

/**
 * @author Mario
 * 
 */
public final class JpgFileFilter extends ListFileFilter {

    /**
     * Lower case list of accepted extensions.
     */
    private static final String[] extensions = { ".jpg", ".jpeg" };

    private static final String description = "JPEG_Description";

    /**
     * 
     */
    public JpgFileFilter() {
        super(extensions, description);
    }
}
