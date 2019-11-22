package com.bondex.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tempuri1.IPermissionService;
import org.tempuri1.IPermissionServiceProxy;

import com.bondex.security.entity.OperatorIdData;
import com.bondex.security.entity.OperatorIdResult;
import com.google.gson.Gson;

@Controller
public class Test {
	private String applicationId = "827";
	private String operatorId = "280600";
	Gson gson = new Gson();

	@RequestMapping("qxtest")
	public void test(HttpServletRequest request, HttpSession session) throws RemoteException {
		Object object = session.getAttribute("userInfo");
		System.out.println(object);
		Map<String, String> map = (Map<String, String>) object;
		String param = "token=" + map.get("token");
		String aa = sendPost("http://cas.bondex.com.cn:8080/loadMore.jsp", param);
		System.out.println(aa);
		OperatorIdResult idResult = gson.fromJson(aa, OperatorIdResult.class);
		OperatorIdData[] data = idResult.getMessage();
		OperatorIdData data2 = data[0];
		Map<String, String> map2 = data2.getOpids();
		// 设置操作id

		IPermissionService permissionService = new IPermissionServiceProxy();
		// 用户功能模块权限
		String rt = permissionService.getHasPermissionModuleList(applicationId, operatorId);
		System.err.println(rt);
		// 用户功能模块按钮权限
		String button = permissionService.getHasPermissionPageButton(applicationId, "AirPrintLabel", null, operatorId);
		System.err.println(button);

	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			/*
			 * out = new PrintWriter(conn.getOutputStream()); // 发送请求参数 out.print(param); //
			 * flush输出流的缓冲 out.flush();
			 */

			out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
			out.write(param);
			out.flush();

			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
