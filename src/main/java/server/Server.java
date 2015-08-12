package server;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Server implements Runnable {

    private int port = 0;

    public Server(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
    private boolean running = true;

    public synchronized boolean isRunning() {
        return running;
    }
    public synchronized void terminate() {
        running = false;
    }

    protected abstract void listenForRequestAndProcess();
    protected abstract void setupServer();

    @Override
    public void run() {
        setupServer();
        while (isRunning()) {
            listenForRequestAndProcess();
        }
    }

}
