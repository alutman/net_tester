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
        super(port);
    }

    @Override
    protected void processRequest() {

        /* RECEIVE INCOMING REQUESTS */
        byte[] receiveData = new byte[Client.MESSAGE_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            //TODO Replace with serverSocket.bind()? may be pointless
            serverSocket.receive(receivePacket);
        }
        catch(IOException e) {
            System.err.println("[UDP Server] Receive failed. "+e.getClass().getCanonicalName()+":"+e.getMessage());
        }

        /* WRITE RESPONSE */
        String response = new String(receivePacket.getData()).toUpperCase();
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        byte[] sendData = response.getBytes();
        DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length, IPAddress, port);
        try {
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            System.err.println("[UDP Server] Send failed. "+e.getClass().getCanonicalName()+":"+e.getMessage());
        }
    }


    @Override
    protected void startListening() {
        this.serverSocket = null;
        try {
            serverSocket = new DatagramSocket(this.getPort());
        }
        catch (SocketException se){
            throw new RuntimeException(se);
        }
    }
}
