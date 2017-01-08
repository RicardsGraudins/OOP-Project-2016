package ie.gmit.sw.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

	  //send string to client
	  public void sendMessage(String msg) {
			try{
				out.writeObject(msg);
				out.flush();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}//catch
	  }//sendMessage
	  
	  //send file to client
	  public void sendFileMessage(File msg) {
			try{
				out.writeObject(msg);
				out.flush();
			}//try
			catch(IOException ioException){
				ioException.printStackTrace();
			}//catch
	  }//sendFileMessage
	  
	//display a list of files available for download to the client
	  public void listFiles() {
		  File f = null;
		  File[] paths;
		  
		  try {
			  //location that stores files
			  f = new File("c:/myfiles");
			  
			  //returns pathnames for files
			  paths = f.listFiles();
			  
			  //print out pathnames
			  for (File path:paths){
				  sendFileMessage(path);
			  }//for
			  
		  } catch(Exception e) {
			  e.printStackTrace();
		  }//catch
	  }//listFiles
	  
	  //reference http://stackoverflow.com/questions/4687615/how-to-achieve-transfer-file-between-client-and-server-using-java-socket
	  //tried several approaches which work fine on their own but not quite for this project - empty file
	  public void downloadFile() {
		  //String fileToSend = "C:\\test.txt";
		  BufferedOutputStream outToClient = null;
		  
		  ServerSocket welcomeSocket = null;
		  Socket connectionSocket = null;
		  
		  //recieve file name for fileToSend
		  String fileToSend = "";
		try {
			fileToSend = (String)in.readObject();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}//try
		  
		  
		  try {
			welcomeSocket = new ServerSocket(8888);
			connectionSocket = welcomeSocket.accept();
			outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
		  } catch (IOException e) {
				
		  }//catch
		  
		  if (outToClient != null) {
			  File myFile = new File(fileToSend);
			  byte[] mybytearray = new byte[(int) myFile.length()];
				
			  FileInputStream fis = null;
				
			  try {
				  fis = new FileInputStream(myFile);
			  } catch (FileNotFoundException ex) {
					
			  }//catch
			  BufferedInputStream bis = new BufferedInputStream(fis);
			  
			  try {
				  bis.read(mybytearray, 0, mybytearray.length);
				  outToClient.write(mybytearray, 0, mybytearray.length);
				  outToClient.flush();
				  outToClient.close();
				  connectionSocket.close();
	
			  } catch (IOException ex) {
				  // Do exception handling
			  }//catch
		  }//if
	  }//downloadFile
	  
	  /*
	  public void downloadFile() {
		  String fileToSend = "C:\\test.txt";
		  BufferedOutputStream outToClient = null;
		  
		  try {
			outToClient = new BufferedOutputStream(clientSocket.getOutputStream());
		  } catch (IOException e) {
				
		  }//catch
		  
		  if (outToClient != null) {
			  File myFile = new File(fileToSend);
			  byte[] mybytearray = new byte[(int) myFile.length()];
				
			  FileInputStream fis = null;
				
			  try {
				  fis = new FileInputStream(myFile);
			  } catch (FileNotFoundException ex) {
					
			  }//catch
			  BufferedInputStream bis = new BufferedInputStream(fis);
			  
			  try {
				  bis.read(mybytearray, 0, mybytearray.length);
				  outToClient.write(mybytearray, 0, mybytearray.length);
				  outToClient.flush();
				  //outToClient.close();
	
			  } catch (IOException ex) {
				  // Do exception handling
			  }//catch
		  }//if
	  }//downloadFile
	  */
	  
	  /*
	  public void downloadFile() throws ClassNotFoundException, IOException {
		  FileInputStream fis = null;
		  BufferedInputStream bis = null;
		  OutputStream os = null;
		  //String file_to_send;
		  
		  //file_to_send = (String)in.readObject();
		  String file_to_send = "c:/test2/test2.txt";
		  File myFile = new File (file_to_send);
		  
		  byte [] myByteArray = new byte [(int)myFile.length()];
		  fis = new FileInputStream(myFile);
		  bis = new BufferedInputStream(fis);
		  bis.read(myByteArray,0,myByteArray.length);
		  os = clientSocket.getOutputStream();
		  System.out.println("Sending " + file_to_send + "(" + myByteArray.length + "bytes)");
		  os.write(myByteArray, 0, myByteArray.length);
		  os.flush();
		  System.out.println("File Sent");
		  bis.close();
	  }//downloadFile
	  */
	  
	  //testing if correct information added to logfile by deserializing logs.txt
	  //adding to an ArrayList output consisting of DateObjects and printing to console
	  public void outputFileContents() {
		  try {
			FileInputStream fis = new FileInputStream("log.txt");
			ObjectInputStream ois = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			ArrayList<DateObject> output = (ArrayList<DateObject>) ois.readObject();
			
			//print out contents
			for (DateObject h : output){
				System.out.println(h.toString());
			}//for
			
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}//catch
	  }//outputFileContent
	  
	  public void run() {
	    //System.out.println("Accepted Client : ID - " + clientID + " : Address - "
	    //    + clientSocket.getInetAddress().getHostName());
		  
		  //Creating an ArrayList logs of object DateObject
		  //Whenever a request is made, new DateObject is created and added to ArrayList logs
		  //When the client disconnects write the arraylist to file logs.txt
		  ArrayList<DateObject> logs = new ArrayList<DateObject>();
		  String command = "[INFO] the following ipaddress connected";
		  String clientId = "127.0.0.1";
		  LocalDateTime now = LocalDateTime.now();
		  
		  //client must connect to server first therefore the following object is always added first
		  DateObject date = new DateObject(command, clientId, now);
		  logs.add(date);
		  
		  try 
		  {
	    	out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("\nAccepted Client : ID - " + clientID + " : Address - "
			        + clientSocket.getInetAddress().getHostName());
			
			sendMessage("Connected to 127.0.0.1 in port 7777");
			//while loop here 1-4
			int choice = (int)in.readObject();
			while (choice != 4){
				switch (choice){
				case 1:
					//do nothing, client already connected to server
					//outputFileContents(); //checking if outPutFileContents works
					choice = (int)in.readObject();
					break;
				case 2:
					//execute print file listing
					listFiles();
					//change command and now strings, create object and add to arraylist for list files
					command = "[INFO] listing requested by";
					now = LocalDateTime.now();
					DateObject date1 = new DateObject(command, clientId, now);
					logs.add(date1);
					choice = (int)in.readObject();
					break;
				case 3:
					//execute download file
					downloadFile();
					//change command and now strings, create object and add to arraylist for download file
					command = "[INFO] download file requested by";
					now = LocalDateTime.now();
					DateObject date2 = new DateObject(command, clientId, now);
					logs.add(date2);
					choice = (int)in.readObject();
					break;
				default:
					choice = (int)in.readObject();
					break;
				}//switch
			}//while
			
			//change command and now strings, create object and add to arraylist for disconnect
			command = "[INFO] the following ipaddress disconnected";
			now = LocalDateTime.now();
			DateObject date3 = new DateObject(command, clientId, now);
			logs.add(date3);
			
		   //Write arraylist to file logs.txt
		   FileOutputStream fout = new FileOutputStream("log.txt");
		   ObjectOutputStream oos = new ObjectOutputStream(fout);
		   oos.writeObject(logs);
		   
		   oos.close();
	      
			System.out.println("\nEnding Client : ID - " + clientID + " : Address - "
			        + clientSocket.getInetAddress().getHostName());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }//catch
	}//run
}//ClientServiceThread