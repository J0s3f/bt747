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
import bt747.sys.Convert;
import waba.sys.Settings;

import gps.convert.Conv;

/**
 * @author Mario De Weerd
 * @author Herbert Geus (initial code for saving settings on WindowsCE)
  */
public class AppSettings implements gps.settings {
    static private final String CONFIG_FILE_NAME =
        //#if RXTX java.lang.System.getProperty("bt747_settings",

        (waba.sys.Settings.platform.startsWith("Win32")
         //#if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")  
         ) ?
            "SettingsBT747.pdb" : 
            "/My Documents/BT747/SettingsBT747.pdb"
         //#if RXTX )
         ; 

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
    private final static int C_GPXUTC0_IDX=C_NMEASET_IDX+C_NMEASET_SIZE;
    private final static int C_GPXUTC0_SIZE=1;
    private final static int C_TRKSEP_IDX=C_GPXUTC0_IDX+C_GPXUTC0_SIZE;
    private final static int C_TRKSEP_SIZE=4;
    private final static int C_ADVFILTACTIVE_IDX=C_TRKSEP_IDX+C_TRKSEP_SIZE;
    private final static int C_ADVFILTACTIVE_SIZE=1;
    private final static int C_minDist_IDX=C_ADVFILTACTIVE_IDX+C_ADVFILTACTIVE_SIZE;
    private final static int C_minDist_SIZE=8;
    private final static int C_maxDist_IDX=C_minDist_IDX+C_minDist_SIZE;
    private final static int C_maxDist_SIZE=8;
    private final static int C_minSpeed_IDX=C_maxDist_IDX+C_maxDist_SIZE;
    private final static int C_minSpeed_SIZE=8;
    private final static int C_maxSpeed_IDX=C_minSpeed_IDX+C_minSpeed_SIZE;
    private final static int C_maxSpeed_SIZE=8;
    private final static int C_maxHDOP_IDX=C_maxSpeed_IDX+C_maxSpeed_SIZE;
    private final static int C_maxHDOP_SIZE=8;
    private final static int C_maxPDOP_IDX=C_maxHDOP_IDX+C_maxHDOP_SIZE;
    private final static int C_maxPDOP_SIZE=8;
    private final static int C_maxVDOP_IDX=C_maxPDOP_IDX+C_maxPDOP_SIZE;
    private final static int C_maxVDOP_SIZE=8;
    private final static int C_minRecCount_IDX=C_maxVDOP_IDX+C_maxVDOP_SIZE;
    private final static int C_minRecCount_SIZE=8;
    private final static int C_maxRecCount_IDX=C_minRecCount_IDX+C_minRecCount_SIZE;
    private final static int C_maxRecCount_SIZE=8;
    private final static int C_minNSAT_IDX=C_maxRecCount_IDX+C_maxRecCount_SIZE;
    private final static int C_minNSAT_SIZE=4;
    private final static int C_GPXTRKSEGBIG_IDX=C_minNSAT_IDX+C_minNSAT_SIZE;
    private final static int C_GPXTRKSEGBIG_SIZE=1;
    private final static int C_DECODEGPS_IDX=C_GPXTRKSEGBIG_IDX+C_GPXTRKSEGBIG_SIZE;
    private final static int C_DECODEGPS_SIZE=4;
    private final static int C_COLOR_INVALIDTRACK_IDX=C_DECODEGPS_IDX+C_DECODEGPS_SIZE;
    private final static int C_COLOR_INVALIDTRACK_SIZE=8;
    private final static int C_NEXT_IDX=C_COLOR_INVALIDTRACK_IDX+C_COLOR_INVALIDTRACK_SIZE;
    // Next lines just to add new items faster using replace functions
    private final static int C_NEXT_SIZE=4;
    private final static int C_NEW_NEXT_IDX=C_NEXT_IDX+C_NEXT_SIZE;

    private final static int C_DEFAULT_DEVICE_TIMEOUT=3500; // ms
    private final static int C_DEFAULT_LOG_REQUEST_AHEAD=3;

    private String baseDirPath;
    private String logFile;
    private String reportFileBase;
    
    private boolean solveMacLagProblem=false;
    
    
    public AppSettings() {
        init();
        waba.sys.Vm.debug(CONFIG_FILE_NAME);
        //#if RXTX waba.sys.Vm.debug(java.lang.System.getProperty("bt747_settings"));
    }
    
    private boolean isWin32LikeDevice() {
        return waba.sys.Settings.platform.startsWith("WindowsCE")
        || waba.sys.Settings.platform.startsWith("PocketPC")
        ||(waba.sys.Settings.platform.startsWith("Win32")&&Settings.onDevice)
        ;
    }
    
