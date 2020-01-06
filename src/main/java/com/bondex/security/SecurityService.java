package com.bondex.security;

import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.bondex.cas.SpringCasAutoconfig;
import com.bondex.common.Common;
import com.bondex.entity.Datagrid;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.SecurityHead;
import com.bondex.security.entity.SecurityModel;
import com.bondex.security.entity.TokenResult;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.Axis2WebServiceClient;
import com.bondex.util.GsonUtil;
import com.bondex.util.HttpClient;
import com.google.gson.reflect.TypeToken;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User;
/**
 * 权限获取模块
 * @author Qianli
 * 
 * 2019年12月26日 上午10:31:10
 */
@Component
public class SecurityService {

	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private String applicationId; //应用id
	
	private SpringCasAutoconfig springCasAutoconfig;
	
	public SecurityService(SpringCasAutoconfig springCasAutoconfig) {
		super();
		this.springCasAutoconfig = springCasAutoconfig;
		this.applicationId = springCasAutoconfig.getApplicationId();
	}

	
	//获取用户信息 存入session 并且绑定最新与Token opid
	public UserInfo setSerlvetContext(String username){
		UserInfo userInfo=null;
				if (null != username) {
					Session session = SecurityUtils.getSubject().getSession();
					// log.debug(account);
					TokenResult result = GsonUtil.GsonToBean(username, TokenResult.class);
					List<UserInfo> userInfoList = result.getMessage();
					userInfo = userInfoList.get(0);
					
					List<Opid> opids = new ArrayList<Opid>();
					Map<String, String> opidsMap = userInfo.getOpids();
				
					for (String opid : opidsMap.keySet()) {
						Opid opid2 = new Opid(opid, opidsMap.get(opid));
						opids.add(opid2);
					}
					// 将opids 返回当前用户对应的一至多个操作ID
					userInfo.setAllOpid(opids);
					
					// 绑定用户默认的opid
					String getBindingOpid = GetBindingOpid(userInfo.getToken());
					if(userInfo.getAllOpid().size()==1){ //只有一个号时，绑定默认的 
						getBindingOpid = userInfo.getAllOpid().get(0).getOpid();//默认第一个号作为登陆
						BindingOpid(getBindingOpid, userInfo.getToken()); //绑定一个默认的opid
					}
					  userInfo.setOpid(getBindingOpid);
					  userInfo.setOpname(userInfo.getOpids().get(getBindingOpid));
					  
					//初始页面 的时候弹出 opids 提供用户选择
					Datagrid<Opid> datagrid = new Datagrid<Opid>();
					datagrid.setTotal(new Integer(userInfoList.size()).toString());
					datagrid.setRows(userInfo.getAllOpid());
					session.setAttribute(Common.Session_opids, datagrid);
					session.setAttribute(Common.Session_thisOpid, getBindingOpid);
					session.setAttribute(Common.Session_thisUsername, userInfo.getOpname());
					// 将用户信息存入session中
					session.setAttribute(Common.Session_UserInfo, userInfo);
					
					 log.debug("CAS登陆认证成功信息:{}",GsonUtil.GsonString(userInfo));
				}
				return userInfo;
			
			}
	
	

