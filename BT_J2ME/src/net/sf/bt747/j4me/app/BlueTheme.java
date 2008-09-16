package net.sf.bt747.j4me.app;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;

import org.j4me.logging.Log;
import org.j4me.ui.Theme;
import org.j4me.ui.UIManager;

/**
 * A red on white theme.
 */
public final class BlueTheme extends Theme {

    private static Font MEDIUM_FONT;
    private static Font MEDIUM_FONT_BOLD;

    public BlueTheme(final int width) {
        Log.debug("BlueTheme");
        if (MEDIUM_FONT == null) {
            Font base;
            int baseFace = Font.getDefaultFont().getFace();
            int maxsize;
            Log.debug("Default size " + Font.getDefaultFont().charWidth('0'));
            Log.debug("Width " + width);
            maxsize = width / 24;
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

            MEDIUM_FONT = base;
            MEDIUM_FONT_BOLD = Font.getFont(base.getFace(), Font.STYLE_BOLD
                    | Font.STYLE_PLAIN, base.getSize());
        }
    }

    public final Font getFont() {
        return MEDIUM_FONT;
    }

    public final Font getMenuFont() {
        return MEDIUM_FONT;
    }

    public final Font getTitleFont() {
        return MEDIUM_FONT_BOLD;
    }

    /**
     * @see Theme#getBackgroundColor()
     */
    public final int getBackgroundColor() {
        return WHITE;
    }

    /**
     * @see Theme#getFontColor()
     */
    public final int getFontColor() {
        return NAVY;
    }

    /**
     * @see Theme#getBorderColor()
     */
    public final int getBorderColor() {
        return NAVY;
    }

    /**
     * @see Theme#getHighlightColor()
     */
    public final int getHighlightColor() {
        return LIGHT_BLUE;
    }

    /**
     * @see Theme#getMenuBarBackgroundColor()
     */
    public final int getMenuBarBackgroundColor() {
        return NAVY;
    }

    /**
     * @see Theme#getMenuBarHighlightColor()
     */
    public final int getMenuBarHighlightColor() {
        return LIGHT_BLUE;
    }

    /**
     * @see Theme#getMenuBarBorderColor()
     */
    public final int getMenuBarBorderColor() {
        return WHITE;
    }

    /**
     * @see Theme#getMenuFontColor()
     */
    public final int getMenuFontColor() {
        return WHITE;
    }

    /**
     * @see Theme#getMenuFontHighlightColor()
     */
    public final int getMenuFontHighlightColor() {
        return SILVER;
    }

    /**
     * @see Theme#getTitleBarBackgroundColor()
     */
    public final int getTitleBarBackgroundColor() {
        return NAVY;
    }

    /**
     * @see Theme#getTitleBarHighlightColor()
     */
    public final int getTitleBarHighlightColor() {
        return BLUE;
    }

    /**
     * @see Theme#getTitleBarBorderColor()
     */
    public final int getTitleBarBorderColor() {
        return LIGHT_BLUE;
    }

    /**
     * @see Theme#getTitleFontColor()
     */
    public final int getTitleFontColor() {
        return WHITE;
    }

    /**
     * @see Theme#getScrollbarBackgroundColor()
     */
    public final int getScrollbarBackgroundColor() {
        return BLUE;
    }

    /**
     * @see Theme#getScrollbarHighlightColor()
     */
    public final int getScrollbarHighlightColor() {
        return SILVER;
    }

    /**
     * @see Theme#getScrollbarBorderColor()
     */
    public final int getScrollbarBorderColor() {
        return BLUE;
    }

    /**
     * @see Theme#getScrollbarTrackbarColor()
     */
    public final int getScrollbarTrackbarColor() {
        return BLUE;
    }

    public final String getMenuTextForCancel() {
        return "Back";
    }
}
