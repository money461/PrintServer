package com.bondex.filter;

import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.authentication.TokenResult;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.tempuri1.IPermissionService;
import org.tempuri1.IPermissionServiceProxy;

import com.bondex.cas.entity.CasConfigs;
import com.bondex.entity.Datagrid;
import com.bondex.security.entity.JsonResult;
import com.bondex.security.entity.OperatorIdData;
import com.bondex.security.entity.OperatorIdResult;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.SecurityHead;
import com.bondex.security.entity.SecurityModel;
import com.bondex.util.HttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * preHandler -> Controller -> postHandler -> model渲染-> afterCompletion
 * 
 * @author bondex_public
 *
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
	private CasConfigs casConfigs;
	private String applicationId = "827";
	Gson gson = new Gson();
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 获取Configs对象
		if (casConfigs == null) {
			BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
			casConfigs = (CasConfigs) factory.getBean("casConfigs");
		}

		String url = request.getRequestURI();
		if (url.equals(casConfigs.getContextpath()+"/login")) {
			HttpSession session = request.getSession();
			if (null == session.getAttribute("userSecurity")) {// 如果已经获取到用户权限，则直接从缓存中取出

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println("获取用户权限开始：" + dateFormat.format(new Date()));

				// 登陆操作后，获取用户信息、权限信息
				Map info = getUserInfo(request);
				List<Opid> opids = getOpids(info, session);
				Map<String, Object> userSecurity = getSecurity(session, opids); // 返回权限数据，并将数据存放session

				System.out.println("获取用户权限结束：" + dateFormat.format(new Date()));
			}
		}
		return true;
	}

	/**
	 * 获取登陆用户信息
	 * 
	 * @param request
	 * @return
	 */
	public Map getUserInfo(HttpServletRequest request) {
		AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
		String userName = principal.getName();
		TokenResult result = (new Gson()).fromJson(userName, TokenResult.class);
		List userInfo = result.getMessage();
		Map info = (Map) userInfo.get(0);
		request.getSession().setAttribute("userInfo", info);// 用户信息存session
		return info;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

	/**
	 * 获取用户权限，并存放session
	 * 
	 * @param session
	 * @param opids
	 * 
	 * @throws RemoteException
	 */
	private Map<String, Object> getSecurity(HttpSession session, List<Opid> opids) throws RemoteException {
		System.out.println("用户权限初始化1：" + session + opids);

		Map<String, Object> map = new HashMap<String, Object>();

		System.out.println("用户权限初始化：" + new IPermissionServiceProxy());
		IPermissionService permissionService = new IPermissionServiceProxy();
		org.tempuri.IPermissionService printPermissionService = new org.tempuri.IPermissionServiceProxy();

		for (Opid opid : opids) {
			// 获取用户功能模块权限
			String rt = permissionService.getHasPermissionModuleList(applicationId, opid.getOpid());
			System.err.println("用户功能模块权限：" + rt);

			Type objectType = new TypeToken<SecurityHead<SecurityModel>>() {}.getType();
			SecurityHead<SecurityModel> securityHead = gson.fromJson(rt, objectType);
			List<SecurityModel> securityModels = securityHead.getJsonResult();
			List<List<String>> securityModel1 = new ArrayList<>();
			List<List<JsonResult>> jsonResults = new ArrayList<>();
			// 获取用户功能模块按钮权限
			for (SecurityModel securityModel : securityModels) {
				if (!securityModel.getPageCode().equals("AirExpHAWBList") && !securityModel.getPageCode().equals("AirExpMAWBList") && !securityModel.getPageCode().equals("AirExpOrder")) {
					String button = permissionService.getHasPermissionPageButton(applicationId, securityModel.getPageCode(), null, opid.getOpid());
					System.err.println("用户功能模块按钮权限：" + button);
					// 转json对象
					Type objectTypebutton = new TypeToken<SecurityHead<String>>() {
					}.getType();
					SecurityHead<String> securityHead1 = gson.fromJson(button, objectTypebutton);
					securityModel1.add(securityHead1.getJsonResult());
					List<String> list = securityHead1.getJsonResult();
					for (String string : list) {
						String rtPrint = printPermissionService.getUserReport(opid.getOpid(), applicationId, securityModel.getPageCode(), string);
						// 转json对象
						Type printButton = new TypeToken<SecurityHead<JsonResult>>() {
						}.getType();
						SecurityHead<JsonResult> printButtons = gson.fromJson(rtPrint, printButton);
						if (printButtons.getJsonResult().size() > 0) {
							jsonResults.add(printButtons.getJsonResult());
						}
						System.out.println("用户打印权限：" + rtPrint);
					}
				}

				map.put(opid.getOpid() + "printButton", jsonResults);// 用户打印权限
				map.put(opid.getOpid() + "model", securityModels);// 用户功能模块权限
				map.put(opid.getOpid() + "button", securityModel1);// 用户功能模块按钮权限
			}
		}
		// 用户权限存放session
		session.setAttribute("userSecurity", map);
		return map;
	}

	/**
	 * 调用cas接口，获取带opids的用户信息,存放session中，并返回opids
	 * 
	 * @param map
	 * @param session
	 * @return
	 */
	private List<Opid> getOpids(Map<String, String> map, HttpSession session) {
		Datagrid<Opid> datagrid = new Datagrid<Opid>();
		String rt = HttpClient.sendPost(casConfigs.getLoadMore(), "token=" + map.get("token"));
		OperatorIdResult idResult = gson.fromJson(rt, OperatorIdResult.class);
		OperatorIdData[] data = idResult.getMessage();
		// 用户信息存放session
		List<Opid> opids = new ArrayList<Opid>();
		for (int i = 0; i < data.length; i++) {
			Map<String, String> opid = data[i].getOpids();
			Set<Entry<String, String>> entrySet = opid.entrySet();
			for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				Opid opid2 = new Opid(entry.getKey(), entry.getValue());
				opids.add(opid2);
			}
		}
		datagrid.setTotal(new Integer(data.length).toString());
		datagrid.setRows(opids);
		session.setAttribute("opids", new Gson().toJson(datagrid));
		return opids;
	}

}
