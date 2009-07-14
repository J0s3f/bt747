/**
 * 
 */
package net.sf.bt747.j2se.app.exiftool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.bt747.j2se.app.utils.StreamConnector;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public final class ExifTool {

    private static String exifToolPath = "exiftool";
    private static String exifToolVersion = null;
    private static boolean hasExifTool = false;
    private static boolean tested = false;

    public static void setExifToolPath(final String path) {
        exifToolPath = path;
        tested = false;
    }

    public static String getExifToolVersion() {
        if (hasExifTool) {
            return exifToolVersion;
        } else {
            return null;
        }
    }

    public static boolean hasExifTool() {
        if (!tested) {
            testExifTool();
        }
        return hasExifTool;
    }

    private final static String[] VERSION_OPTS = { "-ver" };

    /**
     * May need semaphore.
     */
    private static void testExifTool() {
        try {
            final byte[] result = execExifTool(VERSION_OPTS);
            exifToolVersion = (new String(result)).trim();
            hasExifTool = true;
        } catch (IOException e) {
            Generic.debug("ExifTool", e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tested = true;
    }

    /**
     * check if exiftool can be executed and set the available field
     * accordingly.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private static byte[] execExifTool(String[] args) throws IOException,
            InterruptedException {
        /* Setup the command */
        final ArrayList<String> exifCommand = new ArrayList<String>();

        /* Exif tool path */
        exifCommand.add(exifToolPath);

        /* User arguments */
        for (String s : args) {
            exifCommand.add(s);
        }

        final ProcessBuilder exifProcessBuilder = new ProcessBuilder();
        exifProcessBuilder.command(exifCommand);
        exifProcessBuilder.redirectErrorStream(true);

        final Process exifProcess = exifProcessBuilder.start();
        final ByteArrayOutputStream bo = new ByteArrayOutputStream();

        StreamConnector.connect(exifProcess.getInputStream(), bo);

        exifProcess.waitFor();

        return bo.toByteArray();
    }

}
