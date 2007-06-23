/*
* Created on 22 juin 2007
*
* TODO To change the template for this generated file go to
* Window - Preferences - Java - Code Style - Code Templates
*/
package gps;

import waba.io.File;

/**
* @author Mario De Weerd
*
* TODO To change the template for this generated type comment go to
* Window - Preferences - Java - Code Style - Code Templates
*/
public class BT747LogConvert {
    
    
    public final static void toCSV(final String fileName, final int logFormat) {
        File m_File=null; 
        if(File.isAvailable()) {
            m_File=new File(fileName,File.READ_ONLY);
            if(!m_File.isOpen()) {
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
                                        offsetInBuffer=indexInBuffer;
                                        okInBuffer=indexInBuffer;
                                        foundRecord=true;
                                        recCount++;
                                        //System.out.println(recCount);
                                        foundAnyRecord=true;
                                        //TODO: Handle record (here or further)
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
    
    
    
}
