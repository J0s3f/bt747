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
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to write a CSV file.
 * 
 * @author Mario De Weerd
 */
public final class GPSPostGISFile extends GPSFile {

    /**
     * WGS84 Long Lat reference http://spatialreference.org/ref/epsg/4326/
     */
    private static final int POSTGIS_SPATIALREF_WGS84_LONG_LAT = 4236;
    /**
     * 
     */
    private static final float METERS_TO_FEET = 3.28083989501312f;
    /**
     * Reused StringBuffer for output construction.
     */
    private final StringBuffer rec = new StringBuffer(1024);
    private final StringBuffer recPost = new StringBuffer(1024);
    /**
     * Separator in the satellite field - for future parameterization.
     */
    private String satSeperator = ";";

    private String tableName = "defaultpostgistable";
    private String dbName = "defaultdb";

    private char separatorChar = ' ';

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);

        if (getParamObject().hasParam(
                GPSConversionParameters.POSTGIS_TABLE_NAME)) {
            tableName = getParamObject().getStringParam(
                    GPSConversionParameters.POSTGIS_TABLE_NAME);
        }
    }

    public final boolean needPassToFindFieldsActivatedInLog() {
        return true;
    }

    protected final void writeFileHeader(final String Name) {
        rec.setLength(0);
        recPost.setLength(0);

        /** Create db with postgis. */
        // rec.append("CREATE DATABASE my_spatial_db TEMPLATE=template_postgis ;\r\n");

        /** DB is selected before executing SQL */

        rec.append("CREATE TABLE ");
        rec.append(tableName);
        rec.append(" ( " + "track_id" + " INTEGER" + "," + "track_starttime"
                + " TIMESTAMP" + "," + "track_endtime" + " TIMESTAMP"
                + ");\r\n");

        rec.append("SELECT AddGeometryColumn('" + tableName + "','"
                + "track_geom" + "'," + POSTGIS_SPATIALREF_WGS84_LONG_LAT
                + ",'MULTILINESTRING'" + ",3" // Dimensions [2 or 3]
                + ");\r\n");
        rec.append("BEGIN;\r\n");
        rec.append("INSERT INTO " + tableName);
        rec.append(" ( ");
        rec.append("track_id" + "," + "track_starttime" + ","
                + "track_endtime" + ",");
        rec.append("track_geom" + ")\r\nVALUES (\r\n");

        rec.append("1, '1999-01-01 10:00', '1999-01-01 11:00',\r\n");

        rec.append("ST_GeomFromEWKT('SRID="
                + POSTGIS_SPATIALREF_WGS84_LONG_LAT + ";"
                + "MULTILINESTRING((\r\n");
        separatorChar = ' ';
        writeTxt(rec.toString());
        rec.setLength(0);
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected final boolean recordIsNeeded(final GPSRecord s) {
        return ptFilters[GPSFilter.TRKPT].doFilter(s)
                || ptFilters[GPSFilter.WAYPT].doFilter(s);
    }

    // private GPSRecord prevRecord=null;
    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeRecord(gps.log.GPSRecord)
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        if (cachedRecordIsNeeded(r)) {
            /** The rows are filled while using named fields */

            if (r.hasPosition() && selectedFileFields.hasPosition()) {
                rec.setLength(0);
                rec.append(separatorChar);
                separatorChar = ',';
                rec.append(JavaLibBridge.toString(r.getLatitude(), 6));
                rec.append(' ');
                rec.append(JavaLibBridge.toString(r.getLongitude(), 6));
                rec.append(' ');
                if ((r.hasHeight()) && (selectedFileFields.hasHeight())) {
                    if (!imperial) {
                        rec.append(JavaLibBridge.toString(r.getHeight(), 3));
                    } else {
                        rec.append(JavaLibBridge.toString(r.getHeight()
                                * METERS_TO_FEET, 3));
                    }
                } else {
                    rec.append(0);
                }
                rec.append("\r\n");
                writeTxt(rec.toString());
                rec.setLength(0);
            }
        } // activeFields!=null
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#finaliseFile()
     */
    public void finaliseFile() {
        rec.setLength(0);
        rec.append("))')"); // End of multilineString
        rec.append(" );\r\n"); // End of values
        rec.append("COMMIT;\r\n");
        writeTxt(rec.toString());
        rec.setLength(0);
        super.finaliseFile();
    }
}