	/**
	 * 获取用户权限，并存放session
	 * 
	 * @param session
	 * @param opids
	 * 
	 * @throws RemoteException
	 */
	public Map<String, Object> getSecurity( UserInfo userInfo,Set<String> premissionSet) {
		
		// 获取当前用户与token绑定的opid
		String opid = GetBindingOpid(userInfo.getToken());
		//List<Opid> opids = userInfo.getAllOpid(); //获取所有的opid
		userInfo.setOpid(opid);
		userInfo.setOpname(userInfo.getOpids().get(opid));
		
		//权限map
		Map<String, Object> authorizationMap = new HashMap<String, Object>();
		
		
			// 获取用户功能模块权限
		/*	String rt = permissionService.getHasPermissionModuleList(applicationId, opid.getOpid());
			System.err.println("用户功能模块权限：" + rt);*/
			
			// 查询模块权限
			LinkedHashMap<String, String> moduleMap = new LinkedHashMap<>();
			moduleMap.put("ApplicationId", applicationId);
			moduleMap.put("OperatorId", opid);
			
			JSONObject securityModeljsonObject = Axis2WebServiceClient.getWebServicePararm(moduleMap, "GetHasPermissionModuleList").getJSONObject("GetHasPermissionModuleListResult");

//			SecurityHead<SecurityModel> securityHead = GsonUtil.GsonToBean(securityModeljsonObject.toJSONString(),SecurityHead.class);
			
			
			Type objectType = new TypeToken<SecurityHead<SecurityModel>>() {}.getType();
			SecurityHead<SecurityModel> securityHead = GsonUtil.getGson().fromJson(securityModeljsonObject.toJSONString(), objectType);
			
			List<SecurityModel> securityModels = securityHead.getJsonResult();
			
			if(null!=securityModels && securityModels.size()>0){
				
				List<List<String>> securityModel1 = new ArrayList<>();
				List<List<JsonResult>> jsonResults = new ArrayList<>();
				
				// 获取用户功能模块按钮权限
				for (SecurityModel securityModel : securityModels) {
					String pageCode = securityModel.getPageCode();
					if (!pageCode.equals("AirExpHAWBList") && !securityModel.getPageCode().equals("AirExpMAWBList") && !securityModel.getPageCode().equals("AirExpOrder")) {
						premissionSet.add(pageCode); //添加权限realm
						LinkedHashMap<String, String> buttonMap = new LinkedHashMap<>();
						buttonMap.put("ApplicationId", applicationId);
						buttonMap.put("PageCode", securityModel.getPageCode());
						buttonMap.put("PreButtonName", ""); // 模块名称（非必填）
						buttonMap.put("OperatorId", opid); // 用户岗位操作号（必填）
						// 返回功能模块按钮数组
						JSONObject buttonjsonObject = Axis2WebServiceClient.getWebServicePararm(buttonMap, "GetHasPermissionPageButton").getJSONObject("GetHasPermissionPageButtonResult");
						
						
//					String button = permissionService.getHasPermissionPageButton(applicationId, securityModel.getPageCode(), null, opid.getOpid());
						
						// 转json对象
						Type objectTypebutton = new TypeToken<SecurityHead<String>>() {}.getType();
						
						SecurityHead<String> securityHead1 = GsonUtil.getGson().fromJson(buttonjsonObject.toJSONString(), objectTypebutton);
						
						securityModel1.add(securityHead1.getJsonResult());
						
						List<String> list = securityHead1.getJsonResult();
						
						for (String BtnName : list) {
							premissionSet.add(BtnName); //按钮权限
							LinkedHashMap<String, String> userReportMap = new LinkedHashMap<>();
							userReportMap.put("OpId", opid); // 用户岗位操作号（必填
							userReportMap.put("ApplicationId", applicationId);
							userReportMap.put("PageCode", securityModel.getPageCode());
							userReportMap.put("BtnNames", BtnName); // 打印按钮名称（必填
							
							JSONObject userReportjsonObject = Axis2WebServiceClient.getWebServicePararm(userReportMap, "GetUserReport").getJSONObject("GetUserReportResult");
							
//						String rtPrint = printPermissionService.getUserReport(opid.getOpid(), applicationId, securityModel.getPageCode(), string);
							
							// 转json对象
							Type printButton = new TypeToken<SecurityHead<JsonResult>>() {}.getType();
							SecurityHead<JsonResult> printButtons =  GsonUtil.getGson().fromJson(userReportjsonObject.toJSONString(), printButton);
							if (printButtons.getJsonResult().size() > 0) {
								List<JsonResult> jsonResult = printButtons.getJsonResult();
								jsonResults.add(jsonResult);
							}
						}
					}
					
					authorizationMap.put(opid + Common.UserSecurity_Model, securityModels);// 用户功能模块权限
					authorizationMap.put(opid + Common.UserSecurity_PrintButton, jsonResults);// 用户打印权限
					authorizationMap.put(opid + Common.UserSecurity_Button, securityModel1);// 用户功能模块按钮权限
			}
			
			}
		
		return authorizationMap;
	}

	
	/**
	 * 获取当前用户最新的操作ID
	 * 
	 * @param token
	 * @return
	 */
	public String GetBindingOpid(String token) {

		if (StringUtils.isNotBlank(token)) {

			StringBuffer sb2 = new StringBuffer();
			String param = sb2.append("token=").append(token).toString();
			try {
				String res =HttpClient.sendPost("http://cas.bondex.com.cn:8080/takeopid.jsp", param);

				return res;
			} catch (Exception e) {
				log.debug("获取当前绑定token:{}的opid失败！原因：{}", token, e.getMessage());

			} finally {

			}

		}
		return null;

	}

	
	
	
	
	/**
	 * 绑定opid 登录CAS系统之后设置或切换操作ID
	 * 
	 * @param opid
	 * @param token
	 *            CAS登录成功后返回的token
	 * @return
	 */
	public Boolean BindingOpid(String opid, String token) {

		if (StringUtils.isNotBlank(opid) && StringUtils.isNotBlank(token)) {

			StringBuffer sb2 = new StringBuffer();
			String param = sb2.append("opid=").append(opid).append("&").append("token=").append(token).toString();
			try {
				String res = HttpClient.sendPost("http://cas.bondex.com.cn:8080/putopid.jsp", param);

				if (null != res && "ok".equalsIgnoreCase(res)) {
					log.debug("绑定opid:{},token:{}绑定成功！", opid, token);
					return true;
				}
			} catch (Exception e) {
				log.debug("绑定opid:{},token:{}失败！原因：{}", opid, token, e.getMessage());

			} finally {

			}

		}

		return false;

	}
	
	/**
	 * CAS 登陆成功后 调用cas接口，获取带opids的用户信息
	 * 
	 * @param map
	 * @param session
	 * @return
	 */
	private UserInfo getAllOpids(String token) {
	
		String rt = HttpClient.sendPost(springCasAutoconfig.getLoadMore(), "token=" + token);
		// log.debug(rt);
	
		TokenResult idResult = GsonUtil.GsonToBean(rt, TokenResult.class);
		List<UserInfo> userInfoList = idResult.getMessage();
		UserInfo userInfo = userInfoList.get(0);
	
		List<Opid> opids = new ArrayList<Opid>();
	
		Map<String, String> opidsMap = userInfo.getOpids();
	
		for (String opid : opidsMap.keySet()) {
			Opid opid2 = new Opid(opid, opidsMap.get(opid));
			opids.add(opid2);
		}
		
		// 将opids 返回当前用户对应的一至多个操作ID
		userInfo.setAllOpid(opids);
		if (null == userInfo.getOpid()) {
			userInfo.setOpid(opids.get(0).getOpid()); //多个号设置默认的绑定的opid
		}
		userInfo.setToken(token);
		return userInfo;
	}
	
}
