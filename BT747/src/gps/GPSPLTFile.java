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

import waba.io.File;
import waba.sys.Convert;
import waba.sys.Time;
import waba.util.Date;

/**Class to write a PLT file (OZI).
 * @author Mario De Weerd
 */
public class GPSPLTFile implements GPSFile {
    File m_File=null;
    int m_recCount;
    private GPSRecord activeFields;
    
    /**
     * 
     */
    public GPSPLTFile() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    GPSFilter m_Filter=null;
    
    public void setFilter(GPSFilter filter) {
        m_Filter=filter;
    }
    
    public boolean nextPass() {
        return false;
    }
    /* (non-Javadoc)
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext, final int Card) {
        // TODO Auto-generated method stub
        m_File=new File(basename+ext,File.DONT_OPEN,Card);
        if(m_File.exists()) {
            m_File.delete();
        }
        m_File=new File(basename+ext,File.CREATE, Card);
        if(!m_File.isOpen()) {
            waba.sys.Vm.debug("Could not open "+basename+ext);
            m_File=null;
        } else {
            m_recCount=0;
        }
    }
    
//    Track File (.plt) 
//    Line 1 : File type and version information
//    Line 2 : Geodetic Datum used for the Lat/Lon positions for each trackpoint
//    Line 3 : "Altitude is in feet" - just a reminder that the altitude is always stored in feet
//    Line 4 : Reserved for future use
//    Line 5 : multiple fields as below
//
//    Field 1 : always zero (0)
//    Field 2 : width of track plot line on screen - 1 or 2 are usually the best
//    Field 3 : track color (RGB)
//    Field 4 : track description (no commas allowed)
//    Field 5 : track skip value - reduces number of track points plotted, usually set to 1 
//    Field 6 : track type - 0 = normal , 10 = closed polygon , 20 = Alarm Zone
//    Field 7 : track fill style - 0 =bsSolid; 1 =bsClear; 2 =bsBdiagonal; 3 =bsFdiagonal; 4 =bsCross; 
//    5 =bsDiagCross; 6 =bsHorizontal; 7 =bsVertical;
//    Field 8 : track fill color (RGB)
//
//    Line 6 : Number of track points in the track, not used, the number of points is determined when reading the points file 
//

    
    public void writeLogFmtHeader(final GPSRecord f) {
        activeFields= new GPSRecord(f);
        writeTxt("BT747 Track Point File http://sf.net/projects/bt747 Version 0.77\r\n"
                +"WGS 84\r\n"
                +"Altitude is in feet\r\n"
                +"Reserved 3\r\n"
                +"0,2,255,BT747 Track,0,0,2,8421376\r\n"
                +"50000\r\n"  // number of points in the track, not used, unknown at this point.
                );
        //"NSAT (USED/VIEW),SAT INFO (SID-ELE-AZI-SNR)
    }
    
    private final static int DAYS_BETWEEN_1970_1983=4748;
    public Time utcTime(int utc_int) {
        long utc=utc_int&0xFFFFFFFFL;
        Time t=new Time();
        t.second=(int)utc%60;
        utc/=60;
        t.minute=(int)utc%60;
        utc/=60;
        t.hour=(int)utc%24;
        utc/=24;
        // Now days since 1/1/1970
        Date d= new Date(1,1,1983); //Minimum = 1983
        d.advance(((int)utc)-DAYS_BETWEEN_1970_1983);
        t.year=d.getYear();
        t.month=d.getMonth();
        t.day=d.getDay();
        return t;
    }
    
    private void writeTxt(final String s) {
        m_File.writeBytes(s.getBytes(),0,s.length());
    }
    
//  Trackpoint data 
    //
//        One line per trackpoint 
//        each field separated by a comma 
//        non essential fields need not be entered but comma separators must still be used (example ,,)
//        defaults will be used for empty fields 
    //
    //
//        Note that OziExplorer reads the Date/Time from field 5, the date and time in fields 6 & 7 are ignored.
    //
//        Example
//        -27.350436, 153.055540,1,-777,36169.6307194, 09-Jan-99, 3:08:14 
//        -27.348610, 153.055867,0,-777,36169.6307194, 09-Jan-99, 3:08:14 
    
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(GPSRecord s) {
        boolean prevField=false;
        m_recCount++;
        //rec+=Convert.toString(m_recCount);
        
        if(activeFields!=null && m_Filter.doFilter(s)) {
            String rec="";

//          Field 1 : Latitude - decimal degrees.
            if(activeFields.latitude!=0) {
                rec+=Convert.toString(s.latitude,6);
            }
            rec+=",";
//          Field 2 : Longitude - decimal degrees.
            if(activeFields.longitude!=0) {
                rec+=Convert.toString(s.longitude,6);
            }
            rec+=",";
//          Field 3 : Code - 0 if normal, 1 if break in track line
            rec+="0,"; // Normal for the moment - could detect break later ...
//          Field 4 : Altitude in feet (-777 if not valid)
            if(activeFields.height!=0) {
                rec+=Convert.toString((int)(s.height*39.37));
            } else {
                rec+="-777";
            }
            rec+=",";
//          Field 5 : Date - see Date Format below, if blank a preset date will be used
//          TDateTime value is the number of days that have passed since 12/30/1899.
            //private final static int DAYS_BETWEEN_19700101_18991230=4748;
            
//          Field 6 : Date as a string 
//          Field 7 : Time as a string
            if(activeFields.utc!=0) {
                Time t=utcTime(s.utc);
                rec+=Convert.toString(
                        (s.utc+(activeFields.milisecond!=0?(s.milisecond/1000.0):0))
                        /86400.0+25569,  //Days since 30/12/1899
                        7);  // 7 fractional digits
                rec+=",";
                rec+=
                    ( t.month<10?"0":"")+Convert.toString(t.month)+"/"
                    +(   t.day<10?"0":"")+Convert.toString(t.day)+"/"
                    +Convert.toString(t.year)
                    +","
                    +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
                    +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
                    +(t.second<10?"0":"")+Convert.toString(t.second)
                    ;
                if(activeFields.milisecond!=0) {
                    rec+=".";
                    rec+=(s.milisecond<100)?"0":"";
                    rec+=(s.milisecond<10)?"0":"";
                    rec+=Convert.toString(s.milisecond);
                }
            } else {
                rec+=",,";
            }
            rec+="\r\n";
            writeTxt(rec);
        } // activeFields!=null
    }
    
    /* (non-Javadoc)
     * @see gps.GPSFile#FinaliseFile()
     */
    public void finaliseFile() {
        // TODO Auto-generated method stub
        if(m_File!=null) {
            m_File.close();
            m_File=null;
        }
        
    }
    
}
