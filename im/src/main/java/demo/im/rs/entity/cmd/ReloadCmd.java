package demo.im.rs.entity.cmd;

import java.util.Date;

import demo.im.rs.entity.Command;

public class ReloadCmd extends Command {

	public ReloadCmd(){
		this.setCategory("RELOAD");
		this.setTime(new Date());
	}
}
