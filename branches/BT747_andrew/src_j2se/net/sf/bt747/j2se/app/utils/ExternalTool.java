/**
 * 
 */
package net.sf.bt747.j2se.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747StringTokenizer;

/**
 * @author Mario
 * 
 */
public final class ExternalTool {

    private final String externalToolCmd;

    /**
     * 
     */
    public ExternalTool(final String toolCmdLine) {
        externalToolCmd = toolCmdLine;
    }

    /**
     * Execute tool and use tokens to replace arguments
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    public final byte[] execTool(final BT747Hashtable tokens)
            throws IOException, InterruptedException {
        // externalToolCmd;
        final Vector<String> args = new Vector<String>();
        final BT747StringTokenizer tk = JavaLibBridge
                .getStringTokenizerInstance(externalToolCmd, ' ');
        while (tk.hasMoreTokens()) {
            final String param = Generic.expandPercentTokens(tk.nextToken(),
                    tokens);
            args.add(param);
        }
        return execTool(args);
    }

    /**
     * Execute the command.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private final byte[] execTool(final List<String> exifCommand)
            throws IOException, InterruptedException {
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
