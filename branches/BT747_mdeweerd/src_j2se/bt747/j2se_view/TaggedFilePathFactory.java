/**
 * 
 */
package bt747.j2se_view;

/**
 * @author Mario
 *
 */
public class TaggedFilePathFactory {

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
        final int ptIndex = currentPath.lastIndexOf('.');
        String newPath;
        newPath = currentPath.substring(0, ptIndex);
        newPath += "_tagged";
        newPath += currentPath.substring(ptIndex);
        return newPath;
    }

}
