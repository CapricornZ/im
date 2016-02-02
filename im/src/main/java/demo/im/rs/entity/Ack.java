package demo.im.rs.entity;

import java.util.Date;

public class Ack{
	
	private String category;
	private String user;
	private Date time;

	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	
	public String getUser() { return user; }
	public void setUser(String user) { this.user = user; }
	
	public Date getTime() { return time; }
	public void setTime(Date time) { this.time = time; }
}
