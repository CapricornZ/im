package com.gerrydevstory.stockticker;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(HandshakeInterceptor.class);
	@Override  
    public boolean beforeHandshake(ServerHttpRequest request,  
            ServerHttpResponse response, WebSocketHandler wsHandler,  
            Map<String, Object> attributes) throws Exception {

        logger.debug("Before Hand Shake");
        //ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        //HttpSession session = servletRequest.getServletRequest().getSession(false);
        //String user = (String) session.getAttribute("USER");
        //attributes.put("USER", user);
        return super.beforeHandshake(request, response, wsHandler, attributes);  
    }  
  
    @Override  
    public void afterHandshake(ServerHttpRequest request,  
            ServerHttpResponse response, WebSocketHandler wsHandler,  
            Exception ex) {
    	
    	logger.debug("After Hand Shake");  
        super.afterHandshake(request, response, wsHandler, ex);  
    }  
}
