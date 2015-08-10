package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by alutman on 10-Aug-15.
 */
public class UDPServer implements Runnable {

    private boolean isRunning = true;
    private int port = 0;

    public UDPServer(int port) {
        this.port = port;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }
    public synchronized void stop() {
        isRunning = false;
    }


    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        try {
            serverSocket = new DatagramSocket(port);
        }
        catch (SocketException se){
            throw new RuntimeException(se);
        }
        while(isRunning())
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            }
            catch(IOException ioe) {
                System.err.println("Receive failed: "+ioe.getMessage());
            }

            String sentence = new String( receivePacket.getData());
//            System.out.println("RECEIVED: " + sentence);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            try {
                serverSocket.send(sendPacket);
            } catch (IOException ioe) {
                System.err.println("Send failed: "+ioe.getMessage());
            }
        }
    }
}
