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

/**Class to write a CSV file.
 * @author Mario De Weerd
 */
public class GPSCSVFile implements GPSFile {
    File m_File=null;
    int m_recCount;
    private GPSRecord activeFields;
    
    /**
     * 
     */
    public GPSCSVFile() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(String basename, String ext, GPSRecord f) {
        // TODO Auto-generated method stub
        if(File.isAvailable()) {
            m_File=new File(basename+ext);
            if(m_File.exists()) {
                m_File.delete();
            }
            m_File=new File(basename+ext,File.CREATE|File.WRITE_ONLY);
            if(!m_File.isOpen()) {
                m_File=null;
            } else {
                activeFields= new GPSRecord(f);
                m_recCount=0;
                writeHeader();
            }
        }
    }
    
    public void writeHeader() {
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
            writeTxt(",HEIGHT");
        }
        if(activeFields.speed!=0) {
            writeTxt(",SPEED");
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
        if(activeFields.milisecond!=0) {
            writeTxt(",MILISECOND");
        }
        if(activeFields.distance!=0) {
            writeTxt(",DISTANCE");
        }
        writeTxt(",");
        writeTxt("\r\n");
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
    
    // TODO: Should do something similar for  double 
    private final String floatToString(final float f) {
        String s="";
        if(((f>=0.0)&&(f<1.0))||((f<0.0)&&(f>-1.0))) {
            float myf = 0;
            if (myf<0.0) {
                myf=-f;
                s="-";
            } else {
                myf=f;
            }
            int m=(int)(myf*1000.0+0.499999999999);
            
            if(m==0) {
                s+="0.000";
            } else if (m <10) {
                s+="0.00";
                s+=Convert.toString(m);
            } else if (m <100) {
                s+="0.0";
                s+=Convert.toString(m);
            } else {
                s+="0."+Convert.toString(m);
            }
        } else {
            s=Convert.toString(f,3);
        }
        return s;
    }
    
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(GPSRecord s) {
        boolean prevField=false;
        String rec="";
        m_recCount++;
        rec+=Convert.toString(m_recCount);
        
        if(activeFields!=null) {
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
                +(t.second<10?"0":"")+Convert.toString(t.second)
                ;
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
                if(s.latitude>=0) {
                    rec+=Convert.toString(s.latitude,6);
                    rec+=",N";
                } else {
                    rec+=Convert.toString(-s.latitude,6);
                    rec+=",S";
                }
            }
            if(activeFields.longitude!=0) {
                rec+=",";
                if(s.longitude>=0) {
                    rec+=Convert.toString(s.longitude,6);
                    rec+=",E";
                } else {
                    rec+=Convert.toString(-s.longitude,6);
                    rec+=",W";
                }
            }
            if(activeFields.height!=0) {
                rec+=",";
                rec+=floatToString(s.height)+" m";
            }
            if(activeFields.speed!=0) {
                rec+=",";
                rec+=floatToString(s.speed)+" km/h";
            }
            if(activeFields.heading!=0) {
                rec+=",";
                rec+=Convert.toString(s.heading);
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
                rec+=Convert.toString(s.pdop); 
            }
            if(activeFields.hdop!=0) {
                rec+=",";
                rec+=Convert.toString(s.hdop); 
            }
            if(activeFields.vdop!=0) {
                rec+=",";
                rec+=Convert.toString(s.vdop); 
            }
            if(activeFields.milisecond!=0) {
                rec+=",";
                rec+=Convert.toString(s.milisecond); 
            }
            if(activeFields.distance!=0) {
                rec+=",";
                rec+=Convert.toString(s.distance); 
            }
            rec+=",";
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
