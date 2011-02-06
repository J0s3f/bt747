package org.j4me.ui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.j4me.util.MathFunc;

/**
 * Derive this class to set the application's theme. A theme controls the
 * color scheme and background graphics used to skin the application.
 * <p>
 * Themes do not alter the shapes or functionality of components. To change
 * the behavior of appearance of any component it is up to you to derive a new
 * component type and implement the changes.
 */
public class Theme {
    // Colors.
    // There are many online lists of colors. One is available at
    // http://www.december.com/html/spec/colordec.html
    /** Black */
    public static final int BLACK = 0x00000000;
    /** Brown */
    public static final int BROWN = 0x00804000;
    /** Blue */
    public static final int BLUE = 0x000000FF;
    /** Navy blue */
    public static final int NAVY = 0x00000080;
    /** Neon blue */
    public static final int NEON_BLUE = 0x004DFFFF;
    /** Light blue */
    public static final int LIGHT_BLUE = 0x000080FF;
    /** Red */
    public static final int RED = 0x00FF0000;
    /** Maroon */
    public static final int MAROON = 0x00802020;
    /** Light red */
    public static final int LIGHT_RED = 0x00FF5050;
    /** Magenta */
    public static final int MAGENTA = 0x00FF28FF;
    /** Orange */
    public static final int ORANGE = 0x00FF8000;
    /** Burnt orange */
    public static final int BURNT_ORANGE = 0x00D07000;
    /** Yellow */
    public static final int YELLOW = 0x00FFFF00;
    /** Green */
    public static final int GREEN = 0x0066FF33;
    /** Medium green */
    public static final int MEDIUM_GREEN = 0x0000A700;
    /** Light green */
    public static final int LIGHT_GREEN = 0x0030C020;
    /** Dark green */
    public static final int DARK_GREEN = 0x001F432D;
    /** Forest green */
    public static final int FOREST_GREEN = 0x0033773A;
    /** Blue-green */
    public static final int BLUE_GREEN = 0x00008080;
    /** Cyan */
    public static final int CYAN = 0x0033FFFF;
    /** Silver */
    public static final int SILVER = 0x00C0C0C0;
    /** Gray */
    public static final int GRAY = 0x00808080;
    /** Light gray */
    public static final int LIGHT_GRAY = 0x00C0C0C0;
    /** Lavendar */
    public static final int LAVENDAR = 0x00E6E6FA;
    /** White */
    public static final int WHITE = 0x00FFFFFF;

    /**
     * The percentage of the way down the title bar that the secondary color
     * used in the gradient fill is at its max.
     */
    private static final double TITLE_BAR_SECONDARY_COLOR_MAX = 0.60; // 60%

    /**
     * The percentage of the way down the menu bar that the secondary color
     * used in the gradient fill is at its max.
     */
    private static final double MENU_BAR_SECONDARY_COLOR_MAX = 0.10; // 10%

    /**
     * The percentage of the way across the scrollbar that the secondary color
     * used in the gradient fill is at its max.
     */
    private static final double SCROLLBAR_SECONDARY_COLOR_MAX = 0.80; // 80%

    /**
     * The width of the scrollbar in pixels.
     */
    private static final int SCROLLBAR_WIDTH = 6;

    /**
     * The font used for writing text in the normal canvas area. It is the
     * default font used by components.
     */
    private final Font defaultFont;

    /**
     * The font used to write menu bar options.
     */
    private final Font menuFont;

    /**
     * The font used in the title area.
     */
    private final Font titleFont;

    /**
     * Creates a <code>Theme</code> object. After creating the theme it must
     * be attached to the UI manager to use it through the
     * <code>setTheme</code> method.
     */
    public Theme() {
        // Get the application's fonts.
        final Font base = Font.getDefaultFont();
        final int face = base.getFace();
        final int size = base.getSize();

        defaultFont = Font.getFont(face, Font.STYLE_PLAIN, size);
        menuFont = Font.getFont(face, Font.STYLE_PLAIN, size);
        titleFont = Font.getFont(face, Font.STYLE_BOLD, size);
    }

    /**
     * Gets the basic default font. This font is used for writing menu
     * options, labels, and regular text throughout the UI.
     * 
     * @return The font used for normal text within the UI.
     * @see #getFontColor()
     */
    public Font getFont() {
        return defaultFont;
    }

