package demo.captcha.simulate;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;

import com.alibaba.druid.util.Base64;

public class Simulator {

	private Random random = new Random();
	
	private String[] repository;
	public void setRepository(String[] value){ this.repository = value; }
	
	private String[] tips;
	public void setTips(String[] value){ this.tips = value; }
	
	public CaptchaReq generateCaptcha() throws IOException{
		
		CaptchaReq rtn = new CaptchaReq();
		
		ByteArrayOutputStream tip = new ByteArrayOutputStream();
		ByteArrayOutputStream captcha = new ByteArrayOutputStream();
		String sfile = "";
		String value = "";
		
		String url = this.repository[this.random.nextInt(repository.length)];
		String[] array = url.split("/");
		String fileName = array[array.length-1];
		String [] file = fileName.split("\\.");
		if(file[0].indexOf('-') > 0){
			
			value = file[0].split("-")[0];
			String pattern = file[0].split("-")[1];
			
			if("14".equals(pattern))
				sfile = "tip2.png";
			if("25".equals(pattern))
				sfile = "tip4.png";
			if("36".equals(pattern))
				sfile = "tip1.png";

		} else {
			
			sfile = this.tips[this.random.nextInt(tips.length)];
			if(sfile.equals("tip1.png"))
				value = file[0].substring(2);
			if(sfile.equals("tip4.png"))
				value = file[0].substring(1,5);
			if(sfile.equals("tip2.png"))
				value = file[0].substring(0,4);
		}
		
		//CAPTCHA img
		URL u = new URL(url);
		BufferedImage image = ImageIO.read(u);
		ImageIO.write(image, "jpeg", captcha);
		captcha.flush();
		
		//TIP img
		InputStream in = new ClassPathResource(sfile).getInputStream();
		byte[] bytes = new byte[1024];
		while(in.read(bytes)!=-1)
			tip.write(bytes);
		in.close();
		
		UUID uuid = UUID.randomUUID();
		rtn.setCaptcha(Base64.byteArrayToBase64(captcha.toByteArray()));
		rtn.setTip(Base64.byteArrayToBase64(tip.toByteArray()));
		rtn.setUid(uuid.toString());
		rtn.setValue(value);
		
		return rtn;
	}
	
	public void fetch() throws IOException{
		
		for(int i=0; i<this.repository.length; i++){
			
			String[] array = this.repository[i].split("/");
			String fileName = array[array.length-1];
			
			ByteArrayOutputStream captcha = new ByteArrayOutputStream();
			//CAPTCHA img			
			URL u = new URL(this.repository[i]);
			BufferedImage image = ImageIO.read(u);
			ImageIO.write(image, "jpeg", captcha);
			captcha.flush();
			
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(captcha.toByteArray());
			fos.close();
		}
		
	}
}

