package demo.im.rs.entity;

import java.util.Date;

public class Message extends Command {
	
	public Message(){
		this.setCategory("MESSAGE");
		this.setTime(new Date());
	}
	
	public Message(String msg){
		this.setCategory("MESSAGE");
		this.setTime(new Date());
		this.content = msg;
	}
	
	private String content;

	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }	
}
