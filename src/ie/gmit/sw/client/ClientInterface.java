package ie.gmit.sw.client;

public interface ClientInterface {
	public void run();
	
	public void listFiles();
	
	public void downloadFile();
	
	public void sendMessage(String msg);
	
	public void sendInt(int num);
	
	public static void menu(){};
}
