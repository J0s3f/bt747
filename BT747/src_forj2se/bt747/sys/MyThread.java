package bt747.sys;

public class MyThread implements java.lang.Runnable {

    public java.lang.Thread jvThread;
    public bt747.sys.Thread btThread=null;
    
    public MyThread(bt747.sys.Thread t) {
        btThread=t;
    }
    public final void run() {
        //System.out.println("new Thread().run() succeed");
        while(btThread!=null)
        {
            btThread.run();
            try {
                jvThread.sleep(2);
            } catch(InterruptedException e){}
        }
    }
    
}
