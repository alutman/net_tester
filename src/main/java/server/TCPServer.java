package server;

import java.io.*;
import java.net.*;
import client.Client;

/**
* Created by alutman on 10-Aug-15.
*/
public class TCPServer extends Server {

	private ServerSocket serverSocket;

	public TCPServer(int port) {
		super(port, "TCP");
	}

	@Override
	protected void listenForRequestAndProcess() {
		try {
			/* ACCEPT INCOMING CONNECTIONS */
			Socket connectionSocket = serverSocket.accept();
			DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());

			/* READ NEXT REQUEST */
			String response = "default";
			try {
				byte[] clientRequest = new byte[Client.MESSAGE_SIZE];
				inFromClient.readFully(clientRequest);
				response = new String(clientRequest);
			}
			catch(EOFException e) {
				logException(e, "Could not read fully from client");
			}

			/* WRITE RESPONSE */
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			outToClient.writeBytes(response );

		} catch (IOException e) {
			logException(e, "Receive/send failure");
		}


	}

	@Override
	protected void setupServer() {
		this.serverSocket = null;
		try {
			serverSocket = new ServerSocket(this.getPort());
		} catch (Exception e) {
			logException(e, "Setup failed");
			this.terminate();
		}
	}

}
