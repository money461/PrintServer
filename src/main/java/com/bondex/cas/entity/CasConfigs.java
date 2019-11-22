package com.bondex.cas.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:cas-config.properties")
@ConfigurationProperties(prefix = "cas")
@Component
public class CasConfigs {
	
	@Value("${server.context-path}")
	private  String contextpath;
	
	public String getContextpath() {
		return contextpath;
	}

	public void setContextpath(String contextpath) {
		this.contextpath = contextpath;
	}

	private String loadMore;

	public String getLoadMore() {
		return loadMore;
	}

	public void setLoadMore(String loadMore) {
		this.loadMore = loadMore;
	}
	
	

}
