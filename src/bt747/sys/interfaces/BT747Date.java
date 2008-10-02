package bt747.sys.interfaces;

public interface BT747Date {

    /**
     * Advance the time.
     * 
     * @param s
     *            The number of seconds to advance.
     */
    void advance(final int s);

    /**
     * Return the date in the format usually used in the GPS devices: number of
     * seconds since the epoch 1970.
     * 
     * @return number of seconds since the epoch.
     */
    int dateToUTCepoch1970();

    String getDateString();

    // private static final int DAYS_Julian_1970 = (new
    // Date(1,1,1970)).getJulianDay();

//    /** Returns the number of days since the January 1 of the epoch year (1970). */
//    int getJulianDay();

    int getYear();

    int getMonth();

    int getDay();

}