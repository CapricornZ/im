package demo.im.rs.entity;

import java.util.Date;

public class Reload extends Command {

	public Reload(){
		this.setCategory("RELOAD");
		this.setTime(new Date());
	}
}
