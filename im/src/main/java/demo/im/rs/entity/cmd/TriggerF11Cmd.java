package demo.im.rs.entity.cmd;

import java.util.Date;

import demo.im.rs.entity.Command;

public class TriggerF11Cmd extends Command {

	public TriggerF11Cmd(){
		this.setCategory("TRIGGERF11");
		this.setTime(new Date());
	}
}
