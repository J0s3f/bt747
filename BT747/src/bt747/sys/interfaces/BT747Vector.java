package bt747.sys.interfaces;

public interface BT747Vector {

    void addElement(final Object o);

    int size();

    Object elementAt(final int arg0);

    void removeAllElements();

    Object pop();

    void removeElementAt(final int index);

    void insertElementAt(final Object o, final int index);

    void mypush(final Object item);

    String[] toStringArray();

}
