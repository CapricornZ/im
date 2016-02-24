package demo.captcha.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.im.rs.entity.Captcha;

public class CaptchaConvert implements MessageConverter {

	private Gson gson;
	
	public CaptchaConvert(){
		
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.setDateFormat("HH:mm:ss");
		this.gson = gsonB.create();
	}

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		
		Captcha captcha = (Captcha)object;
		String content = this.gson.toJson(captcha);
		Message message = session.createObjectMessage(content);
		return message;
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		
		ObjectMessage objMessage = (ObjectMessage) message;
		String content = (String) objMessage.getObject();
		return content;
	}
}
