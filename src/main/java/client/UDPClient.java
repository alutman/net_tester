package client;

import java.io.IOException;
import java.net.*;

/**
 * Created by alutman on 10-Aug-15.
 */
public class UDPClient implements Runnable{

    private int port;
    private String address;
    private Long timeout;

    public UDPClient(String address, int port, Long timeout) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        while(true) {


            InetAddress IPAddress = null;
            try {
                IPAddress = InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "ClientMessage"+System.currentTimeMillis();
            sendData = sentence.getBytes();

            DatagramSocket clientSocket = null;
            try {
                clientSocket = new DatagramSocket();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

            Long startTime = System.currentTimeMillis();
            try {
//                System.out.println("Sending");
                clientSocket.send(sendPacket);
            } catch (IOException ioe) {
                System.err.println("Send failed: " + ioe.getMessage());
            }

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                clientSocket.receive(receivePacket);
            } catch (IOException ioe) {
                System.err.println("Receive failed: " + ioe.getMessage());
            }
            Long endTime = System.currentTimeMillis();

            System.out.println("Ping: "+(endTime-startTime)+" ms");

            String modifiedSentence = new String(receivePacket.getData());
//            System.out.println("FROM SERVER:" + modifiedSentence);
            clientSocket.close();

            try {
                Thread.sleep(this.timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
