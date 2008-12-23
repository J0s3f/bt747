package gps.log.in;

import bt747.sys.File;
import bt747.sys.Generic;

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
public final class WindowedFile {

    private File file;
    private String path;
    private int mode;
    private int card;

    public WindowedFile(final String path, final int mode, final int card)
            throws Exception {
        this.path = path;
        this.mode = mode;
        this.card = card;
        open();
    }

    private void open() {
        file = new File(path, mode, card);
    }

    private int bufferSize = 0x400;
    private byte[] buffer;
    private int bufferFill = 0;
    private int currentPosition;

    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getPath() {
        return file.getPath();
    }

    public int getLastError() {
        return file.getLastError();
    }

    public boolean isOpen() {
        return file.isOpen();
    }

    public int getSize() throws Exception {
        return file.getSize();
    }

    public byte[] getBuffer() throws Exception {
        if (bufferFill == 0) {
            fillBuffer(0);
        }
        return buffer;
    }

    private void sanitizeBuffer() {
        if (buffer == null) {
            buffer = new byte[bufferSize];
            bufferFill = 0;
        }
    }

    private final void readBytes(final int bytesToRead) {
        currentPosition += bufferFill;
        bufferFill = file.readBytes(buffer, 0, bytesToRead);
    }

    public final byte[] fillBuffer(final int newPosition) {
        sanitizeBuffer();
        if (newPosition < currentPosition) {
            // Initialize pointer, position, ...
            file.close();
            file = null;
            open();
            bufferFill = 0;
            currentPosition = 0;
        }
        if (newPosition > currentPosition + bufferFill) {
            // Need to skip bytes
            int bytesToSkip = newPosition - currentPosition - bufferFill;
            do {
                int bytesToRead = bytesToSkip;
                if (bytesToRead > bufferSize) {
                    bytesToRead = bufferSize;
                }
                readBytes(bytesToRead);
                bytesToSkip -= bufferFill;
            } while (bytesToSkip > 0);
        }
        if (newPosition > currentPosition
                && newPosition < currentPosition + bufferFill) {
            // New position is already in buffer - copy bytes that are
            // present.
            int j = 0;
            int bytesToSkip = newPosition - currentPosition;
            for (int i = bytesToSkip; i < bufferFill; j++, i++) {
                buffer[j] = buffer[i];
            }
            bufferFill -= bytesToSkip;
            currentPosition += bytesToSkip;

        } else if (newPosition >= currentPosition + bufferFill) {
            currentPosition += bufferFill;
            bufferFill = 0;
        }
        if (bufferSize != bufferFill) {
            try {
                if (currentPosition != newPosition) {
                    Generic.debug(path + ": Problem in position " + currentPosition
                            + " request:" + newPosition);
                }
//                currentPosition = newPosition;
                bufferFill += file.readBytes(buffer, bufferFill, bufferSize
                        - bufferFill);
            } catch (Exception e) {
                Generic.debug("Read problem during fillBuffer", e);
                return null;
            }
        }
        return buffer;
    }

    public final int getBufferFill() {
        return bufferFill;
    }

    public final boolean close() {
        boolean result;
        buffer = null;

        result = file.close();
        file = null;
        return result;
    }

}
