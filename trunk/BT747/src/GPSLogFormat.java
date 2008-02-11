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
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MessageBox;

import gps.BT747_dev;
import gps.GPSstate;
import gps.GpsEvent;

import bt747.Txt;
import bt747.ui.Button;
import bt747.ui.Check;

/**
 * @author Mario De Weerd
 */
public class GPSLogFormat extends Container {
    private static final int C_LOG_FMT_COUNT = 21;
    /** The object that is used to communicate with the GPS device. */
    private GPSstate m_GPSstate;
    /** The tickboxes for the format items */
    private Check [] chkLogFmtItems =new Check[C_LOG_FMT_COUNT];
    /** The button that requests to change the log format of the device */
    private Button m_btChangeFormatErase;
    private Button m_btChangeFormat;
    private Button m_btErase;
    
    private Label m_lbEstNbrRecords;
    
    /** Initialiser of this Container.<br>
     * Requires Object to communicate with GPS device.
     * @param p_GPSstate Object to communicate with GPS device.
     */
    public GPSLogFormat(GPSstate p_GPSstate) {
        //super("Log ON/OFF", Container.);
        m_GPSstate = p_GPSstate;
    };
    
    /** Initiliaser once all objects received initial setup
     * 
     */
    public void onStart () {
        // Add all tick buttons.
        for (int i=0;i<C_LOG_FMT_COUNT;i++) {
            chkLogFmtItems[i]= new Check(BT747_dev.logFmtItems[i]);
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
    
    /** Options for the first warning message */
    private static final String[] C_EraseOrCancel = {
            Txt.ERASE, Txt.CANCEL
    };
    /** Options for the first warning message */
    private static final String[] C_YesrCancel = {
            Txt.YES, Txt.CANCEL
    };
    /** Options for the second warning message - reverse order on purpose */
    private static final String[] C_CancelConfirmErase = {
            Txt.CANCEL, Txt.CONFIRM_ERASE
    };

    
    /** (User) request to change the log format.
     * Warns about requirement to erase the log too.
     * TODO: Wait until erase is finished
     */
    private void changeLogFormatAndErase() {
        /** Object to open multiple message boxes */
        MessageBox m_mb; 
        m_mb=new MessageBox(
                Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatAndErase,
                C_EraseOrCancel);
        m_mb.popupBlockingModal();
        if(m_mb.getPressedButtonIndex()==0) {
            m_mb=new MessageBox(
                    Txt.TITLE_ATTENTION,
                    Txt.C_msgWarningFormatAndErase2,
                    C_CancelConfirmErase);
            m_mb.popupBlockingModal();
            if(m_mb.getPressedButtonIndex()==1) {
                // Set format and reset log
                m_GPSstate.setLogFormat(getSelectedLogFormat());
                m_GPSstate.eraseLog();
            }
        }
    }

    /** (User) request to change the log format.
     * The log is not erased and may be incompatible with other applications
     */
    private void changeLogFormat() {
        /** Object to open multiple message boxes */
        MessageBox m_mb; 
        m_mb=new MessageBox(Txt.TITLE_ATTENTION,
                waba.sys.Convert.insertLineBreak(Settings.screenWidth-6,
                        '|',
                        getFontMetrics(getFont()),
                        Txt.C_msgWarningFormatIncompatibilityRisk),C_YesrCancel);
        m_mb.popupBlockingModal();
        if(m_mb.getPressedButtonIndex()==0) {
            m_GPSstate.setLogFormat(getSelectedLogFormat());
        }
    }

    /** (User) request to change the log format.
     * Warns about requirement to erase the log too.
     * TODO: Wait until erase is finished
     */
    private void eraseLogFormat() {
        /** Object to open multiple message boxes */
        MessageBox m_mb; 
        m_mb=new MessageBox(
                Txt.TITLE_ATTENTION,
                Txt.C_msgEraseWarning,
                C_EraseOrCancel);
        m_mb.popupBlockingModal();
        if(m_mb.getPressedButtonIndex()==0) {
            m_mb=new MessageBox(
                    Txt.TITLE_ATTENTION,
                    Txt.C_msgEraseWarning2,
                    C_CancelConfirmErase);
            m_mb.popupBlockingModal();
            if(m_mb.getPressedButtonIndex()==1) {
                // Erase log
                m_GPSstate.eraseLog();
            }
        }
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
//            chkLogFmtItems[i].repaintNow();
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
            int size=BT747_dev.logRecordSize(getSelectedLogFormat(), m_GPSstate.isHolux(),12);
            if(m_GPSstate.isHolux()) {
                size+=1;
            } else {
                size+=2;
            }
            count= m_GPSstate.logMemUsefullSize()/size;
            m_lbEstNbrRecords.setText(count+Txt.REC_ESTIMATED);
        } catch (Exception e) {
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
                changeLogFormatAndErase();
            } else if (event.target==m_btChangeFormat) {
                changeLogFormat();
            } else if (event.target==m_btErase) {
                eraseLogFormat();
            } else if (event.target==this) {
                m_GPSstate.getLogFormat();
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
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                updateLogFormat(m_GPSstate.logFormat);
                event.consumed=true;
            }
        }
    }
}
