package com.hy.boke.config;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 * @author tongbufucj
 *
 */
@Component
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * redis加锁，加锁成功返回true，失败返回false
     * @param key
     * @param value 当前时间 + 超时时间
     * @return
     */
    public boolean lock(String key, String value) {
        boolean isAddLock = Boolean.valueOf((redisTemplate.opsForValue().get("isAddLock") == null ? "true" : redisTemplate.opsForValue().get("isAddLock")));
        if(isAddLock) {
            if(redisTemplate.opsForValue().setIfAbsent(key, value)) {
                return true;
            }
            String currentValue = redisTemplate.opsForValue().get(key);
            if(!StringUtils.isEmpty(key) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
                String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
                if(!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                    return true;
                }
            }

            return false;
        }else{
            return true;
        }
    }

    /**
     * 解锁
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if(!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 强制解锁
     * @param key
     */
    public void unlock(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    /**
     * 判断当前用户积分明细消息模板是否推送中
     * @return
     */
    public boolean checkIsPushIntegral() {
        boolean isPushingIntegral = Boolean.valueOf((redisTemplate.opsForValue().get("isPushingIntegral") == null ? "true" : redisTemplate.opsForValue().get("isPushingIntegral")));
        return isPushingIntegral;
    }

    /**
     * 判断消息模板是否已推送，已推送过返回true，否则返回false
     * @param key 消息模板ID
     * @return
     */
    public boolean checkIsPushed(String key) {
        boolean pushed = Boolean.valueOf((redisTemplate.opsForValue().get(key) == null ? "false" : redisTemplate.opsForValue().get(key)));
        if(!pushed) {
            redisTemplate.opsForValue().set(key, "true", 5L, TimeUnit.DAYS);
        }
        return pushed;
    }

    /**
     * 批量删除以 pri_key 开头的redis信息
     * @param pre_key
     */
    public void batchDel(String pre_key) {
        Set<String> keys = redisTemplate.keys(pre_key+"*");
        if(!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 设置Redis缓存
     * @param time
     * @param unit
     */
    public void set(String key, String val, long time, TimeUnit unit) {
        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(key)) {
            return;
        }
        redisTemplate.opsForValue().set(key, val, time, unit);
    }

    /**
     * 获取Redis缓存，缓存类型：String
     * @param key
     * @return
     */
    public String getString(String key) {
        if(StringUtils.isEmpty(key)) {
            return null;
        }
        String val = redisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(val)) {
            return null;
        }
        return val;
    }

    /**
     * 获取Redis缓存，缓存类型：Object
     * @param t
     * @return
     */


    /**
     * 获取Redis缓存，缓存类型：Boolean
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        String val = getString(key);
        if(StringUtils.isEmpty(val)) {
            return false;
        }
        return Boolean.valueOf(getString(key));
    }

    /**
     * 判断key是否存在，存在返回true，否则返回false
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取以key开头的所有数据量
     * @return
     */
    public int getKeyPrefixCount(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        if(CollectionUtils.isEmpty(keys)) {
            return 0;
        }
        return keys.size();
    }

}

