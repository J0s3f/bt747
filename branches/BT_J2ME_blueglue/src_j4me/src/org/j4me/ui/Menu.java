package org.j4me.ui;

import org.j4me.logging.Log;
import org.j4me.ui.components.Component;
import org.j4me.ui.components.MenuOption;

/**
 * The <code>Menu</code> class is used for creating the application's menus.
 * <p>
 * J2ME devices have small screens and are not all very responsive to
 * scrolling. However, they do have keypads. The <code>Menu</code> class
 * respects this and limits menus to a total of 9 possible choices (1-9) plus
 * "Exit". Usually all the choices can be seen on a single screen and selected
 * with a single button.
 * <p>
 * Override this class to change how the menu is painted for your application.
 */
public class Menu extends Dialog {
    /**
     * The screen that invoked this one or <code>null</code> if there is no
     * previous screen.
     */
    protected DeviceScreen previous;

    /**
     * Constructs a menu.
     */
    public Menu() {
        // No spacing between components.
        // The MenuOption component will add spacing for us.
        setSpacing(0);

        // Add the menu bar.
        final Theme theme = UIManager.getTheme();
        final String rightMenuText = theme.getMenuTextForOK();
        setMenuText(null, rightMenuText);
    }

    /**
     * Constructs a menu.
     * 
     * @param name
     *                is the title for this menu, for example "Main Menu". It
     *                appears at the top of the screen in the title area.
     * @param previous
     *                is the screen to return to if the user cancels this.
     */
    public Menu(final String name, final DeviceScreen previous) {
        this();

        this.previous = previous;

        setTitle(name);
        setPrevious(previous);
    }

    /**
     * Sets the screen to return to if the user cancels this menu. If
     * <code>previous</code> is <code>null</code>, there will be no
     * "Cancel" button.
     * 
     * @param previous
     *                is the screen to go to if the user presses "Cancel".
     */
    public void setPrevious(final DeviceScreen previous) {
        // Record the previous screen.
        this.previous = previous;

        // Set the menu text.
        final Theme theme = UIManager.getTheme();
        final String leftMenuText = (previous == null ? null : theme
                .getMenuTextForCancel());
        final String rightMenuText = theme.getMenuTextForOK();
        setMenuText(leftMenuText, rightMenuText);
    }

    /**
     * Appends a new menu option to this menu.
     * 
     * @param option
     *                is the menu item to add.
     */
    public void appendMenuOption(final MenuItem option) {
        final MenuOption choice = new MenuOption(option);
        append(choice);
    }

    /**
     * Appends a screen as a menu option. If selected the screen will be
     * shown. The screen's title is used as its text.
     * 
     * @param option
     *                is screen to add as a menu item.
     */
    public void appendMenuOption(final DeviceScreen option) {
        final MenuOption choice = new MenuOption(option);
        append(choice);
    }

    /**
     * Appends a screen as a menu option. If selected the screen will be
     * shown.
     * 
     * @param text
     *                is string that appears in the menu option.
     * @param option
     *                is screen to add as a menu item.
     */
    public void appendMenuOption(final String text, final DeviceScreen option) {
        final MenuOption choice = new MenuOption(text, option);
        append(choice);
    }

    /**
     * Appends another menu as a menu option. The submenu will have an arrow
     * next to it to indicate to the user it is another menu.
     * <p>
     * To use a <code>Menu</code> as a screen and not a submenu call the
     * <code>appendMenuOption</code> method instead.
     * 
     * @param submenu
     *                is the screen to add as a menu item.
     */
    public void appendSubmenu(final Menu submenu) {
        final MenuOption choice = new MenuOption(submenu, true);
        append(choice);
    }

    /**
     * The left menu button takes the user back to the previous screen. If
     * there is no previous screen it has no effect.
     */
    protected void declineNotify() {
        // Go back to the previous screen.
        if (previous != null) {
            previous.show();
        }

        // Continue processing the event.
        super.declineNotify();
    }

    /**
     * The right menu button selects the highlighted menu item.
     */
    protected void acceptNotify() {
        // Go to the highlighted screen.
        final int highlighted = getSelected();
        selection(highlighted);

        // Continue processing the event.
        super.acceptNotify();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.j4me.ui.Dialog#keyReleased(int)
     */
    protected void keyReleased(final int keyCode) {
        boolean select = false;
        boolean back = false;
        if (keyCode == DeviceScreen.RIGHT) {
            final Component component = get(getSelected());
            if (component instanceof MenuOption) {
                final MenuOption mo = (MenuOption) component;
                if (mo.isSubmenu()) {
                    select = true;
                }
            }
            // Object get(getS)
            // if(get(getSelected()).)

        } else if (keyCode == DeviceScreen.LEFT) {
            back = true;
        }

        super.keyReleased(keyCode);

        if (select) {
            selection(getSelected());
        } else if (back) {
            declineNotify();
        }
    }

    /**
     * Responds to key press events that are specific to menu screens. Selects
     * the highlighted menu choice if the joystick's <code>FIRE</code> key
     * is pressed. Scrolls from the last choice to the first choice if
     * <code>DOWN</code> is pressed and from the first to the last if
     * <code>UP</code> is pressed.
     * 
     * @param keyCode
     *                is the key code of the button the user pressed.
     */
    protected void keyPressed(final int keyCode) {
        handleKeyPressOrRepeat(keyCode, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.j4me.ui.Dialog#keyRepeated(int)
     */
    protected void keyRepeated(final int keyCode) {
        handleKeyPressOrRepeat(keyCode, true);
    }

    private final void handleKeyPressOrRepeat(final int keyCode,
            final boolean isRepeated) {
        final int initialSize = size();
        int nextPos = -100; // Negative value means no movement

        // Wrap the scroll around the screen?
        if (keyCode == DeviceScreen.DOWN) {
            if (getSelected() == size() - 1) {
                // Go to the first menu choice.
                nextPos = 0;
            } else if (isRepeated) {
                nextPos = getSelected() + 1;
            }
        } else if (keyCode == DeviceScreen.UP) {
            if ((getSelected() == 0) && (size() > 1)) {
                // Go to the last menu choice.
                nextPos = size() - 1;
            } else if (isRepeated) {
                nextPos = getSelected() - 1;
            }
        }

        if (!isRepeated) {
            super.keyPressed(keyCode);
        } else {
            super.keyRepeated(keyCode);
        }

        // Were we going to the first or last menu choice?
        // Only do these after super.keyPressed(). Otherwise
        // keyPressed() will scroll again so we'll actually wind
        // up on the second or second-to-last menu choice.
        if ((nextPos >= 0) && (size() == initialSize)) {
            Log.debug(isRepeated ? "KeyRepeated" : "KeyPressed");
            setSelected(nextPos);
        }
    }

    /**
     * Selects a menu item.
     * 
     * @param selection
     *                is the index of <code>choice</code> that is selected.
     */
    private void selection(final int selection) {
        final Component component = get(selection);

        if (component instanceof MenuOption) {
            final MenuOption chosen = (MenuOption) component;

            // Record this as the selection.
            setSelected(selection);

            // Perform the selection operation.
            chosen.select();
        }
    }
}
