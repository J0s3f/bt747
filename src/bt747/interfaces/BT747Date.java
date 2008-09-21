package bt747.interfaces;

public interface BT747Date {

    /**
     * Advance the time.
     * 
     * @param s
     *            The number of seconds to advance.
     */
    public abstract void advance(final int s);

    /**
     * Return the date in the format usually used in the GPS devices: number of
     * seconds since the epoch 1970.
     * 
     * @return number of seconds since the epoch.
     */
    public abstract int dateToUTCepoch1970();

    public abstract String getDateString();

    // private static final int DAYS_Julian_1970 = (new
    // Date(1,1,1970)).getJulianDay();
    public abstract int getJulianDay();

    public abstract int getYear();

    public abstract int getMonth();

    public abstract int getDay();

}