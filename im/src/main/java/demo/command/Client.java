package demo.command;

import java.io.IOException;
import java.sql.Timestamp;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.im.rs.entity.Command;
import demo.im.rs.entity.util.TimestampTypeAdapter;

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
		
		Gson gson = new GsonBuilder().
				registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").
				create();
		String value = gson.toJson(command);
		TextMessage msg = new TextMessage(value);
		session.sendMessage(msg);
	}
	
	public void send(TextMessage message) throws IOException{
		
		this.session.sendMessage(message);
	}
}
