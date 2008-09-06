// Started initially from http://forums.sun.com/thread.jspa?messageID=10356617

package net.sf.bt747.btserver;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sun.kvem.bluetooth.DiscoveryAgent;
import com.sun.kvem.bluetooth.LocalDevice;

public class Server implements SerialPortEventListener, Runnable {
    public static final String UUID_STRING = "A781FDBA229B486A8C21CEBD00000000";
    public static final String SERVICE_NAME = "btGPSBridge";
    private StreamConnectionNotifier server;

    JFrame jframe;
    JTextArea textArea;

    public Server() {

    }

    SerialPort sp;
    static InputStream spIs;
    static OutputStream spOs;

    // Static for the moment - one server at a time
    static InputStream is;
    static OutputStream os;

    private int connectLocalSerial() {
        int result = -1;
        try {
            String portStr = "COM4";
            System.out.println("Info: trying to open " + portStr);
            CommPortIdentifier portIdentifier;
            portIdentifier = CommPortIdentifier.getPortIdentifier(portStr);
            if (portIdentifier.isCurrentlyOwned()) {
                logMessage("Error: Port is currently in use");
            } else {
                CommPort commPort = portIdentifier.open(getClass().getName(),
                        2000);
                if (commPort instanceof SerialPort) {
                    sp = (SerialPort) commPort;
                    sp.setSerialPortParams(115200, 8, 1, 0);
                    spIs = sp.getInputStream();
                    spOs = sp.getOutputStream();
                    logMessage("opened");
                    result = 0;
                } else {
                    sp = null;
                    spIs = null;
                    spOs = null;
                    System.out
                            .println("Error: Only serial ports are handled by this example.");
                }
            }
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void doWork() {
        this.jframe = new JFrame("BT Server");
        this.jframe.setLayout(new BorderLayout());

        this.textArea = new JTextArea(6, 20);
        this.textArea.setVisible(true);
        JScrollPane jsp = new JScrollPane(this.textArea);
        this.jframe.add(jsp, BorderLayout.CENTER);

        this.jframe.pack();
        this.jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jframe.setVisible(true);
        this.jframe.repaint();

        myStartServer();
    }

    StreamConnection conn;

    public void logMessage(String message) {
        this.textArea.setText(this.textArea.getText() + message + "\n");
        this.textArea.setCaretPosition(this.textArea.getText().length());
        this.textArea.repaint();
        System.out.println(message);
        System.out.flush();
    }

    public void disconnect() {
        logMessage("Disconnecting");
        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        is = null;
        try {
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        os = null;
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn = null;
        try {
            sp.removeEventListener();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sp.close();
        } catch (Exception e) {
            this.logMessage(e.getMessage());
            e.printStackTrace();
        }
        sp = null;
        try {
            spIs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        spIs = null;
        try {
            spOs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        spOs = null;

        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        server = null;
        myStartServer();
    }

    private void myStartServer() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                if (server == null) {
                    startServer();
                }
            }
        });
    }

    public void startServer() {
        LocalDevice local;
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);
            this.logMessage("max of "
                    + LocalDevice
                            .getProperty("bluetooth.connected.devices.max")
                    + " connection(s) supported");

            String url = "btspp://localhost:" + UUID_STRING + ";name="
                    + SERVICE_NAME;

            server = (StreamConnectionNotifier) Connector.open(url);
            this.logMessage("waiting for connection...");

            // while (true) {
            conn = server.acceptAndOpen();
            this.logMessage("connection opened");
            connectLocalSerial();
            is = conn.openInputStream();
            os = conn.openOutputStream();

            sp.addEventListener(this);
            sp.notifyOnDataAvailable(true);

            java.awt.EventQueue.invokeLater(this);
            // }
        } catch (Exception e) {
            e.printStackTrace();
            this.logMessage(e.getMessage());
        }
    }

    public void run() {
        byte buffer[] = new byte[1000];
        boolean c = true;
        logMessage("Run");

        while (c) {
            try {
                // logMessage("Loop");
                if (is.available() > 0) {
                    int numChars = is.read(buffer);
                    String s = new String(buffer);
                    logMessage("received from mobile phone: "
                            + s.substring(0, numChars));
                    spOs.write(buffer, 0, numChars);
                    spOs.flush();
                } else {
                    Thread.sleep(20);
                }
            } catch (Throwable e) {
                // TODO: handle exception
                e.printStackTrace();
                c = false;
            }
        }
        disconnect();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                net.sf.bt747.btserver.Server svr = new net.sf.bt747.btserver.Server();
                svr.doWork();
            }
        });
    }

    public void serialEvent(SerialPortEvent arg0) {
        // arg

        if (arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            byte[] buffer = new byte[1000];

            try {
                while (spIs != null && spIs.available() > 0) {
                    int numChars = spIs.read(buffer);
                    if (numChars > 0) {
                        String s = new String(buffer);
                        /*
                         * logMessage("sent to mobile phone: " + s.substring(0,
                         * numChars));
                         */
                        os.write(buffer, 0, numChars);
                        os.flush();
                    }
                }
            } catch (IOException e) {
                try {
                    disconnect();
                } catch (Throwable n) {
                    n.printStackTrace();
                }
            }
        }

    }

}
