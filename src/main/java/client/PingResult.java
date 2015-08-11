package client;

/**
 * Created by alutman on 11-Aug-15.
 */
public class PingResult {
    protected long timeTaken;
    protected String errorMessage;
    protected ErrorType errorType;
    protected boolean hasError;


    public PingResult(long timeTaken) {
        this.timeTaken = timeTaken;
        this.hasError = false;
    }

    public PingResult(ErrorType errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.hasError = true;
    }


}
