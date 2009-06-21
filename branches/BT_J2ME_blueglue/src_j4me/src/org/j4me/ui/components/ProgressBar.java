package org.j4me.ui.components;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Graphics;

import org.j4me.ui.Theme;
import org.j4me.util.MathFunc;

/**
 * Displays a progress bar that indicates the state of a background process.
 * <p>
 * Progress bars come in two flavors. There is the standard progress bar which
 * appears as a rectangle that fills from left-to-right as progress toward
 * some known point occurs. There is also a spinner which is animated to show
 * constant progress and is useful when it is unknown how long the operation
 * will last. These are differentiated by <code>setMaxSize</code>; when 0
 * it is a spinner and when positive it is a progress bar.
 */
public class ProgressBar extends Component {
    /**
     * The millseconds between frames in the spinner animation. The number of
     * frames for one complete spin is 12, one for each hour on the clock.
     */
    private static final int ANIMATION_INTERVAL = 2000 / 12;

    /**
     * The percentage of the screen's width that a progress bar is.
     */
    private double widthPercentage = 0.90; // 90%

    /**
     * The number of times the default font height that the progress bar
     * should be. Progress bars default to 1.2 (120%) and spinners to 2.0
     * (200%).
     */
    private double heightPercentage = 0;

    /**
     * The label that appears right above the progress bar. For example it
     * might say "Downloaded 567 of 10235 bytes". If this is <code>null</code>
     * then no label will appear.
     */
    private Label label;

    /**
     * The amount of the operation represented by this progress bar that has
     * been completed. It should be between 0 and <code>max</code>.
     */
    private int value;

    /**
     * The total amount the operation represented by this progress bar is
     * working toward. When <code>current</code> equals <code>max</code>
     * the progress bar will be full.
     * <p>
     * If this is 0, the component will draw itself as a spinner component
     * because it doesn't know how long the operation will last.
     */
    private int max;

    /**
     * One less than the current hour pointed to by the spinner component. As
     * the spinner rotates, this is the leading edge (i.e. fully colored). It
     * goes between 0-11 so the actual hour is 1 greater.
     */
    private int spinnerHour;

    /**
     * A timer that triggers animation events every
     * <code>ANIMATION_INTERVAL</code> milliseconds when this component is
     * visible.
     */
    private Timer timer;

    /**
     * Creates a progress bar component.
     */
    public ProgressBar() {
        setHorizontalAlignment(Graphics.HCENTER);
    }

    /**
     * @return The text that appears above the progess bar. If
     *         <code>null</code> there is no text.
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
     *                is the text that appears above the progress bar. If
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
     * @return The amount of completed progess.
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the maximum amount of progress. If this is 0, the total progress
     * is unknown and this component will be represented by a spinner
     * animation.
     * 
     * @param value
     *                is the amount of completed progess.
     */
    public void setValue(final int value) {
        if (value < 0) {
            this.value = 0;
        } else if (value > max) {
            this.value = max;
        } else {
            this.value = value;
        }
    }

    /**
     * Returns the maximum amount of progress. If this is 0, the total
     * progress is unknown and this component will be represented by a spinner
     * animation.
     * 
     * @return The maximum amount of progress.
     */
    public int getMaxValue() {
        return max;
    }

    /**
     * @param max
     *                is the maximum amount of progress.
     */
    public void setMaxValue(final int max) {
        // Set the max.
        if (max < 0) {
            this.max = 0;
        } else {
            // Stop the animation timer in case it was running.
            stopTimer();

            this.max = max;
        }

        // Make sure the current value isn't more than the new max.
        if (this.max < value) {
            value = this.max;
        }
    }

    /**
     * Sets how far across the screen the progress bar extends. This has no
     * effect on the size of a spinner.
     * 
     * @param percentageOfScreen
     *                is the length of the progress bar relative to the width
     *                of the screen. It must be between 0.00 and 1.00.
     */
    public void setRelativeWidth(final double percentageOfScreen) {
        if ((percentageOfScreen < 0.0) || (percentageOfScreen > 1.0)) {
            // The percentage must be between 0.00 and 1.00.
            throw new IllegalArgumentException(String
                    .valueOf(percentageOfScreen));
        }

        widthPercentage = percentageOfScreen;
    }

