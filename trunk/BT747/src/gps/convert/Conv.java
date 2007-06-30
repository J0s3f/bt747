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
    public final static int hexStringToBytes(final String hexStr, byte[] p_Buffer) {
        char[] z_Data = hexStr.toCharArray();
        int length=z_Data.length;
        byte z_Byte;
        for (int i = 0; i < z_Data.length; i += 2) {
            switch (z_Data[i])
            {
                case '0': z_Byte = (byte)0x00;break;
                case '1': z_Byte = (byte)0x10;break;
                case '2': z_Byte = (byte)0x20;break;
                case '3': z_Byte = (byte)0x30;break;
                case '4': z_Byte = (byte)0x40;break;
                case '5': z_Byte = (byte)0x50;break;
                case '6': z_Byte = (byte)0x60;break;
                case '7': z_Byte = (byte)0x70;break;
                case '8': z_Byte = (byte)0x80;break;
                case '9': z_Byte = (byte)0x90;break;
                case 'A': z_Byte = (byte)0xA0;break;
                case 'B': z_Byte = (byte)0xB0;break;
                case 'C': z_Byte = (byte)0xC0;break;
                case 'D': z_Byte = (byte)0xD0;break;
                case 'E': z_Byte = (byte)0xE0;break;
                case 'F': z_Byte = (byte)0xF0;break;
                case 'a': z_Byte = (byte)0xA0;break;
                case 'b': z_Byte = (byte)0xB0;break;
                case 'c': z_Byte = (byte)0xC0;break;
                case 'd': z_Byte = (byte)0xD0;break;
                case 'e': z_Byte = (byte)0xE0;break;
                case 'f': z_Byte = (byte)0xF0;break;
                default: z_Byte=0;
            }
            switch (z_Data[i+1])
            {
                case '0': z_Byte |= (byte)0; break;
                case '1': z_Byte |= (byte)1; break;
                case '2': z_Byte |= (byte)2; break;
                case '3': z_Byte |= (byte)3; break;
                case '4': z_Byte |= (byte)4; break;
                case '5': z_Byte |= (byte)5; break;
                case '6': z_Byte |= (byte)6; break;
                case '7': z_Byte |= (byte)7; break;
                case '8': z_Byte |= (byte)8; break;
                case '9': z_Byte |= (byte)9; break;
                case 'A': z_Byte |= (byte)0xA; break;
                case 'B': z_Byte |= (byte)0xB; break;
                case 'C': z_Byte |= (byte)0xC; break;
                case 'D': z_Byte |= (byte)0xD; break;
                case 'E': z_Byte |= (byte)0xE; break;
                case 'F': z_Byte |= (byte)0xF; break;
                case 'a': z_Byte |= (byte)0xA; break;
                case 'b': z_Byte |= (byte)0xB; break;
                case 'c': z_Byte |= (byte)0xC; break;
                case 'd': z_Byte |= (byte)0xD; break;
                case 'e': z_Byte |= (byte)0xE; break;
                case 'f': z_Byte |= (byte)0xF; break;
            }
            p_Buffer[i >> 1] = z_Byte;
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
            int z_nibble;
            switch (hexStr.charAt(i))
            {
                case '0': z_nibble = 0; break;
                case '1': z_nibble = 1; break;
                case '2': z_nibble = 2; break;
                case '3': z_nibble = 3; break;
                case '4': z_nibble = 4; break;
                case '5': z_nibble = 5; break;
                case '6': z_nibble = 6; break;
                case '7': z_nibble = 7; break;
                case '8': z_nibble = 8; break;
                case '9': z_nibble = 9; break;
                case 'A': z_nibble = 0xA; break;
                case 'B': z_nibble = 0xB; break;
                case 'C': z_nibble = 0xC; break;
                case 'D': z_nibble = 0xD; break;
                case 'E': z_nibble = 0xE; break;
                case 'F': z_nibble = 0xF; break;
                case 'a': z_nibble = 0xA; break;
                case 'b': z_nibble = 0xB; break;
                case 'c': z_nibble = 0xC; break;
                case 'd': z_nibble = 0xD; break;
                case 'e': z_nibble = 0xE; break;
                case 'f': z_nibble = 0xF; break;
                default: z_nibble=0;
            }
            p_Result<<=4;
            p_Result+=z_nibble;
        }
        return p_Result;
    }
}
