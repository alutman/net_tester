package shared;

/**
 * Created by alutman on 11-Aug-15.
 */
public abstract class StoppableRunner implements Runnable {
    private boolean running = true;

    public synchronized boolean isRunning() {
        return running;
    }
    public synchronized void terminate() {
        running = false;
    }

}
