package demo.im.rs.entity.cmd;

import java.util.Date;

import demo.im.rs.entity.Command;

public class Submit1Cmd extends Command {

	public Submit1Cmd(){
		this.setCategory("SUBMIT1");
		this.setTime(new Date());
	}
}
