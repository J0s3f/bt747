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
 * @author Herbert Geus (initial code for saving settings on WindowsCE)
  */
public class AppSettings implements gps.settings {
    static private final String CONFIG_FILE_NAME = waba.sys.Settings.platform.startsWith("Win32") ?
            "SettingsBT747.pdb" : 
            "/My Documents/BT747/SettingsBT747.pdb"; 

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
    private final static int C_WAYPT_RCR_IDX=C_TIMEOFFSETHOURS_IDX+C_TIMEOFFSETHOURS_SIZE;
    private final static int C_WAYPT_RCR_SIZE=4;
    private final static int C_WAYPT_VALID_IDX=C_WAYPT_RCR_IDX+C_WAYPT_RCR_SIZE;
    private final static int C_WAYPT_VALID_SIZE=4;
    private final static int C_TRKPT_RCR_IDX=C_WAYPT_VALID_IDX+C_WAYPT_VALID_SIZE;
    private final static int C_TRKPT_RCR_SIZE=4;
    private final static int C_TRKPT_VALID_IDX=C_TRKPT_RCR_IDX+C_TRKPT_RCR_SIZE;
    private final static int C_TRKPT_VALID_SIZE=4;
    private final static int C_ONEFILEPERDAY_IDX=C_TRKPT_VALID_IDX+C_TRKPT_VALID_SIZE;
    private final static int C_ONEFILEPERDAY_SIZE=1;
    private final static int C_NOGEOID_IDX=C_ONEFILEPERDAY_IDX+C_ONEFILEPERDAY_SIZE;
    private final static int C_NOGEOID_SIZE=4;
    private final static int C_LOGAHEAD_IDX=C_NOGEOID_IDX+C_NOGEOID_SIZE;
    private final static int C_LOGAHEAD_SIZE=1;
    private final static int C_NMEASET_IDX=C_LOGAHEAD_IDX+C_LOGAHEAD_SIZE;
    private final static int C_NMEASET_SIZE=8;
    private final static int C_NEXT_IDX=C_NMEASET_IDX+C_NMEASET_SIZE;
    
    // Next lines just to add new items faster using replace functions
    private final static int C_NEXT_SIZE=4;
    private final static int C_NEW_NEXT_IDX=C_NEXT_IDX+C_NEXT_SIZE;
    
    private final static int C_DEFAULT_DEVICE_TIMEOUT=3500; // ms
    private final static int C_DEFAULT_LOG_REQUEST_AHEAD=3;

    private String baseDirPath;
    private String logFile;
    private String reportFileBase;
    
    public AppSettings() {
        init();
    }
    
    private boolean isWin32LikeDevice() {
        return waba.sys.Settings.platform.startsWith("WindowsCE")
        || waba.sys.Settings.platform.startsWith("PocketPC")
        ||(waba.sys.Settings.platform.startsWith("Win32")&&Settings.onDevice);
    }
    
    public void init() {
        String mVersion;
        int VersionX100=0;
        if(Settings.appSettings==null||Settings.appSettings.length()<100) {
            Settings.appSettings=new String(new byte[2048]);
            if ( isWin32LikeDevice() ) {
                int readLength = 0;
                
                //waba.sys.Vm.debug("on Device "+waba.sys.Settings.platform);
                //waba.sys.Vm.debug("loading config file "+CONFIG_FILE_NAME);
                File m_prefFile = new File("");
                try {
                    m_prefFile = new File(CONFIG_FILE_NAME,File.READ_ONLY);
                } catch (Exception e) {
                    //            Vm.debug("Exception new log create");
                }
                readLength = m_prefFile.getSize();
                if (readLength >= 100)
                {
                    byte[] appSettingsArray = new byte[2048];
                    
                    m_prefFile.readBytes(appSettingsArray, 0, readLength);
                    Settings.appSettings = new String(appSettingsArray);
                }
                m_prefFile.close();
            }
        }
        mVersion=getStringOpt(C_VERSION_IDX, C_VERSION_SIZE);
        if((mVersion.length()==4)&&(mVersion.charAt(1)=='.')) {
            getSettings();
            VersionX100=Convert.toInt(mVersion.charAt(0)+mVersion.substring(2,4));
        }  
        updateSettings(VersionX100);
    }
    
