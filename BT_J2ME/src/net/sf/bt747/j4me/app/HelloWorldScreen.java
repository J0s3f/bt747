package net.sf.bt747.j4me.app;
import javax.microedition.lcdui.*;
import org.j4me.ui.*;
import org.j4me.ui.components.*;

/**
 * A screen that displays "Hello World".
 */
public class HelloWorldScreen extends Dialog
{
    /**
     * Constructor.
     */
    public HelloWorldScreen ()
    {
        // The title across the top of the screen.
        //setTitle( "J4ME" );
        
        // Set the menu text at the bottom of the screen.
        //  "Exit" will appear on the left and when the user presses the phone&#x27;s
        //  left menu button declineNotify() will be called.  When the user
        //  presses the right menu button acceptNotify() will be called.
        setMenuText( "Exit", null );
        
        // Add a UI component.
        Label lbl = new Label("Hello World!");
        lbl.setHorizontalAlignment( Graphics.HCENTER );
        append( lbl );
    }
    
    /**
     * Called when the user presses the left menu button to "Exit".
     */
    public void declineNotify ()
    {
        // Exit the application.
        MTKMidlet.exit();
        
        // Continue processing the event.
        super.declineNotify();
    }
}