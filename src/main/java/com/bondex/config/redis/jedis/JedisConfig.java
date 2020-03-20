package com.bondex.config.redis.jedis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
@Configuration
public class JedisConfig {
	
	  @Value("${spring.redis.host}")
	    private String host;

	    @Value("${spring.redis.port}")
	    private int port;

	    @Value("${spring.redis.password}")
	    private String password;

	    @Value("${spring.redis.jedis.pool.max-idle}")
	    private int maxIdle;

	    @Value("${spring.redis.jedis.pool.max-wait}")
	    private long maxWait;

	    @Value("${spring.redis.jedis.pool.min-idle}")
	    private int minIdle;

	    @Value("${spring.redis.timeout}")
	    private int timeout;
	    
	    @Value("${spring.redission.redlock.database_db}")  
	    private int db;

	    @Bean(name="jedisPoolConfig")
	    public JedisPoolConfig jedisPoolConfig(){
	    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	        jedisPoolConfig.setMaxIdle(maxIdle);
	        jedisPoolConfig.setMaxWaitMillis(maxWait);
	        jedisPoolConfig.setMinIdle(minIdle);
	        jedisPoolConfig.setMaxTotal(100);
	        jedisPoolConfig.setTestOnBorrow(true);  
	        jedisPoolConfig.setTestOnReturn(true);  
	        jedisPoolConfig.setTestWhileIdle(true);  
	        jedisPoolConfig.setNumTestsPerEvictionRun(10);  
	        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(60000);  
			return jedisPoolConfig;
	    }
	    
	    /**
	     * 默认存放的位置
	     * @param jedisPoolConfig
	     * @return
	     */
	    @Bean(name="jedisPool")
	    public JedisPool redisPoolFactory(JedisPoolConfig jedisPoolConfig) {
	        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password,0);
	        return jedisPool;
	    }
	    
	    //存入指定数据库db
	    @Bean(name="redislockPool")
	    public JedisPool redisLockPoolFactory(JedisPoolConfig jedisPoolConfig) {
	    	JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password,db);
	    	return jedisPool;
	    }
	    
	    
	 /**
     * 测试redis
     * @param args
     */
    public static void main(String[] args) {
    	JedisPoolConfig poolConfig=new JedisPoolConfig();  
        poolConfig.setMaxIdle(100);  
        poolConfig.setMinIdle(0);  
        poolConfig.setMaxWaitMillis(1000);
        poolConfig.setMaxTotal(100);
        poolConfig.setTestOnBorrow(true);  
        poolConfig.setTestOnReturn(true);  
        poolConfig.setTestWhileIdle(true);  
        poolConfig.setNumTestsPerEvictionRun(10);  
        poolConfig.setTimeBetweenEvictionRunsMillis(60000);  
        JedisPool jedisPool= new JedisPool(poolConfig, "172.16.88.150", 6379, 1000,"123456");
//        Jedis jedis = jedisPool.getResource();
//    	 Set<String> keys = jedis.keys("GT19090109:*");
//    	 System.out.println(keys.size());
//    	 System.out.println(jedis.get("name"));
//        
       
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();  
//       
//        jedisConnectionFactory.setPoolConfig(poolConfig);
//        jedisConnectionFactory.setHostName("192.168.42.128");  
//        jedisConnectionFactory.setPassword("123456");  
//        jedisConnectionFactory.setPort(6379);  
//        jedisConnectionFactory.setTimeout(10000);
//        jedisConnectionFactory.setDatabase(11); 
//        
//        Jedis jedis = jedisConnectionFactory.getShardInfo().createResource();
//        Jedis jedis = (Jedis)jedisConnectionFactory.getConnection().getNativeConnection();
//    	 jedis.set("name","qianli");
//    	 System.out.println(jedis.get("name"));
    	 
}
}
