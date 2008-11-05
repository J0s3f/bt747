/*
 * Created on 22 juin 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gps;

import waba.io.File;
import waba.sys.Convert;
import waba.sys.Vm;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class BT747LogConvert {
    
    
    public final static void toGPSFile(final String fileName, final int logFormat,
            final GPSFile gpsFile) {
        File m_File=null; 
        GPSRecord gpsRec=new GPSRecord();
        if(File.isAvailable()) {
            m_File=new File(fileName,File.READ_ONLY);
            if(!m_File.isOpen()) {
                Vm.debug("Could not open "+fileName);
                m_File=null;
            } else {
                final int C_BUF_SIZE=0x800;
                byte[] bytes=new byte[C_BUF_SIZE];
                int offset;
                int sizeToRead;
                int nextAddrToRead;
                int recCount;
                int bufIdx;
                int fileSize;
                int minRecordSize=BT747_dev.logRecordMinSize(logFormat);
                int maxRecordSize=minRecordSize; /* TODO: Determine correct value */
                recCount=0;
                offset=0;
                nextAddrToRead=0;
                fileSize=m_File.getSize();
                boolean endOfBlockInBuffer=false;                
                while(nextAddrToRead<fileSize) {
                    int okInBuffer=-1; // Last ending position in buffer
                    
                    // Determine size to read
                    if((nextAddrToRead&0xFFFF)<0x200) {
                        nextAddrToRead=(nextAddrToRead&0xFFFF0000)|0x200;
                    }
                    int endOfBlock=(nextAddrToRead&0xFFFF0000)|0xFFFF;
                    sizeToRead=endOfBlock+1-nextAddrToRead;
                    if(sizeToRead>C_BUF_SIZE) {
                        sizeToRead=C_BUF_SIZE;
                        endOfBlockInBuffer=false;
                    } else {
                        endOfBlockInBuffer=true;
                    }
                    if((sizeToRead+nextAddrToRead)>fileSize) { // TODO: check formula
                        sizeToRead=fileSize-nextAddrToRead;
                        endOfBlockInBuffer=false;
                    }
                    
                    // Read the bytes
                    int readResult;
                    m_File.setPos(nextAddrToRead);
                    readResult=m_File.readBytes(bytes, 0, sizeToRead);
                    nextAddrToRead+=sizeToRead;
                    int offsetInBuffer=0;
                    boolean continueInBuffer=true;
                    
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
                                        recCount++;
                                        //System.out.println(recCount);
                                        foundAnyRecord=true;
                                        //TODO: Handle record (here or further)
                                        /* Handle record */
                                        if((logFormat&(1<<BT747_dev.FMT_UTC_IDX))!=0) {
                                            gpsRec.utc=
                                                (0xFF&bytes[recIdx++])<<0
                                                |(0xFF&bytes[recIdx++])<<8
                                                |(0xFF&bytes[recIdx++])<<16
                                                |(0xFF&bytes[recIdx++])<<24
                                                ;
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_VALID_IDX))!=0) { 
                                            gpsRec.valid=
                                                (0xFF&bytes[recIdx++])<<0
                                                |(0xFF&bytes[recIdx++])<<8
                                                ;
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
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) { 
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_ELEVATION_IDX))!=0) { 
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_AZIMUTH_IDX))!=0) { 
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_SNR_IDX))!=0) { 
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_RCR_IDX))!=0) { 
                                            gpsRec.rcr=
                                                (0xFF&bytes[recIdx++])<<0
                                                |(0xFF&bytes[recIdx++])<<8
                                                ;
                                        }
                                        if((logFormat&(1<<BT747_dev.FMT_MILISECOND_IDX))!=0) { 
                                            gpsRec.milisecond=
                                                (0xFF&bytes[recIdx++])<<0
                                                |(0xFF&bytes[recIdx++])<<8
                                                ;
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
                                        /* End handling record */
                                        break;
                                    } else {
                                        checkSum^=0xFF&bytes[indexInBuffer++];
                                    }
                                } while(!foundRecord&&(indexInBuffer<maxRecordSize));
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
                    
                }
            } /* nextAddrToRead<fileSize */
        }
        
        if(m_File!=null) {
            m_File.close();
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
            //gpsRec.nsat=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_SID_IDX))!=0) { 
            //gpsRec.=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_ELEVATION_IDX))!=0) { 
            //gpsRec.=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_AZIMUTH_IDX))!=0) { 
            //gpsRec.=-1;
        }
        if((logFormat&(1<<BT747_dev.FMT_SNR_IDX))!=0) { 
            //gpsRec.s=-1;
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