import waba.sys.Convert;
import waba.sys.Settings;

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
	 * @param Baudnbr The Baudnbr to set.
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
	 * @param Baudnbr The Baudnbr to set.
	 */
	public static void setStartupOpenPort(boolean value) {
		Settings.appSettings=Settings.appSettings.substring(0,16)
		+(value?"1":"0")
		+Settings.appSettings.substring(7);
	}
	
	static public int Hex2Int(String p_Value) {
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
	
}
