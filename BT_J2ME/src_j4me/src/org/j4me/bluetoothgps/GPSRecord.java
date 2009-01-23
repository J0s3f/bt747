package org.j4me.bluetoothgps;

/**
 * Storage data type for parsed GPS data.
 */
final class GPSRecord {
    /**
     * Character that indicates a warning.
     */
    public String altitude;
    public String date;
    public String secondsSinceMidnight;
    public String hdop;
    public String lattitude;
    public char lattitudeDirection;
    public String longitude;
    public char longitudeDirection;
    public String quality;
    public String satelliteCount;
    public String vdop;

    /**
     * The ground speed in knots.
     */
    public String speed;
    public String course;

    /**
     * Constructs a record object for the current position calculated by GPS.
     */
    public GPSRecord() {
    }

    /**
     * Creates a deep copy of a GPS record object.
     * 
     * @param record
     *                is GPS record to make a deep copy of.
     */
    public GPSRecord(final GPSRecord record) {
        altitude = record.altitude;
        date = record.date;
        secondsSinceMidnight = record.secondsSinceMidnight;
        hdop = record.hdop;
        lattitude = record.lattitude;
        lattitudeDirection = record.lattitudeDirection;
        longitude = record.longitude;
        longitudeDirection = record.longitudeDirection;
        quality = record.quality;
        satelliteCount = record.satelliteCount;
        vdop = record.vdop;
        speed = record.speed;
        course = record.course;
    }
}
