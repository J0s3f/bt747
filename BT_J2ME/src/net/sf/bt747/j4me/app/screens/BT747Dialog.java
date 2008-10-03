package net.sf.bt747.j4me.app.screens;

import net.sf.bt747.j4me.app.AppController;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;

public abstract class BT747Dialog extends Dialog {

    protected AppController c;
    protected DeviceScreen previous;
    protected DeviceScreen next;

    public BT747Dialog() {
    }

    public final void setController(AppController c) {
        this.c = c;
    }

    public final void setPrevious(DeviceScreen previous) {
        this.previous = previous;
    }

    public final void setNext(DeviceScreen next) {
        this.next = next;
    }
}
