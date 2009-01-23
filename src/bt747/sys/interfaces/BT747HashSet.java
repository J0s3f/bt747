package bt747.sys.interfaces;

public interface BT747HashSet {

    /**
     * Initialises iterator
     * 
     * @return Pointer to self (iterator)
     */
    public BT747HashSet iterator();

    public void add(final Object o);

    public void remove(final Object o);

    public boolean hasNext();

    public Object next();
}
