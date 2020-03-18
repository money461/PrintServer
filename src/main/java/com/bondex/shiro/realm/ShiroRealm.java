package com.bondex.shiro.realm;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.bondex.common.Common;
import com.bondex.shiro.security.SecurityService;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.GsonUtil;
import com.bondex.util.shiro.ShiroUtils;

public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private SecurityService SecurityService;
	/**
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
	 if (token != null && token instanceof UsernamePasswordToken ) {
	  UsernamePasswordToken utoken= (UsernamePasswordToken)token;
	  String username = utoken.getUsername();
	  //封装用户认证信息
	  UserInfo userInfo = SecurityService.setSerlvetContext(username);
	  Object password = utoken.getCredentials();
      SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
    		        userInfo, //用户
		    		password, //密码
		    		null,  //加盐
		    		getName()  //realm name
		    		);
		 return authenticationInfo;
	 }
		return null;
	}

	
	/**
	 * 授权
	 * .doGetAuthorizationInfo执行时机有三个，如下：

		1、subject.hasRole(“admin”) 或 subject.isPermitted(“admin”)：自己去调用这个是否有什么角色或者是否有什么权限的时候；

		2、@RequiresRoles("admin") ：在方法上加注解的时候；

		3、[@shiro.hasPermission name = "admin"][/@shiro.hasPermission]：在页面上加shiro标签的时候，即进这个页面的时候扫描到有这个标签的时候。
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		UserInfo userInfo = (UserInfo)principals.getPrimaryPrincipal();
		Set<String> premissionSet = Collections.synchronizedSet(new LinkedHashSet<String>());
		//获取到当前opid的权限
		Map<String, Object> security = SecurityService.getSecurity(userInfo,premissionSet);
		
//		JSONObject security  = JSONObject.parseObject(ReadTxtFile.readTxtFile("C:\\Users\\admin\\Desktop\\标签打印\\AuthData.json"));
		
		if(null!=security){
			System.out.println(GsonUtil.GsonString(security));
			Session session = ShiroUtils.getSession();
			// 用户权限存放session
			session.setAttribute(Common.Session_UserSecurity, security);
			//进行角色的添加
			authorizationInfo.addRole(userInfo.getOpid());
			
			//权限的添加
			authorizationInfo.addStringPermissions(premissionSet);
			System.out.println(GsonUtil.GsonString(premissionSet));
		}
		
		return authorizationInfo;
	}

	/**
	 * 清除权限
	 */
	public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
		super.clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清除认证
	 */
	public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
		super.clearCachedAuthenticationInfo(principals);
	}
	
	
	
}
