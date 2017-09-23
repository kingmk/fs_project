package com.lmy.common.redis;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.lmy.common.utils.ResourceUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
/**
 * @author 
 * redis pool
 */
public class RedisClient {
	private static JedisPool pool = null;
	private static final Logger logger = Logger.getLogger(RedisClient.class);
	private static final String PREFIX_CACHE = "lmy-core";
	public static final int oneHourSec = 3600;
	public static final int oneDaySec = 3600 * 24 ;
	
	
	static{
		if (pool == null) {
			try{
				logger.debug("开始创建缓存池");
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxTotal(Integer.valueOf(ResourceUtils.getValue(PREFIX_CACHE, "redis.pool.maxActive")));
				config.setMaxIdle(Integer.valueOf(ResourceUtils.getValue(PREFIX_CACHE, "redis.pool.maxIdle")));
				config.setMaxWaitMillis(Integer.valueOf(ResourceUtils.getValue(PREFIX_CACHE, "redis.pool.maxWait")));
				config.setTestOnBorrow(true);
				config.setMinEvictableIdleTimeMillis(1000 * 600);
				config.setTimeBetweenEvictionRunsMillis(1000 * 600);
				String passwd = "D_d55%%*))$Ddkj43^2jK";
				pool = new JedisPool(config, ResourceUtils.getValue(PREFIX_CACHE, "redis.server.ip"), Integer.valueOf(ResourceUtils.getValue(PREFIX_CACHE, "redis.server.port"))
						,Protocol.DEFAULT_TIMEOUT,passwd);	logger.debug("缓存池创建成功");				
			}catch(Exception e){
				logger.error("redis.pool.maxActive="+ResourceUtils.getValue(PREFIX_CACHE, "redis.pool.maxActive"));
				logger.error("redis.pool.maxIdle="+ResourceUtils.getValue(PREFIX_CACHE, "redis.pool.maxIdle"));
				logger.error("redis.pool.maxWait="+ResourceUtils.getValue(PREFIX_CACHE, "redis.pool.maxWait"));
				logger.error("redis.server.ip="+ResourceUtils.getValue(PREFIX_CACHE, "redis.server.ip"));
				logger.error("redis.server.port="+ResourceUtils.getValue(PREFIX_CACHE, "redis.server.port"));
				logger.error(e);
			}
		}
	}
	
	private RedisClient(){
		
	}

	/**
	 * 从连接池获取
	 * 
	 * @return
	 */
	public static Jedis getJedis() {
		return pool.getResource();
	}

	/**
	 * 返回给连接池
	 * 
	 * @param jedis
	 */
	public static void closeJedis(Jedis jedis) {
		if (jedis != null) {
			pool.returnResourceObject(jedis);
		}
	}
	
