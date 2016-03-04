package demo.im.rs.entity.cmd;

import java.util.Date;
import java.util.List;

import demo.im.rs.entity.Command;

public class SetTimerCmd extends Command {

	public SetTimerCmd(){
		this.setCategory("SETTIMER");
		this.setTime(new Date());
	}
	
	private List<String> param;
	public List<String> getParam() { return param; }
	public void setParam(List<String> param) { this.param = param; }
}
