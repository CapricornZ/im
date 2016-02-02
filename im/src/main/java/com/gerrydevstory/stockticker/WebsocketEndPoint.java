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

import demo.im.rs.entity.Ack;
import demo.im.rs.entity.Command;

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

		logger.debug("connection lost......");
		sessions.remove(session);
		super.afterConnectionClosed(session, status);
	}
	
	@Override  
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		logger.info("handleMessage({})", session.getAttributes().get(USER) == null ? "DEFAULT USER" : session.getAttributes().get(USER));
		super.handleTextMessage(session, message);
		
		Ack ack = new com.google.gson.Gson().fromJson(message.getPayload(), Ack.class);
		if("READY".equals(ack.getCategory())){//ready
			
			session.getAttributes().put(USER, ack.getUser());
			logger.info("{} isReady", session.getAttributes().get(USER));
			
		} else {//echo
          
			logger.info("handleMessage(ECHO)", ack.getCategory());
			//String message1 = new com.google.gson.Gson().toJson(command);
			//TextMessage msg = new TextMessage(message.getBytes());
	        TextMessage returnMessage = new TextMessage(message.getPayload()+" received at server");  
	        session.sendMessage(returnMessage);  
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
