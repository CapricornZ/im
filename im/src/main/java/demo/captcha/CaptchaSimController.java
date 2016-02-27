package demo.captcha;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import demo.captcha.simulate.CaptchaReq;
import demo.captcha.simulate.Simulator2;
import demo.im.rs.entity.Captcha;

@RequestMapping(value = "/captcha/test")
@Controller
public class CaptchaSimController {
	
	private static final Logger logger = LoggerFactory.getLogger(CaptchaSimController.class);
	
	@Autowired private Simulator2 simulator;
	@Resource(name="processor") private IProcessor captchaProcessor;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String simulate(){

		return "simulate";
	}
	
	@RequestMapping(value = "/fetch", method = RequestMethod.POST)
	@ResponseBody
	public String fetch() throws IOException{

		return "SUCCESS";
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String test(){
		
		logger.info(String.format("receive test"));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "test";
	}
	
	@RequestMapping(value = "/captcha",method=RequestMethod.GET)
	@ResponseBody
	public CaptchaReq generateCaptcha(){
		
		logger.info(String.format("random CAPTCHA"));
		CaptchaReq rtn = new CaptchaReq();
		try {
			//byte[] tip = simulator.randomTip();
			//byte[] captcha = simulator.randomCaptcha();
			
			//UUID uuid = UUID.randomUUID();
			//rtn.setCaptcha(Base64.byteArrayToBase64(captcha));
			//rtn.setTip(Base64.byteArrayToBase64(tip));
			//rtn.setUid(uuid.toString());
			rtn = this.simulator.generateCaptcha();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return rtn;
	}
	
	@RequestMapping(value = "/request/{uid}", method=RequestMethod.POST)
	@ResponseBody
	public String message(@RequestBody CaptchaReq captchaReq, @PathVariable("uid") String uid){
		
		logger.info(String.format("receive Request {'uid':%s}", uid));
		try{
			
			Captcha captchaCommand = new Captcha();
			captchaCommand.setUid(uid);
			captchaCommand.setCaptcha(captchaReq.getCaptcha());
			captchaCommand.setTip(captchaReq.getTip());
			
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
