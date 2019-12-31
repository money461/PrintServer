package com.bondex.util;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


public class Axis2WebServiceClient {
	
	/**
	 * org.slf4j.Logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(Axis2WebServiceClient.class);

	static ThreadLocal<RPCServiceClient> li = new ThreadLocal<RPCServiceClient>();
	static ThreadLocal<Integer> size = new ThreadLocal<Integer>();

	static OMFactory fac = OMAbstractFactory.getOMFactory();

//	static EndpointReference targetEPR = new EndpointReference("http://app.bondex.com.cn/AlcedoService/OrdersService.svc?singleWsdl");
	static EndpointReference targetEPR = new EndpointReference("http://api.bondex.com.cn:12360/SupportPlatService/PermissionService.svc?singleWsdl");

	/**
	 * 
	 * @param mappara
	 *            参数
	 * @param methodName
	 *            接口方法名称
	 * @param responeMethod
	 *            接口返回结果名称
	 * @return
	 */
	public static JSONObject getWebServicePararm(LinkedHashMap <String, String> mappara, String methodName) {
			// 这个和qname差不多，设置命名空间
			OMNamespace omNs = fac.createOMNamespace("http://tempuri.org/", methodName);
			OMElement data = fac.createOMElement(methodName, omNs);
		
			for (String key : mappara.keySet()) {
				OMElement inner = fac.createOMElement(key, omNs);
				inner.setText(mappara.get(key));
				data.addChild(inner);
			}
			RPCServiceClient sender = li.get();
			Options options = null;
			if (sender == null) {
				try {
					sender = new RPCServiceClient();
				} catch (AxisFault e) {
					e.printStackTrace();
				}
				options = sender.getOptions();
				li.set(sender);
				// 超时时间20s
				options.setTimeOutInMilliSeconds(15000);
				options.setTo(targetEPR);
				options.setProperty(HTTPConstants.SO_TIMEOUT, 20000);
				options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, 20000);
				options.setManageSession(true);
				options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
			} else {
				options = sender.getOptions();
			}
			options.setAction("http://tempuri.org/IPermissionService/" + methodName);
			OMElement result = null;
			Integer exSize = size.get();
			
			try {
				//发送请求
				result = sender.sendReceive(data);
				@SuppressWarnings("unchecked")
				Iterator<OMElement> rs = result.getChildren();
				JSONObject jsob = new JSONObject();
				while (rs.hasNext()) {
					OMElement oe = rs.next();
					String resultWeb = oe.getText();
//					logger.debug("接口数据信息==>>{}",resultWeb);
					if (resultWeb != null && !"".equals(resultWeb)) {
						jsob.put(oe.getLocalName(), JSONObject.parseObject(resultWeb));
					}
				}
				size.set(0);
				return jsob;
			} catch (AxisFault e) {
				size.set(exSize == null ? 1 : exSize + 1);
			} finally {
				try {
					// System.out.println(Thread.currentThread().getName() + "::" +
					// li.get());
					sender.cleanupTransport();
					if (exSize != null && exSize == 3) {
						sender.cleanup();
						li.set(null);
					}
				} catch (AxisFault e) {
					e.printStackTrace();
				}
			 }
		
			return null;
	}

	public static void main(String[] args) {

		LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
		hashMap.put("referenceNo", "AE028181115479");

		/*new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					JSONObject oi = getWebServicePararm(hashMap, "GetKeywordListByReferenceNo");
					System.out.println(oi.getJSONObject("GetKeywordListByReferenceNoResult"));
				}
			}
		}).start();

		while (true) {
			System.out.println(getWebServicePararm(hashMap, "GetKeywordListByReferenceNo"));
		}
		
		 */
	}
		
}
