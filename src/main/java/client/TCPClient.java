package client;

import java.io.*;
import java.net.*;

/**
 * Created by alutman on 10-Aug-15.
 */
public class TCPClient extends Client{

    public TCPClient(String address, int port, int timeout, Long delay, boolean outputCsv, boolean matchResult) {
        super(address, port, timeout, delay, outputCsv, matchResult);
    }

    @Override
    protected PingResult sendRequest() {

        /* SET UP CONNECTION */
        Socket clientSocket = new Socket();
        try {
            clientSocket.connect(new InetSocketAddress(this.getAddress(), this.getPort()), this.getTimeout());
            clientSocket.setSoTimeout(this.getTimeout());
        } catch(SocketTimeoutException ste) {
            return new PingResult(ErrorType.TIMEOUT, "Connect timeout reached: " + ste.getMessage());
        } catch (IOException e) {
            return new PingResult(ErrorType.SOCKET_ERROR, e.getClass().getCanonicalName() + ":" + e.getMessage());
        }


        /* WRITE TO SERVER */
        Long startTime;
        String request = generateMessage();
        try {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            startTime = System.currentTimeMillis();
            outToServer.writeBytes(request);
        } catch (IOException e) {
            return new PingResult(ErrorType.SEND_ERROR, "Send failed: " + e.getMessage());
        }

        /* RECEIVE RESPONSE */
        Long endTime;
        String response;
        try {
            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            byte[] serverResponse = new byte[Client.MESSAGE_SIZE];
            inFromServer.readFully(serverResponse);
            response = new String(serverResponse);

            endTime = System.currentTimeMillis();
        } catch(SocketTimeoutException ste) {
            return new PingResult(ErrorType.TIMEOUT, "Receive timeout reached: " + ste.getMessage());
        } catch (IOException e) {
            return new PingResult(ErrorType.RECEIVE_ERROR, "Receive failed: " + e.getMessage());
        }
        /* HANDLE RESULTS */
        Long delay = endTime - startTime;
        if(isMatchResult() && !response.equals(request)) {
            return new PingResult(ErrorType.RESPONSE_MISMATCH,"Response ("+response+") does not match request ("+request+")");
        }

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
