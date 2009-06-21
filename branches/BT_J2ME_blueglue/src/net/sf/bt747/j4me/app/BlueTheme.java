package net.sf.bt747.j4me.app;

import javax.microedition.lcdui.Font;

import org.j4me.logging.Log;
import org.j4me.ui.Theme;

/**
 * A red on white theme.
 */
public final class BlueTheme extends Theme {

    private static Font MEDIUM_FONT;
    private static Font MEDIUM_FONT_BOLD;

    public BlueTheme(final int height, final int width) {
        int usedWidth;
        if (width > height) {
            usedWidth = width / 2;
        } else {
            usedWidth = width;
        }
        Log.debug("BlueTheme");
        if (BlueTheme.MEDIUM_FONT == null) {
            Font base;
            final int baseFace = Font.getDefaultFont().getFace();
            int maxsize;
            Log.debug("Default size " + Font.getDefaultFont().charWidth('0'));
            Log.debug("Screen WxH " + width + "x" + height);
            maxsize = usedWidth / 24;
            Log.debug("Target size " + maxsize);
            base = Font.getFont(baseFace, Font.STYLE_PLAIN, Font.SIZE_LARGE);
            if (base.charWidth('0') > maxsize) {
                base = Font.getFont(baseFace, Font.STYLE_PLAIN,
                        Font.SIZE_MEDIUM);
                if (base.charWidth('0') > maxsize) {
                    base = Font.getFont(baseFace, Font.STYLE_PLAIN,
                            Font.SIZE_SMALL);
                }
            }
            Log.debug("New size " + base.charWidth('0'));

            BlueTheme.MEDIUM_FONT = base;
            BlueTheme.MEDIUM_FONT_BOLD = Font.getFont(base.getFace(),
                    Font.STYLE_BOLD | Font.STYLE_PLAIN, base.getSize());
        }
    }

    public final Font getFont() {
        return BlueTheme.MEDIUM_FONT;
    }

    public final Font getMenuFont() {
        return BlueTheme.MEDIUM_FONT;
    }

    public final Font getTitleFont() {
        return BlueTheme.MEDIUM_FONT_BOLD;
    }

    /**
     * @see Theme#getBackgroundColor()
     */
    public final int getBackgroundColor() {
        return Theme.WHITE;
    }

    /**
     * @see Theme#getFontColor()
     */
    public final int getFontColor() {
        return Theme.NAVY;
    }

    /**
     * @see Theme#getBorderColor()
     */
    public final int getBorderColor() {
        return Theme.NAVY;
    }

    /**
     * @see Theme#getHighlightColor()
     */
    public final int getHighlightColor() {
        return Theme.LIGHT_BLUE;
    }

    /**
     * @see Theme#getMenuBarBackgroundColor()
     */
    public final int getMenuBarBackgroundColor() {
        return Theme.NAVY;
    }

    /**
     * @see Theme#getMenuBarHighlightColor()
     */
    public final int getMenuBarHighlightColor() {
        return Theme.LIGHT_BLUE;
    }

    /**
     * @see Theme#getMenuBarBorderColor()
     */
    public final int getMenuBarBorderColor() {
        return Theme.WHITE;
    }

    /**
     * @see Theme#getMenuFontColor()
     */
    public final int getMenuFontColor() {
        return Theme.WHITE;
    }

    /**
     * @see Theme#getMenuFontHighlightColor()
     */
    public final int getMenuFontHighlightColor() {
        return Theme.SILVER;
    }

    /**
     * @see Theme#getTitleBarBackgroundColor()
     */
    public final int getTitleBarBackgroundColor() {
        return Theme.NAVY;
    }

    /**
     * @see Theme#getTitleBarHighlightColor()
     */
    public final int getTitleBarHighlightColor() {
        return Theme.BLUE;
    }

    /**
     * @see Theme#getTitleBarBorderColor()
     */
    public final int getTitleBarBorderColor() {
        return Theme.LIGHT_BLUE;
    }

    /**
     * @see Theme#getTitleFontColor()
     */
    public final int getTitleFontColor() {
        return Theme.WHITE;
    }

    /**
     * @see Theme#getScrollbarBackgroundColor()
     */
    public final int getScrollbarBackgroundColor() {
        return Theme.BLUE;
    }

    /**
     * @see Theme#getScrollbarHighlightColor()
     */
    public final int getScrollbarHighlightColor() {
        return Theme.SILVER;
    }

    /**
     * @see Theme#getScrollbarBorderColor()
     */
    public final int getScrollbarBorderColor() {
        return Theme.BLUE;
    }

    /**
     * @see Theme#getScrollbarTrackbarColor()
     */
    public final int getScrollbarTrackbarColor() {
        return Theme.BLUE;
    }

    public final String getMenuTextForCancel() {
        return "Back";
    }
}
