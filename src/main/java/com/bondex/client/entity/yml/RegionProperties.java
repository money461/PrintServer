package com.bondex.client.entity.yml;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "region")
@Component
public class RegionProperties {
	private String[] chengdu;
	private String[] qingdao;

	public String[] getChengdu() {
		return chengdu;
	}

	public void setChengdu(String[] chengdu) {
		this.chengdu = chengdu;
	}

	public String[] getQingdao() {
		return qingdao;
	}

	public void setQingdao(String[] qingdao) {
		this.qingdao = qingdao;
	}

}
