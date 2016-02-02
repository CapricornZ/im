package demo.im.dwr;

import java.util.Date;

public class Message {
	
	private String uid;  
    private String captcha;
    private String tip;
    private Date time;
    
	public String getId() { return uid; }
	public void setId(String id) { this.uid = id; }
	
	public String getCaptcha() { return captcha; }
	public void setCaptcha(String msg) { this.captcha = msg; }
	
	public String getTip() { return tip; }
	public void setTip(String tip) { this.tip = tip; }
	
	public Date getTime() { return time; }
	public void setTime(Date time) { this.time = time; } 
}
