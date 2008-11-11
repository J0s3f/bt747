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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package gps.log.in;

import gps.log.out.GPSFile;

/**
 * Interface defining a Log conversion class.
 * 
 * @author Mario De Weerd
 */
public interface GPSLogConvert {
    int parseFile(final GPSFile gpsFile);

    /**
     * The time offset to apply to the output records in seconds.
     * 
     * @param offset
     *            The time offset in seconds.
     */
    void setTimeOffset(final long offset);

    /**
     * Height conversion active if true.
     * 
     * @param mode
     *            When '-1' : Substract MSL over WGS84<br>
     *            When '1' : Add MSL over WGS84 in output.<br>
     *            When '0': Do nothing<br>
     */
    void setConvertWGS84ToMSL(final int mode);

    int toGPSFile(final String fileName, final GPSFile gpsFile, final int card);

    /**
     * Returns some information regarding the reported error. To be thrown in a
     * future version.
     * 
     * @return
     */
    String getErrorInfo();
    
    void stopConversion();
}