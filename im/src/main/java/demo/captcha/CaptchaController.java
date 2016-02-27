package demo.captcha;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.Base64;

import demo.im.rs.entity.Captcha;

@RequestMapping(value = "/captcha")
@Controller
public class CaptchaController {
	
	class BroadCast implements Runnable{

		private SocketHandler socketHandler;
		public BroadCast(SocketHandler handler){
			this.socketHandler = handler;
		}
		
		@Override
		public void run() {
			template.convertAndSend("/topic/price", this.socketHandler.getActiveUsers());
		}
	}
	
	@PostConstruct
	private void broadcastTimePeriodically() throws Exception {
		scheduler.scheduleAtFixedRate(new BroadCast(this.sessionRepository), 30000);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

	@Autowired private JmsTemplate jmsTemplate;
	@Autowired private SimpMessagingTemplate template;
	@Autowired private SocketHandler sessionRepository;
	
	@Resource(name="processor") private IProcessor captchaProcessor;
	
	private TaskScheduler scheduler = new ConcurrentTaskScheduler();
		
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		
		return "ws0";
	}
	
	@RequestMapping(value = "/response/{uid}",method=RequestMethod.POST)
	@ResponseBody
	public String replyMessage(@RequestBody final String captcha, @PathVariable("uid") final String uid){

		logger.info(String.format("receive Response {CAPTCHA:'%s', TO:'%s'}", captcha, uid));
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
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/request", method=RequestMethod.POST)
	@ResponseBody
	public String message(@RequestParam("captchaImg") MultipartFile captcha,
			@RequestParam("tipImg") MultipartFile tip,
			@RequestParam("uid") String uid){
		
		logger.info(String.format("receive Request {'uid':%s}", uid));
		try{
			
			String base64Captcha = Base64.byteArrayToBase64(captcha.getBytes());
			String base64Tip = Base64.byteArrayToBase64(tip.getBytes());
			
			Captcha captchaCommand = new Captcha();
			captchaCommand.setUid(uid);
			captchaCommand.setCaptcha(base64Captcha);
			captchaCommand.setTip(base64Tip);
			
			this.captchaProcessor.dispatch(captchaCommand);
			String message = this.captchaProcessor.waitResp(uid);
			if(null != message)
				return (String)message;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "ERROR";
	}

}
