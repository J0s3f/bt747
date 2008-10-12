package bt747.sys.interfaces;

import bt747.sys.HashSet;

public interface BT747HashSet {

    /**
     * Initialises iterator
     * 
     * @return Pointer to self (iterator)
     */
    public BT747HashSet iterator();

    public void add(Object o);

    public void remove(Object o);

    public boolean hasNext();

    public Object next();   
}
