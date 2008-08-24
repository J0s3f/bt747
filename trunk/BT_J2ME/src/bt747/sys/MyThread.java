package bt747.sys;

public class MyThread implements java.lang.Runnable {

    public java.lang.Thread jvThread;
    public bt747.sys.Thread btThread = null;
    public boolean running = false;

    public MyThread(final bt747.sys.Thread t) {
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
