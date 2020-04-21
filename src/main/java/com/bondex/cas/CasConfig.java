package com.bondex.cas;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @version 2018年5月15日 11:26:38
 * @author bondex_public
 *
 */
@Configuration
public class CasConfig {
	
	private  final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SpringCasAutoconfig autoconfig; 
	
	private static boolean casEnabled = true; 

	@Autowired
	private ServletContext servletContext;
	
	public CasConfig(SpringCasAutoconfig autoconfig ) {
		this.autoconfig = autoconfig;
	}
	/**
	   * 注册监听HttpSession用于实现单点登出功能
	   */
	@Bean
	public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> serssionListenerBean() {
		ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> sessionListener = new ServletListenerRegistrationBean<SingleSignOutHttpSessionListener>(new SingleSignOutHttpSessionListener());
		servletContext.setInitParameter("casServerUrlPrefix", autoconfig.getCasServerUrlPrefix());
		servletContext.setInitParameter("casServerLogoutUrl", autoconfig.getCasServerLogoutUrl());
		servletContext.setInitParameter("casServerLoginUrl", autoconfig.getCasServerLoginUrl());
		servletContext.setInitParameter("casClientUrl", autoconfig.getCasClientUrl()); //登出后重定向地址
		try {
			sessionListener.setEnabled(casEnabled);
			sessionListener.onStartup(servletContext);
			sessionListener.setListener(new SingleSignOutHttpSessionListener());
			sessionListener.setOrder(Ordered.HIGHEST_PRECEDENCE);//设置优先级
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return sessionListener;
	}
	

  /**
   * 该过滤器用于实现单点登出功能，单点退出配置，一定要放在其他filter之前
   */
	@Bean
	public FilterRegistrationBean SingleSignOutFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new SingleSignOutFilter());
		registration.setEnabled(casEnabled);
		if(autoconfig.getSignOutFilters().size()>0){
			registration.setUrlPatterns(autoconfig.getSignOutFilters());
		}else{
			registration.addUrlPatterns("/*");
		}
		registration.addInitParameter("casServerUrlPrefix", autoconfig.getCasServerUrlPrefix());
		registration.setOrder(2);
		registration.setName("CAS Single Sign Out Filter");
		return registration;
	}
	
	  /**
	   * 该过滤器负责用户的认证工作
	   */
	@Bean
	public FilterRegistrationBean AuthenticationFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		AuthenticationFilter authenticationFilter = new AuthenticationFilter();
		//自定义权限过滤器忽略拦截规则
		/*authenticationFilter.setIgnoreUrlPatternMatcherStrategyClass(new UrlPatternMatcherStrategy() {
					
		    *//**
		     * description:正则表达式的规则，该规则在配置AuthenticationFilter的ignorePattern中设置
		     * @param: [pattern]
		     * @return: void
		     *//*
			@Override
			public void setPattern(String pattern) {
				patternUrl = Pattern.compile(pattern);
			}
			
			@Override
			public boolean matches(String url) {
				//使用正则表达式来匹配需要忽略的连接
                  return this.pattern.matcher(url).find();
			}
		});*/
		
		registration.setFilter(authenticationFilter); //AuthenticationFilter 该过滤器负责CAS用户的认证工作  可以重新实现
		registration.setEnabled(casEnabled);
		
	    if(autoconfig.getAuthFilters().size()>0){
	    	registration.setUrlPatterns(autoconfig.getAuthFilters());//过滤指定路径
	    } else{
	    	registration.addUrlPatterns("/*"); //过滤所有路径
	    }
		
//		registration.addInitParameter("casServerLoginUrl", "http://cas.bondex.com.cn:8080/login");
//		registration.addInitParameter("serverName", path);
		Map<String,String>  initParameters = new HashMap<String, String>();
		initParameters.put("casServerLoginUrl", autoconfig.getCasServerLoginUrl()); ////casServerLoginUrl:cas服务的登陆url
		initParameters.put("serverName", autoconfig.getLocalServerName()); ////本项目登录ip+port
		initParameters.put("useSession", autoconfig.isUseSession()?"true":"false");
		initParameters.put("redirectAfterValidation", autoconfig.isRedirectAfterValidation()?"true":"false");
		//忽略拦截符合正则表达式路径
//        initParameters.put("ignorePattern", "/order/getEmail|/order/send"); //多个使用| 	指明两项之间的一个选择。要匹配 |，请使用 \|。
        initParameters.put("ignorePattern", autoconfig.getIgnorePattern()); //多个使用| 	指明两项之间的一个选择。要匹配 |，请使用 \|。
        //设置正则表达式忽略路径
//      initParameters.put("ignoreUrlPatternType", "自定义权限过滤器忽略拦截规则类UrlPatternMatcherStrategy");
        registration.setInitParameters(initParameters);
        //设定加载的顺序
        registration.setOrder(3);
		registration.setName("CAS Authentication Filter"); //名称
		return registration;
	}
	
	
	/**
	   * 该过滤器负责对Ticket的校验工作
	   */
	@Bean
	public FilterRegistrationBean Cas20ProxyReceivingTicketValidationFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new Cas20ProxyReceivingTicketValidationFilter());
		 if(autoconfig.getValidateFilters().size()>0){
			 registration.setUrlPatterns(autoconfig.getValidateFilters());
		 }else{
			 registration.addUrlPatterns("/*");
		 }
		registration.addInitParameter("casServerUrlPrefix", autoconfig.getCasServerUrlPrefix());
		registration.addInitParameter("serverName", autoconfig.getLocalServerName()); //本地项目地址
		registration.addInitParameter("encoding", "UTF-8");
		registration.addInitParameter("useSession",  autoconfig.isUseSession()?"true":"false");
		registration.setEnabled(casEnabled);
		registration.setOrder(4);
		registration.setName("CAS Validation Filter");
		return registration;
	}

	
	/**
	   * 该过滤器对HttpServletRequest请求包装， 可通过HttpServletRequest的getRemoteUser()方法获得登录用户的登录名
	   *
	   */
	@Bean
	public FilterRegistrationBean HttpServletRequestWrapperFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new HttpServletRequestWrapperFilter());
		registration.setEnabled(casEnabled);
		if(autoconfig.getRequestWrapperFilters().size()>0){
			registration.setUrlPatterns(autoconfig.getRequestWrapperFilters());
		} else{
			registration.addUrlPatterns("/*");
		}
		registration.setOrder(5);
		registration.setName("CAS HttpServletRequest Wrapper Filter");
		return registration;
	}

	 /**
	   * 该过滤器使得可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。
	   	比如AssertionHolder.getAssertion().getPrincipal().getName()。
	   	这个类把Assertion信息放在ThreadLocal变量中，这样应用程序不在web层也能够获取到当前登录信息
	   */
	@Bean
	public FilterRegistrationBean AssertionThreadLocalFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new AssertionThreadLocalFilter());
		 if(autoconfig.getAssertionFilters().size()>0){
			 registration.setUrlPatterns(autoconfig.getAssertionFilters());
		 }else{
			 registration.addUrlPatterns("/*");
		 }
		 registration.setOrder(6);
		 registration.setName("CAS Assertion Thread Local Filter");
		return registration;
	}
	
	public static void main(String[] args) {
		String regex="/order/getEmail|/order/send|/asa/d";
		Pattern pattern = Pattern.compile(regex);
		System.out.println(pattern.matcher("http://127.0.0.1:8088/order/getEmail").find());
		
	}

}
