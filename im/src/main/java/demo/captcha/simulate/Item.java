package demo.captcha.simulate;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.alibaba.druid.util.Base64;

public class Item {
	
	private String value;
	private String tips;
	private String url;
	
	public Item(String[] vals){
		this.value = vals[0];
		this.tips = vals[1];
		this.url = vals[2];
	}
	
	public CaptchaReq generate() throws IOException{
		
		CaptchaReq rtn = new CaptchaReq();
		rtn.value = this.value;
		
		//CAPTCHA
		URL u = new URL(this.url);
		BufferedImage image = ImageIO.read(u);
		ByteArrayOutputStream captcha = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", captcha);
		captcha.flush();
		rtn.captcha = Base64.byteArrayToBase64(captcha.toByteArray());
		
		//TIP
		rtn.tip = Base64.byteArrayToBase64(new ChartGraphics().graphicsGenerate(this.tips));
		
		//UID
		UUID uuid = UUID.randomUUID();
		rtn.uid = uuid.toString();
		
		return rtn;
	}
}
