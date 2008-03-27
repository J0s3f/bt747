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
public class Event extends java.awt.Event {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

//    /**
//     * 
//     */
//    public Event() {
//        super();
//        // TODO Auto-generated constructor stub
//    }
//
    public Event() {
        super(null,0,null);
    }
    
    public Event(Event e) {
        super(e.target, e.id, e.arg);
    }
    /**
     * @param type
     * @param target
     * @param timeStamp
     */
    public Event(int type, Object target, int timeStamp) {
        super(target, type, null);
        // TODO Auto-generated constructor stub
    }

    public Event(Object target, int type, Object arg) {
        super(target, type, arg);
        // TODO Auto-generated constructor stub
    }

    
    static private int next_id=10000;
    public final static int getNextAvailableEventId()
    {
       return next_id++;
    }
    
    public final int getType() {
        return this.id;
    }
    
    public final Object getArg() {
        return this.arg;
    }
    
    public final void setArg(Object arg) {
        this.arg=arg;
    }


}
