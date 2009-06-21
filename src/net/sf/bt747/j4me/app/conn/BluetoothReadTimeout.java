/**
 * 
 */
package net.sf.bt747.j4me.app.conn;

/**
 * Waits while reading from the input stream so that if we get wedged reading
 * from the input stream, this will interrupt the read.
 */
final class BluetoothReadTimeoutThread extends Thread {
    /**
     * Timeout in milliseconds. This how long a read from the input stream
     * should take.
     */
    private short timeout;

    /**
     * The thread that is doing the read
     */
    private Thread runner;

    /**
     * Set to true when the read completed successfully
     */
    private boolean readSuccess = false;

    /**
     * Create the read time out thread.
     * 
     * @param runner
     *                the thread that does the reading
     * @param timeout
     *                the timeout for reads
     */
    BluetoothReadTimeoutThread(final Thread runner, final short timeout) {
        this.timeout = timeout;
        this.runner = runner;
    }

    /**
     * Used to restart the thread. Wakes it up.
     */
    public synchronized void restart() {
        interrupt();
    }

    /**
     * Wait the specified timeout to kill the read thread
     */
    public synchronized void run() {
        while (true) {
            readSuccess = false;

            try {
                // Wait the specified time until for the read
                wait(timeout);
            } catch (final InterruptedException e) {
                // If this wait is interrupted it means that the read
                // finished successfully.
                readSuccess = true;
            }

            if (!readSuccess) {
                // The read timed out. Interrupt the read thread so
                // that it will disconnect
                runner.interrupt();
            }

            try {
                // Wait until restart is called
                wait();
            } catch (final InterruptedException e) {
            }
        }
    }

    /**
     * Let the read timeout thread know that the read finished.
     * 
     * @param readSuccess
     *                <code>true</code> if the read completed successfully
     */
    public synchronized void setReadSuccess(final boolean readSuccess) {
        this.readSuccess = readSuccess;
        interrupt();
    }
    
}
