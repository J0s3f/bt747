package net.sf.bt747.j4me.app.log;

import org.j4me.logging.Level;
import org.j4me.logging.LogMessage;
import org.j4me.ui.Theme;
import org.j4me.ui.components.Label;

/**
 * A UI component that displays a single log message.
 */
public class LogStatement
	extends Label
{
	/**
	 * Constructs a <c>LogStatement</c> component.
	 * 
	 * @param log is the <c>LogMessage</c> to display.
	 */
	public LogStatement (LogMessage log)
	{
		// Set the text.
		setLabel( log.toString() );
		
		// Change the font color used to render the message.
		int color;

		if ( log.level.equals( Level.DEBUG ) )
		{
			color = Theme.GRAY;
		}
		else if ( log.level.equals( Level.WARN ) )
		{
			color = Theme.ORANGE;
		}
		else if ( log.level.equals( Level.ERROR ) )
		{
			color = Theme.RED;
		}
		else  // INFO
		{
			color = Theme.BLACK;
		}
		
		setFontColor( color );
	}
}
