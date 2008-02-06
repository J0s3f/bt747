import waba.ui.Button;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;

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
public class GPSHoluxSpecific extends Container {
  
    private AppSettings m_settings;
    private GPSstate m_gpsState;
    
    private Label lbHoluxName;
    private Edit  edHoluxName;
    
    private Button btSet;
    /**
     * 
     */
    public GPSHoluxSpecific(final AppSettings settings, final GPSstate gpsState) {
        m_settings=settings;
        m_gpsState=gpsState;
    }

    
    /* (non-Javadoc)
     * @see waba.ui.Container#onStart()
     */
    protected void onStart() {
        int bit=1;
        
        lbHoluxName= new Label("Holux Name:");
        add(lbHoluxName,LEFT,TOP);
        add(edHoluxName= new Edit(""),LEFT,AFTER);
        add(btSet=new Button("Set"),CENTER,AFTER+5);
    }
    
    private void doSet() {
        m_gpsState.setHoluxName(edHoluxName.getText().replace(',',';'));
    }
    
    private void updateData() {
        edHoluxName.setText(m_gpsState.getHoluxName());
    }
    

    /** Handle events for this object.
     * @param event The event to be interpreted.
     */
     public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target==this) {
                m_gpsState.requestHoluxName();
                event.consumed=true;
            } else if(event.target==btSet) {
                doSet();
            }
            break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                updateData();
                event.consumed=true;
            }
        }
    }

}
