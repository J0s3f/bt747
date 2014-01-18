/**
 * 
 */
package gps.log;

import bt747.sys.interfaces.BT747Path;

/**
 * Object keeping information about a log file.
 * 
 * @author Mario De Weerd
 * 
 */
public class LogFileInfo {
    private BT747Path path;
    private int startTime;
    private int endTime;
    private GPSRecord activeFileFields;

    public final GPSRecord getActiveFileFields() {
        return activeFileFields;
    }

    public final void setActiveFileFields(final GPSRecord activeFileFields) {
        this.activeFileFields = activeFileFields;
    }

    /**
     * 
     */
    public LogFileInfo(final BT747Path path, final int startTimeUTC,
            final int endTimeUTC) {
        this.path = path;
        startTime = startTimeUTC;
        endTime = endTimeUTC;
    }

    public LogFileInfo(final BT747Path path) {
        this.path = path;
    }

    public final BT747Path getBT747Path() {
        return path;
    }

    public final void setPath(final BT747Path path) {
        this.path = path;
    }

    public final int getStartTime() {
        return startTime;
    }

    public final void setStartTime(final int startTime) {
        this.startTime = startTime;
    }

    public final int getEndTime() {
        return endTime;
    }

    public final void setEndTime(final int endTime) {
        this.endTime = endTime;
    }

}
