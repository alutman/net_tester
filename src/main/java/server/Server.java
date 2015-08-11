package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Server implements Runnable {

    private boolean running = true;
    private int port = 0;

    public Server(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }


    public synchronized boolean isRunning() {
        return running;
    }
    //TODO Merge this thread control with client threading
    //TODO This is useless as it still waits for a call/connection from the client
    public synchronized void terminate() {
        running = false;
    }

     protected abstract void processRequest();
     protected abstract void startListening();

     @Override
     public void run() {
         startListening();
         while(running) {
             processRequest();
         }
     }

}
