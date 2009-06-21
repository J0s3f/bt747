package org.j4me.ui.components;

/**
 * Changes by Mario De Weerd added #setForDecimalOnly . Had to change field
 * and constraint initialisation to make DECIMAL work on german platform for
 * example.
 */

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Theme;
import org.j4me.ui.UIManager;

/**
 * A <code>TextBox</code> is a component for entering text, numbers, and
 * keyboard data. It equivalent to the
 * <code>javax.microedition.lcdui.TextField</code> class.
 * <p>
 * This component is not yet complete. Features that still need to be added
 * include:
 * <ul>
 * <li>Suggestions
 * <li>Constraint: EMAILADDR
 * <li>Constraint: URL
 * <li>Constraint: DECIMAL
 * <li>Constraint: UNEDITABLE
 * <li>Constraint: SENSITIVE
 * <li>Constraint: NON_PREDICTIVE
 * <li>Constraint: INITIAL_CAPS_WORD
 * <li>Constraint: INITIAL_CAPS_SENTENCE
 * </ul>
 * 
 * @see javax.microedition.lcdui.TextField
 */
public class TextBox extends Component {
    /**
     * The default number of characters that can be entered into this text box
     * component. The native edit screen (i.e.
     * <code>javax.microedition.lcdui.TextBox</code>) allocates a buffer of
     * this size so it cannot be too big to fit into memory.
     */
    private static final int DEFAULT_MAX_CHARS = 128;

    /**
     * The number of pixels from the left, top, right, and bottom that text
     * within the box is set.
     */
    private static final int TEXT_OFFSET = 2;

    /**
     * The label that appears right above the text box. For example it might
     * say "User Name". If this is <code>null</code> then no label will
     * appear.
     */
    private Label label;

    /**
     * The contents of the text box.
     */
    private String contents;

    /**
     * The maximum number of characters that can be entered. If this is 0
     * there is no limit.
     */
    private int maxSize = TextBox.DEFAULT_MAX_CHARS;

    /**
     * The constraints that define what kind of text is input. This matches
     * the <code>TextField</code> input constraints integer.
     * <p>
     * One of the following types of text are possible:
     * <ul>
     * <li>ANY - (Default) Text, numbers, line breaks, and all data.
     * <li>EMAILADDR - Email address.
     * <li>NUMERIC - An integer.
     * <li>PHONENUMBER - A phone number.
     * <li>URL - A URL.
     * <li>DECIMAL - A floating point number.
     * </ul>
     * <p>
     * Additionally the input can be modified with a combination of any of:
     * <ul>
     * <li>PASSWORD
     * <li>UNEDITABLE
     * <li>SENSITIVE
     * <li>NON_PREDICTIVE
     * <li>INITIAL_CAPS_WORD
     * <li>INITIAL_CAPS_SENTENCE
     * </ul>
     * 
     * @see javax.microedition.lcdui.TextField
     */
    private int constraints = TextField.ANY;

    /**
     * Creates a new <code>TextBox</code> component.
     */
    public TextBox() {
    }

    /**
     * @return The text that appears above the text box. If <code>null</code>
     *         there is no text.
     */
    public String getLabel() {
        if (label == null) {
            return null;
        } else {
            return label.getLabel();
        }
    }

    /**
     * @param label
     *                is the text that appears above the text box. If
     *                <code>null</code> there will be no text.
     */
    public void setLabel(final String label) {
        if (label == null) {
            this.label = null;
        } else {
            if (this.label == null) {
                this.label = new Label();
            }

            this.label.setLabel(label);
        }

        invalidate();
    }

    /**
     * Gets the contents of the <code>TextBox</code> as a string value.
     * 
     * @return The current contents or <code>null</code> if there is no
     *         data.
     * @see #setString(String)
     */
    public String getString() {
        if (contents == null) {
            return "";
        } else {
            return contents;
        }
    }

    /**
     * Sets the contents of the <code>TextBox</code> as a string value,
     * replacing the previous contents.
     * 
     * @param text
     *                is the new value of the <code>TextBox</code>, or
     *                <code>null<code> if
     *  the <code>TextBox</code> is to be made empty.
     * @throws IllegalArgumentException if <code>text</code> is illegal for the
     *  current input constraints. 
     * @throws IllegalArgumentException if <code>text</code> would exceed the current
     *  maximum capacity.
     *  
     * @see #getString()
     */
    public void setString(final String text) {
        contents = text;
        invalidate();
    }

