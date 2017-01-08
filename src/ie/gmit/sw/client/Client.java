package ie.gmit.sw.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
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
			//3: communicating with the server
			
			try {
				
				message = (String)in.readObject();
				System.out.println(message);
				//while loop recieve data
				menu();
				int choice = stdin.nextInt();
				sendInt(choice);
				while (choice != 4){
					switch (choice){
					case 1:
						System.out.println("Already connected to the server!");
						menu();
						choice = stdin.nextInt();
						sendInt(choice);
						break;
					case 2:
						//recieve file listing
						listFiles();
						menu();
						choice = stdin.nextInt();
						sendInt(choice);
						break;
					case 3:
						//recieve download file
						downloadFile();
						menu();
						choice = stdin.nextInt();
						sendInt(choice);
						break;
					default:
						System.out.println("Invalid");
						menu();
						choice = stdin.nextInt();
						sendInt(choice);
						break;
					}//switch
				}//while
				
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}//catch
			
		}//try
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}//catch
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
				System.out.println("Disconnected.");
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}//catch
		}//finally
	}//run
	
	//send string to server
	public void sendMessage(String msg) {
		try{
			out.writeObject(msg);
			out.flush();
			//System.out.println("client>" + msg);
		}//try
		catch(IOException ioException){
			ioException.printStackTrace();
		}//catch
	}//sendMessage
	
	//send int to server
	public void sendInt(int num) {
		try {
			out.writeObject(num);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}//catch
	}//sendInt
	
	//a quick menu to avoid redundant code
	public static void menu(){
		System.out.println("\n1. Connect to Server");
		System.out.println("2. Print File Listing");
		System.out.println("3. Download File");
		System.out.println("4. Quit");
		
		System.out.print("\nType Option [1-4]> ");
	}//menu
	
	//display a list of files available to download from the server
	public void listFiles() {
		int numOfFiles = 3;
		
			try {
				System.out.println("\n  Files:");
				System.out.println("===========");
				for (int i = 0; i < numOfFiles; i++) {
					File fileContents = (File)in.readObject();
					System.out.println(fileContents);
				}//for
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}//catch
	}//listFiles
	
	
	//download a file available on the server
	public void downloadFile() {
		String fileOutput = "downloads\testout.txt";
		byte[] aByte = new byte[1];
		int bytesRead;
		
		InputStream is = null;
		Socket clientSocket = null;
		
		//send file to download from list
		System.out.print("Download file: ");
		message = stdin.next();
		sendMessage(message);
		
		try {
			clientSocket = new Socket ("127.0.0.1", 8888);
			is = clientSocket.getInputStream();
			
		} catch (IOException ex){
			
		}//catch
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		if (is != null) {
			
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			
			try {
				fos = new FileOutputStream(fileOutput);
				bos = new BufferedOutputStream(fos);
				bytesRead = is.read(aByte, 0, aByte.length);
				
				do {
					baos.write(aByte);
					bytesRead = is.read(aByte);
				} while (bytesRead != -1);
				
				bos.write(baos.toByteArray());
				bos.flush();
				bos.close();
				clientSocket.close();
			} catch (IOException ex) {
				
			}//catch
		}//if
	}//downloadFile
	
	/*
	void downloadFile() {
		String fileOutput = "C:\\testout.txt";
		byte[] aByte = new byte[1];
		int bytesRead;
		
		InputStream is = null;
		
		try {
			is = requestSocket.getInputStream();	
			
		} catch (IOException ex){
			
		}//catch
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		if (is != null) {
			
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			
			try {
				fos = new FileOutputStream(fileOutput);
				bos = new BufferedOutputStream(fos);
				bytesRead = is.read(aByte, 0, aByte.length);
				
				do {
					baos.write(aByte);
					bytesRead = is.read(aByte);
				} while (bytesRead != -1);
				
				bos.write(baos.toByteArray());
				bos.flush();
				//bos.close();
			} catch (IOException ex) {
				
			}//catch
		}//if
	}//downloadFile
	*/
	
	/*
	void downloadFile() throws IOException {
		//file client wishes to download from listFiles
		//System.out.print("Download file: ");
		//String downloadFile = stdin.next();
		//sendMessage(downloadFile);
		
		//System.out.println("Saving file to: ");
		//String file_to_recieved = stdin.next();
		String file_to_recieved = "c:/myfiles/test4.txt";
		
	    int bytesRead;
	    int current = 0;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    int file_size = 60022386;
	    
	    byte [] myByteArray  = new byte [file_size];

	    InputStream is = requestSocket.getInputStream();
	    
	      fos = new FileOutputStream(file_to_recieved);
	      bos = new BufferedOutputStream(fos);
	      bytesRead = is.read(myByteArray,0,myByteArray.length);
	      current = bytesRead;
	      
	      do {
	          bytesRead =
	             is.read(myByteArray, current, (myByteArray.length-current));
	          if(bytesRead >= 0) current += bytesRead;
	       } while(bytesRead > -1);

	       bos.write(myByteArray, 0 , current);
	       bos.flush();
	       System.out.println("File " + file_to_recieved
	           + " downloaded (" + current + " bytes read)");
	       
	       //fos.close();
	       bos.close();
	}//downloadFile
	*/
 	
 	//Starting point for client, display menu into while loop until connect to server is
	//selected and client.run starts, multiple clients can connect to server simultaneously
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
				System.out.println("\nConnecting to server..");
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

