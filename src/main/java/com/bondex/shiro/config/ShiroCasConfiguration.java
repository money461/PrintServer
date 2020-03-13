package com.bondex.shiro.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import javax.servlet.Filter;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.bondex.cas.SpringCasAutoconfig;
import com.bondex.config.CacheManagerConfig;
import com.bondex.filter.MyAuthenticationFilter;
import com.bondex.shiro.realm.ShiroRealm;

/**
 * Created by Mr.Yangxiufeng on 2017/6/20.
 * Time:15:43
 * ProjectName:Common-admin
 */
@Configuration
public class ShiroCasConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(ShiroCasConfiguration.class);
	@Autowired
	private SpringCasAutoconfig casAutoconfig;
	
    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    

    private String loginUrl;
    private String logoutUrl;
    
   //构造器显式传入参数
	public ShiroCasConfiguration(SpringCasAutoconfig casAutoconfig) {
		super();
		this.casAutoconfig = casAutoconfig;
		this.loginUrl = casAutoconfig.getCasServerLoginUrl()+"?service=" + casAutoconfig.getCasClientUrl()+casAutoconfig.getClientloginUrl();
		this.logoutUrl = casAutoconfig.getCasServerLogoutUrl()+"?service="+casAutoconfig.getCasClientUrl();
	}
	/**
     * <p>保证实现了Shiro内部lifecycle函数的bean执行</p>
     * 此处需要静态方法配置 否侧配置文件无法读取
     * @return
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * shiro权限注解要生效，必须配置springAOP通过设置shiro的SecurityManager进行权限验证。
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 注册单点登出filter
     * @return
     */
   /* @Bean
    public FilterRegistrationBean singleSignOutFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setName("singleSignOutFilter");
        bean.setFilter(new SingleSignOutFilter());
        bean.addUrlPatterns("/*");
        bean.setEnabled(true);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }*/


    /**
     * 注册DelegatingFilterProxy（Shiro）
     * 　Shiro对Servlet容器的FilterChain进行了代理，即ShiroFilter在继续Servlet容器的Filter链的执行之前，通过ProxiedFilterChain对Servlet容器的FilterChain进行了代理；即先走Shiro自己的Filter体系，然后才会委托给Servlet容器的FilterChain进行Servlet容器级别的Filter链执行；Shiro的ProxiedFilterChain执行流程：1、先执行Shiro自己的Filter链；2、再执行Servlet容器的Filter链（即原始的 Filter）。
     *
     * @return
     * @author SHANHY
     * @create  2016年1月13日
     */
    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));  //注册shiroFilter过滤器容器代理
        //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
//      filterRegistration.addUrlPatterns("/*");  此处关闭 交由 CAS  AuthenticationFilter 过滤器负责全程拦截
        filterRegistration.setEnabled(true);
        return filterRegistration;
    }

    /**
     * Shiro的过滤器链
     */
    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，因为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     *
     Filter Chain定义说明
     1、一个URL可以配置多个Filter，使用逗号分隔
     2、当设置多个过滤器时，全部验证通过，才视为通过
     3、部分过滤器可指定参数，如perms，roles
     *
     */
    @Bean(name="shiroFilter")
    @Order(6)
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
    	
    	log.debug("配置shirFilter过滤连。。。。");
    	
        ShiroFilterFactoryBean shiroFilterFactoryBean  = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //shiro authc拦截后认证不通过 跳转到 登陆地址  
        //shiroFilterFactoryBean.setLoginUrl("/login");
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面 未登录的请求会由FormAuthenticationFilter重定向/login，登录成功后会将authenticated设置成true
        //此处应该是 cas回调的重定向地址/调用CASFilter过滤器的地址/进入Realm认证的地址
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        // 认证成功后要重定向的地址
        shiroFilterFactoryBean.setSuccessUrl(casAutoconfig.getLocalServerSuccessUrl());
        //未授权界面; 访问controller 层跳转
        shiroFilterFactoryBean.setUnauthorizedUrl(casAutoconfig.getLocalServerUnauthorizedUrl());
        
        //添加filter过滤器集合
        Map<String, Filter> filters = new HashMap<String, Filter>();
        
        //Shiro 的默认Filter 过滤器
        
       /* filters.put("anon", new AnonymousFilter());
        filters.put("authc", new FormAuthenticationFilter());
        filters.put("authcBasic", new BasicHttpAuthenticationFilter());
        filters.put("logout", new LogoutFilter());
        filters.put("noSessionCreation", new LogoutFilter());
        filters.put("perms", new  PermissionsAuthorizationFilter());
        filters.put("roles", new RolesAuthorizationFilter());
        filters.put("port", new PortFilter());
        filters.put("rest", new HttpMethodPermissionFilter());
        filters.put("ssl", new SslFilter());
        filters.put("user", new UserFilter());*/
        
        //自定义Shiro Filter  extends AccessControlFilter 
        
