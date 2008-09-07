package net.sf.bt747.j4me.app;

import gps.log.GPSRecord;

import java.util.Date;

import javax.microedition.lcdui.Font;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Menu;
import org.j4me.ui.components.HorizontalRule;
import org.j4me.ui.components.Label;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;

public class GpsPositionScreen extends Dialog implements ModelListener {

    /**
     * The number of yards in a meter.
     */
    private static final float YARDS_PER_METER = 1.09361329833771f;

    /**
     * How many seconds between getting new location information.
     */
    private static final int INTERVAL = 5;

    /**
     * How many seconds to wait for new location information before giving up.
     */
    private static final int TIMEOUT = -1; // Default

    /**
     * The maximum number of seconds ago a location can be for it to be valid.
     * We will never get locations older than this.
     */
    private static final int MAX_AGE = -1; // Default

    /**
     * A large font used for section headings.
     */
    private static final Font LARGE_FONT = Font.getFont(Font.FACE_SYSTEM,
            Font.STYLE_BOLD, Font.SIZE_LARGE);

    /**
     * The normal font used for data.
     */
    private static final Font NORMAL_FONT = Font.getFont(Font.FACE_SYSTEM,
            Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

    /**
     * The total distance traveled in meters.
     */
    private FieldValue traveled = new FieldValue("Traveled (ft)");

    /**
     * The average speed traveled in meters per second.
     */
    private FieldValue avgSpeed = new FieldValue("Avg speed (MPH)");

    /**
     * The current latitude.
     */
    private FieldValue latitude = new FieldValue("Latitude");

    /**
     * The current longitude.
     */
    private FieldValue longitude = new FieldValue("Longitude");

    /**
     * The current accuracy of the latitude and longitude in meters.
     */
    private FieldValue horizontalAccuracy = new FieldValue(
            "Horizontal accuracy (ft)");

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

    /**
     * The total distance traveled in meters.
     */
    private float totalDistance;

    /**
     * The time when the first distance was recorded. Dividing this into
     * <code>totalDistance</code> gives the average speed.
     */
    private long startTime;

    private DeviceScreen previous;

    private AppController c;

    public GpsPositionScreen(final AppController c, final DeviceScreen previous) {


        this.previous = previous;
        this.c = c;
        
        // Set the menu bar options.
        setMenuText("Back", null);

        // Show the state of the location provider.
        // state.setHorizontalAlignment( Graphics.HCENTER );
        // setStateLabel( model.getLocationProvider().getState() );
        // append( state );

        // Create a UI section for pedometer information.
        //createNewSection("Pedometer");
        //append(traveled);
        //append(avgSpeed);

        // Create a UI section for location information.
        createNewSection("Location");
        append(latitude);
        append(longitude);
        //append(horizontalAccuracy);
        append(new Label()); // Blank line
        append(fvAltitude);
        //append(verticalAccuracy);

        // Create a section for movement information.
        createNewSection("Movement");
        append(fvSpeed);
        append(fvCourse);

        // Create a section for the time.
        createNewSection("Time");
        append(fvTime);

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

        Label header = new Label();
        header.setFont(LARGE_FONT);
        header.setLabel(title);
        append(header);
    }
    
    public void showNotify() {
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
        Menu menu = new Menu("Menu", this);

        // Choose different location provider criteria.
        // menu.appendMenuOption( new CriteriaSelectionScreen(model) );

        // Reset the current location provider.
        // menu.appendMenuOption( new MenuItem()
        // {
        // public String getText ()
        // {
        // return "Reset Location Provider";
        // }
        //
        // public void onSelection ()
        // {
        // // Reset the location provider.
        // try
        // {
        // model.getLocationProvider().reset();
        // }
        // catch (IOException e)
        // {
        // Log.warn("Could not reset the location provider", e);
        // }
        //                    
        // show();
        // }
        // } );

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

            setFont(NORMAL_FONT);
        }

        public void setLabel(final String label) {
            super.setLabel(name + ":  " + label);
        }

        public void setLabel(final double d, final int i) {
            String s = Convert.toString(d, i);
            setLabel(s);
        }

        public void setLabel(final float f, final int i) {
            String s = Convert.toString(f, i);
            setLabel(s);
        }

        public void setLabel(final long l) {
            Date d = new Date(l);
            String s = d.toString();
            setLabel(s);
        }
    }


    public final void modelEvent(final ModelEvent e) {
        GPSRecord g;
        switch (e.getType()) {
        case ModelEvent.GPRMC:
            g = (GPSRecord) e.getArg();
            latitude.setLabel(g.latitude, 6);
            longitude.setLabel(g.longitude, 6);
            fvTime.setLabel(((long)g.utc)*1000L);
            fvSpeed.setLabel(g.speed,1);
            fvCourse.setLabel(g.heading,1);
            //Log.info("GPRMC");
            repaint();
            break;
        case ModelEvent.GPGGA:
            g = (GPSRecord) e.getArg();
            latitude.setLabel(g.latitude, 6);
            longitude.setLabel(g.longitude, 6);
            fvAltitude.setLabel(g.height,1);
            repaint();
        default:
            break;
        }

    }

}
