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

import gps.log.out.GPSConversionParameters;
import gps.log.out.GPSFile;
import net.sf.bt747.j2se.app.utils.ExternalTool;
import net.sf.bt747.j2se.system.J2SERAFile;

import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * Class to convert files using external tool.
 * 
 * @author Mario De Weerd
 * 
 */
public final class ExternalToolConvert extends GpsFileProxyTemplate {

    // private static final String basenamePostfix = "-ext-tmp";
    //
    // /**
    // *
    // */
    public ExternalToolConvert(final GPSFile gpsFile) {
        super(gpsFile);
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
//     */
//    public final void initialiseFile(final String basename, final String ext,
//            final int card, final int oneFilePerDay) {
//        super.initialiseFile(basename + basenamePostfix, ext, card,
//                oneFilePerDay);
//    }

    private final BT747Hashtable filenames = JavaLibBridge
            .getHashtableInstance(10);

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSGPXFile#nextPass()
     */
    @Override
    public boolean nextPass() {
        final boolean isNextPass = super.nextPass();
        if (!isNextPass) {
            String extCommand = "echo No command given for %f";
            if (getParamObject()
                    .hasParam(GPSConversionParameters.EXT_COMMAND)) {
                extCommand = getParamObject().getStringParam(
                        GPSConversionParameters.EXT_COMMAND);
            }

            // Start uploading data.
            BT747HashSet iter = getFilesCreated().iterator();
            
            while (iter.hasNext()) {
                final String fileName = (String) iter.next();
                try {
                    ExternalTool et = new ExternalTool(extCommand);
                    BT747Hashtable ht = JavaLibBridge.getHashtableInstance(5);
                    ht.put("f", fileName);
                    Generic.debug(new String(et.execTool(ht)));
                    (new J2SERAFile(fileName)).delete();
                } catch (Exception e) {
                    // TODO: improve upload error message handling.
                    Generic.exception("External tool error for "
                            + fileName, e);
                }
            }
        }
        return isNextPass;
    }
}
