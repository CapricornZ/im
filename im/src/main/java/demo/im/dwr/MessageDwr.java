package demo.im.dwr;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class MessageDwr implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String msg;
	private String time;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getMsg() { return msg; }
	public void setMsg(String msg) { this.msg = msg; }

	public String getTime() { return time; }
	public void setTime(String time) { this.time = time; }

	@Expose
	private String byid;
	public String getByid() { return byid; }
	public void setByid(String byid) { this.byid = byid; }

	public MessageDwr(String userid, String username) {
		super();
		this.byid = userid;
		this.msg = username;
	}

	// 在Set中添加相同实体会重 把hashCode重写

	@Override
	public int hashCode() {
		return byid.hashCode() * 7 + msg.hashCode() * 11;
	}

	@Override
	public String toString() {
		return String.format("{ id:%s, name:%s }", this.byid, this.msg);
	};
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		MessageDwr meDwr = (MessageDwr) obj;
		if (null != this.getByid() && this.getByid().equals(meDwr.getByid())
				&& this.getMsg() != null
				&& this.getMsg().equals(meDwr.getMsg())) {
			return true;
		}
		return false;
	}
 }