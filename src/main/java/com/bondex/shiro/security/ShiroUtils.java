package com.bondex.shiro.security;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.bondex.common.Common;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.realm.ShiroRealm;
import com.bondex.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

/**
 * shiro 工具类
 * 
 * @author ruoyi
 */
public class ShiroUtils
{
    public static Subject getSubject()
    {
        return SecurityUtils.getSubject();
    }

    public static Session getSession()
    {
        return SecurityUtils.getSubject().getSession();
    }
    
    public static UserInfo getUserInfo(){
    	return (UserInfo)getSubject().getPrincipal();
    }
    
    public static List<JsonResult> getUserPrintTemplateInfo(){
    	Session session = getSession();
		UserInfo userInfo = ShiroUtils.getUserInfo();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
         Object object = map.get(userInfo.getOpid() + Common.UserSecurity_PrintButton);
		//标签权限
		Type objectType = new TypeToken<List<List<JsonResult>>>() {}.getType();
		List<List<JsonResult>> jsonResultsList = GsonUtil.getGson().fromJson(GsonUtil.GsonString(object), objectType);
		List<JsonResult> jsonResults =jsonResultsList.get(0);	
		return jsonResults;
    }

    public static void logout()
    {
        getSubject().logout();
    }

    /*
     * 清除授权
     */
    public static void clearCachedAuthorizationInfo()
    {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        //多个realm 遍历区分清除哪个realm认证的权限
        Iterator<Realm> iterator = rsm.getRealms().iterator();
        while (iterator.hasNext()) {
        	ShiroRealm realm = (ShiroRealm) iterator.next();
        	realm.clearCachedAuthorizationInfo(getSubject().getPrincipals());
		}
        
    }
    /*
     * 清除认证
     */
    public static void clearCachedAuthenticationInfo()
    {
    	RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
    	//多个realm 遍历区分清除哪个realm认证的权限
    	Iterator<Realm> iterator = rsm.getRealms().iterator();
    	while (iterator.hasNext()) {
    		ShiroRealm realm = (ShiroRealm) iterator.next();
    		realm.clearCachedAuthenticationInfo(getSubject().getPrincipals());
    	}
    	
    }


    public static String getIp()
    {
        return getSubject().getSession().getHost();
    }

    public static String getSessionId()
    {
        return String.valueOf(getSubject().getSession().getId());
    }
}
