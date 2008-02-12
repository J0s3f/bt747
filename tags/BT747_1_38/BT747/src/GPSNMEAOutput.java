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

import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;

import gps.BT747_dev;
import gps.GPSstate;
import gps.GpsEvent;

import bt747.Txt;
import bt747.ui.Button;

/*
 * Created on 3 sept. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSNMEAOutput extends Container {
    /** The object that is used to communicate with the GPS device. */
    private ComboBox [] chkNMEAItems =new ComboBox[BT747_dev.C_NMEA_SEN_COUNT];
    /** The button that requests to change the log format of the device */
    
    private static final String C_NMEA_PERIODS[]= {"0","1","2","3","4","5"};
    
    private Button btSet;
    private Button btSetDefaults;
    
    private GPSstate m_GPSstate;
    /**
     * 
     */
    public GPSNMEAOutput(final GPSstate state) {
        m_GPSstate=state;
        // TODO Auto-generated constructor stub
    }

    
    /* (non-Javadoc)
     * @see waba.ui.Container#onStart()
     */
    protected void onStart() {
        for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
            chkNMEAItems[i]= new ComboBox(C_NMEA_PERIODS);
            add( chkNMEAItems[i]);
            chkNMEAItems[i].setRect(((i<((BT747_dev.C_NMEA_SEN_COUNT/2)+1))?LEFT:(getClientRect().width/2)),
                    ((i==0) ||i==((BT747_dev.C_NMEA_SEN_COUNT/2)+1))? TOP:AFTER-1, PREFERRED, PREFERRED-1);
            add( new Label(BT747_dev.NMEA_strings[i]),AFTER,SAME);
//            chkNMEAItems[i].setEnabled(true);
        }
        btSet=new Button(Txt.SET);
        add(btSet,(getClientRect().width/2),AFTER);

        btSetDefaults=new Button(Txt.DEFAULTS);
        add(btSetDefaults,AFTER,SAME);
    }
    
    private void updatePeriods() {
        for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
            chkNMEAItems[i].select(m_GPSstate.NMEA_periods[i]);
//            chkNMEAItems[i].repaintNow();
        }
    }

    private void setPeriods() {
        int[] Periods = new int[BT747_dev.C_NMEA_SEN_COUNT];
        
        for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
            Periods[i]=chkNMEAItems[i].getSelectedIndex();
        }
        m_GPSstate.setNMEAPeriods(Periods);
        m_GPSstate.getNMEAPeriods();
    }

    /** Handle events for this object.
     * @param event The event to be interpreted.
     */
     public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target==this) {
                m_GPSstate.getNMEAPeriods();
                event.consumed=true;
            } else if (event.target==btSet) {
                setPeriods();
                event.consumed=true;
            } else if (event.target==btSetDefaults) {
                m_GPSstate.setNMEADefaultPeriods();
                event.consumed=true;
            } else {
//                boolean z_updated=false;
//                for (int i=0;i<C_LOG_FMT_COUNT;i++) {
//                    if (event.target==chkLogFmtItems[i]) {
//                        z_updated=true;
//                    }
//                }
//                if(z_updated) {
//                    setLogFormatControls();
//                }
            }
        
        break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                
//                updateLogFormat(m_GPSstate.logFormat);
                updatePeriods();
                event.consumed=true;
            }
        }
    }

}
