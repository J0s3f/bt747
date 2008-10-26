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
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
package gps.log.out;

import bt747.sys.interfaces.BT747FileName;

public final class GPSDefaultFileName implements BT747FileName {

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747FileName#getOutputFileName(java.lang.String,
     *      int, java.lang.String, java.lang.String)
     */
    public final String getOutputFileName(final String baseName,
            final int utcTimeSeconds, final String proposedExtension,
            final String proposedTimeSpec) {
        return (baseName + proposedTimeSpec + proposedExtension);
    }

}
