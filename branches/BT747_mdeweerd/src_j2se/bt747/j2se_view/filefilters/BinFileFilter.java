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

import java.io.File;

import javax.swing.filechooser.FileFilter;

import bt747.j2se_view.J2SEAppController;

/**
 * @author Mario
 *
 */
public final class BinFileFilter extends FileFilter {

    /**
     * Lower case list of accepted extensions.
     */
    private final String[] extensions = {
            ".bin"
    };
    
    
    
    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        String filename = f.getName().toLowerCase();
        for (int i = 0; i < extensions.length; i++) {
             if(filename.endsWith(extensions[i])) {
                 return true;
             }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return J2SEAppController.getString("BIN_Description");
    }

}
