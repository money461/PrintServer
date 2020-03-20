package com.bondex.config.redis.redisLock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.bondex.util.RandomUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**
 * 实现分布式锁的一个非常重要的点就是set的value要具有唯一性
 * redis 锁机制 使用相同的key和具有唯一性的value（例如UUID）获取锁
 * @author Qianli
 * 
 * 2019年11月7日 下午2:17:42
 */
@Component(value="redisLockUtil")
public class RedisLockUtil {
	
	/**
	 * org.slf4j.Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	 	@Resource(name="redislockPool")
	    private JedisPool jedisPool;

	 	//复制一份副本，保证线程安全。
	 	private ThreadLocal<Jedis> ThreadLocalJedis = new ThreadLocal<Jedis>(){ //设置锁的key值
	 		 @Override
	         protected Jedis initialValue() {
	 			Jedis  jedis = jedisPool.getResource(); //初始值     如果当前线程没有该ThreadLocal的值，则调用initialValue函数获取初始值返回。
	             return jedis;
	         }
	 	};
	    
	    
	    
	    @Autowired
	    private RedisTemplate<String, Object> redisTemplate;
	    
	    //锁前缀
	    private static final String LOCK_PREFIX = "lock:";
	    //线程本地变量
	    private ThreadLocal<String> localKeys = new ThreadLocal<>();//设置锁的key值
	    private ThreadLocal<String> localRequestIds = new ThreadLocal<>();//设置锁的唯一value值
	    private static final String LOCK_LUA;
	    private static final String UNLOCK_LUA;
	    static {
	        //加锁脚本，其中KEYS[]为外部传入参数
	        //KEYS[1]表示key
	        //KEYS[2]表示value
	        //KEYS[3]表示过期时间
	        //setnx (set if not exists) 如果不存在则设置值
	        LOCK_LUA = " if redis.call('setnx', KEYS[1], KEYS[2]) == 1 " +
	                "then " +
	                "    if KEYS[3] == '-1' then return 1 else return redis.call('pexpire', KEYS[1], KEYS[3]) end "+
	                "else " +
	                "    return 0 " +
	                "end ";

	        //解锁脚本
	        //KEYS[1]表示key
	        //KEYS[2]表示value
	        //return -1 表示未能获取到key或者key的值与传入的值不相等
	        UNLOCK_LUA = " if redis.call('get',KEYS[1]) == KEYS[2] " +
	                "then " +
	                "    return redis.call('del',KEYS[1]) " +
	                "else " +
	                "    return -1 " +
	                "end ";
	    }
	    
	    private static final String LOCK_SUCCESS = "OK";
	    private static final String SET_IF_NOT_EXIST = "NX";
	    private static final String SET_WITH_EXPIRE_TIME = "PX";
	    //释放锁标识
	    private static final Long RELEASE_SUCCESS = 1L;
		
	    
	    /**
	     * 我们加锁就一行代码：jedis.set(String key, String value, String nxxx, String expx, int time)，这个set()方法一共有五个形参：
						
						第一个为key，我们使用key来当锁，因为key是唯一的。
						
						第二个为value，我们传的是requestId，很多童鞋可能不明白，有key作为锁不就够了吗，为什么还要用到value？原因就是我们在上面讲到可靠性时，分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。requestId可以使用UUID.randomUUID().toString()方法生成。
						
						第三个为nxxx，这个参数我们填的是NX，意思是SET IF NOT EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
						
						第四个为expx，这个参数我们传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
						
						第五个为time，与第四个参数相呼应，代表key的过期时间。
						
						总的来说，执行上面的set()方法就只会导致两种结果：1. 当前没有锁（key不存在），那么就进行加锁操作，并对锁设置个有效期，同时value表示加锁的客户端。2. 已有锁存在，不做任何操作。
	     */

