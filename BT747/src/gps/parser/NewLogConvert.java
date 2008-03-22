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
package gps.parser;

import gps.GPSFile;
import gps.GPSLogConvert;
import gps.GPSRecord;

/** This class is used to convert the binary log to a new format.
 * Basically this class interprets the log and creates a {@link GPSRecord}.
 * The {@link GPSRecord} is then sent to the {@link GPSFile} class object to write it
 * to the output.
 * 
 * @author Mario De Weerd
 */
public final class NewLogConvert implements GPSLogConvert {
    protected boolean passToFindFieldsActivatedInLog= false;
    protected int activeFileFields=0;
    String [] argv=new String[1];
    
    /**
     * @return
     * @param gpsFile - object doing actual write to files
     * 
     */
    public final void parseFile(final GPSFile gpsFile) {

    }
    
    public final void setTimeOffset(final long offset) {
    }
    
    public final void setNoGeoid(final boolean b) {
    }
    
    public final void toGPSFile(final String fileName, final GPSFile gpsFile, final int Card) {
        argv[0]=fileName;
        try {
            LogFile.main(argv);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    /**
     * @param holux The holux to set.
     */
    public void setHolux(boolean holux) {
    }
    

    }
