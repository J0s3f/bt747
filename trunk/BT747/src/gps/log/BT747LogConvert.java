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
import gps.convert.Conv;

import bt747.Txt;
import bt747.io.File;
import bt747.sys.Convert;
import bt747.sys.Vm;
import bt747.ui.MessageBox;

/** This class is used to convert the binary log to a new format.
 * Basically this class interprets the log and creates a {@link GPSRecord}.
 * The {@link GPSRecord} is then sent to the {@link GPSFile} class object to write it
 * to the output.
 * 
 * @author Mario De Weerd
 */
public final class BT747LogConvert implements GPSLogConvert {
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
    private boolean holux=false;
    
    private int badrecord_count=0;
    
    private final void updateLogFormat(final GPSFile gpsFile,final int newLogFormat) {
        int bits=newLogFormat;
        int index = 0;
        int total = 0;
        int [] byteSizes;
        
        if(holux) {
            byteSizes= BT747_dev.logFmtByteSizesHolux;
        } else {
            byteSizes= BT747_dev.logFmtByteSizes;
        }
        satRecSize=0;
        
        logFormat=newLogFormat;
        activeFileFields|=logFormat;
        if(!passToFindFieldsActivatedInLog) {
            gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
        }
        if((logFormat&0x80000000)!=0) {
            holux=true;
        }
        minRecordSize=BT747_dev.logRecordMinSize(logFormat, holux);
        maxRecordSize=BT747_dev.logRecordMaxSize(logFormat, holux);
        do {
            if ((bits&1)!=0) {
                switch (index) {
                case BT747_dev.FMT_LATITUDE_IDX:
                case BT747_dev.FMT_LONGITUDE_IDX:
                    total+=byteSizes[index];
                break;
                case BT747_dev.FMT_HEIGHT_IDX:
                    total+=byteSizes[index];
                break;
                
                case BT747_dev.FMT_SID_IDX:
                case BT747_dev.FMT_ELEVATION_IDX:
                case BT747_dev.FMT_AZIMUTH_IDX:
                case BT747_dev.FMT_SNR_IDX:
                    satRecSize+=byteSizes[index];
                break;
                case BT747_dev.FMT_RCR_IDX:
                case BT747_dev.FMT_MILLISECOND_IDX:
                case BT747_dev.FMT_DISTANCE_IDX:
                    
                    // These fields do not contribute to the sat offset
                    break;
                default:
                    // Other fields contribute
                    total+=byteSizes[index];
                break;
                }
            }
            index++;
        } while((bits>>>=1) != 0);
        satIdxOffset=total;
    }    
    
