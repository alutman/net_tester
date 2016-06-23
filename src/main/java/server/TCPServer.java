package server;

import java.io.*;
import java.net.*;
import client.Client;
import shared.OutputFormat;

/**
* Created by alutman on 10-Aug-15.
*/
public class TCPServer extends Server {

	private ServerSocket serverSocket;

	public TCPServer(int port, OutputFormat outputFormat) {
		super(port, "TCP", outputFormat);
	}

	@Override
	protected void listenForRequestAndProcess() {
		try {
			/* ACCEPT INCOMING CONNECTIONS */
			Socket connectionSocket = serverSocket.accept();
			logInfo("Connection established from "+connectionSocket.getRemoteSocketAddress().toString());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			/* READ NEXT REQUEST */
			String response = "X";
			try {
				response = inFromClient.readLine();
				logInfo("Message ("+response+") received from "+connectionSocket.getRemoteSocketAddress().toString());
			}
			catch(EOFException e) {
				logException(e, "Could not read fully from client");
			}

			/* WRITE RESPONSE */
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			outToClient.writeBytes(response +"\n");
			logInfo("Sent message ("+response+") to "+connectionSocket.getRemoteSocketAddress().toString());

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
