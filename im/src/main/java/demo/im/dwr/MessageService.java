package demo.im.dwr;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class MessageService implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
	
	private ApplicationContext ctx;  
	@Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {  
        this.ctx = ctx;  
    }
	
	private JmsTemplate jmsTemplate;
	public void setJmsTemplate(JmsTemplate jms){ this.jmsTemplate = jms; }
	
	//public void sendRequest(Message msg) {
		//ctx.publishEvent(new MessageEvent(msg));
    //}
	private ChatManager chatManager;
	public void setChatManager(ChatManager value){ this.chatManager = value; }
	
	public void sendRequest(Message msg) {
    	
		List<MessageDwr> users = this.chatManager.getUsers();
    	if(users.size() > 0){
    		MessageDwr user = users.remove(0);
    		ScriptSession session = this.chatManager.getScriptSession(user.getByid());
    		
            java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String s = format1.format(msg.getTime());

            ScriptBuffer sb = new ScriptBuffer();
            sb.appendScript(String.format("showMessage({captcha: '%s'", msg.getCaptcha()))
            	.appendScript(String.format(", time: '%s'", s))
            	.appendScript(String.format(", tip: '%s'", msg.getTip()))
            	.appendScript(String.format(", id: '%s'", msg.getId()))
            	.appendScript("})");
            
            session.addScript(sb);
            logger.info("showMessage({msg, tip, time, id}) to " + session.getAttribute("userid"));
    	}
    }

	public void sendResponse(final String captcha, final String uid){
		
		MessageCreator messageCreator = new MessageCreator() {
			@Override
			public javax.jms.Message createMessage(Session session) throws JMSException {
				
				TextMessage message = session.createTextMessage();
				message.setText(captcha);
				message.setStringProperty("from", uid);
				return message;
			}
		}; 
		this.jmsTemplate.send(messageCreator);
	}
	
	public String waitResponse(String uid){
		
		String selector = String.format("from='%s'", uid);
		Object message = this.jmsTemplate.receiveSelectedAndConvert(selector);
		return (String)message;
	}
}
