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

/** This class is used to convert the binary log to a new format.
 * Basically this class interprets the log and creates a {@link GPSRecord}.
 * The {@link GPSRecord} is then sent to the {@link GPSFile} class object to write it
 * to the output.
 * 
 * @author Mario De Weerd
 */
public final class BT747LogConvert {
    private File m_File=null;
    private long timeOffsetSeconds=0;
    protected boolean passToFindFieldsActivatedInLog= false;
    protected int activeFileFields=0;
    
    public final void parseFile(final GPSFile gpsFile) {
        int logFormat=0;
        GPSRecord gpsRec=new GPSRecord();
        final int C_BUF_SIZE=0x800;
        byte[] bytes=new byte[C_BUF_SIZE];
        int sizeToRead;
        int nextAddrToRead;
        int recCount;
        int fileSize;
        int minRecordSize=16;
        int maxRecordSize=16;
        int satidx;
        int idx;        recCount=0;
        nextAddrToRead=0;
        fileSize=m_File.getSize();
        while(nextAddrToRead<fileSize) {
            int okInBuffer=-1; // Last ending position in buffer
            
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
            
            // Read the bytes
            int readResult;
            boolean continueInBuffer=true;
            int offsetInBuffer=0;
            int newLogFormat;

            m_File.setPos(nextAddrToRead);
            if((nextAddrToRead&0xFFFF)==0) {
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
                        logFormat=newLogFormat;
                        activeFileFields|=logFormat;
                        if(!passToFindFieldsActivatedInLog) {
                            gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
                        }
                        minRecordSize=BT747_dev.logRecordMinSize(logFormat);
                        maxRecordSize=BT747_dev.logRecordMaxSize(logFormat);
                    }
                }
                nextAddrToRead+=0x200;
                continueInBuffer=false;
            } else {
                readResult=m_File.readBytes(bytes, 0, sizeToRead);
                if(readResult!=sizeToRead) {
                    (new MessageBox("Error","Problem reading|"+m_File.getPath()+"|"+m_File.lastError)).popupBlockingModal();                                   
                }
                nextAddrToRead+=sizeToRead;
            }
            
            
            // A block of bytes has been read, read the records
            while(continueInBuffer) {
                
                // Skipping on/off parts
                boolean checkForOnOff=true;
                while(checkForOnOff&&(sizeToRead-16>offsetInBuffer)) {
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
                                logFormat=newLogFormat;
                                activeFileFields|=logFormat;
                                if(!passToFindFieldsActivatedInLog) {
                                    gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
                                }
                                minRecordSize=BT747_dev.logRecordMinSize(logFormat);
                                maxRecordSize=BT747_dev.logRecordMaxSize(logFormat);
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
                        checkForOnOff=false;
                    }
                }
                
                checkForOnOff=true;
                boolean foundRecord=false;
                boolean foundAnyRecord=false;
                // Now look for data
                do {
                    foundRecord=false;
                    while(checkForOnOff&&(sizeToRead>offsetInBuffer+maxRecordSize+2)) {
                        int indexInBuffer=offsetInBuffer;
                        int checkSum=0;
                        foundRecord=false;
                        while(indexInBuffer<minRecordSize+offsetInBuffer) {
                            checkSum^=bytes[indexInBuffer++];
                        }
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
                                /* Handle record */
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
                                    }
                                    while (idx-->0) {
                                        if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) {
                                            gpsRec.sid[satidx]=
                                                (0xFF&bytes[recIdx++])<<0;
                                            gpsRec.sidinuse[satidx]=
                                                ((0xFF&bytes[recIdx++])<<0)!=0;
                                            if(false) {
                                            	// satcnt is not used - skipping with iffalse)
                                                int satcnt=
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
                        } while(!foundRecord&&(indexInBuffer<maxRecordSize+offsetInBuffer+2));
                        if(!foundRecord) {
                            // Should have found a re
                            checkForOnOff=false;
                            checkForOnOff=foundRecord;
                        }
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
    
    
    public final void toGPSFile(final String fileName, final GPSFile gpsFile, final int Card) {
        if(File.isAvailable()) {
            m_File=new File(fileName,File.READ_ONLY, Card);
            if(!m_File.isOpen()) {
                Vm.debug("Could not open "+fileName);
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
