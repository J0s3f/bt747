import waba.sys.Convert;
import waba.sys.Settings;
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

/*
 * Created on 15 mai 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BT747Settings {
	
	/**
	 * @return Returns the portnbr.
	 */
	public static int getPortnbr() {
		return Hex2Int(Settings.appSettings.substring(0,8));
	}
	/**
	 * @param portnbr The portnbr to set.
	 */
	public static void setPortnbr(int portnbr) {
		Settings.appSettings=Convert.unsigned2hex(portnbr,8)
		+Settings.appSettings.substring(8);
	}
	public static int getBaudRate() {
		return Hex2Int(Settings.appSettings.substring(8,16));
	}
	/**
	 * @param Baud The Baud rate to set.
	 */
	public static void setBaudRate(int Baud) {
		Settings.appSettings=Settings.appSettings.substring(0,8)
		+Convert.unsigned2hex(Baud,8)
		+Settings.appSettings.substring(8);
	}

	public static boolean getStartupOpenPort() {
		return Hex2Int(Settings.appSettings.substring(16,1))==1;
	}
	/**
	 * @param value The default value for opening the port.
	 */
	public static void setStartupOpenPort(boolean value) {
		Settings.appSettings=Settings.appSettings.substring(0,16)
		+(value?"1":"0")
		+Settings.appSettings.substring(7);
	}
	
	public final static int Hex2Int(final String p_Value) {
		int p_Result=0;
		for (int i = 0; i < p_Value.length(); i++) {
			int z_nibble = (byte)p_Value.charAt(i);
			if( (z_nibble>='0')&&(z_nibble<='9') ) {
				z_nibble-='0';
			} else if ( (z_nibble>='A')&&(z_nibble<='F') ) {
				z_nibble+=-'A'+10;
			} else if ( (z_nibble>='a')&&(z_nibble<='f') ) {
				z_nibble+=-'a'+10;			
			} else {
				z_nibble=0;			
			}
			p_Result<<=4;
			p_Result+=z_nibble;
		}
		return p_Result;
	}
	
	public static final byte[] HexStringToBytes(final String p_Data) {
		char[] z_Data= p_Data.toCharArray();
		byte[] z_Result= new byte[z_Data.length>>1];
		for (int i = 0; i < z_Data.length; i+=2) {
			char c1 = z_Data[i];
			char c2 = z_Data[i+1];
			if( (c1>='0')&&(c1<='9') ) {
				c1-='0';
			} else if ( (c1>='A')&&(c1<='F') ) {
				c1+=-'A'+10;
			} else if ( (c1>='a')&&(c1<='f') ) {
				c1+=-'a'+10;			
			} else {
				c1=0;			
			}
			if( (c2>='0')&&(c2<='9') ) {
				c2-='0';
			} else if ( (c2>='A')&&(c2<='F') ) {
				c2+=-'A'+10;
			} else if ( (c2>='a')&&(c2<='f') ) {
				c2+=-'a'+10;			
			} else {
				c2=0;			
			}
			z_Result[i>>1]=(byte)((c1<<4)+c2);
		}
		
		return z_Result;
	}
	

	
}
