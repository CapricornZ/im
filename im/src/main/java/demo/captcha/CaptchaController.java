package demo.captcha;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import demo.im.rs.entity.Command;
import demo.im.rs.entity.Other;

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
		scheduler.scheduleAtFixedRate(new BroadCast(this.sessionRepository), 5000);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

	@Autowired private SimpMessagingTemplate template;
	@Autowired private SocketHandler sessionRepository;
	
	@Resource(name="processor") private IProcessor captchaProcessor;
	
	private TaskScheduler scheduler = new ConcurrentTaskScheduler();
		
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		
		return "ws0";
	}
	
	@RequestMapping(value = "/user/{SESSION}", method=RequestMethod.DELETE)
	@ResponseBody
	public String remove(@PathVariable("SESSION")String session){
		
		logger.info("receive [DELETE] /user/" + session);
		this.sessionRepository.removeUser(session);
		return "SUCCESS";
	}

	@RequestMapping(value = "/request", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Command message(@RequestParam("captchaImg") MultipartFile captcha,
			@RequestParam("tipImg") MultipartFile tip,
			@RequestParam("uid") String uid){
		
		logger.info("receive Request {'uid':'{}'}", uid);
		try{
			
			String base64Captcha = Base64.byteArrayToBase64(captcha.getBytes());
			String base64Tip = Base64.byteArrayToBase64(tip.getBytes());
			
			Captcha captchaCommand = new Captcha();
			captchaCommand.setUid(uid);
			captchaCommand.setCaptcha(base64Captcha);
			captchaCommand.setTip(base64Tip);
			
			this.captchaProcessor.dispatch(captchaCommand);
			Command message = this.captchaProcessor.waitCommand(uid);
			if(null != message)
				return message;
			//String message = this.captchaProcessor.waitResp(uid);
			//if(null != message)
			//	return (String)message;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return new Other("WAIT TIMEOUT");
	}

}
