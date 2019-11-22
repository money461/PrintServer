package com.bondex.cas.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 2018年5月15日 11:26:38
 * @author bondex_public
 *
 */
@Configuration
public class CasConfig {
	@Value("${cas.url}")
	private String localhostIp;

	/*
	 * public CasConfig() throws UnknownHostException { InetAddress addr =
	 * InetAddress.getLocalHost(); String ip = addr.getHostAddress().toString(); //
	 * 获取本机ip localhostIp = ip + ":8183"; }
	 */

	@Bean
	public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> serssionListenerBean() {
		ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> sessionListener = new ServletListenerRegistrationBean<SingleSignOutHttpSessionListener>(new SingleSignOutHttpSessionListener());
		sessionListener.addInitParameter("casServerLogoutUrl", "http://cas.bondex.com.cn:8080/logout");
		sessionListener.addInitParameter("casClientUrl", "http://" + localhostIp);
		return sessionListener;
	}

	@Bean
	public FilterRegistrationBean SingleSignOutFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new SingleSignOutFilter());
		registration.addUrlPatterns("/*");
		registration.setName("CAS Single Sign Out Filter");
		return registration;
	}

	@Bean
	public FilterRegistrationBean AuthenticationFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new AuthenticationFilter());
		registration.addUrlPatterns("/*");
		registration.addInitParameter("casServerLoginUrl", "http://cas.bondex.com.cn:8080/login");
		registration.addInitParameter("serverName", "http://" + localhostIp);
		registration.setName("CASFilter");
		return registration;
	}

	@Bean
	public FilterRegistrationBean Cas20ProxyReceivingTicketValidationFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new Cas20ProxyReceivingTicketValidationFilter());
		registration.addUrlPatterns("/*");
		registration.addInitParameter("casServerUrlPrefix", "http://cas.bondex.com.cn:8080");
		registration.addInitParameter("serverName", "http://" + localhostIp);
		registration.addInitParameter("encoding", "UTF-8");
		registration.setName("CAS Validation Filter");
		return registration;
	}

	@Bean
	public FilterRegistrationBean HttpServletRequestWrapperFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new HttpServletRequestWrapperFilter());
		registration.addUrlPatterns("/*");
		registration.setName("CAS HttpServletRequest Wrapper Filter");
		return registration;
	}

	@Bean
	public FilterRegistrationBean AssertionThreadLocalFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new AssertionThreadLocalFilter());
		registration.addUrlPatterns("/*");
		registration.setName("CAS Assertion Thread Local Filter");
		return registration;
	}

}
