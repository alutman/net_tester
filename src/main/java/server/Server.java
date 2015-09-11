package server;

import shared.RepeatingRunner;
import shared.OutputFormat;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Server extends RepeatingRunner {

    private int port = 0;
    private String name;
    private OutputFormat outputFormat = OutputFormat.NORMAL;

    public Server(int port, String name, OutputFormat outputFormat) {
        this.port = port;
        this.name = name;
        this.outputFormat = outputFormat;
    }

    public Server(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    protected void logException(Exception e, String message) {
        System.err.println("["+name+" Server] "+message+". "+e.getClass().getCanonicalName()+":"+e.getMessage());
    }
    protected void logInfo(String message) {
        if(outputFormat.equals(OutputFormat.VERBOSE)) {
            System.out.println("["+name+" Server] "+message);
        }
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
