package demo.im.rs.entity;

import java.util.Date;

public class Captcha extends Command {
	
	private String uid;
    private String captcha;
    private String tip;

    public Captcha(){
    	this.setCategory("CAPTCHA");
    	this.setTime(new Date());
    }
	
    public String getUid() { return uid; }
	public void setUid(String uid) { this.uid = uid; }
	
	public String getCaptcha() { return captcha; }
	public void setCaptcha(String captcha) { this.captcha = captcha; }
	
	public String getTip() { return tip; }
	public void setTip(String tip) { this.tip = tip; }    
}
