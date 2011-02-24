package org.j4me.ui.components;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Theme;
import org.j4me.ui.UIManager;

/**
 * @author Marcin Zduniak < marcin@zduniak.com >
 */
public class DateFieldTextBox extends TextBox {

    private Date date;
    
    public void setDate(final Date date) {
        this.date = date;
        if (date != null)
            setString(DateFieldTextBox.date2Str(date));
        else
            setString(null);
    }
    
    public Date getDate() {
        return this.date;
    }

    private static String date2Str(final Date date) {
        final StringBuffer sb = new StringBuffer();

        final Calendar c = Calendar.getInstance();
        c.setTime(date);

        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH) + 1;
        final int day = c.get(Calendar.DAY_OF_MONTH);

        sb.append(year).append('-').append(month).append('-').append(day);
        final String dateStr = sb.toString();

        return dateStr;
    }

    protected void select() {
        final DeviceScreen current = UIManager.getScreen();
        final String label = getLabel();
        final String contents = getString();

        final DateInput entry = new DateInput(current, this, label, contents);

        final Display display = UIManager.getDisplay();
        display.setCurrent(entry);
    }

    /**
     * The native implementation for inputting date. This takes over the
     * entire screen and returns when the user is done entering date.
     */
    private final static class DateInput extends javax.microedition.lcdui.Form
            implements CommandListener {
        /**
         * The Cancel button.
         */
        private final Command cancel;

        /**
         * The OK button.
         */
        private final Command ok;

        /**
         * The screen that invoked this one.
         */
        private final DeviceScreen parent;

        /**
         * The component this screen is collecting data for.
         */
        private final DateFieldTextBox component;

        private final DateField dateField;

        /**
         * Creates a native system input screen for date.
         * 
         * @param parent
         *                is the screen that invoked this one.
         * @param box
         *                is the component on <code>parent</code> this input
         *                is for.
         * @param label
         *                is the title for the input.
         * @param contents
         *                is the initial value of the contents.
         */
        public DateInput(final DeviceScreen parent,
                final DateFieldTextBox dateBox, final String label,
                final String contents) {
            super(label);

            // Record the owners.
            this.parent = parent;
            component = dateBox;

            // Add the menu buttons.
            final Theme theme = UIManager.getTheme();
            final String cancelText = theme.getMenuTextForCancel();
            final String okText = theme.getMenuTextForOK();

            if (isBlackBerry()) {
                // If we add a second button to the BlackBerry the return key
                // will always cancel the input. This has proven a confusing
                // user
                // experience. Most people think they should type something,
                // hit
                // the return key, and have it appear in the text box.
                cancel = null;

                ok = new Command(okText, Command.OK, 1);
                addCommand(ok);
            } else {
                cancel = new Command(cancelText, Command.CANCEL, 1);
                addCommand(cancel);

                ok = new Command(okText, Command.OK, 2);
                addCommand(ok);
            }

            dateField = new DateField(label, DateField.DATE);
            dateField.setDate(dateBox.date);
            append(dateField);

            setCommandListener(this);
        }

        /**
         * Called when the user hits the OK or Cancel button.
         */
        public void commandAction(final Command c, final Displayable d) {
            if (c == ok) {
                // Update the contents of owning date box.
                final Date date = dateField.getDate();
                component.setDate(date);
            }

            // Return to the parent screen.
            parent.show();
            parent.repaint();
        }

        /**
         * @return <code>true</code> if this is a BlackBerry or
         *         <code>false</code> if not.
         */
        public boolean isBlackBerry() {
            try {
                Class.forName("net.rim.device.api.ui.UiApplication");
                return true;
            } catch (final Throwable e) // ClassNotFoundException,
            // NoClassDefFoundError
            {
                return false;
            }
        }
    }
}
