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
package net.sf.bt747.j2se.app.osm;

import gps.log.out.GPSConversionParameters;
import gps.log.out.GPSGPXFile;

import java.io.File;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to upload to OSM.
 * 
 * @author Mario De Weerd
 * 
 */
public final class GPSOSMUploadFile extends GPSGPXFile {

    private static final String basenamePostfix = "-osm-tmp";

    /**
     * 
     */
    public GPSOSMUploadFile() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public final void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename.proto(basename.getPath() + basenamePostfix), ext,
                oneFilePerDay);
    }

    private final BT747Hashtable filenames = JavaLibBridge
            .getHashtableInstance(10);

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#createFile(int, java.lang.String)
     */
    @Override
    protected int createFile(final int utc, final String extra_ext,
            final boolean createNewFile) {
        final int error = super.createFile(utc, extra_ext, createNewFile);

        final String newFileName = getCurrentFileName().getPath();
        if (newFileName != null
                && newFileName.length() > basenamePostfix.length()) {
            int idx = newFileName.lastIndexOf(basenamePostfix);
            final String orgFileName = newFileName.substring(0, idx)
                    + newFileName.substring(idx + basenamePostfix.length());
            filenames.put(orgFileName, newFileName);

        }
        return error;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSGPXFile#nextPass()
     */
    @Override
    public boolean nextPass() {
        final boolean isNextPass = super.nextPass();
        if (!isNextPass) {
            String osmLogin = "";
            String osmPass = "";
            String osmVisibility;
            String osmDescription = null;
            String osmTags = "";
            if (getParamObject().hasParam(GPSConversionParameters.OSM_LOGIN)) {
                osmLogin = getParamObject().getStringParam(
                        GPSConversionParameters.OSM_LOGIN);
            }
            if (getParamObject().hasParam(GPSConversionParameters.OSM_PASS)) {
                osmPass = getParamObject().getStringParam(
                        GPSConversionParameters.OSM_PASS);
            }
            if (getParamObject().hasParam(
                    GPSConversionParameters.OSM_VISIBILITY)) {
                osmVisibility = getParamObject().getStringParam(
                        GPSConversionParameters.OSM_VISIBILITY);
            } else {
                osmVisibility = GPSConversionParameters.OSM_PRIVATE;
            }
            if (getParamObject().hasParam(
                    GPSConversionParameters.OSM_DESCRIPTION)) {
                osmDescription = getParamObject().getStringParam(
                        GPSConversionParameters.OSM_DESCRIPTION);
            }
            if (getParamObject().hasParam(GPSConversionParameters.OSM_TAGS)) {
                osmTags = getParamObject().getStringParam(
                        GPSConversionParameters.OSM_TAGS);
                if (osmTags.length() != 0) {
                    osmTags += ",";
                }
            }
            osmTags += "bt747_direct";
            osmTags = osmTags.replace(",,", ",");
            if (osmTags.charAt(0) == ',') {
                osmTags = osmTags.substring(1);
            }
            // Start uploading data.
            BT747Hashtable iter = filenames.iterator();
            while (iter.hasNext()) {
                final String orgFileName = (String) iter.nextKey();
                final String newFileName = (String) filenames
                        .get(orgFileName);
                String description = osmDescription;
                if (description == null) {
                    description = orgFileName;
                    description.replace("_osm_tmp_", "_");
                }
                try {
                    OsmGpxUpload.upload(osmLogin, osmPass, description,
                            osmTags, new File(newFileName), osmVisibility);
                    (new File(newFileName)).delete();
                } catch (Exception e) {
                    // TODO: improve upload error message handling.
                    Generic.exception("Upload for " + orgFileName + " failed", e);
                }
            }
        }
        return isNextPass;
    }
}
