package bt747.interfaces;

public interface BT747Vector {

    public abstract void addElement(final Object o);

    public abstract int size();

    public abstract Object elementAt(final int arg0);

    public abstract void removeAllElements();

    public abstract Object pop();

    public abstract void removeElementAt(final int index);

    public abstract void mypush(Object item);

    public abstract String[] toStringArray();

}