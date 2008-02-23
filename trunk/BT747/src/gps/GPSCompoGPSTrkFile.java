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

import bt747.sys.Convert;

/**Class to write a CompeGPS TRK file or WPT file.
 * @author Mario De Weerd
 */
public class GPSCompoGPSTrkFile extends GPSFile {

    // The next information is from gpsbabel:
    
//    the meaning of leading characters in CompeGPS data lines (enhanced PCX):
//
//    header lines:    
//    
//    "G": WGS 84         - Datum of the map
//    "N": Anybody            - Name of the user
//    "L": -02:00:00          - Difference to UTC
//    "M": ...            - Any comments
//    "R": 16711680 , xxxx , 1    - Route header
//    "U": 1              - System of coordinates (0=UTM 1=Latitude/Longitude)
//
//    "C":  0 0 255 2 -1.000000   - ???
//    "V":  0.0 0.0 0 0 0 0 0.0   - ???
//    "E": 0|1|00-NUL-00 00:00:00|00:00:00|0 - ???
//    
//    data lines:
//    
//    "W": if(route) routepoint; else waypoint
//    "T": trackpoint    
//        "t": if(track) additionally track info
//         if(!track) additionally trackpoint info
//    "a": link to ...
//    "w": waypoint additional info
    // End fo gpsbabel info
    // "z": 0.0,0.0,0.0,0.0     // bounding box 
    // "P": device information, version, ...
    

    private boolean m_isWayType;
    
    public GPSCompoGPSTrkFile() {
        super();
        C_NUMBER_OF_PASSES=2;
    }

    public void initialiseFile(final String basename, final String ext, final int Card, int oneFilePerDay) {
        super.initialiseFile(basename, ext, Card, oneFilePerDay);
        m_isWayType=false;
    }
     
    public void writeFileHeader(final String s) {
        super.writeFileHeader(s);
        writeTxt("G  WGS 84\r\n"  // WGS 84
                +"U  1\r\n"       // LAT .LON FORMAT
                +"M  Generated using BT747 http://sf.net/projects/bt747 for CompeGPS\r\n"
                );
        //"NSAT (USED/VIEW),SAT INFO (SID-ELE-AZI-SNR)
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected boolean recordIsNeeded(GPSRecord s) {
        return m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s);
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
    private StringBuffer rec=new StringBuffer(1024);
    private StringBuffer wrec=new StringBuffer(1024);

    public void writeRecord(final GPSRecord s) {
        super.writeRecord(s);
        boolean prevField=false;
        //rec+=Convert.toString(m_recCount);
        boolean trackpt;
        boolean waypt;
        trackpt=!m_isWayType&&m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s);
        waypt=m_isWayType&&m_Filters[GPSFilter.C_WAYPT_IDX].doFilter(s);
        
        if(activeFields!=null && (trackpt || waypt) ) {
            rec.setLength(0);
            
            rec.append("T  A ");

            if(activeFields.latitude!=0) {
                if(s.latitude>=0) {
                    rec.append(Convert.toString(s.latitude,8)+(char)0xBA+"N");
                } else {
                    rec.append(Convert.toString(-s.latitude,8)+(char)0xBA+"S");
                }
            } else if(activeFileFields.latitude!=0) {
                rec.append("0�N");
            }

            rec.append(" ");
            if(activeFields.longitude!=0) {
                if(s.longitude>=0) {
                    rec.append(Convert.toString(s.longitude,8)+(char)0xBA+"E");
                } else {
                    rec.append(Convert.toString(-s.longitude,8)+(char)0xBA+"W");
                }
            } else if(activeFileFields.longitude!=0) {
                rec.append("0�E");
            }
            rec.append(" ");

            if(activeFields.utc!=0) {
//                rec.append(Convert.toString(
//                        (s.utc+(activeFields.milisecond!=0?(s.milisecond/1000.0):0))
//                        /86400.0+25569,  //Days since 30/12/1899
//                        7));  // 7 fractional digits
//                rec.append(",");
                rec.append(
                    (   t.getDay()<10?"0":"")+Convert.toString(t.getDay())+"-"
                    +C_MONTHS[t.getMonth()-1]+"-"
                    +((t.getYear()%100)<10?"0":"")+Convert.toString(t.getYear()%100)
                    +" "
                    +(  t.getHour()<10?"0":"")+Convert.toString(t.getHour())+":"
                    +(t.getMinute()<10?"0":"")+Convert.toString(t.getMinute())+":"
                    +(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
                    );
//                if(activeFields.milisecond!=0) {
//                    rec+=".";
//                    rec+=(s.milisecond<100)?"0":"";
//                    rec+=(s.milisecond<10)?"0":"";
//                    rec+=Convert.toString(s.milisecond);
//                }
                rec.append(" ");
            } else {
                rec.append("01-JAN-70 00:00:00 ");
            }

            if(waypt) {
                wrec.setLength(0);
                wrec.append("W  ");
                wrec.append("waypt-"+m_recCount); // name
                wrec.append(rec.substring(2));
            }
            rec.append("s ");
            
            

            if(activeFields.height!=0) {
                rec.append(Convert.toString(s.height,1));
                if(waypt) {
                    wrec.append(Convert.toString(s.height,1));
                }
            } else {
                rec.append("0.0");
                if(waypt) {
                    wrec.append("0.0");
                }
            }
            rec.append(" 0.0 0.0 0.0 0 -1000.0 -1.0 ");
//            if(waypt) {
//                wrec.append("Description")
//            }
            if(activeFields.nsat!=0) {
                rec.append((s.nsat&0xFF00)>>8);  // in use
                rec.append(" ");
            } else {
                rec.append("-1 ");
            }
            rec.append("-1.0 -1.0");

            rec.append("\r\n");
            if(!trackpt) {
                rec.setLength(0);
            }
            if(waypt) {
              rec.append(wrec);
              rec.append("\r\n");
            }
            writeTxt(rec.toString());
            rec.setLength(0);
        } // activeFields!=null
    }    

    public boolean nextPass() {
        super.nextPass();
        if(!m_isWayType) {
            m_recCount=0;
            m_prevdate=0;
            m_isWayType=true;
            m_ext=".WPT";
            return true;
        } else {
            return false;
        }
    }


//    public void finaliseFile() {
//        if(m_File!=null) {
//            String footer;
//            //writeDataFooter();
//            footer= "F  1234";
//            writeTxt(footer);
//        }
//        super.finaliseFile();
//        
//    }
}
