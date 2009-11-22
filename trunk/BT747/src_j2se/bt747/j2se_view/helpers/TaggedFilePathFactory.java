/**
 * 
 */
package bt747.j2se_view.helpers;

import java.io.File;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 * 
 */
public class TaggedFilePathFactory {

    private String destTemplate = "%p" + File.separator + "%f_tagged%e";
    private String orgTemplate = "%p" + File.separator + "%f%e_original";

    /**
     * @param currentPath
     *            Current path for the object.
     * @param refObject
     *            Can be used in the future to use the object info (like a
     *            factory) to determine a better output path. For example, in
     *            the case of an image, use the description.
     * @return
     */
    public String getTaggedFilePath(final String currentPath,
            final Object refObject) {
        return getFilePath(destTemplate, currentPath, refObject);
    }

    /**
     * @param currentPath
     *            Current path for the object.
     * @param refObject
     *            Can be used in the future to use the object info (like a
     *            factory) to determine a better output path. For example, in
     *            the case of an image, use the description.
     * @return
     */
    public String getOrgFilePath(final String currentPath,
            final Object refObject) {
        return getFilePath(orgTemplate, currentPath, refObject);
    }

    /**
     * @param template
     *            The templacte used to determine the filename.
     * @param currentPath
     *            Current path for the object.
     * @param refObject
     *            Can be used in the future to use the object info (like a
     *            factory) to determine a better output path. For example, in
     *            the case of an image, use the description.
     * @return
     */
    public static final String getFilePath(final String template,
            final String currentPath, final Object refObject) {
        int orgDirIndex;
        String baseDir;
        String orgFile;
        String fileBase;
        String fileExt;

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

        final BT747Hashtable tokens = JavaLibBridge.getHashtableInstance(5);
        tokens.put("p", baseDir);
        tokens.put("e", fileExt);
        tokens.put("f", fileBase);

        return Generic.expandPercentTokens(template, tokens);
    }

    public final String getDestTemplate() {
        return this.destTemplate;
    }

    public final void setDestTemplate(String destTemplate) {
        this.destTemplate = destTemplate;
    }

    public final String getOrgTemplate() {
        return this.orgTemplate;
    }

    public final void setOrgTemplate(String orgTemplate) {
        this.orgTemplate = orgTemplate;
    }

}
