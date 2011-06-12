/**
 * 
 */
package net.sf.bt747.gps.log.out.test;

import gps.log.out.GPSFile;
import gps.log.out.GPSSqlFile;

import bt747.model.Model;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 * 
 */
public class SqlTest extends TestConvertOutBase {

    public void testSql() throws Exception {
        GPSFile gpsFile;

        setupCsvInputConverter();
        gpsFile = new GPSSqlFile();
        configureGpsFile(gpsFile);
        gpsFile.initialiseFile(
                new BT747Path(logSource.getPath()+".sql"), "",
                Model.SPLIT_ONE_FILE);

        doConversionTest(gpsFile);
    }

}
