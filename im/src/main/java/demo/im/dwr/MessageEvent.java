package demo.im.dwr;

import org.springframework.context.ApplicationEvent;

public class MessageEvent extends ApplicationEvent {  
   
    private static final long serialVersionUID = 1L;  
   
    public MessageEvent(Object source) {  
        super(source);  
    }  
}
