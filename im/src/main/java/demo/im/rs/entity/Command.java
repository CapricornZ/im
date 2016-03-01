package demo.im.rs.entity;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import demo.im.rs.entity.util.*;

public abstract class Command {

	private String category;
	private Date time;

	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }

	public Date getTime() { return time; }
	public void setTime(Date time) { this.time = time; }
}
