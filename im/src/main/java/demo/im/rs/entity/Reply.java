package demo.im.rs.entity;

import java.util.Date;

/**
 * Operator返回校验码
 * @author martin
 *
 */
public class Reply extends Command {

	public Reply(){
    	this.setCategory("REPLY");
    	this.setTime(new Date());
    }
	
	private String code;
	private String uid;
	private String operator;

	public String getFrom() { return operator; }
	public void setFrom(String user) { this.operator = user; }
	
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	
	public String getUid() { return uid; }
	public void setUid(String uid) { this.uid = uid; }
}
