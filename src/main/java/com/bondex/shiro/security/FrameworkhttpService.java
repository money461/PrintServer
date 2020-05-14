package com.bondex.shiro.security;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import com.alibaba.fastjson.JSONObject;
import com.bondex.cas.SpringCasAutoconfig;
import com.bondex.common.enums.NewPowerHttpEnum;
import com.bondex.config.restTemplate.HttpRestTemplateUtil;
import com.bondex.shiro.security.entity.UserInfo;

@Component
public class FrameworkhttpService {


	private String applicationId; //应用id
	
	private String frameworkapi;//framework权限获取地址
	
	private SpringCasAutoconfig springCasAutoconfig;
	
	public FrameworkhttpService(SpringCasAutoconfig springCasAutoconfig) {
		super();
		this.springCasAutoconfig = springCasAutoconfig;
		this.applicationId = springCasAutoconfig.getApplicationId();
		this.frameworkapi = springCasAutoconfig.getFrameworkapi();
	}
	
	/**
	 * 获取数据
	 * @param paramap 接口参数
	 * @param userInfo 当前用户信息
	 * @param HttpEnum 请求的接口方法名称 枚举中设置
	 * @return
	 */
	/**
	 * 解释:根据委托单号获取订单客户及其关键字数据 方法:GetKeywordListByReferenceNo referenceNo 委托编号
	 * value值表示当哪些异常的时候触发重试，maxAttempts表示最大重试次数默认为3，delay表示重试的延迟时间，multiplier表示上一次延时时间是这一次的倍数。
	 */
	@Retryable(value = TimeoutException.class,maxAttempts = 2,backoff = @Backoff(delay = 2000,multiplier = 1.5))
	public JSONObject getFrameworkHttp(Map<String, String> paramap, UserInfo userInfo, NewPowerHttpEnum HttpEnum) {
		
		JSONObject jsonObject =null;
		StringBuffer rootUrlStr = new StringBuffer(frameworkapi);

		JSONObject param = new JSONObject();
		param.put("Token", userInfo.getToken()); //cas token
		switch (HttpEnum) {
		case GetHasPermissionModuleList:
			rootUrlStr.append(NewPowerHttpEnum.GetHasPermissionModuleList.url);
			//封装参数
			param.put("ApplicationID", applicationId);
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param); //拼接url
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetHasPermissionPageButton:
			rootUrlStr.append(NewPowerHttpEnum.GetHasPermissionPageButton.url);
			param.put("ApplicationID", applicationId);
			param.put("PageCode", paramap.get("PageCode"));
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetPageButtonByPermissionValue:
			rootUrlStr.append(NewPowerHttpEnum.GetPageButtonByPermissionValue.url);
			param.put("ApplicationID", applicationId);
			param.put("PageCode", paramap.get("PageCode")); //页面代码code
			param.put("PreButtonName", paramap.get("PreButtonName")); //前缀
			param.put("PermissionValue", paramap.get("PermissionValue")); //访问权限值
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetOperatorPagePermission:
			rootUrlStr.append(NewPowerHttpEnum.GetOperatorPagePermission.url);
			param.put("ApplicationID", applicationId);
			//param.put("PageCode", paramap.get("PageCode")); //页面代码code
			param.put("OperatorID", userInfo.getOpid()); //操作id
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetUserRoleOp:
			rootUrlStr.append(NewPowerHttpEnum.GetUserRoleOp.url);
			param.put("ApplicationID", applicationId);
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetCompanyInfoOfDeptByOperatorID:
			rootUrlStr.append(NewPowerHttpEnum.GetCompanyInfoOfDeptByOperatorID.url);
			param.put("OperatorID", userInfo.getOpid());
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetFlowNo:
			rootUrlStr.append(NewPowerHttpEnum.GetFlowNo.url);
			param.put("ApplicationID", applicationId);
			param.put("EmsID", userInfo.getOpid());
			param.put("ProFile", paramap.get("ProfitId"));
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetOperator:
			rootUrlStr.append(NewPowerHttpEnum.GetOperator.url);
			urlGetMethodFrameWork(rootUrlStr, param);
			jsonObject = HttpRestTemplateUtil.doGet(rootUrlStr.toString(), JSONObject.class);
			return jsonObject;
			
		case GetModulePrintButton:
			rootUrlStr.append(NewPowerHttpEnum.GetModulePrintButton.url);
			HttpHeaders headers = new HttpHeaders();
		    headers.add("Accept", MediaType.ALL_VALUE);
		    headers.add("Token",userInfo.getToken());
		    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			jsonObject = HttpRestTemplateUtil.doPost(rootUrlStr.toString(),new LinkedMultiValueMap<>(), headers,JSONObject.class);
			return jsonObject;
		default:
			break;
		}
		return jsonObject;
	}

	//GET请求拼接参数 获取完整的ApiUrl
	private void urlGetMethodFrameWork(StringBuffer rootUrlStr, JSONObject objpare) {
		Set<String> set = objpare.keySet();
		String[] keys = new String[set.size()];
		set.toArray(keys);
		for (int i = 0; i < keys.length; i++) {
			if (i == 0) {
				rootUrlStr.append("?");
			}
			String keyStr = keys[i];
			rootUrlStr.append(keyStr + "=" + objpare.getString(keyStr));
			if (keys.length - 1 != i) {
				rootUrlStr.append("&");
			}
		}
	}
	
}