    /**
     * @return
     * @param gpsFile - object doing actual write to files
     * 
     */
    public final void parseFile(final GPSFile gpsFile) {
        GPSRecord gpsRec=new GPSRecord();
        final int C_BUF_SIZE=0x10000;
        byte[] bytes=new byte[C_BUF_SIZE];
        int sizeToRead;
        int nextAddrToRead;
        int recCount;
        int fileSize;
        int satCntIdx;
        int satcnt;
        
        recCount=0;
        logFormat=0;
        nextAddrToRead=0;
        badrecord_count=0;
        try {
            fileSize=m_File.getSize();
        } catch (Exception e) {
            // TODO: handle exception
            fileSize=0;
        }
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
            
            try {
                m_File.setPos(nextAddrToRead);
            } catch (Exception e) {
                // TODO: handle exception
            }
            
            if((nextAddrToRead&0xFFFF)==0) {
                /******************************
                 * This is the header.
                 * Only 20 bytes are read - just enough to get the log format.  
                 */
                if(sizeToRead>=20) {
                    readResult=0;
                    // Read header (20 bytes is enough)
                    try {
                        readResult=m_File.readBytes(bytes, 0, 20);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    if(readResult!=20) {
                        (new MessageBox(
                                Txt.ERROR,
                                Txt.PROBLEM_READING+m_File.getPath()+"|"+m_File.lastError)).popupBlockingModal();                                   
                    }
                    newLogFormat=   (0xFF&bytes[2])<<0
                    |(0xFF&bytes[3])<<8
                    |(0xFF&bytes[4])<<16
                    |(0xFF&bytes[5])<<24;
                    if(newLogFormat==0xFFFFFFFF) {
                        // TODO: Treat error
                        if(logFormat==0) {
                            newLogFormat=0x8000001D; // Supposing holux M-241
                        } else {
                            newLogFormat=logFormat;
                        }
                    }
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
                try {
                    readResult=m_File.readBytes(bytes, 0, sizeToRead);
                } catch (Exception e) {
                    // TODO: handle exception
                    readResult=0;
                }
                if(readResult!=sizeToRead) {
                    (new MessageBox(
                            Txt.ERROR,
                            Txt.PROBLEM_READING+m_File.getPath()+"|"+m_File.lastError)).popupBlockingModal();                                   
                }
                nextAddrToRead+=sizeToRead;
            }
            /**
             * Data read from file into local buffer
             ***************************************************************************/
            
            
            /***************************************************************************
             * Interpret the data read in the local buffer
             */
            while(continueInBuffer) {
                boolean lookForRecord=true;

                while(lookForRecord
                        &&(sizeToRead-16>offsetInBuffer)  // Enough bytes in buffer
                        ) {
                    int nbrBytes;
                    nbrBytes=getSpecialRecord(bytes, offsetInBuffer, gpsFile);
                    lookForRecord=(nbrBytes!=0);
                    offsetInBuffer+=nbrBytes;
                }
                
                /*********************************************
                 * Look for a record
                 */
                boolean foundRecord=false;
                boolean foundAnyRecord=false;
                int satRecords;
                
                if(
                        (sizeToRead>offsetInBuffer+minRecordSize+(holux?1:2)) // Enough bytes in buffer
                        ) { // As long as record may fit in data still to read.
                    int indexInBuffer=offsetInBuffer;
                    int checkSum=0;
                    int allFF=0xFF; // If 0xFF, all bytes are FF.
                    foundRecord=false;
                    satcnt=0;
                    satCntIdx=0;
                    satRecords=0;
                    
                    /***************************************
                     * Get some satellite record information.
                     */
                    if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) {
                        satCntIdx=offsetInBuffer+satIdxOffset;
                        satcnt=(0xFF&bytes[satCntIdx+2])<<0
                        |(0xFF&bytes[satCntIdx+3])<<8;
                        if( (satcnt>32)||(satcnt<0)) {
                            // TODO: handle error  [but ok when end of block or end of file]
                            satcnt=32;
                        }
                        if(satcnt!=0) {
                            satRecords=satcnt*satRecSize-4;
                        }
                    }
                    
                    /*******************************************
                     * Skip minimum number of bytes in a record.
                     */
                    while((indexInBuffer<minRecordSize+satRecords+offsetInBuffer)&&(indexInBuffer<sizeToRead-2)) {
                        allFF&=bytes[indexInBuffer];
                        checkSum^=bytes[indexInBuffer++];
                    }
                    
                    if(   (allFF!=0xFF)
                            &&((!holux &&((bytes[indexInBuffer]=='*')
                                    &&((checkSum&0xFF)==(0xFF&bytes[indexInBuffer+1])))
                            )
                            ||(holux && ((checkSum&0xFF)==(0xFF&bytes[indexInBuffer])))
                            )
                    ) {
                        if (!holux) {
                            indexInBuffer+=2; // Point just past end ('*' and checksum).
                        } else {
                            indexInBuffer+=1;
                        }
                        
                        int recIdx=offsetInBuffer;
                        
                        offsetInBuffer=indexInBuffer;
                        okInBuffer=indexInBuffer;
                        foundRecord=true;
                        
                        
                        int rcrIdx;  // Offset to first field after sat data.
                        if(!holux) {
                            rcrIdx=offsetInBuffer-2-
                            ((((logFormat&(1<<BT747_dev.FMT_DISTANCE_IDX))!=0)?
                                    BT747_dev.logFmtByteSizes[BT747_dev.FMT_DISTANCE_IDX]:0)+ 
                                    (((logFormat&(1<<BT747_dev.FMT_MILLISECOND_IDX))!=0)?BT747_dev.logFmtByteSizes[BT747_dev.FMT_MILLISECOND_IDX]:0)+ 
                                    (((logFormat&(1<<BT747_dev.FMT_RCR_IDX))!=0)?BT747_dev.logFmtByteSizes[BT747_dev.FMT_RCR_IDX]:0));
                        } else {
                            rcrIdx=offsetInBuffer-1-
                            ((((logFormat&(1<<BT747_dev.FMT_DISTANCE_IDX))!=0)?
                                    BT747_dev.logFmtByteSizesHolux[BT747_dev.FMT_DISTANCE_IDX]:0)+ 
                                    (((logFormat&(1<<BT747_dev.FMT_MILLISECOND_IDX))!=0)?BT747_dev.logFmtByteSizesHolux[BT747_dev.FMT_MILLISECOND_IDX]:0)+ 
                                    (((logFormat&(1<<BT747_dev.FMT_RCR_IDX))!=0)?BT747_dev.logFmtByteSizesHolux[BT747_dev.FMT_RCR_IDX]:0));
                        }
                        
                        recCount++;
                        //System.out.println(recCount);

                        foundAnyRecord=true;
                        
                        /******************************************
                         * Get all the information in the record.
                         */
                        gpsRec.recCount=recCount;
                        if(!passToFindFieldsActivatedInLog) {
                            // Only interpret fields if not looking for logFormat changes only

                            //Vm.debug(Convert.unsigned2hex(nextAddrToRead-sizeToRead+recIdx, 8)); // record start position
                            // Vm.debug("Offset:"+recIdx+" "+offsetInBuffer);
                            boolean valid;
                            
                            // Retrieve the record from the file (in buffer).
                            valid=getRecord(bytes, gpsRec, recIdx, rcrIdx, satcnt);
                            if(valid) {
                                gpsFile.writeRecord(gpsRec);
                            } else {
                                badrecord_count++;
                            }
                        }
                        /**
                         * Information from record retrieved
                         ******************************************/
                    } else {
                        // Problem in checksum, data format, ... .
                        if(allFF!=0xFF) {
                            badrecord_count++;
                        }
                    }
                    lookForRecord=foundRecord;
                }  // End if (or while previously) for possible good record.
                
                if(!foundAnyRecord) {
                    if(sizeToRead>offsetInBuffer+maxRecordSize+(holux?1:2)) {  // TODO: recover when 16 bytes available too.
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
    
    public final void setTimeOffset(final long offset) {
        timeOffsetSeconds= offset;
    }
    
    public final void setNoGeoid(final boolean b) {
        noGeoid=b;
    }
    
    public final void toGPSFile(final String fileName, final GPSFile gpsFile, final int Card) {
        if(File.isAvailable()) {
            try {
                m_File=new File(fileName,File.READ_ONLY, Card);
            } catch (Exception e) {
                // TODO: handle exception
            }
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
                if(gpsFile.getFilesCreated()==0) {
                    (new MessageBox(
                            Txt.WARNING,
                            Txt.NO_FILES_WERE_CREATED)).popupBlockingModal();                                   
                }
            }
            
            if(m_File!=null) {
                try {
                    m_File.close();
                } catch (Exception e) {
                    // TODO: handle exception
                }
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
        if((logFormat&(1<<BT747_dev.FMT_MILLISECOND_IDX))!=0) { 
            gpsRec.milisecond=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_DISTANCE_IDX))!=0) { 
            gpsRec.distance=-1;
        }
        
        /* End handling record */
        return gpsRec;
    }
    
    /**
     * @param holux The holux to set.
     */
    public void setHolux(boolean holux) {
        this.holux = holux;
    }
    
    /**
     * Tries to find a special record at the indicated offset.
     * @return int / number of bytes found
     */
    private final int getSpecialRecord(
            final byte[] bytes,
            final int offsetInBuffer,
            GPSFile gpsFile) {
        int newLogFormat;
        int nbrBytesDone=0;
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
                //bt747.sys.Vm.debug("Log format set to :"+Convert.unsigned2hex(value, 8));
                break;
            case 0x03: // log Period change
                //bt747.sys.Vm.debug("Log period set to :"+value);
                break;
            case 0x04: // log distance change
                //bt747.sys.Vm.debug("Log distance set to :"+value);
                break;
            case 0x05: // log speed change
                //bt747.sys.Vm.debug("Log speed set to :"+value);
                break;
            case 0x06: // value: 0x0106= logger on  0x0107= logger off 0x104=??
                //bt747.sys.Vm.debug("Logger off :"+value);
            case 0x07: // value: 0x0106= logger on  0x0107= logger off 0x104=??
                //bt747.sys.Vm.debug("Logger off :"+value);
                break;
            default:
                break; // Added to set SW breakpoint to discover other records.
            
            }
            // No data: on/off
            nbrBytesDone+=16;
        } else if(   ((0xFF&bytes[offsetInBuffer+0])=='H')
                &&((0xFF&bytes[offsetInBuffer+1])=='O')
                &&((0xFF&bytes[offsetInBuffer+2])=='L')
                &&((0xFF&bytes[offsetInBuffer+3])=='U')
                &&((0xFF&bytes[offsetInBuffer+4])=='X')
        ) {
            
            // No data: on/off
            if(!holux) {
                holux = true; // currently set like this
                updateLogFormat(gpsFile, logFormat);
            }
            nbrBytesDone+=16;
            if(//   ((0xFF&bytes[offsetInBuffer+5])=='G')
               // &&((0xFF&bytes[offsetInBuffer+6])=='R')
               // &&((0xFF&bytes[offsetInBuffer+7])=='2')
               // &&((0xFF&bytes[offsetInBuffer+8])=='4')
               // &&((0xFF&bytes[offsetInBuffer+9])=='1')
               // &&
                  ((0xFF&bytes[offsetInBuffer+10])=='W')
                &&((0xFF&bytes[offsetInBuffer+11])=='A')
                &&((0xFF&bytes[offsetInBuffer+12])=='Y')
                &&((0xFF&bytes[offsetInBuffer+13])=='P')
                &&((0xFF&bytes[offsetInBuffer+14])=='N')
                &&((0xFF&bytes[offsetInBuffer+15])=='T')
                ) {
//                Vm.debug("Holux Waypoint");
                }
        }
        return nbrBytesDone;
    }

    
    /**
     * Tries to find a normal record at the indicated offset.
     * @return true if success
     */
    private final boolean getRecord(
            final byte[] bytes, // The data string
            GPSRecord gpsRec,
            final int startIdx,
            final int rcrIdx,
            final int satcnt) {
        int recIdx;
        boolean valid;
        int satidx;
        int idx;
        recIdx=startIdx;
        valid=true;
        
        if((logFormat&(1<<BT747_dev.FMT_UTC_IDX))!=0) {
            gpsRec.utc=
                (0xFF&bytes[recIdx++])<<0
                |(0xFF&bytes[recIdx++])<<8
                |(0xFF&bytes[recIdx++])<<16
                |(0xFF&bytes[recIdx++])<<24
                ;
            if(gpsRec.utc==0xFFFFFFFF) {
                valid=false;
            }
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
            if(!holux) {
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
            } else {
                int latitude=
                    (0xFF&bytes[recIdx++])<<0
                    |(0xFF&bytes[recIdx++])<<8
                    |(0xFF&bytes[recIdx++])<<16
                    |(0xFF&bytes[recIdx++])<<24
                    ;
                gpsRec.latitude=Convert.toFloatBitwise(latitude);
            }
            if(gpsRec.latitude>90.00
                    ||gpsRec.latitude<-90.00) {
                valid=false;
            }
        }
        if((logFormat&(1<<BT747_dev.FMT_LONGITUDE_IDX))!=0) {
            if(!holux) {
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
            } else {
                int longitude=
                    (0xFF&bytes[recIdx++])<<0
                    |(0xFF&bytes[recIdx++])<<8
                    |(0xFF&bytes[recIdx++])<<16
                    |(0xFF&bytes[recIdx++])<<24
                    ;
                gpsRec.longitude=Convert.toFloatBitwise(longitude);//*1.0;
            }
            if(gpsRec.longitude>180.00
                    ||gpsRec.latitude<-180.00) {
                valid=false;
            }
        }
        if((logFormat&(1<<BT747_dev.FMT_HEIGHT_IDX))!=0) { 
            if(!holux) {
                int height=
                    (0xFF&bytes[recIdx++])<<0
                    |(0xFF&bytes[recIdx++])<<8
                    |(0xFF&bytes[recIdx++])<<16
                    |(0xFF&bytes[recIdx++])<<24
                    ;
                gpsRec.height=Convert.toFloatBitwise(height);
            } else {
                int height=
                    
                    (0xFF&bytes[recIdx++])<<8
                    |(0xFF&bytes[recIdx++])<<16
                    |(0xFF&bytes[recIdx++])<<24
                    ;
                gpsRec.height=Convert.toFloatBitwise(height);
            }
            if(noGeoid
                    &&((logFormat&(1<<BT747_dev.FMT_LATITUDE_IDX))!=0)
                    &&((logFormat&(1<<BT747_dev.FMT_LONGITUDE_IDX))!=0)
                    &&valid
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
        if(satcnt==idx) {
            while (idx-->0) {
                if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) {
                    gpsRec.sid[satidx]=
                        (0xFF&bytes[recIdx++])<<0;
                    gpsRec.sidinuse[satidx]=
                        ((0xFF&bytes[recIdx++])<<0)!=0;
                    //                if(false) {
                    //                    // satcnt is not used - skipping with iffalse)
                    //                    satcnt=
                    //                        (0xFF&bytes[recIdx++])<<0
                    //                        |(0xFF&bytes[recIdx++])<<8;
                    //                } else {
                    recIdx+=2;
                    //                }
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
        } else {
            Vm.debug("Problem in sat decode");
        }
        //Vm.debug("Offset1:"+recIdx+" "+rcrIdx);
        if(recIdx!=rcrIdx) {
            Vm.debug("Problem in sat decode (end idx)");
        }
        recIdx=rcrIdx;  // Sat information limit is rcrIdx
        if((logFormat&(1<<BT747_dev.FMT_RCR_IDX))!=0) { 
            gpsRec.rcr=
                (0xFF&bytes[recIdx++])<<0
                |(0xFF&bytes[recIdx++])<<8
                ;
        } else
        {
            gpsRec.rcr=0x0001;  // For filter
        }
        if((logFormat&(1<<BT747_dev.FMT_MILLISECOND_IDX))!=0) { 
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
        
        return valid;
        
    }
}
