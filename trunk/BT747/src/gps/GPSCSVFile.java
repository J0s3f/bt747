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

/**Class to write a CSV file.
 * 
 * @author Mario De Weerd
 */
public class GPSCSVFile extends GPSFile {
    private StringBuffer rec=new StringBuffer(1024);  // reused stringbuffer
    private final char fieldSep=',';  // For future parameterisation
    private final char satSeperator=';';   // For future parameterisation
    
    public boolean needPassToFindFieldsActivatedInLog() {
        return true;
    }
    

    protected void writeFileHeader(String Name) {
        rec.setLength(0);
        //INDEX,RCR,DATE,TIME,VALID,LATITUDE,N/S,LONGITUDE,E/W,HEIGHT,SPEED,
        rec.append("INDEX");
        if(activeFileFields.rcr!=0) {
            rec.append(fieldSep+"RCR");
        }
        if(activeFileFields.utc!=0) {
            rec.append(fieldSep+"DATE"+fieldSep+"TIME");
        }
        if(activeFileFields.valid!=0) {
            rec.append(fieldSep+"VALID");
        }
        if(activeFileFields.latitude!=0) {
            rec.append(fieldSep+"LATITUDE"+fieldSep+"N/S");
        }
        if(activeFileFields.longitude!=0) {
            rec.append(fieldSep+"LONGITUDE"+fieldSep+"E/W");
        }
        if(activeFileFields.height!=0) {
            rec.append(fieldSep+"HEIGHT(m)");
        }
        if(activeFileFields.speed!=0) {
            rec.append(fieldSep+"SPEED(km/h)");
        }
        if(activeFileFields.heading!=0) {
            rec.append(fieldSep+"HEADING");
        }
        if(activeFileFields.dsta!=0) {
            rec.append(fieldSep+"DSTA");
        }
        if(activeFileFields.dage!=0) {
            rec.append(fieldSep+"DAGE");
        }
        if(activeFileFields.pdop!=0) {
            rec.append(fieldSep+"PDOP");
        }
        if(activeFileFields.hdop!=0) {
            rec.append(fieldSep+"HDOP");
        }
        if(activeFileFields.vdop!=0) {
            rec.append(fieldSep+"VDOP");
        }
        if(activeFileFields.nsat!=0) {
            rec.append(fieldSep+"NSAT (USED/VIEW)");
        }
        // SAT INFO NOT HANDLED
//        if(activeFileFields.milisecond!=0) {
//            rec.append(fieldSep+"MILISECOND");
//        }
        if(activeFileFields.distance!=0) {
            rec.append(fieldSep+"DISTANCE(m)");
        }
        if(activeFileFields.sid!=null) {
            rec.append(fieldSep+"SAT INFO (SID");
            if(activeFileFields.ele!=null) {
                rec.append("-ELE");
            }
            if(activeFileFields.azi!=null) {
                rec.append("-AZI");
            }
            if(activeFileFields.snr!=null) {
                rec.append("-SNR");
            }
            rec.append(")");
        }
        rec.append(fieldSep+"\r\n");
        writeTxt(rec.toString());
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
            rec.setLength(0);
            rec.append(Convert.toString(m_recCount));
            if(activeFileFields.rcr!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.rcr!=0) {
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
            if(activeFields.utc!=0) {
                rec.append(fieldSep+Convert.toString(t.year)+"/"
                +( t.month<10?"0":"")+Convert.toString(t.month)+"/"
                +(   t.day<10?"0":"")+Convert.toString(t.day)+fieldSep
                +(  t.hour<10?"0":"")+Convert.toString(t.hour)+":"
                +(t.minute<10?"0":"")+Convert.toString(t.minute)+":"
                +(t.second<10?"0":""));
                if(activeFields.milisecond==0) {
                    rec.append(Convert.toString(t.second));
                } else {
                    rec.append(Convert.toString((float)t.second+s.milisecond/1000.0,3));
                }
            } else if(activeFileFields.utc!=0) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }

            if(activeFields.valid!=0) {
                rec.append(fieldSep);
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
            } else if(activeFileFields.valid!=0) {
                rec.append(fieldSep);
            }

            if(activeFields.latitude!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.latitude,6));
                if(s.latitude>=0) {
                    rec.append(fieldSep+"N");
                } else {
                    rec.append(fieldSep+"S");
                }
            } else if(activeFileFields.latitude!=0) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if(activeFields.longitude!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.longitude,6));
                if(s.longitude>=0) {
                    rec.append(fieldSep+"E");
                } else {
                    rec.append(fieldSep+"W");
                }
            } else if(activeFileFields.longitude!=0) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if(activeFields.height!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.height,3));
            } else if(activeFileFields.height!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.speed!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.speed,3));
            } else if(activeFileFields.speed!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.heading!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.heading,6));
            } else if(activeFileFields.heading!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.dsta!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.dsta)); 
            } else if(activeFileFields.dsta!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.dage!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.dage)); 
            } else if(activeFileFields.dage!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.pdop!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.pdop/100.0,2)); 
            } else if(activeFileFields.pdop!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.hdop!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.hdop/100.0,2)); 
            } else if(activeFileFields.hdop!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.vdop!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.vdop/100.0,2)); 
            } else if(activeFileFields.vdop!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.nsat!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString((s.nsat&0xFF00)>>8)); 
                rec.append("("+Convert.toString(s.nsat&0xFF)+")"); 
            } else if(activeFileFields.nsat!=0) {
                rec.append(fieldSep);
            }
            if(activeFields.distance!=0) {
                rec.append(fieldSep);
                rec.append(Convert.toString(s.distance,2));
            } else if(activeFileFields.distance!=0) {
                rec.append(fieldSep);
            }
            if(activeFileFields.sid!=null) {
                int j=0;
                rec.append(fieldSep);
                if(activeFields.sid!=null) {
                    for(int i=s.sid.length-1;i>=0;i--) {
                        if(j!=0) {
                            rec.append(satSeperator);
                        }
                        if(s.sidinuse[j]) {
                            rec.append('#');
                        }
                        if(s.sid[j]<10) {
                            rec.append('0');
                        }
                        rec.append(s.sid[j]);
                        if(activeFileFields.ele!=null) {
                            rec.append('-');
                            if(activeFields.ele!=null) {
                                if(s.ele[j]<10) {
                                    rec.append('0');
                                }
                                rec.append(s.ele[j]);
                            }
                        }
                        if(activeFileFields.azi!=null) {
                            rec.append('-');
                            if(activeFields.azi!=null) {
                                if(s.azi[j]<100) {
                                    rec.append('0');
                                    if(s.azi[j]<10) {
                                        rec.append('0');
                                    }
                                }
                                rec.append(s.azi[j]);
                            }
                        }
                        if(activeFileFields.snr!=null) {
                            rec.append('-');
                            if(activeFields.snr!=null) {
                                if(s.snr[j]<10) {
                                    rec.append('0');
                                }
                                rec.append(s.snr[j]);
                            }
                        }
                        j++;
                    }
                }
            }
            rec.append(fieldSep);
                
            rec.append("\r\n");
            writeTxt(rec.toString());
        } // activeFields!=null
    }
    
}
