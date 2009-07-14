package net.sf.bt747.j4me.app;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;

import net.sf.bt747.j2me.app.ftp.SimpleFTP;
import net.sf.bt747.j4me.app.screens.BT747Dialog;
import net.sf.bt747.j4me.app.screens.ErrorAlert;

import org.j4me.logging.Level;
import org.j4me.logging.Log;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.TextBox;

import bt747.Version;
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
            // tbAgpsUrl.setForAnyText();
            tbAgpsUrl.setLabel("URL");
            append(tbAgpsUrl);

            updateButtons();
            invalidate();
        }
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    public void showNotify() {
        Log.setLevel(Level.DEBUG);
        Log.debug("AGPS Menu");
        setupScreen();
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
        String tmp = m().getStringOpt(AppSettings.AGPSURL);
        if (tmp.length() < 6 || tmp.equals(AppSettings.DUMMY_AGPS_STRING)) {
            // #ifdef nodefault
//@            tmp = Version.AURL + "MTK7d.EPO";
            // #else
            // This URL is only for BT747!
            // #define agpsurlbase = 'ftp://tsi0013:wrqttr@210.68.183.169/'
            // #expand tmp = "%agpsurlbase%MTK7d.EPO";
            tmp = "ftp://tsi0013:wrqttr@210.68.183.169/MTK7d.EPO";
            // #endif

        }
        final String srcUrl = tmp;
        new Thread(new Runnable() {
            final String url = srcUrl;

            public void run() {
                // Next line for debug (need to modify login and pass!).
                // c.setStringOpt(AppSettings.AGPSURL,"ftp://bt747p:ass@ftpperso.free.fr/MTK7d.EPO");

                byte[] agpsData = null;
                try {
                    if (url.startsWith("ftp://")) {
                        final int colonIdx = url.indexOf(':', 6);
                        final int atIdx = url.indexOf('@');
                        // final int slashIdx = url.indexOf('/', 6);

                        String hostUrl;
                        String user = "anonymous";
                        String pass = "anonymous";
                        if (atIdx < 0 || (colonIdx > 0 && colonIdx > atIdx)) {
                            hostUrl = url.substring(6);
                        } else {
                            if (colonIdx > 0 && colonIdx < atIdx) {
                                // Username and password.
                                user = url.substring(6, colonIdx);
                                pass = url.substring(colonIdx + 1, atIdx);
                            } else {
                                // Only username
                                user = url.substring(6, atIdx);
                                pass = "";
                            }
                            hostUrl = url.substring(atIdx + 1);
                        }
                        final int hostSlash = hostUrl.indexOf('/');
                        if (hostSlash > 0) {
                            final String hostname = hostUrl.substring(0,
                                    hostSlash);
                            final String path = hostUrl
                                    .substring(hostSlash + 1);
                            final int pathSlash = path.indexOf('/');
                            String dir;
                            String name;
                            if (pathSlash > 0) {
                                dir = path.substring(0, pathSlash);
                                name = path.substring(pathSlash + 1);
                            } else {
                                dir = "";
                                name = path;
                            }

                            if (Log.isDebugEnabled()) {
                                Log.debug("<User>" + user //+ "<Pass>" + pass
                                        + "<Site>" + hostname + "<Dir>" + dir
                                        + "<name>" + name);
                            }
                            final SimpleFTP ftp = new SimpleFTP();
                            ftp.connect(hostname, 21, user, pass);
                            if (dir.length() > 0) {
                                ftp.connect(dir);
                            }
                            final ByteArrayOutputStream os = new ByteArrayOutputStream(
                                    120 * 1024);
                            ftp.bin();
                            ftp.retr(os, name);
                            ftp.disconnect();
                            agpsData = os.toByteArray();
                            os.close();
                        }
                    } else {
                        Log.debug("URL = " + url);
                        // ServerSocketConnection
                        // Socket

                        InputConnection con;
                        con = (InputConnection) Connector.open(url,
                                Connector.READ); // ,true
                        final InputStream is = con.openInputStream();
                        con = null;
                        final ByteArrayOutputStream os = new ByteArrayOutputStream(
                                120 * 1024);
                        byte[] buf = new byte[1024];
                        int i = 0;
                        while (true) {
                            final int n = is.read(buf,0,1024);
                            if (n == -1) {
                                break;
                            }
                            os.write(buf,0,n);
                        }
                        is.close();
                        buf = null;
                        agpsData = os.toByteArray();
                    }
                } catch (Exception e) {
                    Log.debug("Problem during AGPS download", e);
                    (new ErrorAlert("Error", "Problem during AGPS download\n"
                            + e.getMessage(), UIManager.getScreen())).show();
                }
                if (agpsData != null) {
                    Log.debug("Array size = " + agpsData.length);
                    try {
                        Log.info("Got AGPS data");
                        c.setAgpsData(agpsData);
                        Log.info("AGPS upload initiated");
                    } catch (Exception e) {
                        Log.debug("Problem during AGPS upload", e);
                        (new ErrorAlert("Error",
                                "Problem during AGPS upload\n"
                                        + e.getMessage(), UIManager
                                        .getScreen())).show();
                    }
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