/**
 * 
 */
package net.sf.bt747.gps.log.out.test;

import gps.log.out.GPSFile;
import gps.log.out.GPSPostGISFile;

import bt747.model.Model;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 * 
 */
public class PostGISTest extends TestConvertOutBase {

    public void testSql() throws Exception {
        GPSFile gpsFile;

        setupCsvInputConverter();
        gpsFile = new GPSPostGISFile();
        configureGpsFile(gpsFile);
        gpsFile.initialiseFile(
                new BT747Path(logSource.getPath()+".postgissql"), "",
                Model.SPLIT_ONE_FILE);

        doConversionTest(gpsFile);
    }

}
