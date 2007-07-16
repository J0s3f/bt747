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
    public void initialiseFile(String basename, String ext) {
        // TODO Auto-generated method stub
        m_File=new File(basename+ext);
        if(m_File.exists()) {
            m_File.delete();
        }
        m_File=new File(basename+ext,File.CREATE);
        if(!m_File.isOpen()) {
            waba.sys.Vm.debug("Could not open "+basename+ext);
            m_File=null;
        } else {
            m_recCount=0;
        }
    }
    
    public void writeLogFmtHeader(final GPSRecord f) {
        activeFields= new GPSRecord(f);
        writeTxt("Datum, WGS 84\r\n");
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
        m_recCount++;
        //rec+=Convert.toString(m_recCount);
        
        if(activeFields!=null && m_Filter.doFilter(s)) {
            String rec="TP,UTM,56J,";

            if(activeFields.latitude!=0) {
                rec+=Convert.toString(s.latitude,6);
            }
            rec+=",";
            if(activeFields.longitude!=0) {
                rec+=Convert.toString(s.longitude,6);
            }
            rec+=",";
            if(activeFields.utc!=0) {
                Time t=utcTime(s.utc);
                
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
            }
            rec+=",";
            if(activeFields.height!=0) {
                rec+=floatToString(s.height);
            }
            rec+=",0,"; // Track section
            if(activeFields.distance!=0) {
                rec+=Convert.toString(s.distance); 
            }
            rec+=",";
            //Time
            rec+=",";
            if(activeFields.speed!=0) {
                rec+=floatToString(s.speed);
            }
            rec+=",";

            if(activeFields.heading!=0) {
                rec+=Convert.toString(s.heading);
            }
//            rec+=",";

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
