package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by alutman on 10-Aug-15.
 */
public class TCPClient extends Client{

    public TCPClient(String address, int port, int timeout, Long delay, boolean outputCsv) {
        super(address, port, timeout, delay, outputCsv);
    }

    @Override
    protected PingResult timeResponse() {

        /* SET UP CONNECTION */
        Socket clientSocket = null;
        //TODO find a way to use clientSocket.connect() instead of this constructor
        try {
            clientSocket = new Socket(this.getAddress(), this.getPort());
            clientSocket.setSoTimeout(this.getTimeout());
        } catch (IOException e) {
            return new PingResult(ErrorType.SOCKET_ERROR, e.getClass().getCanonicalName() + ":" + e.getMessage());
        }


        /* WRITE TO SERVER */
        Long startTime;
        String request = generateMessage();
        try {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            startTime = System.currentTimeMillis();
            outToServer.writeBytes(request + "\n");
            //TODO Check timeout works
        } catch (IOException e) {
            return new PingResult(ErrorType.SEND_ERROR, "Send failed: " + e.getMessage());
        }

        /* RECEIVE RESPONSE */
        Long endTime;
        String response;
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            response = inFromServer.readLine();
            endTime = System.currentTimeMillis();
        } catch(SocketTimeoutException ste) {
            return new PingResult(ErrorType.TIMEOUT, "Receive timeout reached: " + ste.getMessage());
        } catch (IOException e) {
            return new PingResult(ErrorType.RECEIVE_ERROR, "Receive failed: " + e.getMessage());
        }

        /* HANDLE RESULTS */
        Long delay = endTime - startTime;
        if(!response.equals(request.toUpperCase())) {
            return new PingResult(ErrorType.RESPONSE_MISMATCH,"Response ("+response+") does not match request ("+request.toUpperCase()+")");
        }

        //TODO Determine if the connection should be made once and constantly used or constantly created
        try {
            clientSocket.close();
        } catch(IOException ioe) {}

        return new PingResult(delay);
    }

    @Override
    protected String getName() {
        return "TCP";
    }




}
