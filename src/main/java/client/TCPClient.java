package client;

import java.io.*;
import java.net.*;
import shared.OutputFormat;

/**
 * Created by alutman on 10-Aug-15.
 */
public class TCPClient extends Client{

    public TCPClient(String address, int port, int timeout, Long delay, boolean matchResult, OutputFormat outputFormat) {
        super(address, port, timeout, delay, matchResult, outputFormat);
    }

    @Override
    protected PingResult sendRequest() {

        /* SET UP CONNECTION */
        Socket clientSocket = new Socket();
        try {
            clientSocket.connect(new InetSocketAddress(this.getAddress(), this.getPort()), this.getTimeout());
            clientSocket.setSoTimeout(this.getTimeout());
            logInfo("Connected established to "+clientSocket.getRemoteSocketAddress().toString()+" from port "+clientSocket.getLocalPort());
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
            logInfo("Sent byte ("+request+") to "+clientSocket.getRemoteSocketAddress().toString()+" from port "+clientSocket.getLocalPort());
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
            logInfo("Byte ("+response+") received from "+clientSocket.getRemoteSocketAddress().toString()+" from port "+clientSocket.getLocalPort());

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
