package demo.command;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.im.rs.entity.Command;
import demo.im.rs.entity.HeartBeat;
import demo.im.rs.entity.util.TimestampTypeAdapter;

public class Client {
	public static String USER = "USER";
	
	private String user;
	private Date lastPing;
	private String lastMessage;
	private WebSocketSession session;
	
	private Gson gson = new GsonBuilder().
			registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").
			create();
	
	public Client(WebSocketSession session, String user){
		this.user = user;
		this.session = session;
	}
	
	public String getUser(){ return this.user; }
	public WebSocketSession getSession(){ return this.session; }
	public Date getLastPing() { return this.lastPing; }
	public String getLastMessage() { return this.lastMessage; }
	public boolean getIsOnline(){
		
		if(null != this.lastPing){
			long sec = new Date().getTime() - this.lastPing.getTime();
			if(sec <= 30000)
				return true;
		}
		return false;
	}
	
	public void send(Command command) throws IOException{
		
		String value = this.gson.toJson(command);
		TextMessage msg = new TextMessage(value);
		session.sendMessage(msg);
	}
	
	public void send(TextMessage message) throws IOException{
		
		this.session.sendMessage(message);
	}
	
	public void process(HeartBeat hb){
		
		this.lastMessage = hb.getContent();
		this.lastPing = hb.getTime();
	}
}
