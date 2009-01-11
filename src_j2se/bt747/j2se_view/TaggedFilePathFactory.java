/**
 * 
 */
package bt747.j2se_view;

import java.io.File;

/**
 * @author Mario
 * 
 */
public class TaggedFilePathFactory {

    private String template = "%p" + File.pathSeparator + "%f_tagged%e";

    /**
     * @param currentPath
     *                Current path for the object.
     * @param refObject
     *                Can be used in the future to use the object info (like a
     *                factory) to determine a better output path. For example,
     *                in the case of an image, use the description.
     * @return
     */
    public String getTaggedFilePath(final String currentPath,
            final Object refObject) {
        int orgDirIndex;
        orgDirIndex = Math.max(currentPath.lastIndexOf('/'), currentPath
                .lastIndexOf('\\'));
        String baseDir;
        String orgFile;
        if (orgDirIndex >= 0) {
            baseDir = currentPath.substring(0, orgDirIndex + 1);
            orgFile = currentPath.substring(orgDirIndex + 1);
        } else {
            baseDir = "";
            orgFile = currentPath;
        }
        
        int filePtIndex = orgFile.lastIndexOf('.');
        String fileBase;
        String fileExt;
        if(filePtIndex>=0) {
            fileBase = orgFile.substring(0,filePtIndex+1);
            fileExt = orgFile.substring(filePtIndex+1);
        } else {
            fileBase = orgFile;
            fileExt = "";
        }
        
        final int ptIndex = currentPath.lastIndexOf('.');
        String newPath;
        newPath = currentPath.substring(0, ptIndex);
        newPath += "_tagged";
        newPath += currentPath.substring(ptIndex);
        return newPath;
    }

}
