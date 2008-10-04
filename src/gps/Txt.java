package gps;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public final class Txt {
    // GPSFile
    public static final String CLOSE_FAILED =
        "Closing failed (closeFile) - probably a bug.";
    public static final String WRITING_CLOSED =
        "Writing to closed file";
    // BT747_dev class
    public static final String[]C_STR_RCR = {
            "Time", "Speed", "Distance", "Button",
            "App1","App2","App3","App4",
            "App5","App6","App7","App8",
            "App9","AppX","AppY","AppZ"
            };
    public static final String [] logFmtItems = {
        "UTC",      // = 0x00001    // 0
        "VALID",    // = 0x00002    // 1
        "LATITUDE", // = 0x00004    // 2
        "LONGITUDE",// = 0x00008    // 3
        "HEIGHT",   // = 0x00010    // 4
        "SPEED",    // = 0x00020    // 5
        "HEADING",  // = 0x00040    // 6
        "DSTA",     // = 0x00080    // 7
        "DAGE",     // = 0x00100    // 8
        "PDOP",     // = 0x00200    // 9
        "HDOP",     // = 0x00400    // A
        "VDOP",     // = 0x00800    // B
        "NSAT",     // = 0x01000    // C
        "SID",      // = 0x02000    // D
        "ELEVATION",// = 0x04000    // E
        "AZIMUTH",  // = 0x08000    // F
        "SNR",      // = 0x10000    // 10
        "RCR",      // = 0x20000    // 11
        "MILISECOND",// = 0x40000   // 12
        "DISTANCE",  // = 0x80000    // 13
        "VALID PTS ONLY" // =0x80000000
    };

    public static final String C_BAD_LOG_FORMAT = "Bad log format";
}


