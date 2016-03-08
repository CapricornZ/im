package demo.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.im.rs.entity.Command;
import demo.im.rs.entity.CommandAdapter;
import demo.im.rs.entity.HeartBeat;
import demo.im.rs.entity.Ready;
import demo.im.rs.entity.util.TimestampTypeAdapter;

public class SocketHandler extends TextWebSocketHandler{

	private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
	
	private List<Client> clients = new ArrayList<Client>();
	
	public List<Client> getActiveUsers(){
		
		return this.clients;
	}
	
	public void removeUser(String sessionID){
		
		Client found = null;
		for(int i=0; found == null && i<this.clients.size(); i++){
			
			if(this.clients.get(i).getSession().getId().equals(sessionID))
				found = this.clients.get(i);
		}
		if(found != null){
			
			try {
				found.getSession().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.clients.remove(found);
		}
	}

	@Override
	protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {

		logger.debug("Pong Message......");
		super.handlePongMessage(session, message);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

		logger.debug("transport Error......");
		if(this.clients.contains(session))
			this.clients.remove(session);
		
		super.handleTransportError(session, exception);
	}
	
	private String getUser(WebSocketSession session){
		
		HttpHeaders headers = session.getHandshakeHeaders();
		String userAgent = headers.get("user-agent").get(0);
		List<String> cookies = headers.get("cookie");
		String user = userAgent;
		if(null != cookies){
			for(String cookie : cookies){
				String[] kv = cookie.split("=");
				if(kv[0].equals(Client.USER))
					user = kv[1];
				logger.debug("cookie:{}", cookie);
			}
		}
		return user;
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		logger.debug(">>>>>connection established<<<<<");
		String user = this.getUser(session);

		this.clients.add(new Client(session, user));
		super.afterConnectionEstablished(session);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		logger.debug(">>>>>connection lost<<<<<");
		String user = this.getUser(session);
		
		for(int i=this.clients.size()-1; i>=0; i--){
			
			if(session == this.clients.get(i).getSession())
				this.clients.remove(i);
		}
		super.afterConnectionClosed(session, status);
	}
	
	@Override  
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		String user = this.getUser(session);
		logger.info("handle Message({})", user);
		super.handleTextMessage(session, message); 
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Command.class, new CommandAdapter())
				.registerTypeAdapter(Date.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
	    Command ack = gson.fromJson(message.getPayload(), Command.class);
	    
	    logger.info("       Message({})", ack.getCategory());
		if("READY".equals(ack.getCategory())){//ready
			
			Ready ready = (Ready)ack;
			logger.info("       USER({})", ready.getUser());
			session.getAttributes().put("USER", ready.getUser());
		}
		else if("MESSAGE".equals(ack.getCategory())){

	        for(Client client : this.clients)
	        	client.send(message);
	        logger.info("       broadCast \"{}\"", message.getPayload());
		}else if("HEARTBEAT".equals(ack.getCategory())){
			
			logger.info("       receive HEART BEAT from \"{}\"", user);
			Client client = this.findSession(session);
			if(client != null){

				client.process((HeartBeat)ack);
				client.send(new HeartBeat());
			}
		}
	}
	
	private Client findSession(String user){
		
		Client found = null;
		for(int i=0; found==null && i<this.clients.size(); i++){
			if(user.equals(this.clients.get(i).getUser()))
				found = this.clients.get(i);
		}
		return found;
	}
	
	private Client findSession(WebSocketSession session){
		
		Client found = null;
		for(int i=0; found==null && i<this.clients.size(); i++){
			if(session == this.clients.get(i).getSession())
				found = this.clients.get(i);
		}
		return found;
	}
	
	public void send(String user, Command command) throws IOException{
		
		Client client = this.findSession(user);
		if(null != client)
			client.send(command);
	}
	
	public void send(List<String> users, Command command) throws IOException{
		
		for(Client client : this.clients)
			if(users.contains(client.getUser())){
				client.send(command);
			}
	}
}
