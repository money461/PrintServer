package com.bondex.common;

public class Common {

	public final static String Session_UserInfo = "userInfo"; //Session 中的用户信息ID名称
	
	
	public final static String Session_UserSecurity = "userSecurity"; //Session 中的用户权限信息ID名称
	
	
	public final static String Session_opids = "opids"; //Session 中的用户所有的opid及其对应的姓名
	
	
	public final static String Session_thisOpid = "thisOpid"; //session中用户选定的opid号

	
	public final static String Session_thisUsername = "thisUsername"; //session中用户选定的opid对应的姓名
	
	
	public final static String UserSecurity_Model = "model"; //用户功能模块权限 opid+后缀
	
	
	public final static String UserSecurity_PrintButton = "printButton"; //用户打印按钮权限 opid+后缀
	
	
//	public final static String UserSecurity_Button = "button"; //用户功能模块按钮权限 opid+后缀   合并在  "model" 中功能权限
 	
	
	/**
	 * 加密盐前缀
	 */
	public final static String SALT_PREFIX = "::SpringBootDemo::";

	/**
	 * 逗号分隔符
	 */
	public final static String SEPARATOR_COMMA = ",";
	
	
	public static final String POINT = ".";

	public static final String SPACE = " ";
	
	/**
	 * 发送内网
	 */
	public final static String MQAddress_VPNNET = "vpnnet";
	
	/**
	 * 发送外网
	 */
	public final static String MQAddress_OUTNET = "outnet";
	
	/**
	 * 页面Code  后缀
	 */
	public final static String PageCode_Suffix = "_label";
	
}
