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

    public TCPClient(String address, int port, int timeout, Long delay) {
        super(address, port, timeout, delay);
    }

    @Override
    protected long timeResponse() {

        /* SET UP CONNECTION */
        Socket clientSocket = null;
        //TODO find a way to use clientSocket.connect() instead of this constructor
        try {
            clientSocket = new Socket(this.getAddress(), this.getPort());
            clientSocket.setSoTimeout(this.getTimeout());
        } catch (IOException e) {
            printerr(e.getClass().getCanonicalName() + ":" + e.getMessage());
            return ErrorType.SOCKET_ERROR;
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
            printerr("Send failed: " + e.getMessage());
            return ErrorType.SEND_ERROR;
        }

        /* RECEIVE RESPONSE */
        Long endTime;
        String response;
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            response = inFromServer.readLine();
            endTime = System.currentTimeMillis();
        } catch(SocketTimeoutException ste) {
            printerr("Receive timeout reached: " + ste.getMessage());
            return ErrorType.TIMEOUT;
        } catch (IOException e) {
            printerr("Receive failed: " + e.getMessage());
            return ErrorType.RECEIVE_ERROR;
        }

        /* HANDLE RESULTS */
        Long delay = endTime - startTime;
        if(!response.equals(request.toUpperCase())) {
            printerr("Response ("+response+") does not match request ("+request.toUpperCase()+")");
            return ErrorType.RESPONSE_MISMATCH;
        }

        //TODO Determine if the connection should be made once and constantly used or constantly created
        try {
            clientSocket.close();
        } catch(IOException ioe) {}

        return delay;
    }

    @Override
    protected String getName() {
        return "TCP";
    }




}
