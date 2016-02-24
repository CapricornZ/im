package demo.captcha;

import java.io.IOException;

import org.springframework.web.socket.WebSocketSession;

import demo.im.rs.entity.Command;

public interface IConsumer {
	
	String getUser();
	WebSocketSession getSession();
	
	void send(Command command) throws IOException;
	void send(String message) throws IOException;
	void ready();
	void stop();
}
