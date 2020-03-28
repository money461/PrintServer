package com.bondex.common;

import net.sf.ehcache.Statistics;

public class Common {

	public final static String Session_UserInfo = "userInfo"; //Session 中的用户信息ID名称
	
	
	public final static String Session_UserSecurity = "userSecurity"; //Session 中的用户权限信息ID名称
	
	
	public final static String Session_opids = "opids"; //Session 中的用户所有的opid及其对应的姓名
	
	
	public final static String Session_thisOpid = "thisOpid"; //session中用户选定的opid号

	
	public final static String Session_thisUsername = "thisUsername"; //session中用户选定的opid对应的姓名
	
	
	public final static String UserSecurity_Model = "model"; //用户功能模块权限 opid+后缀
	
	
	public final static String UserSecurity_PrintButton = "printButton"; //用户打印按钮权限 opid+后缀
	
	
	public final static String UserSecurity_Button = "button"; //用户功能模块按钮权限 opid+后缀
 	
	
	/**
	 * 加密盐前缀
	 */
	public final static String SALT_PREFIX = "::SpringBootDemo::";

	/**
	 * 逗号分隔符
	 */
	public final static String SEPARATOR_COMMA = ",";
	
	/**
	 * 发送内网
	 */
	public final static String MQAddress_VPNNET = "vpnnet";
	
	/**
	 * 发送外网
	 */
	public final static String MQAddress_OUTNET = "outnet";
	
	
	
	
}
