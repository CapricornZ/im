package demo.captcha.roundrobin;

import java.io.IOException;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.socket.WebSocketSession;

import demo.captcha.IConsumer;
import demo.captcha.IProcessor;
import demo.captcha.firstready.Consumer;
import demo.im.rs.entity.Captcha;

/**
 * 轮询请求
 * @author martin
 *
 */
public class Processor extends IProcessor{
	
	@Override
	public IConsumer createConsumer(JmsTemplate jms, WebSocketSession session) {
		return new Consumer(jms, session);
	}
	
	@Override
	public void dispatch(Captcha captcha) throws IOException, Exception{
		
		IConsumer consumer = this.consumerRepo.next();
		if(null != consumer)
			consumer.send(captcha);
		else
			throw new Exception("No consumer");
	}
}
