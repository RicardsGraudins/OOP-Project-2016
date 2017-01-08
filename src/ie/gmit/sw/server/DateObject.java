package ie.gmit.sw.server;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DateObject implements Serializable {

	private static final long serialVersionUID = 7086064711747023615L;
	private String command;
	private LocalDateTime now;
	private String clientId;

	public DateObject(String command, String clientId, LocalDateTime now){
		this.command = command;
		this.clientId = clientId;
		this.now = now;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public LocalDateTime getNow() {
		return now;
	}

	public void setNow(LocalDateTime now) {
		this.now = now;
	}
	
	public String toString() {
		String outputString = String.format("%s %s %s %s", command, clientId, "at", now);
		return outputString;
	}
}
	