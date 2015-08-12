package client;

import java.io.IOException;
import java.net.*;

/**
 * Created by alutman on 10-Aug-15.
 */
public class UDPClient extends Client {


    public UDPClient(String address, int port, int timeout, boolean outputCsv) {
        super(address, port, timeout, outputCsv);
    }

    @Override
    protected PingResult sendRequest() {

        /* GET HOST */
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName(this.getAddress());
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
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, this.getPort());
        Long startTime = System.currentTimeMillis();
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            return new PingResult(ErrorType.SEND_ERROR, "Send failed: " + e.getMessage());
        }

        /* RECEIVE RESPONSE */
        String response;
        Long endTime;
        byte[] receiveData = new byte[request.length()];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            clientSocket.setSoTimeout(this.getTimeout());
            clientSocket.receive(receivePacket);
            endTime  = System.currentTimeMillis();
            response = new String(receivePacket.getData());
        } catch(SocketTimeoutException ste) {
            return new PingResult(ErrorType.TIMEOUT, "Receive timeout reached: " + ste.getMessage());
        } catch (IOException ioe) {
            return new PingResult(ErrorType.RECEIVE_ERROR, "Receive failed: " + ioe.getMessage());
        }

        /* HANDLE RESULTS */
        Long delay = endTime-startTime;
        if(!response.equals(request.toUpperCase())) {
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
