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
import waba.sys.Convert;
import waba.ui.Button;
import waba.ui.Calendar;
import waba.ui.Check;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.ProgressBar;
import waba.util.Date;

import gps.BT747LogConvert;
import gps.GPSCSVFile;
import gps.GPSFile;
import gps.GPSFilter;
import gps.GPSGPXFile;
import gps.GPSKMLFile;
import gps.GPSPLTFile;
import gps.GPSstate;
import gps.GpsEvent;

/**
 * @author Mario De Weerd
 */
public class GPSLogGet extends Container {
    GPSstate m_GPSstate;

    Check m_chkLogOnOff;;
    Check m_chkLogOverwriteStop;;

    Button m_btStartDate;
    Date m_StartDate=new Date(1,1,1983);

    Button m_btEndDate;
    Date m_EndDate=new Date();

    Button m_btGetLog;

    Button m_btCancelGetLog;

    Button m_btToCSV;
    Button m_btToKML;
    Button m_btToGPX;
    Button m_btToPLT;

    ComboBox m_cbTimeOffsetHours;
    private final static String[] offsetStr = {
            "-12", "-11","-10","-9","-8","-7","-6","-5","-4","-3","-2","-1",
            "+0",
            "+1","+2","+3","+4","+5","+6","+7","+8","+9","+10","+11","+12"
    };
    
    Check m_chkIncremental;
  
    final static int JULIAN_DAY_1_1_1970=18264;   
    ProgressBar m_pb;
    AppSettings m_appSettings;
    
    private Label       m_UsedLabel;
    private Label       m_RecordsLabel;

    GPSFilter m_Filter;

    public int dateToUTCepoch1970(final Date d) {
        return (d.getJulianDay()-JULIAN_DAY_1_1_1970)*24*60*60;
    }
    
    public GPSLogGet(GPSstate state, ProgressBar pb, AppSettings s, GPSFilter filter) {
        m_GPSstate = state;
        m_pb= pb;
        m_appSettings= s;
        m_Filter=filter;
    } 
    

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Container#onStart() TODO: Handle date fields, ...
     */
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        add(m_chkLogOnOff = new Check("Device log on(/off)"), LEFT, TOP); //$NON-NLS-1$
        add(m_chkIncremental = new Check("Incremental"), RIGHT, SAME); //$NON-NLS-1$
        m_chkIncremental.setChecked(true);
        add(m_chkLogOverwriteStop = new Check("Log overwrite(/stop) when full"), LEFT, AFTER); //$NON-NLS-1$
        add(new Label("Start date"), LEFT, AFTER); //$NON-NLS-1$
        add(m_btStartDate = new Button(m_StartDate.getDate()), AFTER, SAME); //$NON-NLS-1$
        //m_btStartDate.setMode(Edit.DATE);
        add(m_btEndDate = new Button(m_EndDate.getDate()), SAME, AFTER); //$NON-NLS-1$
        //m_btEndDate.setMode(Edit.DATE);
        add(new Label("End date"), BEFORE, SAME); 
        add(m_cbTimeOffsetHours=new ComboBox(offsetStr),RIGHT, SAME);
        int offsetIdx=m_appSettings.getTimeOffsetHours()+12;
        if(offsetIdx>24) {
            m_appSettings.setTimeOffsetHours(0);
            offsetIdx=12;
        }
        m_cbTimeOffsetHours.select(offsetIdx);
        add(new Label("UTC"), BEFORE, SAME); 

        //add(new Label("End"),BEFORE,SAME);
        add(m_btGetLog = new Button("Get Log"), LEFT, AFTER + 5); //$NON-NLS-1$
        add(m_btCancelGetLog = new Button("Cancel get"), RIGHT, SAME); //$NON-NLS-1$

        add(m_btToCSV = new Button("To CSV"), LEFT, AFTER + 5); //$NON-NLS-1$
        add(m_btToGPX = new Button("To GPX"), CENTER, SAME); //$NON-NLS-1$
        add(m_btToKML = new Button("To KML"), RIGHT, SAME); //$NON-NLS-1$

        add(m_btToPLT = new Button("To PLT"), LEFT, AFTER + 2); //$NON-NLS-1$
//        add(m_btToGPX = new Button("To GPX"), CENTER, SAME); //$NON-NLS-1$
//        add(m_btToKML = new Button("To KML"), RIGHT, SAME); //$NON-NLS-1$

