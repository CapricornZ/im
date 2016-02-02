package demo.im.dwr;

import java.util.Collection;

import javax.servlet.ServletContext;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.context.ServletContextAware;

@SuppressWarnings("rawtypes")
public class MessageClient implements ApplicationListener, ServletContextAware{

    private ServletContext ctx;
    @Override
    public void setServletContext(ServletContext ctx) { this.ctx = ctx; }
    
    @SuppressWarnings("deprecation")
    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        //如果事件类型是ChatMessageEvent就执行下面操作

        if (event instanceof MessageEvent) {

            Message msg = (Message) event.getSource();
            ServerContext context = ServerContextFactory.get();

            //获得客户端所有chat页面script session连接数
            String path = ctx.getContextPath() + "/captcha.html";
            Collection<ScriptSession> sessions = context.getScriptSessionsByPage(path);

            for (ScriptSession session : sessions) {

                ScriptBuffer sb = new ScriptBuffer();
                java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String s = format1.format(msg.getTime());
                
                //执行setMessage方法
                sb.appendScript(String.format("showMessage({captcha: '%s'", msg.getCaptcha()))
                	.appendScript(String.format(", time: '%s'", s))
                	.appendScript(String.format(", tip: '%s'", msg.getTip()))
                	.appendScript(String.format(", id: '%s'", msg.getId()))
                	.appendScript("})");

                //System.out.println(sb.toString());
                System.out.println("showMessage({msg, tip, time, id}) to " + session.getId());
                //执行客户端script session方法，相当于浏览器执行JavaScript代码
                //上面就会执行客户端浏览器中的showMessage方法，并且传递一个对象过去
                session.addScript(sb);
            }
        }
    }
}
