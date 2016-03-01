package demo.command;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import demo.im.rs.entity.Command;

public class Client {
	public static String USER = "USER";
	
	private String user;
	private WebSocketSession session;
	
	public Client(WebSocketSession session, String user){
		this.user = user;
		this.session = session;
	}
	
	public String getUser(){ return this.user; }
	public WebSocketSession getSession(){ return this.session; }
	
	public void send(Command command) throws IOException{
		
		String value = new com.google.gson.Gson().toJson(command);
		TextMessage msg = new TextMessage(value);
		session.sendMessage(msg);
	}
	
	public void send(TextMessage message) throws IOException{
		
		this.session.sendMessage(message);
	}
}
