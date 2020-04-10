package com.bondex.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.io.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;

import net.sf.ehcache.CacheManager;

/**
 * 
 * @author Qianli
 * 
 * 2019年10月23日 下午2:20:21
 * 用注解的话需要配置cacheManager 
 *  @Cacheable(key = "key", cacheManager = CacheManagerConfig.CacheManagerNames.EHCACHE_CACHE_MAANGER, cacheNames = CacheManagerConfig.EhCacheNames.CACHE_10MINS)
 * 
 * 针对不同的缓存技术，需要实现不同的cacheManager，Spring定义了如下的cacheManger实现。
	
	CacheManger	描述
	SimpleCacheManager	使用简单的Collection来存储缓存，主要用于测试
	ConcurrentMapCacheManager	使用ConcurrentMap作为缓存技术（默认）
	NoOpCacheManager	测试用
	EhCacheCacheManager	使用EhCache作为缓存技术，以前在hibernate的时候经常用
	GuavaCacheManager	使用google guava的GuavaCache作为缓存技术
	HazelcastCacheManager	使用Hazelcast作为缓存技术
	JCacheCacheManager	使用JCache标准的实现作为缓存技术，如Apache Commons JCS
	RedisCacheManager	使用Redis作为缓存技术
	CaffeineCacheManager使用包装了guavacache 
 * 
 * 注意：配置多个cacheManager。但这里需要注意，要设置一个默认的cacheManager，即注解在未设置cacheManager时，自动使用此缓存管理类进行缓存，同时，因为注入了多个cacheManaager，需要在默认的管理器方法上加上@Primary注解
 * 
 */
//@Configuration
//@EnableCaching //开启缓存可以使用注解形式写入缓存
//@EnableConfigurationProperties(CacheProperties.class)
public class CacheManagerConfig extends CachingConfigurerSupport {
	
	private  final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//获取properties prefix = "spring.cache" 配置 
	 private final CacheProperties cacheProperties;
	    CacheManagerConfig(CacheProperties cacheProperties) {
	        this.cacheProperties = cacheProperties;
	    }
	
	/**
     * cacheManager名称
     */
    public interface CacheManagerName {
        /**
         * redis
         */
        String REDIS_CACHE_MANAGER = "redisCacheManager";

        /**
         * ehCache
         */
        String EHCACHE_CACHE_MAANGER = "ehCacheCacheManager";
        
        
        String cacheName = "bondex-labelPrint";
        
        /**
         * Google Guava Cache
         */
        String GUAVA_CACHE_MAANGER = "GuavaCacheCacheManager";
        
        /**
         * Caffeine Cache
         * Caffeine是使用Java8对Guava缓存的重写版本，在Spring Boot 2.0中将取代Guava
         */
        String CAFFEINE_CACHE_MAANGER = "CaffeineCacheCacheManager";
        
        
    }
    
    /**
     * 自定义 key值生成策略
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }

        };
    }
    

	


    //注入 EhCacheCacheManager
	@Bean(CacheManagerConfig.CacheManagerName.EHCACHE_CACHE_MAANGER) 
    public EhCacheCacheManager EhcacheManager() {
		
		//方式一 自己实例化
		/*
	    EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
	 	Resource configLocation = new ClassPathResource("classpath:config/ehcache-config.xml");
        cacheManagerFactoryBean.setConfigLocation(configLocation);
        cacheManagerFactoryBean.setShared(true);
        //如果 Factory 自己手动实列化，需要 执行afterPropertiesSet()方法，因为这是方法是 初始化 类使用的
        //如果Factory 由Spring 容器 创建 ，容器初始化完成后 spring 会去执行这个方法。
        cacheManagerFactoryBean.afterPropertiesSet();//初始化 读取配置文件
        CacheManager buildCacheManager = cacheManagerFactoryBean.getObject();
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(buildCacheManager);
         //由于自己实列化EhCacheManager 需要执行 手动初始化 方法。
         ehCacheCacheManager.initializeCaches();//初始化
	    */
		
		/* //方式二 获取properties 配置注入ehCacheCacheManager
    	 Resource configLocation = this.cacheProperties.resolveConfigLocation(this.cacheProperties.getEhcache().getConfig());
    	 CacheManager buildCacheManager = EhCacheManagerUtils.buildCacheManager(configLocation);
         EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(buildCacheManager);
         logger.debug("~~~~EhCacheCacheManager bean init success~~~~~");
         */
         
        /* 
         //方式三 Configuration是用来指定CacheManager配置信息的，其它通过不同的方式所指定的构造参数最终都会转化为一个对应的Configuration对象，然后再利用该Configuration对象初始化CacheManager。
         net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration(); 
          //新建一个缓存的配置信息
         CacheConfiguration cacheConfiguration = new CacheConfiguration().name("test");
         //指定当前缓存的最大堆内存值为100M
         cacheConfiguration.maxBytesLocalHeap(100, MemoryUnit.MEGABYTES);
         //添加一个cache
         configuration.addCache(cacheConfiguration);
         configuration.dynamicConfig(false);  //不允许动态修改配置信息
         //使用构造方法构建CacheManager时每次都会产生一个新的CacheManager对象，并且会以该CacheManager对应的name作为key保存该CacheManager
         CacheManager cacheManager = new CacheManager(configuration);
         Cache cache = cacheManager.getCache("test");
         cache.put(new Element("test", "test"));
         //CacheManager交由Spring EhCacheCacheManager 管理
         EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(cacheManager);
         */
         
         //方式四
         //这个文件路径可以是相对路径，也可以是绝对路径。这里使用的是相对路径。
//         CacheManager cacheManager = new CacheManager("src/main/resources/config/ehcache-config.xml");
         CacheManager cacheManager = new CacheManager(getCacheManagerConfigFileInputStream());
         cacheManager.addCacheIfAbsent(CacheManagerConfig.CacheManagerName.cacheName);
         EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(cacheManager);
    	 return ehCacheCacheManager;
         
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
     * 获取guava 实列的缓存
     *
     */
/*	@Bean(CacheManagerConfig.CacheManagerName.GUAVA_CACHE_MAANGER)
    public GuavaCacheManager guavaCacheManager(@Qualifier("guavaCache")LoadingCache guavaCache) {
        GuavaCacheManager guavaCacheManager = new GuavaCacheManager(guavaCache);
        guavaCacheManager.setCacheBuilder();
        List<String> guavaCacheNames = Lists.newArrayList();
        guavaCacheNames.add("CacheNamesA");
        guavaCacheNames.add("CacheNamesB");
        guavaCacheManager.setCacheNames(guavaCacheNames);
        return guavaCacheManager;
    }*/
	
	
}
