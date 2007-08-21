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

import waba.sys.Convert;

/**Class to write a GPX file.
 * @author Mario De Weerd
 */
public class GPSNMEAFile extends GPSFile {
    private StringBuffer rec=new StringBuffer(1024);  // reused stringbuffer
    
    private void writeNMEA(final String s) {
        int z_Checksum=0;
        for (int i = rec.length()-1; i >= 0 ; i--) {
            z_Checksum^=(byte)s.charAt(i);
        }
        writeTxt("$");
        writeTxt(s);
        writeTxt("*"+Convert.unsigned2hex(z_Checksum,2)+"\r\n");
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected boolean recordIsNeeded(GPSRecord s) {
        return m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s);
    }

    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(GPSRecord s) {
        super.writeRecord(s);
        boolean prevField=false;
        String timeStr="";
        String dateStr;
        if(activeFields!=null && m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s)) {
            int z_Checksum;
    
            if((activeFields.utc!=0)) {
                /* Write GPZDA sentence if time is available
                 */
                rec.setLength(0);
                rec.append("GPZDA,");
                
                // DATE & TIME
                t=utcTime(s.utc);
                
                timeStr=(  t.hour<10?"0":"")+Convert.toString(t.hour)
                        +(t.minute<10?"0":"")+Convert.toString(t.minute)
                        +(t.second<10?"0":"")+Convert.toString(t.second);
                if(activeFields.milisecond!=0) {
                    timeStr+="."+
                    ((s.milisecond<100)?"0":"")
                    +((s.milisecond<10)?"0":"")
                    +(Convert.toString(s.milisecond));
                } 
                rec.append(timeStr);
                rec.append(
                        ","
                        +(   t.day<10?"0":"")+Convert.toString(t.day)+","
                        +( t.month<10?"0":"")+Convert.toString(t.month)+","
                        +Convert.toString(t.year)+",,"
                );

                writeNMEA(rec.toString());
            }


            
            // FIX

            rec.setLength(0);
            rec.append("GPGGA,");
            
            if((activeFields.utc!=0)) {
                rec.append(timeStr);
            }
            rec.append(",");

            if(activeFields.latitude!=0) {
                String sl;
                double l; 
                if(s.latitude>=0) {
                    sl = ",N,";
                    l=s.latitude;
                } else {
                    sl = ",S,";
                    l=-s.latitude;
                }
                int a = (int)Math.floor(l);
                rec.append( 
                        ((a<10)?"0":"")
                        +a);
                l -= a;
                l *= 60;
                // TODO: check if bug when a number like 9.9999999
                rec.append( ((l<10)?"0":"")
                        + Convert.toString(l,4)
                        );
                rec.append(sl);
            } else {
                rec.append(",,");
            }
            
            if(activeFields.longitude!=0) {
                String sl;
                double l; 
                if(s.longitude>=0) {
                    sl = ",E,";
                    l=s.longitude;
                } else {
                    sl = ",W,";
                    l=-s.longitude;
                }
                int a = (int)Math.floor(l);
                rec.append( ((a<100)?"0":"")
                        +((a<10)?"0":"")
                        +a);
                l -= a;
                l *= 60;
                // TODO: check if bug when a number like 9.9999999
                rec.append( ((l<10)?"0":"")
                        + Convert.toString(l,4)
                );
                rec.append(sl);
            } else {
                rec.append(",,");
            }
    
            //              - 1 is the fix quality. The fix quality can have a value between 0 and 3, defined as follows
            //              - 0=no fix
            //              - 1=GPS or standard positioning service (SPS) fix
            //              - 2=DGPS fix
            //              - 3=Precise positioning service (PPS) fix
            
            if((activeFields.valid!=0)) {
                String tmp="";
                switch(s.valid) {
                case 0x0001: 
                    tmp="0"; //"No fix";
                    break;
                case 0x0002:
                    tmp="1"; //"SPS";
                    break;
                case 0x0004:
                    tmp="2"; // DGPS
                    break;
                case 0x0008:
                    tmp="3"; // PPS, Military signal
                    break;
                case 0x0010:
                    tmp="4";
                    break;
                case 0x0020:
                    tmp="5";
                    break;
                case 0x0040:
                    //tmp+= "Estimated mode";
                    break;
                case 0x0080:
                    //tmp+= "Manual input mode";
                    break;
                case 0x0100:
                    //tmp+= "Simulator mode";
                    break;
                default:
                    //tmp+="Unknown mode";
                }
                rec.append(tmp);
            }
            rec.append(",");
            
            //          - 08 is the number of SV's being tracked
            if(activeFields.nsat!=0) {
                rec.append(Convert.toString((s.nsat&0xFF00)>>8)); 
                //rec+="("+Convert.toString(s.nsat&0xFF)+")"; 
            }
            rec.append(",");
            
            //          - 0.9 is the horizontal dilution of position (HDOP)
            if(activeFields.hdop!=0) {
                rec.append(Convert.toString(s.hdop/100.0,2)); 
            }
            rec.append(",");
            //          - 133.4,M is the altitude, in meters, above mean sea level
            if((activeFields.height!=0)) {
                rec.append(Convert.toString(s.height,3));
                rec.append(",M,0,M,");
            } else {
                rec.append(",,,,");
            }
            //          - 46.9,M is the height of the geoid (mean sea level) above the WGS84 ellipsoid
            //          - (empty field) is the DGPS station ID number
            //          - *42 is the checksum field
            
            if((activeFields.dage!=0)) {
                rec.append(Convert.toString(s.dage)); 
            }
            rec.append(",");
            
            if((activeFields.dsta!=0)) {
                rec.append(Convert.toString(s.dsta)); 
            }
            
            writeNMEA(rec.toString());
            
//            eg4. $GPRMC,hhmmss.ss,A,llll.ll,a,yyyyy.yy,a,x.x,x.x,ddmmyy,x.x,a*hh
//            1    = UTC of position fix
//            2    = Data status (V=navigation receiver warning)
//            3    = Latitude of fix
//            4    = N or S
//            5    = Longitude of fix
//            6    = E or W
//            7    = Speed over ground in knots
//            8    = Track made good in degrees True
//            9    = UT date
//            10   = Magnetic variation degrees (Easterly var. subtracts from true course)
//            11   = E or W
//            12   = Checksum

            rec.setLength(0);
            rec.append("GPRMC,");
            
            if((activeFields.utc!=0)) {
//              1    = UTC of position fix
                rec.append(timeStr);
            }
            
//          2    = Data status (V=navigation receiver warning)
            if((activeFields.valid!=0)) {
                String tmp;
                switch(s.valid) {
                case 0x0001: 
                    tmp=",V,"; //"No fix";
                    break;
                default:
                    tmp=",A,";
                }
                rec.append(tmp);
            } else {
                rec.append(",,");
            }

//          3    = Latitude of fix
//          4    = N or S
            if(activeFields.latitude!=0) {
                String sl;
                double l; 
                if(s.latitude>=0) {
                    sl = ",N,";
                    l=s.latitude;
                } else {
                    sl = ",S,";
                    l=-s.latitude;
                }
                int a = (int)Math.floor(l);
                rec.append( 
                        ((a<10)?"0":"")
                        +a);
                l -= a;
                l *= 60;
                // TODO: check if bug when a number like 9.9999999
                rec.append( ((l<10)?"0":"")
                        + Convert.toString(l,4)
                        );
                rec.append(sl);
            } else {
                rec.append(",,");
            }
            
//          5    = Longitude of fix
//          6    = E or W
            if(activeFields.longitude!=0) {
                String sl;
                double l; 
                if(s.longitude>=0) {
                    sl = ",E,";
                    l=s.longitude;
                } else {
                    sl = ",W,";
                    l=-s.longitude;
                }
                int a = (int)Math.floor(l);
                rec.append( ((a<100)?"0":"")
                        +((a<10)?"0":"")
                        +a);
                l -= a;
                l *= 60;
                // TODO: check if bug when a number like 9.9999999
                rec.append( ((l<10)?"0":"")
                        + Convert.toString(l,4)
                );
                rec.append(sl);
            } else {
                rec.append(",,");
            }
            
//          7    = Speed over ground in knots
//          8    = Track made good in degrees True
//          9    = UT date
//          10   = Magnetic variation degrees (Easterly var. subtracts from true course)
//          11   = E or W
//          12   = Checksum
            if(activeFields.speed!=0) {
                rec.append(Convert.toString(s.speed*1.945945945945946,2));
            }
            rec.append(",");
            if(activeFields.heading!=0) {
                rec.append(Convert.toString(s.heading,6));
            }
            rec.append(",");
            if((activeFields.utc!=0)) {
                // DATE & TIME
                rec.append(
                         (   t.day<10?"0":"")+Convert.toString(t.day)
                        +( t.month<10?"0":"")+Convert.toString(t.month)
                        +(((t.year%100)<10)?"0":"")+Convert.toString(t.year%100)
                );
            }
            rec.append(",,");
    
            writeNMEA(rec.toString());
        } // activeFields!=null
    }
}