    /**
     * Allows any text to be entered including letters, numbers, punctuation,
     * and newlines.
     */
    public void setForAnyText() {
        setRestrictiveConstraint(TextField.ANY);
    }

    /**
     * Allows only integer values to be input.
     */
    public void setForNumericOnly() {
        setRestrictiveConstraint(TextField.NUMERIC);
    }

    /**
     * Allows only integer values to be input.
     */
    public void setForDecimalOnly() {
        setRestrictiveConstraint(TextField.DECIMAL);
    }

    /**
     * Allows for entering a phone number.
     */
    public void setForPhoneNumber() {
        setRestrictiveConstraint(TextField.PHONENUMBER);
    }

    /**
     * @return <code>true</code> if this text box is for entering a phone
     *         number; <code>false</code> otherwise.
     */
    public boolean isPhoneNumber() {
        final boolean phone = ((constraints & TextField.PHONENUMBER) != 0);
        return phone;
    }

    /**
     * Sets a restrictive constraint on this text box. Restrictive constraints
     * define the type of data that can be entered. For example
     * <code>TextField.ANY</code> or <code>TextField.NUMERIC</code>.
     * 
     * @param restriction
     *                is the <code>TextField</code> restrictive constant.
     */
    private void setRestrictiveConstraint(final int restriction) {
        // Keep the modifiers.
        constraints &= TextField.CONSTRAINT_MASK;

        // Set the restrictive value.
        constraints |= restriction;

        invalidate();
    }

    /**
     * Sets if this text box is for sensitive data or not. Sensitive data is
     * displayed as '<code>*</code>' characters.
     * <p>
     * The default is for non-sensitive data.
     * 
     * @param password
     *                is <code>true</code> if this text box contains a
     *                password; <code>false</code> otherwise.
     */
    public void setPassword(final boolean password) {
        setModifierConstraint(TextField.PASSWORD, password);
    }

    /**
     * Returns if this text box is for sensitive data or not.
     * <p>
     * The default is for non-sensitive data.
     * 
     * @return <code>true</code> if this text box contains a password;
     *         <code>false</code> otherwise.
     */
    public final boolean isPassword() {
        final boolean password = ((constraints & TextField.PASSWORD) != 0);
        return password;
    }

    /**
     * Sets a modifier constraint on this text box. Modifiers alter the type
     * of text defined such as <code>TextField.PASSWORD</code>.
     * 
     * @param restriction
     *                is the <code>TextField</code> modifier constant.
     * @param on
     *                when <code>true</code> adds the modifier to the
     *                constraints and when <code>false</code> removes it.
     */
    private final void setModifierConstraint(final int modifier,
            final boolean on) {
        if (on) {
            constraints |= modifier;
        } else {
            constraints &= ~modifier;
        }

        invalidate();
    }

    /**
     * Returns the maximum size (number of characters) that can be stored in
     * this <code>TextBox</code>.
     * 
     * @return The maximum size in characters.
     * @see #setMaxSize(int)
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the maximum size (number of characters) that can be contained in
     * this <code>TextBox</code>. If the current contents of the
     * <code>TextBox</code> are larger than <code>maxSize</code>, the
     * contents are truncated to fit.
     * 
     * @param maxSize
     *                is the new maximum size. It must be 1 or more.
     */
    public void setMaxSize(final int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException(String.valueOf(maxSize));
        }

        // Truncate if smaller than what exists already.
        if (maxSize < size()) {
            contents = contents.substring(0, maxSize);
            repaint();
        }