    private void updateSettings(final int versionX100) {
        switch(versionX100) {
        case 0:
            setPortnbr(0);
            setBaudRate(115200);
            setCard(-1);
            if (waba.sys.Settings.platform.startsWith("Palm")) {
                setBaseDirPath("/Palm");
            } else if ( isWin32LikeDevice() ) {
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
            setChunkSize(waba.sys.Settings.onDevice?220:0x10000);
            setDownloadTimeOut( C_DEFAULT_DEVICE_TIMEOUT );
            /* fall through */
        case 1: 
            setFilterDefaults();
            /* fall through */
        case 2:
            /* fall through */
            setOneFilePerDay(false);
            /* fall through */
        case 3:
            setNoGeoid(false);
            /* fall through */
        case 4:
            setLogRequestAhead(C_DEFAULT_LOG_REQUEST_AHEAD);
            /* fall through */
        case 5:
            setNMEAset(0x0002000A);
            /* fall through */
        }
        setStringOpt("0.06",C_VERSION_IDX, C_VERSION_SIZE);
        getSettings();
    }
    
    public void defaultSettings() {
        updateSettings(0);
    }
    
    private void setFilterDefaults() {
        setTrkPtValid(0xFFFFFFFE);
        setTrkPtRCR(0xFFFFFFFF);
        setWayPtValid(0xFFFFFFFE);
        setWayPtRCR(0x00000008);
    }
    
    public void saveSettings() {
        if ( isWin32LikeDevice() ) {
//            waba.sys.Vm.debug("on Device "+waba.sys.Settings.platform);
//            waba.sys.Vm.debug("saving config file "+CONFIG_FILE_NAME);
            File m_prefFile=new File("");
            try {
                File m_Dir=new File(CONFIG_FILE_NAME.substring(0, CONFIG_FILE_NAME.lastIndexOf('/')),
                                    File.DONT_OPEN);
                if(!m_Dir.exists()) {
                    m_Dir.createDir();
                }
            } catch (Exception e) {
//                Vm.debug("Exception new log delete");
            }
            try {
                m_prefFile=new File(CONFIG_FILE_NAME,File.DONT_OPEN);
                if(m_prefFile.exists()) {
                    m_prefFile.delete();
                }
            } catch (Exception e) {
//                Vm.debug("Exception new log delete");
            }
            try {
                m_prefFile=new File(CONFIG_FILE_NAME,File.CREATE);
                m_prefFile.close();
                m_prefFile=new File(CONFIG_FILE_NAME,File.READ_WRITE);
                m_prefFile.writeBytes(Settings.appSettings.getBytes(), 0, Settings.appSettings.length());
                m_prefFile.close();
            } catch (Exception e) {
//                Vm.debug("Exception new log create");
            }
//            waba.sys.Vm.debug("saved config file length "+Settings.appSettings.length());
        }
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

    private final void setIntOpt(final int src, final int idx, final int size) {
        setOpt(Convert.unsigned2hex(src,size),idx,size);
    }

    private final int getIntOpt(final int idx, final int size) {
        return Conv.hex2Int(getStringOpt(idx,size));
    }
    
    

    private final void setStringOpt(final String src, final int idx, final int size) {
        Settings.appSettings=
            Settings.appSettings.substring(0,idx)
        +src.substring(0, (src.length()<(size-1))?src.length():size)
        +(src.length()<(size-1)?"\0":"")
        +((src.length()<(size-1))?new String(new byte[size-src.length()-1]):"")
        +((Settings.appSettings.length()>idx+size)?
                Settings.appSettings.substring(idx+size,Settings.appSettings.length())
                :"")
        ;
    }

    private final String getStringOpt(final int idx, final int size) {
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
		return getIntOpt(C_PORTNBR_IDX, C_PORTNBR_SIZE);
	}
	/**
	 * @param portnbr The portnbr to set.
	 */
	public void setPortnbr(int portnbr) {
	    setIntOpt(portnbr,C_PORTNBR_IDX,C_PORTNBR_SIZE);
	}
    /**
     * @return The default baud rate
     */
	public int getBaudRate() {
		return getIntOpt(C_BAUDRATE_IDX, C_BAUDRATE_SIZE);
	}
	/**
	 * @param Baud The Baud rate to set as a default.
	 */
	public void setBaudRate(int Baud) {
        setIntOpt(Baud,C_BAUDRATE_IDX, C_BAUDRATE_SIZE);
	}

    /**
     * @return The default chunk size
     */
    public int getChunkSize() {
        // ChunkSize must be multiple of 2 
        int chunkSize=getIntOpt(C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE)&0xFFFFFFFE;
        if (chunkSize<16) {
            chunkSize=0x200;
        }
        return chunkSize;
    }
    /**
     * @param ChunkSize The ChunkSize  to set as a default.
     */
    public void setChunkSize(int ChunkSize) {
        setIntOpt(ChunkSize,C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE);
    }

    /**
    * @return The default chunk size
    */
   public int getDownloadTimeOut() {
       int DownloadTimeOut=getIntOpt(C_DOWNLOADTIMEOUT_IDX, C_DOWNLOADTIMEOUT_SIZE);
       if (DownloadTimeOut<=0) {
           DownloadTimeOut=0x200;
       }
       return DownloadTimeOut;
   }
   /**
    * @param DownloadTimeOut The DownloadTimeOut  to set as a default.
    */
   public void setDownloadTimeOut(int DownloadTimeOut) {
       setIntOpt(DownloadTimeOut,C_DOWNLOADTIMEOUT_IDX, C_DOWNLOADTIMEOUT_SIZE);
   }

   /**
    * @return The default chunk size
    */
   public int getCard() {
       int Card=getIntOpt(C_CARD_IDX, C_CARD_SIZE);
       if (Card<=0||Card>=255) {
           Card=-1;
       }
       return Card;
   }
   /**
    * @param Card The Card  to set as a default.
    */
   public void setCard(int Card) {
       setIntOpt(Card,C_CARD_IDX, C_CARD_SIZE);
   }
   
   /**
    * @return The default chunk size
    */
   public int getTimeOffsetHours() {
       int timeOffsetHours=getIntOpt(C_TIMEOFFSETHOURS_IDX, C_TIMEOFFSETHOURS_SIZE);
       if(timeOffsetHours>100) {
           timeOffsetHours-=0x10000;
       }
       return timeOffsetHours;
   }
   /**
    * @param timeOffsetHours The TIMEOFFSETHOURS  to set as a default.
    */
   public void setTimeOffsetHours(int timeOffsetHours) {
       setIntOpt(timeOffsetHours,C_TIMEOFFSETHOURS_IDX, C_TIMEOFFSETHOURS_SIZE);
   }
   
	public boolean getStartupOpenPort() {
		return getIntOpt(C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE)==1;
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

    public int getWayPtRCR() {
        return getIntOpt(C_WAYPT_RCR_IDX, C_WAYPT_RCR_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setWayPtRCR(int value) {
        setIntOpt(value,C_WAYPT_RCR_IDX, C_WAYPT_RCR_SIZE);
    }
    
    public int getWayPtValid() {
        return getIntOpt(C_WAYPT_VALID_IDX, C_WAYPT_VALID_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setWayPtValid(int value) {
        setIntOpt(value,C_WAYPT_VALID_IDX, C_WAYPT_VALID_SIZE);
    }

    public int getTrkPtRCR() {
        return getIntOpt(C_TRKPT_RCR_IDX, C_TRKPT_RCR_SIZE);
    }

    /**
     * @param value The default value for opening the port.
     */
    public void setTrkPtRCR(int value) {
        setIntOpt(value,C_TRKPT_RCR_IDX, C_TRKPT_RCR_SIZE);
    }
    
    public int getTrkPtValid() {
        return getIntOpt(C_TRKPT_VALID_IDX, C_TRKPT_VALID_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setTrkPtValid(int value) {
        setIntOpt(value,C_TRKPT_VALID_IDX, C_TRKPT_VALID_SIZE);
    }
    
    public boolean getOneFilePerDay() {
        return getIntOpt(C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE)==1;
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setOneFilePerDay(boolean value) {
        setStringOpt((value?"1":"0"),C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE);
    }
    
    public boolean getNoGeoid() {
        return getIntOpt(C_NOGEOID_IDX, C_NOGEOID_SIZE)==1;
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setNoGeoid(boolean value) {
        setStringOpt((value?"1":"0"),C_NOGEOID_IDX, C_NOGEOID_SIZE);
    }

    public int getLogRequestAhead() {
        return getIntOpt(C_LOGAHEAD_IDX, C_LOGAHEAD_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setLogRequestAhead(int value) {
        setIntOpt(value,C_LOGAHEAD_IDX, C_LOGAHEAD_SIZE);
    }

    public int getNMEAset() {
        return getIntOpt(C_NMEASET_IDX, C_NMEASET_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setNMEAset(int value) {
        setIntOpt(value,C_NMEASET_IDX, C_NMEASET_SIZE);
    }
}
