package server;

import shared.RepeatingRunner;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Server extends RepeatingRunner {

    private int port = 0;

    public Server(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    protected abstract void listenForRequestAndProcess();

    protected abstract void setupServer();

    public void init() {
        setupServer();
    }

    public void repeat() {
        listenForRequestAndProcess();
    }

}