        add(m_UsedLabel=new Label(   ""),LEFT, AFTER+3);
        add(m_RecordsLabel=new Label(""),LEFT, AFTER+3);
    }

    public void updateButtons() {
        m_chkLogOnOff.setChecked(m_GPSstate.loggingIsActive);
        m_chkLogOnOff.repaintNow();
        m_chkLogOverwriteStop.setChecked(m_GPSstate.logFullOverwrite);
        m_chkLogOverwriteStop.repaintNow();
        m_UsedLabel.setText(   "Mem Used   : "+Convert.toString(m_GPSstate.logMemUsed)
                +"("+Convert.toString(m_GPSstate.logMemUsedPercent)+"%)");
        m_UsedLabel.repaintNow();
        m_RecordsLabel.setText("Nbr records: "+Convert.toString(m_GPSstate.logNbrLogPts));
        m_RecordsLabel.repaintNow();
    }
    
    private Calendar cal;
    private Button calBt;
    
    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Control#onEvent(waba.ui.Event) TODO : Make filename
     *      configureable.
     */
    public void onEvent(Event event) {
        // TODO Auto-generated method stub
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed=true;
            if (event.target == m_btGetLog) {
                // TODO: Get start log nbr and end log nbr to get
                // actual data from dates.
                //m_btStartDate;
                //m_btEndDate;
                //  m_GPSstate.getLogInit(0,1000,100,m_cbFile.getSelectedItem()+"Test.txt");
                //	m_GPSstate.getLogInit(0,32*1024*1024,100,"/Palm/BT747log.bin");
                // TODO: Optimize download on PalmOS.
                int logRequestSize=m_appSettings.getChunkSize(); // Short by default
                m_GPSstate.getLogInit(0,            /* StartPosition */
                        m_GPSstate.logMemUsed-1,    /* EndPosition */
                        logRequestSize,             /* Size per request */
                        m_appSettings.getLogFilePath(), /* Log file name */
                        m_appSettings.getCard(),    /* Card for file operations */
                        m_chkIncremental.getChecked(), /* Incremental download */
                        m_pb                        /* ProgressBar */
                        ); //$NON-NLS-1$
                m_btGetLog.press(false);
            } else if (event.target == m_btCancelGetLog) {
                m_GPSstate.cancelGetLog();
            } else if (event.target == m_chkLogOnOff) {
                if(m_chkLogOnOff.getChecked()) {
                    // TODO: Update button status after update (propagate some event from State
                    m_GPSstate.startLog();
                } else {
                    m_GPSstate.stopLog();
                }
                m_GPSstate.getLogOnOffStatus();
            } else if (event.target == m_cbTimeOffsetHours) {
                // Work around superwaba bug
                String tmp=(String)m_cbTimeOffsetHours.getSelectedItem();
                if(tmp.charAt(0)=='+') {
                    m_appSettings.setTimeOffsetHours(Convert.toInt((String)tmp.substring(1)));
                } else {
                    m_appSettings.setTimeOffsetHours(Convert.toInt(tmp));
                }
            } else if (event.target == m_chkLogOverwriteStop) {
                    m_GPSstate.setLogOverwrite(m_chkLogOverwriteStop.getChecked());
                m_GPSstate.getLogOverwrite();
            } else if (event.target == m_btEndDate) {
                if (cal == null) {
                    cal = new Calendar();
                }
                calBt=m_btEndDate;
                cal.popupModal();
            } else if (event.target == m_btStartDate) {
                if (cal == null) {
                    cal = new Calendar();
                }
                calBt=m_btStartDate;
                cal.popupModal();
            } else if (event.target==m_btToCSV
                    ||event.target==m_btToKML
                    ||event.target==m_btToPLT
                    ||event.target==m_btToGPX) {
                String ext="";
                GPSFile gpsFile=null;

                if(event.target==m_btToCSV) {
                    gpsFile=new GPSCSVFile();
                    ext=".csv";
                }
                if(event.target==m_btToKML) {
                    gpsFile=new GPSKMLFile();
                    ext=".kml";
                }
                if(event.target==m_btToPLT) {
                    gpsFile=new GPSPLTFile();
                    ext=".plt";
                }
                if(event.target==m_btToGPX) {
                    gpsFile=new GPSGPXFile();
                    ext=".gpx";
                }
                    
                m_Filter.setStartDate(dateToUTCepoch1970(m_StartDate));
                m_Filter.setEndDate(dateToUTCepoch1970(m_EndDate)+(24*60*60-1));
                gpsFile.setFilter(m_Filter);
                // TODO: should get logformat associated with inputfile
                gpsFile.initialiseFile(m_appSettings.getReportFileBasePath(), ext, m_appSettings.getCard());
                /* TODO: Recover the logFormat from a file or so */
                BT747LogConvert lc=new BT747LogConvert();
                lc.setTimeOffset(m_appSettings.getTimeOffsetHours()*3600);
                lc.toGPSFile(m_appSettings.getLogFilePath(),gpsFile,m_appSettings.getCard());
                gpsFile.finaliseFile();
                Convert.toString(3);
            } else if (event.target == this) {
                m_GPSstate.getLogCtrlInfo();
            } else {
                event.consumed=false;
            }
            break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                updateButtons();
                event.consumed=true;
            }
            break;
        case ControlEvent.WINDOW_CLOSED:
            if (event.target == cal)
            {
                Date d = cal.getSelectedDate();
                if(d!=null) {
                    calBt.setText(d.toString());
                    // Can't change the value of the date, changing all
                    m_StartDate=new Date(m_btStartDate.getText());
                    m_EndDate=new Date(m_btEndDate.getText());
                }
            }
            /*cal = null;
            Note: If your program uses the Calendar control only a few
            times, i suggest that you set cal to null so it can get garbage
            collected. Calendar objects waste memory and it is always a
            good idea to save memory when possible*/
            break;
        }
    }
}
