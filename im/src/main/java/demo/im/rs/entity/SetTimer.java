package demo.im.rs.entity;

import java.util.Date;
import java.util.List;

public class SetTimer extends Command {

	public SetTimer(){
		this.setCategory("SETTIMER");
		this.setTime(new Date());
	}
	
	private List<String> param;
	public List<String> getParam() { return param; }
	public void setParam(List<String> param) { this.param = param; }
}
