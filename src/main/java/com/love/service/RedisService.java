package com.love.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

	/**
	 * 从指定的列表右边出队,添加到目的列表中
	 * 
	 * @param srckey 源列表
	 * @param dstkey 　目的列表
	 * @return
	 */
	public String rpoppush(String srckey, String dstkey);

	/**
	 * 获取指定列表的范围数据
	 * 
	 * @param key 　列表名
	 * @param start 　开始位置
	 * @param end 　结束位置
	 * @return
	 */
	public List<String> lrange(String key, int start, int end);

	/**
	 * 从队列的左边取出一条数据
	 * 
	 * @param key 　列表名
	 * @return
	 */
	public String lpop(String key);

	/**
	 * 从队列的左边取出一条数据
	 * 
	 * @param key 　列表名
	 * @return
	 */
	public List<?> lpop(String key, long size);

	/**
	 * 从列表右边添加数据
	 * 
	 * @param key 列表名
	 * @param values 数据
	 * @return
	 */
	public long rpush(String key, String... values);

	/**
	 * 从列表右边添加数据
	 * 
	 * @param key 列表名
	 * @param values 数据
	 * @return
	 */
	public long rpush(String key, Collection<String> values);

	public int hmset(String key, Map<String, String> values);

	/**
	 * 
	 * @Title: hgetAll
	 * @user: zzx
	 * @time: 2016年11月17日上午9:05:10 //DESC 返回哈希表 key 中，所有的域和值
	 * @param key
	 * @return
	 * @return Map<Object,Object> 返回类型
	 * @throws
	 */
	public Map<Object, Object> hgetAll(String key);

	public int hput(String key, String hashKey, String value);

	public String hget(String key, String field);

	public List<?> hkeys(String key);

	/**
	 * 删除hash表中field
	 */
	public void hdel(String key, String field);

	/**
	 * 从列表右边添加数据,并且设置列表的存活时间
	 * 
	 * @param key 列表名
	 * @param liveTime 存活时间(单位 秒)
	 * @param values 数据
	 * @return
	 */
	public long rpush(String key, int liveTime, String... values);

	/**
	 * 从队列的右边取出一条数据
	 * 
	 * @param key 列表名
	 * @return
	 */
	public String rpop(String key);

	/**
	 * 把一个值添加到对应列表中
	 * 
	 * @param key 列表名
	 * @param index 　添加的位置
	 * @param value 　数据
	 * @return
	 */
	public String lset(String key, long index, String value);

	/**
	 * 把所有数据添加到一个列表中
	 * 
	 * @param key 列表名
	 * @param values 　数据
	 * @return
	 */
	public long lpush(String key, String... values);

	/**
	 * 把所有数据添加到一个列表中,并且设置列表的存活时间
	 * 
	 * @param key 列表名
	 * @param values 数据
	 * @param liveTime 存活时间--单位(秒)
	 * @return
	 */
	public long lpush(String key, long liveTime, String... values);

	/**
	 * 返回列表的长度
	 * 
	 * @param key
	 * @return
	 */
	public long llen(String key);

	/**
	 * 删除列表中对应值的元素
	 * 
	 * @param key 列表名
	 * @param count 删除多少个相同的元素
	 * @param value 数据
	 * @return
	 */
	public long lrem(String key, long count, String value);

	/**
	 * 通过keys批量删除
	 * 
	 * @param key
	 */
	public long del(String... keys);

	/**
	 * 通过keys批量删除
	 * 
	 * @param key
	 */
	public long del(List<String> keys);

	/**
	 * 
	 * //DESC 删除单个key
	 * 
	 * @time: 2016年5月27日 上午9:00:36
	 * @param key
	 * @return
	 * @throws
	 */
	public long del(String key);

	/**
	 * 添加key value 并且设置存活时间(byte)
	 * 
	 * @param key
	 * @param value
	 * @param liveTime
	 */
	public void set(byte[] key, byte[] value, long liveTime);

	/**
	 * 添加key value 并且设置存活时间
	 * 
	 * @param key
	 * @param value
	 * @param liveTime 单位秒
	 */
	public void set(String key, String value, long liveTime);

	/**
	 * 添加key value
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value);

	/**
	 * 添加key value
	 * 
	 * @param key
	 * @param value
	 */
	public void setMulti(Map<String, String> map);

	/**
	 * 添加key value
	 * 
	 * @param key
	 * @param value
	 */
	public void setMulti(Map<String, String> map, long liveTime);

	/**
	 * 添加key value (字节)(序列化)
	 * 
	 * @param key
	 * @param value
	 */
	public void set(byte[] key, byte[] value);

	/**
	 * 获取redis value (String)
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key);

	/**
	 * 如果key不存在添加key value 并且设置存活时间(byte)，当key已经存在时，就不做任何操作
	 * 
	 * @param key
	 * @param value
	 * @param liveTime
	 */
	public long setnx(byte[] key, byte[] value, long liveTime);

	/**
	 * 如果key不存在添加key value 并且设置存活时间，当key已经存在时，就不做任何操作
	 * 
	 * @param key
	 * @param value
	 * @param liveTime 单位秒
	 */
	public long setnx(String key, String value, long liveTime);

	/**
	 * 如果key不存在添加key value，当key已经存在时，就不做任何操作
	 * 
	 * @param key
	 * @param value
	 */
	public long setnx(String key, String value);

	/**
	 * 如果key不存在添加key value (字节)(序列化)，当key已经存在时，就不做任何操作
	 * 
	 * @param key
	 * @param value
	 */
	public long setnx(byte[] key, byte[] value);

	/**
	 * 通过正则匹配keys
	 * 
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern);

	/**
	 * 检查key是否已经存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key);

	/**
	 * 清空redis 所有数据
	 * 
	 * @return
	 */
	public String flushDB();

	/**
	 * 查看redis里有多少数据
	 */
	public long dbSize();

	/**
	 * 检查是否连接成功
	 * 
	 * @return
	 */
	public String ping();

	/**
	 * 设置key的生命周期
	 * 
	 * @param key
	 * @param seconds 单位(秒)
	 * @return
	 */
	public boolean expire(String key, long seconds);

	/**
	 * 自增长
	 * 
	 * @param key
	 * @param length 增长步长
	 * @return
	 */
	public long incr(String key);

	/**
	 * 自增长
	 * 
	 * @param key
	 * @param length 增长步长
	 * @return
	 */
	public long incrBy(String key, long len);

	/**
	 * 自增长
	 * 
	 * @param key
	 * @param length 增长步长
	 * @return
	 */
	public double incrBy(String key, double len);

	public long eval(String luaCommand);

	/**
	 * 返回集合中一个或多个随机数 <li>当count大于set的长度时，set所有值返回，不会抛错。</li> <li>当count等于0时，返回[]</li> <li>
	 * 当count小于0时，也能返回。如-1返回一个，-2返回两个</li>
	 * 
	 * @param key
	 * @param count
	 * @return List<String>
	 */
	public Set<String> srandMember(String key, int count);

	/**
	 * 从集合口随机取出一条数据
	 * 
	 * @param key 　集合名
	 * @return
	 */
	public String spop(String key);

	/**
	 * 从集合中随机弹出多条数据
	 * 
	 * @param key 　集合名
	 * @return
	 */
	public List<?> spop(String key, int size);

	/**
	 * 移除集合中一个或多个成员
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public Long srem(String key, List<String> values);

	/**
	 * 向集合添加一个或多个成员，返回添加成功的数量
	 * 
	 * @param key
	 * @param members
	 * @return Long
	 */
	public Long sadd(String key, String... values);

	public long sadd(String key, Collection<String> values1);

	public int hset(String key, Map<String, Object> value);

}
