package demo.captcha.simulate;

public class CaptchaReq{
	
	String value;
	String tip;
	String captcha;
	String uid;
	
	public String getUid() { return uid; }
	public void setUid(String uid) { this.uid = uid; }
	
	public String getTip() { return tip; }
	public void setTip(String tip) { this.tip = tip; }
	
	public String getCaptcha() { return captcha; }
	public void setCaptcha(String captcha) { this.captcha = captcha; }
	
	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }
}