	/**
	 * 判断key 是否存在
	 * @param key
	 * @return
	 */
	public static Boolean exists(String key){
		Jedis jedis = null;
		try {
			logger.debug("#exists key="+key);
			jedis = pool.getResource();
			return jedis.exists(key.getBytes());
		} catch (Exception e) {
			logger.error("#exists key="+key,e);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}
	
	
	/**
	 *  哈希操作
	 * @param key
	 * @param field
	 * @param value
	 */
	public static void hdel(String key, String field) {
		Jedis jedis = null;
		try {
			logger.debug("#hdel key="+key+",fields="+field);
			jedis = pool.getResource();
			jedis.hdel(key.getBytes(), ByteToObjectUtils.ObjectToByte(field));
		} catch (Exception e) {
			logger.error("#hdel key="+key+",fields="+field,e);
		} finally {
			closeJedis(jedis);
		}
	}
	
	/**
	 *  哈希操作
	 * @param key
	 * @param field
	 * @param value
	 */
	public static void hset(String key, String field ,Object value) {
		Jedis jedis = null;
		try {
			logger.debug("#hset key="+key+",field="+field+",value="+value);
			jedis = pool.getResource();
			jedis.hset(key.getBytes(), ByteToObjectUtils.ObjectToByte(field), ByteToObjectUtils.ObjectToByte(value));
		} catch (Exception e) {
			logger.error("#hset key="+key+",value="+value,e);
		} finally {
			closeJedis(jedis);
		}
	}
	
	/**
	 *  哈希操作
	 * @param key
	 * @param field
	 * @param value
	 */
	public static Object hget(String key, String field) {
		Jedis jedis = null;
		try {
			logger.debug("#hget key="+key+",field="+field);
			jedis = pool.getResource();
			byte[] obj = jedis.hget(key.getBytes(), ByteToObjectUtils.ObjectToByte(field));
			if (obj != null) {
				return ByteToObjectUtils.ByteToObject(obj);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("#hget key="+key+",field="+field,e);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param timeout if ${timeout} is null or < 1    有效期 30天   3600 * 24 * 30
	 */
	public static void set(String key, Object value, Integer timeout) {
		Jedis jedis = null;
		try {
			logger.debug("#set key="+key+",value="+value);
			jedis = pool.getResource();
			jedis.set(key.getBytes(), ByteToObjectUtils.ObjectToByte(value));
			if (timeout != null && timeout > 0) {
				jedis.expire(key.getBytes(), timeout);
			}else{
				jedis.expire(key.getBytes(), 3600 * 24 * 30);
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			closeJedis(jedis);
		}
	}
	public static Object get(String key) {
		Jedis jedis = null;
		try {
			logger.debug("#get key="+key);
			jedis = pool.getResource();
			byte[] obj = jedis.get(key.getBytes());
			if (obj != null) {
				return ByteToObjectUtils.ByteToObject(obj);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}

	public static void delete(String... keys) {
		Jedis jedis = null;
		try {
			if(logger.isDebugEnabled()) logger.debug("#delete "+ArrayUtils.toString(keys));
			jedis = pool.getResource();
			jedis.del(keys);
		} catch (Exception e) {
			logger.error(ArrayUtils.toString(keys),e);
		} finally {
			closeJedis(jedis);
		}
	}
	/**
	 * 从表尾/队列尾插入
	 * @param key
	 * @param obj
	 */
	public static void rpush(String key , Object obj) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			if(obj!=null){
				jedis.rpush(key.getBytes(), ByteToObjectUtils.ObjectToByte(obj));	
			}
		} catch (Exception e) {
			logger.error("key="+key+",obj="+obj,e);
		} finally {
			closeJedis(jedis);
		}
	}
	
	/**
	 * 从表头/队列头 取出 阻塞式 
	 * @param key
	 * @param timeout  阻塞式 时间
	 */
	public static Object blpop(String key ,Integer timeout) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			List<byte[]> list = jedis.blpop(timeout, key.getBytes());
			if(CollectionUtils.isNotEmpty(list) && list.size()>1){
				return ByteToObjectUtils.ByteToObject(list.get(1));
			}else{
				return null;
			}
		} catch (Exception e) {
			logger.error("key="+key,e);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}
	
	/**
	 * 从表头/队列头 取出 非阻塞式 
	 * @param key
	 */
	public static Object lpop(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			byte[] b =jedis.lpop(key.getBytes());
			return ByteToObjectUtils.ByteToObject(b);
		} catch (Exception e) {
			logger.error("key="+key,e);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}
	
	/**
	 * 读取队列长度
	 * @param key
	 */
	public static Long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			Long b =jedis.llen(key.getBytes());
			return b;
		} catch (Exception e) {
			logger.error("key="+key,e);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}
	
	/**
	 * 自增/减 
	 * @param key
	 * @num num 自增/减 数 default is 1
	 */
	public static Long incrby(String key,Long num) {
		Jedis jedis = null;
		if(num==null){
			num =1l ;
		}
		try {
			jedis = pool.getResource();
			return jedis.incrBy(key.getBytes(), num);
		} catch (Exception e) {
			logger.error("key="+key+",num="+num,e);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(llen("send_gift_13_15"));
	}
}
