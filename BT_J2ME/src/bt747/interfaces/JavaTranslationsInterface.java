package bt747.interfaces;

public interface JavaTranslationsInterface {

    public abstract BT747Date getDateInstance();

    public abstract BT747Date getDateInstance(final int d, final int m,
            final int y);

    public abstract BT747Date getDateInstance(final String strDate,
            final byte dateFormat);

    public abstract BT747Hashtable getHashtableInstance(
            final int initialCapacity);

    public abstract BT747Vector getVectorInstance();

    public abstract BT747Time getTimeInstance();

    public abstract void debug(final String s, final Throwable e);

    public abstract double pow(final double x, final double y);

    public abstract double acos(final double x);

    public abstract void addThread(final BT747Thread t, final boolean b);

    public abstract void removeThread(final BT747Thread t);

}
