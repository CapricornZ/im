package demo.captcha.firstready;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.GsonBuilder;

import demo.captcha.IConsumer;
import demo.im.rs.entity.Command;

public class Consumer implements IConsumer {
	
	class Run extends Thread{
		
		private Consumer consumer;
		public Run(Consumer consumer){
			this.consumer = consumer;
		}
		
		@Override
		public void run() {
			
			if(this.consumer.lock.tryLock()){
				try{
					
					this.consumer.running = this;
					logger.info("READY! wating for CAPTCHA ...");
					String message = (String)jmsTemplate.receiveAndConvert();
					logger.info("       got CAPTCHA!");

					logger.info("       send CAPTCHA to {}", user);
					TextMessage msg = new TextMessage(message.getBytes());
					session.sendMessage(msg);
				
				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					this.consumer.lock.unlock();
				}
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
	public static final String USER = "USER";

	private String user;
	private JmsTemplate jmsTemplate;
	private WebSocketSession session;
	private final ReentrantLock lock = new ReentrantLock();
	
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
		Run run = new Run(this);
		run.setName(this.user);
		run.start();
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
		synchronized(this){
			this.session.sendMessage(msg);
		}
	}
}