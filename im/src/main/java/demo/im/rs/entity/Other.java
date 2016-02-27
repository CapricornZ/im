package demo.im.rs.entity;

import java.util.Date;

public class Other extends Command {

	public Other(String desc){
		this.setCategory("OTHER");
		this.setTime(new Date());
		this.description = desc;
	}
	
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}
