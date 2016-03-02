package demo.im.rs.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public abstract class Command {

	private String category;
	private Date time;

	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public Date getTime() { return time; }
	public void setTime(Date time) { this.time = time; }
}
