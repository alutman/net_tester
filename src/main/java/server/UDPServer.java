package server;

import client.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by alutman on 10-Aug-15.
 */
public class UDPServer extends Server {

    private DatagramSocket serverSocket;

    public UDPServer(int port) {
        super(port, "UDP");
    }

    @Override
    protected void listenForRequestAndProcess() {

        /* RECEIVE INCOMING REQUESTS */
        byte[] receiveData = new byte[Client.MESSAGE_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            serverSocket.receive(receivePacket);
        }
        catch(IOException e) {
            logException(e, "Receive failed");
        }

        /* WRITE RESPONSE */
        String response = new String(receivePacket.getData());
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        byte[] sendData = response.getBytes();
        DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length, IPAddress, port);
        try {
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            logException(e, "Send failed");
        }
    }


    @Override
    protected void setupServer() {
        this.serverSocket = null;
        try {
            serverSocket = new DatagramSocket(this.getPort());
        }
        catch (Exception e){
            logException(e, "Setup failed");
            this.terminate();
        }
    }
}
