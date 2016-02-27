package demo.captcha;

import java.io.IOException;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.im.rs.entity.Captcha;
import demo.im.rs.entity.Command;
import demo.im.rs.entity.CommandAdapter;

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
	
	public Command waitCommand(String uid){
		
		String selector = String.format("from='%s'", uid);
		String message = (String)this.responseReceiver.receiveSelectedAndConvert(selector);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(Command.class, new CommandAdapter()).create();
		return gson.fromJson(message, Command.class);
	}
}
