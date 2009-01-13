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

import gps.BT747Constants;
import gps.log.out.GPSKMLFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.bt747.j2se.system.J2SEHashtable;

import bt747.sys.Generic;

/**
 * Class to write a KML file.
 * 
 * @author Mario De Weerd
 * 
 */
public final class GPSKMZFile extends GPSKMLFile {
    private ZipOutputStream currentZipStream;

    private J2SEHashtable zips;

    /**
     * 
     */
    public GPSKMZFile() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public final void initialiseFile(final String basename, final String ext,
            final int card, final int oneFilePerDay) {
        super.initialiseFile(basename, ext, card, oneFilePerDay);
        zips = new J2SEHashtable(10);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#FinaliseFile()
     */
    public final void finaliseFile() {
        super.finaliseFile();
        if (currentZipStream != null) {
            try {
                zips.remove(currentZipStream);
            } catch (final Exception e) {
                Generic.debug("zip stream removal", e);
            }
            try {
                currentZipStream.closeEntry();
                currentZipStream.close();
            } catch (final Exception e) {
                Generic.debug("finaliseFile", e);
            }
            currentZipStream = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#createFile(int, java.lang.String)
     */
    @Override
    protected int createFile(final int utc, final String extra_ext,
            final boolean createNewFile) {
        String zipFileName;
        String zipEntryFileName;
        setTrackName(extra_ext);
        zipFileName = filenameBuilder.getOutputFileName(basename, utc,
                ".kmz", extra_ext);
        zipEntryFileName = basename + extra_ext + ".kml";
        int l;
        l = zipEntryFileName.lastIndexOf('/');
        if (l > 0) {
            zipEntryFileName = zipEntryFileName.substring(l + 1);
        }
        l = zipEntryFileName.lastIndexOf('\\');
        if (l > 0) {
            zipEntryFileName = zipEntryFileName.substring(l + 1);
        }
        l = zipEntryFileName.lastIndexOf(':');
        if (l > 0) {
            zipEntryFileName = zipEntryFileName.substring(l + 1);
        }

        int error = BT747Constants.NO_ERROR;

        // Check if file exists and delete - not needed in KMZ
        try {
            if (createNewFile) {
                final File tmpFile = new File(zipFileName);
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
        } catch (final Exception e) {
            Generic.debug("File deletion", e);
            // TODO: handle problem
        }

        try {
            currentZipStream = null;
            if (createNewFile) {
                final FileOutputStream fos = new FileOutputStream(
                        zipFileName, false);
                final BufferedOutputStream bos = new BufferedOutputStream(fos);
                currentZipStream = new ZipOutputStream(bos);
                final ZipEntry e = new ZipEntry(zipEntryFileName);
                currentZipStream.putNextEntry(e);
                zips.put(zipFileName, currentZipStream);
            } else {
                if (zips == null) {
                    Generic.debug("Zip name is null ");
                }
                currentZipStream = (ZipOutputStream) zips.get(zipFileName);
                if (currentZipStream == null) {
                    Generic.debug("Could not find " + zipFileName
                            + " zip stream.");
                }
            }
            // try {
            // int mode = createNewFile ? File.CREATE : File.WRITE_ONLY;
            // outFile = new File(fileName, mode, card);
        } catch (final Exception e) {
            Generic.debug("Zip Entry Creation", e);
            // TODO: handle exception
        }

        return error;
    }

    protected final void writeTxt(final String s) {
        try {
            if (currentZipStream != null) {
                currentZipStream.write(s.getBytes(), 0, s.length());
            } else {
                Generic.debug("Write to closed file", null);
            }
        } catch (final Exception e) {
            Generic.debug("writeTxt", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#isOpen()
     */
    @Override
    protected boolean isOpen() {
        return currentZipStream != null;
    }
}
