package demo.captcha;

import java.util.List;

public interface IRepository {

	IConsumer next();
	List<String> getActiveUsers();
}
