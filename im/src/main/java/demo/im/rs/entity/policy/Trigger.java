package demo.im.rs.entity.policy;

public class Trigger implements ITrigger{

	private String category;
	private int deltaPrice;
	private String priceTime;
	private String captchaTime;
	private String submitTime;
	public int submitReachPrice;
	
	public int getDeltaPrice() { return deltaPrice; }
	public void setDeltaPrice(int deltaPrice) { this.deltaPrice = deltaPrice; }
	
	public String getPriceTime() { return priceTime; }
	public void setPriceTime(String priceTime) { this.priceTime = priceTime; }
	
	public String getCaptchaTime() { return captchaTime; }
	public void setCaptchaTime(String captchaTime) { this.captchaTime = captchaTime; }
	
	public String getSubmitTime() { return submitTime; }
	public void setSubmitTime(String submitTime) { this.submitTime = submitTime; }
	
	public int isSubmitReachPrice() { return submitReachPrice; }
	public void setSubmitReachPrice(int submitReachPrice) { this.submitReachPrice = submitReachPrice; }
	
	@Override
	public String getCategory() { return this.category; }
	@Override
	public void setCategory(String value) { this.category = value; }
}
