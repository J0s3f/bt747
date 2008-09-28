package bt747.interfaces;

public interface BT747Vector {

    void addElement(final Object o);

    int size();

    Object elementAt(final int arg0);

    void removeAllElements();

    Object pop();

    void removeElementAt(final int index);

    void mypush(Object item);

    String[] toStringArray();

}