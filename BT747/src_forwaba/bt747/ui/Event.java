/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.ui;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Event extends waba.ui.Event {

    /**
     * 
     */
    public Event() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param type
     * @param target
     * @param timeStamp
     */
    public Event(int type, Object target, int timeStamp) {
        super(type, target, timeStamp);
        // TODO Auto-generated constructor stub
    }
    
    public final static int getNextAvailableEventId()
    {
       return waba.ui.Event.getNextAvailableEventId();
    }

    public final int getType() {
        return this.type;
    }
}