    public void init() {
        String mVersion;
        int VersionX100=0;
        if(Settings.appSettings==null||Settings.appSettings.length()<100
            //#if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
          ) {
            Settings.appSettings=new String(new byte[2048]);
            if ( isWin32LikeDevice()
                    //#if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")  
                    //#if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
                    ) {
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
                try {
                    m_prefFile.close();
                } catch (Exception e) {
                    
                }
            }
        }
        //#if RXTX if(Convert.toInt(java.lang.System.getProperty("bt747_Mac_solvelag",java.lang.System.getProperty("os.name").startsWith("Mac")?"1":"0"))==1) {
        //#if RXTX         solveMacLagProblem=true;
        //#if RXTX     }

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
                File f=bt747.io.File.getCardVolume();
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
            setOneFilePerDay(0);
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
        case 6:
            setGpxUTC0(false);
            /* fall through */
        case 7:
            setTrkSep(60);
            /* fall through */
        case 8:
            setAdvFilterActive(false);
            setFilterMinRecCount(0);
            setFilterMaxRecCount(0);
            setFilterMinSpeed(0);
            setFilterMaxSpeed(0);
            setFilterMinDist(0);
            setFilterMaxDist(0);
            setFilterMaxPDOP(0);
            setFilterMaxHDOP(0);
            setFilterMaxVDOP(0);
            setFilterMinNSAT(0);
            /* fall through */
        case 9:
            setGpxTrkSegWhenBig(false);
            /* fall through */
        case 10:
            setGpsDecode(true);
            /* fall through */
        case 11:
            setColorInvalidTrack("FF0000");
            /* fall through */
        }
        setStringOpt("0.12",C_VERSION_IDX, C_VERSION_SIZE);
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
        if ( isWin32LikeDevice()
                //#if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")  
                //#if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
                ) {
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

    private final void setBooleanOpt(final boolean value, final int idx, final int size) {
        setStringOpt((value?"1":"0"),idx, size);
    }

    private final boolean getBooleanOpt(final int idx, final int size) {
        return getIntOpt(idx, size)==1;
    }

    private final void setFloatOpt(final float src, final int idx, final int size) {
        setOpt(Convert.unsigned2hex(Convert.toIntBitwise(src),size),idx,size);
    }

    private final float getFloatOpt(final int idx, final int size) {
        return Convert.toFloatBitwise(Conv.hex2Int(getStringOpt(idx,size)));
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
		return getBooleanOpt(C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE);
	}
	/**
	 * @param value The default value for opening the port.
	 */
	public void setStartupOpenPort(boolean value) {
        setBooleanOpt(value,C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE);
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
    public void setTrkPtValid(final int value) {
        setIntOpt(value,C_TRKPT_VALID_IDX, C_TRKPT_VALID_SIZE);
    }
    
    public int getFileSeparationFreq() {
        return getIntOpt(C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setOneFilePerDay(final int value) {
        setIntOpt(value,C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE);
    }
    
    public boolean getNoGeoid() {
        return getBooleanOpt(C_NOGEOID_IDX, C_NOGEOID_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setNoGeoid(final boolean value) {
        setBooleanOpt(value,C_NOGEOID_IDX, C_NOGEOID_SIZE);
    }

    public boolean getAdvFilterActive() {
        return getBooleanOpt(C_ADVFILTACTIVE_IDX, C_ADVFILTACTIVE_SIZE);
    }
    /**
     * @param value The default value for opening the port.
     */
    public void setAdvFilterActive(final boolean value) {
        setBooleanOpt(value,C_ADVFILTACTIVE_IDX, C_ADVFILTACTIVE_SIZE);
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

    public void setNMEAset(final int value) {
        setIntOpt(value,C_NMEASET_IDX, C_NMEASET_SIZE);
    }


    public boolean getGpxUTC0() {
        return getBooleanOpt(C_GPXUTC0_IDX, C_GPXUTC0_SIZE);
    }

    public void setGpxUTC0(final boolean value) {
        setBooleanOpt(value,C_GPXUTC0_IDX, C_GPXUTC0_SIZE);
    }
    public boolean getGpsDecode() {
        return getBooleanOpt(C_DECODEGPS_IDX, C_DECODEGPS_SIZE);
    }

    public void setGpsDecode(final boolean value) {
        setBooleanOpt(value,C_DECODEGPS_IDX, C_DECODEGPS_SIZE);
    }
    public boolean getGpxTrkSegWhenBig() {
        return getBooleanOpt(C_GPXTRKSEGBIG_IDX, C_GPXTRKSEGBIG_SIZE);
    }

    public void setGpxTrkSegWhenBig(final boolean value) {
        setBooleanOpt(value,C_GPXTRKSEGBIG_IDX, C_GPXTRKSEGBIG_SIZE);
    }

    public int getTrkSep() {
        return getIntOpt(C_TRKSEP_IDX, C_TRKSEP_SIZE);
    }

    public void setTrkSep(final int value) {
        setIntOpt(value,C_TRKSEP_IDX, C_TRKSEP_SIZE);
    }

    /**
     * @return Returns the maxDist.
     */
    public float getFilterMaxDist() {
        return getFloatOpt(C_maxDist_IDX, C_maxDist_SIZE);
    }
    /**
     * @param maxDist The maxDist to setFilter.
     */
    public void setFilterMaxDist(float maxDist) {
        setFloatOpt(maxDist,C_maxDist_IDX, C_maxDist_SIZE);
    }
    /**
     * @return Returns the maxHDOP.
     */
    public float getFilterMaxHDOP() {
        return getFloatOpt(C_maxHDOP_IDX, C_maxHDOP_SIZE);
    }
    
    /**
     * @param maxHDOP The maxHDOP to setFilter.
     */
    public void setFilterMaxHDOP(float maxHDOP) {
        setFloatOpt(maxHDOP,C_maxHDOP_IDX, C_maxHDOP_SIZE);
    }
    /**
     * @return Returns the maxPDOP.
     */
    public float getFilterMaxPDOP() {
        return getFloatOpt(C_maxPDOP_IDX, C_maxPDOP_SIZE);
    }
    /**
     * @param maxPDOP The maxPDOP to setFilter.
     */
    public void setFilterMaxPDOP(float maxPDOP) {
        setFloatOpt(maxPDOP,C_maxPDOP_IDX, C_maxPDOP_SIZE);
    }
    /**
     * @return Returns the maxRecCnt.
     */
    public int getFilterMaxRecCount() {
        return getIntOpt(C_maxRecCount_IDX, C_maxRecCount_SIZE);
    }
    /**
     * @param maxRecCnt The maxRecCnt to setFilter.
     */
    public void setFilterMaxRecCount(int maxRecCnt) {
        setIntOpt(maxRecCnt,C_maxRecCount_IDX, C_maxRecCount_SIZE);
    }
    /**
     * @return Returns the maxSpeed.
     */
    public float getFilterMaxSpeed() {
        return getFloatOpt(C_maxSpeed_IDX, C_maxSpeed_SIZE);
    }
    /**
     * @param maxSpeed The maxSpeed to setFilter.
     */
    public void setFilterMaxSpeed(float maxSpeed) {
        setFloatOpt(maxSpeed,C_maxSpeed_IDX, C_maxSpeed_SIZE);
    }
    /**
     * @return Returns the maxVDOP.
     */
    public float getFilterMaxVDOP() {
        return getFloatOpt(C_maxVDOP_IDX, C_maxVDOP_SIZE);
    }
    /**
     * @param maxVDOP The maxVDOP to setFilter.
     */
    public void setFilterMaxVDOP(float maxVDOP) {
        setFloatOpt(maxVDOP,C_maxVDOP_IDX, C_maxVDOP_SIZE);
    }
    /**
     * @return Returns the minDist.
     */
    public float getFilterMinDist() {
        return getFloatOpt(C_minDist_IDX, C_minDist_SIZE);
    }
    /**
     * @param minDist The minDist to setFilter.
     */
    public void setFilterMinDist(float minDist) {
        setFloatOpt(minDist,C_minDist_IDX, C_minDist_SIZE);
    }
    /**
     * @return Returns the minNSAT.
     */
    public int getFilterMinNSAT() {
        return getIntOpt(C_minNSAT_IDX, C_minNSAT_SIZE);
    }
    /**
     * @param minNSAT The minNSAT to setFilter.
     */
    public void setFilterMinNSAT(int minNSAT) {
        setIntOpt(minNSAT,C_minNSAT_IDX, C_minNSAT_SIZE);
    }
    /**
     * @return Returns the minRecCnt.
     */
    public int getFilterMinRecCount() {
        return getIntOpt(C_minRecCount_IDX, C_minRecCount_SIZE);
    }
    /**
     * @param minRecCnt The minRecCnt to setFilter.
     */
    public void setFilterMinRecCount(int minRecCnt) {
        setIntOpt(minRecCnt,C_minRecCount_IDX, C_minRecCount_SIZE);
    }
    /**
     * @return Returns the minSpeed.
     */
    public float getFilterMinSpeed() {
        return getFloatOpt(C_minSpeed_IDX, C_minSpeed_SIZE);
    }
    /**
     * @param minSpeed The minSpeed to setFilter.
     */
    public void setFilterMinSpeed(final float minSpeed) {
        setFloatOpt(minSpeed,C_minSpeed_IDX, C_minSpeed_SIZE);
    }

    public String getColorInvalidTrack() {
        return getStringOpt(C_COLOR_INVALIDTRACK_IDX, C_COLOR_INVALIDTRACK_SIZE);
    }
    
    public void setColorInvalidTrack(final String colorInvalidTrack) {
        setStringOpt(colorInvalidTrack, C_COLOR_INVALIDTRACK_IDX, C_COLOR_INVALIDTRACK_SIZE);
    }

    
    /**
     * @return Returns the solveMacLagProblem.
     */
    public boolean isSolveMacLagProblem() {
        return solveMacLagProblem;
    }
    /**
     * @param solveMacLagProblem The solveMacLagProblem to set.
     */
    public void setSolveMacLagProblem(boolean solveMacLagProblem) {
        this.solveMacLagProblem = solveMacLagProblem;
    }
}