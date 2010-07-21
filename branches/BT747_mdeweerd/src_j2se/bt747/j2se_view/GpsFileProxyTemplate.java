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
package bt747.j2se_view;

import gps.log.GPSFilter;
import gps.log.GPSRecord;
import gps.log.out.GPSConversionParameters;
import gps.log.out.GPSFileInterface;

import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Path;

/**
 * Pure template to gpsFile class
 * 
 * @author Mario De Weerd
 * 
 */
public class GpsFileProxyTemplate implements GPSFileInterface {

    final protected GPSFileInterface delegate;

    public GpsFileProxyTemplate(final GPSFileInterface gpsFile) {
        delegate = gpsFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.in.GPSFileConverterInterface#addLogRecord(gps.log.GPSRecord)
     */
    final public void addLogRecord(GPSRecord r) {
        delegate.addLogRecord(r);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSFileConverterInterface#finaliseFile()
     */
    final public void finaliseFile() {
        delegate.finaliseFile();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSFileConverterInterface#getFilesCreated()
     */
    final public int getNbrFilesCreated() {
        return delegate.getNbrFilesCreated();
    }

    /* (non-Javadoc)
     * @see gps.log.in.GPSFileConverterInterface#getFilesCreated()
     */
    final public BT747HashSet getFilesCreated() {
        return delegate.getFilesCreated();
    }
    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.in.GPSFileConverterInterface#needPassToFindFieldsActivatedInLog
     * ()
     */
    final public boolean needPassToFindFieldsActivatedInLog() {
        return delegate.needPassToFindFieldsActivatedInLog();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSFileConverterInterface#nextPass()
     */
    public boolean nextPass() {
        return delegate.nextPass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.in.GPSFileConverterInterface#setActiveFileFields(gps.log.GPSRecord
     * )
     */
    final public void setActiveFileFields(GPSRecord activeFileFieldsFormat) {
        delegate.setActiveFileFields(activeFileFieldsFormat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.in.GPSFileConverterInterface#writeLogFmtHeader(gps.log.GPSRecord
     * )
     */
    final public void writeLogFmtHeader(GPSRecord f) {
        delegate.writeLogFmtHeader(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#getParamObject()
     */
    final public GPSConversionParameters getParamObject() {
        return delegate.getParamObject();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#initialiseFile(java.lang.String,
     * java.lang.String, int, int)
     */
    public void initialiseFile(BT747Path baseName, String extension,
            int fileSeparationFreq) {
        delegate.initialiseFile(baseName, extension, fileSeparationFreq);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setAddLogConditionInfo(boolean)
     */
    final public void setAddLogConditionInfo(boolean addLogConditionInfo) {
        delegate.setAddLogConditionInfo(addLogConditionInfo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setBadTrackColor(java.lang.String)
     */
    final public void setBadTrackColor(String badTrackColor) {
        delegate.setBadTrackColor(badTrackColor);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setFilenameBuilder(bt747.sys.interfaces
     * .BT747FileName)
     */
    public void setFilenameBuilder(BT747FileName filenameBuilder) {
        delegate.setFilenameBuilder(filenameBuilder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setFilters(gps.log.GPSFilter[])
     */
    public void setFilters(GPSFilter[] ourFilters) {
        delegate.setFilters(ourFilters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setGoodTrackColor(java.lang.String)
     */
    public void setGoodTrackColor(String goodTrackColor) {
        delegate.setGoodTrackColor(goodTrackColor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setImperial(boolean)
     */
    public void setImperial(boolean useImperial) {
        delegate.setImperial(useImperial);

    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setIncludeTrkName(boolean)
     */
    public void setIncludeTrkName(boolean isIncludeTrkName) {
        delegate.setIncludeTrkName(isIncludeTrkName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setMaxDiff(int)
     */
    public void setMaxDiff(int maxDiff) {
        delegate.setMaxDiff(maxDiff);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setOutputFields(gps.log.GPSRecord)
     */
    public void setOutputFields(GPSRecord selectedOutputFields) {
        delegate.setOutputFields(selectedOutputFields);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setOverridePreviousTag(boolean)
     */
    public void setOverridePreviousTag(boolean overridePreviousTag) {
        delegate.setOverridePreviousTag(overridePreviousTag);
    }

    /*
     * (non-Javadoc)
     * 
     * @seegps.log.out.GPSFileConfInterface#setParamObject(gps.log.out.
     * GPSConversionParameters)
     */
    public void setParamObject(GPSConversionParameters paramObject) {
        delegate.setParamObject(paramObject);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setRecordNbrInLogs(boolean)
     */
    public void setRecordNbrInLogs(boolean recordNbrInLogs) {
        delegate.setRecordNbrInLogs(recordNbrInLogs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setTimeOffset(int)
     */
    public void setTimeOffset(int offset) {
        delegate.setTimeOffset(offset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setTrackSepTime(int)
     */
    public void setTrackSepTime(int time) {
        delegate.setTrackSepTime(time);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.out.GPSFileConfInterface#setUserWayPointList(gps.log.GPSRecord
     * [])
     */
    public void setUserWayPointList(GPSRecord[] list) {
        delegate.setUserWayPointList(list);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFileConfInterface#setWayPointTimeCorrection(int)
     */
    public void setWayPointTimeCorrection(int seconds) {
        delegate.setWayPointTimeCorrection(seconds);
    }
}
