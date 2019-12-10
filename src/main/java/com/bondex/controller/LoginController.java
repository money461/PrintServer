package com.bondex.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.entity.Datagrid;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.SecurityModel;
import com.bondex.util.GsonUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Qianli
 * 
 * 2019年12月10日 下午4:33:39
 */
@Controller
public class LoginController {
	@Value("${cas.url}")
	private String localhostIp;
	

	@RequestMapping("login")
	public ModelAndView login(HttpServletRequest request, ModelAndView modelAndView) {
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute("userSecurity");// 获取用户权限
		getSecurity(map);
		modelAndView.setViewName("starter");
		modelAndView.addObject("userInfo", request.getSession().getAttribute("userInfo"));
		return modelAndView;
	}

	private void getSecurity(Map<String, Object> map) {

	}

	@RequestMapping("logout")
	public void logout(HttpServletRequest request, HttpSession session, HttpServletResponse response, SessionStatus sessionStatus) throws IOException {
		ServletContext context = session.getServletContext();
		context.getInitParameter("casServerLogoutUrl");
		session.invalidate(); // 然后是让httpsession失效
		response.sendRedirect("http://cas.bondex.com.cn:8080/logout?service=http://" + localhostIp + "/labelPrint");
	}

	@RequestMapping("getOpids")
	@ResponseBody
	public String getOpids(HttpServletRequest request, HttpSession session) {
		return (String) session.getAttribute("opids");
	}

	@RequestMapping("getThisOpids")
	@ResponseBody
	public String getThisOpids(HttpServletRequest request, HttpSession session) {
		if (session.getAttribute("thisOpid") == null) {
			String rt = (String) session.getAttribute("opids");
			Type objectType = new TypeToken<Datagrid<Opid>>() {	}.getType();
			Datagrid<Opid> datagrid = GsonUtil.getGson().fromJson(rt, objectType);
			if (datagrid.getRows().size() == 1) {
				return datagrid.getRows().get(0).getOpid();
			}
			return "";
		} else {
			return (String) session.getAttribute("thisOpid");
		}
	}

	@RequestMapping("getOpidSecurity")
	@ResponseBody
	public String getOpidSecurity(HttpServletRequest request, HttpSession session, HttpServletResponse response, String opid, String username) {
		session.setAttribute("thisOpid", opid);
		session.setAttribute("thisUsername", username);
		Map<String, Object> map = (Map<String, Object>) session.getAttribute("userSecurity");
		List<SecurityModel> securityModels = (List<SecurityModel>) map.get(opid + "model");
		StringBuffer buffer = new StringBuffer();
		if (securityModels != null) {
			for (SecurityModel securityModel : securityModels) {
				buffer.append(securityModel.getModuleName() + ",");
			}
		} else {
			return null;
		}
		return buffer.toString();
	}

	@RequestMapping("getOpidSecurityButton")
	@ResponseBody
	public String getOpidSecurityButton(HttpServletRequest request, HttpSession session, HttpServletResponse response, String opid) {
		Map<String, Object> map = (Map<String, Object>) session.getAttribute("userSecurity");
		List<List<String>> button = (List<List<String>>) map.get(opid + "button");
		StringBuffer buffer = new StringBuffer();
		for (List<String> list : button) {
			System.out.println(list);
		}

		return buffer.toString();
	}
}