	    /**
	     * 尝试获取redis单机部署下使用的分布式锁
	     * @param jedis Redis客户端
	     * @param lockKey 锁
	     * @param requestId 请求标识 set的唯一值，一般使用 UUID+threadId
	     * @param expireTime 超期时间
	     * @return 是否获取成功
	     */
	    public boolean tryredisLock(String lockKey, String requestId, long expireTime) {
	    	try {
	    		String result = ThreadLocalJedis.get().set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
	    		
	    		if (LOCK_SUCCESS.equals(result)) {
	    			logger.debug("加锁成功!线程id:[{}]--锁名:[{}]--锁值:[{}]--时间:{}",Thread.currentThread().getId(),lockKey,requestId,LocalDateTime.now());
	    			return true;
	    		}
	    		
	    		logger.debug("加锁失败!线程id:[{}]--锁名:[{}]--锁值:[{}]--时间:{}",Thread.currentThread().getId(),lockKey,requestId,LocalDateTime.now());
	    		
	    		return false;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if (null != ThreadLocalJedis.get()) {
					ThreadLocalJedis.get().close(); //一定要关闭链接
					ThreadLocalJedis.remove(); //清除副本
		        }
			}
			return false;
	        
	    }
	
	

    /**
     * 释放 redis单机部署下使用的分布式锁 lua语言  只要 key 与 value 与原来设置时的值完全一致就可以解锁 
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识 取锁时set的唯一值，即UUID+threadId
     * @return 是否释放成功
     */
    public boolean unLock( String lockKey, String requestId) {
    	try {
    		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "+
    				"return redis.call('del', KEYS[1]) "+
    				"else return 0" + 
    				" end";
    		Object result = ThreadLocalJedis.get().eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
    		
    		if (RELEASE_SUCCESS.equals(result)) {
    			logger.debug("解锁成功!线程id:[{}]--锁名:[{}]--锁值:[{}]--时间:{}",Thread.currentThread().getId(),lockKey,requestId,LocalDateTime.now());
    			return true;
    		}
    		return false;
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (null != ThreadLocalJedis.get()) {
				ThreadLocalJedis.get().close(); //一定要关闭链接
				ThreadLocalJedis.remove(); //清除副本
	        }
		}
		return false;
    }
    
    /**
     * 
     * @param lockKey 锁名称
     * @param expireTime 锁超时时间
     * @param waitTimeout 线程获锁等待最大时间 锁等待，防止线程饥饿
     * @return
     */
    public String trylock(String lockKey, long expireTime,long waitTimeout){
     try {
    	 	String requestId = getRequestId();
	    	// 获取锁的超时时间，超过这个时间则放弃获取锁
	        long end = System.currentTimeMillis() + waitTimeout;
	    	while(System.currentTimeMillis() < end){
	    		boolean flag = tryredisLock(lockKey, requestId, expireTime);
	    		if(flag){
	    			//成功获取锁 返回 token
	    			return requestId;
	    		}
	    		
	    		 try {
	                 Thread.sleep(100); //沉睡100ms再次去获取
	             } catch (InterruptedException e) {
	                 Thread.currentThread().interrupt();
	             }
	    	}
			return requestId;
    } catch (Exception e) {
    	logger.error("acquire lock due to error", e);
    }
	return null;
 }

    
  
    /**

     * 获取锁，如果获取不到则一直等待，最长超时5分钟
     * @param key
     * @return
     */
    public boolean lock(String key) {
        return lock(key, 5*60*100, -1);
    }

