/**
 * 
 */
package bt747.j2se_view;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Mario
 *
 */
public final class KnownFileFilter extends FileFilter {

    /**
     * Lower case list of accepted extensions.
     */
    private final String[] extensions = {
            ".bin", ".nmea", ".txt", ".log", ".nme", ".nma", ".trl", ".csv", ".sr"
    };
    
    
    
    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        String filename = f.getName().toLowerCase();
        for (int i = 0; i < extensions.length; i++) {
             if(filename.endsWith(extensions[i])) {
                 return true;
             }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return J2SEAppController.getString("KNOWN_Description");
    }

}