    /**
     * Returns the font used in the menu bar at the bottom of the canvas.
     * <p>
     * By default this font is the system font. Override this font to change
     * the style of the title text.
     * 
     * @return The font used for writing menu text.
     * 
     * @see #getMenuFontColor()
     * @see #paintMenuBar(Graphics, String, boolean, String, boolean, int,
     *      int)
     */
    public Font getMenuFont() {
        return menuFont;
    }

    /**
     * Returns the font used for writing the title in the screen's title area.
     * <p>
     * By default this font is the system font in bold. Override this font to
     * change the style of the title text.
     * 
     * @return The font used for writing the title in the title area at the
     *         top of the screen.
     * @see #getTitleFontColor()
     * @see #paintTrackbar(Graphics, int, int, int, int)
     */
    public Font getTitleFont() {
        return titleFont;
    }

    /**
     * The color of the text written with the font returned by
     * <code>getFont</code>. Colors are defined as 0xAARRGGBB; the
     * first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of text written with the font from
     *         <code>getFont</code>.
     * @see #getFontColor()
     */
    public int getFontColor() {
        return Theme.NAVY;
    }

    /**
     * The color of the text written with the font returned by
     * <code>getMenuFont</code>. Colors are defined as 0xAARRGGBB; the
     * first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of text written with the font from
     *         <code>getMenuFont</code>.
     * @see #getMenuFont()
     * @see #paintMenuBar(Graphics, String, boolean, String, boolean, int,
     *      int)
     */
    public int getMenuFontColor() {
        return Theme.WHITE;
    }

    /**
     * The color of the menu text when the menu button is pressed. Normally it
     * will be the color returned by <code>getMenuFontColor</code>. Colors
     * are defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of the menu text when its menu button is pressed.
     * @see #getMenuFont()
     * @see #getMenuFontColor()
     * @see #paintMenuBar(Graphics, String, boolean, String, boolean, int,
     *      int)
     */
    public int getMenuFontHighlightColor() {
        return Theme.SILVER;
    }

    /**
     * The color of the text written with the font returned by
     * <code>getTitleFont</code>. Colors are defined as 0xAARRGGBB; the
     * first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of text written with the font from
     *         <code>getTitleFont</code>.
     * @see #getTitleFont()
     * @see #paintTrackbar(Graphics, int, int, int, int)
     */
    public int getTitleFontColor() {
        return getMenuFontColor();
    }

    /**
     * Returns the color used for borders in the canvas section of the UI. An
     * example of a border is the outline around a text box. Colors are
     * defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The color of the borders in the UI.
     */
    public int getBorderColor() {
        return Theme.NAVY;
    }

    /**
     * Returns the color used as the background for the canvas section of the
     * screen. This is the area that is not the title bar at the top or menu
     * bar at the bottom. Colors are defined as 0xAARRGGBB; the first-byte
     * alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The color of the title border.
     */
    public int getBackgroundColor() {
        return Theme.WHITE;
    }

    /**
     * Returns the main color used in painting components. For example a
     * progress bar will use this color to show the completed progress. Colors
     * are defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The primary color used to paint components.
     */
    public int getHighlightColor() {
        return Theme.LIGHT_BLUE;
    }

    /**
     * Returns the color of the border around the title bar. Colors are
     * defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * By default this is the same color as the menu border. Override this
     * method to change it.
     * 
     * @return The color of the title border.
     * @see #paintTrackbar(Graphics, int, int, int, int)
     */
    public int getTitleBarBorderColor() {
        return getMenuBarBorderColor();
    }

    /**
     * Returns the primary color of the background of the title bar. A second
     * color defined by <code>getTitleBarHighlightColor</code> is overlaid
     * with a vertical gradient.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is
     * ignored.
     * <p>
     * By default this is the same color as the menu bar background. Override
     * this method to change it.
     * 
     * @return The primary color of the title bar background.
     * 
     * @see #getTitleBarHighlightColor()
     */
    public int getTitleBarBackgroundColor() {
        return getMenuBarBackgroundColor();
    }

    /**
     * Returns the highlight color applied as a vertical gradient to the title
     * bar.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is
     * ignored.
     * <p>
     * By default this is the same color as the menu bar highlight. Override
     * this method to change it.
     * 
     * @return The highlight color of the title bar background.
     * 
     * @see #getTitleBarBackgroundColor()
     */
    public int getTitleBarHighlightColor() {
        return getMenuBarHighlightColor();
    }