    /**
     * 加锁
     * @param key Key
     * @param timeout 过期时间 单位毫秒
     * @param retryTimes 重试次数
     * @return
     */
    public boolean lock(String key, long timeout, int retryTimes) {
        try {
            DefaultRedisScript<Long> LOCK_LUA_SCRIPT = new DefaultRedisScript<>(LOCK_LUA, Long.class);
            final String redisKey = this.getLockKey(key);
            final String requestId = this.getRequestId();
            logger.debug("lock :::: redisKey = " + redisKey + " requestid = " + requestId);
            //组装lua脚本参数
            List<String> keys = Arrays.asList(redisKey, requestId, String.valueOf(timeout));
            //执行脚本
            Long result = redisTemplate.execute(LOCK_LUA_SCRIPT, keys);
            //加锁成功则存储本地变量
            if (result != null && result.equals(LOCK_SUCCESS)) {
                localRequestIds.set(requestId);
                localKeys.set(redisKey);
                logger.info("success to acquire lock:" + Thread.currentThread().getName() + ", Status code reply:" + result);
                return true;
            } else if (retryTimes == 0) {
                //重试次数为0直接返回失败
                return false;
            } else {
                //重试获取锁
                logger.info("retry to acquire lock:" + Thread.currentThread().getName() + ", Status code reply:" + result);
                int count = 0;
                while (true) {
                    try {
                        //休眠一定时间后再获取锁，这里时间可以通过外部设置
                        Thread.sleep(100);
                        result = redisTemplate.execute(LOCK_LUA_SCRIPT, keys);
                        if (result != null && result.equals(LOCK_SUCCESS)) {
                            localRequestIds.set(requestId);
                            localKeys.set(redisKey);
                            logger.info("success to acquire lock:" + Thread.currentThread().getName() + ", Status code reply:" + result);
                            return true;
                        } else {
                            count++;
                            if (retryTimes == count) {
                                logger.info("fail to acquire lock for " + Thread.currentThread().getName() + ", Status code reply:" + result);
                                return false;
                            } else {
                                logger.warn(count + " times try to acquire lock for " + Thread.currentThread().getName() + ", Status code reply:" + result);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("acquire redis occured an exception:" + Thread.currentThread().getName(), e);
                        break;
                    }
                }
            }
        } catch (Exception e1) {
            logger.error("acquire redis occured an exception:" + Thread.currentThread().getName(), e1);
        }
        return false;
    }

    /**
     * 获取LockKey
     * @param key 原始KEY，如果为空，自动生成随机KEY
     * @return
     */
    private String getLockKey(String key) {
        //如果Key为空且线程已经保存，直接用，异常保护
        if (StringUtils.isEmpty(key) && !StringUtils.isEmpty(localKeys.get())) {
            return localKeys.get();
        }
        //如果都是空那就抛出异常
        if (StringUtils.isEmpty(key) && StringUtils.isEmpty(localKeys.get())) {
            throw new RuntimeException("key is null");
        }
        return LOCK_PREFIX + key;
    }

    /**
     * 获取随机请求ID UUID+threadId
     * @return
     */
    private String getRequestId() {
    	//取锁时set的唯一值，即UUID+threadId
    	String id = RandomUtil.UUID32(); //设置
    	long threadId = Thread.currentThread().getId();
    	String token = id + ":" + threadId;
        return token;
    }

    /**
     * 释放锁
     * @param lockey
     * @return
     */
    public boolean unlock(String lockey) {
        try {
            DefaultRedisScript<Long> UNLOCK_LUA_SCRIPT = new DefaultRedisScript<>(UNLOCK_LUA, Long.class);
            String localKey = localKeys.get();
            //如果本地线程没有KEY，说明还没加锁，不能释放
            if(StringUtils.isEmpty(localKey)) {
                logger.error("release lock occured an error: lock key not found");
                return false;
            }
            String redisKey = getLockKey(lockey);
            //判断KEY是否正确，不能释放其他线程的KEY
            if(!StringUtils.isEmpty(localKey) && !localKey.equals(redisKey)) {
                logger.error("release lock occured an error: illegal key:" + lockey);
                return false;
            }
            //组装lua脚本参数 lockey 
            List<String> keys = Arrays.asList(redisKey, localRequestIds.get());
            logger.debug("unlock :::: redisKey = " + redisKey + " requestid = " + localRequestIds.get());
            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            Long result = redisTemplate.execute(UNLOCK_LUA_SCRIPT, keys);
            //如果这里抛异常，后续锁无法释放
            if (result !=null && result == 1L) {
                logger.info("release lock success:" + Thread.currentThread().getName() + ", Status code reply=" + result);
                return true;
            } else if (result!=null && result == -1L) {
                //返回-1说明获取到的KEY值与requestId不一致或者KEY不存在，可能已经过期或被其他线程加锁
                // 一般发生在key的过期时间短于业务处理时间，属于正常可接受情况
                logger.warn("release lock exception:" + Thread.currentThread().getName() + ", key has expired or released. Status code reply=" + result);
            } else {
                //其他情况，一般是删除KEY失败，返回0
                logger.error("release lock failed:" + Thread.currentThread().getName() + ", del key failed. Status code reply=" + result);
            }
        } catch (Exception e) {
            logger.error("release lock occured an exception", e);
        } finally {
            //清除本地变量
            this.clean();
        }
        return false;
    }

    /**
     * 清除本地线程变量，防止内存泄露
     */
    private void clean() {
        localRequestIds.remove();
        localKeys.remove();
    }
    
    
}
