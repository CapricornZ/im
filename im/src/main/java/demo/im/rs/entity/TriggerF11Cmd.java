package demo.im.rs.entity;

import java.util.Date;

public class TriggerF11Cmd extends Command {

	public TriggerF11Cmd(){
		this.setCategory("TRIGGERF11");
		this.setTime(new Date());
	}
}