    /**
     * Returns the color of the border around the menu bar. Colors are defined
     * as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The color of the border around the menu bar.
     * @see #paintMenuBar(Graphics, String, boolean, String, boolean, int,
     *      int)
     */
    public int getMenuBarBorderColor() {
        return getFontColor();
    }

    /**
     * Returns the primary color of the background of the menu bar. A second
     * color defined by <code>getMenuBarHighlightColor</code> is overlaid
     * with a vertical gradient.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is
     * ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The color of the border around the menu bar.
     * 
     * @see #getMenuBarHighlightColor()
     */
    public int getMenuBarBackgroundColor() {
        return Theme.NAVY;
    }

    /**
     * Returns the highlight color applied as a vertical gradient to the menu
     * bar.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is
     * ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The highlight color of the menu bar background.
     * 
     * @see #getMenuBarBackgroundColor()
     */
    public int getMenuBarHighlightColor() {
        return Theme.LIGHT_BLUE;
    }

    /**
     * Gets the height of the title bar in pixels. This method is called
     * whenever the title is set and the title bar is going to be painted.
     * 
     * @return The height of the title bar in pixels.
     * @see #paintTrackbar(Graphics, int, int, int, int)
     */
    public int getTitleHeight() {
        return getTitleFont().getHeight() + 2;
    }

    /**
     * Paints the title bar of the canvas. This method is called only when the
     * title has been set through <code>setTitle</code> and the canvas is
     * not in full screen mode.
     * <p>
     * The supplied <code>Graphics</code> will be set with an appropriate
     * clip and translated such that (0,0) is the top-left corner of the title
     * bar.
     * <p>
     * Override this method to change the appearance of the title bar. For
     * example background or logo images can be placed throughout the
     * application by painting them here.
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     * @param title
     *                is the text for the title bar as defined by the canvas
     *                class.
     * @param width
     *                is the width of the title bar in pixels.
     * @param height
     *                is the height of the title bar in pixels.
     */
    public void paintTitleBar(final Graphics g, final String title,
            final int width, final int height) {
        // Fill the background of the title bar.
        paintTitleBarBackground(g, 0, 0, width, height);

        // Draw a line below the title bar to separate it from the canvas.
        g.setColor(getTitleBarBorderColor());
        g.drawLine(0, height - 1, width, height - 1);

        // Write the title text.
        g.setFont(getTitleFont());
        g.setColor(getTitleFontColor());
        g.drawString(title, width / 2, 1, Graphics.HCENTER | Graphics.TOP);
    }

    /**
     * Paints the background area of the title bar. The text will be added
     * later by the calling <code>paintTitleBar</code> method.
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     * @param x
     *                is the top-left X-coordinate pixel of the title bar.
     * @param y
     *                is the top-left Y-coordinate pixel of the title bar.
     * @param width
     *                is the width of the title bar in pixels.
     * @param height
     *                is the height of the title bar in pixels.
     * 
     * @see #paintTrackbar(Graphics, int, int, int, int)
     */
    protected void paintTitleBarBackground(final Graphics g, final int x,
            final int y, final int width, final int height) {
        // This code would paint the title bar a solid background.
        // int background = getTitleBackgroundColor();
        // g.setColor( background );
        // g.fillRect( x, y, width, height );

        // Paint a gradient background.
        final int primary = getTitleBarBackgroundColor();
        final int secondary = getTitleBarHighlightColor();

        Theme.gradientFill(g, 0, 0, width, height, true, primary, secondary,
                Theme.TITLE_BAR_SECONDARY_COLOR_MAX);
    }

    /**
     * Gets the height of the menu bar in pixels. This method is called
     * whenever the menu is going to be painted.
     * 
     * @return The height of the menu bar in pixels.
     * 
     * @see #paintMenuBar(Graphics, String, boolean, String, boolean, int,
     *      int)
     */
    public int getMenuHeight() {
        return getMenuFont().getHeight() + 2;
    }

