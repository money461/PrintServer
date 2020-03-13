package com.bondex.cas;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.bondex.util.IpUtil;

/**
 * CAS 相关配置信息
 * @author Qianli
 * 
 * 2019年12月12日 下午3:13:46
 */
@Component
@ConfigurationProperties(prefix = "spring.cas")
@PropertySource(value="classpath:config/casconfig.properties",ignoreResourceNotFound=true,encoding="utf-8")
public class SpringCasAutoconfig {

	@Value("${spring.profiles.active}")
	private String active;
	
	@Value("${server.port}")
	private String port;
	@Value("${server.applicationId}")
	private String applicationId; //应用id
	
	@Value("${spring.framework.HttpUrl}") 
	private String frameworkapi; //framework权限获取地址

	static final String separator = "|";

	private String validateFilters;
	private String signOutFilters;
	private String authFilters;
	private String ignorePattern; // 正则表达式 代表cas-authFilters忽略符合该表达式的路径。
	private String assertionFilters;
	private String casFilterUrlPattern; //casFilter 过滤器在shiroFilter过滤链中的规则
	private String requestWrapperFilters;
	private boolean useSession = true;
	private boolean redirectAfterValidation = true;

	private String casServerUrlPrefix; // cas服务端地址前缀
	private String casServerLoginUrl; // cas服务端地址前缀登陆地址
	private String casServerLogoutUrl; // cas服务端地址前缀退出地址
	
	private String localServerName; // 本地项目地址
	private String localServerSuccessUrl; //项目认证成功后重定向
	private String localServerUnauthorizedUrl; //项目未授权跳转地址
	
	private String clientloginUrl; //客户端登陆请求 cas回调地址
	
	private String casClientUrl; //CAS登出后重定向至该地址

	private String casPublicUrl; // CAS 公共账户登陆地址
	
	private String loadMore; // 获取opids接口url

	public List<String> getValidateFilters() {
		return Arrays.asList(validateFilters.split(separator));
	}

	public void setValidateFilters(String validateFilters) {
		this.validateFilters = validateFilters;
	}

	public List<String> getSignOutFilters() {
		return Arrays.asList(signOutFilters.split(separator));
	}

	public void setSignOutFilters(String signOutFilters) {
		this.signOutFilters = signOutFilters;
	}

	public List<String> getAuthFilters() {
		return Arrays.asList(authFilters.split(separator));
	}

	public void setAuthFilters(String authFilters) {
		this.authFilters = authFilters;
	}

	public String getIgnorePattern() {
		return ignorePattern;
	}

	public void setIgnorePattern(String ignorePattern) {
		this.ignorePattern = ignorePattern;
	}

	public List<String> getAssertionFilters() {
		return Arrays.asList(assertionFilters.split(separator));
	}

	public void setAssertionFilters(String assertionFilters) {
		this.assertionFilters = assertionFilters;
	}

	public List<String> getRequestWrapperFilters() {
		return Arrays.asList(requestWrapperFilters.split(separator));
	}
	
	public String getCasFilterUrlPattern() {
		return casFilterUrlPattern;
	}

	public void setCasFilterUrlPattern(String casFilterUrlPattern) {
		this.casFilterUrlPattern = casFilterUrlPattern;
	}

	public void setRequestWrapperFilters(String requestWrapperFilters) {
		this.requestWrapperFilters = requestWrapperFilters;
	}

	public String getCasServerUrlPrefix() {
		return casServerUrlPrefix;
	}

	public void setCasServerUrlPrefix(String casServerUrlPrefix) {
		this.casServerUrlPrefix = casServerUrlPrefix;
	}

	public String getCasServerLoginUrl() {
		return casServerLoginUrl;
	}

	public void setCasServerLoginUrl(String casServerLoginUrl) {
		this.casServerLoginUrl = casServerLoginUrl;
	}

	public String getCasServerLogoutUrl() {
		return casServerLogoutUrl;
	}

	public void setCasServerLogoutUrl(String casServerLogoutUrl) {
		this.casServerLogoutUrl = casServerLogoutUrl;
	}

	public String getLocalServerName() {
		
//		if ("dev".equals(ApplicationContextProvider.getActiveProfile())) {
		if ("test".equals(active)) {
			return "http://" + IpUtil.getIntranetIp() + ":" + port;
		}
		return localServerName;
	}

	public void setLocalServerName(String localServerName) {
		this.localServerName = localServerName;
	}
	
	public String getClientloginUrl() {
		return clientloginUrl;
	}

	public void setClientloginUrl(String clientloginUrl) {
		this.clientloginUrl = clientloginUrl;
	}

	public boolean isRedirectAfterValidation() {
		return redirectAfterValidation;
	}

	public void setRedirectAfterValidation(boolean redirectAfterValidation) {
		this.redirectAfterValidation = redirectAfterValidation;
	}

	public boolean isUseSession() {
		return useSession;
	}

	public void setUseSession(boolean useSession) {
		this.useSession = useSession;
	}

	public String getCasPublicUrl() {
		return casPublicUrl;
	}

	public void setCasPublicUrl(String casPublicUrl) {
		this.casPublicUrl = casPublicUrl;
	}

	public String getLoadMore() {
		return loadMore;
	}

	public void setLoadMore(String loadMore) {
		this.loadMore = loadMore;
	}

	public String getApplicationId() {
		return applicationId;
	}

	
	public String getLocalServerSuccessUrl() {
		return localServerSuccessUrl;
	}

	public void setLocalServerSuccessUrl(String localServerSuccessUrl) {
		this.localServerSuccessUrl = localServerSuccessUrl;
	}

	public String getLocalServerUnauthorizedUrl() {
		return localServerUnauthorizedUrl;
	}

	public void setLocalServerUnauthorizedUrl(String localServerUnauthorizedUrl) {
		this.localServerUnauthorizedUrl = localServerUnauthorizedUrl;
	}

	public String getCasClientUrl() {
		return casClientUrl;
	}

	public void setCasClientUrl(String casClientUrl) {
		this.casClientUrl = casClientUrl;
	}

	public String getFrameworkapi() {
		return frameworkapi;
	}

	public void setFrameworkapi(String frameworkapi) {
		this.frameworkapi = frameworkapi;
	}
	
	
	
	
}