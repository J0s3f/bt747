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
import waba.sys.Time;

/**Class to write a CSV file.
 * @author Mario De Weerd
 */
public class GPSCSVFile extends GPSFile {
    public void writeLogFmtHeader(final GPSRecord f) {
        super.writeLogFmtHeader(f);
        //INDEX,RCR,DATE,TIME,VALID,LATITUDE,N/S,LONGITUDE,E/W,HEIGHT,SPEED,
        writeTxt("INDEX");
        if(activeFields.rcr!=0) {
            writeTxt(",RCR");
        }
        if(activeFields.utc!=0) {
            writeTxt(",DATE,TIME");
        }
        if(activeFields.valid!=0) {
            writeTxt(",VALID");
        }
        if(activeFields.latitude!=0) {
            writeTxt(",LATITUDE,N/S");
        }
        if(activeFields.longitude!=0) {
            writeTxt(",LONGITUDE,E/W");
        }
        if(activeFields.height!=0) {
            writeTxt(",HEIGHT(m)");
        }
        if(activeFields.speed!=0) {
            writeTxt(",SPEED(km/h)");
        }
        if(activeFields.heading!=0) {
            writeTxt(",HEADING");
        }
        if(activeFields.dsta!=0) {
            writeTxt(",DSTA");
        }
        if(activeFields.dage!=0) {
            writeTxt(",DAGE");
        }
        if(activeFields.pdop!=0) {
            writeTxt(",PDOP");
        }
        if(activeFields.hdop!=0) {
            writeTxt(",HDOP");
        }
        if(activeFields.vdop!=0) {
            writeTxt(",VDOP");
        }
        if(activeFields.nsat!=0) {
            writeTxt(",NSAT (USED/VIEW)");
        }
        // SAT INFO NOT HANDLED
//        if(activeFields.milisecond!=0) {
//            writeTxt(",MILISECOND");
//        }
        if(activeFields.distance!=0) {
            writeTxt(",DISTANCE(m)");
        }
        writeTxt(",");
        writeTxt("\r\n");
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
    
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(GPSRecord s) {
        super.writeRecord(s);
        boolean prevField=false;
        
        if(activeFields!=null && m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s)) {
            String rec=Convert.toString(m_recCount);
            if(activeFields.rcr!=0) {
                rec+=",";
                if((s.rcr&BT747_dev.RCR_TIME_MASK)!=0) {
                    rec+="T";
                }
                if((s.rcr&BT747_dev.RCR_SPEED_MASK)!=0) {
                    rec+="S";
                }
                if((s.rcr&BT747_dev.RCR_DISTANCE_MASK)!=0) {
                    rec+="D";
                }
                if((s.rcr&BT747_dev.RCR_BUTTON_MASK)!=0) {
                    rec+="B";
                }
            }
            if(activeFields.utc!=0) {
                Time t=utcTime(s.utc);
                
                
                rec+=","+Convert.toString(t.year)+"/"
                +( t.month<10?"0":"")+Convert.toString(t.month)+"/"
                +(   t.day<10?"0":"")+Convert.toString(t.day)+","
                +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
                +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
                +(t.second<10?"0":"");
                if(activeFields.milisecond==0) {
                    rec+=Convert.toString(t.second);
                } else {
                    rec+=Convert.toString((float)t.second+s.milisecond/1000.0,3);
                }
            }
            if(activeFields.valid!=0) {
                rec+=",";
                switch(s.valid) {
                case 0x0001: 
                    rec+="No fix";
                    break;
                case 0x0002:
                    rec+= "SPS";
                    break;
                case 0x0004:
                    rec+="DGPS";
                    break;
                case 0x0008:
                    rec+="PPS";
                    break;
                case 0x0010:
                    rec+="RTK";
                    break;
                case 0x0020:
                    rec+="FRTK";
                    break;
                case 0x0040:
                    rec+= "Estimated mode";
                    break;
                case 0x0080:
                    rec+= "Manual input mode";
                    break;
                case 0x0100:
                    rec+= "Simulator mode";
                    break;
                default:
                    rec+="Unknown mode";
                }
            }
            if(activeFields.latitude!=0) {
                rec+=",";
                rec+=Convert.toString(s.latitude,6);
                if(s.latitude>=0) {
                    rec+=",N";
                } else {
                    rec+=",S";
                }
            }
            if(activeFields.longitude!=0) {
                rec+=",";
                rec+=Convert.toString(s.longitude,6);
                if(s.longitude>=0) {
                    rec+=",E";
                } else {
                    rec+=",W";
                }
            }
            if(activeFields.height!=0) {
                rec+=",";
                rec+=Convert.toString(s.height,3);
            }
            if(activeFields.speed!=0) {
                rec+=",";
                rec+=Convert.toString(s.speed,3);
            }
            if(activeFields.heading!=0) {
                rec+=",";
                rec+=Convert.toString(s.heading,6);
            }
            if(activeFields.dsta!=0) {
                rec+=",";
                rec+=Convert.toString(s.dsta); 
            }
            if(activeFields.dage!=0) {
                rec+=",";
                rec+=Convert.toString(s.dage); 
            }
            if(activeFields.pdop!=0) {
                rec+=",";
                rec+=Convert.toString(s.pdop/100.0,2); 
            }
            if(activeFields.hdop!=0) {
                rec+=",";
                rec+=Convert.toString(s.hdop/100.0,2); 
            }
            if(activeFields.vdop!=0) {
                rec+=",";
                rec+=Convert.toString(s.vdop/100.0,2); 
            }
            if(activeFields.nsat!=0) {
                rec+=",";
                rec+=Convert.toString((s.nsat&0xFF00)>>8); 
                rec+="("+Convert.toString(s.nsat&0xFF)+")"; 
            }
            if(activeFields.distance!=0) {
                rec+=",";
                rec+=Convert.toString(s.distance,2);
            }
            rec+=",";
            rec+="\r\n";
            writeTxt(rec);
        } // activeFields!=null
    }
    
}