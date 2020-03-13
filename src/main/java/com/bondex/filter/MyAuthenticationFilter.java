package com.bondex.filter;

import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bondex.security.entity.TokenResult;
import com.bondex.shiro.security.ShiroUtils;
import com.bondex.util.GsonUtil;
/**
 * 自定义shiro认证过滤器
 * @author Qianli
 * 
 * 2020年3月6日 上午11:35:48
 */
public class MyAuthenticationFilter extends AuthenticationFilter {
	
	private  final Logger log = LoggerFactory.getLogger(this.getClass());
	 
	private Pattern pattern;
	
	private String ignorePattern;
	
	public MyAuthenticationFilter() {
		super();
	}

	public MyAuthenticationFilter(String ignorePattern) {
		super();
		this.ignorePattern = ignorePattern;
		pattern = Pattern.compile(ignorePattern);
	}
	


	public static final String PERMISSIVE = "permissive";
	
	/**
	 * isAccessAllowed 方法验证请求是否认证
		如果认证结果为 false，根据上面的流程控制，则进入 onAccessDenied 方法中进行认证。
		
	         如果满足（1）.当前的subject是被认证过的。
		             （2）.用户请求的不是登录页面/或者忽略的请求，但是在定义该过滤器时，使用了PERMISSIVE=”permissive”参数。
		只要满足两个条件的一个即可允许操作
	 */
	
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return checkisAccessAllowed(request, response, mappedValue) ||isignoreRequest(request, response) || isPermissive(mappedValue);
    }

    //判断是否忽略被shiro认证拦截
    private boolean isignoreRequest(ServletRequest request, ServletResponse response) {
    	boolean pathsMatchresult = pathsMatch(ignorePattern, request);
    	if(pathsMatchresult){
    		log.debug("请求地址被忽略不进入认证！--URL=>{}",getPathWithinApplication(request));
    	}
		return pathsMatchresult;
	}

	//其实就是判断当前的subject是不是被认证过的
    protected boolean checkisAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        return subject.isAuthenticated();
    }

    /**
     * //判断当前的拦截器是不是配置了PERMISSIVE=”permissive”参数，如果配置了就可以通过
     */
    protected boolean isPermissive(Object mappedValue) {
        if(mappedValue != null) {
            String[] values = (String[]) mappedValue;
            return Arrays.binarySearch(values, PERMISSIVE) >= 0;
        }
        return false;
    }

    // isAccessAllowed 返回false 进入该方法
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		
		log.debug("-------------------请求URL=>{}进入认证！---------------------",getPathWithinApplication(request));
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
		
		return true;
	}


	
	
}
