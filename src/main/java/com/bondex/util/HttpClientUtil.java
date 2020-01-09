package com.bondex.util;

import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	//@SuppressWarnings("resource")
	public static String doPost(String url, String jsonstr, String charset) {

		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try { 
			httpClient = new SSLClient(); 
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
			StringEntity se = new StringEntity(jsonstr, Charset.forName("UTF-8"));
			se.setContentType("application/json;charset=UTF-8");
			se.setContentEncoding(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));
			httpPost.setEntity(se);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
