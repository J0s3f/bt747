package net.sf.bt747.j4me.app;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;

import net.sf.bt747.j2me.app.ftp.SimpleFTP;
import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.logging.Log;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.TextBox;

import bt747.model.AppSettings;

public final class AgpsScreen extends BT747Dialog {
    private TextBox tbAgpsUrl;

    private boolean screenIsSetup = false;

    private void setupScreen() {
        if (!screenIsSetup) {
            screenIsSetup = true;
            deleteAll();
            setTitle("AGPS");

            append(new Label(
                    "Currently under dev&debug (no indication of success or failure)"));

            append(new Label("Enter the URL where the AGPS data is located:"
                    + "scheme://user:password@host:port/url-path;parameters"));

            tbAgpsUrl = new TextBox();
            tbAgpsUrl.setForDecimalOnly();
            tbAgpsUrl.setLabel("URL");
            append(tbAgpsUrl);

            updateButtons();
            invalidate();
        }
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    public void show() {
        Log.debug("AGPS Menu");
        setupScreen();
        super.show();
    }

    private final void updateButtons() {
        tbAgpsUrl.setString(m().getStringOpt(AppSettings.AGPSURL));
        repaint();
    }

    private final void setSettings() {
        c.setStringOpt(AppSettings.AGPSURL, tbAgpsUrl.getString());
        Log.debug("Agps Settings Updated");
        // c.setFixInterval(JavaLibBridge.toInt(edFix.getText()));
    }

    private final void uploadAgps() {
        new Thread(new Runnable() {
            public void run() {
                final String url = m().getStringOpt(AppSettings.AGPSURL);

                try {
                    if (url.startsWith("ftp://")) {
                        final int colonIdx = url.indexOf(':');
                        final int atIdx = url.indexOf('@');
                        final int slashIdx = url.indexOf('/', 6);

                        String hostUrl;
                        String user = "anonymous";
                        String pass = "anonymous";
                        if (atIdx < 0 || (colonIdx > 0 && colonIdx > atIdx)) {
                            hostUrl = url.substring(6);
                        } else {
                            if (colonIdx > 0 && colonIdx < atIdx) {
                                // Username and password.
                                user = url.substring(6, colonIdx - 1);
                                pass = url.substring(colonIdx + 1, atIdx - 1);
                            } else {
                                // Only username
                                user = url.substring(6, atIdx - 1);
                                pass = "";
                            }
                            hostUrl = url.substring(atIdx + 1);
                        }
                        final int hostSlash = hostUrl.indexOf('/');
                        if (hostSlash > 0) {
                            final String hostname = hostUrl.substring(0,
                                    hostSlash - 1);
                            final String path = hostUrl.substring(0,
                                    hostSlash - 1);
                            final int pathSlash = path.indexOf('/');
                            String dir;
                            String name;
                            if (pathSlash > 0) {
                                dir = path.substring(0, pathSlash - 1);
                                name = path.substring(pathSlash + 1);
                            } else {
                                dir = "";
                                name = path;
                            }

                            final SimpleFTP ftp = new SimpleFTP();
                            ftp.connect(hostname, 21, user, pass);
                            if (dir.length() > 0) {
                                ftp.connect(dir);
                            }
                            ByteArrayOutputStream os = new ByteArrayOutputStream(
                                    20 * 1024);
                            ftp.retr(os, name);
                            c.setAgpsData(os.toByteArray());
                        }
                    } else {
                        // ServerSocketConnection
                        // Socket

                        InputConnection con;
                        con = (InputConnection) Connector.open(url,
                                Connector.READ); // ,true
                        final InputStream is = con.openInputStream();
                        con = null;
                        byte[] b = new byte[120 * 1024];
                        byte[] buf = new byte[1024];
                        int i = 0;
                        while (true) {
                            final int n = is.read(buf);
                            if (n == -1) {
                                break;
                            }
                            // Copy buffer
                            for (int j = 0; i < b.length && j < n; j++, i++) {
                                buf[i] = buf[j];
                            }
                        }
                        c.setAgpsData(buf);
                    }
                } catch (Exception e) {
                    Log.debug("Problem during APGS download", e);
                }
            }
        }).start();
    }

    protected void acceptNotify() {
        setSettings();
        uploadAgps();
        previous.show();
        super.acceptNotify();
    }

    protected void declineNotify() {
        setSettings();
        previous.show();
        super.declineNotify();
    }
}
