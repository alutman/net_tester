package client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Client implements Runnable {

    private int port;
    private String address;
    private int timeout;
    private Long delay;
    private boolean outputCsv;

    public static final int MESSAGE_SIZE = 8; /* max size from generate message */

    public Client(String address, int port, int timeout, Long delay, boolean outputCsv) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
        this.delay = delay;
        this.outputCsv = outputCsv;
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

    protected abstract PingResult timeResponse();
    protected abstract String getName();
    private String getDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date now = new Date();
        return df.format(now);
    }
    private String csvOutput(PingResult result) {
        if(result.hasError) {
            return getDate()+","+getName()+","+result.errorType.toString()+","+result.errorMessage;
        }
        else {
            return getDate()+","+getName()+",SUCCESS,"+result.timeTaken;
        }

    }

    private String formatOutput(PingResult result) {
        if(result.hasError) {
            return "["+getName()+" error] "+result.errorMessage;
        }
        else {
            return"["+getName()+"] Ping: " + result.timeTaken+ " ms";
        }

    }

    @Override
    public void run() {
        while (true) {
            PingResult result = timeResponse();
            if(result.hasError) {
                if(outputCsv) {
                    System.err.println(csvOutput(result));
                }
                else {
                    System.err.println(formatOutput(result));
                }
            } else {
                if(outputCsv) {
                    System.out.println(csvOutput(result));
                }
                else {
                    System.out.println(formatOutput(result));
                }
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                //Nada
            }
        }
    }
}
