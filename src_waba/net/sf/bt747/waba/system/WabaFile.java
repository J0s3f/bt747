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
//***  This layer was written for the SuperWaba toolset.           ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sf.bt747.waba.system;

import bt747.interfaces.BT747File;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class WabaFile extends waba.io.File implements BT747File {
    public static char separatorChar = '/';
    public static String separatorStr = "/";

    /**
     * @param path
     */
    public WabaFile(String path) {
        super(path);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param path
     * @param mode
     */
    public WabaFile(String path, int mode) {
        super(path, mode);
        if (mode == waba.io.File.READ_WRITE || mode == waba.io.File.WRITE_ONLY) {
            setPos(getSize()); // Default is append
        }
    }

    /**
     * @param path
     * @param mode
     * @param slot
     */
    public WabaFile(String path, int mode, int slot) {
        super(path, mode, slot);
        if (mode == waba.io.File.READ_WRITE || mode == waba.io.File.WRITE_ONLY) {
            setPos(getSize()); // Default is append
        }
    }

    public static String getCardVolumePath() {
        if (getCardVolume() != null) {
            return getCardVolume().getPath();
        } else {
            return null;
        }
    }

    public int getLastError() {
        return lastError;
    }

}
