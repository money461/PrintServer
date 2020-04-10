package com.bondex.shiro.config;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;
/**
 * 
 * shiro 默认使用 WildcardPermissionResolver
 * 
 * 2020年4月9日 下午1:40:00
 */
public class BitAndWildPermissionResolver implements PermissionResolver {
		@Override  
	    public Permission resolvePermission(String permissionString) {  
	        if(permissionString.endsWith("_label")) {  
	            return new BitPermission(permissionString);  
	        }  
	        return new WildcardPermission(permissionString);  //默认通配符比较实列
	    }  
}
