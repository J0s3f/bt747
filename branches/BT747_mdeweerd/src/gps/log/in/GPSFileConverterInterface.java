/**
 * 
 */
package gps.log.in;

import bt747.sys.interfaces.BT747HashSet;

import gps.log.GPSRecord;

/**
 * @author Mario
 * 
 */
public interface GPSFileConverterInterface {

    public void writeLogFmtHeader(final GPSRecord f);

    public void addLogRecord(final GPSRecord r);

    public boolean needPassToFindFieldsActivatedInLog();

    public void setActiveFileFields(final GPSRecord activeFileFieldsFormat);

    public boolean nextPass();

    public int getNbrFilesCreated();
    
    public BT747HashSet getFilesCreated();

    public void finaliseFile();
}
