package bt747.waba_view;
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
import waba.sys.Settings;
import waba.ui.Button;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;

import gps.BT747_dev;
import gps.GpsEvent;

import bt747.Txt;
import bt747.control.Controller;
import bt747.model.Model;


/**
 * @author Mario De Weerd
 */
public class GPSLogFormat extends Container {
    private static final int C_LOG_FMT_COUNT = 21;
    /** The object that is used to communicate with the GPS device. */
    private Model m;
    private Controller c;
    /** The tickboxes for the format items */
    private MyCheck [] chkLogFmtItems =new MyCheck[C_LOG_FMT_COUNT];
    /** The button that requests to change the log format of the device */
    private Button m_btChangeFormatErase;
    private Button m_btChangeFormat;
    private Button m_btErase;

    private Label m_lbEstNbrRecords;

    /** Initialiser of this Container.<br>
     * Requires Object to communicate with GPS device.
     * @param p_GPSstate Object to communicate with GPS device.
     */
    public GPSLogFormat(Model m, Controller c) {
        //super("Log ON/OFF", Container.);
        this.m = m;
        this.c = c;
    };

    /** Initiliaser once all objects received initial setup
     * 
     */
    public void onStart () {
        // Add all tick buttons.
        for (int i=0;i<C_LOG_FMT_COUNT;i++) {
            chkLogFmtItems[i]= new MyCheck(BT747_dev.logFmtItems[i]);
//          int extra_offset=chkLogFmtItems[i].fm.height-chkLogFmtItems[i].getPreferredHeight();
//          add(chkLogFmtItems[i]);
//          int x=((i==0)?LEFT:((i==((C_LOG_FMT_COUNT/2)))? getClientRect().width/2:SAME));
//          if(((i==0) ||i==((C_LOG_FMT_COUNT/2)))) {
//          chkLogFmtItems[i].setRect(x, TOP, PREFERRED+extra_offset-1, chkLogFmtItems[i].fm.height-1);
//          } else {
//          chkLogFmtItems[i].setRect(x, AFTER-1, PREFERRED+extra_offset-1, chkLogFmtItems[i].fm.height-1);
//          }
            add( chkLogFmtItems[i],
                    ((i==0)?LEFT:((i==((C_LOG_FMT_COUNT/2)))? getClientRect().width/2:SAME)),
                    ((i==0) ||i==((C_LOG_FMT_COUNT/2)))? TOP:AFTER-1
            );
            chkLogFmtItems[i].setEnabled(true);
        }
        m_lbEstNbrRecords=new Label("0000000"+Txt.REC_ESTIMATED);
        add(m_lbEstNbrRecords,LEFT,AFTER);
        m_lbEstNbrRecords.setText("");

        // Add button confirming change of log format.
        m_btChangeFormatErase=new Button(Txt.SET_ERASE);
        add(m_btChangeFormatErase,LEFT,AFTER+5);
        add(m_btChangeFormat=new Button(Txt.SET_NOERASE),AFTER+10,SAME);
        add(m_btErase=new Button(Txt.ERASE),RIGHT,SAME);
        setLogFormatControls();
    }

    /** Get the format set by the user in the user interface. */
    private int getSelectedLogFormat() {
        int bitMask=1;
        int logFormat=0;
        for (int i=0;i<C_LOG_FMT_COUNT-1;i++) {
            if(chkLogFmtItems[i].getChecked()) {
                logFormat|=bitMask;
            }
            bitMask<<=1;
        }
        // Special case : low precision
        if(chkLogFmtItems[C_LOG_FMT_COUNT-1].getChecked()) {
            logFormat|=(1<<BT747_dev.FMT_HOLUX_LOW_PRECISION_IDX);
        }
        return logFormat;
    }

    /** Updates the format options shown the the user.<br>
     * This is typically done when the device responded with the current settings.
     * @param p_logFormat LogFormat to set
     */
    private void updateLogFormat(final int p_logFormat) {
        int bitMask=1;
        //if(GPS_DEBUG) {	waba.sys.Vm.debug("UPD:"+Convert.unsigned2hex(p_logFormat,2)+"\n");}


        for (int i=0;i<C_LOG_FMT_COUNT-1;i++) {
            chkLogFmtItems[i].setChecked((p_logFormat & bitMask)!=0);
//          chkLogFmtItems[i].repaintNow();
            bitMask<<=1;
        }
        chkLogFmtItems[C_LOG_FMT_COUNT-1]
                       .setChecked((p_logFormat&(1<<BT747_dev.FMT_HOLUX_LOW_PRECISION_IDX))!=0);
        setLogFormatControls();
    }

    private void setLogFormatControls() {
        boolean sidSet;
        sidSet=chkLogFmtItems[BT747_dev.FMT_SID_IDX].getChecked();
        chkLogFmtItems[BT747_dev.FMT_ELEVATION_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747_dev.FMT_AZIMUTH_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747_dev.FMT_SNR_IDX].setEnabled(sidSet);

        int count;
        m_lbEstNbrRecords.setText("");
        try {
            int size=BT747_dev.logRecordSize(getSelectedLogFormat(), m.isHolux(),12);
            if(m.isHolux()) {
                size+=1;
            } else {
                size+=2;
            }
            count= m.logMemUsefullSize()/size;
            m_lbEstNbrRecords.setText(count+Txt.REC_ESTIMATED);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    /** Handle events for this object.
     * @param event The event to be interpreted.
     */
    public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target==m_btChangeFormatErase) {
                c.changeLogFormatAndErase(getSelectedLogFormat());
            } else if (event.target==m_btChangeFormat) {
                c.changeLogFormat(getSelectedLogFormat());
            } else if (event.target==m_btErase) {
                c.eraseLogFormat();
            } else if (event.target==this) {
                c.reqLogFormat();
                event.consumed=true;
            } else {
                boolean z_updated=false;
                for (int i=0;i<C_LOG_FMT_COUNT;i++) {
                    if (event.target==chkLogFmtItems[i]) {
                        z_updated=true;
                    }
                }
                if(z_updated) {
                    setLogFormatControls();
                }
            }

            break;
        default:
            if(event.type==GpsEvent.LOG_FORMAT_UPDATE) {
                updateLogFormat(m.getLogFormat());
            }
        }
    }
}
