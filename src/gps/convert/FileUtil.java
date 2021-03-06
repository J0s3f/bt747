package gps.convert;

/**
 * @author Curt Arnold <br>
 *         Original
 * @author Mario De Weerd <br>
 *         Adapted to BT747
 * 
 */
public final class FileUtil {
    /**
     * Returns a relative path for the targetFile relative to the base
     * directory.
     * 
     * @param base
     *                base directory as returned by File.getCanonicalPath()
     * @param target
     *                target file
     * @param separator
     *                path separator on the system
     * @return relative path of target file. Returns targetFile if there were
     *         no commonalities between the base and the target
     * 
     */
    public final static String getRelativePath(final String base,
            final String target, final char separator) {
        try {
            //
            // remove trailing file separator
            //
            String canonicalBase = base;

            if ((base.charAt(base.length() - 1) == '/')
                    || (base.charAt(base.length() - 1) == '\\')) {
                canonicalBase = base.substring(0, base.length() - 1);
            }

            //
            // get canonical name of target and remove trailing separator
            //

            String canonicalTarget;

            canonicalTarget = target;

            if ((canonicalTarget.charAt(canonicalTarget.length() - 1) == '/')
                    || (canonicalTarget.charAt(canonicalTarget.length() - 1) == '\\')) {
                canonicalTarget = canonicalTarget.substring(0,
                        canonicalTarget.length() - 1);
            }

            if (canonicalTarget.equals(canonicalBase)) {
                return ".";
            }

            //
            // see if the prefixes are the same
            //
            if (canonicalBase.substring(0, 2).equals("\\\\")) {
                //
                // UNC file name, if target file doesn't also start with same
                // server name, don't go there
                final int endPrefix = canonicalBase.indexOf('\\', 2);
                final String prefix1 = canonicalBase.substring(0, endPrefix);
                final String prefix2 = canonicalTarget
                        .substring(0, endPrefix);
                if (!prefix1.equals(prefix2)) {
                    return canonicalTarget;
                }
            } else {
                if (canonicalBase.substring(1, 3).equals(":\\")) {
                    final int endPrefix = 2;
                    final String prefix1 = canonicalBase.substring(0,
                            endPrefix);
                    final String prefix2 = canonicalTarget.substring(0,
                            endPrefix);
                    if (!prefix1.equals(prefix2)) {
                        return canonicalTarget;
                    }
                } else {
                    if (canonicalBase.charAt(0) == '/') {
                        if (canonicalTarget.charAt(0) != '/') {
                            return canonicalTarget;
                        }
                    }
                }
            }

            // char separator = File.separatorChar;
            int lastSeparator = -1;
            int minLength = canonicalBase.length();

            if (canonicalTarget.length() < minLength) {
                minLength = canonicalTarget.length();
            }

            int firstDifference = minLength + 1;

            //
            // walk to the shorter of the two paths
            // finding the last separator they have in common
            for (int i = 0; i < minLength; i++) {
                if (canonicalTarget.charAt(i) == canonicalBase.charAt(i)) {
                    if ((canonicalTarget.charAt(i) == '/')
                            || (canonicalTarget.charAt(i) == '\\')) {
                        lastSeparator = i;
                    }
                } else {
                    firstDifference = lastSeparator + 1;
                    break;
                }
            }

            final StringBuffer relativePath = new StringBuffer(50);

            //
            // walk from the first difference to the end of the base
            // adding "../" for each separator encountered
            //
            if (canonicalBase.length() > firstDifference) {
                relativePath.append("..");
                for (int i = firstDifference; i < canonicalBase.length(); i++) {
                    if ((canonicalBase.charAt(i) == '/')
                            || (canonicalBase.charAt(i) == '\\')) {
                        relativePath.append(separator);
                        relativePath.append("..");
                    }
                }
            }

            if (canonicalTarget.length() > firstDifference) {
                //
                // append the rest of the target
                //

                if (relativePath.length() > 0) {
                    relativePath.append(separator);
                }
                relativePath.append(canonicalTarget
                        .substring(firstDifference));
            }

            return relativePath.toString();
        } catch (final Exception ex) {
            // TODO more handling
        }
        return target;
    }
}
