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
import bt747.model.AppSettings;
import bt747.ui.Check;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;

import gps.BT747_dev;

/**
 * @author Mario De Weerd
 *
 * User interface to select NMEA output strings
 */
public class GPSFileNMEAOutputSel extends Container {
    /** The object that is used to communicate with the GPS device. */
    private Check [] chkNMEAItems =new Check[BT747_dev.C_NMEA_SEN_COUNT];
    /** The button that requests to change the log format of the device */
    
    private static final int C_NMEAactiveFilters=0x0002003A;
    
    private AppSettings m_settings;
    /**
     * 
     */
    public GPSFileNMEAOutputSel(final AppSettings settings) {
        m_settings=settings;
    }

    
    /* (non-Javadoc)
     * @see waba.ui.Container#onStart()
     */
    protected void onStart() {
        int bit=1;
        for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
            chkNMEAItems[i]= new Check(BT747_dev.NMEA_strings[i]);
            add( chkNMEAItems[i]);
            chkNMEAItems[i].setRect(((i<((BT747_dev.C_NMEA_SEN_COUNT/2)+1))?LEFT:(getClientRect().width/2)),
                    ((i==0) ||i==((BT747_dev.C_NMEA_SEN_COUNT/2)+1))? TOP:AFTER-1, PREFERRED, PREFERRED-1);
            chkNMEAItems[i].setEnabled((C_NMEAactiveFilters&bit)!=0);
            bit <<=1;
        }
        updateNMEAset();
    }
    
    private void updateNMEAset() {
        int NMEAset;
        int bit;
        NMEAset=m_settings.getNMEAset();
        bit=1;
        for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
            chkNMEAItems[i].setChecked(
                    (NMEAset&bit)!=0
                    );
            bit <<=1;
            //chkNMEAItems[i].repaintNow();
        }
    }

    private void setNMEAset() {
        int NMEAset;
        int bit;
        NMEAset=0;
        
        bit=1;
        for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
            NMEAset|=chkNMEAItems[i].getChecked()?bit:0;
            bit <<=1;
        }
        m_settings.setNMEAset(NMEAset);
    }

    /** Handle events for this object.
     * @param event The event to be interpreted.
     */
     public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
                if(event.target==chkNMEAItems[i]) {
                    setNMEAset();
                    break;
                }
            }
        break;
        }
    }

}
