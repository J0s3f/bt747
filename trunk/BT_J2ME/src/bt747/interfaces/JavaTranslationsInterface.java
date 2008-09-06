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

    public abstract BT747File getFileInstance(String path);

    public abstract BT747File getFileInstance(String path, int mode, int card);

    public abstract BT747File getFileInstance(String path, int mode);
    
    public abstract BT747File getBufFileInstance(String path);

    public abstract BT747File getBufFileInstance(String path, int mode, int card);

    public abstract BT747File getBufFileInstance(String path, int mode);

    public abstract boolean isAvailable();

    public abstract void debug(final String s, final Throwable e);

    public abstract double pow(final double x, final double y);

    public abstract double acos(final double x);

    public abstract void addThread(final BT747Thread t, final boolean b);

    public abstract void removeThread(final BT747Thread t);

    public abstract String toString(final boolean p);

    public abstract String toString(final int p);

    public abstract String toString(final float p);

    public abstract String toString(final double p);

    public abstract String toString(final double p, final int i);

    public abstract String unsigned2hex(final int p, final int i);

    public abstract int toInt(final String s);

    public abstract float toFloat(final String s);

    public abstract double toDouble(final String s);

    public abstract double longBitsToDouble(final long l);

    public abstract float toFloatBitwise(final int l);

    public abstract int toIntBitwise(final float f);

}
