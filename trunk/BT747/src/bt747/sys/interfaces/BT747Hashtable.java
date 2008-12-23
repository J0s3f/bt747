package bt747.sys.interfaces;

public interface BT747Hashtable {

    public abstract BT747Hashtable iterator();

    public abstract Object put(final Object key, final Object value);

    public abstract Object get(final Object key);

    public abstract void remove(final Object o);

    public abstract boolean hasNext();

    public abstract Object nextKey();
    
    public abstract int size();

}