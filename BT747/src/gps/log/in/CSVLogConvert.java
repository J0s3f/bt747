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
package gps.log.in;

import gps.BT747_dev;
import gps.convert.Conv;
import gps.log.GPSRecord;
import gps.log.out.GPSFile;

import bt747.Txt;
import bt747.io.File;
import bt747.sys.Convert;
import bt747.sys.Settings;
import bt747.ui.MessageBox;
import bt747.util.Date;

import moio.util.StringTokenizer;

/** This class is used to convert the binary log to a new format.
 * Basically this class interprets the log and creates a {@link GPSRecord}.
 * The {@link GPSRecord} is then sent to the {@link GPSFile} class object to write it
 * to the output.
 * 
 * @author Mario De Weerd
 */
public final class CSVLogConvert implements GPSLogConvert {
    private int logFormat;
    private File m_File=null;
    private long timeOffsetSeconds=0;
    protected boolean passToFindFieldsActivatedInLog= false;
    protected int activeFileFields=0;
    private boolean noGeoid=false; // If true,remove geoid difference from height
    
    private static final int FMT_NS = -6;
    private static final int FMT_EW = -5;
    private static final int FMT_REC_NBR = -4;
    private static final int FMT_DATE = -3;
    private static final int FMT_TIME = -2;
    private static final int FMT_NO_FIELD = -1;
    
    //private static final int DAYS_BETWEEN_1970_1983 = 4748;
    private static final int DAYS_Julian_1970 = (new Date(1,1,1970)).getJulianDay();
    
