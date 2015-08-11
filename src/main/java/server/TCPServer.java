package server;

import client.Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by alutman on 10-Aug-15.
 */
public class TCPServer extends Server {

	private ServerSocket serverSocket;

    public TCPServer(int port) {
        super(port);
    }

	@Override
	protected void processRequest() {
		try {
            /* ACCEPT INCOMING CONNECTIONS */
            Socket connectionSocket = serverSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            /* READ NEXT REQUEST */
            String clientRequest = inFromClient.readLine();

			String response = clientRequest.toUpperCase();

            /* WRITE RESPONSE */
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            outToClient.writeBytes(response + "\n");

		} catch (IOException e) {
			System.err.println("[TCP Server] receive/send failure. "+e.getClass().getCanonicalName()+":"+e.getMessage());
		}


	}

	@Override
	protected void startListening() {
		this.serverSocket = null;
		try {
			serverSocket = new ServerSocket(this.getPort());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
