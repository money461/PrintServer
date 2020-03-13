package com.bondex.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bondex.cas.SpringCasAutoconfig;
import com.bondex.common.Common;
import com.bondex.entity.page.Datagrid;
import com.bondex.security.entity.Opid;
import com.bondex.security.entity.TokenResult;
import com.bondex.security.entity.UserInfo;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.GsonUtil;
import com.bondex.util.HttpClient;

/**
 * preHandler -> Controller -> postHandler -> model渲染-> afterCompletion
 * 
 * @author bondex_public
 *
 * 
 */
public class LoginInterceptor implements HandlerInterceptor {
	
	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private SpringCasAutoconfig springCasAutoconfig;
	
	public LoginInterceptor(SpringCasAutoconfig springCasAutoconfig) {
		super();
		this.springCasAutoconfig = springCasAutoconfig;
	}
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//已认证
		if(!ShiroUtils.getSubject().isAuthenticated()){
			System.out.println("-------------------用户未认证！---------------------");
			//未认证
			long start = System.currentTimeMillis();
			//进入realm认证
			HttpSession session = request.getSession();
			Assertion assertion =(Assertion)session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
			String loginName =assertion.getPrincipal().getName();
			System.out.println("获取到CAS登陆用户信息："+loginName);
			TokenResult result = GsonUtil.GsonToBean(loginName, TokenResult.class);
			UsernamePasswordToken token = new UsernamePasswordToken(loginName,result.getMessage().get(0).getTgt(),true);
			ShiroUtils.getSubject().login(token); //shiro realm认证
			long end = System.currentTimeMillis();
			log.debug("认证耗时：{}ms",(end-start));
		}
		return true;
		
	}
	
	
	
	
	/**
	 * 获取登陆用户信息
	 * 
	 * @param request
	 * @return
	 */
	public UserInfo getUserInfo(HttpServletRequest request) {
		
		/**
		 * 获取用户信息
		 */
		/*HttpSession session = request.getSession();
		Assertion assertion =(Assertion)session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
		String loginName =assertion.getPrincipal().getName();
		
		System.out.println(loginName);*/
		HttpSession session = request.getSession();
		AttributePrincipal principal = (AttributePrincipal) request.getUserPrincipal();
		String userName = principal.getName();
		TokenResult result = GsonUtil.GsonToBean(userName, TokenResult.class);
		List<UserInfo> userInfoList = result.getMessage();
		UserInfo userInfo = userInfoList.get(0);
		// 获取用户opids 并展示在页面
		userInfo = getAllOpids(userInfo.getToken());
		
		//初始页面 的时候弹出 opids 提供用户选择
		Datagrid<Opid> datagrid = new Datagrid<Opid>();
		datagrid.setTotal((long)userInfoList.size());
		datagrid.setRows(userInfo.getAllOpid());
		
		session.setAttribute(Common.Session_opids, datagrid);
		// 将用户信息存入session中
		session.setAttribute(Common.Session_UserInfo, userInfo);
		
		// 绑定用户默认的opid
//		BindingOpid(userInfo.getOpid(), userInfo.getToken());
		
		return userInfo;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		System.out.println("-------------------用户已认证！---------------------");
	}


	/**
	 * CAS 登陆成功后 调用cas接口，获取带opids的用户信息
	 * 
	 * @param map
	 * @param session
	 * @return
	 */
	private UserInfo getAllOpids(String token) {
		Session session = ShiroUtils.getSession();
		Object attribute = session.getAttribute(Common.Session_opids);
	
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
