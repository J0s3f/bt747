package net.sf.bt747.j4me.app.screens;

import javax.microedition.lcdui.Graphics;

import net.sf.bt747.j4me.app.AppController;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;

public final class DelayedDialog extends Dialog {

    private final AppController c;
    private final DeviceScreen previous;
    private final DeviceScreen next;
    private final Class diaglogClass;

    public DelayedDialog(final Class DeviceScreenClass,
            final AppController c, final DeviceScreen previous,
            final DeviceScreen next) {
        this.c = c;
        this.previous = previous;
        this.next = next;
        diaglogClass = DeviceScreenClass;
    }

    public void showNotify() {
        try {
            BT747Dialog deviceScreen;
            deviceScreen = (BT747Dialog) diaglogClass.newInstance();
            deviceScreen.setController(c);
            deviceScreen.setNext(next);
            deviceScreen.setPrevious(previous);
            deviceScreen.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        // Do not call show in the superclass.
        // This is a dummy screen.
        showNotify();
    }

    protected void paint(final Graphics g) {
        // Nothing to show

    }
}
