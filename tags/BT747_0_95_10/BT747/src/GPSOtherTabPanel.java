import waba.sys.Convert;
import waba.sys.Settings;
import waba.ui.Container;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.MainWindow;
import waba.ui.MessageBox;
import waba.ui.TabPanel;
import waba.ui.Window;

import gps.GPSstate;
import gps.GpsEvent;

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
public class GPSOtherTabPanel extends Container {

    private TabPanel m_TabPanel;
    private GPSFlashOption  m_GPSFlash;
    private GPSNMEAOutput m_GPSNMEAOutput;
    private GPSstate m_GPSstate;
    
    private final String c_tpCaptions[]= {
            "Flsh","NMEA Output"
    };
    
    /**
     * 
     */
    public GPSOtherTabPanel(final GPSstate state) {
        m_GPSstate=state;
    }
    
    /**
     * 
     */
    public void onStart() {
        add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0,0,0,0));
        // TODO Auto-generated method stub
        m_TabPanel.setPanel(0,m_GPSFlash = new GPSFlashOption(m_GPSstate));
        m_TabPanel.setPanel(1,m_GPSNMEAOutput = new GPSNMEAOutput(m_GPSstate));
    }
    public void onEvent(Event event) {
        //
        switch (event.type) {
        case ControlEvent.PRESSED:
            if(event.target==m_TabPanel||event.target==this) {
                Control c;
                c=m_TabPanel.getChildren()[0];
                c.postEvent(new Event(ControlEvent.PRESSED,c,0));                
            }
            break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                Control c;
                c=m_TabPanel.getChildren()[0];
                c.postEvent(new Event(GpsEvent.DATA_UPDATE,c,0));
                event.consumed=true;
            }
            break;
        }
    }
}
