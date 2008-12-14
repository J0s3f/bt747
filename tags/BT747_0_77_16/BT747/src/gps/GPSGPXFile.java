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

/**Class to write a GPX file.
 * @author Mario De Weerd
 */
public class GPSGPXFile implements GPSFile {
    private File m_File=null;
    private int m_recCount;
    private int m_nbrOfPassesToGo;
    private String m_pttype;
    private boolean m_isWayType;
    private GPSRecord activeFields;
    private boolean m_newTrack=true;
    private StringBuffer rec=new StringBuffer(1024);  // reused stringbuffer
    
    /**
     * 
     */
    public GPSGPXFile() {
        super();
        // TODO Auto-generated constructor stub
    }

    GPSFilter m_Filter=null;
    
    public void setFilter(GPSFilter filter) {
        m_Filter=filter;
    }

    /* (non-Javadoc)
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext, final int Card) {
        // TODO Auto-generated method stub
        m_File=new File(basename+ext, File.DONT_OPEN, Card);
        if(m_File.exists()) {
            m_File.delete();
        }
        m_File=new File(basename+ext,File.CREATE,Card);
        if(!m_File.isOpen()) {
            waba.sys.Vm.debug("Could not open "+basename+ext);
            m_File=null;
        } else {
            m_recCount=0;
            m_nbrOfPassesToGo=1;
            m_pttype="way";
            m_isWayType=true;
            writeFileHeader();
            writeDataHeader();
        }
    }
    
    public void writeLogFmtHeader(final GPSRecord f) {
        activeFields= new GPSRecord(f);
    }
    
    public boolean nextPass() {
        if(m_nbrOfPassesToGo>0) {
            m_nbrOfPassesToGo--;
            m_pttype="trk";
            m_isWayType=false;
            writeDataHeader();
            return true;
        } else {
            return false;
        }
    }
    public void writeFileHeader() {
        String header;
        header ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"+
        "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"BT747\" version=\"1.0\" "+
        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
        "    xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
        writeTxt(header);
    }
    
    public void writeDataHeader() {
        String header;
        if(m_isWayType) {
        } else {
            header=
                "<trk>"+
                "<trkseg>"+
                "\r\n";
            m_newTrack=true;
            writeTxt(header);
        }
    }
    
    public void writeDataFooter() {
        String header;
        if(m_isWayType) {
        } else {
            header=
                "</trkseg>"+
                "</trk>"+
                "\r\n";
            writeTxt(header);
        }
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
       
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(GPSRecord s) {
        boolean prevField=false;
        m_recCount++;
        if(activeFields!=null) {
            if(!m_Filter.doFilter(s)) {
                // filtered
                if(!m_isWayType&&!m_newTrack) {
                    m_newTrack=true;
                    writeTxt("</trkseg></trk><trk><trkseg>");
                }
            } else {
                //StringBuffer rec=new StringBuffer(1024);
                rec.setLength(0);
                m_newTrack=false;
                //                "  <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
                //                "    <ele>12.863281</ele>"+
                //                "    <time>2005-05-16T11:49:06Z</time>"+
                //                "    <name>Cala Sant Vicen� - Mallorca</name>"+
                //                "    <sym>City</sym>"+
                //                "  </wpt>"+
                
                // lat="latitudeType [1] ?"
                // lon="longitudeType [1] ?">
                if(m_isWayType) {
                    rec.append("<wpt ");
                } else {
                    rec.append("<trkpt ");
                }
                if(activeFields.latitude!=0) {
                    rec.append("lat=\"");
                    rec.append(Convert.toString(s.latitude,6));
                    rec.append("\" ");
                }
                if(activeFields.longitude!=0) {
                    rec.append("lon=\"");
                    rec.append(Convert.toString(s.longitude,6));
                    rec.append("\"");
                }
                rec.append(" >\r\n");
                //
//                if(m_isWayType) {
//                    rec.append("<name>"+Convert.toString(m_recCount)+"</name>\r\n");
//                }
                //                    <ele> xsd:decimal </ele> [0..1] ?  (elevation in meters)
                
                // <time> xsd:dateTime </time> [0..1] ? //2005-05-16T11:49:06Z
                if((activeFields.utc!=0)) {
                    rec.append("<time>");
                    if(activeFields.utc!=0) {
                        Time t=utcTime(s.utc);
                        
                        rec.append(Convert.toString(t.year)+"-"
                        +( t.month<10?"0":"")+Convert.toString(t.month)+"-"
                        +(   t.day<10?"0":"")+Convert.toString(t.day)+"T"
                        +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
                        +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
                        +(t.second<10?"0":"")+Convert.toString(t.second)
                        );
                        if(activeFields.milisecond!=0) {
                            rec.append(".");
                            rec.append((s.milisecond<100)?"0":"");
                            rec.append((s.milisecond<10)?"0":"");
                            rec.append(Convert.toString(s.milisecond));
                        }
                        rec.append("Z");
                    }
                    rec.append("</time>\r\n");
                }
                //                    <magvar> degreesType </magvar> [0..1] ?
                if(m_isWayType&&(activeFields.heading!=0)) {
                    rec.append("<magvar>");
                    rec.append(Convert.toString(s.heading));
                    rec.append("</magvar>\r\n");
                }
                //                    <geoidheight> xsd:decimal </geoidheight> [0..1] ?
                if((activeFields.height!=0)) {
                    rec.append("<geoidheight>");
                    rec.append(Convert.toString(s.height,3));
                    rec.append("</geoidheight>\r\n");
                }
                
                //                    <name> xsd:string </name> [0..1] ?
                //                    <cmt> xsd:string </cmt> [0..1] ?
                //                    <desc> xsd:string </desc> [0..1] ?
                //                    <src> xsd:string </src> [0..1] ? // Source of data
                
                //                    <link> linkType </link> [0..*] ?
                //                    <sym> xsd:string </sym> [0..1] ?
                //                    <type> xsd:string </type> [0..1] ?
                if(m_isWayType) {
                    if((activeFields.rcr!=0)) {
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
                        //                    if(style.length()!=1) {
                        //                        style="M";
                        //                    }
                        rec.append("<type>");
                        rec.append(style);
                        rec.append("</type>\r\n");
                    }
                    
                    //                    <fix> fixType </fix> [0..1] ?
                    if((activeFields.valid!=0)) {
                        String tmp="";
                        switch(s.valid) {
                        case 0x0001: 
                            tmp+="'none'"; //"No fix";
                            break;
                        case 0x0002:
                            tmp+= "'3d'"; //"SPS";
                            break;
                        case 0x0004:
                            tmp+="'dgps'";
                            break;
                        case 0x0008:
                            tmp+="'pps'"; // Military signal
                            break;
                        case 0x0010:
                            //tmp+="RTK";
                            break;
                        case 0x0020:
                            //tmp+="FRTK";
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
                        if(tmp.length()!=0) {
                            rec.append("<fix>");
                            rec.append(tmp);
                            rec.append("</fix>\r\n");
                        }
                    }
                    //                    <sat> xsd:nonNegativeInteger </sat> [0..1] ?
                    //                    <hdop> xsd:decimal </hdop> [0..1] ?
                    if((activeFields.hdop!=0)) {
                        rec.append("<hdop>");
                        rec.append(Convert.toString(s.hdop/100.0,2)); 
                        rec.append("</hdop>\r\n");
                    }
                    //                    <vdop> xsd:decimal </vdop> [0..1] ?
                    if((activeFields.vdop!=0)) {
                        rec.append("<vdop>");
                        rec.append(Convert.toString(s.vdop/100.0,2)); 
                        rec.append("</vdop>\r\n");
                    }
                    //              <pdop> xsd:decimal </pdop> [0..1] ?
                    if((activeFields.pdop!=0)) {
                        rec.append("<pdop>");
                        rec.append(Convert.toString(s.pdop/100.0,2)); 
                        rec.append("</pdop>\r\n");
                    }
                    //                    <ageofdgpsdata> xsd:decimal </ageofdgpsdata> [0..1] ?
                    if((activeFields.dage!=0)) {
                        rec.append("<ageofdgpsdata>");
                        rec.append(Convert.toString(s.dage)); 
                        rec.append("</ageofdgpsdata>\r\n");
                    }
                    
                    //                    <dgpsid> dgpsStationType </dgpsid> [0..1] ?
                    if((activeFields.dsta!=0)) {
                        rec.append("<dgpsid>");
                        rec.append(Convert.toString(s.dsta)); 
                        rec.append("</dgpsid>\r\n");
                    }
                    //                    <extensions> extensionsType </extensions> [0..1] ?                
                    
                    if((activeFields.speed!=0)) {
                        rec.append("<speed>");
                        rec.append(Convert.toString(s.speed,3));
                        rec.append("</speed>\r\n");
                    }
                    if((activeFields.distance!=0)) {
                        rec.append("<distance>");
                        rec.append(Convert.toString(s.distance,2)); //+" m\r\n" 
                        rec.append("</distance>\r\n");
                    }
                    
                    rec.append("<cmt>");
                    rec.append("<![CDATA[");
                    //              <pdop> xsd:decimal </pdop> [0..1] ?
                    rec.append("]]>");
                    rec.append("</cmt>\r\n");
                }
                if(m_isWayType) {
                    rec.append("</wpt>\r\n");
                } else {
                    rec.append("</trkpt>\r\n");
                }
                
                writeTxt(rec.toString());
                
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
            writeDataFooter();
            footer= "</gpx>";
            writeTxt(footer);
            m_File.close();
            m_File=null;
        }
        
    }
    
}