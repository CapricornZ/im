package demo.im.rs.entity;

import java.util.Date;

public class Reply extends Command {

	public Reply(){
    	this.setCategory("REPLY");
    	this.setTime(new Date());
    }
	
	private String code;
	private String uid;

	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	
	public String getUid() { return uid; }
	public void setUid(String uid) { this.uid = uid; }
}
