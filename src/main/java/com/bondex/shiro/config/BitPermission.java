package com.bondex.shiro.config;

import java.util.regex.Pattern;

import org.apache.shiro.authz.Permission;


public class BitPermission implements Permission  {

	private String permissionString;
	
	public BitPermission(String permissionString) {
		this.permissionString = permissionString;
	}
	public String getPermissionString() {
		return permissionString;
	}
	
	@Override
	public boolean implies(Permission p) {
		if (!(p instanceof BitPermission)) {
			return false;
		}
		BitPermission wp = (BitPermission) p;
		String wpPerm = wp.getPermissionString(); //wpPerm 权限写死的 [a-zA-Z]+_label  permissionString用户拥有的权限
		if (Pattern.matches(wpPerm,permissionString)) {
			return true;
		}
		return false;
	}
	
	
}
