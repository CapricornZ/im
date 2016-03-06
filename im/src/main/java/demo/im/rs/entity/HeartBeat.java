package demo.im.rs.entity;

import java.util.Date;

public class HeartBeat extends Command {

	public HeartBeat(){
		this.setCategory("HEARTBEAT");
		this.setTime(new Date());
	}
	
	private String content;
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
}
