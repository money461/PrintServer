package com.bondex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.bondex.cas.SpringCasAutoconfig;

/**
 * 注册bean
 * 
 * @author bondex_public
 *
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	private SpringCasAutoconfig springCasAutoconfig;
	
	private String clientloginUrl;
	    
	   //构造器显式传入参数
	public WebMvcConfig(SpringCasAutoconfig springCasAutoconfig) {
		super();
		this.springCasAutoconfig = springCasAutoconfig;
		this.clientloginUrl=springCasAutoconfig.getClientloginUrl();
	}
	
	/**
	 * 注册 拦截器
	 */
	/*@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor(springCasAutoconfig)).addPathPatterns("/**");
	}*/

	 /**
     * 直接转发方式（forward），客户端和浏览器只发出一次请求，Servlet、HTML、JSP或其它信息资源，由第二个信息资源响应该请求，在请求对象request中，保存的对象对于每个信息资源是共享的。
     * 转发是服务器跳转只能去往当前web应用的资源
     * 重定向方式（redirect）实际是两次HTTP请求，服务器端在响应第一次请求的时候，让浏览器再向另外一个URL发出请求，从而达到转发的目的。(可以跳转重定向到任意地址 )
     * 重定向是服务器跳转，可以去往任何的资源
     * 添加多个指定的跳转请求页面
     * 将所有的WebMvcConfigurerAdapter组件都会起作用 注意 @Bean注入spring容器
     */

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:"+clientloginUrl);  //请求重定向最终跳转路径http://localhost:8088/login 2次请求
//		registry.addViewController("/").setViewName("forward:/login");  //请求转发最终路径http://localhost:8088/
		registry.addViewController("/error/403").setViewName("/error/403"); //渲染页面视图
		registry.addViewController("/system/main").setViewName("/system/main"); //渲染首页
		registry.addViewController("/love").setViewName("/LoveCode/loveyou"); //渲染首页
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}
	
}