    /**
     * Paints the menu bar at the bottom of the canvas. This method is not
     * called if the canvas is in full screen mode.
     * <p>
     * The supplied <code>Graphics</code> will be set with an appropriate
     * clip and translated such that (0,0) is the top-left corner of the title
     * bar.
     * <p>
     * Override this method to change the appearance or functionality of the
     * menu. Be careful not to write strings that are too long and will not
     * fit on the menu bar.
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     * @param left
     *                is the text to write on the left side of the menu bar.
     *                The left side is associated with dimissing input such as
     *                a "Cancel" button.
     * @param highlightLeft
     *                is <code>true</code> if the menu text
     *                <code>left</code> should be highlighted to indicate
     *                the left menu button is currently pressed.
     * @param right
     *                is the text to write on the right side of the menu bar.
     *                The right side is associated with accepting input such
     *                as an "OK" button.
     * @param highlightRight
     *                is <code>true</code> if the menu text
     *                <code>right</code> should be highlighted to indicate
     *                the right menu button is currently pressed.
     * @param width
     *                is the width of the menu bar in pixels.
     * @param height
     *                is the height of the menu bar in pixels.
     */
    public void paintMenuBar(final Graphics g, final String left,
            final boolean highlightLeft, final String right,
            final boolean highlightRight, final int width, final int height) {
        // Fill the menu bar background.
        paintMenuBarBackground(g, 0, 0, width, height);

        // Draw a line above the menu bar to separate it from the canvas.
        g.setColor(getMenuBarBorderColor());
        g.drawLine(0, 0, width, 0);

        // Write the menu items.
        final int normal = getMenuFontColor();
        final int highlighted = getMenuFontHighlightColor();

        final Font font = getMenuFont();
        g.setFont(font);

        final int offset = font.charWidth(' ') / 2;

        g.setColor(highlightLeft ? highlighted : normal);
        g.drawString(left, offset, height, Graphics.BOTTOM | Graphics.LEFT);

        g.setColor(highlightRight ? highlighted : normal);
        g.drawString(right, width - offset, height, Graphics.BOTTOM
                | Graphics.RIGHT);
    }

    /**
     * Paints the background area of the menu bar. The text will be added
     * later by the calling <code>paintMenuBar</code> method.
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     * @param x
     *                is the top-left X-coordinate pixel of the menu bar.
     * @param y
     *                is the top-left Y-coordinate pixel of the menu bar.
     * @param width
     *                is the width of the menu bar in pixels.
     * @param height
     *                is the height of the menu bar in pixels.
     * 
     * @see #paintMenuBar(Graphics, String, boolean, String, boolean, int,
     *      int)
     */
    protected void paintMenuBarBackground(final Graphics g, final int x,
            final int y, final int width, final int height) {
        // This code would paint the menu bar a solid background.
        // int background = getMenuBackgroundColor();
        // g.setColor( background );
        // g.fillRect( x, y, width, height );

        // Paint a gradient background.
        final int primary = getMenuBarBackgroundColor();
        final int secondary = getMenuBarHighlightColor();

        Theme.gradientFill(g, x, y, width, height, true, primary, secondary,
                Theme.MENU_BAR_SECONDARY_COLOR_MAX);
    }

    /**
     * Gets the localized menu text for OK buttons that appear on forms. By
     * default this is "OK".
     * 
     * @return The text used for the OK button on forms.
     */
    public String getMenuTextForOK() {
        return "OK";
    }

    /**
     * Gets the localized menu text for Cancel buttons that appear on forms.
     * By default this is "Cancel".
     * 
     * @return The text used for the Cancel button on forms.
     */
    public String getMenuTextForCancel() {
        return "Cancel";
    }

