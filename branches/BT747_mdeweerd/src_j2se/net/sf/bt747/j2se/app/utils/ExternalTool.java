/**
 * 
 */
package net.sf.bt747.j2se.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.bt747.j2se.app.utils.StreamConnector;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public final class ExternalTool {

    private final String externalToolPath;

    /**
     * 
     */
    public ExternalTool(final String toolPath) {
        externalToolPath = toolPath;
    }

    /**
     * May need semaphore.
     */
    private void execTool(final String params) {
//        try {
//            final byte[] result = execExifTool(VERSION_OPTS);
//            exifToolVersion = (new String(result)).trim();
//            hasExifTool = true;
//        } catch (IOException e) {
//            Generic.debug("ExifTool", e);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        tested = true;
    }

    /**
     * check if exiftool can be executed and set the available field
     * accordingly.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private byte[] execTool(String[] args) throws IOException,
            InterruptedException {
        final ArrayList<String> argsList = new ArrayList<String>();

        /* User arguments */
        for (String s : args) {
            argsList.add(s);
        }

        return execTool(argsList);
    }

    /**
     * check if exiftool can be executed and set the available field
     * accordingly.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public byte[] execTool(final List<String> args)
            throws IOException, InterruptedException {

        /* Setup the command */
        final ArrayList<String> exifCommand = new ArrayList<String>();

        /* Exif tool path */
        exifCommand.add(externalToolPath);

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
