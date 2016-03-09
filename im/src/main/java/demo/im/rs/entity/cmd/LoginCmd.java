package demo.im.rs.entity.cmd;

import java.util.Date;

import demo.im.rs.entity.Command;

public class LoginCmd extends Command {

	public LoginCmd(){
		this.setCategory("LOGIN");
		this.setTime(new Date());
	}
}
