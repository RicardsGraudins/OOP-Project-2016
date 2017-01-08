package ie.gmit.sw.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientServiceThread extends Thread {
	  Socket clientSocket;
	  String message;
	  int clientID = -1;
	  boolean running = true;
	  ObjectOutputStream out;
	  ObjectInputStream in;

	  ClientServiceThread(Socket s, int i) {
	    clientSocket = s;
	    clientID = i;
	  }

	  public void sendMessage(String msg) {
			try{
				out.writeObject(msg);
				out.flush();
				System.out.println("client> " + msg);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
	  }
	  public void run() {
	    System.out.println("Accepted Client : ID - " + clientID + " : Address - "
	        + clientSocket.getInetAddress().getHostName());
	    try 
	    {
	    	out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Accepted Client : ID - " + clientID + " : Address - "
			        + clientSocket.getInetAddress().getHostName());
			
			do{
				try
				{
					
					System.out.println("client>"+clientID+"  "+ message);
					//if (message.equals("bye"))
					sendMessage("server got the following: "+message);
					message = (String)in.readObject();
				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
				
	    	}while(!message.equals("bye"));
	      
			System.out.println("Ending Client : ID - " + clientID + " : Address - "
			        + clientSocket.getInetAddress().getHostName());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	}
}