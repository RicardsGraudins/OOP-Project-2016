package ie.gmit.sw.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	 //multiple clients can connect to server simultaneously
	 public static void main(String[] args) throws Exception {
		 
		System.out.println("Server online, waiting for connections..");
		@SuppressWarnings("resource")
		ServerSocket m_ServerSocket = new ServerSocket(7777,10);
	    int id =0;
		 while (true) {
			 Socket clientSocket = m_ServerSocket.accept();
			 ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
			 cliThread.start();
		 }//while
	 }//main
}//Server

