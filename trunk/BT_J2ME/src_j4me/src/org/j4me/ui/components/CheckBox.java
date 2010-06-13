package org.j4me.ui.components;

import javax.microedition.lcdui.Graphics;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Theme;

/**
 * The <code>CheckBox</code> component lets a user turn an option on or off.
 * Multiple check box components can be placed sequentially to create a list
 * of choices where zero or more can be chosen.
 */
public class CheckBox extends Component {
    /**
     * The ratio of the side of the checkbox square to the height of the
     * current font.
     */
    private static final double CHECKBOX_TO_FONT_RATIO = 0.70; // 70%

    /**
     * The text that appears to the right of the check box.
     */
    private Label label = new Label();

    /**
     * If the item is checked this will be <code>true</code>. If the item
     * is not this will be <code>false</code>.
     */
    private boolean checked;

    /**
     * Constructs a <code>CheckBox</code> component.
     */
    public CheckBox() {
    }

    /**
     * Gets the value of the text that appears to the right of the check box.
     * 
     * @return The text that appears next to the check box. It will never be
     *         <code>null</code> but may be the empty string "".
     */
    public String getLabel() {
        String l = label.getLabel();

        if (l == null) {
            l = "";
        }

        return l;
    }

    /**
     * Sets the text that appears next to the check box.
     * 
     * @param value
     *                is the text that appears to the right of the check box.
     *                A <code>null</code> is treated as the empty string "".
     */
    public void setLabel(final String value) {
        label.setLabel(value);

        // The label may change the size requirements of this component.
        invalidate();
    }

    /**
     * Returns <code>true</code> if the box is checked or <code>false</code>
     * if it is not.
     * 
     * @return <code>true</code> if the box is checked or <code>false</code>
     *         if it is not.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Checks or unchecks the box.
     * 
     * @param checked
     *                when <code>true</code> checks the box; when
     *                <code>false</code> unchecks it.
     */
    public void setChecked(final boolean checked) {
        this.checked = checked;
    }

    /**
     * An event raised whenever the component is made visible on the screen.
     * This is called before the <code>paintComponent</code> method.
     */
    protected void showNotify() {
        label.visible(true);
        super.showNotify();
    }

    /**
     * An event raised whenever the component is removed from the screen.
     */
    protected void hideNotify() {
        label.visible(false);
        super.hideNotify();
    }

    /**
     * Paints the check box component.
     * 
     * @see org.j4me.ui.components.Component#paintComponent(javax.microedition.lcdui.Graphics,
     *      org.j4me.ui.Theme, int, int, boolean)
     */
    protected void paintComponent(final Graphics g, final Theme theme,
            final int width, final int height, final boolean selected) {
        // Paint the check box.
        final int checkboxSide = checkboxSideSize(theme);
        final int y = (height - checkboxSide) / 2;

        final int offset = Component.paintRect(g, theme, 0, y, checkboxSide,
                checkboxSide, selected);

        // Paint the check in the box.
        if (checked) {
            final int checkColor = theme.getHighlightColor();
            g.setColor(checkColor);

            final int checkOffset = offset + 1;
            final int fillSide = checkboxSide - 2 * checkOffset;

            g.fillRect(checkOffset, y + checkOffset, fillSide, fillSide);
        }

        // Paint the text to the side of the box.
        final int lx = (int) (checkboxSide * 1.5);
        final int lw = width - lx;

        final int[] lDimensions = label.getPreferredSize(theme, lw, height);
        final int lh = lDimensions[1];
        final int ly = (height - lh) / 2;

        label.paint(g, theme, getScreen(), lx, ly, lw, lh, selected);
    }

    /**
     * Returns the size of the checkbox sides. It is a square so both sides
     * have the same length.
     * 
     * @param theme
     *                is the current application theme.
     * @return The number of pixels each side of the checkbox is.
     */
    private int checkboxSideSize(final Theme theme) {
        final int fontHeight = theme.getFont().getHeight();
        final int checkboxSide = (int) (fontHeight * CheckBox.CHECKBOX_TO_FONT_RATIO);
        return Math.max(8,checkboxSide);
    }

    /**
     * Returns the dimensions of the check box.
     * 
     * @see org.j4me.ui.components.Component#getPreferredComponentSize(org.j4me.ui.Theme,
     *      int, int)
     */
    protected int[] getPreferredComponentSize(final Theme theme,
            final int viewportWidth, final int viewportHeight) {
        // Get the height of the checkbox.
        final int checkboxSide = checkboxSideSize(theme);

        // Get the height of the label to the right of the checkbox.
        final int[] labelDimensions = label.getPreferredSize(theme,
                viewportWidth - checkboxSide, viewportHeight);

        // Return the bigger of the two heights.
        int height = Math.max(checkboxSide, labelDimensions[1]);

        // Make sure it is an even height. Makes the checkbox look better.
        height += height % 2;

        return new int[] { viewportWidth, height };
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
            // Flip the box.
            checked = !checked;
            repaint();
        }

        // Continue processing the key event.
        super.keyPressed(keyCode);
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
        // Flip the box.
        checked = !checked;
        repaint();

        // Continue processing the pointer event.
        super.pointerPressed(x, y);
    }
}