    public final void parseFile(final GPSFile gpsFile) {
        GPSRecord gpsRec=new GPSRecord();
        final int C_BUF_SIZE=0x800;
        byte[] bytes=new byte[C_BUF_SIZE];
        int sizeToRead;
        int nextAddrToRead;
        int recCount;
        int fileSize;
        
        boolean firstline=true;
        final int C_MAX_RECORDS=30;
        int[] records=new int[C_MAX_RECORDS];
        try {
        records[0]=FMT_NO_FIELD; // Indicates that there are no records
        
        recCount=0;
        logFormat=0;
        nextAddrToRead=0;
        fileSize=m_File.getSize();
        
        
        final int FMT_HEIGHT_FT_IDX=BT747_dev.FMT_HEIGHT_IDX+100;
        final int FMT_SPEED_MPH_IDX=BT747_dev.FMT_SPEED_IDX+100;
        final int FMT_DISTANCE_FT_IDX=BT747_dev.FMT_DISTANCE_IDX+100;
        
        while(nextAddrToRead<fileSize) {
            /********************************************************************
             * Read data from the data file into the local buffer.
             */
            // Determine size to read
            sizeToRead=C_BUF_SIZE;
            if((sizeToRead+nextAddrToRead)>fileSize) {
                sizeToRead=fileSize-nextAddrToRead;
            }
            
            /* Read the bytes from the file */
            int readResult;
            boolean continueInBuffer=true;
            int offsetInBuffer=0;
            
            m_File.setPos(nextAddrToRead);
            
            /*******************************
             * Not reading header - reading data.
             */
            readResult=m_File.readBytes(bytes, 0, sizeToRead);
            if(readResult!=sizeToRead) {
                (new MessageBox(
                        Txt.ERROR,
                        Txt.PROBLEM_READING+m_File.getPath()+"|"+m_File.lastError)).popupBlockingModal();                                   
            }
            nextAddrToRead+=sizeToRead;
            
            
            /////////////////////////////////
            // DATA has been read in 'bytes'
            //
            
            /***************************************************************************
             * Interpret the data read in the Buffer as long as the records are complete
             */
            // A block of bytes has been read, read the records
            do {
                int eol_pos;
                // Find end of line
                for(eol_pos=offsetInBuffer;
                eol_pos<sizeToRead
                && bytes[eol_pos]!=0x0A
                && bytes[eol_pos]!=0x0D;
                eol_pos++
                );
                continueInBuffer=eol_pos<sizeToRead; // True when \r\n
                
                
                
                if(continueInBuffer) {
                    StringBuffer s=new StringBuffer(eol_pos-offsetInBuffer+1);
                    
                    for (int i = offsetInBuffer; i < eol_pos; i++) {
                        s.append((char)bytes[i]);
                    }
                    
                    StringTokenizer Fields = new StringTokenizer(s.toString(),",");
                    offsetInBuffer=eol_pos;
                    for(;
                    offsetInBuffer<sizeToRead
                    && (bytes[offsetInBuffer]==0x0A
                            || bytes[offsetInBuffer]==0x0D);
                    offsetInBuffer++
                    );
                    if(s.length()!=0) {
                        if(firstline) {
                            // Get header
                            firstline=false;
                            activeFileFields=0;
                            for (int i=0; Fields.hasMoreTokens()&&(i<C_MAX_RECORDS); i++) {
                                String string = Fields.nextToken();
                                if(string.equals("INDEX")) {
                                    records[i]=FMT_REC_NBR;
                                    
                                } else if(string.equals("RCR")) {
                                    records[i]=BT747_dev.FMT_RCR_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_RCR_IDX);
                                } else if(string.equals("TIME")) {
                                    records[i]=FMT_TIME;
                                    activeFileFields|=(1<<BT747_dev.FMT_UTC_IDX);
                                } else if(string.equals("DATE")) {
                                    records[i]=FMT_DATE;
                                    activeFileFields|=(1<<BT747_dev.FMT_UTC_IDX);
                                } else if(string.equals("VALID")) {
                                    records[i]=BT747_dev.FMT_VALID_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_VALID_IDX);
                                } else if(string.equals("LATITUDE")) {
                                    records[i]=BT747_dev.FMT_LATITUDE_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_LATITUDE_IDX);
                                } else if(string.equals("N/S")) {
                                    records[i]=FMT_NS;
                                } else if(string.equals("LONGITUDE")) {
                                    records[i]=BT747_dev.FMT_LONGITUDE_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_LONGITUDE_IDX);
                                } else if(string.equals("E/W")) {
                                    records[i]=FMT_EW;
                                } else if(string.startsWith("HEIGHT(ft)")) {
                                    records[i]=FMT_HEIGHT_FT_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_HEIGHT_IDX);
                                } else if(string.startsWith("HEIGHT")) {
                                    records[i]=BT747_dev.FMT_HEIGHT_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_HEIGHT_IDX);
                                } else if(string.startsWith("SPEED(mph)")) {
                                    records[i]=FMT_SPEED_MPH_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_SPEED_IDX);
                                } else if(string.startsWith("SPEED")) {
                                    records[i]=BT747_dev.FMT_SPEED_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_SPEED_IDX);
                                } else if(string.equals("HEADING")) {
                                    records[i]=BT747_dev.FMT_HEADING_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_HEADING_IDX);
                                } else if(string.equals("DSTA")) {
                                    records[i]=BT747_dev.FMT_DSTA_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_DSTA_IDX);
                                } else if(string.equals("DAGE")) {
                                    records[i]=BT747_dev.FMT_DAGE_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_DAGE_IDX);
                                } else if(string.equals("PDOP")) {
                                    records[i]=BT747_dev.FMT_PDOP_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_PDOP_IDX);
                                } else if(string.equals("HDOP")) {
                                    records[i]=BT747_dev.FMT_HDOP_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_HDOP_IDX);
                                } else if(string.equals("VDOP")) {
                                    records[i]=BT747_dev.FMT_VDOP_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_VDOP_IDX);
                                } else if(string.equals("NSAT (USED/VIEW)")) {
                                    records[i]=BT747_dev.FMT_NSAT_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_NSAT_IDX);
                                } else if(string.startsWith("DISTANCE(ft)")) {
                                    records[i]=FMT_DISTANCE_FT_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_DISTANCE_IDX);
                                } else if(string.startsWith("DISTANCE")) {
                                    records[i]=BT747_dev.FMT_DISTANCE_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_DISTANCE_IDX);
                                } else if(string.startsWith("SAT INFO (SID")) {
                                    records[i]=BT747_dev.FMT_SID_IDX;
                                    activeFileFields|=(1<<BT747_dev.FMT_SID_IDX);
                                    if(string.indexOf("-ELE",12)!=-1) {
                                        activeFileFields|=(1<<BT747_dev.FMT_ELEVATION_IDX);
                                    }
                                    if(string.indexOf("-AZI",12)!=-1) {
                                        activeFileFields|=(1<<BT747_dev.FMT_AZIMUTH_IDX);
                                    }
                                    if(string.indexOf("-SNR",12)!=-1) {
                                        activeFileFields|=(1<<BT747_dev.FMT_SNR_IDX);
                                    }
                                } else {
                                    records[i]=-100;//FMT_UNKNOWN_FIELD;
                                }
                                records[i+1]=FMT_NO_FIELD;
                                
                            }
                            if(passToFindFieldsActivatedInLog) {
                                // Some brute force return
                                return;
                            }
                        } else {
                            
                            // Find end of line
                            // Seperate line
                            // Split fields
                            // Interpret fields
                            
                            int field_nbr=0;
                            int curLogFormat=0;
                            // Defaults
                            gpsRec=new GPSRecord();  // Value after earliest date
                            gpsRec.recCount=++recCount;
                            gpsRec.valid=0xFFFF; // In case valid is not logged
                            gpsRec.rcr=1; // In case RCR is not logged - default = time
                            
                            while(Fields.hasMoreElements()
                                    &&field_nbr<C_MAX_RECORDS
                                    &&records[field_nbr]!=FMT_NO_FIELD) {
                                String field=Fields.nextToken();
                                if(field.length()!=0) {
                                    switch(records[field_nbr]) {
                                    case FMT_NS:
                                        // Supposes longitude preceded
                                        if(field.equals("N")) {
                                            gpsRec.latitude=Math.abs(gpsRec.latitude);
                                        } else if(field.equals("S")) {
                                            gpsRec.latitude=-Math.abs(gpsRec.latitude);
                                        }
                                        break;
                                    case FMT_EW:
                                        // Supposes latitude preceded
                                        if(field.equals("E")) {
                                            gpsRec.longitude=Math.abs(gpsRec.longitude);
                                        } else if(field.equals("W")) {
                                            gpsRec.longitude=-Math.abs(gpsRec.longitude);
                                        }
                                        break;
                                    case FMT_REC_NBR:
                                        gpsRec.recCount=Convert.toInt(field);
                                        recCount=gpsRec.recCount;
                                        break;
                                    case FMT_DATE:
                                    {
                                        byte format;
                                        curLogFormat|=(1<<BT747_dev.FMT_UTC_IDX);
                                        if(field.indexOf('/')==4) {
                                            format=Settings.DATE_YMD;
                                        } else {
                                            format=Settings.DATE_DMY;
                                        }
                                        
                                        long date=(new Date(field,format)).getJulianDay()
                                        -DAYS_Julian_1970;
                                        gpsRec.utc+=date*(24*3600);
                                    }
                                    break;
                                    case FMT_TIME:
                                    {
                                        //gpsRec.utc=;
                                        int dotidx;
                                        curLogFormat|=(1<<BT747_dev.FMT_UTC_IDX);
                                        if((dotidx=field.indexOf('.'))!=-1) {
                                            curLogFormat|=(1<<BT747_dev.FMT_MILLISECOND_IDX);
                                            // TODO: check if idx out of range.
                                            gpsRec.milisecond=Convert.toInt(field.substring(dotidx+1));
                                            field=field.substring(0,dotidx);
                                        }
                                        StringTokenizer tfields=new StringTokenizer(field,":");
                                        if(tfields.countTokens()==3) {
                                            gpsRec.utc+=Convert.toInt(tfields.nextToken())*3600L
                                            +Convert.toInt(tfields.nextToken())*60L
                                            +Convert.toInt(tfields.nextToken());
                                        }
                                        gpsRec.utc+=timeOffsetSeconds;
                                    }
                                    break;
                                    case BT747_dev.FMT_VALID_IDX:
                                        curLogFormat|=(1<<BT747_dev.FMT_VALID_IDX);
                                    if (field.equals("No fix")) {
                                        gpsRec.valid= 0x0001; 
                                    } else if (field.equals( "SPS")) {
                                        gpsRec.valid= 0x0002;
                                    } else if (field.equals("DGPS")) {
                                        gpsRec.valid= 0x0004;
                                    } else if (field.equals("PPS")) {
                                        gpsRec.valid= 0x0008;
                                    } else if (field.equals("RTK")) {
                                        gpsRec.valid= 0x0010;
                                    } else if (field.equals("FRTK")) {
                                        gpsRec.valid= 0x0020;
                                    } else if (field.equals( "Estimated mode")) {
                                        gpsRec.valid= 0x0040;
                                    } else if (field.equals( "Manual input mode")) {
                                        gpsRec.valid= 0x0080;
                                    } else if (field.equals( "Simulator mode")) {
                                        gpsRec.valid= 0x0100;
                                    } else {
                                        gpsRec.valid= 0x0000;
                                    }
                                    curLogFormat|=(1<<BT747_dev.FMT_VALID_IDX);
                                    break;
                                    case BT747_dev.FMT_LATITUDE_IDX:
                                        gpsRec.latitude=Convert.toDouble(field);
                                    curLogFormat|=(1<<BT747_dev.FMT_LATITUDE_IDX);
                                    break;
                                    case BT747_dev.FMT_LONGITUDE_IDX:
                                        gpsRec.longitude=Convert.toDouble(field);
                                    curLogFormat|=(1<<BT747_dev.FMT_LONGITUDE_IDX);
                                    break;
                                    case BT747_dev.FMT_HEIGHT_IDX:
                                    {
                                        StringTokenizer n=new StringTokenizer(field.trim()," ");
                                        gpsRec.height=Convert.toFloat(n.nextToken());
                                    }
                                    curLogFormat|=(1<<BT747_dev.FMT_HEIGHT_IDX);
                                    break;
                                    case FMT_HEIGHT_FT_IDX:
                                    {
                                        StringTokenizer n=new StringTokenizer(field.trim()," ");
                                        gpsRec.height=Convert.toFloat(n.nextToken())/3.28083989501312F;
                                    }
                                    curLogFormat|=(1<<BT747_dev.FMT_HEIGHT_IDX);
                                    break;
                                    
                                    case BT747_dev.FMT_SPEED_IDX:
                                    {
                                        StringTokenizer n=new StringTokenizer(field.trim()," ");
                                        gpsRec.speed=Convert.toFloat(n.nextToken());
                                    }
                                    curLogFormat|=(1<<BT747_dev.FMT_SPEED_IDX);
                                    break;
                                    case FMT_SPEED_MPH_IDX:
                                    {
                                        StringTokenizer n=new StringTokenizer(field.trim()," ");
                                        gpsRec.speed=Convert.toFloat(n.nextToken())/0.621371192237334F;
                                    }
                                        curLogFormat|=(1<<BT747_dev.FMT_SPEED_IDX);
                                        break;

                                    //                      gpsRec.speed=Convert.toFloatBitwise(speed);
                                    case BT747_dev.FMT_HEADING_IDX:
                                        gpsRec.heading=Convert.toFloat(field);
                                    curLogFormat|=(1<<BT747_dev.FMT_HEADING_IDX);
                                    break;
                                    case BT747_dev.FMT_DSTA_IDX:
                                        gpsRec.dsta=Convert.toInt(field);
                                    curLogFormat|=(1<<BT747_dev.FMT_DSTA_IDX);
                                    break;
                                    case BT747_dev.FMT_DAGE_IDX:
                                        gpsRec.dage=Convert.toInt(field);
                                    curLogFormat|=(1<<BT747_dev.FMT_DAGE_IDX);
                                    break;
                                    case BT747_dev.FMT_PDOP_IDX:
                                        gpsRec.pdop=(int)(Convert.toFloat(field)*100);
                                    curLogFormat|=(1<<BT747_dev.FMT_PDOP_IDX);
                                    break;
                                    case BT747_dev.FMT_HDOP_IDX:
                                        gpsRec.hdop=(int)(Convert.toFloat(field)*100);
                                    curLogFormat|=(1<<BT747_dev.FMT_HDOP_IDX);
                                    break;
                                    case BT747_dev.FMT_VDOP_IDX:
                                        gpsRec.vdop=(int)(Convert.toFloat(field)*100);
                                    curLogFormat|=(1<<BT747_dev.FMT_VDOP_IDX);
                                    break;
                                    case BT747_dev.FMT_NSAT_IDX:
                                    {
                                        StringTokenizer nfields=
                                            new StringTokenizer(field,"()");
                                        if(nfields.countTokens()>=2) {
                                            gpsRec.nsat=Convert.toInt(nfields.nextToken())*256
                                            +Convert.toInt(nfields.nextToken());
                                            curLogFormat|=(1<<BT747_dev.FMT_NSAT_IDX);
                                        }
                                    }
                                    // Need to handle ()
                                    break;
                                    case BT747_dev.FMT_MAX_SATS:
                                        break;
                                    case BT747_dev.FMT_SID_IDX:
                                    {
                                        StringTokenizer SatFields = new StringTokenizer(field,";");
                                        int cnt=SatFields.countTokens();
                                        gpsRec.sid=new int[cnt];
                                        gpsRec.sidinuse=new boolean[cnt];
                                        gpsRec.ele=new int[cnt];
                                        gpsRec.azi=new int[cnt];
                                        gpsRec.snr=new int[cnt];
                                        for (int i = 0; i < cnt; i++) {
                                            String satf=SatFields.nextToken();
                                            if(satf.length()!=0) {
                                                gpsRec.sidinuse[i]=(satf.charAt(0)=='#');
                                                StringTokenizer sinfos=
                                                    new StringTokenizer(satf.substring(gpsRec.sidinuse[i]?1:0),"-");
                                                //Vm.debug(sinfos[0]);
                                                if(sinfos.hasMoreElements()) {
                                                    gpsRec.sid[i]=Convert.toInt(sinfos.nextToken());
                                                    curLogFormat|=(1<<BT747_dev.FMT_SID_IDX);
                                                }
                                                if (((activeFileFields&BT747_dev.FMT_ELEVATION_IDX)!=0)
                                                        && sinfos.hasMoreElements()
                                                        ) {
                                                    curLogFormat|=(1<<BT747_dev.FMT_ELEVATION_IDX);
                                                    gpsRec.ele[i]=Convert.toInt(sinfos.nextToken());
                                                }
                                                if (((activeFileFields&BT747_dev.FMT_AZIMUTH_IDX)!=0)
                                                    && sinfos.hasMoreElements()
                                                    ) {
                                                    curLogFormat|=(1<<BT747_dev.FMT_AZIMUTH_IDX);
                                                    gpsRec.azi[i]=Convert.toInt(sinfos.nextToken());
                                                }
                                                if (((activeFileFields&BT747_dev.FMT_SNR_IDX)!=0) 
                                                    && sinfos.hasMoreElements()
                                                    ) {
                                                    curLogFormat|=(1<<BT747_dev.FMT_SNR_IDX);
                                                    gpsRec.snr[i]=Convert.toInt(sinfos.nextToken());
                                                }
                                            }  // length
                                        } // for 
                                    }
                                    break;
                                    
                                    case BT747_dev.FMT_RCR_IDX:
                                    {
                                        curLogFormat|=(1<<BT747_dev.FMT_RCR_IDX);
                                        if(field.indexOf('B',0)!=-1) {
                                            gpsRec.rcr|=BT747_dev.RCR_BUTTON_MASK;
                                        }
                                        if(field.indexOf('T',0)!=-1) {
                                            gpsRec.rcr|=BT747_dev.RCR_TIME_MASK;
                                        }
                                        if(field.indexOf('S',0)!=-1) {
                                            gpsRec.rcr|=BT747_dev.RCR_SPEED_MASK;
                                        }
                                        if(field.indexOf('D',0)!=-1) {
                                            gpsRec.rcr|=BT747_dev.RCR_DISTANCE_MASK;
                                        }
                                        
                                        // Still 16-4 = 12 possibilities.
                                        // Taking numbers from 1 to 9
                                        // Then letters X, Y and Z
                                        char c='1';
                                        int i;
                                        for ( i= 0x10; c <= '9' ; i<<=1, c++) {
                                            if((field.indexOf(c,0)!=-1)) {
                                                gpsRec.rcr|=i;
                                            }
                                        }
                                        c='X';
                                        for ( i= 0x10; c <= '9' ; i<<=1, c++) {
                                            if((field.indexOf(c,0)!=-1)) {
                                                gpsRec.rcr|=i;
                                            }
                                        }
                                    }
                                    break;
                                    case BT747_dev.FMT_MILLISECOND_IDX:
                                        //                        gpsRec.milisecond=
                                        break;
                                    case BT747_dev.FMT_DISTANCE_IDX:
                                    {
                                        StringTokenizer n=new StringTokenizer(field.trim()," ");
                                        gpsRec.distance=Convert.toDouble(n.nextToken());
                                    }
                                    curLogFormat|=(1<<BT747_dev.FMT_DISTANCE_IDX);
                                    break;
                                    case FMT_DISTANCE_FT_IDX:
                                    {
                                        StringTokenizer n=new StringTokenizer(field.trim()," ");
                                        gpsRec.distance=Convert.toDouble(n.nextToken())/3.28083989501312;
                                    }
                                        curLogFormat|=(1<<BT747_dev.FMT_DISTANCE_IDX);
                                        break;
                                    case BT747_dev.FMT_HOLUX_LOW_PRECISION_IDX:
                                        break;
                                    default:
                                        // Error message to show.
                                    }
                                }
                                
                                // Next contdition should be handled by stopping interpretation after
                                // the first line.
                                //if(!passToFindFieldsActivatedInLog) {
                                field_nbr++;
                            }
                            if(noGeoid
                                    &&((curLogFormat&(1<<BT747_dev.FMT_HEIGHT_IDX))!=0)
                                    &&((curLogFormat&(1<<BT747_dev.FMT_LATITUDE_IDX))!=0)
                                    &&((curLogFormat&(1<<BT747_dev.FMT_LONGITUDE_IDX))!=0)
                            ) {
                                gpsRec.height-=Conv.wgs84_separation(gpsRec.latitude, gpsRec.longitude);
                            }
                            if(curLogFormat!=logFormat) {
                                updateLogFormat(gpsFile,curLogFormat);
                            }
                            if(gpsRec.rcr==0) {
                                gpsRec.rcr=1; // Suppose time (for filter)
                            }
                            //if(valid) {
                            gpsFile.writeRecord(gpsRec);
                            //}                //              offsetInBuffer++;
                        } // if header
                    }
                } // line found
            } while(continueInBuffer);
            nextAddrToRead-=(sizeToRead-offsetInBuffer);
        } /* nextAddrToRead<fileSize */
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
    
    public final void setTimeOffset(long offset) {
        timeOffsetSeconds= offset;
    }
    
    public final void setNoGeoid(boolean b) {
        noGeoid=b;
    }
    
    
    public final void toGPSFile(final String fileName, final GPSFile gpsFile, final int Card) {
        try {
        if(File.isAvailable()) {
            m_File=new File(fileName,File.READ_ONLY, Card);
            if(!m_File.isOpen()) {
                (new MessageBox(
                        Txt.ERROR,
                        Txt.COULD_NOT_OPEN+fileName+"|"+m_File.lastError)).popupBlockingModal();                                   
                m_File=null;
            } else {
                passToFindFieldsActivatedInLog=gpsFile.needPassToFindFieldsActivatedInLog();
                if(passToFindFieldsActivatedInLog) {
                    activeFileFields=0;
                    parseFile(gpsFile);
                    gpsFile.setActiveFileFields(getLogFormatRecord(activeFileFields));
                }
                passToFindFieldsActivatedInLog=false;
                do {
                    parseFile(gpsFile);
                } while (gpsFile.nextPass());
                gpsFile.finaliseFile();
            }
            
            if(m_File!=null) {
                m_File.close();
            }
        } } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }
    
    private final void updateLogFormat(final GPSFile gpsFile,final int newLogFormat) {
        logFormat=newLogFormat;
        activeFileFields|=logFormat;
        if(!passToFindFieldsActivatedInLog) {
            gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
        }
    }    
    
    
    public static final GPSRecord getLogFormatRecord(final int logFormat) {
        GPSRecord gpsRec=new GPSRecord();
        if((logFormat&(1<<BT747_dev.FMT_UTC_IDX))!=0) {
            gpsRec.utc=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_VALID_IDX))!=0) { 
            gpsRec.valid=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_LATITUDE_IDX))!=0) { 
            gpsRec.latitude=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_LONGITUDE_IDX))!=0) { 
            gpsRec.longitude=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_HEIGHT_IDX))!=0) { 
            gpsRec.height=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_SPEED_IDX))!=0) { 
            gpsRec.speed=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_HEADING_IDX))!=0) { 
            gpsRec.heading=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_DSTA_IDX))!=0) { 
            gpsRec.dsta=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_DAGE_IDX))!=0) { 
            gpsRec.dage=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_PDOP_IDX))!=0) { 
            gpsRec.pdop=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_HDOP_IDX))!=0) { 
            gpsRec.hdop=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_VDOP_IDX))!=0) { 
            gpsRec.vdop=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_NSAT_IDX))!=0) { 
            gpsRec.nsat=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) { 
            gpsRec.sid=new int[0];
            gpsRec.sidinuse=new boolean[0];
        }
        if((logFormat&(1<<BT747_dev.FMT_ELEVATION_IDX))!=0) { 
            gpsRec.ele=new int[0];
        }
        if((logFormat&(1<<BT747_dev.FMT_AZIMUTH_IDX))!=0) { 
            gpsRec.azi=new int[0];
        }
        if((logFormat&(1<<BT747_dev.FMT_SNR_IDX))!=0) { 
            gpsRec.snr=new int[0];
        }
        if((logFormat&(1<<BT747_dev.FMT_RCR_IDX))!=0) { 
            gpsRec.rcr=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_MILLISECOND_IDX))!=0) { 
            gpsRec.milisecond=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_DISTANCE_IDX))!=0) { 
            gpsRec.distance=-1;
        }
        
        /* End handling record */
        return gpsRec;
    }
}
