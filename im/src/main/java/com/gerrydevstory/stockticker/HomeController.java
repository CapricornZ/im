package com.gerrydevstory.stockticker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.Base64;

import demo.im.rs.entity.Captcha;
import demo.im.rs.entity.Message;

@RequestMapping(value = "/home")
@Controller
public class HomeController implements ApplicationContextAware{
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired 
	private SimpMessagingTemplate template;
	private TaskScheduler scheduler = new ConcurrentTaskScheduler();
	private List<Stock> stockPrices = new ArrayList<Stock>();
	private Random rand = new Random(System.currentTimeMillis());

	@Autowired private WebsocketEndPoint wsEndpoint;
	@Autowired private JmsTemplate jmsTemplate;
	/**
	 * Iterates stock list, update the price by randomly choosing a positive or
	 * negative percentage, then broadcast it to all subscribing clients
	 */
	private void updatePriceAndBroadcast() {
		for (Stock stock : stockPrices) {
			double chgPct = rand.nextDouble() * 5.0;
			if (rand.nextInt(2) == 1)
				chgPct = -chgPct;
			stock.setPrice(stock.getPrice() + (chgPct / 100.0 * stock.getPrice()));
			stock.setTime(new Date());
		}
		template.convertAndSend("/topic/price", stockPrices);
	}

	class BroadCast implements Runnable{

		public BroadCast(WebsocketEndPoint ws){
			this.wsEndPoint = ws;
		}
		private WebsocketEndPoint wsEndPoint;
		@Override
		public void run() {
			
			template.convertAndSend("/topic/price", this.wsEndPoint.getActiveUsers());
		}
		
	}
	/**
	 * Invoked after bean creation is complete, this method will schedule
	 * updatePriceAndBroacast every 1 second
	 * @throws Exception 
	 */
	@PostConstruct
	private void broadcastTimePeriodically() throws Exception {
		scheduler.scheduleAtFixedRate(new BroadCast(this.wsEndpoint), 5000);
		/*scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				updatePriceAndBroadcast();
			}
		}, 1000);*/
		
		//BrokerService broker = new BrokerService();
		//broker.addConnector("tcp://localhost:61616");
		//broker.start();
	}

	/**
	 * Handler to add one stock
	 */
	@MessageMapping("/addStock")
	public void addStock(Stock stock) throws Exception {
		stockPrices.add(stock);
		updatePriceAndBroadcast();
	}

	/**
	 * Handler to remove all stocks
	 */
	@MessageMapping("/removeAllStocks")
	public void removeAllStocks() {
		stockPrices.clear();
		updatePriceAndBroadcast();
	}

	/**
	 * Serve the main page, view will resolve to /WEB-INF/home.jsp
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "home";
	}
	
	@RequestMapping(value = "/ws0", method = RequestMethod.GET)
	public String ws0(HttpSession session, Model model){

		return "ws0";
	}
	
	@RequestMapping(value = "/ws/broadcast", method = RequestMethod.POST)
	@ResponseBody
	public String broadcast(@RequestBody String message) throws IOException{
		
		Message msg = new Message(message);
		this.wsEndpoint.broadcast(msg);
		return "broadcast";
	}

	@RequestMapping(value = "/ws/{USER}", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public String sendUser(@RequestBody Captcha message, @PathVariable("USER") String user) throws IOException{

		this.wsEndpoint.send2User(message, user);
		return "broadcast";
	}
	
	@RequestMapping(value = "/response/{uid}",method=RequestMethod.POST)
	@ResponseBody
	public String replyMessage(@RequestBody final String captcha, @PathVariable("uid") final String uid){

		logger.info(String.format("receive Response {CAPTCHA:'%s', TO:'%s'}", captcha, uid));
		MessageCreator messageCreator = new MessageCreator() {
			@Override
			public javax.jms.Message createMessage(Session session) throws JMSException {
				
				TextMessage message = session.createTextMessage();
				message.setText(captcha);
				message.setStringProperty("from", uid);
				return message;
			}
		};
		
		this.jmsTemplate.send(messageCreator);
		return "SUCCESS";
	}
	
	@RequestMapping(value = "/request", method=RequestMethod.POST)
	@ResponseBody
	public String message(@RequestParam("captchaImg") MultipartFile captcha,
			@RequestParam("tipImg") MultipartFile tip,
			@RequestParam("uid") String uid){
		
		logger.info(String.format("receive Request {'uid':%s}", uid));
		try{
			
			String base64Captcha = Base64.byteArrayToBase64(captcha.getBytes());
			String base64Tip = Base64.byteArrayToBase64(tip.getBytes());
			
			Captcha captchaCommand = new Captcha();
			captchaCommand.setUid(uid);
			captchaCommand.setCaptcha(base64Captcha);
			captchaCommand.setTip(base64Tip);
			
			//轮询
			//this.wsEndpoint.dispatch(captchaCommand);
			
			//ready用户优先
			JmsTemplate sender = (JmsTemplate)this.ctx.getBean("jmsTemplate.request.producer");
			sender.convertAndSend(captchaCommand);

			String selector = String.format("from='%s'", uid);
			JmsTemplate jmsTemplate = (JmsTemplate)this.ctx.getBean("jmsTemplate");
			Object message = jmsTemplate.receiveSelectedAndConvert(selector);
			if(null == message)
				return "ERROR";
			else
				return (String)message;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "ERROR";
	}

	private ApplicationContext ctx;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}
}