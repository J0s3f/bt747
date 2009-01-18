/**
 * 
 */
package gps.log;

/**
 * Object keeping information about a log file.
 * 
 * @author Mario De Weerd
 * 
 */
public class LogFileInfo {
    private String path;
    private int card; // For some systems
    private int startTime;
    private int endTime;

    /**
     * 
     */
    public LogFileInfo(final String path, final int startTimeUTC,
            final int endTimeUTC) {
        this.path = path;
        startTime = startTimeUTC;
        endTime = endTimeUTC;
    }

    /**
     * 
     */
    public LogFileInfo(final String path, final int card,
            final int startTimeUTC, final int endTimeUTC) {
        this(path, startTimeUTC, endTimeUTC);
        this.card = card;
    }

    public LogFileInfo(final String path, final int card) {
        this.path = path;
        this.card = card;
    }

    public final String getPath() {
        return path;
    }

    public final void setPath(final String path) {
        this.path = path;
    }

    public final int getCard() {
        return card;
    }

    public final void setCard(final int card) {
        this.card = card;
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
