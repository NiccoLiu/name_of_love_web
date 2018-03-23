package com.love.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.love.service.RedisService;

/**
 * redis API
 * 
 * 
 * @date 2017年4月11日
 * @since 1.0
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static final String ENCODE = "utf-8";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 
     * 
     * @author liuxinq
     * @param srckey
     * @param dstkey
     * @return
     */
    @Override
    public String rpoppush(final String srckey, final String dstkey) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    return new String(connection.rPopLPush(srckey.getBytes(), dstkey.getBytes()),
                            ENCODE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return "";
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<String> lrange(final String key, final int start, final int end) {
        return redisTemplate.execute(new RedisCallback<List<String>>() {
            List<String> result = new ArrayList<String>();

            @Override
            public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
                List<byte[]> bytelist = connection.lRange(key.getBytes(), start, end);
                for (byte[] b : bytelist) {
                    try {
                        result.add(new String(b, ENCODE));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public String lpop(final String key) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] result = connection.lPop(key.getBytes());
                if (result != null) {
                    try {
                        return new String(result, ENCODE);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return "";
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param size
     * @return
     */
    @Override
    public List<?> lpop(final String key, final long size) {
        List<Object> list = redisTemplate.executePipelined(new RedisCallback<List<Object>>() {
            @Override
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = key.getBytes();
                for (int i = 0; i < size; i++) {
                    connection.lPop(keyBytes);
                }
                return null;
            }
        });
        list.removeAll(Collections.singleton(null));
        return list;
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param values
     * @return
     */
    @Override
    public long rpush(final String key, final String... values) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String v : values) {
                    result = connection.rPush(key.getBytes(), v.getBytes());
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param values
     * @return
     */
    @Override
    public long rpush(final String key, final Collection<String> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param values
     * @return
     */
    @Override
    public int hmset(final String key, final Map<String, String> values) {
        try {
            redisTemplate.opsForHash().putAll(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public Map<Object, Object> hgetAll(final String key) {
        return redisTemplate.boundHashOps(key).entries();
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    @Override
    public int hput(final String key, final String hashKey, final String value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param field
     * @return
     */
    @Override
    public String hget(final String key, final String field) {
        Object obj = redisTemplate.opsForHash().get(key, field);
        if (obj == null) {
            return null;
        }
        return String.valueOf(obj);
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public List<?> hkeys(final String key) {
        Set<Object> sets = redisTemplate.opsForHash().keys(key);
        List<Object> list = new ArrayList<Object>();
        for (Object b : sets) {
            list.add(String.valueOf(b));
        }
        return list;
        /*
         * return redisTemplate.execute(new RedisCallback<List<Object>>(){ List<Object> list = new
         * ArrayList<Object>(); public List<Object> doInRedis(RedisConnection connection) throws
         * DataAccessException { Set<byte[]> sets=connection.hKeys(key.getBytes());
         * if(!sets.isEmpty()){ for(byte[] b : sets){
         * list.add(redisTemplate.getValueSerializer().deserialize(b).toString()); try {
         * list.add(new String (b , redisCode)); } catch (UnsupportedEncodingException e) {
         * e.printStackTrace(); } } return list; } return null; } });
         */
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param field
     */
    @Override
    public void hdel(final String key, final String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param liveTime
     * @param values
     * @return
     */
    @Override
    public long rpush(final String key, final int liveTime, final String... values) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String v : values) {
                    connection.rPush(key.getBytes(), v.getBytes());
                }
                if (liveTime > 0) {
                    connection.expire(key.getBytes(), liveTime);
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public String rpop(final String key) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] result = connection.rPop(key.getBytes());
                if (result != null) {
                    try {
                        return new String(result, ENCODE);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return "";
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param index
     * @param value
     * @return
     */
    @Override
    public String lset(final String key, final long index, final String value) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.lSet(key.getBytes(), index, value.getBytes());
                return "success";
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param values
     * @return
     */
    @Override
    public long lpush(String key, String... values) {
        return this.lpush(key, 0, values);
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param liveTime
     * @param values
     * @return
     */
    @Override
    public long lpush(final String key, final long liveTime, final String... values) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String v : values) {
                    result = connection.lPush(key.getBytes(), v.getBytes());
                }
                if (liveTime > 0) {
                    connection.expire(key.getBytes(), liveTime);
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public long llen(final String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.lLen(key.getBytes());
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param count
     * @param value
     * @return
     */
    @Override
    public long lrem(final String key, final long count, final String value) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.lRem(key.getBytes(), count, value.getBytes());
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param keys
     * @return
     */
    @Override
    public long del(final String... keys) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String k : keys) {
                    result = connection.del(k.getBytes());
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param keys
     * @return
     */
    @Override
    public long del(final List<String> keys) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String k : keys) {
                    result = connection.del(k.getBytes());
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public long del(final String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.del(key.getBytes());
            }

        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     * @param liveTime
     */
    @Override
    public void set(final byte[] key, final byte[] value, final long liveTime) {
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key, value);
                if (liveTime > 0) {
                    connection.expire(key, liveTime);
                }
                return 1L;
            }
        });

    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     * @param liveTime
     */
    @Override
    public void set(String key, String value, long liveTime) {
        this.set(key.getBytes(), value.getBytes(), liveTime);

    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     */
    @Override
    public void set(String key, String value) {
        this.set(key, value, 0L);
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param map
     */
    @Override
    public void setMulti(final Map<String, String> map) {
        setMulti(map, 0L);
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param map
     * @param liveTime
     */
    @Override
    public void setMulti(final Map<String, String> map, final long liveTime) {
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                Set<Map.Entry<String, String>> set = map.entrySet();
                for (Entry<String, String> entry : set) {
                    connection.set(entry.getKey().getBytes(), entry.getValue().getBytes());
                    if (liveTime > 0) {
                        connection.expire(entry.getKey().getBytes(), liveTime);
                    }
                }
                return null;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     */
    @Override
    public void set(byte[] key, byte[] value) {
        this.set(key, value, 0L);

    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public String get(final String key) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] result = connection.get(key.getBytes());
                if (result != null) {
                    try {
                        return new String(result, ENCODE);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return "";
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     * @param liveTime
     * @return
     */
    @Override
    public long setnx(final byte[] key, final byte[] value, final long liveTime) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0L;
                boolean isSuccess = connection.setNX(key, value);
                if (isSuccess) {
                    if (liveTime > 0) {
                        connection.expire(key, liveTime);
                    }
                    result = 1L;
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     * @param liveTime
     * @return
     */
    @Override
    public long setnx(String key, String value, long liveTime) {
        return this.setnx(key.getBytes(), value.getBytes(), liveTime);

    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     * @return
     */
    @Override
    public long setnx(String key, String value) {
        return this.setnx(key, value, 0L);
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param value
     * @return
     */
    @Override
    public long setnx(byte[] key, byte[] value) {
        return this.setnx(key, value, 0L);

    }

    /**
     * 
     * 
     * @author liuxinq
     * @param pattern
     * @return
     */
    @Override
    public Set<String> keys(final String pattern) {
        return redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<String> result = new HashSet<String>();
                for (byte[] d : connection.keys(pattern.getBytes())) {
                    try {
                        result.add(new String(d, ENCODE));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public boolean exists(final String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key.getBytes());
            }

        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @return
     */
    @Override
    public String flushDB() {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "success";
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @return
     */
    @Override
    public long dbSize() {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize();
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @return
     */
    @Override
    public String ping() {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ping();
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param seconds
     * @return
     */
    @Override
    public boolean expire(final String key, final long seconds) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.expire(key.getBytes(), seconds);
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public long incr(final String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.incr(key.getBytes());
            }

        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param len
     * @return
     */
    @Override
    public long incrBy(final String key, final long len) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.incrBy(key.getBytes(), len);
            }
        });

    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param len
     * @return
     */
    @Override
    public double incrBy(final String key, final double len) {
        return redisTemplate.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.incrBy(key.getBytes(), len);
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param luaCommand
     * @return
     */
    @Override
    public long eval(final String luaCommand) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.eval(luaCommand.getBytes(), null, 0);
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param count
     * @return
     */
    @Override
    public Set<String> srandMember(final String key, final int count) {
        return redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<String> result = new HashSet<String>();
                List<byte[]> data = connection.sRandMember(key.getBytes(), count);
                for (byte[] d : data) {
                    try {
                        result.add(new String(d, ENCODE));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @return
     */
    @Override
    public String spop(final String key) {
        return redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] result = connection.sPop(key.getBytes());
                if (result != null) {
                    try {
                        return new String(result, ENCODE);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return "";
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param size
     * @return
     */
    @Override
    public List<?> spop(final String key, final int size) {
        List<Object> list = redisTemplate.executePipelined(new RedisCallback<List<Object>>() {
            @Override
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = key.getBytes();
                for (int i = 0; i < size; i++) {
                    connection.sPop(keyBytes);
                }
                return null;
            }
        });
        list.removeAll(Collections.singleton(null));
        return list;
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param values
     * @return
     */
    @Override
    public Long srem(final String key, final List<String> values) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String v : values) {
                    result = connection.sRem(key.getBytes(), v.getBytes());
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param values
     * @return
     */
    @Override
    public Long sadd(final String key, final String... values) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (String v : values) {
                    result = connection.sAdd(key.getBytes(), v.getBytes());
                }
                return result;
            }
        });
    }

    /**
     * 
     * 
     * @author liuxinq
     * @param key
     * @param values1
     * @return
     */
    @Override
    public long sadd(final String key, final Collection<String> values1) {
        String[] values = (String[]) values1.toArray(new String[values1.size()]);
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 
     * @Description:存放数据信息
     * @param key
     * @param value
     * @return int
     * @exception:
     * @author: liangcm
     * @time:2017年4月27日 下午5:33:05
     */
    @Override
    public int hset(final String key, final Map<String, Object> value) {
        try {
            redisTemplate.opsForHash().putAll(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

}
