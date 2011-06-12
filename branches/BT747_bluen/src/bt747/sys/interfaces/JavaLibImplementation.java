// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package bt747.sys.interfaces;

/**
 * @author Mario De Weerd
 * @author Florian Unger for indicated parts.
 *
 */
public interface JavaLibImplementation {

    BT747Date getDateInstance();

    BT747Date getDateInstance(final int d, final int m, final int y);

    BT747Date getDateInstance(final String strDate, final byte dateFormat);

    BT747Hashtable getHashtableInstance(final int initialCapacity);

    BT747Vector getVectorInstance();

    BT747Time getTimeInstance();

    BT747File getFileInstance(BT747Path path);

    BT747File getFileInstance(BT747Path path, int mode);

    BT747RAFile getRAFileInstance(BT747Path path);

    BT747RAFile getRAFileInstance(BT747Path path, int mode);

    boolean isAvailable();

    void debug(final String s, final Throwable e);

    double pow(final double x, final double y);

    double acos(final double x);

    double atan2(final double x, final double y);

    double atan(final double x);

    void addThread(final BT747Thread t, final boolean b);

    void removeThread(final BT747Thread t);

    /**
     * System method to convert a boolean to a string.
     * 
     * @param p
     *            boolean to convert.
     * @return String corresponding to boolean.
     */
    String toString(final boolean p);

    /**
     * System method to convert an int to a string.
     * 
     * @param p
     *            int to convert.
     * @return String corresponding to boolean.
     */
    // String toString(final int p);
    /**
     * System method to convert a float to a string.
     * 
     * @param p
     *            float to convert.
     * @return String corresponding to floating number.
     */
    String toString(final float p);

    /**
     * System method to convert a double to a string.
     * 
     * @param p
     *            double to convert.
     * @return String corresponding to double number.
     */
    String toString(final double p);

    /**
     * System method to convert a double to a string.
     * 
     * @param p
     *            double to convert.
     * @param i
     *            Number of digits after the decimal point.
     * @return String corresponding to double number.
     */
    String toString(final double p, final int i);

    /**
     * System method to convert an integer to a hexadecimal string.
     * 
     * @param p
     *            The integer to convert. Interpreted as an unsigned.
     * @param i
     *            The number of characters in the hexadecimal string. For
     *            instance, if this value is 8, a value of 0x123 would be
     *            converted to "00000123"
     * @return String value containing the hex representation of the integer
     */
    String unsigned2hex(final int p, final int i);

    /**
     * System method to convert a string to an integer.
     * 
     * @param s
     *            The string to convert.
     * @return The integer corresponding to the string.
     */
    int toInt(final String s);

    /**
     * System method to convert a string to a float.
     * 
     * @param s
     *            The string to convert.
     * @return The float corresponding to the string.
     */
    float toFloat(final String s);

    /**
     * System method to convert a string to a double.
     * 
     * @param s
     *            The string to convert.
     * @return The double corresponding to the string.
     */
    double toDouble(final String s);

    /**
     * System method to bitwise convert a long to a double. The long
     * corresponds to 8 bytes that represent a floating number in IEEE ...
     * format.
     * 
     * @param l
     *            The value to bitwise convert.
     * @return The double corresponding to the bitwise conversion.
     */
    double longBitsToDouble(final long l);

    /**
     * System method to bitwise convert a long to a float. The int corresponds
     * to 4 bytes that represent a floating number in IEEE ... format.
     * 
     * @param l
     *            The value to bitwise convert.
     * @return The float corresponding to the bitwise conversion.
     */
    float toFloatBitwise(final int l);

    /**
     * System method to bitwise convert a float to an int. The int can then be
     * written to a byte array for example.
     * 
     * @param f
     *            The value to bitwise convert.
     * @return The float corresponding to the bitwise conversion.
     */
    int toIntBitwise(final float f);

    void debug(final String s);

    int getTimeStamp();

    String getAppSettings();

    void setAppSettings(final String appSettings);

    BT747Semaphore getSemaphoreInstance(final int value);

    BT747StringTokenizer getStringTokenizer(final String a, final char b);

    BT747HashSet getHashSetInstance();

    /**
     * Obtain an implementation of the BT747HttpSender interface.
     * <br>author Florian Unger
     * 
     * @return an instance of a class implementing the BT747HttpSender
     *         interface.
     * @throws BT747Exception
     */
    BT747HttpSender getHttpSenderInstance() throws BT747Exception;

    /** Convert html encoded string to UTF8
     * @param s
     * @return
     */
    String convertHTMLtoUTF8(final String s);

}
