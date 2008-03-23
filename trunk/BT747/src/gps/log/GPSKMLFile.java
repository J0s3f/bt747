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
package gps.log;

import gps.BT747_dev;

import bt747.sys.Convert;

/**Class to write a KML file.
* @author Mario De Weerd
* @author Herbert Geus (Waypoint code&Track code)
*/
public class GPSKMLFile extends GPSFile {
  private StringBuffer rec=new StringBuffer(1024);  // reused stringbuffer


  private boolean m_isWayType;
  private boolean m_isTrackType;
  private boolean m_isPathType;
  private int m_currentFilter;
  private String m_name;
  private int m_AltitudeMode=0; // 0 = altitude.
  
  /**
   * 
   */
  public GPSKMLFile() {
      super();
      C_NUMBER_OF_PASSES=3;
  }

  /* (non-Javadoc)
   * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
   */
  public void initialiseFile(final String basename, final String ext, final int Card, int oneFilePerDay) {
      super.initialiseFile(basename, ext, Card, oneFilePerDay);
      m_currentFilter=GPSFilter.C_WAYPT_IDX;
      m_isWayType=true;
      m_isTrackType=false;
      m_isPathType=false;
  }
  
  
  
  public boolean nextPass() {
      super.nextPass();
      if(m_nbrOfPassesToGo>0) {
//          if(m_multipleFiles) {
//              closeFile();  // Close every time - if single file, it gets reopened.
//          }
          m_nbrOfPassesToGo--;
          m_prevdate=0;
//          if(!m_multipleFiles) {
//            writeDataFooter();
//          }
          if (m_isWayType) {
            m_isWayType=false;
            m_isTrackType=true;
          } else if (m_isTrackType) {
            m_isTrackType=false;
            m_isPathType=true;
          }
          m_currentFilter=GPSFilter.C_TRKPT_IDX;
//          if(!m_multipleFiles) {
//              writeDataHeader();
//          }
          return true;
      } else {
          return false;
      }
  }


  public void writeFileHeader(final String name) {
      String header;
      m_name = name;
      header ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"+
      "<kml xmlns=\"http://earth.google.com/kml/2.0\">\r\n"+
      "<Document>\r\n"+
      "  <name>i-Blue 747"+name+"</name>\r\n"+
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
      "    <StyleMap id=\"StyleB\">\r\n"+
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
      "    </StyleMap>\r\n";
      writeTxt(header);
  }
  
  protected void writeDataHeader() {
      String header;
      if(m_isWayType) {
        header=
                "  <Folder>\r\n"+
                "    <name>My Waypoints</name>\r\n"+
                "    <open>0</open>\r\n";
      } else if (m_isTrackType) {
          header=
              "  <Folder>\r\n"+
//              "    <name>My Tracks</name>\r\n"+
              "    <name>My Trackpoints</name>\r\n"+
              "    <Folder>\r\n"+
//              "    <name>Track-"+m_name+"</name>\r\n"+
              "    <name>Trackpoints-"+m_name+"</name>\r\n"+
              "      <open>0</open>\r\n"+
              "\r\n";
      } else {
          header=
                "  <Folder>\r\n"+
                "  <name>My Tracks</name>\r\n"+
                "  <open>0</open>\r\n"+
                "  <Placemark>\r\n"+
                "    <name>Track-"+m_name+"</name>\r\n"+
                "    <Style>\r\n"+
                "      <LineStyle>\r\n"+
                "        <color>ffff0000</color>\r\n"+
                "        <width>3.0</width>\r\n"+
                "      </LineStyle>\r\n"+
                "    </Style>\r\n"+
                "    <LineString>\r\n"+
                "    <altitudeMode>absolute</altitudeMode>\r\n"+
                "    <extrude>1</extrude>\r\n"+
                "      <tessellate>1</tessellate>\r\n"+
                "      <coordinates>\r\n";            
      }
      writeTxt(header);
  }
  
  protected void writeDataFooter() {
      String footer;
      if(m_isWayType) {
          footer=
              "  </Folder>\r\n"+
              "\r\n";
      } else if (m_isTrackType) {
          footer=
              "    </Folder>\r\n"+
              "  </Folder>\r\n"+
              "\r\n";
      } else {
          footer=
               "      </coordinates>\r\n"+
               "     </LineString>\r\n"+
               "    </Placemark>\r\n"+
               "  </Folder>\r\n";            
      }
      writeTxt(footer);
  }
     

