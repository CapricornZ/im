package com.gerrydevstory.stockticker;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.im.rs.entity.Captcha;

public class ConsumerMessageListener implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerMessageListener.class);
	
	private com.google.gson.Gson gson = new com.google.gson.Gson();
	
	@Override
	public void onMessage(Message message) {
		
		logger.info("new message received");
		if(message instanceof ObjectMessage){
			
			ObjectMessage objMsg = (ObjectMessage)message;
			try {
				String content = (String)objMsg.getObject();
				Captcha captcha = this.gson.fromJson(content, Captcha.class);
				logger.info("UID:{}",captcha.getUid());
				
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

	}

}