//        filters.put("myauth", new LoginFilter(casAutoconfig.getIgnorePattern()));
        filters.put("myauth", new MyAuthenticationFilter(casAutoconfig.getIgnorePattern()));
        
        //<!--单点登出过滤器-->
      /*  LogoutFilter logoutFilter = new LogoutFilter();
        // <!-- 注销时重定向的URL -->
        logoutFilter.setRedirectUrl(logoutUrl);
        //过滤器名称 logout
        filters.put("logout",logoutFilter);*/
        
        shiroFilterFactoryBean.setFilters(filters); //工厂装配过滤器容器
       
        //配置过滤器规则
        loadShiroFilterChain(shiroFilterFactoryBean);
        return shiroFilterFactoryBean;
    }

    
  
    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        
        //添加过滤器规则.规则 LinkedHashMap 带有顺序
        LinkedHashMap<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();

        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了 按照顺序执行校验
        /**
         *  // http://www.oschina.net/question/99751_91561
        // 此处有坑： 退出登录，其实不用实现任何东西，只需要保留这个接口即可，也不可能通过下方的代码进行退出
        // SecurityUtils.getSubject().logout();
        // 因为退出操作是由Shiro控制的
         * 配置 该过滤器后 不用写 controller层，shiro自动实现拦截退出
         */
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        // 配置不会被拦截的链接，一般是排除前端文件（anon:指定的url可以匿名访问）
        filterChainDefinitionMap.put("/*.js", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui/**","anon");
        filterChainDefinitionMap.put("/druid/**","anon");  //允许访问Druid数据库链接监控客户端
        //<!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
       //控制页面跳转的权限
        filterChainDefinitionMap.put("/layout/west/airlabel","perms[AirPrintLabel]");  
//        filterChainDefinitionMap.put("paiang/viewdata","perms[paiangPrintLabel]");	  
        //user:需要已登录或“记住我”的用户才能访问;
//        filterChainDefinitionMap.put("/**", "user");
        //4.登录过的不拦截
        //authc:所有url都必须认证通过才可以访问;
        filterChainDefinitionMap.put("/**", "myauth"); //拦截所有请求	

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        
    }
    
       
    /**
     * <p>安全管理器</p>
     * Shiro提供了三个默认实现：
     *DefaultSessionManager：DefaultSecurityManager使用的默认实现，用于JavaSE环境；
     *ServletContainerSessionManager：DefaultWebSecurityManager使用的默认实现，用于Web环境，其直接使用Servlet容器的会话；
     *DefaultWebSessionManager：用于Web环境的实现，可以替代ServletContainerSessionManager，自己维护着会话，直接废弃了Servlet容器的会话管理。
     * @return
     */
    @Bean(name = "securityManager")
    public SecurityManager securityManager(ShiroRealm shiroRealm) {
    	log.debug("注入shiro安全管理器环境。。。");
    	
    	DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    	
    	securityManager.setAuthenticator(modularRealmAuthenticator()); // 需要在自定义realm之前设置
    	
//    	securityManager.setRealm(getIniRealm());
    	securityManager.setCacheManager(ehcacheManager());
    	// 自定义session管理 使用redis或者ehcache
//    	securityManager.setSessionManager(defaultWebSessionManager());
    	// 注入记住我管理器
    	securityManager.setRememberMeManager(rememberMeManager());
    	// 指定 SubjectFactory
    	securityManager.setSubjectFactory(new DefaultWebSubjectFactory());
    	// 设置realm.
    	LinkedList<Realm> realms = new LinkedList<Realm>();
    	realms.add(shiroRealm);
    	securityManager.setRealms(realms);
    	
    	return securityManager;
    }
  
    
    /**
     * 系统自带的Realm管理，主要针对多realm
     * */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator(){
    	//设置 只要有一个 Realm 验证成功即可 默认 可以不设置
    	ModularRealmAuthenticator modularRealmAuthenticator = new ModularRealmAuthenticator();
    	modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        //自己重写的ModularRealmAuthenticator
//        MyModularRealmAuthenticator modularRealmAuthenticator = new MyModularRealmAuthenticator();
//        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return modularRealmAuthenticator;

    }
	
    
    
    /**
     * @return
     */
    @Bean("shiroRealm")
    public ShiroRealm getShiroRealm(){
    	ShiroRealm shiroRealm = new ShiroRealm();
    	shiroRealm.setCacheManager(ehcacheManager());
    	shiroRealm.setCachingEnabled(true);
//    	shiroRealm.setAuthenticationCachingEnabled(true);
//    	shiroRealm.setAuthenticationCacheName("authenticationCache");
//    	shiroRealm.setAuthorizationCacheName("authorizationCache");
    	//设置凭证(密码)验证器，用于SimpleAuthorizationInfo验证token中密码是否和数据库密码是否匹配
//    	shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher()); 
    	return shiroRealm;
    }
/*    *//**
     * 配置 Inirealm
     * @return
     *//*
    public IniRealm getIniRealm(){
    	log.debug("读取配置Realm文件。。。。");
    	IniRealm iniRealm = new IniRealm("classpath:config/shiro.ini");
    	iniRealm.setCacheManager(cacheManager());
    	iniRealm.setCachingEnabled(true);
    	return iniRealm;
    }*/

   /* *//**
     * <p>自定义cookie</p>
     * @return
     *//*
    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("SHIROSESSIONID"); //设置Cookie名字，默认为JSESSIONID；
        return simpleCookie;
    }*/

    /*@Bean
    public EhCacheManager cacheManager() {
    	EhCacheManager cacheManager = new EhCacheManager();
    	cacheManager.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
    	return cacheManager;
    }*/
    
    /**
     * shiro cache缓存管理器 使用Ehcache实现
     */
    @Bean(name=CacheManagerConfig.CacheManagerName.EHCACHE_CACHE_MAANGER) 
    public EhCacheManager ehcacheManager()
    {
        net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.getCacheManager("bondex-labelPrint");
        EhCacheManager em = new EhCacheManager();
        if (Objects.isNull(cacheManager))
        {
            em.setCacheManager(new net.sf.ehcache.CacheManager(getCacheManagerConfigFileInputStream()));
            return em;
        }
        else
        {
            em.setCacheManager(cacheManager);
            return em;
        }
    }

    /**
     * 返回配置文件流 避免ehcache配置文件一直被占用，无法完全销毁项目重新部署
     */
    protected InputStream getCacheManagerConfigFileInputStream()
    {
        String configFile = "classpath:config/ehcache-shiro.xml";
        InputStream inputStream = null;
        try
        {
            inputStream = ResourceUtils.getInputStreamForPath(configFile);
            byte[] b = IOUtils.toByteArray(inputStream);
            InputStream in = new ByteArrayInputStream(b);
            return in;
        }
        catch (IOException e)
        {
            throw new ConfigurationException(
                    "Unable to obtain input stream for cacheManagerConfigFile [" + configFile + "]", e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    
    /**
     * <p>session管理器</p>
     * @return
     */
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager defaultWebSessionManager() {
    	DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    	sessionManager.setCacheManager(ehcacheManager());
    	//全局的会话信息时间,,单位为毫秒 3小时 默认30分钟
    	sessionManager.setGlobalSessionTimeout(8 * MILLIS_PER_HOUR);
    	//session检测扫描信息时间间隔,单位为毫秒 设置调度时间间隔，单位毫秒，默认就是1小时；
    	sessionManager.setSessionValidationInterval(8 * MILLIS_PER_HOUR);;
    	//是否开启扫描
    	sessionManager.setSessionValidationSchedulerEnabled(true);
    	//去掉URL中的JSESSIONID
    	sessionManager.setSessionIdCookieEnabled(false);
    	//在会话过期时是否想删除过期的会话 默认是开启的
    	sessionManager.setDeleteInvalidSessions(true);
    	//设置属于该系统的SessionID名称 
    	sessionManager.setSessionIdCookie(simpleCookie());
    	
    	return sessionManager;
    }
    
    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *  所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     * @return
     */
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));

        return hashedCredentialsMatcher;
    }


    /**
     *  开启shiro aop注解支持.  例如在请求方法上添加注解 @RequiresPermissions("/item/query")//执行queryItems方法需要"/item/query"权限
     *  使用代理方式;所以需要开启代码支持;
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    
    /**
     * <p>自定义cookie</p>
     * @return
     */
    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("labelPrintSESSIONID"); //设置Cookie名字，默认为JSESSIONID；
        return simpleCookie;
    }
    /**
     * cookie对象;
     *
     * @return
     */
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(2592000);
        simpleCookie.setHttpOnly(true);
        simpleCookie.setSecure(true);
        return simpleCookie;
    }

    /**
     * cookie管理对象;记住我功能
     *
     * @return
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("1QWLxg+NYmxraMoxAXu/Iw=="));
        return cookieRememberMeManager;
    }
   
    
}

