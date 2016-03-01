package demo.command;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.captcha.message.Consumer;
import demo.im.rs.entity.Command;
import demo.im.rs.entity.CommandAdapter;
import demo.im.rs.entity.Message;
import demo.im.rs.entity.Ready;
import demo.im.rs.entity.util.TimestampTypeAdapter;

public class SocketHandler extends TextWebSocketHandler{

	private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
	
	private List<WebSocketSession> clients = new ArrayList<WebSocketSession>();
	public List<String> getActiveUsers(){
		
		List<String> rtn = new ArrayList<String>();
		for(WebSocketSession session : this.clients){
			rtn.add((String)session.getAttributes().get("USER"));
		}
		return rtn;
	}
	
	public void send(String user, Command command) throws IOException{
		
		for(int i=0; i<this.clients.size(); i++){
			
			String client = (String)this.clients.get(i).getAttributes().get("USER");
			if(client.equals(user)){
				
				String value = new com.google.gson.Gson().toJson(command);
				TextMessage msg = new TextMessage(value);
				this.clients.get(i).sendMessage(msg);
			}
		}
	}
	
	public void send(List<String> users, Command command) throws IOException {
		
		String value = new com.google.gson.Gson().toJson(command);
		TextMessage msg = new TextMessage(value);
		
		for(WebSocketSession session : this.clients){
			
			String client = (String)session.getAttributes().get("USER");
			if(users.contains(client))
				try{
					session.sendMessage(msg);
				}catch(Exception ex){
					ex.printStackTrace();
				}
		}
	}
	
	public void send(String user, String message) throws IOException {
		
		for(int i=0; i<this.clients.size(); i++){
			
			String client = (String)this.clients.get(i).getAttributes().get("USER");
			if(client.equals(user)){
				
				Message mess = new Message();
				mess.setContent(message);
				String value = new com.google.gson.Gson().toJson(mess);
				TextMessage msg = new TextMessage(value);
				this.clients.get(i).sendMessage(msg);
			}
		}
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		logger.debug("connection established......");
		this.clients.add(session);
		super.afterConnectionEstablished(session);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		logger.debug("connection({}) lost......", session.getAttributes().get(Consumer.USER));
		for(int i=this.clients.size()-1; i>=0; i--){
			
			if(session == this.clients.get(i))
				this.clients.remove(i);
		}
		super.afterConnectionClosed(session, status);
	}
	
	@Override  
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		logger.info("handle Message({})", session.getAttributes().get("USER") == null ? "DEFAULT USER" : session.getAttributes().get("USER"));
		super.handleTextMessage(session, message); 
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Command.class, new CommandAdapter())
				.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
	    Command ack = gson.fromJson(message.getPayload(), Command.class);
	    
	    logger.info("       Message({})", ack.getCategory());
		if("READY".equals(ack.getCategory())){//ready
			
			Ready ready = (Ready)ack;
			logger.info("       USER({})", ready.getUser());
			session.getAttributes().put("USER", ready.getUser());
		}
		else if("MESSAGE".equals(ack.getCategory())){

	        for(WebSocketSession sess : this.clients)
	        	sess.sendMessage(message);
	        logger.info("broadCast \"{}\"", message.getPayload());
		}
	}
}
