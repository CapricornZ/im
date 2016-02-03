package com.gerrydevstory.stockticker;

import java.io.IOException;
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

import demo.im.rs.entity.Ready;
import demo.im.rs.entity.Command;
import demo.im.rs.entity.CommandAdapter;
import demo.im.rs.entity.Message;

public class WebsocketEndPoint extends TextWebSocketHandler {

	static public final String USER = "USER";
	
	private static final Logger logger = LoggerFactory.getLogger(WebsocketEndPoint.class);
	private List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
	private int current = 0;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		logger.debug("connection established......");
		sessions.add(session);
		super.afterConnectionEstablished(session);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		logger.debug("connection({}) lost......", session.getAttributes().get(USER));
		sessions.remove(session);
		super.afterConnectionClosed(session, status);
	}
	
	@Override  
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		logger.info("handleMessage({})", session.getAttributes().get(USER) == null ? "DEFAULT USER" : session.getAttributes().get(USER));
		super.handleTextMessage(session, message);
		
		//Ready ack = new com.google.gson.Gson().fromJson(message.getPayload(), Ready.class);

		Gson gson = new GsonBuilder().registerTypeAdapter(Command.class, new CommandAdapter()).create();
	    Command ack = gson.fromJson(message.getPayload(), Command.class);
	    
		if("READY".equals(ack.getCategory())){//ready
			
			Ready ready = (Ready)ack;
			session.getAttributes().put(USER, ready.getUser());
			logger.info("{} isReady", session.getAttributes().get(USER));
		}
		if("MESSAGE".equals(ack.getCategory())){
          
			logger.info("handleMessage(ECHO)", ack.getCategory());
			Message msg = (Message)ack;
	        TextMessage returnMessage = new TextMessage(message.getPayload());
	        for(WebSocketSession sess : this.sessions)
	        	sess.sendMessage(returnMessage);  
		}
    }
	
	public void broadcast(Command command) throws IOException{
		
		String message = new com.google.gson.Gson().toJson(command);
		TextMessage msg = new TextMessage(message.getBytes());
		for(WebSocketSession session : this.sessions)
			session.sendMessage(msg);
	}
	
	public void send2User(Command command, String user) throws IOException{
		
		String message = new com.google.gson.Gson().toJson(command);
		TextMessage msg = new TextMessage(message.getBytes());
		for(WebSocketSession session : this.sessions)
			if(session.getAttributes().get(USER) != null && session.getAttributes().get(USER).equals(user)) 
				session.sendMessage(msg);
	}
	
	public void dispatch(Command command) throws IOException{
		
		String message = new com.google.gson.Gson().toJson(command);
		TextMessage msg = new TextMessage(message.getBytes());
		if(this.sessions.size() > 0){
			
			int idx = (++this.current) % this.sessions.size();
			WebSocketSession user = this.sessions.get(idx);
			logger.debug("dispatch COMMAND to user:{}", user.getAttributes().get(USER));
			user.sendMessage(msg);
		}
	}
}
