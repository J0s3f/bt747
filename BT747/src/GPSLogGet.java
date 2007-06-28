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
import waba.ui.Button;
import waba.ui.Calendar;
import waba.ui.Check;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.ProgressBar;
import waba.util.Date;
import waba.util.Vector;

import gps.BT747LogConvert;
import gps.GPSCSVFile;
import gps.GPSFile;
import gps.GPSFilter;
import gps.GPSGPXFile;
import gps.GPSKMLFile;
import gps.GPSstate;

/**
 * @author Mario De Weerd
 */
public class GPSLogGet extends Container {
    GPSstate m_GPSstate;

    Check m_chkLogOnOff;;

    Button m_btStartDate;
    Date m_StartDate=new Date(1,1,1983);

    Button m_btEndDate;
    Date m_EndDate=new Date();

    Button m_btGetLog;

    Button m_btCancelGetLog;

    Button m_btToCSV;
    Button m_btToKML;
    Button m_btToGPX;
    
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

    // TODO: Just some code as a reference to find a path.
    void recursiveList(String path, Vector v) {
        if (path == null)
            return;
        File file = new File(path);
        String[] list = file.listFiles();
        if (list != null)
            for (int i = 0; i < list.length && i < 49; i++)
                if (list[i] != null) {
                    waba.sys.Vm.debug(list[i] + "\n"); //$NON-NLS-1$
                    v.addElement(path + list[i]);
                    if (list[i].endsWith("/")) // is a path? //$NON-NLS-1$
                        recursiveList(path + list[i], v);
                }
    }

    // TODO: Just some code as a reference to find a path.
    void nonRecursiveList(String path, Vector v) {
        if (path == null)
            return;
        File file = new File(path);
        String[] list = file.listFiles();
        if (list != null)
            for (int i = 0; i < list.length && i < 49; i++)
                if (list[i] != null) {
                    waba.sys.Vm.debug(list[i] + "\n"); //$NON-NLS-1$
                    v.addElement(path + list[i]);
                    //if (list[i].endsWith("/")) // is a path?
                    //	recursiveList(path+list[i],v);
                }
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
        add(new Label("Start date"), LEFT, AFTER); //$NON-NLS-1$
        add(m_btStartDate = new Button(m_StartDate.getDate()), AFTER, SAME); //$NON-NLS-1$
        //m_btStartDate.setMode(Edit.DATE);
        add(m_btEndDate = new Button(m_EndDate.getDate()), SAME, AFTER); //$NON-NLS-1$
        //m_btEndDate.setMode(Edit.DATE);
        add(new Label("End date"), BEFORE, SAME); 

        //add(new Label("End"),BEFORE,SAME);
        add(m_btGetLog = new Button("Get Log"), LEFT, AFTER + 10); //$NON-NLS-1$
        add(m_btCancelGetLog = new Button("Cancel get"), RIGHT, SAME); //$NON-NLS-1$

        add(m_btToCSV = new Button("To CSV"), LEFT, AFTER + 5); //$NON-NLS-1$
        add(m_btToGPX = new Button("To GPX"), CENTER, SAME); //$NON-NLS-1$
        add(m_btToKML = new Button("To KML"), RIGHT, SAME); //$NON-NLS-1$

        add(m_UsedLabel=new Label(   ""),LEFT, AFTER+3);
        add(m_RecordsLabel=new Label(""),LEFT, AFTER+3);
    }

    public void updateButtons() {
        boolean logIsActive=m_GPSstate.loggingIsActive;
        m_chkLogOnOff.setChecked(logIsActive);
        m_UsedLabel.setText(   "Mem Used   : "+Convert.toString(m_GPSstate.logMemUsed)
                +"("+Convert.toString(m_GPSstate.logMemUsedPercent)+"%)");
        m_UsedLabel.repaintNow();
        m_RecordsLabel.setText("Nbr records: "+Convert.toString(m_GPSstate.logNbrLogPts));
        m_RecordsLabel.repaintNow();
    }
    
    Calendar cal;
    Button calBt;
    Date calDate;
    
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
                int logRequestSize=0x10000;
                if(waba.sys.Settings.platform.startsWith("PalmOS")) {
                    logRequestSize=0x800;
                }
                //logRequestSize=0x800;
                m_GPSstate.getLogInit(0,            /* StartPosition */
                        m_GPSstate.logMemUsed-1,    /* EndPosition */
                        logRequestSize,             /* Size per request */
                        m_appSettings.getLogFile(), /* Log file name */
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
                if(event.target==m_btToGPX) {
                    gpsFile=new GPSGPXFile();
                    ext=".gpx";
                }
                    
                m_Filter.startDate=dateToUTCepoch1970(m_StartDate);
                m_Filter.endDate=dateToUTCepoch1970(m_EndDate)+(24*60*60-1);
                gpsFile.setFilter(m_Filter);
                // TODO: should get logformat associated with inputfile
                gpsFile.initialiseFile("/Palm/GPSDATA", ext);
                /* TODO: Recover the logFormat from a file or so */
                BT747LogConvert lc=new BT747LogConvert();
                lc.toGPSFile(m_appSettings.getLogFile(),gpsFile);
                gpsFile.finaliseFile();
                Convert.toString(3);
            } else if (event.target == this) {
                m_GPSstate.getLogCtrlInfo();
            } else if (event.target==null) {
                updateButtons();
            } else {
                event.consumed=false;
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
