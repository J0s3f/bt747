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

/** Structure to hold GPS data for one point
 * @author Mario De Weerd
 */
public class GPSRecord {
    public int  utc;
    public int  valid;
    public double latitude;
    public double longitude;
    public float  height;
    public float  speed;
    public float  heading;
    public int  dsta;
    public int  dage;
    public int  pdop;
    public int  hdop;
    public int  vdop;
    // TODO: Add structures for NSAT, ...
    public int  rcr;  /** Recorder reason */
    public int  milisecond;
    public double distance;

    public GPSRecord() {} {
    }
    
    /**
     * 
     */
    public GPSRecord(GPSRecord r) {
        this.utc= r.utc;
        this.valid= r.valid;
        this.latitude= r.latitude;
        this.longitude= r.longitude;
        this.height= r.height;
        this.speed= r.speed;
        this.heading= r.heading;
        this.dsta= r.dsta;
        this.dage= r.dage;
        this.pdop= r.pdop;
        this.hdop= r.hdop;
        this.vdop= r.vdop;
        // TODO: Add structures for NSAT, ...
        this.rcr= r.rcr;
        this.milisecond= r.milisecond;
        this.distance= r.distance;
    }
    
}
