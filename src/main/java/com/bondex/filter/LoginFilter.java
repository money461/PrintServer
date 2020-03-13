package com.bondex.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bondex.security.entity.TokenResult;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.GsonUtil;

public class LoginFilter implements Filter {
	
	private  final Logger log = LoggerFactory.getLogger(this.getClass());
 
	private Pattern pattern;
	
	private String ignorePattern;
	
	public LoginFilter() {
		super();
	}

	public LoginFilter(String ignorePattern) {
		super();
		this.ignorePattern = ignorePattern;
		pattern = Pattern.compile(ignorePattern);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		pattern = Pattern.compile(ignorePattern);
	}

	/**
	 * 拦截CAS回调获取权限信息
	 */
	@Override
	public void doFilter(ServletRequest requ, ServletResponse resp, FilterChain chain)throws IOException, ServletException {
		
		 final HttpServletRequest request = (HttpServletRequest) requ;
		 if (isRequestUrlExcluded(request)) {
			   log.debug("URL=>{} Request is ignored.",request.getRequestURI());
	            chain.doFilter(requ, resp);
	            return;
	        }
		
		if(!ShiroUtils.getSubject().isAuthenticated()){
			System.out.println("-------------------用户未认证！---------------------");
			//未认证
			long start = System.currentTimeMillis();
			//进入realm认证
			Session session = ShiroUtils.getSubject().getSession();
			Assertion assertion =(Assertion)session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
			String loginName =assertion.getPrincipal().getName();
			System.out.println("获取到CAS登陆用户信息："+loginName);
			TokenResult result = GsonUtil.GsonToBean(loginName, TokenResult.class);
			UsernamePasswordToken token = new UsernamePasswordToken(loginName,result.getMessage().get(0).getTgt(),true);
			ShiroUtils.getSubject().login(token); //shiro realm认证
			long end = System.currentTimeMillis();
			log.debug("认证耗时：{}ms",(end-start));
		}
		
		chain.doFilter(requ, resp); //有权限无需获取
		
	}

	 
	 
	private boolean isRequestUrlExcluded(javax.servlet.http.HttpServletRequest request) {
		final StringBuffer urlBuffer = request.getRequestURL();
        if (request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        final String requestUri = urlBuffer.toString();
        
		return this.pattern.matcher(requestUri).find();
	}


	@Override
	public void destroy() {
		
	}
	
}
