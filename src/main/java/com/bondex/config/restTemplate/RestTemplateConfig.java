package com.bondex.config.restTemplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class RestTemplateConfig {
	// socket读数据超时时间：从服务器获取响应数据的超时时间
	@Value("${restTemplate.socketTimeout}")
	private int SocketTimeout;
	// 连接目标超时
	@Value("${restTemplate.connectTimeout}")
	private int connectTimeout;
	// //从连接池中获取连接的超时时间
	@Value("${restTemplate.connectionRequestTimeout}")
	private int connectionRequestTimeout;
	// 连接池中最大连接数
	@Value("${restTemplate.maxConnTotal}")
	private int MaxConnTotal;
	
	@Autowired
	public HandInterceptor hi;

	@Bean(name="restTemplate")
	public RestTemplate getRestTemplate(RestTemplateBuilder RBuilder) {
		// 生成一个设置了连接超时时间、请求超时时间、异常最大重试次数的httpClient
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(SocketTimeout).build();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(config).setMaxConnTotal(MaxConnTotal).setMaxConnPerRoute(MaxConnTotal).setRetryHandler(new DefaultHttpRequestRetryHandler(3, true));

		HttpClient httpClient = builder.build();
		// 使用httpClient创建一个ClientHttpRequestFactory的实现
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		// ClientHttpRequestFactory作为参数构造一个使用作为底层的RestTemplate
		RBuilder.requestFactory(requestFactory);

		RestTemplate rtl = RBuilder.build();
		List<ClientHttpRequestInterceptor> list = new ArrayList<>();

		rtl.setInterceptors(list);
		return rtl;
	}
	
		@Bean("myRestTemplate")
		public RestTemplate myRestTemplate(RestTemplateBuilder RBuilder) {
			// 生成一个设置了连接超时时间、请求超时时间、异常最大重试次数的httpClient
			RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
					.setConnectTimeout(connectTimeout).setSocketTimeout(SocketTimeout).build();
			HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(config)
					.setMaxConnTotal(MaxConnTotal).setMaxConnPerRoute(MaxConnTotal)
					.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true)); //重试次数

			// 编码
			FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
			FastJsonConfig fastJsonConfig = new FastJsonConfig();
			fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
			fastConverter.setFastJsonConfig(fastJsonConfig);
			// 处理中文乱码问题
			List<MediaType> fastMediaTypes = new ArrayList<MediaType>();
			fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
			fastMediaTypes.add(MediaType.APPLICATION_JSON);
			fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
			fastMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
			fastMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
			fastMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
			fastMediaTypes.add(MediaType.APPLICATION_PDF);
			fastMediaTypes.add(MediaType.APPLICATION_RSS_XML);
			fastMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
			fastMediaTypes.add(MediaType.APPLICATION_XML);
			fastMediaTypes.add(MediaType.IMAGE_GIF);
			fastMediaTypes.add(MediaType.IMAGE_JPEG);
			fastMediaTypes.add(MediaType.IMAGE_PNG);
			fastMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
			fastMediaTypes.add(MediaType.TEXT_HTML);
			fastMediaTypes.add(MediaType.TEXT_MARKDOWN);
			fastMediaTypes.add(MediaType.TEXT_PLAIN);
			fastMediaTypes.add(MediaType.TEXT_XML);
			fastConverter.setSupportedMediaTypes(fastMediaTypes);

			HttpClient httpClient = builder.build();
			// 使用httpClient创建一个ClientHttpRequestFactory的实现
			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			// ClientHttpRequestFactory作为参数构造一个使用作为底层的RestTemplate
			RestTemplate rtl = RBuilder.build();
			rtl.setRequestFactory(requestFactory);
			
					List<HttpMessageConverter<?>> converterList = rtl.getMessageConverters();
					// 重新设置StringHttpMessageConverter字符集为UTF-8，解决中文乱码问题
					List<HttpMessageConverter<?>> converterTarget = new ArrayList<HttpMessageConverter<?>>();
					for (HttpMessageConverter<?> item : converterList) {
						if (item instanceof MappingJackson2HttpMessageConverter) {
							converterTarget.add(item);
						}
					}
					if (null != converterTarget) {
						converterList.removeAll(converterTarget);
					}
					// 加入FastJson转换器
					converterList.add(fastConverter);

			
			rtl.setMessageConverters(converterList);

			List<ClientHttpRequestInterceptor> list = new ArrayList<>();
			list.add(hi);
			rtl.setInterceptors(list);

			//
			return rtl;
		}
	
}
