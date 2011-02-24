// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package net.sf.bt747.j4me.app;

import gps.GpsEvent;
import gps.convert.ExternalUtils;
import gps.log.GPSRecord;

import java.util.Date;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Menu;
import org.j4me.ui.Theme;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.HorizontalRule;
import org.j4me.ui.components.Label;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;

public final class GpsPositionScreen extends
        net.sf.bt747.j4me.app.screens.BT747Dialog implements ModelListener {
    /**
     * The current latitude.
     */
    private FieldValue latitude = new FieldValue("Latitude");

    /**
     * The current longitude.
     */
    private FieldValue longitude = new FieldValue("Longitude");

    /**
     * Indication of fix.
     */
    private FieldValue fvFix = new FieldValue("Fix");

    /**
     * An indication of accuracy.
     */
    private FieldValue fvHdop = new FieldValue("HDOP");

    /**
     * The current accuracy of the latitude and longitude in meters.
     */
    private FieldValue NSAT = new FieldValue("Satellites (#):");

    /**
     * The current altitude in meters.
     */
    private FieldValue fvAltitude = new FieldValue("Altitude (m)");

    /**
     * The current accuracy of the altitude in meters.
     */
    private FieldValue verticalAccuracy = new FieldValue(
            "Vertical accuracy (ft)");

    /**
     * The current speed in meters per second.
     */
    private FieldValue fvSpeed = new FieldValue("Speed (km/h)");

    /**
     * The current compass bearing in degrees where 0.0 is true north.
     */
    private FieldValue fvCourse = new FieldValue("Course (deg)");

    /**
     * The time of the last location.
     */
    private FieldValue fvTime = new FieldValue("");
    
    private boolean screenSetup = false;

    public void setupScreen() {
        if (!screenSetup) {
            screenSetup = true;
            deleteAll();
            // Set the menu bar options.
            setMenuText("Back", null);

            // Show the state of the location provider.
            // state.setHorizontalAlignment( Graphics.HCENTER );
            // setStateLabel( model.getLocationProvider().getState() );
            // append( state );

            // Create a UI section for pedometer information.
            // createNewSection("Pedometer");
            // append(traveled);
            // append(avgSpeed);

            // Create a UI section for location information.
            // createNewSection("Location");
            append(fvFix);
            append(latitude);
            append(longitude);
            
            append(new HorizontalRule());

            // append(horizontalAccuracy);
            // append(new Label()); // Blank line
            append(fvAltitude);
            // append(verticalAccuracy);

            // Create a section for movement information.
            // createNewSection("Movement");
            append(fvSpeed);
            append(fvCourse);

            append(new HorizontalRule());
            // Create a section for the time.
            // createNewSection("Time");
            append(fvTime);

            append(new HorizontalRule());
            // createNewSection("Precision");
            append(fvHdop);
            append(NSAT);
        }
        // Register for location updates.
        // LocationProvider provider = model.getLocationProvider();
    }

    /**
     * Adds components for a new section of information.
     * 
     * @param title
     *            is the name of the section.
     */
    private void createNewSection(final String title) {
        append(new HorizontalRule());

        final Label header = new Label();
        header.setFont(UIManager.getTheme().getMenuFont());
        header.setLabel(title);
        append(header);
    }

    public void showNotify() {
        setupScreen();
        c.getAppModel().addListener(this);
        c.setGpsDecode(true);
        super.showNotify();
    }

    public void hideNotify() {
        c.getAppModel().removeListener(this);
        super.hideNotify();
    }

    /**
     * Called when the user presses the "Menu" menu option.
     * 
     * @see org.j4me.ui.DeviceScreen#acceptNotify()
     */
    protected final void acceptNotify() {
        final Menu menu = new Menu("Menu", this);

        menu.show();

        // Continue processing the event.
        super.acceptNotify();
    }

    /**
     * Called when the user presses the "Back" button.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected final void declineNotify() {
        // Go back to the previous screen.
        if (previous != null) {
            previous.show();
        }
    }

    /**
     * Shows a field and its value such as "Speed (m/s): 5.0".
     */
    private static final class FieldValue extends Label {
        private final String name;

        public FieldValue(final String name) {
            this.name = name;
        }

        public final void setLabel(final String label) {
            if (name.length() != 0) {
                super.setLabel(name + ":  " + label);
            } else {
                super.setLabel(label);
            }
        }

        public final void setLabel(final double d, final int i) {
            final String s = JavaLibBridge.toString(d, i);
            setLabel(s);
        }

        public final void setLabel(final float f, final int i) {
            final String s = JavaLibBridge.toString(f, i);
            setLabel(s);
        }

        public final void setLabel(final long l) {
            final String s = (new Date(l)).toString();
            setLabel(s);
        }
    }

    /**
     * Updates the lat and lon color according to the validity of the fix.
     * 
     * @param valid
     */
    private void updateValidColor(final int valid) {
        final int currentColor = latitude.getFontColor();
        int newColor;
        if (valid != 0x0001) {
            newColor = UIManager.getTheme().getFontColor();
        } else {
            newColor = Theme.RED;
        }

        if (currentColor != newColor) {
            latitude.setFontColor(newColor);
            longitude.setFontColor(newColor);
            latitude.repaint();
            longitude.repaint();
        }
    }

    /**
     * Handle the event coming from the GPS model. In this screen we retrieve
     * the position information.
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public final void modelEvent(final ModelEvent e) {
        final int type = e.getType();

        switch (type) {
        case GpsEvent.GPRMC:
        case GpsEvent.GPGGA:
            final GPSRecord g = (GPSRecord) e.getArg();
            switch (type) {
            case GpsEvent.GPRMC:
                // GPRMC string received. Taking GPRMC parameters from GPS
                // record.
                latitude.setLabel(g.latitude, 6);
                longitude.setLabel(g.longitude, 6);
                fvTime.setLabel((g.utc) * 1000L + g.milisecond);
                fvSpeed.setLabel(g.speed, 1);
                fvCourse.setLabel(g.heading, 1);
                updateValidColor(g.valid);
                // Log.info("GPRMC");
                repaint();
                break;
            case GpsEvent.GPGGA:
                // GPGGA string received. Taking GPGGA parameters from GPS
                // record.
                latitude.setLabel(g.latitude, 6);
                longitude.setLabel(g.longitude, 6);
                NSAT.setLabel((g.nsat / 256)
                        + (((g.nsat&0xFF) < 255) ? "" : "(" + (g.nsat & 0xFF) + ")"));
                {
                    String mslStr;
                    mslStr = JavaLibBridge.toString(g.height - g.geoid, 1);
                    mslStr += "(calc: ";
                    mslStr += JavaLibBridge.toString(g.height - g.geoid
                            + ExternalUtils.wgs84Separation(g.latitude, g.longitude),
                            1);
                    mslStr += ")";
                    fvAltitude.setLabel(mslStr);
                }
                fvHdop.setLabel(g.hdop / 100f, 2);
                fvFix.setLabel(gps.log.out.CommonOut.getFixText(g.valid));
                updateValidColor(g.valid);
                repaint();
            default:
                break;
            }

        }
    }
}
