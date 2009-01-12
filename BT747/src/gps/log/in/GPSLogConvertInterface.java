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
package gps.log.in;

import gps.log.out.GPSFile;

/**
 * Interface defining a Log conversion class.
 * 
 * @author Mario De Weerd
 */
public interface GPSLogConvertInterface {
    /**
     * Parses the file once and will use {@link GPSFile} to generate the output.
     * 
     * @param gpsFile
     *            Represents the structure to generate the output.
     * @return error indication.
     */
    int parseFile(final GPSFileConverterInterface gpsFile);

    /**
     * Height conversion active if true.
     * 
     * @param mode
     *            When '-1' : Substract MSL over WGS84<br>
     *            When '1' : Add MSL over WGS84 in output.<br>
     *            When '0': Do nothing<br>
     */
    void setConvertWGS84ToMSL(final int mode);

    int toGPSFile(final String fileName,
            final GPSFileConverterInterface gpsFile, final int card);

    /**
     * Returns some information regarding the reported error. To be thrown in a
     * future version.
     * 
     * @return Textual description of the error.
     */
    String getErrorInfo();

    void stopConversion();
}