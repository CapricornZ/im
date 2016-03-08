package demo.captcha.security;

import java.util.List;

public class User {
	
	private String principal;
	private String credential;
	private List<String> roles;
	
	public String getPrincipal() { return principal; }
	public void setPrincipal(String principal) { this.principal = principal; }
	
	public String getCredential() { return credential; }
	public void setCredential(String credential) { this.credential = credential; }
	
	public List<String> getRoles() { return roles; }
	public void setRoles(List<String> roles) { this.roles = roles; }	
}
