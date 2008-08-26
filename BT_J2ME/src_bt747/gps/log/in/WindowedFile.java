package gps.log.in;

import bt747.generic.Generic;
import bt747.io.File;

/**
 * This class copes with the inability of the VM to handle random access files
 * when one wants to look at the file content using a forward moving window on
 * the file. The new start position of the window must be at least the previous
 * start position of the window. This class performs the required internal
 * buffer copy and then required file read.
 * 
 * @author Mario De Weerd
 * 
 */
public class WindowedFile {

    private File file;
    private String path;
    private int mode;
    private int card;

    public WindowedFile(String path, int mode, int card) throws Exception {
        this.path = path;
        this.mode = mode;
        this.card = card;
        open();
    }

    private void open() throws Exception {
        file = new File(path, mode, card);
    }

    private int bufferSize = 1000;
    private byte[] buffer;
    private int bufferFill = 0;
    private int currentPosition;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public final int getBufferSize() {
        return bufferSize;
    }

    public final String getPath() {
        return file.getPath();
    }
    
    public final int getLastError() {
        return file.lastError;
    }
    
    public final boolean isOpen() {
        return file.isOpen();
    }
    
    public final int getSize() throws Exception {
        return file.getSize();
    }
    public final byte[] getBuffer() throws Exception {
        if (bufferFill == 0) {
            fillBuffer(0);
        }
        return buffer;
    }

    private final void sanitizeBuffer() {
        if (buffer == null) {
            buffer = new byte[bufferSize];
            bufferFill = 0;
        }
    }

    public final byte[] fillBuffer(int newPosition) throws Exception {
        sanitizeBuffer();
        if (newPosition < currentPosition) {
            file.close();
            file = null;
            open();
            bufferFill = 0;
            currentPosition = 0;
        }
        if (newPosition > currentPosition + bufferFill) {
            // Need to skip bytes
            int bytesToSkip = newPosition - currentPosition + bufferFill;
            do {
                int bytesToRead = bytesToSkip;
                if (bytesToRead > bufferSize) {
                    bytesToRead = bufferSize;
                }
                file.readBytes(buffer, 0, bytesToRead);
                bytesToSkip -= bytesToRead;
            } while (bytesToSkip > 0);
        }
        if (newPosition > currentPosition
                && newPosition < currentPosition + bufferFill) {
            int j = 0;
            for (int i = newPosition - currentPosition; i < bufferFill; j++, i++) {
                buffer[j] = buffer[i];
            }
            bufferFill -= newPosition - currentPosition;
        } else if (newPosition == currentPosition) {
            // No operation
        }
        bufferFill = 0;
        try {
            currentPosition = newPosition;
            bufferFill = file.readBytes(buffer, bufferFill, bufferSize
                    - bufferFill);
        } catch (Exception e) {
            Generic.debug("Read problem during fillBuffer", e);
            return null;
        }
        return buffer;
    }

    public final boolean close() throws Exception {
        boolean result;
        buffer = null;

        result = file.close();
        file = null;
        return result;
    }

}
