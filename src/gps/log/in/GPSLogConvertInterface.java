// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.in;

import gps.log.out.GPSFile;

import bt747.model.Model;
import bt747.sys.interfaces.BT747Path;

/**
 * Interface defining a Log conversion class.
 * 
 * @author Mario De Weerd
 */
public abstract class GPSLogConvertInterface {
    /**
     * Parses the file once and will use {@link GPSFile} to generate the
     * output.
     * 
     * @param gpsFile
     *                Represents the structure to generate the output.
     * @return error indication.
     */
    public abstract int parseFile(final Object file,
            final GPSFileConverterInterface gpsFile);

    /**
     * Must return the object that can be used with parseFile. May be closed
     * after end of parseFile call.
     * 
     * @return
     */
    protected abstract Object getFileObject(final BT747Path path);

    protected abstract void closeFileObject(final Object file);

    protected int factorConversionWGS84ToMSL = 0;
    protected int mode = Model.HEIGHT_AUTOMATIC;
    
    /**
     * Height conversion active if true.
     * 
     * @param mode
     *                When '-1' : Substract MSL over WGS84<br>
     *                When '1' : Add MSL over WGS84 in output.<br>
     *                When '0': Do nothing<br>
     */
    public final void setConvertWGS84ToMSL(final int mode) {
        factorConversionWGS84ToMSL = mode;
    }

    /** Added for multiple log conversion. */
    public final void setHeightConvertMode(final int mode) {
        this.mode = mode;
    }

    public abstract int toGPSFile(final BT747Path fileName,
            final GPSFileConverterInterface gpsFile);

    /**
     * Returns some information regarding the reported error. To be thrown in
     * a future version.
     * 
     * @return Textual description of the error.
     */
    protected String errorInfo;

    public final String getErrorInfo() {
        return errorInfo;
    }

    protected boolean stop = false;

    public void stopConversion() {
        stop = true;
    }

    /**
     * @return log type such as {@link Model#TRL_LOGTYPE}.
     */
    public abstract int getType();

    protected int loggerType = 0;

    public void setLoggerType(final int loggerType) {
        this.loggerType = loggerType;
    }
    
    public final int getLoggerType() {
        return loggerType;
    }
}
