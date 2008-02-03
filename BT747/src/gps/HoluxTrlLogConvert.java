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

import bt747.io.File;
import bt747.sys.Convert;
import bt747.ui.MessageBox;

import gps.convert.Conv;

/** This class is used to convert the binary log to a new format.
 * Basically this class interprets the log and creates a {@link GPSRecord}.
 * The {@link GPSRecord} is then sent to the {@link GPSFile} class object to write it
 * to the output.
 * 
 * @author Mario De Weerd
 */
public final class HoluxTrlLogConvert implements GPSLogConvert {
    private int minRecordSize;
    private int maxRecordSize;
    private int logFormat;
    private File m_File=null;
    private long timeOffsetSeconds=0;
    protected boolean passToFindFieldsActivatedInLog= false;
    protected int activeFileFields=
        (1<<BT747_dev.FMT_UTC_IDX)
        |(1<<BT747_dev.FMT_LATITUDE_IDX)
        |(1<<BT747_dev.FMT_LONGITUDE_IDX)
        |(1<<BT747_dev.FMT_HEIGHT_IDX)
        ;
    
    private boolean noGeoid=false; // If true,remove geoid difference from height
    
    private int satIdxOffset;
    private int satRecSize;
    
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
        
        if(!passToFindFieldsActivatedInLog) {
            gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
        }

        minRecordSize=15;
        maxRecordSize=minRecordSize;
        
        recCount=0;
        logFormat=0;
        nextAddrToRead=0;
        fileSize=m_File.getSize();
        while(nextAddrToRead+minRecordSize+1<fileSize) {
            int okInBuffer=-1; // Last ending position in buffer
            
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
                (new MessageBox("Error","Problem reading|"+m_File.getPath()+"|"+m_File.lastError)).popupBlockingModal();                                   
            }
            nextAddrToRead+=sizeToRead;
            
            /***************************************************************************
             * Interpret the data read in the Buffer as long as the records are complete
             */
            // A block of bytes has been read, read the records
            while(sizeToRead>offsetInBuffer+minRecordSize) {
                // As long as record may fit in data still to read.
                int indexInBuffer=offsetInBuffer;
                int checkSum=0;
                
                while((indexInBuffer<minRecordSize+offsetInBuffer)&&(indexInBuffer<sizeToRead-1)) {
                    checkSum^=bytes[indexInBuffer++];
                }
                
                indexInBuffer+=1;
                
                int recIdx=offsetInBuffer;
                offsetInBuffer=indexInBuffer;
                
                recCount++;
                
                if(((checkSum&0xFF)==(0xFF&bytes[indexInBuffer-1]))) {
                    /******************************************
                     * Get all the information in the record.
                     */
                    gpsRec.recCount=recCount;
                    if(!passToFindFieldsActivatedInLog) {
                        gpsRec.valid = 0xFFFF;
                        gpsRec.rcr=0x0001;  // For filter
                        // Only interpret fiels if not looking for logFormat changes only
                        gpsRec.utc=
                            (0xFF&bytes[recIdx++])<<0
                            |(0xFF&bytes[recIdx++])<<8
                            |(0xFF&bytes[recIdx++])<<16
                            |(0xFF&bytes[recIdx++])<<24
                            ;
                        gpsRec.utc+=timeOffsetSeconds;
                        
                        int latitude=
                            (0xFF&bytes[recIdx++])<<0
                            |(0xFF&bytes[recIdx++])<<8
                            |(0xFF&bytes[recIdx++])<<16
                            |(0xFF&bytes[recIdx++])<<24
                            ;
                        gpsRec.latitude=Convert.toFloatBitwise(latitude);
                        
                        int longitude=
                            (0xFF&bytes[recIdx++])<<0
                            |(0xFF&bytes[recIdx++])<<8
                            |(0xFF&bytes[recIdx++])<<16
                            |(0xFF&bytes[recIdx++])<<24
                            ;
                        gpsRec.longitude=Convert.toFloatBitwise(longitude);//*1.0;
                        
                        int height=
                            
                            (0xFF&bytes[recIdx++])<<8
                            |(0xFF&bytes[recIdx++])<<16
                            |(0xFF&bytes[recIdx++])<<24
                            ;
                        gpsRec.height=Convert.toFloatBitwise(height);
                        if(noGeoid) {
                            gpsRec.height-=Conv.wgs84_separation(gpsRec.latitude, gpsRec.longitude);
                        }
                        gpsFile.writeRecord(gpsRec);
                    }
                }
            } /* ContinueInBuffer*/
            nextAddrToRead-=(sizeToRead-offsetInBuffer);
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
        
        gpsRec.utc=-1;
        gpsRec.latitude=-1;
        gpsRec.longitude=-1;
        gpsRec.height=-1;
        
        /* End handling record */
        return gpsRec;
    }
    
    
}
