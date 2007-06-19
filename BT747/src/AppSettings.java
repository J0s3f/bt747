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
import waba.sys.Convert;
import waba.sys.Settings;
import gps.convert.Conv;

/**
 * @author Mario De Weerd
  */
public class AppSettings implements gps.settings {
	
	/**
	 * @return Returns the portnbr.
	 */
	public int getPortnbr() {
		return Conv.hex2Int(Settings.appSettings.substring(0,8));
	}
	/**
	 * @param portnbr The portnbr to set.
	 */
	public void setPortnbr(int portnbr) {
		Settings.appSettings=Convert.unsigned2hex(portnbr,8)
		+Settings.appSettings.substring(8);
	}
    /**
     * @return The default baud rate
     */
	public int getBaudRate() {
		return Conv.hex2Int(Settings.appSettings.substring(8,16));
	}
	/**
	 * @param Baud The Baud rate to set as a default.
	 */
	public void setBaudRate(int Baud) {
		Settings.appSettings=Settings.appSettings.substring(0,8)
		+Convert.unsigned2hex(Baud,8)
		+Settings.appSettings.substring(8);
	}

	public boolean getStartupOpenPort() {
		return Conv.hex2Int(Settings.appSettings.substring(16,1))==1;
	}
	/**
	 * @param value The default value for opening the port.
	 */
	public void setStartupOpenPort(boolean value) {
		Settings.appSettings=Settings.appSettings.substring(0,16)
		+(value?"1":"0")
		+Settings.appSettings.substring(7);
	}
	

}
