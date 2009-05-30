/**
 * 
 */
package gps.mvc;

import gps.GpsEvent;

import bt747.sys.File;
import bt747.sys.Generic;

/**
 * @author Mario
 *
 */
public class LogFile {

    private File logFile;
    private String logFileName;
    private int logFileCard;
    
    public final GpsEvent openNewLog(final String fileName, final int card) {
        try {
            if ((logFile != null) && logFile.isOpen()) {
                logFile.close();
            }

            logFile = new File(fileName, bt747.sys.File.DONT_OPEN,
                    card);
            logFileName = fileName;
            logFileCard = card;
            if (logFile.exists()) {
                logFile.delete();
            }

            logFile = new File(fileName, bt747.sys.File.CREATE, card);
            // lastError 10530 = Read only
            logFileName = fileName;
            logFileCard = card;
            logFile.close();
            logFile = new File(fileName, bt747.sys.File.WRITE_ONLY,
                    card);
            logFileName = fileName;
            logFileCard = card;

            if ((logFile == null) || !(logFile.isOpen())) {
                return new GpsEvent(GpsEvent.COULD_NOT_OPEN_FILE, fileName);
            }
        } catch (final Exception e) {
            Generic.debug("openNewLog", e);
        }
        return null;
    }

    public final void reOpenLogWrite(final String fileName, final int card) {
        closeLog();
        try {
            logFile = new File(fileName, File.WRITE_ONLY, card);
            logFileName = fileName;
            logFileCard = card;
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
