package demo.im.rs.entity;

import java.util.Date;

/**
 * 校验码无法识别时，重新请求校验码
 * @author martin
 *
 */
public class Retry extends Command {
	
	public Retry(){
		this.setCategory("RETRY");
		this.setTime(new Date());
	}
	
	private String operator;
	private String uid;
	
	public String getOperator() { return operator; }
	public void setOperator(String user) { this.operator = user; }
	
	public String getUid() { return uid; }
	public void setUid(String uid) { this.uid = uid; }
}
