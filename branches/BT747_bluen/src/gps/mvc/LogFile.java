/**
 * 
 */
package gps.mvc;

import gps.GpsEvent;

import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 *
 */
public class LogFile {

    private File logFile;
    private BT747Path logPath;
    
    public final GpsEvent openNewLog(final BT747Path path) {
        try {
            if ((logFile != null) && logFile.isOpen()) {
                logFile.close();
            }

            logFile = new File(path, bt747.sys.File.DONT_OPEN
                    );
            logPath = path;
            if (logFile.exists()) {
                logFile.delete();
            }

            logFile = new File(path, bt747.sys.File.CREATE);
            // lastError 10530 = Read only
            logPath = path;
            logFile.close();
            logFile = new File(path, bt747.sys.File.WRITE_ONLY);
            logPath = path;

            if ((logFile == null) || !(logFile.isOpen())) {
                return new GpsEvent(GpsEvent.COULD_NOT_OPEN_FILE, logPath);
            }
        } catch (final Exception e) {
            Generic.debug("openNewLog", e);
        }
        return null;
    }

    public final void reOpenLogWrite(final BT747Path path) {
        closeLog();
        try {
            logFile = new File(path, File.WRITE_ONLY);
            logPath = path;
        } catch (final Exception e) {
            Generic.debug("reOpenLogWrite", e);
        }
    }

    public final void closeLog() {
        try {
            if (logFile != null) {
                if (logFile.isOpen()) {
                    logFile.close();
                    logFile = null;
                }
            }
        } catch (final Exception e) {
            Generic.debug("CloseLog", e);
        }
    }

    /**
     * @return the logFile
     */
    public File getLogFile() {
        return this.logFile;
    }
}
