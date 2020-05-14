package com.bondex.config.exception;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
/**
 * 重写BasicErrorController,主要负责系统的异常页面的处理以及错误信息的显示
 * <p/>
 * 此处指需要记录
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/16 16:26
 * <p/>
 * 要注意，这个类里面的代码一定不能有异常或者潜在异常发生，否则可能会让程序陷入死循环。
 * 处理容器级别的错误，比如 Filter 中抛出的异常
 * <p/>
 * @since 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
//@EnableConfigurationProperties({ServerProperties.class})
public class GlobalBasicErrorHandler extends BasicErrorController  {
	   private static final Logger log = LoggerFactory.getLogger(GlobalBasicErrorHandler.class);
	   
	   @Value("${server.error.path:${error.path:/error}}")
	    private static String errorPath = "/error";
	    
	  /*  @Autowired
	    private ServerProperties serverProperties;
	   
	   private ErrorAttributes errorAttributes;*/
	   
	     @Autowired
	    public GlobalBasicErrorHandler(ErrorAttributes errorAttributes, ServerProperties serverProperties, List<ErrorViewResolver> errorViewResolvers) {
	        super(errorAttributes, serverProperties.getError(), errorViewResolvers);
	    }
	     
	   /**
	       在resource/templates下添加error.html页面，springBoot会自动找到该页面作为错误页面，适合内嵌Tomcat或者war方式。
	    SpringBoot错误视图提供了以下错误属性：
	    timestamp：错误发生时间；
		status：HTTP状态吗；
		error：错误原因；
		exception：异常的类名；
		message：异常消息（如果这个错误是由异常引起的）；
		errors：BindingResult异常里的各种错误（如果这个错误是由异常引起的）；errors：JSR303数据校验的错误都在这里
		trace：异常跟踪信息（如果这个错误是由异常引起的）；
		path：错误发生时请求的URL路径。
	    */
	     
	     @Override
	     @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	     public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
	         HttpStatus status = this.getStatus(request);
	         response.setStatus(status.value());
	         log.debug("*错误页面跳转****SpringBoot容器级别错误,非业务类异常,Http状态码：{} *******",response.getStatus());
	         // 获取 Spring Boot 默认提供的错误信息，然后添加一个自定义的错误信息
	         Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
	         
	         ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
	         modelAndView = modelAndView == null ? new ModelAndView("/error/error", model,status) : modelAndView;
	         return modelAndView;
	     }
	     
	     @Override
	     public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
	         HttpStatus status = getStatus(request);
	         log.debug("*返回JSON格式错误****SpringBoot容器级别错误,非业务类异常,Http状态码：{} *******",status.value());
	         // 获取 Spring Boot 默认提供的错误信息，然后添加一个自定义的错误信息
	         Map<String, Object> body = this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.ALL));
	         body.put("msg", "出错啦------");
	         return new ResponseEntity<>(body, status);
	     }
	     
/*
	 	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE,value = "/404")
	    public ModelAndView errorHtml404(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest) {
	        response.setStatus(HttpStatus.NOT_FOUND.value());
	        Map<String, Object> model = getErrorAttributes(webRequest, isIncludeStackTrace(request, MediaType.TEXT_HTML));

	        return new ModelAndView("error/404", model);
	    }

	    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE,value = "/403")
	    public ModelAndView errorHtml403(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest) {
	        response.setStatus(HttpStatus.FORBIDDEN.value());
	        // 404拦截规则，如果是静态文件发生的404则不记录到DB
	        Map<String, Object> model = getErrorAttributes(webRequest, isIncludeStackTrace(request, MediaType.TEXT_HTML));
	        if (!String.valueOf(model.get("path")).contains(".")) {
	            model.put("status", HttpStatus.FORBIDDEN.value());
	        }
	        return new ModelAndView("error/403", model);
	    }

	    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE,value = "/400")
	    public ModelAndView errorHtml400(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest) {
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        Map<String, Object> model = getErrorAttributes(webRequest, isIncludeStackTrace(request, MediaType.TEXT_HTML));
	        return new ModelAndView("error/400", model);
	    }

	    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE,value = "/401")
	    public ModelAndView errorHtml401(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest) {
	        response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        Map<String, Object> model = getErrorAttributes(webRequest, isIncludeStackTrace(request, MediaType.TEXT_HTML));
	        return new ModelAndView("error/401", model);
	    }

	    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE,value = "/500")
	    public ModelAndView errorHtml500(HttpServletRequest request, HttpServletResponse response, WebRequest webRequest) {
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        Map<String, Object> model = getErrorAttributes(webRequest, isIncludeStackTrace(request, MediaType.TEXT_HTML));
	        return new ModelAndView("error/500", model);
	    }

	    *//**
	     * Determine if the stacktrace attribute should be included.
	     *
	     * @param request  the source request
	     * @param produces the media type produced (or {@code MediaType.ALL})
	     * @return if the stacktrace attribute should be included
	     *//*
	    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
	        ErrorProperties.IncludeStacktrace include = this.serverProperties.getError().getIncludeStacktrace();
	        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
	            return true;
	        }
	        return include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM && getTraceParameter(request);
	    }


	    *//**
	     * 获取错误的信息
	     *
	     * @param webRequest
	     * @param includeStackTrace
	     * @return
	     *//*
	    private Map<String, Object> getErrorAttributes(WebRequest webRequest,boolean includeStackTrace) {
	        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
	    }
*/

	    /**
	     * 实现错误路径,暂时无用
	     *
	     * @return
	     */
	    @Override
	    public String getErrorPath() {
	        return errorPath;
	    }

	
}
