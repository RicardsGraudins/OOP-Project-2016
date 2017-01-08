package ie.gmit.sw.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Client implements ClientInterface {
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	String message="";
 	String ipaddress;
 	Scanner stdin;
 	Client(){}

	public void run()
	{
		stdin = new Scanner(System.in);
		try{
			//1. creating a socket to connect to the server
			
			//System.out.println("\nPlease Enter your IP Address");
			//ipaddress = stdin.next();
			//requestSocket = new Socket(ipaddress, 7777);
			//System.out.println("Connected to "+ipaddress+" in port 7777");
			
		   //getting server ip and port number from config.xml
		   String serverHost = "";
		   String portNumber = "";
		   @SuppressWarnings("unused")
		   String downloadDir = "";
		      try {	
		          File inputFile = new File("config.xml");
		          DocumentBuilderFactory dbFactory 
		             = DocumentBuilderFactory.newInstance();
		          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		          Document doc = dBuilder.parse(inputFile);
		          doc.getDocumentElement().normalize();

		          NodeList nList = doc.getElementsByTagName("client-config");
		         
		          for (int temp = 0; temp < nList.getLength(); temp++) {
		             Node nNode = nList.item(temp);

		             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		                Element eElement = (Element) nNode;

		                //System.out.println("Server-Host : " 
		                //   + eElement
		                //   .getElementsByTagName("server-host")
		                //   .item(0)
		                //   .getTextContent());
		                
		                serverHost = eElement.getElementsByTagName("server-host").item(0).getTextContent();
		                
		               // System.out.println("Server-Port : " 
		               // + eElement
		               //    .getElementsByTagName("server-port")
		               //    .item(0)
		               //    .getTextContent());
		                
		                portNumber = eElement.getElementsByTagName("server-port").item(0).getTextContent();
		                
		                downloadDir = eElement.getElementsByTagName("download-dir").item(0).getTextContent();
		             }//if
		          }//for
		       } catch (Exception e) {
		          e.printStackTrace();
		       }//catch
			      
		      int portNumberInt = Integer.parseInt(portNumber);
		      requestSocket = new Socket(serverHost, portNumberInt);
		      //System.out.println("Connected to " + serverHost + " in port " + portNumberInt);
			
			
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
			do{
				try
				{
						
						message = (String)in.readObject();
						System.out.println("Please Enter the Message to send...");
						message = stdin.next();
						sendMessage(message);
						
						
						
				}//try
				catch(ClassNotFoundException classNot)
				{
					System.err.println("data received in unknown format");
				}//catch
			}while(!message.equals("bye"));
		}//try
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}//catch
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}//catch
		}//finally
	}//run
	
	public void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}//try
		catch(IOException ioException){
			ioException.printStackTrace();
		}//catch
	}//sendMessage
	
	public static void menu(){
		System.out.println("1. Connect to Server");
		System.out.println("2. Print File Listing");
		System.out.println("3. Download File");
		System.out.println("4. Quit");
		
		System.out.print("\nType Option [1-4]> ");
	}//menu
 	
 	
	public static void main(String[] args) {
		Client client = new Client();
		int choice;
		Scanner console;
		console = new Scanner(System.in);
		
		menu();
		choice = console.nextInt();
		while (choice != 4){
			switch (choice){
			case 1:
				System.out.println("Connecting to server..");
				client.run();
				choice = 4;
				break;
			case 2:
				System.out.println("\nCannot print file listing without server connection.");
				System.out.print("Type Option [1-4]> ");
				choice = console.nextInt();
				break;
			case 3:
				System.out.println("\nCannot download file without server connection.");
				System.out.print("Type Option [1-4]> ");
				choice = console.nextInt();
				break;
			case 4:
				System.out.println("Exiting..");
				choice = 4;
				break;
			default:
				System.out.println("\nInvalid input");
				System.out.print("Type Option [1-4]> ");
				choice = console.nextInt();
				break;
			}//switch
		}//while
		console.close();
	}//main
}//WebClient