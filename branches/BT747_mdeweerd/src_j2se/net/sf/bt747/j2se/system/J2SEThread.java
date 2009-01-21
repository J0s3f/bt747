package net.sf.bt747.j2se.system;

import bt747.sys.Generic;

public final class J2SEThread implements java.lang.Runnable {

    protected java.lang.Thread jvThread;
    protected bt747.sys.interfaces.BT747Thread btThread = null;
    private volatile boolean running = false;

    public J2SEThread(final bt747.sys.interfaces.BT747Thread t) {
        btThread = t;
    }

    public final void run() {
        running = true;
        // System.out.println("new Thread().run() succeed");
        while (running) {
            try {
                btThread.run();
                // J2SEGeneric.debug("Thread waiting", null);
                java.lang.Thread.sleep(2);
            } catch (final Exception e) {
                Generic.debug("Thread " + this, e);
            }
        }
        running = false;
        // J2SEGeneric.debug("Thread " + this + " ended", null);
        // Vm.debug("Thread ended");
    }

    /**
     * @param running
     *                the running to set
     */
    protected final void setRunning(final boolean running) {
        this.running = running;
    }

    /**
     * @return the running
     */
    protected final boolean isRunning() {
        return running;
    }
}
