package server;

import shared.StoppableRunner;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Server extends StoppableRunner {

    private int port = 0;

    public Server(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

     protected abstract void listenForRequestAndProcess();
     protected abstract void setupServer();

     @Override
     public void run() {
         setupServer();
         while(isRunning()) {
             listenForRequestAndProcess();
         }
     }

}
