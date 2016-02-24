package demo.captcha;

import java.io.IOException;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.socket.WebSocketSession;

import demo.im.rs.entity.Captcha;

/**
 * 验证码处理器
 * @author martin
 */
public abstract class IProcessor {

	protected JmsTemplate responseReceiver;
	public void setResponseReceiver(JmsTemplate jms){ this.responseReceiver = jms; }
	
	protected IRepository consumerRepo;
	public void setConsumerRepo(IRepository repo){ this.consumerRepo = repo; }
	
	public abstract void dispatch(Captcha captcha) throws IOException, Exception;
	public abstract IConsumer createConsumer(JmsTemplate jms, WebSocketSession session);
	
	public String waitResp(String uid){
		
		String selector = String.format("from='%s'", uid);
		Object message = this.responseReceiver.receiveSelectedAndConvert(selector);
		return (String)message;
	}
}
