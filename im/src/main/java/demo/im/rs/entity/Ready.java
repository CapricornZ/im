package demo.im.rs.entity;

import java.util.Date;

/**
 * Operator 置Ready状态
 * @author martin
 *
 */
public class Ready extends Command {
	
	private String user;

	public Ready(){
    	this.setCategory("READY");
    	this.setTime(new Date());
    }
	
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
}
