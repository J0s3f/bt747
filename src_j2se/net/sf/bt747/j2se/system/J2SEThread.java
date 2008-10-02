package net.sf.bt747.j2se.system;

public final class J2SEThread implements java.lang.Runnable {

    public java.lang.Thread jvThread;
    public bt747.sys.interfaces.BT747Thread btThread = null;
    public boolean running = false;

    public J2SEThread(final bt747.sys.interfaces.BT747Thread t) {
        btThread = t;
    }

    public final void run() {
        running = true;
        // System.out.println("new Thread().run() succeed");
        while (btThread != null) {
            try {
                btThread.run();
                java.lang.Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
        // Vm.debug("Thread ended");
    }

}
