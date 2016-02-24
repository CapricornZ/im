package demo.im.dwr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("chatManager")
public class ChatManager {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatManager.class);
    @Autowired
    private HttpServletRequest request;
    
    /** 保存当前在线用户列表 */
    public static List<MessageDwr> users = new ArrayList<MessageDwr>();
    private static Map<String, ScriptSession> SCR_MAPS = new HashMap<String, ScriptSession>();
    
    public List<MessageDwr> getUsers(){ return users; }
    /**
     * 更新当前在线用户
     * 
     * @param userid
     * @param username
     * @return
     */
    public String updateUsersList(String userid, String username) {
        
    	logger.info("register : {{{}, {}}}", userid, username);
    	
    	MessageDwr user = new MessageDwr(userid, username);
        users.add(user);
        this.setScriptSessionFlag(userid);
        
        // 获取当前最新scriptSession
        Collection<ScriptSession> sessions = SCR_MAPS.values();
        Util util = new Util(sessions);
        // 处理这些页面中的一些元素
        util.removeAllOptions("receiver");
        util.addOptions("receiver", users, "byid", "msg");

        return null;
    }

    /**
     * 将用户id和页面脚本session绑定
     * 
     * @param userid
     */
    public void setScriptSessionFlag(String userid) {
    	
        ScriptSession session = getOnlyScriptSession(userid);
        if (null != session.getAttribute("userid")) {
            String uid = (String) session.getAttribute("userid");
            if (StringUtils.isBlank(uid)) {
                session.setAttribute("userid", userid);
            }
        } else {
            session.setAttribute("userid", userid);
        }
    }

    // 储存最新scriptSession
    public ScriptSession getOnlyScriptSession(String userid) {
        
    	StringBuilder sb = new StringBuilder();
    	for(String user : SCR_MAPS.keySet()){
    		sb.append(user + ", ");
    	}
    	logger.debug("consumers : [{}]", sb);
    	ScriptSession scriptSession = SCR_MAPS.get(userid);
        scriptSession = WebContextFactory.get().getScriptSession();
        SCR_MAPS.put(userid, scriptSession);
        return scriptSession;
    }

    /**
     * 根据用户id获得指定用户的页面脚本session
     * 
     * @param userid
     * @return
     */
    public ScriptSession getScriptSession(String userid) {
    	
    	logger.info("getScriptSession(userid:{})", userid);
    	
        ScriptSession scriptSessions = null;
        Collection<ScriptSession> sessions = SCR_MAPS.values();
        for (ScriptSession session : sessions) {
            String xuserid = (String) session.getAttribute("userid");
            if (xuserid != null && xuserid.equals(userid)) {
                scriptSessions = session;
            }
        }
        return scriptSessions;
    }

    /**
     * 发送消息
     * @param sender 发送者
     * @param receiverid 接受者id
     * @param msg 消息
     */    
    public void send(String sender, String receiverid, String msg) {
        ScriptSession session = this.getScriptSession(receiverid);
        ScriptBuffer sb = new ScriptBuffer();
        sb.appendScript("showMessage({msg: ")
                .appendData(msg)
                .appendScript(", time: '")
                .appendScript( new SimpleDateFormat("HH:mm:ss").format(new Date()))
                .appendScript("',sender:'").appendScript(sender)
                .appendScript("'})");
        session.addScript(sb);
    }

}