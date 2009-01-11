/**
 * 
 */
package bt747.j2se_view;

import java.io.File;

/**
 * @author Mario De Weerd
 * 
 */
public class TaggedFilePathFactory {

    private String destTemplate = "%p" + File.separator + "%f_tagged%e";
    private String orgTemplate = "%p" + File.separator + "%f%e_original";

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
        return getFilePath(destTemplate, currentPath, refObject);
    }

    /**
     * @param currentPath
     *                Current path for the object.
     * @param refObject
     *                Can be used in the future to use the object info (like a
     *                factory) to determine a better output path. For example,
     *                in the case of an image, use the description.
     * @return
     */
    public String getOrgFilePath(final String currentPath,
            final Object refObject) {
        return getFilePath(orgTemplate, currentPath, refObject);
    }

    /**
     * @param template
     *                The templacte used to determine the filename.
     * @param currentPath
     *                Current path for the object.
     * @param refObject
     *                Can be used in the future to use the object info (like a
     *                factory) to determine a better output path. For example,
     *                in the case of an image, use the description.
     * @return
     */
    public static final String getFilePath(final String template,
            final String currentPath, final Object refObject) {
        int orgDirIndex;
        String baseDir;
        String orgFile;
        String fileBase;
        String fileExt;
        String newPath;
        int lastIndex;
        int percentIndex;

        orgDirIndex = Math.max(currentPath.lastIndexOf('/'), currentPath
                .lastIndexOf('\\'));
        if (orgDirIndex >= 0) {
            baseDir = currentPath.substring(0, orgDirIndex + 1);
            orgFile = currentPath.substring(orgDirIndex + 1);
        } else {
            baseDir = "";
            orgFile = currentPath;
        }

        final int filePtIndex = orgFile.lastIndexOf('.');
        if (filePtIndex >= 0) {
            fileBase = orgFile.substring(0, filePtIndex);
            fileExt = orgFile.substring(filePtIndex);
        } else {
            fileBase = orgFile;
            fileExt = "";
        }

        newPath = template;
        lastIndex = 0;
        while ((percentIndex = newPath.indexOf('%', lastIndex)) >= 0) {
            if (percentIndex + 1 < newPath.length()) {
                final char type = newPath.charAt(percentIndex + 1);
                String replaceStr = null;
                switch (type) {
                case 'p':
                    replaceStr = baseDir;
                    break;
                case 'e':
                    replaceStr = fileExt;
                    break;
                case 'f':
                    replaceStr = fileBase;
                    break;
                default:
                    lastIndex = percentIndex + 1;
                    continue;
                }
                newPath = newPath.substring(0, percentIndex) + replaceStr
                        + newPath.substring(percentIndex + 2);
            } else {
                lastIndex = percentIndex + 1;
            }
        }
        return newPath;
    }

}
