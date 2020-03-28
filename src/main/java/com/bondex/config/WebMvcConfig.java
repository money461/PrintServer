package com.bondex.config;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.bondex.cas.SpringCasAutoconfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

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
		registry.addViewController("/label/airlabel").setViewName("/airlabel/airlabel"); //渲染空运标签
		registry.addViewController("/system/main").setViewName("/system/main"); //渲染首页
		registry.addViewController("/admin/user").setViewName("/admin/user/userlist"); //渲染用户
		registry.addViewController("/admin/labeltemplate").setViewName("/admin/labeltemplate/labeltemplate"); //渲染打印模板管理页面
		registry.addViewController("/label/addtemplate").setViewName("/admin/labeltemplate/add"); //渲染添加打印模板管理页面
		registry.addViewController("/admin/region").setViewName("/admin/region/regionlist"); //渲染区域办公室管理页面
		registry.addViewController("/user/adduser").setViewName("/admin/user/add"); //渲染添加用户页面
		registry.addViewController("/currentlabel/currentlabel").setViewName("/current/currentLabel"); //渲染通用标签
		
		registry.addViewController("/admin/labelLog").setViewName("/admin/log/labelLog"); //渲染标签日志管理页面
		registry.addViewController("/admin/printLog").setViewName("/admin/log/printLog"); //渲染标签日志管理页面
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}
	
	/**
	 * 消息转换器
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// 处理中文乱码问题
		List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
		supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
		supportedMediaTypes.add(MediaType.APPLICATION_PDF);
		supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
		supportedMediaTypes.add(MediaType.APPLICATION_XML);
		supportedMediaTypes.add(MediaType.IMAGE_GIF);
		supportedMediaTypes.add(MediaType.IMAGE_JPEG);
		supportedMediaTypes.add(MediaType.IMAGE_PNG);
		supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
		supportedMediaTypes.add(MediaType.TEXT_HTML);
		supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		supportedMediaTypes.add(MediaType.TEXT_XML);
		
	    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectMapper objectMapper = builder.build();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        //设置日期格式
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	objectMapper.setDateFormat(smt);
        
        objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);// 忽略 transient 修饰的属性
		
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
		mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
		converters.add(mappingJackson2HttpMessageConverter);
		
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.defaultCharset());
		stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
		converters.add(stringHttpMessageConverter);
		
		// 定义convert
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig config = new FastJsonConfig();
		
		  SerializerFeature[] serializerFeatures = new SerializerFeature[]{
				    SerializerFeature.PrettyFormat,
	                //    输出key是包含双引号
//	                SerializerFeature.QuoteFieldNames,
	                //    是否输出为null的字段,若为null 则显示该字段
//	                SerializerFeature.WriteMapNullValue,
	                //    数值字段如果为null，则输出为0
	                SerializerFeature.WriteNullNumberAsZero,
	                //     List字段如果为null,输出为[],而非null
	                SerializerFeature.WriteNullListAsEmpty,
	                //    字符类型字段如果为null,输出为"",而非null
	                SerializerFeature.WriteNullStringAsEmpty,
	                //    Boolean字段如果为null,输出为false,而非null
	                SerializerFeature.WriteNullBooleanAsFalse,
	                //    Date的日期转换器
	                SerializerFeature.WriteDateUseDateFormat,
	                //    循环引用
	                SerializerFeature.DisableCircularReferenceDetect,
	        };
		
		config.setSerializerFeatures(serializerFeatures);
		config.setDateFormat("yyyy-MM-dd HH:mm:ss");
		// 处理中文的乱码问题
		
		// 创建MediaType的集合
		// 设置编码格式为UTF8
		// 将supportedMediaTypes对象赋值给fastJsonHttpMessageConverter的SupportedMediaTypes属性
		fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes );
		fastJsonHttpMessageConverter.setFastJsonConfig(config);
		converters.add(fastJsonHttpMessageConverter);
		super.configureMessageConverters(converters);
		
	}
}
