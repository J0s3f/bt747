/**
 * 
 */
package gps.log.in;

import gps.BT747Constants;

import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario De Weerd
 * 
 */
public class GPSInputConversionFactory {
    protected GPSInputConversionFactory next = null;
    protected static GPSInputConversionFactory head = new DefaultGPSOutputFactory();

    /**
     * Get the first handler in the chain of command.
     * 
     * @return first handler.
     */
    public static final GPSInputConversionFactory getHandler() {
        return GPSInputConversionFactory.head;
    }

    /**
     * Gets a conversion instance for the given filename. Child class should
     * call super method if it can't produce an instance.
     * 
     * @param logFile
     * @return conversion instance.
     */
    public GPSLogConvertInterface getInputConversionInstance(
            final BT747Path logFile, final int loggerType) {
        if (next != null) {
            return next.getInputConversionInstance(logFile, loggerType);
        } else {
            return null;
        }
    }

    /**
     * Adds a new handler to the head of the chain of command.
     * 
     * @param newFactory
     */
    public static final void addHandler(
            final GPSInputConversionFactory newFactory) {
        newFactory.next = GPSInputConversionFactory.head;
        GPSInputConversionFactory.head = newFactory;
    }

    /**
     * Default Factory to provide input handler.
     * 
     * @author Mario De Weerd.
     * 
     */
    private static final class DefaultGPSOutputFactory extends
            GPSInputConversionFactory {

        public final GPSLogConvertInterface getInputConversionInstance(
                final BT747Path logFile, final int inLoggerType) {

            final String logFileLC = logFile.getPath().toLowerCase();
            if (logFileLC.endsWith(".trl")) {
                return new HoluxTrlLogConvert();
            } else if (logFileLC.endsWith(".csv")) {
                return new CSVLogConvert();
            } else if (logFileLC.endsWith(".nmea")
                    || logFileLC.endsWith(".nme")
                    || logFileLC.endsWith(".nma")
                    || logFileLC.endsWith(".txt")) {
                return new NMEALogConvert();
            } else if (logFileLC.endsWith(".log")) {
                if (inLoggerType == BT747Constants.GPS_TYPE_SKYTRAQ) {
                    return new SkytraqLogConvert();
                } else {
                    return new NMEALogConvert();
                }
            } else if (logFileLC.endsWith(".sr")) {
                return new WPLogConvert();
            } else {
                if (inLoggerType == BT747Constants.GPS_TYPE_SKYTRAQ) {
                    return new SkytraqLogConvert();
                } else {
                    return new BT747LogConvert();
                }
            }
        }
    }

}
