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
public class GPSGPXFile extends GPSFile {
    private StringBuffer rec=new StringBuffer(1024);  // reused stringbuffer

    private boolean m_isWayType;
    private boolean m_newTrack=true;
    private int m_currentFilter;
    
    /**
     * 
     */
    public GPSGPXFile() {
        super();
        // TODO Auto-generated constructor stub
        C_NUMBER_OF_PASSES=2;
    }

    /* (non-Javadoc)
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext, final int Card, boolean oneFilePerDay) {
        super.initialiseFile(basename, ext, Card, oneFilePerDay);
        m_currentFilter=GPSFilter.C_WAYPT_IDX;
        m_isWayType=true;
    }
    
    
    
    public boolean nextPass() {
        if(m_nbrOfPassesToGo>0) {
            if(m_oneFilePerDay) {
                closeFile();
            }
            m_nbrOfPassesToGo--;
            m_recCount=0;
            m_prevdate=0;
            m_isWayType=false;
            m_currentFilter=GPSFilter.C_TRKPT_IDX;
            if(!m_oneFilePerDay) {
                writeDataHeader();
            }
            return true;
        } else {
            return false;
        }
    }

    protected void writeFileHeader(final String Name) {
        String header;
        header ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"+
        "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"BT747\" version=\"1.0\" "+
        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
        "    xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">" +
        "<name>" +
        Name +
        "</name>";
        writeTxt(header);
    }
    
    protected void writeDataHeader() {
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
    
    protected void writeDataFooter() {
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
       
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(GPSRecord s) {
        super.writeRecord(s);

        if(activeFields!=null) {

            if(!m_Filters[m_currentFilter].doFilter(s)) {
                // The track is interrupted by a removed log item.
                // Break the track in the output file
                if(!m_isWayType&&!m_newTrack&&!m_FirstRecord) {
                    m_newTrack=true;
                    writeTxt("</trkseg></trk>");
                    writeTxt("<trk><trkseg>");              
                }
            } else {
                // This log item is to be transcribed in the output file.
                
                String timeStr="";  // String that will represent time
                String fixStr="";   // String that will represent fix type
                String rcrStr="";   // String that will represent log reason
                String hdopStr="";  // String that will represent HDOP
                String nsatStr="";  // String that will represent number of sats
                

                //StringBuffer rec=new StringBuffer(1024);
                rec.setLength(0);
                m_newTrack=false;
                //                "  <wpt lat=\"39.921055008\" lon=\"3.054223107\">"+
                //                "    <ele>12.863281</ele>"+
                //                "    <time>2005-05-16T11:49:06Z</time>"+
                //                "    <name>Cala Sant Vicenç - Mallorca</name>"+
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
                        timeStr+=Convert.toString(t.year)+"-"
                        +( t.month<10?"0":"")+Convert.toString(t.month)+"-"
                        +(   t.day<10?"0":"")+Convert.toString(t.day)+"T"
                        +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
                        +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
                        +(t.second<10?"0":"")+Convert.toString(t.second)
                        ;
                        if(activeFields.milisecond!=0) {
                            timeStr+=".";
                            timeStr+=(s.milisecond<100)?"0":"";
                            timeStr+=(s.milisecond<10)?"0":"";
                            timeStr+=Convert.toString(s.milisecond);
                        }
                        timeStr+="Z";
                        rec.append(timeStr);
                    }
                    rec.append("</time>\r\n");
                }
                //                    <magvar> degreesType </magvar> [0..1] ?
                if((activeFields.heading!=0)) {
                    rec.append("<magvar>");
                    rec.append(Convert.toString(s.heading));
                    rec.append("</magvar>\r\n");
                }
                //                    <geoidheight> xsd:decimal </geoidheight> [0..1] ?
                if((activeFields.height!=0)) {
                    rec.append("<ele>");
                    rec.append(Convert.toString(s.height,3));
                    rec.append("</ele>\r\n");
                }
                
                //                    <name> xsd:string </name> [0..1] ?
                rec.append("<name>");
                if(m_isWayType) {
                    rec.append("wpt-");
                } else {
                    rec.append("trkpt-");
                }
                if((activeFields.utc!=0)) {
                    rec.append(timeStr);
                } else {
                    rec.append(Convert.toString(m_recCount));
                }
                rec.append("</name>\r\n");
                
                //                    <cmt> xsd:string </cmt> [0..1] ?
                //                    <desc> xsd:string </desc> [0..1] ?
                //                    <src> xsd:string </src> [0..1] ? // Source of data
                
                //                    <link> linkType </link> [0..*] ?
                //                    <sym> xsd:string </sym> [0..1] ?
                //                    <type> xsd:string </type> [0..1] ?
                if((activeFields.rcr!=0)) {
                    if((s.rcr&BT747_dev.RCR_TIME_MASK)!=0) {
                        rcrStr+="T";
                    }
                    if((s.rcr&BT747_dev.RCR_SPEED_MASK)!=0) {
                        rcrStr+="S";
                    }
                    if((s.rcr&BT747_dev.RCR_DISTANCE_MASK)!=0) {
                        rcrStr+="D";
                    }
                    if((s.rcr&BT747_dev.RCR_BUTTON_MASK)!=0) {
                        rcrStr+="B";
                    }
                    //                    if(style.length()!=1) {
                    //                        style="M";
                    //                    }
                    rec.append("<type>");
                    rec.append(rcrStr);
                    rec.append("</type>\r\n");
                }
                
                //                    <fix> fixType </fix> [0..1] ?
                if((activeFields.valid!=0)) {
                    switch(s.valid) {
                    case 0x0001: 
                        fixStr+="none"; //"No fix";
                        break;
                    case 0x0002:
                        fixStr+= "3d"; //"SPS";
                        break;
                    case 0x0004:
                        fixStr+="dgps";
                        break;
                    case 0x0008:
                        fixStr+="pps"; // Military signal
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
                    if(fixStr.length()!=0) {
                        rec.append("<fix>");
                        rec.append(fixStr);
                        rec.append("</fix>\r\n");
                    }
                }
                //                    <sat> xsd:nonNegativeInteger </sat> [0..1] ?
                //                    <hdop> xsd:decimal </hdop> [0..1] ?
                if((activeFields.hdop!=0)) {
                    hdopStr=Convert.toString(s.hdop/100.0,2);
                    rec.append("<hdop>");
                    rec.append(hdopStr); 
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
                if((activeFields.nsat!=0)) {
                    nsatStr+=Convert.toString(s.nsat/256); 
                    nsatStr+="(";
                    nsatStr+=Convert.toString(s.nsat%256); 
                    nsatStr+=")";
                    rec.append("<nsat>");
                    rec.append(nsatStr);
                    rec.append("</nsat>\r\n");
                }
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
                
                // No comments, so commented out.
                rec.append("<cmt>");
                rec.append("<![CDATA[");
                rec.append(fixStr+","+rcrStr+","+hdopStr+","+nsatStr);
                //                    //              <pdop> xsd:decimal </pdop> [0..1] ?
                rec.append("]]>");
                rec.append("</cmt>\r\n");
                
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
