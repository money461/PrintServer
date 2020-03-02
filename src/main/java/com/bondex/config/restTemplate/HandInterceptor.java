package com.bondex.config.restTemplate;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class HandInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		// TODO Auto-generated method stub
		HttpHeaders headers = request.getHeaders();
		headers.add("postman-token", "c9cdb78d-8829-972b-1b52-d7f8034079b7");
		// 保证请求继续被执行
		return execution.execute(request, body);
	}
}