    /**
     * Gets how far across the screen the progress bar extends.
     * 
     * @return The length of the progress bar relative to the width of the
     *         screen.
     */
    public double getRelativeWidth() {
        return widthPercentage;
    }

    /**
     * Sets the height of the progress bar relative to the default font
     * specified in the theme. Setting to 0 uses the default heights.
     * 
     * @param percentageOfFontHeight
     *                is how tall, relative to the default font, the progress
     *                bar is. For example 1.2 is 120% the size.
     */
    public void setRelativeHeight(final double percentageOfFontHeight) {
        if (percentageOfFontHeight < 0) {
            throw new IllegalArgumentException(String
                    .valueOf(percentageOfFontHeight));
        }

        heightPercentage = percentageOfFontHeight;
        invalidate();
    }

    /**
     * Returns the height of the progress bar relative to the height of the
     * default font specified in the theme.
     * 
     * @return The height of the progress bar as a percentage of the default
     *         font's height. For example 1.2 is 120% the size.
     */
    public double getRelativeHeight() {
        if (heightPercentage == 0) {
            // Default heights.
            if (max == 0) // spinner
            {
                return 2.0;
            } else // progress bar
            {
                return 1.2;
            }
        } else // user specified height
        {
            return heightPercentage;
        }
    }

    /**
     * Paints the progress bar.
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
            final int width, int height, final boolean selected) {
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

        if (max == 0) {
            startTimer();
            paintSpinner(g, theme, 0, y, width, height, selected);
        } else {
            paintBar(g, theme, 0, y, width, height, selected);
        }
    }

    /**
     * Paints a progress bar that shows an operation with a known duration.
     * The bar fills in left-to-right as progress occurs. The portion on the
     * left indicating completed progress will take up
     * <code>getCurrent / getMax</code> percent of the progress bar.
     * 
     * @param g
     *                is the <code>Graphics</code> object to be used for
     *                rendering the item.
     * @param theme
     *                is the application's theme. Use it to get fonts and
     *                colors.
     * @param x
     *                is the left side of the component.
     * @param y
     *                is the top side of the component.
     * @param width
     *                is the width, in pixels, to paint the component.
     * @param height
     *                is the height, in pixels, to paint the component.
     * @param selected
     *                is <code>true</code> when this components is currently
     *                selected and <code>false</code> when it is not.
     */
    protected void paintBar(final Graphics g, final Theme theme, int x,
            final int y, int width, final int height, final boolean selected) {
        // Get the location of the progress bar.
        final int barWidth = (int) (width * getRelativeWidth());

        final int horizontalAlignment = getHorizontalAlignment();

        if (horizontalAlignment == Graphics.HCENTER) {
            x += (width - barWidth) / 2;
        } else if (horizontalAlignment == Graphics.RIGHT) {
            x += (width - barWidth);
        }

        width = barWidth;

        // Calculate the completed progress.
        final double percentageCompleted = (double) value / (double) max;
        final int completedWidth = (int) MathFunc.round(width
                * percentageCompleted);

        // Paint the completed portion of the progress bar.
        final int complete = theme.getBorderColor();
        final int highlight = theme.getHighlightColor();

        Theme.gradientFill(g, x, y, completedWidth, height, true, complete,
                highlight, 0.50);

        // Paint the uncompleted portion of the progress bar.
        final int incomplete = theme.getBackgroundColor();
        g.setColor(incomplete);

        g.fillRect(x + completedWidth, y, x + width - completedWidth, height);

        // Paint a border around the progress bar.
        final int border = theme.getBorderColor();
        g.setColor(border);

        g.drawRect(x, y, width - 1, height - 1);
    }

