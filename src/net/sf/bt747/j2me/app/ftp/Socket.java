/**
 * 
 */
package net.sf.bt747.j2me.app.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

/**
 * @author Mario
 * 
 */

public class Socket {
    SocketConnection c;

    public Socket(final String host, final int port) throws IOException {
        c = (SocketConnection) Connector
                .open("socket://" + host + ":" + port);
    }

    public InputStream getInputStream() throws IOException {
        return c.openInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return c.openOutputStream();
    }

    public void close() throws IOException {
        c.close();
    }

}
