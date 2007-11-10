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
import waba.sys.Vm;
import waba.ui.MessageBox;

import gps.convert.Conv;

/** This class is used to convert the binary log to a new format.
 * Basically this class interprets the log and creates a {@link GPSRecord}.
 * The {@link GPSRecord} is then sent to the {@link GPSFile} class object to write it
 * to the output.
 * 
 * @author Mario De Weerd
 */
public final class BT747LogConvert {
    private int minRecordSize;
    private int maxRecordSize;
    private int logFormat;
    private File m_File=null;
    private long timeOffsetSeconds=0;
    protected boolean passToFindFieldsActivatedInLog= false;
    protected int activeFileFields=0;
    private boolean noGeoid=false; // If true,remove geoid difference from height

    private int satIdxOffset;
    private int satRecSize;
    
    private final void updateLogFormat(final GPSFile gpsFile,final int newLogFormat) {
        int bits=newLogFormat;
        int index = 0;
        int total = 0;
        
        satRecSize=0;
        logFormat=newLogFormat;
        activeFileFields|=logFormat;
        if(!passToFindFieldsActivatedInLog) {
            gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
        }
        minRecordSize=BT747_dev.logRecordMinSize(logFormat);
        maxRecordSize=BT747_dev.logRecordMaxSize(logFormat);
        
        do {
            if ((bits&1)!=0) {
                switch (index) {
                case BT747_dev.FMT_SID_IDX:
                case BT747_dev.FMT_ELEVATION_IDX:
                case BT747_dev.FMT_AZIMUTH_IDX:
                case BT747_dev.FMT_SNR_IDX:
                    satRecSize+=BT747_dev.logFmtByteSizes[index];
                    break;
                case BT747_dev.FMT_RCR_IDX:
                case BT747_dev.FMT_MILISECOND_IDX:
                case BT747_dev.FMT_DISTANCE_IDX:
                
                // These fields do not contribute to the sat offset
                    break;
                default:
                    // Other fields contribute
                    total+=BT747_dev.logFmtByteSizes[index];
                break;
                }
            }
            index++;
        } while((bits>>=1) != 0);
        satIdxOffset=total;
    }    

