package com.bondex.config.freemaker;

import org.apache.shiro.subject.Subject;

import com.jagregory.shiro.freemarker.PermissionTag;
/**
 * spring boot shiro Freemarker 扩展权限标签hasAnyPermissions 可以判断多个权限标签code
 * <@shiro.hasAnyPermissions name="3020101,3020103"></@shiro.hasAnyPermissions>
 * @author Qianli
 * 
 */
public class HasAnyPermissionsTag extends PermissionTag {

	 private static final long serialVersionUID = 1L;
	    private static final String PERMISSION_NAMES_DELIMETER = ",";

	    public HasAnyPermissionsTag() {
	    }

	    @Override
	    protected boolean showTagBody(String permissions) {
	        boolean hasAnyPermission = false;
	        Subject subject = getSubject();
	        if (subject != null) {
	            for (String permission : permissions.split(PERMISSION_NAMES_DELIMETER)) {
	                if (subject.isPermitted(permission.trim())) {
	                    hasAnyPermission = true;
	                    break;
	                }
	            }
	        }
	        return hasAnyPermission;
	    }

}
