package demo.command;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import demo.im.rs.entity.Message;
import demo.im.rs.entity.cmd.ReloadCmd;
import demo.im.rs.entity.cmd.SetTimerCmd;
import demo.im.rs.entity.cmd.SetTriggerCmd;
import demo.im.rs.entity.cmd.TriggerF11Cmd;
import demo.im.rs.entity.cmd.UpdatePolicyCmd;
import demo.im.rs.entity.policy.Trigger;

@RequestMapping(value = "/command")
@Controller
public class CommandController {
	
	private static final Logger logger = LoggerFactory.getLogger(CommandController.class);
	
	@Autowired
	private SocketHandler repo;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		
		List<String> users = repo.getActiveUsers();
		model.addAttribute("CLIENTS", users);
		return "command";
	}
	
	@RequestMapping(value = "/message/{CLIENT}", method=RequestMethod.PUT)
	@ResponseBody
	public String message(@RequestBody String message, @PathVariable("CLIENT")String client) throws IOException{
		
		Message command = new Message();
		command.setContent(message);
		this.repo.send(client, command);
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/reload/{CLIENT}", method=RequestMethod.PUT)
	@ResponseBody
	public String reload(@PathVariable("CLIENT")String client) throws IOException{
		
		this.repo.send(client, new ReloadCmd());
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/batch/message/{MESSAGE}", method=RequestMethod.PUT, consumes="application/json; charset=utf-8")
	@ResponseBody
	public String message(@RequestBody List<String> clients, @PathVariable("MESSAGE")String message) throws IOException {

		logger.info("receive /batch/message/{MESSAGE}");
		this.repo.send(clients, new Message(message));
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/batch/trigger/reload", method=RequestMethod.PUT, consumes="application/json; charset=utf-8")
	@ResponseBody
	public String reload(@RequestBody List<String> clients) throws IOException{
		
		logger.info("receive /batch/trigger/reload");
		this.repo.send(clients, new ReloadCmd());
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/batch/trigger/F11", method=RequestMethod.PUT, consumes="application/json; charset=utf-8")
	@ResponseBody
	public String triggerF11(@RequestBody List<String> clients) throws IOException{
		
		logger.info("receive /batch/trigger/F11");
		this.repo.send(clients, new TriggerF11Cmd());
		return "SUCCESS";
	}
	
	public static class SetParamToClient{
		
		private List<String> clients;
		private List<String> param;
		
		public List<String> getClients() { return clients; }
		public void setClients(List<String> clients) { this.clients = clients; }
		
		public List<String> getParam() { return param; }
		public void setParam(List<String> param) { this.param = param; }
	}
	
	@RequestMapping(value = "/batch/setParam", method=RequestMethod.PUT, consumes="application/json; charset=utf-8")
	@ResponseBody
	public String setParam(@RequestBody SetParamToClient param) throws IOException{
		
		logger.info("receive /batch/setParam");
		SetTimerCmd command = new SetTimerCmd();
		command.setParam(param.getParam());
		this.repo.send(param.getClients(), command);
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/batch/updatePolicy", method=RequestMethod.PUT, consumes="application/json; charset=utf-8")
	@ResponseBody
	public String updatePolicy(@RequestBody List<String> clients) throws IOException{
		
		logger.info("receive /batch/updatePolicy");
		this.repo.send(clients, new UpdatePolicyCmd());
		return "SUCCESS";
	}
	
	public static class TriggerV1{
		
		private List<String> clients;
		private Trigger trigger;
		
		public List<String> getClients() { return clients; }
		public void setClients(List<String> clients) { this.clients = clients; }
		
		public Trigger getTrigger() { return trigger; }
		public void setTrigger(Trigger trigger) { this.trigger = trigger; }
	}
	
	@RequestMapping(value = "/batch/trigger/V1", method=RequestMethod.PUT, consumes="application/json; charset=utf-8")
	@ResponseBody
	public String triggerV1(@RequestBody TriggerV1 request) throws IOException{
		
		logger.info("receive /batch/trigger/V1");
		String trigger = new com.google.gson.Gson().toJson(request.getTrigger());
		this.repo.send(request.getClients(), new SetTriggerCmd(trigger));
		return "SUCCESS";
	}
}
