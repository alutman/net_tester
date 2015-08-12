package shared;


/**
 * Created by alutman on 11-Aug-15.
 */
/* Runnable which repeats itself with an optional delay amount */
public abstract class RepeatingRunner implements Runnable {
    private volatile boolean stop = false;
    private volatile Thread workingThread;

    private long delay;

    public RepeatingRunner(long delay) {
        this.delay = delay;
    }
    public RepeatingRunner() {
        this.delay = 0;
    }

    /* Shut down the runner */
    public void terminate() {
        stop = true;
        if(workingThread != null) {
            workingThread.interrupt();
        }
    }

    public long getDelay() {
        return delay;
    }

    /* A more graceful method to shut down the runner */
    public void finish() {
        stop = true;
    }

    /* Run once method for setup */
    public abstract void init();
    /* Method to run repeatably  */
    public abstract void repeat();

    @Override
    public final void run() {
        this.workingThread = Thread.currentThread();
        if(this.stop) { return; }
        init();
        while(!stop && !this.workingThread.isInterrupted()) {
            try {
                repeat();
                Thread.sleep(delay);
            } catch(InterruptedException ie) {
                if(stop) {
                    this.workingThread.interrupt();
                }
            }
        }
    }


}
