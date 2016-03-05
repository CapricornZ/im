package demo.im.rs.entity.cmd;

import java.util.Date;

import demo.im.rs.entity.Command;

public class SetTriggerCmd extends Command {
	
	public SetTriggerCmd(){
		this.setCategory("SETTRIGGER");
		this.setTime(new Date());
	}
	
	public SetTriggerCmd(String trigger){
		this.setCategory("SETTRIGGER");
		this.setTime(new Date());
		this.trigger = trigger;
	}
	
	private String trigger;
	public String getTrigger() { return trigger; }
	public void setTrigger(String trigger) { this.trigger = trigger; }
}
