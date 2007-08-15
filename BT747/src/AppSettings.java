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
import waba.io.File;
import waba.sys.Convert;
import waba.sys.Settings;

import gps.convert.Conv;

/**
 * @author Mario De Weerd
  */
public class AppSettings implements gps.settings {

    private final static int C_PORTNBR_IDX=0;
    private final static int C_PORTNBR_SIZE=8;
    private final static int C_BAUDRATE_IDX=C_PORTNBR_IDX+C_PORTNBR_SIZE;
    private final static int C_BAUDRATE_SIZE=8;
    private final static int C_VERSION_IDX=C_BAUDRATE_IDX+C_BAUDRATE_SIZE;
    private final static int C_VERSION_SIZE=8;
    private final static int C_BASEDIRPATH_IDX=C_VERSION_IDX+C_VERSION_SIZE;
    private final static int C_BASEDIRPATH_SIZE=256;
    private final static int C_REPORTFILEBASE_IDX=C_BASEDIRPATH_IDX+C_BASEDIRPATH_SIZE;
    private final static int C_REPORTFILEBASE_SIZE=40;
    private final static int C_LOGFILE_IDX=C_REPORTFILEBASE_IDX+C_REPORTFILEBASE_SIZE;
    private final static int C_LOGFILE_SIZE=40;
    private final static int C_OPENSTARTUP_IDX=C_LOGFILE_IDX+C_LOGFILE_SIZE;
    private final static int C_OPENSTARTUP_SIZE=40;
    private final static int C_CHUNKSIZE_IDX=C_OPENSTARTUP_IDX+C_OPENSTARTUP_SIZE;
    private final static int C_CHUNKSIZE_SIZE=8;
    private final static int C_DOWNLOADTIMEOUT_IDX=C_CHUNKSIZE_IDX+C_CHUNKSIZE_SIZE;
    private final static int C_DOWNLOADTIMEOUT_SIZE=8;
    private final static int C_CARD_IDX=C_DOWNLOADTIMEOUT_IDX+C_DOWNLOADTIMEOUT_SIZE;
    private final static int C_CARD_SIZE=4;
    private final static int C_TIMEOFFSETHOURS_IDX=C_CARD_IDX+C_CARD_SIZE;
    private final static int C_TIMEOFFSETHOURS_SIZE=4;
    private final static int C_DEFAULT_DEVICE_TIMEOUT=3500; // ms

    private String baseDirPath;
    private String logFile;
    private String reportFileBase;
    
    public AppSettings() {
        init();
    }
    
    public void init() {
        String mVersion;
        if(Settings.appSettings==null||Settings.appSettings.length()<100) {
            Settings.appSettings=new String(new byte[2048]);
        }
        mVersion=getStringOpt(C_VERSION_IDX, C_VERSION_SIZE);
        if((mVersion.length()<2)||(mVersion.charAt(1)!='.')) {
            defaultSettings();
            setStringOpt("0.01",C_VERSION_IDX, C_VERSION_SIZE);
        } else {
            //defaultSettings();
            getSettings();
        }
    }
    
    public void defaultSettings() {
        setPortnbr(0);
        setBaudRate(115200);
        if (waba.sys.Settings.platform.startsWith("Palm")) {
            setBaseDirPath("/Palm");
            setCard(-1);
        } else if ( waba.sys.Settings.platform.startsWith("WindowsCE")
                ||waba.sys.Settings.platform.startsWith("PocketPC") 
                )
        {
            File f=File.getCardVolume();
            if(f==null) {
                setBaseDirPath("/EnterYourDir");
            } else {
                setBaseDirPath(f.getPath());
            }
        } else {
            setBaseDirPath("/BT747");
        }
 
        setLogFile("BT747log.bin");
        setReportFileBase("GPSDATA");
        setStartupOpenPort(false);
        setChunkSize(waba.sys.Settings.onDevice?0x200:0x10000);
        setDownloadTimeOut( C_DEFAULT_DEVICE_TIMEOUT );
        getSettings();
    }

    public void getSettings() {
//        setPortnbr(0);
//        setBaudRate(115200);
        baseDirPath=getStringOpt(C_BASEDIRPATH_IDX, C_BASEDIRPATH_SIZE);
        reportFileBase=getStringOpt(C_REPORTFILEBASE_IDX, C_REPORTFILEBASE_SIZE);
        logFile=getStringOpt(C_LOGFILE_IDX, C_LOGFILE_SIZE);
    }

    private final void setOpt(final String src, final int idx, final int size) {
        Settings.appSettings=
            Settings.appSettings.substring(0,idx)
        +src.substring(0, (src.length()<(size))?src.length():size)
        +Settings.appSettings.substring((src.length()<(size-1))?idx+src.length():idx+size)
        ;
    }

    private final void setStringOpt(final String src, final int idx, final int size) {
        Settings.appSettings=
            Settings.appSettings.substring(0,idx)
        +src.substring(0, (src.length()<(size-1))?src.length():size-1)
        +"\0"
        +((src.length()<(size-1))?new String(new byte[size-src.length()-1]):"")
        +((Settings.appSettings.length()>idx+size)?
                Settings.appSettings.substring(idx+size,Settings.appSettings.length())
                :"")
        ;
    }

