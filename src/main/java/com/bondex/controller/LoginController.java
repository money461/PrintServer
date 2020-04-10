package com.bondex.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.common.Common;
import com.bondex.common.enums.ResEnum;
import com.bondex.entity.page.Datagrid;
import com.bondex.entity.res.MsgResult;
import com.bondex.shiro.security.SecurityService;
import com.bondex.shiro.security.entity.Opid;
import com.bondex.shiro.security.entity.SecurityModel;
import com.bondex.shiro.security.entity.UserInfo;
import com.bondex.util.shiro.ShiroUtils;

/**
 * 
 * @author Qianli
 * 
 * 2019年12月10日 下午4:33:39
 */
@Controller
public class LoginController {
	
	@Autowired
	private SecurityService SecurityService;

	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	//退出 访问CAS /loginout
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response, SessionStatus sessionStatus) throws IOException {
		System.out.println("用户退出。。。。。。。。。。。。。。。。。。。。。。。。。。");
		HttpSession session = request.getSession();
		ServletContext context = session.getServletContext();
//	    Assertion assertion = session != null ? (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : null;
		session.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, null); //清除用户信息
//		session.invalidate(); // 然后是让httpsession失效
		ShiroUtils.clearCachedAuthenticationInfo();
		ShiroUtils.clearCachedAuthorizationInfo();
		ShiroUtils.logout();
		response.sendRedirect(context.getInitParameter("casServerLogoutUrl") +"?service=" + context.getInitParameter("casClientUrl"));

	}

	
	/**
	 * 进入主页面
	 * @param request
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping("/login")
	@ResponseBody
	public ModelAndView login(HttpServletRequest request, ModelAndView modelAndView) {
		modelAndView.setViewName("index");//重定向至主页面
		return modelAndView;
	}


	/**
	 * 获取用户信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getUserInfo",method=RequestMethod.GET)
	@ResponseBody
	public MsgResult getUserInfo(HttpServletRequest request) {
		UserInfo userInfo = ShiroUtils.getUserInfo();
		return  MsgResult.result(ResEnum.SUCCESS.CODE, ResEnum.SUCCESS.MESSAGE, userInfo);
	}
	
	
	/**
	 * 获取用户opids
	 * @param request
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("getOpids")
	@ResponseBody
	public Datagrid<Opid> getOpids(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Datagrid<Opid> data =(Datagrid<Opid>) session.getAttribute(Common.Session_opids);
		return  data;
	}

	/**
	 * 获取用户选定的 opid
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping("getThisOpids")
	@ResponseBody
	public String getThisOpids(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String opid = null;
		if (session.getAttribute(Common.Session_thisOpid) == null) {
			UserInfo userInfo= (UserInfo)session.getAttribute(Common.Session_UserInfo);
			opid = userInfo.getOpid();
			if(null==opid){
				opid = userInfo.getAllOpid().get(0).getOpid();
			}
			
		} else {
			opid = (String) session.getAttribute(Common.Session_thisOpid);
		}
		
		return opid;
	}

	/**
	 * 用户选定opid后，获取用户权限 将权限信息放入session
	 * @param request
	 * @param session
	 * @param response
	 * @param opid  //用户选定的opid -->thisOpid
	 * @param username //用户选定的username -->thisUsername
	 * @return
	 */
	@RequestMapping("getOpidSecurity")
	@ResponseBody
	public String getOpidSecurity(HttpServletRequest request, HttpServletResponse response, String opid, String username) {
		log.debug("操作opid：{},姓名：{} 账户切换。。。。。。。。。。。。。。。。。。。。。。。。。。",opid,username);
		
		HttpSession session = request.getSession();
		UserInfo userInfo = (UserInfo)session.getAttribute(Common.Session_UserInfo);
		userInfo.setOpid(opid);
		userInfo.setOpname(username);
		session.setAttribute(Common.Session_UserInfo, userInfo); //****此处确定用户被选中的opid*******确定当前环境用户确定的操作id及其姓名
		session.setAttribute(Common.Session_thisOpid, opid); 
		session.setAttribute(Common.Session_thisUsername, username);
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		List<SecurityModel> securityModels = (List<SecurityModel>) map.get(opid + Common.UserSecurity_Model);
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

	/**
	 * 获取按钮权限
	 * @param request
	 * @param session
	 * @param response
	 * @param opid
	 * @return
	 */
	@RequestMapping("getOpidSecurityButton")
	@ResponseBody
	public String getOpidSecurityButton(HttpServletRequest request, HttpServletResponse response, String opid) {
		HttpSession session = request.getSession();
		Map<String, Object> map = (Map<String, Object>) session.getAttribute(Common.Session_UserSecurity);
		List<List<String>> button = (List<List<String>>) map.get(opid + Common.UserSecurity_Button);
		StringBuffer buffer = new StringBuffer();
		for (List<String> list : button) {
			System.out.println(list);
		}

		return buffer.toString();
	}
	/**
	 * 获取菜单数据
	 * @param request
	 * @param response
	 * @param opid
	 * @return
	 */
	@RequestMapping(value="getOpidSecurityModel",method=RequestMethod.GET)
	@ResponseBody
	public SortedSet<SecurityModel> getOpidSecurityModel(HttpServletRequest request, HttpServletResponse response) {
		SortedSet<SecurityModel> userSecurityModelInfo = ShiroUtils.getUserSecurityModelInfo();
		return userSecurityModelInfo;
	}
	
	
	
	
	/**
	 * 操作账户切换
	 * @param request
	 * @param reponse
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/accountSwitch" }, method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	@ResponseBody
	public MsgResult accountSwitch(String opid, String opidName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.debug("切换操作账户:{}-{} 。。。。。。。。。。。。。。。。。。。。。。。。。。",opid,opidName);
		UserInfo userInfo = (UserInfo)ShiroUtils.getSubject().getPrincipal();
		//重新绑定opid与token
		Boolean flag = SecurityService.BindingOpid(opid,userInfo.getToken());
		if(flag){
			//清除认证
			ShiroUtils.clearCachedAuthenticationInfo();
			//清除授权
			ShiroUtils.clearCachedAuthorizationInfo();
			//退出 清除认证
			ShiroUtils.logout();
			
			//request.getRequestDispatcher("/index").forward(request, response); //内部转发，页面不跳转
			
			return new MsgResult(ResEnum.SUCCESS.CODE, "账户切换成功！");
		}else{
			
			return new MsgResult(ResEnum.DELETE_ERROR.CODE, "账户切换失败！");
		}
		
	}
	
	
}