  /* (non-Javadoc)
   * @see gps.GPSFile#WriteRecord()
   */
  public void writeRecord(final GPSRecord s) {
      super.writeRecord(s);

      if(activeFields!=null) {
          rec.setLength(0);
          if(m_Filters[m_currentFilter].doFilter(s)) {
            if (m_isWayType || m_isTrackType) {
                    rec.append("<Placemark>\r\n");
                    if (m_isTrackType) {
                        rec.append("<visibility>0</visibility>\r\n");
                    }
                    rec.append("<name>");
                    if(activeFields.utc!=0) {
                        rec.append("TIME: "
                            +(  t.getHour()<10?"0":"")+Convert.toString(t.getHour())+":"
                            +(t.getMinute()<10?"0":"")+Convert.toString(t.getMinute())+":"
                            +(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
                            );
                    } else {
                        rec.append("IDX: ");
                        rec.append(Convert.toString(s.recCount));
                    }
                    rec.append("</name>\r\n");
                    
                    
                if((activeFields.utc!=0)) {
                    rec.append("<TimeStamp><when>");
                    if(activeFields.utc!=0) {
                        rec.append(Convert.toString(t.getYear())+"-"
                        +( t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"-"
                        +(   t.getDay()<10?"0":"")+Convert.toString(t.getDay())+"T"
                        +(  t.getHour()<10?"0":"")+Convert.toString(t.getHour())+":"
                        +(t.getMinute()<10?"0":"")+Convert.toString(t.getMinute())+":"
                        +(t.getSecond()<10?"0":"")
                        );
                        if(activeFields.milisecond==0) {
                            rec.append(Convert.toString(t.getSecond()));
                        } else {
                            rec.append(Convert.toString((float)t.getSecond()+s.milisecond/1000.0,3));
                        }
                        rec.append("Z");
                    }
                    rec.append("</when></TimeStamp>\r\n");
                }
    
                    rec.append("<styleUrl>");
                    if(activeFields.rcr!=0) {
                        String style=getRCRstr(s);
                        
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
                        if(style.length()>1 || ((s.rcr&BT747_dev.RCR_ALL_APP_MASK)!=0)) {
                            style="M";
                        }
                        rec.append("#Style");
                        rec.append(style);
                    }
                    rec.append("</styleUrl>\r\n");
                    
                    
                    if((activeFields.longitude!=0)&&(activeFields.latitude!=0)) {
                        rec.append("<Point>\r\n");
                        rec.append("<coordinates>");
                        rec.append(Convert.toString(s.longitude,6));
                        rec.append(",");
                        rec.append(Convert.toString(s.latitude,6));
                        if(activeFields.height!=0) {
                            rec.append(",");
                            rec.append(Convert.toString(s.height,3));
                        }
                        rec.append("</coordinates>");
                        rec.append("</Point>\r\n");
                    }
                    
                    rec.append("<description>");
                    rec.append("<![CDATA[");
                    if(recordNbrInLogs) {
                        rec.append("IDX: ");
                        rec.append(Convert.toString(s.recCount));
                        rec.append("<br/>");
                    }
                    if(activeFields.utc!=0) {
                        rec.append("DATE: ");
                        rec.append(Convert.toString(t.getYear())+"-"
                        +( t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"-"
                        +(   t.getDay()<10?"0":"")+Convert.toString(t.getDay())
                        );
                    }
                    if(activeFields.rcr!=0) {
                        rec.append("<br />RCR: ");
                        if((s.rcr&BT747_dev.RCR_TIME_MASK)!=0) {
                            rec.append("T");
                        }
                        if((s.rcr&BT747_dev.RCR_SPEED_MASK)!=0) {
                            rec.append("S");
                        }
                        if((s.rcr&BT747_dev.RCR_DISTANCE_MASK)!=0) {
                            rec.append("D");
                        }
                        if((s.rcr&BT747_dev.RCR_BUTTON_MASK)!=0) {
                            rec.append("B");
                        }
                    }
    //                if(activeFields.utc!=0) {
    //                    Time t=utcTime(s.utc);
    //                    
    //                    rec.append("<br />DATE: ");
    //                    rec.append(Convert.toString(t.getYear())+"/"
    //                    +( t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"/"
    //                    +(   t.getDay()<10?"0":"")+Convert.toString(t.getDay())+"<br />"
    //                    +"TIME: "
    //                    +(  t.getHour()<10?"0":"")+Convert.toString(t.getHour())+":"
    //                    +(t.getMinute()<10?"0":"")+Convert.toString(t.getMinute())+":"
    //                    +(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
    //                    );
    //                }
                    if(activeFields.valid!=0) {
//                      rec.append("<br />VALID: ");
                        rec.append(", VALID: ");
                        switch(s.valid) {
                        case 0x0001: 
                            rec.append("No fix");
                            break;
                        case 0x0002:
                            rec.append( "SPS");
                            break;
                        case 0x0004:
                            rec.append("DGPS");
                            break;
                        case 0x0008:
                            rec.append("PPS");
                            break;
                        case 0x0010:
                            rec.append("RTK");
                            break;
                        case 0x0020:
                            rec.append("FRTK");
                            break;
                        case 0x0040:
                            rec.append( "Estimated mode");
                            break;
                        case 0x0080:
                            rec.append( "Manual input mode");
                            break;
                        case 0x0100:
                            rec.append( "Simulator mode");
                            break;
                        default:
                            rec.append("Unknown mode");
                        }
                    }
                    if(activeFields.latitude!=0) {
                        rec.append("<br />LATITUDE: ");
                        if(s.latitude>=0) {
                            rec.append(Convert.toString(s.latitude,6));
                            rec.append(" N");
                        } else {
                            rec.append(Convert.toString(-s.latitude,6));
                            rec.append(" S");
                        }
                    }
                    if(activeFields.longitude!=0) {
                        rec.append("<br />LONGITUDE: ");
                        if(s.longitude>=0) {
                            rec.append(Convert.toString(s.longitude,6));
                            rec.append(" E");
                        } else {
                            rec.append(Convert.toString(-s.longitude,6));
                            rec.append(" W");
                        }
                    }
                    if(activeFields.height!=0) {
                        rec.append("<br />HEIGHT: ");
                        if(!imperial) {
                            rec.append(Convert.toString(s.height,3)+" m");
                        } else {
                            /// speed/distance/altitude in imperial units (mph/miles/feet?). 
                            rec.append(Convert.toString(s.height*3.28083989501312,3)+" feet");
                        }
                    }
                    if(activeFields.speed!=0) {
                        rec.append("<br />SPEED: ");
                        if(!imperial) {
                            rec.append(Convert.toString(s.speed,3)+" km/h");
                        } else {
                            rec.append(Convert.toString(s.speed*0.621371192237334,3)+" mph");
                        }
                    }
                    if(activeFields.heading!=0) {
                        rec.append("<br />HEADING: ");
                        rec.append(Convert.toString(s.heading));
                    }
                    if(activeFields.dsta!=0) {
                        rec.append("<br />DSTA: ");
                        rec.append(Convert.toString(s.dsta)); 
                    }
                    if(activeFields.dage!=0) {
                        rec.append("<br />DAGE: ");
                        rec.append(Convert.toString(s.dage)); 
                    }
                    if(activeFields.pdop!=0) {
                        rec.append("<br />PDOP: ");
                        rec.append(Convert.toString(s.pdop/100.0,2)); 
                    }
                    if(activeFields.hdop!=0) {
                        rec.append("<br />HDOP: ");
                        rec.append(Convert.toString(s.hdop/100.0,2)); 
                    }
                    if(activeFields.vdop!=0) {
                        rec.append("<br />VDOP: ");
                        rec.append(Convert.toString(s.vdop/100.0,2)); 
                    }
                    if(activeFields.distance!=0) {
                        rec.append("<br />DISTANCE: ");
                        if(!imperial) {
                            rec.append(Convert.toString(s.distance,2));
                            rec.append(" m");
                        } else {
                            rec.append(Convert.toString(s.distance*3.2808398950131234,2));
                            rec.append(" feet");
                        }
                    }
                    
                    rec.append("]]>");
                    rec.append("</description>");
                    rec.append("</Placemark>\r\n");
                    
                    rec.append("\r\n");
                    writeTxt(rec.toString());
            } else if (m_isPathType) {
                rec.setLength(0);
                if(activeFields.longitude!=0&&activeFields.latitude!=0) {
                    if((activeFields.height!=0)!=(m_AltitudeMode==0)) {
                        rec.append("</coordinates>");
                        rec.append(
                        "    </LineString><LineString>\r\n"+
                        "    <extrude>1</extrude>\r\n"+
                        "    <tessellate>1</tessellate>\r\n"+
                        "    <altitudeMode>");
                        if(activeFields.height!=0) {
                            m_AltitudeMode=0;
                            //clampToGround, relativeToGround, absolute
                            rec.append("absolute");
                        } else {
                            rec.append("clampToGround");
                            m_AltitudeMode=1;
                        }
                        rec.append("</altitudeMode><coordinates>\r\n");
                    }
                    rec.append("        ");
                    rec.append(Convert.toString(s.longitude,6));
                    rec.append(",");
                    rec.append(Convert.toString(s.latitude,6));
                    if(activeFields.height!=0) {
                        rec.append(",");
                        rec.append(Convert.toString(s.height,3));
                    }
                    rec.append("\r\n");
                    writeTxt(rec.toString());
                }
            }
              
          }
      } // activeFields!=null
  }
  
  /* (non-Javadoc)
   * @see gps.GPSFile#FinaliseFile()
   */
  public void finaliseFile() {
      if(this.isOpen()) {
          String footer;
          writeDataFooter();            
          footer=
          "</Document>\r\n"+
          "</kml>";
          writeTxt(footer);
      }
      super.finaliseFile();
  }
  
}
