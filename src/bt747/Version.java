package bt747;

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

/**
 * This class has a partial automatic update in the build script. and is used
 * to reference the version.
 * 
 * @link Original code found on
 *       http://forum.java.sun.com/thread.jspa?forumID=31&threadID=583820
 * @author Mario De Weerd
 */
public final class Version {

    /** Build number (timestamp with format yyyyMMddHHmmssSSS). */
    public static final long BUILD = 00000000000000000L; // automatically
    // set during Ant
    // compilation!
    public static final String BUILD_STR = ".DEVELOPMENTVERSION"; // automatically
    // set
    // during
    // Ant
    // compilation!
    /** Release date of this version (date format dd.MM.yyyy). */
    public static final String DATE = "00.00.0000"; // automatically set
    // during Ant compilation!
    /**
     * Version number of format x.y.z, with
     * <ul>
     * <li>x = major version
     * <li>y = minor version
     * <li>z = bug fix version
     * </ul>
     */
    public static final String VERSION_NUMBER = "2.dev";

    /**
     * URL for base address of AGPS data. Replaced during build process.
     * http://epodownload.mediatek.com/EPO.DAT is not a good source.
     */
    public static final String AURL = "http://bt747.org/MTK14.EPO";
}
