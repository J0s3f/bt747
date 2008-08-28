package net.sf.bt747.j4me.app;

import gps.log.GPSRecord;

import java.io.IOException;
import java.util.Date;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.j4me.bluetoothgps.LocationProvider;
import org.j4me.examples.bluetoothgps.CriteriaSelectionScreen;
import org.j4me.examples.log.LogScreen;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;
import org.j4me.ui.components.HorizontalRule;
import org.j4me.ui.components.Label;

import bt747.model.Controller;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

public class GPSInfo    extends Dialog
implements ModelListener
{
    
    /**
     * The number of yards in a meter.
     */
    private static final float YARDS_PER_METER = 1.09361329833771f;

    /**
     * How many seconds between getting new location information.
     */
    private static final int INTERVAL = 5;
    
    /**
     * How many seconds to wait for new location information before
     * giving up.
     */
    private static final int TIMEOUT = -1;  // Default
    
    /**
     * The maximum number of seconds ago a location can be for it to
     * be valid.  We will never get locations older than this.
     */
    private static final int MAX_AGE = -1;  // Default

    /**
     * A large font used for section headings.
     */
    private static final Font LARGE_FONT = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE );
    
    /**
     * The normal font used for data.
     */
    private static final Font NORMAL_FONT = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM );

    /**
     * The total distance traveled in meters.
     */
    private FieldValue traveled = new FieldValue( "Traveled (ft)" );
    
    /**
     * The average speed traveled in meters per second.
     */
    private FieldValue avgSpeed = new FieldValue( "Avg speed (MPH)" );
    
    /**
     * The current latitude.
     */
    private FieldValue latitude = new FieldValue( "Latitude" );
    
    /**
     * The current longitude.
     */
    private FieldValue longitude = new FieldValue( "Longitude" );
    
    /**
     * The current accuracy of the latitude and longitude in meters.
     */
    private FieldValue horizontalAccuracy = new FieldValue( "Horizontal accuracy (ft)" );
    
    /**
     * The current altitude in meters.
     */
    private FieldValue altitude = new FieldValue( "Altitude (ft)" );
    
    /**
     * The current accuracy of the altitude in meters.
     */
    private FieldValue verticalAccuracy = new FieldValue( "Vertical accuracy (ft)" );
    
    /**
     * The current speed in meters per second.
     */
    private FieldValue speed = new FieldValue( "Speed (MPH)" );
    
    /**
     * The current compass bearing in degrees where 0.0 is true north.
     */
    private FieldValue course = new FieldValue( "Course (deg)" );
    
    /**
     * The time of the last location.
     */
    private FieldValue time = new FieldValue( "Timestamp" );
    
    /**
     * The total distance traveled in meters.
     */
    private float totalDistance;
    
    /**
     * The time when the first distance was recorded.  Dividing this into
     * <code>totalDistance</code> gives the average speed.
     */
    private long startTime;

    DeviceScreen previous;
    
    Controller c;
    
    public GPSInfo(Controller c, DeviceScreen previous) {
        
        this.previous = previous;
        
        // Set the menu bar options.
        setMenuText( "Back", "Menu" );

        // Show the state of the location provider.
        //state.setHorizontalAlignment( Graphics.HCENTER );
        //setStateLabel( model.getLocationProvider().getState() );
        //append( state );
        
        // Create a UI section for pedometer information.
        createNewSection( "Pedometer" );
        append( traveled );
        append( avgSpeed );
        
        // Create a UI section for location information.
        createNewSection( "Location" );
        append( latitude );
        append( longitude );
        append( horizontalAccuracy );
        append( new Label() );  // Blank line
        append( altitude );
        append( verticalAccuracy );
        
        // Create a section for movement information.
        createNewSection( "Movement" );
        append( speed );
        append( course );
        
        // Create a section for the time.
        createNewSection( "Time" );
        append( time );
        
        // Register for location updates.
        // LocationProvider provider = model.getLocationProvider();
        c.getModel().addListener(this);

    }
    
    /**
     * Adds components for a new section of information.
     * 
     * @param title is the name of the section.
     */
    private void createNewSection (String title)
    {
        append( new HorizontalRule() );
        
        Label header = new Label();
        header.setFont( LARGE_FONT );
        header.setLabel( title );
        append( header );
    }
    
    /**
     * Called when the user presses the "Menu" menu option.
     * 
     * @see org.j4me.ui.DeviceScreen#acceptNotify()
     */
    protected void acceptNotify ()
    {
        Menu menu = new Menu( "Menu", this );
        
        // Choose different location provider criteria.
        //menu.appendMenuOption( new CriteriaSelectionScreen(model) );
        
        // Reset the current location provider.
//        menu.appendMenuOption( new MenuItem()
//            {
//                public String getText ()
//                {
//                    return "Reset Location Provider";
//                }
//
//                public void onSelection ()
//                {
//                    // Reset the location provider.
//                    try
//                    {
//                        model.getLocationProvider().reset();
//                    }
//                    catch (IOException e)
//                    {
//                        Log.warn("Could not reset the location provider", e);
//                    }
//                    
//                    show();
//                }
//            } );
        
        // See the application's log.
        menu.appendMenuOption( new LogScreen(this) );
        
        menu.show();
        
        // Continue processing the event.
        super.acceptNotify();
    }
    
    /**
     * Called when the user presses the "Back" button.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected void declineNotify ()
    {
        // Go back to the previous screen.
        if ( previous != null )
        {
            previous.show();
        }
    }


    /**
     * Shows a field and its value such as "Speed (m/s):  5.0".
     */
    private static final class FieldValue
        extends Label
    {
        private final String name;
        
        public FieldValue (String name)
        {
            this.name = name;
            
            setFont( NORMAL_FONT );
        }
        
        public void setLabel (String label)
        {
            super.setLabel( name + ":  " + label );
        }
        
        public void setLabel (double d)
        {
            String s = Double.toString( d );
            setLabel( s );
        }
        
        public void setLabel (float f)
        {
            String s = Float.toString( f );
            setLabel( s );
        }
        
        public void setLabel (long l)
        {
            Date d = new Date( l );
            String s = d.toString();
            setLabel( s );
        }
    }


    public void modelEvent(ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.GPRMC:
            GPSRecord g = (GPSRecord) e.getArg();
            
            latitude.setLabel( g.latitude );
            longitude.setLabel( g.longitude );
            repaint();
            break;
        }
        
    }

}
