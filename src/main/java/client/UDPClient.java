package client;

import java.io.IOException;
import java.net.*;
import shared.OutputFormat;

/**
 * Created by alutman on 10-Aug-15.
 */
public class UDPClient extends Client {


    public UDPClient(String address, int port, int timeout, Long delay, boolean matchResult, OutputFormat outputFormat) {
        super(address, port, timeout, delay, matchResult, outputFormat);
    }

    @Override
    protected PingResult sendRequest() {

        /* GET HOST */
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(this.getAddress());
        } catch (UnknownHostException e) {
            return new PingResult(ErrorType.UNKNOWN_HOST, "Unknown host: "+e.getMessage());
        }

        /* SET UP CONNECTION */
        DatagramSocket clientSocket = null;
        try {
            //Client port is auto selected by the kernel
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            return new PingResult(ErrorType.SOCKET_ERROR, e.getClass().getCanonicalName() + ":" + e.getMessage());
        }

        /* SEND PACKET */
        String request = generateMessage();
        byte[] sendData = request.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, this.getPort());
        Long startTime = System.currentTimeMillis();
        try {
            clientSocket.send(sendPacket);
            logInfo("Sent message ("+request+") to "+ipAddress.toString()+":"+this.getPort()+" from port "+clientSocket.getLocalPort());
        } catch (IOException e) {
            return new PingResult(ErrorType.SEND_ERROR, "Send failed: " + e.getMessage());
        }

        /* RECEIVE RESPONSE */
        String response;
        Long endTime;
        byte[] receiveData = new byte[Client.MESSAGE_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            clientSocket.setSoTimeout(this.getTimeout());
            clientSocket.receive(receivePacket);

            endTime  = System.currentTimeMillis();
            response = new String(receivePacket.getData());
            logInfo("Message ("+response+") received from "+ipAddress.toString()+":"+this.getPort()+" from port "+clientSocket.getLocalPort());

        } catch(SocketTimeoutException ste) {
            return new PingResult(ErrorType.TIMEOUT, "Receive timeout reached: " + ste.getMessage());
        } catch (IOException ioe) {
            return new PingResult(ErrorType.RECEIVE_ERROR, "Receive failed: " + ioe.getMessage());
        }

        /* HANDLE RESULTS */
        Long delay = endTime-startTime;
        if(isMatchResult() && !response.equals(request)) {
            return new PingResult(ErrorType.RESPONSE_MISMATCH,"Response ("+response+") does not match request ("+request.toUpperCase()+")");
        }

        clientSocket.close();
        return new PingResult(delay);
    }

    @Override
    protected String getName() {
        return "UDP";
    }

}
