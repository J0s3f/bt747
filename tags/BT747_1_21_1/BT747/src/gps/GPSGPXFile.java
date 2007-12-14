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

/**Class to write a GPX file.
 * @author Mario De Weerd
 */
public class GPSGPXFile extends GPSFile {
    private StringBuffer rec=new StringBuffer(1024);  // reused stringbuffer

    private boolean m_isWayType;
    private boolean m_newTrack=true;
    private int m_currentFilter;
    private String m_TrackName="";
    private boolean m_TrkSegSplitOnlyWhenSmall=false;
    
    /**
     * 
     */
    public GPSGPXFile() {
        super();
        C_NUMBER_OF_PASSES=2;
    }

    /* (non-Javadoc)
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext, final int Card, int oneFilePerDay) {
        super.initialiseFile(basename, ext, Card, oneFilePerDay);
        m_currentFilter=GPSFilter.C_WAYPT_IDX;
        m_isWayType=true;
    }
    
    
    
    public boolean nextPass() {
        super.nextPass();
        if(m_nbrOfPassesToGo>0) {
//            if(m_multipleFiles) {
//                closeFile();
//            }
            m_nbrOfPassesToGo--;
            m_recCount=0;
            m_prevdate=0;
            m_isWayType=false;
            m_currentFilter=GPSFilter.C_TRKPT_IDX;
//            if(!m_multipleFiles) {
//                writeDataHeader();
//            }
            return true;
        } else {
            return false;
        }
    }

    protected void writeFileHeader(final String Name) {
        String header;
        header ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"+
        "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"BT747\" version=\"" +
        bt747.Version.VERSION_NUMBER +
        "\" "+
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
                "<name>";
            header+=m_TrackName;
            header+=
                "</name>" +
                "</trk>"+
                "\r\n";
            writeTxt(header);
        }
    }

    protected void writeTrkSegSplit() {
        String header;
        if(m_isWayType) {
        } else {
            header="</trkseg><trkseg>";
            writeTxt(header);
        }
    }

    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    final private static char []zeros = "0000000".toCharArray();
    public void writeRecord(GPSRecord s) {
        super.writeRecord(s);

        if(activeFields!=null) {

            if(!m_Filters[m_currentFilter].doFilter(s)) {
                // The track is interrupted by a removed log item.
                // Break the track in the output file
                if(!m_isWayType&&!m_newTrack&&!m_FirstRecord) {
                    if(m_TrkSegSplitOnlyWhenSmall
                       &&((activeFields.utc==0)||(m_prevtime+m_TrackSepTime>s.utc))) {
                        writeTrkSegSplit();
                    } else {
                        writeDataFooter();
                        writeDataHeader();
                    }
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
                    rec.append(Convert.toString(s.latitude,8));
                    rec.append("\" ");
                }
                if(activeFields.longitude!=0) {
                    rec.append("lon=\"");
                    rec.append(Convert.toString(s.longitude,8));
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
                        timeStr+=Convert.toString(t.getYear())+"-"
                        +( t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"-"
                        +(   t.getDay()<10?"0":"")+Convert.toString(t.getDay())+"T"
                        +(  t.getHour()<10?"0":"")+Convert.toString(t.getHour())+":"
                        +(t.getMinute()<10?"0":"")+Convert.toString(t.getMinute())+":"
                        +(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
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
                    rec.append("<course>");
                    rec.append(Convert.toString(s.heading));
                    rec.append("</course>\r\n");
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
                if(m_newTrack) {
                    StringBuffer tx=new StringBuffer();
                    String tmp=Convert.toString(m_recCount);
                    int nZeros=5-tmp.length();
                    if(nZeros<0) {
                        nZeros=0;
                    }
                    tx.append(zeros,0,nZeros);
                    m_TrackName="#"+tx.toString()+Convert.toString(m_recCount)+"#";
                }
                if((activeFields.utc!=0)) {
                    rec.append(timeStr);
                    if(m_newTrack) {
                        m_TrackName+=" "+timeStr;
                    }
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
                    rec.append("<type>");
                    rec.append(getRCRstr(s));
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
                    rec.append("<sat>");
                    rec.append(nsatStr);  // Sat used
                    rec.append("</sat>\r\n");
//                    nsatStr+="(";
//                    nsatStr+=Convert.toString(s.nsat%256); 
//                    nsatStr+=")";
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
                    rec.append(Convert.toString(s.speed/3.6f,4));  // must be meters/second
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

                m_newTrack=false;
                
            }
        } // activeFields!=null
    }
    
    /* (non-Javadoc)
     * @see gps.GPSFile#FinaliseFile()
     */
    public void finaliseFile() {
        if(m_File!=null) {
            String footer;
            writeDataFooter();
            footer= "</gpx>";
            writeTxt(footer);
            closeFile();
        }
        
    }
    
    /**
     * @return Returns the m_TrkSegSplitOnlyWhenSmall.
     */
    public boolean isTrkSegSplitOnlyWhenSmall() {
        return m_TrkSegSplitOnlyWhenSmall;
    }
    /**
     * @param trkSegSplitOnlyWhenSmall The m_TrkSegSplitOnlyWhenSmall to set.
     */
    public void setTrkSegSplitOnlyWhenSmall(boolean trkSegSplitOnlyWhenSmall) {
        m_TrkSegSplitOnlyWhenSmall = trkSegSplitOnlyWhenSmall;
    }
}
