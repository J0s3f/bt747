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
package gps.convert;

/**
 * Implement some conversion functions
 * 
 * @author Mario De Weerd
 */
public final class Conv {
    /** Convert a string in hexadecimal to a list of bytes
     * 
     * @param hexStr Hexadecimal representation of bytes
     * @return list of bytes 
     */
    public final static int HexStringToBytes(final String hexStr, byte[] p_Buffer) {
        char[] z_Data = hexStr.toCharArray();
        int length=z_Data.length;
        for (int i = 0; i < z_Data.length; i += 2) {
            int c1 = z_Data[i]&(~0x20)-'0';  // Uppercase
            int c2 = z_Data[i + 1]&(~0x20)-'0'; // Uppercase
            if ((c1 >= 0) && (c1 <= 9)) {
            } else if ((c1 >= 'A'-'0') && (c1 <= 'F'-'0')) {
                c1 += -('A'-'0') + 10;
            } else {
                c1 = 0;
            }
            if ((c2 >= 0) && (c2 <= 9)) {
            } else if ((c2 >= 'A'-'0') && (c2 <= 'F'-'0')) {
                c2 += -('A'-'0') + 10;
            } else {
                c2 = 0;
            }
            p_Buffer[i >> 1] = (byte) ((c1 << 4) + c2);
        }

        return length;
    }
    
    /** Convert a string in hexadecimal to the corresponding int
     * 
     * @param hexStr Hexadecimal representation of bytes
     * @return list of bytes 
     */
    public final static int hex2Int(final String hexStr) {
        int p_Result=0;
        for (int i = 0; i < hexStr.length(); i++) {
            int z_nibble = (byte)hexStr.charAt(i);
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
