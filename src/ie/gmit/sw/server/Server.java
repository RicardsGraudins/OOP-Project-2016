package ie.gmit.sw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket m_ServerSocket = new ServerSocket(7777,10);
		 int id =0;
		 
		 System.out.println("Server online waiting for connection...");
		 while (true) {
			 Socket clientSocket = m_ServerSocket.accept();
			 ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
			 cliThread.start();
		 }//while
	 }//main
}//Server