    /**
     * Paints a spinner component used to show progress during an operation of
     * an unknown duration.
     * 
     * @param g
     *                is the <code>Graphics</code> object to be used for
     *                rendering the item.
     * @param theme
     *                is the application's theme. Use it to get fonts and
     *                colors.
     * @param x
     *                is the left side of the component.
     * @param y
     *                is the top side of the component.
     * @param width
     *                is the width, in pixels, to paint the component.
     * @param height
     *                is the height, in pixels, to paint the component.
     * @param selected
     *                is <code>true</code> when this components is currently
     *                selected and <code>false</code> when it is not.
     */
    protected void paintSpinner(final Graphics g, final Theme theme,
            final int x, final int y, final int width, final int height,
            final boolean selected) {
        // The component is square so get the size of a side.
        final int side = Math.min(width, height);

        // Get the diameter of a circle that makes up the spinner.
        final int diameter = side / 6;
        final int radius = diameter / 2;

        // The centers of all the circles are on the hour hands of a clock.
        final double toCenter = side / 3;
        final int temp = (side / 2) - radius;
        int left = x + temp;
        final int top = y + temp;

        // Adjust for the horizontal alignment.
        final int horizontalAlignment = getHorizontalAlignment();

        if (horizontalAlignment == Graphics.HCENTER) {
            left += (width - side) / 2;
        } else if (horizontalAlignment == Graphics.RIGHT) {
            left += (width - side);
        }

        // The color of this circle is between the background and primary
        // component color. (spinnerHour + 1) is at full color while one
        // more is at the background color. This gives the illusion of
        // it moving clockwise.
        final int background = theme.getBackgroundColor();
        final int foreground = theme.getBorderColor();

        final int redStart = (background & 0x00FF0000) >> 16;
        final int greenStart = (background & 0x0000FF00) >> 8;
        final int blueStart = (background & 0x000000FF);

        int redDelta = (foreground & 0x00FF0000) >> 16;
        int greenDelta = (foreground & 0x0000FF00) >> 8;
        int blueDelta = (foreground & 0x000000FF);

        redDelta = (redDelta - redStart) / 12;
        greenDelta = (greenDelta - greenStart) / 12;
        blueDelta = (blueDelta - blueStart) / 12;

        // Draw each circle.
        for (int hour = 1; hour <= 12; hour++) {
            // Each hour hand on a clock is spaced 30 degrees apart (360
            // degrees / 12 hours).
            // They fall onto a 30-60-90 degree triangle.

            // Calculate the center of this circle.
            final int angle = ((hour - 3) * -30 + 360) % 360; // 3 o'clock =
                                                                // 0 degrees,
                                                                // 12 = 90
                                                                // degrees
            final double radians = Math.toRadians(angle);
            final int cx = (int) MathFunc.round(Math.cos(radians) * toCenter);
            final int cy = (int) MathFunc.round(Math.sin(radians) * toCenter)
                    * -1; // Y-coordinates flipped on screen

            // Draw a circle for this spoke on the spinner.
            final int offset = ((hour - spinnerHour) + 12) % 12;
            final int red = redStart + (offset * redDelta);
            final int green = greenStart + (offset * greenDelta);
            final int blue = blueStart + (offset * blueDelta);
            g.setColor(red, green, blue);

            g.fillRoundRect(left + cx, top + cy, diameter, diameter,
                    diameter, diameter);
        }
    }

    /**
     * Returns the dimensions of the progress bar.
     * 
     * @see org.j4me.ui.components.Component#getPreferredComponentSize(org.j4me.ui.Theme,
     *      int, int)
     */
    protected int[] getPreferredComponentSize(final Theme theme,
            final int viewportWidth, final int viewportHeight) {
        final int width = (int) (viewportWidth * getRelativeWidth());

        final int fontHeight = theme.getFont().getHeight();
        int height = (int) (fontHeight * getRelativeHeight());

        if (max == 0) {
            // Make sure height of spinners is divisible by 6.
            height += (height % 6);
        }

        // Add the height of the label above the component.
        if (label != null) {
            final int[] labelDimensions = label.getPreferredComponentSize(
                    theme, viewportWidth, viewportHeight);
            height += labelDimensions[1];
        }

        return new int[] { width, height };
    }

    /**
     * Starts the animation timer for this spinner.
     */
    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new SpinnerTask(), 0,
                    ProgressBar.ANIMATION_INTERVAL);
        }
    }

    /**
     * Stops the animation timer for this spinner.
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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

        // If this is a spinner, stop it.
        stopTimer();

        // Continue processing the event.
        super.hideNotify();
    }

    /**
     * A task executed at regularly scheduled intervals that forces the
     * spinner animation to advance to the next frame.
     */
    private final class SpinnerTask extends TimerTask {
        public void run() {
            // Increment the position of the lead spoke.
            spinnerHour = (spinnerHour + 1) % 12;

            // Repaint the spinner.
            repaint();
        }
    }
}
