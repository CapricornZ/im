package demo.captcha.security;

import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

public class MyRealm extends AuthorizingRealm {

	private Map<String, User> repository;
	public void setRepository(Map<String, User> repository) {
		this.repository = repository;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		String currentUsername = (String) super.getAvailablePrincipal(principals);
		SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();
		if(null != currentUsername){
			User user = this.repository.get(currentUsername);
			if(null != user){
				simpleAuthorInfo.addRoles(user.getRoles());
				return simpleAuthorInfo;
			}
		}
		// 若该方法什么都不做直接返回null的话,就会导致任何用户访问/admin/listUser.jsp时都会自动跳转到unauthorizedUrl指定的地址
		// 详见applicationContext.xml中的<bean id="shiroFilter">的配置
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {

		// 获取基于用户名和密码的令牌
		// 实际上这个authcToken是从LoginController里面currentUser.login(token)传过来的
		// 两个token的引用都是一样的
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		System.out.println("验证当前Subject时获取到token为"
				+ ReflectionToStringBuilder.toString(token, ToStringStyle.MULTI_LINE_STYLE));
		User user = this.repository.get(token.getUsername());
		if( null != user ){
			AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getPrincipal(), user.getCredential(), this.getName());
			this.setSession("currentUser", user.getPrincipal());
			return authcInfo;
		}
		return null;
	}

	/**
	 * 将一些数据放到ShiroSession中,以便于其它地方使用
	 * 
	 * @see 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到
	 */
	private void setSession(Object key, Object value) {

		Subject currentUser = SecurityUtils.getSubject();
		if (null != currentUser) {
			Session session = currentUser.getSession();
			System.out
					.println("Session默认超时时间为[" + session.getTimeout() + "]毫秒");
			if (null != session) {
				session.setAttribute(key, value);
			}
		}
	}

}
