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

/**Class to write a KML file.
 * @author Mario De Weerd
 */
public class GPSKMLFile extends GPSFile {
    File m_File=null;
    int m_recCount;
    private GPSRecord activeFields;
    
    public boolean nextPass() {
        return false;
    }    
    /**
     * 
     */
    public GPSKMLFile() {
        super();
        // TODO Auto-generated constructor stub
    }

    GPSFilter[] m_Filters=null;
    public void setFilters(GPSFilter[] filters) {
        m_Filters=filters;
    }
    
    /* (non-Javadoc)
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext, final int Card, boolean oneFilePerDay) {
        // TODO Auto-generated method stub
        m_File=new File(basename+ext,File.DONT_OPEN,Card);
        if(m_File.exists()) {
            m_File.delete();
        }
        m_File=new File(basename+ext,File.CREATE,Card);
        if(!m_File.isOpen()) {
            waba.sys.Vm.debug("Could not open "+basename+ext);
            m_File=null;
        } else {
            m_recCount=0;
            writeHeader();
        }
    }
    public void writeLogFmtHeader(final GPSRecord f) {
        activeFields= new GPSRecord(f);
    }
    
    public void writeHeader() {
        String header;
        header ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"+
        "<kml xmlns=\"http://earth.google.com/kml/2.0\">\r\n"+
        "<Document>\r\n"+
        "  <name>i-Blue 747</name>\r\n"+
        "    <open>1</open>\r\n"+
        "  <Style id=\"TimeStamp0\">\r\n"+
        "    <IconStyle>\r\n"+
        "      <Icon>\r\n"+
        "        <href>root://icons/palette-3.png</href>\r\n"+
        "        <x>128</x>\r\n"+
        "        <y>128</y>\r\n"+
        "        <w>32</w>\r\n"+
        "        <h>32</h>\r\n"+
        "      </Icon>\r\n"+
        "    </IconStyle>\r\n"+
        "    <LabelStyle>\r\n"+
        "      <scale>0</scale>\r\n"+
        "    </LabelStyle>\r\n"+
        "  </Style>\r\n"+
        "  <Style id=\"TimeStamp1\">\r\n"+
        "    <IconStyle>\r\n"+
        "      <Icon>\r\n"+
        "        <href>root://icons/palette-3.png</href>\r\n"+
        "        <x>128</x>\r\n"+
        "        <y>128</y>\r\n"+
        "        <w>32</w>\r\n"+
        "        <h>32</h>\r\n"+
        "      </Icon>\r\n"+
        "    </IconStyle>\r\n"+
        "    <LabelStyle>\r\n"+
        "      <scale>1</scale>\r\n"+
        "    </LabelStyle>\r\n"+
        "  </Style>\r\n"+
        "  <StyleMap id=\"StyleT\">\r\n"+
        "    <Pair>\r\n"+
        "      <key>normal</key>\r\n"+
        "      <styleUrl>#TimeStamp0</styleUrl>\r\n"+
        "    </Pair>\r\n"+
        "    <Pair>\r\n"+
        "      <key>highlight</key>\r\n"+
        "      <styleUrl>#TimeStamp1</styleUrl>\r\n"+
        "    </Pair>\r\n"+
        "  </StyleMap>\r\n"+
        "    <Style id=\"DistanceStamp0\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-2.png</href>\r\n"+
        "          <x>160</x>\r\n"+
        "          <y>192</y>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>0</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <Style id=\"DistanceStamp1\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-2.png</href>\r\n"+
        "          <x>160</x>\r\n"+
        "          <y>192</y>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>1</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <StyleMap id=\"StyleD\">\r\n"+
        "      <Pair>\r\n"+
        "        <key>normal</key>\r\n"+
        "        <styleUrl>#DistanceStamp0</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "      <Pair>\r\n"+
        "        <key>highlight</key>\r\n"+
        "        <styleUrl>#DistanceStamp1</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "    </StyleMap>\r\n"+
        "    <Style id=\"SpeedStamp0\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-4.png</href>\r\n"+
        "          <x>224</x>\r\n"+
        "          <y>192</y>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>0</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <Style id=\"SpeedStamp1\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-4.png</href>\r\n"+
        "          <x>224</x>\r\n"+
        "          <y>192</y>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>1</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <StyleMap id=\"StyleS\">\r\n"+
        "      <Pair>\r\n"+
        "        <key>normal</key>\r\n"+
        "        <styleUrl>#SpeedStamp0</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "      <Pair>\r\n"+
        "        <key>highlight</key>\r\n"+
        "        <styleUrl>#SpeedStamp1</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "    </StyleMap>\r\n"+
        "    <Style id=\"Unknown0\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-4.png</href>\r\n"+
        "          <x>224</x>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>0</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <Style id=\"Unknown1\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-4.png</href>\r\n"+
        "          <x>224</x>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>1</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <StyleMap id=\"StyleU\">\r\n"+
        "      <Pair>\r\n"+
        "        <key>normal</key>\r\n"+
        "        <styleUrl>#Unknown0</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "      <Pair>\r\n"+
        "        <key>highlight</key>\r\n"+
        "        <styleUrl>#Unknown1</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "    </StyleMap>\r\n"+
        "    <Style id=\"MixStamp0\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-4.png</href>\r\n"+
        "          <x>192</x>\r\n"+
        "          <y>64</y>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>0</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <Style id=\"MixStamp1\">\r\n"+
        "      <IconStyle>\r\n"+
        "        <Icon>\r\n"+
        "          <href>root://icons/palette-4.png</href>\r\n"+
        "          <x>192</x>\r\n"+
        "          <y>64</y>\r\n"+
        "          <w>32</w>\r\n"+
        "          <h>32</h>\r\n"+
        "        </Icon>\r\n"+
        "      </IconStyle>\r\n"+
        "      <LabelStyle>\r\n"+
        "        <scale>1</scale>\r\n"+
        "      </LabelStyle>\r\n"+
        "    </Style>\r\n"+
        "    <StyleMap id=\"StyleM\">\r\n"+
        "      <Pair>\r\n"+
        "        <key>normal</key>\r\n"+
        "        <styleUrl>#MixStamp0</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "      <Pair>\r\n"+
        "        <key>highlight</key>\r\n"+
        "        <styleUrl>#MixStamp1</styleUrl>\r\n"+
        "      </Pair>\r\n"+
        "    </StyleMap>\r\n"+
        "  <Folder>\r\n"+
        "    <name>My Places</name>\r\n"+
        "    <open>0</open>\r\n";
        writeTxt(header);
    }
    
    private final static int DAYS_BETWEEN_1970_1983=4748;
    public Time utcTime(final int utc_int) {
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
    
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(GPSRecord s) {
        boolean prevField=false;
        m_recCount++;
        if(activeFields!=null) {
            if(m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s)) {
                String rec="";
                
                rec+="<Placemark>\r\n";
                
                rec+="<name>";
                if(activeFields.utc!=0) {
                    Time t=utcTime(s.utc);
                    
                    rec+="TIME: "
                        +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
                        +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
                        +(t.second<10?"0":"")+Convert.toString(t.second)
                        ;
                } else {
                    rec+="IDX: ";
                    rec+=Convert.toString(m_recCount);
                }
                rec+="</name>\r\n";
                
                
            if((activeFields.utc!=0)) {
                rec+="<TimeStamp><when>";
                if(activeFields.utc!=0) {
                    Time t=utcTime(s.utc);
                    
                    rec+=Convert.toString(t.year)+"-"
                    +( t.month<10?"0":"")+Convert.toString(t.month)+"-"
                    +(   t.day<10?"0":"")+Convert.toString(t.day)+"T"
                    +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
                    +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
                    +(t.second<10?"0":"")
                    ;
                    if(activeFields.milisecond==0) {
                        rec+=Convert.toString(t.second);
                    } else {
                        rec+=Convert.toString((float)t.second+s.milisecond/1000.0,3);
                    }
                    rec+="Z";
                }
                rec+="</when></TimeStamp>\r\n";
            }

                rec+="<styleUrl>";
                if(activeFields.rcr!=0) {
                    String style="";
                    if((s.rcr&BT747_dev.RCR_TIME_MASK)!=0) {
                        style+="T";
                    }
                    if((s.rcr&BT747_dev.RCR_SPEED_MASK)!=0) {
                        style+="S";
                    }
                    if((s.rcr&BT747_dev.RCR_DISTANCE_MASK)!=0) {
                        style+="D";
                    }
                    if((s.rcr&BT747_dev.RCR_BUTTON_MASK)!=0) {
                        style+="B";
                    }
                    if(style.length()!=1) {
                        style="M";
                    }
                    rec+="#Style";
                    rec+=style;
                }
                rec+="</styleUrl>\r\n";
                
                
                rec+="<Point>\n";
                rec+="<coordinates>";
                if(activeFields.longitude!=0) {
                    rec+=Convert.toString(s.longitude,6);
                } else {
                    rec+="0";
                }
                rec+=",";
                if(activeFields.latitude!=0) {
                    rec+=Convert.toString(s.latitude,6);
                } else {
                    rec+="0";
                }
                rec+=",";
                if(activeFields.height!=0) {
                    rec+=Convert.toString(s.height,3);
                }
                rec+="</coordinates>";
                rec+="</Point>\n";
                
                rec+="<description>";
                rec+="<![CDATA[";
                if(activeFields.rcr!=0) {
                    rec+="RCR: ";
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
//                if(activeFields.utc!=0) {
//                    Time t=utcTime(s.utc);
//                    
//                    rec+="<br />DATE: ";
//                    rec+=Convert.toString(t.year)+"/"
//                    +( t.month<10?"0":"")+Convert.toString(t.month)+"/"
//                    +(   t.day<10?"0":"")+Convert.toString(t.day)+"<br />"
//                    +"TIME: "
//                    +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
//                    +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
//                    +(t.second<10?"0":"")+Convert.toString(t.second)
//                    ;
//                }
                if(activeFields.valid!=0) {
                    rec+="<br />VALID: ";
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
                    rec+="<br />LATITUDE: ";
                    if(s.latitude>=0) {
                        rec+=Convert.toString(s.latitude,6);
                        rec+=" N";
                    } else {
                        rec+=Convert.toString(-s.latitude,6);
                        rec+=" S";
                    }
                }
                if(activeFields.longitude!=0) {
                    rec+="<br />LONGITUDE: ";
                    if(s.longitude>=0) {
                        rec+=Convert.toString(s.longitude,6);
                        rec+=" E";
                    } else {
                        rec+=Convert.toString(-s.longitude,6);
                        rec+=" W";
                    }
                }
                if(activeFields.height!=0) {
                    rec+="<br />HEIGHT: ";
                    rec+=Convert.toString(s.height,3)+" m";
                }
                if(activeFields.speed!=0) {
                    rec+="<br />SPEED: ";
                    rec+=Convert.toString(s.speed,3)+" km/h";
                }
                if(activeFields.heading!=0) {
                    rec+="<br />HEADING: ";
                    rec+=Convert.toString(s.heading);
                }
                if(activeFields.dsta!=0) {
                    rec+="<br />DSTA: ";
                    rec+=Convert.toString(s.dsta); 
                }
                if(activeFields.dage!=0) {
                    rec+="<br />DAGE: ";
                    rec+=Convert.toString(s.dage); 
                }
                if(activeFields.pdop!=0) {
                    rec+="<br />PDOP: ";
                    rec+=Convert.toString(s.pdop/100.0,2); 
                }
                if(activeFields.hdop!=0) {
                    rec+="<br />HDOP: ";
                    rec+=Convert.toString(s.hdop/100.0,2); 
                }
                if(activeFields.vdop!=0) {
                    rec+="<br />VDOP: ";
                    rec+=Convert.toString(s.vdop/100.0,2); 
                }
                if(activeFields.distance!=0) {
                    rec+="<br />DISTANCE: ";
                    rec+=Convert.toString(s.distance,2); 
                }
                
                rec+="]]>";
                rec+="</description>";
                rec+="</Placemark>\r\n";
                
                rec+="\r\n";
                writeTxt(rec);
                
            }
        } // activeFields!=null
    }
    
    /* (non-Javadoc)
     * @see gps.GPSFile#FinaliseFile()
     */
    public void finaliseFile() {
        // TODO Auto-generated method stub
        if(m_File!=null) {
            String footer;
            footer="  </Folder>\r\n"+
            "</Document>\r\n"+
            "</kml>";
            writeTxt(footer);
            m_File.close();
            m_File=null;
        }
        
    }
    
}