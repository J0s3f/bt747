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
package gps;


/** Interface used by gps to retrieve and set default settings for the
 * application.
 * @author Mario De Weerd
 */
public interface settings {
    /**
     * @return Returns the portnbr.
     */
    public int getPortnbr();
    /**
     * @return The default baud rate
     */
    public int getBaudRate();
    /**
     * @return The card to read/write
     */
    public int getCard();


    /**
     * @return The default baud rate
     */
    public int getDownloadTimeOut();
   
    /**
     * @param Baud The Baud rate to set as a default.
     */
    public void setBaudRate(int Baud);

    public void setPortnbr(int portnbr);

    /** Determine if port should be opened on startup
     * 
     * @return True if port should be opened.
     */
    public boolean getStartupOpenPort();
    /**
     * @param value The default value for opening the port.
     */
    public void setStartupOpenPort(boolean value);

    /**
     * Get the number of chunks to read ahead.
     * @return
     */
    public int getLogRequestAhead();

    public String getBaseDirPath();

    

    
    /**
     * @param startAddr the startAddr to set
     */
    public void setStartAddr(int startAddr);
    /**
     * @param endAddr the endAddr to set
     */
    public void setEndAddr(int endAddr) ;

    /**
     * @param downloadOnGoing the downloadOnGoing to set
     */
    public void setDownloadOnGoing(boolean downloadOnGoing);

    /**
     * @param nextReadAddr the nextReadAddr to set
     */
    public void setNextReadAddr(int nextReadAddr);
    
}
