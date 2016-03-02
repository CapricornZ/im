package demo.captcha;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.captcha.IConsumer;
import demo.captcha.IProcessor;
import demo.captcha.IRepository;
import demo.captcha.message.Consumer;
import demo.im.rs.entity.Command;
import demo.im.rs.entity.CommandAdapter;
import demo.im.rs.entity.Ready;
import demo.im.rs.entity.Reply;
import demo.im.rs.entity.Retry;
import demo.im.rs.entity.util.TimestampTypeAdapter;

public class SocketHandler extends TextWebSocketHandler implements IRepository, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
	
	private ApplicationContext ctx;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}
	
	private IProcessor processor;
	public void setProcessor(IProcessor processor){ 
		this.processor = processor;
		this.processor.setConsumerRepo(this);
	}
	
	private JmsTemplate jmsTemplate;
	public void setJmsReply(JmsTemplate jms){
		this.jmsTemplate = jms;
	}
	
	private int current = 0;
	private List<IConsumer> clients = new ArrayList<IConsumer>();
	
	@Override
	public List<String> getActiveUsers(){
		
		List<String> rtn = new ArrayList<String>();
		for(IConsumer consumer : this.clients)
			rtn.add(consumer.getUser());
		return rtn;
	}
	
	@Override
	public IConsumer next() {
		
		if(this.clients.size() != 0){
			int idx = (++this.current) % this.clients.size();
			return this.clients.get(idx);
		} else
			return null;
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		logger.debug("connection established......");
		JmsTemplate jmsTemplate = (JmsTemplate)this.ctx.getBean("jmsTemplate.request.consumer");
		IConsumer client = this.processor.createConsumer(jmsTemplate, session);
		this.clients.add(client);
		super.afterConnectionEstablished(session);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		logger.debug("connection({}) lost......", session.getAttributes().get(Consumer.USER));
		for(int i=this.clients.size()-1; i>=0; i--){
			IConsumer client = (IConsumer)this.clients.get(i);
			if(client.getUser().equals(session.getAttributes().get(Consumer.USER))){
				this.clients.remove(i);
				client.stop();
			}
		}
		super.afterConnectionClosed(session, status);
	}
	
	@Override  
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		logger.info("handle Message({})", session.getAttributes().get(Consumer.USER) == null ? "DEFAULT USER" : session.getAttributes().get(Consumer.USER));
		super.handleTextMessage(session, message); 
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Command.class, new CommandAdapter())
				.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
	    Command ack = gson.fromJson(message.getPayload(), Command.class);
	    
	    logger.info("       Message({})", ack.getCategory());
		if("READY".equals(ack.getCategory())){//ready
			
			Ready ready = (Ready)ack;
			session.getAttributes().put(Consumer.USER, ready.getUser());
			for(IConsumer consumer : this.clients){
				if(consumer.getSession().equals(session))
					consumer.ready();
			}
		}
		if("REPLY".equals(ack.getCategory())){//reply
			
			final Reply reply = (Reply)ack;
			logger.info("receive Response {CAPTCHA:'{}', TO:'{}'} by OPERATOR:'{}'", reply.getCode(), reply.getUid(), reply.getFrom());
			
			MessageCreator messageCreator = new MessageCreator() {
				@Override
				public javax.jms.Message createMessage(Session session) throws JMSException {
					
					javax.jms.TextMessage message = session.createTextMessage();
					message.setText(new com.google.gson.Gson().toJson(reply));
					message.setStringProperty("from", reply.getUid());
					return message;
				}
			};
			this.jmsTemplate.send(messageCreator);
		}
		if("RETRY".equals(ack.getCategory())){
			
			final Retry retry = (Retry)ack;
			logger.info("receive RETRY by OPERATOR:'{}'", retry.getFrom());
			
			MessageCreator messageCreator = new MessageCreator() {
				@Override
				public javax.jms.Message createMessage(Session session) throws JMSException {
					
					javax.jms.TextMessage message = session.createTextMessage();
					message.setText(new com.google.gson.Gson().toJson(retry));
					message.setStringProperty("from", retry.getUid());
					return message;
				}
			};
			this.jmsTemplate.send(messageCreator);
		}
		if("MESSAGE".equals(ack.getCategory())){

	        for(IConsumer consumer : this.clients)
	        	consumer.send(message.getPayload());
	        logger.info("broadCast \"{}\"", message.getPayload());
		}
    }

}
