import waba.ui.Check;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;

import gps.BT747_dev;

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
public class GPSFileNMEAOutputSel extends Container {
    /** The object that is used to communicate with the GPS device. */
    private Check [] chkNMEAItems =new Check[BT747_dev.C_NMEA_SEN_COUNT];
    /** The button that requests to change the log format of the device */
    
    private final int C_NMEAactiveFilters=0x0002000A;
    
    private AppSettings m_settings;
    /**
     * 
     */
    public GPSFileNMEAOutputSel(final AppSettings settings) {
        m_settings=settings;
        // TODO Auto-generated constructor stub
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
            chkNMEAItems[i].repaintNow();
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
