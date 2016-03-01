package demo.im.rs.entity;

import java.util.Date;

public class HeartBeat extends Command {

	public HeartBeat(){
		this.setCategory("HEARTBEAT");
		this.setTime(new Date());
	}
}
