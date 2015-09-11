package client;

import shared.RepeatingRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.lang.Math;
import shared.OutputFormat;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Client extends RepeatingRunner {

    private int port;
    private String address;
    private int timeout;
    private boolean matchResult;
    private OutputFormat outputFormat = OutputFormat.NORMAL;

    public static final int MESSAGE_SIZE = 1; /* max size from generate message */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String CSV_SUCCESS_STRING = "SUCCESS";

    public Client(String address, int port, int timeout, Long delay, boolean matchResult, OutputFormat outputFormat) {
        super(delay);
        this.address = address;
        this.port = port;
        this.timeout = timeout;
        this.matchResult = matchResult;
        this.outputFormat = outputFormat;
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

    public boolean isMatchResult() {
        return matchResult;
    }

    protected String generateMessage() {
        String rand = Math.random() + "";
        rand = rand.substring(rand.length()-(MESSAGE_SIZE+1), rand.length()-1);
        return rand;
    }

    protected void logInfo(String message) {
        if(outputFormat.equals(OutputFormat.VERBOSE)) {
            System.out.println("["+getName()+"] "+message);
        }
    }

    protected abstract PingResult sendRequest();
    protected abstract String getName();
    private String getDate() {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        Date now = new Date();
        return df.format(now);
    }
    private String csvOutput(PingResult result) {
        if(result.hasError) {
            return getDate()+","+getName()+","+result.errorType.toString()+","+result.errorMessage;
        }
        else {
            return getDate()+","+getName()+","+CSV_SUCCESS_STRING+","+result.timeTaken;
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

    /* Nothing needed in init for Client */
    public void init() {}
    public void repeat() {
        PingResult result = sendRequest();
        if(result.hasError) {
            if(outputFormat.equals(OutputFormat.CSV)) {
                System.err.println(csvOutput(result));
            }
            else {
                System.err.println(formatOutput(result));
            }
        } else {
            if(outputFormat.equals(OutputFormat.CSV)) {
                System.out.println(csvOutput(result));
            }
            else {
                System.out.println(formatOutput(result));
            }
        }
    }

}
