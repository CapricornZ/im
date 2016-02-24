package demo.im.dwr;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.Base64;

@RequestMapping(value = "/dwr")
@Controller
public class DwrController {
	
	private static final Logger logger = LoggerFactory.getLogger(DwrController.class);
	
	@Autowired
	private MessageService messageService;
	public void setMessageService(MessageService service) { this.messageService = service; }
	
	@RequestMapping(value = "/response/{uid}",method=RequestMethod.POST)
	@ResponseBody
	public String replyMessage(@RequestBody String captcha, @PathVariable("uid") String uid){

		logger.info(String.format("receive Response {CAPTCHA:'%s', TO:'%s'}", captcha, uid));
		this.messageService.sendResponse(captcha, uid);
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/request",method=RequestMethod.POST)
	@ResponseBody
	public String message(@RequestParam("captchaImg") MultipartFile captcha,
			@RequestParam("tipImg") MultipartFile tip,
			@RequestParam("uid") String uid){
		
		logger.info(String.format("receive Request {'uid':%s}", uid));
		try{
			
			String base64Captcha = new sun.misc.BASE64Encoder().encodeBuffer(captcha.getBytes());
			String base64Tip = new sun.misc.BASE64Encoder().encodeBuffer(tip.getBytes());
			base64Captcha = Base64.byteArrayToBase64(captcha.getBytes());
			base64Tip = Base64.byteArrayToBase64(tip.getBytes());
			
			Message request = new Message();
			request.setId(uid);
			request.setCaptcha(base64Captcha);
			request.setTip(base64Tip);
			request.setTime(new Date());
			this.messageService.sendRequest(request);

			String response = this.messageService.waitResponse(uid);
			return response;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "ERROR";
	}

}
