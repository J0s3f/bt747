/**
 * 
 */
package gps;

/**
 * Constants for GPS Protocols.
 * 
 * @author Robert Tomanek <bt747.free.fr@mail.robert.tomanek.org>
 * @author Mario De Weerd
 */
public interface ProtocolConstants {
    /**
     * Indicates invalid protocol - should not be used of course, can be used
     * in error handling.
     */
    public final static int PROTOCOL_INVALID = -1;

    /**
     * The MTK protocol for most MTK based devices. Some flavours of this
     * protocol are represented in other protocols. For example
     * {@link #PROTOCOL_HOLUX_PHLX}.
     */
    public final static int PROTOCOL_MTK = 0;

    /**
     * Protocol (DPL700/BT-CD110m).
     */
    public final static int PROTOCOL_WONDEPROUD = 1;

    /**
     * The Holux specific protocol that uses $PHLX... NMEA strings. Could be
     * further derived in other protocols according to what the Holux devices
     * provide. Holux has the bad habit of radically changing their vendor
     * specific protocol and even changing the meaning of some stuff that is
     * done in the Mtk protocol that is still available.
     */
    public final static int PROTOCOL_HOLUX_PHLX = 2;
    
    /**
     * The SIRFIII protocol. This one can also have flavors.
     */
    public final static int PROTOCOL_SIRFIII = 3;
}
