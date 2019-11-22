package com.bondex.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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

	@Bean
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
}
