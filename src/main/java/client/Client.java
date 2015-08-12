package client;

import shared.RepeatingRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by alutman on 10-Aug-15.
 */
public abstract class Client extends RepeatingRunner {

    private int port;
    private String address;
    private int timeout;
    private Long delay;
    private boolean outputCsv;

    private static final int MESSAGE_NUMBER_LENGTH = 4;
    private static final int MESSAGE_NAME_LENGTH = 4;
    private static final char MESSAGE_NAME_PADDING_CHAR = 'm';
    public static final int MESSAGE_SIZE = MESSAGE_NUMBER_LENGTH + MESSAGE_NAME_LENGTH; /* max size from generate message */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String CSV_SUCCESS_STRING = "SUCCESS";

    public Client(String address, int port, int timeout, Long delay, boolean outputCsv) {
        super(delay);
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

    /* Produce a message to send from the name, time and various message lengths. Length should always equal MESSAGE_SIZE */
    protected String generateMessage() {
        //Get the largest portion of the name as specified
        String messageString = getName().toLowerCase().substring(0, Math.min(getName().length(), MESSAGE_NAME_LENGTH));
        //Pad any leftover space with the padding character
        char[] ca = new char[MESSAGE_NAME_LENGTH - messageString.length()];
        Arrays.fill(ca, MESSAGE_NAME_PADDING_CHAR);
        messageString = messageString.concat(new String(ca));
        //Generate a number to the maximum specified digits
        Double messageNumber = System.currentTimeMillis() % Math.pow(10d, MESSAGE_NUMBER_LENGTH);
        //Pad the number with zeros if necessary
        return String.format("%s%0"+MESSAGE_NUMBER_LENGTH+".0f", messageString, messageNumber);
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
    }

}