    public final void parseFile(final GPSFile gpsFile) {
        GPSRecord gpsRec=new GPSRecord();
        final int C_BUF_SIZE=0x800;
        byte[] bytes=new byte[C_BUF_SIZE];
        int sizeToRead;
        int nextAddrToRead;
        int recCount;
        int fileSize;
        int satCntIdx;
        int satcnt;
        int satidx;
        int idx;
        recCount=0;
        logFormat=0;
        nextAddrToRead=0;
        fileSize=m_File.getSize();
        while(nextAddrToRead<fileSize) {
            int okInBuffer=-1; // Last ending position in buffer
            
            /********************************************************************
             * Read data from the raw data file into the local buffer.
             */
            // Determine size to read
            if((nextAddrToRead&0xFFFF)<0x200) {
                // Read the header
                nextAddrToRead=(nextAddrToRead&0xFFFF0000)|0x200;
                nextAddrToRead=(nextAddrToRead&0xFFFF0000);
            }
            int endOfBlock=(nextAddrToRead&0xFFFF0000)|0xFFFF;
            sizeToRead=endOfBlock+1-nextAddrToRead;
            if(sizeToRead>C_BUF_SIZE) {
                sizeToRead=C_BUF_SIZE;
            }
            if((sizeToRead+nextAddrToRead)>fileSize) {
                sizeToRead=fileSize-nextAddrToRead;
            }
            
            /* Read the bytes from the file */
            int readResult;
            boolean continueInBuffer=true;
            int offsetInBuffer=0;
            int newLogFormat;

            m_File.setPos(nextAddrToRead);
            
            if((nextAddrToRead&0xFFFF)==0) {
                /******************************
                 * This is the header.
                 * Only 20 bytes are read - just enough to get the log format.  
                 */
                if(sizeToRead>=20) {
                    // Read header (20 bytes is enough)
                    readResult=m_File.readBytes(bytes, 0, 20);
                    if(readResult!=20) {
                        (new MessageBox("Error","Problem reading|"+m_File.getPath()+"|"+m_File.lastError)).popupBlockingModal();                                   
                    }
                    newLogFormat=   (0xFF&bytes[2])<<0
                    |(0xFF&bytes[3])<<8
                    |(0xFF&bytes[4])<<16
                    |(0xFF&bytes[5])<<24;
                    if(newLogFormat!=logFormat) {
                        updateLogFormat(gpsFile, newLogFormat);
                    }
                }
                nextAddrToRead+=0x200;
                continueInBuffer=false;
            } else {
                /*******************************
                 * Not reading header - reading data.
                 */
                readResult=m_File.readBytes(bytes, 0, sizeToRead);
                if(readResult!=sizeToRead) {
                    (new MessageBox("Error","Problem reading|"+m_File.getPath()+"|"+m_File.lastError)).popupBlockingModal();                                   
                }
                nextAddrToRead+=sizeToRead;
            }
            
            
            /***************************************************************************
             * Interpret the data read in the Buffer as long as the records are complete
             */
            // A block of bytes has been read, read the records
            while(continueInBuffer) {
                
                /******************************************************
                 * Read special operations section (if any)
                 */
                boolean lookForRecord=true;
                while(lookForRecord&&(sizeToRead-16>offsetInBuffer)) {
                    // As long as this log entry is possible and 
                    //  bytes in buffer are sufficient.
                    if(   ((0xFF&bytes[offsetInBuffer+0])==0xAA)
                            &&((0xFF&bytes[offsetInBuffer+1])==0xAA)
                            &&((0xFF&bytes[offsetInBuffer+2])==0xAA)
                            &&((0xFF&bytes[offsetInBuffer+3])==0xAA)
                            &&((0xFF&bytes[offsetInBuffer+4])==0xAA)
                            &&((0xFF&bytes[offsetInBuffer+5])==0xAA)
                            &&((0xFF&bytes[offsetInBuffer+6])==0xAA)
                            &&((0xFF&bytes[offsetInBuffer+12])==0xBB)
                            &&((0xFF&bytes[offsetInBuffer+13])==0xBB)
                            &&((0xFF&bytes[offsetInBuffer+14])==0xBB)
                            &&((0xFF&bytes[offsetInBuffer+15])==0xBB)
                    ) {
                        int value=
                            (0xFF&bytes[offsetInBuffer+8])<<0
                            |(0xFF&bytes[offsetInBuffer+9])<<8
                            |(0xFF&bytes[offsetInBuffer+10])<<16
                            |(0xFF&bytes[offsetInBuffer+11])<<24
                            ;
                        // There is a special operation here
                        switch(0xFF&bytes[offsetInBuffer+7]) {
                        case 0x02: // logBitMaskChange
                            newLogFormat= value;
                            if(newLogFormat!=logFormat) {
                                updateLogFormat(gpsFile, newLogFormat);
                            }
                            break;
                        case 0x03: // log Period change
                            break;
                        case 0x04: // log distance change
                            break;
                        case 0x05: // log speed change
                            break;
                        case 0x07: // value: 0x0106= logger on  0x0107= logger off 0x104=??
                            break;
                        default:
                            break; // Added to set SW breakpoint to discover other records.
                        }
                            
                        // No data: on/off
                        offsetInBuffer+=16;
                    } else {
                        lookForRecord=false;
                    }
                }
                lookForRecord=true;
                
                /*********************************************
                 * Read data section(s) (if any)
                 */
                boolean foundRecord=false;
                boolean foundAnyRecord=false;
                int satRecords;
                do {
                    foundRecord=false;
                    
                    while(lookForRecord&&(sizeToRead>offsetInBuffer+minRecordSize+2)) {
                        // As long as record may fit in data still to read.
                        int indexInBuffer=offsetInBuffer;
                        int checkSum=0;
                        foundRecord=false;
                        satcnt=0;
                        satCntIdx=0;
                        satRecords=0;
                        
                        /***************************************
                         * Get some satellite record information.
                         */
                        if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) {
                            satCntIdx=offsetInBuffer-2+satIdxOffset+2;
                            satcnt=(0xFF&bytes[satCntIdx+2])<<0
                            |(0xFF&bytes[satCntIdx+3])<<8;
                            if(satcnt>32) {
                                // TODO: handle error
                                satcnt=32;
                            }
                            if(satcnt!=0) {
                                satRecords=satcnt*satRecSize-8;
                            }
                        }

//                        java.lang.System.out.print(recCount);
//                        java.lang.System.out.println(" ");
                        
                        /*******************************************
                         * Skip minimum number of bytes in a record.
                         */
                        while((indexInBuffer<minRecordSize+satRecords+offsetInBuffer)&&(indexInBuffer<sizeToRead-2)) {
                            checkSum^=bytes[indexInBuffer++];
                        }
                        
                        /*******************************************
                         * Skip until potential length found.
                         */
                        do {
                            if((bytes[indexInBuffer]=='*')
                                    &&((checkSum&0xFF)==(0xFF&bytes[indexInBuffer+1]))) {
                                indexInBuffer+=2; // Point just past end ('*' and checksum).
                                int recIdx=offsetInBuffer;
                                
                                offsetInBuffer=indexInBuffer;
                                okInBuffer=indexInBuffer;
                                foundRecord=true;


                                int rcrIdx=offsetInBuffer-2-((((logFormat&(1<<BT747_dev.FMT_DISTANCE_IDX))!=0)?8:0)+ 
                                        (((logFormat&(1<<BT747_dev.FMT_MILISECOND_IDX))!=0)?2:0)+ 
                                        (((logFormat&(1<<BT747_dev.FMT_RCR_IDX))!=0)?2:0));
                                recCount++;
                                //System.out.println(recCount);
                                foundAnyRecord=true;
                                /******************************************
                                 * Get all the information in the record.
                                 */
                                if(!passToFindFieldsActivatedInLog) {
                                    // Only interpret fiels if not looking for logFormat changes only
                                    if((logFormat&(1<<BT747_dev.FMT_UTC_IDX))!=0) {
                                        gpsRec.utc=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            |(0xFF&bytes[recIdx++])<<16
                                            |(0xFF&bytes[recIdx++])<<24
                                            ;
                                        gpsRec.utc+=timeOffsetSeconds;
                                    } else {
                                        gpsRec.utc= 1000;  // Value after earliest date
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_VALID_IDX))!=0) { 
                                        gpsRec.valid=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    } else {
                                        gpsRec.valid = 0xFFFF;
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_LATITUDE_IDX))!=0) {
                                        long latitude=
                                            (0xFFL&bytes[recIdx++])<<0
                                            |(0xFFL&bytes[recIdx++])<<8
                                            |(0xFFL&bytes[recIdx++])<<16
                                            |(0xFFL&bytes[recIdx++])<<24
                                            |(0xFFL&bytes[recIdx++])<<32
                                            |(0xFFL&bytes[recIdx++])<<40
                                            |(0xFFL&bytes[recIdx++])<<48
                                            |(0xFFL&bytes[recIdx++])<<56
                                            ;
                                        gpsRec.latitude=Convert.longBitsToDouble(latitude);
                                        
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_LONGITUDE_IDX))!=0) { 
                                        long longitude=
                                            (0xFFL&bytes[recIdx++])<<0
                                            |(0xFFL&bytes[recIdx++])<<8
                                            |(0xFFL&bytes[recIdx++])<<16
                                            |(0xFFL&bytes[recIdx++])<<24
                                            |(0xFFL&bytes[recIdx++])<<32
                                            |(0xFFL&bytes[recIdx++])<<40
                                            |(0xFFL&bytes[recIdx++])<<48
                                            |(0xFFL&bytes[recIdx++])<<56
                                            ;
                                        gpsRec.longitude=Convert.longBitsToDouble(longitude);
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_HEIGHT_IDX))!=0) { 
                                        int height=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            |(0xFF&bytes[recIdx++])<<16
                                            |(0xFF&bytes[recIdx++])<<24
                                            ;
                                        gpsRec.height=Convert.toFloatBitwise(height);
                                        if(noGeoid
                                                &&((logFormat&(1<<BT747_dev.FMT_LATITUDE_IDX))!=0)
                                                &&((logFormat&(1<<BT747_dev.FMT_LONGITUDE_IDX))!=0)
                                                ) {
                                            gpsRec.height-=Conv.wgs84_separation(gpsRec.latitude, gpsRec.longitude);
                                        }
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_SPEED_IDX))!=0) {
                                        int speed=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            |(0xFF&bytes[recIdx++])<<16
                                            |(0xFF&bytes[recIdx++])<<24
                                            ;
                                        gpsRec.speed=Convert.toFloatBitwise(speed);
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_HEADING_IDX))!=0) { 
                                        int heading=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            |(0xFF&bytes[recIdx++])<<16
                                            |(0xFF&bytes[recIdx++])<<24
                                            ;
                                        gpsRec.heading=Convert.toFloatBitwise(heading);
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_DSTA_IDX))!=0) { 
                                        gpsRec.dsta=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_DAGE_IDX))!=0) { 
                                        gpsRec.dage=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            |(0xFF&bytes[recIdx++])<<16
                                            |(0xFF&bytes[recIdx++])<<24
                                            ;
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_PDOP_IDX))!=0) { 
                                        gpsRec.pdop=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_HDOP_IDX))!=0) { 
                                        gpsRec.hdop=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_VDOP_IDX))!=0) { 
                                        gpsRec.vdop=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_NSAT_IDX))!=0) { 
                                        gpsRec.nsat=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    }
                                    idx=0;
                                    satidx=0;
                                    if(rcrIdx-recIdx>0) {
                                        idx=(0xFF&bytes[recIdx+2])<<0
                                            |(0xFF&bytes[recIdx+3])<<8;
                                        gpsRec.sid=new int[idx];
                                        gpsRec.sidinuse=new boolean[idx];
                                        gpsRec.ele=new int[idx];
                                        gpsRec.azi=new int[idx];
                                        gpsRec.snr=new int[idx];
                                        if(idx==0) {
                                            recIdx+=4;
                                        }
                                    }
                                    while (idx-->0) {
                                        if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) {
                                            gpsRec.sid[satidx]=
                                                (0xFF&bytes[recIdx++])<<0;
                                            gpsRec.sidinuse[satidx]=
                                                ((0xFF&bytes[recIdx++])<<0)!=0;
                                            if(true) {
                                            	// satcnt is not used - skipping with iffalse)
                                                satcnt=
                                                    (0xFF&bytes[recIdx++])<<0
                                                    |(0xFF&bytes[recIdx++])<<8;
                                            } else {
                                            	recIdx+=2;
                                            }
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_ELEVATION_IDX))!=0) {
                                            gpsRec.ele[satidx]=
                                                (0xFF&bytes[recIdx++])<<0
                                                |(0xFF&bytes[recIdx++])<<8
                                                ;
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_AZIMUTH_IDX))!=0) { 
                                            gpsRec.azi[satidx]=
                                                (0xFF&bytes[recIdx++])<<0
                                                |(0xFF&bytes[recIdx++])<<8
                                                ;
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_SNR_IDX))!=0) { 
                                            gpsRec.snr[satidx]=
                                                (0xFF&bytes[recIdx++])<<0
                                                |(0xFF&bytes[recIdx++])<<8
                                                ;
                                        }
                                        satidx++;
                                    }
                                    recIdx=rcrIdx;  // Sat information limit is rcrIdx
                                    if((logFormat&(1<<BT747_dev.FMT_RCR_IDX))!=0) { 
                                        gpsRec.rcr=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    } else
                                    {
                                        gpsRec.rcr=0xFFFF;  // For filter
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_MILISECOND_IDX))!=0) { 
                                        gpsRec.milisecond=
                                            (0xFF&bytes[recIdx++])<<0
                                            |(0xFF&bytes[recIdx++])<<8
                                            ;
                                    } else {
                                        gpsRec.milisecond=0;
                                    }
                                    if((logFormat&(1<<BT747_dev.FMT_DISTANCE_IDX))!=0) { 
                                        long distance=
                                            (0xFFL&bytes[recIdx++])<<0
                                            |(0xFFL&bytes[recIdx++])<<8
                                            |(0xFFL&bytes[recIdx++])<<16
                                            |(0xFFL&bytes[recIdx++])<<24
                                            |(0xFFL&bytes[recIdx++])<<32
                                            |(0xFFL&bytes[recIdx++])<<40
                                            |(0xFFL&bytes[recIdx++])<<48
                                            |(0xFFL&bytes[recIdx++])<<56
                                            ;
                                        gpsRec.distance=Convert.longBitsToDouble(distance);
                                    }
                                    gpsFile.writeRecord(gpsRec);
                                }
                                /* End handling record */
                                break;
                            } else {
                                checkSum^=0xFF&bytes[indexInBuffer++];
                            }
                        } while(!foundRecord&&(indexInBuffer<maxRecordSize+offsetInBuffer+2)&&(indexInBuffer<sizeToRead-2));
                        lookForRecord=foundRecord;
                    }
                } while(foundRecord);
                if(!foundAnyRecord) {
                    if(sizeToRead>offsetInBuffer+maxRecordSize+2) {
                        // Did not find any record - expected at least one.
                        // Try to recover.
                        offsetInBuffer++;
                    } else {
                        // There is not enough data in the buffer, we'll need to get some more.
                        continueInBuffer=false;
                    }   
                }
                
            } /* ContinueInBuffer*/
            if(okInBuffer>0) {
                nextAddrToRead-=(sizeToRead-okInBuffer);
            }
        } /* nextAddrToRead<fileSize */
    }
    
    public final void setTimeOffset(long offset) {
        timeOffsetSeconds= offset;
    }
    
    public final void setNoGeoid(boolean b) {
        noGeoid=b;
    }
    
    
    public final void toGPSFile(final String fileName, final GPSFile gpsFile, final int Card) {
        if(File.isAvailable()) {
            m_File=new File(fileName,File.READ_ONLY, Card);
            if(!m_File.isOpen()) {
                (new MessageBox("Error","Could not open|"+fileName+"|"+m_File.lastError)).popupBlockingModal();                                   
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
        if((logFormat&(1<<BT747_dev.FMT_MILISECOND_IDX))!=0) { 
            gpsRec.milisecond=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_DISTANCE_IDX))!=0) { 
            gpsRec.distance=-1;
        }
        
        /* End handling record */
        return gpsRec;
    }
    
    
}