    /**
     * Paints the background of the main section of the screen. This includes
     * everything except for the title bar at the top and menu bar at the
     * bottom. However, if this canvas is in full screen mode, then this
     * method paints the entire screen.
     * <p>
     * After this method is called, the screen's <code>paintCanvas</code>
     * method will be.
     * <p>
     * By default this method paints the entire background the color specified
     * by <code>getBackgroundColor</code>. Override this implementation to
     * provide a different background for the entire application, such as an
     * image.
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     */
    public void paintBackground(final Graphics g) {
        final int color = getBackgroundColor();

        final int x = g.getClipX();
        final int y = g.getClipY();
        final int w = g.getClipWidth();
        final int h = g.getClipHeight();

        // Clear the canvas.
        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    /**
     * Paints the vertical scrollbar. The scrollbar must go on the right side
     * of the form and span from the top to the bottom. Its width is returned
     * from this method and used to calculate the width of the remaining form
     * area to draw components in.
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     * @param x
     *                is the top-left X-coordinate pixel of the form area.
     * @param y
     *                is the top-left Y-coordinate pixel of the form area.
     * @param width
     *                is the width of the form area in pixels.
     * @param height
     *                is the height of the form area in pixels.
     * @param offset
     *                is the vertical scrolling position of the top pixel to
     *                show on the form area.
     * @param formHeight
     *                is the total height of all the components on the form.
     *                This is bigger than <code>height</code>.
     */
    public void paintVerticalScrollbar(final Graphics g, final int x,
            final int y, final int width, final int height, final int offset,
            final int formHeight) {
        // Make the scrollbar as wide as the rounding diameter.
        final int scrollbarWidth = getVerticalScrollbarWidth();
        final int left = x + width - scrollbarWidth;

        // Draw the scrollbar background.
        paintScrollbarBackground(g, left, y, scrollbarWidth, height);

        // Draw an edge to the scrollbar.
        final int border = getBorderColor();
        g.setColor(border);
        g.drawLine(left, y, left, y + height);

        // Calculate the height of the trackbar.
        final int scrollableHeight = formHeight - height;
        final double trackbarPercentage = (double) height
                / (double) formHeight;
        int trackbarHeight = (int) MathFunc
                .round(height * trackbarPercentage);
        trackbarHeight = Math.max(trackbarHeight, 2 * scrollbarWidth);

        // Calculate the range and location of the trackbar.
        // The scrollbar doesn't actually go from 0% to 100%. The top
        // is actually 1/2 the height of the trackbar from the top of
        // the screen. The bottom is 1/2 the height from the bottom.
        final int rangeStart = trackbarHeight / 2;
        final int range = height - 2 * rangeStart;

        final double offsetPercentage = (double) offset
                / (double) scrollableHeight;
        final int center = y + rangeStart
                + (int) MathFunc.round(offsetPercentage * range);

        // Draw the trackbar.
        paintTrackbar(g, left, center - rangeStart, scrollbarWidth,
                trackbarHeight);
    }

    /**
     * Paints the background area of the scrollbar. This does not include the
     * trackbar (which will be painted later by
     * <code>paintScrollbarTrackbar</code>).
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     * @param x
     *                is the top-left X-coordinate pixel of the scrollbar.
     * @param y
     *                is the top-left Y-coordinate pixel of the scrollbar.
     * @param width
     *                is the width of the scrollbar in pixels.
     * @param height
     *                is the height of the scrollbar in pixels.
     * 
     * @see #paintVerticalScrollbar(Graphics, int, int, int, int, int, int)
     * @see #paintTrackbar(Graphics, int, int, int, int)
     */
    protected void paintScrollbarBackground(final Graphics g, final int x,
            final int y, final int width, final int height) {
        // This code would paint the scrollbar a solid background.
        // int background = getScrollbarBackgroundColor();
        // g.setColor( background );
        // g.fillRect( x, y, width, height );

        // Paint a gradient background.
        final int primary = getScrollbarBackgroundColor();
        final int secondary = getScrollbarHighlightColor();

        Theme.gradientFill(g, x, y, width, height, false, primary, secondary,
                Theme.SCROLLBAR_SECONDARY_COLOR_MAX);
    }

    /**
     * Paints the trackbar on the scrollbar. The trackbar is the sliding bit
     * found on the scrollbar that shows the user where the current screen is
     * relative to the scrolling.
     * 
     * @param g
     *                is the <code>Graphics</code> object to paint with.
     * @param x
     *                is the top-left X-coordinate pixel of the trackbar.
     * @param y
     *                is the top-left Y-coordinate pixel of the trackbar.
     * @param width
     *                is the width of the trackbar in pixels.
     * @param height
     *                is the height of the trackbar in pixels.
     * 
     * @see #paintVerticalScrollbar(Graphics, int, int, int, int, int, int)
     * @see #paintScrollbarBackground(Graphics, int, int, int, int)
     */
    protected void paintTrackbar(final Graphics g, final int x, final int y,
            final int width, final int height) {
        // This code would paint the scrollbar a solid background.
        // int trackbar = getScrollbarTrackbarColor();
        // g.setColor( trackbar );
        // g.fillRect( x, y, width, height );

        // Paint a gradient background.
        final int primary = getScrollbarTrackbarColor();
        final int secondary = getScrollbarBorderColor();

        Theme.gradientFill(g, x, y, width, height, false, primary, secondary,
                0.80);
    }

    /**
     * Returns the width of the vertical scrollbar.
     * 
     * @return The number of pixels wide the scrollbar is.
     */
    public int getVerticalScrollbarWidth() {
        return Theme.SCROLLBAR_WIDTH;
    }

    /**
     * Returns the color of the border around the scrollbar. Colors are
     * defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * By default this is the same as the border color. Override this method
     * to change it.
     * 
     * @return The color of the border around the scrollbar.
     * @see #paintVerticalScrollbar(Graphics, int, int, int, int, int, int)
     */
    public int getScrollbarBorderColor() {
        return getBorderColor();
    }

    /**
     * Returns the color of the background of the scrollbar. This is the area
     * without the trackbar on it.
     * <p>
     * By default this is the same as the scrollbar's border color. Override
     * this method to change it.
     * 
     * @return The color of the scrollbar background.
     * @see #paintVerticalScrollbar(Graphics, int, int, int, int, int, int)
     */
    public int getScrollbarBackgroundColor() {
        return getMenuBarBackgroundColor();
    }

    /**
     * Returns the highlight color applied as a horizontal gradient to the
     * scrollbar.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is
     * ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The highlight color of the scrollbar background.
     * 
     * @see #getScrollbarBackgroundColor()
     */
    public int getScrollbarHighlightColor() {
        return getMenuFontHighlightColor();
    }

    /**
     * Returns the color of the trackbar within the scrollbar. The trackbar is
     * the block that moves up and down the scrollbar to visually inform the
     * user of where in the scrolling they are.
     * <p>
     * By default this is the same as the menu bar's background color.
     * Override this method to change it.
     * 
     * @return The color of the scrollbar's trackbar.
     * @see #paintVerticalScrollbar(Graphics, int, int, int, int, int, int)
     */
    public int getScrollbarTrackbarColor() {
        return getMenuBarHighlightColor();
    }

    /**
     * Fills a rectangle with linear gradient. The gradient colors go from
     * <code>primaryColor</code> to <code>secondaryColor</code> at
     * <code>maxSecondary</code>. So if <code>maxSecondary == 0.70</code>
     * then a line across the fill rectangle 70% of the way would be
     * <code>secondaryColor</code>.
     * 
     * @param g
     *                is the <code>Graphics</code> object for painting.
     * @param x
     *                is the left edge of the rectangle.
     * @param y
     *                is the top edge of the rectangle.
     * @param width
     *                is the width of the rectangle.
     * @param height
     *                is the height of the rectangle.
     * @param fillVertically
     *                is <code>true</code> if the gradient goes from
     *                top-to-bottom or <code>false</code> for left-to-right.
     * @param primaryColor
     *                is the main color.
     * @param secondaryColor
     *                is the highlight color.
     * @param maxSecondary
     *                is between 0.00 and 1.00 and says how far down the fill
     *                will <code>secondaryColor</code> peak.
     */
    public static void gradientFill(final Graphics g, final int x,
            final int y, final int width, final int height,
            final boolean fillVertically, final int primaryColor,
            final int secondaryColor, final double maxSecondary) {
        // Break the primary color into red, green, and blue.
        final int pr = (primaryColor & 0x00FF0000) >> 16;
        final int pg = (primaryColor & 0x0000FF00) >> 8;
        final int pb = (primaryColor & 0x000000FF);

        // Break the secondary color into red, green, and blue.
        final int sr = (secondaryColor & 0x00FF0000) >> 16;
        final int sg = (secondaryColor & 0x0000FF00) >> 8;
        final int sb = (secondaryColor & 0x000000FF);

        // Draw a horizonal line for each pixel from the top to the bottom.
        final int end = (fillVertically ? height : width);

        for (int i = 0; i < end; i++) {
            // Calculate the color for this line.
            final double p = (double) i / (double) end;
            final double v = Math.abs(maxSecondary - p);
            final double v2 = 1.0 - v;

            final int red = (int) (pr * v + sr * v2);
            final int green = (int) (pg * v + sg * v2);
            final int blue = (int) (pb * v + sb * v2);

            g.setColor(red, green, blue);

            // Draw the line.
            if (fillVertically) {
                g.drawLine(x, y + i, x + width, y + i);
            } else // horizontal
            {
                g.drawLine(x + i, y, x + i, y + height);
            }
        }
    }
}
