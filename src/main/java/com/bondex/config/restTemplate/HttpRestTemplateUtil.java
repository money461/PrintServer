package com.bondex.config.restTemplate;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpRestTemplateUtil {

	private static final Logger log = LoggerFactory.getLogger(HttpRestTemplateUtil.class);
    
    private static RestTemplate restTemplate;
    
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
    
    @Resource(name="myRestTemplate")
    public void setRestTemplate(RestTemplate restTemplate) {
    	HttpRestTemplateUtil.restTemplate = restTemplate;
    }    
    
    /**
     * 发送POST请求
     * @param url 请求url
     * @param returnType 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public static final <T> T doPostByJson(String url, Class<T> returnType) {
        return doPostByJson(url, null, returnType);
    }
    
    /**
     * 发送POST请求
     * @param url 请求url
     * @param data 发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param returnType 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public static final <T, E> T doPostByJson(String url, E data, Class<T> returnType) {
        return doPost(url, data, MediaType.APPLICATION_JSON_UTF8, returnType);
    }
    
    /**
     * 发送POST请求
     * @param url 请求url
     * @param data 发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param returnType 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public static final <T> T doPostByFormData(String url, MultiValueMap<String, String> data, Class<T> returnType) {
        return doPost(url, data, MediaType.APPLICATION_FORM_URLENCODED, returnType);
    }
    
    /**
     * 发送GET请求
     * @param url 请求url
     * @param clazz 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public static final <T> T doGet(String url, Class<T> clazz) {
        log.info("GET_REQUEST: {}, {}", url, clazz.getName());
        
        T result = restTemplate.getForObject(url, clazz); //getForObject 返回的是一个对象，这个对象就是服务端返回的具体值。
        log.info("GET_RESPONSE: {}", result);
        
        return result;
    }
    
    /**
     * 发送POST请求
     * @param url 请求url
     * @param data 发送的数据,必须重写toString()方法,否则不能正确记录日志信息
     * @param requestType 请求头类型
     * @param returnType 返回类型,必须重写toString()方法,否则不能正确记录日志信息
     * @return 指定的返回类型
     */
    public static final <T, E> T doPost(String url, E data, MediaType requestType, Class<T> returnType) {
        log.info("POST_REQUEST: {}, {}, {}, {}", url, data, requestType, returnType.getName());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(requestType);
        HttpEntity<E> entity = new HttpEntity<>(data, headers);
        
        T result = restTemplate.postForObject(url, entity, returnType); //postForObject 返回的是一个对象，这个对象就是服务端返回的具体值。
        
        log.info("POST_RESPONSE: {}", result);
        return result;
    }

    
    /**
     * RestTemplate默认使用HttpMessageConverter实例将HTTP消息转换成POJO或者从POJO转换成HTTP消息。默认情况下会注册主mime类型的转换器，但也可以通过setMessageConverters注册自定义转换器。
		RestTemplate使用了默认的DefaultResponseErrorHandler，对40X Bad Request或50X internal异常error等错误信息捕捉。
		RestTemplate还可以使用拦截器interceptor，进行对请求链接跟踪，以及统一head的设置。
		*/
	
}
