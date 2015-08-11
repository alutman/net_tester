package client;

import java.io.IOException;
import java.net.*;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Client implements Runnable {

    private int port;
    private String address;
    private int timeout;
    private Long delay;

    public static final int MESSAGE_SIZE = 8; /* max size from generate message */

    public Client(String address, int port, int timeout, Long delay) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
        this.delay = delay;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public int getTimeout() {
        return timeout;
    }

    public Long getDelay() {
        return delay;
    }

    protected String generateMessage() {
        return String.format("%s%s%04d", getName().toLowerCase().substring(0, 3), "m", System.currentTimeMillis() % 10000);
    }

    protected abstract long timeResponse();
    protected abstract String getName();

    protected void print(String s) {
        System.out.println("["+getName()+"] " + s);
    }
    protected void printerr(String s) {
        System.err.println("["+getName()+" error] "+s);
    }

    @Override
    public void run() {
        while (true) {
            long time = timeResponse();
            if(time >= 0) {
                print("Ping: " + time+" ms");
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                //Nada
            }
        }
    }
}
