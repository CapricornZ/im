package demo.im.rs.entity.cmd;

import java.util.Date;

import demo.im.rs.entity.Command;

public class UpdatePolicyCmd extends Command {
	
	public UpdatePolicyCmd(){
		this.setCategory("UPDATEPOLICY");
		this.setTime(new Date());
	}

}
