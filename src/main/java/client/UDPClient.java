package client;

import java.io.IOException;
import java.net.*;

/**
 * Created by alutman on 10-Aug-15.
 */
public class UDPClient extends Client {


    public UDPClient(String address, int port, int timeout, Long delay) {
        super(address, port, timeout, delay);
    }

    @Override
    protected long timeResponse() {

        /* GET HOST */
        InetAddress IPAddress = null;
        try {
            IPAddress = InetAddress.getByName(this.getAddress());
        } catch (UnknownHostException e) {
            printerr("Unknown host: "+e.getMessage());
            return ErrorType.UNKNOWN_HOST;
        }

        /* SET UP CONNECTION */
        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            printerr(e.getClass().getCanonicalName()+":"+e.getMessage());
            return ErrorType.SOCKET_ERROR;
        }

        /* SEND PACKET */
        String request = generateMessage();
        byte[] sendData = request.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, this.getPort());
        Long startTime = System.currentTimeMillis();
        try {
            //TODO replace with clientSocket.connect()?
            clientSocket.send(sendPacket);
        } catch (IOException ioe) {
            printerr("Send failed: " + ioe.getMessage());
            return ErrorType.SEND_ERROR;
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
            printerr("Receive timeout reached: "+ste.getMessage());
            return ErrorType.TIMEOUT;
        } catch (IOException ioe) {
            printerr("Receive failed: " + ioe.getMessage());
            return ErrorType.RECEIVE_ERROR;
        }

        /* HANDLE RESULTS */
        Long delay = endTime-startTime;
        if(!response.equals(request.toUpperCase())) {
            printerr("Response ("+response+") does not match request ("+request.toUpperCase()+")");
            return ErrorType.RESPONSE_MISMATCH;
        }

        //TODO Determine if the connection should be made once and constantly used or constantly created
        // May have no difference with UDP
        clientSocket.close();
        return delay;
    }

    @Override
    protected String getName() {
        return "UDP";
    }

}