        this.maxSize = maxSize;
    }

    /**
     * Gets the number of characters that are currently stored in this
     * <code>TextBox</code>.
     * 
     * @return The number of characters in the <code>TextBox</code>.
     */
    public int size() {
        int size = 0;

        if (contents != null) {
            size = contents.length();
        }

        return size;
    }

    /**
     * Paints the text box.
     * 
     * @param g
     *                is the <code>Graphics</code> object to be used for
     *                rendering the item.
     * @param theme
     *                is the application's theme. Use it to get fonts and
     *                colors.
     * @param width
     *                is the width, in pixels, to paint the component.
     * @param height
     *                is the height, in pixels, to paint the component.
     * @param selected
     *                is <code>true</code> when this components is currently
     *                selected and <code>false</code> when it is not.
     * 
     * @see org.j4me.ui.components.Component#paintComponent(Graphics, Theme,
     *      int, int, boolean)
     */
    protected void paintComponent(final Graphics g, final Theme theme,
            int width, int height, final boolean selected) {
        int y = 0;

        // Paint the label above this component.
        if (label != null) {
            // Make the justification the same as for this component.
            label.setHorizontalAlignment(getHorizontalAlignment());

            // Paint the label.
            label.paint(g, theme, getScreen(), 0, 0, width, height, selected);

            // The top of the progress bar is below the label.
            final int labelHeight = label.getHeight();
            y = labelHeight;
            height -= labelHeight;
        }

        // Paint the text box.
        int offset = Component.paintRect(g, theme, 0, y, width, height,
                selected);

        // Paint the contents of the text box.
        if (contents != null) {
            // Indent the text a bit from the sides of the component's
            // interior.
            offset += TextBox.TEXT_OFFSET;

            // Calculate the layout of the text inside the text box.
            final int left = offset;
            final int top = y + offset;
            width -= 2 * offset;
            height -= 2 * offset;

            final int anchor = Graphics.LEFT | Graphics.TOP;

            g.clipRect(left, top, width, height);
            g.setColor(theme.getFontColor());

            // Determine the text to display inside the text box.
            String display = contents;

            if (isPassword()) {
                final int length = contents.length();
                final StringBuffer builder = new StringBuffer(length);

                for (int i = 0; i < length; i++) {
                    builder.append('*');
                }

                display = builder.toString();
            } else if (isPhoneNumber()) {
                // Modify phone numbers with an area code.
                if (display.length() == 10) {
                    final StringBuffer builder = new StringBuffer(15);

                    builder.append("(");
                    builder.append(display.substring(0, 3));
                    builder.append(") ");

                    builder.append(display.substring(3, 6));
                    builder.append("-");

                    builder.append(display.substring(6, 10));

                    display = builder.toString();
                }
            }

            // Paint the text inside the text box.
            g.drawString(display, left, top, anchor);
        }
    }

    /**
     * Returns the dimensions of the text box.
     * 
     * @see org.j4me.ui.components.Component#getPreferredComponentSize(org.j4me.ui.Theme,
     *      int, int)
     */
    protected int[] getPreferredComponentSize(final Theme theme,
            final int viewportWidth, final int viewportHeight) {
        final int fontHeight = theme.getFont().getHeight();
        int height = fontHeight + 2
                * (Component.HIGHLIGHTED_BORDER_WIDTH + TextBox.TEXT_OFFSET);

        // Add the height of the label above the component.
        if (label != null) {
            final int[] labelDimensions = label.getPreferredComponentSize(
                    theme, viewportWidth, viewportHeight);
            height += labelDimensions[1];
        }

        return new int[] { viewportWidth, height };
    }

    /**
     * An event raised whenever the component is made visible on the screen.
     * This is called before the <code>paintComponent</code> method.
     * 
     * @see Component#showNotify()
     */
    protected void showNotify() {
        // Pass the event to contained components.
        if (label != null) {
            label.visible(true);
        }

        // Continue processing the event.
        super.showNotify();
    }

    /**
     * An event raised whenever the component is removed from the screen.
     * 
     * @see Component#hideNotify()
     */
    protected void hideNotify() {
        // Pass the event to contained components.
        if (label != null) {
            label.visible(false);
        }

        // Continue processing the event.
        super.hideNotify();
    }

    /**
     * @return <code>true</code> because this component accepts user input.
     */
    public boolean acceptsInput() {
        return true;
    }

    /**
     * Called when a key is pressed.
     * 
     * @param keyCode
     *                is the key code of the key that was pressed.
     */
    public void keyPressed(final int keyCode) {
        // Was it an input character?
        // Otherwise it was a menu button of special character.
        if ((keyCode > 0) || (keyCode == DeviceScreen.FIRE)) {
            select();
        }
    }

    /**
     * Called when the pointer is pressed.
     * 
     * @param x
     *                is the horizontal location where the pointer was pressed
     *                relative to the top-left corner of the component.
     * @param y
     *                is the vertical location where the pointer was pressed
     *                relative to the top-left corner of the component.
     */
    public void pointerPressed(final int x, final int y) {
        // If anywhere on the text box is pressed it has been selected.
        select();

        // Stop processing the pointer event.
        // i.e. do not call super.pointerPressed( x, y );
    }

    /**
     * Called when the text box's value is being edited. This method replaces
     * the current screen with the JVM's <code>TextBox</code> screen. When
     * the user enters a new value the original screen returns and is updated
     * with the new value.
     */
    protected void select() {
        // Create a text entry screen using the LCDUI.
        // We use the LCDUI because we can't provide this functionality
        // on all phones by painting ourselves. For instance Motorola
        // phones provide special menus for deleting characters because
        // they do not have a dedicated button.
        //
        // There is a problem with ProGuard 4.0 optimizations. We need
        // to actually pass in the label and contents strings instead of
        // having them collected by the constructor of TextInput. It
        // does not work and throws an exception without doing this to
        // trick the optimizer.
        final DeviceScreen current = UIManager.getScreen();
        final String label = getLabel();
        final String theString = getString();
        final int maxSize = getMaxSize();
        // TextInput entry = new TextInput(current, this, label, contents,
        // maxSize, constraints);
        final TextInput entry = new TextInput(current, this, label, "",
                maxSize, TextField.ANY);

        entry.setConstraints(constraints);
        entry.setString(theString);

        // Display the text entry screen.
        final Display display = UIManager.getDisplay();
        display.setCurrent(entry);

    }

    /**
     * The native implementation for inputting text. This takes over the
     * entire screen and returns when the user is done entering text.
     */
    private final static class TextInput extends
            javax.microedition.lcdui.TextBox implements CommandListener {

        private static boolean commaOrDotKnown = false;
        private static boolean commaOrDot;

        private static boolean isComma() {
            if (!commaOrDotKnown) {
                commaOrDotKnown = true;
                try {
                    if ((new TextField("Test", "2,2", 15, TextField.DECIMAL))
                            .getConstraints() != 0) {
                        commaOrDot = true;
                    }
                } catch (Exception e) {
                    commaOrDot = false;
                }
            }
            return commaOrDot;
        }

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
        private final TextBox component;

        /**
         * Creates a native system input screen for text.
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
         * @param maxSize
         *                is the maximum number of characters that can be
         *                entered.
         * @param constraints
         *                are the options that effect the type of data that
         *                can be entered.
         */
        public TextInput(final DeviceScreen parent, final TextBox box,
                final String label, final String contents, final int maxSize,
                final int constraints) {
            super(label, contents, maxSize, constraints);
            if ((constraints & TextField.DECIMAL) != 0) {
                String input = contents;
                if (isComma()) {
                    input.replace('.', ',');
                } else {
                    input.replace(',', '.');
                }
                this.setString(input);
            }

            // Record the owners.
            this.parent = parent;
            component = box;

            // Add the menu buttons.
            final Theme theme = UIManager.getTheme();

            final String cancelText = theme.getMenuTextForCancel();
            cancel = new Command(cancelText, Command.CANCEL, 1);
            addCommand(cancel);

            final String okText = theme.getMenuTextForOK();
            ok = new Command(okText, Command.OK, 2);
            addCommand(ok);

            setCommandListener(this);
        }

        /**
         * Called when the user hits the OK or Cancel button.
         */
        public void commandAction(final Command c, final Displayable d) {
            if (c == ok) {
                // Update the contents of owning box.
                final String input = this.getString();
                if (getConstraints() == TextField.DECIMAL) {
                    input.replace(',', '.');
                }
                System.err.println("Input:"+input);
                component.setString(input);
                System.err.println("Actual set:"+component.getString());
            }

            // Return to the parent screen.
            parent.show();
            parent.repaint();
        }
    }
}