    private String getStringOpt(final int idx, final int size) {
        String s;
        int i;
        if(idx+size>Settings.appSettings.length()) {
            return "";
        } else {
            s=Settings.appSettings.substring(idx,idx+size);
            if((i=s.indexOf("\0"))!=-1) {
                return s.substring(0, i);
            } else {
                return s;
            }
        }
    }

    /**
	 * @return Returns the portnbr.
	 */
	public int getPortnbr() {
		return Conv.hex2Int(getStringOpt(C_PORTNBR_IDX, C_PORTNBR_SIZE));
	}
	/**
	 * @param portnbr The portnbr to set.
	 */
	public void setPortnbr(int portnbr) {
	    setOpt(Convert.unsigned2hex(portnbr,8),C_PORTNBR_IDX,C_PORTNBR_SIZE);
	}
    /**
     * @return The default baud rate
     */
	public int getBaudRate() {
		return Conv.hex2Int(getStringOpt(C_BAUDRATE_IDX, C_BAUDRATE_SIZE));
	}
	/**
	 * @param Baud The Baud rate to set as a default.
	 */
	public void setBaudRate(int Baud) {
        setOpt(Convert.unsigned2hex(Baud,8),C_BAUDRATE_IDX, C_BAUDRATE_SIZE);
	}

    /**
     * @return The default chunk size
     */
    public int getChunkSize() {
        // ChunkSize must be multiple of 2 
        int chunkSize=Conv.hex2Int(getStringOpt(C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE))&0xFFFFFFFE;
        if (chunkSize<16) {
            chunkSize=0x200;
        }
        return chunkSize;
    }
    /**
     * @param ChunkSize The ChunkSize  to set as a default.
     */
    public void setChunkSize(int ChunkSize) {
        setOpt(Convert.unsigned2hex(ChunkSize,8),C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE);
    }

    /**
    * @return The default chunk size
    */
   public int getDownloadTimeOut() {
       int DownloadTimeOut=Conv.hex2Int(getStringOpt(C_DOWNLOADTIMEOUT_IDX, C_DOWNLOADTIMEOUT_SIZE));
       if (DownloadTimeOut<=0) {
           DownloadTimeOut=0x200;
       }
       return DownloadTimeOut;
   }
   /**
    * @param DownloadTimeOut The DownloadTimeOut  to set as a default.
    */
   public void setDownloadTimeOut(int DownloadTimeOut) {
       setOpt(Convert.unsigned2hex(DownloadTimeOut,C_DOWNLOADTIMEOUT_SIZE),C_DOWNLOADTIMEOUT_IDX, C_DOWNLOADTIMEOUT_SIZE);
   }

   /**
    * @return The default chunk size
    */
   public int getCard() {
       int Card=Conv.hex2Int(getStringOpt(C_CARD_IDX, C_CARD_SIZE));
       if (Card<=0) {
           Card=0x200;
       }
       return Card;
   }
   /**
    * @param Card The Card  to set as a default.
    */
   public void setCard(int Card) {
       setOpt(Convert.unsigned2hex(Card,C_CARD_SIZE),C_CARD_IDX, C_CARD_SIZE);
   }
   
   /**
    * @return The default chunk size
    */
   public int getTimeOffsetHours() {
       int timeOffsetHours=Conv.hex2Int(getStringOpt(C_TIMEOFFSETHOURS_IDX, C_TIMEOFFSETHOURS_SIZE));
       if(timeOffsetHours>100) {
           timeOffsetHours-=0x10000;
       }
       return timeOffsetHours;
   }
   /**
    * @param timeOffsetHours The TIMEOFFSETHOURS  to set as a default.
    */
   public void setTimeOffsetHours(int timeOffsetHours) {
       setOpt(Convert.unsigned2hex(timeOffsetHours,C_TIMEOFFSETHOURS_SIZE),C_TIMEOFFSETHOURS_IDX, C_TIMEOFFSETHOURS_SIZE);
   }
   
	public boolean getStartupOpenPort() {
		return Conv.hex2Int(getStringOpt(C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE))==1;
	}
	/**
	 * @param value The default value for opening the port.
	 */
	public void setStartupOpenPort(boolean value) {
        setStringOpt((value?"1":"0"),C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE);
	}
	
    /** The location of the logFile
     * 
     */
    /**
     * @return Returns the logFile full path.
     */
    public String getLogFilePath() {
        return baseDirPath+"/"+logFile;
    }

    public String getLogFile() {
        return logFile;
    }
    
    /**
     * @param logFile The logFile to set.
     */
    public void setLogFile(String logFile) {
        this.logFile = logFile;
        setStringOpt(this.logFile, C_LOGFILE_IDX, C_LOGFILE_SIZE);
    }
    
    public String getBaseDirPath() {
        return baseDirPath;
    }
    
    public void setBaseDirPath(String baseDirPath) {
        this.baseDirPath = baseDirPath;
        setStringOpt(this.baseDirPath, C_BASEDIRPATH_IDX, C_BASEDIRPATH_SIZE);
    }
    
    public String getReportFileBase() {
        return reportFileBase;
    }
    public void setReportFileBase(String reportFileBase) {
        this.reportFileBase = reportFileBase;
        setStringOpt(this.reportFileBase, C_REPORTFILEBASE_IDX, C_REPORTFILEBASE_SIZE);
    }

    public String getReportFileBasePath() {
        return this.baseDirPath+"/"+reportFileBase;
    }
}
