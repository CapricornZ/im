package demo.captcha.roundrobin;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.GsonBuilder;

import demo.captcha.IConsumer;
import demo.im.rs.entity.Command;

public class Consumer implements IConsumer {

	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
	public static final String USER = "USER";

	private String user;
	private JmsTemplate jmsTemplate;
	private WebSocketSession session;
	
	private Thread running;
	
	public Consumer(JmsTemplate jms, WebSocketSession session){
		this.jmsTemplate = jms;
		this.session = session;
		this.user = (String)(this.session.getAttributes().get(USER) == null ? "DEFAULT USER" : this.session.getAttributes().get(USER));
	}
	
	@Override
	public String getUser(){ return user; }
	@Override
	public WebSocketSession getSession() { return this.session; }
	
	@Override
	public void ready(){
		this.user = (String)this.session.getAttributes().get(USER);
	}
	
	@Override
	public void stop(){
		
		if( null != running){
			running.interrupt();
		}
	}

	@Override
	public void send(Command command) throws IOException {
		
		GsonBuilder gson = new GsonBuilder();
		gson.setDateFormat("HH:mm:ss");
		String message = gson.create().toJson(command);
		TextMessage msg = new TextMessage(message.getBytes());
		
		synchronized(this){
			logger.debug("dispatch COMMAND to user:{}", this.getUser());
			this.session.sendMessage(msg);
		}
	}

	@Override
	public void send(String message) throws IOException {
		
		TextMessage msg = new TextMessage(message);
		this.session.sendMessage(msg);
	}
}
