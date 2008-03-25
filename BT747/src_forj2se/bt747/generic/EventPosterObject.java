/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.generic;

import bt747.ui.Event;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EventPosterObject {

    java.awt.Component m_Control;
    /**
     * 
     */
    public EventPosterObject(java.awt.Component s) {
        m_Control=s;
    }
    
    public void postEvent(Event e) {
        //m_Control.
        m_Control.postEvent(e);
    }

}
