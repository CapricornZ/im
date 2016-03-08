package demo.im.rs.entity.cmd;

import java.util.Date;

import demo.im.rs.entity.Command;

public class TimeSyncCmd extends Command {

	public TimeSyncCmd(){
		this.setCategory("TIMESYNC");
		this.setTime(new Date());
	}
}
