package com.bondex.aspect;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * . 使用AOP统一处理Web请求日志
 * @author Qianli
 * 
 * 2018年6月1日 下午7:10:30
 */
@Aspect
@Component
public class WebLogAspect {

	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	    ThreadLocal<Long> startTime = new ThreadLocal<>();

	    @Pointcut("execution(* com.bondex..*Controller.*(..))")
	    public void webLog(){}

	    @Before("webLog()")
	    public void doBefore(JoinPoint joinPoint) throws Throwable {
	        startTime.set(System.currentTimeMillis());

	        // 接收到请求，记录请求内容
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	        HttpServletRequest request = attributes.getRequest();
	        
	        // 记录下请求内容
	        logger.debug("跳转URL-Referer: "+request.getHeader("Referer"));
	        logger.debug("请求URL : " + request.getRequestURL().toString());
	        logger.debug("HTTP_METHOD : " + request.getMethod());
	        logger.debug("CLIENT_IP : " + this.getRealIp(request));
	        logger.debug("User-Agent : "+request.getHeader("User-Agent"));
	        logger.debug("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//	        logger.debug("METHOD_PARAM_NAMES : " + Arrays.toString( ((CodeSignature)joinPoint.getStaticPart().getSignature()).getParameterNames()));
	        logger.debug("METHOD_PARAM_NAMES : " + Arrays.toString( ((MethodSignature)joinPoint.getSignature()).getParameterNames()));
	        logger.debug("PARAM_VALUES : " + Arrays.toString(joinPoint.getArgs()));

	        Enumeration<String> e = request.getParameterNames();
	        while(e.hasMoreElements()){
	        	String param = (String)e.nextElement();//调用nextElement方法获得元素
	        	logger.debug("{}={}",param,request.getParameter(param));
	        }
	        
	    }

	    @AfterReturning(returning = "ret", pointcut = "webLog()")
	    public void doAfterReturning(Object ret) throws Throwable {
	        // 处理完请求，返回内容
	        logger.debug("RESPONSE : " + ret);
	        logger.debug("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
	        logger.debug("--------------------------响应分割线---------------------------------");
	    }
	    
	    @AfterThrowing(pointcut = "webLog()", throwing = "ex")
	    public void afterThrowing(JoinPoint joinPoint, Throwable ex) throws Throwable {
	        logger.error("捕获到了异常...", ex);
	    }

	    
	    /**
	     * 获取客户端真实IP
	     *
	     * @param request
	     * @return
	     */
	    public static String getRealIp(HttpServletRequest request) {
	        String ip = request.getHeader("x-forwarded-for");
	        return checkIp(ip) ? ip : (
	                checkIp(ip = request.getHeader("Proxy-Client-IP")) ? ip : (
	                        checkIp(ip = request.getHeader("WL-Proxy-Client-IP")) ? ip :
	                                request.getRemoteAddr()));
	    }

	    
	    /**
	     * 校验IP
	     *
	     * @param ip
	     * @return
	     */
	    private static boolean checkIp(String ip) {
	        return !StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip);
	    }
}
